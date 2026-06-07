package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.vaadin.flow.component.tabs.Tabs;

public class DefaultLazyTabsConfigurator
        extends AbstractLazyTabsConfigurator<LazyTabsConfigurator.BaseTabsConfigurator>
        implements LazyTabsConfigurator.BaseTabsConfigurator {
    public DefaultLazyTabsConfigurator(Tabs tabs) {
        super(tabs);
    }

    @Override
    protected BaseTabsConfigurator getConfigurator() {
        return this;
    }



}
