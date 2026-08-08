package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;

/**
 * Non-interactive status pill — a coloured dot + label inside a tinted pill shape.
 *
 * <pre>
 *   ● Posted          (SUCCESS — green)
 *   ● Partial receipt (WARNING — amber)
 *   ● In progress     (INFO    — blue)
 *   ● Quality check   (VIOLET  — violet)
 *   ● ASN: NW-2281    (DEFAULT — gray)
 * </pre>
 *
 * <p>The coloured dot is rendered via a CSS {@code ::before} pseudo-element whose colour is
 * always {@code currentColor}, so it automatically matches the variant's text colour.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // Row of status badges (matches the dh-meta design)
 * Div meta = new Div();
 * meta.addClassName("st-badge-row");
 * meta.add(
 *     StatusBadge.of("Posted",              StatusBadge.Variant.SUCCESS),
 *     StatusBadge.of("Partial receipt",     StatusBadge.Variant.WARNING),
 *     StatusBadge.of("5 of 8 lines received"),
 *     StatusBadge.of("3-way match: pending"),
 *     StatusBadge.of("ASN: NW-2281")
 * );
 *
 * // Inside a list item / card (second screenshot pattern)
 * StatusBadge.of("In progress",   StatusBadge.Variant.INFO)
 * StatusBadge.of("Quality check", StatusBadge.Variant.VIOLET)
 * StatusBadge.of("Partial · 2 issues", StatusBadge.Variant.WARNING)
 * }</pre>
 *
 * <h3>Available variants</h3>
 * <ul>
 *   <li>{@link Variant#DEFAULT} — gray  (neutral info, counts, metadata)</li>
 *   <li>{@link Variant#SUCCESS} — green (posted, reconciled, completed)</li>
 *   <li>{@link Variant#WARNING} — amber (partial, pending, caution)</li>
 *   <li>{@link Variant#DANGER}  — red   (error, rejected, failed)</li>
 *   <li>{@link Variant#INFO}    — blue  (in progress, draft, scheduled)</li>
 *   <li>{@link Variant#VIOLET}  — violet (quality check, review)</li>
 * </ul>
 */
@StyleSheet("context://status-badge.css")
public class StatusBadge extends Span {

    // ── Variant ───────────────────────────────────────────────────────────

    /**
     * Semantic colour variant for a {@link StatusBadge}.
     * Each variant maps to a CSS class that sets both the background tint and
     * the text/dot colour — all values live in {@code status-badge.css}.
     */
    public enum Variant {

        /** Gray — neutral info, counts, metadata. */
        DEFAULT(null),

        /** Green — posted, reconciled, completed, active. */
        SUCCESS("st-badge--success"),

        /** Amber — partial, pending, on hold, caution. */
        WARNING("st-badge--warning"),

        /** Red — error, rejected, failed, cancelled. */
        DANGER("st-badge--danger"),

        /** Blue — in progress, processing, draft, scheduled. */
        INFO("st-badge--info"),

        /** Violet — quality check, under review, flagged. */
        VIOLET("st-badge--violet");

        private final String cssClass;

        Variant(String cssClass) {
            this.cssClass = cssClass;
        }

        /** @return the CSS class for this variant, or {@code null} for {@link #DEFAULT} */
        public String cssClass() {
            return cssClass;
        }
    }

    // ── Size ──────────────────────────────────────────────────────────────

    /** Size preset for a {@link StatusBadge}. */
    public enum Size {
        /** Default — 12 px font, 3 px vertical padding (compact). */
        SM(null),
        /** Medium — 13 px font, 4 px vertical padding (matches form row height). */
        MD("st-badge--md");

        private final String cssClass;

        Size(String cssClass) {
            this.cssClass = cssClass;
        }

        /** @return the CSS class for this size, or {@code null} for {@link #SM} */
        public String cssClass() {
            return cssClass;
        }
    }

    // ── Constructors ──────────────────────────────────────────────────────

    /**
     * Creates a gray (DEFAULT) status badge.
     * @param label label text (not null)
     */
    public StatusBadge(String label) {
        this(label, Variant.DEFAULT, Size.SM);
    }

    /**
     * Creates a status badge with a semantic colour variant.
     * @param label   label text (not null)
     * @param variant colour variant
     */
    public StatusBadge(String label, Variant variant) {
        this(label, variant, Size.SM);
    }

    /**
     * Creates a status badge with a semantic colour variant and size.
     * @param label   label text (not null)
     * @param variant colour variant
     * @param size    size preset
     */
    public StatusBadge(String label, Variant variant, Size size) {
        super(label);
        getClassNames().add("st-badge");
        applyVariant(variant);
        applySize(size);
    }

    // ── Localizable constructors ──────────────────────────────────────────

    /** Creates a gray badge with localizable label. */
    public StatusBadge(Localizable label) {
        this(resolve(label), Variant.DEFAULT, Size.SM);
    }

    /** Creates a badge with localizable label and variant. */
    public StatusBadge(Localizable label, Variant variant) {
        this(resolve(label), variant, Size.SM);
    }

    // ── Factory methods ───────────────────────────────────────────────────

    /** Creates a gray (DEFAULT) status badge. */
    public static StatusBadge of(String label) {
        return new StatusBadge(label);
    }

    /** Creates a status badge with the given variant. */
    public static StatusBadge of(String label, Variant variant) {
        return new StatusBadge(label, variant);
    }

    /** Creates a status badge with variant and size. */
    public static StatusBadge of(String label, Variant variant, Size size) {
        return new StatusBadge(label, variant, size);
    }

    /** Creates a gray badge with localizable label. */
    public static StatusBadge of(Localizable label) {
        return new StatusBadge(label);
    }

    /** Creates a badge with localizable label and variant. */
    public static StatusBadge of(Localizable label, Variant variant) {
        return new StatusBadge(label, variant);
    }

    // ── Mutators ──────────────────────────────────────────────────────────

    /**
     * Changes the colour variant at runtime (e.g. when the underlying status changes).
     * Removes any previous variant class before applying the new one.
     * @param variant the new variant (not null)
     */
    public void setVariant(Variant variant) {
        // Remove all known variant classes first
        for (Variant v : Variant.values()) {
            if (v.cssClass() != null) {
                getClassNames().remove(v.cssClass());
            }
        }
        applyVariant(variant);
    }

    /** Updates the badge label from a {@link Localizable} descriptor. */
    public void setText(Localizable text) {
        super.setText(resolve(text));
    }

    // ── Internal ─────────────────────────────────────────────────────────

    private void applyVariant(Variant variant) {
        if (variant != null && variant.cssClass() != null) {
            getClassNames().add(variant.cssClass());
        }
    }

    private void applySize(Size size) {
        if (size != null && size.cssClass() != null) {
            getClassNames().add(size.cssClass());
        }
    }

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }
}
