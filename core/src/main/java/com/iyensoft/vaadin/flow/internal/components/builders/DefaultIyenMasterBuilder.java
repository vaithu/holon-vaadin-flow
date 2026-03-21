package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;

public class DefaultIyenMasterBuilder
        extends AbstractIyenCommonConfigurator<IyenMasterBuilder>
        implements IyenMasterBuilder {

    public DefaultIyenMasterBuilder(Layout layout) {
        super(layout);
    }

    public DefaultIyenMasterBuilder() {
        super(
                Components.layout()
                        .styleName("iyen-master").build()
        );
    }

    @Override
    public Layout build() {
        return getComponent();
    }

    @Override
    protected IyenMasterBuilder getConfigurator() {
        return this;
    }
}
