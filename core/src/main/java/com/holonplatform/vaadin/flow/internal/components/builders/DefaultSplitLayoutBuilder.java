package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutBuilder;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class DefaultSplitLayoutBuilder
        extends AbstractSplitLayoutConfigurator<SplitLayoutBuilder>
        implements SplitLayoutBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultSplitLayoutBuilder(SplitLayout component) {
        super(component);
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public SplitLayout build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected SplitLayoutBuilder getConfigurator() {
        return this;
    }
}
