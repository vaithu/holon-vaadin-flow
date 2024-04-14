package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultNotificationBuilder;
import com.vaadin.flow.component.notification.Notification;

public interface NotificationBuilder extends NotificationConfigurator<NotificationBuilder>,
        ComponentBuilder<Notification, NotificationBuilder> {

    static NotificationBuilder create() {
        return new DefaultNotificationBuilder();
    }

}
