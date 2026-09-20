package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.SignInPage;
import com.iyensoft.vaadin.flow.components.builders.SignInPageConfigurator;

public class DefaultSignInPageConfigurator
        extends AbstractSignInPageConfigurator<SignInPageConfigurator.BaseSignInPageConfigurator>
        implements SignInPageConfigurator.BaseSignInPageConfigurator {

    public DefaultSignInPageConfigurator(SignInPage component) {
        super(component);
    }

    @Override
    protected BaseSignInPageConfigurator getConfigurator() {
        return this;
    }
}
