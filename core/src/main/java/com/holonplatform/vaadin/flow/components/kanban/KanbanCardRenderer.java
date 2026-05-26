package com.holonplatform.vaadin.flow.components.kanban;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Renders a card component for a kanban item.
 *
 * @param <T> item type
 * @since 10.0.0
 */
@FunctionalInterface
public interface KanbanCardRenderer<T> extends SerializableFunction<T, Component> {
}

