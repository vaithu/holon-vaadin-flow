package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.builders.DetailBuilder;
import com.vaadin.flow.component.html.Div;

public class DefaultDetailBuilder
        extends AbstractDetailConfigurator<DetailBuilder>
        implements DetailBuilder {

    public DefaultDetailBuilder(Div detail) {
        super(detail);
        Components.configure(detail).styleName("detail-view");
    }

    @Override
    protected DetailBuilder getConfigurator() {
        return this;
    }

    @Override
    public Div build() {
        return getComponent();
    }
}
