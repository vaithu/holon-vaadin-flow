package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultNotFoundPageBuilder;

/**
 * Fluent builder for {@link NotFoundPage} components.
 */
public interface NotFoundPageBuilder
        extends NotFoundPageConfigurator<NotFoundPageBuilder>, ComponentBuilder<NotFoundPage, NotFoundPageBuilder> {

    /**
     * Create a new {@link NotFoundPageBuilder} for a fresh {@link NotFoundPage}.
     *
     * @return a new builder
     */
    static NotFoundPageBuilder create() {
        return create(new NotFoundPage());
    }

    /**
     * Create a new {@link NotFoundPageBuilder} for the given {@link NotFoundPage}.
     *
     * @param notFoundPage the not-found page to build (not null)
     * @return a new builder
     */
    static NotFoundPageBuilder create(NotFoundPage notFoundPage) {
        return new DefaultNotFoundPageBuilder(notFoundPage);
    }
}
