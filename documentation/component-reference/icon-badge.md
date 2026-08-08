# IconBadge

Circular tinted icon badge — a {@link Div} wrapper that renders a vaadin-icon inside a round, semantically-colored circle, matching the AlertDialog header icon pattern and the shadcn/ui icon-badge convention.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/IconBadge.java`
- **Signature:** `public class IconBadge extends Div`

## Key APIs

### Factory methods

- `IconBadgeBuilder builder()`
- `IconBadgeBuilder builder(Icon icon)`
- `IconBadgeBuilder builder(Icon icon, Alert.Variant variant)`
- `IconBadgeBuilder builder(Icon icon, Alert.Variant variant, Size size)`
- `IconBadgeBuilder builder(Icon icon, Alert.Variant variant, Size size, String text)`
- `IconBadge of(Component icon)`
- `IconBadge of(Component icon, Alert.Variant variant)`
- `IconBadge of(Component icon, Alert.Variant variant, Size size)`

### Common methods

- `String getCssClass()`
- `Alert.Variant getVariant()`
- `void setVariant(Alert.Variant variant)`
- `String getText()`
- `void setText(String text)`
- `void clearText()`
- `IconBadge text(String text)`
- `Size getBadgeSize()`
- `boolean hasText()`
- `void setBadgeSize(Size size)`
- `void setIcon(Icon icon)`
- `void setIcon(VaadinIcon icon)`

## Usage

```java
IconBadge component; // See source for constructor/builder options
```
