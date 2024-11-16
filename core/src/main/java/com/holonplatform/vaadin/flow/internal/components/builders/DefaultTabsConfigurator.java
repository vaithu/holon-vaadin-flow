package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabsConfigurator;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultTabsConfigurator
        extends AbstractTabsConfigurator<TabsConfigurator.BaseTabsConfigurator>
        implements TabsConfigurator.BaseTabsConfigurator {
    public DefaultTabsConfigurator(Tabs tabs) {
        super(tabs);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseTabsConfigurator getConfigurator() {
        return this;
    }



}
