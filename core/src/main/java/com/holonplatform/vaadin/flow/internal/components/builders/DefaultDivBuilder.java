package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.vaadin.flow.component.html.Div;

public class DefaultDivBuilder extends AbstractDivConfigurator<DivBuilder>
        implements DivBuilder {
    public DefaultDivBuilder() {
        super(new Div());
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
    protected DivBuilder getConfigurator() {
        return this;
    }
}
