package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.BulkItemPickerDialogBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Two-panel dialog for adding multiple items in bulk with configurable quantities.
 *
 * <h3>Layout</h3>
 * <pre>
 * â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
 * â”‚  Add Items in Bulk                               [Ã—]    â”‚
 * â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
 * â”‚  [ðŸ” Search itemsâ€¦    ]  â”‚  Selected Items  [2]  Total: â”‚
 * â”‚  â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€   â”‚  â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ â”‚
 * â”‚  Coffee Table        [âœ“] â”‚  [Item 5] Coffee Table  -1+ â”‚
 * â”‚  Sofa                [âœ“] â”‚  [Item 7] Sofa          -1+ â”‚
 * â”‚  Storage Cabinet     [ ] â”‚                              â”‚
 * â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”´â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
 * â”‚                               [Cancel]  [Add Items]     â”‚
 * â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * BulkItemPickerDialog.builder()
 *     .title("Add Products")
 *     .items(productService.findAll().stream()
 *         .map(p -> BulkPickerItem.of(p.getId(), p.getName(), p.getSku(), p.getCost()))
 *         .toList())
 *     .onConfirm(entries -> entries.forEach(e -> order.addLine(e.item(), e.quantity())))
 *     .build()
 *     .open();
 * }</pre>
 *
 * <p>All visual styling is defined in {@code bulk-item-picker-dialog.css}.
 * No inline styles or Lumo tokens are used in Java.</p>
 *
 * @see BulkPickerItem
 * @see BulkPickerEntry
 * @see BulkItemPickerDialogBuilder
 */
@StyleSheet("context://h-dialog.css")
@StyleSheet("context://bulk-item-picker-dialog.css")
public class BulkItemPickerDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    // â”€â”€ Internal state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /** Eagerly loaded catalogue (used when no itemProvider is set). */
    private final List<BulkPickerItem> allItems = new ArrayList<>();

    /** id â†’ quantity for selected items; insertion-ordered for stable display */
    private final LinkedHashMap<String, Integer> selectedQty = new LinkedHashMap<>();

    /**
     * Cache of BulkPickerItem objects that have been selected by the user.
     * Required in lazy-provider mode where {@code allItems} is empty and
     * selected items must persist even when they scroll out of the current page.
     */
    private final Map<String, BulkPickerItem> selectedItemsCache = new LinkedHashMap<>();

    /**
     * Optional lazy item provider (simple, no pagination).
     * When set, the search field delegates to this function instead of filtering
     * {@code allItems} in-memory. The function receives the raw query string.
     */
    private Function<String, List<BulkPickerItem>> itemProvider;

    // â”€â”€ Paged provider (offset / limit / count â€” Spring Data style) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Paginated fetch callback. Receives a {@link BulkPickerFetchQuery} with
     * {@code (query, offset, limit)} and returns a slice of matching items.
     */
    private Function<BulkPickerFetchQuery, List<BulkPickerItem>> pagedFetchProvider;

    /**
     * Optional count callback. Receives the search query and returns the total
     * number of matching items â€” used to display "Page X of Y Â· N items".
     * When null, pagination shows only "Page X" and disables "Next" when a short
     * page is returned.
     */
    private Function<String, Long> pagedCountProvider;

    /** Current 0-based page index in paged mode. Reset to 0 on every search change. */
    private int currentPage = 0;

    /** Number of items per page. Default: {@value #DEFAULT_PAGE_SIZE}. */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /** Cached total-item count for the last query. Avoids re-counting on page navigation. */
    private long totalCount = 0;

    /** The query string that produced {@link #totalCount}. Null = uncached. */
    private String cachedCountQuery = null;

    /** Default page size when none is specified. */
    private static final int DEFAULT_PAGE_SIZE = 20;

    // â”€â”€ UI references (mutated by setter API) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private final H3 titleHeading;
    private final TextField searchField;
    private final Div itemListDiv;
    private final Div selectedListDiv;
    private final Span countBadge;
    private final Span totalQtySpan;
    private final Span rightTitleSpan;
    private final Button addItemsBtn;
    private final Button cancelBtn;

    /** Pagination bar â€” only visible in paged provider mode. */
    private final Div paginationBar;
    private final Button prevBtn;
    private final Button nextBtn;
    private final Span pageInfoSpan;

    // â”€â”€ Callbacks â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Consumer<List<BulkPickerEntry>> confirmCallback;
    private Runnable cancelCallback;

    /** Debounce delay in milliseconds applied to the search field value change. */
    private static final int SEARCH_DEBOUNCE_MS = 300;

    // â”€â”€ Constructor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Creates an empty {@code BulkItemPickerDialog} with default labels.
     * Items and callbacks should be set via the builder or setter API before opening.
     */
    public BulkItemPickerDialog() {
        // Theme: reuse h-dialog shell styles + bip-specific sizing
        getElement().getThemeList().add("h-dialog");
        getElement().getThemeList().add("bip-dialog");
        setDraggable(false);
        setResizable(false);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        // â”€â”€ Header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        titleHeading = Components.h3()
                .text(LocalizationProvider.localize("Add Items in Bulk", "bulk_picker.title"))
                .styleName("h-dialog__title").build();

        Div headerText = Components.div().add(titleHeading).styleName("h-dialog__header-text").build();

        Button closeBtn = Components.button()
                .icon(VaadinIcon.CLOSE)
                .styleName("h-dialog__close-btn")
                .tertiaryInline()
                .ariaLabel(LocalizationProvider.localize("Close dialog", "bulk_picker.close_aria"))
                .withClickListener(e -> handleCancel())
                .build();

        getHeader().add(headerText, closeBtn);

        // â”€â”€ Left panel â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        searchField = new TextField();
        searchField.setPlaceholder(LocalizationProvider.localize(
                "Type to search or scan the barcode of the item", "bulk_picker.search_placeholder"));
        searchField.setAriaLabel(LocalizationProvider.localize("Search items", "bulk_picker.search_aria"));
        searchField.setClearButtonVisible(true);
        searchField.addClassName("bip__search");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        // Lazy mode: wait for the user to pause typing before triggering a search.
        // This prevents a round-trip on every keystroke â€” critical for provider mode.
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setValueChangeTimeout(SEARCH_DEBOUNCE_MS);
        // Reset to page 0 on every new search query so results always start at the top.
        searchField.addValueChangeListener(e -> {
            currentPage = 0;
            cachedCountQuery = null;
            refreshItemList();
        });

        itemListDiv = Components.div().styleName("bip__item-list").build();

        // â”€â”€ Pagination bar (hidden until a paged provider is set) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        prevBtn = Components.button()
                .icon(VaadinIcon.ANGLE_LEFT)
                .styleName("bip__pagination-btn")
                .tertiary()
                .ariaLabel(LocalizationProvider.localize("Previous page", "bulk_picker.prev_page_aria"))
                .withClickListener(e -> navigatePage(-1))
                .build();

        pageInfoSpan = Components.span().styleName("bip__pagination-info").build();

        nextBtn = Components.button()
                .icon(VaadinIcon.ANGLE_RIGHT)
                .styleName("bip__pagination-btn")
                .tertiary()
                .ariaLabel(LocalizationProvider.localize("Next page", "bulk_picker.next_page_aria"))
                .withClickListener(e -> navigatePage(1))
                .build();

        paginationBar = Components.div()
                .add(prevBtn, pageInfoSpan, nextBtn)
                .styleName("bip__pagination")
                .visible(false)
                .build();

        Div leftPanel = Components.div().add(searchField, itemListDiv, paginationBar).styleName("bip__left").build();

        // â”€â”€ Right panel â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        rightTitleSpan = Components.span()
                .text(LocalizationProvider.localize("Selected Items", "bulk_picker.selected_title"))
                .styleName("bip__right-title").build();

        countBadge = Components.span().text("0").styleName("bip__count-badge").build();

        totalQtySpan = Components.span()
                .text(LocalizationProvider.localize("Total Quantity: {0}", "bulk_picker.total_qty")
                        .replace("{0}", "0"))
                .styleName("bip__total-qty").build();

        Div rightHeader = Components.div().add(rightTitleSpan, countBadge, totalQtySpan).styleName("bip__right-header").build();

        selectedListDiv = Components.div().styleName("bip__selected-list").build();

        renderSelectedEmptyState();

        Div rightPanel = Components.div().add(rightHeader, selectedListDiv).styleName("bip__right").build();

        // â”€â”€ Body split â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        Div divider = Components.div().styleName("bip__divider").build();

        Div body = Components.div().add(leftPanel, divider, rightPanel).styleName("bip").build();

        add(body);

        // â”€â”€ Footer â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        cancelBtn = Components.button()
                .text(LocalizationProvider.localize("Cancel", "bulk_picker.cancel_btn"))
                .styleName("h-dialog__cancel-btn")
                .tertiary()
                .withClickListener(e -> handleCancel())
                .build();

        addItemsBtn = Components.button()
                .text(LocalizationProvider.localize("Add Items", "bulk_picker.add_btn"))
                .styleName("h-dialog__action-btn")
                .primary()
                .withClickListener(e -> handleConfirm())
                .build();

        getFooter().add(cancelBtn, addItemsBtn);

        // Vaadin 25.2 fix: route ESC through handleCancel() so the cancelCallback fires
        // and the dialog state is reset. Without this listener, ESC auto-closes via the
        // framework, bypassing cancelCallback entirely.
        addDialogCloseActionListener(e -> handleCancel());
    }

    // â”€â”€ Public API â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Replaces the full item catalogue shown in the left panel (eager mode).
     * Clears any lazy provider, resets the current selection, and refreshes both panels.
     *
     * <p>Use {@link #setItemProvider(Function)} instead when the catalogue is large
     * and should be fetched from a service on each search query.</p>
     *
     * @param items the new catalogue (not null; may be empty)
     */
    public void setItems(Collection<BulkPickerItem> items) {
        Objects.requireNonNull(items, "items must not be null");
        this.itemProvider = null;
        this.pagedFetchProvider = null;
        this.pagedCountProvider = null;
        this.currentPage = 0;
        this.cachedCountQuery = null;
        this.selectedItemsCache.clear();
        allItems.clear();
        allItems.addAll(items);
        selectedQty.clear();
        refreshItemList();
        refreshSelectedList();
    }

    /**
     * Sets a lazy item provider that is called for every search query instead of
     * filtering an in-memory list.  Use this for large catalogues backed by a
     * database or remote service.
     *
     * <p>The provider receives the current verbatim search text (may be empty for
     * the initial load) and returns a list of matching {@link BulkPickerItem} objects.
     * Limit results inside the provider to keep rendering fast (e.g. top-50).</p>
     *
     * <p>The search field fires the provider only after the user stops typing for
     * {@value #SEARCH_DEBOUNCE_MS} ms ({@link ValueChangeMode#LAZY}), so call-rate
     * is naturally throttled without manual debouncing in the provider.</p>
     *
     * <pre>{@code
     * dialog.setItemProvider(query ->
     *     productService.searchByNameOrSku(query, 50));
     * }</pre>
     *
     * @param provider {@code (query) â†’ List<BulkPickerItem>} â€” called with the
     *                 current search text on every debounced keystroke (not null)
     */
    public void setItemProvider(Function<String, List<BulkPickerItem>> provider) {
        Objects.requireNonNull(provider, "provider must not be null");
        this.itemProvider = provider;
        this.pagedFetchProvider = null;
        this.pagedCountProvider = null;
        this.currentPage = 0;
        this.cachedCountQuery = null;
        this.allItems.clear();
        this.selectedItemsCache.clear();
        this.selectedQty.clear();
        refreshItemList();
        refreshSelectedList();
    }

    // â”€â”€ Paged provider API â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Sets a paginated item provider using Spring Data-style offset/limit semantics.
     *
     * <p>The {@code fetchProvider} is called on every debounced search with a
     * {@link BulkPickerFetchQuery} carrying {@code (query, offset, limit)}.
     * The {@code countProvider} is called once per query change to retrieve the total
     * number of matching items â€” enabling "Page X of Y Â· N items" in the pagination bar.</p>
     *
     * <pre>{@code
     * dialog.setPagedProvider(
     *     q -> productRepo.findByNameOrSku(q.query(), q.offset(), q.limit()),
     *     q -> productRepo.countByNameOrSku(q)
     * );
     * }</pre>
     *
     * @param fetchProvider paginated fetch callback (not null)
     * @param countProvider total-count callback (not null)
     */
    public void setPagedProvider(
            Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider,
            Function<String, Long> countProvider) {
        Objects.requireNonNull(fetchProvider, "fetchProvider must not be null");
        Objects.requireNonNull(countProvider, "countProvider must not be null");
        this.pagedFetchProvider = fetchProvider;
        this.pagedCountProvider = countProvider;
        this.itemProvider = null;
        this.allItems.clear();
        this.selectedItemsCache.clear();
        this.selectedQty.clear();
        this.currentPage = 0;
        this.cachedCountQuery = null;
        refreshItemList();
        refreshSelectedList();
    }

    /**
     * Sets a paginated item provider without a count function.
     *
     * <p>The pagination bar shows only "Page X" and disables the Next button when
     * the returned slice is shorter than {@link #setPageSize(int) pageSize}.</p>
     *
     * @param fetchProvider paginated fetch callback (not null)
     */
    public void setPagedProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider) {
        Objects.requireNonNull(fetchProvider, "fetchProvider must not be null");
        this.pagedFetchProvider = fetchProvider;
        this.pagedCountProvider = null;
        this.itemProvider = null;
        this.allItems.clear();
        this.selectedItemsCache.clear();
        this.selectedQty.clear();
        this.currentPage = 0;
        this.cachedCountQuery = null;
        refreshItemList();
        refreshSelectedList();
    }

    /**
     * Sets the number of items fetched per page in paged provider mode.
     * Default: {@value #DEFAULT_PAGE_SIZE}.
     *
     * @param pageSize items per page (â‰¥ 1)
     */
    public void setPageSize(int pageSize) {
        if (pageSize < 1) throw new IllegalArgumentException("pageSize must be >= 1");
        this.pageSize = pageSize;
    }

    /**
     * Appends a single item to the catalogue without clearing the selection.
     *
     * @param item the item to content (not null)
     */
    public void addItem(BulkPickerItem item) {
        Objects.requireNonNull(item, "item must not be null");
        allItems.add(item);
        refreshItemList();
    }

    /**
     * Sets the dialog header title text.
     *
     * @param title the title text (not null)
     */
    public void setTitle(String title) {
        titleHeading.setText(title);
    }

    /**
     * Sets the search field placeholder text.
     *
     * @param placeholder the placeholder (not null)
     */
    public void setSearchPlaceholder(String placeholder) {
        searchField.setPlaceholder(placeholder);
    }

    /**
     * Sets the label of the primary "Add Items" button.
     *
     * @param text the button label (not null)
     */
    public void setAddButtonText(String text) {
        addItemsBtn.setText(text);
    }

    /**
     * Sets the label of the secondary "Cancel" button.
     *
     * @param text the button label (not null)
     */
    public void setCancelButtonText(String text) {
        cancelBtn.setText(text);
    }

    /**
     * Registers the callback invoked when the user clicks "Add Items".
     * Receives the list of selected {@link BulkPickerEntry} objects (item + quantity).
     *
     * @param callback the confirm handler (not null)
     */
    public void setConfirmCallback(Consumer<List<BulkPickerEntry>> callback) {
        this.confirmCallback = callback;
    }

    /**
     * Registers the callback invoked when the user cancels or closes the dialog.
     *
     * @param callback the cancel handler (may be null)
     */
    public void setCancelCallback(Runnable callback) {
        this.cancelCallback = callback;
    }

    /**
     * Returns an unmodifiable snapshot of the currently selected entries.
     *
     * @return list of {@link BulkPickerEntry} (never null)
     */
    public List<BulkPickerEntry> getSelectedEntries() {
        return buildEntries();
    }

    /**
     * Clears all item selections without closing the dialog.
     */
    public void clearSelection() {
        selectedQty.clear();
        refreshItemList();
        refreshSelectedList();
    }

    /**
     * Adds a typed event listener for confirm events.
     *
     * @param listener the listener (not null)
     * @return registration for removing the listener
     */
    public Registration addConfirmListener(ComponentEventListener<ConfirmEvent> listener) {
        return addListener(ConfirmEvent.class, listener);
    }

    // â”€â”€ Static factory â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Returns a new Holon fluent {@link BulkItemPickerDialogBuilder}.
     *
     * <pre>{@code
     * BulkItemPickerDialog.builder()
     *     .title("Add Products")
     *     .items(catalogue)
     *     .onConfirm(entries -> handleAdd(entries))
     *     .build()
     *     .open();
     * }</pre>
     *
     * @return a new builder
     */
    public static BulkItemPickerDialogBuilder builder() {
        return BulkItemPickerDialogBuilder.create();
    }

    // â”€â”€ Rendering helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void refreshItemList() {
        itemListDiv.removeAll();
        String rawQuery = searchField.getValue() == null ? "" : searchField.getValue().trim();

        if (pagedFetchProvider != null) {
            // â”€â”€ Paged / offset-limit mode â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            int offset = currentPage * pageSize;
            List<BulkPickerItem> items = pagedFetchProvider.apply(
                    new BulkPickerFetchQuery(rawQuery, offset, pageSize));

            // Re-count only when the query text changes (not on Prev/Next navigation).
            if (pagedCountProvider != null && !rawQuery.equals(cachedCountQuery)) {
                totalCount = pagedCountProvider.apply(rawQuery);
                cachedCountQuery = rawQuery;
            } else if (pagedCountProvider == null) {
                // Estimate: if a full page was returned, at least one more page exists.
                totalCount = (long) offset + items.size()
                        + (items.size() == pageSize ? 1 : 0);
            }

            items.forEach(item -> itemListDiv.add(buildItemRow(item)));
            if (items.isEmpty()) itemListDiv.add(buildEmptySpan(rawQuery));
            updatePaginationBar();

        } else if (itemProvider != null) {
            // â”€â”€ Simple lazy provider (no pagination) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            List<BulkPickerItem> items = itemProvider.apply(rawQuery);
            items.forEach(item -> itemListDiv.add(buildItemRow(item)));
            if (items.isEmpty()) itemListDiv.add(buildEmptySpan(rawQuery));
            paginationBar.setVisible(false);

        } else {
            // â”€â”€ Eager / in-memory mode â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            String filter = rawQuery.toLowerCase(Locale.ROOT);
            List<BulkPickerItem> items = allItems.stream()
                    .filter(item -> matchesFilter(item, filter))
                    .toList();
            items.forEach(item -> itemListDiv.add(buildItemRow(item)));
            if (items.isEmpty()) itemListDiv.add(buildEmptySpan(rawQuery));
            paginationBar.setVisible(false);
        }
    }

    /** Updates the pagination bar label and Prev/Next enabled states. */
    private void updatePaginationBar() {
        long totalPages;
        if (pagedCountProvider != null) {
            totalPages = (long) Math.ceil((double) totalCount / pageSize);
        } else {
            // Without a count, "there's a next page" only if a full page was returned.
            totalPages = totalCount > (long) (currentPage + 1) * pageSize
                    ? (long) currentPage + 2 : (long) currentPage + 1;
        }
        totalPages = Math.max(1L, totalPages);

        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled((long) (currentPage + 1) * pageSize < totalCount);

        String info = pagedCountProvider != null
                ? "Page " + (currentPage + 1) + " of " + totalPages
                  + "  Â·  " + totalCount + " items"
                : "Page " + (currentPage + 1);
        pageInfoSpan.setText(info);
        paginationBar.setVisible(true);
    }

    /** Moves to the next or previous page and re-fetches. */
    private void navigatePage(int delta) {
        currentPage = Math.max(0, currentPage + delta);
        refreshItemList();
    }

    /** Builds an empty-state {@link Span} for the item list. */
    private static Span buildEmptySpan(String rawQuery) {
        String text = rawQuery.isEmpty()
                ? LocalizationProvider.localize("No items available", "bulk_picker.empty_items")
                : LocalizationProvider.localize("No items match your search", "bulk_picker.no_results");
        return Components.span().text(text).styleName("bip__empty").build();
    }

    private static boolean matchesFilter(BulkPickerItem item, String filter) {
        if (filter.isBlank()) return true;
        return item.name().toLowerCase(Locale.ROOT).contains(filter)
                || item.sku().toLowerCase(Locale.ROOT).contains(filter);
    }

    private Div buildItemRow(BulkPickerItem item) {
        boolean isSelected = selectedQty.containsKey(item.id());

        // Item name
        Span nameSpan = Components.span().text(item.name()).styleName("bip__item-name").build();
        if (isSelected) nameSpan.addClassName("bip__item-name--link");

        Span metaSpan = Components.span()
                .text("SKU: " + item.sku() + "  Purchase Rate: $" + String.format(Locale.ROOT, "%.2f", item.purchaseRate()))
                .styleName("bip__item-meta")
                .build();

        Div info = Components.div().add(nameSpan, metaSpan).styleName("bip__item-info").build();

        // Selection indicator
        Div checkDiv = Components.div().styleName("bip__item-check").build();
        if (isSelected) {
            checkDiv.addClassName("bip__item-check--checked");
            checkDiv.add(VaadinIcon.CHECK_CIRCLE.create());
        } else {
            checkDiv.addClassName("bip__item-check--unchecked");
            checkDiv.add(VaadinIcon.CIRCLE_THIN.create());
        }

        Div row = Components.div().add(info, checkDiv).styleName("bip__item").build();
        if (isSelected) row.addClassName("bip__item--selected");
        row.addClickListener(e -> toggleItem(item));

        return row;
    }

    private void refreshSelectedList() {
        selectedListDiv.removeAll();

        if (selectedQty.isEmpty()) {
            renderSelectedEmptyState();
        } else {
            selectedQty.forEach((id, qty) -> {
                BulkPickerItem item = findById(id);
                if (item != null) {
                    selectedListDiv.add(buildSelectedRow(item, qty));
                }
            });
        }
        updateCounters();
    }

    private void renderSelectedEmptyState() {
        selectedListDiv.removeAll();
        var empty = Components.span()
                .text(LocalizationProvider.localize("No items selected", "bulk_picker.selected_empty"))
                .styleName("bip__empty").build();
        selectedListDiv.add(empty);
        updateCounters();
    }

    private Div buildSelectedRow(BulkPickerItem item, int qty) {
        var nameSpan = Components.span().text("[" + item.sku() + "] " + item.name()).styleName("bip__selected-name").build();

        IntegerField qtyField = new IntegerField();
        qtyField.addClassName("bip__qty-field");
        qtyField.setStepButtonsVisible(true);
        qtyField.setMin(1);
        qtyField.setMax(9999);
        qtyField.setStep(1);
        qtyField.setValue(qty);
        qtyField.addValueChangeListener(e -> {
            int effective = (e.getValue() == null || e.getValue() < 1) ? 1 : e.getValue();
            selectedQty.put(item.id(), effective);
            updateCounters();
        });

        var row = Components.div().add(nameSpan, qtyField).styleName("bip__selected-item").build();
        return row;
    }

    private void updateCounters() {
        int count = selectedQty.size();
        int total = selectedQty.values().stream().mapToInt(Integer::intValue).sum();
        countBadge.setText(String.valueOf(count));
        String template = LocalizationProvider.localize("Total Quantity: {0}", "bulk_picker.total_qty");
        totalQtySpan.setText(template.replace("{0}", String.valueOf(total)));
    }

    // â”€â”€ Interaction handlers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void toggleItem(BulkPickerItem item) {
        if (selectedQty.containsKey(item.id())) {
            selectedQty.remove(item.id());
            selectedItemsCache.remove(item.id()); // evict from cache on deselect
        } else {
            selectedQty.put(item.id(), 1);
            // Cache the full item so it can be resolved in the right panel even when
            // it's no longer present in the current provider search results.
            selectedItemsCache.put(item.id(), item);
        }
        refreshItemList();
        refreshSelectedList();
    }

    private void handleConfirm() {
        List<BulkPickerEntry> entries = buildEntries();
        if (confirmCallback != null) {
            confirmCallback.accept(entries);
        }
        fireEvent(new ConfirmEvent(this, false, entries));
        close();
    }

    private void handleCancel() {
        if (cancelCallback != null) {
            cancelCallback.run();
        }
        close();
    }

    // â”€â”€ Utilities â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private BulkPickerItem findById(String id) {
        // In provider mode allItems is empty; use the cache of previously selected items.
        BulkPickerItem cached = selectedItemsCache.get(id);
        if (cached != null) return cached;
        return allItems.stream().filter(i -> i.id().equals(id)).findFirst().orElse(null);
    }

    private List<BulkPickerEntry> buildEntries() {
        return selectedQty.entrySet().stream()
                .map(e -> {
                    BulkPickerItem item = findById(e.getKey());
                    return item != null ? BulkPickerEntry.of(item, e.getValue()) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // â”€â”€ Events â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    /**
     * Fired when the user clicks "Add Items".
     * Contains the resulting list of {@link BulkPickerEntry} objects.
     */
    public static class ConfirmEvent extends ComponentEvent<BulkItemPickerDialog> {

        @Serial
        private static final long serialVersionUID = 1L;

        private final List<BulkPickerEntry> entries;

        /**
         * @param source     the dialog
         * @param fromClient always {@code false} â€” server-side event
         * @param entries    the confirmed entries
         */
        public ConfirmEvent(BulkItemPickerDialog source, boolean fromClient, List<BulkPickerEntry> entries) {
            super(source, fromClient);
            this.entries = List.copyOf(entries);
        }

        /**
         * Returns the confirmed entries (item + quantity).
         *
         * @return immutable list of entries (never null)
         */
        public List<BulkPickerEntry> getEntries() {
            return entries;
        }
    }
}















