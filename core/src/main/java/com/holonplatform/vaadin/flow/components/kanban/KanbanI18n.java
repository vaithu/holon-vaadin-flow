package com.holonplatform.vaadin.flow.components.kanban;

import com.holonplatform.core.i18n.Localizable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Internationalization labels for the {@link com.holonplatform.vaadin.flow.components.KanbanBoard} component.
 *
 * <p>Every label is stored as a {@link Localizable} — it carries both a default English fallback
 * (used when no translation is found) and an optional message code used for lookup through the
 * Holon Platform {@link com.holonplatform.vaadin.flow.i18n.LocalizationProvider} / Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} chain.</p>
 *
 * <p>Create an instance via {@link #defaults()}, then override only the labels you need:</p>
 * <pre>{@code
 * // Plain text override (no message-code lookup):
 * KanbanI18n i18n = KanbanI18n.defaults()
 *         .addCard("+ Neue Karte")
 *         .open("Öffnen");
 *
 * // Full Localizable override (uses I18NProvider / LocalizationContext):
 * KanbanI18n i18n = KanbanI18n.defaults()
 *         .addCard(Localizable.builder()
 *                 .message("+ Add card")
 *                 .messageCode("myapp.kanban.add-card")
 *                 .build());
 *
 * KanbanBoard.<Task, Status>builder()
 *         .withI18n(i18n)
 *         ...
 *         .build();
 * }</pre>
 *
 * <p>Default message codes (override any via your {@code messages*.properties}):</p>
 * <ul>
 *   <li>{@value #CODE_COLUMN_OPTIONS} – column options button</li>
 *   <li>{@value #CODE_ADD_CARD} – add-card footer button</li>
 *   <li>{@value #CODE_OPEN} – card "Open" action</li>
 *   <li>{@value #CODE_EDIT} – card "Edit" action</li>
 *   <li>{@value #CODE_DELETE} – card "Delete" action</li>
 * </ul>
 *
 * @since 10.0.0
 */
public final class KanbanI18n implements Serializable {

    /** Message code for the column options button. */
    public static final String CODE_COLUMN_OPTIONS = "kanban.column.options";
    /** Message code for the add-card footer button. */
    public static final String CODE_ADD_CARD       = "kanban.column.add-card";
    /** Message code for the card Open action button. */
    public static final String CODE_OPEN           = "kanban.card.action.open";
    /** Message code for the card Edit action button. */
    public static final String CODE_EDIT           = "kanban.card.action.edit";
    /** Message code for the card Delete action button. */
    public static final String CODE_DELETE         = "kanban.card.action.delete";

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Fields ──────────────────────────────────────────────────────────────

    private Localizable columnOptions = Localizable.builder()
            .message("...").messageCode(CODE_COLUMN_OPTIONS).build();
    private Localizable addCard = Localizable.builder()
            .message("+ Add card").messageCode(CODE_ADD_CARD).build();
    private Localizable open = Localizable.builder()
            .message("Open").messageCode(CODE_OPEN).build();
    private Localizable edit = Localizable.builder()
            .message("Edit").messageCode(CODE_EDIT).build();
    private Localizable delete = Localizable.builder()
            .message("Delete").messageCode(CODE_DELETE).build();

    // ── Constructor ──────────────────────────────────────────────────────────

    private KanbanI18n() {}

    // ── Factory ──────────────────────────────────────────────────────────────

    /**
     * Returns a new {@code KanbanI18n} initialised with English defaults and the
     * built-in message codes.  Override individual labels as needed.
     *
     * @return a mutable {@code KanbanI18n} instance
     */
    public static KanbanI18n defaults() {
        return new KanbanI18n();
    }

    // ── Fluent setters – String overloads (plain text, no lookup) ────────────

    /** Sets the column-options button label as plain text (no message-code lookup). */
    public KanbanI18n columnOptions(String text) {
        return columnOptions(Localizable.builder().message(requireNonBlank(text, "columnOptions")).build());
    }

    /** Sets the add-card button label as plain text (no message-code lookup). */
    public KanbanI18n addCard(String text) {
        return addCard(Localizable.builder().message(requireNonBlank(text, "addCard")).build());
    }

    /** Sets the card Open action label as plain text (no message-code lookup). */
    public KanbanI18n open(String text) {
        return open(Localizable.builder().message(requireNonBlank(text, "open")).build());
    }

    /** Sets the card Edit action label as plain text (no message-code lookup). */
    public KanbanI18n edit(String text) {
        return edit(Localizable.builder().message(requireNonBlank(text, "edit")).build());
    }

    /** Sets the card Delete action label as plain text (no message-code lookup). */
    public KanbanI18n delete(String text) {
        return delete(Localizable.builder().message(requireNonBlank(text, "delete")).build());
    }

    // ── Fluent setters – Localizable overloads (full i18n) ───────────────────

    /**
     * Sets the column-options button label as a {@link Localizable}.
     * The board resolves the label via {@link com.holonplatform.vaadin.flow.i18n.LocalizationProvider}
     * at render time (falling back to {@link Localizable#getMessage()}).
     */
    public KanbanI18n columnOptions(Localizable localizable) {
        this.columnOptions = Objects.requireNonNull(localizable, "columnOptions Localizable must not be null");
        return this;
    }

    /** Sets the add-card button label as a {@link Localizable}. */
    public KanbanI18n addCard(Localizable localizable) {
        this.addCard = Objects.requireNonNull(localizable, "addCard Localizable must not be null");
        return this;
    }

    /** Sets the card Open action label as a {@link Localizable}. */
    public KanbanI18n open(Localizable localizable) {
        this.open = Objects.requireNonNull(localizable, "open Localizable must not be null");
        return this;
    }

    /** Sets the card Edit action label as a {@link Localizable}. */
    public KanbanI18n edit(Localizable localizable) {
        this.edit = Objects.requireNonNull(localizable, "edit Localizable must not be null");
        return this;
    }

    /** Sets the card Delete action label as a {@link Localizable}. */
    public KanbanI18n delete(Localizable localizable) {
        this.delete = Objects.requireNonNull(localizable, "delete Localizable must not be null");
        return this;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Localizable getColumnOptions() { return columnOptions; }
    public Localizable getAddCard()       { return addCard; }
    public Localizable getOpen()          { return open; }
    public Localizable getEdit()          { return edit; }
    public Localizable getDelete()        { return delete; }

    // ── Internal ─────────────────────────────────────────────────────────────

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " label must not be null or blank");
        }
        return value;
    }
}
