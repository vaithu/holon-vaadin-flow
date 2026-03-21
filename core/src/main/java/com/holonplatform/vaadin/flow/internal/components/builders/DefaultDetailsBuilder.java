package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DetailsBuilder;
import com.vaadin.flow.component.details.Details;

public class DefaultDetailsBuilder
        extends AbstractDetailsConfigurator<DetailsBuilder>
        implements DetailsBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultDetailsBuilder(Details component) {
        super(component);
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public Details build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected DetailsBuilder getConfigurator() {
        return this;
    }
}
