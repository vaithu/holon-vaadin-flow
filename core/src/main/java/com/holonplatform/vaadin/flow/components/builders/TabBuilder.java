package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabBuilder;
import com.vaadin.flow.component.tabs.Tab;

public interface TabBuilder extends TabConfigurator<TabBuilder>,
        ComponentBuilder<Tab, TabBuilder> {

    static TabBuilder create() {
        return new DefaultTabBuilder(new Tab());
    }

}
