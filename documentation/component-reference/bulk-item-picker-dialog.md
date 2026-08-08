# BulkItemPickerDialog

Two-panel dialog for adding multiple items in bulk with configurable quantities.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/BulkItemPickerDialog.java`
- **Signature:** `public class BulkItemPickerDialog extends Dialog`

## Key APIs

### Factory methods

- `BulkItemPickerDialogBuilder builder()`

### Common methods

- `void setItems(Collection<BulkPickerItem> items)`
- `void setItemProvider(Function<String, List<BulkPickerItem>> provider)`
- `void setPagedProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider, Function<String, Long> countProvider)`
- `void setPagedProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider)`
- `void setPageSize(int pageSize)`
- `void addItem(BulkPickerItem item)`
- `void setTitle(String title)`
- `void setSearchPlaceholder(String placeholder)`
- `void setAddButtonText(String text)`
- `void setCancelButtonText(String text)`
- `void setConfirmCallback(Consumer<List<BulkPickerEntry>> callback)`
- `void setCancelCallback(Runnable callback)`

## Usage

```java
BulkItemPickerDialog component; // See source for constructor/builder options
```
