package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.EntityCreationFormBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityCreationForm;

/**
 * Default {@link EntityCreationFormBuilder} implementation.
 */
public class DefaultEntityCreationFormBuilder
        extends AbstractEntityCreationFormConfigurator<EntityCreationFormBuilder>
        implements EntityCreationFormBuilder {

    public DefaultEntityCreationFormBuilder() {
        super(new EntityCreationForm());
    }

    @Override
    protected EntityCreationFormBuilder getConfigurator() {
        return this;
    }

    @Override
    public EntityCreationForm build() {
        applyState();
        applyPostProcessors();
        return getComponent();
    }
}
