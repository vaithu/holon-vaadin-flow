package com.holonplatform.vaadin.flow.components.kanban;

import com.holonplatform.core.query.QueryFilter;

import java.io.Serializable;

/**
 * Counts items for a kanban column.
 *
 * @param <C> column identifier type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanCountProvider<C> extends Serializable {

    /**
     * Returns item count for the given column and optional filter.
     *
     * @param columnId column identifier
     * @param filter active filter, or {@code null}
     * @return total item count
     */
    long count(C columnId, QueryFilter filter);
}

