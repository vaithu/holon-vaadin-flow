package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo page for the {@link ArAgingBar} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Card variants (border accent colour)</li>
 *   <li>Accounts-receivable aging (finance)</li>
 *   <li>Sales pipeline stages</li>
 *   <li>Project budget allocation</li>
 *   <li>HR recruitment funnel</li>
 *   <li>Minimal — two segments, no footer</li>
 *   <li>Runtime mutation — {@code setBarTitle()}, {@code setVariant()}, {@code setSegments()}</li>
 * </ol>
 */
@PageTitle("ArAgingBar – Holon Demo")
@Route(value = "ar-aging-bar", layout = DemoMainLayout.class)
public class ArAgingBarDemoView extends Div {

    public ArAgingBarDemoView() {
        addClassName("app-view");

        add(new H1("ArAgingBar"));
        add(new Paragraph(
                "A proportional segmented bar card for visualising distributions — AR aging, " +
                "pipeline stages, budget breakdowns, recruitment funnels and more. " +
                "The builder follows the PanelConfigurator nested sub-builder pattern with " +
                "Consumer<> overloads: header(), content() (with inner segment()), and footer(). " +
                "All public setters return 'this' for runtime mutation after initial build."));

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(
                variantsExample(),
                arAgingExample(),
                pipelineExample(),
                budgetExample(),
                recruitmentExample(),
                minimalExample(),
                runtimeMutationExample()
        );
        add(examples);
    }

    // ── 1. Card variants ─────────────────────────────────────────────────────

    private DemoExample variantsExample() {
        var preview = new Div();
        preview.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px");

        for (ArAgingBar.Variant v : ArAgingBar.Variant.values()) {
            preview.add(
                Components.arAgingBar()
                    .header(h -> h
                        .icon("#")
                        .title("Card variant · " + v.name())
                        .variant(v))
                    .content(c -> c
                        // key   → shown in the header legend (swatch + label)
                        // value → shown as a label centred inside the bar segment
                        .segment(s -> s.key("Part A").value("Part A · 50%").percent(50).variant(ArAgingBar.Variant.SUCCESS))
                        .segment(s -> s.key("Part B").value("Part B · 30%").percent(30).variant(ArAgingBar.Variant.INFO))
                        .segment(s -> s.key("Part C").value("Part C · 20%").percent(20).variant(ArAgingBar.Variant.WARNING)))
                    .footer(f -> f.left("Left stat").center("Centre stat").right("Right stat"))
                    .build()
            );
        }

        return new DemoExample("Card Variants", preview, """
                // The preview renders one card per Variant value — 5 cards total.
                // The card border accent + icon-badge tint both come from .header().variant().
                // Segment colours are independent and chosen per-segment in .content().
                //
                // key   → swatch + label shown in the header legend row
                // value → label centred inside the coloured bar segment

                for (ArAgingBar.Variant v : ArAgingBar.Variant.values()) {
                    Components.arAgingBar()
                        .header(h -> h
                            .icon("#")
                            .title("Card variant · " + v.name())
                            .variant(v))                      // ← card border / icon tint
                        .content(c -> c
                            .segment(s -> s
                                .key("Part A")               // ← appears in header legend
                                .value("Part A · 50%")       // ← appears inside the bar
                                .percent(50)
                                .variant(ArAgingBar.Variant.SUCCESS))
                            .segment(s -> s
                                .key("Part B").value("Part B · 30%").percent(30)
                                .variant(ArAgingBar.Variant.INFO))
                            .segment(s -> s
                                .key("Part C").value("Part C · 20%").percent(20)
                                .variant(ArAgingBar.Variant.WARNING)))
                        .footer(f -> f.left("Left stat").center("Centre stat").right("Right stat"))
                        .build();
                }
                """);
    }

    // ── 2. AR Aging (finance) ─────────────────────────────────────────────────

    private DemoExample arAgingExample() {
        var bar = Components.arAgingBar()
                .header(h -> h
                    .icon("€")
                    .title("AR aging · Helix Robotics")
                    .variant(ArAgingBar.Variant.INFO))
                .content(c -> c
                    .segment(s -> s.key("Current").value("Current · €35K").percent(56).variant(ArAgingBar.Variant.SUCCESS))
                    .segment(s -> s.key("1–30d")  .value("1-30d · €18.7K").percent(30).variant(ArAgingBar.Variant.INFO))
                    .segment(s -> s.key("31–60d") .value("31-60d · €8.7K").percent(14).variant(ArAgingBar.Variant.WARNING)))
                .footer(f -> f
                    .left("€0 owed").center("€0 overdue").right("€62.4K total open"))
                .build();

        return new DemoExample("Accounts-Receivable Aging (finance)", bar, """
                Components.arAgingBar()
                    .header(h -> h
                        .icon("€")
                        .title("AR aging · Helix Robotics")
                        .variant(ArAgingBar.Variant.INFO))
                    .content(c -> c
                        .segment(s -> s.key("Current").value("Current · €35K").percent(56).variant(Variant.SUCCESS))
                        .segment(s -> s.key("1–30d")  .value("1-30d · €18.7K").percent(30).variant(Variant.INFO))
                        .segment(s -> s.key("31–60d") .value("31-60d · €8.7K").percent(14).variant(Variant.WARNING)))
                    .footer(f -> f
                        .left("€0 owed").center("€0 overdue").right("€62.4K total open"))
                    .build();
                """);
    }

    // ── 3. Sales pipeline stages ──────────────────────────────────────────────

    private DemoExample pipelineExample() {
        var bar = Components.arAgingBar()
                .header(h -> h
                    .icon("⚡")
                    .title("Sales pipeline · Q3 2026")
                    .variant(ArAgingBar.Variant.SUCCESS))
                .content(c -> c
                    .segment(s -> s.key("Prospect")   .value("Prospect · 6")   .percent(15).variant(ArAgingBar.Variant.DEFAULT))
                    .segment(s -> s.key("Qualified")   .value("Qualified · 11") .percent(28).variant(ArAgingBar.Variant.INFO))
                    .segment(s -> s.key("Proposal")    .value("Proposal · 9")   .percent(22).variant(ArAgingBar.Variant.WARNING))
                    .segment(s -> s.key("Negotiation") .value("Negotiation · 6").percent(15).variant(ArAgingBar.Variant.DANGER))
                    .segment(s -> s.key("Closed Won")  .value("Won · 8")        .percent(20).variant(ArAgingBar.Variant.SUCCESS)))
                .footer(f -> f
                    .left("40 deals").center("€1.4M pipeline").right("72 % avg prob"))
                .build();

        return new DemoExample("Sales Pipeline Stages", bar, """
                Components.arAgingBar()
                    .header(h -> h
                        .icon("⚡")
                        .title("Sales pipeline · Q3 2026")
                        .variant(ArAgingBar.Variant.SUCCESS))
                    .content(c -> c
                        .segment(s -> s.key("Prospect")   .value("Prospect · 6")   .percent(15).variant(Variant.DEFAULT))
                        .segment(s -> s.key("Qualified")   .value("Qualified · 11") .percent(28).variant(Variant.INFO))
                        .segment(s -> s.key("Proposal")    .value("Proposal · 9")   .percent(22).variant(Variant.WARNING))
                        .segment(s -> s.key("Negotiation") .value("Negotiation · 6").percent(15).variant(Variant.DANGER))
                        .segment(s -> s.key("Closed Won")  .value("Won · 8")        .percent(20).variant(Variant.SUCCESS)))
                    .footer(f -> f
                        .left("40 deals").center("€1.4M pipeline").right("72 % avg prob"))
                    .build();
                """);
    }

    // ── 4. Project budget allocation ──────────────────────────────────────────

    private DemoExample budgetExample() {
        var bar = Components.arAgingBar()
                .header(h -> h
                    .icon("$")
                    .title("Budget · Project Titan")
                    .variant(ArAgingBar.Variant.WARNING))
                .content(c -> c
                    .segment(s -> s.key("Spent")    .value("Spent · $136K")    .percent(68).variant(ArAgingBar.Variant.DANGER))
                    .segment(s -> s.key("Committed").value("Committed · $36K") .percent(18).variant(ArAgingBar.Variant.WARNING))
                    .segment(s -> s.key("Available").value("Available · $28K") .percent(14).variant(ArAgingBar.Variant.SUCCESS)))
                .footer(f -> f
                    .left("$200K budget").center("$172K consumed").right("14 % remaining"))
                .build();

        return new DemoExample("Project Budget Allocation", bar, """
                Components.arAgingBar()
                    .header(h -> h
                        .icon("$")
                        .title("Budget · Project Titan")
                        .variant(ArAgingBar.Variant.WARNING))  // amber border = over 80% consumed
                    .content(c -> c
                        .segment(s -> s.key("Spent")    .value("Spent · $136K")    .percent(68).variant(Variant.DANGER))
                        .segment(s -> s.key("Committed").value("Committed · $36K") .percent(18).variant(Variant.WARNING))
                        .segment(s -> s.key("Available").value("Available · $28K") .percent(14).variant(Variant.SUCCESS)))
                    .footer(f -> f
                        .left("$200K budget").center("$172K consumed").right("14 % remaining"))
                    .build();
                """);
    }

    // ── 5. HR recruitment funnel ──────────────────────────────────────────────

    private DemoExample recruitmentExample() {
        var bar = Components.arAgingBar()
                .header(h -> h
                    .icon("👤")
                    .title("Hiring funnel · Senior Backend Engineer")
                    .variant(ArAgingBar.Variant.DEFAULT))
                .content(c -> c
                    .segment(s -> s.key("Applied")     .value("Applied · 80")  .percent(50).variant(ArAgingBar.Variant.DEFAULT))
                    .segment(s -> s.key("Phone screen").value("Screen · 35")   .percent(22).variant(ArAgingBar.Variant.INFO))
                    .segment(s -> s.key("Interview")   .value("Interview · 25").percent(16).variant(ArAgingBar.Variant.WARNING))
                    .segment(s -> s.key("Offer")       .value("Offer · 20")    .percent(12).variant(ArAgingBar.Variant.SUCCESS)))
                .footer(f -> f
                    .left("160 total candidates").center("62 % screen pass-rate").right("~18d avg time-to-offer"))
                .build();

        return new DemoExample("HR Recruitment Funnel", bar, """
                Components.arAgingBar()
                    .header(h -> h
                        .icon("👤")
                        .title("Hiring funnel · Senior Backend Engineer")
                        .variant(ArAgingBar.Variant.DEFAULT))
                    .content(c -> c
                        .segment(s -> s.key("Applied")     .value("Applied · 80")  .percent(50).variant(Variant.DEFAULT))
                        .segment(s -> s.key("Phone screen").value("Screen · 35")   .percent(22).variant(Variant.INFO))
                        .segment(s -> s.key("Interview")   .value("Interview · 25").percent(16).variant(Variant.WARNING))
                        .segment(s -> s.key("Offer")       .value("Offer · 20")    .percent(12).variant(Variant.SUCCESS)))
                    .footer(f -> f
                        .left("160 total candidates")
                        .center("62 % screen pass-rate")
                        .right("~18d avg time-to-offer"))
                    .build();
                """);
    }

    // ── 6. Minimal — two segments, no footer ──────────────────────────────────

    private DemoExample minimalExample() {
        var bar = Components.arAgingBar()
                .header(h -> h
                    .icon("💾")
                    .title("Storage usage")
                    .variant(ArAgingBar.Variant.DANGER))
                .content(c -> c
                    .segment(s -> s.key("Used").value("82 %").percent(82).variant(ArAgingBar.Variant.DANGER))
                    .segment(s -> s.key("Free").value("18 %").percent(18).variant(ArAgingBar.Variant.SUCCESS)))
                // no .footer() → footer area is hidden
                .build();

        return new DemoExample("Minimal — two segments, no footer", bar, """
                // Omit .footer() entirely and the footer area is not rendered.
                Components.arAgingBar()
                    .header(h -> h
                        .icon("💾")
                        .title("Storage usage")
                        .variant(ArAgingBar.Variant.DANGER))
                    .content(c -> c
                        .segment(s -> s.key("Used").value("82 %").percent(82).variant(Variant.DANGER))
                        .segment(s -> s.key("Free").value("18 %").percent(18).variant(Variant.SUCCESS)))
                    .build();
                """);
    }

    // ── 7. Runtime mutation ───────────────────────────────────────────────────

    private DemoExample runtimeMutationExample() {
        record Scenario(
            String title, ArAgingBar.Variant card,
            double p1, double p2, double p3,
            ArAgingBar.Variant c1, ArAgingBar.Variant c2, ArAgingBar.Variant c3,
            String left, String center, String right
        ) {}

        var scenarios = List.of(
            new Scenario("AR aging · On track",  ArAgingBar.Variant.SUCCESS,
                65, 25, 10, ArAgingBar.Variant.SUCCESS, ArAgingBar.Variant.INFO, ArAgingBar.Variant.WARNING,
                "€0 owed", "€0 overdue", "€58K total open"),
            new Scenario("AR aging · Attention", ArAgingBar.Variant.WARNING,
                40, 30, 30, ArAgingBar.Variant.SUCCESS, ArAgingBar.Variant.INFO, ArAgingBar.Variant.WARNING,
                "€12K owed", "€12K overdue", "€62.4K total open"),
            new Scenario("AR aging · Critical",  ArAgingBar.Variant.DANGER,
                20, 30, 50, ArAgingBar.Variant.SUCCESS, ArAgingBar.Variant.WARNING, ArAgingBar.Variant.DANGER,
                "€45K owed", "€45K overdue", "€90K total open")
        );

        var s0 = scenarios.get(0);
        ArAgingBar bar = Components.arAgingBar()
                .header(h -> h.icon("€").title(s0.title()).variant(s0.card()))
                .content(c -> c
                    .segment(seg -> seg.key("Current").value("Current · " + s0.p1() + "%").percent(s0.p1()).variant(s0.c1()))
                    .segment(seg -> seg.key("1–30d")  .value("1-30d · "   + s0.p2() + "%").percent(s0.p2()).variant(s0.c2()))
                    .segment(seg -> seg.key("31–60d") .value("31-60d · "  + s0.p3() + "%").percent(s0.p3()).variant(s0.c3())))
                .footer(f -> f.left(s0.left()).center(s0.center()).right(s0.right()))
                .build();

        var idx = new int[]{ 0 };

        var cycleBtn = Components.button()
                .text("Cycle scenario →")
                .secondary()
                .withClickListener(e -> {
                    idx[0] = (idx[0] + 1) % scenarios.size();
                    var s = scenarios.get(idx[0]);
                    bar.setBarTitle(s.title());
                    bar.setVariant(s.card());
                    bar.setSegments(List.of(
                        new ArAgingBar.Segment("Current", "Current · " + s.p1() + "%", s.p1(), s.c1()),
                        new ArAgingBar.Segment("1–30d",   "1-30d · "   + s.p2() + "%", s.p2(), s.c2()),
                        new ArAgingBar.Segment("31–60d",  "31-60d · "  + s.p3() + "%", s.p3(), s.c3())
                    ));
                    bar.setLeftStat(s.left());
                    bar.setCenterStat(s.center());
                    bar.setRightStat(s.right());
                })
                .build();

        var container = new Div(bar, cycleBtn);
        container.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "16px");

        return new DemoExample("Runtime Mutation — click to cycle", container, """
                // Build once — then push live data into the existing component.

                // ── Setters ──────────────────────────────────────────────────────
                bar.setBarTitle("AR aging · Attention");
                bar.setVariant(ArAgingBar.Variant.WARNING);
                bar.setSegments(List.of(
                    new ArAgingBar.Segment("Current", "Current · 40%", 40, Variant.SUCCESS),
                    new ArAgingBar.Segment("1–30d",   "1-30d · 30%",   30, Variant.INFO),
                    new ArAgingBar.Segment("31–60d",  "31-60d · 30%",  30, Variant.WARNING)
                ));
                bar.setLeftStat("€12K owed");
                bar.setCenterStat("€12K overdue");
                bar.setRightStat("€62.4K total open");

                // ── Getters ──────────────────────────────────────────────────────
                bar.getBarTitle();    // "AR aging · Attention"
                bar.getVariant();     // Variant.WARNING
                bar.getLeftStat();    // "€12K owed"
                bar.getCenterStat();  // "€12K overdue"
                bar.getRightStat();   // "€62.4K total open"
                """);
    }
}
