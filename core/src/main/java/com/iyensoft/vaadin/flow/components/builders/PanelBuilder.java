package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.components.builders.LayoutConfigurator;
import com.iyensoft.vaadin.flow.components.IyenPanel;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultPanelBuilder;
import com.vaadin.flow.component.Component;

public interface PanelBuilder extends LayoutConfigurator<PanelBuilder>, ComponentBuilder<IyenPanel, PanelBuilder> {
    static PanelBuilder create(Component... components) {
        return new DefaultPanelBuilder(components);
    }
}
