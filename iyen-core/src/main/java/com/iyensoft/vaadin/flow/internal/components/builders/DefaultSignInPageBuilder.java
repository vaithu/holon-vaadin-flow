package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.SignInPage;
import com.iyensoft.vaadin.flow.components.builders.SignInPageBuilder;

public class DefaultSignInPageBuilder extends AbstractSignInPageConfigurator<SignInPageBuilder>
        implements SignInPageBuilder {

    public DefaultSignInPageBuilder(SignInPage component) {
        super(component);
    }

    @Override
    public SignInPage build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected SignInPageBuilder getConfigurator() {
        return this;
    }
}
