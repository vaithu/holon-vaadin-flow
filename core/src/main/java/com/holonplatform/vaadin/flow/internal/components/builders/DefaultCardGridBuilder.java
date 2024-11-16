package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.CardGridBuilder;
import com.vaadin.flow.component.html.Div;

public class DefaultCardGridBuilder
        extends AbstractCardGridConfigurator<CardGridBuilder>
        implements CardGridBuilder {

    public DefaultCardGridBuilder(Div component) {
        super(component);
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
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
    protected CardGridBuilder getConfigurator() {
        return this;
    }
}
