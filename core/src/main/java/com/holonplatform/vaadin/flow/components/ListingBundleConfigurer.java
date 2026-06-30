package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;

import java.util.List;
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

    // ── Fetch callbacks ──────────────────────────────────────────────────────

    @FunctionalInterface
    interface FetchCallback<T> {
        Stream<T> fetch(Query<T, Void> query, String searchText, QuerySort sort);
    }

    @FunctionalInterface
    interface FilteredFetchCallback<T> {
        Stream<T> fetch(Query<T, Void> query, String searchText, QueryFilter filter, QuerySort sort);
    }

    @FunctionalInterface
    interface ColumnAwareFilteredFetchCallback<T> {
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
}
