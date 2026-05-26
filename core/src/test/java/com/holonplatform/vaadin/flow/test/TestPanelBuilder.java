package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.IyenPanel;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PanelBuilder}.
 */
class TestPanelBuilder {

    @Test
    void create_empty_returnsNonNull() {
        assertNotNull(PanelBuilder.create());
    }

    @Test
    void create_withComponents_returnsNonNull() {
        assertNotNull(PanelBuilder.create(new Span("A"), new Span("B")));
    }

    @Test
    void build_default_returnsPanel() {
        IyenPanel panel = PanelBuilder.create().build();
        assertNotNull(panel);
        assertTrue(panel.getClassNames().contains("iyen-panel"));
    }

    @Test
    void build_withComponents_containsChildren() {
        IyenPanel panel = PanelBuilder.create(new Span("A")).build();
        assertEquals(1, panel.getComponentCount());
    }

    @Test
    void add_components() {
        IyenPanel panel = PanelBuilder.create()
                .add(new Span("X"))
                .build();
        assertEquals(1, panel.getComponentCount());
    }

    @Test
    void styleName_addsClassName() {
        IyenPanel panel = PanelBuilder.create()
                .styleName("my-panel")
                .build();
        assertTrue(panel.getClassNames().contains("my-panel"));
    }

    @Test
    void fluent_chain() {
        IyenPanel panel = PanelBuilder.create()
                .add(new Span("Content"))
                .styleName("card-panel")
                .build();
        assertNotNull(panel);
        assertTrue(panel.getClassNames().contains("iyen-panel"));
        assertTrue(panel.getClassNames().contains("card-panel"));
        assertEquals(1, panel.getComponentCount());
    }
}
