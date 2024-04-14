package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCardBuilder;
import com.vaadin.flow.component.html.H4;

public interface CardBuilder extends CardConfigurator<CardBuilder>, HasStyleConfigurator<CardBuilder>{

    static CardBuilder create(String titleText) {
        return new DefaultCardBuilder(titleText);
    }

    static CardBuilder create() {
        return new DefaultCardBuilder();
    }

    static CardBuilder create(LabelBuilder<H4> title) {
        return new DefaultCardBuilder(title);
    }
}
