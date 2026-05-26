package com.holonplatform.vaadin.flow.components.builders;

/**
 * Kanban board builder.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @since 10.0.0
 */
public interface KanbanBoardBuilder<T, C>
        extends KanbanBoardConfigurator<T, C, KanbanBoardBuilder<T, C>> {

    com.holonplatform.vaadin.flow.components.KanbanBoard<T, C> build();
}


