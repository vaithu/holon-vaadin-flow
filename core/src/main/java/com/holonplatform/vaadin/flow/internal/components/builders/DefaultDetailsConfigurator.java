package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HasDetailsConfigurator;
import com.vaadin.flow.component.details.Details;

public class DefaultDetailsConfigurator
        extends AbstractDetailsConfigurator<HasDetailsConfigurator.BaseDetailsConfigurator>
        implements HasDetailsConfigurator.BaseDetailsConfigurator {
    public DefaultDetailsConfigurator(Details details) {
        super(details);

    }



    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseDetailsConfigurator getConfigurator() {
        return this;
    }
}
