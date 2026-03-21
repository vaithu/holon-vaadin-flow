package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TitleBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultTitleBuilder
        extends AbstractTitleConfigurator<TitleBuilder>
        implements TitleBuilder {

    public DefaultTitleBuilder(HorizontalLayout component) {
        super(component);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected TitleBuilder getConfigurator() {
        return this;
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public HorizontalLayout build() {
        return getComponent();
    }
}
