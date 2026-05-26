package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.util.UUID;

/**
 * Mutable row bean for invoice / purchase-order / quote line items.
 *
 * <p><b>Computed properties (read-only):</b>
 * <ul>
 *   <li>{@link #getAmount()}  = quantity × rate × (1 + taxRate)</li>
 *   <li>{@link #getSubtotal()} = quantity × rate</li>
 * </ul>
 *
 * <p>Designed to be used with {@link LineItemGrid}.
 *
 * @since 10.0.0
 */
public class LineItemRow {

    private final String  id          = UUID.randomUUID().toString();
    private String        itemName;
    private String        sku;
    private String        description;
    private Integer       quantity    = 1;
    private Double        rate;
    private String        taxLabel;
    private double        taxRate     = 0.0;
    private String        account;
    private String        project;

    // ── Getters ───────────────────────────────────────────────────────────────

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

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setItemName(String v)    { this.itemName    = v; }
    public void setSku(String v)         { this.sku         = v; }
    public void setDescription(String v) { this.description = v; }
    public void setQuantity(Integer v)   { this.quantity    = v; }
    public void setRate(Double v)        { this.rate        = v; }
    public void setTaxLabel(String v)    { this.taxLabel    = v; }
    public void setTaxRate(double v)     { this.taxRate     = v; }
    public void setAccount(String v)     { this.account     = v; }
    public void setProject(String v)     { this.project     = v; }

    // ── Computed (read-only) ──────────────────────────────────────────────────

    /** Line total including tax: {@code quantity × rate × (1 + taxRate)}. */
    public double getAmount() {
        if (quantity == null || rate == null) return 0.0;
        return quantity * rate * (1.0 + taxRate);
    }

    /** Raw subtotal before tax: {@code quantity × rate}. */
    public double getSubtotal() {
        if (quantity == null || rate == null) return 0.0;
        return quantity * rate;
    }

    /** Tax portion of the line: {@code subtotal × taxRate}. */
    public double getTaxAmount() {
        return getSubtotal() * taxRate;
    }
}

