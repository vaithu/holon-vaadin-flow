# LineItemGrid

Keyboard-centric inline spreadsheet for document line items (invoices, POs, quotes).

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/LineItemGrid.java`
- **Signature:** `public class LineItemGrid extends Composite<Div> implements HasComponent`

## Key APIs

### Factory methods

- `Builder builder()`

### Common methods

- `String getId()`
- `String getItemName()`
- `String getSku()`
- `String getDescription()`
- `Integer getQuantity()`
- `Double getRate()`
- `String getTaxLabel()`
- `double getTaxRate()`
- `String getAccount()`
- `String getProject()`
- `void setItemName(String v)`
- `void setSku(String v)`

## Usage

```java
LineItemGrid component; // See source for constructor/builder options
```
