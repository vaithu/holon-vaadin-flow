package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.HeroStrip;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo page for the {@link HeroStrip} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Gradient variants (all 6 background options)</li>
 *   <li>Customer KPIs — 5 cells on INFO/blue gradient</li>
 *   <li>Contact KPIs — 5 cells on VIOLET gradient</li>
 *   <li>ValueVariant — DEFAULT, OK, ALERT tints side by side</li>
 *   <li>Pulse dot — with and without animated amber dot</li>
 *   <li>Runtime mutation — {@code setVariant()} and {@code setCells()}</li>
 * </ol>
 */
@PageTitle("HeroStrip – Holon Demo")
@Route(value = "hero-strip", layout = DemoMainLayout.class)
public class HeroStripDemoView extends Div {

    public HeroStripDemoView() {
        addClassName("app-view");

        add(new H1("HeroStrip"));
        add(new Paragraph(
                "A horizontal gradient 'hero strip' card that displays N key metric cells side by side " +
                "in equally-wide columns separated by translucent vertical dividers. " +
                "Each cell shows a small uppercase label (optionally with an animated pulse dot), " +
                "a large monospace value, and an optional sub-label. " +
                "Six gradient variants (DEFAULT, INFO, SUCCESS, WARNING, DANGER, VIOLET) and " +
                "three value colour tints (DEFAULT, OK, ALERT) are available. " +
                "All public setters return 'this' for runtime mutation after initial build."));

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(
                gradientVariantsExample(),
                customerKpisExample(),
                contactKpisExample(),
                valueVariantExample(),
                pulseDotExample(),
                responsiveExample(),
                runtimeMutationExample()
        );
        add(examples);
    }

    // ── 1. Gradient Variants ──────────────────────────────────────────────────

    private DemoExample gradientVariantsExample() {
        var preview = new Div();
        preview.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px");

        for (HeroStrip.Variant v : HeroStrip.Variant.values()) {
            preview.add(
                Components.heroStrip()
                    .variant(v)
                    .cell(c -> c.header("Metric A").content("1,234").footer("sub-label · detail"))
                    .cell(c -> c.header("Metric B").content("56%").footer("another metric"))
                    .cell(c -> c.header("Variant").content(v.name()))
                    .build()
            );
        }

        return new DemoExample("Gradient Variants", preview, """
                // The preview renders one strip per Variant value — 6 strips total.
                // The gradient background of the entire strip is controlled by .variant().

                for (HeroStrip.Variant v : HeroStrip.Variant.values()) {
                    Components.heroStrip()
                        .variant(v)
                        .cell(c -> c.header("Metric A").content("1,234").footer("sub-label · detail"))
                        .cell(c -> c.header("Metric B").content("56%").footer("another metric"))
                        .cell(c -> c.header("Variant").content(v.name()))
                        .build();
                }
                """);
    }

    // ── 2. Customer KPIs ─────────────────────────────────────────────────────

    private DemoExample customerKpisExample() {
        var strip = Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals · 80% avg prob").pulse(true))
                .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
                .cell(c -> c.header("CSAT (NPS)").content("62").footer("Q2 survey · promoter").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("Health score").content("5/5").footer("on track").valueVariant(HeroStrip.ValueVariant.OK))
                .build();

        return new DemoExample("Customer KPIs", strip, """
                Components.heroStrip()
                    .variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("Open pipeline").content("€182K")
                        .footer("4 active deals · 80% avg prob").pulse(true))
                    .cell(c -> c.header("Booked YTD").content("€624K")
                        .footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
                    .cell(c -> c.header("AR balance").content("€62,400")
                        .footer("3 open · all on-time"))
                    .cell(c -> c.header("CSAT (NPS)").content("62")
                        .footer("Q2 survey · promoter").valueVariant(HeroStrip.ValueVariant.OK))
                    .cell(c -> c.header("Health score").content("5/5")
                        .footer("on track").valueVariant(HeroStrip.ValueVariant.OK))
                    .build();
                """);
    }

    // ── 3. Contact KPIs ──────────────────────────────────────────────────────

    private DemoExample contactKpisExample() {
        var strip = Components.heroStrip()
                .variant(HeroStrip.Variant.VIOLET)
                .cell(c -> c.header("Engagement").content("82 / 100").footer("very engaged · 4 last 30d").pulse(true).valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("Authority").content("€100K").footer("co-signs > €25K"))
                .cell(c -> c.header("Last contact").content("2h").footer("call · 18 min"))
                .cell(c -> c.header("Open deals").content("€182K").footer("4 active · 80% prob"))
                .cell(c -> c.header("Time zone").content("13:42").footer("Europe/Vienna · Tue-Thu 9-12").valueVariant(HeroStrip.ValueVariant.ALERT))
                .build();

        return new DemoExample("Contact KPIs", strip, """
                Components.heroStrip()
                    .variant(HeroStrip.Variant.VIOLET)
                    .cell(c -> c.header("Engagement").content("82 / 100")
                        .footer("very engaged · 4 last 30d").pulse(true).valueVariant(HeroStrip.ValueVariant.OK))
                    .cell(c -> c.header("Authority").content("€100K")
                        .footer("co-signs > €25K"))
                    .cell(c -> c.header("Last contact").content("2h")
                        .footer("call · 18 min"))
                    .cell(c -> c.header("Open deals").content("€182K")
                        .footer("4 active · 80% prob"))
                    .cell(c -> c.header("Time zone").content("13:42")
                        .footer("Europe/Vienna · Tue-Thu 9-12").valueVariant(HeroStrip.ValueVariant.ALERT))
                    .build();
                """);
    }

    // ── 4. ValueVariant ───────────────────────────────────────────────────────

    private DemoExample valueVariantExample() {
        var preview = new Div();
        preview.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px");

        // DEFAULT — white value text
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("DEFAULT").content("€182K").footer("white — no modifier"))
                .build()
        );

        // OK — mint green
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("OK").content("€624K").footer("mint green — positive metric").valueVariant(HeroStrip.ValueVariant.OK))
                .build()
        );

        // ALERT — amber
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("ALERT").content("€62,400").footer("amber — attention needed").valueVariant(HeroStrip.ValueVariant.ALERT))
                .build()
        );

        return new DemoExample("ValueVariant — value text colour", preview, """
                // DEFAULT — white (no extra CSS class)
                Components.heroStrip().variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("DEFAULT").content("€182K").footer("white — no modifier"))
                    .build();

                // OK — mint green tint
                Components.heroStrip().variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("OK").content("€624K").footer("mint green — positive metric")
                        .valueVariant(HeroStrip.ValueVariant.OK))
                    .build();

                // ALERT — amber tint
                Components.heroStrip().variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("ALERT").content("€62,400").footer("amber — attention needed")
                        .valueVariant(HeroStrip.ValueVariant.ALERT))
                    .build();
                """);
    }

    // ── 5. Pulse dot ──────────────────────────────────────────────────────────

    private DemoExample pulseDotExample() {
        var preview = new Div();
        preview.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px");

        // Without pulse
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("No pulse").content("€182K").footer("pulse(false) — default"))
                .cell(c -> c.header("No pulse").content("€624K").footer("pulse omitted"))
                .build()
        );

        // With pulse on first cell
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("With pulse").content("€182K").footer("pulse(true) — amber dot").pulse(true))
                .cell(c -> c.header("No pulse").content("€624K").footer("pulse(false)"))
                .build()
        );

        return new DemoExample("Pulse dot", preview, """
                // Without pulse (default)
                Components.heroStrip()
                    .variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("No pulse").content("€182K").footer("pulse(false) — default"))
                    .cell(c -> c.header("No pulse").content("€624K").footer("pulse omitted"))
                    .build();

                // With pulse on the first cell — renders an animated amber dot before the header
                Components.heroStrip()
                    .variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("With pulse").content("€182K").footer("pulse(true) — amber dot").pulse(true))
                    .cell(c -> c.header("No pulse").content("€624K").footer("pulse(false)"))
                    .build();
                """);
    }

    // ── 6. Responsive ─────────────────────────────────────────────────────────

    private DemoExample responsiveExample() {
        var preview = new Div();
        preview.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "16px");

        // Non-responsive (default) — labelled
        var nonResponsiveLabel = new com.vaadin.flow.component.html.Paragraph(
                "Default (resize window to see it break on narrow screens):");
        nonResponsiveLabel.getStyle().set("font-size", "12px").set("color", "var(--lumo-secondary-text-color)").set("margin", "0");
        preview.add(nonResponsiveLabel);
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals · 80% prob").pulse(true))
                .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
                .cell(c -> c.header("CSAT (NPS)").content("62").footer("Q2 survey · promoter").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("Health score").content("5/5").footer("on track").valueVariant(HeroStrip.ValueVariant.OK))
                .build()
        );

        // Responsive — labelled
        var responsiveLabel = new com.vaadin.flow.component.html.Paragraph(
                "With .responsive() — cells wrap cleanly at tablet and mobile widths:");
        responsiveLabel.getStyle().set("font-size", "12px").set("color", "var(--lumo-secondary-text-color)").set("margin", "0");
        preview.add(responsiveLabel);
        preview.add(
            Components.heroStrip()
                .variant(HeroStrip.Variant.INFO)
                .responsive()
                .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals · 80% prob").pulse(true))
                .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
                .cell(c -> c.header("CSAT (NPS)").content("62").footer("Q2 survey · promoter").valueVariant(HeroStrip.ValueVariant.OK))
                .cell(c -> c.header("Health score").content("5/5").footer("on track").valueVariant(HeroStrip.ValueVariant.OK))
                .build()
        );

        return new DemoExample("Responsive wrapping", preview, """
                // Default — all cells in one row (grid-template-columns: repeat(N, 1fr) set inline).
                // On mobile (375px with 5 cells) each column is ~60px — content gets crushed.
                Components.heroStrip()
                    .variant(HeroStrip.Variant.INFO)
                    .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals").pulse(true))
                    // ... 4 more cells
                    .build();

                // Responsive — add .responsive() to enable auto-fit / minmax wrapping.
                // Breakpoints applied via .hstrip--responsive CSS class:
                //   > 768px  : auto-fit (typically one row, same as default)
                //   481–768px: cells wrap to 2–3 per row automatically
                //   ≤ 480px  : forced 2-column grid
                //   ≤ 320px  : forced 1-column
                Components.heroStrip()
                    .variant(HeroStrip.Variant.INFO)
                    .responsive()                    // ← the only addition
                    .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals").pulse(true))
                    // ... 4 more cells
                    .build();

                // Runtime toggle:
                strip.setResponsive(true);
                strip.isResponsive();               // true
                """);
    }

    // ── 7. Runtime Mutation ───────────────────────────────────────────────────

    private DemoExample runtimeMutationExample() {
        record Scenario(
            HeroStrip.Variant variant,
            String label1, String value1, String sub1, HeroStrip.ValueVariant vv1,
            String label2, String value2, String sub2, HeroStrip.ValueVariant vv2,
            String label3, String value3, String sub3, HeroStrip.ValueVariant vv3
        ) {}

        var scenarios = List.of(
            new Scenario(
                HeroStrip.Variant.SUCCESS,
                "Open pipeline", "€182K", "4 active · 80% prob", HeroStrip.ValueVariant.DEFAULT,
                "Booked YTD", "€624K", "14 orders · on track", HeroStrip.ValueVariant.OK,
                "Health score", "5/5", "all green", HeroStrip.ValueVariant.OK
            ),
            new Scenario(
                HeroStrip.Variant.WARNING,
                "Open pipeline", "€90K", "2 active · 60% prob", HeroStrip.ValueVariant.ALERT,
                "Booked YTD", "€312K", "7 orders · below target", HeroStrip.ValueVariant.ALERT,
                "Health score", "3/5", "needs attention", HeroStrip.ValueVariant.ALERT
            ),
            new Scenario(
                HeroStrip.Variant.DANGER,
                "Open pipeline", "€42K", "1 active · 30% prob", HeroStrip.ValueVariant.ALERT,
                "Booked YTD", "€124K", "3 orders · critical", HeroStrip.ValueVariant.ALERT,
                "Health score", "1/5", "critical — action needed", HeroStrip.ValueVariant.ALERT
            )
        );

        var s0 = scenarios.get(0);
        HeroStrip strip = Components.heroStrip()
                .variant(s0.variant())
                .cell(c -> c.header(s0.label1()).content(s0.value1()).footer(s0.sub1()).valueVariant(s0.vv1()))
                .cell(c -> c.header(s0.label2()).content(s0.value2()).footer(s0.sub2()).valueVariant(s0.vv2()))
                .cell(c -> c.header(s0.label3()).content(s0.value3()).footer(s0.sub3()).valueVariant(s0.vv3()))
                .build();

        var idx = new int[]{ 0 };

        var cycleBtn = Components.button()
                .text("Cycle scenario →")
                .secondary()
                .withClickListener(e -> {
                    idx[0] = (idx[0] + 1) % scenarios.size();
                    var s = scenarios.get(idx[0]);
                    strip.setVariant(s.variant());
                    strip.setCells(List.of(
                        new HeroStrip.Cell(s.label1(), s.value1(), s.sub1(), false, s.vv1()),
                        new HeroStrip.Cell(s.label2(), s.value2(), s.sub2(), false, s.vv2()),
                        new HeroStrip.Cell(s.label3(), s.value3(), s.sub3(), false, s.vv3())
                    ));
                })
                .build();

        var container = new Div(strip, cycleBtn);
        container.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "16px");

        return new DemoExample("Runtime Mutation — click to cycle", container, """
                // Build once — then push live data into the existing component.

                // ── Setters ──────────────────────────────────────────────────────
                strip.setVariant(HeroStrip.Variant.WARNING);
                strip.setCells(List.of(
                    new HeroStrip.Cell("Open pipeline", "€90K",  "2 active · 60% prob",    false, HeroStrip.ValueVariant.ALERT),
                    new HeroStrip.Cell("Booked YTD",    "€312K", "7 orders · below target", false, HeroStrip.ValueVariant.ALERT),
                    new HeroStrip.Cell("Health score",  "3/5",   "needs attention",         false, HeroStrip.ValueVariant.ALERT)
                ));

                // ── Getters ──────────────────────────────────────────────────────
                strip.getVariant();   // HeroStrip.Variant.WARNING
                """);
    }
}
