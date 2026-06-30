/*
 * Copyright 2016-2024 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.SheetBuilder;
import com.iyensoft.vaadin.flow.enums.HeadingLevel;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.function.Supplier;

/**
 * A mobile-first slide-in panel inspired by shadcn/ui {@code Sheet}.
 *
 * <p>Unlike a modal {@link com.vaadin.flow.component.dialog.Dialog}, the Sheet slides in from a
 * viewport edge. The backdrop is always semi-transparent but non-blocking (the user can still see
 * the content behind it). The Sheet integrates with the browser History API so the hardware
 * <em>back</em> button on Android and mobile browsers closes it naturally.</p>
 *
 * <p>Supported sides:</p>
 * <ul>
 *   <li>{@link Side#BOTTOM} — slides up from the bottom (primary mobile pattern)</li>
 *   <li>{@link Side#LEFT}   — slides in from the left  (navigation drawer)</li>
 *   <li>{@link Side#RIGHT}  — slides in from the right (detail / filter panel)</li>
 * </ul>
 *
 * <p>Composition:</p>
 * <pre>
 * Sheet  (.sheet + .sheet--{side} [.sheet--fullscreen-mobile])
 *  ├── Backdrop     (.sheet__backdrop)       — dimmed overlay, click-to-close
 *  └── Panel        (.sheet__panel)          — the sliding surface
 *       ├── Handle  (.sheet__handle)         — drag indicator (BOTTOM only, visual)
 *       ├── Header  (.sheet__header)         — {@link Header} with nav buttons + title
 *       │    ├── prefix  → back Button (.sheet__btn-back)
 *       │    ├── column  → SheetTitle heading + SheetDescription details
 *       │    └── actions → close Button (.sheet__btn-close)
 *       ├── Content (.sheet__content)        — arbitrary user content
 *       └── Footer  (.sheet__footer)         — optional action / meta row
 * </pre>
 *
 * <p>Both nav buttons are shown by default and can be hidden via
 * {@link #setShowBackButton(boolean)} / {@link #setShowCloseButton(boolean)}.</p>
 *
 * <p>On mobile viewports the sheet can be expanded to cover the full screen via
 * {@link #setFullscreenOnMobile(boolean)}.</p>
 *
 * <p>Preferred usage via builder:</p>
 * <pre>{@code
 * Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM)
 *     .title("Filter options")
 *     .description("Narrow down your results.")
 *     .content(myFilterForm)
 *     .onClose(() -> applyFilters())
 *     .build();
 * sheet.open();
 * }</pre>
 *
 * @see SheetBuilder
 * @see SheetTitle
 * @see SheetDescription
 */
@StyleSheet("context://sheet.css")
public class Sheet extends Div {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // JS — browser History API bridge
    // -----------------------------------------------------------------------

    private static final String JS_PUSH_HISTORY = """
            (function(sheetId) {
                if (!window.__vaadinSheets) {
                    window.__vaadinSheets = [];
                    window.addEventListener('popstate', function() {
                        if (window.__vaadinSheets.length > 0) {
                            var id = window.__vaadinSheets.pop();
                            var el = document.getElementById(id);
                            if (el && el.$server) el.$server.closeFromHistory();
                        }
                    });
                }
                window.__vaadinSheets.push(sheetId);
                history.pushState({__vs: sheetId}, document.title);
            })($0);
            """;

    private static final String JS_CLEAN_HISTORY = """
            (function(sheetId) {
                if (window.__vaadinSheets) {
                    var idx = window.__vaadinSheets.lastIndexOf(sheetId);
                    if (idx !== -1) window.__vaadinSheets.splice(idx, 1);
                }
                history.replaceState(null, document.title);
            })($0);
            """;

    // -----------------------------------------------------------------------
    // Side
    // -----------------------------------------------------------------------

    /** The edge from which the Sheet panel slides in. */
    public enum Side {
        /** Slides up from the bottom — primary mobile pattern (action sheet / bottom drawer). */
        BOTTOM("sheet--bottom"),
        /** Slides in from the left — navigation drawer. */
        LEFT("sheet--left"),
        /** Slides in from the right — detail panel / filter panel. */
        RIGHT("sheet--right");

        private final String cssClass;

        Side(String cssClass) { this.cssClass = cssClass; }

        /** Returns the BEM modifier class for this side. */
        public String getCssClass() { return cssClass; }
    }

    // -----------------------------------------------------------------------
    // DOM structure
    // -----------------------------------------------------------------------

    private final Div    handle;
    /** Header component: prefix = back button, column = title/description, actions = close button. */
    private Header header;
    private final Button backButton;
    private final Button closeButton;
    private final Div    contentSlot;
    private final Div    panel;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private Side             currentSide;
    private SheetTitle       currentTitle;
    private SheetDescription currentDescription;

    private boolean closeOnBackdropClick = true;
    private boolean historyEnabled       = true;
    private boolean historyEntryPushed   = false;
    private boolean backdropVisible      = true;
    private boolean belowHeader          = false;
    private boolean customHeaderVisible   = false;

    private Supplier<Component[]> lazyContentSupplier;
    private Runnable onOpenCallback;
    private Runnable onCloseCallback;
    private Footer footer;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /** Creates a {@link Side#BOTTOM} sheet with no content. */
    public Sheet() {
        this(Side.BOTTOM);
    }

    /**
     * Creates a sheet that slides in from the given {@code side}.
     *
     * @param side the slide direction (not null)
     */
    public Sheet(Side side) {
        addClassName("sheet");

        // Backdrop — full-screen dimmed overlay
        Div backdrop = Components.div().styleName("sheet__backdrop").build();
        backdrop.addClickListener(e -> { if (closeOnBackdropClick) close(); });

        // Panel — the visible sliding surface
        this.panel = Components.div().styleName("sheet__panel").build();

        // Handle — drag indicator (visual only, BOTTOM sheets on mobile)
        this.handle = Components.div().styleName("sheet__handle").build();

        // Back button — closes the sheet (mirrors hardware back button)
        this.backButton = Components.button().icon(LineAwesomeIcon.ARROW_LEFT_SOLID.create())
                .styleName("sheet__btn-back").ariaLabel("Back").withClickListener(e -> close()).build();
        this.backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);

        // Close button — closes the sheet and cleans up history entry
        this.closeButton = Components.button().icon(LineAwesomeIcon.TIMES_SOLID.create())
                .styleName("sheet__btn-close").ariaLabel("Close").withClickListener(e -> close()).build();
        this.closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);

        // Header: prefix = back, column = title+description, actions = close
        this.header = createDefaultHeader();

        // Content slot — holds arbitrary user components
        this.contentSlot = Components.div().styleName("sheet__content").build();

        rebuildPanel();
        add(backdrop, panel);

        setSide(side);
        syncHeaderVisibility();
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /** @return a new {@link SheetBuilder} for a {@link Side#BOTTOM} sheet */
    public static SheetBuilder builder() {
        return SheetBuilder.create();
    }

    /**
     * @param side the slide direction (not null)
     * @return a new {@link SheetBuilder} for the given side
     */
    public static SheetBuilder builder(Side side) {
        return SheetBuilder.create(side);
    }

    // -----------------------------------------------------------------------
    // Lifecycle — open / close
    // -----------------------------------------------------------------------

    /**
     * Opens the sheet: populates lazy content on first call, attaches to UI, triggers the
     * slide-in CSS transition, pushes a history entry (if enabled), fires {@code onOpen}.
     */
    public void open() {
        if (lazyContentSupplier != null) {
            Component[] components = lazyContentSupplier.get();
            lazyContentSupplier = null;
            contentSlot.removeAll();
            if (components != null) contentSlot.add(components);
        }

        UI ui = UI.getCurrent();
        if (ui != null && !isAttached()) ui.add(this);

        addClassName("sheet--open");

        if (historyEnabled && ui != null && isAttached()) {
            getElement().executeJs(JS_PUSH_HISTORY, ensureId());
            this.historyEntryPushed = true;
        }
        if (onOpenCallback != null) onOpenCallback.run();
    }

    /**
     * Closes the sheet: triggers slide-out CSS transition, cleans up history entry via
     * {@code replaceState} (does NOT fire {@code popstate}), fires {@code onClose}.
     * Idempotent — safe to call on an already-closed sheet.
     */
    public void close() {
        if (!isOpen()) return;
        if (historyEntryPushed) {
            getElement().executeJs(JS_CLEAN_HISTORY, ensureId());
        }
        doInternalClose();
    }

    /** @return {@code true} if the sheet is currently open */
    public boolean isOpen() {
        return getClassNames().contains("sheet--open");
    }

    /**
     * Removes the sheet from the UI DOM entirely.
     * For routine open/close cycles prefer {@link #close()} — the element stays attached.
     */
    public void detach() {
        if (isAttached()) {
            getParent().ifPresent(parent -> {
                if (parent instanceof HasComponents hc) hc.remove(this);
            });
        }
    }

    // -----------------------------------------------------------------------
    // @ClientCallable — hardware back button bridge
    // -----------------------------------------------------------------------

    /**
     * Called from JS when {@code popstate} fires and this sheet is top of the stack.
     * The browser has already consumed the history entry — no further manipulation needed.
     */
    @ClientCallable
    public void closeFromHistory() {
        if (!isOpen()) return;
        doInternalClose();
    }

    // -----------------------------------------------------------------------
    // Side API
    // -----------------------------------------------------------------------

    public Side getSide() { return currentSide; }

    public void setSide(Side side) {
        if (this.currentSide != null) removeClassName(this.currentSide.getCssClass());
        this.currentSide = side;
        if (side != null) {
            addClassName(side.getCssClass());
            handle.setVisible(side == Side.BOTTOM);
        }
    }

    // -----------------------------------------------------------------------
    // Title API
    // -----------------------------------------------------------------------

    /** @return the current title component, or {@code null} if not set */
    public SheetTitle getSheetTitle() { return currentTitle; }

    /**
     * Sets (or replaces) the title. Passing {@code null} removes it.
     *
     * @param title the title component, or {@code null} to remove
     */
    public void setTitle(SheetTitle title) {
        ensureHeader();
        this.currentTitle = title;
        header.setHeading(title != null ? title : HeadingLevel.NONE.getComponent(""));
        syncHeaderVisibility();
    }

    public void setTitle(String text) {
        setTitle(new SheetTitle(text));
    }

    public void setTitle(Localizable localizable) {
        setTitle(new SheetTitle(localizable));
    }

    // -----------------------------------------------------------------------
    // Description API
    // -----------------------------------------------------------------------

    /** @return the current description component, or {@code null} if not set */
    public SheetDescription getSheetDescription() { return currentDescription; }

    /**
     * Sets (or replaces) the description. Passing {@code null} removes it.
     *
     * @param description the description component, or {@code null} to remove
     */
    public void setDescription(SheetDescription description) {
        ensureHeader();
        this.currentDescription = description;
        if (description != null) {
            header.setDetails(description);
        } else {
            header.setDetails();
        }
        syncHeaderVisibility();
    }

    public void setDescription(String text) {
        setDescription(new SheetDescription(text));
    }

    public void setDescription(Localizable localizable) {
        setDescription(new SheetDescription(localizable));
    }

    // -----------------------------------------------------------------------
    // Content API
    // -----------------------------------------------------------------------

    /**
     * Replaces the content slot. Clears any pending lazy-content supplier.
     *
     * @param components the components to display in the sheet body
     */
    public void setContent(Component... components) {
        this.lazyContentSupplier = null;
        contentSlot.removeAll();
        if (components != null) contentSlot.add(components);
    }

    /**
     * Replaces the sheet header shown above the content.
     *
     * <p>Passing {@code null} restores the default compact sheet header.</p>
     */
    public void setHeader(Header header) {
        this.header = header != null ? header : createDefaultHeader();
        this.customHeaderVisible = header != null;
        rebuildPanel();
        syncHeaderVisibility();
    }

    /**
     * Replaces the sheet footer shown below the content.
     *
     * <p>Passing {@code null} removes the footer slot entirely.</p>
     */
    public void setFooter(Footer footer) {
        this.footer = footer;
        rebuildPanel();
    }

    public void clearContent() {
        contentSlot.removeAll();
    }

    // -----------------------------------------------------------------------
    // Behaviour API
    // -----------------------------------------------------------------------

    public void setCloseOnBackdropClick(boolean closeOnBackdropClick) {
        this.closeOnBackdropClick = closeOnBackdropClick;
    }

    public boolean isCloseOnBackdropClick() { return closeOnBackdropClick; }

    /**
     * Sets whether the semi-transparent backdrop is shown behind the panel when the sheet opens.
     *
     * <ul>
     *   <li>{@code true} (default) — backdrop dims the content behind the sheet</li>
     *   <li>{@code false} — the sheet slides in on top of the existing view with no dimming;
     *       useful for persistent side panels or filter drawers where the user needs to
     *       interact with the background after closing</li>
     * </ul>
     *
     * @param backdropVisible {@code true} to show the dimming backdrop
     */
    public void setBackdropVisible(boolean backdropVisible) {
        this.backdropVisible = backdropVisible;
        if (backdropVisible) {
            removeClassName("sheet--no-backdrop");
        } else {
            addClassName("sheet--no-backdrop");
        }
    }

    /** @return {@code true} if the dimming backdrop is shown (default) */
    public boolean isBackdropVisible() { return backdropVisible; }

    /**
     * Controls whether the panel (and backdrop) start below the application header on
     * non-mobile screens (≥ 768 px), keeping the AppBar and its navigation buttons visible.
     *
     * <ul>
     *   <li>{@code false} (default) — panel covers the full viewport height, including
     *       the AppBar area</li>
     *   <li>{@code true} — panel starts below the AppBar; uses the CSS custom property
     *       {@code --vaadin-app-layout-navbar-offset-top} for the top offset.
     *       Vaadin {@code AppLayout} sets this automatically. For custom AppBars, declare
     *       it in your application CSS:
     *       <pre>:root &#123; --vaadin-app-layout-navbar-offset-top: 64px; &#125;</pre>
     *       Has no effect on mobile (≤ 767 px) or on {@link Side#BOTTOM} sheets.</li>
     * </ul>
     *
     * @param belowHeader {@code true} to keep the AppBar visible above the sheet
     */
    public void setBelowHeader(boolean belowHeader) {
        this.belowHeader = belowHeader;
        if (belowHeader) {
            addClassName("sheet--below-header");
        } else {
            removeClassName("sheet--below-header");
        }
    }

    /** @return {@code true} if the panel starts below the AppBar on non-mobile screens */
    public boolean isBelowHeader() { return belowHeader; }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public boolean isHistoryEnabled() { return historyEnabled; }

    public void setFullscreenOnMobile(boolean fullscreen) {
        if (fullscreen) addClassName("sheet--fullscreen-mobile");
        else removeClassName("sheet--fullscreen-mobile");
    }

    public boolean isFullscreenOnMobile() {
        return getClassNames().contains("sheet--fullscreen-mobile");
    }

    /**
     * Shows or hides the back navigation button.
     * Default: {@code true} (visible).
     */
    public void setShowBackButton(boolean show) {
        backButton.setVisible(show);
        syncHeaderVisibility();
    }

    public boolean isShowBackButton() { return backButton.isVisible(); }

    /**
     * Shows or hides the close button.
     * Default: {@code true} (visible).
     */
    public void setShowCloseButton(boolean show) {
        closeButton.setVisible(show);
        syncHeaderVisibility();
    }

    public boolean isShowCloseButton() { return closeButton.isVisible(); }

    /**
     * Returns the Sheet's built-in close {@link Button}.
     *
     * <p>Useful when a custom header is supplied via {@link #setHeader(Header)}: the caller
     * can embed this button inside the custom header so close functionality is preserved.</p>
     */
    public Button getCloseButton() { return closeButton; }

    /**
     * Sets a lazy-content supplier invoked exactly once on the first {@link #open()} call.
     * The DOM tree is reused on subsequent opens; the supplier is discarded after first use.
     *
     * @param supplier supplier returning the initial content; always called on the UI thread
     */
    public void setLazyContent(Supplier<Component[]> supplier) {
        this.lazyContentSupplier = supplier;
    }

    public void setOnOpen(Runnable onOpen) { this.onOpenCallback = onOpen; }

    public void setOnClose(Runnable onClose) { this.onCloseCallback = onClose; }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void doInternalClose() {
        removeClassName("sheet--open");
        this.historyEntryPushed = false;
        if (onCloseCallback != null) onCloseCallback.run();
    }

    /** Hides the header entirely when there is nothing to show inside it. */
    private void syncHeaderVisibility() {
        header.setVisible(customHeaderVisible
                || currentTitle != null || currentDescription != null
                || backButton.isVisible() || closeButton.isVisible());
    }

    private Header createDefaultHeader() {
        Header defaultHeader = new Header("", HeadingLevel.NONE);
        defaultHeader.addClassName("sheet__header");
        defaultHeader.setPrefix(backButton);
        defaultHeader.setActions(closeButton);
        return defaultHeader;
    }

    private void ensureHeader() {
        if (header == null) {
            header = createDefaultHeader();
            customHeaderVisible = false;
            rebuildPanel();
        }
    }

    private void rebuildPanel() {
        panel.removeAll();
        panel.add(handle);
        if (header != null) {
            panel.add(header);
        }
        panel.add(contentSlot);
        if (footer != null) {
            footer.addClassName("sheet__footer");
            panel.add(footer);
        }
    }

    private String ensureId() {
        return getId().orElseGet(() -> {
            String id = "vs-" + Integer.toHexString(System.identityHashCode(this));
            setId(id);
            return id;
        });
    }
}


