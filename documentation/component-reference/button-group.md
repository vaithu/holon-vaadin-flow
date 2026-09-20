# ButtonGroup

A visually unified group of {@link Button} instances — borders between adjacent buttons are merged and corner radius is applied only to the outermost edges, creating a single cohesive control.

## Class

- **Package:** `com.iyensoft.vaadin.flow.components`
- **Source:** `core/src/main/java/com/iyensoft/vaadin/flow/components/ButtonGroup.java`
- **Signature:** `public class ButtonGroup extends Div`

## Key APIs

### Factory methods

- `ButtonGroupBuilder builder()`

### Common methods

- `void add(Button... buttons)`
- `void remove(Button... buttons)`
- `void setOrientation(Orientation orientation)`
- `ButtonGroupConfigurator.BaseButtonGroupConfigurator configure(ButtonGroup buttonGroup)`

## Usage

```java
ButtonGroup component; // See source for constructor/builder options
```
