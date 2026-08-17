# TotalsRow

A single label/value row of a [`TotalsCard`](totals-card.md). Composition:
[`TotalsLabel`](totals-label.md) + [`TotalsValue`](totals-value.md).

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TotalsRow.java`
- **Signature:** `public class TotalsRow extends Div`

## Variant

| Variant | CSS modifier | Effect |
|---|---|---|
| `DEFAULT` | `totals-card__row--default` (n/a, no modifier applied) | Neutral row |
| `DISCOUNT` | `totals-card__row--discount` | Value rendered in the success colour |
| `WARNING` | `totals-card__row--warning` | Value rendered in the warning colour |
| `GRAND_TOTAL` | `totals-card__row--grand` | Bold, larger font, top border, primary-coloured value |

## Key APIs

- `TotalsRow(String label, String value)`
- `TotalsRow(String label, String value, Variant variant)`
- `TotalsRow(Localizable label, Localizable value, Variant variant)`
- `Variant getVariant()` / `void setVariant(Variant variant)`
- `TotalsLabel getLabel()` / `void setLabel(...)`
- `TotalsValue getValue()` / `void setValue(...)`
- `void setValueMuted(boolean muted)`

## Usage

```java
TotalsRow row = new TotalsRow("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING);
row.setValueMuted(false);
```

