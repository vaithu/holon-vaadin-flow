package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
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
        var filterPanel = DynamicFilterPanel.of(Product.class);
        final String[] searchText = new String[]{""};

        var grid = new Grid<Product>();
        grid.setId("grid-toolbar-products");
        grid.getElement().setAttribute("data-testid", "grid-toolbar-products");
        grid.addColumn(Product::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Product::getName).setHeader("Name").setFlexGrow(1);
        grid.addColumn(Product::getCategory).setHeader("Category").setAutoWidth(true);
        grid.addColumn(p -> p.getPrice() != null ? "$" + p.getPrice() : "").setHeader("Price").setAutoWidth(true);
        grid.addColumn(p -> p.isActive() ? "Active" : "Inactive").setHeader("Status").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        CallbackDataProvider<Product, Void> dataProvider = DataProvider.fromFilteringCallbacks(
                query -> productService.fetch(
                        query.getOffset(),
                        query.getLimit(),
                        searchText[0],
                        filterPanel.getQueryFilter().orElse(null),
                        null),
                query -> (int) productService.count(
                        searchText[0],
                        filterPanel.getQueryFilter().orElse(null))
        );
        grid.setDataProvider(dataProvider);

        var plainToolbar = Components.gridToolbar()
                .selectionGrid(grid)
                .searchPlaceholder("Search products…")
                .primaryAction(new Button("Add product", event -> Notification.show("Product form requested")))
                .bulkAction("Export", () -> Notification.show("Exporting " + grid.getSelectedItems().size() + " products"))
                .bulkAction("Assign", () -> Notification.show("Assigning " + grid.getSelectedItems().size() + " products"))
                .destructiveBulkAction("Delete", () -> Notification.show("Deleting " + grid.getSelectedItems().size() + " products"))
                .build();
        plainToolbar.setId("grid-toolbar-demo");
        plainToolbar.getElement().setAttribute("data-testid", "grid-toolbar-demo");

        var toolbar = Components.gridToolbar()
                .selectionGrid(grid)
                .searchPlaceholder("Search products…")
                .filterPanel(filterPanel)
                .primaryAction(new Button("Add product", event -> Notification.show("Product form requested")))
                .bulkAction("Export", () -> Notification.show("Exporting " + grid.getSelectedItems().size() + " products"))
                .bulkAction("Assign", () -> Notification.show("Assigning " + grid.getSelectedItems().size() + " products"))
                .destructiveBulkAction("Delete", () -> Notification.show("Deleting " + grid.getSelectedItems().size() + " products"))
                .build();

        toolbar.getSearchField().addValueChangeListener(event -> {
            searchText[0] = event.getValue();
            dataProvider.refreshAll();
        });

        filterPanel.addFilterChangeListener(event -> dataProvider.refreshAll());

        var preview = new Div(plainToolbar, grid);
        preview.setId("grid-toolbar-preview");
        preview.getStyle().set("container-type", "inline-size");
        preview.getStyle().set("width", "100%");
        preview.getStyle().set("max-width", "920px");
        preview.getStyle().set("margin", "0 auto");

        var filterPreview = new Div(toolbar, grid);
        filterPreview.setId("grid-toolbar-filter-preview");
        filterPreview.getStyle().set("container-type", "inline-size");
        filterPreview.getStyle().set("width", "100%");
        filterPreview.getStyle().set("max-width", "920px");
        filterPreview.getStyle().set("margin", "0 auto");

        var mobileToggle = new Button("Simulate mobile width (380px)", VaadinIcon.MOBILE.create(), event -> {
            boolean mobile = preview.getElement().getClassList().contains("grid-toolbar-preview--mobile");
            preview.getElement().getClassList().set("grid-toolbar-preview--mobile", !mobile);
            preview.getStyle().set("max-width", mobile ? "920px" : "380px");
        });
        mobileToggle.setId("grid-toolbar-mobile-toggle");

        var example = new Div(mobileToggle, preview, filterPreview);
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
                filterPanel.addFilterChangeListener(e -> dataProvider.refreshAll());
                """);
    }
}