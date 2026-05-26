package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SearchBarBuilder}.
 */
class TestSearchBarBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(SearchBarBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsHorizontalLayout() {
        HorizontalLayout layout = SearchBarBuilder.create().build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Search listener
    // =========================================================================

    @Test
    void search_setsListener() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .search(event -> {})
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // New Button
    // =========================================================================

    @Test
    void newButton_addsButton() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .newButton(btn -> btn.text("Add"))
                .build();
        assertNotNull(layout);
        // button should have been added as a child
        assertTrue(layout.getComponentCount() > 0);
    }

    // =========================================================================
    // Component Configurator
    // =========================================================================

    @Test
    void id_setsId() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .id("search-bar")
                .build();
        assertTrue(layout.getId().isPresent());
        assertEquals("search-bar", layout.getId().get());
    }

    @Test
    void styleName_addsClass() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .styleName("custom-bar")
                .build();
        assertTrue(layout.getClassNames().contains("custom-bar"));
    }

    // =========================================================================
    // Size
    // =========================================================================

    @Test
    void width_setsWidth() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .width("100%")
                .build();
        assertEquals("100%", layout.getWidth());
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        HorizontalLayout layout = SearchBarBuilder.create()
                .search(event -> {})
                .newButton(btn -> btn.text("Create"))
                .id("my-search")
                .styleName("top-bar")
                .width("100%")
                .build();
        assertNotNull(layout);
        assertEquals("my-search", layout.getId().orElse(null));
        assertTrue(layout.getClassNames().contains("top-bar"));
    }
}
