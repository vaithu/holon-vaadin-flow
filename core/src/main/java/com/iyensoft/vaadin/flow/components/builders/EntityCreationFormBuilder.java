package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultEntityCreationFormBuilder;
import com.iyensoft.vaadin.flow.components.EntityCreationForm;

/**
 * Builder to create and configure {@link EntityCreationForm} components.
 */
public interface EntityCreationFormBuilder
        extends EntityCreationFormConfigurator<EntityCreationFormBuilder>,
                ComponentBuilder<EntityCreationForm, EntityCreationFormBuilder> {

    static EntityCreationFormBuilder create() {
        return new DefaultEntityCreationFormBuilder();
    }
}
