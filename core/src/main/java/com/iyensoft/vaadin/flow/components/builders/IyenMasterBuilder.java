package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultIyenMasterBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface IyenMasterBuilder extends IyenCommonConfigurator<IyenMasterBuilder>, ComponentBuilder<Layout,IyenMasterBuilder> {

    static IyenMasterBuilder create(Layout layout) {
        return new DefaultIyenMasterBuilder(layout);
    }

    static IyenMasterBuilder create() {
        return new DefaultIyenMasterBuilder();
    }
}
