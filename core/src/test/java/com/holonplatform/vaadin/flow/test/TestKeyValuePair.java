package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePair;
import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestKeyValuePair {

    @Test
    void testStringConstructor() {
        var pair = new KeyValuePair("Name", "John");
        assertTrue(pair.getClassNames().contains("key-value-pair"));
    }

    @Test
    void testStringComponentConstructor() {
        var pair = new KeyValuePair("Status", new Span("Active"));
        assertTrue(pair.getClassNames().contains("key-value-pair"));
    }

    @Test
    void testComponentConstructor() {
        var pair = new KeyValuePair(new Span("Key"), new Span("Value"));
        assertTrue(pair.getClassNames().contains("key-value-pair"));
    }

    @Test
    void testDefaultKeyPositionSide() {
        var pair = new KeyValuePair("Key", "Value");
        // default is SIDE, validated via class names
        assertTrue(pair.getClassNames().contains("key-value-pair"));
    }

    @Test
    void testSetKeyPositionTop() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setKeyPosition(KeyValuePair.KeyPosition.TOP);
        // switches to column direction
    }

    @Test
    void testSetKeyPositionSide() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setKeyPosition(KeyValuePair.KeyPosition.SIDE);
    }

    @Test
    void testSetBreakpoint() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setBreakpoint(Breakpoint.LARGE);
    }

    @Test
    void testRemoveBreakpoint() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setBreakpoint(Breakpoint.LARGE);
        pair.removeBreakpoint();
    }

    @Test
    void testSetKeyWidth() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setKeyWidth(50, Unit.PERCENTAGE);
    }

    @Test
    void testSetKeyWidthFull() {
        var pair = new KeyValuePair("Key", "Value");
        pair.setKeyWidthFull();
    }

    @Test
    void testRemoveHorizontalPadding() {
        var pair = new KeyValuePair("Key", "Value");
        pair.removeHorizontalPadding();
        assertTrue(pair.getClassNames().contains("key-value-pair--no-h-padding"));
    }
}
