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

    @Override
    public IyenPanel build() {
        return getComponent();
    }

    @Override
    protected PanelBuilder getConfigurator() {
        return this;
    }
}