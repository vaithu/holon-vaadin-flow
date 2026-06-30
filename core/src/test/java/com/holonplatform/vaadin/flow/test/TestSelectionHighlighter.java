package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import org.junit.jupiter.api.Test;

/**
 * Browser-free unit tests that verify {@link SelectionHighlighter} correctly
 * installs and applies the CSS part-name generator on a grid listing.
 *
 * <p>Tests confirm:</p>
 * <ol>
 *   <li>The generator returns {@code null} for all rows when nothing is selected.</li>
 *   <li>The generator returns {@code "mdl-selected"} for the highlighted item only.</li>
 *   <li>Clearing the selection (passing {@code null}) removes the part name.</li>
 *   <li>A custom key extractor compares by key, not object identity.</li>
 *   <li>Switching selection removes the old highlight.</li>
 *   <li>A custom part name is honoured.</li>
 *   <li>{@code setPartNameGenerator} is called exactly once at construction.</li>
 *   <li>Each {@code setHighlighted} call reinstalls the generator (always-reinstall strategy).</li>
 *   <li>Integration: {@code master(listing())} adds {@code mdl-master-grid} to the Grid.</li>
 * </ol>
 *
 * <p>No browser, servlet container, or Vaadin UI session required.</p>
 */
class TestSelectionHighlighter {

    record Product(Long id, String name) {}

    // ── 1. No selection → generator returns null for every item ──────────────

    @Test
    void noSelection_generatorReturnsNullForAllItems() {
        CapturingListing<Product> stub = new CapturingListing<>();
        new SelectionHighlighter<>(stub);

        assertNull(stub.currentGenerator.apply(new Product(1L, "Alpha")),
                "Generator must return null when nothing is highlighted");
        assertNull(stub.currentGenerator.apply(new Product(2L, "Beta")),
                "Generator must return null for any item when nothing is highlighted");
    }

    // ── 2. setHighlighted → selected item returns "mdl-selected" ─────────────

    @Test
    void afterSetHighlighted_selectedItemReturnsPartName() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product alpha = new Product(1L, "Alpha");
        Product beta  = new Product(2L, "Beta");

        SelectionHighlighter<Product> sut = new SelectionHighlighter<>(stub);
        sut.setHighlighted(alpha);

        assertEquals("mdl-selected", stub.currentGenerator.apply(alpha),
                "Highlighted item must return 'mdl-selected'");
        assertNull(stub.currentGenerator.apply(beta),
                "Non-highlighted item must return null");
    }

    // ── 3. setHighlighted(null) clears the selection ──────────────────────────

    @Test
    void setHighlightedNull_generatorReturnsNullForAll() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product alpha = new Product(1L, "Alpha");

        SelectionHighlighter<Product> sut = new SelectionHighlighter<>(stub);
        sut.setHighlighted(alpha);
        sut.setHighlighted(null);   // clear

        assertNull(stub.currentGenerator.apply(alpha),
                "After clearing, previously-highlighted item must return null");
    }

    // ── 4. Key extractor — compares by extracted key, not object identity ─────

    @Test
    void keyExtractor_matchesByKey_notObjectIdentity() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product original  = new Product(42L, "Widget");
        Product sameId    = new Product(42L, "Widget");  // different instance, same id

        SelectionHighlighter<Product> sut =
                new SelectionHighlighter<>(stub, Product::id);
        sut.setHighlighted(original);

        assertEquals("mdl-selected", stub.currentGenerator.apply(sameId),
                "Items with the same key must be treated as the same row");
    }

    @Test
    void keyExtractor_differentKey_returnsNull() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product original  = new Product(1L, "Alpha");
        Product different = new Product(2L, "Beta");

        SelectionHighlighter<Product> sut =
                new SelectionHighlighter<>(stub, Product::id);
        sut.setHighlighted(original);

        assertNull(stub.currentGenerator.apply(different),
                "Item with a different key must not be highlighted");
    }

    // ── 5. Changing selection removes previous highlight ──────────────────────

    @Test
    void changingSelection_removesOldHighlight() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product alpha = new Product(1L, "Alpha");
        Product beta  = new Product(2L, "Beta");

        SelectionHighlighter<Product> sut = new SelectionHighlighter<>(stub);
        sut.setHighlighted(alpha);
        sut.setHighlighted(beta);

        assertNull(stub.currentGenerator.apply(alpha),
                "Old selection must be removed");
        assertEquals("mdl-selected", stub.currentGenerator.apply(beta),
                "New selection must be applied");
    }

    // ── 6. Custom part name is honoured ───────────────────────────────────────

    @Test
    void customPartName_isReturnedInsteadOfDefault() {
        CapturingListing<Product> stub = new CapturingListing<>();
        Product alpha = new Product(1L, "Alpha");

        SelectionHighlighter<Product> sut =
                new SelectionHighlighter<>(stub, null, "my-accent");
        sut.setHighlighted(alpha);

        assertEquals("my-accent", stub.currentGenerator.apply(alpha),
                "Custom part name must be used instead of 'mdl-selected'");
    }

    // ── 7. setPartNameGenerator called exactly once at construction ────────────

    @Test
    void construction_callsSetPartNameGeneratorExactlyOnce() {
        CapturingListing<Product> stub = new CapturingListing<>();
        new SelectionHighlighter<>(stub);

        assertEquals(1, stub.setGeneratorCallCount,
                "setPartNameGenerator must be called once at construction");
    }

    // ── 8. Every setHighlighted call reinstalls the generator ─────────────────

    @Test
    void setHighlighted_alwaysReinstallsGenerator() {
        // SelectionHighlighter always calls setPartNameGenerator on every highlight
        // so that Vaadin's DataCommunicator.reset() is triggered and all visible rows
        // re-evaluate their part names (fixes silent no-op on lazy CallbackDataProvider).
        CapturingListing<Product> stub = new CapturingListing<>();
        Product alpha = new Product(1L, "Alpha");
        Product beta  = new Product(2L, "Beta");

        SelectionHighlighter<Product> sut = new SelectionHighlighter<>(stub);
        int afterConstruction = stub.setGeneratorCallCount; // == 1

        sut.setHighlighted(alpha);
        assertEquals(afterConstruction + 1, stub.setGeneratorCallCount,
                "Generator must be reinstalled on every setHighlighted call");

        // Reinstalled generator returns correct part names
        assertEquals("mdl-selected", stub.currentGenerator.apply(alpha),
                "Reinstalled generator must return the correct part name for the selected item");
        assertNull(stub.currentGenerator.apply(beta),
                "Reinstalled generator must return null for other items");
    }

    @Test
    void setHighlighted_subsequentCalls_eachReinstallsGenerator() {
        CapturingListing<Product> stub = new CapturingListing<>();
        SelectionHighlighter<Product> sut = new SelectionHighlighter<>(stub);
        int afterConstruction = stub.setGeneratorCallCount;

        sut.setHighlighted(new Product(1L, "Alpha"));
        sut.setHighlighted(new Product(2L, "Beta"));

        // One reinstall per highlight call (two highlights → +2)
        assertEquals(afterConstruction + 2, stub.setGeneratorCallCount,
                "Each highlight must trigger one generator reinstall");
    }

    // ── 9. Integration: master(listing()) adds "mdl-master-grid" to Grid ──────

    @Test
    void masterListingWire_addsMdlMasterGridClassToGridComponent() {
        MasterDetailLayout<Product> layout =
                Components.masterDetail(Product.class)
                          .master(m -> m
                                  .listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                          .build();

        assertTrue(containsMdlMasterGrid(layout),
                "'mdl-master-grid' CSS class must be added to the vaadin-grid inside master listing");
    }

    // ── 10. Integration: SelectionHighlighter is installed (indirectly) ────────

    @Test
    void masterListingWithSelectionKey_dispatchSyncTriggersHighlight() {
        // This test wires the full pipeline: item click → dispatchSync → highlight.
        // We verify it by checking the MasterDetailLayout can dispatchSync without NPE.
        // (Full click simulation would require a browser session.)
        List<Product> syncLog = new java.util.ArrayList<>();

        MasterDetailLayout<Product> layout =
                Components.masterDetail(Product.class)
                          .withDetailSync(syncLog::add)
                          .master(m -> m
                                  .selectionKey(Product::id)
                                  .listing(l -> l.fetch((q, text, sort) -> Stream.empty())))
                          .build();

        Product p = new Product(1L, "Test");
        layout.dispatchSync(p);

        assertEquals(1, syncLog.size(), "dispatchSync must fire the detail-sync handler");
        assertEquals(p, syncLog.get(0));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Recursively searches the component tree for a {@link Grid} carrying {@code mdl-master-grid}. */
    private boolean containsMdlMasterGrid(Component root) {
        if (root instanceof Grid<?> g && g.getClassNames().contains("mdl-master-grid")) {
            return true;
        }
        return root.getChildren().anyMatch(this::containsMdlMasterGrid);
    }

    // ── Stub ItemListing ──────────────────────────────────────────────────────

    /**
     * Minimal {@link ItemListing} stub that:
     * <ul>
     *   <li>Captures every {@link #setPartNameGenerator} call (generator + call count).</li>
     *   <li>{@link #refreshItem} throws {@link UnsupportedOperationException} — kept for
     *       completeness but is no longer invoked by {@link SelectionHighlighter}
     *       (always-reinstall strategy makes refreshItem unnecessary).</li>
     *   <li>All other methods throw {@link UnsupportedOperationException} to catch
     *       accidental invocations.</li>
     * </ul>
     */
    @SuppressWarnings("all")
    private static class CapturingListing<T> implements ItemListing<T, String> {

        SerializableFunction<T, String> currentGenerator;
        int setGeneratorCallCount = 0;

        // ── Methods under test ───────────────────────────────────────────────

        @Override
        public void setPartNameGenerator(SerializableFunction<T, String> generator) {
            this.currentGenerator = generator;
            this.setGeneratorCallCount++;
        }

        /** Kept for interface completeness; no longer called by SelectionHighlighter. */
        @Override
        public void refreshItem(T item) {
            throw new UnsupportedOperationException("No item-identity support in stub");
        }

        @Override
        public Component getComponent() { return new Div(); }

        @Override public void refresh()                                        { /* no-op */ }

        // ── Stub-out all remaining ItemListing methods ───────────────────────

        @Override public DataProvider<T, ?>  getDataProvider()                 { throw new UnsupportedOperationException(); }
        @Override public DataProvider<T, ?>  getBackEndDataProvider()          { throw new UnsupportedOperationException(); }
        @Override public List<String>        getVisibleColumns()               { throw new UnsupportedOperationException(); }
        @Override public List<String>        getHiddenColumns()                { throw new UnsupportedOperationException(); }
        @Override public void setColumnVisible(String p, boolean v)            { throw new UnsupportedOperationException(); }
        @Override public Optional<String>    getColumnHeader(String p)         { throw new UnsupportedOperationException(); }
        @Override public void hideAllColumnsExcept(String p)                   { throw new UnsupportedOperationException(); }
        @Override public void restoreAllColumns()                              { throw new UnsupportedOperationException(); }
        @Override public void setFrozenMultiSelectCheckBoxColumn(boolean f)    { throw new UnsupportedOperationException(); }
        @Override public void setFrozenToEnd(String p, boolean f)             { throw new UnsupportedOperationException(); }
        @Override public void setScrollUsingUpDownKeys()                       { throw new UnsupportedOperationException(); }
        @Override public void setToggleableColumns()                           { throw new UnsupportedOperationException(); }
        @Override public List<Grid.Column<T>> getAllColumns()                  { throw new UnsupportedOperationException(); }
        @Override public void hide(String p)                                   { throw new UnsupportedOperationException(); }
        @Override public void addIndexColumn()                                 { throw new UnsupportedOperationException(); }
        @Override public void addIndexColumn(String p)                         { throw new UnsupportedOperationException(); }
        @Override public <V extends Component> Grid.Column<T> addComponentColumn(ValueProvider<T, V> cp) { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.shared.Registration addItemClickListener(com.vaadin.flow.component.ComponentEventListener<ItemClickEvent<T>> l) { throw new UnsupportedOperationException(); }
        @Override public com.vaadin.flow.shared.Registration addSelectionListener(com.vaadin.flow.data.selection.SelectionListener<Grid<T>, T> l) { throw new UnsupportedOperationException(); }
        @Override public void addHoverEffect(AttachEvent e, SerializableFunction<T, String> g) { throw new UnsupportedOperationException(); }
        @Override public void removeColumnByKey(String k)                      { throw new UnsupportedOperationException(); }
        @Override public void removeColumn(String p)                           { throw new UnsupportedOperationException(); }
        @Override public void removeColumns(List<String> ps)                   { throw new UnsupportedOperationException(); }
        @Override public void setColumnOrder(List<Grid.Column<T>> cols)        { throw new UnsupportedOperationException(); }
        @Override public void selectAll()                                      { throw new UnsupportedOperationException(); }
        @Override public boolean isItemDetailsVisible(T i)                    { throw new UnsupportedOperationException(); }
        @Override public void setItemDetailsVisible(T i, boolean v)           { throw new UnsupportedOperationException(); }
        @Override public void sort(List<ItemSort<String>> s)                   { throw new UnsupportedOperationException(); }
        @Override public void setSelectionMode(Selectable.SelectionMode m)     { throw new UnsupportedOperationException(); }
        @Override public Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getHeader() { throw new UnsupportedOperationException(); }
        @Override public Optional<ItemListingSection<String, ? extends ItemListingRow<String>>> getFooter() { throw new UnsupportedOperationException(); }
        @Override public boolean isEditable()                                  { throw new UnsupportedOperationException(); }
        @Override public Optional<T> isEditing()                               { throw new UnsupportedOperationException(); }
        @Override public void editItem(T i)                                    { throw new UnsupportedOperationException(); }
        @Override public void cancelEditing()                                  { throw new UnsupportedOperationException(); }
        @Override public boolean saveEditingItem()                             { throw new UnsupportedOperationException(); }
        @Override public void refreshEditingItem()                             { throw new UnsupportedOperationException(); }
        @Override public Editor<T> getEditor()                                 { throw new UnsupportedOperationException(); }
        @Override public void setMobileColumn(Renderer<T> r)                  { throw new UnsupportedOperationException(); }
        @Override public void setMobileColumn(ValueProvider<T, Component> c)  { throw new UnsupportedOperationException(); }
        @Override public void setMobileHeader(String h)                        { throw new UnsupportedOperationException(); }
        @Override public void setMobileHeader(Component c)                    { throw new UnsupportedOperationException(); }
        @Override public void addThemeVariants(GridVariant... v)              { throw new UnsupportedOperationException(); }
        @Override public void removeThemeVariants(GridVariant... v)           { throw new UnsupportedOperationException(); }
        @Override public void showMobileColumn(boolean m)                      { throw new UnsupportedOperationException(); }
        @Override public boolean isMobileColumnVisible()                       { throw new UnsupportedOperationException(); }
        @Override public void hideMobileColumn()                               { throw new UnsupportedOperationException(); }
        @Override public GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fc) { throw new UnsupportedOperationException(); }
        @Override public Optional<T> getItemAtIndex(int i)                    { throw new UnsupportedOperationException(); }
        @Override public void compact()                                        { throw new UnsupportedOperationException(); }
        @Override public void stretch()                                        { throw new UnsupportedOperationException(); }
        @Override public void wrapCellContent()                                { throw new UnsupportedOperationException(); }
        @Override public void setEmptyStateText(String t)                      { throw new UnsupportedOperationException(); }
        @Override public void setEmptyStateComponent(Component c)             { throw new UnsupportedOperationException(); }
        @Override public List<QuerySortOrder> getColumnSorts()                 { throw new UnsupportedOperationException(); }
        @Override public boolean isFrozen()                                    { throw new UnsupportedOperationException(); }
        @Override public void setFrozen(boolean f)                             { throw new UnsupportedOperationException(); }
        @Override public List<T> getAdditionalItems()                          { throw new UnsupportedOperationException(); }
        @Override public void addAdditionalItem(T i)                           { throw new UnsupportedOperationException(); }
        @Override public boolean removeAdditionalItem(T i)                    { throw new UnsupportedOperationException(); }
        @Override public void removeAdditionalItems()                          { throw new UnsupportedOperationException(); }
        @Override public void recalculateColumnWidths()                        { throw new UnsupportedOperationException(); }
        @Override public void scrollToIndex(int i)                             { /* no-op */ }
        @Override public void scrollToStart()                                  { /* no-op */ }
        @Override public void scrollToEnd()                                    { /* no-op */ }
        @Override public Selectable.SelectionMode getSelectionMode()           { throw new UnsupportedOperationException(); }
        @Override public Set<T> getSelectedItems()                             { throw new UnsupportedOperationException(); }
        @Override public Optional<T> getFirstSelectedItem()                    { throw new UnsupportedOperationException(); }
        @Override public void select(T i)                                      { throw new UnsupportedOperationException(); }
        @Override public void deselect(T i)                                    { throw new UnsupportedOperationException(); }
        @Override public void deselectAll()                                    { throw new UnsupportedOperationException(); }
        @Override public Registration addSelectionListener(SelectionListener<T> l) { throw new UnsupportedOperationException(); }
        @Override public Optional<T> getFirstItem()                            { throw new UnsupportedOperationException(); }
    }
}



