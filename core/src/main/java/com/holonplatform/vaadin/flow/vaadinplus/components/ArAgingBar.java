package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.Panel;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A generic proportional-bar card widget that displays a horizontal multi-segment
 * bar, a colour-coded legend, and up to three summary stats in the footer.
 *
 * <p>Built on top of {@link Panel} (card shell), {@link Header} (icon + title + legend),
 * and {@link Footer} (left / centre / right summary stats).</p>
 *
 * <p>The visual urgency of the card as a whole is expressed through a {@link Variant}
 * (following the same contract as {@link Alert.Variant}). Each bar segment's colour
 * is fully user-defined per segment — no predefined bucket palette is assumed.</p>
 *
 * <h3>Structure</h3>
 * <pre>
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  [icon]  Title                    ■ Label A  ■ Label B  ■ Label C  …   │  ← Header
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  ███████████████████████  ██████████████  ████████                      │  ← track bar
 * │  Segment A label          Segment B label  Segment C label              │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  Left stat            Centre stat                   Right stat          │  ← Footer
 * └──────────────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>AR Aging example</h3>
 * <pre>{@code
 * ArAgingBar bar = ArAgingBarBuilder.create()
 *     .title("AR aging · Helix Robotics")
 *     .icon("€")
 *     .variant(ArAgingBar.Variant.INFO)
 *     .segment("Current",     "Current · €35K",  56, ArAgingBar.Variant.SUCCESS)
 *     .segment("1–30d",      "1-30d · €18.7K",  30, ArAgingBar.Variant.INFO)
 *     .segment("31–60d",     "31-60d · €8.7K",  14, ArAgingBar.Variant.WARNING)
 *     .leftStat("€0 owed")
 *     .centerStat("€0 overdue")
 *     .rightStat("€62.4K total open")
 *     .build();
 * }</pre>
 *
 * <h3>Pipeline example</h3>
 * <pre>{@code
 * ArAgingBar bar = ArAgingBarBuilder.create()
 *     .title("Pipeline · Q3 2026")
 *     .icon("⚡")
 *     .variant(ArAgingBar.Variant.SUCCESS)
 *     .segment("Prospect",    "Prospect · 8",    20, ArAgingBar.Variant.DEFAULT)
 *     .segment("Qualified",   "Qualified · 14",  35, ArAgingBar.Variant.INFO)
 *     .segment("Negotiation", "Negotiation · 18",45, ArAgingBar.Variant.SUCCESS)
 *     .leftStat("40 deals")
 *     .centerStat("€1.2M pipeline")
 *     .rightStat("72% avg prob")
 *     .build();
 * }</pre>
 *
 * <h3>Run-time mutation</h3>
 * <pre>{@code
 * bar.setVariant(ArAgingBar.Variant.WARNING);
 * bar.setTitle("AR aging · Lumen Health");
 * bar.setSegments(List.of(
 *     new ArAgingBar.Segment("Current", "Current · €24K", 40, ArAgingBar.Variant.SUCCESS),
 *     new ArAgingBar.Segment("61–90d",  "61-90d · €36K",  60, ArAgingBar.Variant.DANGER)
 * ));
 * bar.setLeftStat("€0 owed");
 * bar.setCenterStat("€36K overdue");
 * bar.setRightStat("€60K total open");
 * }</pre>
 *
 * <h3>CSS</h3>
 * {@code META-INF/resources/ar-aging-bar.css} — BEM root: {@code .arb}
 *
 */
@StyleSheet("context://ar-aging-bar.css")
public class ArAgingBar extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    static final String CSS_ROOT = "arb";

    // ── Variant ────────────────────────────────────────────────────────────

    /**
     * Visual variant — controls the card border accent and the icon badge colour.
     * Follows the same CSS-modifier convention as {@link Alert.Variant}.
     */
    public enum Variant implements Serializable {

        /** Neutral / no emphasis. */
        DEFAULT("default",  "var(--text-mute, #5b6878)"),
        /** Healthy / on-track. */
        SUCCESS("success",  "var(--success,   #2e9a6a)"),
        /** Attention required. */
        WARNING("warning",  "var(--warn,      #b8860b)"),
        /** Critical / action needed immediately. */
        DANGER( "danger",   "var(--danger,    #c0392b)"),
        /** Informational. */
        INFO(   "info",     "var(--primary,   #1576d3)");

        private final String cssModifier;
        private final String color;

        Variant(String cssModifier, String color) {
            this.cssModifier = cssModifier;
            this.color       = color;
        }

        /** CSS modifier token (e.g. {@code "warning"}). */
        public String getCssModifier() {
            return cssModifier;
        }

        /**
         * CSS colour expression suitable for use as a {@code background} value
         * (e.g. {@code "var(--success, #2e9a6a)"}). Used for segment bars and
         * legend swatches.
         *
         * @return the CSS colour string (never null)
         */
        public String getColor() {
            return color;
        }

        /** {@code true} for the neutral {@link #DEFAULT} variant. */
        public boolean isDefault() {
            return this == DEFAULT;
        }

        /**
         * Full BEM modifier class using {@code "--"} as separator.
         *
         * @param componentPrefix BEM block prefix (e.g. {@code "arb"})
         * @return the full CSS class name
         */
        public String getCssClass(String componentPrefix) {
            return getCssClass(componentPrefix, "--");
        }

        /**
         * Full BEM modifier class with a custom separator.
         *
         * @param componentPrefix BEM block prefix
         * @param separator       separator between prefix and modifier
         * @return the full CSS class name
         */
        public String getCssClass(String componentPrefix, String separator) {
            return componentPrefix + separator + cssModifier;
        }

        /**
         * CSS class for this component's root (shorthand for {@code getCssClass("arb")}).
         *
         * @return the full CSS class name
         */
        public String getCssClass() {
            return getCssClass(CSS_ROOT);
        }
    }

    // ── Segment data ───────────────────────────────────────────────────────

    /**
     * An immutable definition of one proportional segment in the bar.
     *
     * <p>The {@code color} uses the same {@link Variant} palette as the card itself,
     * so developers pick from a controlled set of semantic colours rather than
     * writing raw CSS strings.</p>
     *
     * @param key     label shown in the header legend (e.g. {@code "Current"})
     * @param value   optional text rendered centred inside the bar segment
     *                (e.g. {@code "Current · €35K"}); {@code null} or blank = no label
     * @param percent flex-basis percentage (0–100); segments should sum to 100
     * @param color   semantic colour variant for the swatch and the bar segment
     */
    public record Segment(String key, String value, double percent, Variant color)
            implements Serializable {}

    // ── Mutable DOM references ─────────────────────────────────────────────

    private Variant   currentVariant;
    private Span      iconSpan;
    private final Span titleSpan;
    private final Div  trackDiv;
    private final Div  legendDiv;
    private final Span leftStatSpan;
    private final Span centerStatSpan;
    private final Span rightStatSpan;

    // ── Constructors ───────────────────────────────────────────────────────

    /**
     * Creates a new {@link ArAgingBar} with the given variant, assembling its DOM internally.
     *
     * @param variant the initial visual variant (may be {@code null} for {@link Variant#DEFAULT})
     */
    public ArAgingBar(Variant variant) {
        this.iconSpan = new Span("■");
        iconSpan.addClassName("arb__icon");

        this.titleSpan = new Span("");
        titleSpan.addClassName("arb__heading-text");

        Div headingDiv = new Div(iconSpan, titleSpan);
        headingDiv.addClassName("arb__heading");

        this.legendDiv = new Div();
        legendDiv.addClassName("arb__legend");

        Header header = new Header("");
        header.setHeadingFontSize(null);
        header.setHeading(headingDiv);
        header.setActions(legendDiv);
        header.withoutSticky();

        this.trackDiv = new Div();
        trackDiv.addClassName("arb__track");
        trackDiv.getElement().setAttribute("role", "img");
        trackDiv.getElement().setAttribute("aria-label", buildTrackAriaLabel(null));

        this.leftStatSpan   = new Span("");
        this.centerStatSpan = new Span("");
        this.rightStatSpan  = new Span("");
        leftStatSpan.addClassName("arb__stat");
        centerStatSpan.addClassName("arb__stat");
        rightStatSpan.addClassName("arb__stat");

        Div footerDiv = new Div(leftStatSpan, centerStatSpan, rightStatSpan);
        footerDiv.addClassName("arb__footer");

        Panel panel = new Panel();
        panel.setHeader(header);
        panel.setContent(trackDiv, footerDiv);

        addClassName(CSS_ROOT);
        getElement().setAttribute("role", "region");
        add(panel);
        this.currentVariant = variant;
        if (variant != null) addClassName(variant.getCssClass());
        refreshRootAriaLabel(titleSpan.getText());
    }

    /** Package-private: accepts pre-assembled DOM elements — used by the legacy builder. */
    ArAgingBar(Variant variant, Span titleSpan, Div trackDiv, Div legendDiv,
               Span leftStatSpan, Span centerStatSpan, Span rightStatSpan,
               Panel panel) {
        addClassName(CSS_ROOT);
        // A11Y: mark as a named landmark region so screen readers can jump to it
        getElement().setAttribute("role", "region");
        this.titleSpan      = titleSpan;
        this.trackDiv       = trackDiv;
        this.legendDiv      = legendDiv;
        this.leftStatSpan   = leftStatSpan;
        this.centerStatSpan = centerStatSpan;
        this.rightStatSpan  = rightStatSpan;
        this.iconSpan       = new Span("■"); // default; builder may override via setIcon after construction
        add(panel);
        // apply initial variant without going through setVariant to avoid null-check on currentVariant
        this.currentVariant = variant;
        if (variant != null) {
            addClassName(variant.getCssClass());
        }
        // A11Y: initial region label derived from title (track label was set by the builder)
        refreshRootAriaLabel(titleSpan.getText());
    }

    // ── Icon API ───────────────────────────────────────────────────────────

    /**
     * Updates the icon badge character displayed in the header.
     *
     * @param icon icon text; {@code null} resets to the default {@code "■"}
     * @return this (fluent)
     */
    public ArAgingBar setIcon(String icon) {
        iconSpan.setText(icon != null ? icon : "■");
        return this;
    }

    // ── Segment API (additive) ─────────────────────────────────────────────

    /**
     * Appends a single segment to both the track bar and the header legend.
     *
     * @param segment the segment to add; {@code null} is silently ignored
     * @return this (fluent)
     */
    public ArAgingBar addSegment(Segment segment) {
        if (segment != null) {
            trackDiv.add(buildSegmentDiv(segment));
            legendDiv.add(buildLegendItem(segment));
        }
        return this;
    }

    // ── Getter API ─────────────────────────────────────────────────────────

    /**
     * Returns the current title text.
     *
     * @return title (may be empty but never {@code null})
     */
    public String getBarTitle() {
        return titleSpan.getText();
    }

    /**
     * Returns the current left footer stat text.
     *
     * @return left stat (may be empty but never {@code null})
     */
    public String getLeftStat() {
        return leftStatSpan.getText();
    }

    /**
     * Returns the current centre footer stat text.
     *
     * @return centre stat (may be empty but never {@code null})
     */
    public String getCenterStat() {
        return centerStatSpan.getText();
    }

    /**
     * Returns the current right footer stat text.
     *
     * @return right stat (may be empty but never {@code null})
     */
    public String getRightStat() {
        return rightStatSpan.getText();
    }

    /**
     * Returns the current visual variant.
     *
     * @return the current {@link Variant} (never {@code null} after construction)
     */
    public Variant getVariant() {
        return currentVariant;
    }

    /**
     * Changes the visual variant, swapping the corresponding CSS modifier class.
     *
     * @param variant the new variant (not null)
     * @return this (fluent)
     */
    public ArAgingBar setVariant(Variant variant) {
        if (this.currentVariant != null) {
            removeClassName(this.currentVariant.getCssClass());
        }
        this.currentVariant = variant;
        if (variant != null) {
            addClassName(variant.getCssClass());
        }
        return this;
    }

    // ── Title API ──────────────────────────────────────────────────────────

    /**
     * Updates the title text displayed next to the icon in the header.
     *
     * @param title new title; {@code null} clears the text
     * @return this (fluent)
     */
    public ArAgingBar setBarTitle(String title) {
        titleSpan.setText(title != null ? title : "");
        refreshRootAriaLabel(title);     // A11Y: keep region label in sync
        return this;
    }

    // ── Segment API ────────────────────────────────────────────────────────

    /**
     * Re-renders both the proportional track bar and the header legend with the
     * supplied segments.
     *
     * <p>Segments are drawn left-to-right in list order. The {@code percent}
     * values are used as {@code flex-basis} percentages; ideally they should sum
     * to 100 (any remainder leaves transparent space at the trailing edge).</p>
     *
     * @param segments ordered list of segments; {@code null} or empty clears both bar and legend
     * @return this (fluent)
     */
    public ArAgingBar setSegments(List<Segment> segments) {
        trackDiv.removeAll();
        legendDiv.removeAll();
        if (segments != null) {
            for (Segment seg : segments) {
                if (seg != null) {
                    trackDiv.add(buildSegmentDiv(seg));
                    legendDiv.add(buildLegendItem(seg));
                }
            }
        }
        // A11Y: regenerate the track's img aria-label to reflect the new segments
        trackDiv.getElement().setAttribute("aria-label", buildTrackAriaLabel(segments));
        return this;
    }

    // ── Footer stat API ────────────────────────────────────────────────────

    /**
     * Updates the left footer stat (e.g. {@code "€0 owed"}).
     *
     * @param text stat text; {@code null} clears the span
     * @return this (fluent)
     */
    public ArAgingBar setLeftStat(String text) {
        leftStatSpan.setText(text != null ? text : "");
        return this;
    }

    /**
     * Updates the centre footer stat (e.g. {@code "€0 overdue"}).
     *
     * @param text stat text; {@code null} clears the span
     * @return this (fluent)
     */
    public ArAgingBar setCenterStat(String text) {
        centerStatSpan.setText(text != null ? text : "");
        return this;
    }

    /**
     * Updates the right footer stat (e.g. {@code "€62.4K total open"}).
     *
     * @param text stat text; {@code null} clears the span
     * @return this (fluent)
     */
    public ArAgingBar setRightStat(String text) {
        rightStatSpan.setText(text != null ? text : "");
        return this;
    }

    // ── Internal helpers ───────────────────────────────────────────────────

    /**
     * Updates the root element's {@code aria-label} using the current title.
     * Falls back to the localised "AR aging bar" string when the title is blank.
     */
    private void refreshRootAriaLabel(String title) {
        String label = (title != null && !title.isBlank())
                ? title
                : LocalizationProvider.localize("AR aging bar", "ar_aging_bar.region_aria");
        getElement().setAttribute("aria-label", label);
    }

    /**
     * Computes an {@code aria-label} for the proportional track bar from the
     * provided segment list.
     *
     * <p>Example output: {@code "Proportional bar: Current 56%, 1–30d 30%, 31–60d 14%"}</p>
     *
     * @param segments segment list (may be {@code null} or empty)
     * @return the computed aria-label string
     */
    static String buildTrackAriaLabel(List<Segment> segments) {
        if (segments == null || segments.isEmpty()) {
            return LocalizationProvider.localize("Proportional bar", "ar_aging_bar.track_aria_empty");
        }
        String summary = segments.stream()
                .filter(s -> s != null)
                .map(s -> (s.key() != null ? s.key() : "") + " " + (int) s.percent() + "%")
                .collect(Collectors.joining(", "));
        return MessageFormat.format(
                LocalizationProvider.localize("Proportional bar: {0}", "ar_aging_bar.track_aria"),
                summary);
    }

    /** Builds a coloured segment div for the track bar. */
    static Div buildSegmentDiv(Segment seg) {
        Div div = new Div();
        div.addClassName("arb__seg");
        div.addClassName("arb__seg--" + seg.color().getCssModifier());
        div.getStyle().set("flex", "0 0 " + seg.percent() + "%");
        if (seg.value() != null && !seg.value().isBlank()) {
            Span lbl = new Span(seg.value());
            lbl.addClassName("arb__seg-lbl");
            div.add(lbl);
        }
        return div;
    }

    /** Builds one swatch + label item for the header legend. */
    static Div buildLegendItem(Segment seg) {
        Div item = new Div();
        item.addClassName("arb__legend-item");
        Div swatch = new Div();
        swatch.addClassNames("arb__legend-swatch", "arb__legend-swatch--" + seg.color().getCssModifier());
        // A11Y: the colour swatch is purely decorative — the text label carries the meaning
        swatch.getElement().setAttribute("aria-hidden", "true");
        item.add(swatch, new Span(seg.key() != null ? seg.key() : ""));
        return item;
    }
}
