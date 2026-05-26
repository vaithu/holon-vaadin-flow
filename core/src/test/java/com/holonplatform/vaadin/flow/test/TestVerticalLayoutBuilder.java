package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VerticalLayoutBuilder}.
 */
class TestVerticalLayoutBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(VerticalLayoutBuilder.create());
    }

    @Test
    void build_default_returnsVerticalLayout() {
        VerticalLayout layout = VerticalLayoutBuilder.create().build();
        assertNotNull(layout);
    }

    @Test
    void build_default_spacingDisabled() {
        VerticalLayout layout = VerticalLayoutBuilder.create().build();
        assertFalse(layout.isSpacing());
    }

    @Test
    void build_default_marginDisabled() {
        VerticalLayout layout = VerticalLayoutBuilder.create().build();
        assertFalse(layout.isMargin());
    }

    @Test
    void add_component() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .add(new Div("A"))
                .build();
        assertEquals(1, layout.getComponentCount());
    }

    @Test
    void add_multiple() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .add(new Div("A"), new Span("B"))
                .build();
        assertEquals(2, layout.getComponentCount());
    }

    @Test
    void spacing_enables() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .spacing(true)
                .build();
        assertTrue(layout.isSpacing());
    }

    @Test
    void padding_enables() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .padding(true)
                .build();
        assertTrue(layout.isPadding());
    }

    @Test
    void styleName_addsClass() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .styleName("my-vl")
                .build();
        assertTrue(layout.getClassNames().contains("my-vl"));
    }

    @Test
    void fluent_chain() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .spacing(true)
                .padding(true)
                .add(new Div("Child"))
                .styleName("wrapper")
                .build();
        assertNotNull(layout);
        assertTrue(layout.isSpacing());
        assertTrue(layout.isPadding());
        assertEquals(1, layout.getComponentCount());
        assertTrue(layout.getClassNames().contains("wrapper"));
    }
}
