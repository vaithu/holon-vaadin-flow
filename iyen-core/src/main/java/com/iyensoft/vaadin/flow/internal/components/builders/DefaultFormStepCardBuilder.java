package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.FormStepCardBuilder;
import com.iyensoft.vaadin.flow.components.FormStepCard;

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
