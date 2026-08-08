/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Paginated navigation bar for {@link ItemListing}, built on the shadcn/ui-inspired
 * {@link Pagination} component family.
 *
 * <p>The bar renders:</p>
 * <pre>
 *   [Previous]  1  …  4  [5]  6  …  10  [Next]
 * </pre>
 *
 * <p>Page numbers around the current page ({@value #SIBLING_COUNT} sibling on each
 * side) are shown; a {@link PaginationEllipsis} bridges gaps to the first and last
 * page.  When there are few enough pages all of them are shown without ellipsis.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * BeanListing<Person> listing = ...;
 * ItemListingPaginationBar<Person, String> bar =
 *         new ItemListingPaginationBar<>(listing).withPageSize(20);
 * layout.content(listing.getComponent(), bar);
 * }</pre>
 *
 * @param <T> item type
 * @param <P> listing property type
 * @since 10.0.0
 */
public class ItemListingPaginationBar<T, P> extends Pagination {

    // -----------------------------------------------------------------------
    // PageChangeListener
    // -----------------------------------------------------------------------

    /** Serializable functional interface for page-change notifications. */
    @FunctionalInterface
    public interface PageChangeListener extends Serializable {
        void onPageChanged(int page);
    }

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Number of page links shown on each side of the current page. */
    private static final int SIBLING_COUNT = 1;

    /**
     * Threshold below which ALL pages are shown (no ellipsis needed):
     * first + last + (2 * SIBLING_COUNT + 1) middle + 2 potential ellipses = 7
     */
    private static final int SHOW_ALL_THRESHOLD = 2 * SIBLING_COUNT + 5;

    // -----------------------------------------------------------------------
    // Sentinel for the page-window algorithm
    // -----------------------------------------------------------------------

    /** Marker value used in {@link #computePageWindow} to indicate a mid-gap ellipsis slot. */
    private static final int ELLIPSIS = -1;

    /**
     * Marker value used in {@link #computeLazyPageWindow} to indicate a <em>trailing</em>
     * ellipsis — meaning "there are more pages but the total is unknown".
     * Rendered as a disabled {@link PaginationEllipsis} after the current page.
     */
    private static final int TRAILING_ELLIPSIS = -2;

    // -----------------------------------------------------------------------
    // Status text (kept for API backwards compatibility)
    // -----------------------------------------------------------------------

    @FunctionalInterface
    public interface StatusTextProvider extends Serializable {
        String getStatusText(int currentPage, int totalPages);
    }

    private static final StatusTextProvider DEFAULT_STATUS_TEXT_PROVIDER =
            (currentPage, totalPages) -> LocalizationProvider.localize(
                    "Page {0} of {1}", "pagination.status_text", currentPage, totalPages);

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private final ItemListing<T, P> listing;

    private int currentPage = 1;
    private int totalPages  = 1;

    /**
     * Fallback total-item count used when the data provider does not support
     * {@link DataProvider#size(com.vaadin.flow.data.provider.Query)} (e.g. lazy
     * providers without a count callback).
     * <p>
     * When {@code -1} (the default) the bar falls back to showing only the
     * current page: {@code totalPages = currentPage}.  Set a positive value via
     * {@link #withItemCountEstimate(int)} to get approximate multi-page navigation
     * without a real backend count query.
     * </p>
     */
    private int itemCountEstimate = -1;

    /**
     * Whether the current page is known to have a following page.
     *
     * <p>Set to {@code true} by default (optimistic — enables the Next button before
     * the first fetch completes).  Updated by {@link #setHasNextPage(boolean)} which
     * is called from {@link ItemListingPageSizeSelector} after each look-ahead fetch
     * In unmanaged mode this flag is derived from the total
     * item count returned by {@link #refreshState()}.</p>
     *
     * <p>Defaults to {@code false} so that {@link #refreshState()} computes the
     * page count purely from the item count and does not artificially expand
     * {@code totalPages} to {@code currentPage + 1} before the first look-ahead
     * fetch.  Managed-fetch mode sets this explicitly via
     * {@link #setHasNextPage(boolean)}.</p>
     */
    private boolean hasNextPage = false;

    /**
     * {@code true} when the total number of pages/items is genuinely unknown —
     * i.e. the data provider is lazy and has no count callback, OR the bar is
     * operating in managed-fetch (look-ahead) mode.
     *
     * <p>When {@code true} the bar switches to <em>cursor-style rendering</em>:
     * it shows a compact sliding window around the current page plus a trailing
     * ellipsis when {@link #hasNextPage} is {@code true}, instead of a
     * traditional numbered list with a "last page" anchor.  This prevents the
     * confusing "ever-growing page list" that appears when
     * {@link #setHasNextPage(boolean)} keeps expanding {@link #totalPages}.</p>
     */
    private boolean unknownTotal = false;

    private StatusTextProvider statusTextProvider = DEFAULT_STATUS_TEXT_PROVIDER;
    private boolean autoRefreshOnDataChange;
    private DataProvider<T, ?> observedDataProvider;
    private Registration dataProviderRegistration;
    private com.holonplatform.core.Registration filterAutoRefreshRegistration;

    /**
     * Listeners notified whenever the current page changes.
     * Used by {@link ItemListingPageSizeSelector} to update the page offset
     * in the wrapped fetch callback (true page-based data swap).
     */
    private final List<PageChangeListener> pageChangeListeners = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a pagination bar bound to the given {@link ItemListing}.
     *
     * @param listing the listing to paginate (not null)
     */
    public ItemListingPaginationBar(ItemListing<T, P> listing) {
        ObjectUtils.argumentNotNull(listing, "ItemListing must be not null");
        this.listing = listing;
        addClassName("listing-pagination-bar");
        // Do NOT call refreshState() here — that would trigger a COUNT(*) SQL query
        // before the component is even added to the UI.  State is computed lazily on
        // first attach so the count fires only when the pagination bar becomes visible.
        renderPages();  // render a minimal 1-page skeleton so the component is not empty
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // the look-ahead fetch will update hasNextPage → setHasNextPage() → re-render.
        // Skip refreshState() to avoid a spurious COUNT(*) against the data provider's
        // count callback (which in managed mode just returns currentPageSz, giving totalPages=1).
        if (pageChangeListeners.isEmpty()) {
            refreshState();
        }
        // Managed mode: render initial skeleton with hasNextPage=true (optimistic),
        // the look-ahead fetch will correct it immediately.
        renderPages();
    }
    // -----------------------------------------------------------------------
    // Navigation
    // -----------------------------------------------------------------------

    /**
     * Recomputes total pages from the listing data provider and re-renders.
     *
     * <p>When the underlying data provider is lazy and has no count callback,
     * the size query throws {@link UnsupportedOperationException}.  In that case
     * the bar falls back to the configured {@link #itemCountEstimate}: if a
     * positive estimate is set the page count is derived from it; otherwise only
     * the current page is shown (safe minimum — no broken navigation).</p>
     */
    public void refreshState() {
        ensureDataProviderAutoRefreshBinding();
        final int pageSize = getPageSize();
        int totalItems;
        try {
            totalItems = Math.max(0, listing.getDataProvider().size(new Query<>()));
            unknownTotal = false;
        } catch (IllegalStateException | UnsupportedOperationException ignored) {
            // Lazy provider without a count callback throws IllegalStateException
            // ("Trying to use exact size with a lazy loading component…").
            // Some providers throw UnsupportedOperationException instead.
            // In both cases fall back to the configured estimate, or cover only
            // the current page as a safe minimum.
            totalItems = itemCountEstimate > 0
                    ? itemCountEstimate
                    : currentPage * pageSize;
            unknownTotal = (itemCountEstimate <= 0);
        }
        this.totalPages  = Math.max(1, (int) Math.ceil((double) totalItems / (double) pageSize));
        // If the look-ahead knows there is a next page, ensure totalPages > currentPage
        // even when the count-based value would say otherwise (e.g. managed mode where
        // size() returns currentPageSz giving totalPages=1).
        if (hasNextPage) {
            this.totalPages = Math.max(this.totalPages, currentPage + 1);
        }
        this.currentPage = clampPage(currentPage);
        renderPages();
    }

    /**
     * Navigates to the requested page (1-based) and scrolls the listing.
     *
     * <p>When page-change listeners are registered (e.g. by
     * {@link ItemListingPageSizeSelector} in managed-fetch mode) the listeners
     * are fired first so they can swap the underlying page data.  The grid is
     * then scrolled to index 0 (top of the new page) instead of an absolute
     * index, because the data provider now contains only the current page's rows.</p>
     *
     * <p>Note: this method does NOT re-query the data provider for a count.
     * {@link #totalPages} is already known from the last {@link #refreshState()}
     * call (triggered by {@code onAttach}, a filter change, or an explicit
     * {@link #refreshState()} call). Re-fetching the count on every page click
     * would issue a {@code COUNT(*)} SQL per navigation event, violating the
     * lazy-loading contract.</p>
     *
     * @param page requested page number (clamped to [1, totalPages])
     */
    public void goToPage(int page) {
        // Do NOT call refreshState() here — that would fire a COUNT(*) SQL on
        // every page click. totalPages is already up-to-date from the last
        // onAttach / filter-change / data-change refresh.
        ensureDataProviderAutoRefreshBinding();
        this.currentPage = clampPage(page);
        firePageChangeListeners(currentPage);
        // If managed listeners are present they control the data; scroll to top.
        // Otherwise fall back to virtual-scroll offset.
        listing.scrollToIndex(pageChangeListeners.isEmpty()
                ? (currentPage - 1) * getPageSize()
                : 0);
        renderPages();
    }

    /** Navigates to the first page. */
    public void goToFirstPage()    { goToPage(1); }

    /** Navigates to the previous page. */
    public void goToPreviousPage() { goToPage(currentPage - 1); }

    /** Navigates to the next page. */
    public void goToNextPage()     { goToPage(currentPage + 1); }

    /** Navigates to the last page. */
    public void goToLastPage()     { goToPage(totalPages); }

    // -----------------------------------------------------------------------
    // Look-ahead next-page signalling
    // -----------------------------------------------------------------------

    /**
     * Called by {@link ItemListingPageSizeSelector} after each page fetch (managed-fetch
     * mode) to signal whether a next page exists — without issuing a {@code COUNT(*)} query.
     *
     * <p>The selector wraps every fetch with a <em>look-ahead</em>: it requests
     * {@code pageSize + 1} rows from the backend.  If {@code pageSize + 1} rows come
     * back there is a next page; if fewer rows come back, the current page is the last.</p>
     *
     * <p>This completely replaces the need for a {@code COUNT(*)} SQL query to drive
     * the pagination bar state.</p>
     *
     * @param hasNextPage {@code true} if the current page is followed by at least one more
     * @since 10.0.1
     */
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
        this.unknownTotal = true;  // look-ahead mode — total is always unknown
        if (!hasNextPage) {
            // We are definitively on the last page — totalPages = currentPage
            this.totalPages = currentPage;
        } else {
            // At least one more page exists — expand totalPages if needed
            this.totalPages = Math.max(totalPages, currentPage + 1);
        }
        renderPages();
    }

    /** Returns whether the current page is known to have a following page. */
    public boolean isHasNextPage() { return hasNextPage; }

    /**
     * Seeds the total page count directly from a known item count and page size,
     * bypassing the data provider's count callback.
     *
     * <p>Called by {@link ItemListingPageSizeSelector} in managed-fetch mode to set
     * the bar's initial and post-filter page count without issuing a COUNT(*) query
     * via the data provider.</p>
     *
     * @param totalItems known (or estimated) number of items (&ge; 0)
     * @param pageSize   page size to use for the calculation (&gt; 0)
     * @since 10.0.1
     */
    public void seedTotalPagesFromCount(int totalItems, int pageSize) {
        if (pageSize <= 0) return;
        this.totalPages  = Math.max(1, (int) Math.ceil((double) Math.max(0, totalItems) / (double) pageSize));
        this.currentPage = clampPage(currentPage);
        this.unknownTotal = false;  // we have a real count — use full numbered pagination
        renderPages();
    }
    // -----------------------------------------------------------------------

    /**
     * Registers a listener that is notified whenever the current page changes
     * (including page-size resets that return to page 1).
     *
     * <p>Used internally by {@link ItemListingPageSizeSelector} when
     * {@link ItemListingPageSizeSelector.Builder#withLazyFetch} is configured
     * to implement true page-based data swapping instead of virtual-scroll.</p>
     *
     * @param listener receives the new 1-based page number
     * @return registration to remove the listener
     * @since 10.0.1
     */
    public Registration addPageChangeListener(PageChangeListener listener) {
        ObjectUtils.argumentNotNull(listener, "Page change listener must not be null");
        pageChangeListeners.add(listener);
        return () -> pageChangeListeners.remove(listener);
    }

    private void firePageChangeListeners(int page) {
        pageChangeListeners.forEach(l -> l.onPageChanged(page));
    }

    // -----------------------------------------------------------------------
    // Page size
    // -----------------------------------------------------------------------

    /**
     * Changes the page size and resets to the first page.
     *
     * @param pageSize page size (&gt; 0)
     */
    public void setPageSize(int pageSize) {
        if (pageSize <= 0) throw new IllegalArgumentException("Page size must be > 0");
        if (listing.getComponent() instanceof Grid<?> grid) {
            grid.setPageSize(pageSize);
        }
        this.currentPage = 1;
        refreshState();
        firePageChangeListeners(1);   // notify managed listeners of page reset
        listing.scrollToIndex(0);
    }

    /** Fluent variant of {@link #setPageSize(int)}. */
    public ItemListingPaginationBar<T, P> withPageSize(int pageSize) {
        setPageSize(pageSize);
        return this;
    }

    /**
     * Sets a fixed total-item estimate used when the data provider cannot answer
     * a count query (lazy loading without a count callback).
     *
     * <p>The bar computes {@code totalPages = ceil(estimate / pageSize)}, giving
     * approximate page navigation without hitting the backend.  The estimate does
     * not need to be exact — the bar will never navigate past the last real page
     * because {@link #goToPage(int)} calls {@link ItemListing#scrollToIndex(int)},
     * which Vaadin clamps to the actual data boundary automatically.</p>
     *
     * <p>Example: if you know your dataset is usually around 500 rows, pass
     * {@code 500} and the bar will show roughly the right number of pages for any
     * chosen page size without a {@code COUNT(*)} query.</p>
     *
     * @param estimate positive total-item estimate (&gt; 0)
     * @return this bar (fluent)
     * @since 10.0.1
     */
    public ItemListingPaginationBar<T, P> withItemCountEstimate(int estimate) {
        if (estimate <= 0) throw new IllegalArgumentException("Item count estimate must be > 0");
        this.itemCountEstimate = estimate;
        refreshState();
        return this;
    }

    /**
     * Returns the current page size from the underlying grid (default 50).
     */
    public int getPageSize() {
        if (listing.getComponent() instanceof Grid<?> grid) {
            return Math.max(1, grid.getPageSize());
        }
        return 50;
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public int getCurrentPage() { return currentPage; }
    public int getTotalPages()  { return totalPages; }

    // -----------------------------------------------------------------------
    // Status text (backwards-compatible — text is not rendered inside the nav)
    // -----------------------------------------------------------------------

    public void setStatusTextProvider(StatusTextProvider provider) {
        this.statusTextProvider = ObjectUtils.argumentNotNull(provider,
                "StatusTextProvider must be not null");
    }

    public ItemListingPaginationBar<T, P> withStatusTextProvider(StatusTextProvider provider) {
        setStatusTextProvider(provider);
        return this;
    }

    /** Returns the current status text according to the configured provider. */
    public String getStatusText() {
        return statusTextProvider.getStatusText(currentPage, totalPages);
    }

    // -----------------------------------------------------------------------
    // Auto-refresh on data change
    // -----------------------------------------------------------------------

    public void setAutoRefreshOnDataChange(boolean autoRefreshOnDataChange) {
        this.autoRefreshOnDataChange = autoRefreshOnDataChange;
        if (!autoRefreshOnDataChange) {
            removeDataProviderRegistration();
            return;
        }
        ensureDataProviderAutoRefreshBinding();
        refreshState();
    }

    public ItemListingPaginationBar<T, P> withAutoRefreshOnDataChange(boolean flag) {
        setAutoRefreshOnDataChange(flag);
        return this;
    }

    public ItemListingPaginationBar<T, P> withAutoRefreshOnDataChange() {
        return withAutoRefreshOnDataChange(true);
    }

    public boolean isAutoRefreshOnDataChange() { return autoRefreshOnDataChange; }

    // -----------------------------------------------------------------------
    // Filter auto-refresh (listener-based)
    // -----------------------------------------------------------------------

    public com.holonplatform.core.Registration autoRefreshOnFilterChange(
            FilterInputGroup filterGroup, boolean refreshListing) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        return filterGroup.addFilterChangeListener(event -> {
            if (refreshListing) listing.refresh();
            refreshState();
        });
    }

    public com.holonplatform.core.Registration autoRefreshOnFilterChange(FilterInputGroup filterGroup) {
        return autoRefreshOnFilterChange(filterGroup, false);
    }

    public ItemListingPaginationBar<T, P> withFilterAutoRefresh(FilterInputGroup filterGroup,
                                                                boolean refreshListing) {
        clearFilterAutoRefresh();
        this.filterAutoRefreshRegistration = autoRefreshOnFilterChange(filterGroup, refreshListing);
        return this;
    }

    public ItemListingPaginationBar<T, P> withFilterAutoRefresh(FilterInputGroup filterGroup) {
        return withFilterAutoRefresh(filterGroup, false);
    }

    public void clearFilterAutoRefresh() {
        if (filterAutoRefreshRegistration != null) {
            filterAutoRefreshRegistration.remove();
            filterAutoRefreshRegistration = null;
        }
    }

    // -----------------------------------------------------------------------
    // Filter auto-refresh (signal-based — preferred when using Vaadin Signals)
    // -----------------------------------------------------------------------

    /**
     * Registers a signal-based effect that re-runs whenever the filter signal
     * emitted by {@link FilterInputGroup#queryFilterSignal()} changes.
     *
     * <p>Compared to {@link #autoRefreshOnFilterChange(FilterInputGroup, boolean)},
     * this variant:</p>
     * <ul>
     *   <li>Is <em>lifecycle-bound</em> to this pagination bar component — the
     *       effect is automatically inactive while the bar is detached and cleans
     *       up completely when the bar is removed from the UI, with no manual
     *       registration removal required.</li>
     *   <li>Uses Vaadin's reactive {@link Signal} graph, consistent with
     *       {@link ItemListing#refreshOnFilterSignal(FilterInputGroup)}.</li>
     * </ul>
     *
     * @param filterGroup   filter group whose signal to observe (not null)
     * @param refreshListing {@code true} to also call {@link ItemListing#refresh()}
     *                       before recomputing the page count
     * @return a {@link Registration} that can be used to remove the effect early;
     *         the effect is also removed automatically on component detach
     * @since 10.0.1
     */
    public Registration autoRefreshOnFilterSignal(FilterInputGroup filterGroup, boolean refreshListing) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        final Signal<?> filterSignal = filterGroup.queryFilterSignal();
        // Skip the first run (bind time) — same parity as the listener variant.
        final AtomicBoolean initialized = new AtomicBoolean(false);
        return Signal.effect(this, () -> {
            filterSignal.get();   // declare dependency on the filter signal
            if (initialized.getAndSet(true)) {
                if (refreshListing) listing.refresh();
                refreshState();
            }
        });
    }

    /**
     * Convenience overload of {@link #autoRefreshOnFilterSignal(FilterInputGroup, boolean)}
     * that does not additionally call {@link ItemListing#refresh()}.
     *
     * @param filterGroup filter group whose signal to observe (not null)
     * @return a {@link Registration} to remove the effect early
     * @since 10.0.1
     */
    public Registration autoRefreshOnFilterSignal(FilterInputGroup filterGroup) {
        return autoRefreshOnFilterSignal(filterGroup, false);
    }

    /**
     * Fluent variant of {@link #autoRefreshOnFilterSignal(FilterInputGroup, boolean)}.
     *
     * @param filterGroup    filter group whose signal to observe (not null)
     * @param refreshListing whether to also refresh the listing data
     * @return this bar (fluent)
     * @since 10.0.1
     */
    public ItemListingPaginationBar<T, P> withFilterAutoRefreshSignal(FilterInputGroup filterGroup,
                                                                      boolean refreshListing) {
        autoRefreshOnFilterSignal(filterGroup, refreshListing);
        return this;
    }

    /**
     * Fluent variant of {@link #autoRefreshOnFilterSignal(FilterInputGroup)}.
     *
     * @param filterGroup filter group whose signal to observe (not null)
     * @return this bar (fluent)
     * @since 10.0.1
     */
    public ItemListingPaginationBar<T, P> withFilterAutoRefreshSignal(FilterInputGroup filterGroup) {
        return withFilterAutoRefreshSignal(filterGroup, false);
    }

    // -----------------------------------------------------------------------
    // Internal rendering
    // -----------------------------------------------------------------------

    /**
     * Rebuilds the {@link PaginationContent} to reflect the current page state.
     *
     * <p>Delegates to {@link #renderLazyPages()} when {@link #unknownTotal} is
     * {@code true} (lazy / look-ahead mode), otherwise renders full numbered
     * pagination via {@link #renderKnownPages()}.</p>
     */
    private void renderPages() {
        if (unknownTotal) {
            renderLazyPages();
        } else {
            renderKnownPages();
        }
    }

    /**
     * Cursor-style rendering for lazy data sources where the total page count
     * is not known.
     *
     * <p>Pattern (sibling window = 1 before current):</p>
     * <pre>
     *   Page 1, hasNext:  [←]  [1]  …  [→]
     *   Page 4, hasNext:  [←]   1  2  3  [4]  …  [→]
     *   Page 6, hasNext:  [←]   1  …  5  [6]  …  [→]
     *   Page 6, last:     [←]   1  …  5  [6]       [→↓]
     * </pre>
     *
     * <p>Key UX rules:</p>
     * <ul>
     *   <li>Page 1 is always shown so the user can jump back to start.</li>
     *   <li>A trailing {@link PaginationEllipsis} (non-clickable) signals that
     *       more data exists without implying a specific last-page number.</li>
     *   <li>The Next button is disabled only when {@link #hasNextPage} is
     *       {@code false} (look-ahead confirmed last page).</li>
     *   <li>No last-page anchor is ever rendered — it would be a lie.</li>
     * </ul>
     */
    private void renderLazyPages() {
        PaginationContent content = getContent();
        content.clear();

        // ---- Previous ----
        PaginationPrevious prev = new PaginationPrevious();
        prev.setDisabled(currentPage <= 1);
        prev.addClickListener(e -> { if (currentPage > 1) goToPage(currentPage - 1); });
        content.add(new PaginationItem(prev));

        // ---- Sliding window (no last-page anchor) ----
        List<Integer> window = computeLazyPageWindow(currentPage, hasNextPage);
        for (int slot : window) {
            if (slot == ELLIPSIS || slot == TRAILING_ELLIPSIS) {
                PaginationEllipsis ellipsis = new PaginationEllipsis();
                if (slot == TRAILING_ELLIPSIS) {
                    ellipsis.addClassName("pagination-ellipsis--trailing");
                }
                content.add(new PaginationItem(ellipsis));
            } else {
                final int page = slot;
                PaginationLink link = new PaginationLink(page, page == currentPage);
                if (page != currentPage) {
                    link.addClickListener(e -> goToPage(page));
                }
                content.add(new PaginationItem(link));
            }
        }

        // ---- Next ----
        PaginationNext next = new PaginationNext();
        next.setDisabled(!hasNextPage);
        next.addClickListener(e -> { if (hasNextPage) goToPage(currentPage + 1); });
        content.add(new PaginationItem(next));
    }

    /**
     * Full numbered pagination rendering for data sources where the total item
     * count is known (count query succeeded or was seeded via
     * {@link #seedTotalPagesFromCount}).
     *
     * <p>Algorithm (sibling = 1):</p>
     * <pre>
     *   totalPages ≤ 7      → show all page numbers
     *   otherwise           → 1  [...]  (cur-1) cur (cur+1)  [...]  N
     * </pre>
     */
    private void renderKnownPages() {
        PaginationContent content = getContent();
        content.clear();

        // ---- Previous ----
        PaginationPrevious prev = new PaginationPrevious();
        prev.setDisabled(currentPage <= 1);
        prev.addClickListener(e -> { if (currentPage > 1) goToPage(currentPage - 1); });
        content.add(new PaginationItem(prev));

        // ---- Page numbers ----
        List<Integer> window = computePageWindow(currentPage, totalPages, SIBLING_COUNT);
        for (int slot : window) {
            if (slot == ELLIPSIS) {
                content.add(new PaginationItem(new PaginationEllipsis()));
            } else {
                final int page = slot;
                PaginationLink link = new PaginationLink(page, page == currentPage);
                if (page != currentPage) {
                    link.addClickListener(e -> goToPage(page));
                }
                content.add(new PaginationItem(link));
            }
        }

        // ---- Next ----
        PaginationNext next = new PaginationNext();
        next.setDisabled(currentPage >= totalPages);
        next.addClickListener(e -> { if (currentPage < totalPages) goToPage(currentPage + 1); });
        content.add(new PaginationItem(next));
    }

    /**
     * Computes the page-number slots for <em>cursor-style</em> (unknown-total) rendering.
     *
     * <p>Rules:</p>
     * <ul>
     *   <li>Pages 1 through {@code current} are shown with a mid-gap
     *       {@link #ELLIPSIS} when {@code current} is far from 1.</li>
     *   <li>If {@code hasNextPage} is {@code true} a {@link #TRAILING_ELLIPSIS}
     *       is appended after the current page to signal that more data exists.</li>
     *   <li>No last-page slot is ever emitted.</li>
     * </ul>
     *
     * <p>Examples:</p>
     * <pre>
     *   current=1, hasNext=true  → [1, -2]
     *   current=2, hasNext=true  → [1, 2, -2]
     *   current=4, hasNext=true  → [1, 2, 3, 4, -2]
     *   current=6, hasNext=true  → [1, -1, 5, 6, -2]
     *   current=6, hasNext=false → [1, -1, 5, 6]
     * </pre>
     *
     * @param current     current 1-based page number
     * @param hasNextPage whether a following page is known to exist
     * @return ordered slot list; {@link #ELLIPSIS} = mid-gap, {@link #TRAILING_ELLIPSIS} = more-data indicator
     */
    public static List<Integer> computeLazyPageWindow(int current, boolean hasNextPage) {
        List<Integer> result = new ArrayList<>();
        if (current <= 1) {
            result.add(1);
        } else if (current <= 4) {
            // Show all pages from 1 to current — short enough, no ellipsis needed
            for (int i = 1; i <= current; i++) result.add(i);
        } else {
            // Show page 1, mid-gap ellipsis, then (current-1) and current
            result.add(1);
            result.add(ELLIPSIS);
            result.add(current - 1);
            result.add(current);
        }
        if (hasNextPage) {
            result.add(TRAILING_ELLIPSIS);
        }
        return result;
    }

    /**
     * Computes the ordered sequence of page-number slots to display, where
     * {@link #ELLIPSIS} ({@value #ELLIPSIS}) represents a gap.
     *
     * <p>Examples for sibling=1:</p>
     * <pre>
     *   totalPages=5, current=3  → [1, 2, 3, 4, 5]         (all, no ellipsis)
     *   totalPages=10, current=1 → [1, 2, 3, -1, 10]
     *   totalPages=10, current=5 → [1, -1, 4, 5, 6, -1, 10]
     *   totalPages=10, current=9 → [1, -1, 8, 9, 10]
     * </pre>
     */
    public static List<Integer> computePageWindow(int current, int total, int siblings) {
        List<Integer> result = new ArrayList<>();
        if (total <= 0) return result;
        if (total == 1) { result.add(1); return result; }

        // Show all pages when few enough
        if (total <= SHOW_ALL_THRESHOLD) {
            for (int i = 1; i <= total; i++) result.add(i);
            return result;
        }

        int windowStart = Math.max(2, current - siblings);
        int windowEnd   = Math.min(total - 1, current + siblings);

        result.add(1);
        if (windowStart > 2)         result.add(ELLIPSIS);
        for (int i = windowStart; i <= windowEnd; i++) result.add(i);
        if (windowEnd < total - 1)   result.add(ELLIPSIS);
        result.add(total);
        return result;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private int clampPage(int page) {
        return Math.max(1, Math.min(page, totalPages));
    }

    private void ensureDataProviderAutoRefreshBinding() {
        if (!autoRefreshOnDataChange) return;
        final DataProvider<T, ?> dp = listing.getDataProvider();
        if (dp == observedDataProvider) return;
        removeDataProviderRegistration();
        observedDataProvider = dp;
        dataProviderRegistration = dp.addDataProviderListener(event -> refreshState());
    }

    private void removeDataProviderRegistration() {
        if (dataProviderRegistration != null) {
            dataProviderRegistration.remove();
            dataProviderRegistration = null;
        }
        observedDataProvider = null;
    }
}

