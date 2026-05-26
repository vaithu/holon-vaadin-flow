package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.iyensoft.vaadin.flow.components.builders.TabsBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Demo page for {@link TabsBuilder}.
 *
 * <p>TabsBuilder builds a bare {@link Tabs} selector; content wiring is the caller's
 * responsibility via {@code addSelectedChangeListener}. Use {@code LazyTabsBuilder}
 * when built-in lazy content switching is required.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic tabs — plain string labels</li>
 *   <li>Tabs with prefix icons</li>
 *   <li>Tabs with counter badges</li>
 *   <li>Manual content switching via addSelectedChangeListener</li>
 *   <li>Programmatic selection — selectedIndex &amp; selectedTab</li>
 *   <li>Equal-width tabs via flexGrowForEnclosedTabs(1.0)</li>
 *   <li>Vertical orientation with side panel</li>
 *   <li>Autoselect disabled</li>
 *   <li>i18n labels via Localizable</li>
 * </ol>
 */
@PageTitle("Tabs – Holon Demo")
@Route(value = "tabs", layout = DemoMainLayout.class)
public class TabsDemoView extends Div {

    public TabsDemoView() {
        addClassName("app-view");

        var title = new H1("TabsBuilder");

        var desc = new Paragraph(
                "TabsBuilder is a fluent API over Vaadin's Tabs component. " +
                "It produces a bare tab selector — content wiring is the caller's responsibility " +
                "via addSelectedChangeListener. Use LazyTabsBuilder for built-in lazy content " +
                "switching. This demo covers all decoration options (icons, badges), orientation, " +
                "flex-grow, programmatic selection, autoselect control, and Localizable i18n labels.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(iconsExample());
        examples.add(badgesExample());
        examples.add(contentSwitchingExample());
        examples.add(programmaticSelectionExample());
        examples.add(flexGrowExample());
        examples.add(verticalExample());
        examples.add(autoselectExample());
        examples.add(i18nExample());

        add(title, desc, examples);
    }

    // ── Example 1 ─────────────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var tabs = TabsBuilder.create()
                .withTab("Overview", "Details", "Activity", "Settings")
                .build();

        return new DemoExample("Basic — plain string labels", tabs, """
                // withTab(String...) is the fastest way to build a tab bar with plain labels.
                // The first tab is auto-selected on render (default autoselect = true).
                TabsBuilder.create()
                    .withTab("Overview", "Details", "Activity", "Settings")
                    .build();
                """);
    }

    // ── Example 2 ─────────────────────────────────────────────────────────────

    private DemoExample iconsExample() {
        var tabs = TabsBuilder.create()
                .withTab(
                    new Tab(VaadinIcon.HOME.create(),  new Span("Home")),
                    new Tab(VaadinIcon.GROUP.create(), new Span("Team")),
                    new Tab(VaadinIcon.CHART.create(), new Span("Analytics")),
                    new Tab(VaadinIcon.COG.create(),   new Span("Settings"))
                )
                .build();

        return new DemoExample("Tabs with prefix icons", tabs, """
                // For icon + label, construct a Tab manually and pass it via withTab(Tab...).
                // The icon is placed before the label text using the Tab(icon, span) constructor.
                TabsBuilder.create()
                    .withTab(
                        new Tab(VaadinIcon.HOME.create(),  new Span("Home")),
                        new Tab(VaadinIcon.GROUP.create(), new Span("Team")),
                        new Tab(VaadinIcon.CHART.create(), new Span("Analytics")),
                        new Tab(VaadinIcon.COG.create(),   new Span("Settings"))
                    )
                    .build();
                """);
    }

    // ── Example 3 ─────────────────────────────────────────────────────────────

    private DemoExample badgesExample() {
        var tabs = TabsBuilder.create()
                .withTab("Inbox",        12)
                .withTab("Sent",          0)
                .withTab("Drafts",        3)
                .withTab("All Messages", 42)
                .build();

        return new DemoExample("Tabs with counter badges", tabs, """
                // withTab(label, counter) appends a numeric badge Span to the tab header.
                // TabsBuilder is a bare selector bar — content wiring is the caller's job.
                TabsBuilder.create()
                    .withTab("Inbox",        12)
                    .withTab("Sent",          0)
                    .withTab("Drafts",        3)
                    .withTab("All Messages", 42)
                    .build();
                """);
    }

    // ── Example 4 ─────────────────────────────────────────────────────────────

    private DemoExample contentSwitchingExample() {
        var names = new String[]{"Overview", "Details", "History", "Comments"};

        var tabs = TabsBuilder.create()
                .withTab(names)
                .build();

        // Map each Tab to a content panel after building
        List<Tab> tabList = tabs.getChildren()
                .filter(Tab.class::isInstance)
                .map(Tab.class::cast)
                .toList();

        Map<Tab, Component> panels = new HashMap<>();
        var contentArea = new Div();
        for (int i = 0; i < tabList.size(); i++) {
            var panel = new Div(new Paragraph(names[i] + " panel content."));
            panel.setVisible(i == 0);
            panels.put(tabList.get(i), panel);
            contentArea.add(panel);
        }

        tabs.addSelectedChangeListener(e -> {
            panels.values().forEach(p -> p.setVisible(false));
            var selected = panels.get(e.getSelectedTab());
            if (selected != null) selected.setVisible(true);
        });

        var wrapper = new VerticalLayout(tabs, contentArea);
        wrapper.setPadding(false);
        wrapper.setSpacing(false);

        return new DemoExample("Manual content switching via addSelectedChangeListener", wrapper, """
                // TabsBuilder is a bare Tabs selector — wire content manually.
                // 1. Build the bar.
                // 2. Collect Tab instances via getChildren().
                // 3. Map each Tab to a panel and hide all but the first.
                // 4. On selection change: hide all, show the matched panel.
                var tabs = TabsBuilder.create().withTab("Overview", "Details").build();

                List<Tab> tabList = tabs.getChildren()
                    .filter(Tab.class::isInstance).map(Tab.class::cast).toList();

                Map<Tab, Component> panels = new HashMap<>();
                panels.put(tabList.get(0), overviewDiv);
                panels.put(tabList.get(1), detailsDiv);

                tabs.addSelectedChangeListener(e -> {
                    panels.values().forEach(p -> p.setVisible(false));
                    panels.get(e.getSelectedTab()).setVisible(true);
                });

                // TIP: for built-in lazy content management use LazyTabsBuilder instead.
                """);
    }

    // ── Example 5 ─────────────────────────────────────────────────────────────

    private DemoExample programmaticSelectionExample() {
        var statusLabel = new Span("Selected: Activity");

        var tabs = TabsBuilder.create()
                .withTab("Overview", "Details", "Activity", "Settings")
                .withSelectedChangeListener(e -> {
                    var t = e.getSelectedTab();
                    statusLabel.setText("Selected: " + (t != null ? t.getLabel() : "none"));
                })
                .selectedIndex(2)   // open on "Activity" (zero-based)
                .build();

        return new DemoExample("Programmatic selection — selectedIndex & selectedTab",
                new Div(tabs, statusLabel), """
                // selectedIndex(n) — zero-based; fires the listener on first render.
                // selectedTab(tab) — select by Tab reference.
                var activityTab = new Tab("Activity");

                TabsBuilder.create()
                    .withTab(new Tab("Overview"), new Tab("Details"), activityTab, new Tab("Settings"))
                    .withSelectedChangeListener(e ->
                        label.setText("Selected: " + e.getSelectedTab().getLabel()))
                    .selectedIndex(2)           // select "Activity" by index
                 // .selectedTab(activityTab)   // equivalent — select by reference
                    .build();
                """);
    }

    // ── Example 6 ─────────────────────────────────────────────────────────────

    private DemoExample flexGrowExample() {
        var tabs = TabsBuilder.create()
                .flexGrowForEnclosedTabs(1.0)
                .withTab("Short", "A Very Long Tab Label", "Medium Label", "X")
                .build();

        return new DemoExample("Equal-width tabs via flexGrowForEnclosedTabs(1.0)", tabs, """
                // flexGrowForEnclosedTabs(1.0) — every tab expands to the same width.
                // Default (0.0): each tab sizes to its label length.
                TabsBuilder.create()
                    .flexGrowForEnclosedTabs(1.0)
                    .withTab("Short", "A Very Long Tab Label", "Medium Label", "X")
                    .build();
                """);
    }

    // ── Example 7 ─────────────────────────────────────────────────────────────

    private DemoExample verticalExample() {
        var labels = new String[]{"Profile", "Security", "Notifications", "Billing"};

        var tabs = TabsBuilder.create()
                .orientation(Tabs.Orientation.VERTICAL)
                .withTab(labels)
                .build();

        // Wire tabs to content panels
        List<Tab> tabList = tabs.getChildren()
                .filter(Tab.class::isInstance).map(Tab.class::cast).toList();
        Map<Tab, Paragraph> panels = new HashMap<>();
        for (int i = 0; i < tabList.size(); i++) {
            panels.put(tabList.get(i), new Paragraph(labels[i] + " settings panel."));
        }

        var contentArea = new Div();
        if (!tabList.isEmpty()) contentArea.add(panels.get(tabList.getFirst()));

        tabs.addSelectedChangeListener(e -> {
            contentArea.removeAll();
            var panel = panels.get(e.getSelectedTab());
            if (panel != null) contentArea.add(panel);
        });

        var wrapper = new HorizontalLayout(tabs, contentArea);
        wrapper.setWidthFull();

        return new DemoExample("Vertical orientation with side panel", wrapper, """
                // orientation(Tabs.Orientation.VERTICAL) stacks tabs top-to-bottom.
                // Pair with HorizontalLayout to place tabs on the left, content on the right.
                var tabs = TabsBuilder.create()
                    .orientation(Tabs.Orientation.VERTICAL)
                    .withTab("Profile", "Security", "Notifications", "Billing")
                    .build();

                var layout = new HorizontalLayout(tabs, contentArea);
                """);
    }

    // ── Example 8 ─────────────────────────────────────────────────────────────

    private DemoExample autoselectExample() {
        var label = new Span("No tab selected yet — click one");

        var tabs = TabsBuilder.create()
                .autoselect(false)      // nothing pre-selected on render
                .withTab("Alpha", "Beta", "Gamma")
                .withSelectedChangeListener(e -> {
                    var t = e.getSelectedTab();
                    label.setText(t != null ? "Selected: " + t.getLabel() : "Deselected");
                })
                .build();

        return new DemoExample("Autoselect disabled — nothing pre-selected on render",
                new Div(tabs, label), """
                // autoselect(false) — no tab is highlighted on first render.
                // Useful in toolbars where implying a default choice is undesirable.
                // autoselect(true) is Vaadin's default behaviour.
                TabsBuilder.create()
                    .autoselect(false)
                    .withTab("Alpha", "Beta", "Gamma")
                    .withSelectedChangeListener(e ->
                        label.setText("Selected: " + e.getSelectedTab().getLabel()))
                    .build();
                """);
    }

    // ── Example 9 ─────────────────────────────────────────────────────────────

    private DemoExample i18nExample() {
        // Localizable: messageCode used when an I18NProvider is present; message() is the fallback.
        var overviewLabel  = Localizable.builder().message("Overview")
                .messageCode("tabs.demo.overview").build();
        var analyticsLabel = Localizable.builder().message("Analytics")
                .messageCode("tabs.demo.analytics").build();
        var reportsLabel   = Localizable.builder().message("Reports")
                .messageCode("tabs.demo.reports").build();
        var settingsLabel  = Localizable.builder().message("Settings")
                .messageCode("tabs.demo.settings").build();

        var tabs = TabsBuilder.create()
                .withTab(overviewLabel)
                .withTab(analyticsLabel, VaadinIcon.CHART.create())
                .withTab(reportsLabel,   5)
                .withTab(settingsLabel,  VaadinIcon.COG.create())
                .build();

        return new DemoExample("i18n labels via Localizable (messageCode + fallback)", tabs, """
                // Localizable resolves: Vaadin I18NProvider → Holon LocalizationContext
                // → falls back to message("…") when no provider is configured.
                var overviewLabel = Localizable.builder()
                    .message("Overview")               // shown when no i18n provider
                    .messageCode("tabs.demo.overview") // key for I18NProvider lookup
                    .build();

                TabsBuilder.create()
                    .withTab(overviewLabel)                              // plain i18n label
                    .withTab(analyticsLabel, VaadinIcon.CHART.create()) // i18n label + icon
                    .withTab(reportsLabel,   5)                          // i18n label + badge
                    .withTab(settingsLabel,  VaadinIcon.COG.create())   // i18n label + icon
                    .build();

                // Quick shorthand (fallback only, no message code):
                Localizable.of("Settings")
                """);
    }
}

