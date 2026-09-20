package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.MasterBuilder;
import com.vaadin.flow.component.html.Div;

public class DefaultMasterBuilder
        extends AbstractMasterConfigurator<MasterBuilder>
        implements MasterBuilder {

    public DefaultMasterBuilder(Div master) {
        super(master);
    }

    @Override
    protected MasterBuilder getConfigurator() {
        return this;
    }

    @Override
    public Div build() {
        applyPostProcessors();
        return getComponent();
    }
}
