package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutConfigurator;

public class DefaultFlexBoxLayoutConfigurator
        extends AbstractFlexBoxLayoutConfigurator<FlexBoxLayoutConfigurator.BaseFlexBoxLayoutConfigurator>
        implements FlexBoxLayoutConfigurator.BaseFlexBoxLayoutConfigurator {
    public DefaultFlexBoxLayoutConfigurator(FlexBoxLayout layout) {
        super(layout);
    }

    @Override
    protected BaseFlexBoxLayoutConfigurator getConfigurator() {
        return this;
    }
}
