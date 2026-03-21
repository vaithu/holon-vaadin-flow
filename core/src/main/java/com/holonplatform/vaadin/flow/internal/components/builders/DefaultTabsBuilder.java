package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultTabsBuilder
        extends AbstractTabsConfigurator<TabsBuilder>
        implements TabsBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultTabsBuilder(Tabs component) {
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
    protected TabsBuilder getConfigurator() {
        return this;
    }
}
