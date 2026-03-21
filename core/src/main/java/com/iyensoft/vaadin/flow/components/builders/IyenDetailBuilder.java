package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultIyenDetailBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface IyenDetailBuilder  extends IyenCommonConfigurator<IyenDetailBuilder>, ComponentBuilder<Layout,IyenDetailBuilder> {

    static IyenDetailBuilder create(Layout layout) {
        return new DefaultIyenDetailBuilder(layout);
    }

    static IyenDetailBuilder create() {
        return new DefaultIyenDetailBuilder();
    }

}
