package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.KanbanBoard;
import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.kanban.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.AuditEntry;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.Severity;
import com.iyensoft.vaadin.flow.components.builders.CardBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link KanbanBoard} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Simple sprint board – drag cards between columns with a move notification</li>
 *   <li>Bug tracker board – card actions (Open/Edit/Delete) and column actions (Options/Add card)</li>
 *   <li>Publishing workflow – comment threads and live move audit trail</li>
 *   <li>Lazy-loaded backlog – KanbanDataProvider (offset/limit) + KanbanCountProvider</li>
 * </ol>
 */
@PageTitle("KanbanBoard – Holon Demo")
@Route(value = "kanban-board", layout = DemoMainLayout.class)
public class KanbanBoardDemoView extends Div {

    // ── Domain model – Example 1 ────────────────────────────────────────────

    public enum TaskStatus { TODO, IN_PROGRESS, REVIEW, DONE }

    public static final class Task {
        private final String id;
        private String title;
        private final String assignee;
        private TaskStatus status;

        public Task(String id, String title, String assignee, TaskStatus status) {
            this.id       = id;
            this.title    = title;
            this.assignee = assignee;
            this.status   = status;
        }

        public String     getId()       { return id; }
        public String     getTitle()    { return title; }
        public String     getAssignee() { return assignee; }
        public TaskStatus getStatus()   { return status; }
        public void setStatus(TaskStatus s) { this.status = s; }
    }

    // ── Domain model – Example 2 ────────────────────────────────────────────

    public enum IssueStatus { OPEN, IN_PROGRESS, TESTING, CLOSED }

    public enum Priority {
        HIGH("\uD83D\uDD34 High"),
        MEDIUM("\uD83D\uDFE1 Medium"),
        LOW("\uD83D\uDFE2 Low");

        private final String label;
        Priority(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum IssueType {
        BUG("\uD83D\uDC1B Bug"),
        FEATURE("\u2728 Feature"),
        CHORE("\uD83D\uDD27 Chore");

        private final String label;
        IssueType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public static final class Issue {
        private final String id;
        private String title;
        private final String reporter;
        private final Priority priority;
        private final IssueType type;
        private IssueStatus status;

        public Issue(String id, String title, String reporter,
                     Priority priority, IssueType type, IssueStatus status) {
            this.id       = id;
            this.title    = title;
            this.reporter = reporter;
            this.priority = priority;
            this.type     = type;
            this.status   = status;
        }

        public String      getId()       { return id; }
        public String      getTitle()    { return title; }
        public String      getReporter() { return reporter; }
        public Priority    getPriority() { return priority; }
        public IssueType   getType()     { return type; }
        public IssueStatus getStatus()   { return status; }
        public void setTitle(String t)       { this.title  = t; }
        public void setStatus(IssueStatus s) { this.status = s; }
    }

    // ── Domain model – Example 3 ────────────────────────────────────────────

    public enum ArticleStatus { DRAFT, IN_REVIEW, APPROVED, PUBLISHED }

    public static final class Article {
        private final String id;
        private final String title;
        private final String author;
        private final int wordCount;
        private ArticleStatus status;

        public Article(String id, String title, String author, int wordCount, ArticleStatus status) {
            this.id        = id;
            this.title     = title;
            this.author    = author;
            this.wordCount = wordCount;
            this.status    = status;
        }

        public String        getId()       { return id; }
        public String        getTitle()    { return title; }
        public String        getAuthor()   { return author; }
        public int           getWordCount(){ return wordCount; }
        public ArticleStatus getStatus()  { return status; }
        public void setStatus(ArticleStatus s) { this.status = s; }
    }

    // ── Domain model – Example 4 ────────────────────────────────────────────

    public enum BacklogStatus { BACKLOG, SPRINT, IN_PROGRESS, DONE }

    public static final class Ticket {
        private final String id;
        private final String summary;
        private final String team;
        private final int storyPoints;
        private BacklogStatus status;

        public Ticket(String id, String summary, String team, int storyPoints, BacklogStatus status) {
            this.id          = id;
            this.summary     = summary;
            this.team        = team;
            this.storyPoints = storyPoints;
            this.status      = status;
        }

        public String        getId()          { return id; }
        public String        getSummary()     { return summary; }
        public String        getTeam()        { return team; }
        public int           getStoryPoints() { return storyPoints; }
        public BacklogStatus getStatus()      { return status; }
        public void setStatus(BacklogStatus s) { this.status = s; }
    }

    // ── Sample data ─────────────────────────────────────────────────────────

    private static final List<Task> TASKS = new ArrayList<>(List.of(
        new Task("T-1", "Set up CI pipeline",      "Alice", TaskStatus.TODO),
        new Task("T-2", "Design data model",        "Bob",   TaskStatus.TODO),
        new Task("T-3", "Implement login screen",   "Carol", TaskStatus.IN_PROGRESS),
        new Task("T-4", "Write unit tests",         "Dave",  TaskStatus.IN_PROGRESS),
        new Task("T-5", "API integration",          "Alice", TaskStatus.REVIEW),
        new Task("T-6", "Performance optimisation", "Bob",   TaskStatus.REVIEW),
        new Task("T-7", "Deploy to staging",        "Carol", TaskStatus.DONE),
        new Task("T-8", "Release notes",            "Dave",  TaskStatus.DONE)
    ));

    // ── Constructor ─────────────────────────────────────────────────────────

    public KanbanBoardDemoView() {
        addClassName("app-view");

        var title = new H1("KanbanBoard");

        var desc = new Paragraph(
                "Generic drag-and-drop Kanban board. Cards are draggable between columns; " +
                "a KanbanMoveHandler validates and persists moves. The board supports " +
                "card action buttons (Open / Edit / Delete), column action handlers " +
                "(Options / Add card), comment threads, move audit trail, " +
                "lazy column data providers, semantic status badges (KanbanStatusBadge), " +
                "i18n button labels (KanbanI18n), column count providers, " +
                "move validation (beforeMove hook), and programmatic moveItem / getColumnOf APIs.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(simpleSprintBoardExample());
        examples.add(bugTrackerBoardExample());
        examples.add(publishingWorkflowExample());
        examples.add(lazyLoadingExample());
        examples.add(inMemoryFilterExample());
        examples.add(datastoreFilterExample());
        examples.add(statusBadgeExample());
        examples.add(i18nExample());
        examples.add(moveValidationAndCountExample());
        examples.add(programmaticMoveExample());

        add(title, desc, examples);
    }

    // ── Examples ─────────────────────────────────────────────────────────────

    /**
     * Example 1 – minimal sprint board.
     */
    private DemoExample simpleSprintBoardExample() {

        var board = KanbanBoard.<Task, TaskStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(TaskStatus.TODO,        "To Do",       KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(TaskStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(TaskStatus.REVIEW,      "Review",      KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(TaskStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Task::getId)
                .withItemColumnProvider(Task::getStatus)
                .withItemColumnUpdater(Task::setStatus)
                .withCardRenderer(task -> {
                    var assignee = LabelBuilder.span()
                            .text("\uD83D\uDC64 " + task.getAssignee())
                            .styleName("demo-kanban-card__assignee")
                            .build();
                    var idBadge = LabelBuilder.span()
                            .text(task.getId())
                            .styleName("demo-kanban-card__id")
                            .build();
                    var title = LabelBuilder.span()
                            .text(task.getTitle())
                            .styleName("demo-kanban-card__title")
                            .build();
                    var footer = DivBuilder.create()
                            .styleName("demo-kanban-card__footer")
                            .add(idBadge)
                            .build();
                    return DivBuilder.create()
                            .styleName("demo-kanban-card")
                            .add(title, assignee, footer)
                            .build();
                })
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());

                    var from  = request.fromColumn().name();
                    var to    = request.toColumn().name();
                    var taskTitle = request.item().getTitle();

                    var n = Notification.show(
                            "\u2713 \"" + taskTitle + "\" moved: " + from + " \u2192 " + to,
                            3000, Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return KanbanMoveResult.ACCEPTED;
                })
                .withItems(TASKS)
                .build();

        return new DemoExample("Simple Sprint Board (drag & drop with notification)",
                board.getComponent(), """
                var board = KanbanBoard.<Task, TaskStatus>builder()
                    .withColumns(List.of(
                        KanbanColumn.of(TaskStatus.TODO,        "To Do"),
                        KanbanColumn.of(TaskStatus.IN_PROGRESS, "In Progress"),
                        KanbanColumn.of(TaskStatus.REVIEW,      "Review"),
                        KanbanColumn.of(TaskStatus.DONE,        "Done")
                    ))
                    .withItemIdentifierProvider(Task::getId)
                    .withItemColumnProvider(Task::getStatus)
                    .withItemColumnUpdater(Task::setStatus)
                    .withCardRenderer(task -> {
                        var title = new Span(task.getTitle());
                        var assignee = new Span(task.getAssignee());
                        var idBadge = new Span(task.getId());
                        var footer = new Div(idBadge);
                        var card = new Div(title, assignee, footer);
                        return card;
                    })
                    .withMoveHandler(req -> {
                        req.item().setStatus(req.toColumn());
                        Notification.show(req.item().getTitle() + " moved to " + req.toColumn());
                        return KanbanMoveResult.ACCEPTED;
                    })
                    .withItems(tasks)
                    .build();
                content(board.getComponent());
                """);
    }

    /**
     * Example 2 – bug tracker board.
     * Demonstrates card actions (Open / Edit / Delete) and column actions
     * (Options button / Add card button with a creation Dialog).
     */
    private DemoExample bugTrackerBoardExample() {

        var issues = new ArrayList<Issue>(List.of(
            new Issue("BUG-1",  "Login crashes on Safari",   "Alice", Priority.HIGH,   IssueType.BUG,     IssueStatus.OPEN),
            new Issue("BUG-2",  "CSV export missing headers","Bob",   Priority.MEDIUM, IssueType.BUG,     IssueStatus.OPEN),
            new Issue("FEAT-1", "Dark mode support",         "Carol", Priority.LOW,    IssueType.FEATURE,  IssueStatus.IN_PROGRESS),
            new Issue("FEAT-2", "Email notifications",       "Dave",  Priority.MEDIUM, IssueType.FEATURE,  IssueStatus.IN_PROGRESS),
            new Issue("CHORE-1","Upgrade Spring Boot",       "Alice", Priority.HIGH,   IssueType.CHORE,    IssueStatus.TESTING),
            new Issue("BUG-3",  "Memory leak in scheduler",  "Bob",   Priority.HIGH,   IssueType.BUG,     IssueStatus.TESTING),
            new Issue("FEAT-3", "Multi-tenant support",      "Carol", Priority.HIGH,   IssueType.FEATURE,  IssueStatus.CLOSED),
            new Issue("CHORE-2","Update API docs",           "Dave",  Priority.LOW,    IssueType.CHORE,    IssueStatus.CLOSED)
        ));

        // Holder lets handlers call board.refresh() without a circular capture
        @SuppressWarnings("unchecked")
        KanbanBoard<Issue, IssueStatus>[] ref = new KanbanBoard[1];

        var board = KanbanBoard.<Issue, IssueStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(IssueStatus.OPEN,        "Open",        KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(IssueStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(IssueStatus.TESTING,     "Testing",     KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(IssueStatus.CLOSED,      "Closed",      KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Issue::getId)
                .withItemColumnProvider(Issue::getStatus)
                .withItemColumnUpdater(Issue::setStatus)
                .withCardRenderer(issue -> {
                    // ── KanbanStatusBadge for type ──────────────────────────────
                    var typeBadge = switch (issue.getType()) {
                        case BUG     -> KanbanStatusBadge.error(issue.getType().getLabel());
                        case FEATURE -> KanbanStatusBadge.info(issue.getType().getLabel());
                        case CHORE   -> KanbanStatusBadge.defaultVariant(issue.getType().getLabel());
                    };
                    // ── KanbanStatusBadge for priority ──────────────────────────
                    var priorityBadge = switch (issue.getPriority()) {
                        case HIGH   -> KanbanStatusBadge.error(issue.getPriority().getLabel());
                        case MEDIUM -> KanbanStatusBadge.warning(issue.getPriority().getLabel());
                        case LOW    -> KanbanStatusBadge.success(issue.getPriority().getLabel());
                    };
                    var idBadge = LabelBuilder.span()
                            .text(issue.getId())
                            .styleName("demo-kanban-card__id")
                            .build();
                    var reporter = LabelBuilder.span()
                            .text("\uD83D\uDC64 " + issue.getReporter())
                            .styleName("demo-kanban-card__assignee")
                            .build();
                    var header = DivBuilder.create()
                            .styleName("demo-kanban-card__header")
                            .add(typeBadge, priorityBadge)
                            .build();
                    var title = LabelBuilder.span()
                            .text(issue.getTitle())
                            .styleName("demo-kanban-card__title")
                            .build();
                    var footer = DivBuilder.create()
                            .styleName("demo-kanban-card__footer")
                            .add(idBadge, reporter)
                            .build();
                    return DivBuilder.create()
                            .styleName("demo-kanban-card")
                            .add(header, title, footer)
                            .build();
                })
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());
                    var n = Notification.show(
                            "\"" + request.item().getTitle() + "\" \u2192 " + request.toColumn().name(),
                            2500, Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return KanbanMoveResult.ACCEPTED;
                })
                .withCardActionHandler(new KanbanCardActionHandler<Issue>() {

                    @Override
                    public void onOpen(Issue issue) {
                        var dialog = new Dialog();
                        dialog.setHeaderTitle(issue.getId() + " \u2013 " + issue.getTitle());
                        dialog.setWidth("420px");

                        var content = new Div();
                        content.add(
                            detailRow("ID",       issue.getId()),
                            detailRow("Reporter", issue.getReporter()),
                            detailRow("Type",     issue.getType().getLabel()),
                            detailRow("Priority", issue.getPriority().getLabel()),
                            detailRow("Status",   issue.getStatus().name())
                        );

                        dialog.add(content);
                        dialog.getFooter().add(new Button("Close", e -> dialog.close()));
                        dialog.open();
                    }

                    @Override
                    public void onEdit(Issue issue) {
                        var dialog = new Dialog();
                        dialog.setHeaderTitle("Edit \u2013 " + issue.getId());
                        dialog.setWidth("380px");

                        var titleField = new TextField("Title");
                        titleField.setValue(issue.getTitle());
                        titleField.setWidthFull();

                        dialog.add(titleField);
                        dialog.getFooter().add(
                            new Button("Save", e -> {
                                if (!titleField.getValue().isBlank()) {
                                    issue.setTitle(titleField.getValue());
                                    dialog.close();
                                    if (ref[0] != null) ref[0].refresh();
                                }
                            }),
                            new Button("Cancel", e -> dialog.close())
                        );
                        dialog.open();
                    }

                    @Override
                    public void onDelete(Issue issue) {
                        issues.remove(issue);
                        if (ref[0] != null) ref[0].refresh();
                        var n = Notification.show(
                                "\"" + issue.getTitle() + "\" deleted",
                                2500, Notification.Position.BOTTOM_START);
                        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                })
                .withColumnActionHandler(new KanbanColumnActionHandler<IssueStatus>() {

                    @Override
                    public void onOptions(IssueStatus columnId) {
                        Notification.show("Column options: " + columnId.name(),
                                2000, Notification.Position.TOP_CENTER);
                    }

                    @Override
                    public void onAddCard(IssueStatus columnId) {
                        var dialog = new Dialog();
                        dialog.setHeaderTitle("Add issue to " + columnId.name());
                        dialog.setWidth("380px");

                        var titleField = new TextField("Title");
                        titleField.setPlaceholder("Issue title");
                        titleField.setWidthFull();

                        var prioritySelect = new Select<Priority>();
                        prioritySelect.setLabel("Priority");
                        prioritySelect.setItems(Priority.values());
                        prioritySelect.setItemLabelGenerator(Priority::getLabel);
                        prioritySelect.setValue(Priority.MEDIUM);
                        prioritySelect.setWidthFull();

                        var typeSelect = new Select<IssueType>();
                        typeSelect.setLabel("Type");
                        typeSelect.setItems(IssueType.values());
                        typeSelect.setItemLabelGenerator(IssueType::getLabel);
                        typeSelect.setValue(IssueType.BUG);
                        typeSelect.setWidthFull();

                        var form = new Div(titleField, prioritySelect, typeSelect);
                        dialog.add(form);

                        dialog.getFooter().add(
                            new Button("Add", e -> {
                                if (!titleField.getValue().isBlank()) {
                                    var newId = "ISS-" + (issues.size() + 1);
                                    issues.add(new Issue(newId, titleField.getValue(), "Me",
                                            prioritySelect.getValue(),
                                            typeSelect.getValue(),
                                            columnId));
                                    dialog.close();
                                    if (ref[0] != null) ref[0].refresh();
                                }
                            }),
                            new Button("Cancel", e -> dialog.close())
                        );
                        dialog.open();
                    }
                })
                .withItems(issues)
                .build();

        ref[0] = board;

        return new DemoExample("Bug Tracker Board (card actions + column actions)",
                board.getComponent(), """
                // ── Card action handler ─────────────────────────────────────────────
                // Adds Open / Edit / Delete buttons to every card.
                .withCardActionHandler(new KanbanCardActionHandler<Issue>() {

                    @Override public void onOpen(Issue issue) {
                        var dialog = new Dialog();
                        dialog.setHeaderTitle(issue.getId() + " – " + issue.getTitle());
                        // … content detail rows …
                        dialog.open();
                    }

                    @Override public void onEdit(Issue issue) {
                        var titleField = new TextField("Title", issue.getTitle());
                        var dialog = new Dialog();
                        dialog.content(titleField);
                        dialog.getFooter().content(new Button("Save", e -> {
                            issue.setTitle(titleField.getValue());
                            dialog.close();
                            board.refresh();         // re-render updated title
                        }));
                        dialog.open();
                    }

                    @Override public void onDelete(Issue issue) {
                        issues.remove(issue);        // remove from data source
                        board.refresh();             // re-render board
                    }
                })

                // ── Column action handler ────────────────────────────────────────────
                // Wires the column (…) options button AND the "+ Add card" footer button.
                .withColumnActionHandler(new KanbanColumnActionHandler<IssueStatus>() {

                    @Override public void onOptions(IssueStatus col) {
                        Notification.show("Options for: " + col.name());
                    }

                    @Override public void onAddCard(IssueStatus col) {
                        var titleField = new TextField("Title");
                        var dialog = new Dialog();
                        dialog.content(titleField /*, prioritySelect, typeSelect */);
                        dialog.getFooter().content(new Button("Add", e -> {
                            issues.content(new Issue(newId, titleField.getValue(), "Me",
                                    priority, type, col));
                            dialog.close();
                            board.refresh();
                        }));
                        dialog.open();
                    }
                })
                """);
    }

    /**
     * Example 3 – publishing workflow.
     * Demonstrates comment provider, comment handler, programmatic addComment(),
     * getComments(), and a live TimelineStepper audit log updated via afterMove hook.
     */
    private DemoExample publishingWorkflowExample() {

        // Pre-seeded comments (simulating data loaded from a backend)
        Map<String, List<KanbanComment>> seeds = new HashMap<>();
        seeds.put("ART-1", new ArrayList<>(List.of(
            new KanbanComment("Editor",  "Intro section needs more context.",     Instant.now().minus(3, ChronoUnit.HOURS)),
            new KanbanComment("Alice",   "Revising today, will resubmit.",        Instant.now().minus(1, ChronoUnit.HOURS))
        )));
        seeds.put("ART-3", new ArrayList<>(List.of(
            new KanbanComment("Reviewer","LGTM – minor grammar fixes only.",      Instant.now().minus(30, ChronoUnit.MINUTES))
        )));
        seeds.put("ART-5", new ArrayList<>(List.of(
            new KanbanComment("Bob",     "SEO keywords need updating.",           Instant.now().minus(2, ChronoUnit.HOURS))
        )));

        var articles = new ArrayList<Article>(List.of(
            new Article("ART-1", "Getting Started with Vaadin Flow", "Alice",  1240, ArticleStatus.DRAFT),
            new Article("ART-2", "Spring Boot Best Practices",        "Bob",   2100, ArticleStatus.DRAFT),
            new Article("ART-3", "Responsive Design Patterns",        "Carol", 980,  ArticleStatus.IN_REVIEW),
            new Article("ART-4", "JPA Performance Tips",              "Dave",  1560, ArticleStatus.IN_REVIEW),
            new Article("ART-5", "Security in Vaadin Apps",           "Alice", 1870, ArticleStatus.APPROVED),
            new Article("ART-6", "Testing with JUnit 5",              "Bob",   1430, ArticleStatus.APPROVED),
            new Article("ART-7", "CI/CD for Java Projects",           "Carol", 2050, ArticleStatus.PUBLISHED),
            new Article("ART-8", "Microservices with Spring",         "Dave",  2300, ArticleStatus.PUBLISHED)
        ));

        @SuppressWarnings("unchecked")
        KanbanBoard<Article, ArticleStatus>[] ref = new KanbanBoard[1];

        int[] moveCount = {0};

        Span[] auditLabel = {new Span("No moves yet")};
        auditLabel[0].addClassName("demo-kanban-audit__count");

        // Live TimelineStepper — each move prepends a new entry at the top
        var timeline = new TimelineStepper();
        timeline.setWidth("100%");
        timeline.setHasMore(false);

        var board = KanbanBoard.<Article, ArticleStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(ArticleStatus.DRAFT,     "Draft",     KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(ArticleStatus.IN_REVIEW, "In Review", KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(ArticleStatus.APPROVED,  "Approved",  KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(ArticleStatus.PUBLISHED, "Published", KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Article::getId)
                .withItemColumnProvider(Article::getStatus)
                .withItemColumnUpdater(Article::setStatus)
                .withCardRenderer(article -> {
                    var authorBadge = new Span("\uD83D\uDC64 " + article.getAuthor());

                    var wordsBadge = new Span(article.getWordCount() + " words");

                    return CardBuilder.create()
                            .title(LabelBuilder.span().text(article.getTitle())
                                    .styleName("demo-kanban-card__title"))
                            .subtitle(authorBadge)
                            .withFooter(wordsBadge)
                            .styleName("demo-kanban-card")
                            .build();
                })
                .withMoveHandler(new KanbanMoveHandler<Article, ArticleStatus>() {

                    private static final DateTimeFormatter FMT =
                            DateTimeFormatter.ofPattern("HH:mm:ss");

                    @Override
                    public KanbanMoveResult onMove(KanbanMoveRequest<Article, ArticleStatus> req) {
                        req.item().setStatus(req.toColumn());
                        var n = Notification.show(
                                "\"" + req.item().getTitle() + "\" \u2192 " + friendlyName(req.toColumn()),
                                2500, Notification.Position.BOTTOM_START);
                        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        return KanbanMoveResult.ACCEPTED;
                    }

                    @Override
                    public void afterMove(KanbanMoveRequest<Article, ArticleStatus> req,
                                         KanbanMoveResult result) {
                        moveCount[0]++;
                        int count = moveCount[0];
                        auditLabel[0].setText(count + (count == 1 ? " move recorded" : " moves recorded"));

                        boolean accepted = result == KanbanMoveResult.ACCEPTED;
                        var action = accepted
                                ? "\u2192 " + friendlyName(req.toColumn())
                                : "Rejected \u2192 " + friendlyName(req.toColumn());

                        timeline.prependEntries(List.of(
                                new AuditEntry(String.valueOf(count),
                                        LocalDateTime.now().format(FMT),
                                        req.item().getTitle(),
                                        action)
                                        .detail("from: " + friendlyName(req.fromColumn()))
                                        .severity(accepted ? Severity.SUCCESS : Severity.WARNING)
                                        .category(friendlyName(req.toColumn()))
                        ));
                    }
                })
                // Provide pre-seeded comments (simulates reading from a backend)
                .withCommentProvider(article -> seeds.getOrDefault(article.getId(), List.of()))
                // Handler called when board.addComment() is used (simulates persisting to backend)
                .withCommentHandler((article, comment) -> {
                    var n = Notification.show(
                            "Comment posted on \"" + article.getTitle() + "\"",
                            2000, Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                })
                .withCardActionHandler(new KanbanCardActionHandler<Article>() {

                    @Override
                    public void onOpen(Article article) {
                        var dialog = new Dialog();
                        dialog.setHeaderTitle(article.getId() + " \u2013 " + article.getTitle());
                        dialog.setWidth("480px");

                        var details = new Div();
                        details.add(
                            detailRow("Author", article.getAuthor()),
                            detailRow("Words",  article.getWordCount() + ""),
                            detailRow("Status", article.getStatus().name())
                        );

                        var commentsSection = new Div();

                        var commentsHeading = new Span("Comments");

                        var commentsList = new Div();

                        if (ref[0] != null) {
                            var existing = ref[0].getComments(article);
                            if (existing.isEmpty()) {
                                var empty = new Span("No comments yet.");
                                commentsList.add(empty);
                            } else {
                                existing.forEach(c -> commentsList.add(commentRow(c)));
                            }
                        }

                        var addField = new TextField();
                        addField.setPlaceholder("Write a comment\u2026");
                        addField.setWidthFull();

                        var postBtn = new Button("Post", e -> {
                            if (!addField.getValue().isBlank() && ref[0] != null) {
                                ref[0].addComment(article, KanbanComment.of("Me", addField.getValue()));
                                dialog.close();
                            }
                        });

                        var addRow = new Div(addField, postBtn);

                        commentsSection.add(commentsHeading, commentsList, addRow);
                        dialog.add(details, commentsSection);
                        dialog.getFooter().add(new Button("Close", e -> dialog.close()));
                        dialog.open();
                    }
                })
                .withItems(articles)
                .build();

        ref[0] = board;

        // ── Audit section ────────────────────────────────────────────────────

        var clearBtn = new Button("Clear history", e -> {
            if (ref[0] != null) ref[0].clearMoveAuditTrail();
            timeline.setItems(List.of());
            moveCount[0] = 0;
            auditLabel[0].setText("No moves yet");
        });

        var auditHeader = new Div(auditLabel[0], clearBtn);

        var auditSection = new Div(auditHeader, timeline);

        var container = new Div(board.getComponent(), auditSection);

        return new DemoExample("Publishing Workflow (comments + live move audit trail)",
                container, """
                // ── Live TimelineStepper audit log ────────────────────────────────────
                var timeline = new TimelineStepper();
                timeline.setHasMore(false);

                // ── afterMove hook – fires after every accepted or rejected move ───────
                .withMoveHandler(new KanbanMoveHandler<Article, ArticleStatus>() {

                    @Override
                    public KanbanMoveResult onMove(KanbanMoveRequest<Article, ArticleStatus> req) {
                        req.item().setStatus(req.toColumn());
                        return KanbanMoveResult.ACCEPTED;
                    }

                    @Override
                    public void afterMove(KanbanMoveRequest<Article, ArticleStatus> req,
                                         KanbanMoveResult result) {
                        // Prepend a new timeline entry — newest always appears at the top
                        timeline.prependEntries(List.of(
                            new AuditEntry(id, LocalDateTime.now().format(FMT),
                                    req.item().getTitle(),
                                    (result == ACCEPTED ? "→ " : "Rejected → ") + req.toColumn())
                                .detail("from: " + req.fromColumn())
                                .severity(result == ACCEPTED ? Severity.SUCCESS : Severity.WARNING)
                                .category(req.toColumn().name())
                        ));
                    }
                })

                // ── Comment provider + handler ────────────────────────────────────────
                .withCommentProvider(article -> commentRepo.findBy(article.getId()))
                .withCommentHandler((article, comment) -> commentRepo.save(comment))

                // ── Programmatically content a comment (from a card's Open dialog) ────────
                board.addComment(article, KanbanComment.of("Me", text));

                // ── Clear both the board's internal trail and the timeline display ─────
                board.clearMoveAuditTrail();
                timeline.setItems(List.of());
                """);
    }

    /**
     * Example 4 – lazy-loaded backlog (slice / no-count pattern).
     * <p>Uses {@code KanbanDataProvider} with offset+limit (like JPA {@code Slice<T>}).
     * No {@code KanbanCountProvider} is registered — the board simply renders the first
     * page per column with no total-count query.  100 tickets (25 per column), page
     * size 5 so the "first-page-only" behaviour is clearly visible.
     */
    private DemoExample lazyLoadingExample() {

        // ── Build 100 in-memory tickets (25 per column) ─────────────────────
        var allTickets = new ArrayList<Ticket>();
        String[] teams = {"Platform", "UI", "Backend", "QA"};
        int[]    pts   = {1, 2, 3, 5, 8};
        String[] names = {
            "Set up environment",    "Write unit tests",     "Implement feature",
            "Fix regression",        "Code review",          "Update documentation",
            "Deploy to staging",     "Performance testing",  "Security audit",
            "Refactor module",       "Add logging",          "Update dependencies",
            "Integration tests",     "Database migration",   "API endpoint",
            "UI component",          "Service layer",        "Repository layer",
            "Config update",         "Error handling",       "Monitoring setup",
            "Alert configuration",   "Load testing",         "Cache tuning",
            "Release prep"
        };
        int seq = 0;
        for (BacklogStatus status : BacklogStatus.values()) {
            for (int i = 0; i < 25; i++) {
                seq++;
                allTickets.add(new Ticket(
                        "TKT-" + seq,
                        names[i],
                        teams[i % teams.length],
                        pts[i % pts.length],
                        status));
            }
        }

        var board = KanbanBoard.<Ticket, BacklogStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(BacklogStatus.BACKLOG,     "Backlog",     KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(BacklogStatus.SPRINT,      "Sprint",      KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(BacklogStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(BacklogStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Ticket::getId)
                .withItemColumnProvider(Ticket::getStatus)
                .withItemColumnUpdater(Ticket::setStatus)
                .withCardRenderer(ticket -> {
                    var idBadge = LabelBuilder.span()
                            .text(ticket.getId())
                            .styleName("demo-kanban-card__id")
                            .build();
                    var teamBadge = LabelBuilder.span()
                            .text("\uD83D\uDCE6 " + ticket.getTeam())
                            .styleName("demo-kanban-card__assignee")
                            .build();
                    var ptsBadge = LabelBuilder.span()
                            .text(ticket.getStoryPoints() + " pts")
                            .styleName("demo-kanban-card__id")
                            .build();

                    return CardBuilder.create()
                            .title(LabelBuilder.span().text(ticket.getSummary())
                                    .styleName("demo-kanban-card__title"))
                            .withFooter(idBadge, teamBadge, ptsBadge)
                            .styleName("demo-kanban-card")
                            .build();
                })
                // ── Slice data provider: no COUNT query, just offset + limit ─────
                // Equivalent to JPA repository.findByStatus(col, PageRequest.of(0, pageSize)).
                // query.offset() = 0 on initial load; query.limit() = columnPageSize.
                .withDataProvider((query, filter) ->
                        allTickets.stream()
                                .filter(t -> t.getStatus() == query.columnId())
                                .skip(query.offset())
                                .limit(query.limit()))
                // ── No withColumnCountProvider → badge = rendered card count ──────
                // This mirrors JPA Slice: fetch a page, no COUNT(*).
                // The column badge simply reflects what was loaded.
                .withColumnPageSize(5)  // 25 exist per column; only first 5 are rendered
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());
                    var n = Notification.show(
                            "\"" + request.item().getSummary() + "\" \u2192 " + request.toColumn().name(),
                            2000, Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return KanbanMoveResult.ACCEPTED;  // triggers refresh() → re-fetches all columns
                })
                .build();

        var info = new Span(
                "100 tickets total \u2022 25 per column \u2022 page size = 5 \u2022 " +
                "slice pattern: no KanbanCountProvider, badge = rendered count only. " +
                "Drag a card \u2013 the board re-fetches offset 0..limit after every move.");

        var container = new Div(board.getComponent(), info);

        return new DemoExample(
                "Lazy-loaded Backlog \u2013 Slice pattern (offset + limit, no COUNT)",
                container, """
                // ── Slice pattern: withDataProvider without withColumnCountProvider ──────────
                // Exactly like JPA Slice<T> – fetch offset..limit, no COUNT(*) query at all.
                // KanbanQuery carries: columnId | offset (0-based) | limit (= page size)
                .withDataProvider((query, filter) ->
                    repo.findSlice(
                        query.columnId(),   // which column
                        query.offset(),     // pagination start  (always 0 on initial load)
                        query.limit(),      // max rows to return
                        filter))            // optional QueryFilter from DynamicFilterPanel

                // ── No withColumnCountProvider → column-header badge = rendered count ────────
                // Add withColumnCountProvider only when you need the true total per column
                // (costs an extra COUNT(*) per column on every refresh).
                // Omit it (slice) when "first N items" is enough – cheaper, simpler.

                // ── Page size ─────────────────────────────────────────────────────────────────
                .withColumnPageSize(5)   // default 100

                // ── Detecting "has more" (optional) ───────────────────────────────────────────
                // Fetch limit+1 rows in the provider; if size == limit+1 → more items exist.
                // Only pass the first limit rows back to the board.
                // board.refresh() re-fetches every column from offset=0 on every state change.
                """);
    }

    // ── Examples 5 & 6 – Filtering ───────────────────────────────────────────

    /**
     * Example 5 – in-memory filtering.
     * DynamicFilterPanel introspects the Task bean; toPredicate() is applied inside the
     * data-provider lambda so every refresh re-evaluates the live filter.
     */
    private DemoExample inMemoryFilterExample() {

        var allTasks = new ArrayList<Task>(List.of(
            new Task("T-01", "Set up CI pipeline",        "Alice", TaskStatus.TODO),
            new Task("T-02", "Design data model",          "Bob",   TaskStatus.TODO),
            new Task("T-03", "Write unit tests",           "Carol", TaskStatus.TODO),
            new Task("T-04", "Add logging",                "Dave",  TaskStatus.TODO),
            new Task("T-05", "Implement login screen",     "Alice", TaskStatus.IN_PROGRESS),
            new Task("T-06", "API integration",            "Bob",   TaskStatus.IN_PROGRESS),
            new Task("T-07", "Performance optimisation",   "Carol", TaskStatus.IN_PROGRESS),
            new Task("T-08", "Database migration",         "Dave",  TaskStatus.IN_PROGRESS),
            new Task("T-09", "Security audit",             "Alice", TaskStatus.REVIEW),
            new Task("T-10", "Code review – auth module",  "Bob",   TaskStatus.REVIEW),
            new Task("T-11", "Deploy to staging",          "Carol", TaskStatus.DONE),
            new Task("T-12", "Release notes",              "Dave",  TaskStatus.DONE),
            new Task("T-13", "Update dependencies",        "Alice", TaskStatus.DONE),
            new Task("T-14", "Monitoring setup",           "Bob",   TaskStatus.DONE)
        ));

        var panel = DynamicFilterPanel.of(Task.class);

        var board = KanbanBoard.<Task, TaskStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(TaskStatus.TODO,        "To Do",       KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(TaskStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(TaskStatus.REVIEW,      "Review",      KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(TaskStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Task::getId)
                .withItemColumnProvider(Task::getStatus)
                .withItemColumnUpdater(Task::setStatus)
                .withCardRenderer(task -> {
                    var assignee = LabelBuilder.span()
                            .text("\uD83D\uDC64 " + task.getAssignee())
                            .styleName("demo-kanban-card__assignee")
                            .build();
                    var idBadge = LabelBuilder.span()
                            .text(task.getId())
                            .styleName("demo-kanban-card__id")
                            .build();
                    var title = LabelBuilder.span()
                            .text(task.getTitle())
                            .styleName("demo-kanban-card__title")
                            .build();
                    return DivBuilder.create()
                            .styleName("demo-kanban-card")
                            .add(title, assignee,
                                    DivBuilder.create().styleName("demo-kanban-card__footer").add(idBadge).build())
                            .build();
                })
                // ── In-memory: use panel.toPredicate() inside the provider lambda ─────
                // The 'filter' parameter is null; filtering is driven by toPredicate().
                .withDataProvider((query, filter) ->
                        allTasks.stream()
                                .filter(t -> t.getStatus() == query.columnId())
                                .filter(panel.toPredicate())
                                .skip(query.offset())
                                .limit(query.limit()))
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());
                    return KanbanMoveResult.ACCEPTED;
                })
                .build();

        // Re-fetch all columns whenever the user clicks "Apply filter" or removes a row
        board.refreshOnFilterChange(panel);

        return new DemoExample(
                "In-memory Filtering (DynamicFilterPanel + toPredicate)",
                filterContainer(panel, board.getComponent()),
                """
                // ── In-memory wiring ─────────────────────────────────────────────────────
                var panel = DynamicFilterPanel.of(Task.class); // introspects bean fields

                var board = KanbanBoard.<Task, TaskStatus>builder()
                    // ... columns, cardRenderer, moveHandler ...
                    .withDataProvider((query, filter) ->
                        // 'filter' param is null for in-memory; use panel.toPredicate() instead.
                        // toPredicate() converts the last applied filter rows into a Predicate<T>.
                        allTasks.stream()
                            .filter(t -> t.getStatus() == query.columnId())
                            .filter(panel.toPredicate())   // ← live predicate from panel
                            .skip(query.offset())
                            .limit(query.limit()))
                    .build();

                // Refresh all columns on every "Apply filter" click or row-removal
                board.refreshOnFilterChange(panel);

                content(panel, board.getComponent());
                """);
    }

    /**
     * Example 6 – database-style filtering.
     * Uses the one-liner {@link KanbanBoard#bindFilters} which wires the filter group AND
     * registers auto-refresh in a single call. The production Datastore pattern is shown
     * in the code snippet; the live demo simulates it in-memory.
     */
    private DemoExample datastoreFilterExample() {

        // ── Build dataset: 60 tickets (15 per column) ───────────────────────
        var all = new ArrayList<Ticket>();
        String[] filterTeams  = {"Platform", "UI", "Backend", "QA"};
        int[]    filterPts    = {1, 2, 3, 5, 8};
        String[] filterTitles = {
            "Set up environment",  "Write unit tests",    "Implement feature",
            "Fix regression",      "Code review",         "Update documentation",
            "Deploy to staging",   "Performance testing", "Security audit",
            "Refactor module",     "Add logging",         "Update dependencies",
            "Integration tests",   "Database migration",  "Release prep"
        };
        int seq = 0;
        for (BacklogStatus status : BacklogStatus.values()) {
            for (int i = 0; i < 15; i++) {
                seq++;
                all.add(new Ticket("TKT-" + seq, filterTitles[i],
                        filterTeams[i % filterTeams.length],
                        filterPts[i % filterPts.length], status));
            }
        }

        // ── Filter panel ─────────────────────────────────────────────────────
        var panel = DynamicFilterPanel.of(Ticket.class);
        // Pre-populate IN / NOT_IN multi-select for enum and known string columns
        panel.setItems("status", List.of(BacklogStatus.values()));
        panel.setItems("team",   List.of(filterTeams));

        var board = KanbanBoard.<Ticket, BacklogStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(BacklogStatus.BACKLOG,     "Backlog",     KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(BacklogStatus.SPRINT,      "Sprint",      KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(BacklogStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(BacklogStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Ticket::getId)
                .withItemColumnProvider(Ticket::getStatus)
                .withItemColumnUpdater(Ticket::setStatus)
                .withCardRenderer(ticket -> {
                    var idBadge   = LabelBuilder.span().text(ticket.getId())
                            .styleName("demo-kanban-card__id").build();
                    var teamBadge = LabelBuilder.span().text("\uD83D\uDCE6 " + ticket.getTeam())
                            .styleName("demo-kanban-card__assignee").build();
                    var ptsBadge  = LabelBuilder.span().text(ticket.getStoryPoints() + " pts")
                            .styleName("demo-kanban-card__id").build();
                    return CardBuilder.create()
                            .title(LabelBuilder.span().text(ticket.getSummary())
                                    .styleName("demo-kanban-card__title"))
                            .withFooter(idBadge, teamBadge, ptsBadge)
                            .styleName("demo-kanban-card")
                            .build();
                })
                .withColumnPageSize(15)
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());
                    return KanbanMoveResult.ACCEPTED;
                })
                .build();

        // ── bindFilters = setItems(panel, provider) + refreshOnFilterChange ─
        // The board resolves the current QueryFilter from the panel and passes it as
        // 'filter' to the lambda on every refresh.
        // Live demo falls back to toPredicate() since there is no Datastore here.
        board.bindFilters(panel, (query, filter) ->
                all.stream()
                        .filter(t -> t.getStatus() == query.columnId())
                        .filter(panel.isAnyActive() ? panel.toPredicate() : t -> true)
                        .skip(query.offset())
                        .limit(query.limit()));

        return new DemoExample(
                "Database-style Filtering (bindFilters one-liner)",
                filterContainer(panel, board.getComponent()),
                """
                // ── Production Datastore wiring (one-liner) ──────────────────────────────
                var panel = DynamicFilterPanel.of(Ticket.class);
                panel.setItems("status", List.of(BacklogStatus.values())); // IN options
                panel.setItems("team",   List.of("Platform", "UI", "Backend", "QA"));

                var board = KanbanBoard.<Ticket, BacklogStatus>builder()
                    // ... columns, cardRenderer, moveHandler ...
                    .withColumnPageSize(20)
                    .build();

                // bindFilters = setItems(filterGroup, provider) + refreshOnFilterChange(filterGroup)
                // On every refresh the board resolves the current QueryFilter from the panel
                // and passes it as the second argument to the data-provider lambda.
                board.bindFilters(panel, (query, filter) ->
                    datastore.query(TICKET_TARGET)
                             .restrict(query.limit(), query.offset())
                             .filter(filter)                           // QueryFilter (null = no filter)
                             .filter(QueryFilter.eq(STATUS, query.columnId()))
                             .stream(BeanProjection.of(Ticket.class)));

                content(panel, board.getComponent());
                """);
    }

    // ── Helpers ─────────────────────────────────���────────────────────────────

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Example 7 – KanbanStatusBadge.
     * Shows that type and priority metadata can be expressed as semantic tinted
     * pill badges using the built-in {@link KanbanStatusBadge} helper, with no
     * extra CSS needed (all styling comes from {@code kanban-board.css}).
     */
    private DemoExample statusBadgeExample() {

        var issues = new ArrayList<>(List.of(
            new Issue("BSG-1",  "Login crashes on Safari",      "Alice", Priority.HIGH,   IssueType.BUG,     IssueStatus.OPEN),
            new Issue("BSG-2",  "Dark mode flicker",            "Bob",   Priority.MEDIUM, IssueType.BUG,     IssueStatus.IN_PROGRESS),
            new Issue("BSG-3",  "Add export to PDF",            "Carol", Priority.LOW,    IssueType.FEATURE, IssueStatus.IN_PROGRESS),
            new Issue("BSG-4",  "Upgrade Spring Boot",          "Dave",  Priority.HIGH,   IssueType.CHORE,   IssueStatus.TESTING),
            new Issue("BSG-5",  "Search results pagination",    "Alice", Priority.MEDIUM, IssueType.FEATURE, IssueStatus.TESTING),
            new Issue("BSG-6",  "Old API endpoint removed",     "Bob",   Priority.LOW,    IssueType.CHORE,   IssueStatus.CLOSED)
        ));

        var board = KanbanBoard.<Issue, IssueStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(IssueStatus.OPEN,        "Open",        KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(IssueStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(IssueStatus.TESTING,     "Testing",     KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(IssueStatus.CLOSED,      "Closed",      KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Issue::getId)
                .withItemColumnProvider(Issue::getStatus)
                .withItemColumnUpdater(Issue::setStatus)
                .withCardRenderer(issue -> {
                    // ── KanbanStatusBadge: semantic tinted pills, no extra CSS ──
                    var typeBadge = switch (issue.getType()) {
                        case BUG     -> KanbanStatusBadge.error(issue.getType().getLabel());
                        case FEATURE -> KanbanStatusBadge.info(issue.getType().getLabel());
                        case CHORE   -> KanbanStatusBadge.defaultVariant(issue.getType().getLabel());
                    };
                    var priorityBadge = switch (issue.getPriority()) {
                        case HIGH   -> KanbanStatusBadge.error(issue.getPriority().getLabel());
                        case MEDIUM -> KanbanStatusBadge.warning(issue.getPriority().getLabel());
                        case LOW    -> KanbanStatusBadge.success(issue.getPriority().getLabel());
                    };
                    var header = DivBuilder.create()
                            .styleName("demo-kanban-card__header")
                            .add(typeBadge, priorityBadge)
                            .build();
                    var title = LabelBuilder.span()
                            .text(issue.getTitle())
                            .styleName("demo-kanban-card__title")
                            .build();
                    var idBadge = LabelBuilder.span()
                            .text(issue.getId())
                            .styleName("demo-kanban-card__id")
                            .build();
                    var footer = DivBuilder.create()
                            .styleName("demo-kanban-card__footer")
                            .add(idBadge,
                                 LabelBuilder.span().text("\uD83D\uDC64 " + issue.getReporter())
                                         .styleName("demo-kanban-card__assignee").build())
                            .build();
                    return DivBuilder.create()
                            .styleName("demo-kanban-card")
                            .add(header, title, footer)
                            .build();
                })
                .withMoveHandler(request -> {
                    request.item().setStatus(request.toColumn());
                    return KanbanMoveResult.ACCEPTED;
                })
                .withItems(issues)
                .build();

        return new DemoExample("KanbanStatusBadge – semantic tinted card badges",
                board.getComponent(), """
                // KanbanStatusBadge lives in kanban-board.css — no extra CSS needed.
                // Use the static factories to create a tinted pill for any categorical metadata.

                // ── Type badge ──��────────────────────────────────────────────────────
                var typeBadge = switch (issue.getType()) {
                    case BUG     -> KanbanStatusBadge.error(issue.getType().getLabel());   // red
                    case FEATURE -> KanbanStatusBadge.info(issue.getType().getLabel());    // violet
                    case CHORE   -> KanbanStatusBadge.defaultVariant(issue.getType().getLabel()); // slate
                };

                // ── Priority badge ───────────────────────────────────────────────────
                var priorityBadge = switch (issue.getPriority()) {
                    case HIGH   -> KanbanStatusBadge.error(issue.getPriority().getLabel());   // red
                    case MEDIUM -> KanbanStatusBadge.warning(issue.getPriority().getLabel()); // amber
                    case LOW    -> KanbanStatusBadge.success(issue.getPriority().getLabel()); // green
                };

                // ── Column variant constants (same palette) ──────────────────────────
                // Use KanbanStatusBadge.COL_* when building column definitions:
                KanbanColumn.of(IssueStatus.OPEN,        "Open",        KanbanStatusBadge.COL_DEFAULT),
                KanbanColumn.of(IssueStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                KanbanColumn.of(IssueStatus.TESTING,     "Testing",     KanbanStatusBadge.COL_WARNING),
                KanbanColumn.of(IssueStatus.CLOSED,      "Closed",      KanbanStatusBadge.COL_SUCCESS),
                // KanbanStatusBadge.COL_ERROR  → red column (blocked / failed)
                """);
    }

    /**
     * Example 8 – KanbanI18n.
     * Demonstrates overriding the built-in English button labels with custom text.
     * Supports both plain-text overrides and full {@link com.holonplatform.core.i18n.Localizable}
     * with message-code lookup via Holon's I18NProvider / {@code messages*.properties}.
     */
    private DemoExample i18nExample() {

        var i18n = KanbanI18n.defaults()
                .columnOptions("\u2699 Options")      // replaces "..."
                .addCard("\u002B New task")            // replaces "+ Add card"
                .open("\uD83D\uDC41 View")            // replaces "Open"
                .edit("\u270F Edit")                  // replaces "Edit"
                .delete("\uD83D\uDDD1 Remove");       // replaces "Delete"

        var tasks = new ArrayList<>(List.of(
            new Task("I18-1", "Set up CI pipeline",      "Alice", TaskStatus.TODO),
            new Task("I18-2", "Design data model",        "Bob",   TaskStatus.TODO),
            new Task("I18-3", "Implement login screen",   "Carol", TaskStatus.IN_PROGRESS),
            new Task("I18-4", "Write unit tests",         "Dave",  TaskStatus.DONE)
        ));

        @SuppressWarnings("unchecked")
        KanbanBoard<Task, TaskStatus>[] ref = new KanbanBoard[1];

        var board = KanbanBoard.<Task, TaskStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(TaskStatus.TODO,        "To Do",       KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(TaskStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(TaskStatus.REVIEW,      "Review",      KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(TaskStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Task::getId)
                .withItemColumnProvider(Task::getStatus)
                .withItemColumnUpdater(Task::setStatus)
                .withCardRenderer(task -> DivBuilder.create()
                        .styleName("demo-kanban-card")
                        .add(LabelBuilder.span().text(task.getTitle()).styleName("demo-kanban-card__title").build(),
                             LabelBuilder.span().text("\uD83D\uDC64 " + task.getAssignee()).styleName("demo-kanban-card__assignee").build())
                        .build())
                .withCardActionHandler(new KanbanCardActionHandler<Task>() {
                    @Override
                    public void onOpen(Task t) {
                        Notification.show("\uD83D\uDC41 View: " + t.getTitle(), 2000, Notification.Position.BOTTOM_START);
                    }
                    @Override
                    public void onEdit(Task t) {
                        Notification.show("\u270F Edit: " + t.getTitle(), 2000, Notification.Position.BOTTOM_START);
                    }
                    @Override
                    public void onDelete(Task t) {
                        tasks.remove(t);
                        if (ref[0] != null) ref[0].refresh();
                    }
                })
                .withColumnActionHandler(new KanbanColumnActionHandler<TaskStatus>() {
                    @Override
                    public void onOptions(TaskStatus col) {
                        Notification.show("\u2699 Options: " + col.name(), 2000, Notification.Position.TOP_CENTER);
                    }
                    @Override
                    public void onAddCard(TaskStatus col) {
                        Notification.show("\u002B New task in " + col.name(), 2000, Notification.Position.TOP_CENTER);
                    }
                })
                .withMoveHandler(req -> { req.item().setStatus(req.toColumn()); return KanbanMoveResult.ACCEPTED; })
                .withItems(tasks)
                .withI18n(i18n)  // ← apply custom labels
                .build();

        ref[0] = board;

        var hint = new Span("Hover over the column header to see the custom '⚙ Options' button. " +
                "Card action buttons are labeled '👁 View', '✏ Edit', '🗑 Remove'.");

        var container = new Div(board.getComponent(), hint);

        return new DemoExample("Custom button labels (KanbanI18n)", container, """
                // ── Plain-text overrides (no message-code lookup) ────────────────────
                KanbanI18n i18n = KanbanI18n.defaults()
                        .columnOptions("⚙ Options")   // replaces built-in "..."
                        .addCard("＋ New task")         // replaces "+ Add card"
                        .open("👁 View")
                        .edit("✏ Edit")
                        .delete("🗑 Remove");

                // ── Full Localizable override (message-code lookup via I18NProvider) ─
                KanbanI18n i18n = KanbanI18n.defaults()
                        .addCard(Localizable.builder()
                                .message("+ Add card")
                                .messageCode(KanbanI18n.CODE_ADD_CARD)   // "kanban.column.content-card"
                                .build());

                // Built-in message codes (override in messages*.properties):
                //   kanban.column.options   → column options button
                //   kanban.column.content-card  → content-card footer button
                //   kanban.card.action.open → card Open action
                //   kanban.card.action.edit → card Edit action
                //   kanban.card.action.delete → card Delete action

                var board = KanbanBoard.<Task, TaskStatus>builder()
                        // ... columns, renderers, handlers ...
                        .withI18n(i18n)   // pass null to reset to English defaults
                        .build();
                """);
    }

    /**
     * Example 9 – beforeMove validation gate + KanbanCountProvider.
     * <ul>
     *   <li>{@code beforeMove} rejects the move before {@code onMove} runs if the
     *       business rule is violated (DONE → BACKLOG is not allowed).</li>
     *   <li>{@code withColumnCountProvider} supplies the real total per column so the
     *       column-header badge shows the true count even when page size < total.</li>
     * </ul>
     */
    private DemoExample moveValidationAndCountExample() {

        // 30 tickets — 10 per column (page size = 5, so badge must come from countProvider)
        var allTickets = new ArrayList<Ticket>();
        String[] mvTeams  = {"Platform", "UI", "Backend", "QA", "DevOps"};
        int[]    mvPts    = {1, 2, 3, 5, 8};
        String[] mvNames  = {
            "Set up environment",   "Write unit tests",    "Implement feature",
            "Fix regression",       "Code review",         "Update documentation",
            "Deploy to staging",    "Performance testing", "Security audit",
            "Release prep"
        };
        int seq = 0;
        for (BacklogStatus status : new BacklogStatus[]{BacklogStatus.BACKLOG, BacklogStatus.SPRINT, BacklogStatus.DONE}) {
            for (int i = 0; i < 10; i++) {
                seq++;
                allTickets.add(new Ticket("MV-" + seq, mvNames[i % mvNames.length],
                        mvTeams[i % mvTeams.length], mvPts[i % mvPts.length], status));
            }
        }

        var board = KanbanBoard.<Ticket, BacklogStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(BacklogStatus.BACKLOG, "Backlog",     KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(BacklogStatus.SPRINT,  "Sprint",      KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(BacklogStatus.DONE,    "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Ticket::getId)
                .withItemColumnProvider(Ticket::getStatus)
                .withItemColumnUpdater(Ticket::setStatus)
                .withCardRenderer(ticket -> {
                    // Colour the story-points badge semantically
                    var ptsBadge = KanbanStatusBadge.of(
                            ticket.getStoryPoints() + " pts",
                            ticket.getStoryPoints() >= 5
                                    ? KanbanStatusBadge.Variant.WARNING
                                    : KanbanStatusBadge.Variant.DEFAULT);
                    return DivBuilder.create()
                            .styleName("demo-kanban-card")
                            .add(LabelBuilder.span().text(ticket.getSummary()).styleName("demo-kanban-card__title").build(),
                                 LabelBuilder.span().text("\uD83D\uDCE6 " + ticket.getTeam()).styleName("demo-kanban-card__assignee").build(),
                                 ptsBadge)
                            .build();
                })
                // ── beforeMove gate: prevent DONE → BACKLOG ──────────────────────
                .withMoveHandler(new KanbanMoveHandler<Ticket, BacklogStatus>() {

                    @Override
                    public boolean beforeMove(KanbanMoveRequest<Ticket, BacklogStatus> req) {
                        if (req.fromColumn() == BacklogStatus.DONE
                                && req.toColumn() == BacklogStatus.BACKLOG) {
                            var n = Notification.show(
                                    "\u274C Cannot revert DONE \u2192 BACKLOG",
                                    3000, Notification.Position.MIDDLE);
                            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            return false;  // abort — onMove is never called
                        }
                        return true;
                    }

                    @Override
                    public KanbanMoveResult onMove(KanbanMoveRequest<Ticket, BacklogStatus> req) {
                        req.item().setStatus(req.toColumn());
                        return KanbanMoveResult.ACCEPTED;
                    }
                })
                // ── KanbanCountProvider: real column count for the header badge ────
                // Without this, badge = rendered card count (page size = 5).
                // With this, badge = true total (10) — like a COUNT(*) in production.
                .withColumnCountProvider((columnId, filter) ->
                        allTickets.stream().filter(t -> t.getStatus() == columnId).count())
                .withDataProvider((query, filter) ->
                        allTickets.stream()
                                .filter(t -> t.getStatus() == query.columnId())
                                .skip(query.offset())
                                .limit(query.limit()))
                .withColumnPageSize(5)   // only 5 rendered per column, but badge shows 10
                .build();

        var hint = new Span(
                "30 tickets total \u2022 10 per column \u2022 page size = 5. " +
                "Column badges show the true count (10) via KanbanCountProvider. " +
                "Try dragging a DONE card to BACKLOG \u2014 beforeMove blocks the move.");

        var container = new Div(board.getComponent(), hint);

        return new DemoExample(
                "Move validation (beforeMove gate) + column count badges (KanbanCountProvider)",
                container, """
                // ── beforeMove gate ─────────────────────────────────────────────────
                // Return false to abort BEFORE onMove runs.
                // Useful for business-rule / state-machine validation.
                .withMoveHandler(new KanbanMoveHandler<>() {

                    @Override
                    public boolean beforeMove(KanbanMoveRequest<Ticket, BacklogStatus> req) {
                        if (req.fromColumn() == DONE && req.toColumn() == BACKLOG) {
                            Notification.show("Cannot revert DONE → BACKLOG");
                            return false;   // move aborted; onMove is never called
                        }
                        return true;
                    }

                    @Override
                    public KanbanMoveResult onMove(KanbanMoveRequest<Ticket, BacklogStatus> req) {
                        req.item().setStatus(req.toColumn());
                        return KanbanMoveResult.ACCEPTED;
                    }
                })

                // ── KanbanCountProvider: true total per column ───────────────────────
                // Without: column badge = rendered card count (= page size or less).
                // With:    column badge = real total returned by this lambda.
                // In production, map to a COUNT(*) query:
                .withColumnCountProvider((columnId, filter) ->
                        repo.countByStatus(columnId, filter))   // real COUNT(*)

                // ── KanbanStatusBadge with a Variant chosen at runtime ───────────────
                var ptsBadge = KanbanStatusBadge.of(
                        ticket.getStoryPoints() + " pts",
                        ticket.getStoryPoints() >= 5
                                ? KanbanStatusBadge.Variant.WARNING
                                : KanbanStatusBadge.Variant.DEFAULT);
                """);
    }

    /**
     * Example 10 – programmatic API: moveItem() + getColumnOf().
     * Shows that the board can be driven from application code without user drag-and-drop.
     */
    private DemoExample programmaticMoveExample() {

        var allTasks = new ArrayList<>(List.of(
            new Task("PM-1", "Set up project",       "Alice", TaskStatus.TODO),
            new Task("PM-2", "Design architecture",  "Bob",   TaskStatus.TODO),
            new Task("PM-3", "Implement login",      "Carol", TaskStatus.IN_PROGRESS),
            new Task("PM-4", "Write unit tests",     "Dave",  TaskStatus.IN_PROGRESS),
            new Task("PM-5", "Deploy to staging",    "Alice", TaskStatus.REVIEW),
            new Task("PM-6", "Release notes",        "Bob",   TaskStatus.DONE)
        ));

        @SuppressWarnings("unchecked")
        KanbanBoard<Task, TaskStatus>[] ref = new KanbanBoard[1];

        var board = KanbanBoard.<Task, TaskStatus>builder()
                .withColumns(List.of(
                        KanbanColumn.of(TaskStatus.TODO,        "To Do",       KanbanStatusBadge.COL_DEFAULT),
                        KanbanColumn.of(TaskStatus.IN_PROGRESS, "In Progress", KanbanStatusBadge.COL_INFO),
                        KanbanColumn.of(TaskStatus.REVIEW,      "Review",      KanbanStatusBadge.COL_WARNING),
                        KanbanColumn.of(TaskStatus.DONE,        "Done",        KanbanStatusBadge.COL_SUCCESS)
                ))
                .withItemIdentifierProvider(Task::getId)
                .withItemColumnProvider(Task::getStatus)
                .withItemColumnUpdater(Task::setStatus)
                .withCardRenderer(task -> DivBuilder.create()
                        .styleName("demo-kanban-card")
                        .add(LabelBuilder.span().text(task.getTitle()).styleName("demo-kanban-card__title").build(),
                             LabelBuilder.span().text("\uD83D\uDC64 " + task.getAssignee()).styleName("demo-kanban-card__assignee").build(),
                             LabelBuilder.span().text(task.getId()).styleName("demo-kanban-card__id").build())
                        .build())
                .withMoveHandler(req -> { req.item().setStatus(req.toColumn()); return KanbanMoveResult.ACCEPTED; })
                .withItems(allTasks)
                .build();

        ref[0] = board;

        // ── Programmatic controls ─────────────────────────────────────────────

        var promoteBtn = new Button("\u25B6 Promote first TODO \u2192 In Progress", e -> {
            if (ref[0] == null) return;
            allTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.TODO)
                    .findFirst()
                    .ifPresentOrElse(t -> {
                        boolean moved = ref[0].moveItem(t, TaskStatus.IN_PROGRESS);
                        var n = Notification.show(
                                moved ? "\u2713 \"" + t.getTitle() + "\" \u2192 In Progress"
                                      : "No TODO tasks to promote",
                                2000, Notification.Position.BOTTOM_START);
                        n.addThemeVariants(moved ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_CONTRAST);
                    }, () -> Notification.show("No TODO tasks left", 2000, Notification.Position.BOTTOM_START));
        });

        var whereBtn = new Button("\uD83D\uDD0E Where is PM-3?", e -> {
            if (ref[0] == null) return;
            allTasks.stream()
                    .filter(t -> "PM-3".equals(t.getId()))
                    .findFirst()
                    .ifPresent(t -> {
                        var col = ref[0].getColumnOf(t);
                        Notification.show(
                                "PM-3 is in: " + col.map(Enum::name).orElse("unknown"),
                                2500, Notification.Position.BOTTOM_START);
                    });
        });

        var controls = new Div(promoteBtn, whereBtn);

        var container = new Div(board.getComponent(), controls);

        return new DemoExample("Programmatic API (moveItem + getColumnOf)", container, """
                // ── board.moveItem(item, toColumn) ────────────────────────────────────
                // Equivalent to a programmatic drag-and-drop.
                // Runs the full pipeline: beforeMove → onMove → afterMove → audit trail.
                // Returns true if the move was accepted; false if rejected or invalid.
                boolean moved = board.moveItem(task, TaskStatus.IN_PROGRESS);

                // ── board.getColumnOf(item) ───────────────────────────────────────────
                // Returns the column the item is currently rendered in (Optional).
                // Falls back to itemColumnProvider.apply(item) if not yet rendered.
                Optional<TaskStatus> col = board.getColumnOf(task);  // Optional[IN_PROGRESS]

                // ── Use cases ─────────────────────────────────────────────────────────
                // • "Promote all TODO" button in a sprint planning toolbar
                // • Auto-advance workflow steps from a background scheduler
                // • Keyboard shortcut: move selected card without drag
                """);
    }

    /** Wraps a filter panel and a board component in a labelled container div. */
    private static Div filterContainer(Div filterPanel, com.vaadin.flow.component.Component board) {
        var container = new Div(filterPanel, board);
        return container;
    }

    private static String friendlyName(ArticleStatus status) {
        return switch (status) {
            case DRAFT -> "Draft";
            case IN_REVIEW -> "In Review";
            case APPROVED -> "Approved";
            case PUBLISHED -> "Published";
        };
    }

    private static Div detailRow(String key, String value) {
        var row = new Div();

        var k = new Span(key);

        var v = new Span(value);

        row.add(k, v);
        return row;
    }

    private static Div commentRow(KanbanComment comment) {
        var row = new Div();

        var header = new Div();

        if (comment.author() != null && !comment.author().isBlank()) {
            var author = new Span(comment.author());
            header.add(author);
        }

        var time = new Span(formatAgo(comment.createdAt()));
        header.add(time);

        var msg = new Span(comment.message());

        row.add(header, msg);
        return row;
    }

    @SuppressWarnings("unused") // kept for demo reference
    private static <C> Div auditRow(KanbanMoveAuditEntry<C> entry) {
        var row = new Div();

        var result = new Span(entry.result().name());
        result.addClassName(entry.result() == KanbanMoveResult.ACCEPTED
                ? "demo-kanban-audit-row__result--accepted"
                : "demo-kanban-audit-row__result--rejected");

        var from = entry.fromColumn() != null ? String.valueOf(entry.fromColumn()) : "?";
        var move = new Span(entry.itemId() + ": " + from + " \u2192 " + entry.toColumn());

        var time = new Span(formatAgo(entry.timestamp()));

        row.add(result, move, time);
        return row;
    }

    private static String formatAgo(Instant instant) {
        long seconds = java.time.Duration.between(instant, Instant.now()).abs().getSeconds();
        if (seconds < 60)  return "just now";
        long minutes = seconds / 60;
        if (minutes < 60)  return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24)    return hours + "h ago";
        return java.time.LocalDate.ofInstant(instant,
                java.time.ZoneId.systemDefault()).toString();
    }
}









