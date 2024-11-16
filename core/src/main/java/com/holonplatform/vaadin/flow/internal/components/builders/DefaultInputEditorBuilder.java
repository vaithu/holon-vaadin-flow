package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HasInputEditorConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultInputEditorBuilder
        extends AbstractInputEditorConfigurator<HasInputEditorConfigurator.InputEditorBuilder>
        implements HasInputEditorConfigurator.InputEditorBuilder {

    private final HorizontalLayout horizontalLayout;
    public DefaultInputEditorBuilder(HorizontalLayout horizontalLayout) {
        super(horizontalLayout);
        this.horizontalLayout = horizontalLayout;
        this.horizontalLayout.setSpacing(false);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected InputEditorBuilder getConfigurator() {
        return this;
    }

    @Override
    public HorizontalLayout build() {
        return this.horizontalLayout;
    }
}
