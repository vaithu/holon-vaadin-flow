package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.EntityCreationFormBuilder;
import com.iyensoft.vaadin.flow.components.EntityCreationForm;

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
