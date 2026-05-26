package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link BeanListing}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic BeanListing with auto-columns and in-memory data</li>
 *   <li>Custom column headers, visible columns, sortable, multi-select</li>
 *   <li>FilterInput type showcase (all built-in types + custom)</li>
 *   <li>FilterInputGroup wired to BeanListing via addFilterChangeListener</li>
 *   <li>FilterInputForm + BeanListing.setItems(filterGroup, callback) + refreshOnFilterChange</li>
 *   <li>DynamicFilterPanel — type-aware row filter builder (simple mode)</li>
 *   <li>DynamicFilterPanel + PropertyListing (PropertyBox)</li>
 *   <li>DynamicFilterPanel — advanced mode: per-row logical connectors, NOT toggle, multi-value IN/NOT_IN</li>
 *   <li>Paginated BeanListing with the Pagination navigation component</li>
 *   <li>ItemListingPaginationBar + ItemListingPageSizeSelector + search TextField (zero COUNT(*))</li>
 * </ol>
 */
@PageTitle("BeanListing – Holon Demo")
@Route(value = "bean-listing", layout = DemoMainLayout.class)
public class BeanListingDemoView extends Div {

    // ── Status enum ──────────────────────────────────────────────────────────
    public enum Status {AVAILABLE, OUT_OF_STOCK, DISCONTINUED}

    // ── Demo bean (Java-bean convention: getters + no-arg constructor) ───────
    public static final class Product {
        private long id;
        private String name;
        private String category;
        private double price;
        private boolean active;
        private Status status;
        private LocalDate addedOn;

        /**
         * No-arg constructor required by bean introspection.
         */
        public Product() {
        }

        public Product(long id, String name, String category, double price,
                       boolean active, Status status, LocalDate addedOn) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.active = active;
            this.status = status;
            this.addedOn = addedOn;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public double getPrice() {
            return price;
        }

        public boolean isActive() {
            return active;
        }

        public Status getStatus() {
            return status;
        }

        public LocalDate getAddedOn() {
            return addedOn;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCategory(String c) {
            this.category = c;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public void setAddedOn(LocalDate d) {
            this.addedOn = d;
        }
    }

    // ── Holon properties – used as keys for FilterInput bindings ────────────
    private static final StringProperty NAME_PROP = StringProperty.create("name");
    private static final PathProperty<Double> PRICE_PROP = PathProperty.create("price", Double.class);
    private static final PathProperty<Boolean> ACTIVE_PROP = PathProperty.create("active", Boolean.class);
    private static final PathProperty<Status> STATUS_PROP = PathProperty.create("status", Status.class);
    private static final PathProperty<LocalDate> DATE_PROP = PathProperty.create("addedOn", LocalDate.class);

    // ── Sample data ──────────────────────────────────────────────────────────
    private static final List<Product> PRODUCTS = List.of(
            new Product(1, "Laptop Pro 15", "Electronics", 1299.99, true, Status.AVAILABLE, LocalDate.of(2024, 1, 15)),
            new Product(2, "Wireless Mouse", "Accessories", 49.99, true, Status.AVAILABLE, LocalDate.of(2024, 2, 10)),
            new Product(3, "USB-C Hub 7in1", "Accessories", 89.99, false, Status.OUT_OF_STOCK, LocalDate.of(2024, 3, 5)),
            new Product(4, "Mech Keyboard", "Accessories", 159.99, true, Status.AVAILABLE, LocalDate.of(2024, 1, 20)),
            new Product(5, "4K Monitor 27\"", "Electronics", 799.99, true, Status.AVAILABLE, LocalDate.of(2024, 2, 28)),
            new Product(6, "Webcam 1080p", "Electronics", 129.99, false, Status.DISCONTINUED, LocalDate.of(2023, 11, 1)),
            new Product(7, "LED Desk Lamp", "Office", 45.00, true, Status.AVAILABLE, LocalDate.of(2024, 4, 1)),
            new Product(8, "Ergonomic Chair", "Office", 449.99, true, Status.AVAILABLE, LocalDate.of(2024, 3, 20)),
            new Product(9, "BT Headphones", "Electronics", 199.99, true, Status.AVAILABLE, LocalDate.of(2024, 2, 14)),
            new Product(10, "Notebook Stand", "Accessories", 35.99, false, Status.DISCONTINUED, LocalDate.of(2023, 12, 10)),
            new Product(11, "Smart Speaker", "Electronics", 89.99, true, Status.AVAILABLE, LocalDate.of(2024, 4, 5)),
            new Product(12, "Desk Mat XL", "Office", 29.99, true, Status.AVAILABLE, LocalDate.of(2024, 3, 15)),
            new Product(13, "USB Flash 128G", "Accessories", 19.99, true, Status.AVAILABLE, LocalDate.of(2024, 1, 30)),
            new Product(14, "Gaming Headset", "Electronics", 89.99, false, Status.OUT_OF_STOCK, LocalDate.of(2024, 2, 20)),
            new Product(15, "Portable SSD 1T", "Electronics", 119.99, true, Status.AVAILABLE, LocalDate.of(2024, 4, 10))
    );

    private static final int PAGE_SIZE = 5;

    // ── Constructor ──────────────────────────────────────────────────────────
    public BeanListingDemoView() {
        addClassName("app-view");

        var title = new H1("BeanListing");

        var desc = new Paragraph(
                "BeanListing is the primary server-side listing component for bean types. " +
                        "It wraps Vaadin Grid with the Holon property system, supporting automatic column creation, " +
                        "pluggable FilterInput / FilterInputGroup / FilterInputForm for live filtering, " +
                        "and integrates with the Pagination navigation component for page-based UX. " +
                        "In real applications the fetch callback delegates to a Holon Datastore — " +
                        "all examples below use in-memory data for standalone demo purposes.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(customColumnsExample());
        examples.add(filterInputTypesExample());
        examples.add(filterGroupExample());
        examples.add(filterFormExample());
        examples.add(dynamicFilterPanelExample());
        examples.add(dynamicFilterPanelPropertyListingExample());
        examples.add(advancedLogicalFilterPanelExample());
        examples.add(paginatedExample());
        examples.add(paginationBarWithSearchExample());

        add(title, desc, examples);
    }

    // ── Example builders ─────────────────────────────────────────────────────

    /**
     * 1. Auto-columns — simplest BeanListing form.
     */
    private DemoExample basicExample() {
        var listing = BeanListing.builder(Product.class, true)
                .header("id", "ID")
                .header("name", "Name")
                .header("category", "Category")
                .header("price", "Price (€)")
                .header("active", "Active")
                .header("status", "Status")
                .header("addedOn", "Added On")
                .height("280px")
                .build();
        listing.setItems(q -> PRODUCTS.stream()
                .skip(q.getOffset()).limit(q.getLimit()));

        return new DemoExample("Basic BeanListing (auto columns)", listing.getComponent(), """
                // Pass true to auto-create one column per bean property.
                BeanListing<Product> listing = BeanListing.builder(Product.class, true)
                    .header("name",     "Product Name")  // override column header
                    .header("price",    "Price (€)")
                    .height("280px")
                    .build();
                
                // Supply data as a List or varargs.
                listing.setItems(products);
                
                // Get the underlying Vaadin component (e.g. to embed in a layout):
                Component grid = listing.getComponent();
                """);
    }

    /**
     * 2. Explicit visible columns + sortable + multi-select mode.
     */
    private DemoExample customColumnsExample() {
        var listing = BeanListing.builder(Product.class, true)
                .header("name", "Product Name")
                .header("category", "Category")
                .header("price", "Price (€)")
                .header("active", "Available")
                .header("status", "Status")
                .visibleColumns(List.of("name", "category", "price", "active", "status"))
                .sortable("name", true)
                .sortable("price", true)
                .sortable("category", true)
                .selectionMode(SelectionMode.MULTI)
                .height("280px")
                .build();
        listing.setItems(q -> PRODUCTS.stream()
                .skip(q.getOffset()).limit(q.getLimit()));

        var selectionLabel = new Span("Select rows using the checkboxes");
        // Cast to Selectable<T> to disambiguate from ItemListing.addSelectionListener(Grid variant)
        ((com.holonplatform.vaadin.flow.components.Selectable<Product>) listing)
                .addSelectionListener(e -> {
                    int n = e.getAllSelectedItems().size();
                    selectionLabel.setText(n == 0 ? "No rows selected" : n + " row(s) selected");
                });

        var container = new Div(listing.getComponent(), selectionLabel);

        return new DemoExample("Custom Columns + Sortable + Multi-Select", container, """
                BeanListing<Product> listing = BeanListing.builder(Product.class, true)
                    .header("name",  "Product Name")
                    .header("price", "Price (€)")
                    // Restrict which columns are shown (in order):
                    .visibleColumns(List.of("name", "category", "price", "active", "status"))
                    // Enable click-to-sort on specific columns:
                    .sortable("name",  true)
                    .sortable("price", true)
                    // Selection: NONE / SINGLE / MULTI
                    .selectionMode(SelectionMode.MULTI)
                    .height("280px")
                    .build();
                listing.setItems(products);
                
                // React to row selection:
                listing.addSelectionListener(e -> {
                    Set<Product> selected = e.getAllSelectedItems();
                    label.setText(selected.size() + " selected");
                });
                
                // Programmatic selection control:
                listing.select(product);
                listing.deselectAll();
                Set<Product> current = listing.getSelectedItems();
                """);
    }

    /**
     * 3. All FilterInput types shown as live, interactive widgets.
     */
    private DemoExample filterInputTypesExample() {
        var grid = new Div();

        // String – contains (case-insensitive)
        addFilterRow(grid, "String – contains, case-insensitive",
                FilterInput.string(NAME_PROP));

        // Number – exact equality
        addFilterRow(grid, "Number – exact equality",
                FilterInput.number(PRICE_PROP, Double.class));

        // Number range – from ≤ value ≤ to
        addFilterRow(grid, "Number Range – from ≤ value ≤ to  (either bound optional)",
                FilterInput.numberRange(PRICE_PROP, Double.class));

        // Boolean tri-state
        addFilterRow(grid, "Boolean – tri-state: null = any, TRUE = active, FALSE = inactive",
                FilterInput.bool(ACTIVE_PROP));

        // Enum – exact constant
        addFilterRow(grid, "Enum – exact constant  (constants auto-populated from the enum type)",
                FilterInput.enumeration(STATUS_PROP, Status.class));

        // LocalDate – exact
        addFilterRow(grid, "LocalDate – exact date equality",
                FilterInput.localDate(DATE_PROP));

        // LocalDate range
        addFilterRow(grid, "LocalDate Range – from ≤ date ≤ to",
                FilterInput.localDateRange(DATE_PROP));

        // Custom via FilterInput.from()
        FilterInput<String> customFilter = FilterInput.from(
                Input.string().build(),
                value -> (value != null && !value.isBlank())
                        ? Optional.of(QueryFilter.eq(NAME_PROP, value))
                        : Optional.empty());
        addFilterRow(grid, "Custom – FilterInput.from(input, FilterConverter) exact-match",
                customFilter);

        // Auto-inferred from property type
        addFilterRow(grid, "Auto-inferred – FilterInput.of(property) → detects type automatically",
                FilterInput.of(NAME_PROP));

        return new DemoExample("FilterInput Type Showcase (all built-in types)", grid, """
                // Holon properties serve as typed keys for filter bindings.
                StringProperty          NAME_PROP   = StringProperty.create("name");
                PathProperty<Double>    PRICE_PROP  = PathProperty.create("price",   Double.class);
                PathProperty<Boolean>   ACTIVE_PROP = PathProperty.create("active",  Boolean.class);
                PathProperty<Status>    STATUS_PROP = PathProperty.create("status",  Status.class);
                PathProperty<LocalDate> DATE_PROP   = PathProperty.create("addedOn", LocalDate.class);
                
                // String: case-insensitive "contains"
                FilterInput<String> nameFilter = FilterInput.string(NAME_PROP);
                
                // Number: exact equality
                FilterInput<Double> priceFilter = FilterInput.number(PRICE_PROP, Double.class);
                
                // Number range: from ≤ value ≤ to  (either bound may be null → open-ended)
                FilterInput<FilterInput.Range<Double>> rangeFilter
                    = FilterInput.numberRange(PRICE_PROP, Double.class);
                // Programmatically set a range value:
                rangeFilter.getInput().setValue(new FilterInput.Range<>(10.0, 200.0));
                
                // Boolean tri-state: null = no filter, TRUE, FALSE
                FilterInput<Boolean> activeFilter = FilterInput.bool(ACTIVE_PROP);
                
                // Enum: exact – constants auto-populated from Status.getEnumConstants()
                FilterInput<Status> statusFilter
                    = FilterInput.enumeration(STATUS_PROP, Status.class);
                
                // LocalDate: exact date
                FilterInput<LocalDate> dateFilter = FilterInput.localDate(DATE_PROP);
                
                // LocalDate range
                FilterInput<FilterInput.Range<LocalDate>> dateRange
                    = FilterInput.localDateRange(DATE_PROP);
                
                // Custom: any Input + a FilterConverter lambda
                FilterInput<String> customFilter = FilterInput.from(
                    Input.string().build(),
                    value -> (value != null && !value.isBlank())
                        ? Optional.of(QueryFilter.eq(NAME_PROP, value))
                        : Optional.empty()
                );
                
                // Auto-infer from property type (String→string, Boolean→bool, Enum→enum, …)
                FilterInput<?> autoFilter = FilterInput.of(NAME_PROP);
                
                // Core API on any FilterInput:
                boolean active  = filter.isActive();           // has a non-empty value?
                Optional<QueryFilter> qf = filter.getQueryFilter();
                filter.reset();                                // clear / deactivate
                filter.addFilterChangeListener(e -> refresh()); // value-change listener
                """);
    }

    /**
     * 4. FilterInputGroup wired to BeanListing via addFilterChangeListener.
     */
    private DemoExample filterGroupExample() {
        FilterInput<String> nameFilter = FilterInput.string(NAME_PROP);
        FilterInput<Boolean> activeFilter = FilterInput.bool(ACTIVE_PROP);
        FilterInput<Status> statusFilter = FilterInput.enumeration(STATUS_PROP, Status.class);

        FilterInputGroup filterGroup = FilterInputGroup.builder()
                .withFilter(NAME_PROP, nameFilter)
                .withFilter(ACTIVE_PROP, activeFilter)
                .withFilter(STATUS_PROP, statusFilter)
                .build();

        var listing = BeanListing.builder(Product.class, true)
                .visibleColumns(List.of("name", "category", "price", "active", "status"))
                .header("name", "Name")
                .header("price", "Price (€)")
                .header("active", "Active")
                .header("status", "Status")
                .height("220px")
                .build();

        // Mutable backing list – the callback always streams from it; refresh() re-fetches.
        var shown = new java.util.ArrayList<>(PRODUCTS);
        listing.setItems(q -> shown.stream()
                .skip(q.getOffset()).limit(q.getLimit()));

        var countLabel = new Span(PRODUCTS.size() + " products shown");

        // On any filter change, rebuild the in-memory filtered list.
        filterGroup.addFilterChangeListener(e -> {
            String name = nameFilter.getInput().getValue();
            Boolean active = activeFilter.getInput().getValue();
            Status status = statusFilter.getInput().getValue();

            var filtered = PRODUCTS.stream()
                    .filter(p -> name == null || name.isBlank()
                            || p.getName().toLowerCase().contains(name.toLowerCase()))
                    .filter(p -> active == null || p.isActive() == active)
                    .filter(p -> status == null || p.getStatus() == status)
                    .collect(Collectors.toList());

            shown.clear();
            shown.addAll(filtered);
            listing.getDataProvider().refreshAll();
            countLabel.setText(filtered.size() + " of " + PRODUCTS.size() + " products shown");
        });

        var resetBtn = new Button("Reset filters", VaadinIcon.CLOSE_CIRCLE_O.create());
        resetBtn.addClickListener(e -> filterGroup.resetAll());

        var filterRow = new Div(
                labeledFilter("Name",      nameFilter  .getComponent()),
                labeledFilter("Available", activeFilter.getComponent()),
                labeledFilter("Status",    statusFilter.getComponent()),
                resetBtn);

        var filterBar = new Div(filterRow);

        var container = new Div(filterBar, countLabel, listing.getComponent());

        return new DemoExample("FilterInputGroup + addFilterChangeListener", container, """
                // 1. Create typed FilterInput instances
                FilterInput<String>  nameFilter   = FilterInput.string(NAME_PROP);
                FilterInput<Boolean> activeFilter = FilterInput.bool(ACTIVE_PROP);
                FilterInput<Status>  statusFilter = FilterInput.enumeration(STATUS_PROP, Status.class);
                
                // 2. Compose into a group — combined filter is the AND of all active inputs
                FilterInputGroup group = FilterInputGroup.builder()
                    .withFilter(NAME_PROP,   nameFilter)
                    .withFilter(ACTIVE_PROP, activeFilter)
                    .withFilter(STATUS_PROP, statusFilter)
                    .build();
                
                // 3. Place each filter's UI component manually in a layout
                filterBar.add(
                    nameFilter.getComponent(),
                    activeFilter.getComponent(),
                    statusFilter.getComponent());
                
                // 4. React to any filter change
                group.addFilterChangeListener(e -> {
                    // In a real app: re-run Datastore query with the combined QueryFilter.
                    Optional<QueryFilter> combined = group.getQueryFilter();
                    // Demo: manual in-memory re-filter.
                    listing.setItems(applyFilter(data, nameFilter, activeFilter, statusFilter));
                });
                
                // 5. Reset all inputs at once
                resetButton.addClickListener(e -> group.resetAll());
                
                // 6. Check if any filter is active
                boolean anyActive = group.isAnyActive();
                
                // 7. Access a specific filter by property key
                Optional<FilterInput<String>> fi = group.getFilterInput(NAME_PROP);
                
                // ── Reactive / Signal-based (Vaadin 25) ──
                Signal<Optional<QueryFilter>> filterSignal = group.queryFilterSignal();
                Signal<Boolean> anyActiveSignal            = group.anyActiveSignal();
                """);
    }

    /**
     * 5. FilterInputForm — typed inputs composed onto a layout component automatically.
     *    Unlike FilterInputGroup (which gives you raw inputs to place yourself),
     *    FilterInputForm builds a single rendered component (getComponent()) containing
     *    all inputs. The form fires filter-change events whenever any input changes.
     *    All active inputs are AND-combined into a single QueryFilter automatically.
     */
    private DemoExample filterFormExample() {
        var shown = new java.util.ArrayList<>(PRODUCTS);

        var listing = BeanListing.builder(Product.class, true)
                .visibleColumns(List.of("name", "category", "price", "active", "status"))
                .header("name",   "Name")
                .header("price",  "Price (€)")
                .header("active", "Active")
                .header("status", "Status")
                .height("220px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        // ── Individual inputs — kept as references for in-memory value access ──
        FilterInput<String>  nameInput   = FilterInput.string(NAME_PROP);
        FilterInput<Status>  statusInput = FilterInput.enumeration(STATUS_PROP, Status.class);
        FilterInput<Boolean> activeInput = FilterInput.bool(ACTIVE_PROP);

        // ── FilterInputForm: wraps inputs in a HorizontalLayout toolbar ────────
        // form.getComponent() is the HorizontalLayout — add it directly to the view.
        var form = FilterInputForm.horizontalLayout()
                .withFilter(NAME_PROP,   nameInput)
                .withFilter(STATUS_PROP, statusInput)
                .withFilter(ACTIVE_PROP, activeInput)
                .build();

        var countLabel = new Span(PRODUCTS.size() + " products shown");

        var resetBtn = new Button("Reset filters", VaadinIcon.CLOSE_CIRCLE_O.create());
        resetBtn.addClickListener(e -> form.resetAll());

        // ── Wire: re-filter in-memory on every input change ────────────────────
        form.addFilterChangeListener(e -> {
            String  name   = nameInput.getInput().getValue();
            Status  status = statusInput.getInput().getValue();
            Boolean active = activeInput.getInput().getValue();

            var filtered = PRODUCTS.stream()
                    .filter(p -> name   == null || name.isBlank()
                            || p.getName().toLowerCase().contains(name.toLowerCase()))
                    .filter(p -> status == null || p.getStatus() == status)
                    .filter(p -> active == null || p.isActive() == active)
                    .toList();

            shown.clear();
            shown.addAll(filtered);
            listing.getDataProvider().refreshAll();
            countLabel.setText(filtered.size() == PRODUCTS.size()
                    ? PRODUCTS.size() + " products shown"
                    : filtered.size() + " of " + PRODUCTS.size() + " products shown");
        });

        var toolbar = new Div(form.getComponent(), resetBtn);

        var container = new Div(toolbar, countLabel, listing.getComponent());

        return new DemoExample("FilterInputForm + BeanListing (fixed fields)", container, """
                // FilterInputForm wraps a fixed set of typed FilterInput instances in a
                // rendered layout component.  Unlike FilterInputGroup, you never need to
                // extract getComponent() for each input — the form owns the layout.

                FilterInput<String>  nameInput   = FilterInput.string(NAME_PROP);
                FilterInput<Status>  statusInput = FilterInput.enumeration(STATUS_PROP, Status.class);
                FilterInput<Boolean> activeInput = FilterInput.bool(ACTIVE_PROP);

                // ── Horizontal toolbar (no per-field labels) ──────────────────────────
                FilterInputForm<HorizontalLayout> form = FilterInputForm.horizontalLayout()
                    .withFilter(NAME_PROP,   nameInput)
                    .withFilter(STATUS_PROP, statusInput)
                    .withFilter(ACTIVE_PROP, activeInput)
                    .build();

                // The form component is already a HorizontalLayout — add it directly:
                add(form.getComponent());

                // ── FormLayout with labeled inputs ────────────────────────────────────
                FilterInputForm<FormLayout> formLayout = FilterInputForm.formLayout()
                    .withFilter(NAME_PROP,
                        FilterInput.from(
                            Input.string().label("Name").placeholder("contains…").build(),
                            v -> Optional.ofNullable(v).filter(s -> !s.isBlank())
                                         .map(NAME_PROP::containsIgnoreCase)))
                    .withFilter(STATUS_PROP, statusInput)
                    .withFilter(ACTIVE_PROP, activeInput)
                    .build();
                add(formLayout.getComponent());

                // ── Auto-infer inputs with the property-only shorthand ────────────────
                FilterInputForm<FormLayout> autoForm = FilterInputForm.formLayout()
                    .withFilter(NAME_PROP)    // auto-infers FilterInput.string
                    .withFilter(ACTIVE_PROP)  // auto-infers FilterInput.bool
                    .withFilter(STATUS_PROP)  // auto-infers FilterInput.enumeration
                    .build();

                // ── Wire filter changes (in-memory) ───────────────────────────────────
                form.addFilterChangeListener(e -> {
                    String  name   = nameInput.getInput().getValue();
                    Status  status = statusInput.getInput().getValue();
                    Boolean active = activeInput.getInput().getValue();
                    // rebuild shown list and call listing.getDataProvider().refreshAll();
                });

                // ── Datastore wiring — one-call convenience ───────────────────────────
                listing.bindFilters(form, (query, filter) -> {
                    var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
                    if (filter != null) q.filter(filter);
                    return q.stream(BeanProjection.of(Product.class));
                });
                // OR in two steps:
                listing.setItems(form, (query, filter) -> datastoreStream(query, filter));
                listing.refreshOnFilterChange(form);   // Signal variant: refreshOnFilterSignal(form)

                // ── Reset / state inspection ──────────────────────────────────────────
                form.resetAll();                           // clear all inputs + fire change
                boolean anyActive = form.isAnyActive();    // true if any filter has a value
                Optional<QueryFilter> combined = form.getQueryFilter();  // AND of all active filters
                """);
    }


    /** Wraps a filter component with a small identifying label above it. */
    private static Div labeledFilter(String label, com.vaadin.flow.component.Component input) {
        var lbl = new Span(label);
        var wrapper = new Div(lbl, input);
        return wrapper;
    }

    /**
     * 6. DynamicFilterPanel — type-aware row-based filter builder wired to BeanListing.
     *    Each row: [Select filter] [Operator] [Value(s)] [×]
     *    Operators are type-aware: String gets Contains/Starts With/Ends With,
     *    Number gets >/</≥/≤/Between, Date gets Before/After/On or Before/On or After/Between.
     *    "Apply filter" builds a QueryFilter AND applies an in-memory Predicate<T>.
     */
    private DemoExample dynamicFilterPanelExample() {
        var shown = new java.util.ArrayList<>(PRODUCTS);

        var listing = BeanListing.builder(Product.class, true)
                .visibleColumns(List.of("name", "category", "price", "active", "status", "addedOn"))
                .header("name",    "Name")
                .header("price",   "Price (€)")
                .header("active",  "Active")
                .header("status",  "Status")
                .header("addedOn", "Added On")
                .height("240px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        var panel = DynamicFilterPanel.of(Product.class);

        var countLabel = new Span(PRODUCTS.size() + " products shown");

        // Wire: on Apply, rebuild the in-memory filtered list via toPredicate()
        panel.addFilterChangeListener(e -> {
            shown.clear();
            shown.addAll(PRODUCTS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            countLabel.setText(shown.size() + " of " + PRODUCTS.size() + " products shown");
        });

        var container = new Div(panel, countLabel, listing.getComponent());

        return new DemoExample("DynamicFilterPanel – type-aware row filter builder", container, """
                // 1. Create the panel – bean properties are introspected automatically
                DynamicFilterPanel<Product> panel = DynamicFilterPanel.of(Product.class);
                add(panel);  // embed in your view
                
                // 2. (Optional) switch to OR-combine mode
                panel.setMatchAll(false);  // default is AND
                
                // ── In-memory wiring ──────────────────────────────────────────────────
                var shown = new ArrayList<>(allProducts);
                listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));
                
                panel.addFilterChangeListener(e -> {
                    // toPredicate() evaluates the last applied filter via reflection
                    shown.clear();
                    shown.addAll(allProducts.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });
                
                // ── Datastore wiring (production) ─────────────────────────────────────
                listing.setItems(panel, (query, filter) -> {
                    // filter = combined QueryFilter from panel.getQueryFilter()
                    var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
                    if (filter != null) q.filter(filter);
                    return q.stream(BeanProjection.of(Product.class));
                });
                listing.refreshOnFilterChange(panel);  // refresh on every Apply click
                
                // ── Supported operators by type ────────────────────────────────────────
                // String   : Equals, Not Equals, Contains, Not Contains, Starts With, Ends With,
                //            Is Empty, Is Not Empty
                // Number   : Equals, Not Equals, >, <, >=, <=, Between
                // Date     : Equals, Not Equals, Before, After, On or Before, On or After, Between
                // Boolean  : Equals, Not Equals
                // Enum     : Equals, Not Equals
                """);
    }

    /**
     * 7. DynamicFilterPanel + PropertyListing — demonstrates that the same
     *    panel works identically with PropertyBox-based listings.
     *    Uses DynamicFilterPanel.ofProperties(…) so the actual Holon Property
     *    objects are retained: PropertyBox.getValue(property) is used in
     *    toPredicate() instead of Java reflection.
     */
    private DemoExample dynamicFilterPanelPropertyListingExample() {
        // ── Holon property set (same props as in the demo bean) ──────────────
        StringProperty                        PL_NAME     = StringProperty.create("name");
        PathProperty<String>                  PL_CATEGORY = PathProperty.create("category", String.class);
        PathProperty<Double>                  PL_PRICE    = PathProperty.create("price", Double.class);
        PathProperty<Boolean>                 PL_ACTIVE   = PathProperty.create("active", Boolean.class);
        PathProperty<BeanListingDemoView.Status> PL_STATUS = PathProperty.create("status", BeanListingDemoView.Status.class);
        PathProperty<LocalDate>               PL_DATE     = PathProperty.create("addedOn", LocalDate.class);

        var propertySet = PropertySet.of(PL_NAME, PL_CATEGORY, PL_PRICE, PL_ACTIVE, PL_STATUS, PL_DATE);

        // Convert PRODUCTS to PropertyBox
        var allBoxes = PRODUCTS.stream()
                .map(p -> PropertyBox.builder(propertySet)
                        .set(PL_NAME,     p.getName())
                        .set(PL_CATEGORY, p.getCategory())
                        .set(PL_PRICE,    p.getPrice())
                        .set(PL_ACTIVE,   p.isActive())
                        .set(PL_STATUS,   p.getStatus())
                        .set(PL_DATE,     p.getAddedOn())
                        .build())
                .toList();

        var shown = new java.util.ArrayList<>(allBoxes);

        var listing = PropertyListing.builder(propertySet)
                .header(PL_NAME,     "Name")
                .header(PL_CATEGORY, "Category")
                .header(PL_PRICE,    "Price (€)")
                .header(PL_ACTIVE,   "Active")
                .header(PL_STATUS,   "Status")
                .header(PL_DATE,     "Added On")
                .height("240px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        // DynamicFilterPanel typed as PropertyBox – uses actual Property objects
        var panel = DynamicFilterPanel.ofProperties(
                PL_NAME, PL_CATEGORY, PL_PRICE, PL_ACTIVE, PL_STATUS, PL_DATE);

        var countLabel = new Span(allBoxes.size() + " items shown");

        // Wire via toPredicate() — uses PropertyBox.getValue(property) directly
        panel.addFilterChangeListener(e -> {
            shown.clear();
            shown.addAll(allBoxes.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            countLabel.setText(shown.size() + " of " + allBoxes.size() + " items shown");
        });

        var container = new Div(panel, countLabel, listing.getComponent());

        return new DemoExample("DynamicFilterPanel + PropertyListing (PropertyBox)", container, """
                // ── Define the Holon property set ─────────────────────────────────────
                StringProperty              NAME     = StringProperty.create("name");
                PathProperty<Double>        PRICE    = PathProperty.create("price",   Double.class);
                PathProperty<LocalDate>     DATE     = PathProperty.create("addedOn", LocalDate.class);
                PathProperty<Status>        STATUS   = PathProperty.create("status",  Status.class);
                var propertySet = PropertySet.of(NAME, PRICE, DATE, STATUS);
                
                // ── Build a PropertyListing ───────────────────────────────────────────
                PropertyListing listing = PropertyListing.builder(propertySet)
                    .header(NAME,  "Product Name")
                    .header(PRICE, "Price (€)")
                    .height("240px").build();
                listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));
                
                // ── Create the panel from the same property set ───────────────────────
                // DynamicFilterPanel.ofProperties(…) keeps the actual Property objects,
                // so queries use them directly as filter operands, and toPredicate()
                // uses PropertyBox.getValue(property) for in-memory filtering.
                DynamicFilterPanel<PropertyBox> panel =
                    DynamicFilterPanel.ofProperties(NAME, PRICE, DATE, STATUS);
                add(panel);
                
                // ── OR use ofPropertySet(…) ───────────────────────────────────────────
                DynamicFilterPanel<PropertyBox> panel2 = DynamicFilterPanel.ofPropertySet(propertySet);
                
                // ── In-memory wiring (Predicate<PropertyBox>) ─────────────────────────
                panel.addFilterChangeListener(e -> {
                    shown.clear();
                    shown.addAll(allBoxes.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });
                
                // ── Datastore wiring (production, QueryFilter) ────────────────────────
                listing.setItems(panel, (query, filter) -> {
                    var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
                    if (filter != null) q.filter(filter);
                    return q.stream(propertySet);
                });
                listing.refreshOnFilterChange(panel);  // or refreshOnFilterSignal(panel)
                """);
    }

    /**
     * 8. DynamicFilterPanel — advanced mode.
     *
     * <p>Demonstrates:
     * <ul>
     *   <li>{@link DynamicFilterPanel#setAdvancedMode(boolean)} — each row beyond the first
     *       gains an AND/OR connector selector and a NOT toggle</li>
     *   <li>{@link DynamicFilterPanel#setItems(String, List)} — registers a static list of
     *       items so that {@code IN} / {@code NOT_IN} operators render a
     *       {@code MultiSelectComboBox} for the {@code category} field</li>
     *   <li>Enum auto-population — {@code Status} constants are populated automatically
     *       without any registration</li>
     *   <li>All wiring is identical to simple mode: {@link DynamicFilterPanel#toPredicate()}
     *       evaluates the last-applied filter respecting connectors and NOT flags</li>
     * </ul>
     */
    private DemoExample advancedLogicalFilterPanelExample() {
        var shown = new java.util.ArrayList<>(PRODUCTS);

        var listing = BeanListing.builder(Product.class, true)
                .visibleColumns(List.of("name", "category", "price", "active", "status", "addedOn"))
                .header("name",     "Name")
                .header("category", "Category")
                .header("price",    "Price (€)")
                .header("active",   "Active")
                .header("status",   "Status")
                .header("addedOn",  "Added On")
                .height("240px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        // Advanced mode: every row (except the first) shows an AND/OR connector selector
        // on the left and a NOT toggle on the right.
        var panel = DynamicFilterPanel.of(Product.class)
                .setAdvancedMode(true);

        // Register items for the "category" String property so that "Is One Of" /
        // "Is Not One Of" operators show a searchable multi-select CheckBox dropdown.
        panel.setItems("category", List.of("Electronics", "Accessories", "Office"));

        // "status" is an Enum → constants are auto-populated; no setItems() call needed.

        var countLabel = new Span(PRODUCTS.size() + " products shown");

        panel.addFilterChangeListener(e -> {
            shown.clear();
            // toPredicate() fully respects connectors (AND/OR/XOR/…) and NOT flags
            shown.addAll(PRODUCTS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            countLabel.setText(shown.size() + " of " + PRODUCTS.size() + " products shown");
        });

        var container = new Div(panel, countLabel, listing.getComponent());

        return new DemoExample(
                "DynamicFilterPanel – Advanced Mode (logical connectors + multi-value IN/NOT_IN)",
                container, """
                // ── 1. Enable advanced mode ───────────────────────────────────────────
                DynamicFilterPanel<Product> panel = DynamicFilterPanel.of(Product.class)
                    .setAdvancedMode(true);
                // Each row 2+ gains:
                //   • A connector selector on the left  (And / Or / And Not / Or Not / Nand / Nor / Xor)
                //   • A NOT toggle on the right — negates the row's own condition first
                
                // ── 2. Register items for multi-value operators ───────────────────────
                // String properties: register a static list so that "Is One Of" / "Is Not One Of"
                // renders a searchable MultiSelectComboBox instead of a plain text field.
                panel.setItems("category", List.of("Electronics", "Accessories", "Office"));
                
                // Enum properties auto-populate from their constants — no registration needed.
                // Status.AVAILABLE / OUT_OF_STOCK / DISCONTINUED appear automatically.
                
                // ── 3. Lazy (database-backed) multi-select ────────────────────────────
                panel.setLazyItems("team",
                    query -> teamService.find(
                        query.getFilter().orElse(""),   // text typed in the search box
                        query.getOffset(), query.getLimit()),
                    query -> teamService.count(query.getFilter().orElse("")));
                
                // ── 4. Wire filtering (identical to simple mode) ──────────────────────
                panel.addFilterChangeListener(e -> {
                    shown.clear();
                    // toPredicate() evaluates connectors + NOT flags via in-memory reflection
                    shown.addAll(allProducts.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });
                
                // ── Datastore wiring (production) — getQueryFilter() returns the combined filter ──
                listing.setItems(panel, (query, filter) -> {
                    var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
                    if (filter != null) q.filter(filter);
                    return q.stream(BeanProjection.of(Product.class));
                });
                listing.refreshOnFilterChange(panel);
                
                // ── Available connectors (rows 2+) ────────────────────────────────────
                // AND      → acc AND cond
                // OR       → acc OR  cond
                // AND_NOT  → acc AND NOT cond
                // OR_NOT   → acc OR  NOT cond
                // NAND     → NOT(acc AND cond)
                // NOR      → NOT(acc OR  cond)
                // XOR      → (acc AND NOT cond) OR (NOT acc AND cond)
                
                // ── Operators available for multi-value (IN/NOT_IN) ───────────────────
                // String  → Is One Of,     Is Not One Of   (requires setItems / setLazyItems)
                // Enum    → Is One Of,     Is Not One Of   (auto-populated)
                """);
    }

    /**
     * 9. Paginated BeanListing — page-by-page slice + Pagination navigation component.
     */
    private DemoExample paginatedExample() {
        final int total = PRODUCTS.size();
        final int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        final int[] page = {1};

        // Mutable page buffer – the fetch callback always streams from it.
        var pageBuffer = new java.util.ArrayList<>(PRODUCTS.subList(0, PAGE_SIZE));

        var listing = BeanListing.builder(Product.class, true)
                .visibleColumns(List.of("id", "name", "category", "price", "active"))
                .header("id", "#")
                .header("name", "Name")
                .header("category", "Category")
                .header("price", "Price (€)")
                .header("active", "Active")
                .allRowsVisible(true)
                .build();
        listing.setItems(q -> pageBuffer.stream()
                .skip(q.getOffset()).limit(q.getLimit()));

        var pageInfo = new Span();

        var paginationHolder = new Div();

        // Initial render
        updatePageInfo(pageInfo, page[0], totalPages, total);
        rebuildPagination(paginationHolder, page, totalPages, total, listing, pageBuffer, pageInfo);

        var paginationBar = new Div(paginationHolder, pageInfo);

        var container = new Div(listing.getComponent(), paginationBar);

        return new DemoExample("Paginated BeanListing + Pagination component", container, """
                // Keep page size and current-page state.
                final int PAGE_SIZE   = 5;
                final int totalPages  = (int) Math.ceil((double) total / PAGE_SIZE);
                final int[] page      = {1};
                
                // Mutable buffer drives the fetch callback — no re-wiring of data provider needed.
                List<Product> pageBuffer = new ArrayList<>(getPage(1));
                BeanListing<Product> listing = BeanListing.builder(Product.class, true)
                    .visibleColumns(List.of("id", "name", "category", "price", "active"))
                    .allRowsVisible(true)   // show all rows without inner grid scroll
                    .build();
                listing.setItems(q -> pageBuffer.stream());
                
                // Navigate to a page: update buffer and refresh the data provider.
                void goToPage(int p) {
                    page[0] = p;
                    int offset = (p - 1) * PAGE_SIZE;
                    pageBuffer.clear();
                    pageBuffer.addAll(allProducts.subList(offset,
                        Math.min(offset + PAGE_SIZE, total)));
                    listing.getDataProvider().refreshAll();
                    rebuildPagination(holder, page, totalPages);
                }
                
                // Rebuild Pagination component on each page change:
                void rebuildPagination(Div holder, int[] page, int totalPages) {
                    holder.removeAll();
                    Pagination pg = new Pagination();
                    PaginationContent c = pg.getContent();
                
                    PaginationPrevious prev = new PaginationPrevious();
                    prev.setDisabled(page[0] == 1);
                    prev.addClickListener(e -> { if (page[0] > 1) goToPage(page[0] - 1); });
                    c.add(new PaginationItem(prev));
                
                    for (int p = 1; p <= totalPages; p++) {
                        final int target = p;
                        PaginationLink link = new PaginationLink(p, p == page[0]);
                        link.addClickListener(e -> goToPage(target));
                        c.add(new PaginationItem(link));
                    }
                
                    PaginationNext next = new PaginationNext();
                    next.setDisabled(page[0] == totalPages);
                    next.addClickListener(e -> { if (page[0] < totalPages) goToPage(page[0] + 1); });
                    c.add(new PaginationItem(next));
                
                    holder.add(pg);
                }
                
                // Combining with FilterInputGroup (reset to page 1 on filter change):
                filterGroup.addFilterChangeListener(e -> { page[0] = 1; goToPage(1); });
                """);
    }

    // ── Example 10 — ItemListingPaginationBar + PageSizeSelector + search ────

    /**
     * 10. Demonstrates {@link ItemListingPaginationBar} + {@link com.holonplatform.vaadin.flow.components.ItemListingPageSizeSelector}
     * wired together with a search {@link TextField}.
     *
     * <p>Key points:
     * <ul>
     *   <li>Zero {@code COUNT(*)} queries — look-ahead fetches {@code pageSize+1} rows to detect next page.</li>
     *   <li>Filter text is read from the {@code TextField} closure at fetch time, always fresh.</li>
     *   <li>{@code ValueChangeMode.LAZY} debounces keystrokes to 400 ms.</li>
     * </ul>
     */
    private DemoExample paginationBarWithSearchExample() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "active")
                .pageSizes(3, 5, 10)
                .defaultPageSize(5)
                .search("Search by name or category…")
                .fetch((q, text) -> PRODUCTS.stream()
                        .filter(p -> text.isBlank()
                                || p.getName().toLowerCase().contains(text.toLowerCase())
                                || p.getCategory().toLowerCase().contains(text.toLowerCase()))
                        .skip(q.getOffset())
                        .limit(q.getLimit()))
                .build();

        var container = new Div(bundle.toolbar(), bundle.grid(), bundle.footer());

        return new DemoExample(
                "10. ItemListingPaginationBar + PageSizeSelector + Search (zero COUNT(*))",
                container,
                """
                // Single chained call — listing, bar, selector and search are all pre-wired.
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price")
                    .pageSizes(10, 25, 50)
                    .defaultPageSize(10)
                    .search("Search products…")          // adds TextField with LAZY debounce
                    .fetch((q, text) -> service.fetch(   // look-ahead: zero COUNT(*) queries
                            q.getOffset(),
                            q.getLimit(),                // wrapper requests limit+1 internally
                            text))
                    .build();

                // Layout — three lines, no manual wiring
                add(bundle.toolbar(),   // [Show 10▾ entries]    [🔍 Search…]
                    bundle.grid(),
                    bundle.footer());   // [Previous] [1] [2] [Next]

                // Optional: access individual components
                BeanListing<Product>              listing = bundle.listing();
                ItemListingPaginationBar<Product,?> bar   = bundle.bar();
                TextField                          search = bundle.search();

                // With DynamicFilterPanel:
                var bundle2 = Components.listing(Product.class)
                    .columns("id", "name", "price")
                    .search("Quick search…")
                    .withFilterPanel()              // adds DynamicFilterPanel
                    .fetch((q, text, filter) -> {  // receives both search text + QueryFilter
                        var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
                        if (filter != null) q2.filter(filter);
                        if (!text.isBlank()) q2.filter(NAME.containsIgnoreCase(text));
                        return q2.stream(BeanProjection.of(Product.class));
                    })
                    .build();

                add(bundle2.filterPanel(),   // DynamicFilterPanel (null if not configured)
                    bundle2.toolbar(),
                    bundle2.grid(),
                    bundle2.footer());
                """);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Adds a labelled filter-input row to the showcase grid.
     */
    private static void addFilterRow(Div grid, String caption, FilterInput<?> fi) {
        var label = new Span(caption);
        var row = new Div(label, fi.getComponent());
        grid.add(row);
    }

    /**
     * Updates the page-info label text.
     */
    private static void updatePageInfo(Span pageInfo, int page, int totalPages, int total) {
        int offset = (page - 1) * PAGE_SIZE;
        pageInfo.setText("Page " + page + " of " + totalPages
                + "  ·  Items " + (offset + 1) + "–" + Math.min(offset + PAGE_SIZE, total)
                + " of " + total);
    }

    /**
     * Clears the holder and builds a fresh Pagination bar wired to buffer mutation + refresh.
     */
    private void rebuildPagination(Div holder, int[] page, int totalPages, int total,
                                   BeanListing<Product> listing,
                                   java.util.ArrayList<Product> pageBuffer, Span pageInfo) {
        holder.removeAll();
        var pg = new Pagination();
        PaginationContent c = pg.getContent();

        // Previous
        var prev = new PaginationPrevious();
        prev.setDisabled(page[0] == 1);
        prev.addClickListener(e -> {
            if (page[0] > 1) {
                page[0]--;
                refreshPage(page[0], total, listing, pageBuffer, pageInfo,
                        totalPages, holder);
            }
        });
        c.add(new PaginationItem(prev));

        // Page links (with ellipsis for large page counts)
        if (totalPages <= 7) {
            for (int p = 1; p <= totalPages; p++) {
                final int target = p;
                var link = new PaginationLink(target, target == page[0]);
                link.addClickListener(e -> refreshPage(target, total, listing,
                        pageBuffer, pageInfo, totalPages, holder));
                c.add(new PaginationItem(link));
            }
        } else {
            addPageLinks(c, 1, Math.min(2, page[0] - 1), page[0], totalPages, total,
                    listing, pageBuffer, pageInfo, holder, page);
            if (page[0] > 4) c.add(new PaginationItem(new PaginationEllipsis()));
            int pFrom = Math.max(3, page[0] - 1);
            int pTo = Math.min(totalPages - 2, page[0] + 1);
            addPageLinks(c, pFrom, pTo, page[0], totalPages, total,
                    listing, pageBuffer, pageInfo, holder, page);
            if (page[0] < totalPages - 3) c.add(new PaginationItem(new PaginationEllipsis()));
            addPageLinks(c, Math.max(page[0] + 2, totalPages - 1), totalPages, page[0],
                    totalPages, total, listing, pageBuffer, pageInfo, holder, page);
        }

        // Next
        var next = new PaginationNext();
        next.setDisabled(page[0] == totalPages);
        next.addClickListener(e -> {
            if (page[0] < totalPages) {
                page[0]++;
                refreshPage(page[0], total, listing, pageBuffer, pageInfo,
                        totalPages, holder);
            }
        });
        c.add(new PaginationItem(next));

        holder.add(pg);
    }

    /**
     * Updates the page buffer, refreshes the data provider, and rebuilds pagination.
     */
    private void refreshPage(int targetPage, int total, BeanListing<Product> listing,
                             java.util.ArrayList<Product> pageBuffer, Span pageInfo,
                             int totalPages, Div holder) {
        int offset = (targetPage - 1) * PAGE_SIZE;
        pageBuffer.clear();
        pageBuffer.addAll(PRODUCTS.subList(offset, Math.min(offset + PAGE_SIZE, total)));
        listing.getDataProvider().refreshAll();
        updatePageInfo(pageInfo, targetPage, totalPages, total);
        int[] pageRef = {targetPage};
        rebuildPagination(holder, pageRef, totalPages, total, listing, pageBuffer, pageInfo);
    }

    private void addPageLinks(PaginationContent c, int from, int to, int current,
                              int totalPages, int total,
                              BeanListing<Product> listing,
                              java.util.ArrayList<Product> pageBuffer, Span pageInfo,
                              Div holder, int[] page) {
        for (int p = from; p <= to; p++) {
            final int target = p;
            var link = new PaginationLink(target, target == current);
            link.addClickListener(e -> {
                page[0] = target;
                refreshPage(target, total, listing, pageBuffer, pageInfo, totalPages, holder);
            });
            c.add(new PaginationItem(link));
        }
    }
}







