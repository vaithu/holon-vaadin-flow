package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DivConfigurator;
import com.vaadin.flow.component.html.Div;

public class DefaultDivConfigurator extends AbstractDivConfigurator<DivConfigurator.BaseDivConfigurator>
        implements DivConfigurator.BaseDivConfigurator {
    public DefaultDivConfigurator(Div component) {
        super(component);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseDivConfigurator getConfigurator() {
        return this;
    }


}
