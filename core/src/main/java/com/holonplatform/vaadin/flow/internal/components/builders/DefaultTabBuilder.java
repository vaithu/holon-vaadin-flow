package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabBuilder;
import com.vaadin.flow.component.tabs.Tab;

public class DefaultTabBuilder
        extends AbstractTabConfigurator<TabBuilder>
        implements TabBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultTabBuilder(Tab component) {
        super(component);
    }



    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public Tab build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected TabBuilder getConfigurator() {
        return this;
    }
}
