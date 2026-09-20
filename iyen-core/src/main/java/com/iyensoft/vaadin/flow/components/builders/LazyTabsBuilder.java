package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultLazyTabsBuilder;
import com.vaadin.flow.component.tabs.Tabs;

public interface LazyTabsBuilder extends LazyTabsConfigurator<LazyTabsBuilder>,
        ComponentBuilder<Tabs, LazyTabsBuilder>, DeferrableLocalizationConfigurator<LazyTabsBuilder> {

    static LazyTabsBuilder create() {
        return create(new Tabs());
    }

    static LazyTabsBuilder create(Tabs tabs) {
        return new DefaultLazyTabsBuilder(tabs);
    }

}
