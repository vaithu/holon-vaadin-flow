package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.builders.ListingBundleConfigurator;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo for the chained {@link com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator}
 * → {@link com.holonplatform.vaadin.flow.components.builders.GridHeaderConfigurator}
 * → {@link ListingBundleConfigurator} fluent-configurator hierarchy.
 *
 * <p>Each example builds a {@link ListingBundle} the normal way, then re-configures
 * it after the fact via {@code ListingBundleConfigurator.configure(bundle)} — showing
 * that all three levels of fluent methods (heading / grid actions / row click) are
 * available in a single chain, and that the underlying parts (header, search,
 * toolbar, footer) can be retrieved either via the bundle's own accessors or via
 * the configurator's read-only accessors.
 */
@PageTitle("ListingBundleConfigurator – Holon Demo")
@Route(value = "listing-bundle-configurator", layout = DemoMainLayout.class)
public class ListingBundleConfiguratorDemoView extends Div {

    private final ProductService productService;

    public ListingBundleConfiguratorDemoView(ProductService productService) {
        this.productService = productService;

        addClassName("app-view");

        add(new H1("ListingBundleConfigurator"),
                new Paragraph(
                        "ListingBundleConfigurator extends GridHeaderConfigurator extends HeaderConfigurator. "
                                + "Use ListingBundleConfigurator.configure(bundle) to fluently style the heading, "
                                + "manage default/context actions on the GridHeader, attach row-click listeners, "
                                + "and access the underlying search field, toolbar and footer — all from one chain."),
                example1RetrieveParts(),
                example2HeaderRestyling(),
                example3SelectionContextActions());
    }

    // ── Example 1 — retrieve header, search, toolbar, footer ────────────────

    private DemoExample example1RetrieveParts() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search products…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Single fluent chain — every accessor returns the live wired component
        var cfg = ListingBundleConfigurator.configure(bundle);

        GridHeader  grid    = bundle.header();
        TextField   search  = cfg.getSearchField();   // from ListingBundleConfigurator
        Div         toolbar = cfg.getToolbar();
        Div         footer  = cfg.getFooter();

        // Prove they are the same instances exposed by the bundle
        var bundleHeader = bundle.header();
        assert grid    == bundleHeader;
        assert search  == bundle.search();

        var info = new Span(
                "header=" + (grid != null) + ", "
                        + "gridHeader=" + (grid != null) + ", "
                        + "search=" + (search != null) + ", "
                        + "toolbar=" + (toolbar != null) + ", "
                        + "footer=" + (footer != null));
        info.getElement().getStyle().set("font-family", "monospace");

        return new DemoExample(
                "1. Retrieve header / search / toolbar / footer from the bundle",
                new Div(info, bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                ListingBundle<Product> bundle = Components.listing(Product.class)
                    .gridHeader("Products")
                    .columns("id", "name", "category", "price")
                    .search("Search products…")
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();

                // Configurator gives typed access to every assembled part
                var cfg = ListingBundleConfigurator.configure(bundle);

                GridHeader grid    = bundle.header();       // same live instance
                TextField  search  = cfg.getSearchField();   // == bundle.search()  (may be null)
                Div        toolbar = cfg.getToolbar();       // == bundle.toolbar()
                Div        footer  = cfg.getFooter();        // == bundle.footer()
                """);
    }

    // ── Example 2 — restyle the heading after build ─────────────────────────

    private DemoExample example2HeaderRestyling() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Initial title")
                .columns("id", "name", "category", "price")
                .pageSizes(5)
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Re-configure the header using HeaderConfigurator methods inherited
        // through the ListingBundleConfigurator chain. Also drop the bottom border.
        ListingBundleConfigurator.configure(bundle)
                .heading(Components.h3().text("Catalogue (restyled)").build())
                .withSize(Font.Size.XLARGE)
                .withoutBorder();

        return new DemoExample(
                "2. Restyle the heading via inherited HeaderConfigurator methods",
                new Div(bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                // All Header* methods are inherited — they delegate to the live bundle header()
                ListingBundleConfigurator.configure(bundle)
                    .heading(Components.h3().text("Catalogue (restyled)").build())
                    .withSize(Font.Size.XLARGE)
                    .withoutBorder();          // suppress header bottom border
                """);
    }

    // ── Example 3 — wire selection-aware context actions + row click ────────

    private DemoExample example3SelectionContextActions() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .columns("id", "name", "category", "price")
                .pageSizes(5)
                .search("Search…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        var deleteBtn = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.addClickListener(e -> Notification.show("Pretend-deleted selected rows"));

        var exportBtn = new Button("Export CSV", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addClickListener(e -> Notification.show("Pretend-exported selected rows"));

        // Disable row selection so a click only fires onItemClick (no visual selection toggle).
        bundle.listing().setSelectionMode(Selectable.SelectionMode.NONE);

        // Selection actions appear when rows are selected; row click opens a notification
        ListingBundleConfigurator.configure(bundle)
                .contextActions(deleteBtn, exportBtn)
                .onItemClick(e -> Notification.show("Clicked: " + e.getItem().getName()));

        return new DemoExample(
                "3. Selection context actions + row click via the chained configurator",
                new Div(bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer()),
                """
                // Stop the grid from selecting the row when the user clicks it
                bundle.listing().setSelectionMode(Selectable.SelectionMode.NONE);

                ListingBundleConfigurator.configure(bundle)
                    .contextActions(deleteBtn, exportBtn)                         // GridHeaderConfigurator
                    .onItemClick(e -> Notification.show(e.getItem().getName()));  // ListingBundleConfigurator
                """);
    }
}




