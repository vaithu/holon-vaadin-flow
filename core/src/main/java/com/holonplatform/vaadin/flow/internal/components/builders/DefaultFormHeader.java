package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultFormHeader
        extends AbstractFormHeaderConfigurator<FormHeaderBuilder>
        implements FormHeaderBuilder {


    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultFormHeader(HorizontalLayout component) {
        super(component);
    }

    public DefaultFormHeader() {
        this(new HorizontalLayout());
    }

    public DefaultFormHeader(String title) {
        this();
        title(title);
    }

    @Override
    public HorizontalLayout build() {
        return getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected FormHeaderBuilder getConfigurator() {
        return this;
    }
}
