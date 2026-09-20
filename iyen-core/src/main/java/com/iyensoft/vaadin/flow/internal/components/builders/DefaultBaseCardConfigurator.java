package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.CardConfigurator;
import com.vaadin.flow.component.card.Card;

/**
 * Default implementation of {@link CardConfigurator.BaseCardConfigurator}
 * for configuring an existing {@link Card} instance without building a new one.
 *
 * <p>Use {@link CardConfigurator#configure(Card)} to obtain an instance.</p>
 */
public class DefaultBaseCardConfigurator
        extends AbstractCardConfigurator<CardConfigurator.BaseCardConfigurator>
        implements CardConfigurator.BaseCardConfigurator {

    public DefaultBaseCardConfigurator(Card card) {
        super(card);
    }

    @Override
    protected CardConfigurator.BaseCardConfigurator getConfigurator() {
        return this;
    }
}

