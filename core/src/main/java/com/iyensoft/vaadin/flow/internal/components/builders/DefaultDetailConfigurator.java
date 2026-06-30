package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.DetailConfigurator;
import com.vaadin.flow.component.html.Div;

public class DefaultDetailConfigurator extends AbstractDetailConfigurator<DetailConfigurator.BaseDetailConfigurator> implements DetailConfigurator.BaseDetailConfigurator {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultDetailConfigurator(Div component) {
        super(component);
    }

    @Override
    protected BaseDetailConfigurator getConfigurator() {
        return this;
    }
}
