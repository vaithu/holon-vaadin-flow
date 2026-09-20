package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultMaterialAppBarBuilder;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.vaadin.flow.component.Component;

/** Fluent builder for Material 3 {@link MaterialAppBar} components. */
public interface MaterialAppBarBuilder extends MaterialAppBarConfigurator<MaterialAppBarBuilder>,
        ComponentBuilder<MaterialAppBar, MaterialAppBarBuilder> {

    static MaterialAppBarBuilder create() {
        return new DefaultMaterialAppBarBuilder();
    }

    static MaterialAppBarBuilder create(Component... components) {
        return new DefaultMaterialAppBarBuilder(components);
    }
}