package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.iyensoft.vaadin.flow.components.builders.NotFoundPageConfigurator;

public class DefaultNotFoundPageConfigurator
        extends AbstractNotFoundPageConfigurator<NotFoundPageConfigurator.BaseNotFoundPageConfigurator>
        implements NotFoundPageConfigurator.BaseNotFoundPageConfigurator {

    public DefaultNotFoundPageConfigurator(NotFoundPage component) {
        super(component);
    }

    @Override
    protected BaseNotFoundPageConfigurator getConfigurator() {
        return this;
    }
}
