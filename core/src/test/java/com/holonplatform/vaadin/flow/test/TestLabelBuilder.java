package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.vaadin.flow.component.html.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LabelBuilder} and {@link LabelConfigurator}.
 */
class TestLabelBuilder {

    // =========================================================================
    // Factory methods
    // =========================================================================

    @Test
    void span_returnsNonNull() {
        assertNotNull(LabelBuilder.span());
    }

    @Test
    void div_returnsNonNull() {
        assertNotNull(LabelBuilder.div());
    }

    @Test
    void paragraph_returnsNonNull() {
        assertNotNull(LabelBuilder.paragraph());
    }

    @Test
    void h1_returnsNonNull() {
        assertNotNull(LabelBuilder.h1());
    }

    @Test
    void h2_returnsNonNull() {
        assertNotNull(LabelBuilder.h2());
    }

    @Test
    void h3_returnsNonNull() {
        assertNotNull(LabelBuilder.h3());
    }

    @Test
    void h4_returnsNonNull() {
        assertNotNull(LabelBuilder.h4());
    }

    @Test
    void h5_returnsNonNull() {
        assertNotNull(LabelBuilder.h5());
    }

    @Test
    void h6_returnsNonNull() {
        assertNotNull(LabelBuilder.h6());
    }

    @Test
    void emphasis_returnsNonNull() {
        assertNotNull(LabelBuilder.emphasis());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Nested
    class BuildTests {

        @Test
        void span_builds() {
            Span span = LabelBuilder.span().build();
            assertNotNull(span);
        }

        @Test
        void div_builds() {
            Div div = LabelBuilder.div().build();
            assertNotNull(div);
        }

        @Test
        void h1_builds() {
            H1 h1 = LabelBuilder.h1().build();
            assertNotNull(h1);
        }
    }

    // =========================================================================
    // Text
    // =========================================================================

    @Test
    void text_setsText() {
        Span span = LabelBuilder.span().text("Hello").build();
        assertEquals("Hello", span.getText());
    }

    @Test
    void htmlText_setsInnerHtml() {
        Div div = LabelBuilder.div().htmlText("<b>Bold</b>").build();
        assertNotNull(div);
    }

    // =========================================================================
    // Component Configurator
    // =========================================================================

    @Test
    void id_setsId() {
        Span span = LabelBuilder.span().id("my-label").build();
        assertTrue(span.getId().isPresent());
        assertEquals("my-label", span.getId().get());
    }

    @Test
    void styleName_addsClass() {
        Span span = LabelBuilder.span().styleName("title").build();
        assertTrue(span.getClassNames().contains("title"));
    }

    @Test
    void visible_false_hidesComponent() {
        Span span = LabelBuilder.span().visible(false).build();
        assertFalse(span.isVisible());
    }

    @Test
    void enabled_false_disablesComponent() {
        Span span = LabelBuilder.span().enabled(false).build();
        assertFalse(span.isEnabled());
    }

    // =========================================================================
    // Configure (existing instance)
    // =========================================================================

    @Test
    void configure_existingSpan() {
        Span span = new Span();
        LabelConfigurator.configure(span).text("Configured");
        assertEquals("Configured", span.getText());
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Span span = LabelBuilder.span()
                .text("My Label")
                .id("lbl-1")
                .styleName("highlight")
                .visible(true)
                .enabled(true)
                .build();
        assertNotNull(span);
        assertEquals("My Label", span.getText());
        assertEquals("lbl-1", span.getId().orElse(null));
        assertTrue(span.getClassNames().contains("highlight"));
    }
}
