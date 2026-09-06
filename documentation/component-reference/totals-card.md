# TotalsCard

A generic summary/totals card made of stacked label/value [`TotalsRow`](totals-row.md)s,
optionally ending with an emphasised grand-total row. Fully driven by CSS
(`totals-card.css`); no inline styles or Lumo tokens are used.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TotalsCard.java`
- **Signature:** `public class TotalsCard extends Div`

## Key APIs

### Factory methods

- `TotalsCardBuilder builder()`

### Common methods

- `List<TotalsRow> getRows()`
- `void addRow(TotalsRow row)`
- `void addRow(String label, String value)`
- `void addRow(String label, String value, TotalsRow.Variant variant)`
- `void addRow(Localizable label, Localizable value, TotalsRow.Variant variant)`
- `void removeRow(TotalsRow row)`
- `void clearRows()`

### Appraisal variant methods (`TotalsCard.Variant.APPRAISAL`)

- `TotalsCard setVariant(TotalsCard.Variant variant)` / `TotalsCard.Variant getVariant()`
- `TotalsCard setEyebrow(String text)` / `setEyebrow(Localizable text)`
- `TotalsCard setHighlight(String caption, String initialValue, String suffix, Consumer<String> onValueChange)`
- `TotalsCard setGauge(TotalsGauge gauge)` / `setGauge(String amount, String label, double percent)` / `TotalsGauge getGauge()`
- `TotalsCard setSlider(String label, int min, int max, int value, IntConsumer onChange)`
- `TotalsCard setToggleGroup(List<String> options, int selectedIndex, IntConsumer onSelectionChange)`
- `TotalsCard setTermsNote(String text)` / `setTermsNote(Localizable text)`
- `TotalsCard setPrimaryAction(String label, Runnable onClick)`
- `TotalsCard setSecondaryAction(String label, Runnable onClick)`

All of the above are also exposed as fluent `TotalsCardConfigurator`/`TotalsCardBuilder` methods
(`variant(...)`, `eyebrow(...)`, `highlight(...)`, `gauge(...)`, `slider(...)`, `toggleGroup(...)`,
`termsNote(...)`, `primaryAction(...)`, `secondaryAction(...)`).

## Usage

```java
TotalsCard totals = TotalsCard.builder()
    .row("Revenue YTD", "€1,420,400")
    .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
    .row("Net revenue YTD", "€1,278,360")
    .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
    .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
    .build();
```

To mute a row's value independently of its variant (e.g. an informational, non-monetary-total row):

```java
totals.getRows().get(3).setValueMuted(true);
```

### Appraisal variant usage

`TotalsCard.Variant.APPRAISAL` turns the card into a dark, self-contained "live appraisal" summary:
an eyebrow + editable highlight field header, an optional [`TotalsGauge`](totals-gauge.md) ring, the
usual rows, an LTV-style slider, a mutually-exclusive term toggle (built on `ChipGroup`), and up to
two stacked action buttons. Sections appear in the DOM in the order their configuration method is
first invoked, so call them in the natural top-to-bottom order:

```java
TotalsCard appraisal = TotalsCard.builder()
    .variant(TotalsCard.Variant.APPRAISAL)
    .eyebrow("Live Appraisal")
    .highlight("24K spot rate", "148.00", "/g", rate -> recompute(rate))
    .gauge("$1,475", "Offer @ 65% LTV", 65)
    .row("Total appraised value", "$2,289.89")
    .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.DISCOUNT)
    .row("Net appraised value", "$2,266.41")
    .row("Loan offer", "$1,475.00", TotalsRow.Variant.GRAND_TOTAL)
    .slider("LTV", 10, 80, 65, ltv -> recompute(ltv))
    .toggleGroup(List.of("15 Days", "30 Days", "60 Days"), 1, days -> setTerm(days))
    .termsNote("Interest accrues monthly at 4.0%.")
    .primaryAction("Approve & Continue to Signing", this::approve)
    .secondaryAction("Reset Ticket", this::reset)
    .build();
```

## Related

- [`TotalsRow`](totals-row.md)
- [`TotalsLabel`](totals-label.md)
- [`TotalsValue`](totals-value.md)
- [`TotalsGauge`](totals-gauge.md)

