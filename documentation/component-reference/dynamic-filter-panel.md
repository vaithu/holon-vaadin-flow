# DynamicFilterPanel

A dynamic, row-based filter builder component that implements {@link FilterInputGroup}.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/DynamicFilterPanel.java`
- **Signature:** `public class DynamicFilterPanel<T> extends Div implements FilterInputGroup`

## Key APIs

### Factory methods

- `DynamicFilterPanel<T> of(Class<T> beanType)`

### Common methods

- `record PropInfo(String name, String label, Class<?> type, Property<?> rawProperty)`
- `String getLabel()`
- `DynamicFilterPanel<PropertyBox> ofProperties(Property<?>... properties)`
- `DynamicFilterPanel<PropertyBox> ofPropertySet(PropertySet<?> propertySet)`
- `void makeDialogResizableAndDraggable(Dialog dialog)`
- `DynamicFilterPanel<T> setMatchAll(boolean matchAll)`
- `DynamicFilterPanel<T> setAdvancedMode(boolean advancedMode)`
- `DynamicFilterPanel<T> setItems(String propertyName, List<V> items)`
- `DynamicFilterPanel<T> setLazyItems(String propertyName, CallbackDataProvider.FetchCallback<V, String> fetchCallback, CallbackDataProvider.CountCallback<V, String> countCallback)`
- `Optional<QueryFilter> getQueryFilter()`
- `boolean isAnyActive()`
- `int getActiveFilterCount()`

## Usage

```java
DynamicFilterPanel component; // See source for constructor/builder options
```
