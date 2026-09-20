package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.SignUpPage;
import com.iyensoft.vaadin.flow.components.builders.SignUpPageConfigurator;

public class DefaultSignUpPageConfigurator
        extends AbstractSignUpPageConfigurator<SignUpPageConfigurator.BaseSignUpPageConfigurator>
        implements SignUpPageConfigurator.BaseSignUpPageConfigurator {

    public DefaultSignUpPageConfigurator(SignUpPage component) {
        super(component);
    }

    @Override
    protected BaseSignUpPageConfigurator getConfigurator() {
        return this;
    }
}
