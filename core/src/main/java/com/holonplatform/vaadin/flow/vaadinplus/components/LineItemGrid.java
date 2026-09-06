package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor.Column;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Keyboard-centric inline spreadsheet for document line items (invoices, POs, quotes).
 *
 * <p>{@code LineItemGrid} is a domain preset built on top of the generic {@link ItemLineEditor}
 * engine: it configures an {@code ItemLineEditor<LineItemRow>} with the Item / Qty / Rate / Tax /
 * Amount columns (plus a Description / Account / Project "details" group), a subtotal + tax +
 * total {@link TotalsCard} footer, and three generic {@link ItemLineEditor} capabilities:</p>
 * <ul>
 *   <li>the responsive mobile card list with a {@link Sheet} edit form (Excel-like Enter/Tab
 *       navigation and the "Show Details" toggle are also generic {@link ItemLineEditor}
 *       behaviour, always on),</li>
 *   <li>the bulk "Add Rows" toolbar stepper,</li>
 *   <li>the catalog "Pick Items" two-panel bulk-content dialog.</li>
 * </ul>
 * <p>Only the {@link LineItemRow} bean, the column renderers, the tax/item catalog lookups and
 * the totals computation below are specific to this document-line-item domain — everything else
 * is inherited, unmodified, from {@link ItemLineEditor}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * LineItemGrid grid = LineItemGrid.builder()
 *     .title("Items")
 *     .withItemSuggestion("Laptop", "SKU-001", 1299.00)
 *     .withItemSuggestion("Mouse",  "SKU-002", 29.99)
 *     .withTaxOption("GST 10%", 0.10)
 *     .withTaxOption("VAT 20%", 0.20)
 *     .withInitialRows(2)
 *     .build();
 *
 * grid.setOnChangeListener(rows -> save(rows));
 * }</pre>
 *
 * @since 10.0.0
 * @see ItemLineEditor
 */
@StyleSheet("context://line-item-grid.css")
public class LineItemGrid extends Composite<Div> implements HasComponent {

    // ── Domain model ──────────────────────────────────────────────────────────

    /**
     * A mutable, self-contained row in the grid.
     * Amount is computed: {@code quantity × rate × (1 + taxRate)}.
     */
    public static class LineItemRow {
        private final String id = UUID.randomUUID().toString();
        private String itemName;
        private String sku;
        private String description;
        private Integer quantity = 1;
        private Double  rate;
        private String  taxLabel;
        private double  taxRate = 0.0;
        private String  account;
        private String  project;

        public String  getId()          { return id; }
        public String  getItemName()    { return itemName; }
        public String  getSku()         { return sku; }
        public String  getDescription() { return description; }
        public Integer getQuantity()    { return quantity; }
        public Double  getRate()        { return rate; }
        public String  getTaxLabel()    { return taxLabel; }
        public double  getTaxRate()     { return taxRate; }
        public String  getAccount()     { return account; }
        public String  getProject()     { return project; }

        public void setItemName(String v)    { this.itemName    = v; }
        public void setSku(String v)         { this.sku         = v; }
        public void setDescription(String v) { this.description = v; }
        public void setQuantity(Integer v)   { this.quantity    = v; }
        public void setRate(Double v)        { this.rate        = v; }
        public void setTaxLabel(String v)    { this.taxLabel    = v; }
        public void setTaxRate(double v)     { this.taxRate     = v; }
        public void setAccount(String v)     { this.account     = v; }
        public void setProject(String v)     { this.project     = v; }

        /** @return subtotal factoring in tax */
        public double getAmount() {
            if (quantity == null || rate == null) return 0.0;
            return quantity * rate * (1.0 + taxRate);
        }

        /** @return raw line total without tax */
        public double getSubtotal() {
            if (quantity == null || rate == null) return 0.0;
            return quantity * rate;
        }
    }

    /** Catalog item suggestion for the item ComboBox. */
    public record ItemSuggestion(String name, String sku, double defaultRate) {}

    /** Tax rate option for the tax ComboBox. */
    public record TaxOption(String label, double rate) {}

    /** Maximum rows recommended (mirrors {@link ItemLineEditor}'s row cap). */
    public static final int MAX_ROWS = 40;

    private final ItemLineEditor<LineItemRow> editor;
    private final List<ItemSuggestion> itemSuggestions;
    private final List<TaxOption>      taxOptions;
    private Consumer<List<LineItemRow>> changeListener;

    // ── Constructor ───────────────────────────────────────────────────────────

    private LineItemGrid(Builder b) {
        this.itemSuggestions = new ArrayList<>(b.itemSuggestions);
        this.taxOptions      = new ArrayList<>(b.taxOptions);

        getContent().addClassName("line-item-grid");

        String resolvedTitle = b.title != null ? b.title
                : LocalizationProvider.localize("Line Items", "line_item.title");

        var editorBuilder = ItemLineEditor.builder(LineItemRow.class)
                .title(resolvedTitle)
                .rowFactory(LineItemRow::new)
                .maxRows(MAX_ROWS)
                .addColumn(itemColumn())
                .addColumn(qtyColumn())
                .addColumn(rateColumn())
                .addColumn(taxColumn())
                .addColumn(amountColumn())
                .addColumn(descriptionColumn())
                .addColumn(accountColumn())
                .addColumn(projectColumn())
                .footer(this::buildFooter)
                .mobileCard(
                        row -> row.getItemName() != null ? row.getItemName() : "—",
                        this::buildCardMeta,
                        row -> fmt(row.getAmount()))
                .bulkAddEnabled(true)
                .addButtonVisible(false)
                .emptyState(
                        LocalizationProvider.localize("No line items", "line_item.empty_title"),
                        LocalizationProvider.localize("Add a row to get started.", "line_item.empty_description"))
                .onChange(rows -> fireChange());

        if (!itemSuggestions.isEmpty()) {
            editorBuilder = editorBuilder.itemPicker(
                    LocalizationProvider.localize("Pick Items", "line_item.pick_items"),
                    itemSuggestions, ItemSuggestion::name,
                    s -> LocalizationProvider.localize("SKU: {0}  \u00b7  Purchase Rate: ${1}",
                            "line_item.sku_meta", s.sku(), fmt(s.defaultRate())),
                    (s, qty) -> {
                        var row = new LineItemRow();
                        row.setItemName(s.name());
                        row.setSku(s.sku());
                        row.setRate(s.defaultRate());
                        row.setQuantity(qty);
                        return row;
                    });
        }

        this.editor = editorBuilder.build();
        getContent().add(editor.getComponent());

        editor.addInitialRows(b.initialRows);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Appends a new empty row and refreshes the grid. */
    public void addRow() {
        editor.addRow();
    }

    /**
     * Appends {@code count} new empty rows in a single batch refresh.
     * Silently clamped so the total never exceeds {@value #MAX_ROWS}.
     *
     * @param count number of rows to add (clamped to available capacity)
     */
    public void addRows(int count) {
        editor.addRows(count);
    }

    /** Removes the specified row and refreshes the grid. */
    public void removeRow(LineItemRow row) {
        editor.removeRow(row);
    }

    /** Returns an unmodifiable snapshot of all current rows. */
    public List<LineItemRow> getRows() {
        return editor.getRows();
    }

    /** Replaces all rows with the provided list. */
    public void setRows(List<LineItemRow> newRows) {
        editor.setRows(newRows);
    }

    /**
     * Registers a listener called whenever any cell value changes or a row is
     * added / removed.
     *
     * @param listener receives the current row snapshot (never null)
     */
    public void setOnChangeListener(Consumer<List<LineItemRow>> listener) {
        this.changeListener = listener;
    }

    private void fireChange() {
        if (changeListener != null) {
            changeListener.accept(getRows());
        }
    }

    // ── Column definitions (domain-specific) ─────────────────────────────────

    private Column<LineItemRow> itemColumn() {
        return Column.<LineItemRow>of("item", LocalizationProvider.localize("ITEM", "line_item.col_item"), row -> {
            var cb = new ComboBox<ItemSuggestion>();
            cb.setWidthFull();
            cb.setItems(itemSuggestions);
            cb.setItemLabelGenerator(ItemSuggestion::name);
            cb.setPlaceholder(LocalizationProvider.localize("Select item…", "line_item.placeholder_item"));
            preselectItem(cb, row.getItemName());
            cb.addValueChangeListener(e -> {
                var s = e.getValue();
                if (s != null) {
                    row.setItemName(s.name());
                    row.setSku(s.sku());
                    if (row.getRate() == null) row.setRate(s.defaultRate());
                } else {
                    row.setItemName(null);
                    row.setSku(null);
                }
                editor.notifyRowChanged(row);
            });
            return cb;
        }).flexGrow(2);
    }

    private Column<LineItemRow> qtyColumn() {
        return Column.<LineItemRow>of("qty", LocalizationProvider.localize("QTY", "line_item.col_qty"), row -> {
            var f = new IntegerField();
            f.setWidthFull();
            f.setMin(0);
            f.setValue(row.getQuantity() != null ? row.getQuantity() : 1);
            f.addValueChangeListener(e -> {
                row.setQuantity(e.getValue());
                editor.notifyRowChanged(row);
            });
            return f;
        }).width("90px");
    }

    private Column<LineItemRow> rateColumn() {
        return Column.<LineItemRow>of("rate", LocalizationProvider.localize("RATE", "line_item.col_rate"), row -> {
            var f = new NumberField();
            f.setWidthFull();
            f.setMin(0);
            if (row.getRate() != null) f.setValue(row.getRate());
            f.addValueChangeListener(e -> {
                row.setRate(e.getValue());
                editor.notifyRowChanged(row);
            });
            return f;
        }).width("120px");
    }

    private Column<LineItemRow> taxColumn() {
        return Column.<LineItemRow>of("tax", LocalizationProvider.localize("TAX", "line_item.col_tax"), row -> {
            var cb = new ComboBox<TaxOption>();
            cb.setWidthFull();
            cb.setItems(taxOptions);
            cb.setItemLabelGenerator(TaxOption::label);
            cb.setPlaceholder(LocalizationProvider.localize("No Tax", "line_item.placeholder_no_tax"));
            preselectTax(cb, row.getTaxLabel());
            cb.addValueChangeListener(e -> {
                var t = e.getValue();
                if (t != null) { row.setTaxLabel(t.label()); row.setTaxRate(t.rate()); }
                else           { row.setTaxLabel(null);      row.setTaxRate(0.0); }
                editor.notifyRowChanged(row);
            });
            return cb;
        }).width("130px");
    }

    private Column<LineItemRow> amountColumn() {
        return Column.<LineItemRow>of("amount", LocalizationProvider.localize("AMOUNT", "line_item.col_amount"),
                row -> Components.span().text(fmt(row.getAmount())).styleName("lig-amount").build())
                .width("140px");
    }

    private Column<LineItemRow> descriptionColumn() {
        return Column.<LineItemRow>of("desc", LocalizationProvider.localize("DESCRIPTION", "line_item.col_desc"),
                row -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder(LocalizationProvider.localize("Description…", "line_item.placeholder_desc"));
                    if (row.getDescription() != null) f.setValue(row.getDescription());
                    f.addValueChangeListener(e -> {
                        row.setDescription(e.getValue());
                        editor.notifyRowChanged(row);
                    });
                    return f;
                }).flexGrow(1).detail();
    }

    private Column<LineItemRow> accountColumn() {
        return Column.<LineItemRow>of("account", LocalizationProvider.localize("ACCOUNT", "line_item.col_account"),
                row -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder(LocalizationProvider.localize("Account…", "line_item.placeholder_account"));
                    if (row.getAccount() != null) f.setValue(row.getAccount());
                    f.addValueChangeListener(e -> {
                        row.setAccount(e.getValue());
                        editor.notifyRowChanged(row);
                    });
                    return f;
                }).width("140px").detail();
    }

    private Column<LineItemRow> projectColumn() {
        return Column.<LineItemRow>of("project", LocalizationProvider.localize("PROJECT", "line_item.col_project"),
                row -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder(LocalizationProvider.localize("Project…", "line_item.placeholder_project"));
                    if (row.getProject() != null) f.setValue(row.getProject());
                    f.addValueChangeListener(e -> {
                        row.setProject(e.getValue());
                        editor.notifyRowChanged(row);
                    });
                    return f;
                }).width("130px").detail();
    }

    // ── Footer / mobile card helpers (domain-specific) ────────────────────────

    private TotalsCard buildFooter(List<LineItemRow> lines) {
        double subtotal = lines.stream().mapToDouble(LineItemRow::getSubtotal).sum();
        double taxTotal  = lines.stream().mapToDouble(r -> r.getSubtotal() * r.getTaxRate()).sum();
        return TotalsCard.builder()
                .row(LocalizationProvider.localize("Subtotal", "line_item.subtotal"), fmt(subtotal))
                .row(LocalizationProvider.localize("Tax", "line_item.tax"), fmt(taxTotal))
                .row(LocalizationProvider.localize("Total", "line_item.total"), fmt(subtotal + taxTotal),
                        TotalsRow.Variant.GRAND_TOTAL)
                .build();
    }

    private String buildCardMeta(LineItemRow row) {
        var qty  = row.getQuantity() != null ? row.getQuantity() : 0;
        var rate = row.getRate()     != null ? fmt(row.getRate()) : "—";
        var tax  = row.getTaxLabel() != null ? "  ·  " + row.getTaxLabel() : "";
        return qty + " × " + rate + tax;
    }

    // ── Utility helpers ───────────────────────────────────────────────────────

    private void preselectItem(ComboBox<ItemSuggestion> cb, String name) {
        if (name == null) return;
        itemSuggestions.stream().filter(s -> s.name().equals(name)).findFirst().ifPresent(cb::setValue);
    }

    private void preselectTax(ComboBox<TaxOption> cb, String label) {
        if (label == null) return;
        taxOptions.stream().filter(t -> t.label().equals(label)).findFirst().ifPresent(cb::setValue);
    }

    private static String fmt(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    // ── Builder ───────────────────────────────────────────────────────────────

    /**
     * Creates a new {@link Builder} for a {@link LineItemGrid}.
     *
     * <pre>{@code
     * LineItemGrid grid = LineItemGrid.builder()
     *     .title("Invoice Lines")
     *     .withItemSuggestion("Laptop", "SKU-001", 1299.00)
     *     .withTaxOption("GST 10%", 0.10)
     *     .withInitialRows(1)
     *     .build();
     * }</pre>
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Fluent builder for {@link LineItemGrid}. */
    public static final class Builder {

        private String title;
        private final List<ItemSuggestion> itemSuggestions = new ArrayList<>();
        private final List<TaxOption>      taxOptions      = new ArrayList<>();
        private int initialRows = 1;

        private Builder() {}

        /**
         * Sets the section title shown above the grid.
         * Default (when never called): the localized {@code "Line Items"} message
         * (message code {@code line_item.title}).
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the section title from a {@link Localizable} descriptor.
         *
         * @param title localizable section title (not null)
         * @return this builder
         */
        public Builder title(Localizable title) {
            this.title = LocalizationProvider.localize(title)
                    .orElseGet(() -> title.getMessage() != null ? title.getMessage() : "");
            return this;
        }

        /** Adds a single item suggestion to the item ComboBox. */
        public Builder withItemSuggestion(String name, String sku, double defaultRate) {
            itemSuggestions.add(new ItemSuggestion(name, sku, defaultRate));
            return this;
        }

        /** Adds all items from the provided list. */
        public Builder withItemSuggestions(List<ItemSuggestion> items) {
            itemSuggestions.addAll(items);
            return this;
        }

        /** Adds a tax option to the tax ComboBox. */
        public Builder withTaxOption(String label, double rate) {
            taxOptions.add(new TaxOption(label, rate));
            return this;
        }

        /** Adds all tax options from the provided list. */
        public Builder withTaxOptions(List<TaxOption> taxes) {
            taxOptions.addAll(taxes);
            return this;
        }

        /** Sets the number of empty rows to pre-populate on creation. Clamped to {@value LineItemGrid#MAX_ROWS}. Default: {@code 1}. */
        public Builder withInitialRows(int count) {
            this.initialRows = Math.max(0, Math.min(count, MAX_ROWS));
            return this;
        }

        /** Builds the configured {@link LineItemGrid}. */
        public LineItemGrid build() {
            return new LineItemGrid(this);
        }
    }

    // ── HasComponent ──────────────────────────────────────────────────────────

    @Override
    public Component getComponent() {
        return getContent();
    }
}



