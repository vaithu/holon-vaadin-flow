package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;

public class DefaultIyenDetailBuilder
        extends AbstractIyenCommonConfigurator<IyenDetailBuilder>
        implements IyenDetailBuilder {
    public DefaultIyenDetailBuilder(Layout layout) {
        super(layout);
    }

    public DefaultIyenDetailBuilder() {
        super(
                Components.layout().styleName("iyen-detail").build()
        );
    }

    @Override
    protected IyenDetailBuilder getConfigurator() {
        return this;
    }

    @Override
    public Layout build() {
        return getComponent();
    }
}
