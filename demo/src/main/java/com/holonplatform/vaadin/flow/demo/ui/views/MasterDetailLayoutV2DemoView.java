package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("MasterDetailLayout V2 – Holon Demo")
@Route(value = "master-detail-layout-v2", layout = DemoMainLayout.class)
public class MasterDetailLayoutV2DemoView extends Div {

    private final ProductService productService;
    private final MasterDetailConfigurator<Product, ?> layout;
    private final AtomicReference<Product> selectedProduct = new AtomicReference<>();

    private final H3 masterTitle = new H3("Products");
    private final Span masterSummary = new Span("Browse and manage the product catalog.");

    private final H3 detailTitle = new H3("Select a product");
    private final Span detailBadge = Components.Badge.badge().text("Idle").build();
    private final Avatar detailAvatar = Components.avatar("Product").profile().build();

    private final TextField nameField = new TextField("Name");
    private final TextField categoryField = new TextField("Category");
    private final NumberField priceField = new NumberField("Price");
    private final Checkbox activeField = new Checkbox("Active");
    private final Span status = new Span("Select a product to edit it.");

    private final Button masterRefreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
    private final Button masterNewButton = new Button("New", new Icon(VaadinIcon.PLUS));
    private final Button masterClearButton = new Button("Clear", new Icon(VaadinIcon.BACKSPACE));
    private final Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
    private final Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));

    public MasterDetailLayoutV2DemoView(ProductService productService) {
        this.productService = productService;
        setSizeFull();
        layout = buildLayout();
        layout.setSizeFull();
        add(layout.getComponent());
    }

    private MasterDetailConfigurator<Product, ?> buildLayout() {
        configureButtons();

        Div detailContainer = new Div();
        var configurator = Components.masterDetailV2(Product.class);
        var master = configurator.masterView();
        master.header()
                .heading(masterTitle)
                .details(masterSummary)
                .actions(Components.button().newButton().build())
                .add();
        master.listingBundle()
                .autoCreateColumns(false)
                .columns("name", "category", "price", "active")
                .multiSelect()
                .mobileViewColumn(Components.<Product>mobileGridColumnLit()
                        .flexDirection(FlexDirection.ROW)
                        .withAvatarAsPrimary(Product::getName)
                        .withSecondaryText(Product::getName)
                        .withTertiaryText(product -> String.valueOf(product.getPrice()))

                        .build())
                .mobileViewHeader(
                        Components.hl()
                                .addToStart(new Span("Product Name"))
                                .addToEnd(new Span("Price"))
                                .build()
                )
                .gridHeader("")
                .gridHeader(Components.button().delete().build())
                .pageSizes(10, 25, 50)
                .defaultPageSize(10)
                .search("Search products…")
                .withFilterPanel()
                .fetch((query, text, filter, sort) -> productService.fetch(query.getOffset(), query.getLimit(), text, filter, sort))
                .add()
                .add();
        return configurator
                .defaultView(master)
                .detail()
                .header()
                .prefix(detailAvatar)
                .heading(detailTitle)
                .breadcrumb(
                        new BreadcrumbItem(Components.span().text("Catalog").build()),
                        new BreadcrumbItem(Components.span().text("Products").build()),
                        new BreadcrumbItem(Components.span().text("Product details").build()))
                .details(detailBadge)
                .heading(detailTitle)
                .actions(saveButton, deleteButton)
                .withoutBorder()
                .add()
                .tabs()
                .withLazyTab("Overview", this::buildOverviewTab)
                .withLazyTab("Activity", this::buildActivityTab)
                .withLazyTab("Notes", this::buildNotesTab)
                .withContainer(detailContainer)
                .selectedIndex(0)
                .add()
                .withDetailSync(detailContainer, this::showProduct)
                .add()
                .itemId((Product product) -> String.valueOf(product.getId()), this::parseProductId)
                .deepLink((Product product) -> String.valueOf(product.getId()), this::parseProductId)
                .autoShowFirst()
                .onItemChanged((Product product) -> status.setText(product != null ? "Editing " + product.getName() : "Select a product to edit it."))
                .onDataChanged(() -> status.setText("Product list refreshed."))
                .build();
    }

    private void configureButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        masterRefreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        masterNewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        masterClearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        masterRefreshButton.addClickListener(e -> layout.notifyDataChanged());
        masterNewButton.addClickListener(e -> {
            selectedProduct.set(null);
            showProduct(null);
        });
        masterClearButton.addClickListener(e -> {
            layout.clearSelection();
            selectedProduct.set(null);
            showProduct(null);
        });

        saveButton.addClickListener(e -> saveSelectedProduct());
        deleteButton.addClickListener(e -> deleteSelectedProduct());
    }

    private VerticalLayout buildOverviewTab() {
        nameField.setWidthFull();
        categoryField.setWidthFull();
        priceField.setWidthFull();
        priceField.setStep(0.01);

        VerticalLayout form = new VerticalLayout(status, nameField, categoryField, priceField, activeField);
        form.setPadding(false);
        form.setSpacing(true);
        form.setWidthFull();
        return form;
    }

    private VerticalLayout buildActivityTab() {
        VerticalLayout content = new VerticalLayout(
                Components.span().text("Recent edits, import/export activity, and audit events can live here.").build());
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();
        return content;
    }

    private VerticalLayout buildNotesTab() {
        VerticalLayout content = new VerticalLayout(
                Components.span().text("Notes, comments, and product-specific attachments can be added here.").build());
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();
        return content;
    }

    private Optional<Product> parseProductId(String id) {
        try {
            return productService.findById(Long.parseLong(id));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private void showProduct(Product product) {
        selectedProduct.set(product);
        boolean hasSelection = product != null;

        nameField.setValue(hasSelection ? defaultString(product.getName()) : "");
        categoryField.setValue(hasSelection ? defaultString(product.getCategory()) : "");
        priceField.setValue(hasSelection && product.getPrice() != null ? product.getPrice().doubleValue() : null);
        activeField.setValue(hasSelection && product.isActive());

        detailTitle.setText(hasSelection ? product.getName() : "Select a product");
        detailAvatar.setName(hasSelection ? defaultString(product.getName()) : "Product");
        detailBadge.setText(hasSelection ? (product.isActive() ? "Active" : "Inactive") : "Idle");
        saveButton.setEnabled(true);
        deleteButton.setEnabled(hasSelection && product.getId() != null);
        status.setText(hasSelection ? "Editing " + product.getName() : "Select a product to edit it.");
    }

    private void saveSelectedProduct() {
        Product product = selectedProduct.get();
        boolean isNew = product == null;
        if (isNew) {
            product = new Product();
        }
        product.setName(nameField.getValue());
        product.setCategory(blankToNull(categoryField.getValue()));
        Double price = priceField.getValue();
        product.setPrice(price != null ? BigDecimal.valueOf(price) : null);
        product.setActive(Boolean.TRUE.equals(activeField.getValue()));

        Product saved = productService.save(product);
        selectedProduct.set(saved);
        layout.refreshItem(saved);
        layout.notifyDataChanged();
        showInfo(isNew ? "Product created." : "Product saved.");
        showProduct(saved);
    }

    private void deleteSelectedProduct() {
        Product product = selectedProduct.get();
        if (product == null || product.getId() == null) {
            return;
        }

        productService.delete(product);
        selectedProduct.set(null);
        layout.notifyDataChanged();
        showInfo("Product deleted.");
        showProduct(null);
    }

    private static String defaultString(String value) {
        return value != null ? value : "";
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static void showInfo(String message) {
        var notification = Notification.show(message, 2500, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
