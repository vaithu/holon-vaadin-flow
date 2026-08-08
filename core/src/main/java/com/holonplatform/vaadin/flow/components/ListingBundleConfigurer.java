package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Build-time configuration contract for a listing bundle — shared by
 * {@link ListingBundleBuilder} (standalone builder) and
 * {@link com.iyensoft.vaadin.flow.components.builders.MasterConfigurator.ListingBundleNode}
 * (embedded listing inside a master panel).
 *
 * <p>All methods return {@code C} so the concrete self-type is preserved throughout
 * the fluent chain. The terminal operation ({@code build()} or {@code add()}) is
 * defined by the subtype, not here.</p>
 *
 * @param <T> bean item type
 * @param <C> concrete configurator self-type
 * @since 10.0.1
 */
public interface ListingBundleConfigurer<T, C extends ListingBundleConfigurer<T, C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // ── Row action ───────────────────────────────────────────────────────────

    /**
     * Describes a single action rendered inside the per-row actions column.
     *
     * @param <T>     bean item type
     * @param icon    optional icon shown to the left of the label ({@code null} = text-only)
     * @param label   display text of the menu item
     * @param handler    callback invoked with the row item when this action is selected
     * @param destructive when {@code true} the button is styled with the {@code error} theme
     *                    (red colour) — use for irreversible actions such as Delete
     */
    record RowAction<T>(VaadinIcon icon, String label, Consumer<T> handler, boolean destructive) {
        /** Convenience factory — no icon, non-destructive. */
        public static <T> RowAction<T> of(String label, Consumer<T> handler) {
            return new RowAction<>(null, label, handler, false);
        }
        /** Convenience factory — with icon, non-destructive. */
        public static <T> RowAction<T> of(VaadinIcon icon, String label, Consumer<T> handler) {
            return new RowAction<>(icon, label, handler, false);
        }
        /** Convenience factory — with icon and explicit destructive flag. */
        public static <T> RowAction<T> of(VaadinIcon icon, String label, Consumer<T> handler, boolean destructive) {
            return new RowAction<>(icon, label, handler, destructive);
        }
    }

    // ── Fetch callbacks ──────────────────────────────────────────────────────

    @FunctionalInterface
    interface FetchCallback<T> extends Serializable {
        Stream<T> fetch(Query<T, Void> query, String searchText, QuerySort sort);
    }

    @FunctionalInterface
    interface FilteredFetchCallback<T> extends Serializable {
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter, QuerySort sort);
    }

    @FunctionalInterface
    interface ColumnAwareFilteredFetchCallback<T> extends Serializable {
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter, QuerySort sort, List<String> columns);
    }

    // ── Grid-header ──────────────────────────────────────────────────────────

    C gridHeader(Component... components);

    C gridHeader(String title, Component... contextActions);

    C gridHeader(String title);

    // ── Column header labels ──────────────────────────────────────────────────

    C header(String column, String label);

    C header(String column, Localizable localizable);

    C header(String column, String defaultLabel, String messageCode);

    // ── Columns ───────────────────────────────────────────────────────────────

    C columns(String... cols);

    C hidden(String... cols);

    // ── Data fetch ────────────────────────────────────────────────────────────

    C fetch(FetchCallback<T> callback);

    C fetch(FilteredFetchCallback<T> callback);

    C fetch(ColumnAwareFilteredFetchCallback<T> callback);

    // ── Pagination ────────────────────────────────────────────────────────────

    C pageSizes(Integer... sizes);

    C defaultPageSize(int size);

    C paginated();

    C virtualScroll();

    C paginated(boolean paginated);

    // ── Search field ──────────────────────────────────────────────────────────

    C search(String placeholder);

    C search(Localizable localizable);

    C search(String defaultPlaceholder, String messageCode);

    // ── Filter panel ──────────────────────────────────────────────────────────

    C withFilterPanel();

    C withFilterPanel(boolean advancedMode);

    C advancedSearchLabel(String label);

    C retainFilterValues(boolean retain);

    // ── Menu actions ──────────────────────────────────────────────────────────

    C withMenuAction(String label, Runnable action);

    C withMenuAction(VaadinIcon icon, String label, Runnable action);

    // ── Misc ──────────────────────────────────────────────────────────────────

    C multiSelect();

    C autoCreateColumns(boolean autoCreate);

    C importAction(Runnable action);

    C exportAction(Runnable action);

    // ── Item click ────────────────────────────────────────────────────────────

    /** Register a global item-click listener (wired during build). */
    C onItemClick(ComponentEventListener<ItemClickEvent<T>> listener);

    // ── Mobile / responsive ───────────────────────────────────────────────────

    C onItemClickListener(ViewMode viewMode, ComponentEventListener<ItemClickEvent<T>> listener);

    C viewModeSupplier(Supplier<ViewMode> supplier);

    C mobileViewColumn(Renderer<T> renderer);

    C mobileViewHeader(String text);

    C mobileViewHeader(Component component);

    // ── Post-processor ────────────────────────────────────────────────────────

    /**
     * Registers a callback that is invoked on the fully-built {@link ItemListing}
     * <em>after</em> all standard columns and the actions column have been added,
     * but <em>before</em> the {@link ListingBundle} wrapper is assembled.
     *
     * <p>Use this as an escape hatch for low-level {@code Grid} / {@code ItemListing}
     * customisation not covered by the fluent API — e.g. custom column renderers,
     * conditional cell styles, frozen columns, or programmatic sort defaults.</p>
     *
     * <pre>{@code
     * Components.listing(Product.class)
     *     .columns("id", "name", "price")
     *     .withListingPostProcessor(listing -> {
     *         listing.getAllColumns().stream()
     *             .filter(c -> "price".equals(c.getKey()))
     *             .findFirst()
     *             .ifPresent(c -> c.setRenderer(new NumberRenderer<>(
     *                 Product::getPrice, "%(,.2f", Locale.US)));
     *     })
     *     .build();
     * }</pre>
     */
    C withListingPostProcessor(Consumer<ItemListing<T, ?>> postProcessor);

    // ── Per-row actions column ────────────────────────────────────────────────

    /**
     * Registers an <em>Edit</em> action in the per-row actions column.
     * The column is rendered as a frozen-to-end column with a vertical ellipsis (⋮)
     * trigger that opens a {@code MenuBar} sub-menu listing all registered actions.
     * The column is added automatically when at least one action is registered.
     *
     * <pre>{@code
     * Components.listing(Product.class)
     *     .withEditAction(product -> navigator.navigateTo(EditView.class, product.getId()))
     *     .withDeleteAction(product -> service.delete(product.getId()))
     *     .build();
     * }</pre>
     */
    C withEditAction(Consumer<T> onEdit);

    /**
     * Registers a <em>Delete</em> action in the per-row actions column.
     *
     * @see #withEditAction(Consumer)
     */
    C withDeleteAction(Consumer<T> onDelete);

    /**
     * Adds a custom action with an icon to the per-row actions column.
     *
     * @param icon    icon shown to the left of the label
     * @param label   display text
     * @param handler called with the row item when the action is selected
     */
    C withRowAction(VaadinIcon icon, String label, Consumer<T> handler);

    /**
     * Adds a text-only custom action to the per-row actions column.
     *
     * @param label   display text
     * @param handler called with the row item when the action is selected
     */
    C withRowAction(String label, Consumer<T> handler);

    // ── High-performance mode ─────────────────────────────────────────────────

    /**
     * Switches the per-row actions column from the default
     * {@code ComponentRenderer} (one {@code MenuBar} instance <em>per visible row</em>)
     * to a high-performance mode that uses:
     * <ul>
     *   <li>A <b>single shared {@code ContextMenu}</b> for the entire grid — O(1) server
     *       components regardless of row count or page size.</li>
     *   <li>A <b>{@code LitRenderer}</b> for the trigger cell — purely client-side DOM,
     *       zero server-side component cost per row.</li>
     *   <li>The grid's native {@code itemClickListener} (carries {@code clientX/Y}) to
     *       open the menu at the exact cursor position.</li>
     * </ul>
     *
     * <p><b>When to use:</b> high-concurrency deployments (hundreds of simultaneous
     * sessions), or grids with virtual scroll and large visible row counts.</p>
     *
     * <pre>{@code
     * Components.listing(Product.class)
     *     .withEditAction(p -> navigator.navigateTo(EditView.class, p.getId()))
     *     .withDeleteAction(p -> service.delete(p.getId()))
     *     .withHighPerformanceActions()  // flip to O(1) mode
     *     .build();
     * }</pre>
     */
    C withHighPerformanceActions();

    // ── Empty state ───────────────────────────────────────────────────────────

    /**
     * Sets a custom {@link Empty} component to display when the grid has no items at all
     * (i.e. the dataset is genuinely empty — no search text or filter is active).
     *
     * <p>Only effective in managed-fetch mode (when {@link #fetch(FetchCallback)} or
     * its overloads are called on the builder).</p>
     *
     * @param emptyState the empty state component to show (not null)
     * @return this configurator
     */
    C emptyState(Empty emptyState);

    /**
     * Adds a default empty state shown when the grid has no items at all.
     * The default uses a {@code VaadinIcon.INBOX} icon, "No items" title, and a short description.
     *
     * @return this configurator
     */
    C emptyState();

    /**
     * Sets a custom {@link Empty} component to display when a search or filter is active
     * but no records match the current criteria.
     *
     * <p>Only effective in managed-fetch mode.</p>
     *
     * @param noResultsState the no-results state component to show (not null)
     * @return this configurator
     */
    C noResultsState(Empty noResultsState);

    /**
     * Adds a default no-results state shown when search/filter is active but yields no records.
     * The default uses a {@code VaadinIcon.SEARCH} icon, "No results found" title, and a hint.
     *
     * @return this configurator
     */
    C noResultsState();
}
