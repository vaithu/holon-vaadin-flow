package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Query metadata for kanban data providers.
 *
 * @param <C> column identifier type
 * @param columnId requested column identifier
 * @param offset zero-based offset
 * @param limit max items to fetch
 * @since 10.0.0
 */
public record KanbanQuery<C>(C columnId, int offset, int limit) implements Serializable {

    public KanbanQuery {
        if (columnId == null) {
            throw new IllegalArgumentException("Column id must be not null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be >= 0");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be > 0");
        }
    }
}

