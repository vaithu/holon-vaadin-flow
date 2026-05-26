package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Move request context for drag-and-drop or programmatic moves.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @param item moved item
 * @param fromColumn source column id
 * @param toColumn target column id
 * @since 10.0.0
 */
public record KanbanMoveRequest<T, C>(T item, C fromColumn, C toColumn) implements Serializable {

    public KanbanMoveRequest {
        if (item == null) {
            throw new IllegalArgumentException("Item must be not null");
        }
        if (fromColumn == null) {
            throw new IllegalArgumentException("Source column must be not null");
        }
        if (toColumn == null) {
            throw new IllegalArgumentException("Target column must be not null");
        }
    }
}

