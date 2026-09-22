package com.iyensoft.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.test.AbstractSessionTest;
import com.iyensoft.vaadin.flow.components.ItemListingPaginationBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Guards the data-provider binding lifecycle of {@link ItemListingPaginationBar}.
 *
 * <p>The bar registers a {@link DataProviderListener} on the listing's
 * {@link DataProvider}. That provider is owned by the listing and can outlive the bar,
 * so the registration must be dropped on detach — otherwise a detached bar stays
 * reachable from the provider's listener list, and every data change runs
 * {@code refreshState()} (a {@code size()} COUNT query plus a full re-render) on a
 * component nobody can see.</p>
 */
class TestItemListingPaginationBarBinding extends AbstractSessionTest {

    /** A {@link ListDataProvider} that tracks how many listeners are currently registered. */
    private static class TrackingDataProvider extends ListDataProvider<String> {

        private int liveListeners;

        TrackingDataProvider(List<String> items) {
            super(items);
        }

        @Override
        public Registration addDataProviderListener(DataProviderListener<String> listener) {
            final Registration delegate = super.addDataProviderListener(listener);
            liveListeners++;
            return () -> {
                delegate.remove();
                liveListeners--;
            };
        }

        int liveListeners() {
            return liveListeners;
        }
    }

    @Test
    void dataProviderListener_isRegisteredOnAttach_andRemovedOnDetach() {
        TrackingDataProvider provider = new TrackingDataProvider(items(60));
        var bar = new ItemListingPaginationBar<>(new StubListing(provider));
        bar.setAutoRefreshOnDataChange(true);

        attach(bar);
        assertEquals(1, provider.liveListeners(),
                "the bar must observe the data provider while attached");

        detach(bar);
        assertEquals(0, provider.liveListeners(),
                "a detached bar must not stay registered on the data provider");
    }

    @Test
    void dataProviderListener_isReEstablishedOnReAttach() {
        TrackingDataProvider provider = new TrackingDataProvider(items(60));
        var bar = new ItemListingPaginationBar<>(new StubListing(provider));
        bar.setAutoRefreshOnDataChange(true);

        attach(bar);
        detach(bar);
        attach(bar);

        assertEquals(1, provider.liveListeners(),
                "re-attaching must restore the binding, and must not double-register");
    }

    @Test
    void dataProviderListener_doesNotAccumulateAcrossManyAttachCycles() {
        TrackingDataProvider provider = new TrackingDataProvider(items(60));
        var bar = new ItemListingPaginationBar<>(new StubListing(provider));
        bar.setAutoRefreshOnDataChange(true);

        for (int i = 0; i < 5; i++) {
            attach(bar);
            detach(bar);
        }

        assertEquals(0, provider.liveListeners(),
                "listeners must not accumulate over repeated attach/detach cycles");
    }

    @Test
    void reAttachRebindsEvenInManagedMode_whereOnAttachSkipsRefreshState() {
        // Managed mode = at least one page-change listener registered. onAttach() then
        // skips refreshState(), so the re-binding must not be left to that call.
        TrackingDataProvider provider = new TrackingDataProvider(items(60));
        var bar = new ItemListingPaginationBar<>(new StubListing(provider));
        bar.setAutoRefreshOnDataChange(true);
        bar.addPageChangeListener(page -> { /* puts the bar into managed mode */ });

        attach(bar);
        detach(bar);
        attach(bar);

        assertEquals(1, provider.liveListeners(),
                "managed mode must still re-bind the data provider listener on re-attach");
    }

    @Test
    void disablingAutoRefresh_dropsTheRegistration() {
        TrackingDataProvider provider = new TrackingDataProvider(items(60));
        var bar = new ItemListingPaginationBar<>(new StubListing(provider));
        bar.setAutoRefreshOnDataChange(true);
        attach(bar);
        assertEquals(1, provider.liveListeners());

        bar.setAutoRefreshOnDataChange(false);

        assertEquals(0, provider.liveListeners());
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private void attach(ItemListingPaginationBar<?, ?> bar) {
        ui.add(bar);
        ComponentUtil.onComponentAttach(bar, true);
    }

    private void detach(ItemListingPaginationBar<?, ?> bar) {
        ui.remove(bar);
        ComponentUtil.onComponentDetach(bar);
    }

    private static List<String> items(int size) {
        return java.util.stream.IntStream.rangeClosed(1, size).mapToObj(i -> "item-" + i).toList();
    }

    /** Minimal {@link ItemListing} stub with a stable component reference. */
    private static class StubListing implements ItemListing<String, String> {

        private final DataProvider<String, ?> dataProvider;
        private final Div component = new Div();

        StubListing(DataProvider<String, ?> dataProvider) {
            this.dataProvider = dataProvider;
        }

        @Override public DataProvider<String, ?> getDataProvider()        { return dataProvider; }
        @Override public DataProvider<String, ?> getBackEndDataProvider() { return dataProvider; }
        @Override public Component getComponent()                          { return component; }

        @Override public void refresh()            { /* no-op */ }
        @Override public void scrollToIndex(int i) { /* no-op */ }
        @Override public void scrollToStart()      { /* no-op */ }
        @Override public void scrollToEnd()        { /* no-op */ }
        @Override public void setItemIndexProvider(com.vaadin.flow.data.provider.ItemIndexProvider p) { /* no-op */ }
        @Override public void scrollToItem(String i) { /* no-op */ }
        @Override public void recalculateColumnWidths() { /* no-op */ }

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
        @Override public void setMobileHeader(Component c)              { throw new UnsupportedOperationException(); }
        @Override public void addThemeVariants(com.vaadin.flow.component.grid.GridVariant... v) { throw new UnsupportedOperationException(); }
        @Override public void removeThemeVariants(com.vaadin.flow.component.grid.GridVariant... v) { throw new UnsupportedOperationException(); }
        @Override public void showMobileColumn(boolean m)               { throw new UnsupportedOperationException(); }
        @Override public boolean isMobileColumnVisible()                { throw new UnsupportedOperationException(); }
        @Override public void hideMobileColumn()                        { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.component.grid.dataview.GridLazyDataView<String> setItems(com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback<String, Void> fc) { throw new UnsupportedOperationException(); }
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
        @Override public com.holonplatform.vaadin.flow.components.Selectable.SelectionMode getSelectionMode() { throw new UnsupportedOperationException(); }
        @Override public java.util.Set<String> getSelectedItems()       { throw new UnsupportedOperationException(); }
        @Override public Optional<String> getFirstSelectedItem()        { throw new UnsupportedOperationException(); }
        @Override public void select(String i)                          { throw new UnsupportedOperationException(); }
        @Override public void deselect(String i)                        { throw new UnsupportedOperationException(); }
        @Override public void deselectAll()                             { throw new UnsupportedOperationException(); }
        @Override public com.holonplatform.core.Registration addSelectionListener(com.holonplatform.vaadin.flow.components.Selectable.SelectionListener<String> l) { throw new UnsupportedOperationException(); }
        @Override public Optional<String> getFirstItem()                { throw new UnsupportedOperationException(); }
        @Override public List<com.vaadin.flow.component.grid.GridSortOrder<String>> getGridSortOrders() { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.component.grid.Grid<String> getGrid() { throw new UnsupportedOperationException(); }
    }
}



