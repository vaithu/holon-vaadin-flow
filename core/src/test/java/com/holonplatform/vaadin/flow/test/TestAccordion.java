package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccordion {

    @Test
    public void testAccordion() {
        Accordion accordion = Components.accordion()
                .withPanel()
                .add()
                .build();

        assertNotNull(accordion);
        assertEquals(1,accordion.getChildren().count());
        accordion.getChildren().forEach(component -> assertInstanceOf(AccordionPanel.class,component));
    }
}
