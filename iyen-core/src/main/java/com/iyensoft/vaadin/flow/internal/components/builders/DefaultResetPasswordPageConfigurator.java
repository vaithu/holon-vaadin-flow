package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.iyensoft.vaadin.flow.components.builders.ResetPasswordPageConfigurator;

public class DefaultResetPasswordPageConfigurator
        extends AbstractResetPasswordPageConfigurator<ResetPasswordPageConfigurator.BaseResetPasswordPageConfigurator>
        implements ResetPasswordPageConfigurator.BaseResetPasswordPageConfigurator {

    public DefaultResetPasswordPageConfigurator(ResetPasswordPage component) {
        super(component);
    }

    @Override
    protected BaseResetPasswordPageConfigurator getConfigurator() {
        return this;
    }
}
