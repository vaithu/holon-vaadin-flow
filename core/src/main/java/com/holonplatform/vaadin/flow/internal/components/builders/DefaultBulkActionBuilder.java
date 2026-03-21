package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultBulkActionBuilder extends AbstractBulkActionConfigurator<BulkActionBuilder> implements BulkActionBuilder {


    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultBulkActionBuilder(HorizontalLayout component) {
        super(component);
    }

    public DefaultBulkActionBuilder() {
        super(new HorizontalLayout());
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

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected DefaultBulkActionBuilder getConfigurator() {
        return this;
    }
}
