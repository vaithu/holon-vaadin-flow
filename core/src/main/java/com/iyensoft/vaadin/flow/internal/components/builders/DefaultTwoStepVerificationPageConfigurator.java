package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.TwoStepVerificationPage;
import com.iyensoft.vaadin.flow.components.builders.TwoStepVerificationPageConfigurator;

public class DefaultTwoStepVerificationPageConfigurator
        extends AbstractTwoStepVerificationPageConfigurator<TwoStepVerificationPageConfigurator.BaseTwoStepVerificationPageConfigurator>
        implements TwoStepVerificationPageConfigurator.BaseTwoStepVerificationPageConfigurator {

    public DefaultTwoStepVerificationPageConfigurator(TwoStepVerificationPage component) {
        super(component);
    }

    @Override
    protected BaseTwoStepVerificationPageConfigurator getConfigurator() {
        return this;
    }
}
