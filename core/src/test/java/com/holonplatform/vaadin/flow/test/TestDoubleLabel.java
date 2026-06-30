package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.DoubleLabel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDoubleLabel {

    @Test
    void testConstruction() {
        var dl = new DoubleLabel("Top", "Bottom");
        assertTrue(dl.getClassNames().contains("double-label"));
        assertNotNull(dl.getSpanTop());
        assertNotNull(dl.getSpanBottom());
    }

    @Test
    void testSpanClassNames() {
        var dl = new DoubleLabel("Top", "Bottom");
        assertTrue(dl.getSpanTop().getClassNames().contains("double-label__top"));
        assertTrue(dl.getSpanBottom().getClassNames().contains("double-label__bottom"));
    }

    @Test
    void testSetAlignLeft() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setAlignLeft();
        assertTrue(dl.getClassNames().contains("double-label--align-left"));
        assertFalse(dl.getClassNames().contains("double-label--align-center"));
    }

    @Test
    void testSetAlignCenter() {
        // Center is the default: setAlignCenter() removes the left-alignment modifier.
        // No explicit "double-label--align-center" class is added (CSS default handles it).
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setAlignLeft();
        dl.setAlignCenter();
        assertFalse(dl.getClassNames().contains("double-label--align-left"),
                "setAlignCenter() must remove the left-alignment modifier");
    }

    @Test
    void testSetFixedWidth() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setFixedWidth();
        assertTrue(dl.getClassNames().contains("double-label--fixed-width"));
        assertFalse(dl.getClassNames().contains("double-label--grow"));
    }

    @Test
    void testSetGrow() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setFixedWidth();
        dl.setGrow();
        assertTrue(dl.getClassNames().contains("double-label--grow"));
        assertFalse(dl.getClassNames().contains("double-label--fixed-width"));
    }

    @Test
    void testSetNoBorder() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setNoBorder();
        assertTrue(dl.getClassNames().contains("double-label--no-border"));
    }

    @Test
    void testSetTitleTop() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setTitleTop("New Top");
        assertEquals("New Top", dl.getSpanTop().getText());
    }

    @Test
    void testSetTitleBottom() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.setTitleBottom("New Bottom");
        assertEquals("New Bottom", dl.getSpanBottom().getText());
    }

    @Test
    void testAddClassNamesToSpans() {
        var dl = new DoubleLabel("Top", "Bottom");
        dl.addClassNamesToSpans("custom-class");
        assertTrue(dl.getSpanTop().getClassNames().contains("custom-class"));
        assertTrue(dl.getSpanBottom().getClassNames().contains("custom-class"));
    }
}
