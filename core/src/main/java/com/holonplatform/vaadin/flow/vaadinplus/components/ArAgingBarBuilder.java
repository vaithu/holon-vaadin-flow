package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.Panel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link ArAgingBar} — a generic proportional-bar card widget.
 *
 * <p>Follows the nested sub-builder pattern (like {@code PanelBuilder /
 * PanelConfigurator}) with {@code Consumer<>} overloads for each section so
 * call-sites read as a clean indented block:</p>
 *
 * <pre>{@code
 * // ── Consumer style (recommended) ─────────────────────────────────────────
 * Components.arAgingBar()
 *     .header(h -> h
 *         .icon("€")
 *         .title("AR aging · Helix Robotics")
 *         .variant(ArAgingBar.Variant.INFO))
 *     .content(c -> c
 *         .segment(s -> s
 *             .key("Current").value("Current · €35K").percent(56).variant(Variant.SUCCESS))
 *         .segment(s -> s
 *             .key("1–30d").value("1-30d · €18.7K").percent(30).variant(Variant.INFO))
 *         .segment(s -> s
 *             .key("31–60d").value("31-60d · €8.7K").percent(14).variant(Variant.WARNING)))
 *     .footer(f -> f
 *         .left("€0 owed").center("€0 overdue").right("€62.4K total open"))
 *     .build();
 *
 * // ── Chained sub-builder style ─────────────────────────────────────────────
 * Components.arAgingBar()
 *     .header()
 *         .icon("€").title("AR aging · Helix Robotics").variant(ArAgingBar.Variant.INFO)
 *         .add()
 *     .content()
 *         .segment().key("Current").value("Current · €35K").percent(56).variant(Variant.SUCCESS).add()
 *         .segment().key("1–30d").value("1-30d · €18.7K").percent(30).variant(Variant.INFO).add()
 *         .add()
 *     .footer()
 *         .left("€0 owed").center("€0 overdue").right("€62.4K total open")
 *         .add()
 *     .build();
 * }</pre>
 *
 * @see ArAgingBar
 * @see com.holonplatform.vaadin.flow.components.Components#arAgingBar()
 */
public class ArAgingBarBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Builder state — package-private so inner classes can write directly ─

    String icon       = "■";
    String title;
    ArAgingBar.Variant             cardVariant  = ArAgingBar.Variant.DEFAULT;
    final List<ArAgingBar.Segment> segments     = new ArrayList<>();
    String leftStat;
    String centerStat;
    String rightStat;
    Consumer<ArAgingBar>           postProcessor;

    private ArAgingBarBuilder() {}

    /** Creates a new {@code ArAgingBarBuilder}. */
    public static ArAgingBarBuilder create() {
        return new ArAgingBarBuilder();
    }

    // ── Consumer-style section methods (primary API) ───────────────────────

    /**
     * Configures the header (icon, title, card variant) via a lambda.
     *
     * <pre>{@code
     * .header(h -> h.icon("€").title("AR aging · Helix Robotics").variant(Variant.INFO))
     * }</pre>
     *
     * @param configurator consumer called with a {@link HeaderBuilder}; {@code null} = no-op
     * @return this (fluent)
     */
    public ArAgingBarBuilder header(Consumer<HeaderBuilder> configurator) {
        if (configurator != null) configurator.accept(new HeaderBuilder(this));
        return this;
    }

    /**
     * Configures the proportional bar segments via a lambda.
     *
     * <pre>{@code
     * .content(c -> c
     *     .segment(s -> s.key("Current").value("€35K").percent(56).variant(Variant.SUCCESS))
     *     .segment(s -> s.key("1–30d").value("€18.7K").percent(30).variant(Variant.INFO)))
     * }</pre>
     *
     * @param configurator consumer called with a {@link ContentBuilder}; {@code null} = no-op
     * @return this (fluent)
     */
    public ArAgingBarBuilder content(Consumer<ContentBuilder> configurator) {
        if (configurator != null) configurator.accept(new ContentBuilder(this));
        return this;
    }

    /**
     * Configures the footer stats (left, centre, right) via a lambda.
     *
     * <pre>{@code
     * .footer(f -> f.left("€0 owed").center("€0 overdue").right("€62.4K total open"))
     * }</pre>
     *
     * @param configurator consumer called with a {@link FooterBuilder}; {@code null} = no-op
     * @return this (fluent)
     */
    public ArAgingBarBuilder footer(Consumer<FooterBuilder> configurator) {
        if (configurator != null) configurator.accept(new FooterBuilder(this));
        return this;
    }

    // ── Chained sub-builder entry points (PanelConfigurator style) ─────────

    /**
     * Opens the header sub-builder.
     * Call {@link HeaderBuilder#add()} to return to this builder.
     *
     * @return a new {@link HeaderBuilder}
     */
    public HeaderBuilder header() {
        return new HeaderBuilder(this);
    }

    /**
     * Opens the content sub-builder.
     * Call {@link ContentBuilder#add()} to return to this builder.
     *
     * @return a new {@link ContentBuilder}
     */
    public ContentBuilder content() {
        return new ContentBuilder(this);
    }

    /**
     * Opens the footer sub-builder.
     * Call {@link FooterBuilder#add()} to return to this builder.
     *
     * @return a new {@link FooterBuilder}
     */
    public FooterBuilder footer() {
        return new FooterBuilder(this);
    }

    // ── Post-processor ─────────────────────────────────────────────────────

    /**
     * Registers a callback invoked with the fully assembled {@link ArAgingBar}
     * at the end of {@link #build()}, before the bar is returned. Multiple calls
     * compose in registration order.
     *
     * @param processor consumer called with the built bar; {@code null} is ignored
     * @return this (fluent)
     */
    public ArAgingBarBuilder withPostProcessor(Consumer<ArAgingBar> processor) {
        if (processor != null) {
            this.postProcessor = this.postProcessor == null
                    ? processor
                    : this.postProcessor.andThen(processor);
        }
        return this;
    }

    // ── Build ──────────────────────────────────────────────────────────────

    /**
     * Builds and returns the configured {@link ArAgingBar}.
     *
     * @return the fully assembled widget
     */
    public ArAgingBar build() {

        // ── Heading: icon badge + mutable title span ───────────────────────
        Span iconSpan = new Span(icon);
        iconSpan.addClassName("arb__icon");

        Span titleSpan = new Span(title != null ? title : "");
        titleSpan.addClassName("arb__heading-text");

        Div headingDiv = new Div(iconSpan, titleSpan);
        headingDiv.addClassName("arb__heading");

        // ── Legend: one swatch+label per segment ──────────────────────────
        Div legendDiv = new Div();
        legendDiv.addClassName("arb__legend");
        for (ArAgingBar.Segment seg : segments) {
            legendDiv.add(ArAgingBar.buildLegendItem(seg));
        }

        // ── Header ────────────────────────────────────────────────────────
        Header header = new Header("");
        header.setHeadingFontSize(null);
        header.setHeading(headingDiv);
        header.setActions(legendDiv);
        header.withoutSticky();

        // ── Track bar ─────────────────────────────────────────────────────
        Div trackDiv = new Div();
        trackDiv.addClassName("arb__track");
        // A11Y: the bar is a graphical representation — role="img" with a descriptive label
        trackDiv.getElement().setAttribute("role", "img");
        trackDiv.getElement().setAttribute("aria-label", ArAgingBar.buildTrackAriaLabel(segments));
        for (ArAgingBar.Segment seg : segments) {
            trackDiv.add(ArAgingBar.buildSegmentDiv(seg));
        }

        // ── Footer stats ──────────────────────────────────────────────────
        Span leftStatSpan   = new Span(leftStat   != null ? leftStat   : "");
        Span centerStatSpan = new Span(centerStat != null ? centerStat : "");
        Span rightStatSpan  = new Span(rightStat  != null ? rightStat  : "");
        leftStatSpan.addClassName("arb__stat");
        centerStatSpan.addClassName("arb__stat");
        rightStatSpan.addClassName("arb__stat");

        // ── Panel assembly ────────────────────────────────────────────────
        Panel panel = new Panel();
        panel.setHeader(header);

        boolean hasFooter = leftStat != null || centerStat != null || rightStat != null;
        if (hasFooter) {
            Div footerDiv = new Div(leftStatSpan, centerStatSpan, rightStatSpan);
            footerDiv.addClassName("arb__footer");
            panel.setContent(trackDiv, footerDiv);
        } else {
            panel.setContent(trackDiv);
        }

        ArAgingBar bar = new ArAgingBar(
                cardVariant, titleSpan, trackDiv, legendDiv,
                leftStatSpan, centerStatSpan, rightStatSpan, panel);

        if (postProcessor != null) postProcessor.accept(bar);
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Inner sub-builders
    // ═══════════════════════════════════════════════════════════════════════

    // ── HeaderBuilder ──────────────────────────────────────────────────────

    /**
     * Sub-builder for the header section: icon badge, title text, card variant.
     *
     * <p>Obtain via {@link ArAgingBarBuilder#header()} or
     * {@link ArAgingBarBuilder#header(Consumer)}.</p>
     */
    public static final class HeaderBuilder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ArAgingBarBuilder parent;

        HeaderBuilder(ArAgingBarBuilder parent) {
            this.parent = parent;
        }

        /**
         * Sets the icon badge character or short string displayed in the header
         * (e.g. {@code "€"}, {@code "⚡"}, {@code "%"}). Defaults to {@code "■"}.
         *
         * @param icon icon text; {@code null} keeps the default
         * @return this (fluent)
         */
        public HeaderBuilder icon(String icon) {
            parent.icon = icon != null ? icon : "■";
            return this;
        }

        /**
         * Sets the card title shown next to the icon badge.
         * Can be changed at runtime via {@link ArAgingBar#setBarTitle}.
         *
         * @param title title text; {@code null} renders no text
         * @return this (fluent)
         */
        public HeaderBuilder title(String title) {
            parent.title = title;
            return this;
        }

        /**
         * Sets the card title using a {@link Localizable} — resolved via the current
         * {@link com.holonplatform.core.i18n.LocalizationContext} or Vaadin {@code I18NProvider}.
         *
         * @param title localizable title; {@code null} renders no text
         * @return this (fluent)
         */
        public HeaderBuilder title(Localizable title) {
            parent.title = title != null ? LocalizationProvider.localize(title).orElse(null) : null;
            return this;
        }

        /**
         * Sets the card's visual variant (border accent colour + icon badge tint).
         * Can be changed at runtime via {@link ArAgingBar#setVariant}.
         * Defaults to {@link ArAgingBar.Variant#DEFAULT}.
         *
         * @param variant the card variant; {@code null} defaults to {@link ArAgingBar.Variant#DEFAULT}
         * @return this (fluent)
         */
        public HeaderBuilder variant(ArAgingBar.Variant variant) {
            parent.cardVariant = variant != null ? variant : ArAgingBar.Variant.DEFAULT;
            return this;
        }

        /**
         * Closes this sub-builder and returns to the parent {@link ArAgingBarBuilder}.
         *
         * @return the parent builder
         */
        public ArAgingBarBuilder add() {
            return parent;
        }
    }

    // ── ContentBuilder ─────────────────────────────────────────────────────

    /**
     * Sub-builder for the proportional bar segments.
     *
     * <p>Obtain via {@link ArAgingBarBuilder#content()} or
     * {@link ArAgingBarBuilder#content(Consumer)}.</p>
     */
    public static final class ContentBuilder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ArAgingBarBuilder parent;

        ContentBuilder(ArAgingBarBuilder parent) {
            this.parent = parent;
        }

        /**
         * Opens a {@link SegmentBuilder} for one proportional segment.
         * Call {@link SegmentBuilder#add()} to register it and return here.
         *
         * @return a new {@link SegmentBuilder}
         */
        public SegmentBuilder segment() {
            return new SegmentBuilder(this);
        }

        /**
         * Configures one segment via a consumer — no {@code .add()} needed.
         *
         * <pre>{@code
         * .segment(s -> s.key("Current").value("€35K").percent(56).variant(Variant.SUCCESS))
         * }</pre>
         *
         * @param configurator consumer called with a new {@link SegmentBuilder};
         *                     {@code null} = no-op
         * @return this (fluent)
         */
        public ContentBuilder segment(Consumer<SegmentBuilder> configurator) {
            SegmentBuilder seg = new SegmentBuilder(this);
            if (configurator != null) configurator.accept(seg);
            seg.add(); // register even if configurator is null (results in a blank segment)
            return this;
        }

        /**
         * Closes this sub-builder and returns to the parent {@link ArAgingBarBuilder}.
         *
         * @return the parent builder
         */
        public ArAgingBarBuilder add() {
            return parent;
        }
    }

    // ── SegmentBuilder ─────────────────────────────────────────────────────

    /**
     * Sub-builder for one proportional segment in the bar.
     *
     * <p>Obtain via {@link ContentBuilder#segment()} or
     * {@link ContentBuilder#segment(Consumer)}.</p>
     */
    public static final class SegmentBuilder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ContentBuilder parent;

        private String             key;
        private String             value;
        private double             percent;
        private ArAgingBar.Variant color = ArAgingBar.Variant.DEFAULT;

        SegmentBuilder(ContentBuilder parent) {
            this.parent = parent;
        }

        /**
         * Sets the legend label shown in the header legend
         * (e.g. {@code "Current"}, {@code "1–30d"}).
         *
         * @param key legend label; {@code null} renders an empty label
         * @return this (fluent)
         */
        public SegmentBuilder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the legend label using a {@link Localizable}.
         *
         * @param key localizable legend label; {@code null} renders an empty label
         * @return this (fluent)
         */
        public SegmentBuilder key(Localizable key) {
            this.key = key != null ? LocalizationProvider.localize(key).orElse(null) : null;
            return this;
        }

        /**
         * Sets the optional label centred inside the bar segment
         * (e.g. {@code "Current · €35K"}). {@code null} = no bar label.
         *
         * @param value bar label; {@code null} or blank = no label
         * @return this (fluent)
         */
        public SegmentBuilder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the bar segment label using a {@link Localizable}.
         *
         * @param value localizable bar label; {@code null} or blank = no label
         * @return this (fluent)
         */
        public SegmentBuilder value(Localizable value) {
            this.value = value != null ? LocalizationProvider.localize(value).orElse(null) : null;
            return this;
        }

        /**
         * Sets the segment width as a {@code flex-basis} percentage (0–100).
         * All segments should ideally sum to 100.
         *
         * @param percent percentage width
         * @return this (fluent)
         */
        public SegmentBuilder percent(double percent) {
            this.percent = percent;
            return this;
        }

        /**
         * Sets the semantic colour variant for both the legend swatch and the bar fill.
         * Defaults to {@link ArAgingBar.Variant#DEFAULT}.
         *
         * @param color the colour variant; {@code null} defaults to {@link ArAgingBar.Variant#DEFAULT}
         * @return this (fluent)
         */
        public SegmentBuilder variant(ArAgingBar.Variant color) {
            this.color = color != null ? color : ArAgingBar.Variant.DEFAULT;
            return this;
        }

        /**
         * Registers this segment and returns to the parent {@link ContentBuilder}.
         *
         * @return the parent content builder
         */
        public ContentBuilder add() {
            parent.parent.segments.add(new ArAgingBar.Segment(key, value, percent, color));
            return parent;
        }
    }

    // ── FooterBuilder ──────────────────────────────────────────────────────

    /**
     * Sub-builder for the three footer stat slots: left, centre, right.
     *
     * <p>Obtain via {@link ArAgingBarBuilder#footer()} or
     * {@link ArAgingBarBuilder#footer(Consumer)}.</p>
     */
    public static final class FooterBuilder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ArAgingBarBuilder parent;

        FooterBuilder(ArAgingBarBuilder parent) {
            this.parent = parent;
        }

        /**
         * Sets the left footer stat (e.g. {@code "€0 owed"}, {@code "40 deals"}).
         * Can be changed at runtime via {@link ArAgingBar#setLeftStat}.
         *
         * @param text stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder left(String text) {
            parent.leftStat = text;
            return this;
        }

        /**
         * Sets the left footer stat using a {@link Localizable}.
         *
         * @param text localizable stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder left(Localizable text) {
            parent.leftStat = text != null ? LocalizationProvider.localize(text).orElse(null) : null;
            return this;
        }

        /**
         * Sets the centre footer stat (e.g. {@code "€0 overdue"}, {@code "€1.2M pipeline"}).
         * Can be changed at runtime via {@link ArAgingBar#setCenterStat}.
         *
         * @param text stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder center(String text) {
            parent.centerStat = text;
            return this;
        }

        /**
         * Sets the centre footer stat using a {@link Localizable}.
         *
         * @param text localizable stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder center(Localizable text) {
            parent.centerStat = text != null ? LocalizationProvider.localize(text).orElse(null) : null;
            return this;
        }

        /**
         * Sets the right footer stat (e.g. {@code "€62.4K total open"}, {@code "72% avg prob"}).
         * Can be changed at runtime via {@link ArAgingBar#setRightStat}.
         *
         * @param text stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder right(String text) {
            parent.rightStat = text;
            return this;
        }

        /**
         * Sets the right footer stat using a {@link Localizable}.
         *
         * @param text localizable stat text; {@code null} renders an empty span
         * @return this (fluent)
         */
        public FooterBuilder right(Localizable text) {
            parent.rightStat = text != null ? LocalizationProvider.localize(text).orElse(null) : null;
            return this;
        }

        /**
         * Closes this sub-builder and returns to the parent {@link ArAgingBarBuilder}.
         *
         * @return the parent builder
         */
        public ArAgingBarBuilder add() {
            return parent;
        }
    }
}
