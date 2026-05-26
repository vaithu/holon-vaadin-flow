package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.LineItemGrid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo page for {@link LineItemGrid}.
 *
 * <ol>
 *   <li>Minimal — empty grid with a single blank row</li>
 *   <li>Pre-populated — catalog items + tax options, 3 seeded rows</li>
 *   <li>Details toggle — Description / Account / Project columns</li>
 *   <li>Bulk row add — row-count stepper + "Add Rows" button</li>
 *   <li>Bulk items dialog — two-panel "Pick Items" dialog with search + per-item qty</li>
 *   <li>Change listener — notified on every mutation</li>
 * </ol>
 */
@PageTitle("LineItemGrid – Holon Demo")
@Route(value = "line-item-grid", layout = DemoMainLayout.class)
public class LineItemGridDemoView extends Div {

    // ── Sample catalogue ──────────────────────────────────────────────────────

    private static final List<LineItemGrid.ItemSuggestion> ITEMS = List.of(
            new LineItemGrid.ItemSuggestion("Storage Cabinet",          "SKU-003",  345.00),
            new LineItemGrid.ItemSuggestion("Dining Table and Chairs",  "SKU-004",  126.00),
            new LineItemGrid.ItemSuggestion("Coffee Table",             "SKU-005",  331.00),
            new LineItemGrid.ItemSuggestion("Area Rug",                 "SKU-006",  579.00),
            new LineItemGrid.ItemSuggestion("Sofa",                     "SKU-007",  307.00),
            new LineItemGrid.ItemSuggestion("Queen Size Bed",           "SKU-009",  489.00),
            new LineItemGrid.ItemSuggestion("Bookshelf",                "SKU-010",  179.00),
            new LineItemGrid.ItemSuggestion("Floor Lamp",               "SKU-011",   59.99)
    );

    private static final List<LineItemGrid.TaxOption> TAXES = List.of(
            new LineItemGrid.TaxOption("GST 10%", 0.10),
            new LineItemGrid.TaxOption("VAT 20%", 0.20),
            new LineItemGrid.TaxOption("HST 13%", 0.13)
    );

    // ── Constructor ───────────────────────────────────────────────────────────

    public LineItemGridDemoView() {
        addClassName("app-view");

        var title = new H1("LineItemGrid");

        var desc = new Paragraph(
                "Keyboard-centric inline spreadsheet for document line items — invoices, " +
                "purchase orders, quotes, and bills. All rows are always in the DOM " +
                "(no virtual scrolling) so Tab and Enter navigate like Excel. " +
                "The toolbar supports both bulk row add (stepper + button) and a two-panel " +
                "'Pick Items' dialog for selecting multiple catalog items with per-item quantities. " +
                "On mobile the table switches to a card list with a Sheet form per row.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(minimalExample());
        examples.add(prePopulatedExample());
        examples.add(withDetailsExample());
        examples.add(bulkRowAddExample());
        examples.add(bulkItemsDialogExample());
        examples.add(changeListenerExample());

        add(title, desc, examples);
    }

    // ── Example 1 — Minimal ───────────────────────────────────────────────────

    private DemoExample minimalExample() {
        var grid = LineItemGrid.builder()
                .title("Order Lines")
                .withInitialRows(1)
                .build();

        return new DemoExample("Minimal — blank grid, one empty row", grid.getComponent(), """
                LineItemGrid grid = LineItemGrid.builder()
                    .title("Order Lines")
                    .withInitialRows(1)
                    .build();

                add(grid.getComponent());

                // Keyboard shortcuts:
                //  Tab / Shift+Tab → move between cells within a row
                //  Enter           → jump to the same column in the next row (Excel-style)
                """);
    }

    // ── Example 2 — Pre-populated catalogue ──────────────────────────────────

    private DemoExample prePopulatedExample() {
        var grid = LineItemGrid.builder()
                .title("Invoice Lines")
                .withItemSuggestions(ITEMS)
                .withTaxOptions(TAXES)
                .withInitialRows(3)
                .build();

        // Seed visible data so the Amount column shows computed values immediately
        var snapshot = grid.getRows();
        if (snapshot.size() >= 3) {
            snapshot.get(0).setItemName("Coffee Table");  snapshot.get(0).setQuantity(2);  snapshot.get(0).setRate(331.00);
            snapshot.get(1).setItemName("Sofa");          snapshot.get(1).setQuantity(1);  snapshot.get(1).setRate(307.00);
            snapshot.get(2).setItemName("Area Rug");      snapshot.get(2).setQuantity(3);  snapshot.get(2).setRate(579.00);
            grid.setRows(snapshot);
        }

        return new DemoExample(
                "Pre-populated — item catalogue, tax options and seeded rows",
                grid.getComponent(), """
                var items = List.of(
                    new LineItemGrid.ItemSuggestion("Coffee Table", "SKU-005", 331.00),
                    new LineItemGrid.ItemSuggestion("Sofa",         "SKU-007", 307.00),
                    new LineItemGrid.ItemSuggestion("Area Rug",     "SKU-006", 579.00)
                );

                var taxes = List.of(
                    new LineItemGrid.TaxOption("GST 10%", 0.10),
                    new LineItemGrid.TaxOption("VAT 20%", 0.20)
                );

                LineItemGrid grid = LineItemGrid.builder()
                    .title("Invoice Lines")
                    .withItemSuggestions(items)
                    .withTaxOptions(taxes)
                    .withInitialRows(3)
                    .build();
                """);
    }

    // ── Example 3 — Details toggle ────────────────────────────────────────────

    private DemoExample withDetailsExample() {
        var grid = LineItemGrid.builder()
                .title("Quote Lines")
                .withItemSuggestions(ITEMS)
                .withTaxOptions(TAXES)
                .withInitialRows(2)
                .build();

        return new DemoExample(
                "Details toggle — click 'Show Details' to reveal Description / Account / Project",
                grid.getComponent(), """
                // Three detail columns are hidden by default:
                //   · Description  (data-lig-col=5)
                //   · Account      (data-lig-col=6)
                //   · Project      (data-lig-col=7)
                //
                // The 'Show Details' / 'Hide Details' button in the toolbar toggles all three.
                // No code needed — the component handles it internally.

                LineItemGrid grid = LineItemGrid.builder()
                    .title("Quote Lines")
                    .withItemSuggestions(catalogItems)
                    .withTaxOptions(taxOptions)
                    .withInitialRows(2)
                    .build();
                """);
    }

    // ── Example 4 — Bulk row add ──────────────────────────────────────────────

    private DemoExample bulkRowAddExample() {
        var grid = LineItemGrid.builder()
                .title("PO Lines — bulk row add")
                .withInitialRows(1)
                .build();

        return new DemoExample(
                "Bulk row add — use the row-count stepper then click 'Add Rows'",
                grid.getComponent(), """
                // The toolbar contains an IntegerField (↑↓ stepper, range 1–50) next to
                // the 'Add Rows' button. Set the count to e.g. 5 and click — five empty
                // rows are created in a single batch refresh with one fireChange() call.

                LineItemGrid grid = LineItemGrid.builder()
                    .title("PO Lines")
                    .withInitialRows(1)
                    .build();

                // Programmatic equivalent (same single-refresh behaviour):
                grid.addRows(5);   // adds 5 empty rows, one refreshAll()
                grid.addRow();     // adds 1 empty row  (delegates to addRows(1))
                """);
    }

    // ── Example 5 — Pick Items (bulk add dialog) ──────────────────────────────

    private DemoExample bulkItemsDialogExample() {
        var grid = LineItemGrid.builder()
                .title("Invoice Lines — bulk items")
                .withItemSuggestions(ITEMS)
                .withTaxOptions(TAXES)
                .withInitialRows(0)
                .build();

        return new DemoExample(
                "Bulk items dialog — click 'Pick Items' for the two-panel catalog browser",
                grid.getComponent(), """
                // The 'Pick Items' button appears automatically when item suggestions are
                // configured. It opens a split dialog:
                //
                //   LEFT  — searchable catalog (filter by name or SKU); click row to select.
                //            Selected rows show a green ✓ and a blue highlight.
                //   RIGHT — "Selected Items N · Total Quantity X" header followed by
                //            each selected item with its own [−] qty [+] stepper.
                //            Decrementing to 0 removes the item from the selection.
                //
                // "Add Items" creates one LineItemRow per selection in a single batch refresh.

                LineItemGrid grid = LineItemGrid.builder()
                    .title("Invoice Lines")
                    .withItemSuggestions(catalogItems)  // ← triggers 'Pick Items' button
                    .withTaxOptions(taxOptions)
                    .withInitialRows(0)
                    .build();
                """);
    }

    // ── Example 6 — Change listener ──────────────────────────────────────────

    private DemoExample changeListenerExample() {
        var grid = LineItemGrid.builder()
                .title("PO Lines — live total")
                .withItemSuggestions(ITEMS)
                .withTaxOptions(TAXES)
                .withInitialRows(2)
                .build();

        grid.setOnChangeListener(rows -> {
            double total = rows.stream()
                    .mapToDouble(LineItemGrid.LineItemRow::getAmount)
                    .sum();
            var n = Notification.show(
                    String.format("Total updated: $%,.2f across %d line(s)", total, rows.size()),
                    2000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        });

        return new DemoExample(
                "Change listener — notified on every cell edit, row add, row delete, or bulk import",
                grid.getComponent(), """
                LineItemGrid grid = LineItemGrid.builder()
                    .withItemSuggestions(items)
                    .withTaxOptions(taxes)
                    .withInitialRows(2)
                    .build();

                // Fired once per logical operation (bulk row add or bulk items import
                // triggers a single call, not one per row).
                grid.setOnChangeListener(rows -> {
                    double total = rows.stream()
                        .mapToDouble(LineItemGrid.LineItemRow::getAmount)
                        .sum();
                    invoiceService.updateTotal(total);
                });

                // Programmatic row manipulation:
                grid.addRow();                          // add 1 empty row
                grid.addRows(5);                        // add 5 empty rows (single refresh)
                grid.removeRow(rows.get(0));            // remove a specific row
                grid.setRows(loadedRows);               // replace all rows at once
                List<LineItemRow> all = grid.getRows(); // unmodifiable snapshot
                """);
    }
}

