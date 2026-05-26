package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link SideNavBuilder} — the fluent navigation sidebar builder.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic SideNav with group label and hierarchical items</li>
 *   <li>Recursive search field ({@code withSearch()})</li>
 *   <li>Collapse / expand toggle ({@code withCollapse()})</li>
 *   <li>Full-featured: label + icons + search + collapse</li>
 * </ol>
 */
@PageTitle("SideNav – Holon Demo")
@Route(value = "sidenav", layout = DemoMainLayout.class)
public class SideNavDemoView extends Div {

    public SideNavDemoView() {
        addClassName("app-view");

        var title = new H1("SideNav");

        var desc = new Paragraph(
                "SideNavBuilder is a fluent API over Vaadin's SideNav component. " +
                "It adds a recursive search TextField, a collapse/expand toggle that shrinks the " +
                "sidebar to an icon-only rail, and full support for group labels, nested items, " +
                "and prefix icons. " +
                "Call buildWrapper() to get the composite Div — or build() for the bare SideNav.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(searchExample());
        examples.add(collapseExample());
        examples.add(fullFeaturedExample());

        add(title, desc, examples);
    }

    // ── Example 1 ─────────────────────────────────────────────────────────────

    /**
     * Basic nav: group label + icons + hierarchical items.
     * Demonstrates the CSS hover/active/child-item colouring without any extras.
     */
    private DemoExample basicExample() {
        // Store the builder first — .add() returns SideNavConfigurator<?> (raw),
        // so .build() must be called on the typed builder reference.
        var builder = SideNavBuilder.create().label("Platform");

        builder.withNavItem("Playground", "/playground", VaadinIcon.ROCKET.create())
                .expanded(true)
                .withSubNavItem("History",  "/history")
                .withSubNavItem("Starred",  "/starred")
                .withSubNavItem("Settings", "/playground/settings")
                .add();
        builder.withNavItem("Models",        "/models",        VaadinIcon.CLUSTER.create()).add();
        builder.withNavItem("Documentation", "/documentation", VaadinIcon.BOOK.create()).add();
        builder.withNavItem("Settings",      "/settings",      VaadinIcon.COG.create()).add();

        return new DemoExample("Basic — group label + icons + nested items", wrap(builder.build()), """
                // label("Platform") renders a small uppercase section header.
                // Store the builder first so .build() can be called after the item chains.
                // withSubNavItem() adds a child to the current parent item.
                // Hover, active, and child-item colours come entirely from menu.css.
                var builder = SideNavBuilder.create().label("Platform");

                builder.withNavItem("Playground", "/playground", VaadinIcon.ROCKET.create())
                    .expanded(true)
                    .withSubNavItem("History",  "/history")
                    .withSubNavItem("Starred",  "/starred")
                    .withSubNavItem("Settings", "/playground/settings")
                    .add();
                builder.withNavItem("Models",        "/models",        VaadinIcon.CLUSTER.create()).add();
                builder.withNavItem("Documentation", "/documentation", VaadinIcon.BOOK.create()).add();
                builder.withNavItem("Settings",      "/settings",      VaadinIcon.COG.create()).add();

                var nav = builder.build();
                """);
    }

    // ── Example 2 ─────────────────────────────────────────────────────────────

    /**
     * Search field: type to filter items recursively.
     * Parent items auto-expand when a child matches.
     */
    private DemoExample searchExample() {
        var builder = SideNavBuilder.create()
                .label("Navigation")
                .withSearch("Search menu…");

        builder.withNavItem("Dashboard",    "/dashboard",    VaadinIcon.DASHBOARD.create()).add();
        builder.withNavItem("Analytics",    "/analytics",    VaadinIcon.CHART.create())
                .withSubNavItem("Reports",    "/analytics/reports")
                .withSubNavItem("Metrics",    "/analytics/metrics")
                .withSubNavItem("Forecasts",  "/analytics/forecasts")
                .add();
        builder.withNavItem("Integrations", "/integrations", VaadinIcon.CONNECT.create())
                .withSubNavItem("Webhooks",   "/integrations/webhooks")
                .withSubNavItem("OAuth Apps", "/integrations/oauth")
                .add();
        builder.withNavItem("Team",     "/team",     VaadinIcon.GROUP.create()).add();
        builder.withNavItem("Settings", "/settings", VaadinIcon.COG.create()).add();

        return new DemoExample("withSearch() — recursive item filter as you type", builder.buildWrapper(), """
                // withSearch("placeholder") enables the TextField above the SideNav.
                // As the user types, filterRecursive() is called:
                //   • matching parents auto-expand to reveal matched children
                //   • clearing the field restores all items
                // Requires buildWrapper() — not available from build().
                var builder = SideNavBuilder.create()
                    .label("Navigation")
                    .withSearch("Search menu…");

                builder.withNavItem("Dashboard", "/dashboard", VaadinIcon.DASHBOARD.create()).add();
                builder.withNavItem("Analytics", "/analytics", VaadinIcon.CHART.create())
                    .withSubNavItem("Reports",   "/analytics/reports")
                    .withSubNavItem("Metrics",   "/analytics/metrics")
                    .withSubNavItem("Forecasts", "/analytics/forecasts")
                    .add();
                // … more items …

                var wrapper = builder.buildWrapper();
                """);
    }

    // ── Example 3 ─────────────────────────────────────────────────────────────

    /**
     * Collapse toggle: the chevron at the bottom shrinks the sidebar to an icon-only rail.
     * The icon rotates 180° via CSS transition; no JS or icon swap needed.
     */
    private DemoExample collapseExample() {
        var builder = SideNavBuilder.create()
                .label("Workspace")
                .withCollapse();

        builder.withNavItem("Home",      "/home",      VaadinIcon.HOME.create()).add();
        builder.withNavItem("Projects",  "/projects",  VaadinIcon.FOLDER.create()).add();
        builder.withNavItem("Calendar",  "/calendar",  VaadinIcon.CALENDAR.create()).add();
        builder.withNavItem("Inbox",     "/inbox",     VaadinIcon.ENVELOPE.create()).add();
        builder.withNavItem("Reports",   "/reports",   VaadinIcon.CHART_LINE.create()).add();
        builder.withNavItem("Settings",  "/settings",  VaadinIcon.COG.create()).add();

        return new DemoExample("withCollapse() — icon-only rail on collapse", builder.buildWrapper(), """
                // withCollapse() appends a chevron Button at the bottom of the wrapper.
                // On click it toggles CSS class "sidenav-host--collapsed" on the host Div.
                // menu.css handles all visual transitions:
                //   • wrapper width → --sidenav-collapsed-width (3.25 rem)
                //   • item text hidden by overflow:hidden on a fixed-width ::part(link)
                //   • group label + search field fade via max-height:0 + opacity:0
                //   • chevron rotates 180° via CSS transform — no icon swap in Java
                // Every item MUST have a prefix icon for icon-only mode to be useful.
                var builder = SideNavBuilder.create()
                    .label("Workspace")
                    .withCollapse();

                builder.withNavItem("Home",     "/home",     VaadinIcon.HOME.create()).add();
                builder.withNavItem("Projects", "/projects", VaadinIcon.FOLDER.create()).add();
                // … more items …

                var wrapper = builder.buildWrapper();
                """);
    }

    // ── Example 4 ─────────────────────────────────────────────────────────────

    /**
     * Full-featured: group label + icons + nested items + search + collapse.
     * Production-ready sidebar pattern matching the reference screenshot.
     */
    private DemoExample fullFeaturedExample() {
        var builder = SideNavBuilder.create()
                .label("Platform")
                .withSearch("Search menu…")
                .withCollapse();

        builder.withNavItem("Playground", "/playground", VaadinIcon.ROCKET.create())
                .expanded(true)
                .withSubNavItem("History",  "/playground/history")
                .withSubNavItem("Starred",  "/playground/starred")
                .withSubNavItem("Settings", "/playground/settings")
                .add();
        builder.withNavItem("Models",        "/models",        VaadinIcon.CLUSTER.create()).add();
        builder.withNavItem("Documentation", "/documentation", VaadinIcon.BOOK.create()).add();
        builder.withNavItem("Settings",      "/settings",      VaadinIcon.COG.create()).add();

        return new DemoExample("Full-featured — label + icons + search + collapse", builder.buildWrapper(), """
                // Combine withSearch() and withCollapse() freely — they compose independently.
                // buildWrapper() returns a Div (.sidenav-host) containing:
                //   1. TextField  (.sidenav-search)         — if withSearch() was called
                //   2. SideNav    (vaadin-side-nav)
                //   3. Button     (.sidenav-collapse-toggle) — if withCollapse() was called
                var builder = SideNavBuilder.create()
                    .label("Platform")
                    .withSearch("Search menu…")
                    .withCollapse();

                builder.withNavItem("Playground", "/playground", VaadinIcon.ROCKET.create())
                    .expanded(true)
                    .withSubNavItem("History",  "/playground/history")
                    .withSubNavItem("Starred",  "/playground/starred")
                    .withSubNavItem("Settings", "/playground/settings")
                    .add();
                builder.withNavItem("Models",        "/models",        VaadinIcon.CLUSTER.create()).add();
                builder.withNavItem("Documentation", "/documentation", VaadinIcon.BOOK.create()).add();
                builder.withNavItem("Settings",      "/settings",      VaadinIcon.COG.create()).add();

                var wrapper = builder.buildWrapper();
                """);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Wraps a bare SideNav in a minimal host Div so it renders with the same
     * border / background as the buildWrapper() examples.
     */
    private static Div wrap(com.vaadin.flow.component.sidenav.SideNav nav) {
        var d = new Div(nav);
        d.addClassName("sidenav-host");
        return d;
    }
}

