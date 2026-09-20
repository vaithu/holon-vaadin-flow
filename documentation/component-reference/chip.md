# Chip

Interactive pill-shaped filter chip rendered as a native {@code <button>}.

## Class

- **Package:** `com.iyensoft.vaadin.flow.components`
- **Source:** `core/src/main/java/com/iyensoft/vaadin/flow/components/Chip.java`
- **Signature:** `public class Chip extends Component implements ClickNotifier<Chip>`

## Key APIs

### Factory methods

- `Chip of(String label)`
- `Chip of(String label, long count)`

### Common methods

- `Chip withCount(long count)`
- `void setCount(long count)`
- `Chip active(boolean active)`
- `boolean isActive()`
- `String getLabel()`
- `void setLabel(String label)`
- `Chip small()`
- `Chip large()`
- `Chip enabled(boolean enabled)`

## Usage

```java
Chip component; // See source for constructor/builder options
```
