package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.DivBuilder;
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
