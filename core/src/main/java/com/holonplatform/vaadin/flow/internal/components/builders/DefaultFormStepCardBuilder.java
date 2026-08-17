package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormStepCardBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;

/**
 * Default {@link FormStepCardBuilder} implementation.
 */
public class DefaultFormStepCardBuilder
        extends AbstractFormStepCardConfigurator<FormStepCardBuilder>
        implements FormStepCardBuilder {

    public DefaultFormStepCardBuilder() {
        super(new FormStepCard());
    }

    @Override
    protected FormStepCardBuilder getConfigurator() {
        return this;
    }

    @Override
    public FormStepCard build() {
        assembleCard();
        applyPostProcessors();
        return getComponent();
    }
}
