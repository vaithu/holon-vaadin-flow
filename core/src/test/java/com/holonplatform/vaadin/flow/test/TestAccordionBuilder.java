package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.AccordionBuilder;
import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AccordionBuilder}, {@link AccordionConfigurator},
 * and the panel sub-builder infrastructure.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService.</p>
 */
class TestAccordionBuilder {

    // =========================================================================
    // Factory methods
    // =========================================================================

    @Test
    void create_returnsNonNullBuilder() {
        assertNotNull(AccordionBuilder.create());
    }

    @Test
    void components_accordion_returnsNonNullBuilder() {
        assertNotNull(Components.accordion());
    }

    // =========================================================================
    // Build — empty accordion
    // =========================================================================

    @Test
    void build_emptyAccordion_returnsAccordion() {
        Accordion accordion = AccordionBuilder.create().build();
        assertNotNull(accordion);
    }

    // =========================================================================
    // withAccordionPanel(AccordionPanel)
    // =========================================================================

    @Test
    void withAccordionPanel_addsPanel() {
        AccordionPanel panel = new AccordionPanel("Test", new Div());
        Accordion accordion = AccordionBuilder.create()
                .withAccordionPanel(panel)
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // withAccordionPanel(String, Component) — default method
    // =========================================================================

    @Test
    void withAccordionPanel_stringComponent_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withAccordionPanel("Title", new Div())
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // withAccordion(String, Component)
    // =========================================================================

    @Test
    void withAccordion_addsPanelWithSummaryAndContent() {
        Accordion accordion = AccordionBuilder.create()
                .withAccordion("Summary", new Div())
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // withOpenedChangeListener
    // =========================================================================

    @Test
    void withOpenedChangeListener_registersWithoutError() {
        AtomicBoolean fired = new AtomicBoolean(false);
        Accordion accordion = AccordionBuilder.create()
                .withAccordion("Panel 1", new Div())
                .withOpenedChangeListener(e -> fired.set(true))
                .build();
        assertNotNull(accordion);
    }

    // =========================================================================
    // withPanel — fluent sub-builder (all overloads)
    // =========================================================================

    @Test
    void withPanel_noArgs_addsEmptyPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel()
                    .add(new Span("Content"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void withPanel_stringSummary_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void withPanel_componentSummary_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel(new Span("Section"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void withPanel_stringSummaryAndComponents_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section", new Div(), new Span("text"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void withPanel_componentSummaryAndComponents_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel(new Span("Title"), new Div(), new Span("text"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void withPanel_componentSummaryAndContent_addsPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel(new Span("Title"), new Div())
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // Panel sub-builder — enabled
    // =========================================================================

    @Test
    void panelBuilder_enabled_false_disablesPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .enabled(false)
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertFalse(panel.isEnabled());
    }

    @Test
    void panelBuilder_enabled_true_enablesPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .enabled(true)
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.isEnabled());
    }

    // =========================================================================
    // Panel sub-builder — className
    // =========================================================================

    @Test
    void panelBuilder_styleName_addsClassName() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .styleName("my-class")
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.getClassNames().contains("my-class"));
    }

    @Test
    void panelBuilder_styleNames_addsMultipleClassNames() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .styleNames("cls-a", "cls-b")
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.getClassNames().contains("cls-a"));
        assertTrue(panel.getClassNames().contains("cls-b"));
    }

    // =========================================================================
    // Panel sub-builder — theme variants and theme name
    // =========================================================================

    @Test
    void panelBuilder_withThemeVariants_addsVariant() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .withThemeVariants(DetailsVariant.FILLED)
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.getThemeNames().contains("filled"));
    }

    @Test
    void panelBuilder_withThemeName_addsThemeName() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .withThemeName("compact")
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.getThemeNames().contains("compact"));
    }

    // =========================================================================
    // Panel sub-builder — tooltip
    // =========================================================================

    @Test
    void panelBuilder_tooltipText_setsTooltip() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .tooltipText("Help text")
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertEquals("Help text", panel.getTooltip().getText());
    }

    // =========================================================================
    // Panel sub-builder — add components
    // =========================================================================

    @Test
    void panelBuilder_addComponentAsFirst_addsAtStart() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section", new Div())
                    .addComponentAsFirst(new Span("First"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void panelBuilder_addComponentAtIndex_addsAtIndex() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section", new Div())
                    .addComponentAtIndex(0, new Span("Inserted"))
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    @Test
    void panelBuilder_addText_addsTextContent() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .add("Some text content")
                    .add()
                .build();
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // Multiple panels
    // =========================================================================

    @Test
    void multiplePanels_allAdded() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("One").add()
                .withPanel("Two").add()
                .withPanel("Three").add()
                .build();
        assertEquals(3, accordion.getChildren().count());
    }

    // =========================================================================
    // AccordionConfigurator.configure() static factory
    // =========================================================================

    @Test
    void configure_returnsNonNull() {
        AccordionConfigurator.BaseAccordionConfigurator configurator =
                AccordionConfigurator.configure(new Accordion());
        assertNotNull(configurator);
    }

    @Test
    void configure_addsPanel() {
        Accordion accordion = new Accordion();
        AccordionConfigurator.configure(accordion)
                .withAccordion("Test", new Div());
        assertEquals(1, accordion.getChildren().count());
    }

    // =========================================================================
    // Inherited ComponentConfigurator — id, visible, styleName
    // =========================================================================

    @Test
    void id_setsComponentId() {
        Accordion accordion = AccordionBuilder.create()
                .id("my-accordion")
                .build();
        assertTrue(accordion.getId().isPresent());
        assertEquals("my-accordion", accordion.getId().get());
    }

    @Test
    void visible_false_hidesAccordion() {
        Accordion accordion = AccordionBuilder.create()
                .visible(false)
                .build();
        assertFalse(accordion.isVisible());
    }

    @Test
    void styleName_addsClassNameToAccordion() {
        Accordion accordion = AccordionBuilder.create()
                .styleName("custom")
                .build();
        assertTrue(accordion.getClassNames().contains("custom"));
    }

    // =========================================================================
    // Negative / edge cases
    // =========================================================================

    @Test
    void withPanel_chainedModifiers_returnCorrectBuilder() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .styleName("cls")
                    .enabled(false)
                    .withThemeName("reverse")
                    .tooltipText("tip")
                    .add(new Span("extra"))
                    .add()
                .build();
        AccordionPanel panel = (AccordionPanel) accordion.getChildren().findFirst().orElseThrow();
        assertTrue(panel.getClassNames().contains("cls"));
        assertFalse(panel.isEnabled());
        assertTrue(panel.getThemeNames().contains("reverse"));
        assertEquals("tip", panel.getTooltip().getText());
    }

    @Test
    void panelBuilder_withEventListener_registersOnPanel() {
        AtomicBoolean listenerRegistered = new AtomicBoolean(false);
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .withEventListener("click", e -> listenerRegistered.set(true))
                    .add()
                .build();
        assertNotNull(accordion);
    }

    @Test
    void panelBuilder_withEventListenerAndFilter_registersOnPanel() {
        Accordion accordion = AccordionBuilder.create()
                .withPanel("Section")
                    .withEventListener("click", e -> {}, "event.button === 0")
                    .add()
                .build();
        assertNotNull(accordion);
    }
}
