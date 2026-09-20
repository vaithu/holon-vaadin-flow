package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;

public class DefaultPanelBuilder extends AbstractPanelConfigurator<PanelBuilder> implements PanelBuilder{

    public DefaultPanelBuilder(Panel component) {
        super(component);
    }

    @Override
    public Panel build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected PanelBuilder getConfigurator() {
        return this;
    }

}
