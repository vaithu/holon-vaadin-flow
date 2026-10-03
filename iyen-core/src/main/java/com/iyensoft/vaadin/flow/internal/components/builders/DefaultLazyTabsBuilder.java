package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultLazyTabsBuilder
        extends AbstractLazyTabsConfigurator<LazyTabsBuilder>
        implements LazyTabsBuilder {
    public DefaultLazyTabsBuilder(Tabs component) {
        super(component);
    }

    public DefaultLazyTabsBuilder(Div parent) {
        super(parent);
    }

    @Override
    public Tabs build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected LazyTabsBuilder getConfigurator() {
        return this;
    }
}
