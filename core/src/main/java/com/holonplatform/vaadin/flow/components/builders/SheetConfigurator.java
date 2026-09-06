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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSheetConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetTitle;
import com.vaadin.flow.component.Component;

import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * Fluent configurator for {@link Sheet} components.
 *
 * <p>Covers all Sheet-specific properties (side, title, description, content, backdrop behaviour,
 * History API integration, open/close callbacks) in addition to the standard Holon Platform
 * component properties inherited from {@link ComponentConfigurator}, {@link HasSizeConfigurator}
 * and {@link HasStyleConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 * @see SheetBuilder
 * @see Sheet
 */
public interface SheetConfigurator<C extends SheetConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Side
    // -----------------------------------------------------------------------

    /**
     * Sets the edge from which the sheet panel slides in.
     * Default: {@link Sheet.Side#BOTTOM}.
     *
     * @param side the slide direction (not null)
     * @return this configurator for chaining
     */
    C side(Sheet.Side side);

    // -----------------------------------------------------------------------
    // Mode (M3: Modal vs. Standard side sheet)
    // -----------------------------------------------------------------------

    /**
     * Sets the {@link Sheet.Mode}. Only meaningful for {@link Sheet.Side#LEFT} /
     * {@link Sheet.Side#RIGHT} sheets. Default: {@link Sheet.Mode#MODAL}.
     *
     * @param mode the mode to apply (not null)
     * @return this configurator for chaining
     * @see Sheet#setMode(Sheet.Mode)
     */
    C mode(Sheet.Mode mode);

    /**
     * Enables/disables drag-to-resize on the panel's inner edge. Automatically set by
     * {@link #mode(Sheet.Mode)}; call after it to override.
     *
     * @param resizable {@code true} to allow the user to drag-resize the panel
     * @return this configurator for chaining
     */
    C resizable(boolean resizable);

    /**
     * Sets the minimum panel width (in pixels) allowed while drag-resizing.
     * Default: {@code 256} (M3 minimum side sheet width).
     *
     * @param minWidthPx the minimum width in pixels
     * @return this configurator for chaining
     */
    C minResizeWidth(int minWidthPx);

    /**
     * Sets the maximum panel width (in pixels) allowed while drag-resizing.
     * Default: {@code 400} (M3 recommended maximum side sheet width).
     *
     * @param maxWidthPx the maximum width in pixels
     * @return this configurator for chaining
     */
    C maxResizeWidth(int maxWidthPx);

    /**
     * Registers a callback fired after the user finishes drag-resizing the panel, receiving
     * the resulting panel width in pixels.
     *
     * @param onResize the callback (not null)
     * @return this configurator for chaining
     */
    C onResize(IntConsumer onResize);

    // -----------------------------------------------------------------------
    // Title
    // -----------------------------------------------------------------------

    /**
     * Sets the header title using a pre-built {@link SheetTitle}.
     *
     * @param title the title component (not null)
     * @return this configurator for chaining
     */
    C title(SheetTitle title);

    /**
     * Sets the header title from a plain string.
     *
     * @param text the title text (not null)
     * @return this configurator for chaining
     */
    C title(String text);

    /**
     * Sets the header title from a Holon {@link Localizable}.
     * Resolved immediately if a localization context is active; re-resolved on each attach.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator for chaining
     */
    C title(Localizable localizable);

    /**
     * Sets a custom header component for the sheet.
     * Replaces the default compact header built by {@link Sheet}.
     *
     * @param header the header component to display above the content (not null)
     * @return this configurator for chaining
     */
    C header(Header header);

    // -----------------------------------------------------------------------
    // Description
    // -----------------------------------------------------------------------

    /**
     * Sets the header description using a pre-built {@link SheetDescription}.
     *
     * @param description the description component (not null)
     * @return this configurator for chaining
     */
    C description(SheetDescription description);

    /**
     * Sets the header description from a plain string.
     *
     * @param text the description text (not null)
     * @return this configurator for chaining
     */
    C description(String text);

    /**
     * Sets the header description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator for chaining
     */
    C description(Localizable localizable);

    // -----------------------------------------------------------------------
    // Content
    // -----------------------------------------------------------------------

    /**
     * Sets the body content of the sheet.
     * Replaces any previously added content.
     *
     * @param components the components to place in the sheet body (not null)
     * @return this configurator for chaining
     */
    C content(Component... components);

    // -----------------------------------------------------------------------
    // Behaviour
    // -----------------------------------------------------------------------

    /**
     * Sets whether the semi-transparent backdrop is shown behind the panel when the sheet opens.
     *
     * <ul>
     *   <li>{@code true} (default) — backdrop dims the content behind the sheet</li>
     *   <li>{@code false} — the sheet slides in with no dimming; useful for persistent side
     *       panels or filter drawers where the user still needs to see the background</li>
     * </ul>
     *
     * @param backdropVisible {@code true} to show the dimming backdrop
     * @return this configurator for chaining
     */
    C backdropVisible(boolean backdropVisible);

    /**
     * Sets whether clicking the backdrop closes the sheet. Default: {@code true}.
     *
     * @param closeOnBackdropClick {@code true} to close on backdrop click
     * @return this configurator for chaining
     */
    C closeOnBackdropClick(boolean closeOnBackdropClick);

    /**
     * Sets whether the sheet integrates with the browser History API so the hardware back button
     * closes the sheet. Default: {@code true}.
     *
     * @param historyEnabled {@code true} to enable History API integration
     * @return this configurator for chaining
     */
    C historyEnabled(boolean historyEnabled);

    /**
     * Sets whether the sheet panel covers the full viewport on mobile-width screens (≤ 767 px).
     * On wider screens the sheet retains its normal dimensions.
     *
     * <p>Adds/removes the CSS modifier class {@code sheet--fullscreen-mobile}; all visual
     * rules live in {@code sheet.css}.</p>
     *
     * @param fullscreen {@code true} to enable full-viewport mode on mobile
     * @return this configurator for chaining
     */
    C fullscreenOnMobile(boolean fullscreen);

    /**
     * Controls whether the panel (and backdrop) start below the application header on
     * non-mobile screens (≥ 768 px), so the AppBar and its navigation buttons remain visible.
     *
     * <ul>
     *   <li>{@code false} (default) — panel covers the full viewport height</li>
     *   <li>{@code true} — panel starts below the AppBar; reads
     *       {@code --vaadin-app-layout-navbar-offset-top} from CSS (set automatically by
     *       Vaadin {@code AppLayout}; for custom AppBars content
     *       {@code :root &#123; --vaadin-app-layout-navbar-offset-top: 64px; &#125;} to your
     *       application CSS). Has no effect on mobile or {@link Sheet.Side#BOTTOM} sheets.</li>
     * </ul>
     *
     * @param belowHeader {@code true} to keep the AppBar visible above the sheet
     * @return this configurator for chaining
     */
    C belowHeader(boolean belowHeader);

    /**
     * Controls the visibility of the back navigation button in the sheet header.
     *
     * <p>The back button calls {@code history.back()} and closes the sheet via the existing
     * History API bridge (same behaviour as the hardware back button on Android).</p>
     *
     * <p>Default: {@code true} (visible).</p>
     *
     * @param show {@code false} to hide the back button
     * @return this configurator for chaining
     */
    C backButton(boolean show);

    /**
     * Controls the visibility of the close button in the sheet header.
     *
     * <p>The close button explicitly closes the sheet and pops the pushed history entry
     * so the browser URL is restored to its origin state.</p>
     *
     * <p>Default: {@code true} (visible).</p>
     *
     * @param show {@code false} to hide the close button
     * @return this configurator for chaining
     */
    C closeButton(boolean show);

    // -----------------------------------------------------------------------
    // Lazy content
    // -----------------------------------------------------------------------

    /**
     * Sets a lazy-content supplier invoked exactly once on the first {@link Sheet#open()} call.
     * After the supplier runs its result is added to the content slot and the supplier reference
     * is discarded; subsequent opens reuse the already-rendered DOM tree.
     *
     * <p><strong>SaaS / 10 K-user pattern:</strong> pair this with a {@code @Cacheable} service
     * method to avoid redundant DB round-trips when the user opens and closes the sheet
     * repeatedly across a session.</p>
     *
     * <pre>{@code
     * Sheet.builder()
     *     .title("Order details")
     *     .lazyContent(() -> new Component[]{ orderDetailForm.build(service.findById(id)) })
     *     .build();
     * }</pre>
     *
     * @param supplier supplier returning the initial content components; always called on the
     *                 Vaadin UI thread — no synchronization required
     * @return this configurator for chaining
     */
    C lazyContent(Supplier<Component[]> supplier);

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    /**
     * Registers the callback fired immediately after the sheet opens.
     *
     * @param onOpen the callback (not null)
     * @return this configurator for chaining
     */
    C onOpen(Runnable onOpen);

    /**
     * Registers the callback fired immediately after the sheet closes (via any mechanism —
     * explicit {@link Sheet#close()}, backdrop click, or hardware back button).
     *
     * @param onClose the callback (not null)
     * @return this configurator for chaining
     */
    C onClose(Runnable onClose);

    /**
     * Sets a custom footer component for the sheet.
     *
     * @param footer the footer component to display below the content (not null)
     * @return this configurator for chaining
     */
    C footer(Footer footer);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a configurator for an existing {@link Sheet} instance.
     *
     * @param sheet the sheet to configure (not null)
     * @return a {@link BaseSheetConfigurator}
     */
    static BaseSheetConfigurator configure(Sheet sheet) {
        return new DefaultSheetConfigurator(sheet);
    }

    /**
     * Base configurator type returned by {@link #configure(Sheet)}.
     */
    interface BaseSheetConfigurator extends SheetConfigurator<BaseSheetConfigurator> {
    }
}

