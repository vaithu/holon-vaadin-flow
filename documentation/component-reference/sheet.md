# Sheet

A mobile-first slide-in panel inspired by shadcn/ui {@code Sheet}.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/Sheet.java`
- **Signature:** `public class Sheet extends Div`

## Key APIs

### Factory methods

- `SheetBuilder builder()`
- `SheetBuilder builder(Side side)`

### Common methods

- `String getCssClass()`
- `void open()`
- `void close()`
- `boolean isOpen()`
- `void detach()`
- `void closeFromHistory()`
- `Side getSide()`
- `void setSide(Side side)`
- `SheetTitle getSheetTitle()`
- `void setTitle(SheetTitle title)`
- `void setTitle(String text)`
- `void setTitle(Localizable localizable)`

## Usage

```java
Sheet component; // See source for constructor/builder options
```
