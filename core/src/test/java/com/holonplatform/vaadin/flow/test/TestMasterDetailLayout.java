package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MasterDetailLayout} and {@link MasterDetailBuilder}.
 *
 * <p>Tests run without a live Vaadin UI — they verify the server-side state machine only.
 * Browser-side behaviour (Signal effects, History API, Sheet open/close transitions)
 * requires integration tests with a running Vaadin UI.</p>
 */
class TestMasterDetailLayout {

    // ─────────────────────────────────────────────────────────────────────────
    // Test entity
    // ─────────────────────────────────────────────────────────────────────────

    static final class Item {
        private final int    id;
        private       String name;

        Item(int id, String name) { this.id = id; this.name = name; }

        int    getId()   { return id; }
        String getName() { return name; }
    }

    private static final List<Item> ITEMS = List.of(
            new Item(1, "Alpha"),
            new Item(2, "Beta"),
            new Item(3, "Gamma"));

    // ─────────────────────────────────────────────────────────────────────────
    // Builder — validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void build_withoutGrid_throwsNPE() {
        MasterDetailBuilder<Item> builder = MasterDetailBuilder.create();
        builder.detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span(item.getName()) });
        assertThrows(NullPointerException.class, builder::build,
                "masterGrid is mandatory and must throw");
    }

    @Test
    void build_withoutDetailContent_throwsNPE() {
        Grid<Item> grid = new Grid<>();
        MasterDetailBuilder<Item> builder = MasterDetailBuilder.<Item>create()
                .masterGrid(grid);
        assertThrows(NullPointerException.class, builder::build,
                "detailContent is mandatory and must throw");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Builder — minimal build
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void build_minimal_producesNonNullLayout() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span(item.getName()) })
                .build();

        assertNotNull(layout);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Builder — full build
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void build_withAllOptions_producesNonNullLayout() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterHeader(new Span("Items"))
                .masterSearch(new com.vaadin.flow.component.textfield.TextField())
                .masterGrid(grid)
                .detailHeader(new Span("Details"))
                .detailMenu(new Span("Menu"))
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Div() })
                .itemId(i -> String.valueOf(i.getId()),
                        id -> ITEMS.stream().filter(x -> x.getId() == Integer.parseInt(id)).findFirst())
                .mobileSheetTitle("Item details")
                .onDataChanged(() -> { /* grid refresh placeholder */ })
                .build();

        assertNotNull(layout);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Selection signal — initial state
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void selectionSignal_initiallyEmpty() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span(item.getName()) })
                .build();

        assertNotNull(layout.selectionSignal());
        assertTrue(layout.selectionSignal().peek().isEmpty(),
                "Selection must start empty");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // clearSelection — deselects grid
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void clearSelection_deselectsGrid() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .build();

        grid.select(ITEMS.getFirst());
        assertFalse(grid.getSelectedItems().isEmpty());

        layout.clearSelection();
        assertTrue(grid.getSelectedItems().isEmpty(), "Grid must be deselected after clearSelection");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // select — selects item in grid
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void select_setsGridSelection() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .build();

        layout.select(ITEMS.get(1));
        assertTrue(grid.getSelectedItems().contains(ITEMS.get(1)),
                "select() must update the grid's selection");
    }

    @Test
    void select_null_doesNothing() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .build();

        assertDoesNotThrow(() -> layout.select(null));
        assertTrue(grid.getSelectedItems().isEmpty());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // notifyDataChanged
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void notifyDataChanged_callsAllListeners() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        AtomicInteger count = new AtomicInteger(0);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .onDataChanged(count::incrementAndGet)
                .onDataChanged(count::incrementAndGet)
                .build();

        layout.notifyDataChanged();
        assertEquals(2, count.get(), "Both onDataChanged listeners must be called");
    }

    @Test
    void notifyDataChanged_listenerThrows_doesNotPropagateException() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        AtomicBoolean secondCalled = new AtomicBoolean(false);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .onDataChanged(() -> { throw new RuntimeException("simulated error"); })
                .onDataChanged(() -> secondCalled.set(true))
                .build();

        assertDoesNotThrow(layout::notifyDataChanged,
                "Exception from one listener must not propagate");
        assertTrue(secondCalled.get(),
                "Subsequent listener must still be called after a throwing listener");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // addDataChangedListener — dynamic registration
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void addDataChangedListener_andRemove() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        AtomicInteger count = new AtomicInteger(0);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .build();

        var reg = layout.addDataChangedListener(count::incrementAndGet);
        layout.notifyDataChanged();
        assertEquals(1, count.get());

        reg.remove();
        layout.notifyDataChanged();
        assertEquals(1, count.get(), "Listener must not be called after removal");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // restoreSelection — positive / negative
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void restoreSelection_withValidId_selectsItem() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .itemId(i -> String.valueOf(i.getId()),
                        id -> ITEMS.stream().filter(x -> x.getId() == Integer.parseInt(id)).findFirst())
                .build();

        layout.restoreSelection("2");
        assertTrue(grid.getSelectedItems().stream().anyMatch(i -> i.getId() == 2),
                "restoreSelection must select the item with matching ID");
    }

    @Test
    void restoreSelection_withUnknownId_noSelection() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .itemId(i -> String.valueOf(i.getId()),
                        id -> ITEMS.stream().filter(x -> x.getId() == Integer.parseInt(id)).findFirst())
                .build();

        layout.restoreSelection("999");
        assertTrue(grid.getSelectedItems().isEmpty(),
                "restoreSelection with unknown ID must leave grid unselected");
    }

    @Test
    void restoreSelection_withoutItemLoader_doesNothing() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                // No itemId configured
                .build();

        assertDoesNotThrow(() -> layout.restoreSelection("1"),
                "restoreSelection without itemLoader must be a no-op");
        assertTrue(grid.getSelectedItems().isEmpty());
    }

    @Test
    void restoreSelection_withBlankId_doesNothing() {
        Grid<Item> grid = new Grid<>();
        grid.setItems(ITEMS);

        AtomicBoolean loaderCalled = new AtomicBoolean(false);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new com.vaadin.flow.component.Component[]{ new Span() })
                .itemId(i -> String.valueOf(i.getId()), id -> {
                    loaderCalled.set(true);
                    return Optional.empty();
                })
                .build();

        layout.restoreSelection("   ");
        assertFalse(loaderCalled.get(), "itemLoader must not be called for blank id");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Factory method
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void staticBuilderFactory_returnsSameAsInterfaceCreate() {
        assertNotNull(MasterDetailLayout.builder());
        assertNotNull(MasterDetailBuilder.create());
    }
}




