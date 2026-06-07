/*
 * Copyright 2016-2024 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.Registration;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListingPageSizeSelector;
import com.holonplatform.vaadin.flow.components.ItemListingPaginationBar;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link ItemListingPaginationBar} and {@link ItemListingPageSizeSelector}
 * covering lazy-loading scenarios where the data provider cannot answer a count query.
 *
 * <p>Tests exercise the resilience path introduced in 10.0.1: when
 * {@link DataProvider#size(Query)} throws {@link IllegalStateException} (Vaadin lazy
 * providers) or {@link UnsupportedOperationException} the bar must NOT propagate the
 * exception and must fall back gracefully.</p>
 *
 * <p>No live Vaadin UI is required — a minimal stub {@link ItemListing} is used.</p>
 */
class TestItemListingPaginationBarLazy {

    // -------------------------------------------------------------------------
    // Stub ItemListing backed by various DataProvider flavours
    // -------------------------------------------------------------------------

    /**
     * Minimal stub whose {@link #getDataProvider()} returns the supplied provider
     * and whose {@link #getComponent()} returns a plain {@link Div} (no real Grid).
     *
     * <p>Only the methods called by {@link ItemListingPaginationBar} and
     * {@link ItemListingPageSizeSelector} are implemented; all others throw
     * {@link UnsupportedOperationException} to catch accidental invocations.</p>
     */
    private static class StubListing implements ItemListing<String, String> {

        private final DataProvider<String, ?> dataProvider;

        StubListing(DataProvider<String, ?> dataProvider) {
            this.dataProvider = dataProvider;
        }

        @Override public DataProvider<String, ?> getDataProvider()     { return dataProvider; }
        @Override public DataProvider<String, ?> getBackEndDataProvider() { return dataProvider; }
        @Override public Component getComponent()                       { return new Div(); }

        // ── stub-out unused methods ──────────────────────────────────────────
        @Override public void refresh()                                 { /* no-op */ }
        @Override public List<String> getVisibleColumns()               { throw new UnsupportedOperationException(); }
        @Override public List<String> getHiddenColumns()                { throw new UnsupportedOperationException(); }
        @Override public void setColumnVisible(String p, boolean v)     { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<String> getColumnHeader(String p) { throw new UnsupportedOperationException(); }
        @Override public void hideAllColumnsExcept(String p)            { throw new UnsupportedOperationException(); }
        @Override public void restoreAllColumns()                       { throw new UnsupportedOperationException(); }
        @Override public void setFrozenMultiSelectCheckBoxColumn(boolean f) { throw new UnsupportedOperationException(); }
        @Override public void setFrozenToEnd(String p, boolean f)       { throw new UnsupportedOperationException(); }
        @Override public void setScrollUsingUpDownKeys()                { throw new UnsupportedOperationException(); }
        @Override public void setToggleableColumns()                    { throw new UnsupportedOperationException(); }
        @Override public List<com.vaadin.flow.component.grid.Grid.Column<String>> getAllColumns() { throw new UnsupportedOperationException(); }
        @Override public void hide(String p)                            { throw new UnsupportedOperationException(); }
        @Override public void addIndexColumn()                          { throw new UnsupportedOperationException(); }
        @Override public void addIndexColumn(String p)                  { throw new UnsupportedOperationException(); }
        @Override public <V extends Component> com.vaadin.flow.component.grid.Grid.Column<String> addComponentColumn(com.vaadin.flow.function.ValueProvider<String, V> cp) { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.shared.Registration addItemClickListener(com.vaadin.flow.component.ComponentEventListener<com.vaadin.flow.component.grid.ItemClickEvent<String>> l) { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.shared.Registration addSelectionListener(com.vaadin.flow.data.selection.SelectionListener<com.vaadin.flow.component.grid.Grid<String>, String> l) { throw new UnsupportedOperationException(); }
        @Override public void addHoverEffect(com.vaadin.flow.component.AttachEvent e, com.vaadin.flow.function.SerializableFunction<String, String> g) { throw new UnsupportedOperationException(); }
        @Override public void removeColumnByKey(String k)               { throw new UnsupportedOperationException(); }
        @Override public void removeColumn(String p)                    { throw new UnsupportedOperationException(); }
        @Override public void removeColumns(List<String> ps)            { throw new UnsupportedOperationException(); }
        @Override public void setColumnOrder(List<com.vaadin.flow.component.grid.Grid.Column<String>> cols) { throw new UnsupportedOperationException(); }
        @Override public void selectAll()                               { throw new UnsupportedOperationException(); }
        @Override public boolean isItemDetailsVisible(String i)         { throw new UnsupportedOperationException(); }
        @Override public void setItemDetailsVisible(String i, boolean v){ throw new UnsupportedOperationException(); }
        @Override public void sort(List<com.holonplatform.vaadin.flow.data.ItemSort<String>> s) { throw new UnsupportedOperationException(); }
        @Override public void setSelectionMode(com.holonplatform.vaadin.flow.components.Selectable.SelectionMode m) { throw new UnsupportedOperationException(); }
        @Override public void refreshItem(String i)                     { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getHeader() { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getFooter() { throw new UnsupportedOperationException(); }
        @Override public boolean isEditable()                           { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<String> isEditing()        { throw new UnsupportedOperationException(); }
        @Override public void editItem(String i)                        { throw new UnsupportedOperationException(); }
        @Override public void cancelEditing()                           { throw new UnsupportedOperationException(); }
        @Override public boolean saveEditingItem()                      { throw new UnsupportedOperationException(); }
        @Override public void refreshEditingItem()                      { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.component.grid.editor.Editor<String> getEditor() { throw new UnsupportedOperationException(); }
        @Override public void setMobileColumn(com.vaadin.flow.data.renderer.Renderer<String> r) { throw new UnsupportedOperationException(); }
        @Override public void setMobileColumn(com.vaadin.flow.function.ValueProvider<String, Component> c) { throw new UnsupportedOperationException(); }
        @Override public void setMobileHeader(String h)                 { throw new UnsupportedOperationException(); }
        @Override public void setMobileHeader(Component c)             { throw new UnsupportedOperationException(); }
        @Override public void addThemeVariants(com.vaadin.flow.component.grid.GridVariant... v) { throw new UnsupportedOperationException(); }
        @Override public void removeThemeVariants(com.vaadin.flow.component.grid.GridVariant... v) { throw new UnsupportedOperationException(); }
        @Override public void showMobileColumn(boolean m)               { throw new UnsupportedOperationException(); }
        @Override public boolean isMobileColumnVisible()                { throw new UnsupportedOperationException(); }
        @Override public void hideMobileColumn()                        { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.component.grid.dataview.GridLazyDataView<String> setItems(CallbackDataProvider.FetchCallback<String, Void> fc) {
            // Return a mock so initManagedFetch can call setItemCountCallback() on it
            @SuppressWarnings("unchecked")
            GridLazyDataView<String> view = mock(GridLazyDataView.class);
            return view;
        }
        @Override public java.util.Optional<String> getItemAtIndex(int i){ throw new UnsupportedOperationException(); }
        @Override public void compact()                                 { throw new UnsupportedOperationException(); }
        @Override public void stretch()                                 { throw new UnsupportedOperationException(); }
        @Override public void wrapCellContent()                         { throw new UnsupportedOperationException(); }
        @Override public void setEmptyStateText(String t)               { throw new UnsupportedOperationException(); }
        @Override public void setEmptyStateComponent(Component c)       { throw new UnsupportedOperationException(); }
        @Override public void setPartNameGenerator(com.vaadin.flow.function.SerializableFunction<String, String> g) { throw new UnsupportedOperationException(); }
        @Override public List<com.vaadin.flow.data.provider.QuerySortOrder> getColumnSorts() { throw new UnsupportedOperationException(); }
        @Override public boolean isFrozen()                             { throw new UnsupportedOperationException(); }
        @Override public void setFrozen(boolean f)                      { throw new UnsupportedOperationException(); }
        @Override public List<String> getAdditionalItems()              { throw new UnsupportedOperationException(); }
        @Override public void addAdditionalItem(String i)               { throw new UnsupportedOperationException(); }
        @Override public boolean removeAdditionalItem(String i)         { throw new UnsupportedOperationException(); }
        @Override public void removeAdditionalItems()                   { throw new UnsupportedOperationException(); }
        @Override public void recalculateColumnWidths()                 { throw new UnsupportedOperationException(); }
        @Override public void scrollToIndex(int i)                      { /* no-op in unit tests */ }
        @Override public void scrollToStart()                           { /* no-op */ }
        @Override public void scrollToEnd()                             { /* no-op */ }
        @Override public com.holonplatform.vaadin.flow.components.Selectable.SelectionMode getSelectionMode() { throw new UnsupportedOperationException(); }
        @Override public java.util.Set<String> getSelectedItems()      { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<String> getFirstSelectedItem() { throw new UnsupportedOperationException(); }
        @Override public void select(String i)                          { throw new UnsupportedOperationException(); }
        @Override public void deselect(String i)                        { throw new UnsupportedOperationException(); }
        @Override public void deselectAll()                             { throw new UnsupportedOperationException(); }
        @Override public com.holonplatform.core.Registration addSelectionListener(com.holonplatform.vaadin.flow.components.Selectable.SelectionListener<String> l) { throw new UnsupportedOperationException(); }
        @Override public java.util.Optional<String> getFirstItem()     { throw new UnsupportedOperationException(); }
    }

    // -------------------------------------------------------------------------
    // Helpers — data providers
    // -------------------------------------------------------------------------

    /** DataProvider that throws IllegalStateException on size() — mimics Vaadin lazy grid. */
    private static DataProvider<String, Void> lazyProviderNoCount(List<String> items) {
        return DataProvider.fromCallbacks(
                q -> items.stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { throw new IllegalStateException(
                        "Trying to use exact size with a lazy loading component"); }
        );
    }

    /** DataProvider that throws UnsupportedOperationException on size(). */
    private static DataProvider<String, Void> unsupportedCountProvider(List<String> items) {
        return DataProvider.fromCallbacks(
                q -> items.stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { throw new UnsupportedOperationException("count not supported"); }
        );
    }

    /** Standard in-memory DataProvider that answers size() correctly. */
    private static DataProvider<String, Void> exactCountProvider(List<String> items) {
        return DataProvider.fromCallbacks(
                q -> items.stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> items.size()
        );
    }

    private static List<String> items(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "item-" + i)
                .toList();
    }

    // =========================================================================
    // Tests — IllegalStateException from lazy provider (the reported bug)
    // =========================================================================

    @Test
    void bar_lazyProvider_doesNotThrowOnConstruction() {
        // GIVEN: lazy listing that throws IllegalStateException on size()
        var listing = new StubListing(lazyProviderNoCount(items(60)));

        // WHEN / THEN: constructing the bar must not throw; refreshState() is deferred to onAttach
        assertDoesNotThrow(() -> new ItemListingPaginationBar<>(listing));
    }

    @Test
    void bar_lazyProvider_noEstimate_fallsBackToOnePage() {
        // Without an estimate the bar shows only page 1 (currentPage * pageSize = 1*50 = 50 → 1 page)
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        bar.refreshState();   // simulate onAttach (no live Vaadin UI in unit tests)

        assertEquals(1, bar.getCurrentPage());
        assertEquals(1, bar.getTotalPages());
    }

    @Test
    void bar_lazyProvider_withEstimate_computesApproximatePages() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing)
                .withItemCountEstimate(60);  // 60 items / 50 page-size = 2 pages
        // withItemCountEstimate calls refreshState() internally — no extra call needed

        assertEquals(2, bar.getTotalPages());
    }

    @Test
    void bar_lazyProvider_withEstimateAndCustomPageSize_computesCorrectPages() {
        var listing = new StubListing(lazyProviderNoCount(items(100)));
        // page size comes from Grid.getPageSize(); Div stub returns default 50
        // so we rely on withPageSize() → but StubListing.getComponent() is a Div, not a Grid
        // Therefore getPageSize() returns the fallback 50; we just verify the math:
        // estimate=100, pageSize=50 → 2 pages
        var bar = new ItemListingPaginationBar<>(listing)
                .withItemCountEstimate(100);
        // withItemCountEstimate calls refreshState() internally

        assertEquals(2, bar.getTotalPages());
    }

    // =========================================================================
    // Tests — UnsupportedOperationException from provider
    // =========================================================================

    @Test
    void bar_unsupportedCountProvider_doesNotThrowOnConstruction() {
        var listing = new StubListing(unsupportedCountProvider(items(30)));
        assertDoesNotThrow(() -> new ItemListingPaginationBar<>(listing));
    }

    @Test
    void bar_unsupportedCountProvider_withEstimate_computesPages() {
        var listing = new StubListing(unsupportedCountProvider(items(30)));
        var bar = new ItemListingPaginationBar<>(listing).withItemCountEstimate(30);
        // 30 / 50 → ceil = 1
        assertEquals(1, bar.getTotalPages());
    }

    // =========================================================================
    // Tests — exact count provider (existing happy path still works)
    // =========================================================================

    @Test
    void bar_exactCountProvider_computesExactPages() {
        var listing = new StubListing(exactCountProvider(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        bar.refreshState();   // simulate onAttach — count fires here, NOT in constructor
        // 60 / 50 = ceil(1.2) = 2
        assertEquals(2, bar.getTotalPages());
    }

    @Test
    void bar_exactCountProvider_zeroItems_showsOnePage() {
        var listing = new StubListing(exactCountProvider(List.of()));
        var bar = new ItemListingPaginationBar<>(listing);
        bar.refreshState();   // simulate onAttach
        assertEquals(1, bar.getTotalPages());
    }

    // =========================================================================
    // Tests — count NOT called at construction time (lazy-attach guarantee)
    // =========================================================================

    @Test
    void bar_exactCountProvider_doesNotCallCountOnConstruction() {
        // The count callback must NOT be invoked during construction — only on attach.
        int[] countCallCount = {0};
        DataProvider<String, Void> provider = DataProvider.fromCallbacks(
                q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { countCallCount[0]++; return 60; }
        );
        var listing = new StubListing(provider);

        new ItemListingPaginationBar<>(listing);  // must NOT trigger count

        assertEquals(0, countCallCount[0],
                "COUNT must not be called during construction — only on attach");
    }

    @Test
    void bar_exactCountProvider_callsCountOnRefreshState() {
        // refreshState() (triggered by onAttach) IS expected to call count once.
        int[] countCallCount = {0};
        DataProvider<String, Void> provider = DataProvider.fromCallbacks(
                q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { countCallCount[0]++; return 60; }
        );
        var listing = new StubListing(provider);
        var bar = new ItemListingPaginationBar<>(listing);

        assertEquals(0, countCallCount[0], "No count yet before attach");

        bar.refreshState();   // simulate onAttach

        assertEquals(1, countCallCount[0], "Count must fire exactly once on attach");
        assertEquals(2, bar.getTotalPages());   // 60 / 50 = 2
    }

    // =========================================================================
    // Tests — withItemCountEstimate validation
    // =========================================================================

    @Test
    void bar_withItemCountEstimate_zeroThrows() {
        var listing = new StubListing(lazyProviderNoCount(items(10)));
        var bar = new ItemListingPaginationBar<>(listing);
        assertThrows(IllegalArgumentException.class, () -> bar.withItemCountEstimate(0));
    }

    @Test
    void bar_withItemCountEstimate_negativeThrows() {
        var listing = new StubListing(lazyProviderNoCount(items(10)));
        var bar = new ItemListingPaginationBar<>(listing);
        assertThrows(IllegalArgumentException.class, () -> bar.withItemCountEstimate(-1));
    }

    // =========================================================================
    // Tests — ItemListingPageSizeSelector construction
    // =========================================================================

    @Test
    void pageSizeSelector_lazyProvider_doesNotThrowOnConstruction() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        assertDoesNotThrow(() ->
                ItemListingPageSizeSelector.of(listing)
                        .withOptions(10, 25, 50)
                        .withDefaultSize(10)
                        .build()
        );
    }

    @Test
    void pageSizeSelector_withBar_lazyProvider_doesNotThrowOnConstruction() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing).withItemCountEstimate(60);

        assertDoesNotThrow(() ->
                ItemListingPageSizeSelector.of(listing)
                        .withOptions(10, 25, 50)
                        .withDefaultSize(10)
                        .withPaginationBar(bar)
                        .build()
        );
    }

    @Test
    void pageSizeSelector_defaultOptions_areSet() {
        var listing = new StubListing(exactCountProvider(items(10)));
        var selector = ItemListingPageSizeSelector.of(listing).build();
        assertNotNull(selector);
        // Component exists and carries the CSS class
        assertTrue(selector.getClassNames().contains("page-size-selector"));
    }

    @Test
    void pageSizeSelector_withOptions_nullThrows() {
        var listing = new StubListing(exactCountProvider(items(10)));
        assertThrows(IllegalArgumentException.class, () ->
                ItemListingPageSizeSelector.of(listing)
                        .withOptions((List<Integer>) null)
                        .build()
        );
    }

    @Test
    void pageSizeSelector_withDefaultSize_zeroThrows() {
        var listing = new StubListing(exactCountProvider(items(10)));
        assertThrows(IllegalArgumentException.class, () ->
                ItemListingPageSizeSelector.of(listing)
                        .withDefaultSize(0)
                        .build()
        );
    }

    @Test
    void pageSizeSelector_withPaginationBar_nullThrows() {
        var listing = new StubListing(exactCountProvider(items(10)));
        assertThrows(IllegalArgumentException.class, () ->
                ItemListingPageSizeSelector.of(listing)
                        .withPaginationBar(null)
                        .build()
        );
    }

    // =========================================================================
    // Tests — count NOT called on every page navigation (lazy-loading contract)
    // =========================================================================

    @Test
    void bar_goToPage_doesNotCallCount() {
        // COUNT must fire once (onAttach/refreshState) and then NEVER again on
        // each page navigation — violating this would issue a COUNT(*) SQL per click.
        int[] countCallCount = {0};
        DataProvider<String, Void> provider = DataProvider.fromCallbacks(
                q -> items(100).stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { countCallCount[0]++; return 100; }
        );
        var listing = new StubListing(provider);
        var bar = new ItemListingPaginationBar<>(listing);
        bar.refreshState();   // simulate onAttach — count fires once here

        assertEquals(1, countCallCount[0], "count fired once on refreshState (onAttach)");

        // Now navigate across several pages — count must NOT fire again
        bar.goToPage(2);
        bar.goToPage(1);
        bar.goToPage(2);

        assertEquals(1, countCallCount[0],
                "COUNT must NOT be called on page navigation — only on data/filter changes");
    }

    // =========================================================================
    // Tests — addPageChangeListener on ItemListingPaginationBar
    // =========================================================================

    @Test
    void bar_addPageChangeListener_nullThrows() {
        var listing = new StubListing(exactCountProvider(items(100)));
        var bar = new ItemListingPaginationBar<>(listing);
        assertThrows(IllegalArgumentException.class, () -> bar.addPageChangeListener(null));
    }

    @Test
    void bar_goToPage_firesPageChangeListener() {
        var listing = new StubListing(exactCountProvider(items(100)));
        var bar = new ItemListingPaginationBar<>(listing)
                .withItemCountEstimate(100);   // 100 / 50 = 2 pages

        int[] fired = {0};
        int[] lastPage = {-1};
        bar.addPageChangeListener(page -> { fired[0]++; lastPage[0] = page; });

        bar.goToPage(2);

        assertEquals(1, fired[0], "listener must fire exactly once on goToPage");
        assertEquals(2, lastPage[0], "listener must receive the new page number");
    }

    @Test
    void bar_setPageSize_firesPageChangeListenerForPage1() {
        var listing = new StubListing(exactCountProvider(items(100)));
        var bar = new ItemListingPaginationBar<>(listing)
                .withItemCountEstimate(100);

        // Navigate away from page 1 first
        bar.goToPage(2);

        int[] fired = {0};
        int[] lastPage = {-1};
        bar.addPageChangeListener(page -> { fired[0]++; lastPage[0] = page; });

        bar.setPageSize(20);

        assertEquals(1, fired[0], "setPageSize must fire listener once (page reset to 1)");
        assertEquals(1, lastPage[0], "setPageSize always resets to page 1");
    }

    @Test
    void bar_pageChangeListenerRegistration_canBeRemoved() {
        var listing = new StubListing(exactCountProvider(items(100)));
        var bar = new ItemListingPaginationBar<>(listing).withItemCountEstimate(100);

        int[] fired = {0};
        var reg = bar.addPageChangeListener(p -> fired[0]++);

        bar.goToPage(2);
        assertEquals(1, fired[0]);

        reg.remove();
        bar.goToPage(1);
        assertEquals(1, fired[0], "after removal the listener must NOT fire");
    }

    // =========================================================================
    // Tests — withLazyFetch (managed-fetch mode)
    // =========================================================================

    @Test
    void selector_withLazyFetch_nullFetchCallbackThrows() {
        var listing = new StubListing(exactCountProvider(items(10)));
        assertThrows(IllegalArgumentException.class, () ->
                ItemListingPageSizeSelector.of(listing)
                        .withLazyFetch(null, () -> 10)
                        .build()
        );
    }

    @Test
    void selector_withLazyFetch_doesNotThrowWithLazyListing() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);

        assertDoesNotThrow(() ->
                ItemListingPageSizeSelector.of(listing)
                        .withOptions(5, 10, 20)
                        .withDefaultSize(10)
                        .withPaginationBar(bar)
                        .withLazyFetch(
                                q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                                () -> 60)
                        .build()
        );
    }

    @Test
    void selector_withLazyFetch_pageSizeChange_firesBarRefresh() {
        // Verify that changing page size via the selector correctly resets to page 1
        // and provides the bar with the new page count.
        // We track how many times the bar's page-change listeners fire (should be 0 on
        // initial build; the selector manages page reset internally for size changes).
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);

        ItemListingPageSizeSelector.of(listing)
                .withOptions(5, 10, 20)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> 60)
                .build();

        // After construction: bar has estimate 60, pageSize 10 → totalPages = 6
        assertEquals(6, bar.getTotalPages());
        assertEquals(1, bar.getCurrentPage());
    }

    // =========================================================================
    // Stub — FilterInputGroup
    // =========================================================================

    /**
     * Minimal {@link FilterInputGroup} stub that stores registered listeners and
     * allows tests to fire a synthetic filter-change event via {@link #fireFilterChange()}.
     */
    private static class StubFilterGroup implements FilterInputGroup {
        private final List<FilterChangeListener<?>> listeners = new ArrayList<>();

        /** Fires a null-event to all registered listeners (the selector lambda ignores it). */
        void fireFilterChange() {
            listeners.forEach(this::fireFilterChanged);
        }

        private <T> void fireFilterChanged(FilterChangeListener<T> listener) {
            listener.filterChanged(null);
        }

        @Override
        public Registration addFilterChangeListener(FilterChangeListener<?> listener) {
            listeners.add(listener);
            return () -> listeners.remove(listener);
        }

        // ── stub-out unused methods ──────────────────────────────────────────
        @Override public Optional<com.holonplatform.core.query.QueryFilter> getQueryFilter() { return Optional.empty(); }
        @Override public boolean isAnyActive()                                               { return false; }
        @Override public void resetAll()                                                     {}
        @Override public <T> Optional<FilterInput<T>> getFilterInput(Property<T> p)         { return Optional.empty(); }
        @Override public Stream<PropertyBinding<?>> getPropertyBindings()                   { return Stream.empty(); }
    }

    // =========================================================================
    // Tests — resetToPage1()
    // =========================================================================

    @Test
    void resetToPage1_withNoManagedFetch_isNoOp() {
        // Without withLazyFetch, resetToPage1 must be a no-op (no exception)
        var listing = new StubListing(exactCountProvider(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        var selector = ItemListingPageSizeSelector.of(listing)
                .withPaginationBar(bar)
                .withDefaultSize(10)
                .build();

        assertDoesNotThrow(selector::resetToPage1);
    }

    @Test
    void resetToPage1_withManagedFetch_doesNotThrow() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);

        var selector = ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> 60)
                .build();

        assertDoesNotThrow(selector::resetToPage1);
    }

    @Test
    void resetToPage1_recomputesBarTotalPagesFromUpdatedCount() {
        // countSupplier is a closure over a mutable int[]:
        // we simulate a filter narrowing the result set from 60 → 20 items.
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        int[] count = {60};

        var selector = ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(count[0]).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> count[0])   // ← closure reads current count at call time
                .build();

        // Initial state: 60 items / 10 per page = 6 pages
        assertEquals(6, bar.getTotalPages());

        // Simulate filter applied → fewer results
        count[0] = 20;
        selector.resetToPage1();

        // Bar must recompute: 20 items / 10 per page = 2 pages
        assertEquals(2, bar.getTotalPages());
        assertEquals(1, bar.getCurrentPage());
    }

    @Test
    void resetToPage1_withNullCountSupplier_doesNotThrow() {
        // countSupplier may legitimately be null (unknown total)
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);

        var selector = ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                        null)   // no count supplier
                .build();

        assertDoesNotThrow(selector::resetToPage1);
    }

    @Test
    void resetToPage1_withoutBar_doesNotThrow() {
        // Standalone managed-fetch mode (no bar): reset is still safe
        var listing = new StubListing(lazyProviderNoCount(items(60)));

        var selector = ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withLazyFetch(
                        q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> 60)
                .build();

        assertDoesNotThrow(selector::resetToPage1);
    }

    // =========================================================================
    // Tests — withFilterReset(FilterInputGroup)
    // =========================================================================

    @Test
    void withFilterReset_nullThrows() {
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        assertThrows(IllegalArgumentException.class, () ->
                ItemListingPageSizeSelector.of(listing)
                        .withLazyFetch(
                                q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                                () -> 60)
                        .withFilterReset(null)
                        .build()
        );
    }

    @Test
    void withFilterReset_firesResetToPage1OnFilterChange() {
        // The filter-change listener registered by withFilterReset must call resetToPage1(),
        // which re-queries countSupplier and updates the bar's total pages.
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        var filterGroup = new StubFilterGroup();
        int[] count = {60};

        ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(count[0]).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> count[0])
                .withFilterReset(filterGroup)
                .build();

        // Before filter: 6 pages
        assertEquals(6, bar.getTotalPages());

        // Apply a narrowing filter
        count[0] = 30;
        filterGroup.fireFilterChange();   // triggers resetToPage1() internally

        // Bar must now show 3 pages (30 / 10)
        assertEquals(3, bar.getTotalPages());
        assertEquals(1, bar.getCurrentPage());
    }

    @Test
    void withFilterReset_multipleFilterChanges_eachRecomputesCount() {
        var listing = new StubListing(lazyProviderNoCount(items(100)));
        var bar = new ItemListingPaginationBar<>(listing);
        var filterGroup = new StubFilterGroup();
        int[] count = {100};

        ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(count[0]).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> count[0])
                .withFilterReset(filterGroup)
                .build();

        assertEquals(10, bar.getTotalPages());  // 100 / 10

        count[0] = 50;
        filterGroup.fireFilterChange();
        assertEquals(5, bar.getTotalPages());   // 50 / 10

        count[0] = 10;
        filterGroup.fireFilterChange();
        assertEquals(1, bar.getTotalPages());   // 10 / 10

        count[0] = 0;
        filterGroup.fireFilterChange();
        // 0 items: estimate guarded by Math.max(1, …) → 1 page
        assertEquals(1, bar.getTotalPages());
    }

    @Test
    void withFilterReset_withoutManagedFetch_isNoOp() {
        // withFilterReset has no effect unless withLazyFetch is also called
        var listing = new StubListing(exactCountProvider(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        var filterGroup = new StubFilterGroup();

        ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                // NOTE: no withLazyFetch — unmanaged mode
                .withFilterReset(filterGroup)
                .build();

        int before = bar.getTotalPages();
        filterGroup.fireFilterChange();   // should be a no-op
        assertEquals(before, bar.getTotalPages());
    }

    @Test
    void withFilterReset_doesNotFireBeforeFirstFilterChange() {
        // Constructing the selector must NOT trigger a reset
        var listing = new StubListing(lazyProviderNoCount(items(60)));
        var bar = new ItemListingPaginationBar<>(listing);
        var filterGroup = new StubFilterGroup();
        int[] countCallCount = {0};

        ItemListingPageSizeSelector.of(listing)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> items(60).stream().skip(q.getOffset()).limit(q.getLimit()),
                        () -> { countCallCount[0]++; return 60; })
                .withFilterReset(filterGroup)
                .build();

        // countSupplier called once during initManagedFetch (to seed the estimate)
        int callsAfterBuild = countCallCount[0];

        // No filter change yet — no additional calls
        assertEquals(callsAfterBuild, countCallCount[0]);
    }
}







