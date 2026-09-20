package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultCardConfigurator;
import com.vaadin.flow.component.card.Card;

public interface CardBuilder extends CardConfigurator<CardBuilder>, ComponentBuilder<Card, CardBuilder> {

    static  CardBuilder create()
    {
        return create(new  Card());
    }

    static  CardBuilder create(Card card)
    {
        return new DefaultCardConfigurator(card);
    }

}
