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
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * @since 10.0.1
 * @see Components#listing(Property[])
 * @see Components#listing(PropertySet)
 */
public final class PropertyListingBundleBuilder {

    // ── Callback interfaces (mirrors ListingBundleBuilder) ─────────────────

    /**
     * Fetch callback that receives the Vaadin {@link Query}, search text, and a Holon
     * {@link QuerySort} derived from the grid's current sort state.
     */
    @FunctionalInterface
    public interface FetchCallback {
        Stream<PropertyBox> fetch(Query<PropertyBox, Void> query, String searchText, QuerySort sort);
    }

    /**
     * Fetch callback that additionally receives the {@link QueryFilter} committed by the
     * {@link DynamicFilterPanel} and a Holon {@link QuerySort}.
     */
    @FunctionalInterface
    public interface FilteredFetchCallback {
        Stream<PropertyBox> fetch(Query<PropertyBox, Void> query, String searchText,
                                  QueryFilter filter, QuerySort sort);
    }

    // ── Logger ─────────────────────────────────────────────────────────────

    private static final Logger log = LoggerFactory.getLogger(PropertyListingBundleBuilder.class);

    // ── Builder state ──────────────────────────────────────────────────────

    private final PropertySet<?>         propertySet;
    /** Per-property header overrides. Key = property (identity), Value = label. */
    private final Map<Property<?>, String> headers    = new LinkedHashMap<>();
    private List<Integer>                pageSizes    = List.of(10, 25, 50, 100);
    private int                          defaultPageSize = 10;
    private String                       searchPlaceholder;
    private boolean                      includeFilterPanel;
    private FetchCallback                fetchCallback;
    private FilteredFetchCallback        filteredFetchCallback;
    /** Extra items appended to the options menu. */
    private final List<ListingBundle.MenuAction> menuActions = new ArrayList<>();
    /** Optional import handler; when set, an "Import" item appears in the options menu. */
    private Runnable importAction;
    /** Optional export handler; when set, an "Export" item appears in the options menu. */
    private Runnable exportAction;
    /** Title for the GridHeader (null = legacy toolbar mode). */
    private String gridHeaderTitle;
    /** Context actions for the GridHeader. */
    private Component[] gridHeaderContextComponents;
    /** Label of the "Advanced Search" menu item (default: "Advanced Search"). */
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
     * Adds a search {@link TextField} to the toolbar with LAZY debounce (400 ms).
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
     * @param title the header title
     * @param contextActions optional components shown when rows are selected
     * @return this builder
     */
    public PropertyListingBundleBuilder gridHeader(String title, Component... contextActions) {
        this.gridHeaderTitle = Objects.requireNonNull(title, "title must not be null");
        this.gridHeaderContextComponents = contextActions != null ? Arrays.copyOf(contextActions, contextActions.length) : null;
        return this;
    }

    /**
     * Appends an extra item (text-only) to the options menu.
     *
     * @param label  display text (not null)
     * @param action action to run when clicked (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder withMenuAction(String label, Runnable action) {
        Objects.requireNonNull(label,  "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        this.menuActions.add(ListingBundle.MenuAction.of(label, action));
        return this;
    }

    /**
     * Appends an extra item (icon + text) to the options menu.
     *
     * @param icon   icon shown to the left of the label (not null)
     * @param label  display text (not null)
     * @param action action to run when clicked (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder withMenuAction(VaadinIcon icon, String label, Runnable action) {
        Objects.requireNonNull(icon,   "icon must not be null");
        Objects.requireNonNull(label,  "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        this.menuActions.add(ListingBundle.MenuAction.of(icon, label, action));
        return this;
    }

    /**
     * @deprecated Use {@link #withMenuAction(String, Runnable)} instead.
     */
    @Deprecated(since = "10.0.2", forRemoval = true)
    public PropertyListingBundleBuilder withFilterOption(String label, Runnable action) {
        return withMenuAction(label, action);
    }

    /**
     * Registers a handler for the <em>Import</em> menu item.
     * The item is only visible when a handler is provided.
     *
     * @param action action to run when the user clicks "Import" (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder importAction(Runnable action) {
        this.importAction = Objects.requireNonNull(action, "importAction must not be null");
        return this;
    }

    /**
     * Registers a handler for the <em>Export</em> menu item.
     * The item is only visible when a handler is provided.
     *
     * @param action action to run when the user clicks "Export" (not null)
     * @return this builder
     */
    public PropertyListingBundleBuilder exportAction(Runnable action) {
        this.exportAction = Objects.requireNonNull(action, "exportAction must not be null");
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

        // ── 3. Search field ─────────────────────────────────────────────────
        TextField search = null;
        if (searchPlaceholder != null) {
            search = new TextField();
            search.setPlaceholder(searchPlaceholder);
            search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
            search.addClassName("listing-toolbar__search");
        }

        // ── 4. Filter panel — uses the property set for introspection ────────
        DynamicFilterPanel<PropertyBox> panel = null;
        if (includeFilterPanel) {
            panel = DynamicFilterPanel.ofPropertySet(propertySet);
        }

        // ── 5. Selector ─────────────────────────────────────────────────────
        final TextField                        fSearch = search;
        final DynamicFilterPanel<PropertyBox>  fPanel  = panel;

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
            CallbackDataProvider.FetchCallback<PropertyBox, Void> wrappedFetch = q -> {
                String text = fSearch != null ? fSearch.getValue() : "";
                QueryFilter qf = fPanel != null ? fPanel.getQueryFilter().orElse(null) : null;
                QuerySort sort = toQuerySort(q.getSortOrders());
                if (filteredFetchCallback != null) {
                    return filteredFetchCallback.fetch((Query<PropertyBox, Void>) q, text, qf, sort);
                }
                return fetchCallback.fetch((Query<PropertyBox, Void>) q, text, sort);
            };
            sb.withLazyFetch(wrappedFetch, null);
        }

        if (search != null) sb.withSearchField(search);
        if (panel  != null) sb.withFilterResetSignal(panel);  // ← Signal.effect: lifecycle-aware reactive page reset

        ItemListingPageSizeSelector<PropertyBox, ?> selector = sb.build();

        return (ListingBundle<PropertyBox>) new ListingBundle(listing, bar, selector, search, panel,
                menuActions, importAction, exportAction,
                advancedSearchLabel, retainFilterValues, gridHeaderTitle, gridHeaderContextComponents, List.of(), paginatedMode);
    }

    /**
     * Converts Vaadin {@link QuerySortOrder} list to a Holon {@link QuerySort}.
     */
    private static QuerySort toQuerySort(java.util.List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return null;
        }
        java.util.List<QuerySort> sorts = new java.util.ArrayList<>(sortOrders.size());
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
}

