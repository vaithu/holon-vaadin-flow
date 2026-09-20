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

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.DynamicFilterPanel;
import com.iyensoft.vaadin.flow.components.Empty;
import com.iyensoft.vaadin.flow.components.GridHeader;
import com.iyensoft.vaadin.flow.components.GridToolbar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Fluent builder that assembles a {@link ListingBundle}{@code <PropertyBox>} — a fully
 * pre-wired set of {@link PropertyListing}, {@link ItemListingPaginationBar},
 * {@link ItemListingPageSizeSelector}, an optional search {@link TextField}, and an
 * optional {@link DynamicFilterPanel} — in a single chained call.
 *
 * <h3>Usage with explicit properties</h3>
 * <pre>{@code
 * var bundle = Components.listing(NAME, CATEGORY, PRICE, STATUS)
 *     .header(NAME,     "Product Name")
 *     .header(PRICE,    "Price (€)")
 *     .pageSizes(10, 25, 50)
 *     .search("Search products…")
 *     .fetch((q, text) -> service.fetch(q.getOffset(), q.getLimit(), text))
 *     .build();
 *
 * content(bundle.toolbar(), bundle.grid(), bundle.footer());
 * }</pre>
 *
 * <h3>Usage with a PropertySet</h3>
 * <pre>{@code
 * var bundle = Components.listing(PRODUCT_SET)
 *     .pageSizes(10, 25, 50)
 *     .withFilterPanel()
 *     .fetch((q, text, filter) -> {
 *         var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
 *         if (filter != null) q2.filter(filter);
 *         if (!text.isBlank()) q2.filter(NAME.containsIgnoreCase(text));
 *         return q2.stream(PRODUCT_SET);
 *     })
 *     .build();
 * }</pre>
 *
 * @see Components#listing(Property[])
 * @see Components#listing(PropertySet)
 * @since 10.0.1
 */
public final class PropertyListingBundleBuilder {

    // ── Callback interfaces (mirrors ListingBundleBuilder) ─────────────────

    /**
     * Fetch callback that receives the Vaadin {@link Query}, search text, and a Holon
     * {@link QuerySort} derived from the grid's current sort state.
     */
    @FunctionalInterface
    public interface FetchCallback extends java.io.Serializable {
        Stream<PropertyBox> fetch(Query<PropertyBox, Void> query, String searchText, QuerySort sort);
    }

    /**
     * Fetch callback that additionally receives the {@link QueryFilter} committed by the
     * {@link DynamicFilterPanel} and a Holon {@link QuerySort}.
     */
    @FunctionalInterface
    public interface FilteredFetchCallback extends java.io.Serializable {
        Stream<PropertyBox> fetch(Query<PropertyBox, Void> query, String searchText,
                                  QueryFilter filter, QuerySort sort);
    }

    // ── Logger ─────────────────────────────────────────────────────────────

    private static final Logger log = LoggerFactory.getLogger(PropertyListingBundleBuilder.class);

    // ── Builder state ──────────────────────────────────────────────────────

    private final PropertySet<?> propertySet;
    /**
     * Per-property header overrides. Key = property (identity), Value = label.
     */
    private final Map<Property<?>, String> headers = new LinkedHashMap<>();
    private List<Integer> pageSizes = List.of(10, 25, 50, 100);
    private int defaultPageSize = 10;
    private String searchPlaceholder;
    private boolean includeFilterPanel;
    private FetchCallback fetchCallback;
    private FilteredFetchCallback filteredFetchCallback;
    /**
     * Title for the GridHeader (null = legacy toolbar mode).
     */
    private String gridHeaderTitle;
    /**
     * Context actions for the GridHeader.
     */
    private Component[] gridHeaderContextComponents;
    /**
     * Label of the "Advanced Search" menu item (default: "Advanced Search").
     */
    private String advancedSearchLabel = "Advanced Search";
    /**
     * When {@code true} (default) the filter dialog retains its values between
     * open/close cycles.  Set to {@code false} via {@link #retainFilterValues(boolean)}
     * to reset the panel every time the dialog is opened.
     */
    private boolean retainFilterValues = true;
    /**
     * When {@code true} the bundle starts in paginated mode (pagination bar visible,
     * fixed-page fetch).  When {@code false} (default) the bundle starts in virtual-scroll
     * mode (infinite scroll, pagination bar hidden).
     */
    private boolean paginatedMode = false;
    /**
     * Per-row action column — populated by withEditAction / withDeleteAction / withRowAction.
     */
    private final List<ListingBundleConfigurer.RowAction<PropertyBox>> rowActions = new ArrayList<>();
    /**
     * When true, use the O(1) LitRenderer+ContextMenu/Sheet strategy instead of ComponentRenderer.
     */
    private boolean highPerformanceActions;
    /**
     * Optional post-processor applied to the built listing before assembling the bundle.
     */
    private Consumer<ItemListing<PropertyBox, ?>> postProcessor;
    private Consumer<com.iyensoft.vaadin.flow.components.builders.GridToolbarBuilder> toolbarCustomizer;
    /** Optional empty state shown when dataset is genuinely empty (no search/filter). */
    private Empty emptyState;
    /** Optional empty state shown when search/filter is active but yields no results. */
    private Empty noResultsState;

    // ── Package constructors ───────────────────────────────────────────────

    PropertyListingBundleBuilder(Property<?>... properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.propertySet = PropertySet.of(properties);
    }

    PropertyListingBundleBuilder(PropertySet<?> propertySet) {
        this.propertySet = Objects.requireNonNull(propertySet, "propertySet must not be null");
    }

    // ── Fluent API ─────────────────────────────────────────────────────────

    /**
     * Overrides the header label for a specific property column.
     *
     * <pre>{@code
     * .header(NAME,  "Product Name")
     * .header(PRICE, "Price (€)")
     * }</pre>
     *
     * @param property the property whose column header to override (not null)
     * @param label    display label
     * @return this builder
     */
    public PropertyListingBundleBuilder header(Property<?> property, String label) {
        Objects.requireNonNull(property, "property must not be null");
        this.headers.put(property, label);
        return this;
    }

    /**
     * Sets the page-size options shown in the selector dropdown.
     *
     * @param sizes one or more positive page sizes
     * @return this builder
     */
    public PropertyListingBundleBuilder pageSizes(Integer... sizes) {
        this.pageSizes = Arrays.asList(sizes);
        return this;
    }

    /**
     * Sets the initially selected page size (default: 10).
     *
     * @param size positive page size
     * @return this builder
     */
    public PropertyListingBundleBuilder defaultPageSize(int size) {
        if (size <= 0) throw new IllegalArgumentException("defaultPageSize must be > 0");
        this.defaultPageSize = size;
        return this;
    }

    /**
     * Adds a search {@link TextField} to the toolbar. Filtering triggers on Enter key press
     * or when the clear button empties the field.
     *
     * @param placeholder placeholder text
     * @return this builder
     */
    public PropertyListingBundleBuilder search(String placeholder) {
        this.searchPlaceholder = placeholder;
        return this;
    }

    /**
     * Adds a {@link DynamicFilterPanel} above the toolbar, introspecting the configured
     * property set. When a search field is also configured, the panel is integrated
     * into the toolbar and toggled via an "Advanced Search" menu item — do <em>not</em>
     * content {@code bundle.filterPanel()} to the layout separately in that case.
     * Use {@link #fetch(FilteredFetchCallback)} to receive the committed
     * {@link QueryFilter} on every data load.
     *
     * @return this builder
     */
    public PropertyListingBundleBuilder withFilterPanel() {
        this.includeFilterPanel = true;
        return this;
    }

    /**
     * Overrides the label of the "Advanced Search" toggle item in the options menu.
     * Defaults to {@code "Advanced Search"}.
     *
     * @param label display text (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder advancedSearchLabel(String label) {
        this.advancedSearchLabel = Objects.requireNonNull(label, "label must not be null");
        return this;
    }

    /**
     * Sets the context-action components for the {@link GridHeader}.
     *
     * @param components context action components
     * @return this builder
     */
    public PropertyListingBundleBuilder gridHeader(Component... components) {
        this.gridHeaderContextComponents = components != null ? Arrays.copyOf(components, components.length) : null;
        return this;
    }

    /**
     * Sets the GridHeader context-action components.
     *
     * @param components context action components
     * @return this builder
     */
    public PropertyListingBundleBuilder contextActions(Component... components) {
        return gridHeader(components);
    }

    /**
     * Adds a {@link GridHeader} with the given title and optional context actions.
     *
     * @param title          the header title
     * @param contextActions optional components shown when rows are selected
     * @return this builder
     */
    public PropertyListingBundleBuilder gridHeader(String title, Component... contextActions) {
        this.gridHeaderTitle = Objects.requireNonNull(title, "title must not be null");
        this.gridHeaderContextComponents = contextActions != null ? Arrays.copyOf(contextActions, contextActions.length) : null;
        return this;
    }

    /**
     * Controls whether the Advanced Search dialog retains its filter values between
     * open/close cycles (default: {@code true}).
     *
     * <p>When set to {@code false} the filter panel is reset every time the dialog is
     * opened, so the user always starts with a blank slate.</p>
     *
     * @param retain {@code true} to retain values (default), {@code false} to reset on each open
     * @return this builder
     */
    public PropertyListingBundleBuilder retainFilterValues(boolean retain) {
        this.retainFilterValues = retain;
        return this;
    }

    /**
     * Configures the bundle to start in <strong>paginated mode</strong>: a fixed page of
     * rows is shown at a time and the user navigates via the pagination bar in the footer.
     *
     * <p>When neither {@code paginated()} nor {@code virtualScroll()} is called the bundle
     * defaults to virtual-scroll mode.</p>
     *
     * @return this builder
     * @see #virtualScroll()
     */
    public PropertyListingBundleBuilder paginated() {
        this.paginatedMode = true;
        return this;
    }

    /**
     * Configures the bundle to start in <strong>virtual-scroll mode</strong> (the default):
     * infinite scroll, pagination bar hidden.
     *
     * @return this builder
     * @see #paginated()
     */
    public PropertyListingBundleBuilder virtualScroll() {
        this.paginatedMode = false;
        return this;
    }

    /**
     * Controls whether the bundle starts in paginated or virtual-scroll mode.
     *
     * @param paginated {@code true} for paginated mode, {@code false} (default) for virtual scroll
     * @return this builder
     */
    public PropertyListingBundleBuilder paginated(boolean paginated) {
        this.paginatedMode = paginated;
        return this;
    }

    /**
     * Registers the lazy-fetch callback (search text only, no filter panel).
     *
     * @param callback fetch callback (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder fetch(FetchCallback callback) {
        this.fetchCallback = Objects.requireNonNull(callback, "fetchCallback must not be null");
        return this;
    }

    /**
     * Registers the lazy-fetch callback that receives both search text and the
     * {@link DynamicFilterPanel}'s committed {@link QueryFilter}.
     *
     * @param callback filtered fetch callback (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder fetch(FilteredFetchCallback callback) {
        this.filteredFetchCallback = Objects.requireNonNull(callback, "filteredFetchCallback must not be null");
        return this;
    }

    // ── Row actions ────────────────────────────────────────────────────────

    /**
     * Registers an Edit action in the per-row actions column (⋮ trigger).
     * The column is added automatically when at least one action is registered.
     */
    public PropertyListingBundleBuilder withEditAction(Consumer<PropertyBox> onEdit) {
        rowActions.add(ListingBundleConfigurer.RowAction.of(VaadinIcon.EDIT, "Edit",
                                                            Objects.requireNonNull(onEdit, "onEdit must not be null")));
        return this;
    }

    /**
     * Registers a Delete action in the per-row actions column.
     *
     * @see #withEditAction(Consumer)
     */
    public PropertyListingBundleBuilder withDeleteAction(Consumer<PropertyBox> onDelete) {
        rowActions.add(ListingBundleConfigurer.RowAction.of(VaadinIcon.TRASH, "Delete",
                                                            Objects.requireNonNull(onDelete, "onDelete must not be null"), true));
        return this;
    }

    /**
     * Adds a custom action (with icon) to the per-row actions column.
     */
    public PropertyListingBundleBuilder withRowAction(VaadinIcon icon, String label,
                                                      Consumer<PropertyBox> handler) {
        rowActions.add(ListingBundleConfigurer.RowAction.of(icon,
                                                            Objects.requireNonNull(label, "label must not be null"),
                                                            Objects.requireNonNull(handler, "handler must not be null")));
        return this;
    }

    /**
     * Adds a text-only custom action to the per-row actions column.
     */
    public PropertyListingBundleBuilder withRowAction(String label, Consumer<PropertyBox> handler) {
        rowActions.add(ListingBundleConfigurer.RowAction.of(
                Objects.requireNonNull(label, "label must not be null"),
                Objects.requireNonNull(handler, "handler must not be null")));
        return this;
    }

    /**
     * Switches the action column to high-performance mode: a single shared
     * {@code ContextMenu} (desktop) / bottom {@code Sheet} (mobile touch) per grid
     * instead of one {@code MenuBar} per visible row.
     *
     * @see ListingBundleConfigurer#withHighPerformanceActions()
     */
    public PropertyListingBundleBuilder withHighPerformanceActions() {
        this.highPerformanceActions = true;
        return this;
    }

    // ── Post-processor ─────────────────────────────────────────────────────

    /**
     * Registers a callback invoked on the fully-built {@link ItemListing} after all
     * columns and the optional actions column have been added, but before the
     * {@link ListingBundle} wrapper is assembled.
     *
     * @see ListingBundleConfigurer#withListingPostProcessor(Consumer)
     */
    public PropertyListingBundleBuilder withListingPostProcessor(Consumer<ItemListing<PropertyBox, ?>> postProcessor) {
        this.postProcessor = Objects.requireNonNull(postProcessor, "postProcessor must not be null");
        return this;
    }

    /**
     * Registers a callback invoked on the internal {@link com.iyensoft.vaadin.flow.components.builders.GridToolbarBuilder}
     * before it is built, allowing toolbar-level customisations (e.g. {@code primaryAction},
     * {@code bulkAction}, {@code optionsMenuAction}) not covered by the fluent API.
     *
     * @see ListingBundleConfigurer#withToolbarCustomizer(Consumer)
     */
    public PropertyListingBundleBuilder withToolbarCustomizer(Consumer<com.iyensoft.vaadin.flow.components.builders.GridToolbarBuilder> customizer) {
        this.toolbarCustomizer = Objects.requireNonNull(customizer, "customizer must not be null");
        return this;
    }

    // ── Empty state ─────────────────────────────────────────────────────────

    /**
     * Sets a custom {@link Empty} component to display when the grid has no items at all
     * (dataset is genuinely empty — no search text or filter is active).
     *
     * @param emptyState the empty state component (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder emptyState(Empty emptyState) {
        this.emptyState = Objects.requireNonNull(emptyState, "emptyState must not be null");
        return this;
    }

    /**
     * Enables a default empty state shown when the grid has no items.
     * Uses a {@code VaadinIcon.INBOX} icon, "No items" title, and a short description.
     *
     * @return this builder
     */
    public PropertyListingBundleBuilder emptyState() {
        return emptyState(Empty.builder()
                .icon(new Icon(VaadinIcon.INBOX))
                .title(LocalizationProvider.localize("No items", "listing.empty_title"))
                .description(LocalizationProvider.localize("There are no items to display.", "listing.empty_description"))
                .build());
    }

    /**
     * Sets a custom {@link Empty} component shown when search/filter is active but yields no results.
     *
     * @param noResultsState the no-results state component (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder noResultsState(Empty noResultsState) {
        this.noResultsState = Objects.requireNonNull(noResultsState, "noResultsState must not be null");
        return this;
    }

    /**
     * Enables a default no-results state shown when search/filter is active but yields no records.
     * Uses a {@code VaadinIcon.SEARCH} icon, "No results found" title, and a hint.
     *
     * @return this builder
     */
    public PropertyListingBundleBuilder noResultsState() {
        return noResultsState(Empty.builder()
                .icon(new Icon(VaadinIcon.SEARCH))
                .title(LocalizationProvider.localize("No results found", "listing.no_results_title"))
                .description(LocalizationProvider.localize("No records match the current search or filter criteria. Try adjusting your search.", "listing.no_results_description"))
                .build());
    }

    // ── Build ──────────────────────────────────────────────────────────────

    /**
     * Assembles and returns the {@link ListingBundle}{@code <PropertyBox>}.
     *
     * @return the fully wired bundle
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ListingBundle<PropertyBox> build() {

        // ── 1. PropertyListing ──────────────────────────────────────────────
        var lb = PropertyListing.builder(propertySet);
        headers.forEach(lb::header);
        PropertyListing listing = lb.build();

        // ── 2. Pagination bar ───────────────────────────────────────────────
        var bar = new ItemListingPaginationBar<>(listing);

        // ── 3. Filter panel — uses the property set for introspection ────────
        DynamicFilterPanel<PropertyBox> panel = null;
        if (includeFilterPanel) {
            panel = DynamicFilterPanel.ofPropertySet(propertySet);
        }

        // ── 4. Toolbar — built up front so its own search field / filter dialog can be wired
        //      directly into the fetch closure and page-size selector below ──────────────────
        var toolbarBuilder = Components.gridToolbar(gridHeaderContextComponents);
        if (searchPlaceholder != null) {
            toolbarBuilder.searchPlaceholder(searchPlaceholder);
        }
        if (panel != null) {
            toolbarBuilder.filterPanel(panel);
        }
        if (toolbarCustomizer != null) {
            toolbarCustomizer.accept(toolbarBuilder);
        }
        GridToolbar toolbar = toolbarBuilder.build();

        // GridToolbar retains filter values across open/close by default; reset on every open when disabled.
        if (panel != null && !retainFilterValues) {
            final DynamicFilterPanel<PropertyBox> resetPanel = panel;
            toolbar.getFilterDialog().addOpenedChangeListener(e -> {
                if (e.isOpened()) {
                    resetPanel.resetAll();
                }
            });
        }

        // ── 5. Selector ─────────────────────────────────────────────────────
        final boolean fSearchEnabled = searchPlaceholder != null;
        final TextField fSearch = toolbar.getSearchField();
        final DynamicFilterPanel<PropertyBox> fPanel = panel;

        // Raw Builder to avoid PropertyListing's Property<?> wildcard issues
        ItemListingPageSizeSelector.Builder sb =
                (ItemListingPageSizeSelector.Builder) ItemListingPageSizeSelector.of(listing);
        sb.withOptions(new ArrayList<>(pageSizes));
        sb.withDefaultSize(defaultPageSize);
        sb.withPaginationBar(bar);

        if (fetchCallback != null || filteredFetchCallback != null) {
            // Misconfiguration guard: withFilterPanel() declared but only plain FetchCallback provided.
            // The plain callback receives (query, searchText) — the QueryFilter from the panel
            // is NOT forwarded. The grid will still refresh on Apply but data won't change.
            if (includeFilterPanel && filteredFetchCallback == null && fetchCallback != null) {
                log.warn("PropertyListingBundleBuilder: withFilterPanel() was configured but the fetch " +
                         "callback does not accept a QueryFilter. Use " +
                         ".fetch(FilteredFetchCallback) — i.e. .fetch((q, text, filter) -> ...) — " +
                         "so the DynamicFilterPanel's filter is passed to your query. " +
                         "Currently the grid will refresh on Apply but the filter is silently ignored.");
            }
            final FetchCallback fCb = this.fetchCallback;
            final FilteredFetchCallback fFiltCb = this.filteredFetchCallback;
            CallbackDataProvider.FetchCallback<PropertyBox, Void> wrappedFetch = q -> {
                String text = fSearchEnabled ? fSearch.getValue() : "";
                QueryFilter qf = fPanel != null ? fPanel.getQueryFilter().orElse(null) : null;
                QuerySort sort = toQuerySort(q.getSortOrders());
                if (fFiltCb != null) {
                    return fFiltCb.fetch((Query<PropertyBox, Void>) q, text, qf, sort);
                }
                return fCb.fetch((Query<PropertyBox, Void>) q, text, sort);
            };
            sb.withLazyFetch(wrappedFetch, null);
        }

        if (fSearchEnabled) sb.withSearchField(fSearch);
        if (panel != null) sb.withFilterResetSignal(panel);  // ← Signal.effect: lifecycle-aware reactive page reset

        ItemListingPageSizeSelector<PropertyBox, ?> selector = sb.build();

        // ── Actions column ────────────────────────────────────────────────────
        Grid<PropertyBox> grid = (Grid<PropertyBox>) listing.getComponent();
        if (!rowActions.isEmpty()) {
            if (highPerformanceActions) {
                addHighPerformanceActionColumn(grid, List.copyOf(rowActions));
            } else {
                addActionColumn(grid, List.copyOf(rowActions));
            }
        }

        // ── Post-processor ────────────────────────────────────────────────────
        if (postProcessor != null) {
            postProcessor.accept(listing);
        }

        ListingBundle<PropertyBox> bundle = (ListingBundle<PropertyBox>) new ListingBundle(listing, bar, selector, toolbar, panel,
                                                                              gridHeaderTitle, paginatedMode,
                                                                              emptyState, noResultsState);

        // Wire item-count listener for empty-state visibility after each fetch.
        if (selector != null && (emptyState != null || noResultsState != null)) {
            selector.setItemCountListener(bundle::onDataFetched);
        }

        return bundle;
    }

    /**
     * Converts Vaadin {@link QuerySortOrder} list to a Holon {@link QuerySort}.
     */
    private static QuerySort toQuerySort(List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return null;
        }
        List<QuerySort> sorts = new ArrayList<>(sortOrders.size());
        for (QuerySortOrder order : sortOrders) {
            String propertyName = order.getSorted();
            if (propertyName == null || propertyName.isBlank()) continue;
            QuerySort.SortDirection direction =
                    (order.getDirection() == com.vaadin.flow.data.provider.SortDirection.DESCENDING)
                    ? QuerySort.SortDirection.DESCENDING
                    : QuerySort.SortDirection.ASCENDING;
            sorts.add(QuerySort.of(PathProperty.create(propertyName, Object.class), direction));
        }
        if (sorts.isEmpty()) return null;
        return sorts.size() == 1 ? sorts.getFirst() : QuerySort.of(sorts);
    }

    // ── Action column helpers ──────────────────────────────────────────────────
    // Mirrors the same methods in AbstractListingBundleConfigurer for BeanListing.
    // Both operate on the underlying Grid<T> so the logic is identical.

    /**
     * Standard action column — one {@code MenuBar} per visible row (O(rows)).
     * Suitable for small or moderately-sized paginated grids.
     */
    private static void addActionColumn(Grid<PropertyBox> grid,
                                        List<ListingBundleConfigurer.RowAction<PropertyBox>> actions) {
        var col = grid.addColumn(new ComponentRenderer<>(item -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            menuBar.addClassName("action-column__menu");
            MenuItem trigger = menuBar.addItem(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
            SubMenu subMenu = trigger.getSubMenu();
            for (ListingBundleConfigurer.RowAction<PropertyBox> action : actions) {
                MenuItem actionItem = subMenu.addItem(action.label(),
                                                      e -> action.handler().accept(item));
                if (action.icon() != null) {
                    actionItem.addComponentAsFirst(new Icon(action.icon()));
                }
                if (action.destructive()) {
                    actionItem.getElement().setAttribute("theme", "error");
                }
            }
            return menuBar;
        }));
        col.setKey("__actions");
        col.setHeader("");
        col.setAutoWidth(true);
        col.setFlexGrow(0);
        col.setFrozenToEnd(true);
        col.addClassName("action-column");
    }

    /**
     * High-performance action column — a single {@code ContextMenu} (desktop) or
     * bottom {@code Sheet} (mobile touch) per grid regardless of row count (O(1)).
     * Use via {@link #withHighPerformanceActions()} for high-concurrency deployments.
     */
    private static void addHighPerformanceActionColumn(Grid<PropertyBox> grid,
                                                       List<ListingBundleConfigurer.RowAction<PropertyBox>> actions) {
        // Correct Vaadin 25 LitRenderer pattern: one button + one withFunction per action.
        // @click="${action0}" — direct binding, NO arrow-function wrapper.
        // Arrow functions look up the name as a closure variable (undefined = does nothing).
        StringBuilder tpl = new StringBuilder("<span class=\"action-column__wrap\">");
        for (int i = 0; i < actions.size(); i++) {
            ListingBundleConfigurer.RowAction<PropertyBox> a = actions.get(i);
            if (a.icon() != null) {
                String iconName = "vaadin:" + a.icon().name().toLowerCase().replace('_', '-');
                String btnTheme = a.destructive() ? "icon tertiary error" : "icon tertiary";
                tpl.append(String.format(
                        "<vaadin-button theme=\"%s\" @click=\"${action%d}\" title=\"%s\">" +
                        "<vaadin-icon icon=\"%s\"></vaadin-icon></vaadin-button>",
                        btnTheme, i, a.label(), iconName));
            } else {
                String btnTheme = a.destructive() ? "tertiary error" : "tertiary";
                tpl.append(String.format(
                        "<vaadin-button theme=\"%s\" @click=\"${action%d}\">%s</vaadin-button>",
                        btnTheme, i, a.label()));
            }
        }
        tpl.append("</span>");

        LitRenderer<PropertyBox> litRenderer = LitRenderer.of(tpl.toString());
        for (int i = 0; i < actions.size(); i++) {
            final ListingBundleConfigurer.RowAction<PropertyBox> action = actions.get(i);
            litRenderer = litRenderer.withFunction("action" + i,
                                                   item -> action.handler().accept(item));
        }

        var col = grid.addColumn(litRenderer);
        col.setKey("__actions");
        col.setHeader("");
        col.setAutoWidth(true);
        col.setFlexGrow(0);
        col.setFrozenToEnd(true);
        col.addClassName("action-column");
    }
}

