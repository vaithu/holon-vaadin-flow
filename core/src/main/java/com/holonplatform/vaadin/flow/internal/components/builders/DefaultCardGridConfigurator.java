package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.CardGridConfigurator;
import com.vaadin.flow.component.html.Div;

public class DefaultCardGridConfigurator
        extends AbstractCardGridConfigurator<CardGridConfigurator.BaseCardGridConfigurator>
        implements CardGridConfigurator.BaseCardGridConfigurator {

    public DefaultCardGridConfigurator(Div component) {
        super(component);

    }

    @Override
    public Div build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseCardGridConfigurator getConfigurator() {
        return this;
    }
}
