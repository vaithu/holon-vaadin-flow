package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FlexLayoutConfigurator;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DefaultFlexLayoutConfigurator extends AbstractFlexLayoutConfigurator<FlexLayoutConfigurator.BaseFlexLayoutConfigurator>
     implements FlexLayoutConfigurator.BaseFlexLayoutConfigurator {
    public DefaultFlexLayoutConfigurator(FlexLayout component) {
        super(component);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected DefaultFlexLayoutConfigurator getConfigurator() {
        return this;
    }
}
