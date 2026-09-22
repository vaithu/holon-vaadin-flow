# "Optimizing Sluggish UI" (Vaadin 8 article) — Applied to `iyen-core`

Source: https://vaadin.com/docs/v8/framework/articles/OptimizingSluggishUI

That article targets Vaadin 7/8 Framework (`VerticalLayout`/`HorizontalLayout`/`Table`/
`CustomLayout`), but its underlying principles translate directly to Vaadin Flow /
`iyen-core`, a large (300+ class) UI component library built on `Div`/`Composite`. Each
tip below is mapped to the Flow equivalent, with the module audited against it.

## Tips and how `iyen-core` fares

| # | v8 tip | Flow equivalent | Status in `iyen-core` |
|---|--------|------------------|------------------------|
| 1 | Render less components | Don't rebuild a whole subtree when only one item's state changed | ⚠️ **Fixed** — see below |
| 2 | Keep the component tree flat | Avoid `Div`-in-`Div`/`VerticalLayout`-in-`VerticalLayout` wrapping with no layout purpose | ✅ Mostly flat; builder classes (`Components.div()`, `ResponsiveDiv`) build single-level rows |
| 3 | Use the right component | Prefer `Div` over `VerticalLayout`/`HorizontalLayout` unless spacing/alignment/expand-ratio is actually used | ✅ Components already favor `Div` (e.g. `GridToolbar`, `TransferList`, `BulkItemPickerDialog` are `Div`-based); the few `VerticalLayout` uses (e.g. `ListingBundle` sort/columns dialogs) do use `spacing()`/`padding()` |
| 4 | Use Grid/Table efficiently (lazy loading, light renderers) | Lazy `DataProvider`, avoid heavy per-cell `ComponentRenderer` where avoidable | ✅ `ListingBundle`/`PropertyListing` rely on Holon's lazy `DataProvider` support; `BulkItemPickerDialog` already supports a paged/offset-limit provider mode so only one page of rows is ever rendered; `LineItemGrid`/`ItemLineEditor` is an editable spreadsheet (not a `Grid`) capped at `MAX_ROWS = 40`, so per-row editable components (`ComboBox`, `IntegerField`, …) stay bounded |
| 5 | Avoid complex custom layouts (e.g. HTML-table-based `CustomLayout`) | N/A in Flow (no `CustomLayout`); avoid ad-hoc nested `Div` grids for what CSS Grid/Flexbox can do in one level | ✅ `ResponsiveDiv`/CSS classes (`*.css` companion files) are used instead of Java-side nested layout components |
| 6 | Use a light theme | Keep CSS/theme small, avoid heavy per-component styling overhead | ✅ Styling is externalized to per-component `@StyleSheet("context://*.css")` files rather than inline styles, keeping the client-side style payload cacheable and minimal |
| 7 | Use captions/icons/tooltips moderately | Don't attach icon+tooltip+ariaLabel to components that are hidden/disabled by default | ✅ Optional toolbar features (filter UI in `GridToolbar`) are built lazily in `enableFilterUi()`, only when the feature flag is turned on, not unconditionally for every instance |

## Fix applied: `TransferList` — Tip #1 "Render less components"

**File:** `iyen-core/src/main/java/com/iyensoft/vaadin/flow/components/TransferList.java`

### Before
Clicking a single row to highlight/unhighlight it called `renderAvailableList()` /
`renderSelectedList()`, which did:

```java
availableListDiv.removeAll();               // destroy every row's DOM node
availableItems.forEach(item -> availableListDiv.add(buildItemRow(...))); // rebuild all of them
```

So toggling **one** row's checkmark tore down and recreated **all N** rows in that panel
— an O(n) DOM rebuild (with click listener re-registration) triggered by a single click,
exactly the "render less components" pitfall the article warns about ("It's easy to
forget that the browser is always the browser, and too much complicated stuff will
cause slowness").

### After
Each render pass now indexes the built rows by item id (`availableRowIndex` /
`selectedRowIndex`). The highlight toggle handlers no longer call the full render
method; they patch only the affected row in place:

```java
private void toggleAvailableHighlight(String id) {
    toggle(highlightedAvailable, id);
    applyHighlight(availableRowIndex.get(id), highlightedAvailable.contains(id));
    updateMoveButtonStates();
}

private static void applyHighlight(Div row, boolean highlighted) {
    if (row == null) return;
    row.setClassName("transfer-list__item--highlighted", highlighted);
    row.getChildren().findFirst().ifPresent(checkBox -> {
        checkBox.getElement().removeAllChildren();
        if (highlighted) checkBox.getElement().appendChild(VaadinIcon.CHECK.create().getElement());
    });
}
```

Full `renderAvailableList()` / `renderSelectedList()` rebuilds are still used where they
are actually needed — i.e. when the *item set itself* changes (`setAvailableItems`,
`setSelectedItems`, `moveHighlightedRight/Left`, `moveAllRight/Left`) — but a plain
highlight click is now an O(1) DOM patch instead of an O(n) rebuild.

## Other candidates reviewed but left unchanged

- **`BulkItemPickerDialog.toggleItem()`** also does a full `refreshItemList()` +
  `refreshSelectedList()` per click. Left as-is because the left list is already bounded
  by `pageSize` in paged mode (Tip #4 is already applied there), so the impact is much
  smaller than the unbounded `TransferList` case; patching it would need to handle three
  different data-source modes (paged / lazy-provider / in-memory) and was judged too
  risky to change without dedicated tests.
- **`ListingBundle` sort / show-hide-columns dialogs** build a `VerticalLayout` with a
  handful of rows, but only lazily when the dialog is opened by the user, and the number
  of rows is bounded by the grid's column count — not a hot path.
- **`GridToolbar`** filter UI (`enableFilterUi()`) is already built lazily behind a
  feature flag rather than unconditionally for every toolbar instance.

