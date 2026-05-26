package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ContextMenuBuilder;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ContextMenuBuilder}.
 */
class TestContextMenuBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(ContextMenuBuilder.create());
    }

    @Test
    void build_noTarget_returnsContextMenu() {
        ContextMenu menu = ContextMenuBuilder.create().build();
        assertNotNull(menu);
    }

    @Test
    void build_withTarget_setsTarget() {
        Div target = new Div("Target");
        ContextMenu menu = ContextMenuBuilder.create().build(target);
        assertNotNull(menu);
        assertSame(target, menu.getTarget());
    }

    @Test
    void withItem_text_addsItem() {
        ContextMenu menu = ContextMenuBuilder.create()
                .withItem("Edit", e -> {})
                .build();
        assertNotNull(menu);
        assertEquals(1, menu.getItems().size());
        assertEquals("Edit", menu.getItems().get(0).getText());
    }

    @Test
    void withItem_multiple_addsItems() {
        ContextMenu menu = ContextMenuBuilder.create()
                .withItem("Cut", e -> {})
                .withItem("Copy", e -> {})
                .withItem("Paste", e -> {})
                .build();
        assertEquals(3, menu.getItems().size());
    }

    @Test
    void withItem_component_addsItem() {
        ContextMenu menu = ContextMenuBuilder.create()
                .withItem(new Div("Custom"), e -> {})
                .build();
        assertEquals(1, menu.getItems().size());
    }

    @Test
    void fluent_chain() {
        Div target = new Div();
        ContextMenu menu = ContextMenuBuilder.create()
                .withItem("Action 1", e -> {})
                .withItem("Action 2", e -> {})
                .build(target);
        assertNotNull(menu);
        assertSame(target, menu.getTarget());
        assertEquals(2, menu.getItems().size());
    }
}
