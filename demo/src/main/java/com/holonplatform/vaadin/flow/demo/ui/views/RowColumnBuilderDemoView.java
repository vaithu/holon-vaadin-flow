package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.enums.ColSpan;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo for {@link RowBuilder} and {@link ColumnBuilder} — all {@link ColSpan} variants
 * and every {@link ViewMode} breakpoint, using {@link Highlight} KPI cards as content.
 *
 * <p>Two span APIs:
 * <ul>
 *   <li>{@code span(ColSpan)} — base span (no breakpoint), always applied</li>
 *   <li>{@code at(ViewMode, int)} — N items per row (equal-division responsive)</li>
 *   <li>{@code at(ViewMode, ColSpan)} — explicit responsive span (asymmetric layouts)</li>
 * </ul>
 */
@PageTitle("RowBuilder & ColumnBuilder – Holon Demo")
@Route(value = "row-column-builder", layout = DemoMainLayout.class)
public class RowColumnBuilderDemoView extends Div {

    public RowColumnBuilderDemoView() {
        addClassName("app-view");

        var title = new H1("RowBuilder & ColumnBuilder");

        var desc = new Paragraph(
                "Fluent builders for CSS Grid-based 12-column layouts. " +
                "RowBuilder creates a .row container; ColumnBuilder places items inside it. " +
                "Use span(ColSpan) for a fixed base span, at(ViewMode, int) for equal-division " +
                "responsive layouts, and at(ViewMode, ColSpan) for asymmetric splits. " +
                "Resize the browser to see responsive examples adapt in real time.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(colSpanReferenceExample());
        examples.add(spanCompositionsExample());
        examples.add(responsiveChainExample());
        examples.add(gridColumnsFixedExample());
        examples.add(gridColumnsResponsiveExample());
        examples.add(asymmetricLayoutExample());
        examples.add(allViewModesReferenceExample());

        add(title, desc, examples);
    }

    // ── Example 1 ────────────────────────────────────────────────────────────

    /** ColSpan.COL_1 through COL_12 — all 12 constants in one grid. */
    private DemoExample colSpanReferenceExample() {
        var wrapper = ResponsiveDiv.flex().column().gapM().marginS().build();

        for (ColSpan cs : ColSpan.values()) {
            wrapper.add(RowBuilder.create().styleName("gap-m")
                    .add(ColumnBuilder.create().span(cs).add(
                            Highlight.builder("ColSpan." + cs.name(), "col-span-" + cs.getGridSpan())
                                    .valueFontSize(Font.Size.MEDIUM)
                                    .details(new Span("spans " + cs.getGridSpan() + " / 12 columns"))
                                    .build()))
                    .build());
        }

        return new DemoExample("ColSpan Reference — COL_1 through COL_12", wrapper, """
                // span(ColSpan) — base span, always applied, no breakpoint:
                ColumnBuilder.create().span(ColSpan.COL_12)   // col-span-12  full row
                ColumnBuilder.create().span(ColSpan.COL_6)    // col-span-6   half
                ColumnBuilder.create().span(ColSpan.COL_4)    // col-span-4   one-third
                ColumnBuilder.create().span(ColSpan.COL_3)    // col-span-3   one-quarter
                ColumnBuilder.create().span(ColSpan.COL_8)    // col-span-8   two-thirds
                ColumnBuilder.create().span(ColSpan.COL_9)    // col-span-9   three-quarters
                ColumnBuilder.create().span(ColSpan.COL_2)    // col-span-2   one-sixth
                ColumnBuilder.create().span(ColSpan.COL_1)    // col-span-1   one-twelfth
                """);
    }

    // ── Example 2 ────────────────────────────────────────────────────────────

    /** Common symmetric compositions: full / halves / thirds / quarters / sixths. */
    private DemoExample spanCompositionsExample() {
        var wrapper = new Div();

        wrapper.add(RowBuilder.create().styleName("gap-m")
                .add(kpi(ColSpan.COL_12, "Full Width", "COL_12", "col-span-12"))
                .build());

        wrapper.add(RowBuilder.create().styleName("gap-m")
                .add(kpi(ColSpan.COL_6, "Left",  "COL_6", "col-span-6"),
                     kpi(ColSpan.COL_6, "Right", "COL_6", "col-span-6"))
                .build());

        wrapper.add(RowBuilder.create().styleName("gap-m")
                .add(kpi(ColSpan.COL_4, "Col 1", "COL_4", "col-span-4"),
                     kpi(ColSpan.COL_4, "Col 2", "COL_4", "col-span-4"),
                     kpi(ColSpan.COL_4, "Col 3", "COL_4", "col-span-4"))
                .build());

        wrapper.add(RowBuilder.create().styleName("gap-m")
                .add(kpi(ColSpan.COL_3, "Q1", "COL_3", "col-span-3"),
                     kpi(ColSpan.COL_3, "Q2", "COL_3", "col-span-3"),
                     kpi(ColSpan.COL_3, "Q3", "COL_3", "col-span-3"),
                     kpi(ColSpan.COL_3, "Q4", "COL_3", "col-span-3"))
                .build());

        wrapper.add(RowBuilder.create().styleName("gap-m")
                .add(kpi(ColSpan.COL_2, "1", "COL_2", "col-span-2"),
                     kpi(ColSpan.COL_2, "2", "COL_2", "col-span-2"),
                     kpi(ColSpan.COL_2, "3", "COL_2", "col-span-2"),
                     kpi(ColSpan.COL_2, "4", "COL_2", "col-span-2"),
                     kpi(ColSpan.COL_2, "5", "COL_2", "col-span-2"),
                     kpi(ColSpan.COL_2, "6", "COL_2", "col-span-2"))
                .build());

        return new DemoExample("Span Compositions — COL_12 / COL_6+6 / COL_4×3 / COL_3×4 / COL_2×6", wrapper, """
                // Full row
                ColumnBuilder.create().span(ColSpan.COL_12)

                // Two halves
                ColumnBuilder.create().span(ColSpan.COL_6)

                // Three thirds
                ColumnBuilder.create().span(ColSpan.COL_4)

                // Four quarters
                ColumnBuilder.create().span(ColSpan.COL_3)

                // Six sixths
                ColumnBuilder.create().span(ColSpan.COL_2)
                """);
    }

    // ── Example 3 ────────────────────────────────────────────────────────────

    /** Mobile-first responsive chain: 1 → 2 → 3 → 4 items per row. */
    private DemoExample responsiveChainExample() {
        var row = RowBuilder.create().styleName("gap-m")
                .add(
                    kpiResponsive("Downloads", "101.1K", "↑ 3%"),
                    kpiResponsive("Purchases", "12.2K",  "↑ 7%"),
                    kpiResponsive("Customers", "5.3K",   "↑ 2%"),
                    kpiResponsive("Channels",  "7",      "→ 0%")
                )
                .build();

        return new DemoExample(
                "Responsive Chain — 1 per row → 2 → 3 → 4  (resize to see)",
                row, """
                // at(ViewMode, int) — "N items per row"  →  span = 12 / N
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)               // base: 1 per row  (< 768 px)
                    .at(ViewMode.TABLET,        2)      // md:col-span-6  — 2 per row
                    .at(ViewMode.DESKTOP,       3)      // lg:col-span-4  — 3 per row
                    .at(ViewMode.LARGE_DESKTOP, 4)      // xl:col-span-3  — 4 per row
                    .add(card);
                """);
    }

    // ── Example 4 ────────────────────────────────────────────────────────────

    /** RowBuilder.gridColumns(int) — override the default 12-col layout. */
    private DemoExample gridColumnsFixedExample() {
        var wrapper = new Div();

        wrapper.add(labeledRow("gridColumns(2)",
                RowBuilder.create().styleName("gap-m").gridColumns(2)
                        .add(kpiNoSpan("Revenue", "$18K", "↑ 4%"),
                             kpiNoSpan("Costs",   "$14K", "↓ 2%"))
                        .build()));

        wrapper.add(labeledRow("gridColumns(3)",
                RowBuilder.create().styleName("gap-m").gridColumns(3)
                        .add(kpiNoSpan("Jan", "8,201", "↑ 2%"),
                             kpiNoSpan("Feb", "7,943", "↓ 3%"),
                             kpiNoSpan("Mar", "9,102", "↑ 14%"))
                        .build()));

        wrapper.add(labeledRow("gridColumns(4)",
                RowBuilder.create().styleName("gap-m").gridColumns(4)
                        .add(kpiNoSpan("Q1", "$84K",  "↑ 5%"),
                             kpiNoSpan("Q2", "$91K",  "↑ 8%"),
                             kpiNoSpan("Q3", "$78K",  "↓ 14%"),
                             kpiNoSpan("Q4", "$103K", "↑ 32%"))
                        .build()));

        return new DemoExample("RowBuilder gridColumns(int) — Fixed Column Count", wrapper, """
                // Equal-column grid — children need no span class.
                RowBuilder.create().gridColumns(2).styleName("gap-m")
                RowBuilder.create().gridColumns(3).styleName("gap-m")
                RowBuilder.create().gridColumns(4).styleName("gap-m")
                """);
    }

    // ── Example 5 ────────────────────────────────────────────────────────────

    /** RowBuilder.gridColumns(ViewMode, int) — responsive column count. */
    private DemoExample gridColumnsResponsiveExample() {
        var kpis = new String[][]{
            {"Revenue", "$284K", "↑ 12%"}, {"Orders",  "3,842", "↑ 5%"},
            {"Users",   "18.2K", "↑ 4%"},  {"Returns",  "2.1%", "↓ 1%"},
            {"Rating",  "4.8 ★", "↑ 0%"},  {"NPS",       "72",  "↑ 3%"},
        };

        var row = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)
                .gridColumns(ViewMode.MOBILE,  2)
                .gridColumns(ViewMode.TABLET,  3)
                .gridColumns(ViewMode.DESKTOP, 6);

        for (var kpi : kpis) {
            row.add(ColumnBuilder.create().add(
                    Highlight.builder(kpi[0], kpi[1]).valueFontSize(Font.Size.XLARGE)
                            .details(trend(kpi[2])).build()));
        }

        return new DemoExample("RowBuilder gridColumns(ViewMode, int) — Responsive Column Count",
                row.build(), """
                // 1 → 2 → 3 → 6 columns as viewport grows.
                // Children need no span class.
                RowBuilder.create()
                    .styleName("gap-m")
                    .gridColumns(1)                      // base: 1 col
                    .gridColumns(ViewMode.MOBILE,  2)    // sm:grid-cols-2
                    .gridColumns(ViewMode.TABLET,  3)    // md:grid-cols-3
                    .gridColumns(ViewMode.DESKTOP, 6)    // lg:grid-cols-6
                    .add(ColumnBuilder.create().add(card1), …)
                    .build();
                """);
    }

    // ── Example 6 ────────────────────────────────────────────────────────────

    /** Asymmetric layouts: at(ViewMode, ColSpan) for exact spans per breakpoint. */
    private DemoExample asymmetricLayoutExample() {
        // 8 + 4 split
        var main8 = ColumnBuilder.create()
                .span(ColSpan.COL_12)
                .at(ViewMode.DESKTOP, ColSpan.COL_8)
                .add(Highlight.builder("Main Content", "COL_8 at DESKTOP")
                        .valueFontSize(Font.Size.XLARGE)
                        .details(new Span("span(COL_12)  ·  at(DESKTOP, COL_8)"))
                        .build()).build();

        var side4 = ColumnBuilder.create()
                .span(ColSpan.COL_12)
                .at(ViewMode.DESKTOP, ColSpan.COL_4)
                .add(Highlight.builder("Sidebar", "COL_4 at DESKTOP")
                        .valueFontSize(Font.Size.XLARGE)
                        .details(new Span("span(COL_12)  ·  at(DESKTOP, COL_4)"))
                        .build()).build();

        // 9 + 3 split (tablet+) and 10 + 2 (desktop+)
        var main10 = ColumnBuilder.create()
                .span(ColSpan.COL_12)
                .at(ViewMode.TABLET,  ColSpan.COL_9)
                .at(ViewMode.DESKTOP, ColSpan.COL_10)
                .add(Highlight.builder("Wide Main", "COL_9→COL_10")
                        .valueFontSize(Font.Size.XLARGE)
                        .details(new Span("at(TABLET, COL_9)  ·  at(DESKTOP, COL_10)"))
                        .build()).build();

        var narrow2 = ColumnBuilder.create()
                .span(ColSpan.COL_12)
                .at(ViewMode.TABLET,  ColSpan.COL_3)
                .at(ViewMode.DESKTOP, ColSpan.COL_2)
                .add(Highlight.builder("Narrow", "COL_3→COL_2")
                        .valueFontSize(Font.Size.XLARGE)
                        .details(new Span("at(TABLET, COL_3)  ·  at(DESKTOP, COL_2)"))
                        .build()).build();

        var wrapper = new Div();
        wrapper.add(RowBuilder.create().styleName("gap-m").add(main8, side4).build());
        wrapper.add(RowBuilder.create().styleName("gap-m").add(main10, narrow2).build());

        return new DemoExample("Asymmetric Layout — at(ViewMode, ColSpan)", wrapper, """
                // at(ViewMode, ColSpan) uses the EXACT span — no 12/N division.
                // Use for unequal columns like main + sidebar.

                // 8 + 4  split:
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)                    // full-width on mobile
                    .at(ViewMode.DESKTOP, ColSpan.COL_8)     // lg:col-span-8
                    .add(mainCard);

                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.DESKTOP, ColSpan.COL_4)     // lg:col-span-4
                    .add(sidebarCard);

                // 9 + 3  →  10 + 2  across breakpoints:
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.TABLET,  ColSpan.COL_9)     // md:col-span-9
                    .at(ViewMode.DESKTOP, ColSpan.COL_10)    // lg:col-span-10
                    .add(wideContent);
                """);
    }

    // ── Example 7 ────────────────────────────────────────────────────────────

    /** All five ViewMode breakpoints as a quick-reference card row. */
    private DemoExample allViewModesReferenceExample() {
        record Bp(ViewMode mode, int minPx) {}
        var bps = new Bp[]{
            new Bp(ViewMode.MOBILE,        640),
            new Bp(ViewMode.TABLET,        768),
            new Bp(ViewMode.DESKTOP,      1024),
            new Bp(ViewMode.LARGE_DESKTOP,1280),
            new Bp(ViewMode.ULTRA_WIDE,   1536),
        };

        var row = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)
                .gridColumns(ViewMode.TABLET,        2)
                .gridColumns(ViewMode.DESKTOP,       3)
                .gridColumns(ViewMode.LARGE_DESKTOP, 5);

        for (var bp : bps) {
            row.add(ColumnBuilder.create().add(
                    Highlight.builder(bp.mode().name(),
                            "\"" + bp.mode().getPrefix() + ":\"")
                            .valueFontSize(Font.Size.MEDIUM)
                            .details(new Span("≥ " + bp.minPx() + " px"))
                            .build()));
        }

        return new DemoExample("All ViewModes Reference", row.build(), """
                // ViewMode → prefix → min-width
                // MOBILE        → "sm:"  → ≥  640 px
                // TABLET        → "md:"  → ≥  768 px
                // DESKTOP       → "lg:"  → ≥ 1024 px
                // LARGE_DESKTOP → "xl:"  → ≥ 1280 px
                // ULTRA_WIDE    → "2xl:" → ≥ 1536 px

                // Equal-division (N items per row):
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.TABLET,         2)   // md:col-span-6
                    .at(ViewMode.DESKTOP,        3)   // lg:col-span-4
                    .at(ViewMode.LARGE_DESKTOP,  4)   // xl:col-span-3
                    .at(ViewMode.ULTRA_WIDE,     6)   // 2xl:col-span-2
                    .add(card);

                // Asymmetric (exact span via ColSpan):
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.DESKTOP, ColSpan.COL_8)   // lg:col-span-8
                    .add(mainContent);
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** KPI column with a fixed ColSpan. */
    private ColumnBuilder kpi(ColSpan cs, String label, String value, String detail) {
        return ColumnBuilder.create().span(cs).add(
                Highlight.builder(label, value)
                        .valueFontSize(Font.Size.XLARGE)
                        .details(new Span(detail))
                        .build());
    }

    /** KPI column without a span class — for use inside gridColumns() rows. */
    private ColumnBuilder kpiNoSpan(String label, String value, String trendText) {
        return ColumnBuilder.create().add(
                Highlight.builder(label, value)
                        .valueFontSize(Font.Size.XLARGE)
                        .details(trend(trendText))
                        .build());
    }

    /** Full responsive chain: COL_12 → 2/row → 3/row → 4/row. */
    private ColumnBuilder kpiResponsive(String label, String value, String trendText) {
        return ColumnBuilder.create()
                .span(ColSpan.COL_12)
                .at(ViewMode.TABLET,        2)
                .at(ViewMode.DESKTOP,       3)
                .at(ViewMode.LARGE_DESKTOP, 4)
                .add(Highlight.builder(label, value)
                        .valueFontSize(Font.Size.XLARGE)
                        .details(trend(trendText))
                        .build());
    }

    private Div labeledRow(String label, Div row) {
        var section = new Div();
        var caption = new Span(label);
        section.add(caption, row);
        return section;
    }

    private Span trend(String text) {
        var s = new Span(text);
        if (text.startsWith("↑"))      s.addClassName("demo-highlight-detail--positive");
        else if (text.startsWith("↓")) s.addClassName("demo-highlight-detail--negative");
        else                           s.addClassName("demo-highlight-detail");
        return s;
    }
}

