package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.DetailsBuilder;
import com.holonplatform.vaadin.flow.components.builders.HasDetailsConfigurator;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DetailsBuilder} and the {@link HasDetailsConfigurator} infrastructure.
 */
class TestDetailsBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(DetailsBuilder.create());
    }

    // =========================================================================
    // Build — empty details
    // =========================================================================

    @Test
    void build_empty_returnsDetails() {
        Details details = DetailsBuilder.create().build();
        assertNotNull(details);
    }

    // =========================================================================
    // summaryText
    // =========================================================================

    @Test
    void summaryText_setsSummary() {
        Details details = DetailsBuilder.create()
                .summaryText("Click to expand")
                .build();
        assertEquals("Click to expand", details.getSummaryText());
    }

    // =========================================================================
    // summary — component
    // =========================================================================

    @Test
    void summary_component_setsSummary() {
        Span summarySpan = new Span("Custom Summary");
        Details details = DetailsBuilder.create()
                .summary(summarySpan)
                .build();
        assertNotNull(details.getSummary());
    }

    // =========================================================================
    // opened
    // =========================================================================

    @Test
    void opened_true_opensDetails() {
        Details details = DetailsBuilder.create()
                .summaryText("Section")
                .opened(true)
                .build();
        assertTrue(details.isOpened());
    }

    @Test
    void opened_false_closesDetails() {
        Details details = DetailsBuilder.create()
                .opened(false)
                .build();
        assertFalse(details.isOpened());
    }

    // =========================================================================
    // add — components as content
    // =========================================================================

    @Test
    void add_components_addsContent() {
        Details details = DetailsBuilder.create()
                .summaryText("Section")
                .add(new Paragraph("Content 1"), new Paragraph("Content 2"))
                .build();
        assertEquals(2, details.getContent().count());
    }

    @Test
    void add_text_addsTextContent() {
        Details details = DetailsBuilder.create()
                .summaryText("Section")
                .add("Some text")
                .build();
        assertNotNull(details);
    }

    @Test
    void addComponentAsFirst_addsAtStart() {
        Details details = DetailsBuilder.create()
                .summaryText("Section")
                .add(new Div())
                .addComponentAsFirst(new Span("First"))
                .build();
        assertNotNull(details);
    }

    @Test
    void addComponentAtIndex_addsAtIndex() {
        Details details = DetailsBuilder.create()
                .summaryText("Section")
                .add(new Div())
                .addComponentAtIndex(0, new Span("Inserted"))
                .build();
        assertNotNull(details);
    }

    // =========================================================================
    // Theme variants
    // =========================================================================

    @Test
    void withThemeVariants_addsVariant() {
        Details details = DetailsBuilder.create()
                .withThemeVariants(DetailsVariant.FILLED)
                .build();
        assertTrue(details.getThemeNames().contains("filled"));
    }

    @Test
    void withThemeVariants_multipleVariants() {
        Details details = DetailsBuilder.create()
                .withThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE)
                .build();
        assertTrue(details.getThemeNames().contains("filled"));
        assertTrue(details.getThemeNames().contains("reverse"));
    }

    // =========================================================================
    // Inherited ComponentConfigurator
    // =========================================================================

    @Test
    void id_setsComponentId() {
        Details details = DetailsBuilder.create()
                .id("my-details")
                .build();
        assertTrue(details.getId().isPresent());
        assertEquals("my-details", details.getId().get());
    }

    @Test
    void visible_false_hidesDetails() {
        Details details = DetailsBuilder.create()
                .visible(false)
                .build();
        assertFalse(details.isVisible());
    }

    @Test
    void styleName_addsClassName() {
        Details details = DetailsBuilder.create()
                .styleName("custom-details")
                .build();
        assertTrue(details.getClassNames().contains("custom-details"));
    }

    @Test
    void enabled_false_disables() {
        Details details = DetailsBuilder.create()
                .enabled(false)
                .build();
        assertFalse(details.isEnabled());
    }

    // =========================================================================
    // HasDetailsConfigurator.configure — existing instance
    // =========================================================================

    @Test
    void configure_returnsNonNull() {
        assertNotNull(HasDetailsConfigurator.configure(new Details()));
    }

    @Test
    void configure_mutatesExisting() {
        Details details = new Details();
        HasDetailsConfigurator.configure(details)
                .summaryText("Configured")
                .opened(true)
                .add(new Paragraph("Content"));
        assertEquals("Configured", details.getSummaryText());
        assertTrue(details.isOpened());
    }

    // =========================================================================
    // Fluent chaining
    // =========================================================================

    @Test
    void fluentChain_setsAll() {
        Details details = DetailsBuilder.create()
                .summaryText("Advanced Settings")
                .opened(true)
                .add(new Paragraph("Setting 1"), new Paragraph("Setting 2"))
                .withThemeVariants(DetailsVariant.FILLED)
                .styleName("advanced")
                .id("adv-details")
                .build();

        assertEquals("Advanced Settings", details.getSummaryText());
        assertTrue(details.isOpened());
        assertTrue(details.getThemeNames().contains("filled"));
        assertTrue(details.getClassNames().contains("advanced"));
        assertEquals("adv-details", details.getId().orElse(""));
    }
}
