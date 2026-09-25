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
package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.ItemListing;

import com.holonplatform.core.internal.utils.FormatUtils;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Assembled result of {@code ListingBundleBuilder#build()} or
 * {@code PropertyListingBundleBuilder#build()}.
 *
 * <p>Works identically for both {@link BeanListing} and {@link PropertyListing} — the
 * {@link #listing()} accessor returns the underlying {@link ItemListing} in both cases.</p>
 *
 * <p>{@code ListingBundle} extends {@link com.vaadin.flow.component.Composite Composite&lt;Div&gt;} and
 * self-assembles toolbar, grid, and footer in its constructor — just add the bundle directly to your
 * view layout:</p>
 *
 * <pre>{@code
 * // Bean listing — just add the bundle; toolbar + grid + footer are already assembled inside it
 * var bundle = Components.listing(Product.class)
 *     .columns("id", "name", "category", "price")
 *     .search("Search products…")
 *     .fetch((q, text, sort) -> service.fetch(q, text, sort))
 *     .build();
 *
 * add(bundle);  // that's all — no need to call toolbar()/grid()/footer() manually
 *
 * // Property listing
 * var bundle = Components.listing(NAME, CATEGORY, PRICE)
 *     .header(NAME, "Product Name")
 *     .search("Search…")
 *     .fetch((q, text, sort) -> service.fetch(q, text, sort))
 *     .build();
 *
 * add(bundle);
 *
 * // Only call toolbar()/grid()/footer() individually when you need to place the three
 * // pieces in different slots of your layout (e.g. toolbar inside an AppLayout header):
 * add(bundle.toolbar());
 * setContent(bundle.grid());
 * }</pre>
 *
 * @param <T> item type ({@code Product} for BeanListing; {@code PropertyBox} for PropertyListing)
 * @since 10.0.1
 */
@StyleSheet("context://layout.css")
@StyleSheet("context://utilities.css")
@StyleSheet("context://buttons.css")
@StyleSheet("context://toolbar.css")
@StyleSheet("context://menu.css")
@StyleSheet("context://mobile-grid.css")
@StyleSheet("context://master-detail-v2.css")
@StyleSheet("context://document-row-lit-renderer.css")
@StyleSheet("context://mobile-list-lit-renderer.css")
@StyleSheet("context://action-menu-lit-renderer.css")
public final class ListingBundle<T> extends Div {

    // Sort dialog constants
    private static final String SORT_DIR_ASCENDING = "Ascending";
    private static final String SORT_DIR_DESCENDING = "Descending";

    private final ItemListing<T, ?> listing;
    private final ItemListingPaginationBar<T, ?> bar;
    private final ItemListingPageSizeSelector<T, ?> selector;
    /**
     * {@code null} when {@code withFilterPanel()} was not called on the builder.
     */
    private final DynamicFilterPanel<T> filterPanel;
    /**
     * Cached plain title label, created lazily.
     */
    private Component headerTitle;
    /**
     * Whether the grid is currently in paginated mode (true) or default virtual scroll mode (false). Default is virtual scroll.
     */
    private boolean paginatedMode;
    /**
     * Cached toolbar so repeated access returns the same instance.
     */
    private GridToolbar gridToolbar;
    /**
     * Cached footer div so visibility can be toggled.
     */
    private Div footerDiv;
    /**
     * Shown when the dataset is genuinely empty (no search/filter active). {@code null} = feature disabled.
     */
    private final Empty emptyState;
    /**
     * Shown when search/filter is active but yields no results. {@code null} = feature disabled.
     */
    private final Empty noResultsState;

    public ListingBundle(ItemListing<T, ?> listing,
                         ItemListingPaginationBar<T, ?> bar,
                         ItemListingPageSizeSelector<T, ?> selector,
                         GridToolbar toolbar,
                         DynamicFilterPanel<T> filterPanel,
                         String title,
                         boolean paginatedMode,
                         Empty emptyState,
                         Empty noResultsState) {
        super();
        this.listing = listing;
        this.bar = bar;
        this.selector = selector;
        this.filterPanel = filterPanel;
        this.paginatedMode = paginatedMode;
        this.emptyState = emptyState;
        this.noResultsState = noResultsState;

        // When explicitly starting in paginated mode, switch the selector (which defaults to
        // virtual-scroll) so the first data fetch uses page-based offsets and fixed count.
        if (paginatedMode && selector != null) {
            selector.setPaginatedMode(true);
        }
        // Make the grid fill its container by default — avoids every view having
        // to set width/flex manually. The rule lives in pagination.css.
        listing.getComponent().addClassName("listing-bundle-grid");

        if (title != null) {
            headerTitle = Components.h2()
                    .text(title)
                    .styleName("listing-header-title")
                    .build();
            add(headerTitle);
        }

        if (emptyState != null || noResultsState != null) {
            if (emptyState != null && noResultsState != null) {
                // Both states: wrap them in a single container so the Grid's one empty-state
                // slot is occupied by the wrapper; onDataFetched() toggles each child's
                // visibility inside the wrapper to show the correct state.
                noResultsState.setVisible(false); // emptyState is shown by default
                var wrapper = Components.div()
                        .styleName("listing-empty-state-wrapper")
                        .add(emptyState, noResultsState)
                        .build();
                listing.setEmptyStateComponent(wrapper);
            } else {
                // Single state: set it directly; the Grid shows/hides it automatically.
                listing.setEmptyStateComponent(emptyState != null ? emptyState : noResultsState);
            }
        }

        gridToolbar = toolbar;
        if (selector != null) {
            gridToolbar.addComponentAsFirst(selector);
        }

        // Configure options menu - set what features are available
//        var grid = (Grid<T>) listing.getComponent();
        var managedColumns = getManagedColumns(listing);
        boolean hasColumnManagement = managedColumns.size() > 1;

        gridToolbar.enableOptionsMenu();
        gridToolbar.refresh(true);
        gridToolbar.sort(hasColumnManagement);
        gridToolbar.resetColumnWidths(hasColumnManagement);
        gridToolbar.showHideColumns(hasColumnManagement);
        gridToolbar.viewModeToggle(true);

        // Now add the actual implementations in the options menu
        if (gridToolbar.isRefreshEnabled()) {
            gridToolbar.addOptionsMenuAction(LineAwesomeIcon.SYNC_SOLID.create(), "Refresh",
                                             "grid_toolbar.options_refresh",
                                             () -> listing.getDataProvider().refreshAll());
        }

        if (gridToolbar.isSortEnabled()) {
            gridToolbar.addOptionsMenuAction(LineAwesomeIcon.SORT_SOLID.create(), "Sort…",
                                             "grid_toolbar.options_sort",
                                             () -> openSortDialog(listing, managedColumns));
        }

        if (gridToolbar.isResetColumnWidthsEnabled()) {
            gridToolbar.addOptionsMenuAction(LineAwesomeIcon.COMPRESS_ARROWS_ALT_SOLID.create(),
                                             "Reset column widths", "grid_toolbar.options_reset_widths",
                                             () -> managedColumns.forEach(col -> col.setAutoWidth(true)));
        }

        if (gridToolbar.isShowHideColumnsEnabled()) {
            gridToolbar.addOptionsMenuAction(LineAwesomeIcon.COLUMNS_SOLID.create(), "Show/Hide columns…",
                                             "grid_toolbar.options_show_hide",
                                             () -> openShowHideColumnsDialog(managedColumns));
        }

        if (gridToolbar.isViewModeToggleEnabled()) {
            gridToolbar.addOptionsMenuAction(LineAwesomeIcon.TH_LARGE_SOLID.create(), "Toggle view mode",
                                             "grid_toolbar.options_view_mode",
                                             this::toggleViewMode);
        }

        add(gridToolbar, listing.getComponent(), footer());

        Components.configure(this)
                .styleName("listing-bundle");

    }

    /**
     * Sets the grid header component, adding it as the first child of the ListingBundle.
     */
    public void setGridHeader(Component header) {
        if (header != null) {
            this.headerTitle = header;
            addComponentAsFirst(header);
        }
    }

    // ── Accessors ──────────────────────────────────────────────────────────

    /**
     * The underlying {@link ItemListing} ({@link BeanListing} or {@link PropertyListing}).
     * Cast to the specific subtype when you need listing-specific methods.
     */
    public ItemListing<T, ?> listing() {return listing;}

    /**
     * The pagination bar.
     */
    public ItemListingPaginationBar<T, ?> bar() {return bar;}

    /**
     * The underlying grid component.
     */
    public Grid<T> grid() {
        return listing.getGrid();
    }

    /**
     * The toolbar container.
     */
    public Div toolbar() {
        return gridToolbar;
    }

    /**
     * The footer container.
     */
    public Div footer() {
        return createFooter();
    }

    /**
     * Optional access to the search field, backed by the {@link GridToolbar}'s own search input.
     */
    public Optional<TextField> getSearchOptional() {
        return Optional.ofNullable(gridToolbar).map(GridToolbar::getSearchField);
    }

    /**
     * Optional access to the filter panel.
     */
    public Optional<DynamicFilterPanel<T>> getFilterPanelOptional() {
        return Optional.ofNullable(filterPanel);
    }

    /**
     * The page-size selector (also owns the data binding in managed-fetch mode).
     */
    public ItemListingPageSizeSelector<T, ?> selector() {return selector;}

    /**
     * Adds an item click listener to the grid and applies a pointer cursor CSS class
     * so the user gets visual feedback that rows are clickable.
     *
     * @param listener the click listener
     */
    public void addItemClickListener(com.vaadin.flow.component.ComponentEventListener<com.vaadin.flow.component.grid.ItemClickEvent<T>> listener) {
        listing.addItemClickListener(listener);
        listing.hasStyle().ifPresent(style -> style.addClassName("listing-bundle-clickable"));
    }

    // ── Empty state ────────────────────────────────────────────────────────

    /**
     * Called by {@link ItemListingPageSizeSelector} after each managed fetch with the
     * actual item count returned by the backend.
     *
     * <p>Determines which component to show:</p>
     * <ul>
     *   <li>{@code count > 0} — grid visible, both empty states hidden</li>
     *   <li>{@code count == 0} + search or filter active — {@code noResultsState} shown
     *       (falls back to {@code emptyState} if {@code noResultsState} is null)</li>
     *   <li>{@code count == 0} + no active search/filter — {@code emptyState} shown
     *       (falls back to {@code noResultsState} if {@code emptyState} is null)</li>
     * </ul>
     *
     * @param count number of items returned by the last fetch (0 = empty)
     */
    public void onDataFetched(int count) {
        if (emptyState == null && noResultsState == null) return;

        boolean isEmpty = count == 0;

        Empty toShow = null;
        if (isEmpty) {
            boolean filtered = isFiltered();
            if (filtered && noResultsState != null) {
                toShow = noResultsState;
            } else if (!filtered && emptyState != null) {
                toShow = emptyState;
            } else {
                // One state configured: show whichever is available
                toShow = emptyState != null ? emptyState : noResultsState;
            }
        }

        if (emptyState != null) emptyState.setVisible(emptyState == toShow);
        if (noResultsState != null) noResultsState.setVisible(noResultsState == toShow);
    }

    /**
     * Returns {@code true} when a search term is entered or at least one filter is active.
     * Used to differentiate "empty dataset" from "search / filter returned no results".
     */
    private boolean isFiltered() {
        boolean searchActive = gridToolbar != null && !gridToolbar.getSearchField().getValue().isBlank();
        boolean filterActive = filterPanel != null && filterPanel.isAnyActive();
        return searchActive || filterActive;
    }

    // ── Layout helpers ─────────────────────────────────────────────────────

    /**
     * Returns the plain title heading when {@code gridHeader(String)} was configured on the
     * builder, or {@code null} otherwise.
     *
     * <p>Search, filter, sort/column-management, view-mode toggle, and selection feedback
     * (context actions / bulk actions) all live in {@link #toolbar()} regardless of whether
     * a title is configured — this heading is a static label only.</p>
     */
    public Component header() {
        return headerTitle;
    }

    /**
     * Builds and caches the footer {@link Div} with the page-size selector on the left
     * and the pagination bar on the right.
     * Applies the {@code listing-footer} CSS class from core {@code pagination.css}.
     */
    private Div createFooter() {
        if (footerDiv != null) return footerDiv;
        footerDiv = Components.div().styleName("listing-footer").build();
        // Hidden by default — only shown when the user switches to paginated mode via the menu.
        footerDiv.setVisible(paginatedMode);
        if (selector != null) {
            footerDiv.add(selector);
        }
        footerDiv.add(bar);
        return footerDiv;
    }

    /**
     * Managed columns for Sort / Show-Hide, in the exact order they were added to the grid —
     * which already matches the builder's {@code columns(...)} order when one was configured.
     */
    private List<Grid.Column<T>> getManagedColumns(Grid<T> grid) {
        var columnsByKey = new LinkedHashMap<String, Grid.Column<T>>();
        for (Grid.Column<T> column : grid.getColumns()) {
            String key = column.getKey();
            if (key != null) {
                columnsByKey.putIfAbsent(key, column);
            }
        }
        return new ArrayList<>(columnsByKey.values());
    }

    /**
     * Managed columns for Sort / Show-Hide, in the exact order they were added to the grid —
     * which already matches the builder's {@code columns(...)} order when one was configured.
     */
    private List<Grid.Column<T>> getManagedColumns(ItemListing<T, ?> listing) {
        var columnsByKey = new LinkedHashMap<String, Grid.Column<T>>();
        listing.getAllColumns().forEach(column -> {
            String key = column.getKey();
            if (key != null) {
                columnsByKey.putIfAbsent(key, column);
            }
        });
        return new ArrayList<>(columnsByKey.values());
    }

    private void toggleViewMode() {
        paginatedMode = !paginatedMode;
        if (footerDiv != null) {
            footerDiv.setVisible(paginatedMode);
        }
        if (selector != null) {
            selector.setPaginatedMode(paginatedMode);
        }
    }

    private void openSortDialog(ItemListing<T, ?> listing, List<Grid.Column<T>> availableColumns) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(LocalizationProvider.localize("Sort", "listing_bundle.sort_dialog.title"));
        dialog.setWidth("min(500px, 90vw)");

        var sortRows = Components.verticalLayout()
                .padding(false)
                .spacing(true)
                .fullWidth()
                .build();

        // Each sort row: column combo + direction combo + remove button
        record SortRow(ComboBox<String> columnCombo, ComboBox<String> directionCombo, Div container) {
        }
        var rows = new ArrayList<SortRow>();
        final boolean[] refreshing = {false};

        // Refresh available columns in all combos to prevent duplicate selections
        Runnable refreshAvailableColumns = () -> {
            if (refreshing[0]) return;
            refreshing[0] = true;
            try {
                var selectedKeys = rows.stream()
                        .map(r -> r.columnCombo().getValue())
                        .filter(v -> v != null && !v.isBlank())
                        .toList();
                for (var r : rows) {
                    String currentValue = r.columnCombo().getValue();
                    var available = availableColumns.stream()
                            .map(Grid.Column::getKey)
                            .filter(key -> key.equals(currentValue) || !selectedKeys.contains(key))
                            .toList();
                    r.columnCombo().setItems(available);
                    r.columnCombo().setValue(currentValue);
                }
            } finally {
                refreshing[0] = false;
            }
        };

        // Creates a new sort row, optionally pre-filled with column key + direction
        java.util.function.BiConsumer<String, String> addRowWithValues = (colKey, direction) -> {
            var colCombo = new ComboBox<String>(
                    LocalizationProvider.localize("Column", "listing_bundle.sort_dialog.column_label"));
            colCombo.setItems(availableColumns.stream()
                                      .map(Grid.Column::getKey)
                                      .toList());
            colCombo.setItemLabelGenerator(item -> FormatUtils.toSentenceCase(item));
            colCombo.addClassName("flex-grow-1");

            var dirCombo = new ComboBox<String>(
                    LocalizationProvider.localize("Direction", "listing_bundle.sort_dialog.direction_label"));
            dirCombo.setItems(SORT_DIR_ASCENDING, SORT_DIR_DESCENDING);
            dirCombo.setItemLabelGenerator(item -> SORT_DIR_DESCENDING.equals(item)
                                                   ? LocalizationProvider.localize("Descending", "listing_bundle.sort_dialog.descending")
                                                   : LocalizationProvider.localize("Ascending", "listing_bundle.sort_dialog.ascending"));
            dirCombo.setValue(direction != null ? direction : SORT_DIR_ASCENDING);
            dirCombo.addClassName("flex-grow-1");

            var removeBtn = Components.button()
                    .icon(VaadinIcon.CLOSE)
                    .error()
                    .ariaLabel("Remove sort level", "listing_bundle.sort_dialog.remove_aria")
                    .tooltip("Remove sort level", "listing_bundle.sort_dialog.remove_tooltip")
                    .styleName("filter-panel__remove")
                    .build();

            // Direction + remove: always side by side (avoids lonely × on mobile)
            var dirGroup = ResponsiveDiv.flex()
                    .row().gapS().alignEnd().fullWidth()
                    .add(dirCombo, removeBtn)
                    .build();

            // Full sort row: stacked on mobile, single row on tablet+
            var row = ResponsiveDiv.flex()
                    .column().gapS().fullWidth()
                    .tablet().row().gapM().alignEnd().end()
                    .add(colCombo, dirGroup)
                    .build();

            var sortRow = new SortRow(colCombo, dirCombo, row);
            rows.add(sortRow);
            sortRows.add(row);

            // Set value after adding to rows list (refresh guard prevents loops)
            if (colKey != null) {
                refreshing[0] = true;
                try {
                    colCombo.setValue(colKey);
                } finally {
                    refreshing[0] = false;
                }
            }

            // Update available columns when selection changes (skip if refresh in progress)
            colCombo.addValueChangeListener(ev -> {
                if (!refreshing[0]) refreshAvailableColumns.run();
            });

            removeBtn.addClickListener(ev -> {
                rows.remove(sortRow);
                sortRows.remove(row);
                refreshAvailableColumns.run();
            });
        };

        Runnable addRow = () -> addRowWithValues.accept(null, null);

        // Pre-populate with current grid sort state
        List<GridSortOrder<T>> currentSort = listing.getGridSortOrders();
        if (currentSort != null && !currentSort.isEmpty()) {
            for (GridSortOrder<T> order : currentSort) {
                String colKey = order.getSorted().getKey();
                String dir = order.getDirection() == SortDirection.DESCENDING ? SORT_DIR_DESCENDING : SORT_DIR_ASCENDING;
                addRowWithValues.accept(colKey, dir);
            }
        } else {
            // Start with one empty row
            addRow.run();
        }

        refreshAvailableColumns.run();

        var addSortBtn = Components.button()
                .text("Add sort level", "listing_bundle.sort_dialog.add_level")
                .icon(VaadinIcon.PLUS)
                .tertiary()
                .onClick(e -> addRow.run())
                .build();

        var content = Components.verticalLayout()
                .padding(false)
                .add(sortRows, addSortBtn)
                .build();
        dialog.add(content);

        Grid<T> grid = listing.getGrid();

        // Footer
        var applyBtn = Components.button()
                .text("Apply", "listing_bundle.sort_dialog.apply")
                .primary()
                .onClick(e -> {
                    List<GridSortOrder<T>> sortOrders = new ArrayList<>();
                    for (var row : rows) {
                        String colKey = row.columnCombo().getValue();
                        String dir = row.directionCombo().getValue();
                        if (colKey != null && !colKey.isBlank()) {
                            Grid.Column<T> col = grid.getColumnByKey(colKey);
                            if (col != null) {
                                SortDirection sortDir = SORT_DIR_DESCENDING.equals(dir)
                                                        ? SortDirection.DESCENDING : SortDirection.ASCENDING;
                                sortOrders.add(new GridSortOrder<>(col, sortDir));
                            }
                        }
                    }
                    grid.sort(sortOrders);
                    dialog.close();
                })
                .build();

        var clearBtn = Components.button()
                .text("Clear sort", "listing_bundle.sort_dialog.clear")
                .tertiary()
                .onClick(e -> {
                    // Reset grid sorting to initial state
                    grid.sort(List.of());
                    // Reset all rows to empty state
                    refreshing[0] = true;
                    try {
                        for (var row : rows) {
                            row.columnCombo().clear();
                            row.directionCombo().setValue("Ascending");
                        }
                    } finally {
                        refreshing[0] = false;
                    }
                })
                .build();

        var closeBtn = UIUtils.Buttons.createCloseButton();
        closeBtn.addClickListener(e -> dialog.close());

        dialog.getFooter().add(clearBtn, closeBtn, applyBtn);
        dialog.open();
    }

    private void openShowHideColumnsDialog(List<Grid.Column<T>> managedColumns) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(LocalizationProvider.localize("Show/Hide Columns", "listing_bundle.columns_dialog.title"));
        dialog.setWidth("min(400px, 90vw)");

        var content = Components.verticalLayout()
                .padding(false)
                .spacing(true)
                .build();

        managedColumns.forEach(col -> {
            var cb = new Checkbox(FormatUtils.toSentenceCase(col.getKey()));
            cb.setValue(col.isVisible());
            cb.addValueChangeListener(e -> col.setVisible(e.getValue()));
            content.add(cb);
        });

        dialog.add(content);

        var closeBtn = UIUtils.Buttons.createCloseButton();
        closeBtn.setText(LocalizationProvider.localize("Close", "listing_bundle.columns_dialog.close"));
        closeBtn.addClickListener(e -> dialog.close());
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }
}

