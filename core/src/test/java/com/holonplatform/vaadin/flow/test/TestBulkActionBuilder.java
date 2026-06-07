package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BulkActionBuilder}.
 */
class TestBulkActionBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(BulkActionBuilder.create());
    }

    @Test
    void build_default_returnsHorizontalLayout() {
        HorizontalLayout layout = BulkActionBuilder.create().build();
        assertNotNull(layout);
    }

    @Test
    void build_default_isWidthFull() {
        HorizontalLayout layout = BulkActionBuilder.create().build();
        assertEquals("100%", layout.getWidth());
    }

    @Test
    void selected_int() {
        HorizontalLayout layout = BulkActionBuilder.create()
                .selected(3)
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void selected_string() {
        HorizontalLayout layout = BulkActionBuilder.create()
                .selected("5 items selected")
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void selected_span() {
        Span span = new Span("10 selected");
        HorizontalLayout layout = BulkActionBuilder.create()
                .selected(span)
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void bulkAction_menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Delete");
        HorizontalLayout layout = BulkActionBuilder.create()
                .bulkAction(menuBar)
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void optionsMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Options");
        HorizontalLayout layout = BulkActionBuilder.create()
                .optionsMenuBar(menuBar)
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void add_components() {
        Span span = new Span("extra");
        HorizontalLayout layout = BulkActionBuilder.create()
                .add(span)
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void styleName_addsClassName() {
        HorizontalLayout layout = BulkActionBuilder.create()
                .styleName("bulk-bar")
                .build();
        assertTrue(layout.getClassNames().contains("bulk-bar"));
    }

    @Test
    void withPostProcessor_executes() {
        boolean[] called = {false};
        BulkActionBuilder.create()
                .withPostProcessor(cfg -> called[0] = true)
                .build();
        assertTrue(called[0]);
    }

    @Test
    void fluent_chain_fullExample() {
        MenuBar actions = new MenuBar();
        actions.addItem("Archive");
        HorizontalLayout layout = BulkActionBuilder.create()
                .selected(2)
                .bulkAction(actions)
                .styleName("my-bulk")
                .build();
        assertNotNull(layout);
        assertTrue(layout.getClassNames().contains("my-bulk"));
        assertTrue(layout.getComponentCount() >= 2);
    }
}
