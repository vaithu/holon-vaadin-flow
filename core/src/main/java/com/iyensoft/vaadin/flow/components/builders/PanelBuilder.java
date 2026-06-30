package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultPanelBuilder;

public interface PanelBuilder extends PanelConfigurator<PanelBuilder>, ComponentBuilder<Panel,PanelBuilder>{

    static PanelBuilder create() {
        return create(new Panel());
    }

    static PanelBuilder create(Panel panel) {
        return new DefaultPanelBuilder(panel);
    }

    
}
