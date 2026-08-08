package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityFormPanel;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Demo page: full CRUD over a real JPA-backed {@link Product} entity using
 * Spring Data JPA + Holon {@link EntityFormPanel} + {@link Components#listing(Class)}.
 *
 * <h2>New listing-bundle features demonstrated here</h2>
 *
 * <h3>1. Fluent row-action column — {@code withEditAction} / {@code withDeleteAction} / {@code withRowAction}</h3>
 * <p>Replaces the old manual {@code addComponentColumn} pattern. A single frozen-to-end
 * column with a vertical-ellipsis (⋮) trigger is added automatically.
 * Clicking the trigger opens an action menu whose content is driven by the actions
 * registered on the builder. No raw {@code Button} or {@code MenuBar} construction
 * is required in view code.</p>
 * <pre>{@code
 * .withEditAction(product -> openForm(product))          // Edit action (VaadinIcon.EDIT)
 * .withDeleteAction(product -> confirmDelete(product))   // Delete action (VaadinIcon.TRASH)
 * .withRowAction(VaadinIcon.COPY, "Duplicate", p -> ...)  // any extra action
 * }</pre>
 *
 * <h3>2. High-performance mode — {@code withHighPerformanceActions()}</h3>
 * <p>By default the action column uses a {@code ComponentRenderer}: one {@code MenuBar}
 * instance per <em>visible</em> row.  For high-concurrency deployments, calling
 * {@code .withHighPerformanceActions()} switches to an O(1) strategy:</p>
 * <ul>
 *   <li>A single {@code ContextMenu} per grid (desktop) or a single bottom
 *       {@code Sheet} per grid (mobile touch) — regardless of row count.</li>
 *   <li>The trigger cell is rendered purely client-side via a {@code LitRenderer}
 *       — zero server-side components per row.</li>
 *   <li>Click coordinates flow from the browser's {@code MouseEvent} through
 *       {@code LitRenderer.withFunction} directly to the server so the menu
 *       opens at the exact cursor position in a single round-trip.</li>
 *   <li>On touch devices {@code e.pointerType === 'touch'} is detected in the
 *       Lit template and encoded as {@code 1} — the server then opens the
 *       bottom {@code Sheet} instead of the {@code ContextMenu}.</li>
 * </ul>
 * <pre>{@code
 * .withHighPerformanceActions()   // flip the action column to O(1) mode
 * }</pre>
 *
 * <h3>3. Post-processor — {@code withListingPostProcessor}</h3>
 * <p>An escape hatch for low-level {@code Grid} / {@code ItemListing} customisation
 * not covered by the fluent API.  The callback is invoked on the fully-built listing
 * after all standard columns and the actions column have been added, but before the
 * {@code ListingBundle} wrapper is assembled.  Use it for custom column renderers,
 * conditional cell styles, frozen columns, or programmatic sort defaults.</p>
 * <pre>{@code
 * .withListingPostProcessor(listing -> {
 *     // Example: format price column as USD currency
 *     listing.getAllColumns().stream()
 *         .filter(c -> "price".equals(c.getKey())).findFirst()
 *         .ifPresent(c -> c.setRenderer(new NumberRenderer<>(...)));
 *     // Example: freeze ID column to the left edge
 *     listing.getAllColumns().stream()
 *         .filter(c -> "id".equals(c.getKey())).findFirst()
 *         .ifPresent(c -> c.setFrozen(true));
 * })
 * }</pre>
 *
 * <h2>Architecture</h2>
 * <pre>
 *   this view  →  ProductService  →  ProductRepository  →  H2 (JPA / Hibernate)
 * </pre>
 */
@PageTitle("Product CRUD (JPA) – Holon Demo")
@Route(value = "product-crud", layout = DemoMainLayout.class)
public class ProductCrudDemoView extends Div {

    private final transient ProductService productService;

    /** Captured after {@link #buildBundle()} — triggers a full data reload. */
    private transient Runnable refreshGrid;

    public ProductCrudDemoView(ProductService productService) {
        this.productService = productService;

        addClassName("app-view");

        // ── Page header ───────────────────────────────────────────────────────
        var heading  = new H1("Product Catalogue");
        var subTitle = new Paragraph(
                "Full CRUD backed by Spring Data JPA + Hibernate + H2 in-memory database. " +
                "All data survives page navigation and is reset on application restart.");
        heading.addClassName("app-page-header");

        // ── Info banner ───────────────────────────────────────────────────────
        var infoAlert = Alert.builder(Alert.Variant.INFO)
                .title("Listing-bundle row actions + high-performance mode")
                .description(
                        "Click the ⋮ button on any row to see the fluent action column. " +
                        "On a touch device the same tap opens a bottom Sheet instead of a dropdown. " +
                        "Open source: AbstractListingBundleConfigurer#addHighPerformanceActionColumn().")
                .build();

        // ── Listing bundle ────────────────────────────────────────────────────
        var bundle = buildBundle();

        var content = new Div();
        content.addClassName("listing-page");
        content.add(heading, subTitle, infoAlert, bundle);
        add(content);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listing bundle
    // ─────────────────────────────────────────────────────────────────────────

    private ListingBundle<Product> buildBundle() {

        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "active", "createdDate")
                .withFilterPanel(true)
                .multiSelect()
                .gridHeader("Product Catalogue")
                .header("id",          "ID")
                .header("name",        "Name")
                .header("category",    "Category")
                .header("price",       "Price (USD)")
                .header("active",      "Active")
                .header("createdDate", "Created")
                .pageSizes(10, 25, 50)
                .search("Search by name or category…")
                .fetch((q, text, filter, sort) ->
                        productService.fetch(q.getOffset(), q.getLimit(), text, filter, sort))

                // ── Header menu action ────────────────────────────────────────
                // Adds an "Add product" item to the ⋮ header options menu.
                .withMenuAction(VaadinIcon.PLUS, "Add product", () -> openForm(null))

                // ── Fluent row actions ────────────────────────────────────────
                // These replace the old manual addComponentColumn + raw Button pattern.
                // A frozen-to-end ⋮ column is created automatically; its menu items
                // are populated in the order registered below.
                //
                // Desktop: opens a ContextMenu at the exact cursor position.
                // Mobile:  opens a bottom Sheet with tap-friendly full-width buttons.
                // The pointer-type detection happens entirely in the Lit template:
                //   @click="${(e) => open(e.clientX, e.clientY, e.pointerType === 'touch' ? 1 : 0)}"
                .withEditAction(this::openForm)
                .withDeleteAction(this::confirmDelete)

                // ── High-performance mode ─────────────────────────────────────
                // Switches the action column from ComponentRenderer (one MenuBar per
                // visible row) to a single shared ContextMenu + LitRenderer trigger.
                //
                //   Default mode:  O(rows) — MenuBar × visible-row-count per session.
                //   HP mode:       O(1)    — 1 ContextMenu + 1 Sheet per grid total.
                //
                // For 10 000 concurrent users × 25 visible rows × 5 objects = 1.25 M
                // objects in default mode vs ~60 k objects in HP mode (~98 % reduction).
                // Always enable this for grids used in high-concurrency deployments.
                .withHighPerformanceActions()

                // ── Post-processor ────────────────────────────────────────────
                // Called on the fully-built ItemListing AFTER the actions column
                // has been added but BEFORE the ListingBundle wrapper is assembled.
                // Use it as an escape hatch for anything the fluent API does not cover.
                //
                // Here: render the price column as USD currency, and freeze the ID
                // column to the left edge so it stays visible while scrolling.
                .withListingPostProcessor(listing -> {
                    // Custom NumberRenderer for the price column
                    listing.getAllColumns().stream()
                            .filter(c -> "price".equals(c.getKey()))
                            .findFirst()
                            .ifPresent(c -> c.setRenderer(new NumberRenderer<>(
                                    Product::getPrice, "$ %(,.2f", Locale.US, "—")));
                    // Freeze ID column to the left edge
                    listing.getAllColumns().stream()
                            .filter(c -> "id".equals(c.getKey()))
                            .findFirst()
                            .ifPresent(c -> c.setFrozen(true));
                })

                .build();

        bundle.listing().setScrollUsingUpDownKeys();

        // Capture the refresh handle used by save / delete callbacks
        refreshGrid = () -> bundle.listing().getDataProvider().refreshAll();

        return bundle;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Create / Edit dialog
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void openForm(Product existing) {

        boolean isNew = (existing == null);
        var dialog = new Dialog();
        dialog.setHeaderTitle(isNew ? "Add product" : "Edit product");
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("480px");

        var panel = EntityFormPanel.bean(Product.class)
                .configure(fb -> fb.excludeFields("id", "createdDate"))
                .saveButton(
                        btn -> btn.primary().text(isNew ? "Create" : "Save"),
                        product -> {
                            try {
                                if (isNew) {
                                    product.setCreatedDate(LocalDate.now());
                                } else {
                                    product.setId(existing.getId());
                                    product.setCreatedDate(existing.getCreatedDate());
                                }
                                productService.save(product);
                                refreshGrid.run();
                                dialog.close();
                                showSuccess(isNew ? "Product created." : "Product saved.");
                            } catch (Exception ex) {
                                showError("Save failed: " + ex.getMessage());
                            }
                        })
                .clearButton(btn -> btn.text("Reset"))
                .cancelButton(btn -> btn.text("Cancel"), dialog::close)
                .build();

        if (!isNew) {
            ((BeanPropertyInputForm<Product>) panel.getForm()).setBean(existing);
        }

        dialog.add(panel);
        dialog.open();
    }

    // ──────────────────────────────────────────��──────────────────────────────
    // Delete confirmation
    // ─────────────────────────────────────────────────────────────────────────

    private void confirmDelete(Product product) {
        var confirm = new ConfirmDialog();
        confirm.setHeader("Delete product?");
        confirm.setText("\"" + product.getName() + "\" will be permanently removed.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> {
            try {
                productService.delete(product);
                refreshGrid.run();
                showSuccess("Product deleted.");
            } catch (Exception ex) {
                showError("Delete failed: " + ex.getMessage());
            }
        });
        confirm.open();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Notifications
    // ────��────────────────────────────────────────────────────────────────────

    private static void showSuccess(String text) {
        var n = Notification.show(text, 3000, Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showError(String text) {
        var n = Notification.show(text, 5000, Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
