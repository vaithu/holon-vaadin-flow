package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.enums.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestHeadingLevel {

    @Test
    void testH1() {
        Component c = HeadingLevel.H1.getComponent("Title");
        assertInstanceOf(H1.class, c);
        assertEquals("Title", c.getElement().getText());
    }

    @Test
    void testH2() {
        Component c = HeadingLevel.H2.getComponent("Title");
        assertInstanceOf(H2.class, c);
    }

    @Test
    void testH3() {
        Component c = HeadingLevel.H3.getComponent("Title");
        assertInstanceOf(H3.class, c);
    }

    @Test
    void testH4() {
        Component c = HeadingLevel.H4.getComponent("Title");
        assertInstanceOf(H4.class, c);
    }

    @Test
    void testH5() {
        Component c = HeadingLevel.H5.getComponent("Title");
        assertInstanceOf(H5.class, c);
    }

    @Test
    void testH6() {
        Component c = HeadingLevel.H6.getComponent("Title");
        assertInstanceOf(H6.class, c);
    }

    @Test
    void testNone() {
        Component c = HeadingLevel.NONE.getComponent("Title");
        assertInstanceOf(Span.class, c);
        assertEquals("Title", c.getElement().getText());
    }

    @Test
    void testAllValues() {
        for (HeadingLevel level : HeadingLevel.values()) {
            Component c = level.getComponent("test");
            assertNotNull(c);
            assertEquals("test", c.getElement().getText());
        }
    }
}
