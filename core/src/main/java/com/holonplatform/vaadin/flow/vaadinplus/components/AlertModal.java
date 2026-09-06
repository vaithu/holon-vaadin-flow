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
import com.holonplatform.vaadin.flow.components.builders.AlertModalBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;

/**
 * Modal alert notification  a {@link Dialog} overlay wrapping an {@link Alert}.
 *
 * <p>Use this component when you need to surface an inline {@link Alert} (info,
 * warning, success, or destructive notification) as a modal overlay.  It is
 * dismissible by default (ESC key and click-outside), making it suitable for
 * informational messages that do not require an explicit user decision.</p>
 *
 * <p>If you need a <strong>confirmation</strong> dialog ("Are you sure?") that
 * requires the user to choose Cancel or Confirm, use {@link AlertDialog} instead.
 *
 * <p>Composition:</p>
 * <pre>
 * AlertModal  (Dialog overlay)
 *  ”€€ Alert
 *       œ€€ Icon            (optional  {@link #setIcon(Icon)})
 *       œ€€ AlertTitle      ({@link #setTitle(String)} / {@link #setTitle(Localizable)})
 *       œ€€ AlertDescription({@link #setDescription(String)})
 *       ”€€ AlertAction     ({@link #setAction(Component...)})
 * </pre>
 *
 * <p>Preferred usage via builder:</p>
 * <pre>{@code
 * AlertModal modal = AlertModal.builder(Alert.Variant.SUCCESS)
 *     .title("Changes saved")
 *     .description("Your profile has been updated successfully.")
 *     .closeOnEsc(true)
 *     .closeOnOutsideClick(true)
 *     .build();
 *
 * modal.open();
 * }</pre>
 *
 * @see Alert
 * @see AlertDialog
 * @see AlertModalBuilder
 */
@StyleSheet("context://alert-modal.css")
public class AlertModal extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Alert inner;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link Alert.Variant#DEFAULT} alert modal.
     */
    public AlertModal() {
        this(Alert.Variant.DEFAULT);
    }

    /**
     * Creates an alert modal with the given variant.
     *
     * @param variant the visual variant (not null)
     */
    public AlertModal(Alert.Variant variant) {
        this.inner = new Alert(variant);

        // AlertModal is dismissible  user is just being notified
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        getElement().setAttribute("role", "alertdialog");
        getElement().setAttribute("aria-modal", "true");
        // Mirror theme attribute to vaadin-dialog-overlay so that
        // vaadin-dialog-overlay[theme~="alert-modal"]::part(*) selectors in
        // alert-modal.css are applied to this dialog's overlay.
        getElement().getThemeList().add("alert-modal");


        add(inner);
    }

    /**
     * Obtain an {@link AlertModalBuilder} for the default variant.
     *
     * @return a new {@link AlertModalBuilder}
     */
    public static AlertModalBuilder builder() {
        return AlertModalBuilder.create();
    }

    /**
     * Obtain an {@link AlertModalBuilder} for the given variant.
     *
     * @param variant the visual variant (not null)
     * @return a new {@link AlertModalBuilder}
     */
    public static AlertModalBuilder builder(Alert.Variant variant) {
        return AlertModalBuilder.create(variant);
    }

    // -----------------------------------------------------------------------
    // Inner Alert accessor
    // -----------------------------------------------------------------------

    /**
     * Returns the inner {@link Alert} that holds the visual content.
     *
     * @return the inner {@link Alert} (never null)
     */
    public Alert getAlert() {
        return inner;
    }

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    public Alert.Variant getVariant() { return inner.getVariant(); }

    public void setVariant(Alert.Variant variant) { inner.setVariant(variant); }

    // -----------------------------------------------------------------------
    // Icon
    // -----------------------------------------------------------------------

    public void setIcon(Icon icon) { inner.setIcon(icon); }

    public void clearIcon() { inner.clearIcon(); }

    // -----------------------------------------------------------------------
    // Title
    // -----------------------------------------------------------------------

    public AlertTitle getAlertTitle() { return inner.getAlertTitle(); }

    public void setTitle(AlertTitle title) { inner.setTitle(title); }

    public void setTitle(String text) { inner.setTitle(text); }

    public void setTitle(Localizable localizable) { inner.setTitle(localizable); }

    // -----------------------------------------------------------------------
    // Description
    // -----------------------------------------------------------------------

    public AlertDescription getAlertDescription() { return inner.getDescription(); }

    public void setDescription(AlertDescription description) { inner.setDescription(description); }

    public void setDescription(String text) { inner.setDescription(text); }

    public void setDescription(Localizable localizable) { inner.setDescription(localizable); }

    // -----------------------------------------------------------------------
    // Action
    // -----------------------------------------------------------------------

    public AlertAction getAlertAction() { return inner.getAction(); }

    public void setAction(AlertAction action) { inner.setAction(action); }

    public void setAction(Component... actions) { inner.setAction(actions); }
}
