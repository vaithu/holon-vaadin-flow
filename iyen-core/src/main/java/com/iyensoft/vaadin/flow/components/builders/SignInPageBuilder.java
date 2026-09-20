package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.SignInPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSignInPageBuilder;

/**
 * Fluent builder for {@link SignInPage} components.
 */
public interface SignInPageBuilder
        extends SignInPageConfigurator<SignInPageBuilder>, ComponentBuilder<SignInPage, SignInPageBuilder> {

    /**
     * Create a new {@link SignInPageBuilder} for a fresh {@link SignInPage}.
     *
     * @return a new builder
     */
    static SignInPageBuilder create() {
        return create(new SignInPage());
    }

    /**
     * Create a new {@link SignInPageBuilder} for the given {@link SignInPage}.
     *
     * @param signInPage the sign-in page to build (not null)
     * @return a new builder
     */
    static SignInPageBuilder create(SignInPage signInPage) {
        return new DefaultSignInPageBuilder(signInPage);
    }
}
