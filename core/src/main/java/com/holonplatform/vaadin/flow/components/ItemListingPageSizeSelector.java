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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.builders.NotificationBuilder;
import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.component.UI;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * A <em>"Show N entries"</em> selector that controls the page size of an
 * {@link ItemListing}.
 *
 * <p>Renders as:</p>
 * <pre>
 *   Show  [10 ▾]  entries
 * </pre>
 *
 * <h4>Standalone (lazy listing, no pagination bar)</h4>
 * <pre>{@code
 * listing.setItems(q -> employees.stream().skip(q.getOffset()).limit(q.getLimit()));
 *
 * ItemListingPageSizeSelector<Employee, String> selector =
 *         Components.pageSizeSelector(listing)
 *             .withOptions(10, 25, 50, 100)
 *             .withDefaultSize(10)
 *             .build();
 * }</pre>
 *
 * <h4>With pagination bar — TRUE page-based data swap (recommended)</h4>
 * <p>Pass the fetch callback to the selector via {@link Builder#withLazyFetch}
 * instead of calling {@code listing.setItems()} directly.  The selector wraps
 * the callback with a mutable page offset so that navigating pages actually
 * replaces the visible data rather than just scrolling the viewport.</p>
 * <pre>{@code
 * ItemListingPaginationBar<Employee, String> bar = new ItemListingPaginationBar<>(listing);
 *
 * ItemListingPageSizeSelector<Employee, String> selector =
 *         Components.pageSizeSelector(listing)
 *             .withOptions(10, 25, 50)
 *             .withDefaultSize(10)
 *             .withPaginationBar(bar)
 *             .withLazyFetch(
 *                 q -> employeeService.fetch(q.getOffset(), q.getLimit()),
 *                 () -> employeeService.count())          // for total page count
 *             .build();
 * }</pre>
 *
 * @param <T> item type
 * @param <P> listing property type
 * @since 10.0.1
 */
@StyleSheet("context://page-size-selector.css")
public class ItemListingPageSizeSelector<T, P> extends Div {

    // -----------------------------------------------------------------------
    // Defaults
    // -----------------------------------------------------------------------

    /** Default page-size options shown when none are configured. */
    public static final List<Integer> DEFAULT_OPTIONS = List.of(10, 25, 50, 100);

    /** Default selected page size. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private final ItemListing<T, P>        listing;
    private ItemListingPaginationBar<T, P> paginationBar;

    /**
     * Current page size — kept in a 1-element array so the managed fetch
     * lambda can capture and read the latest value.
     */
    private final int[] currentPageSz;

    /**
     * Current page offset (= (page-1) * pageSize) used by the managed fetch
     * wrapper.  Zero for standalone mode or when on page 1.
     */
    private final int[] currentPageOffset = {0};

    /**
     * Non-null only in <em>managed-fetch mode</em> ({@link Builder#withLazyFetch}
     * was called).  Holds the lazy data view so the count callback can be updated
     * when the page size changes.
     */
    private GridLazyDataView<T> managedDataView;

    /**
     * Optional count supplier passed via {@link Builder#withLazyFetch}.
     * When non-null it is called in {@link #initManagedFetch} to seed the bar's
     * initial total-page count, and in {@link #resetToPage1()} to recompute it
     * after a filter change — without querying the data provider for a count.
     */
    private Supplier<Integer> countSupplier;

    private Notification noResultsNotification;

    // -----------------------------------------------------------------------
    // Constructor (use the builder or Components factory)
    // -----------------------------------------------------------------------

    private ItemListingPageSizeSelector(Builder<T, P> builder) {
        this.listing        = builder.listing;
        this.paginationBar  = builder.paginationBar;
        this.currentPageSz  = new int[]{ builder.defaultSize };
        this.countSupplier  = builder.countSupplier;

        addClassName("page-size-selector");

        // Managed-fetch mode: the selector owns the data binding
        if (builder.fetchCallback != null) {
            initManagedFetch(builder.fetchCallback, builder.countSupplier, builder.defaultSize);
        }

        // Wire filter reset via Signal.effect (lifecycle-aware, preferred) ────
        // Uses FilterInputGroup.queryFilterSignal() to bridge filter-change events to
        // a Vaadin Signal so the effect is bound to this component's attach/detach lifecycle.
        // The AtomicBoolean guard ensures the effect does NOT fire at registration time —
        // only on subsequent signal changes (i.e. actual filter updates).
        if (builder.filterGroupSignal != null && builder.fetchCallback != null) {
            Signal<Optional<QueryFilter>> filterSignal = builder.filterGroupSignal.queryFilterSignal();
            AtomicBoolean filterInitialized = new AtomicBoolean(false);
            Signal.effect(this, () -> {
                filterSignal.get();  // register reactive dependency
                if (filterInitialized.getAndSet(true)) {
                    resetToPage1();
                }
            });
        }

        // Legacy listener-based filter reset (kept for backward compat, lower priority) ──
        if (builder.filterGroup != null && builder.fetchCallback != null) {
            builder.filterGroup.addFilterChangeListener(e -> resetToPage1());
        }

        // Wire search field: value changes reset to page 1 (lazy debounce by default)
        if (builder.searchField != null && builder.fetchCallback != null) {
            builder.searchField.setValueChangeMode(ValueChangeMode.LAZY);
            builder.searchField.addValueChangeListener(e -> resetToPage1());
        }

        buildUI(builder.options, builder.defaultSize);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Resets the selector to page 1 and refreshes the listing data.
     *
     * <p>Call this whenever the active filter changes so that the page offset
     * is cleared and the bar's page count is recomputed against the new
     * result set.  Typically wired via
     * {@link Builder#withFilterReset(FilterInputGroup)} or manually:</p>
     *
     * <pre>{@code
     * filterPanel.addFilterChangeListener(e -> selector.resetToPage1());
     * }</pre>
     *
     * <p>Has no effect in unmanaged (non-{@link Builder#withLazyFetch}) mode.</p>
     *
     * @since 10.0.1
     */
    public void resetToPage1() {
        if (managedDataView == null) return;
        currentPageOffset[0] = 0;
        final UI currentUi = UI.getCurrent();

        if (!paginatedMode) {
            // Virtual-scroll mode: re-assert unknown count so any previous
            // setItemCountCallback call (e.g. from applyPageSize or a stale
            // paginated-mode fetch) cannot silently cap visible rows.
            managedDataView.setItemCountUnknown();
        } else {
            managedDataView.setItemCountCallback(cq -> currentPageSz[0]);
        }

        // Always recompute bar page count from the count supplier when available —
        // regardless of paginatedMode. Filter changes narrow the result set in both
        // virtual-scroll and paginated mode; the bar should always reflect the new count.
        if (paginationBar != null && countSupplier != null) {
            int count = Math.max(0, countSupplier.get());
            paginationBar.seedTotalPagesFromCount(count, currentPageSz[0]);
            if (count == 0) {
                showNoResultsNotification();
            } else if (noResultsNotification != null) {
                noResultsNotification.close();
                noResultsNotification = null;
            }
        }

        // Use GridLazyDataView.refreshAll() — the correct Vaadin 25 API for lazy data.
        // When called from a headless test there is no active UI, so skip the
        // UI-bound refresh/navigation calls but still keep the selector state updated.
        if (currentUi != null) {
            managedDataView.refreshAll();
            if (paginationBar != null) {
                paginationBar.goToFirstPage();
            }
        }
    }

    private void showNoResultsNotification() {
        if (noResultsNotification != null && noResultsNotification.isOpened()) {
            return;
        }
        noResultsNotification = Components.notification()
                .text("No matching records found.")
                .warning()
                .autoClose(false)
                .closeButton(true)
                .bottomEnd()
                .build();
        noResultsNotification.open();
    }

    // -----------------------------------------------------------------------
    // Managed-fetch initialisation
    // -----------------------------------------------------------------------

    /**
     * Sets up a <em>wrapped</em> fetch callback that:
     * <ol>
     *   <li>Injects the current page offset so navigating pages replaces the grid
     *       data rather than scrolling the viewport.</li>
     *   <li>Uses a <strong>look-ahead</strong>: requests {@code pageSize + 1} rows
     *       from the backend.  If {@code pageSize + 1} rows are returned there is a
     *       next page; if fewer rows are returned the current page is the last.
     *       The result is pushed to the bar via
     *       {@link ItemListingPaginationBar#setHasNextPage(boolean)} — <strong>no
     *       {@code COUNT(*)} query is ever issued</strong>.</li>
     *   <li>Sets {@code setItemCountCallback(q -> currentPageSz)} so the grid
     *       shows exactly N rows and never loads more on scroll.</li>
     *   <li>Registers a page-change listener on the bar (if present) to update
     *       the offset and refresh.</li>
     * </ol>
     */
    private void initManagedFetch(
            CallbackDataProvider.FetchCallback<T, Void> originalFetch,
            Supplier<Integer> suppliedCountSupplier,
            int initialPageSize) {

        // Wrap: inject page offset + look-ahead (fetch limit+1 to detect next page)
        final int[] lastFetchedCount = {initialPageSize};

        managedDataView = listing.setItems(q -> {
            final int limit = q.getLimit();
            // Fetch one extra row — if it comes back, a next page exists
            java.util.List<T> rows = originalFetch.fetch(new Query<>(
                            currentPageOffset[0] + q.getOffset(),
                            limit + 1,   // look-ahead
                            q.getSortOrders(),
                            null,
                            null))
                    .limit(limit + 1L)
                    .toList();
            final boolean next = rows.size() > limit;
            int actualCount = Math.min(rows.size(), limit);
            lastFetchedCount[0] = actualCount;

            // Only apply pagination logic when in paginated mode
            if (paginatedMode) {
                if (paginationBar != null) {
                    paginationBar.setHasNextPage(next);
                }
                // Partial page: update count so subsequent renders don't show blanks,
                // and notify user they've reached the end.
                if (actualCount < currentPageSz[0] && actualCount > 0) {
                    managedDataView.setItemCountCallback(cq -> lastFetchedCount[0]);
                    com.vaadin.flow.component.notification.Notification.show(
                            "Reached end of data", 2000,
                            com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER);
                } else if (actualCount == currentPageSz[0]) {
                    // Full page — restore count to page size for next fetch cycle
                    managedDataView.setItemCountCallback(cq -> currentPageSz[0]);
                }
            }
            return rows.stream().limit(limit);
        });

        // Initial count = pageSize so the grid requests a full page on first load
        managedDataView.setItemCountCallback(q -> currentPageSz[0]);

        // Set initial grid page size so ItemListingPaginationBar.getPageSize() works
        if (listing.getComponent() instanceof Grid<?> grid) {
            grid.setPageSize(initialPageSize);
        }

        if (paginationBar != null) {
            // On page navigation: update offset and reload data
            paginationBar.addPageChangeListener(page -> {
                currentPageOffset[0] = (page - 1) * currentPageSz[0];
                // Restore full page count so the grid requests a full page of items.
                // Without this, navigating back from a partial last page would still
                // use the reduced count, causing the same partial result + notification.
                managedDataView.setItemCountCallback(cq -> currentPageSz[0]);
                managedDataView.refreshAll();
            });
            // Seed initial page count from the count supplier (if provided).
            // This avoids a DataProvider.size() call against a lazy provider that has
            // no count callback — the countSupplier closure knows the real count.
            if (suppliedCountSupplier != null) {
                int count = Math.max(0, suppliedCountSupplier.get());
                paginationBar.seedTotalPagesFromCount(count, initialPageSize);
            }
        }

        // If the component starts in virtual-scroll mode (the default), override the
        // paginated defaults that were set above — unknown count + larger page size.
        if (!paginatedMode) {
            managedDataView.setItemCountUnknown();
            if (listing.getComponent() instanceof Grid<?> g) {
                g.setPageSize(50);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Paginated / virtual-scroll mode toggle
    // -----------------------------------------------------------------------

    /** Whether pagination is currently active (true) or virtual-scroll mode (false). Default is virtual-scroll. */
    private boolean paginatedMode = false;

    /**
     * Switches between paginated mode and default virtual-scroll mode.
     *
     * <p>In paginated mode (default), the grid shows a fixed page of items and
     * navigation is via the pagination bar.</p>
     *
     * <p>In virtual-scroll mode, the grid uses Vaadin's built-in infinite scroll
     * with the same data provider — no page offset is injected and the item count
     * is set to unknown so the grid fetches rows as the user scrolls.</p>
     *
     * @param paginated {@code true} for paginated mode, {@code false} for virtual scroll
     */
    public void setPaginatedMode(boolean paginated) {
        if (this.paginatedMode == paginated) return;
        this.paginatedMode = paginated;

        if (managedDataView == null) return;

        if (paginated) {
            // Switch to paginated: reset to page 1, hard-cap rows to pageSize.
            // Do NOT call refreshAll() — the data already in the grid is still valid
            // for page 1. Vaadin will re-render the grid automatically when the count
            // strategy changes via setItemCountCallback. A refreshAll() would trigger
            // a redundant backend query that the user never asked for.
            currentPageOffset[0] = 0;
            managedDataView.setItemCountCallback(cq -> currentPageSz[0]);
            if (listing.getComponent() instanceof Grid<?> grid) {
                grid.setPageSize(currentPageSz[0]);
            }
            if (paginationBar != null) {
                paginationBar.goToFirstPage();
            }
        } else {
            // Switch to virtual-scroll: reset offset, let the grid grow as the user scrolls.
            // Do NOT call refreshAll() — currently visible rows are already correct.
            // setItemCountUnknown() notifies the client that more rows may exist and enables
            // infinite scroll; the grid will fetch additional rows only when the user scrolls.
            currentPageOffset[0] = 0;
            managedDataView.setItemCountUnknown();
            if (listing.getComponent() instanceof Grid<?> grid) {
                grid.setPageSize(50);
            }
        }
    }

    /**
     * Returns {@code true} if currently in paginated mode.
     */
    public boolean isPaginatedMode() {
        return paginatedMode;
    }

    // -----------------------------------------------------------------------
    // UI assembly
    // -----------------------------------------------------------------------

    private void buildUI(List<Integer> options, int defaultSize) {
        Span prefix = Components.span().text("Show").styleName("page-size-selector__label").build();

        ComboBox<Integer> combo = new ComboBox<>();
        combo.addClassName("page-size-selector__select");
        combo.setItems(options);
        combo.setValue(defaultSize);
        combo.setAllowCustomValue(true);

        // Apply initial page size (unmanaged mode only — managed is handled in initManagedFetch)
        if (managedDataView == null) {
            applyPageSize(defaultSize, false);
        }

        // Standard dropdown selection
        combo.addValueChangeListener(event -> {
            Integer chosen = event.getValue();
            if (chosen != null && chosen > 0) {
                applyPageSize(chosen, true);
            }
        });

        // Custom value typed by the user (e.g. "42")
        combo.addCustomValueSetListener(event -> {
            try {
                int custom = Integer.parseInt(event.getDetail().trim());
                if (custom > 0) {
                    combo.setValue(custom);   // triggers valueChange → applyPageSize
                }
            } catch (NumberFormatException ignored) {
                // non-numeric input — silently keep previous value
            }
        });

        Span suffix = Components.span().text("entries").styleName("page-size-selector__label").build();

        add(prefix, combo, suffix);
    }

    // -----------------------------------------------------------------------
    // Core logic
    // -----------------------------------------------------------------------

    /**
     * Applies a new page size.
     *
     * <h4>Managed-fetch mode ({@link Builder#withLazyFetch} was called)</h4>
     * <ol>
     *   <li>Resets the page offset to 0 (back to page 1).</li>
     *   <li>Updates {@code currentPageSz} so the wrapped count callback returns
     *       the new size.</li>
     *   <li>Updates the grid's page size (so the bar's {@code getPageSize()}
     *       stays accurate).</li>
     *   <li>Refreshes the listing — the wrapped fetch re-fetches from offset 0
     *       with limit = new size.</li>
     *   <li>Refreshes the bar so total page count is recomputed.</li>
     * </ol>
     *
     * <h4>Unmanaged + pagination bar</h4>
     * Delegates to {@link ItemListingPaginationBar#setPageSize(int)}, which
     * owns grid.setPageSize + scroll + bar refresh.
     *
     * <h4>Unmanaged standalone (no bar)</h4>
     * Calls {@code grid.setPageSize(n)} and — for lazy grids —
     * {@code getLazyDataView().setItemCountCallback(q -> n)} to hard-limit
     * visible rows.
     */
    private void applyPageSize(int size, boolean refreshListing) {

        // ── Managed-fetch mode ──────────────────────────────────────────────
        if (managedDataView != null) {
            currentPageSz[0]     = size;
            currentPageOffset[0] = 0;  // reset to page 1

            if (listing.getComponent() instanceof Grid<?> grid) {
                if (!paginatedMode) {
                    // Virtual-scroll: use a large batch size so the infinite scroll
                    // feels smooth; never cap rows with a count callback.
                    grid.setPageSize(Math.max(size, 50));
                    managedDataView.setItemCountUnknown();
                } else {
                    // Paginated: hard-limit to exactly 'size' rows per page.
                    grid.setPageSize(size);
                    managedDataView.setItemCountCallback(q -> size);
                }
            } else {
                if (!paginatedMode) {
                    managedDataView.setItemCountUnknown();
                } else {
                    managedDataView.setItemCountCallback(q -> size);
                }
            }

            managedDataView.refreshAll();
            if (paginatedMode && paginationBar != null) {
                paginationBar.refreshState();
            }
            return;
        }

        // ── Unmanaged + pagination bar ──────────────────────────────────────
        if (paginationBar != null) {
            paginationBar.setPageSize(size);
            return;
        }

        // ── Unmanaged standalone ────────────────────────────────────────────
        if (!(listing.getComponent() instanceof Grid<?> grid)) return;
        grid.setPageSize(size);
        try {
            @SuppressWarnings("unchecked")
            GridLazyDataView<T> lazyView = (GridLazyDataView<T>) grid.getLazyDataView();
            // Hard-limit: grid renders exactly N rows, no infinite scroll
            lazyView.setItemCountCallback(q -> size);
        } catch (IllegalStateException ignored) {
            // In-memory grid — not lazy; a plain refresh is sufficient
            if (refreshListing) listing.refresh();
        }
    }

    // -----------------------------------------------------------------------
    // Factory
    // -----------------------------------------------------------------------

    /**
     * Starts building a selector bound to the given listing.
     *
     * @param listing the listing whose page size will be controlled (not null)
     * @param <T>     item type
     * @param <P>     property type
     * @return a new {@link Builder}
     */
    public static <T, P> Builder<T, P> of(ItemListing<T, P> listing) {
        return new Builder<>(listing);
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Fluent builder for {@link ItemListingPageSizeSelector}.
     *
     * @param <T> item type
     * @param <P> property type
     */
    public static final class Builder<T, P> {

        private final ItemListing<T, P>        listing;
        private List<Integer>                  options      = DEFAULT_OPTIONS;
        private int                            defaultSize  = DEFAULT_PAGE_SIZE;
        private ItemListingPaginationBar<T, P> paginationBar;

        /** Set by {@link #withLazyFetch} — enables managed-fetch mode. */
        private CallbackDataProvider.FetchCallback<T, Void> fetchCallback;
        private Supplier<Integer>                           countSupplier;

        /** Optional: reset to page 1 when filters change (listener-based, legacy). */
        private FilterInputGroup filterGroup;

        /**
         * Optional: reset to page 1 when filters change (Signal-based, preferred).
         * When set, uses {@link Signal#effect} for lifecycle-aware reactive reset.
         */
        private FilterInputGroup filterGroupSignal;

        /** Optional: reset to page 1 when the search field value changes. */
        private TextField searchField;

        private Builder(ItemListing<T, P> listing) {
            ObjectUtils.argumentNotNull(listing, "ItemListing must be not null");
            this.listing = listing;
        }

        /**
         * Sets the page-size options shown in the dropdown.
         *
         * @param sizes one or more positive size values
         * @return this builder
         */
        public Builder<T, P> withOptions(Integer... sizes) {
            this.options = Arrays.asList(sizes);
            return this;
        }

        /**
         * Sets the page-size options shown in the dropdown.
         *
         * @param sizes non-null list of positive size values
         * @return this builder
         */
        public Builder<T, P> withOptions(List<Integer> sizes) {
            ObjectUtils.argumentNotNull(sizes, "Options must be not null");
            this.options = sizes;
            return this;
        }

        /**
         * Sets the initially selected page size.
         * Defaults to {@value ItemListingPageSizeSelector#DEFAULT_PAGE_SIZE}.
         *
         * @param size positive size
         * @return this builder
         */
        public Builder<T, P> withDefaultSize(int size) {
            if (size <= 0) throw new IllegalArgumentException("Default page size must be > 0");
            this.defaultSize = size;
            return this;
        }

        /**
         * Links a {@link ItemListingPaginationBar}.
         *
         * <p>When combined with {@link #withLazyFetch}, the bar's page navigation
         * swaps the underlying data (true pagination) instead of just scrolling
         * the viewport.</p>
         *
         * @param bar the pagination bar to keep in sync (not null)
         * @return this builder
         */
        public Builder<T, P> withPaginationBar(ItemListingPaginationBar<T, P> bar) {
            ObjectUtils.argumentNotNull(bar, "PaginationBar must be not null");
            this.paginationBar = bar;
            return this;
        }

        /**
         * Enables <em>managed-fetch mode</em> — the recommended approach when a
         * pagination bar is present.
         *
         * <p>The selector takes ownership of the data binding: it calls
         * {@code listing.setItems(wrappedCallback)} internally, wrapping your
         * callback with a mutable page offset.  <strong>Do not call
         * {@code listing.setItems()} separately when using this method.</strong></p>
         *
         * <h4>Page navigation — zero COUNT(*) queries</h4>
         * <p>Internally the wrapped fetch requests {@code pageSize + 1} rows from the
         * back end (the <em>look-ahead</em> pattern).  If {@code pageSize + 1} rows
         * come back, there is a next page; if fewer rows come back the current page is
         * the last.  The result is pushed to the bar via
         * {@link ItemListingPaginationBar#setHasNextPage(boolean)} so that
         * <strong>page navigation never issues a COUNT(*) query</strong>.</p>
         *
         * <h4>countSupplier — optional, called at construction and on filter resets</h4>
         * <p>When non-null, the supplier is called:</p>
         * <ul>
         *   <li><strong>Once at construction</strong> — to seed the bar's initial total
         *       page count so the user sees the correct number of pages immediately,
         *       before the first look-ahead fetch fires.</li>
         *   <li><strong>On each {@link ItemListingPageSizeSelector#resetToPage1()} call</strong>
         *       (and therefore on each filter change wired via
         *       {@link #withFilterReset(FilterInputGroup)}) — to recompute the total page
         *       count for the updated result set.</li>
         * </ul>
         * <p>Pass {@code null} to opt out of all count calls.  The bar will then use the
         * look-ahead result only (Next/Prev enabled/disabled, but no total-page number
         * shown until after the first fetch completes).</p>
         *
         * @param fetchCallback  lazy fetch callback (offset + limit honoured); not null
         * @param countSupplier  optional total-item count supplier; may be {@code null}
         * @return this builder
         * @since 10.0.1
         */
        public Builder<T, P> withLazyFetch(
                CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                Supplier<Integer> countSupplier) {
            ObjectUtils.argumentNotNull(fetchCallback, "FetchCallback must be not null");
            this.fetchCallback  = fetchCallback;
            this.countSupplier  = countSupplier;
            return this;
        }

        /**
         * Registers a {@link FilterInputGroup} (e.g. {@code DynamicFilterPanel})
         * whose filter-change events automatically reset the selector to page 1.
         *
         * <p>Only effective in managed-fetch mode ({@link #withLazyFetch}).
         * When the user applies a new filter:</p>
         * <ol>
         *   <li>Page offset is reset to 0 (back to page 1).</li>
         *   <li>The count supplier is re-called with the current filter closure
         *       to give the bar an updated total page count.</li>
         *   <li>The listing is refreshed so the grid re-fetches page 1 of the
         *       filtered result set.</li>
         * </ol>
         *
         * <p>The {@code countSupplier} passed to {@link #withLazyFetch} should be
         * a closure that reads the current filter at call time:</p>
         * <pre>{@code
         * .withLazyFetch(
         *     q -> service.fetch(q.getOffset(), q.getLimit(), filterPanel.getQueryFilter()),
         *     () -> service.count(filterPanel.getQueryFilter()))   // ← reads current filter
         * .withFilterReset(filterPanel)
         * }</pre>
         *
         * @param filterGroup the filter group to observe (not null)
         * @return this builder
         * @since 10.0.1
         */
        public Builder<T, P> withFilterReset(FilterInputGroup filterGroup) {
            ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
            this.filterGroup = filterGroup;
            return this;
        }

        /**
         * Registers a {@link FilterInputGroup} (e.g. {@code DynamicFilterPanel}) using
         * the Signal-based reactive approach.
         *
         * <p>Preferred over {@link #withFilterReset(FilterInputGroup)} for Vaadin 25+ as it
         * uses {@link Signal#effect} bound to this component's lifecycle, rather than a
         * persistent plain listener. The effect activates on component attach, deactivates on
         * detach, and does <strong>not</strong> fire at registration time.</p>
         *
         * <p>Only effective in managed-fetch mode ({@link #withLazyFetch}).</p>
         *
         * @param filterGroup the filter group to observe (not null)
         * @return this builder
         * @since 10.0.1
         */
        public Builder<T, P> withFilterResetSignal(FilterInputGroup filterGroup) {
            ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
            this.filterGroupSignal = filterGroup;
            return this;
        }

        /**
         * Wires a {@link TextField} so that every value change automatically resets
         * the selector to page 1 and re-fetches with the new search text.
         *
         * <p>{@link ValueChangeMode#LAZY} (400 ms debounce) is applied automatically
         * so that a backend fetch is not triggered on every keystroke.</p>
         *
         * <p>The fetch callback you pass to {@link #withLazyFetch} should read the
         * field value via closure at call time:</p>
         * <pre>{@code
         * TextField search = new TextField();
         *
         * Components.pageSizeSelector(listing)
         *     .withLazyFetch(
         *         q -> service.fetch(q.getOffset(), q.getLimit(), search.getValue()),
         *         null)
         *     .withSearchField(search)
         *     .build();
         * }</pre>
         *
         * @param searchField the text field to observe (not null)
         * @return this builder
         * @since 10.0.1
         */
        public Builder<T, P> withSearchField(TextField searchField) {
            ObjectUtils.argumentNotNull(searchField, "SearchField must be not null");
            this.searchField = searchField;
            return this;
        }

        /**
         * Builds the {@link ItemListingPageSizeSelector}.
         *
         * @return the ready-to-use component
         */
        public ItemListingPageSizeSelector<T, P> build() {
            return new ItemListingPageSizeSelector<>(this);
        }
    }
}

