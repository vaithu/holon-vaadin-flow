package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

/**
 * Regression tests for the selection lifecycle {@link MasterDetailLayout} owns:
 * the {@code selectItem}/{@code selectFirst}/{@code restoreFromUrl} funnel, the
 * stale deep-link fallback, auto-select de-duplication and {@code clearSelection}.
 *
 * <p>These pin behaviour that was previously scattered across the demo views and is
 * easy to regress: every one of these assertions corresponds to a redundant backend
 * query or a wrong selection that was observed in a running application.</p>
 */
class TestMasterDetailSelection {

    record Product(Integer id, String name) {}

    private static final Product FIRST = new Product(1, "Alpha");
    private static final Product SECOND = new Product(2, "Beta");

    private MasterDetailLayout<Product> layout;
    private MasterDetailBuilder<Product> sut;

    /** Items dispatched to the detail-sync handlers, in order. */
    private List<Product> synced;
    /** Number of times the initial-item supplier hit the "backend". */
    private AtomicInteger initialItemCalls;
    /** Number of times the URL item loader hit the "backend". */
    private AtomicInteger loadByIdCalls;

    @BeforeEach
    void setUp() {
        synced = new ArrayList<>();
        initialItemCalls = new AtomicInteger();
        loadByIdCalls = new AtomicInteger();

        sut = Components.masterDetail(Product.class);
        layout = sut.build();

        sut.withInitialItem(() -> {
            initialItemCalls.incrementAndGet();
            return Optional.of(FIRST);
        });
        sut.withUrlSync(
                p -> String.valueOf(p.id()),
                id -> {
                    loadByIdCalls.incrementAndGet();
                    return Optional.ofNullable(Map.of("1", FIRST, "2", SECOND).get(id));
                });
        layout.setViewMode(ViewMode.DESKTOP);
        layout.addSyncDispatcher(synced::add);
    }

    private static Location location(String id) {
        return id == null
                ? new Location("products")
                : new Location("products", QueryParameters.of("id", id));
    }

    // ── selectItem: the single selection funnel ────────────────────────────────

    @Test
    void selectItem_dispatchesSyncAndRecordsCurrentItem() {
        layout.selectItem(SECOND);

        assertEquals(List.of(SECOND), synced);
        assertEquals(Optional.of(SECOND), layout.getCurrentItem());
    }

    @Test
    void selectItem_appliesAccentClassFromProvider() {
        sut.withAccentColorProvider(p -> "mdl-accent--" + p.name().toLowerCase());

        layout.selectItem(SECOND);

        assertTrue(layout.getClassNames().contains("mdl-accent--beta"));
    }

    @Test
    void selectItem_replacesPreviousAccentClass() {
        sut.withAccentColorProvider(p -> "mdl-accent--" + p.name().toLowerCase());

        layout.selectItem(FIRST);
        layout.selectItem(SECOND);

        assertFalse(layout.getClassNames().contains("mdl-accent--alpha"),
                "The accent class of the previous selection must be removed");
        assertTrue(layout.getClassNames().contains("mdl-accent--beta"));
    }

    @Test
    void selectItem_doesNotQueryTheBackend() {
        layout.selectItem(SECOND);

        assertEquals(0, initialItemCalls.get());
        assertEquals(0, loadByIdCalls.get(),
                "A row click already has the item; it must never re-load it by id");
    }

    @Test
    void selectItem_updatesSelectionSignal() {
        layout.selectionSignal();

        layout.selectItem(SECOND);

        assertEquals(SECOND, layout.selectionSignal().peek());
    }

    // ── selectFirst ────────────────────────────────────────────────────────────

    @Test
    void selectFirst_usesInitialItemSupplier_withExactlyOneQuery() {
        layout.selectFirst(ViewMode.DESKTOP);

        assertEquals(1, initialItemCalls.get());
        assertEquals(List.of(FIRST), synced);
    }

    @Test
    void selectFirst_isNoOpOnMobile() {
        layout.selectFirst(ViewMode.MOBILE);

        assertEquals(0, initialItemCalls.get());
        assertTrue(synced.isEmpty());
    }

    @Test
    void selectFirst_isNoOpWithoutMode() {
        layout.selectFirst(null);

        assertEquals(0, initialItemCalls.get());
        assertTrue(synced.isEmpty());
    }

    // ── restoreFromUrl ─────────────────────────────────────────────────────────

    @Test
    void restoreFromUrl_selectsTheRequestedItem_withoutSelectingFirst() {
        layout.restoreFromUrl("2");

        assertEquals(List.of(SECOND), synced);
        assertEquals(1, loadByIdCalls.get());
        assertEquals(0, initialItemCalls.get(),
                "A deep link must not pay for a selectFirst query on the way to its target");
    }

    @Test
    void restoreFromUrl_staleId_fallsBackToFirstItem() {
        layout.restoreFromUrl("99999");

        assertEquals(List.of(FIRST), synced);
        assertEquals(1, initialItemCalls.get());
        assertEquals(Optional.of(FIRST), layout.getCurrentItem());
    }

    @Test
    void restoreFromUrl_blankId_isNoOp() {
        layout.restoreFromUrl("   ");

        assertTrue(synced.isEmpty());
        assertEquals(0, initialItemCalls.get());
    }

    @Test
    void restoreFromUrl_nullId_isNoOp() {
        layout.restoreFromUrl(null);

        assertTrue(synced.isEmpty());
        assertEquals(0, initialItemCalls.get());
    }

    @Test
    void restoreFromUrl_withoutUrlSync_isNoOp() {
        MasterDetailLayout<Product> bare = Components.masterDetail(Product.class).build();
        bare.setViewMode(ViewMode.DESKTOP);
        bare.addSyncDispatcher(synced::add);

        bare.restoreFromUrl("2");

        assertTrue(synced.isEmpty());
    }

    // ── autoSelect de-duplication ──────────────────────────────────────────────
    // enableAutoSelect() wires two entry points (a beforeClientResponse callback on
    // attach and a UI-level AfterNavigationListener); both fire on first load, so the
    // layout must resolve the backend exactly once per distinct target.

    @Test
    void autoSelect_plainLocation_selectsFirstOnce_despiteRepeatedEntry() {
        layout.autoSelect(location(null));
        layout.autoSelect(location(null));

        assertEquals(1, initialItemCalls.get());
        assertEquals(List.of(FIRST), synced);
    }

    @Test
    void autoSelect_deepLink_restoresOnce_despiteRepeatedEntry() {
        layout.autoSelect(location("2"));
        layout.autoSelect(location("2"));

        assertEquals(1, loadByIdCalls.get());
        assertEquals(0, initialItemCalls.get());
        assertEquals(List.of(SECOND), synced);
    }

    @Test
    void autoSelect_staleDeepLink_fallsBackOnce_despiteRepeatedEntry() {
        // Regression: the stale-id fallback used to reset the recorded target, so the
        // second entry point saw a mismatch and re-ran the whole fallback.
        layout.autoSelect(location("99999"));
        layout.autoSelect(location("99999"));

        assertEquals(1, loadByIdCalls.get());
        assertEquals(1, initialItemCalls.get());
        assertEquals(List.of(FIRST), synced);
    }

    @Test
    void autoSelect_reNavigationToDifferentId_appliesAgain() {
        layout.autoSelect(location("1"));
        layout.autoSelect(location("2"));

        assertEquals(List.of(FIRST, SECOND), synced);
    }

    @Test
    void autoSelect_blankIdParameter_isTreatedAsNoId() {
        layout.autoSelect(location(""));

        assertEquals(1, initialItemCalls.get());
        assertEquals(0, loadByIdCalls.get());
    }

    @Test
    void autoSelect_nullLocation_selectsFirst() {
        layout.autoSelect(null);

        assertEquals(1, initialItemCalls.get());
    }

    @Test
    void autoSelect_isNoOpOnMobile() {
        layout.setViewMode(ViewMode.MOBILE);

        layout.autoSelect(location("2"));

        assertEquals(0, loadByIdCalls.get());
        assertEquals(0, initialItemCalls.get());
        assertTrue(synced.isEmpty());
    }

    @Test
    void autoSelect_honoursCustomUrlParameterName() {
        MasterDetailBuilder<Product> custom = Components.masterDetail(Product.class);
        MasterDetailLayout<Product> customLayout = custom.build();
        custom.withUrlSync(p -> String.valueOf(p.id()),
                id -> {
                    loadByIdCalls.incrementAndGet();
                    return Optional.ofNullable(Map.of("2", SECOND).get(id));
                },
                "productId");
        custom.withInitialItem(() -> {
            initialItemCalls.incrementAndGet();
            return Optional.of(FIRST);
        });
        customLayout.setViewMode(ViewMode.DESKTOP);
        customLayout.addSyncDispatcher(synced::add);

        customLayout.autoSelect(new Location("products", QueryParameters.of("productId", "2")));

        assertEquals(List.of(SECOND), synced);
        assertEquals(0, initialItemCalls.get());
    }

    @Test
    void autoSelect_ignoresIdUnderTheDefaultNameWhenCustomNameIsConfigured() {
        MasterDetailBuilder<Product> custom = Components.masterDetail(Product.class);
        MasterDetailLayout<Product> customLayout = custom.build();
        custom.withUrlSync(p -> String.valueOf(p.id()),
                id -> Optional.ofNullable(Map.of("2", SECOND).get(id)),
                "productId");
        custom.withInitialItem(() -> {
            initialItemCalls.incrementAndGet();
            return Optional.of(FIRST);
        });
        customLayout.setViewMode(ViewMode.DESKTOP);
        customLayout.addSyncDispatcher(synced::add);

        customLayout.autoSelect(location("2"));

        assertEquals(List.of(FIRST), synced);
        assertEquals(1, initialItemCalls.get());
    }

    // ── clearSelection ─────────────────────────────────────────────────────────

    @Test
    void clearSelection_clearsCurrentItemAccentAndSignal() {
        sut.withAccentColorProvider(p -> "mdl-accent--" + p.name().toLowerCase());
        layout.selectionSignal();
        layout.selectItem(SECOND);

        layout.clearSelection();

        assertTrue(layout.getCurrentItem().isEmpty());
        assertFalse(layout.getClassNames().contains("mdl-accent--beta"));
        assertNull(layout.selectionSignal().peek());
    }

    @Test
    void clearSelection_doesNotInvokeImperativeSyncDispatchers() {
        layout.selectItem(SECOND);
        synced.clear();

        layout.clearSelection();

        assertTrue(synced.isEmpty(),
                "Sync dispatchers are contracted to receive a selected item; react to clearing via selectionSignal()");
    }

    @Test
    void clearSelection_allowsAutoSelectToReapplyTheSameTarget() {
        layout.autoSelect(location("2"));
        layout.clearSelection();

        layout.autoSelect(location("2"));

        assertEquals(2, loadByIdCalls.get());
        assertEquals(List.of(SECOND, SECOND), synced);
    }

    // ── signal ─────────────────────────────────────────────────────────────────

    @Test
    void selectionSignal_isLazyButStable() {
        assertSame(layout.selectionSignal(), layout.selectionSignal());
    }

    @Test
    void selectionSignal_createdAfterSelection_seesTheCurrentSelection() {
        // A subscriber created late — e.g. detail content realized on first display, or any
        // component built after withAutoSelect() already selected during attach — must not
        // observe a spurious "nothing selected".
        layout.selectItem(SECOND);

        assertEquals(SECOND, layout.selectionSignal().peek());
    }

    // ── scrolling safety ───────────────────────────────────────────────────────

    @Test
    void selection_withoutMasterBundle_doesNotThrow() {
        // No master listing is configured in this test fixture, so every scroll request
        // must degrade silently rather than break the selection.
        layout.selectFirst(ViewMode.DESKTOP);
        layout.restoreFromUrl("2");
        layout.selectItem(FIRST);

        assertEquals(List.of(FIRST, SECOND, FIRST), synced);
    }

    @Test
    void setItemIndexProvider_isAcceptedBeforeAnyListingExists() {
        sut.withItemIndexProvider((item, query) -> 0);

        layout.restoreFromUrl("2");

        assertEquals(List.of(SECOND), synced);
    }
}
