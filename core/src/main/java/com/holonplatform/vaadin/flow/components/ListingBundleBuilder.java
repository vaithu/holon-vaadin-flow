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
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
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
 * content(bundle.toolbar(),   // [Show 10▾ entries]    [🔍 Search…]
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
 * // The filter panel is integrated into the toolbar — no separate content() needed.
 * // A ⊟ Filter button next to search reveals the panel as "Advanced Search".
 * content(bundle.toolbar(),   // [Show 10▾ entries] [🔍 Quick search…] [⊟]
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
 * @see Components#listing(Class)
 * @see ListingBundle
 * @since 10.0.1
 */
public final class ListingBundleBuilder<T> {

    private static final Logger log = LoggerFactory.getLogger(ListingBundleBuilder.class);

    /**
     * Per-ViewMode item-click listeners registered via {@link #onItemClickListener}.
     */
    private final Map<ViewMode, ComponentEventListener<ItemClickEvent<T>>> itemClickListeners = new LinkedHashMap<>();

    /**
     * Supplier that returns the <em>current</em> {@link ViewMode} at click time.
     * Provided by the caller — never computed internally.
     */
    private java.util.function.Supplier<ViewMode> viewModeSupplier;
    private Component mobileViewHeaderComponent;
    private Component[] gridHeaderContextComponents;

    /**
     * Sets the context-action components for the {@link GridHeader}.
     * <p>
     * These actions are shown when the underlying grid has selected rows.
     * </p>
     *
     * @param components context action components
     * @return this builder
     */
    public ListingBundleBuilder<T> gridHeader(Component... components) {
        this.gridHeaderContextComponents = components != null ? Arrays.copyOf(components, components.length) : null;
        return this;
    }

    /**
     * Sets the GridHeader context-action components.
     *
     * @param components context action components
     * @return this builder
     */
    public ListingBundleBuilder<T> contextActions(Component... components) {
        return gridHeader(components);
    }

    /**
     * Adds a {@link GridHeader} above the toolbar with the given title and optional
     * context-action components.
     * <p>
     * The context actions are shown when the underlying grid has selected rows.
     * </p>
     *
     * @param title the header title
     * @param contextActions optional components to show as context actions
     * @return this builder
     */
    public ListingBundleBuilder<T> gridHeader(String title, Component... contextActions) {
        this.gridHeaderTitle = Objects.requireNonNull(title, "title must not be null");
        this.gridHeaderContextComponents = contextActions != null
                ? Arrays.copyOf(contextActions, contextActions.length)
                : null;
        return this;
    }

    // ── Callback interfaces ────────────────────────────────────────────────

    /**
     * Fetch callback that receives the Vaadin {@link Query}, search text, and a Holon
     * {@link QuerySort} derived from the grid's current sort state.
     * Used when the bundle has a search field but no {@link DynamicFilterPanel}.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FetchCallback<T> {
        /**
         * Fetches items for the given query, search text, and sort.
         *
         * @param query      Vaadin query carrying offset and limit
         * @param searchText current value of the search field (never null; empty string when blank)
         * @param sort       combined {@link QuerySort} from grid columns, or {@code null} if unsorted
         * @return stream of matching items
         */
        Stream<T> fetch(Query<T, Void> query, String searchText, QuerySort sort);
    }

    /**
     * Fetch callback that receives the Vaadin {@link Query}, search text,
     * {@link QueryFilter} from the {@link DynamicFilterPanel}, and a Holon
     * {@link QuerySort} derived from the grid's current sort state.
     * Used when {@link #withFilterPanel()} is enabled.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FilteredFetchCallback<T> {
        /**
         * Fetches items for the given query, search text, filter, and sort.
         *
         * @param query      Vaadin query carrying offset and limit
         * @param searchText current value of the search field (empty string when none configured)
         * @param filter     committed {@link QueryFilter} from the filter panel, or {@code null}
         * @param sort       combined {@link QuerySort} from grid columns, or {@code null} if unsorted
         * @return stream of matching items
         */
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter, QuerySort sort);
    }

    /**
     * Column-aware variant of {@link FilteredFetchCallback} that additionally receives the list of
     * visible column property names configured via {@link #columns(String...)}.
     * Use this when the backend should perform projection (selective column fetching).
     *
     * @param <T> bean item type
     */
    @FunctionalInterface
    public interface ColumnAwareFilteredFetchCallback<T> {
        /**
         * Fetches items for the given query, search text, filter, sort, and visible columns.
         *
         * @param query      Vaadin query carrying offset and limit
         * @param searchText current value of the search field (empty string when none configured)
         * @param filter     committed {@link QueryFilter} from the filter panel, or {@code null}
         * @param sort       combined {@link QuerySort} from grid columns, or {@code null} if unsorted
         * @param columns    the visible column property names configured via {@link ListingBundleBuilder#columns(String...)}
         *                   (empty list if none were explicitly set)
         * @return stream of matching items
         */
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter, QuerySort sort, List<String> columns);
    }

    // ── Builder state ──────────────────────────────────────────────────────

    private final Class<T> beanType;
    private List<String> columns = List.of();
    private List<String> hiddenColumns = List.of();
    private final Map<String, Localizable> headers = new LinkedHashMap<>();
    private List<Integer> pageSizes = List.of(10, 25, 50, 100);
    private int defaultPageSize = 10;
    private Localizable searchLocalizable;
    private boolean includeFilterPanel;
    private FetchCallback<T> fetchCallback;
    private FilteredFetchCallback<T> filteredFetchCallback;
    private ColumnAwareFilteredFetchCallback<T> columnAwareFilteredFetchCallback;
    /**
     * Extra items appended to the options menu.
     */
    private final List<ListingBundle.MenuAction> menuActions = new ArrayList<>();
    /**
     * Optional import handler; when set, an "Import" item appears in the options menu.
     */
    private Runnable importAction;
    /**
     * Optional export handler; when set, an "Export" item appears in the options menu.
     */
    private Runnable exportAction;
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
     * When true, the grid uses multi-select mode.
     */
    private boolean multiSelect;
    /**
     * Title for the GridHeader (null = no GridHeader).
     */
    private String gridHeaderTitle;
    /**
     * When {@code false} the underlying {@link BeanListing} is created with
     * {@code autoCreateColumns=false}, suppressing the automatic column registration
     * that Vaadin performs from the bean's properties. Defaults to {@code true}.
     */
    private boolean autoCreateColumns = true;
    /**
     * When {@code true} the bundle starts in paginated mode (pagination bar visible,
     * fixed-page fetch).  When {@code false} (default) the bundle starts in virtual-scroll
     * mode (infinite scroll, pagination bar hidden).
     */
    private boolean paginatedMode = false;

    private Renderer<T> mobileColumnRenderer;

    private boolean mobileViewColumn = false;

    private String mobileViewHeaderText;

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
     * Hides the specified columns from the grid while keeping them available in the data model
     * (e.g. for joins, identity, or programmatic access after build).
     *
     * <p>Works alongside {@link #columns(String...)} — include the property in {@code columns}
     * so it participates in data projection, then call {@code hidden} to suppress it visually:</p>
     *
     * <pre>{@code
     * Components.listing(Product.class)
     *     .columns("id", "name", "price")   // id included for DB projection / identity
     *     .hidden("id")                     // but never rendered in the grid
     *     .fetch(...)
     *     .build();
     * }</pre>
     *
     * @param cols column property names to hide (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> hidden(String... cols) {
        this.hiddenColumns = Arrays.asList(cols);
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
     * @param column      property name
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
     * @param column       property name
     * @param defaultLabel fallback label when no localization is found
     * @param messageCode  i18n message code
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
     * In this case use only {@code bundle.toolbar()} — do <strong>not</strong> content
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
     * Appends an extra item (text-only) to the options menu.
     *
     * <pre>{@code
     * .withMenuAction("Duplicate selected", () -> duplicateService.duplicate(bundle.listing()))
     * }</pre>
     *
     * @param label  display text of the menu item (not null)
     * @param action action to run when the item is clicked (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> withMenuAction(String label, Runnable action) {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        this.menuActions.add(ListingBundle.MenuAction.of(label, action));
        return this;
    }

    /**
     * Appends an extra item (icon + text) to the options menu.
     *
     * <pre>{@code
     * .withMenuAction(VaadinIcon.COPY, "Duplicate selected", () -> duplicateService.duplicate(bundle.listing()))
     * }</pre>
     *
     * @param icon   icon shown to the left of the label (not null)
     * @param label  display text of the menu item (not null)
     * @param action action to run when the item is clicked (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> withMenuAction(com.vaadin.flow.component.icon.VaadinIcon icon, String label, Runnable action) {
        Objects.requireNonNull(icon, "icon must not be null");
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        this.menuActions.add(ListingBundle.MenuAction.of(icon, label, action));
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
     * Enables multi-select mode on the grid.
     *
     * @return this builder
     */
    public ListingBundleBuilder<T> multiSelect() {
        this.multiSelect = true;
        return this;
    }

    /**
     * Adds a {@link GridHeader} above the toolbar with the given title.
     * The header integrates selection-aware context actions and a toolbar menu
     * (sort, refresh, import, export, reset column widths, show/hide columns).
     *
     * @param title the header title
     * @return this builder
     */
    public ListingBundleBuilder<T> gridHeader(String title) {
        return gridHeader(title, (Component[]) null);
    }

    /**
     * Configures the bundle to start in <strong>paginated mode</strong>: a fixed page of rows
     * is shown at a time and the user navigates via the pagination bar in the footer.
     *
     * <p>Calling this method is equivalent to calling {@code .paginated(true)}.
     * When neither {@code paginated()} nor {@code virtualScroll()} is called the bundle
     * defaults to virtual-scroll mode.</p>
     *
     * <pre>{@code
     * var bundle = Components.listing(Product.class)
     *     .columns("name", "price", "stock")
     *     .paginated()   // ← explicit paginated mode
     *     .fetch(...)
     *     .build();
     * }</pre>
     *
     * @return this builder
     * @see #virtualScroll()
     */
    public ListingBundleBuilder<T> paginated() {
        this.paginatedMode = true;
        return this;
    }

    /**
     * Configures the bundle to start in <strong>virtual-scroll mode</strong> (the default):
     * the grid uses Vaadin's built-in infinite scroll — no page offset is injected and
     * the item count is set to unknown so the grid fetches rows as the user scrolls.
     * The pagination footer is hidden (but can still be toggled on at runtime).
     *
     * <p>This is the default behaviour and only needs to be called when you want to
     * be explicit, or when overriding a previous {@link #paginated()} call.</p>
     *
     * @return this builder
     * @see #paginated()
     */
    public ListingBundleBuilder<T> virtualScroll() {
        this.paginatedMode = false;
        return this;
    }

    /**
     * Controls whether the bundle starts in paginated or virtual-scroll mode.
     *
     * @param paginated {@code true} for paginated mode, {@code false} (default) for virtual scroll
     * @return this builder
     * @see #paginated()
     * @see #virtualScroll()
     */
    public ListingBundleBuilder<T> paginated(boolean paginated) {
        this.paginatedMode = paginated;
        return this;
    }

    /**
     * Controls whether the underlying {@link BeanListing} automatically creates a column
     * for every bean property at construction time (default: {@code true}).
     *
     * <p><strong>When {@code true}</strong> (default): every bean property becomes a grid
     * column and {@link #columns(String...)} selects which ones are visible.</p>
     *
     * <p><strong>When {@code false}</strong>: no grid columns are registered automatically;
     * the caller is expected to content their own columns post-build via
     * {@link ListingBundle#listing()}. In this mode {@link #columns(String...)} is treated
     * <em>purely as a DB-projection hint</em> — the list is forwarded to
     * {@link ColumnAwareFilteredFetchCallback}'s {@code cols} parameter so the backend can
     * fetch only those fields, but the listing itself stays empty until the caller adds
     * columns explicitly.</p>
     *
     * <pre>{@code
     * // autoCreateColumns=false + columns(...) as projection hint
     * var bundle = Components.listing(Product.class)
     *     .autoCreateColumns(false)
     *     .columns("name", "price")                       // DB-projection only
     *     .fetch((q, text, filter, sort, cols) ->         // cols = ["name","price"]
     *         datastore.query(TARGET)
     *             .restrict(q.getLimit(), q.getOffset())
     *             .stream(BeanProjection.of(Product.class, cols.toArray(String[]::new))))
     *     .build();
     *
     * // Caller adds custom grid columns
     * ((BeanListing<Product>) bundle.listing())
     *     .addComponentColumn(p -> new Span(p.getName()))
     *     .setHeader("Name");
     * }</pre>
     *
     * @param autoCreate {@code true} (default) to register a grid column for every bean
     *                   property; {@code false} to start with an empty grid and treat
     *                   {@code .columns(...)} purely as a DB-projection hint
     * @return this builder
     */
    public ListingBundleBuilder<T> autoCreateColumns(boolean autoCreate) {
        this.autoCreateColumns = autoCreate;
        return this;
    }

    /**
     * Registers a handler for the <em>Import</em> menu item in the options menu.
     * The item is only visible when a handler has been provided.
     *
     * <pre>{@code
     * .importAction(() -> importService.openImportDialog())
     * }</pre>
     *
     * @param action action to run when the user clicks "Import" (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> importAction(Runnable action) {
        this.importAction = Objects.requireNonNull(action, "importAction must not be null");
        return this;
    }

    /**
     * Registers a handler for the <em>Export</em> menu item in the options menu.
     * The item is only visible when a handler has been provided.
     *
     * <pre>{@code
     * .exportAction(() -> exportService.exportToCsv(bundle.listing()))
     * }</pre>
     *
     * @param action action to run when the user clicks "Export" (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> exportAction(Runnable action) {
        this.exportAction = Objects.requireNonNull(action, "exportAction must not be null");
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

    /**
     * Registers a column-aware filtered lazy-fetch callback. Works like
     * {@link #fetch(FilteredFetchCallback)} but additionally receives the list of visible
     * column property names. Use this when the backend should perform projection
     * (fetch only the requested columns) and a {@link DynamicFilterPanel} is configured.
     *
     * <pre>{@code
     * .fetch((q, text, filter, sort, cols) -> {
     *     var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
     *     if (filter != null) q2.filter(filter);
     *     return q2.stream(BeanProjection.of(Product.class, cols));
     * })
     * }</pre>
     *
     * @param callback column-aware filtered fetch callback (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> fetch(ColumnAwareFilteredFetchCallback<T> callback) {
        this.columnAwareFilteredFetchCallback = Objects.requireNonNull(callback, "columnAwareFilteredFetchCallback must not be null");
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
        // When autoCreateColumns=false the underlying BeanListing has NO registered
        // bean-property columns, so calling visibleColumns(...) would throw
        // IllegalArgumentException ("not part of the listing property set"). In that
        // mode the explicit .columns(...) list is treated purely as a DB-projection
        // hint forwarded to ColumnAwareFilteredFetchCallback#fetch(..., cols); the
        // caller is expected to content their own grid columns post-build via the
        // returned ListingBundle#listing().
        var lb = BeanListing.builder(beanType, autoCreateColumns);
        if (autoCreateColumns && !columns.isEmpty()) {
            lb.visibleColumns(columns);
        }
        if (!hiddenColumns.isEmpty()) {
            lb.hiddenColumns(hiddenColumns);
        }
        headers.forEach((col, loc) -> lb.header(col, loc));
        BeanListing<T> listing = lb.build();

        // ── 1a. Multi-select ────────────────────────────────────────────────
        if (multiSelect) {
            listing.setSelectionMode(Selectable.SelectionMode.MULTI);
        }

        // ── 1a2. Multi-sort ─────────────────────────────────────────────────
        {
            var grid = (com.vaadin.flow.component.grid.Grid<T>) listing.getComponent();
            grid.setMultiSort(true);
        }

        // ── 1a3. ViewMode-dispatching item-click listener ───────────────────
        // Wire a single grid listener that consults viewModeSupplier at click time
        // and routes to whichever per-mode handler was registered (if any).
        if (!itemClickListeners.isEmpty() && viewModeSupplier != null) {
            final var modeSupplier = viewModeSupplier;
            final var listeners = Map.copyOf(itemClickListeners);
            var grid = (com.vaadin.flow.component.grid.Grid<T>) listing.getComponent();
            grid.addItemClickListener(event -> {
                ViewMode mode = modeSupplier.get();
                ComponentEventListener<ItemClickEvent<T>> handler = listeners.get(mode);
                if (handler != null) handler.onComponentEvent(event);
            });
        }

        // ── 1b. Auto-style numeric columns ─────────────────────────────────────
        // Detect bean properties whose Java type is numeric and apply the
        // col-numeric CSS class via Column.setClassNameGenerator so the grid
        // renders them right-aligned in a monospace / tabular-nums font.
        // Uses listing.getAllColumns() (ItemListing API) — no Grid cast needed.
        // Skipped when autoCreateColumns=false: in that mode .columns(...) is a
        // DB-projection hint only and the caller will content their own grid columns.
        if (autoCreateColumns && !columns.isEmpty()) {
            for (String colKey : columns) {
                if (isNumericBeanProperty(beanType, colKey)) {
                    listing.getAllColumns().stream()
                            .filter(col -> colKey.equals(col.getKey()))
                            .findFirst()
                            .ifPresent(col -> {
                                col.setPartNameGenerator(item -> "col-numeric");
                                col.setHeaderPartName("col-numeric");
                            });
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
            search.setClearButtonVisible(true);
            // CSS class drives the flex width defined in pagination.css
            search.addClassName("listing-toolbar__search");
        }

        // ── 4. Filter panel ─────────────────────────────────────────────────
        DynamicFilterPanel<T> panel = null;
        if (includeFilterPanel) {
            panel = DynamicFilterPanel.of(beanType);
        }

        // ── 5. Selector — capture finals for lambda ─────────────────────────
        final TextField fSearch = search;
        final DynamicFilterPanel<T> fPanel = panel;

        // Use raw Builder to avoid P-wildcard capture issues when passing bar.
        // The actual type safety is guaranteed by construction: all three objects
        // (listing, bar, selector) are created from the same BeanListing<T> instance.
        ItemListingPageSizeSelector.Builder sb =
                (ItemListingPageSizeSelector.Builder) ItemListingPageSizeSelector.of(listing);
        sb.withOptions(new ArrayList<>(pageSizes));
        int effectiveDefaultPageSize = paginatedMode ? defaultPageSize : Math.max(defaultPageSize, 50);
        sb.withDefaultSize(effectiveDefaultPageSize);
        sb.withPaginationBar(bar);

        if (fetchCallback != null || filteredFetchCallback != null
                || columnAwareFilteredFetchCallback != null) {
            // Misconfiguration guard: withFilterPanel() declared but only plain FetchCallback provided.
            // The plain callback receives (query, searchText, sort) — the QueryFilter from the panel
            // is NOT forwarded. The grid will still refresh on Apply but data won't change.
            if (includeFilterPanel && filteredFetchCallback == null && columnAwareFilteredFetchCallback == null) {
                log.warn("ListingBundleBuilder: withFilterPanel() was configured but the fetch " +
                        "callback does not accept a QueryFilter. Use " +
                        ".fetch((q, text, filter, sort) -> ...) or " +
                        ".fetch((q, text, filter, sort, cols) -> ...) — " +
                        "so the DynamicFilterPanel's filter is passed to your query. " +
                        "Currently the grid will refresh on Apply but the filter is silently ignored.");
            }
            final List<String> fColumns = List.copyOf(columns);
            CallbackDataProvider.FetchCallback<T, Void> wrappedFetch = q -> {
                String text = fSearch != null ? fSearch.getValue() : "";
                QueryFilter qf = fPanel != null ? fPanel.getQueryFilter().orElse(null) : null;
                QuerySort sort = toQuerySort(q.getSortOrders());
                if (columnAwareFilteredFetchCallback != null) {
                    return columnAwareFilteredFetchCallback.fetch((Query<T, Void>) q, text, qf, sort, fColumns);
                }
                if (filteredFetchCallback != null) {
                    return filteredFetchCallback.fetch((Query<T, Void>) q, text, qf, sort);
                }
                return fetchCallback.fetch((Query<T, Void>) q, text, sort);
            };
            sb.withLazyFetch(wrappedFetch, null);
        }

        if (search != null) sb.withSearchField(search);
        if (panel != null) sb.withFilterResetSignal(panel);  // ← Signal.effect: lifecycle-aware reactive page reset

        if (mobileViewColumn) {
            listing.setMobileColumn(mobileColumnRenderer);

            if (mobileViewHeaderText != null) {
                listing.setMobileHeader(mobileViewHeaderText);
            } else if (mobileViewHeaderComponent != null) {
                listing.setMobileHeader(mobileViewHeaderComponent);
            }
        }

        ItemListingPageSizeSelector<T, ?> selector = sb.build();

        return new ListingBundle<>(listing, bar, selector, search, panel,
                menuActions, importAction, exportAction,
                advancedSearchLabel, retainFilterValues,
                gridHeaderTitle, gridHeaderContextComponents, columns, paginatedMode);
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
            var field = beanType.getDeclaredField(propertyName);
            if (!TypeUtils.isNumber(field.getType())) {
                return false;
            }
            // Exclude identifier/primary-key fields — they are not "values" to right-align
            if ("id".equalsIgnoreCase(propertyName)) {
                return false;
            }
            for (var annotation : field.getAnnotations()) {
                String annotationName = annotation.annotationType().getSimpleName();
                if ("Id".equals(annotationName) || "Identifier".equals(annotationName)) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Converts Vaadin {@link QuerySortOrder} list to a Holon {@link QuerySort}.
     * Each sort order's {@code getSorted()} value (the grid column key = bean property name)
     * is mapped to a {@link PathProperty} path, and the direction is preserved.
     *
     * @param sortOrders list of Vaadin sort orders from the grid (may be null or empty)
     * @return combined {@link QuerySort}, or {@code null} if no sort orders are active
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

    /**
     * Registers a grid item-click listener that fires <em>only</em> when the current
     * viewport matches {@code viewMode}.
     *
     * <p>Multiple calls with different {@link ViewMode} values are allowed; the last
     * registration for a given mode wins. The active mode is resolved at click time
     * via the {@link #viewModeSupplier} — wire that first:</p>
     *
     * <pre>{@code
     * Signal<ViewMode> modeSignal = responsiveLayout.viewModeSignal();
     *
     * Components.listing(Product.class)
     *     .viewModeSupplier(modeSignal::getValue)
     *     .onItemClickListener(ViewMode.MOBILE,  e -> openSheet(e.getItem()))
     *     .onItemClickListener(ViewMode.DESKTOP, e -> showInDetailPanel(e.getItem()))
     *     .fetch(...)
     *     .build();
     * }</pre>
     *
     * <p>If no supplier is configured or the current mode has no matching listener,
     * the click is silently ignored.</p>
     *
     * @param viewMode the viewport mode this listener applies to (not null)
     * @param listener the listener to invoke (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> onItemClickListener(ViewMode viewMode,
                                                       ComponentEventListener<ItemClickEvent<T>> listener) {
        Objects.requireNonNull(viewMode, "viewMode must not be null");
        Objects.requireNonNull(listener, "listener must not be null");
        itemClickListeners.put(viewMode, listener);
        return this;
    }

    /**
     * Provides the supplier that resolves the <em>current</em> {@link ViewMode} at
     * item-click time. The builder never computes this value itself; the caller is
     * responsible for wiring it to whatever responsive-layout signal or state they
     * maintain.
     *
     * <pre>{@code
     * // From IyenResponsiveLayout:
     * .viewModeSupplier(responsiveLayout.viewModeSignal()::getValue)
     *
     * // From a plain AtomicReference you manage yourself:
     * AtomicReference<ViewMode> modeRef = new AtomicReference<>(ViewMode.DESKTOP);
     * .viewModeSupplier(modeRef::get)
     * }</pre>
     *
     * @param supplier returns the current {@link ViewMode} on demand (not null)
     * @return this builder
     */
    public ListingBundleBuilder<T> viewModeSupplier(java.util.function.Supplier<ViewMode> supplier) {
        this.viewModeSupplier = Objects.requireNonNull(supplier, "viewModeSupplier must not be null");
        return this;
    }

    public ListingBundleBuilder<T> mobileViewColumn(Renderer<T> renderer) {
        this.mobileColumnRenderer = Objects.requireNonNull(renderer, "renderer must not be null");
        this.mobileViewColumn = true;
        return this;
    }

    public ListingBundleBuilder<T> mobileViewHeader(String text) {
        this.mobileViewHeaderText = Objects.requireNonNull(text, "text must not be null");
        return this;
    }

    public ListingBundleBuilder<T> mobileViewHeader(Component component) {
        this.mobileViewHeaderComponent = Objects.requireNonNull(component, "component must not be null");
        return this;
    }
}

