package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.SignUpPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSignUpPageBuilder;

/**
 * Fluent builder for {@link SignUpPage} components.
 */
public interface SignUpPageBuilder
        extends SignUpPageConfigurator<SignUpPageBuilder>, ComponentBuilder<SignUpPage, SignUpPageBuilder> {

    /**
     * Create a new {@link SignUpPageBuilder} for a fresh {@link SignUpPage}.
     *
     * @return a new builder
     */
    static SignUpPageBuilder create() {
        return create(new SignUpPage());
    }

    /**
     * Create a new {@link SignUpPageBuilder} for the given {@link SignUpPage}.
     *
     * @param signUpPage the sign-up page to build (not null)
     * @return a new builder
     */
    static SignUpPageBuilder create(SignUpPage signUpPage) {
        return new DefaultSignUpPageBuilder(signUpPage);
    }
}
