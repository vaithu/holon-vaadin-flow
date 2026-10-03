package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.iyensoft.vaadin.flow.components.DetailList;
import com.iyensoft.vaadin.flow.components.Empty;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultDetailListBuilder;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Builds a {@link DetailList}: a compact detail panel containing a read-only listing whose rows
 * are reloaded from the selected master item.
 *
 * <pre>{@code
 * Components.lazyTabs()
 *     .withLazyTab("Orders", orderService::countByCustomer, () ->
 *         Components.detailList("Open orders", OrderRow.class, orderService::findByCustomer)
 *             .column(OrderRow::number, "Order")
 *             .column(OrderRow::status, "Status")
 *             .build());
 * }</pre>
 *
 * @param <I> selected master item type
 * @param <R> row type
 */
public interface DetailListBuilder<I, R> extends DetailPanelConfigurator<DetailListBuilder<I, R>> {

    /**
     * Creates a detail list builder.
     *
     * @param title panel title (not null)
     * @param rowType row type (not null)
     * @param rows maps the selected item to its rows (not null)
     * @return a new builder
     */
    static <I, R> DetailListBuilder<I, R> create(String title, Class<R> rowType,
                                                 SerializableFunction<? super I, ? extends Collection<R>> rows) {
        return new DefaultDetailListBuilder<>(title, rowType, rows);
    }

    /**
     * Adds an auto-width column.
     *
     * @param valueProvider provides the cell value from a row (not null)
     * @param header column header
     * @return this builder
     */
    DetailListBuilder<I, R> column(ValueProvider<R, ?> valueProvider, String header);

    /**
     * Configures the state shown when the selected item has no related rows.
     *
     * @param title empty-state title (not null)
     * @param description optional supporting description
     * @return this builder
     */
    DetailListBuilder<I, R> emptyState(String title, String description);

    /**
     * Configures a custom component shown when the selected item has no related rows.
     *
     * @param emptyState empty-state component (not null)
     * @return this builder
     */
    DetailListBuilder<I, R> emptyState(Empty emptyState);

    /**
     * Configures the state shown when the selected item has no related rows.
     *
     * @param title empty-state title (not null)
     * @return this builder
     */
    default DetailListBuilder<I, R> emptyState(String title) {
        return emptyState(title, null);
    }

    /**
     * Applies additional configuration to the underlying Holon listing builder, such as
     * renderers, component columns, or extra theme variants.
     *
     * @param configurer listing configuration (not null)
     * @return this builder
     */
    DetailListBuilder<I, R> listing(Consumer<BeanListingBuilder<R>> configurer);

    /**
     * Builds the detail list.
     *
     * @return the configured detail list
     */
    DetailList<I, R> build();
}
