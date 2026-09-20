package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.MasterConfigurator;
import com.vaadin.flow.component.html.Div;

public class DefaultMasterConfigurator extends AbstractMasterConfigurator<MasterConfigurator.BaseMasterConfigurator> implements MasterConfigurator.BaseMasterConfigurator {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultMasterConfigurator(Div component) {
        super(component);
    }

    @Override
    protected BaseMasterConfigurator getConfigurator() {
        return this;
    }
}
