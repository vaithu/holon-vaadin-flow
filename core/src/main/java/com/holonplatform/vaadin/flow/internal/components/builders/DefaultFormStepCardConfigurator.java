package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormStepCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;

/**
 * Default {@link FormStepCardConfigurator.BaseFormStepCardConfigurator} implementation.
 */
public class DefaultFormStepCardConfigurator
        extends AbstractFormStepCardConfigurator<FormStepCardConfigurator.BaseFormStepCardConfigurator>
        implements FormStepCardConfigurator.BaseFormStepCardConfigurator {

    public DefaultFormStepCardConfigurator(FormStepCard card) {
        super(card);
    }

    @Override
    protected FormStepCardConfigurator.BaseFormStepCardConfigurator getConfigurator() {
        return this;
    }
}
