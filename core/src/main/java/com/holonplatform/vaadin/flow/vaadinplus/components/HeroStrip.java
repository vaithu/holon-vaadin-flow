package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A horizontal gradient "hero strip" card that displays N key metric cells side by side
 * in equally-wide columns separated by translucent vertical dividers.
 *
 * <p>The strip itself is the container — there is no Panel/Header/Footer wrapping.
 * Each cell shows a small label row (optionally with an animated pulse dot), a large
 * monospace value, and an optional sub-label beneath the value.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌──────────────────────────────────────────────────────────────┐
 * │  ● Open pipeline  │  Booked YTD   │  AR balance             │
 * │     €182K         │    €624K      │    €62,400              │
 * │  4 deals · 80%    │  14 orders    │  3 open · on-time       │
 * └──────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Customer KPI example</h3>
 * <pre>{@code
 * Components.heroStrip()
 *     .variant(HeroStrip.Variant.INFO)
 *     .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals · 80% avg prob").pulse(true))
 *     .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
 *     .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
 *     .build();
 * }</pre>
 *
 * <h3>Runtime mutation</h3>
 * <pre>{@code
 * strip.setVariant(HeroStrip.Variant.DANGER);
 * strip.setCells(List.of(
 *     new HeroStrip.Cell("Alert", "3 critical", "immediate action", true, HeroStrip.ValueVariant.ALERT)
 * ));
 * }</pre>
 *
 * <h3>CSS</h3>
 * {@code META-INF/resources/hero-strip.css} — BEM root: {@code .hstrip}
 *
 * @see HeroStripBuilder
 */
@StyleSheet("context://hero-strip.css")
public class HeroStrip extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    static final String CSS_ROOT = "hstrip";

    // ── Variant (strip gradient background) ────────────────────────────────

    /**
     * Gradient background variant for the entire strip.
     */
    public enum Variant implements Serializable {

        /** Neutral dark gradient. */
        DEFAULT("default"),
        /** Blue gradient — default for customer strips. */
        INFO("info"),
        /** Green gradient. */
        SUCCESS("success"),
        /** Amber gradient. */
        WARNING("warning"),
        /** Red gradient. */
        DANGER("danger"),
        /** Violet gradient — default for contact strips. */
        VIOLET("violet");

        @Serial
        private static final long serialVersionUID = 1L;

        private final String cssModifier;

        Variant(String cssModifier) {
            this.cssModifier = cssModifier;
        }

        /** CSS modifier token (e.g. {@code "info"}). */
        public String getCssModifier() {
            return cssModifier;
        }

        /** {@code true} for the neutral {@link #DEFAULT} variant. */
        public boolean isDefault() {
            return this == DEFAULT;
        }

        /**
         * Full BEM modifier class using the given prefix and {@code "--"} as separator.
         *
         * @param prefix BEM block prefix (e.g. {@code "hstrip"})
         * @return the full CSS class name
         */
        public String getCssClass(String prefix) {
            return prefix + "--" + cssModifier;
        }

        /**
         * CSS class for this component's root (shorthand for {@code getCssClass("hstrip")}).
         *
         * @return the full CSS class name
         */
        public String getCssClass() {
            return getCssClass(CSS_ROOT);
        }
    }

    // ── ValueVariant (value text colour modifier) ──────────────────────────

    /**
     * Colour tint applied to the large value text inside a cell.
     */
    public enum ValueVariant implements Serializable {

        /** White — default value colour. */
        DEFAULT(""),
        /** Mint green — positive metric. */
        OK("ok"),
        /** Amber — attention needed. */
        ALERT("alert");

        @Serial
        private static final long serialVersionUID = 1L;

        private final String cssModifier;

        ValueVariant(String cssModifier) {
            this.cssModifier = cssModifier;
        }

        /** CSS modifier token (empty string for {@link #DEFAULT}). */
        public String getCssModifier() {
            return cssModifier;
        }

        /** {@code true} for the {@link #DEFAULT} variant (no extra CSS class). */
        public boolean isDefault() {
            return this == DEFAULT;
        }
    }

    // ── Cell data record ───────────────────────────────────────────────────

    /**
     * Immutable definition of one metric cell in the strip.
     *
     * @param header       small uppercase label (e.g. {@code "Open pipeline"})
     * @param content      large monospace value (e.g. {@code "€182K"})
     * @param footer       small sub-label below the value (e.g. {@code "4 active deals · 80% avg prob"});
     *                     {@code null} or blank = hidden
     * @param pulse        when {@code true}, an animated amber dot is rendered before the header
     * @param valueVariant colour tint applied to the content text; {@code null} defaults to {@link ValueVariant#DEFAULT}
     */
    public record Cell(String header, String content, String footer, boolean pulse, ValueVariant valueVariant)
            implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** Compact constructor — normalises {@code null} valueVariant to DEFAULT. */
        public Cell {
            if (valueVariant == null) valueVariant = ValueVariant.DEFAULT;
        }
    }

    // ── Mutable state ───────────────────────────────────────────────────────

    private Variant currentVariant;
    private boolean responsive;

    // ── Constructor (package-private — use HeroStripBuilder) ───────────────

    HeroStrip(Variant variant, List<Cell> cells, boolean responsive) {
        addClassName(CSS_ROOT);
        // A11Y: mark as a named landmark region
        getElement().setAttribute("role", "region");
        getElement().setAttribute("aria-label",
                LocalizationProvider.localize("Key metrics", "hero_strip.region_aria"));
        this.currentVariant = variant != null ? variant : Variant.DEFAULT;
        addClassName(this.currentVariant.getCssClass());
        this.responsive = responsive;
        if (responsive) addClassName("hstrip--responsive");
        renderCells(cells);
    }

    // ── Responsive API ─────────────────────────────────────────────────────

    /**
     * Returns whether responsive wrapping is enabled.
     *
     * @return {@code true} when the {@code hstrip--responsive} class is active
     */
    public boolean isResponsive() {
        return responsive;
    }

    /**
     * Enables or disables responsive wrapping.
     *
     * <p>When {@code true}, the {@code .hstrip--responsive} CSS class is added:
     * cells wrap to new rows on narrow viewports (auto-fit / minmax), vertical
     * dividers are hidden, and a 2-column grid is forced at ≤ 480 px.</p>
     *
     * @param responsive {@code true} to enable responsive wrapping
     * @return this (fluent)
     */
    public HeroStrip setResponsive(boolean responsive) {
        this.responsive = responsive;
        if (responsive) {
            addClassName("hstrip--responsive");
        } else {
            removeClassName("hstrip--responsive");
        }
        return this;
    }

    // ── Variant API ────────────────────────────────────────────────────────

    /**
     * Returns the current gradient variant.
     *
     * @return the current {@link Variant} (never {@code null} after construction)
     */
    public Variant getVariant() {
        return currentVariant;
    }

    /**
     * Changes the gradient variant, swapping the corresponding CSS modifier class.
     *
     * @param variant the new variant; {@code null} defaults to {@link Variant#DEFAULT}
     * @return this (fluent)
     */
    public HeroStrip setVariant(Variant variant) {
        if (currentVariant != null) removeClassName(currentVariant.getCssClass());
        this.currentVariant = variant != null ? variant : Variant.DEFAULT;
        addClassName(this.currentVariant.getCssClass());
        return this;
    }

    // ── Cells API ──────────────────────────────────────────────────────────

    /**
     * Re-renders all cells with the supplied list.
     *
     * <p>The number of columns is derived from the cell count — each call re-computes
     * the {@code grid-template-columns} inline style.</p>
     *
     * @param cells ordered list of cells; {@code null} or empty clears the strip content
     * @return this (fluent)
     */
    public HeroStrip setCells(List<Cell> cells) {
        removeAll();
        renderCells(cells);
        return this;
    }

    // ── Internal helpers ───────────────────────────────────────────────────

    private void renderCells(List<Cell> cells) {
        if (cells == null || cells.isEmpty()) return;
        // Only set inline columns for the fixed layout; responsive mode uses CSS auto-fit
        if (!responsive) {
            getStyle().set("grid-template-columns", "repeat(" + cells.size() + ", 1fr)");
        }
        for (Cell cell : cells) {
            if (cell != null) add(buildCellDiv(cell));
        }
    }

    /**
     * Builds the DOM subtree for one metric cell.
     *
     * @param cell cell data record (not null)
     * @return the assembled {@link Div} element
     */
    static Div buildCellDiv(Cell cell) {
        Div div = new Div();
        div.addClassName("hstrip__cell");
        // A11Y: each cell is a labelled group so screen readers announce
        //       "Open pipeline group" and then read the value and footer naturally
        div.getElement().setAttribute("role", "group");
        if (cell.header() != null && !cell.header().isBlank()) {
            div.getElement().setAttribute("aria-label", cell.header());
        }

        // Label row (optionally with pulse dot)
        Div labelDiv = new Div();
        labelDiv.addClassName("hstrip__label");
        if (cell.pulse()) {
            Span dot = new Span();
            dot.addClassName("hstrip__pulse");
            // A11Y: the pulsing dot is a decorative animation; hide from AT
            dot.getElement().setAttribute("aria-hidden", "true");
            labelDiv.add(dot);
        }
        labelDiv.add(new Span(cell.header() != null ? cell.header() : ""));
        div.add(labelDiv);

        // Value (large mono)
        Div valueDiv = new Div();
        valueDiv.addClassName("hstrip__value");
        if (cell.valueVariant() != null && !cell.valueVariant().isDefault()) {
            valueDiv.addClassName("hstrip__value--" + cell.valueVariant().getCssModifier());
        }
        valueDiv.setText(cell.content() != null ? cell.content() : "");
        div.add(valueDiv);

        // Sub-label (optional)
        if (cell.footer() != null && !cell.footer().isBlank()) {
            Div subDiv = new Div();
            subDiv.addClassName("hstrip__sub");
            subDiv.setText(cell.footer());
            div.add(subDiv);
        }

        return div;
    }
}
