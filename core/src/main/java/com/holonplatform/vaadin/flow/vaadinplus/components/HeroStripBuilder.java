package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link HeroStrip} — a horizontal gradient metric-strip card.
 *
 * <p>Supports two equivalent coding styles:</p>
 *
 * <h3>Consumer style (recommended)</h3>
 * <pre>{@code
 * Components.heroStrip()
 *     .variant(HeroStrip.Variant.INFO)
 *     .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals · 80% avg prob").pulse(true))
 *     .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders · 22 invoices").valueVariant(HeroStrip.ValueVariant.OK))
 *     .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
 *     .build();
 * }</pre>
 *
 * <h3>Chained sub-builder style</h3>
 * <pre>{@code
 * Components.heroStrip()
 *     .variant(HeroStrip.Variant.INFO)
 *     .cell().header("Open pipeline").content("€182K").footer("4 active deals").pulse(true).add()
 *     .cell().header("Booked YTD").content("€624K").footer("14 orders").valueVariant(HeroStrip.ValueVariant.OK).add()
 *     .build();
 * }</pre>
 *
 * @see HeroStrip
 * @see com.holonplatform.vaadin.flow.components.Components#heroStrip()
 */
public class HeroStripBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Builder state — package-private so inner classes can write directly ─

    HeroStrip.Variant variant = HeroStrip.Variant.DEFAULT;
    boolean responsive = false;
    final List<HeroStrip.Cell> cells = new ArrayList<>();
    Consumer<HeroStrip> postProcessor;

    private HeroStripBuilder() {}

    /** Creates a new {@code HeroStripBuilder}. */
    public static HeroStripBuilder create() {
        return new HeroStripBuilder();
    }

    // ── Responsive ─────────────────────────────────────────────────────────

    /**
     * Enables responsive wrapping: cells reflow to new rows on narrow viewports
     * instead of being squished into a single row.
     *
     * <p>Behaviour at common breakpoints:</p>
     * <ul>
     *   <li>&gt; 768 px — all cells on one row (same as non-responsive)</li>
     *   <li>481–768 px — cells auto-fit into 2–3 per row</li>
     *   <li>≤ 480 px — forced 2-column grid</li>
     *   <li>≤ 320 px — forced 1-column (very small phones)</li>
     * </ul>
     *
     * <pre>{@code
     * Components.heroStrip()
     *     .variant(HeroStrip.Variant.INFO)
     *     .responsive()
     *     .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals"))
     *     .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders").valueVariant(HeroStrip.ValueVariant.OK))
     *     .build();
     * }</pre>
     *
     * @return this (fluent)
     */
    public HeroStripBuilder responsive() {
        this.responsive = true;
        return this;
    }

    // ── Variant ────────────────────────────────────────────────────────────

    /**
     * Sets the gradient background variant for the strip.
     * Defaults to {@link HeroStrip.Variant#DEFAULT}.
     *
     * @param variant the gradient variant; {@code null} defaults to {@link HeroStrip.Variant#DEFAULT}
     * @return this (fluent)
     */
    public HeroStripBuilder variant(HeroStrip.Variant variant) {
        this.variant = variant != null ? variant : HeroStrip.Variant.DEFAULT;
        return this;
    }

    // ── Consumer-style cell method ─────────────────────────────────────────

    /**
     * Adds one metric cell configured via a consumer lambda — no {@code .add()} needed.
     *
     * <pre>{@code
     * .cell(c -> c.label("Open pipeline").value("€182K").sub("4 active deals").pulse(true))
     * }</pre>
     *
     * @param configurator consumer called with a new {@link CellBuilder}; {@code null} = no-op
     * @return this (fluent)
     */
    public HeroStripBuilder cell(Consumer<CellBuilder> configurator) {
        CellBuilder cb = new CellBuilder(this);
        if (configurator != null) configurator.accept(cb);
        cb.add();
        return this;
    }

    // ── Chained sub-builder style ──────────────────────────────────────────

    /**
     * Opens a {@link CellBuilder} for one metric cell.
     * Call {@link CellBuilder#add()} to register the cell and return to this builder.
     *
     * @return a new {@link CellBuilder}
     */
    public CellBuilder cell() {
        return new CellBuilder(this);
    }

    // ── Post-processor ─────────────────────────────────────────────────────

    /**
     * Registers a callback invoked with the fully assembled {@link HeroStrip}
     * at the end of {@link #build()}, before the strip is returned. Multiple calls
     * compose in registration order.
     *
     * @param processor consumer called with the built strip; {@code null} is ignored
     * @return this (fluent)
     */
    public HeroStripBuilder withPostProcessor(Consumer<HeroStrip> processor) {
        if (processor != null) {
            this.postProcessor = this.postProcessor == null
                    ? processor
                    : this.postProcessor.andThen(processor);
        }
        return this;
    }

    // ── Build ──────────────────────────────────────────────────────────────

    /**
     * Builds and returns the configured {@link HeroStrip}.
     *
     * @return the fully assembled strip component
     */
    public HeroStrip build() {
        HeroStrip strip = new HeroStrip(variant, cells, responsive);
        if (postProcessor != null) postProcessor.accept(strip);
        return strip;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Inner sub-builder
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Sub-builder for one metric cell in the strip.
     *
     * <p>Obtain via {@link HeroStripBuilder#cell()} or
     * {@link HeroStripBuilder#cell(Consumer)}.</p>
     */
    public static final class CellBuilder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final HeroStripBuilder parent;

        private String header;
        private String content;
        private String footer;
        private boolean pulse = false;
        private HeroStrip.ValueVariant valueVariant = HeroStrip.ValueVariant.DEFAULT;

        CellBuilder(HeroStripBuilder parent) {
            this.parent = parent;
        }

        /**
         * Sets the small uppercase header label shown above the value
         * (e.g. {@code "Open pipeline"}, {@code "Booked YTD"}).
         *
         * @param header header text; {@code null} renders an empty header
         * @return this (fluent)
         */
        public CellBuilder header(String header) {
            this.header = header;
            return this;
        }

        /**
         * Sets the header label using a {@link Localizable} — resolved via the current
         * {@link com.holonplatform.core.i18n.LocalizationContext} or Vaadin {@code I18NProvider}.
         *
         * @param header localizable header; {@code null} renders an empty header
         * @return this (fluent)
         */
        public CellBuilder header(Localizable header) {
            this.header = header != null ? LocalizationProvider.localize(header).orElse(null) : null;
            return this;
        }

        /**
         * Sets the large monospace content value displayed prominently in the cell
         * (e.g. {@code "€182K"}, {@code "13:42"}).
         *
         * @param content content text; {@code null} renders an empty value
         * @return this (fluent)
         */
        public CellBuilder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Sets the content value using a {@link Localizable}.
         *
         * @param content localizable content; {@code null} renders an empty value
         * @return this (fluent)
         */
        public CellBuilder content(Localizable content) {
            this.content = content != null ? LocalizationProvider.localize(content).orElse(null) : null;
            return this;
        }

        /**
         * Sets the optional small footer label shown below the value
         * (e.g. {@code "4 active deals · 80% avg prob"}).
         * {@code null} or blank = footer is hidden.
         *
         * @param footer footer text; {@code null} or blank hides the footer
         * @return this (fluent)
         */
        public CellBuilder footer(String footer) {
            this.footer = footer;
            return this;
        }

        /**
         * Sets the footer label using a {@link Localizable}.
         *
         * @param footer localizable footer; {@code null} or blank hides the footer
         * @return this (fluent)
         */
        public CellBuilder footer(Localizable footer) {
            this.footer = footer != null ? LocalizationProvider.localize(footer).orElse(null) : null;
            return this;
        }

        /**
         * Controls the animated amber pulse dot rendered before the label.
         * Defaults to {@code false}.
         *
         * @param pulse {@code true} to show the pulse dot
         * @return this (fluent)
         */
        public CellBuilder pulse(boolean pulse) {
            this.pulse = pulse;
            return this;
        }

        /**
         * Sets the colour tint applied to the large value text.
         * Defaults to {@link HeroStrip.ValueVariant#DEFAULT} (white).
         *
         * @param valueVariant the colour variant; {@code null} defaults to {@link HeroStrip.ValueVariant#DEFAULT}
         * @return this (fluent)
         */
        public CellBuilder valueVariant(HeroStrip.ValueVariant valueVariant) {
            this.valueVariant = valueVariant != null ? valueVariant : HeroStrip.ValueVariant.DEFAULT;
            return this;
        }

        /**
         * Registers this cell and returns to the parent {@link HeroStripBuilder}.
         *
         * @return the parent builder
         */
        public HeroStripBuilder add() {
            parent.cells.add(new HeroStrip.Cell(header, content, footer, pulse, valueVariant));
            return parent;
        }
    }
}
