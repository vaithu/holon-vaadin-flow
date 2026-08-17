package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ArAgingBarConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;

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
