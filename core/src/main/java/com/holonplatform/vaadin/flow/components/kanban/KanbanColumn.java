package com.holonplatform.vaadin.flow.components.kanban;

import java.io.Serializable;

/**
 * Kanban column metadata.
 *
 * @param <C> column identifier type
 * @param id unique column identifier
 * @param label display label
 * @param className optional CSS class name
 * @since 10.0.0
 */
public record KanbanColumn<C>(C id, String label, String className) implements Serializable {

    public KanbanColumn {
        if (id == null) {
            throw new IllegalArgumentException("Column id must be not null");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Column label must be not null or blank");
        }
    }

    public static <C> KanbanColumn<C> of(C id, String label) {
        return new KanbanColumn<>(id, label, null);
    }

    public static <C> KanbanColumn<C> of(C id, String label, String className) {
        return new KanbanColumn<>(id, label, className);
    }
}

