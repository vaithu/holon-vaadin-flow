package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.vaadin.flow.component.html.Div;

public class DefaultDivBuilder extends AbstractDivConfigurator<DivBuilder>
        implements DivBuilder {
    public DefaultDivBuilder() {
        super(new Div());
    }

    @Override
    public Div build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected DivBuilder getConfigurator() {
        return this;
    }
}
