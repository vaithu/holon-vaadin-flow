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
import com.holonplatform.core.utils.SizedStack;
import com.vaadin.flow.component.Component;

/**
 * A bounded, signal-free navigation stack of {@link Sheet} panels.
 *
 * <p>Manages a {@link SizedStack} of open {@link Sheet} instances and automatically applies
 * the correct stacking rules on each {@link #push}:</p>
 * <ul>
 *   <li><strong>Root sheet (depth 0)</strong> — {@code backdropVisible(true)}: dims the
 *       application content behind it.</li>
 *   <li><strong>Child sheets (depth &gt; 0)</strong> — {@code backdropVisible(false)}: the
 *       parent sheet's opaque panel already covers the viewport; a second semi-transparent
 *       backdrop would let the parent bleed through.</li>
 *   <li><strong>All sheets</strong> — {@code fullscreenOnMobile(true)} by default: ensures
 *       100 % viewport coverage on mobile so no previous sheet is visible.</li>
 * </ul>
 *
 * <p>Each closed sheet is automatically removed from the browser DOM after the 300 ms
 * slide-out animation completes, preventing ghost fixed-position elements from
 * accumulating.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * var stack = new SheetStack(Sheet.Side.RIGHT);
 *
 * openBtn.addClickListener(e -> stack.push("Categories", categoryList));
 *
 * categoryRow.addClickListener(e -> stack.push(cat.name(), productList));
 *
 * viewBtn.addClickListener(e -> stack.push("Product Detail", detailView));
 * // Hardware Back / close button unwinds one level at a time (History API).
 * }</pre>
 *
 * @see Sheet
 * @see SizedStack
 */
public class SheetStack {

    /** Default maximum number of concurrently open sheets in the stack. */
    public static final int DEFAULT_MAX_DEPTH = 8;

    private final SizedStack<Sheet> openSheets;
    private final Sheet.Side side;
    private boolean fullscreenOnMobile = true;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a {@code SheetStack} that slides sheets in from the given edge,
     * with a max depth of {@value #DEFAULT_MAX_DEPTH}.
     *
     * @param side the edge from which all sheets in this stack slide in (not null)
     */
    public SheetStack(Sheet.Side side) {
        this(side, DEFAULT_MAX_DEPTH);
    }

    /**
     * Creates a {@code SheetStack} with an explicit depth limit.
     * If the limit is exceeded the eldest (bottom) sheet is evicted automatically
     * by {@link SizedStack}.
     *
     * @param side     the slide direction for all sheets in this stack (not null)
     * @param maxDepth maximum number of simultaneously open sheets (&gt; 0)
     */
    public SheetStack(Sheet.Side side, int maxDepth) {
        this.openSheets = new SizedStack<>(maxDepth);
        this.side = side;
    }

    // -----------------------------------------------------------------------
    // Core API
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link Sheet}, configures it for stacking, opens it, and returns it.
     *
     * <p>Stacking rules applied automatically:</p>
     * <ul>
     *   <li>Root (first push): {@code backdropVisible = true}</li>
     *   <li>Every subsequent push: {@code backdropVisible = false}</li>
     *   <li>All: {@code fullscreenOnMobile = }{@link #isFullscreenOnMobile()}</li>
     * </ul>
     *
     * <p>On close (any mechanism — close button, backdrop click, hardware Back):</p>
     * <ol>
     *   <li>The sheet is removed from the internal stack.</li>
     *   <li>A 350 ms client-side timeout removes the sheet element from the DOM
     *       after the slide-out animation finishes.</li>
     *   <li>{@code onClose} (if provided) is called.</li>
     * </ol>
     *
     * @param title   the sheet header title (not null)
     * @param content the body content to place in the sheet
     * @return the opened {@link Sheet} instance
     */
    public Sheet push(String title, Component... content) {
        return push(title, null, content);
    }

    /**
     * Creates a new {@link Sheet}, configures it for stacking, opens it, and returns it.
     *
     * @param title   the sheet header title (not null)
     * @param onClose optional callback fired after the sheet closes (may be null)
     * @param content the body content to place in the sheet
     * @return the opened {@link Sheet} instance
     */
    public Sheet push(String title, Runnable onClose, Component... content) {
        return push(Localizable.builder().message(title).build(), onClose, content);
    }

    /**
     * Creates a new {@link Sheet} with a localizable title, configures it for stacking, opens it, and returns it.
     *
     * <p>The title is resolved at push time via the Holon {@link com.holonplatform.vaadin.flow.i18n.LocalizationProvider}
     * strategy: Vaadin {@link com.vaadin.flow.i18n.I18NProvider} first, then the current
     * {@link com.holonplatform.core.i18n.LocalizationContext}, then the {@link Localizable#getMessage()} default.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // plain default message only (same as push(String, ...))
     * stack.push(Localizable.of("Categories"), categoryList);
     *
     * // message code with default fallback
     * stack.push(Localizable.builder()
     *         .message("Categories")
     *         .messageCode("sheet.categories.title")
     *         .build(), categoryList);
     * }</pre>
     *
     * @param title   the localizable sheet header title (not null)
     * @param content the body content to place in the sheet
     * @return the opened {@link Sheet} instance
     * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider#localize(Localizable)
     */
    public Sheet push(Localizable title, Component... content) {
        return push(title, null, content);
    }

    /**
     * Creates a new {@link Sheet} with a localizable title, configures it for stacking, opens it, and returns it.
     *
     * @param title   the localizable sheet header title (not null)
     * @param onClose optional callback fired after the sheet closes (may be null)
     * @param content the body content to place in the sheet
     * @return the opened {@link Sheet} instance
     * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider#localize(Localizable)
     */
    public Sheet push(Localizable title, Runnable onClose, Component... content) {
        boolean isRoot = openSheets.isEmpty();

        Sheet sheet = new Sheet(side);
        sheet.setTitle(title);
        sheet.setBackdropVisible(isRoot);
        sheet.setFullscreenOnMobile(fullscreenOnMobile);
        sheet.setContent(content);

        sheet.setOnClose(() -> {
            openSheets.remove(sheet);

            if (!isRoot) {
                // Child sheets close instantly: adding sheet--no-transition in the same
                // server-response batch as the removeClassName("sheet--open") prevents
                // the browser from starting the slide-out CSS transition.
                sheet.addClassName("sheet--no-transition");
            }

            // Remove from DOM after the animation (or immediately for child sheets).
            int cleanupDelayMs = isRoot ? 350 : 30;
            sheet.getElement().executeJs(
                    "const el = $0; setTimeout(() => { if (el.isConnected) el.remove(); }, " + cleanupDelayMs + ");",
                    sheet.getElement());

            if (onClose != null) onClose.run();
        });

        openSheets.push(sheet);
        sheet.open();
        return sheet;
    }

    // -----------------------------------------------------------------------
    // Configuration
    // -----------------------------------------------------------------------

    /**
     * Sets whether every pushed sheet should be fullscreen on mobile (≤ 767 px).
     * Default: {@code true}.
     *
     * @param fullscreenOnMobile {@code true} to cover the full viewport on mobile
     */
    public void setFullscreenOnMobile(boolean fullscreenOnMobile) {
        this.fullscreenOnMobile = fullscreenOnMobile;
    }

    /** @return {@code true} if sheets are fullscreen on mobile (default) */
    public boolean isFullscreenOnMobile() {
        return fullscreenOnMobile;
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    /** @return the current number of open sheets */
    public int depth() {
        return openSheets.size();
    }

    /** @return {@code true} if no sheets are currently open */
    public boolean isEmpty() {
        return openSheets.isEmpty();
    }

    /** @return the {@link Sheet.Side} used by this stack */
    public Sheet.Side getSide() {
        return side;
    }
}


