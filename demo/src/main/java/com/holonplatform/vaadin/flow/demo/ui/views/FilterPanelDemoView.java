package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Demo page for the {@link DynamicFilterPanel} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Standalone panel with one pre-added row (matches the reference design)</li>
 *   <li>In-memory filtering wired to a bean list</li>
 *   <li>OR-mode (match any row)</li>
 * </ol>
 */
@PageTitle("FilterPanel – Holon Demo")
@Route(value = "filter-panel", layout = DemoMainLayout.class)
public class FilterPanelDemoView extends Div {

    // ── Demo bean ─────────────────────────────────────────────────────────────

    public enum OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

    public static final class Order {
        private long   id;
        private String customer;
        private String product;
        private double amount;
        private OrderStatus status;
        private LocalDate   orderDate;

        public Order() {}

        public Order(long id, String customer, String product,
                     double amount, OrderStatus status, LocalDate orderDate) {
            this.id        = id;
            this.customer  = customer;
            this.product   = product;
            this.amount    = amount;
            this.status    = status;
            this.orderDate = orderDate;
        }

        public long        getId()        { return id; }
        public String      getCustomer()  { return customer; }
        public String      getProduct()   { return product; }
        public double      getAmount()    { return amount; }
        public OrderStatus getStatus()    { return status; }
        public LocalDate   getOrderDate() { return orderDate; }

        public void setId(long id)               { this.id = id; }
        public void setCustomer(String c)         { this.customer = c; }
        public void setProduct(String p)          { this.product = p; }
        public void setAmount(double a)            { this.amount = a; }
        public void setStatus(OrderStatus s)       { this.status = s; }
        public void setOrderDate(LocalDate d)      { this.orderDate = d; }
    }

    private static final List<Order> ORDERS = List.of(
        new Order(1001, "Alice Martin",   "Laptop Pro",      1299.00, OrderStatus.DELIVERED,  LocalDate.of(2025, 1,  5)),
        new Order(1002, "Bob Chen",       "Wireless Mouse",    49.99, OrderStatus.SHIPPED,     LocalDate.of(2025, 1, 12)),
        new Order(1003, "Carol White",    "4K Monitor",       799.00, OrderStatus.PROCESSING,  LocalDate.of(2025, 2,  3)),
        new Order(1004, "Dave Brown",     "Mechanical KB",    159.99, OrderStatus.PENDING,     LocalDate.of(2025, 2, 18)),
        new Order(1005, "Eva Schmidt",    "USB-C Hub",         89.99, OrderStatus.CANCELLED,   LocalDate.of(2025, 3,  1)),
        new Order(1006, "Frank Lee",      "Desk Lamp",         45.00, OrderStatus.DELIVERED,   LocalDate.of(2025, 3, 10)),
        new Order(1007, "Grace Kim",      "BT Headphones",    199.99, OrderStatus.SHIPPED,     LocalDate.of(2025, 3, 22)),
        new Order(1008, "Henry Davis",    "Webcam 1080p",     129.99, OrderStatus.DELIVERED,   LocalDate.of(2025, 4,  5)),
        new Order(1009, "Iris Johnson",   "Standing Desk",    549.00, OrderStatus.PROCESSING,  LocalDate.of(2025, 4, 14)),
        new Order(1010, "James Wilson",   "Ergonomic Chair",  449.99, OrderStatus.PENDING,     LocalDate.of(2025, 4, 20))
    );

    // ── Constructor ───────────────────────────────────────────────────────────

    public FilterPanelDemoView() {
        addClassName("app-view");

        var title = new H1("FilterPanel");

        var desc = new Paragraph(
                "DynamicFilterPanel is a row-based filter builder: each row exposes a property " +
                "selector, a type-aware operator selector (String → Contains/Starts With/…, " +
                "Number → >/</Between, Date → Before/After/Between, …), and a value input that " +
                "adapts to the selected property type. Clicking Apply filter commits all rows into " +
                "a single QueryFilter (AND by default) and fires a FilterChangeListener that can " +
                "drive either a Holon Datastore query or an in-memory Predicate.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(standaloneExample());
        examples.add(inMemoryFilteringExample());
        examples.add(orModeExample());
        examples.add(dialogFilterExample());
        examples.add(mobileSheetFilterExample());

        add(title, desc, examples);
    }

    // ── Example builders ──────────────────────────────────────────────────────

    /**
     * 1. Standalone panel with one row pre-added — matches the reference design image.
     */
    private DemoExample standaloneExample() {
        var panel = DynamicFilterPanel.of(Order.class);
        // Pre-content one row so the panel looks like the reference design from the start.
        panel.addRow();

        return new DemoExample("Standalone Panel (one pre-added row)", panel, """
                // Create the panel — bean properties are introspected automatically.
                DynamicFilterPanel<Order> panel = DynamicFilterPanel.of(Order.class);

                // Optionally pre-add a row so the panel is ready to use immediately.
                panel.addRow();

                // Embed the panel in your view — it is a Vaadin component (extends Div).
                add(panel);

                // Listen to Apply-filter clicks:
                panel.addFilterChangeListener(e -> {
                    Optional<QueryFilter> filter = panel.getQueryFilter();
                    // Use filter in a Datastore query, or:
                    stream.filter(panel.toPredicate())…
                });
                """);
    }

    /**
     * 2. In-memory filtering — DynamicFilterPanel wired to a list via toPredicate().
     */
    private DemoExample inMemoryFilteringExample() {
        var shown = new ArrayList<>(ORDERS);

        var listing = BeanListing.builder(Order.class, true)
                .visibleColumns(List.of("customer", "product", "amount", "status", "orderDate"))
                .header("customer",  "Customer")
                .header("product",   "Product")
                .header("amount",    "Amount (€)")
                .header("status",    "Status")
                .header("orderDate", "Order Date")
                .height("260px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        var panel = DynamicFilterPanel.of(Order.class);

        var countLabel = new Span(ORDERS.size() + " orders shown");

        panel.addFilterChangeListener(e -> {
            shown.clear();
            shown.addAll(ORDERS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            int n = shown.size();
            countLabel.setText(n == ORDERS.size()
                    ? n + " orders shown"
                    : n + " of " + ORDERS.size() + " orders shown");
        });

        var container = new Div(panel, countLabel, listing.getComponent());

        return new DemoExample("In-memory Filtering (toPredicate)", container, """
                DynamicFilterPanel<Order> panel = DynamicFilterPanel.of(Order.class);

                var shown = new ArrayList<>(allOrders);
                listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

                // Wire: re-filter on every Apply-filter click using the Java Predicate.
                panel.addFilterChangeListener(e -> {
                    shown.clear();
                    shown.addAll(allOrders.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });

                // In production — delegate to Datastore instead:
                listing.setItems(panel, (query, filter) -> {
                    var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
                    if (filter != null) q.filter(filter);
                    return q.stream(BeanProjection.of(Order.class));
                });
                listing.refreshOnFilterChange(panel);   // re-fetch on every Apply click
                """);
    }

    /**
     * 3. OR mode — any of the filter rows can match (not all must match).
     */
    private DemoExample orModeExample() {
        var shown = new ArrayList<>(ORDERS);

        var listing = BeanListing.builder(Order.class, true)
                .visibleColumns(List.of("customer", "product", "amount", "status", "orderDate"))
                .header("customer",  "Customer")
                .header("product",   "Product")
                .header("amount",    "Amount (€)")
                .header("status",    "Status")
                .header("orderDate", "Order Date")
                .height("260px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        var panel = DynamicFilterPanel.of(Order.class);
        panel.setMatchAll(false);   // OR: any row can satisfy the condition

        var modeLabel = new Span("Match mode: OR — at least one row must match");

        var countLabel = new Span(ORDERS.size() + " orders shown");

        panel.addFilterChangeListener(e -> {
            shown.clear();
            shown.addAll(ORDERS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            int n = shown.size();
            countLabel.setText(n == ORDERS.size()
                    ? n + " orders shown"
                    : n + " of " + ORDERS.size() + " orders shown");
        });

        var container = new Div(panel, modeLabel, countLabel, listing.getComponent());

        return new DemoExample("OR Mode (match any row)", container, """
                // Default mode is AND (all rows must match).
                // Switch to OR so the result includes items matching ANY row.
                DynamicFilterPanel<Order> panel = DynamicFilterPanel.of(Order.class);
                panel.setMatchAll(false);   // OR-combine all rows

                // Wiring is identical — toPredicate() honours the matchAll flag.
                panel.addFilterChangeListener(e -> {
                    shown.clear();
                    shown.addAll(allOrders.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });

                // Reset all rows and clear the applied filter:
                panel.resetAll();

                // Check whether at least one filter is applied:
                boolean anyActive = panel.isAnyActive();
                """);
    }
    /**
     * 4. Filter panel inside a Dialog with an active-filter badge on the trigger button.
     *
     * <p>Key points:
     * <ul>
     *   <li>Badge is hidden when no filters are active; shows the count otherwise.</li>
     *   <li>Dialog auto-closes when "Apply filter" is clicked (via {@code addApplyListener}).</li>
     *   <li>Removing a row re-applies immediately but keeps the dialog open so the user
     *       can keep editing.</li>
     * </ul>
     */
    private DemoExample dialogFilterExample() {
        var shown = new ArrayList<>(ORDERS);

        var listing = BeanListing.builder(Order.class, true)
                .visibleColumns(List.of("customer", "product", "amount", "status", "orderDate"))
                .header("customer",  "Customer")
                .header("product",   "Product")
                .header("amount",    "Amount (€)")
                .header("status",    "Status")
                .header("orderDate", "Order Date")
                .height("260px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        var panel = DynamicFilterPanel.of(Order.class);

        // ── Dialog ───────────────────────────────────────────────────────────
        var dialog = new Dialog();
        dialog.setHeaderTitle("Filter orders");
        dialog.setWidth("640px");
        dialog.add(panel);
        dialog.getFooter().add(new Button("Close", e -> dialog.close()));

        // Close dialog when the user explicitly clicks "Apply filter"
        // (row removal auto-applies but must NOT close the dialog).
        panel.addApplyListener(dialog::close);

        // ── Badge on trigger button ───────────────────────────────────────────
        // Badge floats as an overlay at the top-right corner of the button.
        var badge = new Badge("0", BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.PILL);
        badge.setVisible(false);

        var openBtn = new Button("Filters", VaadinIcon.FILTER.create(),
                e -> dialog.open());

        // Wrapper positions the badge over the button corner via CSS.
        var btnWrap = new Div(openBtn, badge);

        // ── Count label ───────────────────────────────────────────────────────
        var countLabel = new Span(ORDERS.size() + " orders shown");

        // ── Wire filter changes ────────────────────────────────────────────────
        panel.addFilterChangeListener(e -> {
            int count = panel.getActiveFilterCount();
            badge.setVisible(count > 0);
            badge.setText(String.valueOf(count));

            shown.clear();
            shown.addAll(ORDERS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            int n = shown.size();
            countLabel.setText(n == ORDERS.size()
                    ? n + " orders shown"
                    : n + " of " + ORDERS.size() + " orders shown");
        });

        var container = new Div(btnWrap, countLabel, listing.getComponent());

        return new DemoExample("Filter Panel in a Dialog (with active-filter badge)", container, """
                // ── Panel + Dialog ──────────────────────────────────────────────────
                var panel  = DynamicFilterPanel.of(Order.class);
                var dialog = new Dialog();
                dialog.setHeaderTitle("Filter orders");
                dialog.add(panel);
                dialog.getFooter().add(new Button("Close", e -> dialog.close()));

                // Close dialog only when "Apply filter" is clicked explicitly;
                // removing a row re-applies but keeps the dialog open for further edits.
                panel.addApplyListener(dialog::close);

                // ── Badge on the trigger button (overlaid at top-right corner) ───────
                var badge   = new Badge("0", BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.PILL);
                var openBtn = new Button("Filters", VaadinIcon.FILTER.create(), e -> dialog.open());

                badge.addClassName("demo-filter-active-badge"); // positions badge over button
                var btnWrap = new Div(openBtn, badge);

                // ── Update badge + listing on every filter change ─────────────────
                panel.addFilterChangeListener(e -> {
                    int count = panel.getActiveFilterCount();  // 0 when no filter applied
                    badge.setVisible(count > 0);
                    badge.setText(String.valueOf(count));

                    shown.clear();
                    shown.addAll(allOrders.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });
                """);
    }

    /**
     * 5. Responsive Sheet filter — the filter panel is shown inline on tablet/desktop
     * and inside a bottom {@link Sheet} on mobile.
     *
     * <p>Key mechanics:
     * <ul>
     *   <li><b>One panel instance — never duplicated.</b>
     *       Vaadin automatically removes a component from its current parent when it is added
     *       to a new one, so moving the panel between the inline wrapper and the Sheet is safe
     *       and preserves all row state across open/close cycles.</li>
     *   <li><b>CSS-only responsive switching via {@link ResponsiveDiv}.</b>
     *       {@code .hidden().show(ViewMode.TABLET)} on the inline wrapper and
     *       {@code .hide(ViewMode.TABLET)} on the trigger row means no server-side
     *       viewport detection is ever needed.</li>
     *   <li><b>Apply closes the Sheet.</b>
     *       {@link DynamicFilterPanel#addApplyListener} fires only on the explicit
     *       "Apply filter" click, so removing a row re-applies the filter immediately
     *       but keeps the Sheet open for further edits.</li>
     *   <li><b>Badge tracks active filter count</b> via
     *       {@link DynamicFilterPanel#getActiveFilterCount()}.</li>
     * </ul>
     */
    private DemoExample mobileSheetFilterExample() {
        var shown = new ArrayList<>(ORDERS);

        var listing = BeanListing.builder(Order.class, true)
                .visibleColumns(List.of("customer", "product", "amount", "status", "orderDate"))
                .header("customer",  "Customer")
                .header("product",   "Product")
                .header("amount",    "Amount (€)")
                .header("status",    "Status")
                .header("orderDate", "Order Date")
                .height("260px")
                .build();
        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        // ── Single panel instance ─────────────────────────────────────────────
        var panel = DynamicFilterPanel.of(Order.class);

        // ── Inline wrapper: visible from tablet+, hidden on mobile (CSS only) ─
        var inlineWrapper = ResponsiveDiv.flex()
                .column().noGap()
                .hidden()                      // mobile: hidden via .hidden CSS class
                .show(ViewMode.TABLET)          // tablet+: visible via md:block
                .add(panel)
                .build();

        // ── Sheet: slides up from the bottom, fullscreen on mobile ────────────
        var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                .title("Filter orders")
                .description("Select conditions and tap Apply filter.")
                .fullscreenOnMobile(true)
                .backButton(false)
                .onClose(() -> inlineWrapper.add(panel))  // move panel back inline on close
                .build();

        // Close sheet when the user explicitly clicks "Apply filter";
        // row removal re-applies but keeps the sheet open for further edits.
        panel.addApplyListener(sheet::close);

        // ── Badge + trigger button: visible on mobile only (CSS only) ─────────
        var badge = new Badge("0", BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.PILL);
        badge.setVisible(false);

        var triggerBtn = new Button("Filters", VaadinIcon.FILTER.create(), e -> {
            sheet.setContent(panel);  // moves panel from inlineWrapper into the sheet
            sheet.open();
        });

        var triggerRow = ResponsiveDiv.flex()
                .row().alignCenter().gapS()
                .hide(ViewMode.TABLET)           // tablet+: hidden (panel shown inline instead)
                .add(triggerBtn, badge)
                .build();

        // ── Count label + filter wiring ───────────────────────────────────────
        var countLabel = new Span(ORDERS.size() + " orders shown");

        panel.addFilterChangeListener(e -> {
            int count = panel.getActiveFilterCount();
            badge.setVisible(count > 0);
            badge.setText(String.valueOf(count));

            shown.clear();
            shown.addAll(ORDERS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
            int n = shown.size();
            countLabel.setText(n == ORDERS.size()
                    ? n + " orders shown"
                    : n + " of " + ORDERS.size() + " orders shown");
        });

        var container = ResponsiveDiv.flex().column().gapS()
                .add(triggerRow, inlineWrapper, countLabel, listing.getComponent())
                .build();

        return new DemoExample(
                "Mobile Sheet Filter — inline on tablet/desktop, Sheet on mobile (resize to see)",
                container, """
                        // ── One panel instance — no duplication ─────────────────────────────
                        var panel = DynamicFilterPanel.of(Order.class);

                        // Inline wrapper: hidden on mobile, visible from tablet+ via CSS only.
                        // No server-side viewport detection needed.
                        var inlineWrapper = ResponsiveDiv.flex().column().noGap()
                            .hidden()               // default: hidden (mobile)
                            .show(ViewMode.TABLET)  // md+: visible
                            .add(panel)
                            .build();

                        // Sheet — slides up from the bottom, fullscreen on mobile.
                        // onClose moves the panel back to the inline wrapper so state is preserved.
                        var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                            .title("Filter orders")
                            .fullscreenOnMobile(true)
                            .backButton(false)
                            .onClose(() -> inlineWrapper.add(panel))
                            .build();

                        // "Apply filter" closes the sheet; row removal keeps it open.
                        panel.addApplyListener(sheet::close);

                        // Trigger button + badge — visible on mobile only (CSS).
                        var badge      = new Badge("0", BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.PILL);
                        var triggerBtn = new Button("Filters", VaadinIcon.FILTER.create(), e -> {
                            sheet.setContent(panel);  // moves panel into the sheet
                            sheet.open();
                        });
                        var triggerRow = ResponsiveDiv.flex().row().alignCenter().gapS()
                            .hide(ViewMode.TABLET)   // md+: hidden
                            .add(triggerBtn, badge)
                            .build();

                        // Wire badge + listing refresh on every filter change.
                        panel.addFilterChangeListener(e -> {
                            int count = panel.getActiveFilterCount();
                            badge.setVisible(count > 0);
                            badge.setText(String.valueOf(count));
                            // ... refresh listing
                        });
                        """);
    }
}

