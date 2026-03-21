package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAutoTabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public interface AutoTabsBuilder extends AutoTabsConfigurator<AutoTabsBuilder>,
        ComponentBuilder<Tabs, AutoTabsBuilder> {

    static AutoTabsBuilder create() {
        return new DefaultAutoTabsBuilder(new Tabs());
    }

}
