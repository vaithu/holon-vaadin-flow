package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.NotificationConfigurator;
import com.vaadin.flow.component.notification.Notification;

public class DefaultNotitificationConfigurator
        extends AbstractNotificationConfigurator<NotificationConfigurator.BaseNotificationConfigurator>
        implements NotificationConfigurator.BaseNotificationConfigurator {
    public DefaultNotitificationConfigurator(Notification notification) {
        super(notification);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseNotificationConfigurator getConfigurator() {
        return this;
    }
}
