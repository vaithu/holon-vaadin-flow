package com.holonplatform.vaadin.flow.components.kanban;

import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A tinted pill badge for use inside a {@link KanbanCardRenderer} to display
 * card status, labels, priority levels, and other categorical metadata.
 *
 * <p>The badge is a {@link Span} with the {@code kanban-status-badge} base class
 * plus a variant modifier. All visual styling is defined in
 * {@code kanban-board.css} — no inline styles are used.</p>
 *
 * <h3>Usage in a KanbanCardRenderer</h3>
 * <pre>{@code
 * board.setCardRenderer(task -> {
 *     Div card = new Div();
 *     card.add(new Span(task.getTitle()));
 *     card.add(KanbanStatusBadge.of(task.getPriority()));   // enum → variant
 *     card.add(KanbanStatusBadge.warning("In Review"));
 *     return card;
 * });
 * }</pre>
 *
 * <h3>Column variant classes</h3>
 * The same semantic palette is available for column headers. Pass one of the
 * following class names to {@link KanbanColumn#of(Object, String, String)}:
 * <ul>
 *   <li>{@code "kanban-col-default"} — neutral slate</li>
 *   <li>{@code "kanban-col-info"}    — violet (in-progress)</li>
 *   <li>{@code "kanban-col-warning"} — amber (in-review)</li>
 *   <li>{@code "kanban-col-success"} — green (done)</li>
 *   <li>{@code "kanban-col-error"}   — red (blocked)</li>
 * </ul>
 *
 * @since 10.0.0
 */
public final class KanbanStatusBadge extends Span {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Variant constants (CSS modifier suffixes) ────────────────────────

    public static final String COL_DEFAULT = "kanban-col-default";
    public static final String COL_INFO    = "kanban-col-info";
    public static final String COL_WARNING = "kanban-col-warning";
    public static final String COL_SUCCESS = "kanban-col-success";
    public static final String COL_ERROR   = "kanban-col-error";

    /**
     * Semantic variant — determines the tinted colour palette of the badge.
     */
    public enum Variant {
        /** Neutral slate — use for generic labels or "To Do" status. */
        DEFAULT,
        /** Violet — use for in-progress or informational status. */
        INFO,
        /** Amber — use for pending-review or caution status. */
        WARNING,
        /** Green — use for done, approved, or positive status. */
        SUCCESS,
        /** Red — use for blocked, failed, or critical status. */
        ERROR
    }

    // ── Constructors ─────────────────────────────────────────────────────

    private KanbanStatusBadge(String text, Variant variant) {
        super(text);
        addClassName("kanban-status-badge");
        addClassName("kanban-status-badge--" + variant.name().toLowerCase());
    }

    // ── Static factories ─────────────────────────────────────────────────

    /**
     * Creates a badge with the given text and variant.
     *
     * @param text    label text (not null)
     * @param variant semantic variant (not null)
     * @return a new {@link KanbanStatusBadge}
     */
    public static KanbanStatusBadge of(String text, Variant variant) {
        return new KanbanStatusBadge(text, variant == null ? Variant.DEFAULT : variant);
    }

    /** Creates a neutral slate badge. */
    public static KanbanStatusBadge defaultVariant(String text) { return of(text, Variant.DEFAULT); }

    /** Creates a violet (in-progress / info) badge. */
    public static KanbanStatusBadge info(String text)    { return of(text, Variant.INFO); }

    /** Creates an amber (in-review / warning) badge. */
    public static KanbanStatusBadge warning(String text) { return of(text, Variant.WARNING); }

    /** Creates a green (done / success) badge. */
    public static KanbanStatusBadge success(String text) { return of(text, Variant.SUCCESS); }

    /** Creates a red (blocked / error) badge. */
    public static KanbanStatusBadge error(String text)   { return of(text, Variant.ERROR); }
}



