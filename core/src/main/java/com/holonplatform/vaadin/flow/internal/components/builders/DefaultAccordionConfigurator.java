package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.vaadin.flow.component.accordion.Accordion;

public class DefaultAccordionConfigurator
    extends AbstractAccordionConfigurator<AccordionConfigurator.BaseAccordionConfigurator>
        implements AccordionConfigurator.BaseAccordionConfigurator {


    public DefaultAccordionConfigurator(Accordion accordion) {
        super(accordion);
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected BaseAccordionConfigurator getConfigurator() {
        return this;
    }


}
