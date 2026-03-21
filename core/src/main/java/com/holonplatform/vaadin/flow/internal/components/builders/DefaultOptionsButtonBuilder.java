package com.holonplatform.vaadin.flow.internal.components.builders;

import com.vaadin.flow.component.button.Button;

public class DefaultOptionsButtonBuilder extends AbstractOptionsButtonConfigurator<DefaultOptionsButtonBuilder> {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultOptionsButtonBuilder(Button component) {
        super(component);
    }

    public DefaultOptionsButtonBuilder() {
        super();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected DefaultOptionsButtonBuilder getConfigurator() {
        return this;
    }
}
