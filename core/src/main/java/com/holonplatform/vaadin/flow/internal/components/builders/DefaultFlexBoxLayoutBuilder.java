package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutBuilder;

public class DefaultFlexBoxLayoutBuilder
        extends AbstractFlexBoxLayoutConfigurator<FlexBoxLayoutBuilder>
        implements FlexBoxLayoutBuilder {

    public DefaultFlexBoxLayoutBuilder() {
        super(new FlexBoxLayout());
    }

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultFlexBoxLayoutBuilder(FlexBoxLayout component) {
        super(component);
    }

    @Override
    protected FlexBoxLayoutBuilder getConfigurator() {
        return this;
    }

    @Override
    public FlexBoxLayout build() {
        return getComponent();
    }
}
