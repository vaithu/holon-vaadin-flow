package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TitleConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultBaseTitleConfigurator
        extends AbstractTitleConfigurator<TitleConfigurator.BaseTitleConfigurator>
        implements TitleConfigurator.BaseTitleConfigurator {
    public DefaultBaseTitleConfigurator(HorizontalLayout layout) {
        super(layout);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseTitleConfigurator getConfigurator() {
        return this;
    }
}
