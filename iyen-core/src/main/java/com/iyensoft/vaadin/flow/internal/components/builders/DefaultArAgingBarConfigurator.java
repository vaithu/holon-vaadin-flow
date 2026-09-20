package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.ArAgingBarConfigurator;
import com.iyensoft.vaadin.flow.components.ArAgingBar;

/**
 * Default {@link ArAgingBarConfigurator.BaseArAgingBarConfigurator} implementation.
 */
public class DefaultArAgingBarConfigurator
        extends AbstractArAgingBarConfigurator<ArAgingBarConfigurator.BaseArAgingBarConfigurator>
        implements ArAgingBarConfigurator.BaseArAgingBarConfigurator {

    public DefaultArAgingBarConfigurator(ArAgingBar bar) {
        super(bar);
    }

    @Override
    protected ArAgingBarConfigurator.BaseArAgingBarConfigurator getConfigurator() {
        return this;
    }
}
