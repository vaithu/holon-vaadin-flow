package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.TabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultTabsBuilder
        extends AbstractTabsConfigurator<TabsBuilder>
        implements TabsBuilder {
    public DefaultTabsBuilder(Tabs component) {
        super(component);
    }

    @Override
    public Tabs build() {
        return getComponent();
    }

    @Override
    protected TabsBuilder getConfigurator() {
        return this;
    }
}
