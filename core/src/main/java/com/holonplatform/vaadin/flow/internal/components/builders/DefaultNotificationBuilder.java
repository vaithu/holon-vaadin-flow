package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.NotificationBuilder;
import com.vaadin.flow.component.notification.Notification;

public class DefaultNotificationBuilder
        extends AbstractNotificationConfigurator<NotificationBuilder>
        implements NotificationBuilder {
    public DefaultNotificationBuilder(Notification component) {
        super(component);
    }

    public DefaultNotificationBuilder() {
        super(new Notification());
        autoClose();
    }

    @Override
    public Notification build() {
        return getComponent();
    }

    @Override
    protected NotificationBuilder getConfigurator() {
        return this;
    }
}
