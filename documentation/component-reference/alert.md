# Alert

Contextual alert component inspired by shadcn/ui Alert.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/Alert.java`
- **Signature:** `public class Alert extends Div`

## Key APIs

### Factory methods

- `AlertBuilder builder()`
- `AlertBuilder builder(Variant variant)`

### Common methods

- `String getCssModifier()`
- `boolean isDefault()`
- `String getCssClass(String componentPrefix)`
- `String getCssClass(String componentPrefix, String separator)`
- `String getCssClass()`
- `Variant getVariant()`
- `void setVariant(Variant variant)`
- `void setIcon(Icon icon)`
- `void clearIcon()`
- `AlertTitle getAlertTitle()`
- `void setTitle(AlertTitle title)`
- `void setTitle(String text)`

## Usage

```java
Alert component; // See source for constructor/builder options
```
