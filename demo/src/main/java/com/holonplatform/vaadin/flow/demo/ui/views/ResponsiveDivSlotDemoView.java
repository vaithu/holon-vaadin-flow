package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Tag;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo view for the {@code slotOnce(ViewMode, Supplier<Component>)} API on {@link ResponsiveDiv}.
 *
 * <p>Shows how to build the RIGHT component for each viewport once, on first attach, without
 * ever creating the unused alternative. Mobile users never pay for a Grid; desktop users never
 * pay for the card list. Both variants share the same data list — the data is cheap; the
 * component tree is not.
 *
 * <p>Examples:
 * <ol>
 *   <li>Product catalog — Grid on desktop, card grid on mobile</li>
 *   <li>Team directory  — table on desktop, avatar-card list on mobile</li>
 *   <li>Order summary   — full data-table on desktop, compact KeyValueList on mobile/tablet</li>
 * </ol>
 */
@PageTitle("ResponsiveDiv slotOnce – Holon Demo")
@Route(value = "responsive-div-slot", layout = DemoMainLayout.class)
public class ResponsiveDivSlotDemoView extends Div {

    // ── Shared demo data ──────────────────────────────────────────────────────

    record Product(long id, String name, String category, double price, String status) {}

    private static final List<Product> PRODUCTS = List.of(
            new Product(1,  "Wireless Keyboard",  "Peripherals", 79.99,  "In Stock"),
            new Product(2,  "USB-C Hub 7-port",   "Peripherals", 49.99,  "In Stock"),
            new Product(3,  "27\" 4K Monitor",    "Displays",    649.00, "Low Stock"),
            new Product(4,  "Mechanical Keyboard","Peripherals", 129.00, "In Stock"),
            new Product(5,  "Noise-Cancel Headset","Audio",      199.00, "In Stock"),
            new Product(6,  "Webcam 4K Pro",      "Cameras",     149.99, "Out of Stock"),
            new Product(7,  "Laptop Stand",       "Accessories",  39.99, "In Stock"),
            new Product(8,  "SSD 2TB NVMe",       "Storage",     179.00, "In Stock"),
            new Product(9,  "Ergonomic Mouse",    "Peripherals",  59.99, "In Stock"),
            new Product(10, "Thunderbolt Dock",   "Peripherals", 249.00, "Low Stock")
    );

    record TeamMember(String name, String role, String department, String email) {}

    private static final List<TeamMember> TEAM = List.of(
            new TeamMember("Alice Nguyen",   "Engineering Lead",     "Backend",   "alice@example.com"),
            new TeamMember("Bob Chen",       "Senior Developer",     "Frontend",  "bob@example.com"),
            new TeamMember("Carol Martinez", "Product Manager",      "Product",   "carol@example.com"),
            new TeamMember("David Kim",      "UX Designer",          "Design",    "david@example.com"),
            new TeamMember("Eva Rossi",      "DevOps Engineer",      "Infra",     "eva@example.com"),
            new TeamMember("Frank Osei",     "QA Lead",              "Quality",   "frank@example.com")
    );

    record Order(String ref, String customer, String date, double total, String status) {}

    private static final List<Order> ORDERS = List.of(
            new Order("ORD-001", "Acme Corp",     "2026-04-01",  1_240.00, "Delivered"),
            new Order("ORD-002", "Globex Ltd",    "2026-04-05",    389.50, "Processing"),
            new Order("ORD-003", "Initech",       "2026-04-08",  2_899.00, "Delivered"),
            new Order("ORD-004", "Umbrella Inc",  "2026-04-12",    645.00, "Shipped"),
            new Order("ORD-005", "Stark Ind.",    "2026-04-15",  7_320.00, "Processing")
    );

    // ── Constructor ───────────────────────────────────────────────────────────

    public ResponsiveDivSlotDemoView() {
        addClassName("app-view");

        var title = new H1("ResponsiveDiv — slotOnce");

        var desc = new Paragraph(
                "slotOnce(ViewMode, Supplier<Component>) builds the right component once on first attach. " +
                "The unused variant's Supplier is never called — no extra Grid, no extra DataProvider, " +
                "no hidden DOM nodes. Resize to a mobile width to see the card layout; " +
                "expand back to desktop to see the table. The component that was built stays; " +
                "no re-render occurs on resize.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(productCatalogExample());
        examples.add(teamDirectoryExample());
        examples.add(orderSummaryExample());

        add(title, desc, examples);
    }

    // ── Example 1 — Product catalog: Grid ↔ card grid ────────────────────────

    private DemoExample productCatalogExample() {
        var container = ResponsiveDiv.flex().column().noGap()
                .slotOnce(ViewMode.MOBILE,  this::buildProductCards)
                .slotOnce(ViewMode.DESKTOP, this::buildProductGrid)
                .build();

        return new DemoExample(
                "Product catalog — Grid on desktop · card grid on mobile",
                container,
                """
                // ONLY the supplier matching the current viewport is ever called.
                // Mobile user → buildProductCards() runs once; buildProductGrid() is NEVER called.
                // Desktop user → buildProductGrid() runs once; buildProductCards() is NEVER called.

                var container = ResponsiveDiv.flex().column()
                    .slotOnce(ViewMode.MOBILE,  this::buildProductCards)
                    .slotOnce(ViewMode.DESKTOP, this::buildProductGrid)
                    .build();
                """);
    }

    /** Desktop: a proper sortable Grid with all columns. */
    private Component buildProductGrid() {
        var grid = new Grid<>(Product.class, false);
        grid.addColumn(Product::id).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(Product::name).setHeader("Name").setFlexGrow(2);
        grid.addColumn(Product::category).setHeader("Category").setFlexGrow(1);
        grid.addColumn(p -> String.format("$%.2f", p.price())).setHeader("Price").setFlexGrow(1);
        grid.addComponentColumn(p -> statusBadge(p.status())).setHeader("Status").setFlexGrow(1);
        grid.setItems(PRODUCTS);
        grid.setAllRowsVisible(true);
        grid.addClassName("product-grid");
        return grid;
    }

    /** Mobile: a 1-column card list — no table headers, touch-friendly spacing. */
    private Component buildProductCards() {
        var cards = ResponsiveDiv.grid().mobile(1).gapS().build();
        for (var p : PRODUCTS) {
            cards.add(buildProductCard(p));
        }
        return cards;
    }

    private Component buildProductCard(Product p) {
        var card = ResponsiveDiv.flex().column().card().padM().gapS().build();

        var header = ResponsiveDiv.flex().row().justifyBetween().alignCenter().noGap().build();
        header.add(new H3(p.name()));
        header.add(statusBadge(p.status()));

        var meta = new Span(p.category() + " · $" + String.format("%.2f", p.price()));
        meta.addClassName("text-secondary");

        card.add(header, meta);
        return card;
    }

    // ── Example 2 — Team directory: table ↔ avatar-card list ─────────────────

    private DemoExample teamDirectoryExample() {
        var container = ResponsiveDiv.flex().column().noGap()
                .slotOnce(ViewMode.MOBILE,  this::buildTeamCards)
                .slotOnce(ViewMode.TABLET,  this::buildTeamCards)   // tablet shares mobile card view
                .slotOnce(ViewMode.DESKTOP, this::buildTeamTable)
                .build();

        return new DemoExample(
                "Team directory — table on desktop · avatar cards on mobile & tablet",
                container,
                """
                // Tablet and mobile share the same card view via two slotOnce registrations.
                // The fallback chain means TABLET → DESKTOP when no TABLET slot is registered;
                // here we explicitly register TABLET → card view for a better touch experience.

                var container = ResponsiveDiv.flex().column()
                    .slotOnce(ViewMode.MOBILE,  this::buildTeamCards)
                    .slotOnce(ViewMode.TABLET,  this::buildTeamCards)   // explicit tablet override
                    .slotOnce(ViewMode.DESKTOP, this::buildTeamTable)
                    .build();
                """);
    }

    private Component buildTeamTable() {
        var grid = new Grid<>(TeamMember.class, false);
        grid.addColumn(TeamMember::name).setHeader("Name").setFlexGrow(2);
        grid.addColumn(TeamMember::role).setHeader("Role").setFlexGrow(2);
        grid.addColumn(TeamMember::department).setHeader("Department").setFlexGrow(1);
        grid.addColumn(TeamMember::email).setHeader("Email").setFlexGrow(2);
        grid.setItems(TEAM);
        grid.setAllRowsVisible(true);
        return grid;
    }

    private Component buildTeamCards() {
        var list = ResponsiveDiv.flex().column().gapS().build();
        for (var m : TEAM) {
            var card = ResponsiveDiv.flex().row().card().padM().gapM().alignCenter().build();

            // Avatar circle (initials)
            var avatar = new Div(new Span(initials(m.name())));
            avatar.addClassName("avatar-circle");

            var info = ResponsiveDiv.flex().column().noGap().grow().build();
            var nameSpan = new Span(m.name());
            nameSpan.addClassName(Font.Weight.BOLD.getClassName());
            var roleSpan = new Span(m.role() + " · " + m.department());
            roleSpan.addClassName("text-secondary");
            info.add(nameSpan, roleSpan);

            card.add(avatar, info);
            list.add(card);
        }
        return list;
    }

    // ── Example 3 — Order summary: data-table ↔ KeyValueList ─────────────────

    private DemoExample orderSummaryExample() {
        var container = ResponsiveDiv.flex().column().noGap()
                .slotOnce(ViewMode.MOBILE,  this::buildOrderKeyValueList)
                .slotOnce(ViewMode.DESKTOP, this::buildOrderTable)
                .build();

        return new DemoExample(
                "Order summary — full table on desktop · compact KeyValueList on mobile",
                container,
                """
                // KeyValueList is a natural mobile-first component; a full Grid is wasteful on
                // a 375px screen. slotOnce picks the right one without any CSS hide/show tricks.

                var container = ResponsiveDiv.flex().column()
                    .slotOnce(ViewMode.MOBILE,  this::buildOrderKeyValueList)
                    .slotOnce(ViewMode.DESKTOP, this::buildOrderTable)
                    .build();
                """);
    }

    private Component buildOrderTable() {
        var grid = new Grid<>(Order.class, false);
        grid.addColumn(Order::ref).setHeader("Reference").setWidth("110px").setFlexGrow(0);
        grid.addColumn(Order::customer).setHeader("Customer").setFlexGrow(2);
        grid.addColumn(Order::date).setHeader("Date").setFlexGrow(1);
        grid.addColumn(o -> String.format("$%.2f", o.total())).setHeader("Total").setFlexGrow(1);
        grid.addComponentColumn(o -> statusBadge(o.status())).setHeader("Status").setFlexGrow(1);
        grid.setItems(ORDERS);
        grid.setAllRowsVisible(true);
        return grid;
    }

    private Component buildOrderKeyValueList() {
        var wrapper = ResponsiveDiv.flex().column().gapM().build();
        for (var o : ORDERS) {
            var card = ResponsiveDiv.flex().column().card().padM().gapS().build();
            var header = ResponsiveDiv.flex().row().justifyBetween().alignCenter().noGap().build();
            header.add(new Span(o.ref()));
            header.add(statusBadge(o.status()));
            card.add(header);
            card.add(new KeyValueList()
                    .addItem(KeyValueItem.of("Customer", o.customer()))
                    .addItem(KeyValueItem.of("Date",     o.date()))
                    .addItem(KeyValueItem.of("Total",    String.format("$%.2f", o.total()))));
            wrapper.add(card);
        }
        return wrapper;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Component statusBadge(String status) {
        var tag = new Tag(status);
        tag.addClassName(switch (status) {
            case "In Stock"   -> "tag--success";
            case "Low Stock"  -> "tag--warning";
            case "Out of Stock", "Shipped" -> "tag--neutral";
            case "Processing" -> "tag--info";
            case "Delivered"  -> "tag--success";
            default           -> "tag--neutral";
        });
        return tag;
    }

    private static String initials(String fullName) {
        var parts = fullName.split(" ");
        return parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
                : fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }
}

