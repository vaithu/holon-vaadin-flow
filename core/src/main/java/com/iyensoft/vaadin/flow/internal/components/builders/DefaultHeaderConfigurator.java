package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.HeaderConfigurator;
import com.iyensoft.vaadin.flow.components.Header;

public class DefaultHeaderConfigurator extends AbstractHeaderConfigurator<HeaderConfigurator.BaseHeaderConfigurator>
        implements HeaderConfigurator.BaseHeaderConfigurator {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultHeaderConfigurator(Header component) {
        super(component);
    }

    @Override
    protected BaseHeaderConfigurator getConfigurator() {
        return this;
    }
}
