package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HorizontalLayoutBuilder}.
 */
class TestHorizontalLayoutBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(HorizontalLayoutBuilder.create());
    }

    @Test
    void build_empty_returnsLayout() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create().build();
        assertNotNull(layout);
    }

    @Test
    void build_spacingDisabledByDefault() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create().build();
        assertFalse(layout.isSpacing());
    }

    @Test
    void add_component_addsChild() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .add(new Div("Child"))
                .build();
        assertEquals(1, layout.getComponentCount());
    }

    @Test
    void add_multiple_components() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .add(new Div("A"), new Div("B"), new Span("C"))
                .build();
        assertEquals(3, layout.getComponentCount());
    }

    @Test
    void spacing_enablesSpacing() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .spacing()
                .build();
        assertTrue(layout.isSpacing());
    }

    @Test
    void padding_enablesPadding() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .padding()
                .build();
        assertTrue(layout.isPadding());
    }

    @Test
    void styleName_addsClassName() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .styleName("my-row")
                .build();
        assertTrue(layout.getClassNames().contains("my-row"));
    }

    @Test
    void fluent_chain_fullExample() {
        Div child = new Div("Child");
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .add(child)
                .spacing()
                .padding()
                .styleName("toolbar")
                .build();
        assertNotNull(layout);
        assertEquals(1, layout.getComponentCount());
        assertTrue(layout.isSpacing());
        assertTrue(layout.isPadding());
    }
}
