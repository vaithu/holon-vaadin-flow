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

import java.io.Serial;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.AlertDialogBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

/**
 * Confirmation alert dialog inspired by shadcn/ui {@code AlertDialog} and
 * Tailwind UI Plus modal dialog patterns.
 *
 * <p>Use this component when an action <strong>requires explicit user confirmation</strong>
 * â€” typically for dangerous or irreversible operations such as deletion or sign-out.
 * The dialog is intentionally <strong>non-dismissible</strong> by default (no ESC, no
 * click-outside) to force an explicit choice.</p>
 *
 * <p>Composition:</p>
 * <pre>
 * AlertDialog  (Dialog overlay)
 *  â”œâ”€â”€ Header      (.alert-dialog__header)
 *  â”‚    â”œâ”€â”€ IconWrapper  (.alert-dialog__icon-wrapper)  [optional â€” Tailwind UI icon badge]
 *  â”‚    â””â”€â”€ HeaderText
 *  â”‚         â”œâ”€â”€ Title       (.alert-dialog__title)
 *  â”‚         â””â”€â”€ Description (.alert-dialog__description)
 *  â”œâ”€â”€ Body        (.alert-dialog__body)               [optional â€” scrollable content slot]
 *  â””â”€â”€ Footer      (.alert-dialog__footer)
 *       â”œâ”€â”€ Cancel button          (.alert-dialog__cancel)
 *       â”œâ”€â”€ Secondary action button(.alert-dialog__secondary-action)  [optional]
 *       â””â”€â”€ Primary action button  (.alert-dialog__action)
 * </pre>
 *
 * <p>Preferred usage via builder:</p>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("Are you absolutely sure?")
 *     .description("This action cannot be undone. This will permanently delete your account.")
 *     .headerIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE)
 *     .size(AlertDialog.Size.LG)
 *     .cancelText("Cancel")
 *     .confirmText("Yes, delete account")
 *     .variant(Alert.Variant.DESTRUCTIVE)
 *     .onConfirm(() -> accountService.delete(currentUser))
 *     .build()
 *     .open();
 * }</pre>
 *
 * <p>For a dismissible modal notification use {@link AlertModal}.</p>
 * <p>For an inline notification banner use {@link Alert}.</p>
 *
 * @see AlertDialogBuilder
 * @see AlertModal
 * @see Alert
 */
@StyleSheet("context://alert-dialog.css")
public class AlertDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Auto-incrementing counter used to generate unique ARIA IDs per dialog instance. */
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();

    // -----------------------------------------------------------------------
    // Enums
    // -----------------------------------------------------------------------

    /**
     * Width preset for the dialog overlay panel.
     *
     * <ul>
     *   <li>{@link #SM}  â€” compact, ~24 rem  (384 px)</li>
     *   <li>{@link #MD}  â€” default, ~28 rem  (448 px)</li>
     *   <li>{@link #LG}  â€” spacious, ~32 rem (512 px)</li>
     *   <li>{@link #XL}  â€” wide, ~36 rem     (576 px)</li>
     *   <li>{@link #XL2} â€” extra-wide, ~42 rem (672 px)</li>
     * </ul>
     */
    public enum Size {
        SM, MD, LG, XL, XL2
    }

    /**
     * Horizontal alignment of the header content (icon + title + description).
     *
     * <ul>
     *   <li>{@link #LEFT}   â€” left-aligned text, icon on the left side on desktop (default)</li>
     *   <li>{@link #CENTER} â€” all content centred â€” best paired with a header icon</li>
     * </ul>
     */
    public enum Alignment {
        LEFT, CENTER
    }

    // -----------------------------------------------------------------------
    // Sub-component slots
    // -----------------------------------------------------------------------

    private final H4     titleEl;
    private final Div    descriptionEl;
    private final Button cancelButton;
    private final Button secondaryActionButton;
    private final Button actionButton;

    /** Optional icon badge in the header â€” hidden by default. */
    private final Div    iconWrapper;

    /** Optional scrollable body content slot â€” hidden by default. */
    private final Div    bodyEl;

    /** Optional header close (Ã—) button â€” hidden by default. */
    private final Button closeButton;

    /** Wrapper div â€” receives modifier classes such as .alert-dialog--closeable. */
    private final Div wrapper;

    // -----------------------------------------------------------------------
    // i18n state (re-resolved on re-attach)
    // -----------------------------------------------------------------------

    private Localizable titleLocalizable;
    private Localizable descriptionLocalizable;
    private Localizable cancelLocalizable;
    private Localizable confirmLocalizable;
    private Localizable secondaryActionLocalizable;

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    /** Unconditional confirm callback â€” dialog always closes after the Runnable completes. */
    private Runnable onConfirmCallback;

    /**
     * Conditional confirm handler â€” dialog closes only when the supplier returns {@code true}.
     * Mutually exclusive with {@link #onConfirmCallback}; setting one clears the other.
     */
    private BooleanSupplier onConfirmCondition;

    private Runnable onCancelCallback;
    private Runnable onSecondaryActionCallback;

    /** CSS modifier class currently applied to the action button (null = default/primary). */
    private String currentActionVariantClass;

    /** Theme value currently applied for the size preset (null = MD default). */
    private String currentSizeTheme;

    /** CSS modifier class currently applied to the icon wrapper (null = default neutral). */
    private String currentIconVariantClass;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an {@link AlertDialog} with default (empty) content.
     * Configure via {@link #builder()} or the setter methods before calling {@link #open()}.
     */
    public AlertDialog() {

        // Unique ARIA IDs for this instance
        String instanceId  = "alert-dialog-" + INSTANCE_COUNTER.incrementAndGet();
        String titleId     = instanceId + "-title";
        String descId      = instanceId + "-desc";

        // ---- structure ----
        this.wrapper = Components.div().styleName("alert-dialog").build();

        Div header = Components.div().styleName("alert-dialog__header").build();

        // Optional icon badge (Tailwind UI colored-circle pattern) â€” hidden by default
        this.iconWrapper = Components.div().styleName("alert-dialog__icon-wrapper").visible(false).build();

        // Text column (title + description)
        Div headerText = Components.div().styleName("alert-dialog__header-text").build();

        this.titleEl = Components.h4().id(titleId).styleName("alert-dialog__title").visible(false).build();

        this.descriptionEl = Components.div().id(descId).styleName("alert-dialog__description").visible(false).build();

        headerText.add(titleEl, descriptionEl);
        header.add(this.iconWrapper, headerText);

        // Optional close button â€” absolutely positioned at wrapper root so it does
        // not disturb the header layout.
        this.closeButton = Components.button()
                .icon(VaadinIcon.CLOSE_SMALL)
                .styleName("alert-dialog__close-btn")
                .ariaLabel(LocalizationProvider.localize("Close", "alert_dialog.close_aria"))
                .withClickListener(e -> close())
                .build();

        // Optional scrollable body content slot â€” hidden by default
        this.bodyEl = Components.div().styleName("alert-dialog__body").visible(false).build();

        // Footer
        Div footer = Components.div().styleName("alert-dialog__footer").build();

        this.cancelButton = Components.button()
                .text(LocalizationProvider.localize("Cancel", "alert_dialog.cancel_btn"))
                .styleName("alert-dialog__cancel")
                .withClickListener(e -> {
                    try {
                        if (this.onCancelCallback != null) this.onCancelCallback.run();
                    } finally {
                        close();
                    }
                })
                .build();

        // Secondary action button (optional, hidden by default)
        this.secondaryActionButton = Components.button()
                .styleName("alert-dialog__secondary-action")
                .visible(false)
                .withClickListener(e -> {
                    try {
                        if (this.onSecondaryActionCallback != null) this.onSecondaryActionCallback.run();
                    } finally {
                        close();
                    }
                })
                .build();

        this.actionButton = Components.button()
                .text(LocalizationProvider.localize("Continue", "alert_dialog.confirm_btn"))
                .styleName("alert-dialog__action")
                .primary()
                .withClickListener(e -> {
                    if (this.onConfirmCondition != null) {
                        if (this.onConfirmCondition.getAsBoolean()) {
                            close();
                        }
                    } else {
                        try {
                            if (this.onConfirmCallback != null) this.onConfirmCallback.run();
                        } finally {
                            close();
                        }
                    }
                })
                .build();

        footer.add(cancelButton, secondaryActionButton, actionButton);
        this.wrapper.add(header, bodyEl, footer, this.closeButton);
        add(this.wrapper);

        // shadcn/ui AlertDialog is intentionally non-dismissible by default.
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        // ARIA semantics â€” role="alertdialog" for urgent/destructive confirmations
        // (per WAI-ARIA spec); aria-labelledby + aria-describedby for screen readers.
        getElement().setAttribute("role", "alertdialog");
        getElement().setAttribute("aria-modal", "true");
        getElement().setAttribute("aria-labelledby", titleId);
        getElement().setAttribute("aria-describedby", descId);

        // Mirror theme attribute to vaadin-dialog-overlay so that
        // vaadin-dialog-overlay[theme~="alert-dialog"]::part(*) selectors in
        // alert-dialog.css are applied to this dialog's overlay.
        getElement().getThemeList().add("alert-dialog");
    }

    /**
     * Obtain an {@link AlertDialogBuilder}.
     *
     * @return a new {@link AlertDialogBuilder}
     */
    public static AlertDialogBuilder builder() {
        return AlertDialogBuilder.create();
    }

    // -----------------------------------------------------------------------
    // Header icon API (Tailwind UI colored-badge pattern)
    // -----------------------------------------------------------------------

    /**
     * Sets the optional header icon â€” displayed inside a neutral colored circle
     * at the top of the header, following the Tailwind UI "with icon" modal pattern.
     *
     * <p>Call {@link #setHeaderIcon(Component, Alert.Variant)} to automatically
     * apply the matching variant colour to the badge background.</p>
     *
     * @param icon the icon component to show (not null)
     */
    public void setHeaderIcon(Component icon) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        this.iconWrapper.removeAll();
        this.iconWrapper.add(icon);
        this.iconWrapper.setVisible(true);
        this.wrapper.addClassName("alert-dialog--has-icon");
    }

    /**
     * Sets the optional header icon <em>and</em> applies a semantic colour to the
     * badge circle that matches the dialog's operation variant.
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
     * @param icon    the icon component to show (not null)
     * @param variant semantic colour variant for the badge background (null = neutral)
     */
    public void setHeaderIcon(Component icon, Alert.Variant variant) {
        setHeaderIcon(icon);
        if (this.currentIconVariantClass != null) {
            this.iconWrapper.removeClassName(this.currentIconVariantClass);
            this.currentIconVariantClass = null;
        }
        if (variant != null && variant != Alert.Variant.DEFAULT) {
            String cls = switch (variant) {
                case DESTRUCTIVE -> "alert-dialog__icon-wrapper--destructive";
                case WARNING     -> "alert-dialog__icon-wrapper--warning";
                case SUCCESS     -> "alert-dialog__icon-wrapper--success";
                case INFO        -> "alert-dialog__icon-wrapper--info";
                default          -> null;
            };
            if (cls != null) {
                this.iconWrapper.addClassName(cls);
                this.currentIconVariantClass = cls;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Size API
    // -----------------------------------------------------------------------

    /**
     * Sets the width preset for the dialog overlay panel.
     *
     * <p>Uses the Vaadin {@code --vaadin-dialog-overlay-width} custom property
     * via the theme attribute mirrored to the overlay element.</p>
     *
     * @param size the desired size preset (null or {@link Size#MD} resets to default)
     */
    public void setSize(Size size) {
        if (this.currentSizeTheme != null) {
            getElement().getThemeList().remove(this.currentSizeTheme);
            this.currentSizeTheme = null;
        }
        if (size == null || size == Size.MD) return;
        this.currentSizeTheme = switch (size) {
            case SM  -> "alert-dialog-sm";
            case LG  -> "alert-dialog-lg";
            case XL  -> "alert-dialog-xl";
            case XL2 -> "alert-dialog-2xl";
            default  -> null;
        };
        if (this.currentSizeTheme != null) {
            getElement().getThemeList().add(this.currentSizeTheme);
        }
    }

    // -----------------------------------------------------------------------
    // Alignment API
    // -----------------------------------------------------------------------

    /**
     * Sets the horizontal alignment of the header content and footer buttons.
     *
     * <p>{@link Alignment#CENTER} centres the icon badge, title, description, and
     * footer buttons â€” the recommended layout when a header icon is used on its own
     * without long description text.</p>
     *
     * @param alignment the desired alignment (null is treated as {@link Alignment#LEFT})
     */
    public void setAlignment(Alignment alignment) {
        if (alignment == Alignment.CENTER) {
            this.wrapper.addClassName("alert-dialog--centered");
        } else {
            this.wrapper.removeClassName("alert-dialog--centered");
        }
    }

    // -----------------------------------------------------------------------
    // Body content slot API (Tailwind UI scrollable body pattern)
    // -----------------------------------------------------------------------

    /**
     * Adds arbitrary components to the scrollable body content slot that sits
     * between the header (title/description) and the footer (buttons).
     *
     * <p>The body slot is hidden until the first call to this method.
     * It is independently scrollable when its content overflows.</p>
     *
     * @param components the components to content (not null)
     */
    public void addBodyContent(Component... components) {
        this.bodyEl.add(components);
        this.bodyEl.setVisible(true);
    }

    /**
     * Removes all components from the body content slot and hides the slot.
     */
    public void clearBodyContent() {
        this.bodyEl.removeAll();
        this.bodyEl.setVisible(false);
    }

    // -----------------------------------------------------------------------
    // Loading state API (Tailwind UI async-action pattern)
    // -----------------------------------------------------------------------

    /**
     * Puts the action (confirm) button into a loading state, showing a spinner
     * icon and disabling the button to prevent double-submission.
     *
     * <p>Revert by calling {@code setLoading(false)}.  Any icon previously set
     * on the action button will be restored to {@code null} on revert.</p>
     *
     * @param loading {@code true} to show the spinner and disable the button
     */
    public void setLoading(boolean loading) {
        if (loading) {
            Icon spinner = new Icon(VaadinIcon.REFRESH);
            spinner.addClassName("alert-dialog__spinner");
            this.actionButton.setIcon(spinner);
            this.actionButton.addClassName("alert-dialog__action--loading");
            this.actionButton.setEnabled(false);
        } else {
            this.actionButton.setIcon(null);
            this.actionButton.removeClassName("alert-dialog__action--loading");
            this.actionButton.setEnabled(true);
        }
    }

    // -----------------------------------------------------------------------
    // Full-screen on mobile API (Tailwind UI bottom-sheet pattern)
    // -----------------------------------------------------------------------

    /**
     * When {@code true}, on mobile screens (â‰¤ 639 px) the dialog expands to the full
     * viewport width and anchors to the bottom of the screen (bottom-sheet pattern),
     * reverting to a normal centred panel on larger screens.
     *
     * @param fullScreen {@code true} to enable the full-screen-on-mobile layout
     */
    public void setFullScreenOnMobile(boolean fullScreen) {
        if (fullScreen) {
            getElement().getThemeList().add("alert-dialog-fullscreen-mobile");
        } else {
            getElement().getThemeList().remove("alert-dialog-fullscreen-mobile");
        }
    }

    // -----------------------------------------------------------------------
    // Footer background API
    // -----------------------------------------------------------------------

    /**
     * Shows or hides a subtle muted background on the footer area.
     *
     * <p>When enabled, the footer receives {@code background-color: var(--surface-1)}
     * and a hairline {@code border-top: 1px solid var(--zinc-100)} to visually separate
     * the action buttons from the dialog body â€” following the Tailwind UI modal pattern.</p>
     *
     * <p>Disabled by default. Enable for dialogs with rich body content ({@link #addBodyContent})
     * or when a clear visual break between content and actions is desired.</p>
     *
     * @param enabled {@code true} to show the muted footer background
     */
    public void setFooterBackground(boolean enabled) {
        if (enabled) {
            this.wrapper.addClassName("alert-dialog--footer-bg");
        } else {
            this.wrapper.removeClassName("alert-dialog--footer-bg");
        }
    }

    // -----------------------------------------------------------------------
    // Stacked buttons API
    // -----------------------------------------------------------------------

    /**
     * Forces the footer buttons into a stacked (column) layout on all screen sizes,
     * overriding the default behaviour where they become a row on desktop.
     *
     * <p>Useful when button labels are long, or the dialog is narrow ({@link Size#SM}).</p>
     *
     * @param stacked {@code true} to always stack buttons vertically
     */
    public void setStackedButtons(boolean stacked) {
        if (stacked) {
            this.wrapper.addClassName("alert-dialog--stacked");
        } else {
            this.wrapper.removeClassName("alert-dialog--stacked");
        }
    }

    // -----------------------------------------------------------------------
    // ARIA role API
    // -----------------------------------------------------------------------

    /**
     * Switches between {@code role="alertdialog"} (default, for urgent/destructive
     * confirmations) and {@code role="dialog"} (for routine, non-urgent confirmations).
     *
     * <p>Per WAI-ARIA spec, {@code role="alertdialog"} should only be used when the
     * dialog requires an <em>immediate</em> response from the user (e.g. permanent
     * deletion). For routine confirmations like "Save changes?" prefer
     * {@code role="dialog"}.</p>
     *
     * @param alertRole {@code true} (default) to keep {@code role="alertdialog"},
     *                  {@code false} to downgrade to {@code role="dialog"}
     */
    public void setAlertRole(boolean alertRole) {
        getElement().setAttribute("role", alertRole ? "alertdialog" : "dialog");
    }

    // -----------------------------------------------------------------------
    // Cancel button visibility
    // -----------------------------------------------------------------------

    /**
     * Shows or hides the cancel button.
     *
     * <p>Set to {@code false} for "I acknowledge" style dialogs where only a single
     * confirm button is needed (e.g. "Got it", "OK").</p>
     *
     * @param visible {@code true} (default) to show the cancel button
     */
    public void setCancelButtonVisible(boolean visible) {
        this.cancelButton.setVisible(visible);
    }

    // -----------------------------------------------------------------------
    // Secondary action button API (Tailwind UI multi-action footer pattern)
    // -----------------------------------------------------------------------

    /**
     * Adds a secondary action button in the footer, positioned between the cancel
     * button and the primary confirm button.
     *
     * <p>Use for patterns like "Move to archive / Delete / Cancel" where two distinct
     * positive actions are available.  The dialog is always closed after the callback.</p>
     *
     * @param text     the button label (not null)
     * @param onAction the callback invoked on click (not null)
     */
    public void setSecondaryAction(String text, Runnable onAction) {
        this.secondaryActionLocalizable = null;
        this.secondaryActionButton.setText(text);
        this.onSecondaryActionCallback = onAction;
        this.secondaryActionButton.setVisible(true);
    }

    /**
     * Adds a secondary action button in the footer from a Holon {@link Localizable}.
     *
     * @param localizable the localizable label (not null)
     * @param onAction    the callback invoked on click (not null)
     */
    public void setSecondaryAction(Localizable localizable, Runnable onAction) {
        this.secondaryActionLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this.secondaryActionButton::setText);
        this.onSecondaryActionCallback = onAction;
        this.secondaryActionButton.setVisible(true);
    }

    /**
     * Hides the secondary action button (default state).
     */
    public void clearSecondaryAction() {
        this.secondaryActionButton.setVisible(false);
        this.onSecondaryActionCallback = null;
    }

    // -----------------------------------------------------------------------
    // Close-button API
    // -----------------------------------------------------------------------

    /**
     * Shows or hides the optional header close (Ã—) button.
     *
     * <p>By default the close button is hidden â€” {@code AlertDialog} is intentionally
     * non-dismissible (shadcn/ui pattern). Set to {@code true} when you want to offer
     * an escape hatch without adding a full Cancel button in the footer.</p>
     *
     * <p>When set to {@code true} the CSS modifier {@code alert-dialog--closeable} is
     * added to the wrapper so that {@code .alert-dialog__close-btn { display: inline-flex }}
     * takes effect.  ESC / outside-click remain controlled by their own setters.</p>
     *
     * @param visible {@code true} to show the button
     */
    public void setCloseButtonVisible(boolean visible) {
        if (visible) {
            this.wrapper.addClassName("alert-dialog--closeable");
        } else {
            this.wrapper.removeClassName("alert-dialog--closeable");
        }
    }

    /**
     * Replaces the default Ã— glyph in the header close button with the given icon component.
     *
     * <p>Call {@link #setCloseButtonVisible(boolean) setCloseButtonVisible(true)} to make the
     * button visible after setting the icon, or use the builder's {@code closeIcon(icon)} method
     * which does both in one call.</p>
     *
     * @param icon the replacement icon component (not null)
     */
    public void setCloseButtonIcon(Component icon) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        this.closeButton.setIcon(icon);
        setCloseButtonVisible(true);   // showing the button is the expected intent
    }

    // -----------------------------------------------------------------------
    // Title API
    // -----------------------------------------------------------------------

    /**
     * Returns the current rendered title text, or {@code null} if not set.
     *
     * @return the title text
     */
    public String getDialogTitle() {
        return titleEl.isVisible() ? titleEl.getText() : null;
    }

    /**
     * Sets the dialog title from a plain string.
     *
     * @param text the title text (not null)
     */
    public void setDialogTitle(String text) {
        this.titleLocalizable = null;
        applyTitle(text);
    }

    /**
     * Sets the dialog title from a Holon {@link Localizable}.
     * Resolved immediately if a localization context is active; re-resolved on each attach.
     *
     * @param localizable the localizable message (not null)
     */
    public void setDialogTitle(Localizable localizable) {
        this.titleLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::applyTitle);
    }

    private void applyTitle(String text) {
        titleEl.setText(text);
        titleEl.setVisible(text != null && !text.isBlank());
    }

    // -----------------------------------------------------------------------
    // Description API
    // -----------------------------------------------------------------------

    /**
     * Returns the current rendered description text, or {@code null} if not set.
     *
     * @return the description text
     */
    public String getDialogDescription() {
        return descriptionEl.isVisible() ? descriptionEl.getText() : null;
    }

    /**
     * Sets the dialog description from a plain string.
     *
     * @param text the description text (not null)
     */
    public void setDialogDescription(String text) {
        this.descriptionLocalizable = null;
        applyDescription(text);
    }

    /**
     * Sets the dialog description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setDialogDescription(Localizable localizable) {
        this.descriptionLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(this::applyDescription);
    }

    private void applyDescription(String text) {
        descriptionEl.setText(text);
        descriptionEl.setVisible(text != null && !text.isBlank());
    }

    // -----------------------------------------------------------------------
    // Cancel button API
    // -----------------------------------------------------------------------

    /**
     * Sets the cancel button label from a plain string.
     * Default: {@code "Cancel"}.
     *
     * @param text the label (not null)
     */
    public void setCancelText(String text) {
        this.cancelLocalizable = null;
        cancelButton.setText(text);
    }

    /**
     * Sets the cancel button label from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setCancelText(Localizable localizable) {
        this.cancelLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(cancelButton::setText);
    }

    /**
     * Sets the callback invoked when the cancel button is clicked.
     * The dialog is always closed after the callback.
     *
     * @param onCancel the callback (not null)
     */
    public void setOnCancel(Runnable onCancel) {
        this.onCancelCallback = onCancel;
    }

    // -----------------------------------------------------------------------
    // Action / confirm button API
    // -----------------------------------------------------------------------

    /**
     * Sets the confirm/action button label from a plain string.
     * Default: {@code "Continue"}.
     *
     * @param text the label (not null)
     */
    public void setConfirmText(String text) {
        this.confirmLocalizable = null;
        actionButton.setText(text);
    }

    /**
     * Sets the confirm/action button label from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setConfirmText(Localizable localizable) {
        this.confirmLocalizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(actionButton::setText);
    }

    /**
     * Sets the callback invoked when the confirm/action button is clicked.
     * The dialog is <strong>always</strong> closed after the callback, even if it throws.
     * Setting this clears any previously registered {@link BooleanSupplier} condition.
     *
     * @param onConfirm the callback (not null)
     */
    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirmCondition = null;   // mutually exclusive with the conditional variant
        this.onConfirmCallback  = onConfirm;
    }

    /**
     * Sets a <strong>conditional</strong> confirm handler.
     *
     * <p>When the action button is clicked the supplier is evaluated:</p>
     * <ul>
     *   <li>{@code true}  â†’ the dialog is closed (success path).</li>
     *   <li>{@code false} â†’ the dialog <em>stays open</em> (e.g. validation failed).</li>
     *   <li>throws        â†’ exception propagates; dialog stays open so the user can retry.</li>
     * </ul>
     *
     * <p>Typical pattern:</p>
     * <pre>{@code
     * AlertDialog.builder()
     *     .title("Delete account?")
     *     .onConfirm(() -> {
     *         try {
     *             accountService.delete(currentUser);
     *             return true;   // success â†’ close the dialog
     *         } catch (ServiceException ex) {
     *             Notification.show("Delete failed: " + ex.getMessage());
     *             return false;  // failure â†’ stay open so the user can retry
     *         }
     *     })
     *     .build()
     *     .open();
     * }</pre>
     *
     * <p>Setting this clears any previously registered {@link Runnable} callback.</p>
     *
     * @param onConfirm the conditional handler (not null)
     */
    public void setOnConfirm(BooleanSupplier onConfirm) {
        this.onConfirmCallback  = null;   // mutually exclusive with the unconditional variant
        this.onConfirmCondition = onConfirm;
    }

    /**
     * Applies a semantic variant to the action (confirm) button, making its colour match
     * the intent of the operation.
     *
     * <ul>
     *   <li>{@link Alert.Variant#DEFAULT}     â€” primary dark button (default)</li>
     *   <li>{@link Alert.Variant#DESTRUCTIVE} â€” red, for irreversible/dangerous actions</li>
     *   <li>{@link Alert.Variant#WARNING}     â€” amber, for cautionary actions</li>
     *   <li>{@link Alert.Variant#SUCCESS}     â€” green, for positive confirmations</li>
     *   <li>{@link Alert.Variant#INFO}        â€” blue, for neutral confirmations</li>
     * </ul>
     *
     * <p>Calling this method clears any previously applied variant class before applying the new one.
     * Passing {@code null} or {@link Alert.Variant#DEFAULT} resets to the base primary style.</p>
     *
     * @param variant the variant to apply (null is treated as {@link Alert.Variant#DEFAULT})
     */
    public void setVariant(Alert.Variant variant) {
        // Clear any previously applied semantic theme variant (DESTRUCTIVE / SUCCESS)
        actionButton.removeThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SUCCESS);
        // Clear any previously applied CSS modifier class (WARNING / INFO)
        if (this.currentActionVariantClass != null) {
            actionButton.removeClassName(this.currentActionVariantClass);
            this.currentActionVariantClass = null;
        }
        if (variant == null || variant == Alert.Variant.DEFAULT) return;
        switch (variant) {
            case DESTRUCTIVE ->
                // Shell theme: vaadin-button[theme~="error"][theme~="primary"] â†’ filled --err red
                actionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            case SUCCESS ->
                // Shell theme: vaadin-button[theme~="success"][theme~="primary"] â†’ filled --ok green
                actionButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            case WARNING -> {
                // No standard Vaadin WARNING+PRIMARY shell rule â€” handled by CSS with
                // .alert-dialog ancestor to reach specificity 0,4,0 > shell hover 0,3,1
                String cls = "alert-dialog__action--warning";
                actionButton.addClassName(cls);
                this.currentActionVariantClass = cls;
            }
            case INFO -> {
                // No standard Vaadin INFO variant â€” handled by CSS
                String cls = "alert-dialog__action--info";
                actionButton.addClassName(cls);
                this.currentActionVariantClass = cls;
            }
        }
    }

    /**
     * Applies the destructive style to the action button.
     * Shortcut for {@code setVariant(Alert.Variant.DESTRUCTIVE)}.
     *
     * @deprecated Use {@link #setVariant(Alert.Variant)} with {@link Alert.Variant#DESTRUCTIVE} instead.
     */
    @Deprecated(since = "10.0.0", forRemoval = true)
    public void setActionDestructive() {
        setVariant(Alert.Variant.DESTRUCTIVE);
    }

    // -----------------------------------------------------------------------
    // Lifecycle â€” re-resolve i18n on re-attach
    // -----------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (titleLocalizable != null)            LocalizationProvider.localize(titleLocalizable).ifPresent(this::applyTitle);
        if (descriptionLocalizable != null)      LocalizationProvider.localize(descriptionLocalizable).ifPresent(this::applyDescription);
        if (cancelLocalizable != null)           LocalizationProvider.localize(cancelLocalizable).ifPresent(cancelButton::setText);
        if (confirmLocalizable != null)          LocalizationProvider.localize(confirmLocalizable).ifPresent(actionButton::setText);
        if (secondaryActionLocalizable != null)  LocalizationProvider.localize(secondaryActionLocalizable).ifPresent(secondaryActionButton::setText);
    }
}



