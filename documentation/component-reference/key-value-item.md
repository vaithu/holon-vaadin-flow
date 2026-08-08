# KeyValueItem

Responsive key-value row that flattens into the parent {@link KeyValueList} three-column CSS grid (<em>key-col | sep | value-col</em>) via {@code display:contents}.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/KeyValueItem.java`
- **Signature:** `public class KeyValueItem extends Div implements HasTooltip`

## Key APIs

### Factory methods

- `KeyValueItem of(String key, String value)`
- `KeyValueItem of(String key, Component value)`
- `KeyValueItem of(Localizable key, Localizable value)`
- `KeyValueItem of(Localizable key, Component value)`
- `Builder builder()`

### Common methods

- `KeyValueItem setKey(String text)`
- `KeyValueItem setKey(Localizable text)`
- `String getKey()`
- `Span getKeyComponent()`
- `KeyValueItem setSuperText(String text)`
- `KeyValueItem setSuperText(Localizable text)`
- `String getSuperText()`
- `KeyValueItem setValue(String text)`
- `KeyValueItem setValue(Localizable text)`
- `KeyValueItem setValue(Component component)`
- `Div getValueContainer()`
- `KeyValueItem setSubText(String text)`

## Usage

```java
KeyValueItem component; // See source for constructor/builder options
```
