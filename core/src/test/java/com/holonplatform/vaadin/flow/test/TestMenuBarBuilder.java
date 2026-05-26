package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.MenuBarBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MenuBarBuilder}.
 */
class TestMenuBarBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(MenuBarBuilder.create());
    }

    @Test
    void build_default_returnsMenuBar() {
        MenuBar mb = MenuBarBuilder.create().build();
        assertNotNull(mb);
    }

    @Test
    void withMenuItem_text_addsItem() {
        MenuBar mb = MenuBarBuilder.create()
                .withMenuItem("File", e -> {})
                .build();
        assertEquals(1, mb.getItems().size());
        assertEquals("File", mb.getItems().get(0).getText());
    }

    @Test
    void withMenuItem_multiple_addsItems() {
        MenuBar mb = MenuBarBuilder.create()
                .withMenuItem("File", e -> {})
                .withMenuItem("Edit", e -> {})
                .withMenuItem("View", e -> {})
                .build();
        assertEquals(3, mb.getItems().size());
    }

    @Test
    void withMenuItem_component_addsItem() {
        MenuBar mb = MenuBarBuilder.create()
                .withMenuItem(new Div("Custom"), e -> {})
                .build();
        assertEquals(1, mb.getItems().size());
    }

    @Test
    void fluent_chain() {
        MenuBar mb = MenuBarBuilder.create()
                .withMenuItem("Action 1", e -> {})
                .withMenuItem("Action 2", e -> {})
                .build();
        assertNotNull(mb);
        assertEquals(2, mb.getItems().size());
    }
}
