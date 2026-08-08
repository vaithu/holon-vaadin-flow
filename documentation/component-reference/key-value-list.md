# KeyValueList

Container for {@link KeyValueItem} rows rendered as a flat 3-column CSS grid: <pre> [key]   [:]   [value] [key]   [:]   [value] ...

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/KeyValueList.java`
- **Signature:** `public class KeyValueList extends Composite<Div>`

## Key APIs

### Factory methods

- `KeyValueList from(T bean)`
- `KeyValueList from(T bean, String... excludeFields)`

### Common methods

- `KeyValueList addItem(KeyValueItem item)`
- `KeyValueList removeItem(KeyValueItem item)`
- `KeyValueList clearItems()`
- `KeyValueList addFromBean(T bean)`
- `KeyValueList addFromBean(T bean, String... excludeFields)`
- `List<KeyValueItem> getItems()`

## Usage

```java
KeyValueList component; // See source for constructor/builder options
```
