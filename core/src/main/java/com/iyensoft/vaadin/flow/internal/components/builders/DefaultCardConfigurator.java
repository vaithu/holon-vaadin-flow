package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.CardBuilder;
import com.vaadin.flow.component.card.Card;

public class DefaultCardConfigurator extends AbstractCardConfigurator<CardBuilder>
implements CardBuilder
{


    public DefaultCardConfigurator(Card component) {
        super(component);
    }

    @Override
    public Card build() {
        return getComponent();
    }

    @Override
    protected CardBuilder getConfigurator() {
        return this;
    }
}