package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAccordionBuilder;
import com.vaadin.flow.component.accordion.Accordion;

public interface AccordionBuilder extends AccordionConfigurator<AccordionBuilder>, ComponentBuilder<Accordion, AccordionBuilder>, DeferrableLocalizationConfigurator<AccordionBuilder> {

    static AccordionBuilder create() {
        return new DefaultAccordionBuilder(new Accordion());

    }

}