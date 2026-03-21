package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLayoutBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;

public interface LayoutBuilder extends LayoutConfigurator<LayoutBuilder>, ComponentBuilder<Layout, LayoutBuilder> {

    static LayoutBuilder create(Component... components) {
        return new DefaultLayoutBuilder(components);
    }

    static LayoutBuilder create() {
        return new DefaultLayoutBuilder(new Layout());
    }
}
