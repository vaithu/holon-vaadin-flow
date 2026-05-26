package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.KanbanBoardBuilder;
import com.holonplatform.vaadin.flow.components.kanban.*;
import com.holonplatform.vaadin.flow.internal.components.DefaultKanbanBoard;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Generic kanban board component.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @since 10.0.0
 */
public interface KanbanBoard<T, C> extends HasComponent {

    static <T, C> KanbanBoardBuilder<T, C> builder() {
        return new DefaultKanbanBoard.DefaultKanbanBoardBuilder<>();
    }

    Collection<KanbanColumn<C>> getColumns();

    void setColumns(Collection<KanbanColumn<C>> columns);

    void setCardRenderer(KanbanCardRenderer<T> cardRenderer);

    void setItemIdentifierProvider(Function<T, String> itemIdentifierProvider);

    void setColumnIdSerializer(Function<C, String> columnIdSerializer);

    void setItemColumnProvider(Function<T, C> itemColumnProvider);

    void setItemColumnUpdater(BiConsumer<T, C> itemColumnUpdater);

    void setMoveHandler(KanbanMoveHandler<T, C> moveHandler);

    void setColumnCountProvider(KanbanCountProvider<C> countProvider);

    void setColumnPageSize(int pageSize);

    int getColumnPageSize();

    void setCardActionHandler(KanbanCardActionHandler<T> cardActionHandler);

    void setColumnActionHandler(KanbanColumnActionHandler<C> columnActionHandler);

    void setCommentProvider(KanbanCommentProvider<T> commentProvider);

    void setCommentHandler(KanbanCommentHandler<T> commentHandler);

    /**
     * Sets the i18n labels used for internally-rendered buttons (options, add card,
     * open, edit, delete). Passing {@code null} resets to the built-in English defaults.
     *
     * @param i18n the {@link KanbanI18n} instance; {@code null} resets to defaults
     */
    void setI18n(KanbanI18n i18n);

    void setItems(Collection<T> items);

    void setItems(KanbanDataProvider<T, C> dataProvider);

    void setItems(FilterInputGroup filterGroup, KanbanDataProvider<T, C> dataProvider);

    Optional<C> getColumnOf(T item);

    Collection<KanbanMoveAuditEntry<C>> getMoveAuditTrail();

    void clearMoveAuditTrail();

    Collection<KanbanComment> getComments(T item);

    boolean addComment(T item, KanbanComment comment);

    boolean moveItem(T item, C toColumn);

    void refresh();

    default Registration refreshOnFilterChange(FilterInputGroup filterGroup) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        return filterGroup.addFilterChangeListener(event -> refresh());
    }

    default Registration bindFilters(FilterInputGroup filterGroup, KanbanDataProvider<T, C> dataProvider) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        ObjectUtils.argumentNotNull(dataProvider, "KanbanDataProvider must be not null");
        setItems(filterGroup, dataProvider);
        return refreshOnFilterChange(filterGroup);
    }
}



