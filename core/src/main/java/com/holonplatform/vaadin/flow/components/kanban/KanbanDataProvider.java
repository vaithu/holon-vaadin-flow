package com.holonplatform.vaadin.flow.components.kanban;

import com.holonplatform.core.query.QueryFilter;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Data provider callback for loading column items.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanDataProvider<T, C> extends Serializable {

    /**
     * Fetches items for a column query.
     *
     * @param query query metadata
     * @param filter optional active filter, can be {@code null}
     * @return item stream
     */
    Stream<T> fetch(KanbanQuery<C> query, QueryFilter filter);
}

