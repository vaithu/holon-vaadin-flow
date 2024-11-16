package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultNotitificationConfigurator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.function.Consumer;

public interface NotificationConfigurator<C extends NotificationConfigurator<C>> extends HasComponentsConfigurator<C>,
ComponentConfigurator<C>,
        HasStyleConfigurator<C>, HasThemeVariantConfigurator<NotificationVariant, C> {

    int duration = 5000;

    void close();

    void open();

    C duration(int duration);

    C opened(boolean opened);

    C position(Notification.Position position);

    C text(String text);

    C error(ValidationException validationException);

    default C warning() {
        return withThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    default C error() {
        return withThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    default C autoClose(int duration) {
        return duration(duration);
    }

    default C autoClose() {
        return autoClose(duration);
    }

    C autoClose(boolean autoClose) ;

    default C success() {
        return withThemeVariants(NotificationVariant.LUMO_SUCCESS).duration(duration);
    }

    default C primary() {
        return withThemeVariants(NotificationVariant.LUMO_PRIMARY).duration(duration);
    }

    default C contrast() {
        return withThemeVariants(NotificationVariant.LUMO_CONTRAST).duration(duration);
    }

    C closeButton(boolean close);

    C closeButton(Consumer<ButtonConfigurator> buttonConfigurator);

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
