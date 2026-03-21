package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.IyenCommonBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public class DefaultIyenCommonBuilder extends AbstractIyenCommonConfigurator<IyenCommonBuilder>

        implements IyenCommonBuilder {


    public DefaultIyenCommonBuilder(Layout layout) {
        super(layout);
    }

    @Override
    public Layout build() {
        return getComponent();
    }

    @Override
    protected IyenCommonBuilder getConfigurator() {
        return this;
    }
}
