package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Optional column-level action callbacks.
 *
 * @param <C> column identifier type
 * @since 10.0.0
 */
public interface KanbanColumnActionHandler<C> extends Serializable {

    /**
     * Called when the column options action is triggered.
     *
     * @param columnId column id
     */
    default void onOptions(C columnId) {
        // no-op
    }

    /**
     * Called when the content-card action is triggered.
     *
     * @param columnId column id
     */
    default void onAddCard(C columnId) {
        // no-op
    }
}

