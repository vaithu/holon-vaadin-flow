package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultIyenCommonBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface IyenCommonBuilder extends IyenCommonConfigurator<IyenCommonBuilder>, ComponentBuilder<Layout, IyenCommonBuilder> {

    static IyenCommonBuilder create(Layout layout) {
        return new DefaultIyenCommonBuilder(layout);
    }

    static IyenCommonBuilder create() {
        return create(new Layout());
    }
}
