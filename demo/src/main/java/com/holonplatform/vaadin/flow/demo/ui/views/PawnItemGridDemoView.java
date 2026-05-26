package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.GridColumns;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePair;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.LineItemGrid;
import com.holonplatform.vaadin.flow.vaadinplus.components.PawnItemRow;
import com.holonplatform.vaadin.flow.vaadinplus.components.PawnItemRow.Category;
import com.holonplatform.vaadin.flow.vaadinplus.components.PawnItemRow.Condition;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo page showing how {@link BeanListing} handles a pawn-broking
 * {@link PawnItemRow} — a deliberately different domain model from
 * {@link LineItemGrid} to illustrate that
 * {@code BeanListing} is more flexible: you simply supply a different
 * bean class and configure the relevant columns via the fluent builder.
 * No new wrapper component is needed.
 *
 * <ol>
 *   <li>Basic listing — auto columns, sensible headers</li>
 *   <li>Formatted monetary columns — currency valueProvider + computed columns</li>
 *   <li>Risk indicator — component column showing LTV badge</li>
 *   <li>DynamicFilterPanel — filter by category, condition and loan amount</li>
 *   <li>Selection detail — click a row to view a KeyValuePairs summary</li>
 * </ol>
 *
 * @since 10.0.0
 */
@PageTitle("PawnItemGrid – Holon Demo")
@Route(value = "pawn-item-grid", layout = DemoMainLayout.class)
public class PawnItemGridDemoView extends Div {

    // ── Sample pawn data ──────────────────────────────────────────────────────

    private static final List<PawnItemRow> ITEMS = buildSampleItems();

    private static List<PawnItemRow> buildSampleItems() {
        List<PawnItemRow> rows = new ArrayList<>();

        rows.add(row("18K yellow-gold diamond solitaire ring",
                Category.JEWELRY, Condition.EXCELLENT, "CERT-AU-1001",
                3_200.00, 1_850.00, 0.15, 30));

        rows.add(row("Apple MacBook Pro 16\" M3 Max",
                Category.ELECTRONICS, Condition.GOOD, "IMEI-3520011234567",
                2_800.00, 1_500.00, 0.15, 60));

        rows.add(row("Rolex Submariner Date ref. 126610LN",
                Category.WATCHES, Condition.EXCELLENT, "ROLEX-K123456",
                14_000.00, 9_500.00, 0.12, 30));

        rows.add(row("Snap-on 311-piece master tool set",
                Category.TOOLS, Condition.GOOD, "SNAP-TBD8850",
                1_200.00, 650.00, 0.18, 30));

        rows.add(row("Gibson Les Paul Standard 1959 reissue",
                Category.MUSICAL_INSTRUMENTS, Condition.FAIR, "GIBS-LPS59-009",
                4_500.00, 2_000.00, 0.15, 60));

        rows.add(row("1st edition Action Comics #1 (restored)",
                Category.COLLECTIBLES, Condition.FAIR, "CGC-0354-2211",
                8_000.00, 3_200.00, 0.12, 90));

        rows.add(row("Callaway Apex Pro 24 golf iron set",
                Category.SPORTING_GOODS, Condition.GOOD, "CAL-APEX24-77B",
                900.00, 480.00, 0.18, 30));

        // Risk item — LTV deliberately above threshold
        rows.add(row("Samsung Galaxy S25 Ultra (cracked back)",
                Category.ELECTRONICS, Condition.POOR, "IMEI-9910055598712",
                800.00, 700.00, 0.20, 30));

        return List.copyOf(rows);
    }

    private static PawnItemRow row(String desc, Category cat, Condition cond,
                                   String serial, double appraised,
                                   double loan, double rate, int days) {
        var r = new PawnItemRow();
        r.setDescription(desc);
        r.setCategory(cat);
        r.setCondition(cond);
        r.setSerialNumber(serial);
        r.setAppraisedValue(appraised);
        r.setLoanAmount(loan);
        r.setMonthlyRate(rate);
        r.setLoanDays(days);
        return r;
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public PawnItemGridDemoView() {
        addClassName("app-view");

        var title = new H1("PawnItemGrid (via BeanListing)");

        var desc = new Paragraph(
                "This page demonstrates that BeanListing is more flexible than a hardcoded " +
                "spreadsheet component: you simply supply a different bean class (PawnItemRow) " +
                "and configure columns via the fluent builder — no new wrapper component required. " +
                "PawnItemRow models a pawn-broking transaction with computed read-only properties " +
                "(monthlyInterest, totalDue, ltvPercent, ltvExceeded) that are exposed as standard " +
                "getters and picked up automatically by BeanListing.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(formattedColumnsExample());
        examples.add(riskIndicatorExample());
        examples.add(dynamicFilterExample());
        examples.add(selectionDetailExample());

        add(title, desc, examples);
    }

    // ── Example 1 — Basic listing ─────────────────────────────────────────────

    private DemoExample basicExample() {
        var listing = BeanListing.builder(PawnItemRow.class, true)
                .header("description",    "Description")
                .header("category",       "Category")
                .header("condition",      "Condition")
                .header("serialNumber",   "Serial / Ref.")
                .header("appraisedValue", "Appraised €")
                .header("loanAmount",     "Loan €")
                .header("loanDays",       "Days")
                .visibleColumns(List.of(
                        "description", "category", "condition",
                        "serialNumber", "appraisedValue", "loanAmount", "loanDays"))
                .height("300px")
                .build();

        listing.setItems(q -> ITEMS.stream().skip(q.getOffset()).limit(q.getLimit()));

        return new DemoExample("Basic BeanListing<PawnItemRow> (auto columns)", listing.getComponent(), """
                // BeanListing introspects PawnItemRow's getters automatically.
                // Pass true to auto-create one column per bean property.
                BeanListing<PawnItemRow> listing = BeanListing.builder(PawnItemRow.class, true)
                    .header("description",    "Description")
                    .header("category",       "Category")
                    .header("condition",      "Condition")
                    .header("serialNumber",   "Serial / Ref.")
                    .header("appraisedValue", "Appraised €")
                    .header("loanAmount",     "Loan €")
                    .header("loanDays",       "Days")
                    .visibleColumns(List.of(
                        "description", "category", "condition",
                        "serialNumber", "appraisedValue", "loanAmount", "loanDays"))
                    .height("300px")
                    .build();

                listing.setItems(q -> items.stream().skip(q.getOffset()).limit(q.getLimit()));
                """);
    }

    // ── Example 2 — Formatted monetary columns ────────────────────────────────

    private DemoExample formattedColumnsExample() {
        var listing = BeanListing.builder(PawnItemRow.class, true)
                .header("description",      "Item")
                .header("category",         "Category")
                .header("appraisedValue",   "Appraised")
                .header("loanAmount",       "Loan")
                .header("loanDays",         "Term (days)")
                .header("monthlyRate",      "Rate / month")
                .header("monthlyInterest",  "Interest")
                .header("totalDue",         "Total Due")
                .visibleColumns(List.of(
                        "description", "category", "appraisedValue",
                        "loanAmount", "loanDays", "monthlyRate",
                        "monthlyInterest", "totalDue"))
                // Format monetary columns
                .valueProvider("appraisedValue",  row -> formatCurrency(row.getAppraisedValue()))
                .valueProvider("loanAmount",       row -> formatCurrency(row.getLoanAmount()))
                .valueProvider("monthlyRate",      row -> String.format("%.0f%%", row.getMonthlyRate() * 100))
                .valueProvider("monthlyInterest",  row -> formatCurrency(row.getMonthlyInterest()))
                .valueProvider("totalDue",         row -> formatCurrency(row.getTotalDue()))
                .sortable("appraisedValue", true)
                .sortable("loanAmount",     true)
                .sortable("totalDue",       true)
                .height("300px")
                .build();

        listing.setItems(q -> ITEMS.stream().skip(q.getOffset()).limit(q.getLimit()));

        return new DemoExample("Formatted monetary columns + computed properties", listing.getComponent(), """
                // BeanListing.valueProvider replaces the default String.valueOf() renderer
                // with your custom presentation — great for currency, percentages, dates.
                //
                // Computed read-only getters (getMonthlyInterest, getTotalDue) are
                // automatically discoverable — no extra configuration needed.

                BeanListing<PawnItemRow> listing = BeanListing.builder(PawnItemRow.class, true)
                    // … headers & visibleColumns …
                    .valueProvider("appraisedValue", row -> "$" + String.format("%,.2f", row.getAppraisedValue()))
                    .valueProvider("monthlyRate",    row -> String.format("%.0f%%", row.getMonthlyRate() * 100))
                    .valueProvider("monthlyInterest",row -> "$" + String.format("%,.2f", row.getMonthlyInterest()))
                    .valueProvider("totalDue",       row -> "$" + String.format("%,.2f", row.getTotalDue()))
                    .sortable("loanAmount", true)
                    .sortable("totalDue",   true)
                    .build();

                // Business formulas (no UI dependency):
                //   interest  = loanAmount × monthlyRate × (loanDays / 30)
                //   totalDue  = loanAmount + interest
                //   ltvPercent = (loanAmount / appraisedValue) × 100
                """);
    }

    // ── Example 3 — Risk indicator badge ─────────────────────────────────────

    private DemoExample riskIndicatorExample() {
        var listing = BeanListing.builder(PawnItemRow.class, true)
                .header("description",  "Item")
                .header("category",     "Category")
                .header("loanAmount",   "Loan €")
                .header("appraisedValue","Appraised €")
                .visibleColumns(List.of("description", "category", "loanAmount", "appraisedValue"))
                .valueProvider("loanAmount",     row -> formatCurrency(row.getLoanAmount()))
                .valueProvider("appraisedValue", row -> formatCurrency(row.getAppraisedValue()))
                // Virtual LTV% column with colour-coded badge
                .withComponentColumn(row -> {
                    double ltv = row.getLtvPercent();
                    boolean exceeded = row.isLtvExceeded();
                    return new Badge(
                            String.format("%.0f%%", ltv),
                            exceeded ? BadgeColor.ERROR : BadgeColor.SUCCESS,
                            BadgeSize.S,
                            BadgeShape.PILL);
                })
                .header("LTV %")
                .width("90px")
                .add()
                .height("300px")
                .build();

        listing.setItems(q -> ITEMS.stream().skip(q.getOffset()).limit(q.getLimit()));

        var legend = new Span("Green = LTV ≤ max, Red = LTV exceeds policy threshold");

        return new DemoExample("Risk indicator — LTV badge via withComponentColumn", new Div(listing.getComponent(), legend), """
                // withComponentColumn lets you render any Vaadin Component per row.
                // Here a Badge with BadgeColor.ERROR signals an over-limit LTV ratio.

                listing = BeanListing.builder(PawnItemRow.class, true)
                    // ... headers + visible columns ...
                    .withComponentColumn(row -> {
                        boolean exceeded = row.isLtvExceeded();
                        return new Badge(
                            String.format("%.0f%%", row.getLtvPercent()),
                            exceeded ? BadgeColor.ERROR : BadgeColor.SUCCESS,
                            BadgeSize.S,
                            BadgeShape.PILL);
                    })
                    .header("LTV %").width("90px").add()
                    .build();

                // PawnItemRow.isLtvExceeded():
                //   return getLtvPercent() > ltvMaxPercent;  // default cap = 60%
                """);
    }

    // ── Example 4 — DynamicFilterPanel ───────────────────────────────────────

    private DemoExample dynamicFilterExample() {
        var panel = DynamicFilterPanel.of(PawnItemRow.class);

        var shown = new ArrayList<>(ITEMS);

        var listing = BeanListing.builder(PawnItemRow.class, true)
                .header("description",   "Item")
                .header("category",      "Category")
                .header("condition",     "Condition")
                .header("loanAmount",    "Loan €")
                .visibleColumns(List.of("description", "category", "condition", "loanAmount"))
                .valueProvider("loanAmount", row -> formatCurrency(row.getLoanAmount()))
                .height("280px")
                .build();

        listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

        panel.addFilterChangeListener(e -> {
            shown.clear();
            shown.addAll(ITEMS.stream().filter(panel.toPredicate()).toList());
            listing.getDataProvider().refreshAll();
        });

        var countLabel = new Span(ITEMS.size() + " items shown");
        panel.addFilterChangeListener(e ->
                countLabel.setText(shown.size() + " of " + ITEMS.size() + " items shown"));

        return new DemoExample("DynamicFilterPanel — type-aware row filter", new Div(panel, countLabel, listing.getComponent()), """
                // DynamicFilterPanel introspects PawnItemRow at construction time:
                //  · String fields  → CONTAINS / STARTS_WITH / IS_EMPTY …
                //  · Enum fields    → EQUALS / IN / NOT_IN …
                //  · Number fields  → EQUALS / GREATER_THAN / BETWEEN …
                //  · Boolean fields → EQUALS / NOT_EQUALS
                //
                // In-memory wiring (demo) — swap the predicate for a Datastore query in production:

                var panel = DynamicFilterPanel.of(PawnItemRow.class);
                var shown = new ArrayList<>(items);

                listing.setItems(q -> shown.stream().skip(q.getOffset()).limit(q.getLimit()));

                panel.addFilterChangeListener(e -> {
                    shown.clear();
                    shown.addAll(items.stream().filter(panel.toPredicate()).toList());
                    listing.getDataProvider().refreshAll();
                });
                """);
    }

    // ── Example 5 — Selection + KeyValuePairs detail ──────────────────────────

    private DemoExample selectionDetailExample() {
        var listing = BeanListing.builder(PawnItemRow.class, true)
                .header("description", "Item")
                .header("category",    "Category")
                .header("condition",   "Condition")
                .header("loanAmount",  "Loan €")
                .visibleColumns(List.of("description", "category", "condition", "loanAmount"))
                .valueProvider("loanAmount", row -> formatCurrency(row.getLoanAmount()))
                .selectionMode(SelectionMode.SINGLE)
                .height("260px")
                .build();

        listing.setItems(q -> ITEMS.stream().skip(q.getOffset()).limit(q.getLimit()));

        var detail = new Div();
        detail.addClassName("app-view-detail");

        ((com.holonplatform.vaadin.flow.components.Selectable<PawnItemRow>) listing)
                .addSelectionListener(e ->
                        e.getFirstSelectedItem().ifPresentOrElse(
                                row -> { detail.removeAll(); detail.add(buildDetail(row)); },
                                detail::removeAll
                        )
                );

        var hint = new Span("← click a row to see its computed loan summary");

        return new DemoExample("Single-select + KeyValuePairs detail panel", new Div(listing.getComponent(), hint, detail), """
                // React to row selection and build a KeyValuePairs summary on the fly.

                listing = BeanListing.builder(PawnItemRow.class, true)
                    // ... headers ...
                    .selectionMode(SelectionMode.SINGLE)
                    .build();

                listing.addSelectionListener(e ->
                    e.getFirstSelectedItem().ifPresent(row ->
                        detail.add(buildDetail(row))));

                // buildDetail builds a KeyValuePairs from the bean's computed properties:
                private Component buildDetail(PawnItemRow row) {
                    var pairs = new KeyValuePairs(
                        new KeyValuePair("Appraised",   formatCurrency(row.getAppraisedValue())),
                        new KeyValuePair("Loan",        formatCurrency(row.getLoanAmount())),
                        new KeyValuePair("Interest",    formatCurrency(row.getMonthlyInterest())),
                        new KeyValuePair("Total Due",   formatCurrency(row.getTotalDue())),
                        new KeyValuePair("LTV %",       String.format("%.1f%%", row.getLtvPercent())),
                        new KeyValuePair("Risk",        row.isLtvExceeded() ? "⚠ Over limit" : "✓ OK")
                    );
                    pairs.setColumns(GridColumns.COLUMNS_2);
                    return pairs;
                }
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String formatCurrency(Double value) {
        if (value == null) return "—";
        return String.format("$%,.2f", value);
    }

    private static String formatCurrency(double value) {
        return String.format("$%,.2f", value);
    }

    private static com.vaadin.flow.component.Component buildDetail(PawnItemRow row) {
        var pairs = new KeyValuePairs(
                new KeyValuePair("Description",   row.getDescription() != null ? row.getDescription() : "—"),
                new KeyValuePair("Category",      row.getCategory().getLabel()),
                new KeyValuePair("Condition",     row.getCondition().getLabel()),
                new KeyValuePair("Serial / Ref.", row.getSerialNumber() != null ? row.getSerialNumber() : "—"),
                new KeyValuePair("Appraised",     formatCurrency(row.getAppraisedValue())),
                new KeyValuePair("Loan Amount",   formatCurrency(row.getLoanAmount())),
                new KeyValuePair("Monthly Rate",  String.format("%.0f%%", row.getMonthlyRate() * 100)),
                new KeyValuePair("Days",          String.valueOf(row.getLoanDays())),
                new KeyValuePair("Interest",      formatCurrency(row.getMonthlyInterest())),
                new KeyValuePair("Total Due",     formatCurrency(row.getTotalDue())),
                new KeyValuePair("LTV %",         String.format("%.1f%% (max %.0f%%)",
                        row.getLtvPercent(), row.getLtvMaxPercent())),
                new KeyValuePair("Risk",          row.isLtvExceeded() ? "⚠ Over limit" : "✓ Within policy")
        );
        pairs.setColumns(GridColumns.COLUMNS_2);
        return pairs;
    }
}








