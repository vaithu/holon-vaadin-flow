package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LayoutConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public class DefaultLayoutConfigurator extends AbstractLayoutConfigurator<LayoutConfigurator.BaseLayoutConfigurator>
        implements LayoutConfigurator.BaseLayoutConfigurator {
    public DefaultLayoutConfigurator(Layout component) {
        super(component);
    }

    @Override
    protected BaseLayoutConfigurator getConfigurator() {
        return this;
    }
}
