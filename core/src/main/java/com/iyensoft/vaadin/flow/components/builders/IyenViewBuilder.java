package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultIyenViewBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface IyenViewBuilder extends IyenViewConfigurator<IyenViewBuilder>, ComponentBuilder<Layout, IyenViewBuilder> {


    static IyenViewBuilder create(Layout layout) {
        return new DefaultIyenViewBuilder(layout);
    }

    static IyenViewBuilder create() {
        return new DefaultIyenViewBuilder();
    }

}
