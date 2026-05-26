package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.ComponentView;
import com.holonplatform.vaadin.flow.vaadinplus.components.Preview;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestComponentView {

    @Test
    void testDefaultClassName() {
        var view = new ComponentView();
        assertTrue(view.getClassNames().contains("component-view"));
    }

    @Test
    void testAddH2() {
        var view = new ComponentView();
        view.addH2("My Heading");
        assertEquals(1, view.getComponentCount());
        var h2 = view.getChildren().findFirst().orElseThrow();
        assertTrue(h2.getClassNames().contains("component-view__heading"));
    }

    @Test
    void testAddPreview() {
        var view = new ComponentView();
        var comp = new Span("Content");
        view.addPreview(comp);
        assertEquals(1, view.getComponentCount());
        var child = view.getChildren().findFirst().orElseThrow();
        assertInstanceOf(Preview.class, child);
    }

    @Test
    void testMultipleChildren() {
        var view = new ComponentView();
        view.addH2("Title");
        view.addPreview(new Span("A"), new Span("B"));
        assertEquals(2, view.getComponentCount());
    }
}
