package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.GridHeaderBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GridHeaderBuilder} and the {@link GridHeader} component.
 */
class TestGridHeaderBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_string_returnsNonNull() {
        assertNotNull(GridHeaderBuilder.create("Products"));
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_returnsGridHeader() {
        GridHeader header = GridHeaderBuilder.create("Products").build();
        assertNotNull(header);
        assertTrue(header.getClassNames().contains("grid-header"));
    }

    @Test
    void build_titleIsPresent() {
        GridHeader header = GridHeaderBuilder.create("Orders").build();
        assertTrue(header.getTitle().isPresent());
        assertEquals("Orders", header.getTitle().get());
    }

    @Test
    void build_keepsActionsColumnVisible() {
        Button action = new Button("Add");

        GridHeader header = GridHeaderBuilder.create("Items")
                .defaultActions(action)
                .build();

        assertTrue(header.getColumnLayout().isVisible(), "GridHeader should keep the actions column visible");
        assertTrue(action.getParent().isPresent(), "default actions must remain attached to the header");
    }

    @Test
    void selectionCount_hidesHeadingWhileSelected() {
        GridHeader header = GridHeaderBuilder.create("Items").build();

        header.updateActionsVisibility(2);

        // The heading element (H2) lives in column → columnLine.
        // We look for any component whose element text is "Items" (works for H2 and Span alike).
        Component heading = header.getColumnLayout().getChildren()
                .flatMap(Component::getChildren)
                .filter(component -> "Items".equals(component.getElement().getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected the header title component to exist"));

        assertFalse(heading.isVisible(), "the heading must be hidden while selection count is shown");
        assertTrue(header.getTopRowLayout().getChildren()
                .flatMap(Component::getChildren)
                .anyMatch(component -> component instanceof Span span && "2 selected".equals(span.getText()) && span.isVisible()),
                "the selection count must be visible in the prefix area");
    }

    // =========================================================================
    // Default actions
    // =========================================================================

    @Nested
    class DefaultActionTests {

        @Test
        void defaultActions_setsComponents() {
            GridHeader header = GridHeaderBuilder.create("Items")
                    .defaultActions(new Button("Add"), new Button("Export"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void defaultActions_replacedByContextOnSelection() {
            GridHeader header = GridHeaderBuilder.create("Items")
                    .defaultActions(new Button("Add"))
                    .contextActions(new Button("Delete"))
                    .build();
            assertNotNull(header);
        }
    }

    // =========================================================================
    // Context actions
    // =========================================================================

    @Test
    void contextActions_setsComponents() {
        GridHeader header = GridHeaderBuilder.create("Items")
                .contextActions(new Button("Delete"), new Button("Archive"))
                .build();
        assertNotNull(header);
    }

    // =========================================================================
    // Style name
    // =========================================================================

    @Test
    void styleName_addsClassName() {
        GridHeader header = GridHeaderBuilder.create("Items")
                .styleName("custom-header")
                .build();
        assertTrue(header.getClassNames().contains("custom-header"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        GridHeader header = GridHeaderBuilder.create("Products")
                .defaultActions(new Button("New"), new Button("Import"))
                .contextActions(new Button("Delete"), new Button("Export"))
                .styleName("products-header")
                .build();
        assertNotNull(header);
        assertTrue(header.getTitle().isPresent());
        assertEquals("Products", header.getTitle().get());
        assertTrue(header.getClassNames().contains("products-header"));
    }
}
