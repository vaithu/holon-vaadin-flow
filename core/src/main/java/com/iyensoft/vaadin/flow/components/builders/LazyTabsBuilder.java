package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultLazyTabsBuilder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public interface LazyTabsBuilder extends LazyTabsConfigurator<LazyTabsBuilder>,
        ComponentBuilder<VerticalLayout, LazyTabsBuilder>, DeferrableLocalizationConfigurator<LazyTabsBuilder> {

    static LazyTabsBuilder create() {
        return new DefaultLazyTabsBuilder(new VerticalLayout());
    }

}
