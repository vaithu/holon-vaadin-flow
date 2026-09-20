package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.ChipVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.RowVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.StatusVariant;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert.Variant;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge.Size;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.AuditEntry;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.Severity;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.iyensoft.vaadin.flow.enums.ButtonPreset;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.iyensoft.vaadin.flow.utils.responsive.ViewModeContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * MaterialHeader + GridToolbar flavored Customer 360 demo based on the customer detail mockup.
 */
@PageTitle("Customer 360 (Material) - Holon Demo")
@Route(value = "customer-master-detail-material", layout = DemoMainLayout.class)
@StyleSheet("context://customer-master-detail.css")
public class CustomerMasterDetailMaterialView extends Div implements BeforeEnterObserver {

    private final transient ProductService productService;

    /** Criteria of the most recent listing fetch, replayed by the item-index provider. */
    private transient String lastFetchText;
    private transient QueryFilter lastFetchFilter;

    /**
     * The product resolved by {@link #beforeEnter}, handed to the URL-sync item loader so the
     * deep link costs a single lookup rather than one per validation and restore.
     */
    private transient Product preloadedProduct;


    private Avatar avatar;
    private Span heading;
    private Span subtitle;
    private BreadcrumbPage currentPage;
    private Div tags;

    public CustomerMasterDetailMaterialView(ProductService productService) {
        this.productService = productService;
//        addClassName("customer-master-detail");
      /*  ResponsiveDiv.configure(this)
                .slotOnce(ViewMode.MOBILE, () -> buildLayout(ViewMode.MOBILE))
                .slotOnce(ViewMode.DESKTOP, () -> buildLayout(ViewMode.DESKTOP))
                .fullHeight()
                .build();*/

        ViewMode viewMode = ViewModeContext.getCurrent().orElse(ViewMode.DESKTOP);

        Components.configure(this)
                .fullHeight()
                .add(buildLayout(viewMode));


    }

    /**
     * Rejects a {@code ?id=} deep link that does not resolve to a product, so a stale or
     * malformed link renders {@link NotFoundErrorView} with a real 404 status instead of
     * silently falling back to the first row.
     *
     * <p>Validating here — during navigation, before the view's content is built — means a
     * bad link never constructs the grid or issues a listing fetch. The resolved product is
     * cached for the URL-sync item loader, so a <em>valid</em> deep link still costs exactly
     * one lookup.</p>
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        preloadedProduct = null;
        String id = event.getLocation().getQueryParameters()
                .getSingleParameter("id").orElse(null);
        if (id == null || id.isBlank()) {
            return;
        }
        Optional<Product> product;
        try {
            product = productService.findById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            product = Optional.empty();
        }
        if (product.isEmpty()) {
            event.rerouteToError(NotFoundException.class, "No customer with id " + id);
            return;
        }
        preloadedProduct = product.get();
    }

    /** Serves the item cached by {@link #beforeEnter} once, then falls back to the service. */
    private Optional<Product> loadById(String id) {
        Product cached = preloadedProduct;
        if (cached != null && id.equals(String.valueOf(cached.getId()))) {
            preloadedProduct = null;
            return Optional.of(cached);
        }
        try {
            return productService.findById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private MasterDetailLayout<Product> buildLayout(ViewMode mode) {
        MasterDetailLayout<Product> layout = Components.masterDetail(Product.class)
                .viewMode(mode)
                .withMobileSheet(Sheet.Side.RIGHT)
                .withUrlSync(product -> String.valueOf(product.getId()), this::loadById)
                .withInitialItem(productService::findFirst)
                .withItemIndexProvider((item, query) ->
                        productService.indexOf(item, lastFetchText, lastFetchFilter, query.getSortOrders())
                                .orElse(null))
                .withAutoSelect()
                .master(master -> master
                        .materialHeader(header -> header
                                .variant(MaterialHeader.Variant.MEDIUM)
                                .viewMode(mode)
                                .headline("Customers")
                                .subtitle(new Span("Customer 360 portfolio"))
                                .details(masterBadges())
                                .primaryAction(Components.button().preset(ButtonPreset.NEW).build()))
                        .listing(listing -> listing
                                .autoCreateColumns(false)
                                .multiSelect()
                                .columns("id", "name", "category", "price", "active")
                                .mobileViewHeader("Customer", "open AR")
                                .mobileViewColumn(mobileColumn())
                                // ListingBundle renders a GridToolbar when search/filter is configured.
                                .search("Search customer #, name, VAT...")
//                                .withFilterPanel()
                                .fetch((query, text, filter, sort) -> {
                                    // Remembered so withItemIndexProvider can count against
                                    // exactly the criteria this fetch used (see indexOf).
                                    lastFetchText = text;
                                    lastFetchFilter = filter;
                                    return productService.fetch(query.getOffset(), query.getLimit(), text, filter, sort);
                                }))
                        .selectionKey(Product::getId))
                .lazyDetail(detail -> detail
                        .materialHeader(header -> header
                                .variant(MaterialHeader.Variant.SMALL)
                                .viewMode(mode)
                                .breadcrumb(breadcrumb())
                                .media(detailAvatar())
                                .headline(detailHeading())
                                .subtitle(detailSubtitle())
                                .tags(detailTags())
                                .actions(detailActions()))
                        .withDetailSync(this::syncDetail)
                        .content(detailTabs()))
                .build();

        return layout;
    }

    private Component masterBadges() {
        return Components.hl().spacing().addToStart(
                Components.iconBadge().size(Size.SM).variant(Variant.INFO).text("342").build(),
                Components.iconBadge().size(Size.SM).variant(Variant.WARNING).text("Overdue").build()).build();
    }

    private LitRenderer<Product> mobileColumn() {
        return LitRendererBuilder.<Product>mobileListItem().withRootVariant(CustomerMasterDetailMaterialView::rowVariant)
                .withNumber(CustomerMasterDetailMaterialView::customerNumber)
                .withWhen(CustomerMasterDetailMaterialView::whenLabel)
                .withVendor(Product::getName)
                .withChip(CustomerMasterDetailMaterialView::chipLabel, CustomerMasterDetailMaterialView::chipVariant)
                .withMetaRef(product -> region(product) + " | " + contacts(product) + " contacts")
                .withAmount(CustomerMasterDetailMaterialView::openAr)
                .withStatus(CustomerMasterDetailMaterialView::status, CustomerMasterDetailMaterialView::statusVariant)
                .build();
    }

    private Breadcrumb breadcrumb() {
        currentPage = new BreadcrumbPage("-");
        return Components.breadcrumb().addWithSeparators(
                new BreadcrumbItem("Home", IndexView.class),
                new BreadcrumbItem("Customers", CustomerMasterDetailMaterialView.class),
                currentPage).build();
    }

    private Avatar detailAvatar() {
        avatar = Components.avatar("?").build();
        return avatar;
    }

    private Span detailHeading() {
        heading = new Span("Select a customer");
        return heading;
    }

    private Span detailSubtitle() {
        subtitle = new Span("Open AR and health snapshot");
        return subtitle;
    }

    private Component detailTags() {
        tags = new Div();
        tags.addClassName("customer-detail-tags");
        return tags;
    }

    private Component detailActions() {
        return Components.hl().spacing().addToStart(
                Components.button().text("Send via WhatsApp").tertiary().build(),
                Components.button().text("Re-send dunning").primary().build()).build();
    }

    private Component detailTabs() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("Overview", new OverviewTab())
                .withLazyTab("Orders 3", OrdersTab::new)
                .withLazyTab("Invoices 12", InvoicesTab::new)
                .withLazyTab("Files 6", FilesTab::new);

        Tabs tabs = builder.selectedIndex(0).build();
        VerticalLayout result = new VerticalLayout(tabs, builder.getContentContainer());
//        result.addClassName("customer-detail-tabs");
        result.setPadding(false);
        result.setSpacing(false);
        return result;
    }

    private void syncDetail(Product product) {
        avatar.setName(product.getName());
        heading.setText(customerNumber(product) + " | " + product.getName());
        subtitle.setText(openAr(product) + " | " + region(product));
        currentPage.setText(customerNumber(product));

        tags.removeAll();
        tags.add(
                Components.iconBadge().size(Size.SM)
                        .variant(product.isActive() ? Variant.SUCCESS : Variant.DESTRUCTIVE)
                        .text(product.isActive() ? "Active" : "Inactive")
                        .build(),
                Components.iconBadge().size(Size.SM).variant(Variant.INFO).text("T1 Strategic").build(),
                Components.iconBadge().size(Size.SM).variant(Variant.INFO).text(region(product)).build());

        if (product.getId() % 4 == 0) {
            tags.add(Components.iconBadge().size(Size.SM).variant(Variant.WARNING).text("VIP").build());
        }

    }

    private static String customerNumber(Product product) {
        return "C-2026-" + String.format("%04d", product.getId());
    }

    private static String region(Product product) {
        return product.getCategory() == null || product.getCategory().isBlank() ? "EMEA | DACH" : product.getCategory();
    }

    private static int contacts(Product product) {
        return (int) (product.getId() % 8) + 1;
    }

    private static BigDecimal price(Product product) {
        return product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
    }

    private static String money(BigDecimal value) {
        return String.format(Locale.US, "EUR %,.2f", value);
    }

    private static String openAr(Product product) {
        BigDecimal value = product.isActive() ? price(product).multiply(BigDecimal.valueOf(60)) : BigDecimal.ZERO;
        return value.signum() == 0 ? "EUR 0 open" : "EUR " + String.format(Locale.US, "%,.1f", value.doubleValue() / 1_000) + "K open";
    }

    private static String whenLabel(Product product) {
        return product.getId() % 5 == 0 ? "updated 2h ago" : product.getId() % 3 == 0 ? "Yesterday" : "1w ago";
    }

    private static RowVariant rowVariant(Product product) {
        return product.isActive() ? RowVariant.SELECTED : RowVariant.EXCEPTION;
    }

    private static String chipLabel(Product product) {
        return product.getId() % 4 == 0 ? "*" : "";
    }

    private static ChipVariant chipVariant(Product product) {
        return product.getId() % 4 == 0 ? ChipVariant.MATCHED : ChipVariant.VARIANCE;
    }

    private static String status(Product product) {
        if (!product.isActive()) {
            return "Churn-risk";
        }
        if (product.getId() % 5 == 0) {
            return "14d overdue";
        }
        return product.getId() % 7 == 0 ? "Onboarding" : "Active";
    }

    private static StatusVariant statusVariant(Product product) {
        if (!product.isActive()) {
            return StatusVariant.AWAITING;
        }
        if (product.getId() % 5 == 0) {
            return StatusVariant.DUE;
        }
        return product.getId() % 7 == 0 ? StatusVariant.APPROVED : StatusVariant.PAID;
    }

    private static Div section(String title, Component... content) {
        return Components.panel()
                .header(title)
                .content(content)
                .card()
                .build();
    }

    private static final class OverviewTab extends Div implements DetailSyncAware<Product> {
        private final HeroStrip hero = Components.heroStrip().variant(HeroStrip.Variant.DARK).wideFirstColumn().build();
        private final EntityFormPanel<Product> accountForm = EntityFormPanel.<Product>bean(Product.class)
                .readOnly()
                .properties("name", "category", "price", "active", "createdDate")
                .build();
        private final ArAgingBar aging = Components.arAgingBar()
                .header(header -> header.title("AR aging").variant(ArAgingBar.Variant.INFO))
                .build();
        private final TimelineStepper activity = Components.timelineStepper().pageSize(10).hasMore(false).width("100%").build();
        private final TotalsCard totals = TotalsCard.builder().build();
        private final Span billing = new Span();
        private final Span delivery = new Span();
        private final Div contacts = new Div();

        OverviewTab() {
//            addClassName("customer-overview");
//            contacts.addClassName("customer-contacts");
            add(hero, section("Account & terms", accountForm), section("Addresses", addresses()), section("AR aging", aging),
                section("Contacts", contacts), section("YTD totals", totals), section("Activity", activity));
        }

        @Override
        public void onItemSelected(Product product) {
            hero.setHeader(new HeroStrip.Header(null, product.getId() % 4 == 0 ? "T1" : "T2", product.getName(),
                                                product.getId() % 4 == 0, customerNumber(product) + " | " + region(product) + " | " + contacts(product) + " contacts"));
            hero.setTags(List.of(
                    new HeroStrip.Tag(product.isActive() ? "Active" : "Inactive", HeroStrip.TagVariant.OK),
                    new HeroStrip.Tag("T1 Strategic", HeroStrip.TagVariant.PRI),
                    new HeroStrip.Tag(region(product), HeroStrip.TagVariant.PRIM)));
            hero.setCells(List.of(
                    new HeroStrip.Cell("Health", product.isActive() ? "A+" : "C", "* 4.7", false, HeroStrip.ValueVariant.OK),
                    new HeroStrip.Cell("Open AR", openAr(product), product.getId() % 5 == 0 ? "14d overdue" : "current", false,
                                       product.getId() % 5 == 0 ? HeroStrip.ValueVariant.ALERT : HeroStrip.ValueVariant.DEFAULT),
                    new HeroStrip.Cell("Open SOs", String.valueOf((product.getId() % 4) + 1), money(price(product).multiply(BigDecimal.valueOf(3))), false, HeroStrip.ValueVariant.DEFAULT),
                    new HeroStrip.Cell("ARR", money(price(product).multiply(BigDecimal.valueOf(120))), "+12% YoY", false, HeroStrip.ValueVariant.DEFAULT)));
            accountForm.setBean(product);
            billing.setText(product.getName() + "\nBilling Office\nMunich, DE 80331");
            delivery.setText(product.getName() + "\nOperations Center\nBerlin, DE 10115");
            setContacts();
            aging.setSegments(List.of(
                    new ArAgingBar.Segment("Current", "Current", 62, ArAgingBar.Variant.SUCCESS),
                    new ArAgingBar.Segment("1-30d", "1-30d", 18, ArAgingBar.Variant.INFO),
                    new ArAgingBar.Segment("31-60d", "31-60d", 14, ArAgingBar.Variant.WARNING),
                    new ArAgingBar.Segment("60d+", "60d+", 6, ArAgingBar.Variant.DANGER)));
            activity.setItems(List.of(
                    new AuditEntry("1", "2h ago", "System", "SEPA DD scheduled").detail(openAr(product)).severity(Severity.SUCCESS),
                    new AuditEntry("2", "Yesterday", "Elena Lindqvist", "Order shipped partial").severity(Severity.INFO),
                    new AuditEntry("3", "4 days ago", "System", "Dunning +7 sent").severity(Severity.WARNING)));
            totals.clearRows();
            totals.addRow("Revenue YTD", money(price(product).multiply(BigDecimal.valueOf(84))));
            totals.addRow("Volume discount (3-yr)", "- " + money(price(product).multiply(BigDecimal.valueOf(8))), TotalsRow.Variant.DISCOUNT);
            totals.addRow("Open AR", openAr(product), TotalsRow.Variant.WARNING);
            totals.addRow("YTD total", money(price(product).multiply(BigDecimal.valueOf(76))), TotalsRow.Variant.GRAND_TOTAL);
        }

        private Component addresses() {
            Div result = new Div();
            result.addClassName("customer-addresses");
            result.add(address("Billing", billing), address("Delivery", delivery));
            return result;
        }

        private static Component address(String label, Span value) {
            Div result = new Div();
            result.addClassName("customer-address");
            Span caption = new Span(label);
            caption.addClassName("customer-address__caption");
            value.addClassName("customer-address__value");
            result.add(caption, value);
            return result;
        }

        private void setContacts() {
            contacts.removeAll();
            for (int index = 1; index <= 4; index++) {
                Avatar avatar = new Avatar("C" + index);
                avatar.setAbbreviation("C" + index);
                Div contact = new Div(avatar, new Span(index == 1 ? "Elena Lindqvist" : "Contact " + index),
                                      new Span(index == 1 ? "Finance director | VIP" : "Operations contact"));
                contact.addClassName("customer-contact");
                contacts.add(contact);
            }
        }
    }

    private record OrderRow(String order, String date, String status, String amount) {
    }

    private record InvoiceRow(String invoice, String dueDate, String status, String balance) {
    }

    private record FileRow(String file, String type, String uploaded, String size) {
    }

    private static final class OrdersTab extends Div implements DetailSyncAware<Product> {
        private final Grid<OrderRow> grid = grid(OrderRow.class, "order", "date", "status", "amount");

        OrdersTab() {
            add(section("Open orders", grid));
        }

        @Override
        public void onItemSelected(Product product) {
            grid.setItems(List.of(
                    new OrderRow("SO-2026-" + String.format("%04d", product.getId() * 10 + 1), "14 Sep 2026", "In fulfilment", money(price(product).multiply(BigDecimal.valueOf(3)))),
                    new OrderRow("SO-2026-" + String.format("%04d", product.getId() * 10 + 2), "02 Sep 2026", "Confirmed", money(price(product).multiply(BigDecimal.valueOf(2))))));
        }
    }

    private static final class InvoicesTab extends Div implements DetailSyncAware<Product> {
        private final Grid<InvoiceRow> grid = grid(InvoiceRow.class, "invoice", "dueDate", "status", "balance");

        InvoicesTab() {
            add(section("Invoices", grid));
        }

        @Override
        public void onItemSelected(Product product) {
            grid.setItems(List.of(
                    new InvoiceRow("INV-2026-" + String.format("%04d", product.getId()), "28 Sep 2026", product.getId() % 5 == 0 ? "14d overdue" : "Open", openAr(product)),
                    new InvoiceRow("INV-2026-" + String.format("%04d", product.getId() + 30), "12 Oct 2026", "Scheduled", money(price(product)))));
        }
    }

    private static final class FilesTab extends Div implements DetailSyncAware<Product> {
        private final Grid<FileRow> grid = grid(FileRow.class, "file", "type", "uploaded", "size");

        FilesTab() {
            add(section("Files", grid));
        }

        @Override
        public void onItemSelected(Product product) {
            grid.setItems(List.of(
                    new FileRow("Master agreement " + customerNumber(product) + ".pdf", "PDF", "12 Sep 2026", "412 KB"),
                    new FileRow("NDA + DPA bundle.pdf", "PDF", "08 Sep 2026", "2.1 MB"),
                    new FileRow("Account forecast.xlsx", "XLSX", "01 Sep 2026", "84 KB")));
        }
    }

    private static <T> Grid<T> grid(Class<T> type, String... properties) {
        Grid<T> grid = new Grid<>(type, false);
        for (String property : properties) {
            grid.addColumn(item -> value(item, property)).setHeader(title(property)).setAutoWidth(true).setFlexGrow(1);
        }
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setAllRowsVisible(true);
        grid.addClassName("customer-data-grid");
        return grid;
    }

    private static String value(Object record, String property) {
        try {
            return String.valueOf(record.getClass().getMethod(property).invoke(record));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Unknown record property: " + property, exception);
        }
    }

    private static String title(String property) {
        String spaced = property.replaceAll("([A-Z])", " $1");
        return spaced.substring(0, 1).toUpperCase(Locale.ROOT) + spaced.substring(1);
    }

}
