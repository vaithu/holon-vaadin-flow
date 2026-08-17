package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormStepCardBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;

/**
 * Builder to create and configure {@link FormStepCard} components.
 */
public interface FormStepCardBuilder
        extends FormStepCardConfigurator<FormStepCardBuilder>, ComponentBuilder<FormStepCard, FormStepCardBuilder> {

    static FormStepCardBuilder create() {
        return new DefaultFormStepCardBuilder();
    }
}
