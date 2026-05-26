package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.Tag;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTag {

    @Test
    void testTextOnly() {
        var tag = new Tag("Label");
        assertTrue(tag.getClassNames().contains("tag"));
    }

    @Test
    void testWithPrefixAndColor() {
        var prefix = new Span("•");
        var tag = new Tag(prefix, "Active", Color.Text.SUCCESS);
        assertTrue(tag.getClassNames().contains("tag"));
        assertTrue(tag.getClassNames().contains(Color.Text.SUCCESS.getClassName()));
    }

    @Test
    void testWithPrefixDefaultColor() {
        var prefix = new Span("•");
        var tag = new Tag(prefix, "Status");
        assertTrue(tag.getClassNames().contains("tag"));
        assertTrue(tag.getClassNames().contains(Color.Text.SECONDARY.getClassName()));
    }

    @Test
    void testSetTextColor() {
        var tag = new Tag("Label");
        tag.setTextColor(Color.Text.PRIMARY);
        assertTrue(tag.getClassNames().contains(Color.Text.PRIMARY.getClassName()));
    }

    @Test
    void testSetTextColorReplacePrevious() {
        var tag = new Tag("Label");
        tag.setTextColor(Color.Text.PRIMARY);
        tag.setTextColor(Color.Text.ERROR);
        assertFalse(tag.getClassNames().contains(Color.Text.PRIMARY.getClassName()));
        assertTrue(tag.getClassNames().contains(Color.Text.ERROR.getClassName()));
    }

    @Test
    void testSetTextColorNull() {
        var tag = new Tag("Label");
        tag.setTextColor(Color.Text.PRIMARY);
        tag.setTextColor(null);
        assertFalse(tag.getClassNames().contains(Color.Text.PRIMARY.getClassName()));
    }

    @Test
    void testSetPrefix() {
        var tag = new Tag("Label");
        tag.setPrefix(new Span("icon"));
        // prefix visible
    }

    @Test
    void testSetPrefixNull() {
        var tag = new Tag("Label");
        tag.setPrefix((com.vaadin.flow.component.Component[]) null);
        // prefix hidden, no exception
    }
}
