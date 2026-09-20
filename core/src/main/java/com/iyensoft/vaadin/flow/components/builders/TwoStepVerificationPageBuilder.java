package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.TwoStepVerificationPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultTwoStepVerificationPageBuilder;

/**
 * Fluent builder for {@link TwoStepVerificationPage} components.
 */
public interface TwoStepVerificationPageBuilder
        extends TwoStepVerificationPageConfigurator<TwoStepVerificationPageBuilder>,
        ComponentBuilder<TwoStepVerificationPage, TwoStepVerificationPageBuilder> {

    /**
     * Create a new {@link TwoStepVerificationPageBuilder} for a fresh
     * {@link TwoStepVerificationPage}.
     *
     * @return a new builder
     */
    static TwoStepVerificationPageBuilder create() {
        return create(new TwoStepVerificationPage());
    }

    /**
     * Create a new {@link TwoStepVerificationPageBuilder} for the given
     * {@link TwoStepVerificationPage}.
     *
     * @param twoStepVerificationPage the two-step verification page to build (not null)
     * @return a new builder
     */
    static TwoStepVerificationPageBuilder create(TwoStepVerificationPage twoStepVerificationPage) {
        return new DefaultTwoStepVerificationPageBuilder(twoStepVerificationPage);
    }
}
