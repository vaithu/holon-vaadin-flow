package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.Preview;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPreview {

    @Test
    void testDefaultClassName() {
        var preview = new Preview();
        assertTrue(preview.getClassNames().contains("preview"));
    }

    @Test
    void testWithComponents() {
        var preview = new Preview(new Span("A"), new Span("B"));
        assertTrue(preview.getClassNames().contains("preview"));
        assertEquals(2, preview.getComponentCount());
    }

    @Test
    void testWithNullComponents() {
        var preview = new Preview((com.vaadin.flow.component.Component[]) null);
        assertTrue(preview.getClassNames().contains("preview"));
        assertEquals(0, preview.getComponentCount());
    }
}
