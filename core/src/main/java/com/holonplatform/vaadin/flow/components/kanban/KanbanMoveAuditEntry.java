package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;
import java.time.Instant;

/**
 * Audit trail entry for a kanban move attempt.
 *
 * @param <C> column identifier type
 * @param itemId moved item identifier
 * @param fromColumn source column, if known
 * @param toColumn target column
 * @param result move result
 * @param timestamp event timestamp
 * @param reason optional reason
 * @since 10.0.0
 */
public record KanbanMoveAuditEntry<C>(
        String itemId,
        C fromColumn,
        C toColumn,
        KanbanMoveResult result,
        Instant timestamp,
        String reason) implements Serializable {

    public KanbanMoveAuditEntry {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("Item id must be not null or blank");
        }
        if (toColumn == null) {
            throw new IllegalArgumentException("Target column must be not null");
        }
        if (result == null) {
            throw new IllegalArgumentException("Move result must be not null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must be not null");
        }
    }
}


