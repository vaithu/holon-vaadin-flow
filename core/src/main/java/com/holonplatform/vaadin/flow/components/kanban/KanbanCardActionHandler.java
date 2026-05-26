package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Optional card actions callback.
 *
 * @param <T> item type
 * @since 10.0.0
 */
public interface KanbanCardActionHandler<T> extends Serializable {

    default void onOpen(T item) {
        // no-op
    }

    default void onEdit(T item) {
        // no-op
    }

    default void onDelete(T item) {
        // no-op
    }
}

