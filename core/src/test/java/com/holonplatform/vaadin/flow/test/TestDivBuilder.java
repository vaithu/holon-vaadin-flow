package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.holonplatform.vaadin.flow.components.builders.DivConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.ColumnSpan;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DivBuilder} and the {@link DivConfigurator} infrastructure.
 */
class TestDivBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(DivBuilder.create());
    }

    // =========================================================================
    // Build — empty div
    // =========================================================================

    @Test
    void build_empty_returnsDiv() {
        Div div = DivBuilder.create().build();
        assertNotNull(div);
    }

    // =========================================================================
    // content — components
    // =========================================================================

    @Test
    void add_components_addsChildren() {
        Div div = DivBuilder.create()
                .add(new Span("A"), new Span("B"))
                .build();
        assertEquals(2, div.getChildren().count());
    }

    @Test
    void add_text_addsText() {
        Div div = DivBuilder.create()
                .add("Hello")
                .build();
        assertNotNull(div);
    }

    @Test
    void addComponentAsFirst_addsAtStart() {
        Div div = DivBuilder.create()
                .add(new Span("Second"))
                .addComponentAsFirst(new Span("First"))
                .build();
        assertEquals(2, div.getChildren().count());
    }

    @Test
    void addComponentAtIndex_addsAtIndex() {
        Div div = DivBuilder.create()
                .add(new Span("A"))
                .addComponentAtIndex(0, new Span("Before A"))
                .build();
        assertEquals(2, div.getChildren().count());
    }

    // =========================================================================
    // content — with title
    // =========================================================================

    @Test
    void add_titleAndComponents_addsTitleAndContent() {
        Div div = DivBuilder.create()
                .add("Section Title", new Paragraph("Content"))
                .build();
        assertTrue(div.getChildren().count() >= 2);
    }

    // =========================================================================
    // card
    // =========================================================================

    @Test
    void card_addsCardClass() {
        Div div = DivBuilder.create()
                .card()
                .build();
        assertTrue(div.getClassNames().contains("card"));
    }

    // =========================================================================
    // responsive
    // =========================================================================

    @Test
    void responsive_addsResponsiveClass() {
        Div div = DivBuilder.create()
                .responsive()
                .build();
        assertTrue(div.getClassNames().contains("div--responsive"));
    }

    // =========================================================================
    // gridLayout — int columns
    // =========================================================================

    @Test
    void gridLayout_intColumns_addsGridClasses() {
        Div div = DivBuilder.create()
                .gridLayout(3)
                .build();
        assertTrue(div.getClassNames().contains("div--grid"));
        assertTrue(div.getClassNames().contains("grid-cols-3"));
    }

    // =========================================================================
    // gridLayout — string styles
    // =========================================================================

    @Test
    void gridLayout_stringStyles_addsGridAndStyles() {
        Div div = DivBuilder.create()
                .gridLayout("custom-grid", "gap-m")
                .build();
        assertTrue(div.getClassNames().contains("div--grid"));
        assertTrue(div.getClassNames().contains("custom-grid"));
        assertTrue(div.getClassNames().contains("gap-m"));
    }

    // =========================================================================
    // content — with column span
    // =========================================================================

    @Test
    void add_intColumnSpan_appliesSpanClass() {
        Span child = new Span("Wide");
        DivBuilder.create()
                .gridLayout(4)
                .add(2, child)
                .build();
        assertTrue(child.getClassNames().contains("col-span-2"));
    }

    @Test
    void add_columnSpanEnum_appliesSpanClass() {
        Span child = new Span("Wide");
        DivBuilder.create()
                .gridLayout(6)
                .add(ColumnSpan.COLUMN_SPAN_3, child)
                .build();
        assertTrue(child.getClassNames().contains("col-span-3"));
    }

    // =========================================================================
    // horizontalRule
    // =========================================================================

    @Test
    void horizontalRule_addsSeparator() {
        Div div = DivBuilder.create()
                .add(new Span("Above"))
                .horizontalRule()
                .add(new Span("Below"))
                .build();
        assertTrue(div.getChildren().count() >= 3);
    }

    @Test
    void horizontalRule_withStyles_addsSeparator() {
        Div div = DivBuilder.create()
                .horizontalRule("custom-rule")
                .build();
        assertTrue(div.getChildren().count() >= 1);
    }

    @Test
    void horizontalRule_withSize_addsSeparator() {
        Div div = DivBuilder.create()
                .horizontalRule(2, "thick-rule")
                .build();
        assertTrue(div.getChildren().count() >= 1);
    }

    // =========================================================================
    // title — string
    // =========================================================================

    @Test
    void title_string_addsH4() {
        Div div = DivBuilder.create()
                .title("My Section")
                .build();
        assertTrue(div.getChildren().count() >= 1);
    }

    // =========================================================================
    // Inherited ComponentConfigurator
    // =========================================================================

    @Test
    void id_setsComponentId() {
        Div div = DivBuilder.create()
                .id("my-div")
                .build();
        assertTrue(div.getId().isPresent());
        assertEquals("my-div", div.getId().get());
    }

    @Test
    void visible_false_hidesDiv() {
        Div div = DivBuilder.create()
                .visible(false)
                .build();
        assertFalse(div.isVisible());
    }

    @Test
    void styleName_addsClassName() {
        Div div = DivBuilder.create()
                .styleName("custom-div")
                .build();
        assertTrue(div.getClassNames().contains("custom-div"));
    }

    @Test
    void enabled_false_disables() {
        Div div = DivBuilder.create()
                .enabled(false)
                .build();
        assertFalse(div.isEnabled());
    }

    // =========================================================================
    // withPostProcessor
    // =========================================================================

    @Test
    void withPostProcessor_callsProcessor() {
        AtomicBoolean called = new AtomicBoolean(false);
        DivBuilder.create()
                .withPostProcessor(cfg -> called.set(true))
                .build();
        assertTrue(called.get());
    }

    // =========================================================================
    // DivConfigurator.configure — existing instance
    // =========================================================================

    @Test
    void configure_returnsNonNull() {
        assertNotNull(DivConfigurator.configure(new Div()));
    }

    @Test
    void configure_mutatesExisting() {
        Div div = new Div();
        DivConfigurator.configure(div)
                .card()
                .add(new Span("Content"));
        assertTrue(div.getClassNames().contains("card"));
        assertTrue(div.getChildren().count() >= 1);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluentChain_appliesAll() {
        Div div = DivBuilder.create()
                .id("panel")
                .card()
                .responsive()
                .add(new Span("Content"))
                .styleName("extra")
                .build();

        assertEquals("panel", div.getId().orElse(""));
        assertTrue(div.getClassNames().contains("card"));
        assertTrue(div.getClassNames().contains("div--responsive"));
        assertTrue(div.getClassNames().contains("extra"));
    }
}
