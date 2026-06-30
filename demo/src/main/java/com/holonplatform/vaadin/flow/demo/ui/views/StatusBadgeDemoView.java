package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.StatusBadge;
import com.holonplatform.vaadin.flow.vaadinplus.components.StatusBadge.Variant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for {@link StatusBadge}.
 *
 * <p>Covers:
 * <ol>
 *   <li>All six variants side-by-side</li>
 *   <li>Document header meta row (replicating the dh-meta design)</li>
 *   <li>List-item card usage (replicating the GR-22xx list design)</li>
 *   <li>Both size presets (SM default and MD)</li>
 *   <li>Runtime variant swap via {@code setVariant()}</li>
 * </ol>
 */
@PageTitle("StatusBadge – Holon Demo")
@Route(value = "status-badge", layout = DemoMainLayout.class)
public class StatusBadgeDemoView extends Div {

    public StatusBadgeDemoView() {
        addClassName("app-view");
        add(new H1("StatusBadge"));
        add(new Paragraph(
                "A non-interactive status pill with a coloured dot prefix and semantic tinted background. " +
                "Use StatusBadge for document states, process stages, and metadata labels. " +
                "The dot is drawn via CSS ::before using currentColor — no per-variant dot rule needed."));

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(
                allVariantsExample(),
                documentMetaRowExample(),
                listItemCardsExample(),
                sizesExample(),
                runtimeVariantExample()
        );
        add(examples);
    }

    // ── Example 1: all six variants ─────────────────────────────────���────

    private DemoExample allVariantsExample() {
        var row = new Div();
        row.addClassName("st-badge-row");
        row.add(
                Components.statusBadge("Default"),
                Components.statusBadge("Success",  Variant.SUCCESS),
                Components.statusBadge("Warning",  Variant.WARNING),
                Components.statusBadge("Danger",   Variant.DANGER),
                Components.statusBadge("Info",     Variant.INFO),
                Components.statusBadge("Violet",   Variant.VIOLET)
        );
        return new DemoExample("All Variants", row, """
                // Wrap in a div.st-badge-row for 8-px gap flex layout.
                Components.statusBadge("Default")
                Components.statusBadge("Success",  StatusBadge.Variant.SUCCESS)
                Components.statusBadge("Warning",  StatusBadge.Variant.WARNING)
                Components.statusBadge("Danger",   StatusBadge.Variant.DANGER)
                Components.statusBadge("Info",     StatusBadge.Variant.INFO)
                Components.statusBadge("Violet",   StatusBadge.Variant.VIOLET)
                """);
    }

    // ── Example 2: dh-meta document header row ───────────────────────────

    private DemoExample documentMetaRowExample() {
        var meta = new Div();
        meta.addClassName("st-badge-row");
        meta.add(
                Components.statusBadge("Posted",               Variant.SUCCESS),
                Components.statusBadge("Partial receipt",      Variant.WARNING),
                Components.statusBadge("5 of 8 lines received"),
                Components.statusBadge("3-way match: pending"),
                Components.statusBadge("ASN: NW-2281")
        );
        return new DemoExample("Document Header Meta Row (dh-meta pattern)", meta, """
                // Replicate the "dh-meta" row from the design:
                //   ● Posted              (green)
                //   ● Partial receipt     (amber)
                //   ● 5 of 8 lines …     (gray — default)
                //   ● 3-way match: …     (gray)
                //   ● ASN: NW-2281        (gray)
                Div meta = new Div();
                meta.addClassName("st-badge-row");
                meta.add(
                    Components.statusBadge("Posted",               Variant.SUCCESS),
                    Components.statusBadge("Partial receipt",      Variant.WARNING),
                    Components.statusBadge("5 of 8 lines received"),
                    Components.statusBadge("3-way match: pending"),
                    Components.statusBadge("ASN: NW-2281")
                );
                """);
    }

    // ── Example 3: list-item / card usage ────────────────────────────────

    private DemoExample listItemCardsExample() {
        record GrItem(String ref, String vendor, int lines, String statusLabel, Variant variant) {}
        var items = java.util.List.of(
                new GrItem("GR-2210", "Vesuvio Foods SRL",   3, "In progress",      Variant.INFO),
                new GrItem("GR-2209", "Helix Robotics",      2, "Quality check",    Variant.VIOLET),
                new GrItem("GR-2204", "Northwind Logistics",  8, "Posted",           Variant.SUCCESS),
                new GrItem("GR-2203", "Lumen Health",         5, "Reconciled",       Variant.SUCCESS),
                new GrItem("GR-2198", "PrahaTech",            4, "Partial · 2 issues", Variant.WARNING),
                new GrItem("GR-2195", "Atelier Moreau",       2, "Posted",           Variant.SUCCESS)
        );

        var list = new Div();
        for (var item : items) {
            var card = new Div();
            card.addClassNames("card");

            var ref    = new Span(item.ref());
            ref.addClassName("font-weight-bold");
            var vendor = new Paragraph(item.vendor() + " · " + item.lines() + " line items");
            var badge  = Components.statusBadge(item.statusLabel(), item.variant());

            card.add(ref, vendor, badge);
            list.add(card);
        }

        return new DemoExample("List Item / Card Usage (GR-22xx pattern)", list, """
                // Inside each list card:
                card.add(
                    new Strong("GR-2204"),
                    new Paragraph("Northwind Logistics · 8 line items"),
                    Components.statusBadge("Posted", StatusBadge.Variant.SUCCESS)
                );
                """);
    }

    // ── Example 4: SM vs MD sizes ────────────────────────────────────────

    private DemoExample sizesExample() {
        var row = new Div();
        row.addClassName("st-badge-row");
        row.add(
                // SM (default)
                new StatusBadge("Posted SM",         Variant.SUCCESS, StatusBadge.Size.SM),
                new StatusBadge("Partial SM",        Variant.WARNING, StatusBadge.Size.SM),
                new StatusBadge("In progress SM",    Variant.INFO,    StatusBadge.Size.SM),
                // MD
                new StatusBadge("Posted MD",         Variant.SUCCESS, StatusBadge.Size.MD),
                new StatusBadge("Partial MD",        Variant.WARNING, StatusBadge.Size.MD),
                new StatusBadge("In progress MD",    Variant.INFO,    StatusBadge.Size.MD)
        );
        return new DemoExample("Size Presets (SM default · MD raised)", row, """
                // SM = 12px font, 3px vertical padding (default — compact)
                new StatusBadge("Posted",      Variant.SUCCESS, StatusBadge.Size.SM)
                // MD = 13px font, 4px vertical padding (matches form element row)
                new StatusBadge("Posted",      Variant.SUCCESS, StatusBadge.Size.MD)
                """);
    }

    // ── Example 5: runtime variant swap ──────────────────────────────────

    private DemoExample runtimeVariantExample() {
        var badge = Components.statusBadge("Pending", Variant.INFO);

        var variants = new Variant[]{ Variant.INFO, Variant.WARNING, Variant.SUCCESS, Variant.DANGER, Variant.VIOLET, Variant.DEFAULT };
        var labels   = new String[]{ "Pending", "On hold", "Posted", "Failed", "Under review", "Archived" };
        var idx      = new int[]{ 0 };

        var cycleBtn = Components.button()
                .text("Cycle variant →")
                .secondary()
                .withClickListener(e -> {
                    idx[0] = (idx[0] + 1) % variants.length;
                    badge.setVariant(variants[idx[0]]);
                    badge.setText(labels[idx[0]]);
                })
                .build();

        var container = new Div(badge, cycleBtn);
        container.addClassNames("d-flex", "align-items-center", "gap-3");

        return new DemoExample("Runtime setVariant() — click to cycle", container, """
                StatusBadge badge = Components.statusBadge("Pending", Variant.INFO);

                // Change variant and label at runtime (e.g. after a status update):
                badge.setVariant(Variant.SUCCESS);
                badge.setText("Posted");
                """);
    }
}




