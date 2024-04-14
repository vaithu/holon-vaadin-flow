package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCardGridBuilder;

public interface CardGridBuilder extends CardGridConfigurator<CardGridBuilder>, HasStyleConfigurator<CardGridBuilder>,
        HasSizeConfigurator<CardGridBuilder>, HasComponentsConfigurator<CardGridBuilder>{

    static CardGridBuilder create() {
        return new DefaultCardGridBuilder();
    }


}
