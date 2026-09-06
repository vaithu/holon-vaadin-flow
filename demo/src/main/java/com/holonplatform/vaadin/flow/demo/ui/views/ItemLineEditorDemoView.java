package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor.Column;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Demo page for {@link ItemLineEditor} — a single generic, domain-agnostic editable line-item
 * list component reused, unmodified, across three unrelated domains:
 * <ol>
 *   <li>Invoice line items (description, quantity, unit price → line total)</li>
 *   <li>Shopping cart lines (product, quantity, unit price → line total; in-memory, no persistence)</li>
 *   <li>Pawn-ticket item appraisal (weight, purity, verification badge → appraised value,
 *       with unverified items excluded from the loan total)</li>
 * </ol>
 * Only the row bean, the {@code addColumn(...)} definitions, the row factory and the
 * {@code footer(...)} totals function differ per example — the component itself is identical.
 */
@PageTitle("ItemLineEditor – Holon Demo")
@Route(value = "item-line-editor", layout = DemoMainLayout.class)
public class ItemLineEditorDemoView extends Div {

    public ItemLineEditorDemoView() {
        addClassName("app-view");

        var title = new H1("ItemLineEditor");
        var desc = new Paragraph(
                "ItemLineEditor<T> is a generic, domain-agnostic editable line-item list: a header "
                        + "with a live count badge, a spreadsheet-style editable grid driven by pluggable "
                        + "column definitions, an \"Add item\" action, per-row removal, an empty-state "
                        + "placeholder, and an optional computed TotalsCard footer. The same component "
                        + "backs invoices, shopping carts and pawn-ticket appraisals below — only the "
                        + "row bean and the column/footer configuration change.");

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(invoiceExample());
        examples.add(cartExample());
        examples.add(pawnAppraisalExample());

        add(title, desc, examples);
    }

    // ── 1. Invoice line items ──��──────────────────────────────────────────────

    public static final class InvoiceLine {
        private String description;
        private Integer quantity = 1;
        private Double unitPrice = 0.0;

        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer v) { this.quantity = v; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double v) { this.unitPrice = v; }

        public double getLineTotal() {
            return (quantity != null ? quantity : 0) * (unitPrice != null ? unitPrice : 0.0);
        }
    }

    private DemoExample invoiceExample() {
        ItemLineEditor<InvoiceLine> editor = ItemLineEditor.builder(InvoiceLine.class)
                .title("Invoice Items")
                .rowFactory(InvoiceLine::new)
                .initialRows(1)
                .addColumn(Column.<InvoiceLine>of("description", "Description", line -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder("Item or service…");
                    if (line.getDescription() != null) f.setValue(line.getDescription());
                    f.addValueChangeListener(e -> line.setDescription(e.getValue()));
                    return f;
                }).flexGrow(2))
                .addColumn(Column.<InvoiceLine>of("qty", "Qty", line -> {
                    var f = new NumberField();
                    f.setWidthFull();
                    f.setMin(0);
                    f.setValue(line.getQuantity() != null ? line.getQuantity().doubleValue() : 1);
                    f.addValueChangeListener(e -> line.setQuantity(
                            e.getValue() != null ? e.getValue().intValue() : null));
                    return f;
                }).width("90px"))
                .addColumn(Column.<InvoiceLine>of("unitPrice", "Unit Price", line -> {
                    var f = new NumberField();
                    f.setWidthFull();
                    f.setMin(0);
                    if (line.getUnitPrice() != null) f.setValue(line.getUnitPrice());
                    f.addValueChangeListener(e -> line.setUnitPrice(e.getValue()));
                    return f;
                }).width("120px"))
                .addColumn(Column.<InvoiceLine>of("total", "Line Total",
                        line -> new Span(fmt(line.getLineTotal()))).width("120px"))
                .footer(lines -> TotalsCard.builder()
                        .row("Total", fmt(lines.stream().mapToDouble(InvoiceLine::getLineTotal).sum()),
                                TotalsRow.Variant.GRAND_TOTAL)
                        .build())
                .build();

        return new DemoExample("Invoice line items", editor.getComponent(), """
                ItemLineEditor<InvoiceLine> invoice = ItemLineEditor.builder(InvoiceLine.class)
                    .title("Invoice Items")
                    .rowFactory(InvoiceLine::new)
                    .initialRows(1)
                    .addColumn(Column.of("description", "Description", line -> descriptionField(line))
                        .flexGrow(2))
                    .addColumn(Column.of("qty", "Qty", line -> qtyField(line)).width("90px"))
                    .addColumn(Column.of("unitPrice", "Unit Price", line -> priceField(line)).width("120px"))
                    .addColumn(Column.of("total", "Line Total",
                        line -> new Span(fmt(line.getLineTotal()))).width("120px"))
                    .footer(lines -> TotalsCard.builder()
                        .row("Total", fmt(lines.stream().mapToDouble(InvoiceLine::getLineTotal).sum()),
                             TotalsRow.Variant.GRAND_TOTAL)
                        .build())
                    .onChange(lines -> saveInvoiceLines(lines))
                    .build();
                """);
    }

    // ── 2. Shopping cart lines ───────────────────────────────────────────────

    public static final class CartLine {
        private String product;
        private Integer quantity = 1;
        private Double unitPrice = 0.0;

        public String getProduct() { return product; }
        public void setProduct(String v) { this.product = v; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer v) { this.quantity = v; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double v) { this.unitPrice = v; }

        public double getLineTotal() {
            return (quantity != null ? quantity : 0) * (unitPrice != null ? unitPrice : 0.0);
        }
    }

    private DemoExample cartExample() {
        ItemLineEditor<CartLine> editor = ItemLineEditor.builder(CartLine.class)
                .title("Shopping Cart")
                .addButtonText("Add product")
                .rowFactory(CartLine::new)
                .emptyState("Your cart is empty", "Add a product to start your order.")
                .emptyIcon(VaadinIcon.CART)
                .addColumn(Column.<CartLine>of("product", "Product", line -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder("Product name…");
                    if (line.getProduct() != null) f.setValue(line.getProduct());
                    f.addValueChangeListener(e -> line.setProduct(e.getValue()));
                    return f;
                }).flexGrow(2))
                .addColumn(Column.<CartLine>of("qty", "Qty", line -> {
                    var f = new NumberField();
                    f.setWidthFull();
                    f.setMin(1);
                    f.setValue(line.getQuantity() != null ? line.getQuantity().doubleValue() : 1);
                    f.addValueChangeListener(e -> line.setQuantity(
                            e.getValue() != null ? e.getValue().intValue() : null));
                    return f;
                }).width("90px"))
                .addColumn(Column.<CartLine>of("unitPrice", "Price", line -> {
                    var f = new NumberField();
                    f.setWidthFull();
                    f.setMin(0);
                    if (line.getUnitPrice() != null) f.setValue(line.getUnitPrice());
                    f.addValueChangeListener(e -> line.setUnitPrice(e.getValue()));
                    return f;
                }).width("120px"))
                .footer(lines -> TotalsCard.builder()
                        .row("Total", fmt(lines.stream().mapToDouble(CartLine::getLineTotal).sum()),
                                TotalsRow.Variant.GRAND_TOTAL)
                        .build())
                .build();

        return new DemoExample("Shopping cart (in-memory, no Datastore)", editor.getComponent(), """
                ItemLineEditor<CartLine> cart = ItemLineEditor.builder(CartLine.class)
                    .title("Shopping Cart")
                    .addButtonText("Add product")
                    .rowFactory(CartLine::new)
                    .emptyState("Your cart is empty", "Add a product to start your order.")
                    .emptyIcon(VaadinIcon.CART)
                    .addColumn(Column.of("product", "Product", line -> productField(line)).flexGrow(2))
                    .addColumn(Column.of("qty", "Qty", line -> qtyField(line)).width("90px"))
                    .addColumn(Column.of("unitPrice", "Price", line -> priceField(line)).width("120px"))
                    .footer(lines -> TotalsCard.builder()
                        .row("Total", fmt(lines.stream().mapToDouble(CartLine::getLineTotal).sum()),
                             TotalsRow.Variant.GRAND_TOTAL)
                        .build())
                    .onChange(lines -> saveCartToSession(lines))   // e.g. a small @SessionScope bean
                    .build();
                """);
    }

    // ── 3. Pawn-ticket item appraisal ────────────────────────────────────────

    public enum Verification { HALLMARK, ACID_TEST, UNVERIFIED }

    public static final class PawnAppraisalItem {
        private String description;
        private Double weightGrams = 0.0;
        private String purity = "14K";
        private Verification verification = Verification.UNVERIFIED;

        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
        public Double getWeightGrams() { return weightGrams; }
        public void setWeightGrams(Double v) { this.weightGrams = v; }
        public String getPurity() { return purity; }
        public void setPurity(String v) { this.purity = v; }
        public Verification getVerification() { return verification; }
        public void setVerification(Verification v) { this.verification = v; }

        private static double purityFraction(String purity) {
            return switch (purity) {
                case "10K" -> 0.417;
                case "18K" -> 0.750;
                case "22K" -> 0.917;
                case "24K" -> 0.999;
                default -> 0.583; // 14K
            };
        }

        /** Appraised value at the given 24K spot rate ($/gram). */
        public double appraisedValue(double spotRate24k) {
            double w = weightGrams != null ? weightGrams : 0.0;
            return w * purityFraction(purity) * spotRate24k;
        }

        public boolean isVerified() {
            return verification == Verification.HALLMARK || verification == Verification.ACID_TEST;
        }
    }

    private DemoExample pawnAppraisalExample() {
        double[] spotRate = {148.00};

        ItemLineEditor<PawnAppraisalItem> editor = ItemLineEditor.builder(PawnAppraisalItem.class)
                .title("Item Appraisal")
                .addButtonText("Add another item")
                .rowFactory(PawnAppraisalItem::new)
                .initialRows(1)
                .emptyState("No items added yet", "Add the gold item being pawned to start the appraisal.")
                .emptyIcon(VaadinIcon.DIAMOND)
                .addColumn(Column.<PawnAppraisalItem>of("description", "Item", line -> {
                    var f = new TextField();
                    f.setWidthFull();
                    f.setPlaceholder("e.g. 14K Gold Ring");
                    if (line.getDescription() != null) f.setValue(line.getDescription());
                    f.addValueChangeListener(e -> line.setDescription(e.getValue()));
                    return f;
                }).flexGrow(2))
                .addColumn(Column.<PawnAppraisalItem>of("weight", "Weight (g)", line -> {
                    var f = new NumberField();
                    f.setWidthFull();
                    f.setMin(0);
                    if (line.getWeightGrams() != null) f.setValue(line.getWeightGrams());
                    f.addValueChangeListener(e -> line.setWeightGrams(e.getValue()));
                    return f;
                }).width("110px"))
                .addColumn(Column.<PawnAppraisalItem>of("purity", "Purity", line -> {
                    var cb = new ComboBox<String>();
                    cb.setWidthFull();
                    cb.setItems("10K", "14K", "18K", "22K", "24K");
                    cb.setValue(line.getPurity());
                    cb.addValueChangeListener(e -> line.setPurity(e.getValue()));
                    return cb;
                }).width("100px"))
                .addColumn(Column.<PawnAppraisalItem>of("verification", "Verification", line -> {
                    var cb = new ComboBox<Verification>();
                    cb.setWidthFull();
                    cb.setItems(Verification.values());
                    cb.setValue(line.getVerification());
                    cb.addValueChangeListener(e -> line.setVerification(
                            e.getValue() != null ? e.getValue() : Verification.UNVERIFIED));
                    return cb;
                }).width("160px"))
                .addColumn(Column.<PawnAppraisalItem>of("appraised", "Appraised", line -> {
                    double value = line.appraisedValue(spotRate[0]);
                    Span span = new Span(fmt(value) + (line.isVerified() ? "" : "  (excl.)"));
                    if (!line.isVerified()) {
                        span.getElement().getStyle().set("color", "var(--lumo-secondary-text-color)");
                    }
                    return span;
                }).width("140px"))
                .footer(lines -> {
                    double total = lines.stream()
                            .filter(PawnAppraisalItem::isVerified)
                            .mapToDouble(l -> l.appraisedValue(spotRate[0]))
                            .sum();
                    long unverifiedCount = lines.stream().filter(l -> !l.isVerified()).count();
                    var builder = TotalsCard.builder()
                            .row("Total appraised value (verified only)", fmt(total),
                                    TotalsRow.Variant.GRAND_TOTAL);
                    if (unverifiedCount > 0) {
                        builder.row(unverifiedCount + " item(s) need verification", "",
                                TotalsRow.Variant.WARNING);
                    }
                    return builder.build();
                })
                .build();

        return new DemoExample("Pawn-ticket item appraisal (unverified items excluded from total)",
                editor.getComponent(), """
                ItemLineEditor<PawnAppraisalItem> appraisal = ItemLineEditor.builder(PawnAppraisalItem.class)
                    .title("Item Appraisal")
                    .addButtonText("Add another item")
                    .rowFactory(PawnAppraisalItem::new)
                    .emptyState("No items added yet",
                                 "Add the gold item being pawned to start the appraisal.")
                    .emptyIcon(VaadinIcon.DIAMOND)
                    .addColumn(Column.of("description", "Item",  line -> descriptionField(line)).flexGrow(2))
                    .addColumn(Column.of("weight", "Weight (g)", line -> weightField(line)).width("110px"))
                    .addColumn(Column.of("purity", "Purity",     line -> purityCombo(line)).width("100px"))
                    .addColumn(Column.of("verification", "Verification",
                        line -> verificationCombo(line)).width("160px"))
                    .addColumn(Column.of("appraised", "Appraised",
                        line -> new Span(fmt(line.appraisedValue(spotRate))
                            + (line.isVerified() ? "" : "  (excl.)"))).width("140px"))
                    // Business rule: unverified items are excluded from the loan total —
                    // ported straight from the original "Item Appraisal" mockup's compliance banner.
                    .footer(lines -> {
                        double total = lines.stream()
                            .filter(PawnAppraisalItem::isVerified)
                            .mapToDouble(l -> l.appraisedValue(spotRate[0]))
                            .sum();
                        long unverified = lines.stream().filter(l -> !l.isVerified()).count();
                        var card = TotalsCard.builder()
                            .row("Total appraised value (verified only)", fmt(total),
                                 TotalsRow.Variant.GRAND_TOTAL);
                        if (unverified > 0) {
                            card.row(unverified + " item(s) need verification", "",
                                     TotalsRow.Variant.WARNING);
                        }
                        return card.build();
                    })
                    .build();
                """);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String fmt(double v) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(v);
    }
}

