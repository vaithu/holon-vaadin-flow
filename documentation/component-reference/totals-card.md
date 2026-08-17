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

## Related

- [`TotalsRow`](totals-row.md)
- [`TotalsLabel`](totals-label.md)
- [`TotalsValue`](totals-value.md)

