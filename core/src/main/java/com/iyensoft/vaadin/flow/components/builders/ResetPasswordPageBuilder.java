package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultResetPasswordPageBuilder;

/**
 * Fluent builder for {@link ResetPasswordPage} components.
 */
public interface ResetPasswordPageBuilder
        extends ResetPasswordPageConfigurator<ResetPasswordPageBuilder>,
        ComponentBuilder<ResetPasswordPage, ResetPasswordPageBuilder> {

    /**
     * Create a new {@link ResetPasswordPageBuilder} for a fresh {@link ResetPasswordPage}.
     *
     * @return a new builder
     */
    static ResetPasswordPageBuilder create() {
        return create(new ResetPasswordPage());
    }

    /**
     * Create a new {@link ResetPasswordPageBuilder} for the given {@link ResetPasswordPage}.
     *
     * @param resetPasswordPage the reset-password page to build (not null)
     * @return a new builder
     */
    static ResetPasswordPageBuilder create(ResetPasswordPage resetPasswordPage) {
        return new DefaultResetPasswordPageBuilder(resetPasswordPage);
    }
}
