package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * Keyboard-centric inline spreadsheet for document line items (invoices, POs, quotes).
 *
 * <h3>Desktop mode</h3>
 * <p>Renders a non-virtualised {@link Grid} ({@code setAllRowsVisible(true)}) so the full
 * Tab order remains in the DOM at all times. Every editable cell is always an input
 * component — no editor-mode toggling. Pressing {@code Enter} inside any cell moves focus
 * to the same column in the next row (Excel-like). A "Show Details" toggle reveals
 * the Description, Account and Project columns.</p>
 *
 * <h3>Mobile mode</h3>
 * <p>CSS hides the table and shows a card list instead via an {@code @media (max-width: 640px)}
 * rule. Tapping a card opens a {@link Sheet} with a full {@link FormLayout} for
 * that row — natural mobile keyboard flow, no column-width constraints.</p>
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

    // ── Internal state ────────────────────────────────────────────────────────

    /**
     * Maximum rows recommended for {@code setAllRowsVisible(true)}.
     * Beyond this, every row means more server-side component instances in the VaadinSession.
     * Hard-cap enforced in {@link #addRows(int)} and {@link Builder#withInitialRows(int)}.
     */
    public static final int MAX_ROWS = 40;

    private final List<LineItemRow>             rows            = new ArrayList<>();
    /** Vaadin's data-view handle — created once in {@link #buildGrid()}, never replaced. */
    private GridListDataView<LineItemRow>       dataView;
    private final List<ItemSuggestion>          itemSuggestions;
    private final List<TaxOption>               taxOptions;
    private final String                        title;

    private final Grid<LineItemRow> grid;
    private final Div               cardsContainer;

    private final Span subtotalSpan   = Components.span().text("0.00").build();
    private final Span taxAmountSpan  = Components.span().text("0.00").build();
    private final Span totalSpan      = Components.span().text("0.00").build();

    private Grid.Column<LineItemRow> descriptionCol;
    private Grid.Column<LineItemRow> accountCol;
    private Grid.Column<LineItemRow> projectCol;

    private boolean          detailsVisible = false;
    private Consumer<List<LineItemRow>> changeListener;

    // ── Constructor ───────────────────────────────────────────────────────────

    private LineItemGrid(Builder b) {
        this.title           = b.title;
        this.itemSuggestions = new ArrayList<>(b.itemSuggestions);
        this.taxOptions      = new ArrayList<>(b.taxOptions);

        getContent().addClassName("line-item-grid");

        // No Responsive.apply() needed — CSS @media query handles mobile/desktop switch

        var toolbar      = buildToolbar();
        this.grid        = buildGrid();
        Div tableWrapper = Components.div().add(grid).styleName("lig-table").build();

        this.cardsContainer = Components.div().styleName("lig-cards").build();

        var footer = buildFooter();

        getContent().add(toolbar, tableWrapper, cardsContainer, footer);

        for (int i = 0; i < b.initialRows; i++) addRow();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Appends a new empty row and refreshes the grid. */
    public void addRow() {
        addRows(1);
    }

    /**
     * Appends {@code count} new empty rows in a single batch refresh.
     * Silently clamps so the total never exceeds {@value #MAX_ROWS}.
     *
     * @param count number of rows to content (clamped to available capacity)
     */
    public void addRows(int count) {
        int available = MAX_ROWS - rows.size();
        int n = Math.max(0, Math.min(count, available));
        for (int i = 0; i < n; i++) rows.add(new LineItemRow());
        refreshAll();
    }

    /** Removes the specified row and refreshes the grid. */
    public void removeRow(LineItemRow row) {
        rows.remove(row);
        refreshAll();
    }

    /** Returns an unmodifiable snapshot of all current rows. */
    public List<LineItemRow> getRows() {
        return List.copyOf(rows);
    }

    /** Replaces all rows with the provided list. */
    public void setRows(List<LineItemRow> newRows) {
        rows.clear();
        rows.addAll(newRows);
        refreshAll();
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

    // ── Grid (desktop) ────────────────────────────────────────────────────────

    private Grid<LineItemRow> buildGrid() {
        var g = Components.grid(LineItemRow.class, false);
        g.setAllRowsVisible(true);
        g.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);
        g.addClassName("lig-grid");

        // ── Item ──────────────────────────────────────────────────────────────
        g.addColumn(new ComponentRenderer<>(row -> {
            var cb = new ComboBox<ItemSuggestion>();
            cb.addClassName("lig-cell");
            cb.getElement().setAttribute("data-lig-col", "0");
            cb.setItems(itemSuggestions);
            cb.setItemLabelGenerator(ItemSuggestion::name);
            cb.setWidthFull();
            cb.setPlaceholder("Select item…");
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
                refreshRow(row);
                fireChange();
            });
            attachEnterNav(cb);
            return cb;
        })).setHeader("ITEM").setFlexGrow(2).setKey("item");

        // ── Quantity ──────────────────────────────────────────────────────────
        g.addColumn(new ComponentRenderer<>(row -> {
            var f = new IntegerField();
            f.addClassName("lig-cell");
            f.getElement().setAttribute("data-lig-col", "1");
            f.setWidthFull();
            f.setMin(0);
            f.setValue(row.getQuantity() != null ? row.getQuantity() : 1);
            f.addValueChangeListener(e -> { row.setQuantity(e.getValue()); refreshRow(row); fireChange(); });
            attachEnterNav(f);
            return f;
        })).setHeader("QTY").setWidth("90px").setFlexGrow(0).setKey("qty");

        // ── Rate ──────────────────────────────────────────────────────────────
        g.addColumn(new ComponentRenderer<>(row -> {
            var f = new NumberField();
            f.addClassName("lig-cell");
            f.getElement().setAttribute("data-lig-col", "2");
            f.setWidthFull();
            f.setMin(0);
            if (row.getRate() != null) f.setValue(row.getRate());
            f.addValueChangeListener(e -> { row.setRate(e.getValue()); refreshRow(row); fireChange(); });
            attachEnterNav(f);
            return f;
        })).setHeader("RATE").setWidth("120px").setFlexGrow(0).setKey("rate");

        // ── Tax ───────────────────────────────────────────────────────────────
        g.addColumn(new ComponentRenderer<>(row -> {
            var cb = new ComboBox<TaxOption>();
            cb.addClassName("lig-cell");
            cb.getElement().setAttribute("data-lig-col", "3");
            cb.setWidthFull();
            cb.setItems(taxOptions);
            cb.setItemLabelGenerator(TaxOption::label);
            cb.setPlaceholder("No Tax");
            preselectTax(cb, row.getTaxLabel());
            cb.addValueChangeListener(e -> {
                var t = e.getValue();
                if (t != null) { row.setTaxLabel(t.label()); row.setTaxRate(t.rate()); }
                else           { row.setTaxLabel(null);      row.setTaxRate(0.0); }
                refreshRow(row);
                fireChange();
            });
            attachEnterNav(cb);
            return cb;
        })).setHeader("TAX").setWidth("130px").setFlexGrow(0).setKey("tax");

        // ── Amount — LitRenderer (client-side template, no server Span object) ─
        g.addColumn(LitRenderer.<LineItemRow>of(
                        "<span class='lig-amount'>${item.amount}</span>")
                .withProperty("amount", row -> fmt(row.getAmount())))
                .setHeader("AMOUNT").setWidth("140px").setFlexGrow(0).setKey("amount");

        // ── Description (details group) ───────────────────────────────────────
        descriptionCol = g.addColumn(new ComponentRenderer<>(row -> {
            var f = new TextField();
            f.addClassName("lig-cell");
            f.getElement().setAttribute("data-lig-col", "5");
            f.setWidthFull();
            f.setPlaceholder("Description…");
            if (row.getDescription() != null) f.setValue(row.getDescription());
            f.addValueChangeListener(e -> { row.setDescription(e.getValue()); fireChange(); });
            attachEnterNav(f);
            return f;
        })).setHeader("DESCRIPTION").setFlexGrow(1).setKey("desc");
        descriptionCol.setVisible(false);

        // ── Account (details group) ───────────────────────────────────────────
        accountCol = g.addColumn(new ComponentRenderer<>(row -> {
            var f = new TextField();
            f.addClassName("lig-cell");
            f.getElement().setAttribute("data-lig-col", "6");
            f.setWidthFull();
            f.setPlaceholder("Account…");
            if (row.getAccount() != null) f.setValue(row.getAccount());
            f.addValueChangeListener(e -> { row.setAccount(e.getValue()); fireChange(); });
            attachEnterNav(f);
            return f;
        })).setHeader("ACCOUNT").setWidth("140px").setFlexGrow(0).setKey("account");
        accountCol.setVisible(false);

        // ── Project (details group) ───────────────────────────────────────────
        projectCol = g.addColumn(new ComponentRenderer<>(row -> {
            var f = new TextField();
            f.addClassName("lig-cell");
            f.getElement().setAttribute("data-lig-col", "7");
            f.setWidthFull();
            f.setPlaceholder("Project…");
            if (row.getProject() != null) f.setValue(row.getProject());
            f.addValueChangeListener(e -> { row.setProject(e.getValue()); fireChange(); });
            attachEnterNav(f);
            return f;
        })).setHeader("PROJECT").setWidth("130px").setFlexGrow(0).setKey("project");
        projectCol.setVisible(false);

        // ── Delete ────────────────────────────────────────────────────────────
        g.addColumn(new ComponentRenderer<>(row -> {
            var btn = Components.button().icon(VaadinIcon.CLOSE_SMALL).tertiary().error().styleName("lig-delete-btn").withClickListener(e -> removeRow(row)).build();
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
            return btn;
        })).setWidth("48px").setFlexGrow(0).setKey("delete");

        // setItems() returns GridListDataView; store it — never call setItems() again.
        // Use dataView.refreshAll() / refreshItem() for all subsequent updates.
        dataView = g.setItems(rows);
        return g;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private Div buildToolbar() {
        Span titleSpan = Components.span().text(title != null ? title : "Line Items").styleName("lig-title").build();

        Button toggleBtn = Components.button().text("Show Details").tertiary().build();
        toggleBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toggleBtn.addClickListener(e -> {
            detailsVisible = !detailsVisible;
            descriptionCol.setVisible(detailsVisible);
            accountCol.setVisible(detailsVisible);
            projectCol.setVisible(detailsVisible);
            toggleBtn.setText(detailsVisible ? "Hide Details" : "Show Details");
        });

        // ── Bulk row count field + Add Rows ───────────────────────────────────
        var rowCountField = new IntegerField();
        rowCountField.addClassName("lig-row-count-field");
        rowCountField.setValue(1);
        rowCountField.setMin(1);
        rowCountField.setMax(50);
        rowCountField.setStepButtonsVisible(true);
        rowCountField.setTitle("Number of rows to content");

        Button addBtn = Components.button().text("Add Rows").icon(VaadinIcon.PLUS).tertiary()
                .withClickListener(e -> {
                    int count = rowCountField.getValue() != null ? rowCountField.getValue() : 1;
                    addRows(count);
                }).build();
        addBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Div actions = Components.div().add(toggleBtn, rowCountField, addBtn).styleName("lig-toolbar-actions").build();

        if (!itemSuggestions.isEmpty()) {
            Button pickBtn = Components.button().text("Pick Items").icon(VaadinIcon.SEARCH).tertiary()
                    .withClickListener(e -> openBulkItemsDialog()).build();
            pickBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            actions.add(pickBtn);
        }

        Div toolbar = Components.div().add(titleSpan, actions).styleName("lig-toolbar").build();
        return toolbar;
    }

    // ── Footer (totals) ───────────────────────────────────────────────────────

    private Div buildFooter() {
        subtotalSpan.addClassName("lig-total-value");
        taxAmountSpan.addClassName("lig-total-value");
        totalSpan.addClassNames("lig-total-value", "lig-grand-total");

        Div footer = Components.div().add(
                footerRow("Subtotal", subtotalSpan),
                footerRow("Tax",      taxAmountSpan),
                footerRow("Total",    totalSpan)
        ).styleName("lig-footer").build();
        return footer;
    }

    private static Div footerRow(String label, Span value) {
        Div row = Components.div().add(Components.span().text(label).build(), value).styleName("lig-footer-row").build();
        return row;
    }

    // ── Mobile cards ──────────────────────────────────────────────────────────

    private void refreshCards() {
        cardsContainer.removeAll();
        rows.forEach(row -> cardsContainer.add(buildCard(row)));
    }

    private Div buildCard(LineItemRow row) {
        Span name = Components.span().text(row.getItemName() != null ? row.getItemName() : "—").styleName("lig-card-name").build();
        Span detail = Components.span().text(buildCardMeta(row)).styleName("lig-card-meta").build();
        Span amount = Components.span().text(fmt(row.getAmount())).styleName("lig-card-amount").build();

        Div left  = Components.div().add(name, detail).styleName("lig-card-left").build();
        Div right = Components.div().add(amount).styleName("lig-card-right").build();
        Div card  = Components.div().add(left, right).styleName("lig-card").build();
        card.addClickListener(e -> openEditSheet(row));
        return card;
    }

    private String buildCardMeta(LineItemRow row) {
        var qty  = row.getQuantity() != null ? row.getQuantity() : 0;
        var rate = row.getRate()     != null ? fmt(row.getRate()) : "—";
        var tax  = row.getTaxLabel() != null ? "  ·  " + row.getTaxLabel() : "";
        return qty + " × " + rate + tax;
    }

    private void openEditSheet(LineItemRow row) {
        var itemCb = new ComboBox<ItemSuggestion>("Item");
        itemCb.setItems(itemSuggestions);
        itemCb.setItemLabelGenerator(ItemSuggestion::name);
        itemCb.setWidthFull();
        preselectItem(itemCb, row.getItemName());

        var qtyField = new IntegerField("Quantity");
        qtyField.setMin(0);
        qtyField.setValue(row.getQuantity() != null ? row.getQuantity() : 1);
        qtyField.setWidthFull();

        var rateField = new NumberField("Rate");
        rateField.setMin(0);
        if (row.getRate() != null) rateField.setValue(row.getRate());
        rateField.setWidthFull();

        var taxCb = new ComboBox<TaxOption>("Tax");
        taxCb.setItems(taxOptions);
        taxCb.setItemLabelGenerator(TaxOption::label);
        taxCb.setWidthFull();
        preselectTax(taxCb, row.getTaxLabel());

        var descField = new TextField("Description");
        descField.setWidthFull();
        if (row.getDescription() != null) descField.setValue(row.getDescription());

        var accountField = new TextField("Account");
        accountField.setWidthFull();
        if (row.getAccount() != null) accountField.setValue(row.getAccount());

        var projectField = new TextField("Project");
        projectField.setWidthFull();
        if (row.getProject() != null) projectField.setValue(row.getProject());

        var form = new FormLayout(itemCb, qtyField, rateField, taxCb, descField,
                accountField, projectField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2));

        var saveBtn = Components.button().text("Save Changes").primary().build();
        saveBtn.addClickListener(e -> {
            var s = itemCb.getValue();
            if (s != null) { row.setItemName(s.name()); row.setSku(s.sku()); }
            row.setQuantity(qtyField.getValue());
            row.setRate(rateField.getValue());
            var t = taxCb.getValue();
            if (t != null) { row.setTaxLabel(t.label()); row.setTaxRate(t.rate()); }
            else           { row.setTaxLabel(null);      row.setTaxRate(0.0); }
            row.setDescription(emptyToNull(descField.getValue()));
            row.setAccount(emptyToNull(accountField.getValue()));
            row.setProject(emptyToNull(projectField.getValue()));
            refreshAll();
        });

        Components.button().text("Delete Row").error().tertiary().withClickListener(e -> removeRow(row)).build();

        Sheet.builder(Sheet.Side.BOTTOM)
                .title(row.getItemName() != null ? row.getItemName() : "Edit Line Item")
                .description("Edit item details, pricing and accounting information.")
                .content(form)
                .fullscreenOnMobile(true)
                .onClose(() -> {})
                .build()
                .open();

        // Note: Save/Delete are inside the form as the Sheet footer slot is handled separately.
        // For production, wire SheetBuilder.footer(...) once API supports it.
    }

    // ── Bulk items dialog ─────────────────────────────────────────────────────

    /**
     * Two-panel bulk content dialog:
     * <ul>
     *   <li><b>Left</b> — searchable catalog list; click to toggle selection.</li>
     *   <li><b>Right</b> — selected items with individual per-item quantity steppers.</li>
     * </ul>
     * Decrementing a quantity to zero deselects the item. "Add Items" creates one row
     * per selected item with its configured quantity in a single batch refresh.
     */
    private void openBulkItemsDialog() {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Add Items in Bulk");
        dialog.setWidth("900px");
        dialog.setCloseOnOutsideClick(true);

        // ── State ─────────────────────────────────────────────────────────────
        var selection    = new LinkedHashMap<ItemSuggestion, Integer>();
        var searchHolder = new String[]{""};
        var renderRef    = new Runnable[1];

        // ── Left panel: search + item list ────────────────────────────────────
        var searchField = new TextField();
        searchField.setWidthFull();
        searchField.setPlaceholder("Type to search or scan the barcode of the item");
        searchField.setClearButtonVisible(true);
        // Debounce: fire only after 300 ms of inactivity — prevents rebuild on every keystroke
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setValueChangeTimeout(300);

        Div searchWrapper = Components.div().add(searchField).styleName("lig-bulk-search-wrapper").build();
        Div itemList = Components.div().styleName("lig-bulk-item-list").build();
        Div leftPanel = Components.div().add(searchWrapper, itemList).styleName("lig-bulk-left").build();

        // ── Right panel: selected items ───────────────────────────────────────
        Span selectedTitle = Components.span().text("Selected Items").styleName("lig-bulk-selected-title").build();
        Span countBadge = Components.span().text("0").styleName("lig-bulk-count-badge").build();
        Span totalQtyLabel = Components.span().text("Total Quantity: 0").styleName("lig-bulk-total-qty").build();

        Div rightHeader = Components.div().add(selectedTitle, countBadge, totalQtyLabel).styleName("lig-bulk-right-header").build();
        Div selectedList = Components.div().styleName("lig-bulk-selected-list").build();
        Div rightPanel = Components.div().add(rightHeader, selectedList).styleName("lig-bulk-right").build();

        // ── Split content ─────────────────────────────────────────────────────
        Div split = Components.div().add(leftPanel, rightPanel).styleName("lig-bulk-split").build();
        dialog.add(split);

        // ── Render logic ──────────────────────────────────────────────────────
        renderRef[0] = () -> {
            // Left: filtered item rows
            itemList.removeAll();
            String filter = searchHolder[0].toLowerCase(Locale.ROOT);
            itemSuggestions.stream()
                    .filter(s -> filter.isEmpty()
                            || s.name().toLowerCase(Locale.ROOT).contains(filter)
                            || s.sku().toLowerCase(Locale.ROOT).contains(filter))
                    .forEach(s -> {
                        boolean sel = selection.containsKey(s);

                        Span nameSpan = Components.span().text(s.name()).styleName("lig-bulk-item-name").build();
                        Span metaSpan = Components.span().text("SKU: " + s.sku() + "  ·  Purchase Rate: $" + fmt(s.defaultRate())).styleName("lig-bulk-item-meta").build();
                        Div textCol = Components.div().add(nameSpan, metaSpan).styleName("lig-bulk-item-text").build();
                        Div itemRow = Components.div().add(textCol).styleName("lig-bulk-item-row").build();
                        if (sel) {
                            itemRow.addClassName("lig-bulk-item-row--selected");
                            var check = VaadinIcon.CHECK_CIRCLE.create();
                            check.addClassName("lig-bulk-item-check");
                            itemRow.add(check);
                        }
                        itemRow.addClickListener(e -> {
                            if (selection.remove(s) == null) selection.put(s, 1);
                            renderRef[0].run();
                        });
                        itemList.add(itemRow);
                    });

            // Right: selected items with individual qty steppers
            selectedList.removeAll();
            selection.forEach((s, qty) -> {
                int[] qHolder = {qty};

                Span nameLabel = Components.span().text("[" + s.sku() + "] " + s.name()).styleName("lig-bulk-selected-name").build();

                Button minusBtn = Components.button().icon(VaadinIcon.MINUS).tertiary().styleName("lig-bulk-qty-btn").build();
                minusBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

                Span qtySpan = Components.span().text(String.valueOf(qHolder[0])).styleName("lig-bulk-qty-value").build();

                Button plusBtn = Components.button().icon(VaadinIcon.PLUS).tertiary().styleName("lig-bulk-qty-btn").build();
                plusBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

                minusBtn.addClickListener(e -> {
                    if (qHolder[0] <= 1) {
                        // deselect when hitting zero
                        selection.remove(s);
                        renderRef[0].run();
                        return;
                    }
                    qHolder[0]--;
                    selection.put(s, qHolder[0]);
                    qtySpan.setText(String.valueOf(qHolder[0]));
                    int total = selection.values().stream().mapToInt(Integer::intValue).sum();
                    countBadge.setText(String.valueOf(selection.size()));
                    totalQtyLabel.setText("Total Quantity: " + total);
                });

                plusBtn.addClickListener(e -> {
                    qHolder[0]++;
                    selection.put(s, qHolder[0]);
                    qtySpan.setText(String.valueOf(qHolder[0]));
                    int total = selection.values().stream().mapToInt(Integer::intValue).sum();
                    totalQtyLabel.setText("Total Quantity: " + total);
                });

                Div stepper = Components.div().add(minusBtn, qtySpan, plusBtn).styleName("lig-bulk-qty-stepper").build();
                Div selectedRow = Components.div().add(nameLabel, stepper).styleName("lig-bulk-selected-row").build();
                selectedList.add(selectedRow);
            });

            // Sync badge + total
            int total = selection.values().stream().mapToInt(Integer::intValue).sum();
            countBadge.setText(String.valueOf(selection.size()));
            totalQtyLabel.setText("Total Quantity: " + total);
        };

        renderRef[0].run();

        searchField.addValueChangeListener(e -> {
            searchHolder[0] = e.getValue() != null ? e.getValue() : "";
            renderRef[0].run();
        });

        // ── Footer ────────────────────────────────────────────────────────────
        var cancelBtn = Components.button().text("Cancel").tertiary().withClickListener(e -> dialog.close()).build();

        var addBtn = Components.button().text("Add Items").icon(VaadinIcon.PLUS).primary().build();
        addBtn.addClickListener(e -> {
            if (selection.isEmpty()) return;
            selection.forEach((s, qty) -> {
                var newRow = new LineItemRow();
                newRow.setItemName(s.name());
                newRow.setSku(s.sku());
                newRow.setRate(s.defaultRate());
                newRow.setQuantity(qty);
                rows.add(newRow);
            });
            refreshAll();
            dialog.close();
        });

        dialog.getFooter().add(cancelBtn, addBtn);
        dialog.open();
    }

    // ── Refresh helpers ───────────────────────────────────────────────────────

    /**
     * Full refresh — used after structural changes (content, remove, setRows, sheet save).
     * Calls {@code dataView.refreshAll()} which tells Vaadin only the data changed;
     * the Grid component itself is NOT replaced.
     */
    private void refreshAll() {
        dataView.refreshAll();
        refreshCards();
        updateTotals();
        fireChange();
    }

    /**
     * Targeted single-row refresh after an inline cell edit.
     * <p>
     * Cost: O(1) — re-renders only this row's cells (via {@code dataView.refreshItem}).
     * Cards are intentionally skipped: mobile users edit via the Sheet form which
     * calls {@link #refreshAll}.
     */
    private void refreshRow(LineItemRow row) {
        dataView.refreshItem(row);
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = rows.stream().mapToDouble(LineItemRow::getSubtotal).sum();
        double taxTotal = rows.stream()
                .mapToDouble(r -> r.getSubtotal() * r.getTaxRate())
                .sum();
        subtotalSpan.setText(fmt(subtotal));
        taxAmountSpan.setText(fmt(taxTotal));
        totalSpan.setText(fmt(subtotal + taxTotal));
    }

    // ── Keyboard navigation ───────────────────────────────────────────────────

    /**
     * Injects a lightweight {@code keydown} listener that intercepts {@code Enter}:
     * – if no ComboBox overlay is open, finds all elements with the same
     *   {@code data-lig-col} value and focuses the next one in DOM order.
     */
    private void attachEnterNav(com.vaadin.flow.component.Component input) {
        input.getElement().executeJs("""
            const el = this;
            el.addEventListener('keydown', function(e) {
                if (e.key !== 'Enter') return;
                if (document.querySelector('vaadin-combo-box-overlay[opened]')) return;
                e.preventDefault();
                e.stopPropagation();
                var col = el.getAttribute('data-lig-col');
                if (!col) return;
                var all = Array.from(document.querySelectorAll('[data-lig-col="' + col + '"]'));
                all.sort(function(a, b) { return (a.compareDocumentPosition(b) & 4) ? -1 : 1; });
                var idx = all.indexOf(el);
                if (idx >= 0 && idx < all.length - 1) {
                    all[idx + 1].focus();
                }
            });
        """);
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

    private void fireChange() {
        if (changeListener != null) changeListener.accept(getRows());
    }

    private static String fmt(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    private static String emptyToNull(String v) {
        return (v == null || v.isBlank()) ? null : v;
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

        private String title = "Line Items";
        private final List<ItemSuggestion> itemSuggestions = new ArrayList<>();
        private final List<TaxOption>      taxOptions      = new ArrayList<>();
        private int initialRows = 1;

        private Builder() {}

        /** Sets the section title shown above the grid. Default: {@code "Line Items"}. */
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
    public com.vaadin.flow.component.Component getComponent() {
        return getContent();
    }
}

























