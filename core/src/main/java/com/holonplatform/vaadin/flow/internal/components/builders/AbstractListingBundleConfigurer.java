package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared state and implementation for all listing bundle configurers.
 *
 * <p>Subclasses decide the terminal operation — {@code build()} for standalone builders,
 * {@code add()} for embedded nodes — but both share all configuration methods here.</p>
 *
 * @param <T> bean item type
 * @param <C> concrete self-type (returned from every fluent method)
 */
public abstract class AbstractListingBundleConfigurer<T, C extends ListingBundleConfigurer<T, C>>
        implements ListingBundleConfigurer<T, C> {

    private static final Logger log = LoggerFactory.getLogger(AbstractListingBundleConfigurer.class);

    // ── Configuration state ────────────────────────────────────────────────────

    protected final Class<T> beanType;

    private final Map<ViewMode, ComponentEventListener<ItemClickEvent<T>>> itemClickListeners = new LinkedHashMap<>();
    private ComponentEventListener<ItemClickEvent<T>> globalItemClickListener;
    private Supplier<ViewMode> viewModeSupplier;
    private Component mobileViewHeaderComponent;
    private Component[] gridHeaderContextComponents;
    private boolean filterPanelAdvancedMode;
    private List<String> columns = List.of();
    private List<String> hiddenColumns = List.of();
    private final Map<String, Localizable> headers = new LinkedHashMap<>();
    private List<Integer> pageSizes = List.of(10, 25, 50, 100);
    private int defaultPageSize = 10;
    private Localizable searchLocalizable;
    private boolean includeFilterPanel;
    private ListingBundleConfigurer.FetchCallback<T> fetchCallback;
    private ListingBundleConfigurer.FilteredFetchCallback<T> filteredFetchCallback;
    private ListingBundleConfigurer.ColumnAwareFilteredFetchCallback<T> columnAwareFilteredFetchCallback;
    private final List<ListingBundle.MenuAction> menuActions = new ArrayList<>();
    private Runnable importAction;
    private Runnable exportAction;
    private String advancedSearchLabel = "Advanced Search";
    private boolean retainFilterValues = true;
    private boolean multiSelect;
    private String gridHeaderTitle;
    private boolean autoCreateColumns = true;
    private boolean paginatedMode;
    private Renderer<T> mobileColumnRenderer;
    private boolean mobileViewColumn;
    private String mobileViewHeaderText;

    protected AbstractListingBundleConfigurer(Class<T> beanType) {
        this.beanType = Objects.requireNonNull(beanType, "beanType must not be null");
    }

    /** Returns {@code this} cast to the concrete self-type. */
    protected abstract C getConfigurator();

    // ── ListingBundleConfigurer implementation ────────────────────────────────

    @Override
    public C gridHeader(Component... components) {
        this.gridHeaderContextComponents = components != null ? Arrays.copyOf(components, components.length) : null;
        return getConfigurator();
    }

    @Override
    public C gridHeader(String title, Component... contextActions) {
        this.gridHeaderTitle = Objects.requireNonNull(title);
        this.gridHeaderContextComponents = contextActions != null ? Arrays.copyOf(contextActions, contextActions.length) : null;
        return getConfigurator();
    }

    @Override
    public C gridHeader(String title) {
        return gridHeader(title, (Component[]) null);
    }

    @Override
    public C header(String column, String label) {
        headers.put(column, Localizable.builder().message(label).build());
        return getConfigurator();
    }

    @Override
    public C header(String column, Localizable localizable) {
        headers.put(column, Objects.requireNonNull(localizable));
        return getConfigurator();
    }

    @Override
    public C header(String column, String defaultLabel, String messageCode) {
        headers.put(column, Localizable.builder().message(defaultLabel).messageCode(messageCode).build());
        return getConfigurator();
    }

    @Override
    public C columns(String... cols) {
        this.columns = Arrays.asList(cols);
        return getConfigurator();
    }

    @Override
    public C hidden(String... cols) {
        this.hiddenColumns = Arrays.asList(cols);
        return getConfigurator();
    }

    @Override
    public C fetch(ListingBundleConfigurer.FetchCallback<T> callback) {
        this.fetchCallback = Objects.requireNonNull(callback);
        return getConfigurator();
    }

    @Override
    public C fetch(ListingBundleConfigurer.FilteredFetchCallback<T> callback) {
        this.filteredFetchCallback = Objects.requireNonNull(callback);
        return getConfigurator();
    }

    @Override
    public C fetch(ListingBundleConfigurer.ColumnAwareFilteredFetchCallback<T> callback) {
        this.columnAwareFilteredFetchCallback = Objects.requireNonNull(callback);
        return getConfigurator();
    }

    @Override
    public C pageSizes(Integer... sizes) {
        this.pageSizes = Arrays.asList(sizes);
        return getConfigurator();
    }

    @Override
    public C defaultPageSize(int size) {
        if (size <= 0) throw new IllegalArgumentException("defaultPageSize must be > 0");
        this.defaultPageSize = size;
        return getConfigurator();
    }

    @Override
    public C paginated() {
        this.paginatedMode = true;
        return getConfigurator();
    }

    @Override
    public C virtualScroll() {
        this.paginatedMode = false;
        return getConfigurator();
    }

    @Override
    public C paginated(boolean paginated) {
        this.paginatedMode = paginated;
        return getConfigurator();
    }

    @Override
    public C search(String placeholder) {
        this.searchLocalizable = Localizable.builder().message(placeholder).build();
        return getConfigurator();
    }

    @Override
    public C search(Localizable localizable) {
        this.searchLocalizable = Objects.requireNonNull(localizable);
        return getConfigurator();
    }

    @Override
    public C search(String defaultPlaceholder, String messageCode) {
        this.searchLocalizable = Localizable.builder().message(defaultPlaceholder).messageCode(messageCode).build();
        return getConfigurator();
    }

    @Override
    public C withFilterPanel() {
        return withFilterPanel(false);
    }

    @Override
    public C withFilterPanel(boolean advancedMode) {
        this.includeFilterPanel = true;
        this.filterPanelAdvancedMode = advancedMode;
        return getConfigurator();
    }

    @Override
    public C advancedSearchLabel(String label) {
        this.advancedSearchLabel = Objects.requireNonNull(label);
        return getConfigurator();
    }

    @Override
    public C retainFilterValues(boolean retain) {
        this.retainFilterValues = retain;
        return getConfigurator();
    }

    @Override
    public C withMenuAction(String label, Runnable action) {
        menuActions.add(ListingBundle.MenuAction.of(Objects.requireNonNull(label), Objects.requireNonNull(action)));
        return getConfigurator();
    }

    @Override
    public C withMenuAction(VaadinIcon icon, String label, Runnable action) {
        menuActions.add(ListingBundle.MenuAction.of(Objects.requireNonNull(icon), Objects.requireNonNull(label), Objects.requireNonNull(action)));
        return getConfigurator();
    }

    @Override
    public C multiSelect() {
        this.multiSelect = true;
        return getConfigurator();
    }

    @Override
    public C autoCreateColumns(boolean autoCreate) {
        this.autoCreateColumns = autoCreate;
        return getConfigurator();
    }

    @Override
    public C importAction(Runnable action) {
        this.importAction = Objects.requireNonNull(action);
        return getConfigurator();
    }

    @Override
    public C exportAction(Runnable action) {
        this.exportAction = Objects.requireNonNull(action);
        return getConfigurator();
    }

    @Override
    public C onItemClickListener(ViewMode viewMode, ComponentEventListener<ItemClickEvent<T>> listener) {
        itemClickListeners.put(Objects.requireNonNull(viewMode), Objects.requireNonNull(listener));
        return getConfigurator();
    }

    @Override
    public C viewModeSupplier(Supplier<ViewMode> supplier) {
        this.viewModeSupplier = Objects.requireNonNull(supplier);
        return getConfigurator();
    }

    @Override
    public C mobileViewColumn(Renderer<T> renderer) {
        this.mobileColumnRenderer = Objects.requireNonNull(renderer);
        this.mobileViewColumn = true;
        return getConfigurator();
    }

    @Override
    public C mobileViewHeader(String text) {
        this.mobileViewHeaderText = Objects.requireNonNull(text);
        return getConfigurator();
    }

    @Override
    public C mobileViewHeader(Component component) {
        this.mobileViewHeaderComponent = Objects.requireNonNull(component);
        return getConfigurator();
    }

    // ── Shared build logic ────────────────────────────────────────────────────

    /**
     * Assembles and returns a fully wired {@link ListingBundle} from the current
     * configuration state. Safe to call from both {@code build()} and {@code add()}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ListingBundle<T> buildBundle() {
        // When autoCreateColumns is explicitly false, columns() is for querying only —
        // no grid columns are auto-generated; only manually-added columns are rendered.
        var lb = BeanListing.builder(beanType, autoCreateColumns);
        if (autoCreateColumns && !columns.isEmpty()) lb.visibleColumns(columns);
        if (!hiddenColumns.isEmpty()) lb.hiddenColumns(hiddenColumns);
        headers.forEach(lb::header);
        BeanListing<T> listing = lb.build();

        if (multiSelect) listing.setSelectionMode(Selectable.SelectionMode.MULTI);

        var grid = (com.vaadin.flow.component.grid.Grid<T>) listing.getComponent();
        grid.setMultiSort(true);

        if (!itemClickListeners.isEmpty() && viewModeSupplier != null) {
            final var modeSupplier = viewModeSupplier;
            final var listeners = Map.copyOf(itemClickListeners);
            listing.addItemClickListener(event -> {
                var handler = listeners.get(modeSupplier.get());
                if (handler != null) handler.onComponentEvent(event);
            });
        }
        if (globalItemClickListener != null) listing.addItemClickListener(globalItemClickListener);

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

        var bar = new ItemListingPaginationBar<>(listing);

        TextField search = null;
        if (searchLocalizable != null) {
            search = new TextField();
            String ph = LocalizationProvider.localize(searchLocalizable)
                    .orElseGet(() -> searchLocalizable.getMessage() != null ? searchLocalizable.getMessage() : "");
            search.setPlaceholder(ph);
            search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
            search.setClearButtonVisible(true);
            search.addClassName("listing-toolbar__search");
        }

        DynamicFilterPanel<T> panel = null;
        if (includeFilterPanel) {
            panel = createFilterPanel();
            panel.setAdvancedMode(filterPanelAdvancedMode);
        }

        final TextField fSearch = search;
        final DynamicFilterPanel<T> fPanel = panel;

        var sb = (ItemListingPageSizeSelector.Builder) ItemListingPageSizeSelector.of(listing);
        sb.withOptions(new ArrayList<>(pageSizes));
        int effectiveDefaultPageSize = paginatedMode ? defaultPageSize : Math.max(defaultPageSize, 50);
        sb.withDefaultSize(effectiveDefaultPageSize);
        sb.withPaginationBar(bar);

        if (fetchCallback != null || filteredFetchCallback != null || columnAwareFilteredFetchCallback != null) {
            if (includeFilterPanel && filteredFetchCallback == null && columnAwareFilteredFetchCallback == null) {
                log.warn("ListingBundleConfigurer: withFilterPanel() is active but the fetch callback does not accept a QueryFilter. " +
                        "Use .fetch((q, text, filter, sort) -> ...) so the filter is applied to your query.");
            }
            final List<String> fColumns = List.copyOf(columns);
            CallbackDataProvider.FetchCallback<T, Void> wrappedFetch = q -> {
                String text = fSearch != null ? fSearch.getValue() : "";
                QueryFilter qf = fPanel != null ? fPanel.getQueryFilter().orElse(null) : null;
                QuerySort sort = toQuerySort(q.getSortOrders());
                if (columnAwareFilteredFetchCallback != null)
                    return columnAwareFilteredFetchCallback.fetch(q, text, qf, sort, fColumns);
                if (filteredFetchCallback != null)
                    return filteredFetchCallback.fetch(q, text, qf, sort);
                return fetchCallback.fetch(q, text, sort);
            };
            sb.withLazyFetch(wrappedFetch, null);
        }

        if (search != null) sb.withSearchField(search);
        if (panel != null) sb.withFilterResetSignal(panel);

        if (mobileViewColumn) {
            listing.setMobileColumn(mobileColumnRenderer);
            if (mobileViewHeaderText != null) listing.setMobileHeader(mobileViewHeaderText);
            else if (mobileViewHeaderComponent != null) listing.setMobileHeader(mobileViewHeaderComponent);
        }

        ItemListingPageSizeSelector<T, ?> selector = sb.build();

        return new ListingBundle<>(listing, bar, selector, search, panel,
                menuActions, importAction, exportAction,
                advancedSearchLabel, retainFilterValues,
                gridHeaderTitle, gridHeaderContextComponents, columns, paginatedMode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DynamicFilterPanel<T> createFilterPanel() {
        List<Property<?>> filterProperties = resolveFilterPanelProperties();
        if (filterProperties.isEmpty()) {
            return DynamicFilterPanel.of(beanType);
        }

        Property<?>[] properties = filterProperties.toArray(new Property<?>[0]);
        return (DynamicFilterPanel) DynamicFilterPanel.ofProperties(properties);
    }

    private List<Property<?>> resolveFilterPanelProperties() {
        BeanPropertySet<T> beanPropertySet = BeanPropertySet.create(beanType);
        Set<String> hidden = hiddenColumns.isEmpty() ? Set.of() : new LinkedHashSet<>(hiddenColumns);

        if (!autoCreateColumns) {
            return allVisibleBeanProperties(beanPropertySet, hidden);
        }

        if (!columns.isEmpty()) {
            List<Property<?>> configured = configuredVisibleBeanProperties(beanPropertySet, columns, hidden);
            if (!configured.isEmpty()) {
                return configured;
            }
        }

        return allVisibleBeanProperties(beanPropertySet, hidden);
    }

    private static List<Property<?>> allVisibleBeanProperties(BeanPropertySet<?> beanPropertySet, Set<String> hidden) {
        List<Property<?>> properties = new ArrayList<>();
        for (PathProperty<?> property : beanPropertySet) {
            if (!hidden.contains(property.relativeName())) {
                properties.add(property);
            }
        }
        return properties;
    }

    private static List<Property<?>> configuredVisibleBeanProperties(BeanPropertySet<?> beanPropertySet,
                                                                    Iterable<String> propertyNames,
                                                                    Set<String> hidden) {
        List<Property<?>> properties = new ArrayList<>();
        for (String propertyName : propertyNames) {
            if (propertyName == null || propertyName.isBlank() || hidden.contains(propertyName)) {
                continue;
            }
            beanPropertySet.getProperty(propertyName).ifPresent(properties::add);
        }
        return properties;
    }

    // ── ComponentConfigurator / HasSizeConfigurator / HasStyleConfigurator no-ops ─
    // ListingBundle sizing and styling are managed at the container level, not here.

    @Override public C id(String id) { return getConfigurator(); }
    @Override public C visible(boolean visible) { return getConfigurator(); }
    @Override public C elementConfiguration(Consumer<Element> element) { return getConfigurator(); }
    @Override public C withThemeName(String themeName) { return getConfigurator(); }
    @Override public C withEventListener(String eventType, DomEventListener listener) { return getConfigurator(); }
    @Override public C withEventListener(String eventType, DomEventListener listener, String filter) { return getConfigurator(); }
    @Override public C withAttachListener(ComponentEventListener<AttachEvent> listener) { return getConfigurator(); }
    @Override public C withDetachListener(ComponentEventListener<DetachEvent> listener) { return getConfigurator(); }
    @Override public C width(String width) { return getConfigurator(); }
    @Override public C height(String height) { return getConfigurator(); }
    @Override public C minWidth(String minWidth) { return getConfigurator(); }
    @Override public C maxWidth(String maxWidth) { return getConfigurator(); }
    @Override public C minHeight(String minHeight) { return getConfigurator(); }
    @Override public C maxHeight(String maxHeight) { return getConfigurator(); }
    @Override public C styleNames(String... styleNames) { return getConfigurator(); }
    @Override public C styleName(String styleName) { return getConfigurator(); }
    @SuppressWarnings("unused")
    public C enabled(boolean enabled) { return getConfigurator(); }

    // ── Private helpers ────────────────────────────────────────────────────────

    @Override
    public C onItemClick(ComponentEventListener<ItemClickEvent<T>> listener) {
        this.globalItemClickListener = Objects.requireNonNull(listener);
        return getConfigurator();
    }

    private static boolean isNumericBeanProperty(Class<?> beanType, String propertyName) {
        try {
            var field = beanType.getDeclaredField(propertyName);
            if (!TypeUtils.isNumber(field.getType())) return false;
            if ("id".equalsIgnoreCase(propertyName)) return false;
            for (var annotation : field.getAnnotations()) {
                String name = annotation.annotationType().getSimpleName();
                if ("Id".equals(name) || "Identifier".equals(name)) return false;
            }
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private QuerySort toQuerySort(List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) return null;
        List<QuerySort> sorts = new ArrayList<>(sortOrders.size());
        for (QuerySortOrder order : sortOrders) {
            String prop = order.getSorted();
            if (prop == null || prop.isBlank()) continue;
            var direction = order.getDirection() == com.vaadin.flow.data.provider.SortDirection.DESCENDING
                    ? QuerySort.SortDirection.DESCENDING : QuerySort.SortDirection.ASCENDING;
            Class<?> type = isNumericBeanProperty(beanType, prop) ? Number.class : Object.class;
            sorts.add(QuerySort.of(PathProperty.create(prop, type), direction));
        }
        if (sorts.isEmpty()) return null;
        return sorts.size() == 1 ? sorts.getFirst() : QuerySort.of(sorts);
    }
}
