package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.NotificationBuilder;
import com.vaadin.flow.component.notification.Notification;

public class DefaultNotificationBuilder
        extends AbstractNotificationConfigurator<NotificationBuilder>
        implements NotificationBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultNotificationBuilder(Notification component) {
        super(component);
    }

    public DefaultNotificationBuilder() {
        super(new Notification());
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public Notification build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected NotificationBuilder getConfigurator() {
        return this;
    }
}
