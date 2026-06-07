package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Demo page for {@link LazyTabsBuilder} / {@link LazyTabsConfigurator}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Eager tabs – same component instance on every switch</li>
 *   <li>Lazy tabs (no cache) – Supplier called on every switch</li>
 *   <li>Lazy tabs with caching – Supplier called once, result reused</li>
 *   <li>Tab variants: icons and counter badges</li>
 *   <li>Programmatic selection + selected-change event</li>
 *   <li>Equal-width tabs via flexGrowForEnclosedTabs</li>
 *   <li>Vertical orientation via configure() with manual layout wiring</li>
 * </ol>
 */
@PageTitle("LazyTabs – Holon Demo")
@Route(value = "lazy-tabs", layout = DemoMainLayout.class)
public class LazyTabsDemoView extends Div {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public LazyTabsDemoView() {
        addClassName("app-view");

        var title = new H1("LazyTabsBuilder");

        var desc = new Paragraph(
                "LazyTabsBuilder wires a Vaadin Tabs bar to a content area. " +
                "Tabs may be eager (a fixed component instance, always reused) or lazy " +
                "(a Supplier<Component> called on demand). " +
                "An optional cache prevents re-creation on repeated tab visits. " +
                "The builder also covers icons, counter badges, orientation, " +
                "flex-grow for equal-width tabs, programmatic selection, and change events. " +
                "configure() lets you attach the configurator to an existing VerticalLayout " +
                "so you can wire tabs and content into any parent layout.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(eagerTabsExample());
        examples.add(lazyNoCacheExample());
        examples.add(lazyCacheExample());
        examples.add(iconsAndCountersExample());
        examples.add(selectionAndEventsExample());
        examples.add(flexGrowExample());
        examples.add(verticalOrientationExample());

        add(title, desc, examples);
    }

    // ── Examples ──────────────────────────────────────────────────────────────

    /**
     * Example 1 – eager tabs.
     * Each tab holds a concrete Component instance that is always shown as-is;
     * no factory is involved.
     */

private Div createContainerDiv() {
        return ResponsiveDiv.flex().column().gapM().marginS().build();
}

private Div createWrapperDiv(Tabs tabs, Div containerDiv) {
        return ResponsiveDiv.flex().column().gapM()
        .add(tabs,containerDiv)
        .build();
}


    private DemoExample eagerTabsExample() {
        Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
        .withContainer(container)
                .withEagerTab("Overview",
                        new Paragraph("Overview — this exact Paragraph instance is always shown."))
                .withEagerTab("Configuration",
                        new Paragraph("Configuration — same instance on every visit."))
                .withEagerTab("Metrics",
                        new Paragraph("Metrics — lives in memory from the moment the board is built."))
                .build();

               

        return new DemoExample("Eager tabs – same component instance on every switch", createWrapperDiv(panel,container) ,"""
                // withEagerTab(label, Component) — the Component is provided upfront.
                // The SAME object is returned on every tab activation (no factory involved).
                // Use when content is cheap to build and must survive tab switches unchanged.
                LazyTabsBuilder.create()
                    .withEagerTab("Overview",     new Paragraph("Overview content"))
                    .withEagerTab("Configuration", new Paragraph("Config content"))
                    .withEagerTab("Metrics",       new Paragraph("Metrics content"))
                    .build();
                """);
    }

    /**
     * Example 2 – lazy tabs without caching.
     * The Supplier is called on EVERY tab switch; the millisecond timestamp in each
     * panel proves a new component is created each time you revisit a tab.
     */
    private DemoExample lazyNoCacheExample() {
        Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
                .withLazyTab("Dashboard", () -> stampedPanel("Dashboard"))
                .withLazyTab("Analytics", () -> stampedPanel("Analytics"))
                .withLazyTab("Reports",   () -> stampedPanel("Reports"))
                .build();

               

        return new DemoExample(
                "Lazy tabs (no cache) – Supplier called on every switch",
                createWrapperDiv(panel,container) ,"""
                // withLazyTab(label, Supplier<Component>) — Supplier runs on EVERY activation.
                // Without .cacheEnabled() the component is re-created each visit.
                // Switch away and back: the timestamp in each panel updates on every visit.
                LazyTabsBuilder.create()
                    .withLazyTab("Dashboard", () -> buildDashboard(service.load()))
                    .withLazyTab("Analytics", () -> buildAnalyticsChart(service.load()))
                    .withLazyTab("Reports",   () -> buildReportGrid(service.findAll()))
                    .build();
                """);
    }

    /**
     * Example 3 – lazy tabs with caching.
     * The Supplier runs exactly once per tab; the timestamp stays fixed on re-visits.
     */
    private DemoExample lazyCacheExample() {
        Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
        .withContainer(container)
                .cacheEnabled()                           // factory called once; result reused
                .withLazyTab("Dashboard", () -> stampedPanel("Dashboard (cached)"))
                .withLazyTab("Analytics", () -> stampedPanel("Analytics (cached)"))
                .withLazyTab("Reports",   () -> stampedPanel("Reports (cached)"))
                .build();

               

        return new DemoExample(
                "Lazy tabs with caching – Supplier called once per tab",
                createWrapperDiv(panel,container) ,"""
                // .cacheEnabled() — Supplier runs ONCE; the result is reused on every visit.
                // Switch away and back: the timestamp stays identical — no rebuild.
                // Ideal for expensive components: Grid, Chart, heavy form, etc.
                LazyTabsBuilder.create()
                    .cacheEnabled()
                    .withLazyTab("Dashboard", () -> buildHeavyDashboard(service.load()))
                    .withLazyTab("Analytics", () -> buildAnalyticsChart(service.load()))
                    .build();

                // Conditional toggle:
                .enableCache(true)   // same as .cacheEnabled()
                .enableCache(false)  // disable (default)
                """);
    }

    /**
     * Example 4 – icons and counter badges.
     * Demonstrates every Tab-decoration overload: icon, counter badge,
     * and icon + TabVariant.
     */
    private DemoExample iconsAndCountersExample() {
        Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
        .withContainer(container)
                // Eager + icon
                .withEagerTab("Home",     VaadinIcon.HOME.create(),
                        new Paragraph("Home — eager tab with a leading icon."))
                // Eager + icon
                .withEagerTab("Team",     VaadinIcon.USERS.create(),
                        new Paragraph("Team — eager tab with a leading icon."))
                // Lazy + counter badge
                .withLazyTab("Inbox",    12,
                        () -> new Paragraph("Inbox — lazy tab, badge shows 12 unread."))
                // Lazy + icon
                .withLazyTab("Settings", VaadinIcon.COG.create(),
                        () -> new Paragraph("Settings — lazy tab with a leading icon."))
                // Lazy + counter (zero)
                .withLazyTab("Alerts",   0,
                        () -> new Paragraph("Alerts — lazy tab, badge shows 0."))
                .build();

               

        return new DemoExample("Icons and counter badges", createWrapperDiv(panel,container) ,"""
                // Icon before label (eager):
                .withEagerTab("Home",  VaadinIcon.HOME.create(),  new HomePanel())
                .withEagerTab("Team",  VaadinIcon.USERS.create(), new TeamPanel())

                // Icon before label (lazy):
                .withLazyTab("Settings", VaadinIcon.COG.create(), () -> new SettingsPanel())

                // Numeric badge counter (lazy):
                .withLazyTab("Inbox",  12, () -> new InboxPanel())
                .withLazyTab("Alerts",  0, () -> new AlertsPanel())

                // Counter + TabVariant (e.g. icon on top):
                .withLazyTab("Inbox", 5, TabVariant.LUMO_ICON_ON_TOP, () -> new InboxPanel())
                .withEagerTab("Home", VaadinIcon.HOME.create(), TabVariant.LUMO_ICON_ON_TOP,
                        new HomePanel())
                """);
    }

    /**
     * Example 5 – programmatic selection and selected-change event.
     * Starts on the third tab (index 2); a live label tracks the active tab name.
     */
    private DemoExample selectionAndEventsExample() {
        var activeLabel = new Span("Active: Reports");
Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
        .withContainer(container)
                .withLazyTab("Overview", () -> new Paragraph("Overview panel"))
                .withLazyTab("Details",  () -> new Paragraph("Details panel"))
                .withLazyTab("Reports",  () -> new Paragraph("Reports panel"))
                .withSelectedChangeListener(e -> {
                    var tab = e.getSelectedTab();
                    activeLabel.setText("Active: " + (tab != null ? tab.getLabel() : "none"));
                })
                .selectedIndex(2)           // start on "Reports"
                .build();

               

        var wrapper = new Div(createWrapperDiv(panel,container) ,activeLabel);

        return new DemoExample(
                "Programmatic selection + selected-change event",
                wrapper, """
                // withSelectedChangeListener() — listener fires on every tab switch
                // selectedIndex(n)    — select by zero-based position
                // selectedTab("label")— select by label text
                // selectedTab(tab)    — select by Tab reference
                LazyTabsBuilder.create()
                    .withLazyTab("Overview", () -> overviewPanel())
                    .withLazyTab("Details",  () -> detailsPanel())
                    .withLazyTab("Reports",  () -> reportsPanel())
                    .withSelectedChangeListener(e ->
                        statusBar.setText("Active: " + e.getSelectedTab().getLabel()))
                    .selectedIndex(2)          // open on Reports
                 // .selectedTab("Reports")   // equivalent — select by label
                 // .selectedTab(reportsTab)  // equivalent — select by Tab ref
                    .build();
                """);
    }

    /**
     * Example 6 – equal-width tabs via flexGrowForEnclosedTabs(1.0).
     * All tabs share the full tab-bar width regardless of label length.
     */
    private DemoExample flexGrowExample() {
        Div container = createContainerDiv();
        var panel = LazyTabsBuilder.create()
        .withContainer(container)
                .flexGrowForEnclosedTabs(1.0)   // every tab stretches to equal width
                .withLazyTab("Short",                 () -> new Paragraph("Short tab"))
                .withLazyTab("A Very Long Tab Label", () -> new Paragraph("Long tab"))
                .withLazyTab("Medium Tab",            () -> new Paragraph("Medium tab"))
                .build();

               

        return new DemoExample(
                "Equal-width tabs via flexGrowForEnclosedTabs(1.0)",
                createWrapperDiv(panel,container) ,"""
                // flexGrowForEnclosedTabs(1.0) — all tabs expand to share the bar equally.
                // Without this, each tab sizes itself to its label width (default).
                LazyTabsBuilder.create()
                    .flexGrowForEnclosedTabs(1.0)
                    .withLazyTab("Short",               () -> shortPanel())
                    .withLazyTab("A Very Long Label",   () -> longPanel())
                    .withLazyTab("Medium",              () -> mediumPanel())
                    .build();

                // Use 0.0 (the default) to restore natural sizing by label width.
                """);
    }

    /**
     * Example 7 – vertical orientation via configure().
     * configure(layout) attaches the configurator to an existing VerticalLayout
     * without calling build(); buildHorizontal() assembles tabs + content side-by-side.
     */
    private DemoExample verticalOrientationExample() {
        var layout = LazyTabsConfigurator.configure(new Tabs())
        .withContainer(createContainerDiv())
                .orientation(Tabs.Orientation.VERTICAL)
                .withLazyTab("Profile",     () -> new Paragraph("Profile settings"))
                .withLazyTab("Security",    () -> new Paragraph("Security settings"))
                .withLazyTab("Preferences", () -> new Paragraph("Notification preferences"))
                .withLazyTab("Billing",     () -> new Paragraph("Billing and payment details"))
                .selectedIndex(0)
                .buildHorizontal();   // tabs on the left, content on the right
        layout.setWidthFull();

        return new DemoExample(
                "Vertical orientation via orientation() with manual layout wiring",
                layout, """
                // LazyTabsConfigurator.configure(layout) — no build(), no ownership transfer.
                // buildHorizontal() places tabs on the left and content on the right.
                var layout = LazyTabsConfigurator.configure(new Tabs())
                    .orientation(Tabs.Orientation.VERTICAL)
                    .withLazyTab("Profile",     () -> profilePanel())
                    .withLazyTab("Security",    () -> securityPanel())
                    .withLazyTab("Preferences", () -> prefsPanel())
                    .withLazyTab("Billing",     () -> billingPanel())
                    .selectedIndex(0)
                    .buildHorizontal();

                // vs. tabs-on-top (default):
                LazyTabsBuilder.create()
                    .withLazyTab("Tab A", () -> panelA())
                    .build();   // returns Tabs: [Tabs]
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Creates a panel that stamps the exact time the Supplier ran.
     * Used by the lazy (no-cache) and lazy (cache) examples to visually prove
     * when content is created.
     */
    private static Div stampedPanel(String name) {
        var div = new Div();
        div.add(new Span(name + " — factory ran at: " + LocalTime.now().format(TIME_FMT)));
        return div;
    }
}

