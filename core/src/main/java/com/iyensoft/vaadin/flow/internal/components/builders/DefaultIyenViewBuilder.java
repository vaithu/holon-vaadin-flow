package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.IyenViewBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public class DefaultIyenViewBuilder
        extends AbstractIyenViewConfigurator<IyenViewBuilder>
        implements IyenViewBuilder {
    public DefaultIyenViewBuilder(Layout layout) {
        super(layout);
    }

    public DefaultIyenViewBuilder() {
        super();
    }

    @Override
    protected IyenViewBuilder getConfigurator() {
        return this;
    }

    @Override
    public Layout build() {
        return getComponent();
    }
}
