package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultZohoBuilder extends AbstractZohoConfigurator<ZohoBuilder> implements ZohoBuilder {
    public DefaultZohoBuilder(HorizontalLayout layout) {
        super(layout);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected ZohoBuilder getConfigurator() {
        return this;
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public HorizontalLayout build() {
        return getComponent();
    }
}
