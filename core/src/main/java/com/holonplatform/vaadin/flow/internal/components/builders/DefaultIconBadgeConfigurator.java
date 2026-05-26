package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.IconBadgeConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;

/**
 * Default {@link IconBadgeConfigurator.BaseIconBadgeConfigurator} implementation.
 */
public class DefaultIconBadgeConfigurator
        extends AbstractIconBadgeConfigurator<IconBadgeConfigurator.BaseIconBadgeConfigurator>
        implements IconBadgeConfigurator.BaseIconBadgeConfigurator {

    public DefaultIconBadgeConfigurator(IconBadge badge) {
        super(badge);
    }

    @Override
    protected IconBadgeConfigurator.BaseIconBadgeConfigurator getConfigurator() {
        return this;
    }
}

