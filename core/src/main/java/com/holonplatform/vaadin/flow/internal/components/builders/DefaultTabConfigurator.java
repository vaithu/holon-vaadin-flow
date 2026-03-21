package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabConfigurator;
import com.vaadin.flow.component.tabs.Tab;

public class DefaultTabConfigurator
        extends AbstractTabConfigurator<TabConfigurator.BaseTabConfigurator>
        implements TabConfigurator.BaseTabConfigurator {
    public DefaultTabConfigurator(Tab Tab) {
        super(Tab);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseTabConfigurator getConfigurator() {
        return this;
    }

}
