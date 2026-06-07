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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertDialogConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.BooleanSupplier;

/**
 * Configurator for {@link AlertDialog} — the shadcn/ui + Tailwind UI-inspired confirmation dialog.
 *
 * @param <C> Concrete configurator type
 */
public interface AlertDialogConfigurator<C extends AlertDialogConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Content
    // -----------------------------------------------------------------------

    /** Sets the dialog title from a plain string. */
    C title(String text);

    /** Sets the dialog title from a Holon {@link Localizable}. */
    C title(Localizable localizable);

    /** Sets the dialog description from a plain string. */
    C description(String text);

    /** Sets the dialog description from a Holon {@link Localizable}. */
    C description(Localizable localizable);

    // -----------------------------------------------------------------------
    // Header icon (Tailwind UI colored-badge pattern)
    // -----------------------------------------------------------------------

    /**
     * Sets the optional header icon displayed inside a neutral circular badge above
     * or beside the title, following the Tailwind UI "with icon" modal pattern.
     *
     * @param icon the icon component (not null)
     */
    C headerIcon(Component icon);

    /**
     * Sets the optional header icon <em>and</em> applies a semantic colour to the
     * badge circle that matches the dialog operation's intent.
     *
     * <pre>{@code
     * AlertDialog.builder()
     *     .title("Delete account?")
     *     .headerIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE)
     *     .variant(Alert.Variant.DESTRUCTIVE)
     *     .onConfirm(() -> accountService.delete(currentUser))
     *     .open();
     * }</pre>
     *
     * @param icon    the icon component (not null)
     * @param variant semantic colour for the badge background
     */
    C headerIcon(Component icon, Alert.Variant variant);

    // -----------------------------------------------------------------------
    // Size
    // -----------------------------------------------------------------------

    /**
     * Sets the width preset for the dialog overlay panel.
     *
     * @param size the desired size preset (null or {@link AlertDialog.Size#MD} = default)
     */
    C size(AlertDialog.Size size);

    // -----------------------------------------------------------------------
    // Alignment
    // -----------------------------------------------------------------------

    /**
     * Sets the horizontal alignment of the header content and footer buttons.
     *
     * @param alignment {@link AlertDialog.Alignment#LEFT} (default) or
     *                  {@link AlertDialog.Alignment#CENTER}
     */
    C alignment(AlertDialog.Alignment alignment);

    /**
     * Centers the icon badge, title, description and footer buttons.
     * Shorthand for {@code alignment(AlertDialog.Alignment.CENTER)}.
     */
    default C centered() {
        return alignment(AlertDialog.Alignment.CENTER);
    }

    // -----------------------------------------------------------------------
    // Body content slot (Tailwind UI scrollable body pattern)
    // -----------------------------------------------------------------------

    /**
     * Adds arbitrary components to the scrollable body content slot that sits
     * between the header and the footer buttons.
     *
     * @param components the components to content (not null)
     */
    C bodyContent(Component... components);

    // -----------------------------------------------------------------------
    // Buttons
    // -----------------------------------------------------------------------

    /** Sets the cancel button label. Default: "Cancel". */
    C cancelText(String text);

    /** Sets the cancel button label from a Holon {@link Localizable}. */
    C cancelText(Localizable localizable);

    /** Sets the confirm/action button label. Default: "Continue". */
    C confirmText(String text);

    /** Sets the confirm/action button label from a Holon {@link Localizable}. */
    C confirmText(Localizable localizable);

    /**
     * Registers an <strong>unconditional</strong> callback fired when the confirm/action button
     * is clicked. The dialog is always closed after the callback, even if it throws.
     *
     * @see #onConfirm(BooleanSupplier)
     */
    C onConfirm(Runnable action);

    /**
     * Registers a <strong>conditional</strong> confirm handler.
     *
     * <p>The dialog closes only when the supplier returns {@code true}.
     * Return {@code false} (or throw) to keep the dialog open — useful when the
     * action can fail and the user should be allowed to retry.</p>
     *
     * <pre>{@code
     * .onConfirm(() -> {
     *     try {
     *         service.delete(id);
     *         return true;   // success → close
     *     } catch (ServiceException ex) {
     *         Notification.show(ex.getMessage());
     *         return false;  // failure → stay open
     *     }
     * })
     * }</pre>
     *
     * @see #onConfirm(Runnable)
     */
    C onConfirm(BooleanSupplier condition);

    /** Registers the callback fired when the cancel button is clicked. */
    C onCancel(Runnable action);

    /**
     * Applies a semantic variant colour to the action (confirm) button so its appearance
     * communicates the intent of the operation.
     *
     * <ul>
     *   <li>{@link Alert.Variant#DEFAULT}     — primary dark (default)</li>
     *   <li>{@link Alert.Variant#DESTRUCTIVE} — red, for dangerous/irreversible operations</li>
     *   <li>{@link Alert.Variant#WARNING}     — amber, for cautionary operations</li>
     *   <li>{@link Alert.Variant#SUCCESS}     — green, for positive confirmations</li>
     *   <li>{@link Alert.Variant#INFO}        — blue, for neutral confirmations</li>
     * </ul>
     *
     * @param variant the variant to apply (not null)
     */
    C variant(Alert.Variant variant);

    /**
     * Shows or hides the cancel button.
     *
     * <p>Set to {@code false} for "I acknowledge" / "Got it" dialogs that only
     * need a single confirm button.</p>
     *
     * @param visible {@code true} (default) to show the cancel button
     */
    C withCancelButton(boolean visible);

    /**
     * Adds a secondary action button in the footer between the cancel button and the
     * primary confirm button, following the Tailwind UI multi-action footer pattern.
     *
     * <pre>{@code
     * AlertDialog.builder()
     *     .title("Move or delete?")
     *     .secondaryAction("Move to archive", () -> service.archive(id))
     *     .confirmText("Delete permanently")
     *     .variant(Alert.Variant.DESTRUCTIVE)
     *     .onConfirm(() -> service.delete(id))
     *     .open();
     * }</pre>
     *
     * @param text     the button label (not null)
     * @param onAction the callback invoked on click (not null)
     */
    C secondaryAction(String text, Runnable onAction);

    /**
     * Adds a secondary action button from a Holon {@link Localizable}.
     *
     * @param localizable the localizable label (not null)
     * @param onAction    the callback invoked on click (not null)
     */
    C secondaryAction(Localizable localizable, Runnable onAction);

    // -----------------------------------------------------------------------
    // Dialog behaviour
    // -----------------------------------------------------------------------

    /** Sets whether ESC closes the dialog. Default: {@code false}. */
    C closeOnEsc(boolean closeOnEsc);

    /** Sets whether clicking outside closes the dialog. Default: {@code false}. */
    C closeOnOutsideClick(boolean closeOnOutsideClick);

    /** Sets whether the dialog is draggable. Default: {@code false}. */
    C draggable(boolean draggable);

    /** Sets whether the dialog is resizable. Default: {@code false}. */
    C resizable(boolean resizable);

    /** Adds a listener notified when the dialog's open/closed state changes. */
    C withOpenedChangeListener(ComponentEventListener<Dialog.OpenedChangeEvent> listener);

    // -----------------------------------------------------------------------
    // Loading state
    // -----------------------------------------------------------------------

    /**
     * Puts the confirm/action button into a loading state (spinner icon + disabled),
     * following the Tailwind UI async-action modal pattern.
     *
     * @param loading {@code true} to show the spinner and disable the button
     */
    C loading(boolean loading);

    // -----------------------------------------------------------------------
    // Layout modifiers
    // -----------------------------------------------------------------------

    /**
     * Forces the footer buttons into a stacked (column) layout on all screen sizes,
     * overriding the default desktop-row behaviour.
     *
     * @param stacked {@code true} to always stack buttons vertically
     */
    C stackedButtons(boolean stacked);

    /**
     * When {@code true}, on mobile screens (≤ 639 px) the dialog expands to full
     * viewport width and anchors to the bottom of the screen (bottom-sheet), reverting
     * to a normal centred panel on larger screens.
     *
     * @param fullScreen {@code true} to enable the full-screen-on-mobile layout
     */
    C fullScreenOnMobile(boolean fullScreen);

    /**
     * Shows or hides a subtle muted background on the footer area.
     *
     * <p>When enabled, the footer receives {@code background-color: var(--surface-1)}
     * and a hairline top border to visually separate the action buttons from the dialog body.
     * Follows the Tailwind UI modal pattern. Disabled by default.</p>
     *
     * <p>Best paired with {@link #bodyContent(Component...)} — dialogs with rich scrollable
     * body content benefit most from a visually distinct footer band.</p>
     *
     * @param enabled {@code true} to show the muted footer background
     */
    C footerBackground(boolean enabled);

    // -----------------------------------------------------------------------
    // ARIA role
    // -----------------------------------------------------------------------

    /**
     * Switches between {@code role="alertdialog"} (default, for urgent/destructive
     * confirmations) and {@code role="dialog"} (for routine, non-urgent confirmations).
     *
     * @param alertRole {@code true} (default) keeps {@code role="alertdialog"};
     *                  {@code false} downgrades to {@code role="dialog"}
     */
    C alertRole(boolean alertRole);

    // -----------------------------------------------------------------------
    // Close button
    // -----------------------------------------------------------------------

    /**
     * Shows or hides the optional header close (×) button.
     *
     * <p>By default the button is hidden — {@code AlertDialog} is intentionally
     * non-dismissible. Set to {@code true} when you want an escape hatch without a
     * full Cancel button in the footer.</p>
     *
     * @param visible {@code true} to show the close button
     */
    C withCloseButton(boolean visible);

    /**
     * Replaces the default × glyph of the header close button with the given icon component
     * <strong>and</strong> makes the button visible.
     *
     * <pre>{@code
     * AlertDialog.builder()
     *     .title("Delete account?")
     *     .closeIcon(new Icon(VaadinIcon.CLOSE_SMALL))
     *     .onConfirm(() -> accountService.delete(currentUser))
     *     .open();
     * }</pre>
     *
     * @param icon the replacement icon component (not null)
     */
    C closeIcon(Component icon);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    static BaseAlertDialogConfigurator configure(AlertDialog dialog) {
        return new DefaultAlertDialogConfigurator(dialog);
    }

    interface BaseAlertDialogConfigurator extends AlertDialogConfigurator<BaseAlertDialogConfigurator> {
    }
}

