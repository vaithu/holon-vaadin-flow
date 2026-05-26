package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.chartjs.ChartJs;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsData;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsDataset;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Map;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link ChartJs} component.
 *
 * <p>Covers all eight built-in Chart.js types:
 * <ol>
 *   <li>Bar   – grouped multi-series vertical bars</li>
 *   <li>Line  – smooth multi-series trend lines</li>
 *   <li>Pie   – single-series proportional slices</li>
 *   <li>Doughnut – like Pie but with a hollow center</li>
 *   <li>Polar Area – radial segments with variable radius</li>
 *   <li>Radar – spider/web chart for multi-axis comparison</li>
 *   <li>Bubble – three-dimensional scatter (x, y, radius)</li>
 *   <li>Scatter – x/y correlation plot</li>
 * </ol>
 */
@PageTitle("ChartJs – Holon Demo")
@Route(value = "chartjs", layout = DemoMainLayout.class)
public class ChartJsDemoView extends Div {

    // ── Shared colour palette ────────────────────────────────────────────────
    private static final String BLUE   = "rgba(54,162,235,0.8)";
    private static final String RED    = "rgba(255,99,132,0.8)";
    private static final String GREEN  = "rgba(75,192,192,0.8)";
    private static final String YELLOW = "rgba(255,205,86,0.8)";
    private static final String PURPLE = "rgba(153,102,255,0.8)";
    private static final String ORANGE = "rgba(255,159,64,0.8)";

    private static final String BLUE_B   = "rgb(54,162,235)";
    private static final String RED_B    = "rgb(255,99,132)";
    private static final String GREEN_B  = "rgb(75,192,192)";
    private static final String YELLOW_B = "rgb(255,205,86)";
    private static final String PURPLE_B = "rgb(153,102,255)";
    private static final String ORANGE_B = "rgb(255,159,64)";

    private static final String CHART_HEIGHT = "300px";

    // ── Constructor ─────────────────────────────────────────────���────────────

    public ChartJsDemoView() {
        addClassName("app-view");

        var title = new H1("ChartJs");

        var desc = new Paragraph(
                "ChartJs wraps Chart.js as a Vaadin Flow server-side component. " +
                "Use ChartJs.builder() for the fluent builder API or pass a " +
                "ChartJsData / ChartJsConfig typed model for full control. " +
                "Chart.js itself is loaded lazily from the jsDelivr CDN on first render.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(barExample());
        examples.add(lineExample());
        examples.add(pieExample());
        examples.add(doughnutExample());
        examples.add(polarAreaExample());
        examples.add(radarExample());
        examples.add(bubbleExample());
        examples.add(scatterExample());

        add(title, desc, examples);
    }

    // ── 1. Bar ───────────────────────────────────────────────────────────────

    private DemoExample barExample() {
        var chart = ChartJs.builder()
                .type(ChartType.BAR)
                .title("Monthly Revenue by Region (€K)")
                .xAxisTitle("Month")
                .yAxisTitle("Revenue (€K)")
                .categories("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                .series(ChartJsDataset.builder()
                        .label("North")
                        .data(120, 145, 132, 160, 178, 195)
                        .color(BLUE).borderColor(BLUE_B).borderWidth(1).build())
                .series(ChartJsDataset.builder()
                        .label("South")
                        .data(85, 97, 110, 128, 142, 155)
                        .color(RED).borderColor(RED_B).borderWidth(1).build())
                .series(ChartJsDataset.builder()
                        .label("West")
                        .data(60, 72, 88, 95, 105, 118)
                        .color(GREEN).borderColor(GREEN_B).borderWidth(1).build())
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Bar Chart – grouped multi-series", chart, """
                // Fluent builder: type + categories + named series
                ChartJs.builder()
                    .type(ChartType.BAR)
                    .title("Monthly Revenue by Region (€K)")
                    .xAxisTitle("Month")
                    .yAxisTitle("Revenue (€K)")
                    .categories("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    .series(ChartJsDataset.builder()
                        .label("North")
                        .data(120, 145, 132, 160, 178, 195)
                        .color("rgba(54,162,235,0.8)")
                        .borderColor("rgb(54,162,235)")
                        .borderWidth(1).build())
                    .series(ChartJsDataset.builder()
                        .label("South")
                        .data(85, 97, 110, 128, 142, 155)
                        .color("rgba(255,99,132,0.8)").build())
                    .height("300px").width("100%")
                    .build();

                // Minimal shorthand — name + vararg values:
                ChartJs.builder()
                    .type(ChartType.BAR)
                    .categories("Q1", "Q2", "Q3", "Q4")
                    .series("Sales",  120, 145, 180, 200)
                    .series("Target", 100, 130, 160, 190)
                    .height("300px").build();
                """);
    }

    // ── 2. Line ──────────────────────────────────────────────────────────────

    private DemoExample lineExample() {
        var chart = ChartJs.builder()
                .type(ChartType.LINE)
                .title("Server Response Time (ms) – last 7 days")
                .xAxisTitle("Day")
                .yAxisTitle("ms")
                .categories("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                .series(ChartJsDataset.builder()
                        .label("API Gateway")
                        .data(42, 58, 46, 71, 55, 38, 44)
                        .color(BLUE).borderColor(BLUE_B).borderWidth(2)
                        .tension(0.4).fill(false).build())
                .series(ChartJsDataset.builder()
                        .label("Database")
                        .data(88, 102, 95, 130, 112, 78, 91)
                        .color(RED).borderColor(RED_B).borderWidth(2)
                        .tension(0.4).fill(false).build())
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Line Chart – smoothed multi-series trend", chart, """
                ChartJs.builder()
                    .type(ChartType.LINE)
                    .title("Server Response Time (ms)")
                    .categories("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    .series(ChartJsDataset.builder()
                        .label("API Gateway")
                        .data(42, 58, 46, 71, 55, 38, 44)
                        .color("rgba(54,162,235,0.8)")
                        .borderColor("rgb(54,162,235)")
                        .borderWidth(2)
                        .tension(0.4)   // Bezier smoothing: 0 = straight, 0.4 = smooth
                        .fill(false)    // no area fill under the line
                        .build())
                    .height("300px").build();
                """);
    }

    // ── 3. Pie ───────────────────────────────────────────────────────────────

    private DemoExample pieExample() {
        var data = ChartJsData.builder()
                .labels("Electronics", "Clothing", "Home & Garden", "Sports", "Books", "Other")
                .dataset(ChartJsDataset.builder()
                        .label("Sales Share")
                        .data(38, 22, 15, 12, 8, 5)
                        .backgroundColor(BLUE, RED, GREEN, YELLOW, PURPLE, ORANGE)
                        .borderColor(BLUE_B, RED_B, GREEN_B, YELLOW_B, PURPLE_B, ORANGE_B)
                        .borderWidth(1)
                        .build())
                .build();

        var chart = ChartJs.builder(ChartType.PIE, data)
                .title("Product Category Sales Share")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Pie Chart – category proportions", chart, """
                // Use ChartJsData typed model for multi-colour slices.
                ChartJsData data = ChartJsData.builder()
                    .labels("Electronics", "Clothing", "Home", "Sports", "Books")
                    .dataset(ChartJsDataset.builder()
                        .label("Sales Share")
                        .data(38, 22, 15, 12, 8)
                        // one backgroundColor per segment
                        .backgroundColor(
                            "rgba(54,162,235,0.8)",
                            "rgba(255,99,132,0.8)",
                            "rgba(75,192,192,0.8)",
                            "rgba(255,205,86,0.8)",
                            "rgba(153,102,255,0.8)")
                        .borderWidth(1)
                        .build())
                    .build();

                ChartJs.builder(ChartType.PIE, data)
                    .title("Product Category Sales Share")
                    .height("300px").build();
                """);
    }

    // ── 4. Doughnut ──────────────────────────────────────────────────────────

    private DemoExample doughnutExample() {
        var data = ChartJsData.builder()
                .labels("Engineering", "Sales", "Marketing", "Operations", "HR")
                .dataset(ChartJsDataset.builder()
                        .label("Budget Allocation")
                        .data(45, 25, 15, 10, 5)
                        .backgroundColor(BLUE, GREEN, YELLOW, ORANGE, PURPLE)
                        .borderColor(BLUE_B, GREEN_B, YELLOW_B, ORANGE_B, PURPLE_B)
                        .borderWidth(2)
                        .build())
                .build();

        var chart = ChartJs.builder(ChartType.DOUGHNUT, data)
                .title("Department Budget Allocation (%)")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Doughnut Chart – hollow-center proportions", chart, """
                // Doughnut is identical to Pie with ChartType.DOUGHNUT.
                ChartJs.builder(ChartType.DOUGHNUT, data)
                    .title("Department Budget Allocation (%)")
                    .height("300px").build();

                // Adjust the cutout radius (default 50%):
                ChartJs.builder()
                    .type(ChartType.DOUGHNUT)
                    .options(ChartJsOptions.builder()
                        .nestedProperty("cutout", "70%")  // wider ring
                        .build())
                    .data(data)
                    .height("300px").build();
                """);
    }

    // ── 5. Polar Area ────────────────────────────────────────────────────────

    private DemoExample polarAreaExample() {
        var data = ChartJsData.builder()
                .labels("Delivery Speed", "Product Quality", "Customer Support",
                        "Value for Money", "Packaging", "Returns Process")
                .dataset(ChartJsDataset.builder()
                        .label("Score")
                        .data(87, 92, 74, 85, 78, 68)
                        .backgroundColor(
                                "rgba(54,162,235,0.6)",
                                "rgba(75,192,192,0.6)",
                                "rgba(255,205,86,0.6)",
                                "rgba(153,102,255,0.6)",
                                "rgba(255,159,64,0.6)",
                                "rgba(255,99,132,0.6)")
                        .borderWidth(1)
                        .build())
                .build();

        var chart = ChartJs.builder(ChartType.POLAR_AREA, data)
                .title("Customer Experience Scores (out of 100)")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Polar Area Chart – radial comparison", chart, """
                // Polar Area: each category gets a radial segment; segment radius ∝ value.
                // Ideal for comparing several metrics of similar importance.
                ChartJs.builder(ChartType.POLAR_AREA, data)
                    .title("Customer Experience Scores (out of 100)")
                    .height("300px").build();
                """);
    }

    // ── 6. Radar ─────────────────────────────────────────────────────────────

    private DemoExample radarExample() {
        var data = ChartJsData.builder()
                .labels("Java", "Spring Boot", "SQL", "React", "DevOps", "Communication")
                .dataset(ChartJsDataset.builder()
                        .label("Candidate A")
                        .data(90, 85, 80, 60, 70, 88)
                        .backgroundColor("rgba(54,162,235,0.2)")
                        .borderColor(BLUE_B)
                        .borderWidth(2)
                        .build())
                .dataset(ChartJsDataset.builder()
                        .label("Candidate B")
                        .data(70, 72, 90, 85, 65, 75)
                        .backgroundColor("rgba(255,99,132,0.2)")
                        .borderColor(RED_B)
                        .borderWidth(2)
                        .build())
                .build();

        var chart = ChartJs.builder(ChartType.RADAR, data)
                .title("Developer Skills Comparison")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Radar Chart – multi-axis skills comparison", chart, """
                // Radar (spider/web) chart: each axis is an independent dimension.
                // Multiple datasets overlay on the same spider for direct comparison.
                ChartJsData data = ChartJsData.builder()
                    .labels("Java", "Spring Boot", "SQL", "React", "DevOps", "Communication")
                    .dataset(ChartJsDataset.builder()
                        .label("Candidate A")
                        .data(90, 85, 80, 60, 70, 88)
                        .backgroundColor("rgba(54,162,235,0.2)")
                        .borderColor("rgb(54,162,235)")
                        .borderWidth(2)
                        .build())
                    .dataset(ChartJsDataset.builder()
                        .label("Candidate B")
                        .data(70, 72, 90, 85, 65, 75)
                        .backgroundColor("rgba(255,99,132,0.2)")
                        .borderColor("rgb(255,99,132)")
                        .borderWidth(2)
                        .build())
                    .build();

                ChartJs.builder(ChartType.RADAR, data)
                    .title("Developer Skills Comparison")
                    .height("300px").build();
                """);
    }

    // ── 7. Bubble ────────────────────────────────────────────────────────────

    private DemoExample bubbleExample() {
        // Bubble data: {x = satisfaction score, y = avg order value €, r = bubble radius ∝ order count}
        var premiumDataset = ChartJsDataset.builder()
                .label("Premium Customers")
                .property("data", List.of(
                        Map.of("x", 88, "y", 320, "r", 18),
                        Map.of("x", 92, "y", 450, "r", 12),
                        Map.of("x", 76, "y", 280, "r", 22),
                        Map.of("x", 95, "y", 610, "r", 8),
                        Map.of("x", 82, "y", 390, "r", 15)
                ))
                .backgroundColor("rgba(54,162,235,0.6)")
                .borderColor(BLUE_B)
                .borderWidth(1)
                .build();

        var standardDataset = ChartJsDataset.builder()
                .label("Standard Customers")
                .property("data", List.of(
                        Map.of("x", 65, "y", 120, "r", 25),
                        Map.of("x", 72, "y", 95,  "r", 30),
                        Map.of("x", 58, "y", 140, "r", 20),
                        Map.of("x", 80, "y", 165, "r", 18),
                        Map.of("x", 45, "y", 75,  "r", 35)
                ))
                .backgroundColor("rgba(255,99,132,0.6)")
                .borderColor(RED_B)
                .borderWidth(1)
                .build();

        var data = ChartJsData.builder()
                .dataset(premiumDataset)
                .dataset(standardDataset)
                .build();

        var chart = ChartJs.builder(ChartType.BUBBLE, data)
                .title("Customer Segments: Satisfaction × Order Value × Volume")
                .xAxisTitle("Satisfaction Score")
                .yAxisTitle("Avg Order Value (€)")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Bubble Chart – three-dimensional data points", chart, """
                // Bubble chart: each point is {x, y, r} — radius encodes a 3rd dimension.
                // Pass data as List<Map<String,Object>> via the generic .property() API.
                ChartJsDataset dataset = ChartJsDataset.builder()
                    .label("Premium Customers")
                    .property("data", List.of(
                        Map.of("x", 88, "y", 320, "r", 18),   // satisfaction, avg €, radius ∝ count
                        Map.of("x", 92, "y", 450, "r", 12),
                        Map.of("x", 76, "y", 280, "r", 22)))
                    .backgroundColor("rgba(54,162,235,0.6)")
                    .borderColor("rgb(54,162,235)")
                    .borderWidth(1)
                    .build();

                ChartJs.builder(ChartType.BUBBLE, ChartJsData.builder().dataset(dataset).build())
                    .title("Customer Segments")
                    .xAxisTitle("Satisfaction Score")
                    .yAxisTitle("Avg Order Value (€)")
                    .height("300px").build();
                """);
    }

    // ── 8. Scatter ───────────────────────────────────────────────────────────

    private DemoExample scatterExample() {
        // Scatter: {x = price €, y = user rating 1–5}
        var dataset = ChartJsDataset.builder()
                .label("Products")
                .property("data", List.of(
                        Map.of("x", 9.99,   "y", 3.2),
                        Map.of("x", 19.99,  "y", 3.8),
                        Map.of("x", 34.99,  "y", 4.1),
                        Map.of("x", 49.99,  "y", 4.3),
                        Map.of("x", 79.99,  "y", 4.5),
                        Map.of("x", 99.99,  "y", 4.6),
                        Map.of("x", 149.99, "y", 4.4),
                        Map.of("x", 199.99, "y", 4.7),
                        Map.of("x", 249.99, "y", 4.8),
                        Map.of("x", 299.99, "y", 4.5),
                        Map.of("x", 399.99, "y", 4.9),
                        Map.of("x", 499.99, "y", 4.6)
                ))
                .backgroundColor(BLUE)
                .borderColor(BLUE_B)
                .borderWidth(1)
                .build();

        var data = ChartJsData.builder().dataset(dataset).build();

        var chart = ChartJs.builder(ChartType.SCATTER, data)
                .title("Price vs. User Rating Correlation")
                .xAxisTitle("Price (€)")
                .yAxisTitle("Avg Rating (1–5)")
                .height(CHART_HEIGHT).width("100%")
                .build();

        return new DemoExample("Scatter Chart – price vs. rating correlation", chart, """
                // Scatter chart: no categories — every point is an {x, y} object.
                // Use .property("data", List<Map<String,Object>>) same as Bubble (no "r").
                ChartJsDataset dataset = ChartJsDataset.builder()
                    .label("Products")
                    .property("data", List.of(
                        Map.of("x", 9.99,  "y", 3.2),
                        Map.of("x", 49.99, "y", 4.3),
                        Map.of("x", 99.99, "y", 4.6),
                        Map.of("x", 199.99,"y", 4.7)))
                    .backgroundColor("rgba(54,162,235,0.8)")
                    .build();

                ChartJs.builder(ChartType.SCATTER, ChartJsData.builder().dataset(dataset).build())
                    .title("Price vs. User Rating Correlation")
                    .xAxisTitle("Price (€)")
                    .yAxisTitle("Avg Rating (1–5)")
                    .height("300px").build();
                """);
    }
}


