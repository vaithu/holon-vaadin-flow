package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.css.CSSUtility;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailAccent;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;

import java.util.List;

/**
 * Demo view for {@link MasterDetailConfigurator}.
 *
 * <p>Covers every entry point and runtime operation:
 * <ol>
 *   <li>{@code mobile(Div)} — single master panel (no detail)</li>
 *   <li>{@code desktop(Div, Div)} — side-by-side pre-built panels</li>
 *   <li>{@code master()/detail()} Consumer lambdas — header, footer, card, actions</li>
 *   <li>Live sync — typed {@code create(Class)}, {@code listing()}, {@code selectionKey},
 *       {@link DetailSyncAware}, {@code LazyTabsBuilder.cacheEnabled()},
 *       {@code selectionSignal()}, {@code selectFirst()}, {@code clearSelection()}</li>
 *   <li>Multiple {@code withDetailSync} — independent handlers updating separate UI fragments</li>
 *   <li>{@code configure(existingLayout, beanType)} — reconfigure a pre-existing layout;
 *       {@code withUrlSync} deep-link pattern (code snippet)</li>
 *   <li>Mobile + {@link Sheet} — {@code viewMode(MOBILE)} skips detail DOM build;
 *       top-level {@code withDetailSync} populates a Sheet on row tap</li>
 * </ol>
 */
@PageTitle("MasterDetailConfigurator – Holon Demo")
@Route(value = "master-detail-configurator", layout = DemoMainLayout.class)
public class MasterDetailConfiguratorDemoView extends Div {

    // ── Sample data ───────────────────────────────────────────────────────────

    public static final class DemoOrder {
        private final long   id;
        private final String customer;
        private final String product;
        private final double amount;
        private final String status;
        public DemoOrder(long id, String customer, String product, double amount, String status) {
            this.id = id; this.customer = customer; this.product = product;
            this.amount = amount; this.status = status;
        }
        public long   getId()       { return id; }
        public String getCustomer() { return customer; }
        public String getProduct()  { return product; }
        public double getAmount()   { return amount; }
        public String getStatus()   { return status; }
    }

    private static final List<DemoOrder> DEMO_ORDERS = List.of(
        new DemoOrder(1001, "Alice Martin",  "Laptop Pro",      1299.00, "DELIVERED"),
        new DemoOrder(1002, "Bob Chen",       "Wireless Mouse",    49.99, "SHIPPED"),
        new DemoOrder(1003, "Carol White",   "4K Monitor",        799.00, "PROCESSING"),
        new DemoOrder(1004, "Dave Brown",    "Mechanical KB",     159.99, "PENDING"),
        new DemoOrder(1005, "Eva Schmidt",   "USB-C Hub",          89.99, "CANCELLED"),
        new DemoOrder(1006, "Frank Lee",     "Desk Lamp",          45.00, "DELIVERED")
    );

    private final ProductService productService;

    public MasterDetailConfiguratorDemoView(ProductService productService) {
        this.productService = productService;
        addClassName("app-view");

        add(new H1("MasterDetailConfigurator"),
                new Paragraph("Builds and returns a MasterDetailLayout — a smart Div that owns all selection " +
                        "and sync state. Use MasterDetailBuilder.create(Class<T>) to build from scratch, or " +
                        "MasterDetailConfigurator.configure(layout, Class<T>) to configure an existing instance."));

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(
                mobileExample(),
                desktopExample(),
                propertySetListingExample(),
                fluentNodeExample(),
                liveSyncExample(),
                multipleHandlersExample(),
                accentColorsExample(),
                configureExistingLayoutExample(),
                mobileSheetExample()
        );
        add(examples);
    }

    // ── Example 1: mobile(Div) ────────────────────────────────────────────────

    private DemoExample mobileExample() {
        Div masterContent = new Div();
        masterContent.addClassName("card");
        masterContent.add(
                new H3("Customers"),
                new Paragraph("Alice Johnson"),
                new Paragraph("Bob Smith"),
                new Paragraph("Carol White")
        );

        MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class).mobile(masterContent).build();

        return new DemoExample("mobile(Div) — single-panel mode", layout, """
                 MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                        .mobile(masterContent)
                        .build();
                // layout.getChildren() → [ masterContent ]
                """);
    }

    // ── Example 2: desktop(Div, Div) ──────────────────────────────────────────

    private DemoExample desktopExample() {
        Div masterDiv = new Div();
        masterDiv.addClassNames("card", CSSUtility.Common.FLEX_GROW_1);
        masterDiv.add(
                new H3("Customers"),
                new Paragraph("Alice Johnson"),
                new Paragraph("Bob Smith"),
                new Paragraph("Carol White")
        );

        Div detailDiv = new Div();
        detailDiv.addClassNames("card", CSSUtility.Common.FLEX_GROW_1);
        detailDiv.add(
                new H3("Details"),
                new Paragraph("Select a customer on the left to view their profile.")
        );

        MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .desktop(masterDiv, detailDiv)
                .build();

        return new DemoExample("desktop(Div, Div) — explicit side-by-side panels", layout, """
                 MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                        .styleName(CSSUtility.Common.D_FLEX)
                        .styleName(CSSUtility.Common.FLEX_ROW)
                        .styleName(CSSUtility.Common.GAP_3)
                        .desktop(masterDiv, detailDiv)
                        .build();
                """);
    }

    // ── Example 4: live listing sync + LazyTabs + DetailSyncAware ─────────────

    /**
     * Demonstrates the recommended SaaS pattern:
     * <ul>
     *   <li>{@code create(DemoOrder.class)} — typed builder; enables no-arg {@code listing()},
     *       {@code selectionKey()}, and type-witness-free {@code withDetailSync}</li>
     *   <li>{@code listing(Consumer)} — uses the stored bean type from {@code create()}</li>
     *   <li>{@code selectionKey(DemoOrder::getId)} — stable row identity; enables highlighting</li>
     *   <li>{@code withDetailSync((DemoOrder o) -> ...)} — explicit handler with a typed lambda</li>
     *   <li>{@link DetailSyncAware} tab auto-discovered by {@code scanAndRegister()} inside the {@code detail()} consumer</li>
     *   <li>{@code LazyTabsBuilder.cacheEnabled()} — one instance per tab per session;
     *       {@code SyncableContentContainer} relays sync to lazy-built tabs automatically</li>
     *   <li>{@code selectionSignal()} — reactive {@link Signal} updated on every row selection; bind with
     *       {@code Signal.effect(component, () -> ...)} for zero-boilerplate reactive labels</li>
     *   <li>{@code selectFirst(ViewMode)} — highlight first row (desktop only; no-op on mobile)</li>
     *   <li>{@code clearSelection()} — un-highlight row and reset signal to {@code null}</li>
     *   <li>{@code notifyDataChanged()} — re-fire all sync handlers with the currently selected item
     *       (call after an in-place save to keep the detail panel fresh)</li>
     * </ul>
     */
    private DemoExample liveSyncExample() {
        // withDetailSync() target: simple Span updated on every row click
        var selectionLabel = new Span("← Select an order");

        // Reactive signal label — zero imperative handler; updated via Signal.effect()
        var signalLabel = new Span("Signal: (nothing selected)");

        // Eager tab — in DOM when the detail() consumer runs → auto-discovered by scanAndRegister()
        var summaryTab = new OrderSummaryTab();

        // LazyTabs with cache ON — one component instance per tab per session
        var tabs = LazyTabsBuilder.create()
                .withContainer(new Div())
                .cacheEnabled()
                .withEagerTab("Summary", summaryTab)
                .withLazyTab("Notes",   OrderNotesTab::new)
                .selectedIndex(0);

        MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "customer", "product", "amount", "status")
                        .search("Search orders…")
                        .fetch((q, text, sort) -> {
                            var stream = DEMO_ORDERS.stream();
                            if (text != null && !text.isBlank()) {
                                var lc = text.toLowerCase();
                                stream = stream.filter(o ->
                                        o.getCustomer().toLowerCase().contains(lc) ||
                                        o.getProduct().toLowerCase().contains(lc));
                            }
                            return stream.skip(q.getOffset()).limit(q.getLimit());
                        }))
                    .selectionKey(DemoOrder::getId)
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    .withDetailSync((DemoOrder o) ->
                            selectionLabel.setText("Order #" + o.getId() + " — " + o.getCustomer()))
                    .header(h -> h.heading("Order Details"))
                    .content(selectionLabel, signalLabel, tabs.buildHorizontal())
                    .styleName("card", CSSUtility.Common.FLEX_GROW_1))
                .build();

        // selectionSignal() — reactive binding; no imperative handler needed for this label
        Signal<DemoOrder> signal = layout.selectionSignal();
        Signal.effect(signalLabel, () -> {
            DemoOrder o = signal.get();
            signalLabel.setText(o != null
                    ? "Signal: #" + o.getId() + " — $" + String.format("%.2f", o.getAmount())
                    : "Signal: (nothing selected)");
        });

        // Runtime operation buttons — call these post-build from @OnShow, button handlers, or after save
        var selectFirstBtn = new Button("selectFirst(DESKTOP)", e -> layout.selectFirst(ViewMode.DESKTOP));
        selectFirstBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        var clearBtn = new Button("clearSelection()", e -> layout.clearSelection());
        clearBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        var notifyBtn = new Button("notifyDataChanged()", e -> layout.notifyDataChanged());
        notifyBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);

        var opsRow = new Div(new Span("Runtime ops:"), selectFirstBtn, clearBtn, notifyBtn);
        opsRow.addClassNames(CSSUtility.Common.D_FLEX, CSSUtility.Common.GAP_2,
                CSSUtility.Common.ALIGN_ITEMS_CENTER, CSSUtility.Common.MB_2);

        var container = new Div(opsRow, layout);
        container.addClassName(CSSUtility.Common.D_FLEX);
        container.addClassName(CSSUtility.Common.FLEX_COLUMN);

        return new DemoExample(
                "Live sync — typed builder, listing(), LazyTabs, selectionSignal(), runtime ops", container, """
                // 1. Consumer-based builder — listing(), selectionKey(), header/footer via lambdas
                 MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                    .master(m -> m
                        .listing(l -> l                          // uses DemoOrder.class from create()
                            .columns("id", "customer", "status")
                            .search("Search orders…")
                            .fetch((q, text, sort) -> service.fetch(q, text)))
                        .selectionKey(DemoOrder::getId)          // stable row identity
                        .card().styleName(FLEX_GROW_1))
                    .detail(d -> d
                        .withDetailSync((DemoOrder o) ->         // typed lambda for clarity
                                label.setText(o.getCustomer()))
                        .content(label, signalLabel, tabs.buildHorizontal())
                        // DetailSyncAware eager/lazy tabs auto-discovered by scanAndRegister()
                        .styleName("card", FLEX_GROW_1))
                    .build();

                // 2. selectionSignal() — reactive binding; updates on every selection change
                @SuppressWarnings("unchecked")
                Signal<DemoOrder> signal = (Signal<DemoOrder>) layout.selectionSignal();
                Signal.effect(signalLabel, () -> {
                    DemoOrder o = signal.get();  // null = nothing selected
                    signalLabel.setText(o != null ? "$ " + o.getAmount() : "(none)");
                });

                // 3. Runtime operations — call post-build, e.g. from @OnShow or button handlers
                layout.selectFirst(ViewMode.DESKTOP); // highlight first row (desktop only)
                layout.clearSelection();              // un-highlight + reset signal → null
                layout.notifyDataChanged();           // re-fire sync with current item (e.g. after save)
                """);
    }

    // ── DetailSyncAware tab panels ────────────────────────────────────────────

    /**
     * Eager tab content — always in the DOM when the {@code detail()} consumer runs.
     * Auto-discovered by {@code scanAndRegister()} and registered as a sync handler
     * with zero boilerplate.
     */
    private static class OrderSummaryTab extends Div implements DetailSyncAware<DemoOrder> {
        private final Span customer = new Span();
        private final Span product  = new Span();
        private final Span amount   = new Span();
        private final Span status   = new Span();

        OrderSummaryTab() {
            add(new Paragraph("Customer: "), customer,
                new Paragraph("Product: "),  product,
                new Paragraph("Amount: "),   amount,
                new Paragraph("Status: "),   status);
        }

        @Override
        public void onItemSelected(DemoOrder order) {
            customer.setText(order.getCustomer());
            product.setText(order.getProduct());
            amount.setText(String.format("$%.2f", order.getAmount()));
            status.setText(order.getStatus());
        }
    }

    /**
     * Lazy tab content — built on first click via {@code OrderNotesTab::new}.
     * The {@code SyncableContentContainer} relays the buffered item immediately
     * after construction, so the panel is never shown in an empty/stale state.
     */
    private static class OrderNotesTab extends Div implements DetailSyncAware<DemoOrder> {
        private final Span noteText = new Span();

        OrderNotesTab() {
            add(new Paragraph("Internal notes:"), noteText);
        }

        @Override
        public void onItemSelected(DemoOrder order) {
            noteText.setText("No notes recorded for order #" + order.getId() + ".");
        }
    }

    private DemoExample fluentNodeExample() {
        Button addBtn = new Button("New", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        Button filterBtn = new Button("Filter", VaadinIcon.FILTER.create());
        filterBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        Button saveBtn = new Button("Save");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelBtn = new Button("Cancel");

        MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .master(m -> m
                    .header(h -> h
                        .heading("Products")
                        .actions(addBtn, filterBtn))
                    .content(
                        new Paragraph("Wireless Mouse"),
                        new Paragraph("Mechanical Keyboard"),
                        new Paragraph("4K Monitor"),
                        new Paragraph("USB-C Hub")
                    )
                    .footer(f -> f
                        .details(new Span("4 products")))
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    .header(h -> h
                        .heading("Wireless Mouse")
                        .details(new Span("Category: Accessories"))
                        .actions(saveBtn, cancelBtn))
                    .content(
                        new Paragraph("Price: $49.99"),
                        new Paragraph("Category: Accessories"),
                        new Paragraph("Status: Active")
                    )
                    .footer(f -> f
                        .actions(new Button("View History"), new Button("Export"))
                        .withoutBorder())
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .build();

        return new DemoExample(
                "master() / detail() — header, footer.details, footer.actions, card", layout, """
                 MasterDetailBuilder.create(DemoOrder.class)
                    .styleName(D_FLEX).styleName(FLEX_ROW).styleName(GAP_3)
                    .master(m -> m
                        .header(h -> h
                            .heading("Customers")
                            .actions(newBtn, filterBtn))    // header right slot
                        .content(customerRows)
                        .footer(f -> f
                            .details(new Span("4 customers")))  // footer middle/info slot
                        .card().styleName(FLEX_GROW_1))
                    .detail(d -> d
                        .header(h -> h
                            .heading("Alice Johnson")
                            .details(new Span("since 2021"))
                            .actions(saveBtn, cancelBtn))
                        .content(detailFields)
                        .footer(f -> f
                            .actions(new Button("History"), new Button("Export"))
                            .withoutBorder())
                        .styleName(FLEX_GROW_1))
                    .build();
                // footer() slots: prefix(left)  details(middle)  actions(right)  meta  legal
                // header() slots: prefix(left)  heading(title)  details(sub-title)  actions(right)
                """);
    }

    // ── Example 5: multiple withDetailSync ───────────────────────────────────

    /**
     * Each {@code withDetailSync} call registers an independent handler — all handlers fire in
     * registration order on every row selection. Use this when separate UI fragments (labels,
     * badges, progress indicators) need to react to the same selection without coupling.
     */
    private DemoExample multipleHandlersExample() {
        var nameSpan     = new Span("—");
        var categorySpan = new Span("—");
        var priceSpan    = new Span("—");
        var activeSpan   = new Span("—");

        MasterDetailLayout<Product> layout = MasterDetailBuilder.create(Product.class)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "name", "category", "price", "active")
                        .fetch((q, text, sort) -> productService.fetch(q.getOffset(), q.getLimit(), text)))
                    .selectionKey(Product::getId)
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    // Each call registers an independent handler — all fire on every row click
                    .withDetailSync((Product p) -> nameSpan.setText(p.getName()))
                    .withDetailSync((Product p) -> categorySpan.setText(p.getCategory()))
                    .withDetailSync((Product p) -> priceSpan.setText(String.format("$%.2f", p.getPrice())))
                    .withDetailSync((Product p) -> activeSpan.setText(p.isActive() ? "Active" : "Inactive"))
                    .header(h -> h.heading("Product Detail"))
                    .content(
                        labelRow("Name",     nameSpan),
                        labelRow("Category", categorySpan),
                        labelRow("Price",    priceSpan),
                        labelRow("Status",   activeSpan)
                    )
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .build();

        return new DemoExample(
                "withDetailSync — multiple independent handlers, no DetailSyncAware needed", layout, """
                // All withDetailSync calls are registered and fire in order on every row click.
                // Use when separate UI fragments need to react independently — no shared state.
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "name", "category", "price", "active")
                        .fetch((q, text, sort) -> service.fetch(q.getOffset(), q.getLimit(), text)))
                    .selectionKey(Product::getId).card())
                .detail(d -> d
                    .withDetailSync((Product p) -> nameSpan.setText(p.getName()))
                    .withDetailSync((Product p) -> categorySpan.setText(p.getCategory()))
                    .withDetailSync((Product p) -> priceSpan.setText("$%.2f".formatted(p.getPrice())))
                    .withDetailSync((Product p) -> activeSpan.setText(p.isActive() ? "Active" : "Inactive"))
                    .content(nameRow, categoryRow, priceRow, statusRow))
                // vs DetailSyncAware — implement the interface on a component to auto-register it;
                // both approaches can be combined in the same detail panel.
                """);
    }

    // ── Example 6: configure(existingLayout) + withUrlSync ───────────────────

    /**
     * Demonstrates two advanced patterns:
     * <ol>
     *   <li>{@code MasterDetailConfigurator.configure(existingLayout, beanType)} — configure a
     *       {@link MasterDetailLayout} instance created elsewhere (e.g. injected, pre-styled,
     *       or provided by a framework).</li>
     *   <li>{@code withUrlSync} — wire URL {@code ?id=} deep-link support at build time.
     *       The code snippet shows the full pattern including {@code restoreFromUrl},
     *       {@code pushUrlState}, {@code clearUrlState}, and {@code notifyDataChanged}.</li>
     * </ol>
     */
    private DemoExample configureExistingLayoutExample() {
        // Live demo: configure(existingLayout, beanType)
        var existingLayout = new MasterDetailLayout<DemoOrder>();
        existingLayout.addClassNames(CSSUtility.Common.D_FLEX, CSSUtility.Common.FLEX_ROW,
                CSSUtility.Common.GAP_3);

        var detailLabel = new Span("← Select an order");

        MasterDetailConfigurator.configure(existingLayout, DemoOrder.class)
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "customer", "status")
                        .fetch((q, text, sort) -> DEMO_ORDERS.stream()
                                .skip(q.getOffset()).limit(q.getLimit())))
                    .selectionKey(DemoOrder::getId)
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    .withDetailSync((DemoOrder o) -> detailLabel.setText(
                            "Order #" + o.getId() + ": " + o.getCustomer() + " — " + o.getProduct()))
                    .header(h -> h.heading("Selected Order"))
                    .content(detailLabel)
                    .styleName(CSSUtility.Common.FLEX_GROW_1));

        return new DemoExample(
                "configure(existingLayout) — reconfigure pattern; withUrlSync deep-link (code snippet)",
                existingLayout, """
                // ── configure(existingLayout, beanType) ─────────────────────────────────
                // Use when the MasterDetailLayout is injected, pre-styled, or created upstream.
                var layout = new MasterDetailLayout();
                layout.addClassNames(D_FLEX, FLEX_ROW, GAP_3);

                MasterDetailConfigurator.configure(layout, DemoOrder.class)
                    .master(m -> m
                        .listing(l -> l.columns("id", "customer").fetch((q, text, sort) -> ...))
                        .selectionKey(DemoOrder::getId).card())
                    .detail(d -> d
                        .withDetailSync((DemoOrder o) -> label.setText(o.getCustomer()))
                        .content(label));

                // ── withUrlSync — URL ?id= deep-link ────────────────────────────────────
                // Wire at build time; no-op until restoreFromUrl() or pushUrlState() is called.
                MasterDetailLayout<DemoOrder> mdl = MasterDetailBuilder.create(DemoOrder.class)
                    .withUrlSync(
                        o -> String.valueOf(o.getId()),
                        id -> orderService.findById(Long.parseLong(id))
                    )
                    .master(m -> m.listing(l -> l.fetch((q, text, sort) -> ...)).selectionKey(Order::getId))
                    .detail(d -> d.withDetailSync((DemoOrder o) -> populate(o)))
                    .build();

                // In the routed view with @QueryParameter:
                @QueryParameter("id") String id;

                @OnShow void onShow() {
                    if (id != null) mdl.restoreFromUrl(id);  // highlight + sync from URL ?id=1001
                    else            mdl.selectFirst(viewMode);
                }

                // Optional: push URL on row click (listing auto-syncs; push is for manual updates)
                void onSaved(DemoOrder saved) {
                    mdl.notifyDataChanged();                     // re-fire sync with current item
                }

                void onDeleted() {
                    mdl.clearSelection();                        // un-highlight, signal → null
                    mdl.clearUrlState(getElement(), viewMode);   // removes ?id=
                }
                """);
    }

    // ── Example 7: Mobile + Sheet (lazyDetail) ───────────────────────────────

    /**
     * {@code lazyDetail(setup)} — the detail panel is constructed <em>only on the first row
     * tap</em>. No component allocations happen at build time. On subsequent taps the
     * already-built panel is updated in-place via {@code DetailSyncAware} /
     * {@code withDetailSync} and the Sheet re-opens.
     *
     * <p>This is the recommended mobile pattern when the detail panel contains heavy
     * components (tabs, grids, charts) that would be wasteful to build speculatively.</p>
     */
    private DemoExample mobileSheetExample() {
        // Pre-configure the Sheet: title + side only — content is set lazily on first tap
        var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                .title("Order Detail")
                .description("Selected order information.")
                .build();

        // lazyDetail() — the consumer body runs ONLY on the first row tap.
        // new OrderSummaryTab() is NOT called at build time.
        MasterDetailLayout<DemoOrder> mdl = MasterDetailBuilder.create(DemoOrder.class)
                .viewMode(ViewMode.MOBILE)
                .withMobileSheet(sheet)
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "customer", "status")
                        .fetch((q, text, sort) -> DEMO_ORDERS.stream()
                                .skip(q.getOffset()).limit(q.getLimit())))
                    .selectionKey(DemoOrder::getId)
                    .card())
                .lazyDetail(d -> d
                    .header(h -> h.heading("Order"))
                    .content(new OrderSummaryTab())   // ← new OrderSummaryTab() runs on 1st tap
                    .withDetailSync((DemoOrder o) -> { /* extra updates on every click */ }))
                .build();

        var container = new Div(mdl);

        return new DemoExample(
                "Mobile + lazyDetail — detail panel built only on first tap, then cached",
                container, """
                // 1. Configure the Sheet (title, side) — no .content() yet
                var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                    .title("Order Detail")
                    .description("Selected order information.")
                    .build();

                // 2. Use lazyDetail() — consumer body runs ONLY on first tap
                MasterDetailLayout mdl = MasterDetailBuilder.create(Order.class)
                    .viewMode(ViewMode.MOBILE)
                    .withMobileSheet(sheet)
                    .master(m -> m
                        .listing(l -> l.columns(...).fetch((q, text, sort) -> ...))
                        .selectionKey(DemoOrder::getId).card())
                    .lazyDetail(d -> d              // nothing constructed here
                        .header(h -> h.heading("Order"))
                        .content(new OrderSummaryTab()) // ← new OrderSummaryTab() runs on 1st tap
                        .withDetailSync((DemoOrder o) -> { /* extra updates on every click */ }))
                    .build();

                // On FIRST row tap:
                //   setup consumer executes → components constructed → Sheet receives content
                //   detail sync handlers fire → content populated
                //   sheet.open()

                // On subsequent taps:
                //   same instances updated via withDetailSync / DetailSyncAware
                //   sheet.open()

                // Without viewMode(MOBILE) + withMobileSheet, lazyDetail() behaves
                // identically to detail(...) — components built eagerly at build time.

                // Convenience: skip Sheet pre-config entirely
                MasterDetailBuilder.create(Order.class)
                    .viewMode(ViewMode.MOBILE)
                    .withMobileSheet(Sheet.Side.BOTTOM)   // auto-creates minimal Sheet
                    .master(m -> m...).detail(d -> d...)
                    .build();
                """);
    }

    // ── Example 2.5: PropertySet listing ──────────────────────────────────────

    /**
     * Demonstrates the {@code PropertySet} path:
     * <ul>
     *   <li>{@code Components.masterDetail(PropertySet)} — no bean class required;
     *       item type is {@link PropertyBox}</li>
     *   <li>{@code listing().fetch(FilteredFetchCallback)} — receives the committed
     *       {@link com.holonplatform.core.query.QueryFilter} when filter panel is active</li>
     *   <li>{@code withFilterPanel()} — activates the dynamic filter panel;
     *       the {@code advancedMode} boolean overload is accepted but the flag is
     *       not propagated (documented limitation of {@code PropertyListingBundleBuilder})</li>
     *   <li>{@code withDetailSync((PropertyBox pb) -> ...)} — typed sync handler;
     *       fields extracted via property path-projection</li>
     * </ul>
     */
    private DemoExample propertySetListingExample() {
        PathProperty<Long>   ORDER_ID       = PathProperty.create("id",       Long.class);
        PathProperty<String> ORDER_CUSTOMER = PathProperty.create("customer", String.class);
        PathProperty<Double> ORDER_AMOUNT   = PathProperty.create("amount",   Double.class);
        PathProperty<String> ORDER_STATUS   = PathProperty.create("status",   String.class);
        PropertySet<?>       ORDER_SET      = PropertySet.of(ORDER_ID, ORDER_CUSTOMER,
                                                              ORDER_AMOUNT, ORDER_STATUS);

        // Convert DEMO_ORDERS to PropertyBox items
        List<PropertyBox> items = DEMO_ORDERS.stream()
                .map(o -> PropertyBox.builder(ORDER_SET)
                        .set(ORDER_ID,       o.getId())
                        .set(ORDER_CUSTOMER, o.getCustomer())
                        .set(ORDER_AMOUNT,   o.getAmount())
                        .set(ORDER_STATUS,   o.getStatus())
                        .build())
                .toList();

        var detailName   = new Span("—");
        var detailAmount = new Span("—");
        var detailStatus = new Span("—");

        MasterDetailLayout<PropertyBox> layout = Components.masterDetail(ORDER_SET)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .master(m -> m
                    .listing(l -> l
                        .header("customer", "Customer")
                        .header("amount",   "Amount")
                        .header("status",   "Status")
                        .search("Search orders…")
                        .withFilterPanel()             // advancedMode no-op documented in adapter
                        .fetch((q, text, filter, sort) -> {
                            var stream = items.stream();
                            if (text != null && !text.isBlank()) {
                                var lc = text.toLowerCase();
                                stream = stream.filter(pb ->
                                        pb.getValue(ORDER_CUSTOMER).toLowerCase().contains(lc));
                            }
                            return stream.skip(q.getOffset()).limit(q.getLimit());
                        }))
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    .withDetailSync((PropertyBox pb) -> {
                        detailName.setText(pb.getValue(ORDER_CUSTOMER));
                        detailAmount.setText(String.format("$%.2f",
                                pb.getValue(ORDER_AMOUNT)));
                        detailStatus.setText(pb.getValue(ORDER_STATUS));
                    })
                    .header(h -> h.heading("Order Detail"))
                    .content(
                            labelRow("Customer", detailName),
                            labelRow("Amount",   detailAmount),
                            labelRow("Status",   detailStatus))
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .build();

        return new DemoExample(
                "PropertySet listing — no bean class; filter panel; withDetailSync on PropertyBox",
                layout, """
                // No bean class needed — use a PropertySet instead
                PathProperty<Long>   ID       = PathProperty.create("id",       Long.class);
                PathProperty<String> CUSTOMER = PathProperty.create("customer", String.class);
                PathProperty<Double> AMOUNT   = PathProperty.create("amount",   Double.class);
                PropertySet<?>       ORDER_SET = PropertySet.of(ID, CUSTOMER, AMOUNT);

                // Components.masterDetail(PropertySet) returns MasterDetailBuilder<PropertyBox>
                Components.masterDetail(ORDER_SET)
                    .master(m -> m
                        .listing(l -> l
                            .header("customer", "Customer")   // column header by path name
                            .search("Search…")
                            .withFilterPanel()                 // activates DynamicFilterPanel
                            // advancedMode overload accepted but ignored (PropertyListingBundleBuilder limitation):
                            // .withFilterPanel(true)
                            .fetch((q, text, filter, sort) -> {
                                // filter = QueryFilter from DynamicFilterPanel (may be null)
                                var stream = datastore.query(TARGET);
                                if (filter != null) stream.filter(filter);
                                return stream.stream(ORDER_SET);
                            }))
                        .card().styleName(FLEX_GROW_1))
                    .detail(d -> d
                        .withDetailSync((PropertyBox pb) -> {
                            // access values by PathProperty — type-safe
                            label.setText(pb.getValue(CUSTOMER));
                        })
                        .header(h -> h.heading("Detail")))
                    .build();
                """);
    }

    // ── Example 8: withAccentColorProvider — all 7 variants ──────────────────

    /**
     * Demonstrates {@code withAccentColorProvider(Function)} with every
     * {@link MasterDetailAccent} variant. Each of the 7 rows maps a unique order
     * status to a distinct accent class so clicking each row shows a different
     * left-bar colour and background tint.
     *
     * <ul>
     *   <li>DELIVERED  → {@link MasterDetailAccent#SUCCESS} (green)</li>
     *   <li>SHIPPED    → {@link MasterDetailAccent#INFO} (cyan)</li>
     *   <li>PROCESSING → {@link MasterDetailAccent#WARNING} (amber)</li>
     *   <li>PENDING    → {@link MasterDetailAccent#PURPLE}</li>
     *   <li>CANCELLED  → {@link MasterDetailAccent#DANGER} (red)</li>
     *   <li>ON_HOLD    → {@link MasterDetailAccent#GRAY}</li>
     *   <li>NEW        → {@link MasterDetailAccent#DEFAULT} (blue)</li>
     * </ul>
     */
    private DemoExample accentColorsExample() {
        var accentOrders = List.of(
            new DemoOrder(2001, "Alice Martin",  "Laptop Pro",     1299.00, "DELIVERED"),
            new DemoOrder(2002, "Bob Chen",      "Wireless Mouse",   49.99, "SHIPPED"),
            new DemoOrder(2003, "Carol White",   "4K Monitor",      799.00, "PROCESSING"),
            new DemoOrder(2004, "Dave Brown",    "Mechanical KB",   159.99, "PENDING"),
            new DemoOrder(2005, "Eva Schmidt",   "USB-C Hub",        89.99, "CANCELLED"),
            new DemoOrder(2006, "Frank Lee",     "Desk Lamp",        45.00, "ON_HOLD"),
            new DemoOrder(2007, "Grace Kim",     "Webcam HD",        79.99, "NEW")
        );

        var hintLabel   = new Span("← Click a row to see its accent colour");
        var statusLabel = new Span();
        var accentDesc  = new Span();

        MasterDetailLayout<DemoOrder> layout = MasterDetailBuilder.create(DemoOrder.class)
                .styleName(CSSUtility.Common.D_FLEX)
                .styleName(CSSUtility.Common.FLEX_ROW)
                .styleName(CSSUtility.Common.GAP_3)
                .withAccentColorProvider(order -> switch (order.getStatus()) {
                    case "DELIVERED"  -> MasterDetailAccent.SUCCESS.cssClass();
                    case "SHIPPED"    -> MasterDetailAccent.INFO.cssClass();
                    case "PROCESSING" -> MasterDetailAccent.WARNING.cssClass();
                    case "PENDING"    -> MasterDetailAccent.PURPLE.cssClass();
                    case "CANCELLED"  -> MasterDetailAccent.DANGER.cssClass();
                    case "ON_HOLD"    -> MasterDetailAccent.GRAY.cssClass();
                    default           -> MasterDetailAccent.DEFAULT.cssClass();
                })
                .master(m -> m
                    .listing(l -> l
                        .columns("id", "customer", "status")
                        .fetch((q, text, sort) -> {
                            var stream = accentOrders.stream();
                            if (text != null && !text.isBlank()) {
                                var lc = text.toLowerCase();
                                stream = stream.filter(o ->
                                        o.getCustomer().toLowerCase().contains(lc) ||
                                        o.getStatus().toLowerCase().contains(lc));
                            }
                            return stream.skip(q.getOffset()).limit(q.getLimit());
                        }))
                    .selectionKey(DemoOrder::getId)
                    .card()
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .detail(d -> d
                    .withDetailSync((DemoOrder o) -> {
                        statusLabel.setText("Status: " + o.getStatus());
                        accentDesc.setText("Accent variant: " + accentName(o.getStatus()));
                        hintLabel.setVisible(false);
                    })
                    .header(h -> h.heading("Order Detail"))
                    .content(hintLabel, statusLabel, accentDesc)
                    .styleName(CSSUtility.Common.FLEX_GROW_1))
                .build();

        return new DemoExample(
                "withAccentColorProvider — all 7 MasterDetailAccent variants", layout, """
                // Map each item's status to a MasterDetailAccent variant via switch expression.
                // Java assigns only the CSS class name — all colour values live in master-detail-v2.css.
                MasterDetailBuilder.create(DemoOrder.class)
                    .withAccentColorProvider(order -> switch (order.getStatus()) {
                        case "DELIVERED"  -> MasterDetailAccent.SUCCESS.cssClass(); // green
                        case "SHIPPED"    -> MasterDetailAccent.INFO.cssClass();    // cyan
                        case "PROCESSING" -> MasterDetailAccent.WARNING.cssClass(); // amber
                        case "PENDING"    -> MasterDetailAccent.PURPLE.cssClass();  // purple
                        case "CANCELLED"  -> MasterDetailAccent.DANGER.cssClass();  // red
                        case "ON_HOLD"    -> MasterDetailAccent.GRAY.cssClass();    // gray
                        default           -> MasterDetailAccent.DEFAULT.cssClass(); // blue
                    })
                    .master(m -> m
                        .listing(l -> l.columns("id", "customer", "status").fetch(...))
                        .selectionKey(DemoOrder::getId).card())
                    .detail(d -> d
                        .withDetailSync((DemoOrder o) -> label.setText(o.getStatus()))
                        .header(h -> h.heading("Order Detail"))
                        .content(statusLabel))
                    .build();

                // Custom colour — no enum needed; define CSS and return the class name directly:
                // CSS:  .my-teal { --mdl-selected-accent: #0d9488; --mdl-selected-bg: rgba(13,148,136,.08); }
                // Java: .withAccentColorProvider(item -> item.isVip() ? "my-teal" : MasterDetailAccent.DEFAULT.cssClass())

                // Available variants (all in MasterDetailAccent enum):
                //   DEFAULT (blue)  SUCCESS (green)  WARNING (amber)  DANGER (red)
                //   INFO (cyan)     PURPLE            GRAY
                """);
    }

    private static String accentName(String status) {
        return switch (status) {
            case "DELIVERED"  -> "SUCCESS (green)";
            case "SHIPPED"    -> "INFO (cyan)";
            case "PROCESSING" -> "WARNING (amber)";
            case "PENDING"    -> "PURPLE";
            case "CANCELLED"  -> "DANGER (red)";
            case "ON_HOLD"    -> "GRAY";
            default           -> "DEFAULT (blue)";
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static Div labelRow(String label, Span value) {
        var row = new Div(new Span(label + ": "), value);
        row.addClassNames(CSSUtility.Common.D_FLEX, CSSUtility.Common.GAP_2);
        return row;
    }
}

