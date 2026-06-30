package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultDetailBuilder;
import com.vaadin.flow.component.html.Div;

public interface DetailBuilder extends DetailConfigurator<DetailBuilder>, ComponentBuilder<Div, DetailBuilder> {


    static DetailBuilder create() {
        return create(new Div());
    }

    static DetailBuilder create(Div component) {
        return new DefaultDetailBuilder(component);
    }
}
