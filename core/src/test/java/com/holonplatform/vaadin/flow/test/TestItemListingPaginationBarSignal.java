/*
 * Copyright 2016-2017 Axioma srl.
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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListingPaginationBar;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Signal-based tests for {@link ItemListingPaginationBar#autoRefreshOnFilterSignal}.
 *
 * <p>Extends {@link AbstractSessionTest} which wires a mock {@link com.vaadin.flow.server.VaadinSession}
 * (with {@code hasLock() → true}) and initialises a per-test {@link com.vaadin.flow.component.UI}.
 * This satisfies the requirement of {@code Signal.effect(Component, Runnable)}, which needs the
 * component to be attached to a UI with a live session before it starts reacting to signal changes.</p>
 *
 * <p>Test groups:</p>
 * <ol>
 *   <li>Effect does NOT fire at registration time (parity with listener variant)</li>
 *   <li>Effect DOES fire after a filter change (once attached)</li>
 *   <li>Effect stops firing after {@code Registration.remove()}</li>
 *   <li>Fluent {@code withFilterAutoRefreshSignal} returns the bar itself</li>
 *   <li>Effect is silent while bar is NOT attached (deactivated between detach/attach)</li>
 * </ol>
 */
class TestItemListingPaginationBarSignal extends AbstractSessionTest {

    // -------------------------------------------------------------------------
    // Stub ItemListing — stable component reference required for attach/detach
    // -------------------------------------------------------------------------

    /**
     * Minimal {@link ItemListing} stub with a <em>stable</em> {@link #getComponent()} reference.
     * Unlike the stub in {@link TestItemListingPaginationBarLazy}, this one always returns
     * the same {@link Div} so that the parent UI can keep track of it across calls.
     */
    private static class StubListing implements ItemListing<String, String> {

        private final DataProvider<String, ?> dataProvider;
        private final Div component = new Div();

        StubListing(DataProvider<String, ?> dataProvider) {
            this.dataProvider = dataProvider;
        }

        @Override public DataProvider<String, ?> getDataProvider()        { return dataProvider; }
        @Override public DataProvider<String, ?> getBackEndDataProvider() { return dataProvider; }
        @Override public Component getComponent()                          { return component; }

        @Override public void refresh()                                    { /* no-op */ }
        @Override public void scrollToIndex(int i)                        { /* no-op */ }
        @Override public void scrollToStart()                             { /* no-op */ }
        @Override public void scrollToEnd()                               { /* no-op */ }

        // ── stub-out unused methods ──────────────────────────────────────────
        @Override public List<String> getVisibleColumns()               { throw new UnsupportedOperationException(); }
        @Override public List<String> getHiddenColumns()                { throw new UnsupportedOperationException(); }
        @Override public void setColumnVisible(String p, boolean v)     { throw new UnsupportedOperationException(); }
        @Override public Optional<String> getColumnHeader(String p)     { throw new UnsupportedOperationException(); }
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
        @Override public Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getHeader() { throw new UnsupportedOperationException(); }
        @Override public Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getFooter() { throw new UnsupportedOperationException(); }
        @Override public boolean isEditable()                           { throw new UnsupportedOperationException(); }
        @Override public Optional<String> isEditing()                   { throw new UnsupportedOperationException(); }
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
            @SuppressWarnings("unchecked")
            GridLazyDataView<String> view = mock(GridLazyDataView.class);
            return view;
        }
        @Override public Optional<String> getItemAtIndex(int i)         { throw new UnsupportedOperationException(); }
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
        @Override public com.holonplatform.vaadin.flow.components.Selectable.SelectionMode getSelectionMode() { throw new UnsupportedOperationException(); }
        @Override public java.util.Set<String> getSelectedItems()       { throw new UnsupportedOperationException(); }
        @Override public Optional<String> getFirstSelectedItem()        { throw new UnsupportedOperationException(); }
        @Override public void select(String i)                          { throw new UnsupportedOperationException(); }
        @Override public void deselect(String i)                        { throw new UnsupportedOperationException(); }
        @Override public void deselectAll()                             { throw new UnsupportedOperationException(); }
        @Override public Registration addSelectionListener(com.holonplatform.vaadin.flow.components.Selectable.SelectionListener<String> l) { throw new UnsupportedOperationException(); }
        @Override public Optional<String> getFirstItem()                { throw new UnsupportedOperationException(); }
    }

    // -------------------------------------------------------------------------
    // Stub FilterInputGroup
    // -------------------------------------------------------------------------

    private static class StubFilterGroup implements FilterInputGroup {
        private final List<FilterChangeListener<?>> listeners = new ArrayList<>();

        /**
         * Tick counter — incremented on every {@link #fireFilterChange()} call.
         * Used by {@link #queryFilterSignal()} to back the signal with an ever-changing
         * integer value, ensuring the {@code ValueSignal} always detects a real change.
         *
         * <p>The default {@link FilterInputGroup#queryFilterSignal()} would use
         * {@code getQueryFilter()} (always {@code Optional.empty()}) as the signal value.
         * Since {@code Optional.empty()} is a singleton, the {@code ValueSignal} would
         * detect no change and the effect would never fire. Overriding with an integer
         * counter that is strictly monotonically increasing avoids this trap.</p>
         */
        private final ValueSignal<Integer> tickSignal = new ValueSignal<>(0);
        private int tick = 0;

        @SuppressWarnings({"unchecked", "rawtypes"})
        void fireFilterChange() {
            tick++;
            tickSignal.set(tick);   // always a new value → signal always fires
            List.copyOf(listeners).forEach(l -> ((FilterChangeListener) l).filterChanged(null));
        }

        /**
         * Returns a signal backed by the tick counter so the effect in
         * {@link ItemListingPaginationBar#autoRefreshOnFilterSignal} always re-runs
         * on each {@link #fireFilterChange()} call.
         */
        @Override
        @SuppressWarnings("unchecked")
        public Signal<Optional<QueryFilter>> queryFilterSignal() {
            // The effect only calls .get() to register a dependency — it never
            // unwraps the Optional<QueryFilter>. Casting Integer signal to the
            // expected type is safe for this test-only usage.
            return (Signal<Optional<QueryFilter>>) (Signal<?>) tickSignal;
        }

        @Override
        public Registration addFilterChangeListener(FilterChangeListener<?> listener) {
            listeners.add(listener);
            return () -> listeners.remove(listener);
        }

        @Override public Optional<QueryFilter> getQueryFilter()            { return Optional.empty(); }
        @Override public boolean isAnyActive()                             { return false; }
        @Override public void resetAll()                                   {}
        @Override public <T> Optional<FilterInput<T>> getFilterInput(Property<T> p) { return Optional.empty(); }
        @Override public Stream<PropertyBinding<?>> getPropertyBindings()  { return Stream.empty(); }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DataProvider<String, Void> exactCountProvider(int size) {
        List<String> items = java.util.stream.IntStream.rangeClosed(1, size)
                .mapToObj(i -> "item-" + i).toList();
        return DataProvider.fromCallbacks(
                q -> items.stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> items.size());
    }

    private static DataProvider<String, Void> countingProvider(int size, int[] callCount) {
        List<String> items = java.util.stream.IntStream.rangeClosed(1, size)
                .mapToObj(i -> "item-" + i).toList();
        return DataProvider.fromCallbacks(
                q -> items.stream().skip(q.getOffset()).limit(q.getLimit()),
                q -> { callCount[0]++; return size; });
    }

    /**
     * Attaches the pagination bar to the per-test UI so {@code onAttach} fires
     * and {@code Signal.effect} registrations become active.
     */
    private void attachBar(ItemListingPaginationBar<?, ?> bar) {
        ui.add(bar);
        ComponentUtil.onComponentAttach(bar, true);
    }

    // =========================================================================
    // Tests — signal-based filter auto-refresh
    // =========================================================================

    @Test
    void autoRefreshOnFilterSignal_doesNotFireAtBindTime() {
        // Signal effect must NOT trigger refreshState() at registration time —
        // same parity as the listener-based variant (AtomicBoolean guard).
        int[] countCalls = {0};
        var listing = new StubListing(countingProvider(60, countCalls));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);             // onAttach fires refreshState() → count called once
        int callsAfterAttach = countCalls[0];

        var filterGroup = new StubFilterGroup();
        bar.autoRefreshOnFilterSignal(filterGroup);

        // Registration alone must not trigger another count query
        assertEquals(callsAfterAttach, countCalls[0],
                "signal effect must not trigger refreshState at registration time");
    }

    @Test
    void autoRefreshOnFilterSignal_firesOnFilterChange() {
        // After the bar is attached and a filter change fires, the signal
        // effect must call refreshState() (which re-queries the count).
        int[] countCalls = {0};
        var listing = new StubListing(countingProvider(60, countCalls));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);
        int callsAfterAttach = countCalls[0];   // 1 call from onAttach

        var filterGroup = new StubFilterGroup();
        bar.autoRefreshOnFilterSignal(filterGroup);

        filterGroup.fireFilterChange();         // triggers signal → effect → refreshState

        assertTrue(countCalls[0] > callsAfterAttach,
                "refreshState must be called after filter change (count > calls after attach)");
    }

    @Test
    void autoRefreshOnFilterSignal_multipleFilterChanges_eachTriggersRefresh() {
        int[] countCalls = {0};
        var listing = new StubListing(countingProvider(60, countCalls));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);
        int baseline = countCalls[0];

        var filterGroup = new StubFilterGroup();
        bar.autoRefreshOnFilterSignal(filterGroup);

        filterGroup.fireFilterChange();
        filterGroup.fireFilterChange();
        filterGroup.fireFilterChange();

        assertTrue(countCalls[0] >= baseline + 3,
                "each filter change must trigger a refreshState call");
    }

    @Test
    void autoRefreshOnFilterSignal_registrationCanBeRemovedEarly() {
        int[] countCalls = {0};
        var listing = new StubListing(countingProvider(60, countCalls));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);

        var filterGroup = new StubFilterGroup();
        com.vaadin.flow.shared.Registration reg = bar.autoRefreshOnFilterSignal(filterGroup);

        filterGroup.fireFilterChange();         // fires once
        int callsAfterFirstFire = countCalls[0];

        reg.remove();                           // deregister effect

        filterGroup.fireFilterChange();         // must NOT trigger refreshState
        filterGroup.fireFilterChange();

        assertEquals(callsAfterFirstFire, countCalls[0],
                "after remove() the effect must not fire on subsequent filter changes");
    }

    @Test
    void autoRefreshOnFilterSignal_withRefreshListing_callsListingRefresh() {
        // When refreshListing=true, listing.refresh() must be called in addition to refreshState().
        int[] refreshCalls = {0};
        DataProvider<String, ?> dp = exactCountProvider(20);

        var listing = new StubListing(dp) {
            @Override public void refresh() { refreshCalls[0]++; }
        };
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);

        var filterGroup = new StubFilterGroup();
        bar.autoRefreshOnFilterSignal(filterGroup, true);   // refreshListing = true

        filterGroup.fireFilterChange();

        assertTrue(refreshCalls[0] > 0,
                "listing.refresh() must be called when refreshListing=true");
    }

    @Test
    void autoRefreshOnFilterSignal_withoutRefreshListing_doesNotCallListingRefresh() {
        int[] refreshCalls = {0};
        DataProvider<String, ?> dp = exactCountProvider(20);

        var listing = new StubListing(dp) {
            @Override public void refresh() { refreshCalls[0]++; }
        };
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);

        var filterGroup = new StubFilterGroup();
        bar.autoRefreshOnFilterSignal(filterGroup, false);  // refreshListing = false

        filterGroup.fireFilterChange();

        assertEquals(0, refreshCalls[0],
                "listing.refresh() must NOT be called when refreshListing=false");
    }

    @Test
    void withFilterAutoRefreshSignal_fluentReturnsBar() {
        var listing = new StubListing(exactCountProvider(60));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);

        var filterGroup = new StubFilterGroup();
        var returned = bar.withFilterAutoRefreshSignal(filterGroup);

        assertSame(bar, returned, "withFilterAutoRefreshSignal must return the bar instance (fluent)");
    }

    @Test
    void withFilterAutoRefreshSignal_withRefreshListing_fluentReturnsBar() {
        var listing = new StubListing(exactCountProvider(60));
        var bar = new ItemListingPaginationBar<>(listing);
        attachBar(bar);

        var filterGroup = new StubFilterGroup();
        var returned = bar.withFilterAutoRefreshSignal(filterGroup, true);

        assertSame(bar, returned);
    }
}

