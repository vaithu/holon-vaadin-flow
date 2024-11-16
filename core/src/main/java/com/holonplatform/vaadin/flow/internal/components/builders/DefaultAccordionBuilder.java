package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AccordionBuilder;
import com.vaadin.flow.component.accordion.Accordion;

public class DefaultAccordionBuilder
    extends AbstractAccordionConfigurator<AccordionBuilder>
        implements AccordionBuilder {
    public DefaultAccordionBuilder(Accordion accordion) {
        super(accordion);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected AccordionBuilder getConfigurator() {
        return this;
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public Accordion build() {
        return getComponent();
    }
}
