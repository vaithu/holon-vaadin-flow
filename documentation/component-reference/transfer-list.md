# TransferList

Dual-panel shuttle component for moving {@link TransferItem} items between an "Available" source list (left) and a "Selected" target list (right).

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TransferList.java`
- **Signature:** `public class TransferList extends Div`

## Key APIs

### Factory methods

- `TransferListBuilder builder()`

### Common methods

- `void setAvailableItems(Collection<TransferItem> items)`
- `void setSelectedItems(Collection<TransferItem> items)`
- `void setAvailableTitle(String title)`
- `void setSelectedTitle(String title)`
- `List<TransferItem> getAvailableItems()`
- `List<TransferItem> getSelectedItems()`
- `Registration addTransferListener(ComponentEventListener<TransferEvent> listener)`
- `void moveHighlightedRight()`
- `void moveAllRight()`
- `void moveHighlightedLeft()`
- `void moveAllLeft()`
- `List<TransferItem> getAvailableItems()`

## Usage

```java
TransferList component; // See source for constructor/builder options
```
