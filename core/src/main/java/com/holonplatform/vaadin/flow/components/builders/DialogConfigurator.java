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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.Dialog.DialogResizeEvent;
import com.vaadin.flow.component.dialog.Dialog.OpenedChangeEvent;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * {@link Dialog} component configurator following the shadcn/ui three-slot model:
 *
 * <ul>
 *   <li><strong>Header</strong>  — {@link #withTitle} / {@link #withDescription}</li>
 *   <li><strong>Body</strong>    — {@link #withContent(String)}, {@link #withContent(Component...)}</li>
 *   <li><strong>Footer</strong>  — {@link #withActionButton} / {@link #withCancelButton} /
 *       {@link #withFooter(Component...)} (raw escape hatch)</li>
 * </ul>
 *
 * @param <C> Concrete configurator type
 * @since 5.2.0
 */
public interface DialogConfigurator<C extends DialogConfigurator<C>>
        extends ComponentConfigurator<C>,
                HasSizeConfigurator<C>,
                HasStyleConfigurator<C>,
                DeferrableLocalizationConfigurator<C> {

    // -----------------------------------------------------------------------
    // Body content
    // -----------------------------------------------------------------------

    /**
     * Adds a component to the dialog body (scrollable content area).
     *
     * @param component the component to add (not null)
     * @return this
     */
    C withComponent(Component component);

    /**
     * Adds a {@link HasComponent} to the dialog body.
     *
     * @param component the component to add (not null)
     * @return this
     */
    default C withComponent(HasComponent component) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withComponent(component.getComponent());
    }

    /**
     * Adds a text paragraph to the dialog body.
     *
     * @param message the display text (not null)
     * @return this
     */
    C withContent(String message);

    /**
     * Adds a localised text paragraph to the dialog body.
     * {@code message} is used as the fallback when no translation is found for {@code messageCode}.
     *
     * @param message     the fallback text
     * @param messageCode the i18n message code
     * @return this
     */
    C withContent(String message, String messageCode);

    /**
     * Adds a localised text paragraph to the dialog body.
     *
     * @param message the {@link Localizable} message (not null)
     * @return this
     */
    C withContent(Localizable message);

    /**
     * Adds one or more components to the dialog body.
     *
     * @param components the components to add
     * @return this
     */
    C withContent(Component... components);

    /**
     * Convenience alias for {@link #withContent(String)}.
     * Maintained for backward compatibility.
     *
     * @param message the display text (not null)
     * @return this
     */
    default C text(String message) {
        return withContent(message);
    }

    /**
     * Convenience alias for {@link #withContent(Localizable)}.
     * Maintained for backward compatibility.
     *
     * @param message the {@link Localizable} message (not null)
     * @return this
     */
    default C text(Localizable message) {
        return withContent(message);
    }

    // -----------------------------------------------------------------------
    // Structured header
    // -----------------------------------------------------------------------

    /**
     * Sets the dialog title rendered in the header slot.
     *
     * @param text the title text (not null)
     * @return this
     */
    C withTitle(String text);

    /**
     * Sets the dialog title from a Holon {@link Localizable}.
     * Re-resolved on each locale-change and re-attach.
     *
     * @param localizable the localizable message (not null)
     * @return this
     */
    C withTitle(Localizable localizable);

    /**
     * Sets a description paragraph rendered below the title in the header slot.
     *
     * @param text the description text (not null)
     * @return this
     */
    C withDescription(String text);

    /**
     * Sets a description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     * @return this
     */
    C withDescription(Localizable localizable);

    // -----------------------------------------------------------------------
    // Footer — explicit DSL; no boolean flags, no ambiguous Consumer overloads
    // -----------------------------------------------------------------------

    /**
     * Adds a primary action button to the dialog footer.
     * <p>Styled with {@code h-dialog__action-btn}. Automatically closes the dialog
     * (respecting any {@link #onClose(BooleanSupplier)} guard) after invoking {@code onClick}.</p>
     *
     * @param text    the button label
     * @param onClick the click handler (not null)
     * @return this
     */
    C withActionButton(String text, ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a localised primary action button to the dialog footer.
     *
     * @param text     the fallback label
     * @param textCode the i18n message code for the label
     * @param onClick  the click handler (not null)
     * @return this
     */
    C withActionButton(String text, String textCode,
                       ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a fully customisable primary action button to the dialog footer.
     * <p>The button is pre-wired to close the dialog; use the configurator to set
     * the label, variant, icon, keyboard shortcut, etc.</p>
     *
     * @param configurator the button configurator (not null)
     * @return this
     */
    C withActionButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    /**
     * Adds a default "Cancel" button to the dialog footer.
     * <p>Styled with {@code h-dialog__cancel-btn}; closes the dialog on click.</p>
     *
     * @return this
     */
    C withCancelButton();

    /**
     * Adds a cancel-style button to the dialog footer with custom configuration.
     * <p>The button is pre-wired to close the dialog; use the configurator to override
     * the label, icon, etc.</p>
     *
     * @param configurator the button configurator (not null)
     * @return this
     */
    C withCancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    /**
     * Adds a primary action button with a semantic variant to the dialog footer.
     * <p>Styled with {@code h-dialog__action-btn} plus the corresponding BEM modifier.</p>
     *
     * @param text    the button label
     * @param variant the semantic variant (not null)
     * @param onClick the click handler (not null)
     * @return this
     */
    C withActionButton(String text, ActionVariant variant,
                       ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a localised primary action button with a semantic variant to the dialog footer.
     *
     * @param text     the fallback label
     * @param textCode the i18n message code for the label
     * @param variant  the semantic variant (not null)
     * @param onClick  the click handler (not null)
     * @return this
     */
    C withActionButton(String text, String textCode, ActionVariant variant,
                       ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a fully customisable action button with a semantic variant to the dialog footer.
     * <p>The button is pre-wired to close the dialog; use the configurator to set
     * the label, icon, keyboard shortcut, etc.</p>
     *
     * @param variant      the semantic variant (not null)
     * @param configurator the button configurator (not null)
     * @return this
     */
    C withActionButton(ActionVariant variant,
                       Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    /**
     * Low-level escape hatch: adds arbitrary components directly to the footer slot.
     * <p>Prefer {@link #withActionButton} / {@link #withCancelButton} for standard buttons.</p>
     *
     * @param components the footer components
     * @return this
     */
    C withFooter(Component... components);

    // -----------------------------------------------------------------------
    // Trigger (DialogTrigger equivalent)
    // -----------------------------------------------------------------------

    /**
     * Registers an existing component as the dialog trigger.
     * <p>Clicking the trigger opens the dialog. Call {@code DialogBuilder.buildTriggered()}
     * to obtain a renderable component containing the trigger wired to the dialog.</p>
     *
     * @param trigger the trigger component (not null)
     * @return this
     */
    C withTrigger(Component trigger);

    /**
     * Creates a default button trigger with the given label.
     * <p>Styled with {@code h-dialog__trigger}.</p>
     *
     * @param text the button label (not null)
     * @return this
     */
    C withTrigger(String text);

    /**
     * Creates a customisable button trigger.
     * <p>The button is styled with {@code h-dialog__trigger}; use the configurator
     * to override label, icon, variant, etc.</p>
     *
     * @param configurator the button configurator (not null)
     * @return this
     */
    C withTrigger(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    // -----------------------------------------------------------------------
    // Action button variant
    // -----------------------------------------------------------------------

    /**
     * Semantic variant applied to the primary action button.
     * Each value maps to a BEM modifier class on {@code .h-dialog__action-btn}.
     */
    enum ActionVariant {

        /** Neutral / dark primary — default style (no modifier). */
        DEFAULT(""),

        /** Destructive (delete, irreversible) — red. */
        DESTRUCTIVE("h-dialog__action-btn--destructive"),

        /** Warning (cautionary) — amber. */
        WARNING("h-dialog__action-btn--warning"),

        /** Positive confirmation — green. */
        SUCCESS("h-dialog__action-btn--success"),

        /** Informational / neutral — blue. */
        INFO("h-dialog__action-btn--info");

        private final String cssModifier;

        ActionVariant(String cssModifier) {
            this.cssModifier = cssModifier;
        }

        /**
         * @return the BEM modifier CSS class, or empty string for {@link #DEFAULT}.
         */
        public String getCssModifier() {
            return cssModifier;
        }
    }

    // -----------------------------------------------------------------------
    // Icon variant  (Tailwind Plus "with icon" modal pattern)
    // -----------------------------------------------------------------------

    /**
     * Semantic colour variant for the dialog header icon container.
     * Each value maps to a BEM modifier on {@code .h-dialog__icon}.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * DialogBuilder.delete(cb)
     *     .withHeaderIcon(new Icon(VaadinIcon.WARNING), IconVariant.DESTRUCTIVE)
     *     .withTitle("Are you absolutely sure?")
     *     .withDescription("This action cannot be undone.")
     *     .open();
     * }</pre>
     */
    enum IconVariant {

        /** Red circle — delete / irreversible operations. */
        DESTRUCTIVE("h-dialog__icon--destructive"),

        /** Amber circle — cautionary / potentially irreversible operations. */
        WARNING("h-dialog__icon--warning"),

        /** Blue circle — informational / neutral operations. */
        INFO("h-dialog__icon--info"),

        /** Green circle — positive / success confirmations. */
        SUCCESS("h-dialog__icon--success");

        private final String cssClass;

        IconVariant(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * @return the BEM modifier CSS class for this variant
         */
        public String getCssClass() {
            return cssClass;
        }
    }

    /**
     * Adds a circular icon container to the dialog header, above the title.
     *
     * <p>The container is styled with {@code .h-dialog__icon} plus the variant-specific
     * BEM modifier (e.g. {@code .h-dialog__icon--destructive}).  Matches the
     * <a href="https://tailwindcss.com/plus/ui-blocks/application-ui/overlays/modal-dialogs">
     * Tailwind Plus "with icon" modal pattern</a>.</p>
     *
     * <p>Pair with {@link #centered()} for the fully-centred layout:</p>
     * <pre>{@code
     * DialogBuilder.delete(cb)
     *     .withHeaderIcon(new Icon(VaadinIcon.WARNING), IconVariant.DESTRUCTIVE)
     *     .withTitle("Are you absolutely sure?")
     *     .withDescription("This action cannot be undone.")
     *     .centered()
     *     .open();
     * }</pre>
     *
     * @param icon    the icon component to display inside the circle (not null)
     * @param variant the colour variant (not null)
     * @return this
     */
    C withHeaderIcon(Component icon, IconVariant variant);

    /**
     * Activates the centred-content layout variant.
     *
     * <p>When called:</p>
     * <ul>
     *   <li>Header content (icon → title → description) is stacked vertically and centred.</li>
     *   <li>The header close button is hidden — dialogs with centred content rely on
     *       footer action buttons to dismiss.</li>
     *   <li>On desktop, footer buttons are centred (not flush-left / flush-right).</li>
     * </ul>
     *
     * <p>Matches the <a href="https://tailwindcss.com/plus/ui-blocks/application-ui/overlays/modal-dialogs">
     * Tailwind Plus centred modal pattern</a>.</p>
     *
     * @return this
     */
    C centered();

    // -----------------------------------------------------------------------
    // Close button control  (shadcn/ui DialogClose pattern)
    // -----------------------------------------------------------------------

    /**
     * Shows or hides the header close (×) button.
     *
     * <p>Defaults to {@code true} (visible). Set to {@code false} for confirmation
     * dialogs that force an explicit choice — e.g. delete / question dialogs — or
     * whenever {@link #centered()} is used (centred mode auto-hides it).</p>
     *
     * <p>Mirrors the shadcn/ui pattern where {@code AlertDialog} omits the close icon
     * to prevent accidental dismissal.</p>
     *
     * @param visible {@code true} to show the button, {@code false} to hide it
     * @return this
     */
    C withCloseButton(boolean visible);

    /**
     * Customises the header close (×) button without changing its visibility.
     *
     * <p>Use to override the icon glyph, {@code aria-label}, tooltip, additional CSS
     * classes, etc. The button's close-on-click wiring is always preserved.</p>
     *
     * @param configurator the button configurator (not null)
     * @return this
     */
    C withCloseButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    /**
     * Replaces the default × glyph of the header close button with the given icon component.
     *
     * <p>Use this when the application provides its own icon library (e.g. Material Symbols,
     * Lucide, or a custom SVG span). The button visibility and close-on-click wiring are
     * always preserved.</p>
     *
     * <pre>{@code
     * DialogBuilder.confirm()
     *     .closeIcon(new Icon(VaadinIcon.CLOSE_SMALL))
     *     .withTitle("Confirm action")
     *     .open();
     * }</pre>
     *
     * @param icon the replacement icon component (not null)
     * @return this
     */
    C closeIcon(Component icon);

    // -----------------------------------------------------------------------
    // Size variants  (shadcn/ui {@code size} prop + Tailwind Plus max-w-*)
    // -----------------------------------------------------------------------

    /**
     * Sets the dialog width to one of the predefined size variants.
     *
     * <p>Applies a {@code theme~="h-dialog--*"} modifier to the overlay element,
     * which overrides {@code --vaadin-dialog-overlay-width} for desktop breakpoints
     * (≥ 640 px). On mobile the dialog always expands to the full viewport width
     * as a bottom sheet — size variants have no effect there.</p>
     *
     * <p>Without calling this method the dialog uses the default width
     * ({@link DialogSize#MD} — 32 rem).</p>
     *
     * @param size the size variant (not null)
     * @return this
     */
    C withSize(DialogSize size);

    /**
     * Predefined dialog width variants inspired by the shadcn/ui {@code size} prop
     * and Tailwind Plus {@code max-w-*} utility classes.
     *
     * <p>Each value maps to a BEM theme modifier on the overlay ({@code h-dialog--*})
     * which overrides {@code --vaadin-dialog-overlay-width} at the desktop breakpoint.</p>
     */
    enum DialogSize {

        /**
         * 24 rem (384 px) — compact alerts and simple info dialogs.
         * Equivalent to Tailwind {@code max-w-sm}.
         */
        SM("h-dialog--sm"),

        /**
         * 32 rem (512 px) — default width (no-size baseline).
         * Equivalent to Tailwind {@code max-w-lg}.
         */
        MD("h-dialog--md"),

        /**
         * 42 rem (672 px) — form dialogs with several input fields.
         * Equivalent to Tailwind {@code max-w-2xl}.
         */
        LG("h-dialog--lg"),

        /**
         * 56 rem (896 px) — wide content, data tables, multi-column forms.
         * Equivalent to Tailwind {@code max-w-4xl}.
         */
        XL("h-dialog--xl"),

        /**
         * 95 vw — nearly full-screen for complex, data-dense layouts.
         * Equivalent to Tailwind {@code max-w-[95vw]}.
         */
        FULL("h-dialog--full");

        private final String themeModifier;

        DialogSize(String themeModifier) {
            this.themeModifier = themeModifier;
        }

        /**
         * @return the BEM theme modifier string added to the overlay element
         */
        public String getThemeModifier() {
            return themeModifier;
        }
    }

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

    /**
     * Registers a callback fired immediately after the dialog opens.
     *
     * @param onOpen the callback (not null)
     * @return this
     */
    C onOpen(Runnable onOpen);

    /**
     * Registers an unconditional close callback.
     * The dialog always closes; the callback fires after closing completes.
     * Clears any previously registered {@link BooleanSupplier} guard.
     *
     * @param onClose the callback (not null)
     * @return this
     */
    C onClose(Runnable onClose);

    /**
     * Registers a conditional close guard.
     * <p>The dialog closes only when the supplier returns {@code true}.
     * Return {@code false} to keep the dialog open — useful for form validation
     * and unsaved-changes guards.</p>
     * <p>Clears any previously registered {@link Runnable}.</p>
     *
     * @param onClose the conditional handler (not null)
     * @return this
     */
    C onClose(BooleanSupplier onClose);

    /**
     * Adds a listener for {@code opened-changed} events.
     *
     * @param listener the listener (not null)
     * @return this
     */
    C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener);

    /**
     * Adds a listener called after the user finishes resizing the overlay.
     * Only fired when resizing is enabled.
     *
     * @param listener the listener (not null)
     * @return this
     * @since 5.5.0
     */
    C withResizeListener(ComponentEventListener<DialogResizeEvent> listener);

    // -----------------------------------------------------------------------
    // Behaviour
    // -----------------------------------------------------------------------

    /**
     * Enables or disables user-driven resizing of the overlay.
     *
     * @param resizable {@code true} to allow resizing
     * @return this
     * @since 5.5.0
     */
    C resizable(boolean resizable);

    /**
     * Enables or disables user-driven dragging of the overlay.
     *
     * @param draggable {@code true} to allow dragging
     * @return this
     * @since 5.5.0
     */
    C draggable(boolean draggable);

    /**
     * Sets whether the dialog is modal (strict) or modeless.
     *
     * @param modal {@code true} for modal / strict, {@code false} for modeless
     * @return this
     * @since 5.5.0
     */
    C modal(boolean modal);

    /**
     * Applies responsive sizing via CSS viewport units.
     * Mobile (&lt; 640 px): 95 % of viewport. Desktop: capped at 900 × 600 px.
     *
     * @return this
     */
    C makeDialogResponsive();

    // -----------------------------------------------------------------------
    // Closable sub-interface
    // -----------------------------------------------------------------------

    /**
     * Extended configurator that also exposes ESC and outside-click close controls.
     *
     * @param <C> Concrete configurator type
     * @since 5.2.0
     */
    interface ClosableDialogConfigurator<C extends ClosableDialogConfigurator<C>>
            extends DialogConfigurator<C> {

        /**
         * Sets whether the dialog can be closed by pressing Escape.
         *
         * @param closeOnEsc {@code true} to close on Escape (default: {@code true})
         * @return this
         */
        C closeOnEsc(boolean closeOnEsc);

        /**
         * Sets whether the dialog can be closed by clicking outside it.
         *
         * @param closeOnOutsideClick {@code true} to close on outside click (default: {@code true})
         * @return this
         */
        C closeOnOutsideClick(boolean closeOnOutsideClick);

    }

}
