package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.query.QueryFilter;
import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.StatusVariant;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
 * Customer 360 demo rebuilt as a pixel-faithful port of the {@code customer-detail.html} mockup.
 *
 * <p>The view carries <strong>no stylesheet of its own</strong>: every visual token — the Outfit /
 * JetBrains Mono type stack, the CRM palette, card shadows and the {@code .mli-*} master-row
 * geometry — already ships with the framework components it composes ({@code master-detail-v2.css},
 * {@code mobile-list-lit-renderer.css}, {@code material-header.css}, {@code hero-strip.css},
 * {@code panel.css}, …), each auto-loaded via its component's {@code @StyleSheet}. Matching the
 * mockup is therefore purely a matter of configuration, not of overriding CSS.</p>
 */
@PageTitle("Customer 360 (Material) - Holon Demo")
@Route(value = "customer-master-detail-material", layout = DemoMainLayout.class)
public class CustomerMasterDetailMaterialView extends Div implements BeforeEnterObserver {

    /** Portfolio-wide counters shown in the master header, mirroring the mockup's filter rail. */
    private static final int TOTAL_CUSTOMERS = 342;

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
    private BreadcrumbPage numberPage;
    private BreadcrumbPage namePage;
    private HorizontalLayout tags;

    public CustomerMasterDetailMaterialView(ProductService productService) {
        this.productService = productService;

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
        return Components.masterDetail(Product.class)
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
                                .withToolbarCustomizer(toolbar -> toolbar.optionsMenu(false))
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
    }

    // ── Master ────────────────────────────────────────────────────────────────

    /** The mockup's segmented filter rail, rendered by the framework's chip styling. */
    private Component masterFilters() {
        return Components.chipGroup()
                .addChip("All", TOTAL_CUSTOMERS, true)
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
        heading = new Span("Select a customer");
        return heading;
    }

    private Span detailSubtitle() {
        subtitle = new Span("Open AR and health snapshot");
        return subtitle;
    }

    private Component detailTags() {
        tags = Components.hl().spacing().build();
        tags.setAlignItems(FlexComponent.Alignment.CENTER);
        return tags;
    }

    private Component[] detailActions() {
        return new Component[]{
                Components.button().icon(VaadinIcon.PRINT).tertiary()
                        .styleName("btn--icon")
                        .build(),
                Components.button().icon(VaadinIcon.COPY).tertiary().build(),
                Components.button().icon(VaadinIcon.DOWNLOAD_ALT).tertiary().build(),
                Components.button().text("Send via WhatsApp").icon(VaadinIcon.COMMENT).build(),
                Components.button().text("Re-send dunning").icon(VaadinIcon.ENVELOPE).primary().build()
        };
    }

    private Component detailTabs() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("Overview", new OverviewTab())
                .withLazyTab("Orders", 3, OrdersTab::new)
                .withLazyTab("Invoices", 12, InvoicesTab::new)
                .withLazyTab("Activity", 42, ActivityTab::new)
                .withLazyTab("Files", 6, FilesTab::new);

        Tabs tabs = builder.selectedIndex(0).build();
        tabs.setWidthFull();
        Component content = builder.getContentContainer();
        content.getElement().getStyle().set("width", "100%");
        VerticalLayout result = new VerticalLayout(tabs, content);
        result.setPadding(false);
        result.setSpacing(false);
        result.setWidthFull();
        // The detail body stretches its children; without this the tab content
        // shrinks to fit and the panels no longer span the detail column.
        result.setAlignItems(FlexComponent.Alignment.STRETCH);
        return result;
    }

    private void syncDetail(Product product) {
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

    private static Div section(String title, Component... content) {
        return stretch(Components.panel().header(title).content(content).card().build());
    }

    /** Panel whose header carries the mockup's right-aligned affordance ("Edit", "+ Add address"). */
    private static Div section(String title, String actionLabel, Component... content) {
        HorizontalLayout header = Components.hl().spacing().build();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(Components.span().styleName("section-heading").text(title).build(),
                Components.button().text(actionLabel).tertiaryInline().small().build());
        return stretch(Components.panel().header(header).content(content).card().build());
    }

    /**
     * Makes a card fill the detail column. {@code .iyen-panel} is {@code align-items: flex-start}
     * and carries its own margin, so stretching the cross axis — rather than forcing
     * {@code width: 100%} — keeps the card flush with its siblings without overflowing the margin.
     */
    private static <C extends Component> C stretch(C component) {
        component.getElement().getStyle().set("align-self", "stretch");
        return component;
    }

    /** A bordered mini-card — the mockup's address and contact blocks. */
    private static Card block(String caption, String title, String... lines) {
        Card card = Components.card()
                .title(Components.divLabel().text(title))
                .subtitle(new Span(caption))
                .build();
        card.addThemeVariants(CardVariant.LUMO_OUTLINED);
        for (String line : lines) {
            // Block-level so each address line keeps its own row, as in the mockup.
            card.add(new Div(new Span(line)));
        }
        return card;
    }

    private static HorizontalLayout blockRow(Component... blocks) {
        HorizontalLayout row = Components.hl().spacing().build();
        row.setWidthFull();
        for (Component block : blocks) {
            row.add(block);
            row.setFlexGrow(1, block);
        }
        return row;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private static final class OverviewTab extends VerticalLayout implements DetailSyncAware<Product> {

        /**
         * The mockup's 360 strip is metrics-only: the thumbnail, star and tag rail it shows in the
         * reference screenshot live in the page header above, so no {@link HeroStrip.Header} is set.
         */
        private final HeroStrip hero = Components.heroStrip().variant(HeroStrip.Variant.DARK).build();

        private final EntityFormPanel<Product> accountForm = EntityFormPanel.<Product>bean(Product.class)
                .readOnly()
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .properties("name", "category", "price", "active", "createdDate")
                .build();

        private final ArAgingBar aging = Components.arAgingBar()
                .header(header -> header.title("AR aging").variant(ArAgingBar.Variant.INFO))
                .build();
        private final TotalsCard totals = TotalsCard.builder().build();

        private final Div billTo = new Div();
        private final Div addresses = new Div();
        private final Div contacts = new Div();

        OverviewTab() {
            setWidthFull();
            setPadding(false);
            setSpacing(false);
            hero.setWidthFull();
            aging.setWidthFull();
            totals.setWidthFull();
            billTo.setWidthFull();
            addresses.setWidthFull();
            contacts.setWidthFull();
            // The hero strip has no margin of its own; match the panels' so the column aligns.
            hero.getStyle().set("margin", "var(--lumo-space-m)");
            hero.getStyle().set("width", "auto");
            stretch(hero);
            add(hero,
                    section("Account & terms", "Edit", billTo, accountForm),
                    section("Addresses", "+ Add address", addresses),
                    section("AR aging", aging),
                    section("Contacts", contacts),
                    section("YTD totals", totals));
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
                    .build();
            card.addThemeVariants(CardVariant.LUMO_HORIZONTAL, CardVariant.LUMO_OUTLINED);
            card.setWidthFull();
            return card;
        }
    }

    private record OrderRow(String order, String date, String status, String amount) {
    }

    private record InvoiceRow(String invoice, String dueDate, String status, String balance) {
    }

    private record FileRow(String file, String type, String uploaded, String size) {
    }

    private static final class OrdersTab extends VerticalLayout implements DetailSyncAware<Product> {
        private final Grid<OrderRow> grid = grid(OrderRow.class, "order", "date", "status", "amount");

        OrdersTab() {
            setWidthFull();
            setPadding(false);
            setSpacing(false);
            add(section("Open orders", grid));
        }

        @Override
        public void onItemSelected(Product product) {
            grid.setItems(List.of(
                    new OrderRow("SO-2026-" + String.format("%04d", product.getId() * 10 + 1), "14 Sep 2026",
                            "In fulfilment", money(price(product).multiply(BigDecimal.valueOf(3)))),
                    new OrderRow("SO-2026-" + String.format("%04d", product.getId() * 10 + 2), "02 Sep 2026",
                            "Confirmed", money(price(product).multiply(BigDecimal.valueOf(2))))));
        }
    }

    private static final class InvoicesTab extends VerticalLayout implements DetailSyncAware<Product> {
        private final Grid<InvoiceRow> grid = grid(InvoiceRow.class, "invoice", "dueDate", "status", "balance");

        InvoicesTab() {
            setWidthFull();
            setPadding(false);
            setSpacing(false);
            add(section("Invoices", grid));
        }

        @Override
        public void onItemSelected(Product product) {
            grid.setItems(List.of(
                    new InvoiceRow("INV-2026-" + String.format("%04d", product.getId()), "28 Sep 2026",
                            overdue(product) ? "14d overdue" : "Open", openAr(product)),
                    new InvoiceRow("INV-2026-" + String.format("%04d", product.getId() + 30), "12 Oct 2026",
                            "Scheduled", money(price(product)))));
        }
    }

    private static final class ActivityTab extends VerticalLayout implements DetailSyncAware<Product> {
        private final TimelineStepper activity = Components.timelineStepper()
                .pageSize(10).hasMore(false).width("100%").build();

        ActivityTab() {
            setWidthFull();
            setPadding(false);
            setSpacing(false);
            add(section("Activity", activity));
        }

        @Override
        public void onItemSelected(Product product) {
            activity.setItems(List.of(
                    new AuditEntry("1", "2h ago", "System", "SEPA DD scheduled")
                            .detail(openAr(product)).severity(Severity.SUCCESS),
                    new AuditEntry("2", "Yesterday", owner(product), "Order shipped partial")
                            .severity(Severity.INFO),
                    new AuditEntry("3", "4 days ago", "System", "Dunning +7 sent")
                            .severity(Severity.WARNING)));
        }
    }

    private static final class FilesTab extends VerticalLayout implements DetailSyncAware<Product> {
        private final Grid<FileRow> grid = grid(FileRow.class, "file", "type", "uploaded", "size");

        FilesTab() {
            setWidthFull();
            setPadding(false);
            setSpacing(false);
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
        grid.setWidthFull();
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
