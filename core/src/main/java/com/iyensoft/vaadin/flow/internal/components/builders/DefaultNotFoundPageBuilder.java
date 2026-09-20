package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.iyensoft.vaadin.flow.components.builders.NotFoundPageBuilder;

public class DefaultNotFoundPageBuilder extends AbstractNotFoundPageConfigurator<NotFoundPageBuilder>
        implements NotFoundPageBuilder {

    public DefaultNotFoundPageBuilder(NotFoundPage component) {
        super(component);
    }

    @Override
    public NotFoundPage build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected NotFoundPageBuilder getConfigurator() {
        return this;
    }
}
