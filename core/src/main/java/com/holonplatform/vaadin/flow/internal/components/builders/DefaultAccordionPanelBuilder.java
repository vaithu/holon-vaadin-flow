package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.vaadin.flow.component.accordion.AccordionPanel;

public class DefaultAccordionPanelBuilder<B extends AccordionConfigurator<B>> extends AbstractAccordionPanelConfigurator<B> {
    public DefaultAccordionPanelBuilder(B parentBuilder, AccordionPanel accordionPanel) {
        super(parentBuilder, accordionPanel);

    }

}
