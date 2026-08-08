# AlertDialog

Confirmation alert dialog inspired by shadcn/ui {@code AlertDialog} and Tailwind UI Plus modal dialog patterns.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/AlertDialog.java`
- **Signature:** `public class AlertDialog extends Dialog`

## Key APIs

### Factory methods

- `AlertDialogBuilder builder()`

### Common methods

- `void setHeaderIcon(Component icon)`
- `void setHeaderIcon(Component icon, Alert.Variant variant)`
- `void setSize(Size size)`
- `void setAlignment(Alignment alignment)`
- `void addBodyContent(Component... components)`
- `void clearBodyContent()`
- `void setLoading(boolean loading)`
- `void setFullScreenOnMobile(boolean fullScreen)`
- `void setFooterBackground(boolean enabled)`
- `void setStackedButtons(boolean stacked)`
- `void setAlertRole(boolean alertRole)`
- `void setCancelButtonVisible(boolean visible)`

## Usage

```java
AlertDialog component; // See source for constructor/builder options
```
