package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJs;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsDataset;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight.AccentColor;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Highlight} component.
 *
 * <p>Every example uses the fluent {@link Highlight#builder(String, String)} /
 * {@link Components#highlight(String, String)} API introduced in 10.0.0.</p>
 *
 * <p>Covers:
 * <ol>
 *   <li>Minimal (heading + value)</li>
 *   <li>With prefix / suffix</li>
 *   <li>Custom font size on value</li>
 *   <li>Heading level variants</li>
 *   <li>With details chips</li>
 *   <li>KPI dashboard grid</li>
 *   <li>KPI stat cards — value-first + accent border + icon-badge (Image 1)</li>
 *   <li>Project cards — card-header + progress bar (Image 2)</li>
 *   <li>Financial cards — accent border + inline trend metric (Image 3)</li>
 *   <li>Stock / company cards — avatar prefix + subheading + price + trend (Image 4)</li>
 *   <li>Sparkline dashboard cards — inline trend + full-bleed area chart (Image 5)</li>
 * </ol>
 */
@PageTitle("Highlight – Holon Demo")
@Route(value = "highlight", layout = DemoMainLayout.class)
public class HighlightDemoView extends Div {

    public HighlightDemoView() {
        addClassName("app-view");

        var title = new H1("Highlight");

        var desc = new Paragraph(
                "KPI / metric card with seven composable slots: cardHeader, prefix, " +
                        "heading (H1–H6 or Span), value (+ optional inline metric), details, " +
                        "suffix, and footer (progress label + bar). " +
                        "Use the fluent Highlight.builder() / Components.highlight() API — " +
                        "all methods support plain String and Holon Localizable overloads " +
                        "for full i18n, and the card carries a WAI-ARIA region role.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(minimalExample());
        examples.add(withPrefixExample());
        examples.add(valueFontSizeExample());
        examples.add(headingLevelExample());
        examples.add(withDetailsExample());
        examples.add(kpiGridExample());
        examples.add(kpiStatCardsExample());
        examples.add(projectCardsExample());
        examples.add(financialCardsExample());
        examples.add(stockCardsExample());
        examples.add(sparklineCardsExample());

        add(title, desc, examples);
    }

    // ── Example builders ─────────────────────────────────────────────────────

    private DemoExample minimalExample() {
        var preview = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        // Use the Components façade shortcut for the minimal form
        preview.add(Components.highlight("Total Revenue", "$128,430").build());
        preview.add(Components.highlight("Active Users", "4,291").build());

        return new DemoExample("Minimal (heading + value)", preview, """
                // Via Components façade:
                Components.highlight("Total Revenue", "$128,430").build();
                
                // Via static factory on Highlight:
                Highlight.builder("Active Users", "4,291").build();
                """);
    }

    private DemoExample withPrefixExample() {
        var preview = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        var revenueIcon = new Span("$");
        preview.add(Highlight.builder("Monthly Revenue", "$28,340")
                .prefix(revenueIcon)
                .build());

        preview.add(Highlight.builder("New Users", "512")
                .prefix(VaadinIcon.USER.create())
                .ariaLabel("New Users KPI")
                .build());

        preview.add(Highlight.builder("Growth Rate", "+12.5%")
                .prefix(VaadinIcon.TRENDING_UP.create())
                .suffix(VaadinIcon.ARROW_UP.create())
                .build());

        return new DemoExample("With Prefix & Suffix", preview, """
                // Prefix and suffix slots accept any Component.
                Highlight.builder("New Users", "512")
                    .prefix(VaadinIcon.USER.create())
                    .ariaLabel("New Users KPI")   // WAI-ARIA for screen readers
                    .build();
                
                // Constructor-style prefix shorthand:
                Highlight.builder("Growth Rate", "+12.5%")
                    .prefix(VaadinIcon.TRENDING_UP.create())
                    .suffix(VaadinIcon.ARROW_UP.create())
                    .build();
                """);
    }

    private DemoExample valueFontSizeExample() {
        var preview = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        for (Font.Size size : new Font.Size[]{
                Font.Size.SMALL, Font.Size.MEDIUM, Font.Size.LARGE,
                Font.Size.XLARGE, Font.Size.XXLARGE, Font.Size.XXXLARGE}) {
            preview.add(Highlight.builder("Font.Size." + size.name(), "9,999")
                    .valueFontSize(size)
                    .build());
        }

        return new DemoExample("Value Font Size", preview, """
                // valueFontSize() swaps a CSS class on the value <span>.
                Highlight.builder("Conversion Rate", "3.2%")
                    .valueFontSize(Font.Size.XXXLARGE)  // XXSMALL … XXXLARGE
                    .build();
                """);
    }

    private DemoExample headingLevelExample() {
        var preview = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        for (HeadingLevel level : HeadingLevel.values()) {
            preview.add(Highlight.builder("HeadingLevel." + level.name(), "42")
                    .headingLevel(level)
                    .build());
        }

        return new DemoExample("Heading Level", preview, """
                // headingLevel() swaps the heading element for correct DOM semantics.
                Highlight.builder("Sessions", "18,204")
                    .headingLevel(HeadingLevel.H2)  // H1 … H6 or NONE (→ Span)
                    .build();
                """);
    }

    private DemoExample withDetailsExample() {
        var preview = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        var up = new Span("↑ 8.1% vs last month");

        preview.add(Highlight.builder("Total Orders", "1,364")
                .details(up, new Span("Avg: $94.30"))
                .build());

        var down = new Span("↓ 2.4% vs last week");

        preview.add(Highlight.builder("Bounce Rate", "41.7%")
                .details(down)
                .build());

        return new DemoExample("With Details", preview, """
                // details() renders a flex-wrap row of Components below the value.
                var trend = new Span("↑ 8.1% vs last month");
                
                Highlight.builder("Total Orders", "1,364")
                    .details(trend, new Span("Avg: $94.30"))
                    .build();
                """);
    }

    private DemoExample kpiGridExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record KPI(String label, String value, String trend) {
        }
        var kpis = java.util.List.of(
                new KPI("Revenue", "$284,290", "↑ 12.4%"),
                new KPI("Orders", "3,842", "↑ 5.1%"),
                new KPI("Customers", "18,204", "↑ 3.8%"),
                new KPI("Avg. Order", "$73.98", "↓ 1.2%")
        );

        for (var kpi : kpis) {
            var trend = new Span(kpi.trend());
            trend.addClassName(kpi.trend().startsWith("↑")
                    ? "demo-highlight-detail--positive"
                    : "demo-highlight-detail--negative");

            grid.add(Components.highlight(kpi.label(), kpi.value())
                    .valueFontSize(Font.Size.XXLARGE)
                    .details(trend)
                    .build());
        }

        return new DemoExample("KPI Dashboard Grid", grid, """
                // Components.highlight() is the Components-façade shortcut.
                // Arrange the cards in a CSS grid for a dashboard row.
                
                Components.highlight("Revenue", "$284,290")
                    .valueFontSize(Font.Size.XXLARGE)
                    .details(new Span("↑ 12.4%"))
                    .build();
                
                // .kpi-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:1rem; }
                """);
    }

    // ── Card pattern examples (Images 1–5) ───────────────────────────────────

    /**
     * Image 1: KPI stat cards — value-first + accent border + icon-badge + trend
     */
    private DemoExample kpiStatCardsExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record Stat(String metric, String value, AccentColor accent,
                    Alert.Variant badge, VaadinIcon icon) {
        }
        var stats = java.util.List.of(
                new Stat("Downloads", "101.1K", AccentColor.PURPLE, Alert.Variant.INFO, VaadinIcon.DOWNLOAD),
                new Stat("Purchases", "12.2K", AccentColor.ORANGE, Alert.Variant.WARNING, VaadinIcon.CART),
                new Stat("Customers", "5.3K", AccentColor.PURPLE, Alert.Variant.SUCCESS, VaadinIcon.USERS),
                new Stat("Channels", "7", AccentColor.TEAL, Alert.Variant.INFO, VaadinIcon.DESKTOP)
        );

        for (var s : stats) {
            var trend = new Span("↑ 3%  from last month");

            grid.add(Highlight.builder(s.metric(), s.value())
                    .valueFontSize(Font.Size.XXLARGE)
                    .valueFirst()                                // big number on top
                    .accentColor(s.accent())                    // coloured left border
                    .details(trend)
                    .suffix(IconBadge.of(s.icon(), s.badge()))  // icon badge (right)
                    .ariaLabel(s.metric() + " KPI")
                    .build());
        }

        return new DemoExample("KPI Stat Cards (value-first + accent + badge)", grid, """
                // Image 1 pattern: large KPI number on top, heading below,
                // trend in details, icon-badge suffix, coloured left-border accent.
                
                var trend = new Span("↑ 3%  from last month");
                
                Highlight.builder("Downloads", "101.1K")
                    .valueFontSize(Font.Size.XXLARGE)
                    .valueFirst()                              // number above heading
                    .accentColor(AccentColor.PURPLE)           // left border stripe
                    .details(trend)
                    .suffix(IconBadge.of(VaadinIcon.DOWNLOAD, Alert.Variant.INFO))
                    .ariaLabel("Downloads KPI")                // WAI-ARIA
                    .build();
                """);
    }

    /**
     * Image 2: Project cards — card-header (icon + action) + heading/value + progress bar
     */
    private DemoExample projectCardsExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record Project(String name, int done, int total, AccentColor accent) {
        }
        var projects = java.util.List.of(
                new Project("Fresh Start Inc.", 4, 10, AccentColor.PURPLE),
                new Project("Project Outsource", 12, 30, AccentColor.PURPLE),
                new Project("Launch App", 7, 7, AccentColor.GREEN)
        );

        for (var p : projects) {
            double pct = (double) p.done() / p.total();
            boolean done = p.done() == p.total();

            var folderIcon = VaadinIcon.FOLDER.create();
            folderIcon.addClassName(done ? "demo-highlight-icon--green" : "demo-highlight-icon--purple");

            String progressText = done ? "Complete!" : ((int) (pct * 100)) + "% complete";

            grid.add(Highlight.builder(p.name(), p.done() + "/" + p.total() + " Tasks")
                    .valueFirst()
                    .accentColor(p.accent())
                    .cardHeader(folderIcon, VaadinIcon.PICTURE.create())
                    .progressLabel(progressText)
                    .progress(pct)
                    .ariaLabel(p.name() + " project card")
                    .build());
        }

        return new DemoExample("Project Cards (card-header + progress bar)", grid, """
                // Image 2 pattern: card-header row at top (icon left, action right),
                // title + subtitle body, progress label + Vaadin ProgressBar at footer.
                
                Highlight.builder("Fresh Start Inc.", "4/10 Tasks")
                    .valueFirst()
                    .accentColor(AccentColor.PURPLE)
                    .cardHeader(
                        VaadinIcon.FOLDER.create(),    // left icon slot
                        VaadinIcon.PICTURE.create()    // right action slot
                    )
                    .progressLabel("40% complete")
                    .progress(0.4)                     // 0.0 – 1.0
                    .ariaLabel("Fresh Start Inc. project card")
                    .build();
                """);
    }

    /**
     * Image 3: Financial cards — accent border + value + inline trend metric + subtext
     */
    private DemoExample financialCardsExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record Financial(String label, String value, String trend, String subtext,
                         AccentColor accent, boolean positive) {
        }
        var cards = java.util.List.of(
                new Financial("Gross Revenue", "$32,502.00", "↗ 24%", "Previous year: $46,018.00", AccentColor.PURPLE, true),
                new Financial("Refunds", "$0.00", "→ 0%", "Previous year: $0.00", AccentColor.PURPLE, false),
                new Financial("Coupons", "$10.00", "↘ 10%", "Previous year: $50.00", AccentColor.TEAL, false)
        );

        for (var f : cards) {
            var trendSpan = new Span(f.trend());
            trendSpan.addClassName(f.positive()
                    ? "demo-highlight-detail--positive"
                    : f.trend().contains("0%") ? "demo-highlight-detail--neutral"
                      : "demo-highlight-detail--negative");

            var subtext = new Span(f.subtext());

            grid.add(Highlight.builder(f.label(), f.value())
                    .valueFontSize(Font.Size.XLARGE)
                    .accentColor(f.accent())
                    .inlineMetric(trendSpan)   // trend % rendered beside the value
                    .details(subtext)
                    .build());
        }

        return new DemoExample("Financial Cards (accent + inline trend metric)", grid, """
                // Image 3 pattern: small uppercase heading, large value with
                // a trend metric inline to its right, subtext in details row.
                
                var trend = new Span("↗ 24%");
                
                Highlight.builder("Gross Revenue", "$32,502.00")
                    .valueFontSize(Font.Size.XLARGE)
                    .accentColor(AccentColor.PURPLE)
                    .inlineMetric(trend)                           // beside the value
                    .details(new Span("Previous year: $46,018.00"))
                    .build();
                """);
    }

    /**
     * Image 4: Stock / company cards — avatar prefix + subheading + price + trend
     */
    private DemoExample stockCardsExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record Stock(String name, String fullName, String price, String pct,
                     boolean up, Alert.Variant badgeVariant, VaadinIcon icon) {
        }

        var stocks = java.util.List.of(
                new Stock("Apple, Inc", "Apple, Inc", "$1,232.00", "11.01%", true, Alert.Variant.DEFAULT, VaadinIcon.CIRCLE),
                new Stock("Paypal, Inc", "Paypal, Inc", "$965.00", "9.05%", false, Alert.Variant.INFO, VaadinIcon.CREDIT_CARD),
                new Stock("Tesla, Inc", "Tesla, Inc", "$1,232.00", "11.01%", true, Alert.Variant.DESTRUCTIVE, VaadinIcon.CAR),
                new Stock("Amazon.com, Inc", "Amazon.com, Inc", "$2,567.00", "11.01%", true, Alert.Variant.WARNING, VaadinIcon.PACKAGE)
        );

        for (var s : stocks) {
            var arrow = (s.up() ? VaadinIcon.ARROW_UP : VaadinIcon.ARROW_DOWN).create();
            arrow.addClassName(s.up() ? "demo-highlight-trend-icon--positive"
                    : "demo-highlight-trend-icon--negative");

            Span trendWrapper = new Span();
            trendWrapper.add(arrow, new Span(s.pct()));
            trendWrapper.addClassName(s.up() ? "demo-highlight-detail--positive"
                    : "demo-highlight-detail--negative");

            grid.add(Highlight.builder(s.name(), s.price())
                    .subheading(s.fullName())                      // muted secondary label
                    .valueFontSize(Font.Size.XLARGE)
                    .prefix(IconBadge.of(s.icon(), s.badgeVariant()))
                    .inlineMetric(trendWrapper)                    // ↑/↓ + % beside price
                    .ariaLabel(s.name() + " stock price")
                    .build());
        }

        return new DemoExample("Stock / Company Cards (prefix avatar + subheading + trend)", grid, """
                // Image 4 pattern: circular avatar prefix, bold company name as heading,
                // muted secondary line via subheading(), price as value,
                // arrow icon + percentage as inline metric beside the price.
                
                Span trend = new Span();
                trend.add(VaadinIcon.ARROW_UP.create(), new Span("11.01%"));
                
                Highlight.builder("Apple, Inc", "$1,232.00")
                    .subheading("Apple, Inc")                     // muted secondary line
                    .valueFontSize(Font.Size.XLARGE)
                    .prefix(IconBadge.of(VaadinIcon.CIRCLE, Alert.Variant.DEFAULT))
                    .inlineMetric(trend)                          // beside the price
                    .ariaLabel("Apple, Inc stock price")
                    .build();
                """);
    }

    /**
     * Image 5: Sparkline dashboard cards — heading + value + trend + full-bleed area chart
     */
    private DemoExample sparklineCardsExample() {
        var grid = ResponsiveDiv.grid().gapM()
                .desktop(4)
                .mobile(1)
                .build();

        record KpiSpark(String label, String value, String trend, boolean up,
                        String lineColor, String fillColor, Number[] points,
                        Alert.Variant badge, VaadinIcon icon) {
        }

        var items = java.util.List.of(
                new KpiSpark("Total Revenue", "$48,295", "+12.5% vs last month", true,
                        "rgb(249,115,22)", "rgba(249,115,22,0.12)",
                        new Number[]{38, 40, 37, 43, 45, 42, 48, 51, 49, 55, 59, 62},
                        Alert.Variant.WARNING, VaadinIcon.DOLLAR),
                new KpiSpark("Active Users", "2,847", "+8.2% vs last month", true,
                        "rgb(13,148,136)", "rgba(13,148,136,0.12)",
                        new Number[]{22, 24, 21, 26, 28, 25, 29, 31, 30, 33, 35, 37},
                        Alert.Variant.INFO, VaadinIcon.GROUP),
                new KpiSpark("Total Orders", "1,432", "-3.1% vs last month", false,
                        "rgb(100,116,139)", "rgba(100,116,139,0.12)",
                        new Number[]{18, 17, 19, 16, 15, 17, 16, 14, 15, 13, 14, 12},
                        Alert.Variant.DEFAULT, VaadinIcon.CART),
                new KpiSpark("Page Views", "284K", "+24.7% vs last month", true,
                        "rgb(234,179,8)", "rgba(234,179,8,0.12)",
                        new Number[]{30, 33, 31, 36, 38, 35, 40, 44, 42, 49, 53, 58},
                        Alert.Variant.WARNING, VaadinIcon.EYE)
        );

        for (var k : items) {
            var trend = new Span(k.up() ? "↗ " + k.trend() : "↘ " + k.trend());
            trend.addClassName(k.up() ? "demo-highlight-detail--positive"
                    : "demo-highlight-detail--negative");

            var sparkline = ChartJs.builder()
                    .type(ChartType.LINE)
                    .categories("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
                    .series(ChartJsDataset.builder()
                            .data(k.points())
                            .borderColor(k.lineColor())
                            .backgroundColor(k.fillColor())
                            .borderWidth(2).tension(0.4).fill(true)
                            .property("pointRadius", 0)
                            .property("pointHoverRadius", 0)
                            .build())
                    .options(Highlight.SPARKLINE_OPTIONS)
                    .height("70px").width("100%")
                    .build();

            grid.add(Highlight.builder(k.label(), k.value())
                    .details(trend)
                    .suffix(IconBadge.of(k.icon(), k.badge()))
                    .sparkline(sparkline)                  // full-bleed chart at bottom
                    .ariaLabel(k.label() + " KPI with trend chart")
                    .build());
        }

        return new DemoExample("Sparkline Dashboard Cards (sparkline slot)", grid, """
                // Image 5 pattern: heading + value + trend details + full-bleed area
                // chart that bleeds flush to all card edges.
                //
                // Highlight.SPARKLINE_OPTIONS strips all Chart.js chrome (legend,
                // axes, grid, tooltips, point dots) leaving only the bare area line.
                
                var sparkline = ChartJs.builder()
                    .type(ChartType.LINE)
                    .categories("1","2","3","4","5","6","7","8","9","10","11","12")
                    .series(ChartJsDataset.builder()
                        .data(38, 40, 37, 43, 45, 42, 48, 51, 49, 55, 59, 62)
                        .borderColor("rgb(249,115,22)")
                        .backgroundColor("rgba(249,115,22,0.12)")
                        .borderWidth(2).tension(0.4).fill(true)
                        .property("pointRadius", 0)
                        .build())
                    .options(Highlight.SPARKLINE_OPTIONS)
                    .height("70px").width("100%")
                    .build();
                
                Highlight.builder("Total Revenue", "$48,295")
                    .details(new Span("↗ +12.5% vs last month"))
                    .suffix(IconBadge.of(VaadinIcon.DOLLAR, Alert.Variant.WARNING))
                    .sparkline(sparkline)
                    .ariaLabel("Total Revenue KPI with trend chart")
                    .build();
                """);
    }
}

