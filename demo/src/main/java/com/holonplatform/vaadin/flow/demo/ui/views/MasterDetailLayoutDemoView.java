package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePair;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormSection;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Demo for {@link MasterDetailLayout} — wired via {@link ListingBundle} for the master
 * panel and {@link LazyTabsBuilder} for the detail panel, backed by {@link ProductService}.
 *
 * <h3>What this demo exercises</h3>
 * <ul>
 *   <li>{@code Components.masterDetail()} with bundle auto-wiring.</li>
 *   <li>{@link LazyTabsBuilder} with mixed eager + lazy + cached tabs.</li>
 *   <li>URL deep-link via {@code itemId(...)} + {@code restoreSelection(?id=)}.</li>
 *   <li>{@code autoSelectFirst(true)} — sensible default selection on desktop.</li>
 *   <li>{@link FormSection} — core component for titled form sections.</li>
 *   <li>{@link KeyValuePairs} — core component for profile/detail display.</li>
 * </ul>
 */
@PageTitle("MasterDetailLayout – Holon Demo")
@Route(value = "master-detail-layout", layout = DemoMainLayout.class)
public class MasterDetailLayoutDemoView extends Div implements BeforeEnterObserver {

    // ─── CSS class names (defined in core: master-detail-layout.css) ─────────
    private static final String CSS_VIEW        = "app-view";
    private static final String CSS_HOST        = "mdl-host";
    private static final String CSS_TAB_BODY    = "mdl-tab-body";
    private static final String CSS_TAB_ACTIONS = "mdl-tab-actions";
    private static final String CSS_DETAIL_META = "mdl-detail-meta";
    private static final String QUERY_PARAM_ID  = "id";

    // ─── Dependencies ────────────────────────────────────────────────────────

    private final ProductService productService;

    // ─── Detail header ───────────────────────────────────────────────────────

    private final Header detailHeader = new Header("Select a product", HeadingLevel.H3);

    // ─── Edit-tab fields ─────────────────────────────────────────────────────

    private final TextField   nameField     = new TextField("Name");
    private final TextField   categoryField = new TextField("Category");
    private final NumberField priceField    = new NumberField("Price");
    private final Button      saveBtn       = new Button("Save changes");

    // ─── Per-tab content roots — built once, reused across selections ────────

    private final Div profileBody = new Div();
    private final Div editBody    = new Div();
    private final Div deleteBody  = new Div();

    private MasterDetailLayout<Product> masterDetail;
    private ListingBundle<Product>      bundle;
    private String                      pendingId;

    // ─── Construction ────────────────────────────────────────────────────────

    public MasterDetailLayoutDemoView(ProductService productService) {
        this.productService = productService;

        addClassName(CSS_VIEW);
        addClassName(CSS_HOST);
        setSizeFull();

        configureDetailHeader();
        configureEditBody();
        configureProfileBody();
        configureDeleteBody();

        add(buildMasterDetail());
    }

    // ─── Master ──────────────────────────────────────────────────────────────

    private ListingBundle<Product> buildBundle() {
        return Components.listing(Product.class)
                .columns("name", "category", "price", "active", "createdDate")
                .gridHeader("Products")
                .pageSizes(10, 25, 50)
                .defaultPageSize(10)
                .withFilterPanel()
                .multiSelect()
                .search("Search products…")
                .fetch((q, text, filter, sort) ->
                        productService.fetch(q.getOffset(), q.getLimit(), text, filter, sort))
                .build();
    }

    // ─── Detail header ───────────────────────────────────────────────────────

    private void configureDetailHeader() {
        detailHeader.setHeadingFontSize(Font.Size.LARGE);
    }

    private void syncDetailHeader(Product p) {
        detailHeader.setHeading(p.getName());
        Span meta = new Span(p.getCategory() + " · $" + p.getPrice());
        meta.addClassName(CSS_DETAIL_META);
        detailHeader.setDetails(meta);
    }

    // ─── Edit tab ────────────────────────────────────────────────────────────

    private void configureEditBody() {
        editBody.addClassName(CSS_TAB_BODY);
        saveBtn.getElement().getThemeList().add("primary");
        saveBtn.addClickListener(e -> currentProduct().ifPresent(this::saveProduct));

        Div actions = new Div(saveBtn);
        actions.addClassName(CSS_TAB_ACTIONS);

        editBody.add(
                FormSection.of("Product Info", nameField, categoryField, priceField),
                actions);
    }

    private void syncEditBody(Product p) {
        nameField.setValue(p.getName() != null ? p.getName() : "");
        categoryField.setValue(p.getCategory() != null ? p.getCategory() : "");
        priceField.setValue(p.getPrice() != null ? p.getPrice().doubleValue() : 0.0);
    }

    private void saveProduct(Product p) {
        p.setName(nameField.getValue());
        p.setCategory(categoryField.getValue());
        p.setPrice(priceField.getValue() != null
                ? BigDecimal.valueOf(priceField.getValue()) : BigDecimal.ZERO);
        productService.save(p);
        masterDetail.notifyDataChanged();
        UIUtils.toast("Product saved", NotificationVariant.LUMO_SUCCESS);
    }

    // ─── Profile tab ─────────────────────────────────────────────────────────

    private void configureProfileBody() {
        profileBody.addClassName(CSS_TAB_BODY);
    }

    private void syncProfileBody(Product p) {
        profileBody.removeAll();
        profileBody.add(new KeyValuePairs(
                new KeyValuePair("Name", p.getName()),
                new KeyValuePair("Category", p.getCategory()),
                new KeyValuePair("Price", "$" + p.getPrice()),
                new KeyValuePair("Active", p.isActive() ? "Yes" : "No"),
                new KeyValuePair("Created", p.getCreatedDate() != null ? p.getCreatedDate().toString() : "—")
        ));
    }

    // ─── New tab (lazy + cached) ─────────────────────────────────────────────

    private Component buildNewBody() {
        TextField   n  = new TextField("Name");
        TextField   c  = new TextField("Category");
        NumberField pr = new NumberField("Price");

        Button add = new Button("Add product");
        add.getElement().getThemeList().add("primary");
        add.addClickListener(ev -> {
            if (n.isEmpty()) {
                UIUtils.toast("Name is required", NotificationVariant.LUMO_ERROR);
                return;
            }
            Product p = new Product(
                    n.getValue(),
                    c.getValue(),
                    pr.getValue() != null ? BigDecimal.valueOf(pr.getValue()) : BigDecimal.ZERO);
            productService.save(p);
            n.clear(); c.clear(); pr.clear();
            masterDetail.notifyDataChanged();
            UIUtils.toast(p.getName() + " created", NotificationVariant.LUMO_SUCCESS);
        });

        Div actions = new Div(add);
        actions.addClassName(CSS_TAB_ACTIONS);

        Div body = new Div(
                FormSection.of("New Product", n, c, pr),
                actions);
        body.addClassName(CSS_TAB_BODY);
        return body;
    }

    // ─── Delete tab ──────────────────────────────────────────────────────────

    private final Span   deleteMessage = new Span();
    private final Button deleteBtn     = new Button("Delete product");

    private void configureDeleteBody() {
        deleteBody.addClassName(CSS_TAB_BODY);
        deleteMessage.addClassName("mdl-delete-msg");
        deleteBtn.getElement().getThemeList().add("error primary");
        deleteBtn.addClickListener(e -> currentProduct().ifPresent(this::deleteProduct));

        Div actions = new Div(deleteBtn);
        actions.addClassName(CSS_TAB_ACTIONS);

        deleteBody.add(deleteMessage, actions);
    }

    private void syncDeleteBody(Product p) {
        deleteMessage.setText("You are about to delete \"" + p.getName()
                + "\". This action cannot be undone.");
        deleteBtn.setText("Delete \"" + p.getName() + "\"");
    }

    private void deleteProduct(Product p) {
        productService.delete(p);
        masterDetail.clearSelection();
        masterDetail.notifyDataChanged();
        UIUtils.toast(p.getName() + " removed", NotificationVariant.LUMO_ERROR);
    }

    // ─── Master-detail assembly ──────────────────────────────────────────────

    private MasterDetailLayout<Product> buildMasterDetail() {
        bundle = buildBundle();

        LazyTabsBuilder tabsBuilder = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("Profile", profileBody)
                .withEagerTab("Edit",    editBody)
                .withLazyTab ("New",     this::buildNewBody)
                .withEagerTab("Delete",  deleteBody)
                .selectedIndex(1)
                .cacheEnabled();

        Div tabContent = tabsBuilder.getContentContainer();
        Tabs tabs = tabsBuilder.build();

        masterDetail = Components.<Product>masterDetail()
        .masterHeader(new Header("Products", HeadingLevel.H2))
                .masterGrid(bundle)
                .detailHeader(detailHeader)
                .detailMenu(tabs)
                .detailContent(p -> new Component[]{ tabContent })
                .itemId(p -> String.valueOf(p.getId()), this::findById)
                .mobileSheetTitle("Product details")
                .autoSelectFirst(true)
                .onDataChanged(() -> bundle.listing().getDataProvider().refreshAll())
                .withDetailSync(detailHeader, this::syncDetailHeader)
                .withDetailSync(profileBody,  this::syncProfileBody)
                .withDetailSync(editBody,     this::syncEditBody)
                .withDetailSync(deleteBody,   this::syncDeleteBody)
                .build();

        masterDetail.setSizeFull();
        masterDetail.getResponsiveLayout().withSeparator();
        return masterDetail;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Optional<Product> currentProduct() {
        return masterDetail == null ? Optional.empty() : masterDetail.selectionSignal().peek();
    }

    private Optional<Product> findById(String idStr) {
        try {
            return productService.findById(Long.parseLong(idStr));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    // ─── URL ?id= deep-link ──────────────────────────────────────────────────

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters()
                .getParameters()
                .getOrDefault(QUERY_PARAM_ID, List.of())
                .stream().findFirst()
                .ifPresent(id -> this.pendingId = id);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (pendingId != null) {
            masterDetail.restoreSelection(pendingId);
            pendingId = null;
        }
    }
}
