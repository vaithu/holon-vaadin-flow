package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultTabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public interface TabsBuilder extends TabsConfigurator<TabsBuilder>,
        ComponentBuilder<Tabs, TabsBuilder> {

    static TabsBuilder create() {
        return new DefaultTabsBuilder(new Tabs());
    }

}
