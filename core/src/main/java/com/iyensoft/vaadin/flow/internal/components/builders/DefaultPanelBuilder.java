package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.IyenPanel;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.vaadin.flow.component.Component;

public class DefaultPanelBuilder
        extends AbstractPanelConfigurator<PanelBuilder>
        implements PanelBuilder {

    public DefaultPanelBuilder(IyenPanel component) {
        super(component);
    }

    public DefaultPanelBuilder(Component... components) {
        super(new IyenPanel(components));
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */

    @Override
    public IyenPanel build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected PanelBuilder getConfigurator() {
        return this;
    }
}