package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;
import java.util.List;

/**
 * Provides comments for a card.
 *
 * @param <T> item type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanCommentProvider<T> extends Serializable {

    /**
     * Gets comments for the given item.
     *
     * @param item item
     * @return comments list
     */
    List<KanbanComment> getComments(T item);
}

