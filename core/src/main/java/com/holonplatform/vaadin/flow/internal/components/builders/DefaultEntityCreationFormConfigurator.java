package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.EntityCreationFormConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityCreationForm;

/**
 * Default {@link EntityCreationFormConfigurator.BaseEntityCreationFormConfigurator} implementation.
 */
public class DefaultEntityCreationFormConfigurator
        extends AbstractEntityCreationFormConfigurator<EntityCreationFormConfigurator.BaseEntityCreationFormConfigurator>
        implements EntityCreationFormConfigurator.BaseEntityCreationFormConfigurator {

    public DefaultEntityCreationFormConfigurator(EntityCreationForm form) {
        super(form);
    }

    @Override
    protected EntityCreationFormConfigurator.BaseEntityCreationFormConfigurator getConfigurator() {
        return this;
    }
}
