package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultSearchBarBuilder extends AbstractSearchBarConfigurator<SearchBarBuilder> implements SearchBarBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultSearchBarBuilder(HorizontalLayout component) {
        super(component);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected SearchBarBuilder getConfigurator() {
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
