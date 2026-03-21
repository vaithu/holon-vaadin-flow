package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabSheetBuilder;
import com.vaadin.flow.component.tabs.TabSheet;

public class DefaultTabSheetBuilder
        extends AbstractTabSheetConfigurator<TabSheetBuilder>
        implements TabSheetBuilder {
    public DefaultTabSheetBuilder(TabSheet tabSheet) {
        super(tabSheet);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected TabSheetBuilder getConfigurator() {
        return this;
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public TabSheet build() {
        return getComponent();
    }
}
