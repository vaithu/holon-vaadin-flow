package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
 *   <li>The dark "live appraisal" {@link TotalsCard.Variant#APPRAISAL} variant: eyebrow + editable
 *       highlight field, {@code TotalsGauge} ring, rows, LTV slider, term toggle and actions</li>
 *   <li>{@link TotalsCard.Variant#APPRAISAL} used for <strong>only</strong> the rows list (no
 *       header/gauge/terms/actions), including a conditionally-hidden "pending" row</li>
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
        examples.add(appraisalExample());
        examples.add(appraisalRowsOnlyExample());

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

    private static final NumberFormat USD = NumberFormat.getCurrencyInstance(Locale.US);

    private static String fmtMoney(double amount) {
        return USD.format(amount);
    }

    private static String fmtRate(double rate) {
        return String.format(Locale.US, "%.2f", rate);
    }

    private DemoExample appraisalExample() {
        // Fixed inputs driving the live recomputation below. The appraised value scales linearly
        // with the spot rate (appraisedValue = weightGrams * spotRate), so editing the "24K spot
        // rate" highlight field re-prices the whole ticket exactly like moving the LTV slider does.
        final double initialRate = 148.00;
        final double initialTotalAppraised = 2289.89;
        final double conditionAdjustment = -23.48;
        final double roundingAdjustment = 1.83;
        final double weightGrams = initialTotalAppraised / initialRate;
        final int initialLtv = 65;
        final List<String> terms = List.of("15 Days", "30 Days", "60 Days");

        // Mutable current state closed over by every callback below.
        double[] currentRate = { initialRate };
        int[] currentLtv = { initialLtv };
        TotalsCard[] cardHolder = new TotalsCard[1];

        // Keep direct references to every row/gauge value that must change reactively.
        TotalsRow totalAppraisedRow = new TotalsRow("Total appraised value", fmtMoney(initialTotalAppraised));
        TotalsRow netAppraisedRow = new TotalsRow("Net appraised value",
                fmtMoney(initialTotalAppraised + conditionAdjustment));
        TotalsRow ltvRow = new TotalsRow("Loan-to-value (" + initialLtv + "%)",
                fmtMoney((initialTotalAppraised + conditionAdjustment) * initialLtv / 100.0));
        TotalsRow loanOfferRow = new TotalsRow("Loan offer",
                fmtMoney((initialTotalAppraised + conditionAdjustment) * initialLtv / 100.0 + roundingAdjustment),
                TotalsRow.Variant.GRAND_TOTAL);

        // Single recompute routine shared by the spot-rate field, the LTV slider and the reset action,
        // so every trigger keeps the rows AND the gauge perfectly in sync.
        Runnable recompute = () -> {
            double appraised = weightGrams * currentRate[0];
            double net = appraised + conditionAdjustment;
            double ltvAmount = net * currentLtv[0] / 100.0;
            double offer = ltvAmount + roundingAdjustment;

            totalAppraisedRow.setValue(fmtMoney(appraised));
            netAppraisedRow.setValue(fmtMoney(net));
            ltvRow.setLabel("Loan-to-value (" + currentLtv[0] + "%)");
            ltvRow.setValue(fmtMoney(ltvAmount));
            loanOfferRow.setValue(fmtMoney(offer));

            TotalsCard card = cardHolder[0];
            if (card != null && card.getGauge() != null) {
                card.getGauge().setPercent(currentLtv[0])
                        .setAmount(fmtMoney(offer))
                        .setLabel("Offer @ " + currentLtv[0] + "% LTV");
            }
        };

        var preview = TotalsCard.builder()
                .variant(TotalsCard.Variant.APPRAISAL)
                .eyebrow("Live Appraisal")
                .highlight("24K spot rate", fmtRate(initialRate), "/g", rateText -> {
                    try {
                        currentRate[0] = Double.parseDouble(rateText.replace(",", "").trim());
                        recompute.run();
                        Notification.show("Spot rate updated to " + rateText + "/g — appraisal recalculated",
                                2500, Notification.Position.BOTTOM_END);
                    } catch (NumberFormatException ex) {
                        Notification.show("Enter a valid numeric spot rate", 2000, Notification.Position.BOTTOM_END);
                    }
                })
                .gauge("$1,475", "Offer @ " + initialLtv + "% LTV", initialLtv)
                .row(totalAppraisedRow)
                // Reduces the value → NEGATIVE (reddish), not DISCOUNT (which reads as a favourable green)
                .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.NEGATIVE)
                .row(netAppraisedRow)
                .row(ltvRow)
                // Explicit DEFAULT: overrides the leading "+" auto-detection so it stays plain, not green
                .row("Rounding adjustment", "+$1.83", TotalsRow.Variant.DEFAULT)
                .row(loanOfferRow)
                .slider("LTV", 10, 80, initialLtv, ltv -> {
                    currentLtv[0] = ltv;
                    recompute.run();
                })
                .toggleGroup(terms, 1, days ->
                        Notification.show("Loan term set to " + terms.get(days),
                                2000, Notification.Position.BOTTOM_END))
                .termsNote("Interest accrues monthly at 4.0%. First payment or renewal due by the " +
                        "maturity date shown in Loan Terms.")
                .primaryAction("Approve & Continue to Signing", () ->
                        Notification.show("Ticket approved — proceeding to signing",
                                3000, Notification.Position.MIDDLE))
                .secondaryAction("Reset Ticket", () -> {
                    currentRate[0] = initialRate;
                    currentLtv[0] = initialLtv;
                    recompute.run();
                    TotalsCard card = cardHolder[0];
                    if (card != null) {
                        // Reflect the reset values back onto the highlight field and slider display;
                        // passing null callbacks keeps the originally-registered listeners untouched.
                        card.setHighlight("24K spot rate", fmtRate(initialRate), "/g", null);
                        card.setSlider("LTV", 10, 80, initialLtv, null);
                    }
                    Notification.show("Ticket reset to defaults", 2000, Notification.Position.BOTTOM_END);
                })
                .build();
        preview.setWidth("22rem");
        cardHolder[0] = preview;

        return new DemoExample("Appraisal variant (dark, live gauge, slider & term toggle)", preview, """
                TotalsCard appraisal = TotalsCard.builder()
                    .variant(TotalsCard.Variant.APPRAISAL)
                    .eyebrow("Live Appraisal")
                    .highlight("24K spot rate", "148.00", "/g", rate -> recompute(rate))
                    .gauge("$1,475", "Offer @ 65% LTV", 65)
                    .row(totalAppraisedRow) // kept as field references so both the spot-rate
                    .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.NEGATIVE)
                    .row(netAppraisedRow)   // field and the LTV slider can recompute them
                    .row(ltvRow)
                    .row("Rounding adjustment", "+$1.83", TotalsRow.Variant.DEFAULT)
                    .row(loanOfferRow)
                    .slider("LTV", 10, 80, 65, ltv -> recompute(ltv))
                    .toggleGroup(List.of("15 Days", "30 Days", "60 Days"), 1,
                        days -> setTerm(terms.get(days)))
                    .termsNote("Interest accrues monthly at 4.0%.")
                    .primaryAction("Approve & Continue to Signing", this::approve)
                    .secondaryAction("Reset Ticket", this::resetToDefaults)
                    .build();

                // recompute() updates the rows AND the gauge together, whether triggered by a
                // spot-rate edit or an LTV slider drag:
                void recompute() {
                    double appraised = weightGrams * currentRate;
                    double net = appraised + conditionAdjustment;
                    double offer = net * currentLtv / 100.0 + roundingAdjustment;
                    totalAppraisedRow.setValue(fmtMoney(appraised));
                    netAppraisedRow.setValue(fmtMoney(net));
                    ltvRow.setLabel("Loan-to-value (" + currentLtv + "%)");
                    ltvRow.setValue(fmtMoney(net * currentLtv / 100.0));
                    loanOfferRow.setValue(fmtMoney(offer));
                    appraisal.getGauge().setPercent(currentLtv)
                        .setAmount(fmtMoney(offer))
                        .setLabel("Offer @ " + currentLtv + "% LTV");
                }
                """);
    }

    private DemoExample appraisalRowsOnlyExample() {
        // Keep direct references to the rows that need to change at runtime.
        TotalsRow pendingRow = new TotalsRow("Pending verification (excluded)", "$0.00",
                TotalsRow.Variant.WARNING);
        pendingRow.setVisible(false); // hidden until an unverified item exists

        TotalsRow ltvRow = new TotalsRow("Loan-to-value (65%)", "$1,473.17", TotalsRow.Variant.DEFAULT);

        var preview = TotalsCard.builder()
                .variant(TotalsCard.Variant.APPRAISAL) // dark surface — no header/gauge/terms/actions
                .row("Total appraised value", "$2,289.89")
                .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.NEGATIVE)
                .row(pendingRow)
                .row("Net appraised value", "$2,266.41")
                .row(ltvRow)
                .row("Rounding adjustment", "+$1.83", TotalsRow.Variant.DEFAULT)
                .row("Loan offer", "$1,475.00", TotalsRow.Variant.GRAND_TOTAL)
                .build();
        preview.setWidth("22rem");

        // Later, e.g. when an unverified item is added / the LTV slider moves elsewhere in the UI:
        // pendingRow.setValue(fmtMoney(pendingTotal));
        // pendingRow.setVisible(pendingTotal > 0);
        // ltvRow.setLabel("Loan-to-value (" + newLtv + "%)");
        // ltvRow.setValue(fmtMoney(newLtvAmount));

        return new DemoExample("Appraisal variant — rows only (no header/gauge/terms/actions)", preview, """
                // Keep direct references to rows that need to change at runtime.
                TotalsRow pendingRow = new TotalsRow("Pending verification (excluded)", "$0.00",
                        TotalsRow.Variant.WARNING);
                pendingRow.setVisible(false); // hidden until an unverified item exists

                TotalsRow ltvRow = new TotalsRow("Loan-to-value (65%)", "$1,473.17", TotalsRow.Variant.DEFAULT);

                TotalsCard card = TotalsCard.builder()
                    .variant(TotalsCard.Variant.APPRAISAL) // dark surface, rows only
                    .row("Total appraised value", "$2,289.89")
                    .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.NEGATIVE)
                    .row(pendingRow)
                    .row("Net appraised value", "$2,266.41")
                    .row(ltvRow)
                    .row("Rounding adjustment", "+$1.83", TotalsRow.Variant.DEFAULT)
                    .row("Loan offer", "$1,475.00", TotalsRow.Variant.GRAND_TOTAL)
                    .build();

                // Later, reactively:
                pendingRow.setValue(fmtMoney(pendingTotal));
                pendingRow.setVisible(pendingTotal > 0);
                ltvRow.setLabel("Loan-to-value (" + newLtv + "%)");
                ltvRow.setValue(fmtMoney(newLtvAmount));
                """);
    }
}

