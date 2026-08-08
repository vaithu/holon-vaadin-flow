package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.util.UUID;

/**
 * Domain model for a single item row in a pawn-broking transaction.
 *
 * <p>This is deliberately <strong>different</strong> from {@link LineItemGrid.LineItemRow}
 * (qty × rate → amount) to illustrate why {@link com.holonplatform.vaadin.flow.components.BeanListing}
 * is more flexible than a hardcoded component: you just supply a different bean class and configure
 * the relevant columns via the fluent builder. No new wrapper component is needed.</p>
 *
 * <h3>Business rules</h3>
 * <ul>
 *   <li><b>Interest</b> = loanAmount × monthlyRate × (loanDays / 30)</li>
 *   <li><b>Total Due</b> = loanAmount + interest</li>
 *   <li><b>LTV %</b> = (loanAmount / appraisedValue) × 100 — typically capped at 60–70 %</li>
 * </ul>
 *
 * @since 10.0.0
 */
public class PawnItemRow {

    // ── Enums ─────────────────────────────────────────────────────────────────

    /** Broad item categories used in pawn transactions. */
    public enum Category {
        JEWELRY("Jewelry"),
        ELECTRONICS("Electronics"),
        WATCHES("Watches"),
        TOOLS("Tools"),
        MUSICAL_INSTRUMENTS("Musical Instruments"),
        COLLECTIBLES("Collectibles"),
        SPORTING_GOODS("Sporting Goods"),
        OTHER("Other");

        private final String label;

        Category(String label) { this.label = label; }

        public String getLabel() { return label; }

        @Override public String toString() { return label; }
    }

    /** Physical condition of the pawned item — directly influences the loan offer. */
    public enum Condition {
        EXCELLENT("Excellent — like new"),
        GOOD("Good — minor wear"),
        FAIR("Fair — visible wear"),
        POOR("Poor — significant damage");

        private final String label;

        Condition(String label) { this.label = label; }

        public String getLabel() { return label; }

        @Override public String toString() { return label; }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final String id = UUID.randomUUID().toString();

    /** Free-text description (e.g. "18K yellow-gold diamond solitaire ring"). */
    private String description;

    /** Item category. */
    private Category category = Category.OTHER;

    /** Physical condition. */
    private Condition condition = Condition.GOOD;

    /** Serial number, IMEI, certificate number, or hallmark reference. */
    private String serialNumber;

    /** Appraised market value assessed by the pawnbroker (pre-loan). */
    private Double appraisedValue;

    /**
     * Amount the pawnbroker is willing to lend.
     * Should be ≤ {@link #getLtvMaxPercent()} % of {@link #appraisedValue}.
     */
    private Double loanAmount;

    /**
     * Monthly interest rate expressed as a decimal (e.g. {@code 0.15} = 15 % / month).
     * Default is 15 % — adjust per jurisdiction and risk.
     */
    private Double monthlyRate = 0.15;

    /**
     * Loan duration in days (typically 30, 60, or 90 days).
     * Fractional months are handled: {@code interest = loan × rate × (days/30)}.
     */
    private Integer loanDays = 30;

    /**
     * Suggested maximum LTV % for this category.
     * Exposed as a property so {@code BeanListing.valueProvider} can format / colour-code it.
     */
    private double ltvMaxPercent = 60.0;

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String   getId()             { return id; }

    public String   getDescription()    { return description; }
    public void     setDescription(String v) { this.description = v; }

    public Category getCategory()       { return category; }
    public void     setCategory(Category v) { this.category = v; }

    public Condition getCondition()     { return condition; }
    public void      setCondition(Condition v) { this.condition = v; }

    public String   getSerialNumber()   { return serialNumber; }
    public void     setSerialNumber(String v) { this.serialNumber = v; }

    public Double   getAppraisedValue() { return appraisedValue; }
    public void     setAppraisedValue(Double v) { this.appraisedValue = v; }

    public Double   getLoanAmount()     { return loanAmount; }
    public void     setLoanAmount(Double v) { this.loanAmount = v; }

    public Double   getMonthlyRate()    { return monthlyRate; }
    public void     setMonthlyRate(Double v) { this.monthlyRate = v; }

    public Integer  getLoanDays()       { return loanDays; }
    public void     setLoanDays(Integer v) { this.loanDays = v; }

    public double   getLtvMaxPercent()  { return ltvMaxPercent; }
    public void     setLtvMaxPercent(double v) { this.ltvMaxPercent = v; }

    // ── Computed getters (read-only in BeanListing) ───────────────────────────

    /**
     * Interest charge: {@code loanAmount × monthlyRate × (loanDays / 30)}.
     * Returns {@code 0.0} if any required field is null.
     */
    public double getMonthlyInterest() {
        if (loanAmount == null || monthlyRate == null || loanDays == null) return 0.0;
        return loanAmount * monthlyRate * (loanDays / 30.0);
    }

    /**
     * Total amount the customer must repay: {@code loanAmount + interest}.
     */
    public double getTotalDue() {
        if (loanAmount == null) return 0.0;
        return loanAmount + getMonthlyInterest();
    }

    /**
     * Current Loan-to-Value ratio as a percentage.
     * Returns {@code 0.0} if appraised value is null or zero.
     */
    public double getLtvPercent() {
        if (loanAmount == null || appraisedValue == null || appraisedValue <= 0) return 0.0;
        return loanAmount / appraisedValue * 100.0;
    }

    /**
     * Returns {@code true} if the current loan exceeds the max LTV for this category,
     * signaling a risk flag (typically highlighted in UI).
     */
    public boolean isLtvExceeded() {
        return getLtvPercent() > ltvMaxPercent;
    }
}
