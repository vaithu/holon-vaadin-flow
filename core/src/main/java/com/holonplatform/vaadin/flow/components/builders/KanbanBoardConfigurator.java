package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.kanban.*;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Kanban board configurator.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @param <B> concrete configurator type
 * @since 10.0.0
 */
public interface KanbanBoardConfigurator<T, C, B extends KanbanBoardConfigurator<T, C, B>> {

    B withColumn(KanbanColumn<C> column);

    B withColumns(Collection<KanbanColumn<C>> columns);

    B withCardRenderer(KanbanCardRenderer<T> cardRenderer);

    B withItemIdentifierProvider(Function<T, String> itemIdentifierProvider);

    B withColumnIdSerializer(Function<C, String> columnIdSerializer);

    B withItemColumnProvider(Function<T, C> itemColumnProvider);

    B withItemColumnUpdater(BiConsumer<T, C> itemColumnUpdater);

    B withMoveHandler(KanbanMoveHandler<T, C> moveHandler);

    B withColumnCountProvider(KanbanCountProvider<C> countProvider);

    B withColumnPageSize(int pageSize);

    B withCardActionHandler(KanbanCardActionHandler<T> cardActionHandler);

    B withColumnActionHandler(KanbanColumnActionHandler<C> columnActionHandler);

    B withCommentProvider(KanbanCommentProvider<T> commentProvider);

    B withCommentHandler(KanbanCommentHandler<T> commentHandler);

    /**
     * Sets the i18n labels used for internally-rendered buttons.
     *
     * @param i18n the {@link KanbanI18n} instance; {@code null} resets to English defaults
     * @return this configurator for chaining
     * @see KanbanI18n#defaults()
     */
    B withI18n(KanbanI18n i18n);

    B withItems(Collection<T> items);

    B withDataProvider(KanbanDataProvider<T, C> dataProvider);
}



