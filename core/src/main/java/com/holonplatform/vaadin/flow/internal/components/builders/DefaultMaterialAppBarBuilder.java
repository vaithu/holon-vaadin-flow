package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MaterialAppBarBuilder;
import com.vaadin.flow.component.Component;

/** Default {@link MaterialAppBarBuilder} implementation. */
public class DefaultMaterialAppBarBuilder extends AbstractMaterialAppBarConfigurator<MaterialAppBarBuilder>
        implements MaterialAppBarBuilder {

    public DefaultMaterialAppBarBuilder(Component... components) {
        super(new com.iyensoft.vaadin.flow.components.MaterialAppBar());
        leading(components);
    }

    @Override
    protected MaterialAppBarBuilder getConfigurator() {
        return this;
    }

    @Override
    public com.iyensoft.vaadin.flow.components.MaterialAppBar build() {
        applyPostProcessors();
        return getComponent();
    }
}