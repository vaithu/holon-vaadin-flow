package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;

public class DefaultLayoutBuilder
        extends AbstractLayoutConfigurator<LayoutBuilder>
        implements LayoutBuilder {

    public DefaultLayoutBuilder(Layout component) {
        super(component);
    }

    public DefaultLayoutBuilder(Component... components) {
        super(new Layout(components));
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */

    @Override
    public Layout build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected LayoutBuilder getConfigurator() {
        return this;
    }
}
