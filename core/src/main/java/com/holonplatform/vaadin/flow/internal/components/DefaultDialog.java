/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Default {@link Dialog} to be used with dialog builders.
 *
 * <h3>BEM structure</h3>
 * <pre>
 * .h-dialog                         — root host (Dialog overlay)
 *   .h-dialog__header               — built-in Vaadin header slot
 *     [.h-dialog__icon]             — optional circular icon container (Tailwind Plus pattern)
 *     .h-dialog__header-text        — groups title + description (shadcn DialogHeader)
 *       .h-dialog__title            — optional title (H4)
 *       .h-dialog__description      — optional description (Paragraph)
 *     .h-dialog__close-btn          — × close button (shadcn DialogClose)
 *   .h-dialog__body                 — scrollable content area (Div)
 *   .h-dialog__footer               — built-in Vaadin footer slot
 *     .h-dialog__cancel-btn         — left-aligned cancel/deny button
 *     .h-dialog__action-btn         — right-aligned primary action button
 * </pre>
 *
 * <h3>Patterns implemented</h3>
 * <ul>
 *   <li><strong>Vaadin Dialog</strong> — server-side overlay, slot API, theme attributes</li>
 *   <li><strong>shadcn/ui Dialog</strong> — three-slot model, DialogTrigger, DialogClose,
 *       ARIA accessible name, conditional close guard, size variants</li>
 *   <li><strong>Tailwind Plus modal</strong> — mobile bottom-sheet, desktop centred modal,
 *       icon header variant, centered variant, size max-width tiers</li>
 * </ul>
 *
 * @since 5.2.0
 */
@StyleSheet("context://h-dialog.css")
public class DefaultDialog extends Dialog {

    private static final long serialVersionUID = 5017183187214693820L;

    // -----------------------------------------------------------------------
    // DOM slots
    // -----------------------------------------------------------------------

    /** Scrollable body content area. */
    private final Div body;

    /**
     * Groups title + description in the header — mirrors shadcn/ui {@code DialogHeader}.
     * Placed between the optional icon and the close button in the header flex row.
     */
    private final Div headerText;

    /** Header × close button — optional, visible by default. */
    private final Button closeButton;

    // -----------------------------------------------------------------------
    // i18n state
    // -----------------------------------------------------------------------

    private Localizable titleLocalizable;
    private Localizable descriptionLocalizable;

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    /** Unconditional close callback — dialog always closes after the Runnable completes. */
    private Runnable onCloseCallback;

    /**
     * Conditional close handler — dialog closes only when the supplier returns {@code true}.
     * Mutually exclusive with {@link #onCloseCallback}.
     */
    private BooleanSupplier onCloseCondition;

    private Runnable onOpenCallback;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public DefaultDialog() {
        super();
        getElement().getClassList().add("h-dialog");
        getElement().getThemeList().add("h-dialog");

        // Header text wrapper — groups title + description in a flex-col (shadcn/ui DialogHeader)
        this.headerText = Components.div().styleName("h-dialog__header-text").build();
        getHeader().add(this.headerText);

        // Close button — lives at the dialog root (NOT in the header slot) so it does not
        // affect the title/description layout. Absolutely positioned via CSS at top-right
        // of the overlay, mirroring the shadcn/ui DialogClose pattern.
        this.closeButton = Components.button()
                .icon(VaadinIcon.CLOSE_SMALL)
                .styleName("h-dialog__close-btn")
                .ariaLabel("Close")
                .withClickListener(e -> attemptClose())
                .build();
        add(this.closeButton);

        // Scrollable body content area
        this.body = Components.div().styleName("h-dialog__body").build();
        add(body);
    }

    // -----------------------------------------------------------------------
    // Lifecycle — open/close callbacks
    // -----------------------------------------------------------------------

    @Override
    public void open() {
        super.open();
        if (onOpenCallback != null) {
            onOpenCallback.run();
        }
    }

    @Override
    public void close() {
        attemptClose();
    }

    /**
     * Core close logic shared by all close triggers (close button, ESC, outside click,
     * programmatic {@link #close()}).
     *
     * <ul>
     *   <li>If a {@link BooleanSupplier} guard is set: close only when it returns
     *       {@code true} — mirrors the shadcn/ui conditional-close pattern.</li>
     *   <li>Otherwise: always close; {@link Runnable} callback fires in a
     *       {@code try/finally} so it is never silently lost.</li>
     * </ul>
     */
    public void attemptClose() {
        if (onCloseCondition != null) {
            if (onCloseCondition.getAsBoolean()) {
                performClose();
            }
            // false → guard rejected — stay open
        } else {
            performClose();
        }
    }

    /**
     * Executes the actual close sequence.
     *
     * <p>Made {@code protected} so that test subclasses can override it without
     * triggering a Vaadin UI context requirement.</p>
     */
    protected void performClose() {
        try {
            super.close();
        } finally {
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Callback API
    // -----------------------------------------------------------------------

    /**
     * Sets an unconditional callback fired after the dialog opens.
     *
     * @param onOpen the callback
     */
    public void setOnOpen(Runnable onOpen) {
        this.onOpenCallback = onOpen;
    }

    /**
     * Sets an unconditional close callback. The dialog always closes; the callback fires
     * in {@code try/finally}. Clears any previously registered {@link BooleanSupplier} guard.
     *
     * @param onClose the callback
     */
    public void setOnClose(Runnable onClose) {
        this.onCloseCondition = null;
        this.onCloseCallback  = onClose;
    }

    /**
     * Sets a conditional close handler. The dialog closes only when the supplier returns
     * {@code true}. Clears any previously registered {@link Runnable} callback.
     *
     * @param onClose the conditional handler
     */
    public void setOnClose(BooleanSupplier onClose) {
        this.onCloseCallback  = null;
        this.onCloseCondition = onClose;
    }

    // -----------------------------------------------------------------------
    // Close-button API  (shadcn/ui DialogClose)
    // -----------------------------------------------------------------------

    /**
     * Shows or hides the header close (×) button.
     *
     * <p>Defaults to {@code true} (visible). Set to {@code false} for confirmation
     * dialogs that require an explicit action-button choice (delete, question, etc.)
     * — matches the shadcn/ui {@code AlertDialog} pattern where forced choice is required.</p>
     *
     * @param visible {@code true} to show the button, {@code false} to hide it
     */
    public void setCloseButtonVisible(boolean visible) {
        this.closeButton.setVisible(visible);
    }

    /**
     * @return {@code true} if the header close button is currently visible
     */
    public boolean isCloseButtonVisible() {
        return this.closeButton.isVisible();
    }

    /**
     * Applies custom configuration (icon, aria-label, CSS classes, etc.) to the header
     * close button. The button's close-on-click wiring is always preserved.
     *
     * @param configurator a {@link Consumer} that receives the raw {@link Button} (not null)
     */
    public void configureCloseButton(Consumer<Button> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(this.closeButton);
    }

    /**
     * Replaces the default × glyph in the header close button with the given icon component.
     *
     * <p>Useful when the application provides its own icon library (e.g. Material Symbols,
     * Lucide, or a custom SVG span). The button's close-on-click wiring is always preserved.</p>
     *
     * @param icon the replacement icon component (not null)
     */
    public void setCloseButtonIcon(Component icon) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        this.closeButton.setIcon(icon);
    }

    // -----------------------------------------------------------------------
    // Title API  (shadcn/ui DialogTitle + aria-labelledby)
    // -----------------------------------------------------------------------

    /**
     * Sets a structured title in the dialog header.
     *
     * <p>Also sets the ARIA accessible name on the dialog overlay so that
     * screen readers announce the title when the dialog opens — mirrors the
     * shadcn/ui {@code aria-labelledby} pattern.</p>
     *
     * @param text the title text (null or blank clears the title)
     */
    public void setDialogTitle(String text) {
        this.titleLocalizable = null;
        applyTitle(text);
    }

    /**
     * Sets a structured title from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message
     */
    public void setDialogTitle(Localizable localizable) {
        this.titleLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::applyTitle);
    }

    private void applyTitle(String text) {
        // Remove any existing title element from the header-text wrapper
        headerText.getChildren()
                .filter(c -> c.getClassNames().contains("h-dialog__title"))
                .findFirst()
                .ifPresent(headerText::remove);

        if (text != null && !text.isBlank()) {
            H4 titleEl = new H4(text);
            titleEl.addClassName("h-dialog__title");

            // Insert before description if description already present (title always first)
            boolean hasDesc = headerText.getChildren()
                    .anyMatch(c -> c.getClassNames().contains("h-dialog__description"));
            if (hasDesc) {
                headerText.addComponentAsFirst(titleEl);
            } else {
                headerText.add(titleEl);
            }

            // Auto-set ARIA accessible name from title (shadcn/ui aria-labelledby equivalent)
            setAriaLabel(text);
        }
    }

    /**
     * @return the current dialog title text, or {@code null} if no title is set
     */
    public String getDialogTitle() {
        return headerText.getChildren()
                .filter(c -> c.getClassNames().contains("h-dialog__title"))
                .findFirst()
                .map(c -> ((H4) c).getText())
                .orElse(null);
    }

    // -----------------------------------------------------------------------
    // Description API  (shadcn/ui DialogDescription + aria-describedby)
    // -----------------------------------------------------------------------

    /**
     * Sets a structured description below the title in the dialog header.
     *
     * @param text the description text (null or blank clears the description)
     */
    public void setDialogDescription(String text) {
        this.descriptionLocalizable = null;
        applyDescription(text);
    }

    /**
     * Sets a structured description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message
     */
    public void setDialogDescription(Localizable localizable) {
        this.descriptionLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::applyDescription);
    }

    private void applyDescription(String text) {
        headerText.getChildren()
                .filter(c -> c.getClassNames().contains("h-dialog__description"))
                .findFirst()
                .ifPresent(headerText::remove);

        if (text != null && !text.isBlank()) {
            Paragraph desc = new Paragraph(text);
            desc.addClassName("h-dialog__description");
            headerText.add(desc); // always after title (end of headerText wrapper)
        }
    }

    /**
     * @return the current dialog description text, or {@code null} if none is set
     */
    public String getDialogDescription() {
        return headerText.getChildren()
                .filter(c -> c.getClassNames().contains("h-dialog__description"))
                .findFirst()
                .map(c -> ((Paragraph) c).getText())
                .orElse(null);
    }

    // -----------------------------------------------------------------------
    // Header icon API  (Tailwind Plus "with icon" modal pattern)
    // -----------------------------------------------------------------------

    /**
     * Adds a circular icon container to the dialog header (before the text group).
     *
     * <p>The container receives the BEM base class {@code h-dialog__icon} plus the
     * supplied {@code variantClass} (e.g. {@code h-dialog__icon--destructive}).
     * The icon is marked as decorative ({@code aria-hidden="true"}) because the
     * title/description carry the same semantic information for screen readers.</p>
     *
     * <p>Calling this method multiple times replaces the previous icon.</p>
     *
     * <p>In non-centered mode the icon sits to the left of the
     * {@code .h-dialog__header-text} wrapper (flex row).  In centered mode
     * ({@link #setCentered(boolean)}) the icon appears above the title in a
     * stacked column — matching the Tailwind Plus centred-modal pattern.</p>
     *
     * @param icon         the icon component (vaadin-icon, svg, span glyph, etc.) — not null
     * @param variantClass the BEM modifier class for the colour variant (not null)
     */
    public void addHeaderIcon(Component icon, String variantClass) {
        ObjectUtils.argumentNotNull(icon, "Icon component must be not null");
        ObjectUtils.argumentNotNull(variantClass, "Variant class must be not null");

        // Remove any previously added icon container
        getHeader().getChildren()
                .filter(c -> c.getClassNames().contains("h-dialog__icon"))
                .findFirst()
                .ifPresent(getHeader()::remove);

        Div iconContainer = Components.div().add(icon).styleName("h-dialog__icon").build();
        iconContainer.addClassNames(variantClass);
        iconContainer.getElement().setAttribute("aria-hidden", "true");
        // Always the first element — headerText and closeButton follow naturally
        getHeader().addComponentAsFirst(iconContainer);
    }

    // -----------------------------------------------------------------------
    // Centered variant API  (Tailwind Plus "centred modal" pattern)
    // -----------------------------------------------------------------------

    /**
     * Enables or disables the centred-content layout variant.
     *
     * <p>When {@code true}:</p>
     * <ul>
     *   <li>The {@code h-dialog--centered} theme modifier is added to the overlay.</li>
     *   <li>The header close button is hidden — centred modals rely solely on footer actions.</li>
     *   <li>CSS centres the header (icon → title → description) and the footer buttons.</li>
     * </ul>
     *
     * @param centered {@code true} to activate the centred variant
     */
    public void setCentered(boolean centered) {
        if (centered) {
            getElement().getThemeList().add("h-dialog--centered");
            setCloseButtonVisible(false);
        } else {
            getElement().getThemeList().remove("h-dialog--centered");
            setCloseButtonVisible(true);
        }
    }

    // -----------------------------------------------------------------------
    // Size API  (shadcn/ui size prop + Tailwind Plus max-w-* variants)
    // -----------------------------------------------------------------------

    /**
     * Sets the dialog width to one of the predefined size variants.
     *
     * <p>Adds the corresponding BEM theme modifier to the overlay element
     * ({@code h-dialog--sm}, {@code h-dialog--md}, etc.), which overrides
     * {@code --vaadin-dialog-overlay-width} at the desktop breakpoint.</p>
     *
     * <p>Multiple calls replace the previously applied size; passing {@code null}
     * removes all size modifiers and restores the default width.</p>
     *
     * @param size the size variant, or {@code null} to reset to default
     */
    public void setDialogSize(DialogConfigurator.DialogSize size) {
        // Remove any previously set size modifier
        for (DialogConfigurator.DialogSize s : DialogConfigurator.DialogSize.values()) {
            getElement().getThemeList().remove(s.getThemeModifier());
        }
        if (size != null) {
            getElement().getThemeList().add(size.getThemeModifier());
        }
    }

    // -----------------------------------------------------------------------
    // Legacy message API  (maps to header title for backward compatibility)
    // -----------------------------------------------------------------------

    /** @return Optional message (mapped from header title) */
    public Optional<String> getMessage() {
        String text = getHeaderTitle();
        if (text != null && !text.isBlank()) {
            return Optional.of(text);
        }
        return Optional.empty();
    }

    /** Sets the dialog header title (legacy path; prefer {@link #setDialogTitle(String)}). */
    public void setMessage(String text) {
        setHeaderTitle(text);
    }

    // -----------------------------------------------------------------------
    // Content API
    // -----------------------------------------------------------------------

    /** Adds a component to the scrollable body. */
    public void addContentComponent(Component component) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        this.body.add(component);
    }

    /** Adds multiple components to the scrollable body. */
    public void addContentComponent(Component... components) {
        this.body.add(components);
    }

    /** @return stream of body children */
    public Stream<Component> getContentComponents() {
        return this.body.getChildren();
    }

    /** Adds a component to the dialog footer. */
    public void addFooterComponent(Component component) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        getFooter().add(component);
    }

    // -----------------------------------------------------------------------
    // Responsive helper
    // -----------------------------------------------------------------------

    /**
     * Makes the dialog responsive using CSS viewport units — no JS resize listener needed.
     *
     * <ul>
     *   <li>Mobile  (< 640 px) : dialog fills 95 % of viewport width / height.</li>
     *   <li>Desktop (≥ 640 px) : dialog is capped at 900 × 600 px.</li>
     * </ul>
     */
    public void makeDialogResponsive() {
        setWidth("min(95vw, 900px)");
        setMaxHeight("min(90vh, 600px)");
    }

    // -----------------------------------------------------------------------
    // i18n re-resolution on re-attach
    // -----------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (titleLocalizable != null) {
            LocalizationProvider.localize(titleLocalizable).ifPresent(this::applyTitle);
        }
        if (descriptionLocalizable != null) {
            LocalizationProvider.localize(descriptionLocalizable).ifPresent(this::applyDescription);
        }
    }
}
