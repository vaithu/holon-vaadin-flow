package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LayoutConfigurator;
import com.iyensoft.vaadin.flow.components.Layout;

public class DefaultLayoutConfigurator extends AbstractLayoutConfigurator<Layout, LayoutConfigurator.BaseLayoutConfigurator>
        implements LayoutConfigurator.BaseLayoutConfigurator {
    public DefaultLayoutConfigurator(Layout component) {
        super(component);
    }

    @Override
    protected BaseLayoutConfigurator getConfigurator() {
        return this;
    }
}
