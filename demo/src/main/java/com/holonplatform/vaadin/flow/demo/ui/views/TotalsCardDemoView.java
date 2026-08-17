package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the {@link TotalsCard} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic stack of default rows</li>
 *   <li>Discount / warning row variants</li>
 *   <li>Automatic positive/negative colouring from the value's leading sign</li>
 *   <li>Muted (informational) value</li>
 *   <li>Grand-total closing row</li>
 *   <li>Full account-summary composition (all variants together)</li>
 * </ol>
 */
@PageTitle("TotalsCard – Holon Demo")
@Route(value = "totals-card", layout = DemoMainLayout.class)
public class TotalsCardDemoView extends Div {

    public TotalsCardDemoView() {
        addClassName("app-view");

        var title = new H1("TotalsCard");

        var desc = new Paragraph(
                "A generic summary card made of stacked label/value rows, rendered on a light-blue " +
                "tinted surface. Each row can be plain, a discount/deduction (success colour), a " +
                "warning (amber colour), an explicit positive/negative amount (success/danger colour), " +
                "or the closing grand-total row (bold, larger, top border, primary-coloured value). " +
                "Rows created with the plain (label, value) form automatically infer the positive/negative " +
                "colour from the value's leading sign (\"+\", \"-\", \"−\", or parentheses), no variant " +
                "needed. Values can also be individually muted for purely informational rows. Fully " +
                "driven by CSS; no inline styles required.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(variantsExample());
        examples.add(signedValuesExample());
        examples.add(mutedValueExample());
        examples.add(fullSummaryExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var preview = TotalsCard.builder()
                .row("Subtotal", "€980.00")
                .row("Shipping", "€12.50")
                .row("Total", "€992.50", TotalsRow.Variant.GRAND_TOTAL)
                .build();

        return new DemoExample("Basic", preview, """
                TotalsCard.builder()
                    .row("Subtotal", "€980.00")
                    .row("Shipping", "€12.50")
                    .row("Total", "€992.50", TotalsRow.Variant.GRAND_TOTAL)
                    .build();
                """);
    }

    private DemoExample variantsExample() {
        var preview = TotalsCard.builder()
                .row("Revenue YTD", "€1,420,400")
                .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
                .row("Net revenue YTD", "€1,278,360")
                .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
                .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
                .build();

        return new DemoExample("Row variants", preview, """
                TotalsCard.builder()
                    .row("Revenue YTD", "€1,420,400")
                    .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
                    .row("Net revenue YTD", "€1,278,360")
                    .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
                    .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
                    .build();
                """);
    }

    private DemoExample signedValuesExample() {
        var preview = TotalsCard.builder()
                .row("Budget", "€50,000")
                .row("Committed spend", "-€62,400")
                .row("Rebate (accounting notation)", "(€3,150)")
                .row("Extra credit", "+€1,800")
                .row("Variance", "-€13,750", TotalsRow.Variant.GRAND_TOTAL)
                .build();

        return new DemoExample("Automatic positive/negative colouring", preview, """
                // No variant needed: the leading sign of the plain-string value
                // is enough to colour it red (negative) or green (positive).
                TotalsCard.builder()
                    .row("Budget", "€50,000")
                    .row("Committed spend", "-€62,400")             // → NEGATIVE (red)
                    .row("Rebate (accounting notation)", "(€3,150)") // → NEGATIVE (red)
                    .row("Extra credit", "+€1,800")                  // → POSITIVE (green)
                    .row("Variance", "-€13,750", TotalsRow.Variant.GRAND_TOTAL)
                    .build();
                """);
    }

    private DemoExample mutedValueExample() {
        var card = TotalsCard.builder()
                .row("Net revenue YTD", "€1,278,360")
                .row("Recurring annual fee", "+ €24,000/yr")
                .build();
        // Mute the informational (non-total) row value.
        card.getRows().get(1).setValueMuted(true);

        return new DemoExample("Muted value", card, """
                TotalsCard card = TotalsCard.builder()
                    .row("Net revenue YTD", "€1,278,360")
                    .row("Recurring annual fee", "+ €24,000/yr")
                    .build();

                // Mute an informational row's value independently of its variant
                card.getRows().get(1).setValueMuted(true);
                """);
    }

    private DemoExample fullSummaryExample() {
        var preview = TotalsCard.builder()
                .row("Revenue YTD", "€1,420,400")
                .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
                .row("Net revenue YTD", "€1,278,360")
                .row("Recurring annual fee", "+ €24,000/yr")
                .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
                .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
                .build();
        preview.getRows().get(3).setValueMuted(true);

        return new DemoExample("Full account summary", preview, """
                TotalsCard totals = TotalsCard.builder()
                    .row("Revenue YTD", "€1,420,400")
                    .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
                    .row("Net revenue YTD", "€1,278,360")
                    .row("Recurring annual fee", "+ €24,000/yr")
                    .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
                    .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
                    .build();

                // Mute the informational recurring-fee row
                totals.getRows().get(3).setValueMuted(true);
                """);
    }
}

