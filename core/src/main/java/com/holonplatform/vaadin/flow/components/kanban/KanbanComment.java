package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;
import java.time.Instant;

/**
 * Card comment entry.
 *
 * @param author comment author
 * @param message comment message
 * @param createdAt creation timestamp
 * @since 10.0.0
 */
public record KanbanComment(String author, String message, Instant createdAt) implements Serializable {

    public KanbanComment {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Comment message must be not null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Comment creation timestamp must be not null");
        }
    }

    public static KanbanComment of(String message) {
        return new KanbanComment(null, message, Instant.now());
    }

    public static KanbanComment of(String author, String message) {
        return new KanbanComment(author, message, Instant.now());
    }
}

