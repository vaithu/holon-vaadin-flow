package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.ListItem;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestListItem {

    @Test
    void testPrimaryAndSecondary() {
        var item = new ListItem("Primary", "Secondary");
        assertTrue(item.getClassNames().contains("list-item"));
    }

    @Test
    void testPrimaryOnly() {
        var item = new ListItem("Primary");
        assertTrue(item.getClassNames().contains("list-item"));
    }

    @Test
    void testWithPrefix() {
        var prefix = new Span("Icon");
        var item = new ListItem(prefix, "Primary", "Secondary");
        assertTrue(item.getClassNames().contains("list-item"));
    }

    @Test
    void testWithSuffix() {
        var suffix = new Span("Action");
        var item = new ListItem("Primary", "Secondary", suffix);
        assertTrue(item.getClassNames().contains("list-item"));
    }

    @Test
    void testWithPrefixAndSuffix() {
        var prefix = new Span("Icon");
        var suffix = new Span("Action");
        var item = new ListItem(prefix, "Primary", "Secondary", suffix);
        assertTrue(item.getClassNames().contains("list-item"));
    }

    @Test
    void testSetPrimaryText() {
        var item = new ListItem("Old", "Sub");
        item.setPrimaryText("New");
        assertEquals("New", item.getPrimary().getText());
    }

    @Test
    void testSetSecondaryText() {
        var item = new ListItem("Primary", "Old");
        item.setSecondaryText("New");
    }

    @Test
    void testSetReverse() {
        var item = new ListItem("Primary", "Secondary");
        item.setReverse(true);
        item.setReverse(false);
    }

    @Test
    void testSetDividerVisible() {
        var item = new ListItem("Primary", "Secondary");
        item.setDividerVisible(true);
        item.setDividerVisible(false);
    }

    @Test
    void testGetContent() {
        var item = new ListItem("Primary", "Secondary");
        assertNotNull(item.getContent());
        assertTrue(item.getContent().getClassNames().contains("list-item__content"));
    }
}
