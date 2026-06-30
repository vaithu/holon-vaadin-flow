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
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.theme.lumo.LumoIcon;

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
public final class ListingBundle<T> extends Div {

    /**
     * An extra item contributed to the options menu via
     * {@link ListingBundleBuilder#withMenuAction(String, Runnable)} or
     * {@link ListingBundleBuilder#withMenuAction(VaadinIcon, String, Runnable)}.
     *
     * @param icon   optional icon shown to the left of the label ({@code null} = text-only)
     * @param label  display text of the menu item
     * @param action action to execute when the item is clicked
     */
    public record MenuAction(VaadinIcon icon, String label, Runnable action) {
        /** Convenience factory — no icon. */
        public static MenuAction of(String label, Runnable action) { return new MenuAction(null, label, action); }
        /** Convenience factory — with icon. */
        public static MenuAction of(VaadinIcon icon, String label, Runnable action) { return new MenuAction(icon, label, action); }
    }

    private final ItemListing<T, ?>                listing;
    private final ItemListingPaginationBar<T, ?>    bar;
    private final ItemListingPageSizeSelector<T, ?> selector;
    /** {@code null} when {@code search(String)} was not called on the builder. */
    private final TextField                         search;
    /** {@code null} when {@code withFilterPanel()} was not called on the builder. */
    private final DynamicFilterPanel<T>             filterPanel;
    private final List<MenuAction>                  menuActions;
    /** Optional import action; when {@code null} the Import menu item is hidden. */
    private final Runnable                          importAction;
    /** Optional export action; when {@code null} the Export menu item is hidden. */
    private final Runnable                          exportAction;
    private final String                            advancedSearchLabel;
    /** Explicit column keys supplied by the builder via {@code columns(...)}. */
    private final List<String>                      columnKeys;
    /**
     * When {@code true} (default) the filter dialog retains its values between
     * open/close cycles.  When {@code false} the panel is reset every time the
     * dialog is opened so the user always starts with a blank slate.
     */
    private final boolean                           retainFilterValues;
    /** Title for the GridHeader (null = legacy toolbar mode). */
    private final String                            gridHeaderTitle;
    /** Context actions to show in the GridHeader when rows are selected. */
    private final Component[]                       gridHeaderContextComponents;
    /** Cached standalone grid header component, created lazily. */
    private GridHeader                              gridHeader;
    /** Lazily-created dialog that hosts the {@link DynamicFilterPanel}. Created once on first call to {@link #toolbar()}. */
    private Dialog                                  filterDialog;
    /** Trigger component that opens the options menu. */
    private Component                               filterOptionsTrigger;
    /** The Advanced Search menu item, when rendered. */
    private MenuItem                                advancedSearchMenuItem;
    /** Small badge shown when filters are active. */
    private Span                                    filterIndicatorBadge;
    /** Prevent duplicate filter-change listener registration. */
    private boolean                                 filterIndicatorListenerRegistered;
    /** Whether the grid is currently in paginated mode (true) or default virtual scroll mode (false). Default is virtual scroll. */
    private boolean                                 paginatedMode;
    /** Cached toolbar div so repeated access returns the same instance. */
    private Div                                     toolbarDiv;
    /** Cached footer div so visibility can be toggled. */
    private Div                                     footerDiv;

    public ListingBundle(ItemListing<T, ?>                listing,
                         ItemListingPaginationBar<T, ?>    bar,
                         ItemListingPageSizeSelector<T, ?> selector,
                         TextField                         search,
                         DynamicFilterPanel<T>             filterPanel,
                         List<MenuAction>                  menuActions,
                         Runnable                          importAction,
                         Runnable                          exportAction,
                         String                            advancedSearchLabel,
                         boolean                           retainFilterValues,
                         String                            gridHeaderTitle,
                         Component[]                       gridHeaderContextComponents,
                         List<String>                      columnKeys,
                         boolean                           paginatedMode) {
        super();
        this.listing             = listing;
        this.bar                 = bar;
        this.selector            = selector;
        this.search              = search;
        this.filterPanel         = filterPanel;
        this.menuActions         = menuActions != null ? menuActions : List.of();
        this.importAction        = importAction;
        this.exportAction        = exportAction;
        this.advancedSearchLabel = advancedSearchLabel != null ? advancedSearchLabel : "Advanced Search";
        this.columnKeys          = columnKeys != null ? List.copyOf(columnKeys) : List.of();
        this.retainFilterValues  = retainFilterValues;
        this.gridHeaderTitle     = gridHeaderTitle;
        this.gridHeaderContextComponents = gridHeaderContextComponents != null ? gridHeaderContextComponents.clone() : null;
        this.paginatedMode       = paginatedMode;

        // When explicitly starting in paginated mode, switch the selector (which defaults to
        // virtual-scroll) so the first data fetch uses page-based offsets and fixed count.
        if (paginatedMode && selector != null) {
            selector.setPaginatedMode(true);
        }

        // Make the grid fill its container by default — avoids every view having
        // to set width/flex manually. The rule lives in pagination.css.
        listing.getComponent().addClassName("listing-bundle-grid");

        var header = header();
        if (header != null) {
            add(header);
        }
        add(toolbar(), listing.getComponent(), footer());

        Components.configure(this)
                .styleName("listing-bundle");

    }

    // ── Accessors ──────────────────────────────────────────────────────────

    /**
     * The underlying {@link ItemListing} ({@link BeanListing} or {@link PropertyListing}).
     * Cast to the specific subtype when you need listing-specific methods.
     */
    public ItemListing<T, ?> listing() { return listing; }

    /** The pagination bar. */
    public ItemListingPaginationBar<T, ?> bar() { return bar; }

    /** The underlying grid component. */
    @SuppressWarnings("unchecked")
    public Grid<T> grid() {
        return (Grid<T>) listing.getComponent();
    }

    /** The toolbar container. */
    public Div toolbar() {
        return createToolbar();
    }

    /** The footer container. */
    public Div footer() {
        return createFooter();
    }

    /** Optional access to the search field. */
    public Optional<TextField> getSearchOptional() {
        return Optional.ofNullable(search);
    }

    /** Optional access to the filter panel. */
    public Optional<DynamicFilterPanel<T>> getFilterPanelOptional() {
        return Optional.ofNullable(filterPanel);
    }

    /** The page-size selector (also owns the data binding in managed-fetch mode). */
    public ItemListingPageSizeSelector<T, ?> selector() { return selector; }

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

    // ── Layout helpers ─────────────────────────────────────────────────────

    /**
     * Returns the {@link GridHeader} when {@code gridHeader(String)} was configured.
     * The header integrates:
     * <ul>
     *   <li>Title (selection-aware — shows "N selected" when rows are selected)</li>
     *   <li>Default actions: page-size selector + search field + single options menu</li>
     *   <li>Context actions: Delete / Export (shown when rows are selected)</li>
     * </ul>
     *
     * <p>The returned {@code GridHeader} can be further customized (e.g. content breadcrumbs,
     * change heading level, set prefix icons). If the builder supplied context actions via
     * {@code ListingBundleBuilder#gridHeader(Component...)}, those actions are wired here and
     * shown automatically when rows are selected.</p>
     *
     * <p>When no grid header was configured, returns {@code null}.</p>
     */
    public GridHeader header() {
        if (gridHeaderTitle == null) return null;
        if (gridHeader != null) return gridHeader;

        // ── Single options menu (⚙ button + ContextMenu) ──────────────────
        // Using Button+ContextMenu instead of MenuBar avoids the MenuBar overflow
        // behaviour that collapses the cog icon into a "..." button when the header
        // row is tight on desktop.
        var menuButton = new Button(LumoIcon.COG.create());
        menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        menuButton.getElement().setAttribute("title", "Grid options");
        menuButton.addClassName("listing-header__options");

        filterIndicatorBadge = new Span();
        filterIndicatorBadge.addClassName("listing-filter-indicator");

        var menuTrigger = new Div(menuButton, filterIndicatorBadge);
        menuTrigger.addClassName("listing-filter-trigger");
        filterOptionsTrigger = menuTrigger;

        var subMenu = new ContextMenu(menuButton);
        subMenu.setOpenOnClick(true);

        updateFilterIndicator();
        registerFilterIndicatorListener();

        var managedColumns = managedColumns(listingGrid());

        // Only show column-management actions when there is more than one managed column.
        // When explicit columns were provided, they define the sort/show-hide universe.
        boolean hasColumnManagement = managedColumns.size() > 1;

        // Sort (opens dialog — indicated by "...")
        if (hasColumnManagement) {
            subMenu.addItem(createMenuItemContent(VaadinIcon.SORT, "Sort…"))
                    .addClickListener(e -> openSortDialog(listingGrid(), managedColumns));
        }

        // Refresh
        subMenu.addItem(createMenuItemContent(VaadinIcon.REFRESH, "Refresh"))
                .addClickListener(e -> listing.getDataProvider().refreshAll());

        // Import — only shown when an importAction was registered
        if (importAction != null) {
            subMenu.addItem(createMenuItemContent(VaadinIcon.UPLOAD, "Import"))
                    .addClickListener(e -> importAction.run());
        }

        // Export — only shown when an exportAction was registered
        if (exportAction != null) {
            subMenu.addItem(createMenuItemContent(VaadinIcon.DOWNLOAD, "Export"))
                    .addClickListener(e -> exportAction.run());
        }

        // ── Separator — only rendered when there is something above it (Import or Export) ──
        if (importAction != null || exportAction != null) {
            var sep1 = subMenu.addItem("");
            sep1.setEnabled(false);
            sep1.addClassName("listing-menu-separator");
        }

        // Reset column widths / Show-Hide — only when real columns exist
        if (hasColumnManagement) {
            subMenu.addItem(createMenuItemContent(VaadinIcon.ARROWS_LONG_H, "Reset column widths"))
                    .addClickListener(e -> managedColumns.forEach(col -> col.setAutoWidth(true)));

            subMenu.addItem(createMenuItemContent(VaadinIcon.EYE, "Show/Hide columns…"))
                    .addClickListener(e -> openShowHideColumnsDialog(managedColumns));
        }

        // Toggle paginated / default (virtual scroll) view.
        // Initial label reflects the current mode: offer to switch to the *other* mode.
        var viewToggleItem = subMenu.addItem(paginatedMode
                ? createMenuItemContent(VaadinIcon.LIST,  "Default view")
                : createMenuItemContent(VaadinIcon.TABLE, "Paginated view"));
        viewToggleItem.addClickListener(e -> {
            paginatedMode = !paginatedMode;
            if (footerDiv != null) {
                footerDiv.setVisible(paginatedMode);
            }
            if (selector != null) {
                selector.setPaginatedMode(paginatedMode);
            }
            if (paginatedMode) {
                viewToggleItem.removeAll();
                viewToggleItem.add(createMenuItemContent(VaadinIcon.LIST, "Default view"));
            } else {
                viewToggleItem.removeAll();
                viewToggleItem.add(createMenuItemContent(VaadinIcon.TABLE, "Paginated view"));
            }
        });

        // Filter panel (if configured) — single entry point, no duplicate button
        if (filterPanel != null) {
            var sep2 = subMenu.addItem("");
            sep2.setEnabled(false);
            sep2.addClassName("listing-menu-separator");
            advancedSearchMenuItem = subMenu.addItem(createMenuItemContent(VaadinIcon.FILTER, advancedSearchLabel + "…"));
            advancedSearchMenuItem.addClickListener(e -> getOrCreateFilterDialog().open());
            advancedSearchMenuItem.addClassName("listing-menu-item--filter");
            advancedSearchMenuItem.setCheckable(true);
        }

        // Extra menu actions — separator only added when there are items to separate
        if (!menuActions.isEmpty()) {
            var sepExtra = subMenu.addItem("");
            sepExtra.setEnabled(false);
            sepExtra.addClassName("listing-menu-separator");
            for (MenuAction action : menuActions) {
                var content = action.icon() != null
                        ? createMenuItemContent(action.icon(), action.label())
                        : new Span(action.label());
                subMenu.addItem(content).addClickListener(e -> action.action().run());
            }
        }

        // ── Default actions: [search] + [menu] ─────────────────────────────
        var actionComponents = new ArrayList<Component>();
        if (search != null) {
            search.addClassName("listing-header__search");
            actionComponents.add(search);
        }
        actionComponents.add(menuTrigger);

        gridHeader = Components.gridHeader(gridHeaderTitle)
                .listing((BeanListing<?>) listing)
                .styleName("listing-header")
                .build();

        gridHeader.setDefaultActions(actionComponents.toArray(Component[]::new));
        if (gridHeaderContextComponents != null) {
            gridHeader.setContextActions(gridHeaderContextComponents);
        }
        return gridHeader;
    }

    private Component createMenuItemContent(VaadinIcon icon, String text) {
        var iconComp = icon.create();
        iconComp.addClassName("listing-menu-icon");
        var span = new Span(text);
        var layout = new HorizontalLayout(iconComp, span);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(false);
        return layout;
    }

    private void registerFilterIndicatorListener() {
        if (filterPanel == null || filterIndicatorListenerRegistered) {
            return;
        }

        filterIndicatorListenerRegistered = true;
        filterPanel.addFilterChangeListener(event -> updateFilterIndicator());
    }

    private void updateFilterIndicator() {
        if (filterIndicatorBadge == null) {
            return;
        }

        boolean hasFilters = filterPanel != null && filterPanel.isAnyActive();
        filterIndicatorBadge.setVisible(hasFilters);
        filterIndicatorBadge.setText(hasFilters ? String.valueOf(Math.max(filterPanel.getActiveFilterCount(), 1)) : "");

        if (advancedSearchMenuItem != null) {
            advancedSearchMenuItem.setCheckable(true);
            advancedSearchMenuItem.setChecked(hasFilters);
            advancedSearchMenuItem.getElement().setAttribute("aria-pressed", String.valueOf(hasFilters));
            advancedSearchMenuItem.getElement().setAttribute("data-filter-active", String.valueOf(hasFilters));
            if (hasFilters) {
                advancedSearchMenuItem.addClassName("listing-menu-item--filter-active");
            } else {
                advancedSearchMenuItem.removeClassName("listing-menu-item--filter-active");
            }
        }

        if (filterOptionsTrigger != null) {
            filterOptionsTrigger.getElement().setAttribute("title", hasFilters
                    ? "Grid options, filters applied"
                    : "Grid options");
        }
    }

    @SuppressWarnings("unchecked")
    private Grid<T> listingGrid() {
        return (Grid<T>) listing.getComponent();
    }

    private void openSortDialog(Grid<T> grid, List<Grid.Column<T>> availableColumns) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Sort");
        dialog.setWidth("min(500px, 90vw)");

        var sortRows = new VerticalLayout();
        sortRows.setPadding(false);
        sortRows.setSpacing(true);
        sortRows.setWidthFull();

        // Each sort row: column combo + direction combo + remove button
        record SortRow(ComboBox<String> columnCombo, ComboBox<String> directionCombo, Div container) {}
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
            var colCombo = new ComboBox<String>("Column");
            colCombo.setItems(availableColumns.stream()
                    .map(Grid.Column::getKey)
                    .toList());
            colCombo.addClassName("flex-grow-1");

            var dirCombo = new ComboBox<String>("Direction");
            dirCombo.setItems("Ascending", "Descending");
            dirCombo.setValue(direction != null ? direction : "Ascending");
            dirCombo.addClassName("flex-grow-1");

            var removeBtn = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);

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
        List<GridSortOrder<T>> currentSort = grid.getSortOrder();
        if (currentSort != null && !currentSort.isEmpty()) {
            for (GridSortOrder<T> order : currentSort) {
                String colKey = order.getSorted().getKey();
                String dir = order.getDirection() == SortDirection.DESCENDING ? "Descending" : "Ascending";
                addRowWithValues.accept(colKey, dir);
            }
        } else {
            // Start with one empty row
            addRow.run();
        }

        refreshAvailableColumns.run();

        var addSortBtn = new Button("Add sort level", new Icon(VaadinIcon.PLUS));
        addSortBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addSortBtn.addClickListener(e -> addRow.run());

        var content = new VerticalLayout(sortRows, addSortBtn);
        content.setPadding(false);
        dialog.add(content);

        // Footer
        var applyBtn = new Button("Apply", e -> {
            List<GridSortOrder<T>> sortOrders = new ArrayList<>();
            for (var row : rows) {
                String colKey = row.columnCombo().getValue();
                String dir = row.directionCombo().getValue();
                if (colKey != null && !colKey.isBlank()) {
                    Grid.Column<T> col = grid.getColumnByKey(colKey);
                    if (col != null) {
                        SortDirection sortDir = "Descending".equals(dir)
                                ? SortDirection.DESCENDING : SortDirection.ASCENDING;
                        sortOrders.add(new GridSortOrder<>(col, sortDir));
                    }
                }
            }
            grid.sort(sortOrders);
            dialog.close();
        });
        applyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var clearBtn = new Button("Clear sort", e -> {
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
        });
        clearBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var closeBtn = new Button("Cancel", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(clearBtn, closeBtn, applyBtn);
        dialog.open();
    }

    private void openShowHideColumnsDialog(List<Grid.Column<T>> managedColumns) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Show/Hide Columns");
        dialog.setWidth("min(400px, 90vw)");

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        managedColumns.forEach(col -> {
            var cb = new Checkbox(col.getKey());
            cb.setValue(col.isVisible());
            cb.addValueChangeListener(e -> col.setVisible(e.getValue()));
            content.add(cb);
        });

        dialog.add(content);

        var closeBtn = new Button("Close", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    /**
     * Builds and caches the toolbar {@link Div}.
     *
     * <p>When {@code gridHeader(String)} is configured, all controls (page-size selector,
     * search, filter) are rendered inside the {@link #header()} — the toolbar remains part
     * of the composite but is hidden via CSS.</p>
     *
     * <p>When no gridHeader is set, the toolbar includes the page-size selector on the left
     * and a right-aligned group with search + filter options button.</p>
     */
    private Div createToolbar() {
        if (toolbarDiv != null) {
            return toolbarDiv;
        }

        var toolbarRow = new Div();
        toolbarRow.addClassName("listing-toolbar");

        // When gridHeader is active, everything is in the header — hide toolbar.
        if (gridHeaderTitle != null) {
            toolbarRow.setVisible(false);
            return toolbarRow;
        }

        if (selector != null) toolbarRow.add(selector);

        // Right group: search field + optional filter options button
        if (search != null || filterPanel != null) {
            var rightGroup = new Div();
            rightGroup.addClassName("listing-toolbar__right");

            if (search != null)  {
                rightGroup.add(search);
            }

            if (filterPanel != null) {
                var menuBar = new MenuBar();
                menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_ICON);
                menuBar.addClassName("listing-toolbar__options");

                var triggerItem = menuBar.addItem(new Icon(VaadinIcon.FILTER));
                triggerItem.getElement().setAttribute("title", "Search options");
                var subMenu = triggerItem.getSubMenu();

                // "Advanced Search" — opens the filter dialog
                advancedSearchMenuItem = subMenu.addItem(advancedSearchLabel);
                advancedSearchMenuItem.addClickListener(e -> getOrCreateFilterDialog().open());
                advancedSearchMenuItem.addClassName("listing-menu-item--filter");
                advancedSearchMenuItem.setCheckable(true);

                // Extra items contributed by the builder
                for (MenuAction action : menuActions) {
                    var content = action.icon() != null
                            ? createMenuItemContent(action.icon(), action.label())
                            : new Span(action.label());
                    subMenu.addItem(content).addClickListener(e -> action.action().run());
                }

                filterIndicatorBadge = new Span();
                filterIndicatorBadge.addClassName("listing-filter-indicator");

                var menuTrigger = new Div(menuBar, filterIndicatorBadge);
                menuTrigger.addClassName("listing-filter-trigger");
                filterOptionsTrigger = menuTrigger;

                updateFilterIndicator();
                registerFilterIndicatorListener();

                rightGroup.add(menuTrigger);
            }

            toolbarRow.add(rightGroup);
        }

        toolbarDiv = toolbarRow;
        return toolbarDiv;
    }

    /**
     * Lazily creates and caches the {@link Dialog} that hosts the {@link DynamicFilterPanel}.
     * Only created once; subsequent calls return the cached instance so filter state is
     * retained between open/close cycles (unless {@code retainFilterValues} is {@code false}).
     *
     * <p><strong>Data refresh contract:</strong> the {@link ItemListingPageSizeSelector} is
     * wired via {@code withFilterResetSignal(filterPanel)} during build, which registers a
     * {@link com.vaadin.flow.signals.Signal#effect Signal.effect} on the selector component.
     * Whenever the filter panel fires a filter-change event (on Apply <em>or</em> row removal),
     * the Signal updates and the lifecycle-bound effect calls {@code resetToPage1()}, which
     * resets the page offset and triggers {@code GridLazyDataView.refreshAll()} — re-fetching
     * with the current filter without any additional wiring here.</p>
     */
    private Dialog getOrCreateFilterDialog() {
        if (filterDialog != null) return filterDialog;

        filterDialog = new Dialog();
        filterDialog.setHeaderTitle(advancedSearchLabel);
        filterDialog.setWidth("min(600px, 95vw)");
        filterDialog.addClassName("listing-filter-dialog");

        // Wrap the panel so CSS can target it without shadow-DOM tricks
        var body = new Div(filterPanel);
        body.addClassName("listing-filter-dialog__body");
        filterDialog.add(body);

        // If retain-values is disabled, reset on every open so the user starts blank.
        // resetAll() fires FilterChangeEvent → queryFilterSignal updates → Signal.effect
        // fires resetToPage1() in the selector → grid refreshes with empty filter.
        if (!retainFilterValues) {
            filterDialog.addOpenedChangeListener(e -> {
                if (e.isOpened()) {
                    filterPanel.resetAll();
                }
            });
        }

        // The panel's "Apply filter" button: close the dialog and directly trigger a
        // page reset + data refresh. Although the selector's Signal.effect (wired via
        // withFilterResetSignal during build) handles the reactive case, we also call
        // selector.resetToPage1() directly here as a reliable, lifecycle-independent path.
        // resetToPage1() is a no-op if managedDataView is null (non-managed mode).
        filterPanel.addApplyListener(() -> {
            filterDialog.close();
            if (selector != null) {
                selector.resetToPage1();
            }
        });

        // Footer: only [Close] — "Clear all" in the panel actions bar already handles row reset.
        // Having both "Reset All" (footer) and "Clear all" (panel) was redundant.
        var closeBtn = new Button("Close", e -> filterDialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        filterDialog.getFooter().add(closeBtn);

        return filterDialog;
    }

    /**
     * Builds and caches the footer {@link Div} with the page-size selector on the left
     * and the pagination bar on the right.
     * Applies the {@code listing-footer} CSS class from core {@code pagination.css}.
     */
    private Div createFooter() {
        if (footerDiv != null) return footerDiv;
        footerDiv = new Div();
        footerDiv.addClassName("listing-footer");
        // Hidden by default — only shown when the user switches to paginated mode via the menu.
        footerDiv.setVisible(paginatedMode);
        if (selector != null) {
            footerDiv.add(selector);
        }
        footerDiv.add(bar);
        return footerDiv;
    }

    private List<Grid.Column<T>> managedColumns(Grid<T> grid) {
        var columnsByKey = new LinkedHashMap<String, Grid.Column<T>>();
        for (Grid.Column<T> column : grid.getColumns()) {
            String key = column.getKey();
            if (key != null) {
                columnsByKey.putIfAbsent(key, column);
            }
        }

        if (!columnKeys.isEmpty()) {
            var explicitColumns = new ArrayList<Grid.Column<T>>(columnKeys.size());
            for (String key : columnKeys) {
                Grid.Column<T> column = columnsByKey.get(key);
                if (column != null) {
                    explicitColumns.add(column);
                }
            }
            return explicitColumns;
        }

        return new ArrayList<>(columnsByKey.values());
    }
}

