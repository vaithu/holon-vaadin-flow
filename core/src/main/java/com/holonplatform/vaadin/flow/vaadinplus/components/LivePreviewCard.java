package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A dark-gradient summary card for displaying a live preview of structured form data.
 *
 * <p>Typically placed in a sidebar alongside a multi-step form to give users immediate
 * visual feedback as they fill in fields. All content is updated programmatically via
 * the returned {@link Stat} handles — no re-render required.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌─────────────────────────────────┐  ← dark-gradient background
 * │ LIVE PREVIEW                    │  eyebrow
 * │ Acme Corp GmbH                  │  title (optional accent span for brand name)
 * │ Technology · Enterprise · Gold  │  subtitle
 * │ ┌─────────────┐ ┌─────────────┐ │
 * │ │ Status      │ │ Location    │ │  stat cells (2-col grid)
 * │ │ Draft       │ │ DE · Berlin │ │
 * │ └─────────────┘ └─────────────┘ │
 * └─────────────────────────────────┘
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * LivePreviewCard card = new LivePreviewCard("Live preview");
 * card.setTitle("Acme", " Corp", " GmbH");           // accent on middle word
 * card.setSubtitle("Technology · Enterprise");
 *
 * LivePreviewCard.Stat status = card.addStat("Status",   "Draft",   LivePreviewCard.StatVariant.WARNING);
 * LivePreviewCard.Stat owner  = card.addStat("Owner",    "—");
 * LivePreviewCard.Stat loc    = card.addStat("Location", "—");
 * LivePreviewCard.Stat cur    = card.addStat("Currency", "EUR");
 *
 * // Update reactively from form events:
 * nameField.addValueChangeListener(e -> card.setTitle(e.getValue()));
 * status.setValue("Active").setVariant(LivePreviewCard.StatVariant.SUCCESS);
 * }</pre>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/live-preview-card.css} — BEM root: {@code .lpc}
 */
@StyleSheet("context://live-preview-card.css")
public class LivePreviewCard extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── BEM class constants ───────────────────────────────────────────────

    private static final String CSS_ROOT     = "lpc";
    private static final String CSS_EYEBROW  = "lpc__eyebrow";
    private static final String CSS_TITLE    = "lpc__title";
    private static final String CSS_ACCENT   = "lpc__title-accent";
    private static final String CSS_SUBTITLE = "lpc__subtitle";
    private static final String CSS_STATS    = "lpc__stats";
    static final String CSS_STAT             = "lpc__stat";
    static final String CSS_STAT_LBL         = "lpc__stat-label";
    static final String CSS_STAT_VAL         = "lpc__stat-value";

    // ── DOM structure ─────────────────────────────────────────────────────

    private final Span eyebrowSpan = new Span();
    private final Div  titleDiv    = new Div();
    private final Div  subtitleDiv = new Div();
    private final Div  statsGrid   = new Div();

    // ── Constructor ───────────────────────────────────────────────────────

    /**
     * Creates a {@code LivePreviewCard} with the given eyebrow label.
     *
     * @param eyebrow small uppercase caption shown above the title (e.g. {@code "Live preview"})
     */
    public LivePreviewCard(String eyebrow) {
        addClassName(CSS_ROOT);
        eyebrowSpan.addClassName(CSS_EYEBROW);
        titleDiv.addClassName(CSS_TITLE);
        subtitleDiv.addClassName(CSS_SUBTITLE);
        statsGrid.addClassName(CSS_STATS);
        add(eyebrowSpan, titleDiv, subtitleDiv, statsGrid);
        setEyebrow(eyebrow);
    }

    /**
     * Creates a {@code LivePreviewCard} with a {@link Localizable} eyebrow label.
     *
     * @param eyebrow localizable eyebrow caption
     */
    public LivePreviewCard(Localizable eyebrow) {
        this(LocalizationProvider.localize(eyebrow.getMessage(), eyebrow.getMessageCode()));
    }

    // ── Eyebrow ───────────────────────────────────────────────────────────

    /**
     * Sets the small uppercase eyebrow text.
     *
     * @param text eyebrow text; {@code null} clears it
     * @return this (fluent)
     */
    public LivePreviewCard setEyebrow(String text) {
        eyebrowSpan.setText(text != null ? text : "");
        return this;
    }

    // ── Title ─────────────────────────────────────────────────────────────

    /**
     * Sets the main title as plain text.
     *
     * @param title title text; {@code null} clears it
     * @return this (fluent)
     */
    public LivePreviewCard setHeader(String title) {
        titleDiv.removeAll();
        if (title != null && !title.isEmpty()) {
            titleDiv.add(new Span(title));
        }
        return this;
    }

    /**
     * Sets the title with an accent-coloured middle word or phrase.
     *
     * <p>Example: {@code setTitle("Acme", " Corp", " GmbH")} renders
     * "Acme" in the accent colour, flanked by plain text.</p>
     *
     * @param before plain text before the accent (may be null/empty)
     * @param accent the highlighted portion (rendered in brand accent colour)
     * @param after  plain text after the accent (may be null/empty)
     * @return this (fluent)
     */
    public LivePreviewCard setTitle(String before, String accent, String after) {
        titleDiv.removeAll();
        if (before != null && !before.isEmpty()) {
            titleDiv.add(new Span(before));
        }
        if (accent != null && !accent.isEmpty()) {
            Span accentSpan = new Span(accent);
            accentSpan.addClassName(CSS_ACCENT);
            titleDiv.add(accentSpan);
        }
        if (after != null && !after.isEmpty()) {
            titleDiv.add(new Span(after));
        }
        return this;
    }

    /**
     * Sets the title from an arbitrary {@link Component} for full rendering control.
     *
     * @param component the title component; {@code null} clears it
     * @return this (fluent)
     */
    public LivePreviewCard setTitle(Component component) {
        titleDiv.removeAll();
        if (component != null) {
            titleDiv.add(component);
        }
        return this;
    }

    // ── Subtitle ──────────────────────────────────────────────────────────

    /**
     * Sets the subtitle line (e.g. {@code "Design · Mid-market · Gold"}).
     *
     * @param subtitle subtitle text; {@code null} clears it
     * @return this (fluent)
     */
    public LivePreviewCard setSubtitle(String subtitle) {
        subtitleDiv.setText(subtitle != null ? subtitle : "");
        return this;
    }

    // ── Stats ─────────────────────────────────────────────────────────────

    /**
     * Adds a stat cell and returns a {@link Stat} handle for dynamic updates.
     *
     * @param label   label shown above the value (e.g. {@code "Status"})
     * @param value   initial value text (e.g. {@code "Draft"})
     * @param variant colour variant applied to the value
     * @return a handle to update label/value/variant at runtime
     */
    public Stat addStat(String label, String value, StatVariant variant) {
        Stat stat = new Stat(label, value, variant);
        statsGrid.add(stat.root);
        return stat;
    }

    /**
     * Adds a stat cell with the default (no highlight) variant.
     *
     * @param label label text
     * @param value initial value text
     * @return a handle to update the stat at runtime
     */
    public Stat addStat(String label, String value) {
        return addStat(label, value, StatVariant.NORMAL);
    }

    /**
     * Removes all stat cells from the stats grid.
     *
     * @return this (fluent)
     */
    public LivePreviewCard clearStats() {
        statsGrid.removeAll();
        return this;
    }

    // ── StatVariant ───────────────────────────────────────────────────────

    /**
     * Colour variant applied to a {@link Stat} value text.
     */
    public enum StatVariant {

        /** White value text — neutral data. */
        NORMAL(null),

        /** Amber/yellow value text — draft, pending, on hold. */
        WARNING("lpc__stat--warn"),

        /** Green value text — active, confirmed, completed. */
        SUCCESS("lpc__stat--ok"),

        /** Red value text — error, rejected, failed. */
        DANGER("lpc__stat--err");

        private final String cssClass;

        StatVariant(String cssClass) {
            this.cssClass = cssClass;
        }

        String cssClass() {
            return cssClass;
        }
    }

    // ── Stat handle ───────────────────────────────────────────────────────

    /**
     * Handle to a single stat cell inside a {@link LivePreviewCard}.
     *
     * <p>Obtained from {@link LivePreviewCard#addStat}. Use it to push reactive
     * updates from form value-change listeners without rebuilding the card.</p>
     *
     * <pre>{@code
     * Stat statusStat = card.addStat("Status", "Draft", StatVariant.WARNING);
     * // Later, when form value changes:
     * statusStat.setValue("Active").setVariant(StatVariant.SUCCESS);
     * }</pre>
     */
    public static final class Stat {

        private final Div  root;
        private final Span labelSpan;
        private final Span valueSpan;
        private StatVariant currentVariant;

        private Stat(String label, String value, StatVariant variant) {
            root = new Div();
            root.addClassName(CSS_STAT);

            labelSpan = new Span(label != null ? label : "");
            labelSpan.addClassName(CSS_STAT_LBL);

            valueSpan = new Span(value != null ? value : "—");
            valueSpan.addClassName(CSS_STAT_VAL);

            root.add(labelSpan, valueSpan);
            applyVariant(variant != null ? variant : StatVariant.NORMAL);
        }

        /**
         * Updates the displayed value text.
         *
         * @param value new value; {@code null} renders as "—"
         * @return this (fluent)
         */
        public Stat setValue(String value) {
            valueSpan.setText(value != null ? value : "—");
            return this;
        }

        /**
         * Updates the label text.
         *
         * @param label new label text
         * @return this (fluent)
         */
        public Stat setLabel(String label) {
            labelSpan.setText(label != null ? label : "");
            return this;
        }

        /**
         * Changes the colour variant of this stat's value.
         *
         * @param variant new variant; {@code null} → {@link StatVariant#NORMAL}
         * @return this (fluent)
         */
        public Stat setVariant(StatVariant variant) {
            if (currentVariant != null && currentVariant.cssClass() != null) {
                root.removeClassName(currentVariant.cssClass());
            }
            applyVariant(variant != null ? variant : StatVariant.NORMAL);
            return this;
        }

        private void applyVariant(StatVariant variant) {
            this.currentVariant = variant;
            if (variant.cssClass() != null) {
                root.addClassName(variant.cssClass());
            }
        }
    }
}
