package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Handles comment creation for a card.
 *
 * @param <T> item type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanCommentHandler<T> extends Serializable {

    /**
     * Handles a new comment for the item.
     *
     * @param item item
     * @param comment comment
     */
    void addComment(T item, KanbanComment comment);
}

