package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestHighlight {

    @Test
    void testBasicConstructor() {
        var h = new Highlight("Revenue", "$42,000");
        assertTrue(h.getClassNames().contains("highlight"));
    }

    @Test
    void testConstructorWithPrefix() {
        var icon = new Span("$");
        var h = new Highlight(icon, "Revenue", "$42,000");
        assertTrue(h.getClassNames().contains("highlight"));
    }

    @Test
    void testConstructorWithSuffix() {
        var suffix = new Span("+12%");
        var h = new Highlight("Revenue", "$42,000", suffix);
        assertTrue(h.getClassNames().contains("highlight"));
    }

    @Test
    void testConstructorWithPrefixAndSuffix() {
        var prefix = new Span("$");
        var suffix = new Span("+12%");
        var h = new Highlight(prefix, "Revenue", "$42,000", suffix);
        assertTrue(h.getClassNames().contains("highlight"));
    }

    @Test
    void testSetHeading() {
        var h = new Highlight("Revenue", "$42,000");
        h.setHeading("Expenses");
        // no exception
    }

    @Test
    void testSetValue() {
        var h = new Highlight("Revenue", "$42,000");
        h.setValue("$50,000");
        // no exception
    }

    @Test
    void testSetValueFontSize() {
        var h = new Highlight("Revenue", "$42,000");
        h.setValueFontSize(Font.Size.XLARGE);
        h.setValueFontSize(Font.Size.SMALL); // replaces previous
    }

    @Test
    void testSetDetails() {
        var h = new Highlight("Revenue", "$42,000");
        h.setDetails(new Span("vs last month"), new Span("+12%"));
        // details should be visible
    }

    @Test
    void testSetDetailsNull() {
        var h = new Highlight("Revenue", "$42,000");
        h.setDetails((com.vaadin.flow.component.Component[]) null);
        // no exception
    }

    @Test
    void testSetPrefix() {
        var h = new Highlight("Revenue", "$42,000");
        h.setPrefix(new Span("icon"));
        h.setPrefix((com.vaadin.flow.component.Component[]) null); // clear
    }

    @Test
    void testSetSuffix() {
        var h = new Highlight("Revenue", "$42,000");
        h.setSuffix(new Span("badge"));
        h.setSuffix((com.vaadin.flow.component.Component[]) null); // clear
    }

    @Test
    void testSetHeadingLevel() {
        var h = new Highlight("Revenue", "$42,000");
        h.setHeadingLevel(HeadingLevel.H1);
        h.setHeadingLevel(HeadingLevel.H4);
    }
}
