package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMasterBuilder;
import com.vaadin.flow.component.html.Div;

public interface MasterBuilder extends MasterConfigurator<MasterBuilder>, ComponentBuilder<Div, MasterBuilder> {


    static MasterBuilder create() {
        return create(new Div());
    }

    static MasterBuilder create(Div component) {
        return new DefaultMasterBuilder(component);
    }
}
