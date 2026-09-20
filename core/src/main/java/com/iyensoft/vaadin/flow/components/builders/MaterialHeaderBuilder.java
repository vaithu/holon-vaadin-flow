package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMaterialHeaderBuilder;
import com.iyensoft.vaadin.flow.components.MaterialHeader;
import com.vaadin.flow.component.Component;

/** Fluent builder for standalone Material 3 {@link MaterialHeader} components. */
public interface MaterialHeaderBuilder extends MaterialHeaderConfigurator<MaterialHeaderBuilder>,
        ComponentBuilder<MaterialHeader, MaterialHeaderBuilder> {

    static MaterialHeaderBuilder create() {
        return new DefaultMaterialHeaderBuilder();
    }

    static MaterialHeaderBuilder create(Component... components) {
        return new DefaultMaterialHeaderBuilder(components);
    }
}