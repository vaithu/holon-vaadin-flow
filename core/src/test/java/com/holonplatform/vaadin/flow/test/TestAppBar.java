package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAppBar {

    @Test
    void testDefaultConstructor() {
        var appBar = new AppBar();
        assertTrue(appBar.getClassNames().contains("app-bar"));
        assertEquals("banner", appBar.getElement().getAttribute("role"));
    }

    @Test
    void testConstructorWithComponents() {
        var logo = new Span("Logo");
        var appBar = new AppBar(logo);
        assertTrue(appBar.getClassNames().contains("app-bar"));
        // logo goes into start slot
        assertEquals(3, appBar.getComponentCount()); // start, middle, end
    }

    @Test
    void testAddToStart() {
        var appBar = new AppBar();
        var comp = new Span("Start");
        appBar.addToStart(comp);
        assertTrue(appBar.getClassNames().contains("app-bar"));
    }

    @Test
    void testAddToMiddle() {
        var appBar = new AppBar();
        var comp = new Span("Middle");
        appBar.addToMiddle(comp);
        assertTrue(appBar.getClassNames().contains("app-bar"));
    }

    @Test
    void testAddToEnd() {
        var appBar = new AppBar();
        var comp = new Span("End");
        appBar.addToEnd(comp);
        assertTrue(appBar.getClassNames().contains("app-bar"));
    }

    @Test
    void testAddToEndAtIndex() {
        var appBar = new AppBar();
        var first = new Span("First");
        var second = new Span("Second");
        appBar.addToEnd(first);
        appBar.addToEnd(0, second);
        assertTrue(appBar.getClassNames().contains("app-bar"));
    }

    @Test
    void testWidthFull() {
        var appBar = new AppBar();
        assertEquals("100%", appBar.getWidth());
    }

    @Test
    void testThreeSlotStructure() {
        var appBar = new AppBar();
        var children = appBar.getChildren().toList();
        assertEquals(3, children.size());
        assertTrue(children.get(0).getClassNames().contains("app-bar__start"));
        assertTrue(children.get(1).getClassNames().contains("app-bar__middle"));
        assertTrue(children.get(2).getClassNames().contains("app-bar__end"));
    }
}
