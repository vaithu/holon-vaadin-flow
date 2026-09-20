package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.iyensoft.vaadin.flow.components.builders.ResetPasswordPageBuilder;

public class DefaultResetPasswordPageBuilder extends AbstractResetPasswordPageConfigurator<ResetPasswordPageBuilder>
        implements ResetPasswordPageBuilder {

    public DefaultResetPasswordPageBuilder(ResetPasswordPage component) {
        super(component);
    }

    @Override
    public ResetPasswordPage build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected ResetPasswordPageBuilder getConfigurator() {
        return this;
    }
}
