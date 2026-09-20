package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.SignUpPage;
import com.iyensoft.vaadin.flow.components.builders.SignUpPageBuilder;

public class DefaultSignUpPageBuilder extends AbstractSignUpPageConfigurator<SignUpPageBuilder>
        implements SignUpPageBuilder {

    public DefaultSignUpPageBuilder(SignUpPage component) {
        super(component);
    }

    @Override
    public SignUpPage build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected SignUpPageBuilder getConfigurator() {
        return this;
    }
}
