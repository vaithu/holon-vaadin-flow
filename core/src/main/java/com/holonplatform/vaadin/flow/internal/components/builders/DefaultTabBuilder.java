package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabBuilder;
import com.vaadin.flow.component.tabs.Tab;

public class DefaultTabBuilder
        extends AbstractTabConfigurator<TabBuilder>
        implements TabBuilder {
    public DefaultTabBuilder(Tab component) {
        super(component);
    }



    @Override
    public Tab build() {
        return getComponent();
    }

    @Override
    protected TabBuilder getConfigurator() {
        return this;
    }
}
