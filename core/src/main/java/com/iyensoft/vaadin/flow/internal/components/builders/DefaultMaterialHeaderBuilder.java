package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.MaterialHeaderBuilder;
import com.iyensoft.vaadin.flow.components.MaterialHeader;
import com.vaadin.flow.component.Component;

/** Default {@link MaterialHeaderBuilder} implementation. */
public class DefaultMaterialHeaderBuilder extends AbstractMaterialHeaderConfigurator<MaterialHeaderBuilder>
        implements MaterialHeaderBuilder {

    public DefaultMaterialHeaderBuilder(Component... components) {
        super(new MaterialHeader());
        leading(components);
    }

    @Override protected MaterialHeaderBuilder getConfigurator() { return this; }

    @Override public MaterialHeader build() {
        applyPostProcessors();
        return getComponent();
    }
}