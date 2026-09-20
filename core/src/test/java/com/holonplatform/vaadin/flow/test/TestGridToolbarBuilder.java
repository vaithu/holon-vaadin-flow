package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.iyensoft.vaadin.flow.components.DynamicFilterPanel;
import com.iyensoft.vaadin.flow.components.GridToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.property.PathProperty;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestGridToolbarBuilder {

    public static class Product {
        private final String name;
        private final double price;
        private final String category;

        public Product(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }
    }

    @Test
    void defaultState_exposesSearchFilterAndPrimaryAction() {
        GridToolbar toolbar = Components.gridToolbar()
                .searchPlaceholder("Search applicants")
                .filterContent(new Span("Stage"))
                .filterActiveCount(2)
                .primaryAction(new Button("Add applicant"))
                .build();

        assertEquals("Search applicants", toolbar.getSearchField().getPlaceholder());
        assertEquals(1, toolbar.getFilterSlot().getComponentCount());
        assertTrue(toolbar.getElement().getClassList().contains("grid-toolbar"));
        assertEquals("toolbar", toolbar.getElement().getAttribute("role"));
    }

    @Test
    void configurator_coversSearchContentBadgeAndPrimaryActionReplacement() {
        Span firstFilter = new Span("First filter");
        Button firstAction = new Button("First action");
        Button replacementAction = new Button("Replacement action");

        GridToolbar toolbar = Components.gridToolbar()
                .searchPlaceholder("Find records")
                .filterContent(firstFilter)
                .filterActiveCount(2)
                .primaryAction(firstAction)
                .primaryAction(replacementAction)
                .build();

        assertEquals("Find records", toolbar.getSearchField().getPlaceholder());
        assertEquals(1, toolbar.getFilterSlot().getComponentCount());
        assertSame(firstFilter, toolbar.getFilterSlot().getChildren().findFirst().orElseThrow());
        assertSame(replacementAction, findButton(toolbar, "Replacement action"));
        assertTrue(findComponentByTestId(toolbar, "grid-toolbar-filter-badge").isVisible());
    }

    @Test
    void clearSelection_callbackIsInvokedWhenNoGridIsBound() {
        AtomicInteger clearCalls = new AtomicInteger();
        GridToolbar toolbar = Components.gridToolbar()
                .selectedCount(2)
                .clearSelection(clearCalls::incrementAndGet)
                .build();

        toolbar.getClearButton().click();

        assertEquals(1, clearCalls.get());
        assertEquals(2, toolbar.getSelectedCount(), "a callback-only clear does not invent selection state");
    }

    @Test
    void bulkActions_executeCallbacksAndExposeLabels() {
        AtomicInteger exportCalls = new AtomicInteger();
        AtomicInteger deleteCalls = new AtomicInteger();
        GridToolbar toolbar = Components.gridToolbar()
                .bulkAction("Export", exportCalls::incrementAndGet)
                .destructiveBulkAction("Delete", deleteCalls::incrementAndGet)
                .selectedCount(1)
                .build();

        findButton(toolbar, "Export").click();
        findButton(toolbar, "Delete").click();

        assertEquals(1, exportCalls.get());
        assertEquals(1, deleteCalls.get());
        assertEquals(java.util.List.of("Export", "Delete"), toolbar.getBulkActionLabels());
    }

    @Test
    void selectionState_switchesRowsAndTracksBulkActions() {
        GridToolbar toolbar = Components.gridToolbar()
                .bulkAction("Export", () -> { })
                .bulkAction("Assign", () -> { })
                .destructiveBulkAction("Delete", () -> { })
                .selectedCount(3)
                .build();

        assertEquals(3, toolbar.getSelectedCount());
        assertFalse(toolbar.getElement().getChildren().findFirst().orElseThrow().isVisible());
        assertTrue(toolbar.getElement().getClassList().contains("grid-toolbar--selected"));
        assertEquals(3, toolbar.getBulkActionLabels().size());
    }

    @Test
    void clearingSelection_returnsToDefaultState() {
        GridToolbar toolbar = Components.gridToolbar().selectedCount(2).build();
        toolbar.setSelectedCount(0);

        assertEquals(0, toolbar.getSelectedCount());
        assertTrue(toolbar.getElement().getChildren().findFirst().orElseThrow().isVisible());
        assertFalse(toolbar.getElement().getClassList().contains("grid-toolbar--selected"));
    }

    @Test
    void selectionGrid_tracksSelectionAndClearDeselectsItems() {
        Grid<String> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems("Alpha", "Beta");

        GridToolbar toolbar = Components.gridToolbar()
                .selectionGrid(grid)
                .build();

        grid.select("Alpha");
        assertEquals(1, toolbar.getSelectedCount());
        assertTrue(toolbar.getElement().getClassList().contains("grid-toolbar--selected"));

        toolbar.getClearButton().click();

        assertTrue(grid.getSelectedItems().isEmpty());
        assertEquals(0, toolbar.getSelectedCount());
        assertFalse(toolbar.getElement().getClassList().contains("grid-toolbar--selected"));
    }

    @Test
    void selectionListing_tracksSelectionAndClearDeselectsItems() {
        Product selected = new Product("Alpha", 10, "A");
        BeanListing<Product> listing = BeanListing.builder(Product.class)
            .items(selected, new Product("Beta", 20, "B"))
                .multiSelect()
                .build();
        GridToolbar toolbar = Components.gridToolbar()
                .selectionListing(listing)
                .build();

        listing.select(selected);
        assertEquals(1, toolbar.getSelectedCount());

        toolbar.getClearButton().click();

        assertTrue(listing.getSelectedItems().isEmpty());
        assertEquals(0, toolbar.getSelectedCount());
    }

    @Test
    void invalidConfiguration_isRejected() {
        GridToolbar toolbar = Components.gridToolbar().build();

        assertThrows(NullPointerException.class, () -> toolbar.setSearchPlaceholder((Localizable) null));
        assertThrows(NullPointerException.class, () -> toolbar.setClearSelectionAction(null));
        assertThrows(IllegalArgumentException.class, () -> toolbar.setFilterActiveCount(-1));
        assertThrows(IllegalArgumentException.class, () -> toolbar.setSelectedCount(-1));
        assertThrows(NullPointerException.class, () -> toolbar.addBulkAction((Localizable) null, () -> { }));
        assertThrows(NullPointerException.class, () -> toolbar.addBulkAction("Export", null));
    }

    @Test
    void filterPanel_applyAndResetUpdateFilterBadgeAndPredicate() {
        DynamicFilterPanel<Product> panel = DynamicFilterPanel.of(Product.class);
        GridToolbar toolbar = Components.gridToolbar()
                .filterPanel(panel)
                .build();
        QueryFilter filter = QueryFilter.eq(PathProperty.create("name", String.class), "Alpha");
        AtomicInteger filterChanges = new AtomicInteger();
        panel.addFilterChangeListener(event -> filterChanges.incrementAndGet());

        panel.applyFilterProgrammatically(filter);

        assertTrue(panel.isAnyActive());
        assertEquals(filter, panel.getQueryFilter().orElseThrow());
        assertEquals(1, filterChanges.get());
        Component filterBadge = findComponentByTestId(toolbar, "grid-toolbar-filter-badge");
        assertTrue(filterBadge.isVisible());

        panel.resetAll();

        assertFalse(panel.isAnyActive());
        assertTrue(panel.toPredicate().test(new Product("Beta", 10, "B")));
        assertEquals(2, filterChanges.get());
        assertFalse(filterBadge.isVisible());
    }

    private static Button findButton(Component component, String text) {
        return component.getChildren()
                .flatMap(child -> child instanceof Button button
                        ? java.util.stream.Stream.of(button)
                        : child.getChildren().flatMap(nested -> findButtons(nested).stream()))
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .orElseThrow();
    }

    private static java.util.List<Button> findButtons(Component component) {
        var buttons = new java.util.ArrayList<Button>();
        if (component instanceof Button button) {
            buttons.add(button);
        }
        component.getChildren().forEach(child -> buttons.addAll(findButtons(child)));
        return buttons;
    }

    private static Component findComponentByTestId(Component component, String testId) {
        if (testId.equals(component.getElement().getAttribute("data-testid"))) {
            return component;
        }
        for (Component child : (Iterable<Component>) component.getChildren()::iterator) {
            try {
                return findComponentByTestId(child, testId);
            } catch (java.util.NoSuchElementException ignored) {
                // Continue searching sibling branches.
            }
        }
        throw new java.util.NoSuchElementException("No component with test id " + testId);
    }
}