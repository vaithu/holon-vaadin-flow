package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.enums.ColSpan;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv.GapSize;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv.GridEntry;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo view for {@link ResponsiveDiv}.
 *
 * <p>Covers all six responsive layout patterns — use this as a quick guide
 * for choosing between {@code flex()} and {@code grid()}:
 * <ul>
 *   <li>Use <b>flex()</b> when layout flows in one direction:
 *       navbar, toolbar, hero, card body, button row</li>
 *   <li>Use <b>grid()</b> when you need fixed columns or two-dimensional control:
 *       feature tiles, asymmetric main+sidebar, KPI dashboards</li>
 * </ul>
 * <ol>
 *   <li>Stack → Inline — hero (flex, direction switch)</li>
 *   <li>1-col → N-col Grid — feature cards (grid, column count)</li>
 *   <li>Asymmetric Split — main + sidebar (grid, ColSpan)</li>
 *   <li>Responsive Gap — tight on mobile, generous on desktop</li>
 *   <li>Show / Hide — different markup per breakpoint (CSS)</li>
 *   <li>Wrap Row — form fields side-by-side, wrapping on mobile</li>
 *   <li>Scoped Chain — full toolbar breakpoint progression</li>
 *   <li>Combined Dashboard — flex shell + nested grids</li>
 * </ol>
 * Resize the browser window to observe each pattern in action.
 */
@PageTitle("ResponsiveDiv – Holon Demo")
@Route(value = "responsive-div", layout = DemoMainLayout.class)
public class ResponsiveDivDemoView extends Div {

    public ResponsiveDivDemoView() {
        addClassName("app-view");

        var title = new H1("ResponsiveDiv");

        var desc = new Paragraph(
                "Use flex() when content flows in one direction (navbar, hero, card body). " +
                "Use grid() when you need fixed columns or two-dimensional placement (tiles, splits, dashboards). " +
                "Both support mobile-first base classes and scoped breakpoint blocks. " +
                "Resize to see each pattern respond."
        );

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(stackToInlineExample());
        examples.add(responsiveGridExample());
        examples.add(asymmetricSplitExample());
        examples.add(responsiveGapExample());
        examples.add(showHideExample());
        examples.add(wrapRowExample());
        examples.add(cardSurfaceExample());
        examples.add(scopedChainExample());
        examples.add(combinedDashboardExample());

        add(title, desc, examples);
    }

    // ── Example 1: Stack → Inline ─────────────────────────────────────────────

    /**
     * A hero section: text + image stacked on mobile, side by side on desktop.
     * Shows the most common flex pattern.
     */
    private DemoExample stackToInlineExample() {
        var h3 = new H3("Build faster with Holon");
        var p  = new Paragraph("A full-stack Vaadin + Spring Boot platform with enterprise-grade " +
                "components and a fluent builder API. Stacked on mobile — side-by-side on desktop.");

        var textBlock  = DivBuilder.create().styleName("demo-rdiv-text-block").add(h3, p).build();
        var imageBlock = DivBuilder.create().styleName("demo-rdiv-image-block")
                .add(VaadinIcon.PICTURE.create(), new Span("Product screenshot")).build();

        var hero = ResponsiveDiv.flex()
                .column().gapM()
                .desktop().row().gapXL().alignCenter().end()
                .styleName("demo-rdiv-hero")
                .add(textBlock, imageBlock)
                .build();

        return new DemoExample("Stack → Inline — hero section (Pattern 1)", hero, """
                var textBlock  = DivBuilder.create().styleName("demo-rdiv-text-block").add(h3, p).build();
                var imageBlock = DivBuilder.create().styleName("demo-rdiv-image-block").add(icon, caption).build();

                var hero = ResponsiveDiv.flex()
                    .column().gapM()
                    .desktop().row().gapXL().alignCenter().end()
                    .styleName("demo-rdiv-hero")
                    .add(textBlock, imageBlock)
                    .build();

                // Emitted classes: flex flex-col gap-m lg:flex-row lg:gap-xl lg:items-center
                """);
    }

    // ── Example 2: Responsive Grid ────────────────────────────────────────────

    /**
     * Feature cards: 1 column on mobile → 2 on tablet → 3 on desktop.
     * The most common grid pattern in marketing / dashboard pages.
     */
    private DemoExample responsiveGridExample() {
        record Feature(VaadinIcon icon, String title, String text) {}
        var features = java.util.List.of(
                new Feature(VaadinIcon.ROCKET, "Blazing Fast",  "Server-push only what changed. No wasted round‑trips."),
                new Feature(VaadinIcon.SHIELD, "Secure",        "Spring Security + CSRF protection out of the box."),
                new Feature(VaadinIcon.GLOBE,  "i18n Ready",    "Full internationalisation via Holon i18n APIs."),
                new Feature(VaadinIcon.HEART,  "Accessible",    "WAI-ARIA roles and keyboard navigation built in."),
                new Feature(VaadinIcon.MAGIC,  "Themeable",     "Aura token system — one variable to rebrand."),
                new Feature(VaadinIcon.CODE,   "Open Source",   "Apache 2.0 licensed, backed by Holon Platform.")
        );

        var grid = ResponsiveDiv.grid()
                .mobile(1).tablet(2).desktop(3)
                .gapM()
                .build();

        for (var f : features) {
            var icon = f.icon().create();
            var h4 = new H4(f.title());
            var p = new Paragraph(f.text());

            grid.add(DivBuilder.create()
                    .styleName("demo-rdiv-feature-card")
                    .add(icon, h4, p)
                    .build());
        }

        return new DemoExample("1 → 2 → 3 Column Grid — feature cards (Pattern 2)", grid, """
                var grid = ResponsiveDiv.grid()
                    .mobile(1).tablet(2).desktop(3).gapM().build();

                for (var f : features) {
                    grid.add(DivBuilder.create()
                        .styleName("demo-rdiv-feature-card")
                        .add(icon, h4, paragraph)
                        .build());
                }

                // Emitted classes: grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-m
                """);
    }

    // ── Example 3: Asymmetric Split ───────────────────────────────────────────

    /**
     * An 8-col main content area + 4-col sidebar.
     * Full-width stacked on mobile; side-by-side on desktop.
     */
    private DemoExample asymmetricSplitExample() {
        var article = DivBuilder.create()
                .styleName("demo-rdiv-article")
                .add(new H3("Article Title"),
                     new Paragraph("This is the main content column. It spans the full width on mobile " +
                             "(col-span-12) and takes 8 of 12 grid columns on desktop."),
                     new Paragraph("Resize below 1024 px to see both blocks stack vertically."))
                .build();

        var link1 = new Span("→ Getting Started");    link1.addClassName("demo-rdiv-sidebar-link");
        var link2 = new Span("→ Component Overview"); link2.addClassName("demo-rdiv-sidebar-link");
        var link3 = new Span("→ Theming Guide");       link3.addClassName("demo-rdiv-sidebar-link");

        var sidebar = DivBuilder.create()
                .styleName("demo-rdiv-sidebar")
                .add(new H4("Related"),
                     new Paragraph("4 of 12 columns on desktop."),
                     new Paragraph("Full-width below 1024 px."),
                     link1, link2, link3)
                .build();

        var layout = ResponsiveDiv.grid()
                .mobile(1).desktop(12)
                .add(
                    GridEntry.of(article).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
                    GridEntry.of(sidebar).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
                )
                .gapL()
                .build();

        return new DemoExample("Asymmetric Split 8 / 4 — main + sidebar (Pattern 3)", layout, """
                var article = DivBuilder.create().styleName("demo-rdiv-article").add(h3, p1, p2).build();
                var sidebar = DivBuilder.create().styleName("demo-rdiv-sidebar").add(h4, p, link1).build();

                var layout = ResponsiveDiv.grid()
                    .mobile(1).desktop(12)
                    .add(
                        GridEntry.of(article).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
                        GridEntry.of(sidebar).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
                    )
                    .gapL().build();
                """);
    }

    // ── Example 4: Responsive Gap ─────────────────────────────────────────────

    /**
     * The same 4-box grid with a tiny gap on mobile and a generous gap on desktop.
     * Demonstrates per-breakpoint gap overrides.
     */
    private DemoExample responsiveGapExample() {
        String[] labels = {"Section A", "Section B", "Section C", "Section D"};
        String[] mods   = {"primary", "secondary", "success", "warning"};

        var grid = ResponsiveDiv.grid()
                .mobile(2).desktop(4)
                .gapXS()
                .gap(ViewMode.DESKTOP, GapSize.L)
                .build();

        for (int i = 0; i < 4; i++) {
            grid.add(DivBuilder.create()
                    .styleNames("demo-rdiv-box", "demo-rdiv-box--" + mods[i])
                    .add(new Span(labels[i]))
                    .build());
        }

        return new DemoExample("Responsive Gap — xs on mobile, l on desktop (Pattern 4)", grid, """
                var grid = ResponsiveDiv.grid()
                    .mobile(2).desktop(4)
                    .gapXS()
                    .gap(ViewMode.DESKTOP, GapSize.L)
                    .build();

                grid.add(DivBuilder.create()
                    .styleNames("demo-rdiv-box", "demo-rdiv-box--primary")
                    .add(new Span("Section A"))
                    .build());

                // Emitted classes: grid grid-cols-2 lg:grid-cols-4 gap-xs lg:gap-l
                """);
    }

    // ── Example 5: Show / Hide ────────────────────────────────────────────────

    /**
     * Shows the CSS show/hide pattern using .hidden()/.hide()/.show().
     * Both containers are in the DOM; CSS determines which is visible.
     */
    private DemoExample showHideExample() {
        var mobileNav = ResponsiveDiv.flex()
                .alignCenter().gapS()
                .hide(ViewMode.DESKTOP)
                .styleName("demo-rdiv-mobile-nav")
                .add(VaadinIcon.MENU.create(), new Span("Menu"))
                .build();

        var desktopNav = ResponsiveDiv.flex()
                .row().alignCenter().gapL().justifyBetween()
                .hidden().show(ViewMode.DESKTOP)
                .styleName("demo-rdiv-desktop-nav")
                .build();
        for (String label : new String[]{"Home", "Products", "Pricing", "Docs", "Contact"}) {
            var link = new Span(label);
            desktopNav.add(link);
        }

        var wrapper = DivBuilder.create()
                .styleName("demo-rdiv-nav-wrapper")
                .add(mobileNav, desktopNav)
                .build();

        return new DemoExample("Show / Hide at breakpoints — nav bar (Pattern 5)", wrapper, """
                var mobileNav = ResponsiveDiv.flex()
                    .alignCenter().gapS()
                    .hide(ViewMode.DESKTOP)
                    .styleName("demo-rdiv-mobile-nav")
                    .add(menuIcon, new Span("Menu"))
                    .build();

                var desktopNav = ResponsiveDiv.flex()
                    .row().alignCenter().gapL().justifyBetween()
                    .hidden().show(ViewMode.DESKTOP)
                    .styleName("demo-rdiv-desktop-nav")
                    .add(homeLink, productsLink, docsLink)
                    .build();

                var wrapper = DivBuilder.create()
                    .styleName("demo-rdiv-nav-wrapper")
                    .add(mobileNav, desktopNav)
                    .build();
                """);
    }

    // ── Example 6: Wrap Row — form fields ────────────────────────────────────

    /**
     * A filter bar: fields sit side-by-side on desktop and wrap to the next line
     * on mobile when space runs out. Shows {@code flex().row().wrap()} — the right
     * choice for "put things in a row, but let them breathe on small screens".
     *
     * <p>Why flex, not grid? The number of visible fields can vary (optional date range,
     * optional status filter). Flex wrap adapts naturally; grid would leave gaps.</p>
     */
    private DemoExample wrapRowExample() {
        record Field(String label, String placeholder) {}
        var fields = java.util.List.of(
                new Field("Search", "Product name…"),
                new Field("Category", "All categories"),
                new Field("Status", "Any status"),
                new Field("From", "Start date"),
                new Field("To", "End date")
        );

        var filterBar = ResponsiveDiv.flex()
                .row().wrap().gapS()
                .desktop().gapM().end()
                .styleName("demo-rdiv-filter-bar")
                .build();

        for (var f : fields) {
            var lbl = new Span(f.label());
            var inp = DivBuilder.create()
                    .styleName("demo-rdiv-field-input")
                    .add(new Span(f.placeholder()))
                    .build();
            var field = DivBuilder.create()
                    .styleName("demo-rdiv-field")
                    .add(lbl, inp)
                    .build();
            filterBar.add(field);
        }

        return new DemoExample("Wrap Row — form filter bar (Pattern 6)", filterBar, """
                // flex().row().wrap() → fields sit side-by-side, wrap to next line when space runs out.
                // Tighter gap on mobile (gap-s), generous gap on desktop (lg:gap-m).
                // Use flex—not grid—when the number of items is variable or content-driven.

                var filterBar = ResponsiveDiv.flex()
                    .row().wrap().gapS()
                    .desktop().gapM().end()
                    .styleName("demo-rdiv-filter-bar")
                    .build();

                for (var f : fields) {
                    filterBar.add(DivBuilder.create()
                        .styleName("demo-rdiv-field")
                        .add(label, input)
                        .build());
                }

                // Emitted classes: flex flex-row flex-wrap gap-s lg:gap-m
                """);
    }

    // ── Example 7: Card surface ───────────────────────────────────────────────

    /**
     * Shows the two card styles: bordered ({@code .card()}) and shadow-lifted
     * ({@code .elevated()}). Both are pure CSS class additions — no inline styles.
     *
     * <p>The grid itself has no card styling; only the individual tiles do.
     * This is the correct separation: the grid controls placement, the card
     * controls the visual surface.</p>
     */
    private DemoExample cardSurfaceExample() {
        record Stat(VaadinIcon icon, String value, String label, String sub) {}
        var stats = java.util.List.of(
                new Stat(VaadinIcon.DOLLAR,          "$284K",  "Revenue",   "+12% vs last month"),
                new Stat(VaadinIcon.PACKAGE,         "3 842",  "Orders",    "+5% vs last month"),
                new Stat(VaadinIcon.USER,            "18 204", "Customers", "+3% vs last month"),
                new Stat(VaadinIcon.TRENDING_UP,     "$73.98", "Avg Order", "-1% vs last month")
        );

        // Bordered cards — use for regular content tiles
        var borderedRow = ResponsiveDiv.grid()
                .mobile(1).tablet(2).desktop(4).gapM()
                .styleName("demo-rdiv-card-section")
                .build();
        for (var s : stats) {
            var icon  = s.icon().create();   icon.addClassName("demo-rdiv-stat-icon");
            var value = new Span(s.value()); value.addClassName("demo-rdiv-stat-value");
            var label = new Span(s.label()); label.addClassName("demo-rdiv-stat-label");
            var sub   = new Span(s.sub());   sub.addClassName("demo-rdiv-stat-sub");

            borderedRow.add(ResponsiveDiv.flex()
                    .column().gapXS().card()             // ← .card() = bordered surface
                    .add(icon, value, label, sub)
                    .build());
        }

        // Elevated cards — use for featured / prominent content
        var elevatedRow = ResponsiveDiv.grid()
                .mobile(1).tablet(2).desktop(4).gapM()
                .styleName("demo-rdiv-card-section")
                .build();
        for (var s : stats) {
            var icon  = s.icon().create();   icon.addClassName("demo-rdiv-stat-icon");
            var value = new Span(s.value()); value.addClassName("demo-rdiv-stat-value");
            var label = new Span(s.label()); label.addClassName("demo-rdiv-stat-label");
            var sub   = new Span(s.sub());   sub.addClassName("demo-rdiv-stat-sub");

            elevatedRow.add(ResponsiveDiv.flex()
                    .column().gapXS().elevated()         // ← .elevated() = shadow surface
                    .add(icon, value, label, sub)
                    .build());
        }

        var label1 = new Span(".card() — bordered"); label1.addClassName("demo-rdiv-card-label");
        var label2 = new Span(".elevated() — shadow lift"); label2.addClassName("demo-rdiv-card-label");

        var wrapper = ResponsiveDiv.flex()
                .column().gapL()
                .add(label1, borderedRow, label2, elevatedRow)
                .build();

        return new DemoExample("Card surface — .card() and .elevated() (Pattern 7)", wrapper, """
                // .card() → background + border + border-radius (rdiv-card class)
                ResponsiveDiv.flex().column().gapXS()
                    .card()
                    .add(icon, value, label, sub)
                    .build();

                // .elevated() → background + drop-shadow, no border (rdiv-card--elevated)
                ResponsiveDiv.flex().column().gapXS()
                    .elevated()
                    .add(icon, value, label, sub)
                    .build();

                // The grid itself is NOT a card — it only controls placement.
                // Each grid child carries its own card surface independently.
                var grid = ResponsiveDiv.grid()
                    .mobile(1).tablet(2).desktop(4).gapM()
                    .add(card1, card2, card3, card4)
                    .build();
                """);
    }

    // ── Example 8: Scoped FlexBuilder chain ──────────────────────────────────

    /**
     * Demonstrates the full scope-builder chain across three breakpoints.
     */
    private DemoExample scopedChainExample() {
        var logo = new Span("⬡ Holon");

        var searchBox = DivBuilder.create()
                .styleName("demo-rdiv-search")
                .add(VaadinIcon.SEARCH.create(), new Span("Search…"))
                .build();

        var actions = DivBuilder.create()
                .styleName("demo-rdiv-actions")
                .add(VaadinIcon.BELL.create(), VaadinIcon.USER.create())
                .build();

        var toolbar = ResponsiveDiv.flex()
                .gapS().alignCenter()
                .mobile().column().gapXS().end()
                .tablet().row().gapM().end()
                .desktop().row().gapL().justifyBetween().end()
                .styleName("demo-rdiv-toolbar")
                .add(logo, searchBox, actions)
                .build();

        return new DemoExample("Scoped chain across 3 breakpoints — toolbar (Pattern 8)", toolbar, """
                var searchBox = DivBuilder.create().styleName("demo-rdiv-search").add(searchIcon, hint).build();
                var actions   = DivBuilder.create().styleName("demo-rdiv-actions").add(bellIcon, userIcon).build();

                var toolbar = ResponsiveDiv.flex()
                    .gapS().alignCenter()
                    .mobile().column().gapXS().end()
                    .tablet().row().gapM().end()
                    .desktop().row().gapL().justifyBetween().end()
                    .styleName("demo-rdiv-toolbar")
                    .add(logo, searchBox, actions)
                    .build();
                """);
    }

    // ── Example 9: Combined dashboard ────────────────────────────────────────

    /**
     * A realistic mini-dashboard that combines everything:
     * flex shell → grid KPI row (each tile is a bordered card) →
     * grid body with asymmetric split (chart panel elevated, feed panel bordered).
     */
    private DemoExample combinedDashboardExample() {
        // ── KPI row — 4 bordered card tiles
        var kpiRow = ResponsiveDiv.grid()
                .mobile(1).tablet(2).desktop(4).gapM().build();

        record Kpi(String label, String value, String trend) {}
        for (var k : java.util.List.of(
                new Kpi("Revenue",   "$284K",  "↑ 12%"),
                new Kpi("Orders",    "3 842",  "↑ 5%"),
                new Kpi("Customers", "18 204", "↑ 3%"),
                new Kpi("Avg Order", "$73.98", "↓ 1%"))) {

            var val   = new Span(k.value());   val.addClassName("demo-rdiv-kpi-value");
            var lbl   = new Span(k.label());   lbl.addClassName("demo-rdiv-kpi-label");
            var trend = new Span(k.trend());
            trend.addClassName(k.trend().startsWith("↑") ? "demo-rdiv-kpi-trend--up" : "demo-rdiv-kpi-trend--down");

            // Each KPI tile is a flex column card
            kpiRow.add(ResponsiveDiv.flex()
                    .column().gapXS().card()
                    .add(val, lbl, trend)
                    .build());
        }

        // ── Chart panel — elevated card (prominent main content)
        var chartPlaceholder = DivBuilder.create()
                .styleName("demo-rdiv-chart-placeholder")
                .add(VaadinIcon.TRENDING_UP.create())
                .build();
        var chartPanel = ResponsiveDiv.flex()
                .column().gapS().elevated()
                .add(new H4("Revenue Trend"), chartPlaceholder)
                .build();

        // ── Feed panel — bordered card (secondary content)
        var feedPanel = ResponsiveDiv.flex()
                .column().gapXS().card()
                .build();
        feedPanel.add(new H4("Activity Feed"));
        for (String activity : new String[]{"User signed up", "Order #1842 placed", "Export completed", "Report generated"}) {
            feedPanel.add(DivBuilder.create()
                    .styleName("demo-rdiv-feed-item")
                    .add(VaadinIcon.CIRCLE.create(), new Span(activity))
                    .build());
        }

        // ── Asymmetric body grid
        var body = ResponsiveDiv.grid()
                .mobile(1).desktop(12).gapM()
                .add(
                    GridEntry.of(chartPanel).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
                    GridEntry.of(feedPanel).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
                )
                .build();

        // ── Dashboard shell
        var dashboard = ResponsiveDiv.flex()
                .column().gapM()
                .styleName("demo-rdiv-dashboard")
                .add(kpiRow, body)
                .build();

        return new DemoExample("Combined dashboard — flex shell + nested card grids (Pattern 9)", dashboard, """
                // KPI tiles: grid container, each child is a bordered .card()
                var kpiRow = ResponsiveDiv.grid().mobile(1).tablet(2).desktop(4).gapM().build();
                kpiRow.add(ResponsiveDiv.flex().column().gapXS().card().add(val, lbl, trend).build());

                // Chart panel: prominent → .elevated() (shadow, no border)
                var chartPanel = ResponsiveDiv.flex().column().gapS()
                    .elevated()
                    .add(h4, chartPlaceholder).build();

                // Feed panel: secondary → .card() (border, no shadow)
                var feedPanel = ResponsiveDiv.flex().column().gapXS()
                    .card()
                    .add(h4, feedItems).build();

                // Asymmetric body: chart 8/12 cols, feed 4/12 cols
                var body = ResponsiveDiv.grid().mobile(1).desktop(12).gapM()
                    .add(
                        GridEntry.of(chartPanel).base(COL_12).desktop(COL_8),
                        GridEntry.of(feedPanel).base(COL_12).desktop(COL_4)
                    ).build();

                // Outer shell: flex column to stack KPI row above body panels
                var dashboard = ResponsiveDiv.flex().column().gapM()
                    .styleName("demo-rdiv-dashboard").add(kpiRow, body).build();
                """);
    }
}

