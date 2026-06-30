package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityFormPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.time.LocalDate;

/**
 * Demo page: full CRUD over a real JPA-backed {@link Product} entity using
 * Spring Data JPA + Holon {@link EntityFormPanel} + {@link Components#listing(Class)}.
 *
 * <p>Architecture follows the strict layered rule:
 * <pre>
 *   this view  →  ProductService  →  ProductRepository  →  H2 (JPA / Hibernate)
 * </pre>
 */
@PageTitle("Product CRUD (JPA) – Holon Demo")
@Route(value = "product-crud", layout = DemoMainLayout.class)
public class ProductCrudDemoView extends Div {

    private final ProductService productService;

    /** Captured after {@link #buildBundle()} — triggers a full data reload. */
    private Runnable refreshGrid;

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
                .title("Spring Data JPA active")
                .description("H2 console available at /h2-console  (JDBC URL: jdbc:h2:mem:demodb)")
                .build();

        // ── Listing bundle ────────────────────────────────────────────────────
        var bundle = buildBundle();

        // Layout: page wrapper that prevents overflow
        var content = new Div();
        content.addClassName("listing-page");
        content.add(heading, subTitle, infoAlert, bundle);
        add(content);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listing bundle
    // ─────────────────────────────────────────────────────────��──────────────

    private ListingBundle<Product> buildBundle() {

        var bundle = Components.listing(Product.class)
                .columns("id", "name", "category", "price", "active", "createdDate")
                .withFilterPanel()
                .multiSelect()
                .gridHeader("Product Catalogue")
                .header("id",          "ID")
                .header("name",        "Name")
                .header("category",    "Category")
                .header("price",       "Price")
                .header("active",      "Active")
                .header("createdDate", "Created")
                .pageSizes(10, 25, 50)
                .search("Search by name or category…")
                .fetch((q, text, filter, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text, filter, sort))
                .build();

        // Add a component column for Edit / Delete actions
        bundle.listing().addComponentColumn(product -> {
            var editBtn = new Button(LumoIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            editBtn.setAriaLabel("Edit " + product.getName());
            editBtn.addClickListener(e -> openForm(product));

            var delBtn = new Button(VaadinIcon.TRASH.create());
            delBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR);
            delBtn.setAriaLabel("Delete " + product.getName());
            delBtn.addClickListener(e -> confirmDelete(product));

            var row = new Div(editBtn, delBtn);
            row.addClassName("flex");
            row.addClassName("gap-xs");
            return row;
        });

        bundle.listing().setScrollUsingUpDownKeys();

        // Capture refresh handle
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
                                } else if (existing != null) {
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

        // Pre-populate inputs when editing
        if (!isNew) {
            ((BeanPropertyInputForm<Product>) panel.getForm()).setBean(existing);
        }

        dialog.add(panel);
        dialog.open();
    }

    // ─────────────────────────────────────────────────────────────────────────
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
    // ───────────���─────────────────────────────────────────────────────────────

    private static void showSuccess(String text) {
        var n = Notification.show(text, 3000, Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showError(String text) {
        var n = Notification.show(text, 5000, Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}





