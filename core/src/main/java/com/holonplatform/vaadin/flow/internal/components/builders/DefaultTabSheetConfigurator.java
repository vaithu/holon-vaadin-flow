package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabSheetConfigurator;
import com.vaadin.flow.component.tabs.TabSheet;

public class DefaultTabSheetConfigurator
        extends AbstractTabSheetConfigurator<TabSheetConfigurator.BaseTabSheetConfigurator>
        implements TabSheetConfigurator.BaseTabSheetConfigurator {
    public DefaultTabSheetConfigurator(TabSheet tabSheet) {
        super(tabSheet);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseTabSheetConfigurator getConfigurator() {
        return this;
    }
}
