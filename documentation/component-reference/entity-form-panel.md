# EntityFormPanel

A full-featured form panel that wraps a {@link BeanPropertyInputForm} or a {@link PropertyInputForm} and provides a standard button footer.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/EntityFormPanel.java`
- **Signature:** `public class EntityFormPanel<T> extends Div`

## Key APIs

### Factory methods

- `PropertyBuilder properties(PropertySet<?> propertySet)`
- `DivPropertyBuilder propertiesDiv(PropertySet<?> propertySet)`
- `PropertyBuilder properties(Property<?>... properties)`
- `DivPropertyBuilder propertiesDiv(Property<?>... properties)`

### Common methods

- `PropertyInputForm getForm()`
- `Button getSaveButton()`
- `Optional<Button> getSaveAndNewButton()`
- `Button getClearButton()`
- `Optional<Button> getCancelButton()`
- `void setAutoRequiredIndicators(boolean autoRequiredIndicators)`
- `boolean isAutoRequiredIndicators()`
- `void setBean(T bean)`
- `BeanBuilder<T> bean(Class<T> beanClass)`
- `DivBeanBuilder<T> beanDiv(Class<T> beanClass)`
- `BeanBuilder<T> configure(Consumer<BeanPropertyInputFormBuilder<C, T>> config)`
- `BeanBuilder<T> initializer(Consumer<C> initializer)`

## Usage

```java
EntityFormPanel component; // See source for constructor/builder options
```
