package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListingPaginationBar;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.util.stream.Stream;
import java.util.stream.Stream;

/**
 * Demo page for {@link com.holonplatform.vaadin.flow.components.ListingBundleBuilder} and
 * {@link com.holonplatform.vaadin.flow.components.PropertyListingBundleBuilder}.
 *
 * <p>All examples are backed by real JPA data via {@link ProductService} + Holon Datastore.
 * Lazy loading is used throughout — the grid only fetches the current page from the DB each time.
 *
 * <p>Covers:
 * <ol>
 *   <li>Minimal bundle — columns + pagination, no search, no filter panel</li>
 *   <li>Bundle with search field (debounced, DB text filter)</li>
 *   <li>Bundle with search + DynamicFilterPanel (combined text + structured QueryFilter → DB)</li>
 *   <li>Custom page sizes + default page size override</li>
 *   <li>Custom column headers</li>
 *   <li>PropertyBox variant via {@code Components.listing(PropertySet)}</li>
 *   <li>Accessing individual bundle components after build</li>
 *   <li>Default empty state — shown when the dataset is genuinely empty</li>
 *   <li>Both empty states — empty dataset vs search/filter that yields no results</li>
 * </ol>
 */
@PageTitle("ListingBundle – Holon Demo")
@Route(value = "listing-bundle", layout = DemoMainLayout.class)
public class ListingBundleDemoView extends Div {

    // ── PropertyBox variant property descriptors (example 6) ─────────────────
    // Must match entity field names so the Holon JPA Datastore maps them correctly.
    private static final StringProperty           PB_NAME     = StringProperty.create("name");
    private static final StringProperty           PB_CATEGORY = StringProperty.create("category");
    private static final PathProperty<BigDecimal> PB_PRICE    = PathProperty.create("price", BigDecimal.class);
    private static final PathProperty<Boolean>    PB_ACTIVE   = PathProperty.create("active", Boolean.class);
    private static final PropertySet<?>           PB_SET      = PropertySet.of(PB_NAME, PB_CATEGORY, PB_PRICE, PB_ACTIVE);

    // ── Spring-injected service (Vaadin Spring manages route views as beans) ──
    private final transient ProductService productService;

    public ListingBundleDemoView(ProductService productService) {
        this.productService = productService;

        addClassName("app-view");

        add(new H1("ListingBundle"),
                new Paragraph(
                        "ListingBundleBuilder assembles a BeanListing, an ItemListingPaginationBar, " +
                        "an ItemListingPageSizeSelector, an optional search TextField, and an optional " +
                        "DynamicFilterPanel in a single chained call. " +
                        "All examples below are backed by real JPA data via the Holon Datastore — " +
                        "only the current page is fetched from the database on each navigation."),
                example1MinimalBundle(),
                example2WithSearch(),
                example3WithFilterPanel(),
                example4CustomPageSizes(),
                example5CustomHeaders(),
                example6PropertyBoxVariant(),
                example7AccessComponents(),
                example8EmptyState(),
                example9BothEmptyStates());
    }

    // ── Example 1 — minimal bundle (no search, no filter panel) ─────────────

    private DemoExample example1MinimalBundle() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "active")
                .pageSizes(5, 10, 25)
                .defaultPageSize(5)
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), ""))
                .build();

        return new DemoExample(
                "1. Minimal bundle — columns + pagination (no search, no filter panel)",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price", "active")
                    .pageSizes(5, 10, 25)
                    .defaultPageSize(5)
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), ""))
                    .build();

                // Three-line layout — everything is pre-wired
                add(bundle.toolbar(),   // [Show 5▾ entries]
                    bundle.grid(),
                    bundle.footer());   // [Previous] [1] [2] … [Next]
                """);
    }

    // ── Example 2 — with search field ─────────────────────────────────────────

    private DemoExample example2WithSearch() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10, 25)
                .defaultPageSize(5)
                .search("Search by name or category…")
                // text is debounced (ValueChangeMode.LAZY) and forwarded directly to the DB
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        return new DemoExample(
                "2. Bundle with search field (ValueChangeMode.LAZY — 400 ms debounce, DB-backed)",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price")
                    .pageSizes(5, 10, 25)
                    .defaultPageSize(5)
                    .search("Search by name or category…")  // adds TextField with LAZY debounce
                    .fetch((q, text) ->                      // text goes straight to the DB
                        productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();

                add(bundle.toolbar(),   // [Show 5▾ entries]    [🔍 Search…]
                    bundle.grid(),
                    bundle.footer());
                """);
    }

    // ── Example 3 — with search + DynamicFilterPanel ──────────────────────────
    // Uses FilteredFetchCallback (3-arg) so the QueryFilter built by the panel is
    // forwarded directly to the Holon Datastore — no in-memory Predicate workaround.

    private DemoExample example3WithFilterPanel() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "active")
                .pageSizes(5, 10, 25)
                .defaultPageSize(5)
                .search("Quick search…")
                .withFilterPanel()
                .fetch((q, text, filter, sort) ->
                    // filter is the QueryFilter from the DynamicFilterPanel (null when no filter applied).
                    // text + filter are both AND-combined in the service query.
                    productService.fetch(       // text + filter AND-combined in the Datastore query
                        q.getOffset(), q.getLimit(), text, filter, sort))
                .build();

        return new DemoExample(
                "3. Bundle with search + DynamicFilterPanel — QueryFilter forwarded to DB",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price", "active")
                    .pageSizes(5, 10, 25)
                    .search("Quick search…")
                    .withFilterPanel()              // panel rendered in "Advanced Search" dialog
                    .fetch((q, text, filter) ->    // receives both search text AND QueryFilter
                        productService.fetch(       // text + filter AND-combined in the Datastore query
                            q.getOffset(), q.getLimit(), text, filter))
                    .build();

                // In ProductService:
                // public Stream<Product> fetch(int offset, int limit, String text, QueryFilter filter) {
                //     var q = datastore.query(TARGET).restrict(limit, offset).sort(NAME.asc());
                //     if (!text.isBlank()) q = q.filter(nameOrCategoryFilter(text));
                //     if (filter != null)  q = q.filter(filter);    // ← Holon QueryFilter applied to DB
                //     return q.stream(BeanProjection.of(Product.class));
                // }

                add(bundle.toolbar(), bundle.grid(), bundle.footer());
                """);
    }

    // ── Example 4 — custom page sizes ────────────────────────────────────────

    private DemoExample example4CustomPageSizes() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "price")
                .pageSizes(3, 7, 15, 30)
                .defaultPageSize(7)
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), ""))
                .build();

        return new DemoExample(
                "4. Custom page sizes — [3, 7, 15, 30] with default 7",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "price")
                    .pageSizes(3, 7, 15, 30)   // dropdown shows these options
                    .defaultPageSize(7)         // selected on first render
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), ""))
                    .build();
                """);
    }

    // ── Example 5 — custom column headers ────────────────────────────────────

    private DemoExample example5CustomHeaders() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "createdDate")
                .header("id",          "#")
                .header("name",        "Product Name")
                .header("category",    "Type")
                .header("price",       "Price (€)")
                .header("createdDate", "Date Added")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search products…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        return new DemoExample(
                "5. Custom column headers via .header(col, label)",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price", "createdDate")
                    .header("id",          "#")
                    .header("name",        "Product Name")
                    .header("category",    "Type")
                    .header("price",       "Price (€)")
                    .header("createdDate", "Date Added")
                    .pageSizes(5, 10)
                    .search("Search products…")
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();
                """);
    }

    // ── Example 6 — PropertyBox variant ──────────────────────────────────────
    // Uses Components.listing(PropertySet) + inline mapping Product → PropertyBox.
    // The PropertySet properties must match entity field names so the grid columns
    // resolve correctly.  The actual DB fetch still uses BeanProjection; mapping is
    // done in the lambda so no Datastore generics workaround is needed.

    private DemoExample example6PropertyBoxVariant() {
        var bundle = Components.listing(PB_SET)
                .header(PB_NAME,     "Product Name")
                .header(PB_CATEGORY, "Category")
                .header(PB_PRICE,    "Price (€)")
                .header(PB_ACTIVE,   "Active?")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search by name…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text)
                        // Map each Product bean to a PropertyBox so PropertyListing can render it.
                        // ProductService.fetch returns Stream<Product> — the mapping is done here
                        // rather than in the service to avoid Holon PropertySet stream-overload
                        // ambiguity with the Datastore generic API.
                        .map(p -> PropertyBox.builder(PB_SET)
                                .set(PB_NAME,     p.getName())
                                .set(PB_CATEGORY, p.getCategory())
                                .set(PB_PRICE,    p.getPrice())
                                .set(PB_ACTIVE,   p.isActive())
                                .build()))
                .build();

        return new DemoExample(
                "6. PropertyBox variant via Components.listing(PropertySet) — DB-backed",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                // Property descriptors — names must match entity/table field names
                StringProperty           NAME     = StringProperty.create("name");
                StringProperty           CATEGORY = StringProperty.create("category");
                PathProperty<BigDecimal> PRICE    = PathProperty.create("price", BigDecimal.class);
                PathProperty<Boolean>    ACTIVE   = PathProperty.create("active", Boolean.class);
                PropertySet<?>           SET      = PropertySet.of(NAME, CATEGORY, PRICE, ACTIVE);

                var bundle = Components.listing(SET)
                    .header(NAME,  "Product Name")
                    .header(PRICE, "Price (€)")
                    .pageSizes(5, 10)
                    .search("Search by name…")
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), text)
                            // Map Product bean → PropertyBox so PropertyListing can render it
                            .map(p -> PropertyBox.builder(SET)
                                    .set(NAME,     p.getName())
                                    .set(CATEGORY, p.getCategory())
                                    .set(PRICE,    p.getPrice())
                                    .set(ACTIVE,   p.isActive())
                                    .build()))
                    .build();

                add(bundle.toolbar(), bundle.grid(), bundle.footer());
                """);
    }

    // ── Example 7 — accessing individual components after build ──────────────

    private DemoExample example7AccessComponents() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Demonstrate typed access to individual pieces
        ItemListing<Product, ?>             listing = bundle.listing();
        ItemListingPaginationBar<Product,?> bar     = bundle.bar();
        TextField                           search  = bundle.getSearchOptional().orElse(null);

        assert listing != null;
        assert bar     != null;
        assert search  != null;

        return new DemoExample(
                "7. Accessing individual components after build (listing, bar, search, filterPanel)",
                new Div(bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                ListingBundle<Product> bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price")
                    .search("Search…")
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();

                // Individual components — all pre-wired, can be further customised
                ItemListing<Product, ?>             listing = bundle.listing();
                ItemListingPaginationBar<Product,?> bar     = bundle.bar();
                TextField                           search  = bundle.search();      // null if not configured
                DynamicFilterPanel<Product>         panel   = bundle.filterPanel(); // null if not configured

                // Standard 3-slot layout
                add(bundle.toolbar(),   // [Show 5▾ entries]  [🔍 Search…]
                    bundle.grid(),
                    bundle.footer());   // [Previous] [1] [2] [Next]
                """);
    }

    // ── Example 8 — default empty state ──────────────────────────────────────
    // The fetch callback always returns an empty stream, so the grid immediately
    // shows the configured empty-state component instead of a blank grid.

    private DemoExample example8EmptyState() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .fetch((q, text, sort) -> Stream.empty())   // always empty → triggers emptyState
                .emptyState()                               // default: INBOX icon + "No items"
                .build();

        return new DemoExample(
                "8. Default empty state — shown when the dataset is genuinely empty",
                bundle,
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price")
                    .fetch((q, text, sort) -> Stream.empty())  // always empty → triggers emptyState
                    .emptyState()   // activates the default: inbox icon + "No items"
                    .build();

                add(bundle);  // ListingBundle is a self-contained Div — just add it

                // Custom empty state:
                // .emptyState(Empty.builder()
                //     .icon(new Icon(VaadinIcon.ARCHIVE))
                //     .title("No products yet")
                //     .description("Add your first product to get started.")
                //     .action(new Button("Add product", e -> navigator.navigateTo(AddView.class)))
                //     .build())
                """);
    }

    // ── Example 9 — both empty states (empty dataset vs no-results) ───────────
    // .emptyState()     → shown when no search/filter is active and the grid is empty
    // .noResultsState() → shown when search text is entered but matches nothing
    // Type "zzznomatch" in the search field to see the no-results state;
    // clear the field to return to the empty-dataset state.

    private DemoExample example9BothEmptyStates() {
        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search — type 'zzznomatch' to see no-results state…")
                // Return real data only when the search text exactly matches a magic word so both
                // empty states are reachable without adding a dedicated empty table.
                .fetch((q, text, sort) -> text.isBlank()
                        ? Stream.empty()                                                    // empty dataset
                        : productService.fetch(q.getOffset(), q.getLimit(), text))          // real search
                .emptyState(Empty.builder()
                        .icon(new com.vaadin.flow.component.icon.Icon(VaadinIcon.INBOX))
                        .title("No items")
                        .description("The product catalogue is currently empty. Add your first product to get started.")
                        .build())
                .noResultsState(Empty.builder()
                        .icon(new com.vaadin.flow.component.icon.Icon(VaadinIcon.SEARCH))
                        .title("No results found")
                        .description("No products match your search. Try different keywords or clear the search field.")
                        .build())
                .build();

        return new DemoExample(
                "9. Both empty states — empty dataset vs search returning no results",
                bundle,
                """
                var bundle = Components.listing(Product.class)
                    .columns("id", "name", "category", "price")
                    .search("Search products…")
                    .fetch((q, text, sort) -> text.isBlank()
                            ? Stream.empty()                                        // triggers emptyState
                            : productService.fetch(q.getOffset(), q.getLimit(), text))  // may trigger noResultsState
                    // Shown when dataset is empty and NO search/filter is active:
                    .emptyState(Empty.builder()
                            .icon(new Icon(VaadinIcon.INBOX))
                            .title("No items")
                            .description("The product catalogue is currently empty.")
                            .build())
                    // Shown when search/filter is active but yields zero records:
                    .noResultsState(Empty.builder()
                            .icon(new Icon(VaadinIcon.SEARCH))
                            .title("No results found")
                            .description("No products match your search. Try different keywords.")
                            .build())
                    .build();

                add(bundle);
                // Detection is automatic — the bundle checks after every fetch whether:
                //   count == 0 && no search/filter → emptyState
                //   count == 0 && search or filter active → noResultsState
                //   count > 0                      → grid (both empty states hidden)
                """);
    }
}

