package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.FilterInputForm;
import com.holonplatform.vaadin.flow.components.KanbanBoard;
import com.holonplatform.vaadin.flow.components.kanban.KanbanColumn;
import com.holonplatform.vaadin.flow.components.kanban.KanbanComment;
import com.holonplatform.vaadin.flow.components.kanban.KanbanMoveAuditEntry;
import com.holonplatform.vaadin.flow.components.kanban.KanbanMoveResult;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link KanbanBoard}.
 */
public class TestKanbanBoard {

    private static final Property<String> TITLE = PathProperty.create("title", String.class);

    @Test
    public void testBoardRendersColumnsAndCardsFromCollection() {
        List<TaskItem> items = new ArrayList<>();
        items.add(new TaskItem("1", "Design login", "TODO"));
        items.add(new TaskItem("2", "Implement API", "IN_PROGRESS"));
        items.add(new TaskItem("3", "Write tests", "TODO"));

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("IN_PROGRESS", "In Progress"))
                .withItemColumnProvider(TaskItem::status)
                .withItems(items)
                .build();

        assertEquals(2, countComponentsByClass(board.getComponent(), "kanban-board-column"));
        assertEquals(3, countComponentsByClass(board.getComponent(), "kanban-board-card"));
    }

    @Test
    public void testMoveItemWithUpdater() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("DONE", "Done"))
                .withItemColumnProvider(TaskItem::status)
                .withItemColumnUpdater(TaskItem::setStatus)
                .withItems(List.of(item))
                .build();

        assertEquals("TODO", board.getColumnOf(item).orElse(null));
        assertTrue(board.moveItem(item, "DONE"));
        assertEquals("DONE", item.status());
        assertEquals("DONE", board.getColumnOf(item).orElse(null));
    }

    @Test
    public void testMoveRejectedByHandler() {
        TaskItem item = new TaskItem("1", "Implement API", "TODO");

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("DONE", "Done"))
                .withItemColumnProvider(TaskItem::status)
                .withMoveHandler(request -> KanbanMoveResult.REJECTED)
                .withItems(List.of(item))
                .build();

        assertFalse(board.moveItem(item, "DONE"));
        assertEquals("TODO", board.getColumnOf(item).orElse(null));
    }

    @Test
    public void testFilterBindingTriggersRefresh() {
        List<TaskItem> items = new ArrayList<>();
        items.add(new TaskItem("1", "Design login", "TODO"));
        items.add(new TaskItem("2", "Implement API", "IN_PROGRESS"));

        FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
                .withFilter(TITLE)
                .build();

        AtomicInteger fetchCalls = new AtomicInteger();

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("IN_PROGRESS", "In Progress"))
                .build();

        board.bindFilters(filters, (query, filter) -> {
            fetchCalls.incrementAndGet();
            String expectedTitle = filters.getFilterInput(TITLE)
                    .flatMap(input -> input.getInput().getValueIfPresent())
                    .orElse(null);
            return items.stream()
                    .filter(item -> Objects.equals(item.status(), query.columnId()))
                    .filter(item -> expectedTitle == null || item.title().contains(expectedTitle));
        });

        int initialCalls = fetchCalls.get();
        assertTrue(initialCalls >= 2, "Each column should trigger an initial fetch");

        filters.getFilterInput(TITLE)
                .orElseThrow(() -> new AssertionError("Title filter missing"))
                .getInput()
                .setValue("Design");

        assertTrue(fetchCalls.get() > initialCalls, "Filter change should refresh and re-fetch items");
        assertEquals(1, countComponentsByClass(board.getComponent(), "kanban-board-card"));
    }

    @Test
    public void testComponentsFactoryBuilder() {
        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withItems(List.of(new TaskItem("1", "Design login", "TODO")))
                .build();

        assertNotNull(board);
        assertEquals(1, countComponentsByClass(board.getComponent(), "kanban-board-card"));
    }

    @Test
    public void testAuditTrailOnMove() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("DONE", "Done"))
                .withItemColumnProvider(TaskItem::status)
                .withItemColumnUpdater(TaskItem::setStatus)
                .withItems(List.of(item))
                .build();

        assertTrue(board.moveItem(item, "DONE"));

        List<KanbanMoveAuditEntry<String>> auditEntries = new ArrayList<>(board.getMoveAuditTrail());
        assertEquals(1, auditEntries.size());
        assertEquals("TODO", auditEntries.get(0).fromColumn());
        assertEquals("DONE", auditEntries.get(0).toColumn());
        assertEquals(KanbanMoveResult.ACCEPTED, auditEntries.get(0).result());

        board.clearMoveAuditTrail();
        assertTrue(board.getMoveAuditTrail().isEmpty());
    }

    @Test
    public void testCommentsAreAttachedToCard() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");
        AtomicInteger commentPersistCalls = new AtomicInteger();

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withCommentHandler((target, comment) -> commentPersistCalls.incrementAndGet())
                .withItems(List.of(item))
                .build();

        assertTrue(board.addComment(item, KanbanComment.of("alice", "Please verify UX copy")));
        assertEquals(1, board.getComments(item).size());
        assertEquals(1, commentPersistCalls.get());
        assertEquals(1, countComponentsByClass(board.getComponent(), "kanban-board-card-comment"));
    }

    @Test
    public void testColumnPageSizeAndCountProvider() {
        List<TaskItem> items = List.of(
                new TaskItem("1", "A", "TODO"),
                new TaskItem("2", "B", "TODO"),
                new TaskItem("3", "C", "TODO")
        );

        AtomicInteger requestedLimit = new AtomicInteger();

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withColumnPageSize(2)
                .withColumnCountProvider((columnId, filter) -> 3)
                .withDataProvider((query, filter) -> {
                    requestedLimit.set(query.limit());
                    return items.stream().filter(i -> Objects.equals(i.status(), query.columnId())).limit(query.limit());
                })
                .build();

        assertEquals(2, board.getColumnPageSize());
        assertEquals(2, requestedLimit.get());

        SpanLookup lookup = findFirstSpanByClass(board.getComponent(), "kanban-board-column-count");
        assertNotNull(lookup);
        assertEquals("3", lookup.text());
    }

    @Test
    public void testCardActionHandlerButtonsAndCallbacks() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");
        AtomicReference<String> action = new AtomicReference<>();

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withCardActionHandler(new com.holonplatform.vaadin.flow.components.kanban.KanbanCardActionHandler<>() {
                    @Override
                    public void onOpen(TaskItem target) {
                        action.set("open");
                    }

                    @Override
                    public void onEdit(TaskItem target) {
                        action.set("edit");
                    }

                    @Override
                    public void onDelete(TaskItem target) {
                        action.set("delete");
                    }
                })
                .withItems(List.of(item))
                .build();

        Button openButton = findFirstButtonByClass(board.getComponent(), "kanban-board-card-action-open");
        Button editButton = findFirstButtonByClass(board.getComponent(), "kanban-board-card-action-edit");
        Button deleteButton = findFirstButtonByClass(board.getComponent(), "kanban-board-card-action-delete");
        assertNotNull(openButton);
        assertNotNull(editButton);
        assertNotNull(deleteButton);
        openButton.click();
        assertEquals("open", action.get());
    }

    @Test
    public void testRejectedMovesAreAuditedWhenSourceIsUnknown() {
        TaskItem item = new TaskItem("1", "Design login", null);

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("DONE", "Done"))
                .withItemColumnProvider(TaskItem::status)
                .withItems(List.of(item))
                .build();

        assertFalse(board.moveItem(item, "TODO"));

        List<KanbanMoveAuditEntry<String>> auditEntries = new ArrayList<>(board.getMoveAuditTrail());
        assertEquals(1, auditEntries.size());
        assertNull(auditEntries.get(0).fromColumn());
        assertEquals("TODO", auditEntries.get(0).toColumn());
        assertEquals(KanbanMoveResult.REJECTED, auditEntries.get(0).result());
        assertEquals("source-column-not-found", auditEntries.get(0).reason());
    }

    @Test
    public void testCommentProviderAndLocalCommentsAreOrderedByTimestamp() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");
        KanbanComment providerComment = new KanbanComment("alice", "From provider", Instant.parse("2026-04-05T10:00:00Z"));
        KanbanComment localComment = new KanbanComment("bob", "From local", Instant.parse("2026-04-05T11:00:00Z"));

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withCommentProvider(target -> List.of(providerComment))
                .withItems(List.of(item))
                .build();

        board.addComment(item, localComment);

        List<KanbanComment> comments = new ArrayList<>(board.getComments(item));
        assertEquals(2, comments.size());
        assertEquals("From provider", comments.get(0).message());
        assertEquals("From local", comments.get(1).message());
    }

    @Test
    public void testUnknownTargetColumnMoveIsAuditedWithSource() {
        TaskItem item = new TaskItem("1", "Design login", "TODO");

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withItems(List.of(item))
                .build();

        assertFalse(board.moveItem(item, "DONE"));

        List<KanbanMoveAuditEntry<String>> auditEntries = new ArrayList<>(board.getMoveAuditTrail());
        assertEquals(1, auditEntries.size());
        assertEquals("TODO", auditEntries.get(0).fromColumn());
        assertEquals("DONE", auditEntries.get(0).toColumn());
        assertEquals(KanbanMoveResult.REJECTED, auditEntries.get(0).result());
        assertEquals("unknown-target-column", auditEntries.get(0).reason());
    }

    @Test
    public void testDuplicateItemIdentifierFailsFast() {
        TaskItem item1 = new TaskItem("1", "Design login", "TODO");
        TaskItem item2 = new TaskItem("2", "Implement API", "TODO");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withItemIdentifierProvider(item -> "same-id")
                .withItems(List.of(item1, item2))
                .build());

        assertTrue(exception.getMessage().contains("Duplicate kanban item id"));
    }

    @Test
    public void testDuplicateSerializedColumnIdentifierFailsFast() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withColumn(KanbanColumn.of("todo", "todo-lowercase"))
                .withColumnIdSerializer(String::toUpperCase)
                .withItemColumnProvider(TaskItem::status)
                .withItems(List.of(new TaskItem("1", "Design login", "TODO")))
                .build());

        assertTrue(exception.getMessage().contains("Duplicate serialized column id"));
    }

    @Test
    public void testColumnActionHandlerButtonsAndCallbacks() {
        AtomicReference<String> action = new AtomicReference<>();

        KanbanBoard<TaskItem, String> board = KanbanBoard.<TaskItem, String>builder()
                .withColumn(KanbanColumn.of("TODO", "To Do"))
                .withItemColumnProvider(TaskItem::status)
                .withColumnActionHandler(new com.holonplatform.vaadin.flow.components.kanban.KanbanColumnActionHandler<>() {
                    @Override
                    public void onOptions(String columnId) {
                        action.set("options:" + columnId);
                    }

                    @Override
                    public void onAddCard(String columnId) {
                        action.set("content:" + columnId);
                    }
                })
                .withItems(List.of(new TaskItem("1", "Design login", "TODO")))
                .build();

        Button optionsButton = findFirstButtonByClass(board.getComponent(), "kanban-board-column-options");
        assertNotNull(optionsButton);
        optionsButton.click();
        assertEquals("options:TODO", action.get());

        Button addCardButton = findFirstButtonByClass(board.getComponent(), "kanban-board-column-content-card");
        assertNotNull(addCardButton);
        addCardButton.click();
        assertEquals("content:TODO", action.get());
    }

    private int countComponentsByClass(Component root, String className) {
        int count = root.getClassNames().contains(className) ? 1 : 0;
        return count + root.getChildren()
                .mapToInt(child -> countComponentsByClass(child, className))
                .sum();
    }

    @SuppressWarnings("unused") // test helper, kept for future assertions
    private int countComponentsByType(Component root, Class<? extends Component> type) {
        int count = type.isInstance(root) ? 1 : 0;
        return count + root.getChildren()
                .mapToInt(child -> countComponentsByType(child, type))
                .sum();
    }

    private Button findFirstButtonByClass(Component root, String className) {
        if (root instanceof Button button && root.getClassNames().contains(className)) {
            return button;
        }
        return root.getChildren()
                .map(child -> findFirstButtonByClass(child, className))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private SpanLookup findFirstSpanByClass(Component root, String className) {
        if (root instanceof com.vaadin.flow.component.html.Span span && root.getClassNames().contains(className)) {
            return new SpanLookup(span.getText());
        }
        return root.getChildren()
                .map(child -> findFirstSpanByClass(child, className))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private record SpanLookup(String text) {
    }

    private static final class TaskItem {

        private final String id;
        private final String title;
        private String status;

        private TaskItem(String id, String title, String status) {
            this.id = id;
            this.title = title;
            this.status = status;
        }

        @SuppressWarnings("unused")
        public String id() {
            return id;
        }

        public String title() {
            return title;
        }

        public String status() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}









