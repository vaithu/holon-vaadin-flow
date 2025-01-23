package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AutoTabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultAutoTabsBuilder
        extends AbstractAutoTabsConfigurator<AutoTabsBuilder>
        implements AutoTabsBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultAutoTabsBuilder(Tabs component) {
        super(component);
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public Tabs build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected AutoTabsBuilder getConfigurator() {
        return this;
    }
}
