# TotalsValue

Value slot of a [`TotalsRow`](totals-row.md). Supports plain text, Holon `Localizable`
(resolved on attach and on explicit set), and arbitrary child components. Can be
individually muted via `setMuted(boolean)`, independently of the owning row's variant.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TotalsValue.java`
- **Signature:** `public class TotalsValue extends Span`

## Key APIs

- `TotalsValue(String text)`
- `TotalsValue(Localizable localizable)`
- `TotalsValue(Component... components)`
- `void setLocalizableText(Localizable localizable)`
- `void setMuted(boolean muted)`

## Usage

```java
TotalsValue value = new TotalsValue("+ €24,000/yr");
value.setMuted(true);
```

