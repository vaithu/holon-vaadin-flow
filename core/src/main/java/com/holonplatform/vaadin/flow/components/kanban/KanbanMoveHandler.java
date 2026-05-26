package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Handles kanban move requests.
 *
 * <p>Implementing classes only need to provide {@link #onMove}; the
 * {@link #beforeMove} and {@link #afterMove} hooks are optional overrides
 * with no-op defaults.</p>
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanMoveHandler<T, C> extends Serializable {

    /**
     * Pre-validation hook before move execution.
     *
     * @param request move request
     * @return {@code true} to continue, {@code false} to reject
     */
    default boolean beforeMove(KanbanMoveRequest<T, C> request) {
        return true;
    }

    /**
     * Executes the move operation.
     *
     * @param request move request
     * @return move result
     */
    KanbanMoveResult onMove(KanbanMoveRequest<T, C> request);

    /**
     * Post-processing hook called for accepted and rejected results.
     *
     * @param request move request
     * @param result move result
     */
    default void afterMove(KanbanMoveRequest<T, C> request, KanbanMoveResult result) {
        // no-op
    }
}

