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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * Fluent builder that assembles a {@link ListingBundle} — a fully pre-wired set of
 * {@link BeanListing}, {@link ItemListingPaginationBar}, {@link ItemListingPageSizeSelector},
 * an optional search {@link TextField}, and an optional {@link DynamicFilterPanel} — in a
 * single chained call.
 *
 * <h3>Minimal usage (search + pagination, no filter panel)</h3>
 * <pre>{@code
 * var bundle = Components.listing(Product.class)
 *     .columns("id", "name", "category", "price")
 *     .pageSizes(10, 25, 50)
 *     .search("Search by name or category…")
 *     .fetch((q, text) -> service.fetch(q.getOffset(), q.getLimit(), text))
 *     .build();
 *
 * add(bundle.toolbar(),   // [Show 10▾ entries]    [🔍 Search…]
 *     bundle.grid(),
 *     bundle.footer());   // [Previous] [1] [2] [Next]
 * }</pre>
 *
 * <h3>With DynamicFilterPanel</h3>
 * <pre>{@code
 * var bundle = Components.listing(Product.class)
 *     .columns("id", "name", "category", "price")
 *     .pageSizes(10, 25, 50)
 *     .search("Quick search…")
 *     .withFilterPanel()
 *     .fetch((q, text, filter) -> {
 *         var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
 *         if (filter != null) q2.filter(filter);
 *         if (!text.isBlank()) q2.filter(PRODUCT_NAME.containsIgnoreCase(text));
 *         return q2.stream(BeanProjection.of(Product.class));
 *     })
 *     .build();
 *
 * // The filter panel is integrated into the toolbar — no separate add() needed.
 * // A ⊟ Filter button next to search reveals the panel as "Advanced Search".
 * add(bundle.toolbar(),   // [Show 10▾ entries] [🔍 Quick search…] [⊟]
 *     bundle.grid(),
 *     bundle.footer());
 * }</pre>
 *
 * <h3>Accessing individual components after build</h3>
 * <pre>{@code
 * BeanListing<Product>              listing = bundle.listing();
 * ItemListingPaginationBar<Product,?> bar   = bundle.bar();
 * TextField                          search = bundle.search();  // null if not configured
 * DynamicFilterPanel<Product>       panel   = bundle.filterPanel(); // null if not configured
 * }</pre>
 *
 * @param <T> bean item type
 * @since 10.0.1
 * @see Components#listing(Class)
 * @see ListingBundle
 */
public final class ListingBundleBuilder<T> {

    private static final Logger log = LoggerFactory.getLogger(ListingBundleBuilder.class);

    // ── Callback interfaces ────────────────────────────────────────────────

    /**
     * Fetch callback that receives the Vaadin {@link Query} and the current search text.
     * Used when the bundle has a search field but no {@link DynamicFilterPanel}.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FetchCallback<T> {
        /**
         * Fetches items for the given query and search text.
         *
         * @param query      Vaadin query carrying offset and limit
         * @param searchText current value of the search field (never null; empty string when blank)
         * @return stream of matching items
         */
        Stream<T> fetch(Query<T, Void> query, String searchText);
    }

    /**
     * Fetch callback that receives the Vaadin {@link Query}, the current search text,
     * and the {@link QueryFilter} committed by the {@link DynamicFilterPanel}.
     * Used when {@link #withFilterPanel()} is enabled.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FilteredFetchCallback<T> {
        /**
         * Fetches items for the given query, search text, and dynamic filter.
         *
         * @param query      Vaadin query carrying offset and limit
         * @param searchText current value of the search field (empty string when none configured)
         * @param filter     committed {@link QueryFilter} from the filter panel, or {@code null}
         * @return stream of matching items
         */
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter);
    }

    // ── Builder state ──────────────────────────────────────────────────────

    private final Class<T>          beanType;
    private List<String>                columns         = List.of();
    private final Map<String, Localizable> headers      = new LinkedHashMap<>();
    private List<Integer>           pageSizes            = List.of(10, 25, 50, 100);
    private int                     defaultPageSize      = 10;
    private Localizable             searchLocalizable;
    private boolean                 includeFilterPanel;
    private FetchCallback<T>        fetchCallback;
    private FilteredFetchCallback<T> filteredFetchCallback;
    /** Extra items appended to the options sub-menu in integrated search+filter mode. */
    private final List<ListingBundle.FilterOption> filterOptions = new ArrayList<>();
    /** Label of the "Advanced Search" menu item (default: "Advanced Search"). */
    private String advancedSearchLabel = "Advanced Search";
    /**
     * When {@code true} (default) the filter dialog retains its values between
     * open/close cycles.  Set to {@code false} via {@link #retainFilterValues(boolean)}
     * to reset the panel every time the dialog is opened.
     */
    private boolean retainFilterValues = true;

    // ── Package constructor (use Components.listing()) ─────────────────────

    ListingBundleBuilder(Class<T> beanType) {
        this.beanType = Objects.requireNonNull(beanType, "beanType must not be null");
    }

    // ── Fluent API ─────────────────────────────────────────────────────────

    /**
     * Sets the visible columns in display order.
     *
     * @param cols column property names
     * @return this builder
     */
    public ListingBundleBuilder<T> columns(String... cols) {
        this.columns = Arrays.asList(cols);
        return this;
    }

    /**
     * Overrides the header label for a specific column using a plain string.
     *
     * @param column property name
     * @param label  display label
     * @return this builder
     */
    public ListingBundleBuilder<T> header(String column, String label) {
        this.headers.put(column, Localizable.builder().message(label).build());
        return this;
    }

    /**
     * Overrides the header label for a specific column using a {@link Localizable} (full i18n control).
     *
     * <pre>{@code
     * .header("price", Localizable.builder().message("Price (€)").messageCode("product.price.header").build())
     * }</pre>
     *
     * @param column     property name
     * @param localizable localizable header descriptor
     * @return this builder
     */
    public ListingBundleBuilder<T> header(String column, Localizable localizable) {
        this.headers.put(column, Objects.requireNonNull(localizable, "localizable must not be null"));
        return this;
    }

    /**
     * Overrides the header label for a specific column with a default message and a message code.
     *
     * <pre>{@code
     * .header("price", "Price (€)", "product.price.header")
     * }</pre>
     *
     * @param column         property name
     * @param defaultLabel   fallback label when no localization is found
     * @param messageCode    i18n message code
     * @return this builder
     */
    public ListingBundleBuilder<T> header(String column, String defaultLabel, String messageCode) {
        this.headers.put(column, Localizable.builder().message(defaultLabel).messageCode(messageCode).build());
        return this;
    }

    /**
     * Sets the page-size options shown in the selector dropdown.
     *
     * @param sizes one or more positive page sizes
     * @return this builder
     */
    public ListingBundleBuilder<T> pageSizes(Integer... sizes) {
        this.pageSizes = Arrays.asList(sizes);
        return this;
    }

    /**
     * Sets the initially selected page size (default: 10).
     *
     * @param size positive page size
     * @return this builder
     */
    public ListingBundleBuilder<T> defaultPageSize(int size) {
        if (size <= 0) throw new IllegalArgumentException("defaultPageSize must be > 0");
        this.defaultPageSize = size;
        return this;
    }

    /**
     * Adds a search {@link TextField} to the toolbar with a plain-string placeholder.
     *
     * <p>The field is configured with a search icon prefix and {@code ValueChangeMode.LAZY}
     * (400 ms debounce). Its value is read fresh on every fetch via the closure passed to
     * {@link #fetch(FetchCallback)} or {@link #fetch(FilteredFetchCallback)}.</p>
     *
     * @param placeholder placeholder text shown when the field is empty
     * @return this builder
     */
    public ListingBundleBuilder<T> search(String placeholder) {
        this.searchLocalizable = Localizable.builder().message(placeholder).build();
        return this;
    }

    /**
     * Adds a search {@link TextField} to the toolbar using a {@link Localizable} placeholder
     * (full i18n control).
     *
     * <pre>{@code
     * .search(Localizable.builder().message("Search…").messageCode("listing.search.placeholder").build())
     * }</pre>
     *
     * @param localizable localizable placeholder descriptor (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> search(Localizable localizable) {
        this.searchLocalizable = Objects.requireNonNull(localizable, "localizable must not be null");
        return this;
    }

    /**
     * Adds a search {@link TextField} to the toolbar with a default placeholder and a message code.
     *
     * <pre>{@code
     * .search("Search products…", "listing.search.placeholder")
     * }</pre>
     *
     * @param defaultPlaceholder fallback text when no localization is found
     * @param messageCode        i18n message code
     * @return this builder
     */
    public ListingBundleBuilder<T> search(String defaultPlaceholder, String messageCode) {
        this.searchLocalizable = Localizable.builder().message(defaultPlaceholder).messageCode(messageCode).build();
        return this;
    }

    /**
     * Adds a {@link DynamicFilterPanel} above the toolbar.
     *
     * <p>When a search field is also configured (via {@link #search(String)}), the filter
     * panel is automatically integrated into the toolbar: it is hidden by default and
     * revealed via an "Advanced Search" option in a menu button next to the search field.
     * In this case use only {@code bundle.toolbar()} — do <strong>not</strong> add
     * {@code bundle.filterPanel()} to the layout separately.</p>
     *
     * <p>The panel's filter-change events automatically reset to page 1 via the
     * selector's {@code withFilterReset()} integration. Use
     * {@link #fetch(FilteredFetchCallback)} so the fetch callback receives the
     * committed {@link QueryFilter}.</p>
     *
     * @return this builder
     */
    public ListingBundleBuilder<T> withFilterPanel() {
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
    public ListingBundleBuilder<T> advancedSearchLabel(String label) {
        this.advancedSearchLabel = Objects.requireNonNull(label, "label must not be null");
        return this;
    }

    /**
     * Appends an extra item to the options menu that appears next to the search field
     * when both search and filter panel are configured.
     *
     * <pre>{@code
     * .withFilterOption("Export results", () -> exportService.export(bundle.listing()))
     * }</pre>
     *
     * @param label  display text of the menu item (not null)
     * @param action action to run when the item is clicked (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> withFilterOption(String label, Runnable action) {
        Objects.requireNonNull(label,  "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        this.filterOptions.add(new ListingBundle.FilterOption(label, action));
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
    public ListingBundleBuilder<T> retainFilterValues(boolean retain) {
        this.retainFilterValues = retain;
        return this;
    }

    /**
     * Registers the lazy-fetch callback for listings that have a search field
     * but no {@link DynamicFilterPanel}.
     *
     * <p>The {@code searchText} parameter contains the current search field value
     * (empty string when the field is blank or not configured).</p>
     *
     * <pre>{@code
     * .fetch((q, text) -> service.fetch(q.getOffset(), q.getLimit(), text))
     * }</pre>
     *
     * @param callback fetch callback (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> fetch(FetchCallback<T> callback) {
        this.fetchCallback = Objects.requireNonNull(callback, "fetchCallback must not be null");
        return this;
    }

    /**
     * Registers the lazy-fetch callback for listings that have both a search field
     * and a {@link DynamicFilterPanel}.
     *
     * <p>Both the search text and the committed {@link QueryFilter} are passed on
     * every fetch so the backend can apply both constraints in one query.</p>
     *
     * <pre>{@code
     * .fetch((q, text, filter) -> {
     *     var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
     *     if (filter != null) q2.filter(filter);
     *     if (!text.isBlank()) q2.filter(NAME.containsIgnoreCase(text));
     *     return q2.stream(BeanProjection.of(Product.class));
     * })
     * }</pre>
     *
     * @param callback filtered fetch callback (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> fetch(FilteredFetchCallback<T> callback) {
        this.filteredFetchCallback = Objects.requireNonNull(callback, "filteredFetchCallback must not be null");
        return this;
    }

    // ── Build ──────────────────────────────────────────────────────────────

    /**
     * Assembles and returns the {@link ListingBundle}.
     *
     * <p>Internally this method:</p>
     * <ol>
     *   <li>Builds the {@link BeanListing} (column visibility + headers).</li>
     *   <li>Creates the {@link ItemListingPaginationBar}.</li>
     *   <li>Creates the search {@link TextField} if {@link #search(String)} was called.</li>
     *   <li>Creates the {@link DynamicFilterPanel} if {@link #withFilterPanel()} was called.</li>
     *   <li>Builds the {@link ItemListingPageSizeSelector}, wiring the pagination bar,
     *       the look-ahead fetch callback (zero {@code COUNT(*)} queries), the search
     *       field debounce, and the filter panel reset — all automatically.</li>
     * </ol>
     *
     * @return the fully wired {@link ListingBundle}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ListingBundle<T> build() {

        // ── 1. Listing ──────────────────────────────────────────────────────
        var lb = BeanListing.builder(beanType);
        if (!columns.isEmpty()) lb.visibleColumns(columns);
        headers.forEach((col, loc) -> lb.header(col, loc));
        BeanListing<T> listing = lb.build();

        // ── 1b. Auto-style numeric columns ─────────────────────────────────────
        // Detect bean properties whose Java type is numeric and apply the
        // col-numeric CSS class via Column.setClassNameGenerator so the grid
        // renders them right-aligned in a monospace / tabular-nums font.
        // Uses listing.getAllColumns() (ItemListing API) — no Grid cast needed.
        if (!columns.isEmpty()) {
            for (String colKey : columns) {
                if (isNumericBeanProperty(beanType, colKey)) {
                    listing.getAllColumns().stream()
                            .filter(col -> colKey.equals(col.getKey()))
                            .findFirst()
                            .ifPresent(col -> col.setPartNameGenerator(item -> "col-numeric"));
                }
            }
        }

        // ── 2. Pagination bar ───────────────────────────────────────────────
        // new ItemListingPaginationBar<>(listing) lets the compiler infer the
        // concrete property type P from BeanListing<T>, avoiding wildcard capture.
        var bar = new ItemListingPaginationBar<>(listing);

        // ── 3. Search field ─────────────────────────────────────────────────
        TextField search = null;
        if (searchLocalizable != null) {
            search = new TextField();
            String ph = LocalizationProvider.localize(searchLocalizable)
                    .orElseGet(() -> searchLocalizable.getMessage() != null ? searchLocalizable.getMessage() : "");
            search.setPlaceholder(ph);
            search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
            // CSS class drives the flex width defined in pagination.css
            search.addClassName("listing-toolbar__search");
        }

        // ── 4. Filter panel ─────────────────────────────────────────────────
        DynamicFilterPanel<T> panel = null;
        if (includeFilterPanel) {
            panel = DynamicFilterPanel.of(beanType);
        }

        // ── 5. Selector — capture finals for lambda ─────────────────────────
        final TextField           fSearch = search;
        final DynamicFilterPanel<T> fPanel = panel;

        // Use raw Builder to avoid P-wildcard capture issues when passing bar.
        // The actual type safety is guaranteed by construction: all three objects
        // (listing, bar, selector) are created from the same BeanListing<T> instance.
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
                log.warn("ListingBundleBuilder: withFilterPanel() was configured but the fetch " +
                         "callback does not accept a QueryFilter. Use " +
                         ".fetch(FilteredFetchCallback) — i.e. .fetch((q, text, filter) -> ...) — " +
                         "so the DynamicFilterPanel's filter is passed to your query. " +
                         "Currently the grid will refresh on Apply but the filter is silently ignored.");
            }
            CallbackDataProvider.FetchCallback<T, Void> wrappedFetch = q -> {
                String text = fSearch != null ? fSearch.getValue() : "";
                QueryFilter qf = fPanel != null ? fPanel.getQueryFilter().orElse(null) : null;
                if (filteredFetchCallback != null) {
                    return filteredFetchCallback.fetch((Query<T, Void>) q, text, qf);
                }
                return fetchCallback.fetch((Query<T, Void>) q, text);
            };
            sb.withLazyFetch(wrappedFetch, null);
        }

        if (search != null) sb.withSearchField(search);
        if (panel  != null) sb.withFilterResetSignal(panel);  // ← Signal.effect: lifecycle-aware reactive page reset

        ItemListingPageSizeSelector<T, ?> selector = sb.build();

        return new ListingBundle<>(listing, bar, selector, search, panel,
                filterOptions, advancedSearchLabel, retainFilterValues);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the named bean property has a numeric Java type.
     * Delegates to {@link TypeUtils#isNumber(Class)} which covers both boxed
     * ({@code Integer}, {@code Long}, {@code BigDecimal}, …) and primitive types.
     * Any {@link NoSuchFieldException} (e.g. inherited / accessor-only fields)
     * is swallowed silently — the column simply won't receive the col-numeric class.
     */
    private static boolean isNumericBeanProperty(Class<?> beanType, String propertyName) {
        try {
            return TypeUtils.isNumber(beanType.getDeclaredField(propertyName).getType());
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}

