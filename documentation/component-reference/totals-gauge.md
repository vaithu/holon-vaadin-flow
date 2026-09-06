# TotalsGauge

A circular donut-style gauge showing a big centred amount, a small caption label, and a
percentage-driven ring fill (e.g. an offer amount at a given loan-to-value percentage).

Typically embedded inside a [`TotalsCard`](totals-card.md) configured with
`TotalsCard.Variant.APPRAISAL`, but it is a fully standalone component and can be used on its own.
Fully driven by CSS (`totals-gauge.css`); no inline styles or Lumo tokens are used.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TotalsGauge.java`
- **Signature:** `public class TotalsGauge extends Div`

## Key APIs

### Constructors

- `TotalsGauge()`
- `TotalsGauge(String amount, String label, double percent)`

### Common methods

- `TotalsGauge setAmount(String amount)` / `setAmount(Localizable amount)`
- `TotalsGauge setLabel(String label)` / `setLabel(Localizable label)`
- `TotalsGauge setPercent(double percent)` — clamped to `[0, 100]`
- `double getPercent()`

## Usage

```java
TotalsGauge gauge = new TotalsGauge("$1,475", "Offer @ 65% LTV", 65);
add(gauge);

// Later, react to a slider change:
gauge.setPercent(70).setLabel("Offer @ 70% LTV");
```

### Inside a TotalsCard

```java
TotalsCard.builder()
    .variant(TotalsCard.Variant.APPRAISAL)
    .gauge("$1,475", "Offer @ 65% LTV", 65)
    .build();
```

## Styling

The ring fill is driven by the `--totals-gauge-percent` CSS custom property (0-100). Colours are
exposed as overridable CSS custom properties in `totals-gauge.css`:

- `--totals-gauge-fill-start`, `--totals-gauge-fill-end` — ring gradient colours
- `--totals-gauge-track-color` — unfilled ring track colour
- `--totals-gauge-bg` — donut hole colour; override to match the enclosing surface
- `--totals-gauge-amount-color`, `--totals-gauge-label-color` — text colours
- `--totals-gauge-size`, `--totals-gauge-thickness` — ring dimensions

## Related

- [`TotalsCard`](totals-card.md)

