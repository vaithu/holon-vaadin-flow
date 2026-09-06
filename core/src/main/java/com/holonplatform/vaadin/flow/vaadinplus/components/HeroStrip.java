package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A gradient "hero strip" card that displays an optional thumbnail/name/meta header row,
 * an optional row of status tag pills, and N key metric cells side by side in equally-wide
 * columns.
 *
 * <p>The strip itself is the container — there is no Panel/Header/Footer wrapping.
 * The optional header row shows an icon thumbnail (with an optional small corner ribbon
 * badge, e.g. {@code "NEW"} or {@code "T1"}) next to a name (optionally starred) and a
 * muted meta subtitle. The optional tags row is a wrapping list of small colour-coded
 * pills (e.g. {@code "● Active"}, {@code "VIP"}). Each metric cell shows a small label row
 * (optionally with an animated pulse dot), a large monospace value, and an optional
 * sub-label beneath the value.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌─────────��────────────────────────────────────────────────────┐
 * │  [ico] Helix Robotics SE ★                                    │
 * │        C-2026-0023 · Munich · since 1.9 yr                    │
 * │  ● Active   ★ T1   EMEA · DACH   VIP                          │
 * ├──────────────────────────────────────────────────────────────┤
 * │  Health    │  Open AR      │  Open SOs   │  ARR              │
 * │  A+        │  €14.8K       │  3          │  €1.84M           │
 * │  ★ 4.7     │  14d          │  €48.2K     │  +12%             │
 * └──────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Customer 360 header example</h3>
 * <pre>{@code
 * Components.heroStrip()
 *     .variant(HeroStrip.Variant.INFO)
 *     .header(h -> h.thumbIcon(VaadinIcon.BUILDING.create())
 *         .ribbon("T1")
 *         .name("Helix Robotics SE")
 *         .starred(true)
 *         .meta("C-2026-0023 · Munich · since 1.9 yr"))
 *     .tag("● Active", HeroStrip.TagVariant.OK)
 *     .tag("★ T1", HeroStrip.TagVariant.PRI)
 *     .tag("EMEA · DACH", HeroStrip.TagVariant.PRIM)
 *     .tag("VIP", HeroStrip.TagVariant.VIOLET)
 *     .cell(c -> c.header("Health").content("A+").footer("★ 4.7").valueVariant(HeroStrip.ValueVariant.OK))
 *     .cell(c -> c.header("Open AR").content("€14.8K").footer("14d").valueVariant(HeroStrip.ValueVariant.ALERT))
 *     .cell(c -> c.header("Open SOs").content("3").footer("€48.2K"))
 *     .cell(c -> c.header("ARR").content("€1.84M").footer("+12%"))
 *     .build();
 * }</pre>
 *
 * <h3>Runtime mutation</h3>
 * <pre>{@code
 * strip.setVariant(HeroStrip.Variant.DANGER);
 * strip.setHeader(new HeroStrip.Header(null, "NEW", "New customer", false, "will assign C-2026-0343"));
 * strip.setTags(List.of(new HeroStrip.Tag("● DRAFT", HeroStrip.TagVariant.WARN)));
 * strip.setCells(List.of(
 *     new HeroStrip.Cell("Alert", "3 critical", "immediate action", true, HeroStrip.ValueVariant.ALERT)
 * ));
 * }</pre>
 *
 * <h3>CSS</h3>
 * {@code META-INF/resources/hero-strip.css} — BEM root: {@code .hstrip}
 *
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
     * @param icon         optional icon component rendered before the label (horizontal layout); {@code null} = no icon
     */
    public record Cell(String header, String content, String footer, boolean pulse, ValueVariant valueVariant, Component icon)
            implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** Compact constructor — normalises {@code null} valueVariant to DEFAULT. */
        public Cell {
            if (valueVariant == null) valueVariant = ValueVariant.DEFAULT;
        }

        /**
         * Convenience constructor without icon (backward compatibility).
         *
         * @param header       small uppercase label
         * @param content      large monospace value
         * @param footer       small sub-label or {@code null}
         * @param pulse        whether to show animated dot
         * @param valueVariant colour variant or {@code null}
         */
        public Cell(String header, String content, String footer, boolean pulse, ValueVariant valueVariant) {
            this(header, content, footer, pulse, valueVariant, null);
        }
    }

    // ── TagVariant (h-tags pill colour) ─────────────────────────────────────

    /**
     * Colour variant for a {@link Tag} pill rendered in the strip's tags row.
     */
    public enum TagVariant implements Serializable {

        /** Green — active / positive status (e.g. {@code "● Active"}). */
        OK("ok"),
        /** Amber/gold — priority / tier marker (e.g. {@code "★ T1"}). */
        PRI("pri"),
        /** Blue — primary informational (e.g. region). */
        PRIM("prim"),
        /** Violet — special marker (e.g. {@code "VIP"}). */
        VIOLET("violet"),
        /** Amber — warning / attention needed (e.g. overdue AR, draft state). */
        WARN("warn"),
        /** Warm gold gradient with dark text — eye-catching highlight (e.g. {@code "🔥 Hot"}). */
        HOT("hot");

        @Serial
        private static final long serialVersionUID = 1L;

        private final String cssModifier;

        TagVariant(String cssModifier) {
            this.cssModifier = cssModifier;
        }

        /** CSS modifier token (e.g. {@code "ok"}). */
        public String getCssModifier() {
            return cssModifier;
        }
    }

    /**
     * Immutable definition of one pill in the strip's tags row (below the header, above the metrics).
     *
     * @param text    the tag text, e.g. {@code "● Active"} or {@code "VIP"}
     * @param variant colour variant; {@code null} defaults to {@link TagVariant#PRIM}
     */
    public record Tag(String text, TagVariant variant) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** Compact constructor — normalises {@code null} variant to {@link TagVariant#PRIM}. */
        public Tag {
            if (variant == null) variant = TagVariant.PRIM;
        }
    }

    // ── Header (thumb + ribbon + name + meta) ───────────────────────────────

    /**
     * Immutable definition of the strip's header row: a thumbnail icon (optionally carrying a small
     * corner ribbon badge) next to a name/title (optionally starred) and a muted meta subtitle.
     *
     * @param thumbIcon the icon rendered inside the thumbnail box; {@code null} = no thumbnail
     * @param ribbon    small corner badge text on the thumbnail (e.g. {@code "NEW"}, {@code "T1"});
     *                  {@code null} or blank = hidden
     * @param name      the main title text (e.g. {@code "Helix Robotics SE"})
     * @param starred   when {@code true}, a gold star is rendered right after the name
     * @param meta      muted monospace subtitle below the name (e.g. {@code "C-2026-0023 · Munich"});
     *                  {@code null} or blank = hidden
     */
    public record Header(Component thumbIcon, String ribbon, String name, boolean starred, String meta)
            implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    // ── Mutable state ───────────────────────────────────────────────────────

    private Variant currentVariant;
    private boolean responsive;
    private boolean wideFirstCell;
    private Header header;
    private final List<Tag> tags = new ArrayList<>();
    private final List<Cell> cellList = new ArrayList<>();

    // ── Constructor (use HeroStripBuilder or direct construction) ─────────

    /**
     * Convenience constructor — creates an empty strip with the given variant.
     * Add cells via {@link #addCell(Cell)} or replace all via {@link #setCells(List)}.
     *
     * @param variant the gradient variant (may be {@code null} for {@link Variant#DEFAULT})
     */
    public HeroStrip(Variant variant) {
        this(variant != null ? variant : Variant.DEFAULT, null, null, null, false);
    }

    /**
     * Creates a strip with cells only (no header/tags row) — kept for backward compatibility.
     *
     * @param variant    the gradient variant (may be {@code null} for {@link Variant#DEFAULT})
     * @param cells      ordered list of metric cells
     * @param responsive whether responsive wrapping is enabled
     */
    public HeroStrip(Variant variant, List<Cell> cells, boolean responsive) {
        this(variant, null, null, cells, responsive);
    }

    /**
     * Creates a fully-featured strip: optional header row, optional tags row and metric cells.
     *
     * @param variant    the gradient variant (may be {@code null} for {@link Variant#DEFAULT})
     * @param header     the header row definition; {@code null} = no header row rendered
     * @param tags       ordered list of tag pills; {@code null} or empty = no tags row rendered
     * @param cells      ordered list of metric cells; {@code null} or empty = no metrics grid rendered
     * @param responsive whether responsive wrapping is enabled
     */
    public HeroStrip(Variant variant, Header header, List<Tag> tags, List<Cell> cells, boolean responsive) {
        addClassName(CSS_ROOT);
        // A11Y: mark as a named landmark region
        getElement().setAttribute("role", "region");
        getElement().setAttribute("aria-label",
                LocalizationProvider.localize("Key metrics", "hero_strip.region_aria"));
        this.currentVariant = variant != null ? variant : Variant.DEFAULT;
        addClassName(this.currentVariant.getCssClass());
        this.responsive = responsive;
        if (responsive) addClassName("hstrip--responsive");
        this.header = header;
        if (tags != null) this.tags.addAll(tags);
        if (cells != null) this.cellList.addAll(cells);
        render();
    }

    // ── Responsive API ───────────────────────────────��─────────────────────

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
        applyMetricsColumns();
        return this;
    }

    // ── Wide first column API ───────────────────────────────────────────────

    /**
     * Returns whether the first metric cell is rendered wider ({@code 1.4fr}) than the others.
     *
     * @return {@code true} when the wide-first-column layout is active
     */
    public boolean isWideFirstCell() {
        return wideFirstCell;
    }

    /**
     * Enables or disables a wider first metric cell (e.g. for a longer label/value such as
     * {@code "Open pipeline · €182K"}), rendering {@code grid-template-columns: 1.4fr 1fr ... 1fr}
     * instead of equally-wide columns. No-op in responsive mode, where auto-fit / minmax takes over.
     *
     * @param wideFirstCell {@code true} to widen the first cell
     * @return this (fluent)
     */
    public HeroStrip setWideFirstCell(boolean wideFirstCell) {
        this.wideFirstCell = wideFirstCell;
        applyMetricsColumns();
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

    // ── Header API ─────────────────────────────────────────────────────────

    /**
     * Returns the current header row definition, if any.
     *
     * @return the current {@link Header}, or {@code null} if no header row is set
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets (or clears) the header row: thumbnail icon + optional ribbon badge, name (optionally
     * starred) and meta subtitle.
     *
     * @param header the header definition; {@code null} removes the header row
     * @return this (fluent)
     */
    public HeroStrip setHeader(Header header) {
        this.header = header;
        render();
        return this;
    }

    // ── Tags API ───────────────────────────────────────────────────────────

    /**
     * Returns an unmodifiable view of the current tag pills.
     *
     * @return the current tags, in rendering order
     */
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Appends a single tag pill to the tags row.
     *
     * @param tag the tag to append; {@code null} is silently ignored
     * @return this (fluent)
     */
    public HeroStrip addTag(Tag tag) {
        if (tag != null) {
            tags.add(tag);
            render();
        }
        return this;
    }

    /**
     * Replaces all tag pills with the supplied list.
     *
     * @param tags ordered list of tags; {@code null} or empty clears the tags row
     * @return this (fluent)
     */
    public HeroStrip setTags(List<Tag> tags) {
        this.tags.clear();
        if (tags != null) this.tags.addAll(tags);
        render();
        return this;
    }

    // ── Cells API ──────────────────────────────────────────────────────────

    /**
     * Appends a single cell to the strip and updates the column grid accordingly.
     *
     * @param cell the cell to append; {@code null} is silently ignored
     * @return this (fluent)
     */
    public HeroStrip addCell(Cell cell) {
        if (cell != null) {
            cellList.add(cell);
            render();
        }
        return this;
    }

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
        this.cellList.clear();
        if (cells != null) this.cellList.addAll(cells);
        render();
        return this;
    }

    // ── Internal helpers ───────────────────────────────────────────────────

    private Div metricsDiv;

    /**
     * Fully rebuilds the strip content (header row, tags row, metrics grid) from the current state.
     */
    private void render() {
        removeAll();
        metricsDiv = null;
        if (header != null) {
            add(buildHeaderRowDiv(header));
        }
        if (!tags.isEmpty()) {
            add(buildTagsRowDiv(tags));
        }
        if (!cellList.isEmpty()) {
            metricsDiv = buildMetricsDiv();
            for (Cell cell : cellList) {
                if (cell != null) metricsDiv.add(buildCellDiv(cell));
            }
            add(metricsDiv);
            applyMetricsColumns();
        }
    }

    /**
     * Recomputes the inline {@code grid-template-columns} style on the metrics container, based on
     * the current cell count (and the {@link #isWideFirstCell()} flag). No-op in responsive mode
     * (CSS auto-fit takes over) or when no metrics are rendered.
     */
    private void applyMetricsColumns() {
        if (metricsDiv == null) return;
        if (!responsive) {
            if (wideFirstCell && cellList.size() > 1) {
                metricsDiv.getStyle().set("grid-template-columns",
                        "1.4fr repeat(" + (cellList.size() - 1) + ", 1fr)");
            } else {
                metricsDiv.getStyle().set("grid-template-columns", "repeat(" + cellList.size() + ", 1fr)");
            }
        } else {
            metricsDiv.getStyle().remove("grid-template-columns");
        }
    }

    /**
     * Builds the container for the metrics grid (a row of {@link #buildCellDiv(Cell)} tiles).
     *
     * @return the assembled {@link Div} element
     */
    private static Div buildMetricsDiv() {
        Div div = new Div();
        div.addClassName("hstrip__metrics");
        return div;
    }

    /**
     * Builds the header row: thumbnail (icon + optional ribbon badge) and body (name + meta).
     *
     * @param header header data record (not null)
     * @return the assembled {@link Div} element
     */
    private static Div buildHeaderRowDiv(Header header) {
        Div row = new Div();
        row.addClassName("hstrip__row");

        Div thumb = new Div();
        thumb.addClassName("hstrip__thumb");
        if (header.thumbIcon() != null) {
            thumb.add(header.thumbIcon());
        }
        if (header.ribbon() != null && !header.ribbon().isBlank()) {
            Div ribbon = new Div();
            ribbon.addClassName("hstrip__thumb-ribbon");
            ribbon.setText(header.ribbon());
            thumb.add(ribbon);
        }
        row.add(thumb);

        Div body = new Div();
        body.addClassName("hstrip__body");

        Div nameDiv = new Div();
        nameDiv.addClassName("hstrip__name");
        nameDiv.add(new Span(header.name() != null ? header.name() : ""));
        if (header.starred()) {
            Span star = new Span("★");
            star.addClassName("hstrip__name-star");
            // A11Y: the star is decorative, the "starred" semantics belong to the name text/aria-label
            star.getElement().setAttribute("aria-hidden", "true");
            nameDiv.add(star);
        }
        body.add(nameDiv);

        if (header.meta() != null && !header.meta().isBlank()) {
            Div metaDiv = new Div();
            metaDiv.addClassName("hstrip__meta");
            metaDiv.setText(header.meta());
            body.add(metaDiv);
        }
        row.add(body);

        return row;
    }

    /**
     * Builds the tags row: a wrapping list of {@code hstrip__tag} pills.
     *
     * @param tags ordered list of tags (not null, not empty)
     * @return the assembled {@link Div} element
     */
    private static Div buildTagsRowDiv(List<Tag> tags) {
        Div tagsRow = new Div();
        tagsRow.addClassName("hstrip__tags");
        for (Tag tag : tags) {
            if (tag == null) continue;
            Span pill = new Span(tag.text() != null ? tag.text() : "");
            pill.addClassName("hstrip__tag");
            pill.addClassName("hstrip__tag--" + tag.variant().getCssModifier());
            tagsRow.add(pill);
        }
        return tagsRow;
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

        // Apply horizontal layout if icon is present
        if (cell.icon() != null) {
            div.addClassName("hstrip__cell--horizontal");
        }

        // A11Y: each cell is a labelled group so screen readers announce
        //       "Open pipeline group" and then read the value and footer naturally
        div.getElement().setAttribute("role", "group");
        if (cell.header() != null && !cell.header().isBlank()) {
            div.getElement().setAttribute("aria-label", cell.header());
        }

        // Icon (if present, rendered on the left)
        if (cell.icon() != null) {
            Div iconDiv = new Div();
            iconDiv.addClassName("hstrip__icon");
            iconDiv.add(cell.icon());
            div.add(iconDiv);
        }

        // Content wrapper (label, value, footer)
        Div contentDiv = new Div();
        contentDiv.addClassName("hstrip__cell-content");

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
        contentDiv.add(labelDiv);

        // Value (large mono)
        // When icon is present: footer is inline as <small>
        // When no icon: footer is a separate line below
        Div valueDiv = new Div();
        valueDiv.addClassName("hstrip__value");
        if (cell.valueVariant() != null && !cell.valueVariant().isDefault()) {
            valueDiv.addClassName("hstrip__value--" + cell.valueVariant().getCssModifier());
        }

        // Render value + optional inline footer
        if (cell.icon() != null && cell.footer() != null && !cell.footer().isBlank()) {
            // Icon mode: render footer inline as <small>
            valueDiv.add(new Span(cell.content() != null ? cell.content() : ""));
            Span smallFooter = new Span(" " + cell.footer());
            smallFooter.addClassName("hstrip__value-small");
            valueDiv.add(smallFooter);
        } else {
            // No icon: render value only (footer will be separate)
            valueDiv.setText(cell.content() != null ? cell.content() : "");
        }

        contentDiv.add(valueDiv);

        // Sub-label (optional, only when no icon)
        // When icon is present, footer is already inline in the value
        if (cell.icon() == null && cell.footer() != null && !cell.footer().isBlank()) {
            Div subDiv = new Div();
            subDiv.addClassName("hstrip__sub");
            subDiv.setText(cell.footer());
            contentDiv.add(subDiv);
        }

        div.add(contentDiv);
        return div;
    }
}
