# ItemListingPaginationBar

Paginated navigation bar for {@link ItemListing}, built on the shadcn/ui-inspired {@link Pagination} component family.

## Class

- **Package:** `com.holonplatform.vaadin.flow.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/components/ItemListingPaginationBar.java`
- **Signature:** `public class ItemListingPaginationBar<T, P> extends Pagination`

## Key APIs

### Common methods

- `void refreshState()`
- `void goToPage(int page)`
- `void goToFirstPage()`
- `void goToPreviousPage()`
- `void goToNextPage()`
- `void goToLastPage()`
- `void setHasNextPage(boolean hasNextPage)`
- `boolean isHasNextPage()`
- `void seedTotalPagesFromCount(int totalItems, int pageSize)`
- `Registration addPageChangeListener(IntConsumer listener)`
- `void setPageSize(int pageSize)`
- `int getPageSize()`

## Usage

```java
ItemListingPaginationBar component; // See source for constructor/builder options
```
