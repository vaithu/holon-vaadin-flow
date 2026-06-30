package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;
import com.iyensoft.vaadin.flow.components.Panel;

public class DefaultPanelConfigurator extends AbstractPanelConfigurator<PanelConfigurator.BasePanelConfigurator>
        implements PanelConfigurator.BasePanelConfigurator {

    public DefaultPanelConfigurator(Panel component) {
        super(component);
    }

    @Override
    protected BasePanelConfigurator getConfigurator() {
        return this;
    }
}