package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.internal.lumo.Background;
import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.holonplatform.vaadin.flow.internal.lumo.GridColumns;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePair;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestKeyValuePairs {

    @Test
    void testDefaultClassName() {
        var pairs = new KeyValuePairs();
        assertTrue(pairs.getClassNames().contains("key-value-pairs"));
    }

    @Test
    void testWithPairs() {
        var pair1 = new KeyValuePair("Name", "John");
        var pair2 = new KeyValuePair("Age", "30");
        var pairs = new KeyValuePairs(pair1, pair2);
        assertTrue(pairs.getClassNames().contains("key-value-pairs"));
    }

    @Test
    void testSetBackground() {
        var pairs = new KeyValuePairs();
        pairs.setBackground(Background.CONTRAST_5);
        assertTrue(pairs.getClassNames().contains(Background.CONTRAST_5.getClassName()));
    }

    @Test
    void testSetBackgroundReplaces() {
        var pairs = new KeyValuePairs();
        pairs.setBackground(Background.CONTRAST_5);
        pairs.setBackground(Background.CONTRAST_10);
        assertFalse(pairs.getClassNames().contains(Background.CONTRAST_5.getClassName()));
        assertTrue(pairs.getClassNames().contains(Background.CONTRAST_10.getClassName()));
    }

    @Test
    void testRemoveBackgroundColor() {
        var pairs = new KeyValuePairs();
        pairs.setBackground(Background.CONTRAST_5);
        pairs.removeBackgroundColor();
        assertFalse(pairs.getClassNames().contains(Background.CONTRAST_5.getClassName()));
    }

    @Test
    void testSetColumns() {
        var pairs = new KeyValuePairs();
        pairs.setColumns(GridColumns.COLUMNS_2);
        assertTrue(pairs.getClassNames().contains(GridColumns.COLUMNS_2.getClassName()));
    }

    @Test
    void testSetColumnsReplaces() {
        var pairs = new KeyValuePairs();
        pairs.setColumns(GridColumns.COLUMNS_2);
        pairs.setColumns(GridColumns.COLUMNS_3);
        assertFalse(pairs.getClassNames().contains(GridColumns.COLUMNS_2.getClassName()));
        assertTrue(pairs.getClassNames().contains(GridColumns.COLUMNS_3.getClassName()));
    }

    @Test
    void testSetStripes() {
        var pairs = new KeyValuePairs();
        pairs.setStripes(true);
        assertTrue(pairs.getElement().getThemeList().contains("stripes"));

        pairs.setStripes(false);
        assertFalse(pairs.getElement().getThemeList().contains("stripes"));
    }

    @Test
    void testSetBreakpoint() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.setBreakpoint(Breakpoint.LARGE);
        // no exception
    }

    @Test
    void testRemoveBreakpoint() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.setBreakpoint(Breakpoint.LARGE);
        pairs.removeBreakpoint();
        // no exception
    }

    @Test
    void testSetKeyPosition() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.setKeyPosition(KeyValuePair.KeyPosition.TOP);
        // no exception
    }

    @Test
    void testSetKeyWidth() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.setKeyWidth(30, Unit.PERCENTAGE);
        // no exception
    }

    @Test
    void testSetKeyWidthFull() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.setKeyWidthFull();
        // no exception
    }

    @Test
    void testRemoveHorizontalPadding() {
        var pair = new KeyValuePair("Key", "Value");
        var pairs = new KeyValuePairs(pair);
        pairs.removeHorizontalPadding();
        // no exception
    }
}
