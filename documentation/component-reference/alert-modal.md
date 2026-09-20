# AlertModal

Modal alert notification — a {@link Dialog} overlay wrapping an {@link Alert}.

## Class

- **Package:** `com.iyensoft.vaadin.flow.components`
- **Source:** `core/src/main/java/com/iyensoft/vaadin/flow/components/AlertModal.java`
- **Signature:** `public class AlertModal extends Dialog`

## Key APIs

### Factory methods

- `AlertModalBuilder builder()`
- `AlertModalBuilder builder(Alert.Variant variant)`

### Common methods

- `Alert getAlert()`
- `Alert.Variant getVariant()`
- `void setVariant(Alert.Variant variant)`
- `void setIcon(Icon icon)`
- `void clearIcon()`
- `AlertTitle getAlertTitle()`
- `void setTitle(AlertTitle title)`
- `void setTitle(String text)`
- `void setTitle(Localizable localizable)`
- `AlertDescription getAlertDescription()`
- `void setDescription(AlertDescription description)`
- `void setDescription(String text)`

## Usage

```java
AlertModal component; // See source for constructor/builder options
```
