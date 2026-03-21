package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutConfigurator;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class DefaultSplitLayoutConfigurator
        extends AbstractSplitLayoutConfigurator<SplitLayoutConfigurator.BaseSplitLayoutConfigurator>
        implements SplitLayoutConfigurator.BaseSplitLayoutConfigurator {
    public DefaultSplitLayoutConfigurator(SplitLayout splitLayout) {
        super(splitLayout);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseSplitLayoutConfigurator getConfigurator() {
        return this;
    }

}
