package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.dependency.StyleSheet;

/**
 * Interactive pill-shaped filter chip rendered as a native {@code <button>}.
 *
 * <p>Chips display a label and an optional trailing count badge. They toggle
 * between an inactive (default) and active (selected) visual state via
 * {@link #active(boolean)} or by placing them inside a
 * {@link ChipGroup}.</p>
 *
 * <h3>Standalone usage</h3>
 * <pre>{@code
 * Chip all  = Chip.of("All",  2418).active(true);
 * Chip open = Chip.of("Open", 14);
 * all.addClickListener(e -> filter("all"));
 * }</pre>
 *
 * <h3>Toggle group (recommended)</h3>
 * <pre>{@code
 * ChipGroup group = ChipGroup.create()
 *     .addChip(Chip.of("All",       2418), true)
 *     .addChip(Chip.of("Open",        14))
 *     .addChip(Chip.of("Posted",    2376))
 *     .addChip(Chip.of("QC Issues",    3))
 *     .onSelect(e -> filter(e.getLabel()))
 *     .build();
 * }</pre>
 *
 * @see ChipGroup
 */
@Tag("button")
@StyleSheet("context://chip.css")
public class Chip extends Component implements ClickNotifier<Chip> {

    private static final String CSS_BASE   = "chip-btn";
    private static final String CSS_ACTIVE = "chip-btn--active";
    private static final String CSS_COUNT  = "chip-count";
    private static final String CSS_SM     = "chip-btn--sm";
    private static final String CSS_LG     = "chip-btn--lg";

    private final Span labelSpan = new Span();
    private final Span countSpan = new Span();
    private boolean    countAttached;

    // ── Constructors ──────────────────────────────────────────────────────

    /**
     * Creates a chip with the given label text.
     * @param label chip label (not null)
     */
    public Chip(String label) {
        getElement().setAttribute("type", "button");
        getClassNames().add(CSS_BASE);
        labelSpan.setText(label == null ? "" : label);
        getElement().appendChild(labelSpan.getElement());
    }

    // ── Factory methods ───────────────────────────────────────────────────

    /** Creates a chip with a label only. */
    public static Chip of(String label) {
        return new Chip(label);
    }

    /** Creates a chip with a label and a numeric count badge. */
    public static Chip of(String label, long count) {
        return new Chip(label).withCount(count);
    }

    // ── Configuration ─────────────────────────────────────────────────────

    /**
     * Attaches or updates the trailing count badge.
     * @param count count value
     * @return this
     */
    public Chip withCount(long count) {
        countSpan.setText(String.valueOf(count));
        if (!countAttached) {
            countSpan.addClassName(CSS_COUNT);
            getElement().appendChild(countSpan.getElement());
            countAttached = true;
        }
        return this;
    }

    /** Updates the count badge value; creates it if not yet present. */
    public void setCount(long count) {
        withCount(count);
    }

    /**
     * Sets the active (selected) state — adds or removes {@code chip-btn--active}.
     * @param active {@code true} to activate
     * @return this
     */
    public Chip active(boolean active) {
        getClassNames().set(CSS_ACTIVE, active);
        return this;
    }

    /** @return {@code true} if this chip is currently in the active/selected state */
    public boolean isActive() {
        return getClassNames().contains(CSS_ACTIVE);
    }

    /** @return the label text of this chip */
    public String getLabel() {
        return labelSpan.getText();
    }

    /** Updates the chip label text. */
    public void setLabel(String label) {
        labelSpan.setText(label == null ? "" : label);
    }

    /** Applies the small size variant ({@code chip-btn--sm}: height 22 px). */
    public Chip small() {
        getClassNames().add(CSS_SM);
        return this;
    }

    /** Applies the large size variant ({@code chip-btn--lg}: height 32 px). */
    public Chip large() {
        getClassNames().add(CSS_LG);
        return this;
    }

    /** Enables or disables the chip. */
    public Chip enabled(boolean enabled) {
        getElement().setEnabled(enabled);
        return this;
    }
}

