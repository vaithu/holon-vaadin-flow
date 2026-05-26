package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DefaultLazyTabsConfigurator
        extends AbstractLazyTabsConfigurator<LazyTabsConfigurator.BaseTabsConfigurator>
        implements LazyTabsConfigurator.BaseTabsConfigurator {
    public DefaultLazyTabsConfigurator(VerticalLayout layout) {
        super(layout);
    }

    @Override
    protected BaseTabsConfigurator getConfigurator() {
        return this;
    }



}
