package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.builders.ListingBundleConfigurator;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
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
 * Demo for the {@link ListingBundleConfigurator} post-build fluent API.
 *
 * <p>Each example builds a {@link ListingBundle} the normal way, then re-configures
 * it after the fact via {@code ListingBundleConfigurator.configure(bundle)} — showing
 * that row-click wiring and read-only accessors (header, search, toolbar, footer) are
 * available in a single chain. The title heading returned by {@link ListingBundle#header()}
 * is a plain {@link Component}; grid-management actions and selection-aware context
 * actions live in {@link ListingBundle#toolbar()} instead.
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
                        "Use ListingBundleConfigurator.configure(bundle) to attach row-click listeners "
                                + "and access the underlying header, search field, toolbar and footer — all "
                                + "from one chain. The title and context actions are configured at build time "
                                + "via ListingBundleBuilder#gridHeader(...)."),
                example1RetrieveParts(),
                example2HeaderRestyling(),
                example3SelectionContextActions());
    }

    // ── Example 1 — retrieve header, search, toolbar, footer ────────────────

    private DemoExample example1RetrieveParts() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5, 10)
                .defaultPageSize(5)
                .search("Search products…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Single fluent chain — every accessor returns the live wired component
        var cfg = ListingBundleConfigurator.configure(bundle);

        Component   header  = bundle.header();
        TextField   search  = cfg.getSearchOptional().orElse(null);   // from ListingBundleConfigurator
        Div         toolbar = cfg.toolbar();
        Div         footer  = cfg.footer();

        var info = new Span(
                "header=" + (header != null) + ", "
                        + "search=" + (search != null) + ", "
                        + "toolbar=" + (toolbar != null) + ", "
                        + "footer=" + (footer != null));
        info.getElement().getStyle().set("font-family", "monospace");

        return new DemoExample(
                "1. Retrieve header / search / toolbar / footer from the bundle",
                new Div(info, bundle),
                """
                ListingBundle<Product> bundle = Components.listing(Product.class)
                    .gridHeader("Products")
                    .columns("id", "name", "category", "price")
                    .search("Search products…")
                    .fetch((q, text) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();

                // Configurator gives typed access to every assembled part
                var cfg = ListingBundleConfigurator.configure(bundle);

                Component  header  = bundle.header();                        // same live instance (the title heading)
                TextField  search  = cfg.getSearchOptional().orElse(null);   // == bundle.getSearchOptional() (may be null)
                Div        toolbar = cfg.toolbar();                           // == bundle.toolbar()
                Div        footer  = cfg.footer();                            // == bundle.footer()
                """);
    }

    // ── Example 2 — restyle the title heading after build ───────────────────

    private DemoExample example2HeaderRestyling() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5)
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // header() returns a plain Component (an H2) — restyle it directly via the
        // standard Vaadin HasText / HasStyle contracts; there is no dedicated
        // Header-configurator for it anymore.
        if (bundle.header() instanceof HasText text) {
            text.setText("Catalogue (restyled)");
        }
        bundle.header().getElement().getStyle().set("font-size", "1.5rem");

        return new DemoExample(
                "2. Restyle the title heading directly",
                new Div(bundle),
                """
                // header() returns a plain Component (an H2) — restyle it directly
                if (bundle.header() instanceof HasText text) {
                    text.setText("Catalogue (restyled)");
                }
                bundle.header().getElement().getStyle().set("font-size", "1.5rem");
                """);
    }

    // ── Example 3 — wire selection-aware context actions + row click ────────

    private DemoExample example3SelectionContextActions() {
        var deleteBtn = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.addClickListener(e -> Notification.show("Pretend-deleted selected rows"));

        var exportBtn = new Button("Export CSV", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addClickListener(e -> Notification.show("Pretend-exported selected rows"));

        // Context actions are supplied at build time — they land in the toolbar's
        // bulk-actions row, shown automatically once rows are selected.
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price")
                .pageSizes(5)
                .search("Search…")
                .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Disable row selection so a click only fires onItemClick (no visual selection toggle).
        bundle.listing().setSelectionMode(Selectable.SelectionMode.NONE);

        ListingBundleConfigurator.configure(bundle)
                .onItemClick(e -> Notification.show("Clicked: " + e.getItem().getName()));

        return new DemoExample(
                "3. Selection context actions + row click via the chained configurator",
                new Div(bundle),
                """
                // Context actions are supplied at build time on the builder itself
                ListingBundle<Product> bundle = Components.listing(Product.class)
                    .gridHeader("Products", deleteBtn, exportBtn)
                    .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text))
                    .build();

                // Stop the grid from selecting the row when the user clicks it
                bundle.listing().setSelectionMode(Selectable.SelectionMode.NONE);

                ListingBundleConfigurator.configure(bundle)
                    .onItemClick(e -> Notification.show(e.getItem().getName()));  // ListingBundleConfigurator
                """);
    }
}




