package com.holonplatform.vaadin.flow.demo.ui.views;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.DynamicFilterPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.Serial;

/**
 * Demo and visual test fixture for the {@link com.iyensoft.vaadin.flow.components.GridToolbar}
 * component backed by JPA entity data ({@link Product}) via {@link ProductService} and Holon Datastore.
 */
@PageTitle("Grid Toolbar - Holon Demo")
@Route(value = "grid-toolbar", layout = DemoMainLayout.class)
public class GridToolbarDemoView extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient ProductService productService;

    public GridToolbarDemoView(ProductService productService) {
        this.productService = productService;
        addClassName("app-view");
        add(new H1("Grid Toolbar"), new Paragraph(
                "Grid toolbar with search, DynamicFilterPanel, selection, and responsive bulk actions backed by JPA and Holon Datastore."),
                toolbarExample());
    }

    private DemoExample toolbarExample() {
        // Two independent previews. Each needs its OWN Grid: a Vaadin Component has a
        // single parent, so reusing one instance in both Divs silently reparented it
        // into whichever preview was constructed last, leaving the other showing a
        // toolbar with no grid under it (and its search wired to nothing).
        var plainPreview = buildPlainPreview();
        var filterPreview = buildFilterPreview();

        var mobileToggle = new Button("Simulate mobile width (380px)", VaadinIcon.MOBILE.create(), event -> {
            boolean mobile = plainPreview.getElement().getClassList().contains("grid-toolbar-preview--mobile");
            plainPreview.getElement().getClassList().set("grid-toolbar-preview--mobile", !mobile);
            plainPreview.getStyle().set("max-width", mobile ? "920px" : "380px");
        });
        mobileToggle.setId("grid-toolbar-mobile-toggle");

        var example = new Div(mobileToggle, plainPreview, filterPreview);
        example.getStyle().set("display", "grid").set("gap", "var(--lumo-space-m)");

        return new DemoExample("Products - toolbar without and with DynamicFilterPanel", example, """
                GridToolbar plainToolbar = Components.gridToolbar()
                        .selectionGrid(grid)
                        .searchPlaceholder("Search products…")
                        .primaryAction(new Button("Add product"))
                        .build();

                GridToolbar toolbar = Components.gridToolbar()
                        .selectionGrid(grid)
                        .searchPlaceholder("Search products…")
                        .filterPanel(DynamicFilterPanel.of(Product.class))
                        .primaryAction(new Button("Add product"))
                        .build();

                // Wire search & DynamicFilterPanel to ProductService (JPA Datastore):
                CallbackDataProvider<Product, Void> dataProvider = DataProvider.fromFilteringCallbacks(
                    q -> productService.fetch(q.getOffset(), q.getLimit(), search, filterPanel.getQueryFilter().orElse(null), null),
                    q -> (int) productService.count(search, filterPanel.getQueryFilter().orElse(null))
                );
                grid.setDataProvider(dataProvider);
                toolbar.getSearchField().addValueChangeListener(e -> { search = e.getValue(); dataProvider.refreshAll(); });
                filterPanel.addFilterChangeListener(e -> dataProvider.refreshAll());
                """);
    }

    /** Toolbar WITHOUT a DynamicFilterPanel — search + selection + bulk actions only. */
    private Div buildPlainPreview() {
        var grid = createProductGrid("grid-toolbar-products");
        final String[] searchText = {""};

        var dataProvider = createDataProvider(searchText, null);
        grid.setDataProvider(dataProvider);

        var toolbar = Components.gridToolbar()
                .selectionGrid(grid)
                .searchPlaceholder("Search products…")
                .primaryAction(new Button("Add product", event -> Notification.show("Product form requested")))
                .bulkAction("Export", () -> Notification.show("Exporting " + grid.getSelectedItems().size() + " products"))
                .bulkAction("Assign", () -> Notification.show("Assigning " + grid.getSelectedItems().size() + " products"))
                .destructiveBulkAction("Delete", () -> Notification.show("Deleting " + grid.getSelectedItems().size() + " products"))
                .build();
        toolbar.setId("grid-toolbar-demo");
        toolbar.getElement().setAttribute("data-testid", "grid-toolbar-demo");

        // Every toolbar must drive its own grid; previously only the filtered toolbar
        // was wired, so typing in this one did nothing.
        toolbar.getSearchField().addValueChangeListener(event -> {
            searchText[0] = event.getValue();
            dataProvider.refreshAll();
        });

        return createPreview("grid-toolbar-preview", toolbar, grid);
    }

    /** Toolbar WITH a DynamicFilterPanel layered on top of the same search behaviour. */
    private Div buildFilterPreview() {
        var grid = createProductGrid("grid-toolbar-products-filtered");
        DynamicFilterPanel<Product> filterPanel = DynamicFilterPanel.of(Product.class);
        final String[] searchText = {""};

        var dataProvider = createDataProvider(searchText, filterPanel);
        grid.setDataProvider(dataProvider);

        var toolbar = Components.gridToolbar()
                .selectionGrid(grid)
                .searchPlaceholder("Search products…")
                .filterPanel(filterPanel)
                .primaryAction(new Button("Add product", event -> Notification.show("Product form requested")))
                .bulkAction("Export", () -> Notification.show("Exporting " + grid.getSelectedItems().size() + " products"))
                .bulkAction("Assign", () -> Notification.show("Assigning " + grid.getSelectedItems().size() + " products"))
                .destructiveBulkAction("Delete", () -> Notification.show("Deleting " + grid.getSelectedItems().size() + " products"))
                .build();
        toolbar.setId("grid-toolbar-filter-demo");
        toolbar.getElement().setAttribute("data-testid", "grid-toolbar-filter-demo");

        toolbar.getSearchField().addValueChangeListener(event -> {
            searchText[0] = event.getValue();
            dataProvider.refreshAll();
        });
        filterPanel.addFilterChangeListener(event -> dataProvider.refreshAll());

        return createPreview("grid-toolbar-filter-preview", toolbar, grid);
    }

    private static Grid<Product> createProductGrid(String testId) {
        var grid = new Grid<Product>();
        grid.setId(testId);
        grid.getElement().setAttribute("data-testid", testId);
        grid.addColumn(Product::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Product::getName).setHeader("Name").setFlexGrow(1);
        grid.addColumn(Product::getCategory).setHeader("Category").setAutoWidth(true);
        grid.addColumn(p -> p.getPrice() != null ? "$" + p.getPrice() : "").setHeader("Price").setAutoWidth(true);
        grid.addColumn(p -> p.isActive() ? "Active" : "Inactive").setHeader("Status").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        return grid;
    }

    /**
     * @param filterPanel optional DynamicFilterPanel contributing a QueryFilter; may be null
     *                    for the plain toolbar, which searches by text only.
     */
    private CallbackDataProvider<Product, Void> createDataProvider(String[] searchText,
                                                                   DynamicFilterPanel<Product> filterPanel) {
        return DataProvider.fromFilteringCallbacks(
                query -> productService.fetch(
                        query.getOffset(),
                        query.getLimit(),
                        searchText[0],
                        queryFilterOf(filterPanel),
                        null),
                query -> (int) productService.count(
                        searchText[0],
                        queryFilterOf(filterPanel))
        );
    }

    /**
     * Null-safe accessor for the panel's current filter.
     *
     * <p>The parameter must stay parameterised as {@code DynamicFilterPanel<Product>}: a raw
     * {@code DynamicFilterPanel} erases every generic member, so {@code getQueryFilter()}
     * returns a raw {@code Optional} and {@code orElse(null)} yields {@code Object}.
     */
    private static QueryFilter queryFilterOf(DynamicFilterPanel<Product> filterPanel) {
        if (filterPanel == null) {
            return null;
        }
        return filterPanel.getQueryFilter().orElse(null);
    }

    /** Container-query wrapper so the toolbar can collapse to its mobile layout. */
    private static Div createPreview(String id, com.vaadin.flow.component.Component toolbar, Grid<Product> grid) {
        var preview = new Div(toolbar, grid);
        preview.setId(id);
        preview.getStyle().set("container-type", "inline-size");
        preview.getStyle().set("width", "100%");
        preview.getStyle().set("max-width", "920px");
        preview.getStyle().set("margin", "0 auto");
        return preview;
    }
}
