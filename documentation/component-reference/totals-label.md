# TotalsLabel

Label slot of a [`TotalsRow`](totals-row.md). Supports plain text, Holon `Localizable`
(resolved on attach and on explicit set), and arbitrary child components.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TotalsLabel.java`
- **Signature:** `public class TotalsLabel extends Span`

## Key APIs

- `TotalsLabel(String text)`
- `TotalsLabel(Localizable localizable)`
- `TotalsLabel(Component... components)`
- `void setLocalizableText(Localizable localizable)`

## Usage

```java
TotalsLabel label = new TotalsLabel("Net revenue YTD");
```

