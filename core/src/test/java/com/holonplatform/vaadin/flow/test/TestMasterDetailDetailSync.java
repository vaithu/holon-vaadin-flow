package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.signals.local.ValueSignal;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Browserless tests for the reactive master-detail sync mechanism introduced via
 * {@link MasterDetailLayout#withDetailSync} and {@link MasterDetailLayout#notifyDataChanged()}.
 *
 * <p>Extends {@link AbstractSessionTest} which wires a mock {@link com.vaadin.flow.server.VaadinSession}
 * (with {@code hasLock() → true}) into the per-test {@code UI} before each test. This satisfies
 * Vaadin 25's {@code Signal.effect} (via {@code ElementEffect}) and {@code Grid.initConnector}
 * ({@code UIInternals.checkHasLock}), both of which require a non-null session on attach.
 *
 * <p>Test groups:
 * <ol>
 *   <li><b>selectionSignal contract</b> — reflects grid.select / clearSelection correctly.</li>
 *   <li><b>dataVersion contract</b> — notifyDataChanged() bumps version, calls listeners.</li>
 *   <li><b>Reactive handler dispatch</b> — withDetailSync handlers fire on selection and save.</li>
 *   <li><b>No-DOM-clearing guarantee</b> — detailContent called once per selection only.</li>
 *   <li><b>Builder null-safety</b> — withDetailSync rejects null owner/handler.</li>
 * </ol>
 */
class TestMasterDetailDetailSync extends AbstractSessionTest {

    // ── Fixture ───────────────────────────────────────────────────────────────

    private record Item(int id, String name) {}

    private static final Item ALICE = new Item(1, "Alice");
    private static final Item BOB   = new Item(2, "Bob");
    private static final Item CAROL = new Item(3, "Carol");

    private record Fixture(
        MasterDetailLayout<Item> layout,
        Grid<Item>               grid,
        Div                      formBody,
        Div                      footer
    ) {}

    private Fixture buildFixture() {
        Grid<Item> grid = new Grid<>(Item.class, false);
        grid.addColumn(Item::name).setHeader("Name");
        grid.setItems(ALICE, BOB, CAROL);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Div formBody = new Div();
        Div footer   = new Div();

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
            .masterGrid(grid)
            .detailContent(item -> new Component[]{ formBody, footer })
            .build();

        return new Fixture(layout, grid, formBody, footer);
    }

    // ── Test helpers ──────────────────────────────────────────────────────────

    /**
     * Attaches {@code layout} to the per-test UI so {@code onAttach} fires (activating
     * {@code Signal.effect} registrations), then forces DESKTOP mode so
     * {@code applySelection()} does not short-circuit on a null viewport.
     *
     * <p>Requires the UI session to be properly set (provided by {@link AbstractSessionTest}).
     */
    private void attachToUi(MasterDetailLayout<?> layout) {
        ui.add(layout);
        ComponentUtil.onComponentAttach(layout, true);
        layout.getResponsiveLayout().forceMode(ViewMode.DESKTOP);
    }

    @SuppressWarnings("unchecked")
    private <T> ValueSignal<T> signalField(MasterDetailLayout<?> layout, String fieldName)
            throws ReflectiveOperationException {
        Field f = layout.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (ValueSignal<T>) f.get(layout);
    }

    // =========================================================================
    // Group 1 — selectionSignal contract
    // =========================================================================

    @Test
    void selectionSignal_emptyBeforeAnySelection() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Optional<Item>> sig = signalField(f.layout(), "selectionSignal");

        assertEquals(Optional.empty(), sig.peek(),
            "Selection signal must be empty before any row is selected");
    }

    @Test
    void selectionSignal_reflectsGridSelect() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Optional<Item>> sig = signalField(f.layout(), "selectionSignal");

        f.grid().select(ALICE);

        assertEquals(Optional.of(ALICE), sig.peek(),
            "Selection signal must contain ALICE after grid.select(ALICE)");
    }

    @Test
    void selectionSignal_updatesOnSubsequentSelection() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Optional<Item>> sig = signalField(f.layout(), "selectionSignal");

        f.grid().select(ALICE);
        f.grid().select(BOB);

        assertEquals(Optional.of(BOB), sig.peek(),
            "Selection signal must reflect the latest selection (BOB)");
    }

    @Test
    void selectionSignal_clearedAfterClearSelection() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Optional<Item>> sig = signalField(f.layout(), "selectionSignal");

        f.grid().select(CAROL);
        f.layout().clearSelection();

        assertEquals(Optional.empty(), sig.peek(),
            "Selection signal must be empty after clearSelection()");
    }

    // =========================================================================
    // Group 2 — dataVersion contract
    // =========================================================================

    @Test
    void dataVersion_startsAtZero() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Integer> ver = signalField(f.layout(), "dataVersion");

        assertEquals(0, ver.peek(), "dataVersion must start at 0");
    }

    @Test
    void notifyDataChanged_incrementsDataVersion() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Integer> ver = signalField(f.layout(), "dataVersion");

        f.layout().notifyDataChanged();

        assertEquals(1, ver.peek(), "dataVersion must be 1 after one notifyDataChanged()");
    }

    @Test
    void notifyDataChanged_accumulatesAcrossCalls() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        ValueSignal<Integer> ver = signalField(f.layout(), "dataVersion");

        f.layout().notifyDataChanged();
        f.layout().notifyDataChanged();
        f.layout().notifyDataChanged();

        assertEquals(3, ver.peek(), "dataVersion must equal the number of notifyDataChanged() calls");
    }

    @Test
    void notifyDataChanged_callsRegisteredOnDataChangedListeners() {
        Fixture f = buildFixture();
        AtomicInteger listenerCalls = new AtomicInteger();

        f.layout().addDataChangedListener(listenerCalls::incrementAndGet);
        f.layout().notifyDataChanged();

        assertEquals(1, listenerCalls.get(),
            "onDataChanged listener must be called once per notifyDataChanged()");
    }

    @Test
    void notifyDataChanged_doesNotChangeSelectionSignal() throws ReflectiveOperationException {
        Fixture f = buildFixture();
        f.grid().select(ALICE);
        ValueSignal<Optional<Item>> sig = signalField(f.layout(), "selectionSignal");
        Optional<Item> before = sig.peek();

        f.layout().notifyDataChanged();

        assertEquals(before, sig.peek(),
            "notifyDataChanged() must not alter selectionSignal — only dataVersion changes");
    }

    // =========================================================================
    // Group 3 — withDetailSync handler dispatch (requires UI + session)
    // =========================================================================

    @Test
    void withDetailSyncOnLayout_handlerFiresOnFirstSelection() {
        Fixture f = buildFixture();
        List<Item> received = new ArrayList<>();

        f.layout().withDetailSync(f.layout(), received::add);
        attachToUi(f.layout());

        f.grid().select(ALICE);

        assertFalse(received.isEmpty(), "Sync handler must fire when a row is selected");
        assertEquals(ALICE, received.getLast(), "Sync handler must receive the selected item");
    }

    @Test
    void withDetailSyncOnLayout_handlerFiresOnSelectionChange() {
        Fixture f = buildFixture();
        List<Item> received = new ArrayList<>();

        f.layout().withDetailSync(f.layout(), received::add);
        attachToUi(f.layout());

        f.grid().select(ALICE);
        f.grid().select(BOB);

        assertEquals(BOB, received.getLast(),
            "Sync handler must receive the new item after each selection change");
    }

    @Test
    void withDetailSyncOnLayout_handlerDoesNotFireAfterClearSelection() {
        Fixture f = buildFixture();
        List<Item> received = new ArrayList<>();

        f.layout().withDetailSync(f.layout(), received::add);
        attachToUi(f.layout());

        f.grid().select(ALICE);
        int callsAfterSelect = received.size();

        f.layout().clearSelection();

        assertEquals(callsAfterSelect, received.size(),
            "Sync handler must NOT fire on clearSelection() — ifPresent() skips empty Optional");
    }

    @Test
    void withDetailSyncOnLayout_handlerRefiresOnNotifyDataChanged() {
        Fixture f = buildFixture();
        List<Item> received = new ArrayList<>();

        f.layout().withDetailSync(f.layout(), received::add);
        attachToUi(f.layout());

        f.grid().select(ALICE);
        int callsAfterSelect = received.size();

        f.layout().notifyDataChanged();   // simulate save

        assertEquals(callsAfterSelect + 1, received.size(),
            "Sync handler must re-fire after notifyDataChanged() so UI reflects saved data");
        assertEquals(ALICE, received.getLast(),
            "Re-fire must supply the current item (same reference, not a stale snapshot)");
    }

    @Test
    void withDetailSyncOnLayout_multipleHandlersAllFireOnSelection() {
        Fixture f = buildFixture();
        List<String> log = new ArrayList<>();

        f.layout().withDetailSync(f.layout(), item -> log.add("A:" + item.name()));
        f.layout().withDetailSync(f.layout(), item -> log.add("B:" + item.name()));
        attachToUi(f.layout());

        f.grid().select(BOB);

        assertTrue(log.contains("A:Bob"), "First withDetailSync handler must fire");
        assertTrue(log.contains("B:Bob"), "Second withDetailSync handler must fire");
    }

    // =========================================================================
    // Group 4 — No DOM clearing: same component instances on every selection
    // =========================================================================

    @Test
    void detailContent_builtOnceAndCached_noDomClearing() {
        Div formBody = new Div();
        Div footer   = new Div();
        AtomicInteger providerCallCount = new AtomicInteger();
        Component[] fixedSlot = { formBody, footer };

        Grid<Item> grid = new Grid<>(Item.class, false);
        grid.addColumn(Item::name);
        grid.setItems(ALICE, BOB, CAROL);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
            .masterGrid(grid)
            .detailContent(item -> {
                providerCallCount.incrementAndGet();
                return fixedSlot;    // same array — same component instances every call
            })
            .build();

        attachToUi(layout);

        grid.select(ALICE);
        assertEquals(1, providerCallCount.get(),
            "detailContent provider must be called once on first selection (ensureComponents caches)");

        grid.select(BOB);
        grid.select(CAROL);
        assertEquals(1, providerCallCount.get(),
            "detailContent provider must NOT be called again — components are cached and reused");
    }

    @Test
    void detailContent_builtOnceOnly_notBySubsequentSelectionsOrNotifyDataChanged() {
        AtomicInteger callCount = new AtomicInteger();
        Div content = new Div();

        Grid<Item> grid = new Grid<>(Item.class, false);
        grid.addColumn(Item::name);
        grid.setItems(ALICE, BOB);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        MasterDetailLayout<Item> layout = MasterDetailLayout.<Item>builder()
            .masterGrid(grid)
            .detailContent(item -> {
                callCount.incrementAndGet();
                return new Component[]{ content };
            })
            .build();

        attachToUi(layout);

        grid.select(ALICE);
        assertEquals(1, callCount.get(),
            "Provider called once for first selection (ensureComponents builds and caches)");

        grid.select(BOB);
        assertEquals(1, callCount.get(),
            "Provider must NOT be called again — cached components are reused across selections");

        // notifyDataChanged bumps dataVersion — withDetailSync effects re-run,
        // but must NOT call the content provider (no DOM swap on save).
        layout.notifyDataChanged();
        assertEquals(1, callCount.get(),
            "notifyDataChanged() must NOT call detailContent — sync is via withDetailSync effects only");
    }

    // =========================================================================
    // Group 5 — Builder null-safety
    // =========================================================================

    @Test
    void builderWithDetailSync_throwsOnNullOwner() {
        Grid<Item> grid = new Grid<>(Item.class, false);
        grid.addColumn(Item::name);

        assertThrows(NullPointerException.class, () ->
            MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new Component[0])
                .withDetailSync(null, item -> {}),
            "withDetailSync must reject null owner");
    }

    @Test
    void builderWithDetailSync_throwsOnNullHandler() {
        Grid<Item> grid = new Grid<>(Item.class, false);
        grid.addColumn(Item::name);
        Div owner = new Div();

        assertThrows(NullPointerException.class, () ->
            MasterDetailLayout.<Item>builder()
                .masterGrid(grid)
                .detailContent(item -> new Component[0])
                .withDetailSync(owner, null),
            "withDetailSync must reject null handler");
    }
}

