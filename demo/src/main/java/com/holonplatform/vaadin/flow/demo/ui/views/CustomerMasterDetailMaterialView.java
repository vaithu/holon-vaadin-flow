package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.holonplatform.vaadin.flow.components.support.ColSpan;
import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.StatusVariant;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.CustomerDetailService;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.Alert.Variant;
import com.iyensoft.vaadin.flow.components.*;
import com.iyensoft.vaadin.flow.components.IconBadge.Size;
import com.iyensoft.vaadin.flow.components.TimelineStepper.AuditEntry;
import com.iyensoft.vaadin.flow.components.TimelineStepper.Severity;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.iyensoft.vaadin.flow.utils.responsive.ViewModeContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
 * Customer 360 demo rebuilt as a pixel-faithful port of the {@code customer-detail.html} mockup.
 *
 * <p>Framework stylesheets own component structure and behavior. This view's scoped stylesheet
 * owns its CRM palette, density, and typography so those application choices do not leak into
 * every {@code MasterDetailLayout} consumer.</p>
 */
@PageTitle("Customer 360 (Material) - Holon Demo")
@Route(value = "customer-master-detail-material", layout = DemoMainLayout.class)
@StyleSheet("context://customer-master-detail-material.css")
public class CustomerMasterDetailMaterialView extends Div implements BeforeEnterObserver {

    private final transient ProductService productService;
    private final transient CustomerDetailService customerDetailService;
    private final MasterDetailLayout<Product> layout;

    Avatar avatar;
    Span heading;
    Span subtitle;
    BreadcrumbPage numberPage;
    BreadcrumbPage namePage;
    HorizontalLayout tags;

    public CustomerMasterDetailMaterialView(ProductService productService,
                                            CustomerDetailService customerDetailService) {
        this.productService = productService;
        this.customerDetailService = customerDetailService;

        ViewMode viewMode = ViewModeContext.getCurrent().orElse(ViewMode.DESKTOP);
        layout = buildLayout(viewMode);

        Components.configure(this)
                .styleName("customer-master-detail-material")
                .fullHeight()
                .add(layout);
    }

    /**
     * Rejects a {@code ?id=} deep link that does not resolve to a product, so a stale or
     * malformed link renders {@link NotFoundErrorView} with a real 404 status instead of
     * silently falling back to the first row.
     *
     * <p>The view instance is constructed before this callback. Validation still happens before
     * navigation completes and before the attached listing performs its first fetch. A resolved
     * product is cached for URL restore, so a valid deep link costs exactly one lookup.</p>
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getLocation().getQueryParameters()
                .getSingleParameter("id").orElse(null);
        if (id == null || id.isBlank()) {
            return;
        }
        try {
            if (layout.preloadFromUrl(id).isEmpty()) {
                event.rerouteToError(NotFoundException.class, "No customer with id " + id);
            }
        } catch (NumberFormatException exception) {
            event.rerouteToError(NotFoundException.class, "Invalid customer id " + id);
        }
    }

    Optional<Product> loadById(String id) {
        return productService.findById(Long.parseLong(id));
    }

    private MasterDetailLayout<Product> buildLayout(ViewMode mode) {
        return Components.masterDetail(Product.class)
                .viewMode(mode)
                .withMobileSheet(Sheet.Side.RIGHT)
                .withUrlSync(product -> String.valueOf(product.getId()), this::loadById)
                .withInitialItem(productService::findFirst)
                .withListingItemIndexProvider((item, context) ->
                        productService.indexOf(item, context.searchText(),
                                        context.getQueryFilter(), context.getQuerySort())
                                .orElse(null))
                .withAutoSelect()
                .master(master -> master
                        .materialHeader(header -> header
                                .variant(MaterialHeader.Variant.SMALL)
                                .viewMode(mode)
                                .headline("Customers")
                                .details(masterFilters()))
                        .listing(listing -> listing
                                .autoCreateColumns(false)
                                .multiSelect()
                                .columns("id", "name", "category", "price", "active")
                                .mobileViewHeader("Customer", "open AR")
                                .mobileViewColumn(mobileColumn())
                                .search("Search customer #, name, VAT...")
                                .emptyState(EmptyStates.noItems(
                                        "No customers found",
                                        "Add a customer to start building your portfolio."))
                                .noResultsState(EmptyStates.noResults())
                                .withToolbarCustomizer(toolbar -> toolbar.optionsMenu(false))
                                .fetch((query, text, filter, sort) ->
                                        productService.fetch(query.getOffset(), query.getLimit(),
                                                text, filter, sort)))
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
                                .sticky()
                                .actions(detailActions()))
                        .withDetailSync(this::syncDetail)
                        .content(detailTabs()))
                .build();
    }

    // ── Master ────────────────────────────────────────────────────────────────

    /** The mockup's segmented filter rail, rendered by the framework's chip styling. */
    private Component masterFilters() {
        return Components.chipGroup()
                .addChip("All", (int) productService.count(null), true)
                .addChip("★ T1", 28)
                .addChip("★ T2", 64)
                .addChip("Trial", 18)
                .addChip("Overdue", 12)
                .wrap();
    }

    /**
     * The master row renderer. Its {@code .mli-*} output is styled by
     * {@code mobile-list-lit-renderer.css}, which is the same markup the mockup hand-wrote —
     * so the row needs data bindings only, never styling.
     */
    private LitRenderer<Product> mobileColumn() {
        return LitRendererBuilder.<Product>mobileListItem()
                .withNumber(CustomerMasterDetailMaterialView::customerNumber)
                .withWhen(CustomerMasterDetailMaterialView::whenLabel)
                .withVendor(CustomerMasterDetailMaterialView::customerName)
                .withMetaRef(product -> city(product) + " · " + vatId(product)
                        + " · " + contacts(product) + " contacts")
                .withAmount(CustomerMasterDetailMaterialView::openAr)
                .withStatus(CustomerMasterDetailMaterialView::status,
                        CustomerMasterDetailMaterialView::statusVariant)
                .build();
    }

    // ── Detail header ─────────────────────────────────────────────────────────

    private Breadcrumb breadcrumb() {
        numberPage = new BreadcrumbPage("-");
        namePage = new BreadcrumbPage("-");
        return Components.breadcrumb().addWithSeparators(
                new BreadcrumbItem("CRM", IndexView.class),
                new BreadcrumbItem("Customers", CustomerMasterDetailMaterialView.class),
                numberPage,
                namePage).build();
    }

    private Avatar detailAvatar() {
        avatar = Components.avatar("?").build();
        return avatar;
    }

    private Span detailHeading() {
        heading = Components.span().text("Select a customer").build();
        return heading;
    }

    private Span detailSubtitle() {
        subtitle = Components.span().text("Open AR and health snapshot").build();
        return subtitle;
    }

    private Component detailTags() {
        tags = Components.hl()
                .spacing()
                .alignItems(FlexComponent.Alignment.CENTER)
                .build();
        return tags;
    }

    private Component[] detailActions() {
        return new Component[]{
                Components.button().icon(VaadinIcon.PRINT).tertiary()
                        .iconRounded()
                        .build(),
                Components.button().icon(VaadinIcon.COPY).tertiary().iconRounded().build(),
                Components.button().icon(VaadinIcon.DOWNLOAD_ALT).tertiary().iconRounded().build(),
                Components.button().text("Send via WhatsApp").icon(VaadinIcon.COMMENT).build(),
                Components.button().text("Re-send dunning").icon(VaadinIcon.ENVELOPE).primary().build()
        };
    }

    private Component detailTabs() {
        Div tabs = Components.div().build();
        LazyTabsBuilder.create(tabs)
                .withEagerTabContents("Overview", new OverviewTab())
                .withLazyTab("Orders", () -> ordersList(customerDetailService))
                .withLazyTab("Invoices", () -> invoicesList(customerDetailService))
                .withLazyTabContents("Activity", () -> new ActivityTab(customerDetailService))
                .withLazyTab("Files", () -> filesList(customerDetailService));
        return tabs;
    }

    void syncDetail(Product product) {
        avatar.setName(product.getName());
        heading.setText(customerName(product));
        subtitle.setText(customerNumber(product)
                + " · created " + createdOn(product)
                + " · since " + tenure(product)
                + " · " + contacts(product) + " contacts"
                + " · owner " + owner(product));
        numberPage.setText(customerNumber(product));
        namePage.setText(product.getName());

        tags.removeAll();
        tags.add(
                Components.iconBadge().size(Size.SM)
                        .variant(product.isActive() ? Variant.SUCCESS : Variant.DESTRUCTIVE)
                        .text(product.isActive() ? "Active" : "Inactive")
                        .build(),
                Components.iconBadge().size(Size.SM).variant(Variant.WARNING).text("★ " + tier(product) + " Strategic").build(),
                Components.iconBadge().size(Size.SM).variant(Variant.INFO).text(region(product)).build());

        if (starred(product)) {
            tags.add(Components.iconBadge().size(Size.SM).variant(Variant.DEFAULT).text("VIP").build());
        }
        if (overdue(product)) {
            tags.add(Components.iconBadge().size(Size.SM).variant(Variant.WARNING).text("14d AR overdue").build());
        }
    }

    // ── Derived demo data ─────────────────────────────────────────────────────

    private static String customerNumber(Product product) {
        return "C-2026-" + String.format("%04d", product.getId());
    }

    /** Name plus the mockup's gold key-account star, which the renderer emits inline. */
    private static String customerName(Product product) {
        return starred(product) ? product.getName() + " ★" : product.getName();
    }

    private static boolean starred(Product product) {
        return product.getId() % 4 == 0;
    }

    private static boolean overdue(Product product) {
        return product.isActive() && product.getId() % 5 == 0;
    }

    private static String tier(Product product) {
        return starred(product) ? "T1" : "T2";
    }

    private static String region(Product product) {
        return product.getCategory() == null || product.getCategory().isBlank() ? "EMEA · DACH" : product.getCategory();
    }

    private static final String[] CITIES = {"Munich", "Prague", "Vienna", "Berlin", "Lyon", "Stockholm", "Rotterdam"};

    private static String city(Product product) {
        return CITIES[(int) (product.getId() % CITIES.length)];
    }

    private static String vatId(Product product) {
        long id = product.getId();
        return String.format("DE %03d %03d %02d", 200 + id % 700, 100 + id % 800, id % 90);
    }

    private static String createdOn(Product product) {
        return String.format("2024-%02d-%02d", (product.getId() % 12) + 1, (product.getId() % 27) + 1);
    }

    private static String tenure(Product product) {
        return String.format(Locale.US, "%.1f yr", 1.0 + (product.getId() % 40) / 10.0);
    }

    private static String owner(Product product) {
        return product.getId() % 2 == 0 ? "Elena Lindqvist" : "Marcus Reiner";
    }

    private static int contacts(Product product) {
        return (int) (product.getId() % 8) + 1;
    }

    private static BigDecimal price(Product product) {
        return product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
    }

    private static String money(BigDecimal value) {
        return String.format(Locale.US, "€%,.2f", value);
    }

    /** Compact money, as the mockup prints it — {@code €14.8K} / {@code €1.84M}. */
    private static String compactMoney(BigDecimal value) {
        double amount = value.doubleValue();
        if (amount >= 1_000_000) {
            return String.format(Locale.US, "€%.2fM", amount / 1_000_000);
        }
        if (amount >= 1_000) {
            return String.format(Locale.US, "€%.1fK", amount / 1_000);
        }
        return String.format(Locale.US, "€%.0f", amount);
    }

    private static BigDecimal openArValue(Product product) {
        return product.isActive() ? price(product).multiply(BigDecimal.valueOf(60)) : BigDecimal.ZERO;
    }

    private static String openAr(Product product) {
        return compactMoney(openArValue(product)) + " open";
    }

    private static String whenLabel(Product product) {
        return product.getId() % 5 == 0 ? "updated 2h ago" : product.getId() % 3 == 0 ? "Yesterday" : "1w ago";
    }

    private static String status(Product product) {
        if (!product.isActive()) {
            return "Churn-risk";
        }
        if (overdue(product)) {
            return "14d overdue";
        }
        return product.getId() % 7 == 0 ? "Onboarding" : "Active";
    }

    private static StatusVariant statusVariant(Product product) {
        if (!product.isActive()) {
            return StatusVariant.OVERDUE;
        }
        if (overdue(product)) {
            return StatusVariant.AWAITING;
        }
        return product.getId() % 7 == 0 ? StatusVariant.DUE : StatusVariant.PAID;
    }

    // ── Card scaffolding ──────────────────────────────────────────────────────

    private static Component sectionAction(String label) {
        return Components.button()
                .text(label)
                .tertiaryInline()
                .small()
                .onClick(event -> Components.notification()
                        .error()
                        .text("Not implemented")
                        .build()
                        .open())
                .build();
    }

    /** A bordered mini-panel — the mockup's address and contact blocks. */
    private static Component block(String caption, String title, String... lines) {
        Component[] content = new Component[lines.length];
        for (int i = 0; i < lines.length; i++) {
            content[i] = Components.div()
                    .add(Components.span().text(lines[i]).build())
                    .build();
        }

        return Components.detailPanel(title)
                .details(new Span(caption))
                .content(content)
                .background(PanelVariant.Background.SURFACE_2)
                .build();
    }

    private static Div blockRow(Component... blocks) {
        RowBuilder row = RowBuilder.create().styleName("gap-m");
        for (Component block : blocks) {
            row.add(ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.TABLET, 2)
                    .add(block));
        }
        return row.build();
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    static final class OverviewTab extends Div implements DetailSyncAware<Product> {

        /**
         * The mockup's 360 strip is metrics-only: the thumbnail, star and tag rail it shows in the
         * reference screenshot live in the page header above, so no {@link HeroStrip.Header} is set.
         */
        private final HeroStrip hero = Components.heroStrip()
                .variant(HeroStrip.Variant.DARK)
                .emptyState("No account metrics available.")
                .fullWidth()
                .build();

        private final EntityFormPanel<Product> accountForm = EntityFormPanel.<Product>bean(Product.class)
                .readOnly()
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .properties("name", "category", "price", "active", "createdDate")
                .autoLabels(true)
                .build();

        private final ArAgingBar aging = Components.arAgingBar()
                .header(header -> header.title("AR aging").variant(ArAgingBar.Variant.INFO))
                .emptyState("No AR aging data available.")
                .fullWidth()
                .build();
        private final TotalsCard totals = TotalsCard.builder()
                .emptyState("No totals available.")
                .fullWidth()
                .build();

        private final Div billTo = Components.div().fullWidth().build();
        private final Div addresses = Components.div().fullWidth().build();
        private final Div contacts = Components.div().fullWidth().build();

        OverviewTab() {
            Components.configure(this)
                    .fullWidth()
                    .add(hero,
                    Components.detailPanel("Addresses")
                            .actions(sectionAction("+ Add address"))
                            .content(addresses)
                            .build(),
                    Components.detailPanel("Account & terms")
                            .actions(sectionAction("Edit"))
                            .content(billTo, accountForm)
                            .build(),
                    Components.detailPanel("AR aging").content(aging).build(),
                    Components.detailPanel("Contacts").content(contacts).build(),
                    Components.detailPanel("YTD totals").content(totals).build());
        }

        @Override
        public void onItemSelected(Product product) {
            hero.setCells(List.of(
                    new HeroStrip.Cell("Account health", product.isActive() ? "A+" : "C",
                            "★ 4.7 · " + tenure(product) + " tenure", true,
                            product.isActive() ? HeroStrip.ValueVariant.OK : HeroStrip.ValueVariant.ALERT),
                    new HeroStrip.Cell("Open AR", compactMoney(openArValue(product)),
                            overdue(product) ? "1 invoice · 14d" : "current", false,
                            overdue(product) ? HeroStrip.ValueVariant.ALERT : HeroStrip.ValueVariant.DEFAULT),
                    new HeroStrip.Cell("Open orders", String.valueOf((product.getId() % 4) + 1),
                            compactMoney(price(product).multiply(BigDecimal.valueOf(200))) + " pending", false,
                            HeroStrip.ValueVariant.DEFAULT),
                    new HeroStrip.Cell("ARR", compactMoney(price(product).multiply(BigDecimal.valueOf(7600))),
                            "+12% YoY", false, HeroStrip.ValueVariant.DEFAULT),
                    new HeroStrip.Cell("NPS", String.valueOf(40 + product.getId() % 55),
                            "promoter · Q2", false, HeroStrip.ValueVariant.DEFAULT)));

            billTo.removeAll();
            billTo.add(billToCard(product));

            accountForm.setBean(product);

            addresses.removeAll();
            addresses.add(blockRow(
                    block("BILLING · PRIMARY", product.getName(),
                            "Landsberger Straße 410", city(product) + " · Germany", vatId(product)),
                    block("SHIP-TO · WAREHOUSE", product.getName() + " DC",
                            "Gutenbergstraße 12", city(product) + " · Germany", "Mon–Fri 07:00–17:00")));

            contacts.removeAll();
            contacts.add(blockRow(
                    block("DECISION MAKER · VIP", owner(product), "Finance director", "+49 89 1200 4410"),
                    block("OPERATIONS", "Jonas Berger", "Head of logistics", "+49 89 1200 4422")));

            aging.setSegments(List.of(
                    new ArAgingBar.Segment("Current", "Current", 62, ArAgingBar.Variant.SUCCESS),
                    new ArAgingBar.Segment("1-30d", "1-30d", 18, ArAgingBar.Variant.INFO),
                    new ArAgingBar.Segment("31-60d", "31-60d", 14, ArAgingBar.Variant.WARNING),
                    new ArAgingBar.Segment("60d+", "60d+", 6, ArAgingBar.Variant.DANGER)));

            totals.clearRows();
            totals.addRow("Revenue YTD", money(price(product).multiply(BigDecimal.valueOf(84))));
            totals.addRow("Volume discount (3-yr)", "- " + money(price(product).multiply(BigDecimal.valueOf(8))),
                    TotalsRow.Variant.DISCOUNT);
            totals.addRow("Open AR", openAr(product), TotalsRow.Variant.WARNING);
            totals.addRow("YTD total", money(price(product).multiply(BigDecimal.valueOf(76))),
                    TotalsRow.Variant.GRAND_TOTAL);
        }

        /** The mockup's highlighted "Bill to" row above the terms grid. */
        private static Card billToCard(Product product) {
            Card card = Components.card()
                    .media(Components.avatar(product.getName()).build())
                    .title(Components.divLabel().text(product.getName()))
                    .subtitle(new Span(vatId(product) + " · Landsberger Straße 410, " + city(product)))
                    .headerSuffix(Components.button().text("View 360 →").tertiaryInline().small().build())
                    .fullWidth()
                    .build();
            card.addThemeVariants(CardVariant.LUMO_HORIZONTAL, CardVariant.LUMO_OUTLINED);
            return card;
        }
    }

    // ── List tabs ─────────────────────────────────────────────────────────────
    // Each loader is the single source for both the tab rows and its badge counter.

    private record OrderRow(String order, String date, String status, String amount) {
    }

    private record InvoiceRow(String invoice, String dueDate, String status, String balance) {
    }

    private record FileRow(String file, String type, String uploaded, String size) {
    }

    static DetailList<Product, OrderRow> ordersList(CustomerDetailService service) {
        return Components.<Product, OrderRow>detailList("Open orders", OrderRow.class,
                        product -> service.findOpenOrders(product).stream()
                                .map(row -> new OrderRow(row.order(), row.date(), row.status(), row.amount()))
                                .toList())
                .emptyState("No open orders.", "This customer has no open orders.")
                .column(OrderRow::order, "Order")
                .column(OrderRow::date, "Date")
                .column(OrderRow::status, "Status")
                .column(OrderRow::amount, "Amount")
                .build();
    }

    static DetailList<Product, InvoiceRow> invoicesList(CustomerDetailService service) {
        return Components.<Product, InvoiceRow>detailList("Invoices", InvoiceRow.class,
                        product -> service.findInvoices(product).stream()
                                .map(row -> new InvoiceRow(row.invoice(), row.dueDate(), row.status(), row.balance()))
                                .toList())
                .emptyState("No invoices.", "This customer has no invoices.")
                .column(InvoiceRow::invoice, "Invoice")
                .column(InvoiceRow::dueDate, "Due date")
                .column(InvoiceRow::status, "Status")
                .column(InvoiceRow::balance, "Balance")
                .build();
    }

    static DetailList<Product, FileRow> filesList(CustomerDetailService service) {
        return Components.<Product, FileRow>detailList("Files", FileRow.class,
                        product -> service.findFiles(product).stream()
                                .map(row -> new FileRow(row.file(), row.type(), row.uploaded(), row.size()))
                                .toList())
                .emptyState("No files uploaded.", "Upload a file to keep customer documents together.")
                .column(FileRow::file, "File")
                .column(FileRow::type, "Type")
                .column(FileRow::uploaded, "Uploaded")
                .column(FileRow::size, "Size")
                .build();
    }

    static final class ActivityTab extends Div implements DetailSyncAware<Product> {
        private final TimelineStepper timeline = Components.timelineStepper()
                .pageSize(10).hasMore(false).width("100%").build();
        private final Empty emptyState = EmptyStates.relatedItems("No activity recorded");
        private final CustomerDetailService service;

        ActivityTab(CustomerDetailService service) {
            this.service = service;
            Components.configure(this).fullWidth()
                    .add(Components.detailPanel("Activity")
                            .content(timeline, emptyState)
                            .build());
            emptyState.setVisible(false);
        }

        @Override
        public void onItemSelected(Product product) {
            List<CustomerDetailService.Activity> entries = service.findActivity(product);
            timeline.setItems(entries.stream()
                    .map(entry -> {
                        AuditEntry audit = new AuditEntry(entry.id(), entry.time(),
                                entry.actor(), entry.description());
                        if (entry.detail() != null) {
                            audit.detail(entry.detail());
                        }
                        return audit.severity(Severity.valueOf(entry.severity()));
                    })
                    .toList());
            timeline.setVisible(!entries.isEmpty());
            emptyState.setVisible(entries.isEmpty());
        }
    }

}
