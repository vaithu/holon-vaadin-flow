package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.components.KanbanBoard;
import com.holonplatform.vaadin.flow.components.builders.KanbanBoardBuilder;
import com.holonplatform.vaadin.flow.components.kanban.*;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Default {@link KanbanBoard} implementation.
 *
 * @param <T> item type
 * @param <C> column identifier type
 * @since 10.0.0
 */
@StyleSheet("context://kanban-board.css")
public class DefaultKanbanBoard<T, C> extends Div implements KanbanBoard<T, C> {

    @Serial
    private static final long serialVersionUID = 5546321834888776344L;

    private static final int DEFAULT_COLUMN_FETCH_LIMIT = 100;

    private final List<KanbanColumn<C>> columns = new ArrayList<>();
    private final Map<String, C> columnsBySerializedId = new HashMap<>();
    private final Map<String, T> itemsById = new HashMap<>();
    private final Map<T, C> renderedColumnByItem = new HashMap<>();
    private final IdentityHashMap<T, String> generatedItemIds = new IdentityHashMap<>();
    private final List<KanbanMoveAuditEntry<C>> moveAuditTrail = new ArrayList<>();
    private final Map<String, List<KanbanComment>> commentsByItemId = new HashMap<>();

    private final Div columnsLayout = Components.div().build();

    private KanbanCardRenderer<T> cardRenderer = item -> Components.span().text(String.valueOf(item)).build();
    private KanbanCardActionHandler<T> cardActionHandler;
    private KanbanColumnActionHandler<C> columnActionHandler;
    private Function<T, String> itemIdentifierProvider;
    private Function<C, String> columnIdSerializer = Objects::toString;
    private Function<T, C> itemColumnProvider;
    private BiConsumer<T, C> itemColumnUpdater;
    private KanbanMoveHandler<T, C> moveHandler;
    private KanbanCountProvider<C> countProvider;
    private KanbanCommentProvider<T> commentProvider;
    private KanbanCommentHandler<T> commentHandler;
    private KanbanDataProvider<T, C> dataProvider;
    private FilterInputGroup filterGroup;
    private int columnPageSize = DEFAULT_COLUMN_FETCH_LIMIT;
    private KanbanI18n i18n = KanbanI18n.defaults();

    public DefaultKanbanBoard() {
        addClassName("kanban-board");
        columnsLayout.addClassName("kanban-board-columns");
        add(columnsLayout);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Collection<KanbanColumn<C>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    @Override
    public void setColumns(Collection<KanbanColumn<C>> columns) {
        ObjectUtils.argumentNotNull(columns, "Columns collection must be not null");
        this.columns.clear();
        this.columns.addAll(columns);
        refresh();
    }

    @Override
    public void setCardRenderer(KanbanCardRenderer<T> cardRenderer) {
        ObjectUtils.argumentNotNull(cardRenderer, "Card renderer must be not null");
        this.cardRenderer = cardRenderer;
        refresh();
    }

    @Override
    public void setItemIdentifierProvider(Function<T, String> itemIdentifierProvider) {
        ObjectUtils.argumentNotNull(itemIdentifierProvider, "Item identifier provider must be not null");
        this.itemIdentifierProvider = itemIdentifierProvider;
        refresh();
    }

    @Override
    public void setColumnIdSerializer(Function<C, String> columnIdSerializer) {
        ObjectUtils.argumentNotNull(columnIdSerializer, "Column id serializer must be not null");
        this.columnIdSerializer = columnIdSerializer;
        refresh();
    }

    @Override
    public void setItemColumnProvider(Function<T, C> itemColumnProvider) {
        ObjectUtils.argumentNotNull(itemColumnProvider, "Item column provider must be not null");
        this.itemColumnProvider = itemColumnProvider;
        refresh();
    }

    @Override
    public void setItemColumnUpdater(BiConsumer<T, C> itemColumnUpdater) {
        ObjectUtils.argumentNotNull(itemColumnUpdater, "Item column updater must be not null");
        this.itemColumnUpdater = itemColumnUpdater;
    }

    @Override
    public void setMoveHandler(KanbanMoveHandler<T, C> moveHandler) {
        ObjectUtils.argumentNotNull(moveHandler, "Move handler must be not null");
        this.moveHandler = moveHandler;
    }

    @Override
    public void setColumnCountProvider(KanbanCountProvider<C> countProvider) {
        ObjectUtils.argumentNotNull(countProvider, "Count provider must be not null");
        this.countProvider = countProvider;
        refresh();
    }

    @Override
    public void setColumnPageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Column page size must be > 0");
        }
        this.columnPageSize = pageSize;
        refresh();
    }

    @Override
    public int getColumnPageSize() {
        return columnPageSize;
    }

    @Override
    public void setCardActionHandler(KanbanCardActionHandler<T> cardActionHandler) {
        ObjectUtils.argumentNotNull(cardActionHandler, "Card action handler must be not null");
        this.cardActionHandler = cardActionHandler;
        refresh();
    }

    @Override
    public void setColumnActionHandler(KanbanColumnActionHandler<C> columnActionHandler) {
        ObjectUtils.argumentNotNull(columnActionHandler, "Column action handler must be not null");
        this.columnActionHandler = columnActionHandler;
        refresh();
    }

    @Override
    public void setCommentProvider(KanbanCommentProvider<T> commentProvider) {
        ObjectUtils.argumentNotNull(commentProvider, "Comment provider must be not null");
        this.commentProvider = commentProvider;
        refresh();
    }

    @Override
    public void setCommentHandler(KanbanCommentHandler<T> commentHandler) {
        ObjectUtils.argumentNotNull(commentHandler, "Comment handler must be not null");
        this.commentHandler = commentHandler;
    }

    @Override
    public void setI18n(KanbanI18n i18n) {
        this.i18n = (i18n != null) ? i18n : KanbanI18n.defaults();
        refresh();
    }

    /**
     * Re-render with the confirmed UI locale so button labels reflect the correct translation.
     * The first {@code refresh()} during the build phase may fire before the UI locale is known;
     * this second pass on attach guarantees correct labels.
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refresh();
    }

    // ── Label resolution ─────────────────────────────────────────────────────

    /**
     * Resolves a {@link Localizable} label using {@link LocalizationProvider}.
     * Falls back to {@link Localizable#getMessage()} when no locale / translation is available.
     */
    private static String resolveLabel(Localizable localizable) {
        return LocalizationProvider.localize(localizable).orElseGet(localizable::getMessage);
    }

    @Override
    public void setItems(Collection<T> items) {
        ObjectUtils.argumentNotNull(items, "Items must be not null");
        this.dataProvider = (query, filter) -> items.stream()
                .filter(item -> Objects.equals(query.columnId(), getItemColumn(item)))
                .skip(query.offset())
                .limit(query.limit());
        refresh();
    }

    @Override
    public void setItems(KanbanDataProvider<T, C> dataProvider) {
        this.filterGroup = null;
        ObjectUtils.argumentNotNull(dataProvider, "KanbanDataProvider must be not null");
        this.dataProvider = dataProvider;
        refresh();
    }

    @Override
    public void setItems(FilterInputGroup filterGroup, KanbanDataProvider<T, C> dataProvider) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        this.filterGroup = filterGroup;
        ObjectUtils.argumentNotNull(dataProvider, "KanbanDataProvider must be not null");
        this.dataProvider = dataProvider;
        refresh();
    }

    @Override
    public Optional<C> getColumnOf(T item) {
        if (item == null) {
            return Optional.empty();
        }
        final C rendered = renderedColumnByItem.get(item);
        if (rendered != null) {
            return Optional.of(rendered);
        }
        return Optional.ofNullable(itemColumnProvider != null ? itemColumnProvider.apply(item) : null);
    }

    @Override
    public Collection<KanbanMoveAuditEntry<C>> getMoveAuditTrail() {
        return Collections.unmodifiableList(moveAuditTrail);
    }

    @Override
    public void clearMoveAuditTrail() {
        moveAuditTrail.clear();
    }

    @Override
    public Collection<KanbanComment> getComments(T item) {
        if (item == null) {
            return Collections.emptyList();
        }
        final List<KanbanComment> comments = new ArrayList<>();
        if (commentProvider != null) {
            final List<KanbanComment> provided = commentProvider.getComments(item);
            if (provided != null) {
                comments.addAll(provided);
            }
        }
        final List<KanbanComment> localComments = commentsByItemId.get(resolveItemId(item));
        if (localComments != null) {
            comments.addAll(localComments);
        }
        comments.sort(Comparator.comparing(KanbanComment::createdAt));
        return Collections.unmodifiableList(comments);
    }

    @Override
    public boolean addComment(T item, KanbanComment comment) {
        if (item == null || comment == null) {
            return false;
        }
        commentsByItemId.computeIfAbsent(resolveItemId(item), key -> new ArrayList<>()).add(comment);
        if (commentHandler != null) {
            commentHandler.addComment(item, comment);
        }
        refresh();
        return true;
    }

    @Override
    public boolean moveItem(T item, C toColumn) {
        if (item == null || toColumn == null) {
            return false;
        }
        final String itemId = resolveItemId(item);
        final Optional<C> sourceColumn = getColumnOf(item);
        if (columns.stream().noneMatch(c -> Objects.equals(c.id(), toColumn))) {
            appendMoveAudit(itemId, sourceColumn.orElse(null), toColumn, KanbanMoveResult.REJECTED,
                    "unknown-target-column");
            return false;
        }
        if (sourceColumn.isEmpty()) {
            appendMoveAudit(itemId, null, toColumn, KanbanMoveResult.REJECTED, "source-column-not-found");
            return false;
        }
        if (Objects.equals(sourceColumn.get(), toColumn)) {
            appendMoveAudit(itemId, sourceColumn.get(), toColumn, KanbanMoveResult.ACCEPTED, "same-column");
            return true;
        }

        final KanbanMoveRequest<T, C> request = new KanbanMoveRequest<>(item, sourceColumn.get(), toColumn);
        final KanbanMoveResult result;

        if (moveHandler != null) {
            if (!moveHandler.beforeMove(request)) {
                appendMoveAudit(itemId, sourceColumn.get(), toColumn, KanbanMoveResult.REJECTED, "before-move-rejected");
                return false;
            }
            result = moveHandler.onMove(request);
            if (result == null) {
                appendMoveAudit(itemId, sourceColumn.get(), toColumn, KanbanMoveResult.REJECTED, "null-move-result");
                moveHandler.afterMove(request, KanbanMoveResult.REJECTED);
                return false;
            }
            moveHandler.afterMove(request, result);
        } else if (itemColumnUpdater != null) {
            itemColumnUpdater.accept(item, toColumn);
            result = KanbanMoveResult.ACCEPTED;
        } else {
            appendMoveAudit(itemId, sourceColumn.get(), toColumn, KanbanMoveResult.REJECTED, "no-move-handler");
            return false;
        }

        appendMoveAudit(itemId, sourceColumn.get(), toColumn, result, null);

        if (KanbanMoveResult.ACCEPTED.equals(result)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    public void refresh() {
        columnsLayout.removeAll();
        columnsBySerializedId.clear();
        itemsById.clear();
        renderedColumnByItem.clear();

        if (columns.isEmpty()) {
            return;
        }

        for (KanbanColumn<C> column : columns) {
            final String serializedColumnId = columnIdSerializer.apply(column.id());
            if (serializedColumnId == null || serializedColumnId.isBlank()) {
                throw new IllegalStateException("Serialized column id must be not null or blank");
            }
            if (columnsBySerializedId.containsKey(serializedColumnId)) {
                throw new IllegalStateException("Duplicate serialized column id: " + serializedColumnId);
            }
            columnsBySerializedId.put(serializedColumnId, column.id());
            columnsLayout.add(buildColumnComponent(column, serializedColumnId));
        }
    }

    @ClientCallable
    protected void onClientDrop(String itemId, String serializedColumnId) {
        if (itemId == null || serializedColumnId == null) {
            return;
        }
        final T item = itemsById.get(itemId);
        final C toColumn = columnsBySerializedId.get(serializedColumnId);
        if (item != null && toColumn != null) {
            moveItem(item, toColumn);
        }
    }

    private Div buildColumnComponent(KanbanColumn<C> column, String serializedColumnId) {
        final Div columnRoot = Components.div().styleName("kanban-board-column").build();
        if (column.className() != null && !column.className().isBlank()) {
            columnRoot.addClassName(column.className());
        }

        final Div header = Components.div().styleName("kanban-board-column-header").build();

        final Span title = Components.span().text(column.label()).styleName("kanban-board-column-title").build();

        final Button optionsButton = Components.button()
                .text(resolveLabel(i18n.getColumnOptions()))
                .styleName("kanban-board-column-options")
                .withClickListener(event -> {
                    if (columnActionHandler != null) {
                        columnActionHandler.onOptions(column.id());
                    }
                })
                .build();

        final Div cards = Components.div().styleName("kanban-board-column-cards").build();
        wireDropTarget(cards, serializedColumnId);

        final List<T> items = fetchColumnItems(column.id());
        final Span count = Components.span().text(String.valueOf(resolveColumnCount(column.id(), items.size()))).styleName("kanban-board-column-count").build();

        final Div heading = Components.div().add(title, count).styleName("kanban-board-column-heading").build();
        header.add(heading, optionsButton);
        columnRoot.add(header, cards);

        if (columnActionHandler != null) {
            final Button addCardButton = Components.button()
                    .text(resolveLabel(i18n.getAddCard()))
                    .styleName("kanban-board-column-add-card")
                    .withClickListener(event -> columnActionHandler.onAddCard(column.id()))
                    .build();
            columnRoot.add(addCardButton);
        }

        for (T item : items) {
            renderedColumnByItem.put(item, column.id());
            cards.add(buildCardComponent(item));
        }

        return columnRoot;
    }

    private List<T> fetchColumnItems(C columnId) {
        if (dataProvider == null) {
            return Collections.emptyList();
        }
        final QueryFilter filter = (filterGroup != null)
                ? filterGroup.getQueryFilter().orElse(null)
                : null;
        try (Stream<T> stream = dataProvider.fetch(new KanbanQuery<>(columnId, 0, columnPageSize), filter)) {
            if (stream == null) {
                return Collections.emptyList();
            }
            return stream.toList();
        }
    }

    private long resolveColumnCount(C columnId, int loadedCount) {
        if (countProvider == null) {
            return loadedCount;
        }
        final QueryFilter filter = (filterGroup != null)
                ? filterGroup.getQueryFilter().orElse(null)
                : null;
        return countProvider.count(columnId, filter);
    }

    private Div buildCardComponent(T item) {
        final Div card = Components.div().styleName("kanban-board-card").build();

        final Component rendered = cardRenderer.apply(item);
        if (rendered != null) {
            card.add(rendered);
        }

        if (cardActionHandler != null) {
            final Div actions = Components.div().styleName("kanban-board-card-actions").build();

            final Button openButton = Components.button().text(resolveLabel(i18n.getOpen())).styleName("kanban-board-card-action-open").withClickListener(event -> cardActionHandler.onOpen(item)).build();
            final Button editButton = Components.button().text(resolveLabel(i18n.getEdit())).styleName("kanban-board-card-action-edit").withClickListener(event -> cardActionHandler.onEdit(item)).build();
            final Button deleteButton = Components.button().text(resolveLabel(i18n.getDelete())).styleName("kanban-board-card-action-delete").withClickListener(event -> cardActionHandler.onDelete(item)).build();

            actions.add(openButton, editButton, deleteButton);
            card.add(actions);
        }

        final Collection<KanbanComment> comments = getComments(item);
        if (!comments.isEmpty()) {
            final Div commentsContainer = Components.div().styleName("kanban-board-card-comments").build();
            for (KanbanComment comment : comments) {
                final Div commentRow = Components.div().styleName("kanban-board-card-comment").build();
                commentRow.getElement().setText(comment.message());
                commentsContainer.add(commentRow);
            }
            card.add(commentsContainer);
        }

        final String itemId = resolveItemId(item);
        final T existing = itemsById.putIfAbsent(itemId, item);
        if (existing != null && existing != item) {
            throw new IllegalStateException("Duplicate kanban item id: " + itemId);
        }

        card.getElement().setProperty("draggable", true);
        card.getElement().setAttribute("data-kanban-item-id", itemId);
        card.getElement().executeJs(
                "const host=this;"
                        + "if(host.__kanbanDragWired){return;}"
                        + "host.__kanbanDragWired=true;"
                        + "host.addEventListener('dragstart', e => {"
                        + "const id = host.getAttribute('data-kanban-item-id');"
                        + "if(e.dataTransfer && id){e.dataTransfer.setData('text/plain', id);}"
                        + "});");

        return card;
    }

    private void wireDropTarget(Div target, String serializedColumnId) {
        target.getElement().executeJs(
                "const host=this;"
                        + "if(host.__kanbanDropWired){return;}"
                        + "host.__kanbanDropWired=true;"
                        + "host.addEventListener('dragover', e => {e.preventDefault(); host.classList.add('kanban-board-drop-target');});"
                        + "host.addEventListener('dragleave', () => host.classList.remove('kanban-board-drop-target'));"
                        + "host.addEventListener('drop', e => {"
                        + "e.preventDefault();"
                        + "host.classList.remove('kanban-board-drop-target');"
                        + "const id = e.dataTransfer ? e.dataTransfer.getData('text/plain') : null;"
                        + "if(id){$0.$server.onClientDrop(id, $1);}"
                        + "});",
                getElement(), serializedColumnId);
    }

    private C getItemColumn(T item) {
        if (itemColumnProvider == null) {
            throw new IllegalStateException("Item column provider is required when using setItems(Collection)");
        }
        return itemColumnProvider.apply(item);
    }

    private String resolveItemId(T item) {
        if (itemIdentifierProvider != null) {
            final String provided = itemIdentifierProvider.apply(item);
            if (provided == null || provided.isBlank()) {
                throw new IllegalStateException("Item identifier provider returned a null/blank id");
            }
            return provided;
        }
        return generatedItemIds.computeIfAbsent(item,
                key -> "kanban-item-" + Integer.toUnsignedString(System.identityHashCode(key)));
    }

    private void appendMoveAudit(String itemId, C fromColumn, C toColumn, KanbanMoveResult result, String reason) {
        if (toColumn == null || result == null) {
            return;
        }
        moveAuditTrail.add(new KanbanMoveAuditEntry<>(itemId, fromColumn, toColumn, result, Instant.now(), reason));
    }

    public static class DefaultKanbanBoardBuilder<T, C> implements KanbanBoardBuilder<T, C> {

        private final DefaultKanbanBoard<T, C> instance = new DefaultKanbanBoard<>();

        @Override
        public KanbanBoardBuilder<T, C> withColumn(KanbanColumn<C> column) {
            ObjectUtils.argumentNotNull(column, "Kanban column must be not null");
            final List<KanbanColumn<C>> merged = new ArrayList<>(instance.columns);
            merged.add(column);
            instance.setColumns(merged);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withColumns(Collection<KanbanColumn<C>> columns) {
            instance.setColumns(columns);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withCardRenderer(KanbanCardRenderer<T> cardRenderer) {
            instance.setCardRenderer(cardRenderer);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withItemIdentifierProvider(Function<T, String> itemIdentifierProvider) {
            instance.setItemIdentifierProvider(itemIdentifierProvider);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withColumnIdSerializer(Function<C, String> columnIdSerializer) {
            instance.setColumnIdSerializer(columnIdSerializer);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withItemColumnProvider(Function<T, C> itemColumnProvider) {
            instance.setItemColumnProvider(itemColumnProvider);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withItemColumnUpdater(BiConsumer<T, C> itemColumnUpdater) {
            instance.setItemColumnUpdater(itemColumnUpdater);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withMoveHandler(KanbanMoveHandler<T, C> moveHandler) {
            instance.setMoveHandler(moveHandler);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withColumnCountProvider(KanbanCountProvider<C> countProvider) {
            instance.setColumnCountProvider(countProvider);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withColumnPageSize(int pageSize) {
            instance.setColumnPageSize(pageSize);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withCardActionHandler(KanbanCardActionHandler<T> cardActionHandler) {
            instance.setCardActionHandler(cardActionHandler);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withColumnActionHandler(KanbanColumnActionHandler<C> columnActionHandler) {
            instance.setColumnActionHandler(columnActionHandler);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withCommentProvider(KanbanCommentProvider<T> commentProvider) {
            instance.setCommentProvider(commentProvider);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withCommentHandler(KanbanCommentHandler<T> commentHandler) {
            instance.setCommentHandler(commentHandler);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withI18n(KanbanI18n i18n) {
            instance.setI18n(i18n);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withItems(Collection<T> items) {
            instance.setItems(items);
            return this;
        }

        @Override
        public KanbanBoardBuilder<T, C> withDataProvider(KanbanDataProvider<T, C> dataProvider) {
            instance.setItems(dataProvider);
            return this;
        }

        @Override
        public KanbanBoard<T, C> build() {
            return instance;
        }
    }
}







