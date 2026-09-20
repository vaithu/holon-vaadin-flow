package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.TwoStepVerificationPage;
import com.iyensoft.vaadin.flow.components.builders.TwoStepVerificationPageBuilder;

public class DefaultTwoStepVerificationPageBuilder
        extends AbstractTwoStepVerificationPageConfigurator<TwoStepVerificationPageBuilder>
        implements TwoStepVerificationPageBuilder {

    public DefaultTwoStepVerificationPageBuilder(TwoStepVerificationPage component) {
        super(component);
    }

    @Override
    public TwoStepVerificationPage build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected TwoStepVerificationPageBuilder getConfigurator() {
        return this;
    }
}
