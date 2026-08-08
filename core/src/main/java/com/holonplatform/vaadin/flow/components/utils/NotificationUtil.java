package com.holonplatform.vaadin.flow.components.utils;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;

/**
 * @author me@fredpena.dev
 * @created 27/01/2024  - 11:10
 */
public final class NotificationUtil {

    private NotificationUtil() {}

    public static void notificationError(String msg) {
        Notification notification = Components.notification()
                .withThemeVariants(NotificationVariant.LUMO_ERROR)
                .duration(3000)
                .topEnd()
                .build();
        notification.getElement().getThemeList().add("n-error");
        addContentAndOpen(notification, msg);
    }

    public static void notificationSuccess(String msg) {
        Notification notification = Components.notification()
                .withThemeVariants(NotificationVariant.LUMO_SUCCESS)
                .duration(3000)
                .topEnd()
                .build();
        notification.getElement().getThemeList().add("n-success");
        addContentAndOpen(notification, msg);
    }

    public static void notificationWarning(String msg) {
        Notification notification = Components.notification()
                .duration(3000)
                .topEnd()
                .build();
        notification.getElement().getThemeList().add("n-warning");
        addContentAndOpen(notification, msg);
    }

    private static void addContentAndOpen(Notification notification, String msg) {
        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Button closeButton = Components.button()
                .icon("lumo", "cross")
                .styleName("notification__close-btn")
                .withClickListener(e -> notification.close())
                .build();

        HorizontalLayout layout = Components.hl()
                .add(icon, new Text(msg), closeButton)
                .alignItems(FlexComponent.Alignment.CENTER)
                .build();

        notification.add(layout);
        notification.open();
    }

    public static void notificationError(ValidationException ex) {
        // ValidationResult in Vaadin 25 no longer exposes the binding; collect error messages only
        String msg = ex.getValidationErrors().stream()
                .map(err -> err.getErrorMessage())
                .filter(m -> m != null && !m.isEmpty())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        notificationError(msg.isEmpty() ? LocalizationProvider.localize("Validation failed", "notification.validation_failed") : msg);
    }
}