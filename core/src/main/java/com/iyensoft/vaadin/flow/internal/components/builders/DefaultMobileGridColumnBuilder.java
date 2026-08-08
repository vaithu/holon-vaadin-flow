package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public class DefaultMobileGridColumnBuilder extends AbstractMobileGridColumnConfigurator<MobileGridColumnBuilder> implements MobileGridColumnBuilder {


    public DefaultMobileGridColumnBuilder(Layout layout) {
        super(layout);
    }

    public DefaultMobileGridColumnBuilder() {
        this(new Layout());
    }

    @Override
    protected MobileGridColumnBuilder getConfigurator() {
        return this;
    }


    @Override
    public Layout build() {
        applyPostProcessors();
        return getComponent();
    }
}
