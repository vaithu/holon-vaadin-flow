package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultNotitificationConfigurator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.ValidationException;

public interface NotificationConfigurator<C extends NotificationConfigurator<C>> extends HasComponentsConfigurator<C>,
ComponentConfigurator<C>,
        HasStyleConfigurator<C>, HasThemeVariantConfigurator<NotificationVariant, C> {

    void close();

    C duration(int duration);

    C opened(boolean opened);

    C position(Notification.Position position);

    C text(String text);

    void show();

    default void open() {
        show();
    }

    C error(ValidationException validationException);

    default C warning() {
        return withThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    default C error() {
        return withThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    default C success() {
        return withThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    default C primary() {
        return withThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    default C contrast() {
        return withThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    C closeButton(boolean close);

   default C topStretch() {
       return position(Notification.Position.TOP_STRETCH);
   }

    default C topStart() {
        return position(Notification.Position.TOP_START);
    }
    default C topCenter() {
        return position(Notification.Position.TOP_CENTER);
    }
    default C topEnd() {
        return position(Notification.Position.TOP_END);
    }
    default C middle() {
        return position(Notification.Position.MIDDLE);
    }
    default C bottomStart() {
        return position(Notification.Position.BOTTOM_START);
    }
    default C bottomCenter() {
        return position(Notification.Position.BOTTOM_CENTER);
    }
    default C bottomEnd() {
        return position(Notification.Position.BOTTOM_END);
    }
    default C bottomStretch() {
        return position(Notification.Position.BOTTOM_STRETCH);
    }

    C div(Div div);

    C icon(VaadinIcon icon);

    C icon(Icon icon);

    C error(Exception e);

    static BaseNotificationConfigurator configure(Notification notification) {
        return new DefaultNotitificationConfigurator(notification);
    }

    interface BaseNotificationConfigurator extends NotificationConfigurator<BaseNotificationConfigurator> {

    }



}
