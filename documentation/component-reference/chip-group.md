# ChipGroup

A flex row of mutually exclusive {@link Chip}s that behaves like a radio-button group.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/ChipGroup.java`
- **Signature:** `public class ChipGroup extends Div`

## Key APIs

### Factory methods

- `ChipGroup create()`

### Common methods

- `ChipGroup addChip(Chip chip)`
- `ChipGroup addChip(Chip chip, boolean active)`
- `ChipGroup addChip(String label)`
- `ChipGroup addChip(String label, boolean active)`
- `ChipGroup addChip(String label, long count)`
- `ChipGroup addChip(String label, long count, boolean active)`
- `ChipGroup onSelect(ComponentEventListener<SelectionEvent> listener)`
- `ChipGroup build()`
- `ChipGroup wrap()`
- `void select(int index)`
- `void select(Chip chip)`
- `Chip getActiveChip()`

## Usage

```java
ChipGroup component; // See source for constructor/builder options
```
