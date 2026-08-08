package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the {@link AppShellLayout} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Minimal shell — brand + nav only</li>
 *   <li>Shell with search</li>
 *   <li>Shell with notifications + language selector</li>
 *   <li>Full shell — all features enabled</li>
 *   <li>Escape hatch — custom end-slot additions via {@code customizeEnd()}</li>
 * </ol>
 */
@PageTitle("AppShellLayout – Holon Demo")
@Route(value = "app-shell-layout", layout = DemoMainLayout.class)
public class AppShellLayoutDemoView extends Div {

    public AppShellLayoutDemoView() {
        addClassName("app-view");

        var title = new H1("AppShellLayout");

        var desc = new Paragraph(
                "AppShellLayout wraps Vaadin's AppLayout and pre-assembles the standard enterprise " +
                "application shell: an AppBar in the navbar with optional brand, search, notification bell, " +
                "language selector, dark/light theme toggle, and user avatar — plus an optional drawer header " +
                "and SideNav wrapper. Configure everything through the fluent builder; no boilerplate required.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(minimalExample());
        examples.add(withSearchExample());
        examples.add(withNotificationsAndLanguagesExample());
        examples.add(fullFeaturedExample());
        examples.add(customizeEndExample());

        add(title, desc, examples);
    }

    // ── Example 1: Minimal ────────────────────────────────────────────────────

    private DemoExample minimalExample() {
        var nav = SideNavBuilder.create()
                .withNavItem("Home",     "/",         VaadinIcon.HOME.create()).add()
                .withNavItem("Settings", "/settings", VaadinIcon.COG.create()).add()
                .buildWrapper();

        var shell = Components.appShell()
                .navbarBrand("My App")
                .nav(nav)
                .build();

        return new DemoExample("Minimal — brand + nav", preview(shell), """
                var nav = SideNavBuilder.create()
                        .withNavItem("Home",     "/",         VaadinIcon.HOME.create()).add()
                        .withNavItem("Settings", "/settings", VaadinIcon.COG.create()).add()
                        .buildWrapper();

                AppShellLayout shell = Components.appShell()
                        .navbarBrand("My App")
                        .nav(nav)
                        .build();
                """);
    }

    // ── Example 2: With search ────────────────────────────────────────────────

    private DemoExample withSearchExample() {
        var nav = SideNavBuilder.create()
                .withNavItem("Dashboard", "/dashboard", VaadinIcon.DASHBOARD.create()).add()
                .withNavItem("Reports",   "/reports",   VaadinIcon.CHART.create()).add()
                .buildWrapper();

        var shell = Components.appShell()
                .navbarBrand("Portal", "v2.0")
                .search("Search docs, components…")
                .nav(nav)
                .build();

        return new DemoExample("With search field", preview(shell), """
                AppShellLayout shell = Components.appShell()
                        .navbarBrand("Portal", "v2.0")
                        .search("Search docs, components…")
                        .nav(SideNavBuilder.create()
                                .withNavItem("Dashboard", "/dashboard", VaadinIcon.DASHBOARD.create()).add()
                                .withNavItem("Reports",   "/reports",   VaadinIcon.CHART.create()).add()
                                .buildWrapper())
                        .build();
                """);
    }

    // ── Example 3: Notifications + languages ─────────────────────────────────

    private DemoExample withNotificationsAndLanguagesExample() {
        var nav = SideNavBuilder.create()
                .withNavItem("Home", "/", VaadinIcon.HOME.create()).add()
                .buildWrapper();

        var shell = Components.appShell()
                .navbarBrand("Acme ERP")
                .notifications(3,
                        "🔔 New release: v25.2",
                        "✅ Build passed — 847 tests",
                        "📦 3 dependencies outdated")
                .languages(
                        "🇺🇸 English (US)",
                        "🇩🇪 Deutsch",
                        "🇫🇷 Français",
                        "🇯🇵 日本語")
                .nav(nav)
                .build();

        return new DemoExample("Notifications + language selector", preview(shell), """
                AppShellLayout shell = Components.appShell()
                        .navbarBrand("Acme ERP")
                        .notifications(3,
                                "🔔 New release: v25.2",
                                "✅ Build passed — 847 tests",
                                "📦 3 dependencies outdated")
                        .languages(
                                "🇺🇸 English (US)",
                                "🇩🇪 Deutsch",
                                "🇫🇷 Français",
                                "🇯🇵 日本語")
                        .nav(nav)
                        .build();
                """);
    }

    // ── Example 4: Full-featured ───���──────────────────────────────────────────

    private DemoExample fullFeaturedExample() {
        var drawerHeader = buildDrawerHeader("My Application");

        var nav = SideNavBuilder.create()
                .withSearch("Filter components…")
                .withCollapse()
                .withNavItem("Home",      "/",          VaadinIcon.HOME.create()).add()
                .withNavItem("Analytics", "/analytics", VaadinIcon.CHART_LINE.create()).add()
                .withNavItem("Users",     "/users",     VaadinIcon.USERS.create()).add()
                .withNavItem("Settings",  "/settings",  VaadinIcon.COG.create()).add()
                .buildWrapper();

        var shell = Components.appShell()
                .navbarBrand("My Application", "v1.0", IndexView.class)
                .search("Search…")
                .notifications(3,
                        "🔔 New release: Vaadin 25.2",
                        "✅ Build passed — 847 tests",
                        "📦 3 dependencies outdated")
                .languages("🇺🇸 English (US)", "🇩🇪 Deutsch", "🇫🇷 Français")
                .themeToggle()
                .user(u -> u
                        .name("Jane Smith")
                        .menu(m -> m
                                .item("Profile & settings")
                                .item("Switch workspace")
                                .item("Sign out")))
                .drawerBrand(drawerHeader)
                .nav(nav)
                .build();

        return new DemoExample("Full-featured — all features enabled", preview(shell), """
                AppShellLayout shell = Components.appShell()
                        .navbarBrand("My Application", "v1.0", HomeView.class)
                        .search("Search…")
                        .notifications(3,
                                "🔔 New release: Vaadin 25.2",
                                "✅ Build passed — 847 tests",
                                "📦 3 dependencies outdated")
                        .languages("🇺🇸 English (US)", "🇩🇪 Deutsch", "🇫🇷 Français")
                        .themeToggle()
                        .user(u -> u
                                .name("Jane Smith")
                                .menu(m -> m
                                        .item("Profile & settings")
                                        .item("Switch workspace")
                                        .item("Sign out")))
                        .drawerBrand(drawerBrand)
                        .nav(nav)
                        .build();
                """);
    }

    // ── Example 5: Escape hatch (customizeEnd) ────────────────────────────────

    private DemoExample customizeEndExample() {
        var nav = SideNavBuilder.create()
                .withNavItem("Home", "/", VaadinIcon.HOME.create()).add()
                .buildWrapper();

        var shell = Components.appShell()
                .navbarBrand("Custom Shell")
                .nav(nav)
                .customizeEnd(appBar -> {
                    // Add any component not covered by the builder
                    var extra = new Span("PRO");
                    extra.getStyle()
                            .set("font-size", "var(--lumo-font-size-xs)")
                            .set("font-weight", "600")
                            .set("color",       "var(--lumo-primary-color)")
                            .set("padding",     "2px 6px")
                            .set("border",      "1px solid var(--lumo-primary-color)")
                            .set("border-radius", "var(--lumo-border-radius-m)");
                    appBar.addToEnd(extra);
                })
                .build();

        return new DemoExample("Escape hatch — customizeEnd()", preview(shell), """
                AppShellLayout shell = Components.appShell()
                        .navbarBrand("Custom Shell")
                        .nav(nav)
                        .customizeEnd(appBar -> {
                            // Inject any custom component into the end slot
                            var badge = new Span("PRO");
                            badge.getStyle().set("color", "var(--lumo-primary-color)");
                            appBar.addToEnd(badge);
                        })
                        .build();
                """);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /**
     * Wraps the shell in a fixed-height container so the preview stays readable
     * inside the demo page without taking over the full viewport.
     */
    private static Div preview(AppLayout shell) {
        var wrapper = new Div(shell);
        wrapper.getStyle()
                .set("height", "320px")
                .set("overflow", "hidden")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("position", "relative");
        return wrapper;
    }

    private static VerticalLayout buildDrawerHeader(String appName) {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.setSize("48px");
        appLogo.setColor("green");

        var nameSpan = new Span(appName);
        nameSpan.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, nameSpan);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }
}
