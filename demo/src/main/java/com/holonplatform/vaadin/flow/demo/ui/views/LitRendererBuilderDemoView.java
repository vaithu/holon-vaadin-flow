/*
 * Copyright 2016-2026 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.CellStyle;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.DocumentRowBuilder.StatusType;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.ChipVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.RowVariant;
import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder.MobileListItemBuilder.StatusVariant;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.StyleSheet;

import java.time.LocalDate;
import java.util.List;

/**
 * Demo page for {@link LitRendererBuilder}.
 *
 * <p>Shows how to compose Grid cell renderers using the fluent DSL — no raw
 * HTML strings required. Demonstrates:
 *
 * <ol>
 *   <li>3-row document row (PO / company / status+meta) via {@code div → span} nesting</li>
 *   <li>Dynamic status CSS class computed server-side via {@code withProperty}</li>
 *   <li>Avatar + name horizontal layout via {@code horizontalLayout}</li>
 *   <li>Action button via {@code vaadinButton} with server-side {@code withFunction}</li>
 * </ol>
 */
@PageTitle("LitRendererBuilder – Holon Demo")
@Route(value = "lit-renderer-builder", layout = DemoMainLayout.class)
@StyleSheet("context://grid-cell.css")
@StyleSheet("context://document-row-lit-renderer.css")
@StyleSheet("context://mobile-list-lit-renderer.css")
public class LitRendererBuilderDemoView extends Div {

    // ── Demo model ───────────────────────────────────────────────────────────

    public enum OrderStatus { PENDING, ACTIVE, CLOSED, ERROR }

    public record Order(
            String poNumber,
            double amount,
            String currency,
            String company,
            OrderStatus status,
            String meta
    ) {
        /** Pre-formatted amount string (e.g. "€18,720.00"). */
        String formattedAmount() {
            return currency + String.format(java.util.Locale.US, "%,.2f", amount);
        }

        /** Human-readable status label. */
        String statusLabel() {
            return switch (status) {
                case PENDING -> "1 exception";
                case ACTIVE  -> "Ready";
                case CLOSED  -> "Matched";
                case ERROR   -> "Exception";
            };
        }

        /**
         * Maps domain enum → {@link StatusType} — no CSS strings involved.
         * Enum values align with {@code document-row-lit-renderer.css} status classes.
         */
        StatusType statusType() {
            return switch (status) {
                case PENDING -> StatusType.PENDING;
                case ACTIVE  -> StatusType.READY;
                case CLOSED  -> StatusType.MATCHED;
                case ERROR   -> StatusType.EXCEPTION;
            };
        }
    }

    private static final List<Order> ORDERS = List.of(
            new Order("PO-2026-0483", 18720.00, "€", "Lumen Health Inc.",      OrderStatus.PENDING, "⏱  2 days"),
            new Order("PO-2026-0484", 4350.50,  "€", "NovaBridge Solutions",  OrderStatus.ACTIVE,  "⚡ 5 hrs"),
            new Order("PO-2026-0485", 99500.00, "€", "Cortex Analytics Ltd.",  OrderStatus.CLOSED,  "✓ Done"),
            new Order("PO-2026-0486", 2100.00,  "€", "BlueSky Retail GmbH",   OrderStatus.ERROR,   "✗ Retry")
    );

    // ── Bill demo model ──────────────────────────────────────────────────────

    public enum BillStatus { AWAITING, APPROVED, PAID, DUE, OVERDUE, DRAFT, VOID }

    /**
     * @param chip1Label  first chip label — null to omit chip
     * @param chip2Label  second chip label — null to omit second chip
     * @param isSelected  true → selected/open row (mli.on)
     * @param isException true → exception row (mli.exception) — amount+when turn red via CSS
     */
    public record Bill(
            String billNumber, double amount, String vendorName, String whenLabel,
            boolean isException, boolean isSelected,
            String chip1Label, ChipVariant chip1Variant,
            String chip2Label, ChipVariant chip2Variant,
            String metaRef, BillStatus status
    ) {
        String formattedAmount() {
            return "€" + String.format(java.util.Locale.US, "%,.2f", amount);
        }

        String statusLabel() {
            return switch (status) {
                case AWAITING -> "Awaiting";
                case APPROVED -> "Approved";
                case PAID     -> "Paid";
                case DUE      -> "Due soon";
                case OVERDUE  -> "Overdue";
                case DRAFT    -> "Draft";
                case VOID     -> "Void";
            };
        }

        StatusVariant statusVariant() {
            return switch (status) {
                case AWAITING -> StatusVariant.AWAITING;
                case APPROVED -> StatusVariant.APPROVED;
                case PAID     -> StatusVariant.PAID;
                case DUE      -> StatusVariant.DUE;
                case OVERDUE  -> StatusVariant.OVERDUE;
                case DRAFT    -> StatusVariant.DRAFT;
                case VOID     -> StatusVariant.VOID;
            };
        }

        /**
         * Amount color and when-label color are driven purely by CSS cascade — no Java code.
         *   EXCEPTION        → .mli.exception  → mli-amt red, mli-when red+bold
         *   SELECTED_EXCEPTION → both
         *   PAID             → .mli.paid       → mli-amt green
         *   SELECTED         → .mli.on         → primary-soft bg + left border
         */
        RowVariant rowVariant() {
            if (isSelected && isException) return RowVariant.SELECTED_EXCEPTION;
            if (isSelected)                return RowVariant.SELECTED;
            if (isException)               return RowVariant.EXCEPTION;
            if (status == BillStatus.PAID) return RowVariant.PAID;
            return RowVariant.NONE;
        }
    }

    /** Exact data from bills.html sample — covers every row variant */
    private static final List<Bill> BILLS = List.of(
        // ── Exception rows (selected+exception, exception, exception) ──
        new Bill("BILL-2026-0334", 42612.00, "PrahaTech s.r.o.",    "exception",     true, true,  "! qty mismatch", ChipVariant.VARIANCE, "3-way", ChipVariant.MATCHED, "PO-2026-0188", BillStatus.AWAITING),
        new Bill("BILL-2026-0331", 18820.00, "Helix Robotics GmbH", "price variance",true, false, "! +€420",        ChipVariant.VARIANCE, null,   null,               "PO-2026-0178", BillStatus.AWAITING),
        new Bill("BILL-2026-0328",  4820.00, "Vesuvio Foods SRL",   "dupe PO",       true, false, "! dup",          ChipVariant.VARIANCE, null,   null,               "PO-2026-0172", BillStatus.AWAITING),
        // ── Normal rows (due/approved badges) ──
        new Bill("BILL-2026-0342", 62840.00, "BioGenetics Lab",     "due in 4d",     false, false, "matched",       ChipVariant.MATCHED,  null,   null,               "PO-2026-0192", BillStatus.DUE),
        new Bill("BILL-2026-0340", 48200.00, "Helix Robotics GmbH", "due in 11d",    false, false, "matched",       ChipVariant.MATCHED,  null,   null,               "PO-2026-0185", BillStatus.APPROVED),
        new Bill("BILL-2026-0337", 28400.00, "Lumen Health AG",     "approved 2d",   false, false, "matched",       ChipVariant.MATCHED,  null,   null,               "PO-2026-0182 · rebate", BillStatus.APPROVED),
        // ── Paid rows — amount turns green via .mli.paid .mli-amt CSS cascade ──
        new Bill("BILL-2026-0325", 24000.00, "Atelier Tremblay",    "paid 1d ago",   false, false, "matched",       ChipVariant.MATCHED,  null,   null,               "SEPA · on-time", BillStatus.PAID),
        new Bill("BILL-2026-0316", 18400.00, "PrahaTech s.r.o.",    "paid 4d ago",   false, false, "matched",       ChipVariant.MATCHED,  null,   null,               "SEPA · on-time", BillStatus.PAID)
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public LitRendererBuilderDemoView() {
        addClassName("app-view");

        add(new H1("LitRendererBuilder"));
        add(new Paragraph(
                "Fluent DSL for composing LitRenderer templates — no raw HTML strings. "
                + "All CSS class names are hardcoded inside each named sub-builder; "
                + "calling code uses only ValueProvider lambdas and semantic enum values."));

        // ── Legacy sub-builders ──
        add(new H2("Semantic Sub-Builders"));
        var legacy = ResponsiveDiv.flex().column().gapL().build();
        legacy.add(documentRowExample());
        legacy.add(mobileListItemExample());
        legacy.add(avatarLayoutExample());
        legacy.add(actionButtonExample());
        add(legacy);

        // ── gridCell() examples ──
        add(new H2("gridCell() — Generic Grid Cell Builder"));
        add(new Paragraph(
                "gridCell() provides N stacked rows with start/end slots. "
                + "CellStyle gives a type-safe API — no CSS class strings required. "
                + "An optional media element (avatar/image/icon) takes the full left side."));
        var gcExamples = ResponsiveDiv.flex().column().gapL().build();
        gcExamples.add(gcBasicTwoRowExample());
        gcExamples.add(gcAvatarMediaExample());
        gcExamples.add(gcDynamicPillExample());
        gcExamples.add(gcProductWithImageExample());
        gcExamples.add(gcEmployeeDirectoryExample());
        gcExamples.add(gcTaskListWithIconMediaExample());
        gcExamples.add(gcClickableRowExample());
        gcExamples.add(gcThreeRowNoMediaExample());
        gcExamples.add(gcStartIconAlignmentExample());
        add(gcExamples);
    }

    // ── Example 1 — 3-row document row ───────────────────────────────────────

    /**
     * Uses {@link LitRendererBuilder#documentRow()} — a semantic sub-builder that
     * mirrors {@code MobileGridColumnBuilder} in style:
     * <ul>
     *   <li>Callers pass only {@code ValueProvider} lambdas and a {@link StatusType} enum</li>
     *   <li>All CSS class names ({@code li-po}, {@code li-amt}, {@code li-row2},
     *       {@code status st-pending}, {@code li-meta}) are hardcoded internally</li>
     *   <li>Zero CSS strings in calling code</li>
     * </ul>
     */
    private DemoExample documentRowExample() {
        var renderer = LitRendererBuilder.<Order>documentRow()
                .withReference(Order::poNumber)
                .withAmount(Order::formattedAmount)
                .withTitle(Order::company)
                .withStatus(Order::statusLabel, Order::statusType)
                .withMeta(Order::meta)
                .build();

        var grid = new Grid<>(Order.class, false);
        grid.addColumn(renderer).setHeader("Order").setAutoWidth(true);
        grid.setItems(ORDERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("3-Row Document Row (documentRow builder)", grid, """
                // No CSS class names — all styling is hardcoded inside DocumentRowLitRenderer
                var renderer = LitRendererBuilder.<Order>documentRow()
                    .withReference(Order::poNumber)       // → li-po (row 1 left)
                    .withAmount(Order::formattedAmount)   // → li-amt (row 1 right)
                    .withTitle(Order::company)            // → li-row2
                    .withStatus(                          // → status st-* (row 3 left)
                        Order::statusLabel,
                        Order::statusType)                // returns StatusType.PENDING etc.
                    .withMeta(Order::meta)                // → li-meta (row 3 right)
                    .build();

                grid.addColumn(renderer).setHeader("Order").setAutoWidth(true);
                """);
    }

    // ── Example 2 — 4-section mobile list item ────────────────────────────────

    /**
     * Demonstrates {@link LitRendererBuilder#mobileListItem()} — the 4-section pattern
     * with top / vendor / meta / bottom layout. Highlights:
     * <ul>
     *   <li>{@code withRootVariant} drives a row-level background tint and left border</li>
     *   <li>{@code withChip} + {@link ChipVariant} renders a colored inline badge in the meta row</li>
     *   <li>{@code withStatus} + {@link StatusVariant} renders the bottom-right badge</li>
     *   <li>Rows without a chip ({@code BILLS[1]}) simply skip the chip node</li>
     * </ul>
     */
    private DemoExample mobileListItemExample() {
        var renderer = LitRendererBuilder.<Bill>mobileListItem()
                .withRootVariant(Bill::rowVariant)
                .withNumber(Bill::billNumber)
                .withWhen(Bill::whenLabel)
                .withVendor(Bill::vendorName)
                .withChip(Bill::chip1Label, Bill::chip1Variant)
                .withSecondaryChip(Bill::chip2Label, Bill::chip2Variant)
                .withMetaRef(Bill::metaRef)
                .withAmount(Bill::formattedAmount)
                .withStatus(Bill::statusLabel, Bill::statusVariant)
                .build();

        var grid = new Grid<>(Bill.class, false);
        grid.addColumn(renderer).setHeader("Bill").setAutoWidth(true);
        grid.setItems(BILLS);
        grid.setAllRowsVisible(true);

        return new DemoExample("4-Section Mobile List Item — bills.html data", grid, """
                var renderer = LitRendererBuilder.<Bill>mobileListItem()
                    // RowVariant drives amount+when color via CSS cascade — no Java logic:
                    //   EXCEPTION        → .mli.exception → mli-amt red, mli-when red+bold
                    //   SELECTED_EXCEPTION → both
                    //   PAID             → .mli.paid      → mli-amt green
                    .withRootVariant(Bill::rowVariant)
                    .withNumber(Bill::billNumber)           // → mli-num  (top, left, blue monospace)
                    .withWhen(Bill::whenLabel)              // → mli-when (top, right, dim; red if exception)
                    .withVendor(Bill::vendorName)           // → mli-vendor
                    .withChip(Bill::chip1Label,             // → span.chip.v / .m / .p
                              Bill::chip1Variant)           //   ChipVariant.VARIANCE → .chip.v (red)
                    .withSecondaryChip(Bill::chip2Label,    //   second chip (e.g. "3-way matched")
                                       Bill::chip2Variant)  //   null label → node omitted entirely
                    .withMetaRef(Bill::metaRef)             // → plain text after chips
                    .withAmount(Bill::formattedAmount)      // → mli-amt (red if exception, green if paid)
                    .withStatus(Bill::statusLabel,          // → mli-status with ::before dot
                                Bill::statusVariant)        //   AWAITING → st-await (amber)
                    .build();                               //   APPROVED → st-approve (violet)
                                                           //   PAID     → st-paid (green)
                                                           //   DUE      → st-due (blue)
                """);
    }

    // ── Example 3 — Avatar + name layout ─────────────────────────────────────

    private DemoExample avatarLayoutExample() {
        var renderer = LitRendererBuilder.<Order>create()
                .withProperty("company", Order::company)
                .withProperty("po",      Order::poNumber)
                .horizontalLayout(h -> h
                        .theme("spacing")
                        .style("align-items: center")
                        .avatar(a -> a.name("${item.company}"))
                        .div(d -> d
                                .span(s -> s.className("li-po").text("${item.company}"))
                                .span(s -> s.className("li-meta").text("${item.po}"))))
                .build();

        var grid = new Grid<>(Order.class, false);
        grid.addColumn(renderer).setHeader("Company").setAutoWidth(true);
        grid.setItems(ORDERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("Avatar + Name Layout", grid, """
                var renderer = LitRendererBuilder.<Order>create()
                    .withProperty("company", Order::company)
                    .withProperty("po",      Order::poNumber)
                    .horizontalLayout(h -> h
                        .theme("spacing")
                        .style("align-items: center")
                        .avatar(a -> a.name("${item.company}"))
                        .div(d -> d
                            .span(s -> s.className("li-po").text("${item.company}"))
                            .span(s -> s.className("li-meta").text("${item.po}"))))
                    .build();
                """);
    }

    // ── Example 3 — Action button with server function ───────────────────────

    private DemoExample actionButtonExample() {
        var renderer = LitRendererBuilder.<Order>create()
                .withProperty("po", Order::poNumber)
                .withProperty("amount", Order::formattedAmount)
                .withFunction("openOrder", (order, ignored) ->
                        com.vaadin.flow.component.notification.Notification.show(
                                "Opening: " + order.poNumber()))
                .horizontalLayout(h -> h
                        .theme("spacing")
                        .style("align-items: center; width: 100%; justify-content: space-between")
                        .span(s -> s.className("li-po").text("${item.po}"))
                        .vaadinButton(b -> b
                                .theme("small tertiary")
                                .text("Open")
                                .onClick("openOrder")))
                .build();

        var grid = new Grid<>(Order.class, false);
        grid.addColumn(renderer).setHeader("Action").setAutoWidth(true);
        grid.setItems(ORDERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("Action Button (withFunction)", grid, """
                var renderer = LitRendererBuilder.<Order>create()
                    .withProperty("po", Order::poNumber)
                    .withFunction("openOrder", (order, ignored) -> service.open(order))
                    .horizontalLayout(h -> h
                        .theme("spacing")
                        .style("align-items: center; width: 100%; justify-content: space-between")
                        .span(s -> s.className("li-po").text("${item.po}"))
                        .vaadinButton(b -> b
                            .theme("small tertiary")
                            .text("Open")
                            .onClick("openOrder")))
                    .build();
                """);
    }

    // =========================================================================
    // gridCell() data models
    // =========================================================================

    public enum CustomerStatus { ACTIVE, TRIAL, SUSPENDED, CHURNED }

    public record Customer(
            String fullName, String email, String city, String plan,
            CustomerStatus status, String phone, LocalDate since
    ) {
        String sinceLabel() { return since != null ? since.toString() : ""; }
        CellStyle.PillStyle statusPill() {
            return switch (status) {
                case ACTIVE    -> CellStyle.pill().success();
                case TRIAL     -> CellStyle.pill().warning();
                case SUSPENDED -> CellStyle.pill().error();
                case CHURNED   -> CellStyle.pill().contrast();
            };
        }
        String statusLabel() {
            return switch (status) {
                case ACTIVE    -> "Active";
                case TRIAL     -> "Trial";
                case SUSPENDED -> "Suspended";
                case CHURNED   -> "Churned";
            };
        }
    }

    private static final List<Customer> CUSTOMERS = List.of(
            new Customer("Jane Smith",    "jane@lumen.io",      "Berlin",   "Enterprise", CustomerStatus.ACTIVE,    "+49 30 12345678",  LocalDate.of(2024, 3, 15)),
            new Customer("Carlos Ruiz",   "c.ruiz@novab.com",   "Madrid",   "Pro",        CustomerStatus.TRIAL,     "+34 91 9876543",   LocalDate.of(2026, 5, 1)),
            new Customer("Aiko Tanaka",   "aiko@cortex.jp",     "Tokyo",    "Enterprise", CustomerStatus.ACTIVE,    "+81 3 1234 5678",  LocalDate.of(2023, 11, 20)),
            new Customer("Lars Eriksson", "lars@bluesky.se",    "Stockholm","Starter",    CustomerStatus.SUSPENDED, "+46 8 123 456",    LocalDate.of(2025, 1, 8)),
            new Customer("Priya Sharma",  "priya@biolab.in",    "Mumbai",   "Pro",        CustomerStatus.ACTIVE,    "+91 22 1234 5678", LocalDate.of(2024, 7, 3)),
            new Customer("Oliver Brown",  "o.brown@atelier.fr", "Paris",    "Starter",    CustomerStatus.CHURNED,   "+33 1 23 45 67 89",LocalDate.of(2022, 9, 14))
    );

    public enum TaskPriority { HIGH, MEDIUM, LOW }
    public enum TaskState   { TODO, IN_PROGRESS, REVIEW, DONE }

    public record Task(
            String id, String title, String assignee, TaskPriority priority,
            TaskState state, String dueDate, String category
    ) {
        String priorityIcon() {
            return switch (priority) {
                case HIGH   -> "vaadin:arrow-up";
                case MEDIUM -> "vaadin:minus";
                case LOW    -> "vaadin:arrow-down";
            };
        }
        CellStyle.PillStyle statePill() {
            return switch (state) {
                case TODO        -> CellStyle.pill().neutral();
                case IN_PROGRESS -> CellStyle.pill().primary();
                case REVIEW      -> CellStyle.pill().warning();
                case DONE        -> CellStyle.pill().success();
            };
        }
        String stateLabel() {
            return switch (state) {
                case TODO        -> "To Do";
                case IN_PROGRESS -> "In Progress";
                case REVIEW      -> "Review";
                case DONE        -> "Done";
            };
        }
    }

    private static final List<Task> TASKS = List.of(
            new Task("TASK-001", "Migrate authentication to OAuth 2.0",  "Jane Smith",    TaskPriority.HIGH,   TaskState.IN_PROGRESS, "Jul 5",  "Security"),
            new Task("TASK-002", "Write unit tests for BillingService",  "Carlos Ruiz",   TaskPriority.MEDIUM, TaskState.TODO,        "Jul 12", "QA"),
            new Task("TASK-003", "Update API documentation",             "Aiko Tanaka",   TaskPriority.LOW,    TaskState.DONE,        "Jun 30", "Docs"),
            new Task("TASK-004", "Fix N+1 query in OrderRepository",     "Lars Eriksson", TaskPriority.HIGH,   TaskState.REVIEW,      "Jul 3",  "Performance"),
            new Task("TASK-005", "Design new onboarding flow screens",   "Priya Sharma",  TaskPriority.MEDIUM, TaskState.IN_PROGRESS, "Jul 8",  "UX")
    );

    public record Product(
            String sku, String name, String category, double price,
            int stock, boolean active, String imageUrl
    ) {
        String formattedPrice() { return "€" + String.format(java.util.Locale.US, "%.2f", price); }
        String stockLabel()     { return stock + " in stock"; }
        CellStyle.PillStyle stockPill() {
            if (stock == 0)   return CellStyle.pill().error();
            if (stock < 10)   return CellStyle.pill().warning();
            return CellStyle.pill().success();
        }
        String stockPillLabel() {
            if (stock == 0)  return "Out of stock";
            if (stock < 10)  return "Low stock";
            return "In stock";
        }
    }

    private static final List<Product> PRODUCTS = List.of(
            new Product("SKU-001", "Ergonomic Office Chair",    "Furniture",     299.00, 42, true,  "https://picsum.photos/seed/chair/48/48"),
            new Product("SKU-002", "Mechanical Keyboard",       "Electronics",   129.99, 8,  true,  "https://picsum.photos/seed/kb/48/48"),
            new Product("SKU-003", "Standing Desk",             "Furniture",     599.00, 0,  false, "https://picsum.photos/seed/desk/48/48"),
            new Product("SKU-004", "4K Monitor 27\"",           "Electronics",   449.00, 15, true,  "https://picsum.photos/seed/monitor/48/48"),
            new Product("SKU-005", "USB-C Hub 7-in-1",          "Accessories",    49.95, 3,  true,  "https://picsum.photos/seed/hub/48/48")
    );

    public record Employee(
            String name, String email, String department, String role,
            String location, boolean remote, String startDate
    ) {
        String locationIcon() { return remote ? "vaadin:home" : "vaadin:building"; }
        String locationLabel() { return remote ? location + " (remote)" : location; }
    }

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee("Jane Smith",    "jane@co.io",    "Engineering",  "Senior Engineer",     "Berlin",    false, "2021-03-01"),
            new Employee("Carlos Ruiz",   "c.ruiz@co.io",  "Engineering",  "Backend Developer",   "Madrid",    true,  "2022-06-15"),
            new Employee("Aiko Tanaka",   "aiko@co.io",    "Design",       "UX Lead",             "Tokyo",     false, "2020-11-20"),
            new Employee("Lars Eriksson", "lars@co.io",    "Finance",      "Financial Analyst",   "Stockholm", true,  "2023-01-08"),
            new Employee("Priya Sharma",  "priya@co.io",   "Product",      "Product Manager",     "Mumbai",    true,  "2021-07-03"),
            new Employee("Oliver Brown",  "oliver@co.io",  "Sales",        "Account Executive",   "Paris",     false, "2022-09-14")
    );

    // =========================================================================
    // gridCell() examples
    // =========================================================================

    // ── GC-1: Basic 2-row, no media ───────────────────────────────────────────
    private DemoExample gcBasicTwoRowExample() {
        var renderer = LitRendererBuilder.<Customer>gridCell()
                .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())
                        .endText(Customer::plan, CellStyle.text().caption()))
                .addRow(row -> row
                        .startSpan(Customer::email, CellStyle.span().caption())
                        .endSpan(Customer::city, CellStyle.span().caption()))
                .build();

        var grid = new Grid<>(Customer.class, false);
        grid.addColumn(renderer).setHeader("Customer").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(CUSTOMERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("1 — Basic 2-Row Cell (no media)", grid, """
                // CellStyle.text() / CellStyle.span() replace raw CSS class strings
                // No media element — rows fill the full cell width
                var renderer = LitRendererBuilder.<Customer>gridCell()
                    .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())  // semibold + header
                        .endText  (Customer::plan,     CellStyle.text().caption())) // xs + muted
                    .addRow(row -> row
                        .startSpan(Customer::email,  CellStyle.span().caption())
                        .endSpan  (Customer::city,   CellStyle.span().caption()))
                    .build();
                """);
    }

    // ── GC-2: Avatar media + 3 rows ──────────────────────────────────────────
    private DemoExample gcAvatarMediaExample() {
        var renderer = LitRendererBuilder.<Customer>gridCell()
                .mediaAvatar(Customer::fullName)
                .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())
                        .endPill(Customer::statusLabel, Customer::statusPill))
                .addRow(row -> row
                        .startSpan(Customer::email, CellStyle.span().caption())
                        .endSpan(Customer::city, CellStyle.span().caption()))
                .addRow(row -> row
                        .startSpan(Customer::phone, CellStyle.span().caption())
                        .endText(Customer::sinceLabel, CellStyle.text().caption()))
                .build();

        var grid = new Grid<>(Customer.class, false);
        grid.addColumn(renderer).setHeader("Customer").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(CUSTOMERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("2 — Avatar Media + 3 Rows", grid, """
                // mediaAvatar() places the avatar in a dedicated left column
                // that spans the full cell height — rows stack to the right
                var renderer = LitRendererBuilder.<Customer>gridCell()
                    .mediaAvatar(Customer::fullName)
                    .addRow(row -> row
                        .startText(Customer::fullName,   CellStyle.text().title())
                        .endPill  (Customer::statusLabel, Customer::statusPill))  // dynamic CellStyle.PillStyle
                    .addRow(row -> row
                        .startSpan(Customer::email,  CellStyle.span().caption())
                        .endSpan  (Customer::city,   CellStyle.span().caption()))
                    .addRow(row -> row
                        .startSpan(Customer::phone,      CellStyle.span().caption())
                        .endText  (Customer::sinceLabel, CellStyle.text().caption()))
                    .build();
                """);
    }

    // ── GC-3: Dynamic pill — style resolved per-row from data ────────────────
    private DemoExample gcDynamicPillExample() {
        var renderer = LitRendererBuilder.<Customer>gridCell()
                .mediaAvatar(Customer::fullName)
                .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())
                        // dynamic pill: CellStyle.PillStyle returned per row
                        .endPill(Customer::statusLabel, customer -> switch (customer.status()) {
                            case ACTIVE    -> CellStyle.pill().success();
                            case TRIAL     -> CellStyle.pill().warning();
                            case SUSPENDED -> CellStyle.pill().error();
                            case CHURNED   -> CellStyle.pill().contrast();
                        }))
                .addRow(row -> row
                        .startSpan(Customer::plan, CellStyle.span().sm().secondary())
                        .endText(Customer::city, CellStyle.text().caption()))
                .build();

        var grid = new Grid<>(Customer.class, false);
        grid.addColumn(renderer).setHeader("Status").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(CUSTOMERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("3 — Dynamic Pill Style Per Row", grid, """
                // The pill style is resolved PER ROW from data — no CSS strings
                // ValueProvider<Customer, CellStyle.PillStyle> → computed at render time
                var renderer = LitRendererBuilder.<Customer>gridCell()
                    .mediaAvatar(Customer::fullName)
                    .addRow(row -> row
                        .startText(Customer::fullName,   CellStyle.text().title())
                        .endPill  (Customer::statusLabel, customer -> switch (customer.status()) {
                            case ACTIVE    -> CellStyle.pill().success();
                            case TRIAL     -> CellStyle.pill().warning();
                            case SUSPENDED -> CellStyle.pill().error();
                            case CHURNED   -> CellStyle.pill().contrast();
                        }))
                    .addRow(row -> row
                        .startSpan(Customer::plan, CellStyle.span().sm().secondary())
                        .endText  (Customer::city, CellStyle.text().caption()))
                    .build();
                """);
    }

    // ── GC-4: Product with image media ────────────────────────────────────────
    private DemoExample gcProductWithImageExample() {
        var renderer = LitRendererBuilder.<Product>gridCell()
                .mediaImage(Product::imageUrl, Product::name, CellStyle.image().rounded())
                .addRow(row -> row
                        .startText(Product::name, CellStyle.text().title())
                        .endText(Product::formattedPrice, CellStyle.text().amount()))
                .addRow(row -> row
                        .startSpan(Product::category, CellStyle.span().caption())
                        .endPill(Product::stockPillLabel, Product::stockPill))
                .addRow(row -> row
                        .startSpan(Product::sku, CellStyle.span().xs().muted())
                        .endText(Product::stockLabel, CellStyle.text().caption()))
                .build();

        var grid = new Grid<>(Product.class, false);
        grid.addColumn(renderer).setHeader("Product").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(PRODUCTS);
        grid.setAllRowsVisible(true);

        return new DemoExample("4 — Product with Image Media", grid, """
                // mediaImage() places a rounded image in the left media column
                // CellStyle.text().amount() → bold + monospace (great for prices)
                var renderer = LitRendererBuilder.<Product>gridCell()
                    .mediaImage(Product::imageUrl, Product::name,
                                CellStyle.image().rounded())
                    .addRow(row -> row
                        .startText(Product::name,           CellStyle.text().title())
                        .endText  (Product::formattedPrice, CellStyle.text().amount()))
                    .addRow(row -> row
                        .startSpan(Product::category,       CellStyle.span().caption())
                        .endPill  (Product::stockPillLabel, Product::stockPill))  // dynamic
                    .addRow(row -> row
                        .startSpan(Product::sku,            CellStyle.span().xs().muted())
                        .endText  (Product::stockLabel,     CellStyle.text().caption()))
                    .build();
                """);
    }

    // ── GC-5: Employee directory with avatar + department pill ────────────────
    private DemoExample gcEmployeeDirectoryExample() {
        var renderer = LitRendererBuilder.<Employee>gridCell()
                .mediaAvatar(Employee::name)
                .addRow(row -> row
                        .startText(Employee::name, CellStyle.text().title())
                        .endPill(Employee::department, CellStyle.pill().primary()))
                .addRow(row -> row
                        .startSpan(Employee::role, CellStyle.span().sm().secondary()))
                .addRow(row -> row
                        .startIcon(Employee::locationIcon, CellStyle.icon().muted().sm())
                        .startSpan(Employee::locationLabel, CellStyle.span().caption())
                        .endText(Employee::startDate, CellStyle.text().caption()))
                .build();

        var grid = new Grid<>(Employee.class, false);
        grid.addColumn(renderer).setHeader("Employee").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(EMPLOYEES);
        grid.setAllRowsVisible(true);

        return new DemoExample("5 — Employee Directory (icon in start slot)", grid, """
                // Dynamic icon in start slot — icon name computed from data
                // Static pill color (all departments get primary blue)
                var renderer = LitRendererBuilder.<Employee>gridCell()
                    .mediaAvatar(Employee::name)
                    .addRow(row -> row
                        .startText(Employee::name,       CellStyle.text().title())
                        .endPill  (Employee::department, CellStyle.pill().primary()))
                    .addRow(row -> row
                        .startSpan(Employee::role,       CellStyle.span().sm().secondary()))
                    .addRow(row -> row
                        .startIcon (Employee::locationIcon,  CellStyle.icon().muted().sm())
                        .startSpan (Employee::locationLabel, CellStyle.span().caption())
                        .endText   (Employee::startDate,     CellStyle.text().caption()))
                    .build();
                """);
    }

    // ── GC-6: Task list with category icon media ──────────────────────────────
    private DemoExample gcTaskListWithIconMediaExample() {
        var renderer = LitRendererBuilder.<Task>gridCell()
                .mediaIcon(Task::priorityIcon, CellStyle.icon().lg())
                .addRow(row -> row
                        .startText(Task::title, CellStyle.text().title())
                        .endPill(Task::stateLabel, Task::statePill))
                .addRow(row -> row
                        .startSpan(Task::assignee, CellStyle.span().sm().secondary())
                        .endText(Task::dueDate, CellStyle.text().caption()))
                .addRow(row -> row
                        .startPill(Task::category, CellStyle.pill().neutral())
                        .endSpan(Task::id, CellStyle.span().xs().muted()))
                .build();

        var grid = new Grid<>(Task.class, false);
        grid.addColumn(renderer).setHeader("Task").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(TASKS);
        grid.setAllRowsVisible(true);

        return new DemoExample("6 — Task List with Dynamic Icon Media", grid, """
                // mediaIcon() with ValueProvider — icon name resolved per row
                // Priority icon changes per task (vaadin:arrow-up / minus / arrow-down)
                var renderer = LitRendererBuilder.<Task>gridCell()
                    .mediaIcon(Task::priorityIcon, CellStyle.icon().lg())
                    .addRow(row -> row
                        .startText(Task::title,      CellStyle.text().title())
                        .endPill  (Task::stateLabel, Task::statePill))  // dynamic pill
                    .addRow(row -> row
                        .startSpan(Task::assignee, CellStyle.span().sm().secondary())
                        .endText  (Task::dueDate,  CellStyle.text().caption()))
                    .addRow(row -> row
                        .startPill(Task::category, CellStyle.pill().neutral())
                        .endSpan  (Task::id,       CellStyle.span().xs().muted()))
                    .build();
                """);
    }

    // ── GC-7: Clickable rows with onItemClick ─────────────────────────────────
    private DemoExample gcClickableRowExample() {
        var renderer = LitRendererBuilder.<Customer>gridCell()
                .mediaAvatar(Customer::fullName)
                .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())
                        .endIcon("vaadin:chevron-right", CellStyle.icon().muted()))
                .addRow(row -> row
                        .startSpan(Customer::email, CellStyle.span().caption()))
                .onItemClick("openCustomer")
                .withFunction("openCustomer", (customer, ignored) ->
                        Notification.show("Opening: " + customer.fullName()))
                .build();

        var grid = new Grid<>(Customer.class, false);
        grid.addColumn(renderer).setHeader("Customer (click a row)").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(CUSTOMERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("7 — Clickable Rows with onItemClick", grid, """
                // onItemClick() wraps the whole cell in a click handler
                // withFunction() wires the server-side callback
                var renderer = LitRendererBuilder.<Customer>gridCell()
                    .mediaAvatar(Customer::fullName)
                    .addRow(row -> row
                        .startText(Customer::fullName, CellStyle.text().title())
                        .endIcon  ("vaadin:chevron-right", CellStyle.icon().muted()))
                    .addRow(row -> row
                        .startSpan(Customer::email, CellStyle.span().caption()))
                    .onItemClick("openCustomer")
                    .withFunction("openCustomer", (customer, ignored) ->
                        navigateTo(customer))
                    .build();
                """);
    }

    // ── GC-8: 3-row, no media — order summary ────────────────────────────────
    private DemoExample gcThreeRowNoMediaExample() {
        var renderer = LitRendererBuilder.<Order>gridCell()
                .addRow(row -> row
                        .startText(Order::poNumber, CellStyle.text().semibold().mono())
                        .endText(Order::formattedAmount, CellStyle.text().amount()))
                .addRow(row -> row
                        .startSpan(Order::company, CellStyle.span().sm().secondary()))
                .addRow(row -> row
                        .startPill(Order::statusLabel, order -> switch (order.status()) {
                            case ACTIVE  -> CellStyle.pill().success();
                            case PENDING -> CellStyle.pill().warning();
                            case ERROR   -> CellStyle.pill().error();
                            case CLOSED  -> CellStyle.pill().contrast();
                        })
                        .endText(Order::meta, CellStyle.text().caption()))
                .build();

        var grid = new Grid<>(Order.class, false);
        grid.addColumn(renderer).setHeader("Order").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(ORDERS);
        grid.setAllRowsVisible(true);

        return new DemoExample("8 — 3-Row Order Summary (no media)", grid, """
                // No media — all 3 rows fill the full cell width
                // CellStyle.text().semibold().mono() for reference numbers
                // Dynamic pill in start slot
                var renderer = LitRendererBuilder.<Order>gridCell()
                    .addRow(row -> row
                        .startText(Order::poNumber,       CellStyle.text().semibold().mono())
                        .endText  (Order::formattedAmount,CellStyle.text().amount()))
                    .addRow(row -> row
                        .startSpan(Order::company, CellStyle.span().sm().secondary()))
                    .addRow(row -> row
                        .startPill(Order::statusLabel, order -> switch (order.status()) {
                            case ACTIVE  -> CellStyle.pill().success();
                            case PENDING -> CellStyle.pill().warning();
                            case ERROR   -> CellStyle.pill().error();
                            case CLOSED  -> CellStyle.pill().contrast();
                        })
                        .endText(Order::meta, CellStyle.text().caption()))
                    .build();
                """);
    }

    // ── GC-9: Start icon alignment + static labels ────────────────────────────
    private DemoExample gcStartIconAlignmentExample() {
        var renderer = LitRendererBuilder.<Task>gridCell()
                .addRow(row -> row
                        .startText(Task::title, CellStyle.text().title())
                        .endPill(Task::stateLabel, Task::statePill))
                .addRow(row -> row
                        .startIcon("vaadin:user",     CellStyle.icon().muted().sm())
                        .startSpan(Task::assignee,    CellStyle.span().sm().secondary())
                        .endIcon("vaadin:calendar",   CellStyle.icon().muted().sm())
                        .endText(Task::dueDate,       CellStyle.text().caption()))
                .addRow(row -> row
                        .startIcon("vaadin:folder-o", CellStyle.icon().muted().sm())
                        .startPill(Task::category,    CellStyle.pill().neutral())
                        .endSpan(Task::id,            CellStyle.span().xs().muted()))
                .build();

        var grid = new Grid<>(Task.class, false);
        grid.addColumn(renderer).setHeader("Task").setAutoWidth(true).setFlexGrow(1);
        grid.setItems(TASKS);
        grid.setAllRowsVisible(true);

        return new DemoExample("9 — Multiple Icons in Start + End Slots", grid, """
                // Static icons used as prefix decorators alongside text/spans
                // Multiple components in same slot are laid out horizontally
                // CellStyle.icon().muted().sm() → small muted icon
                var renderer = LitRendererBuilder.<Task>gridCell()
                    .addRow(row -> row
                        .startText(Task::title,      CellStyle.text().title())
                        .endPill  (Task::stateLabel, Task::statePill))
                    .addRow(row -> row
                        .startIcon ("vaadin:user",     CellStyle.icon().muted().sm())
                        .startSpan (Task::assignee,    CellStyle.span().sm().secondary())
                        .endIcon   ("vaadin:calendar", CellStyle.icon().muted().sm())
                        .endText   (Task::dueDate,     CellStyle.text().caption()))
                    .addRow(row -> row
                        .startIcon ("vaadin:folder-o", CellStyle.icon().muted().sm())
                        .startPill (Task::category,    CellStyle.pill().neutral())
                        .endSpan   (Task::id,          CellStyle.span().xs().muted()))
                    .build();
                """);
    }
}


