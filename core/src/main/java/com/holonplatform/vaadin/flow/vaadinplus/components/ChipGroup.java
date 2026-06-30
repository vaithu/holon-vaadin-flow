package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dependency.StyleSheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A flex row of mutually exclusive {@link Chip}s that behaves like a radio-button group.
 *
 * <p>Clicking any chip deactivates the previously active chip and activates the clicked
 * one.  A {@link SelectionEvent} is fired on every selection change.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * ChipGroup group = ChipGroup.create()
 *     .addChip(Chip.of("All",       2418), true)   // initially active
 *     .addChip(Chip.of("Open",        14))
 *     .addChip(Chip.of("Posted",    2376))
 *     .addChip(Chip.of("QC Issues",    3))
 *     .onSelect(e -> applyFilter(e.getChip().getLabel()))
 *     .build();
 * add(group);
 * }</pre>
 *
 * <h3>Update counts at runtime</h3>
 * <pre>{@code
 * group.getChips().get(0).setCount(newTotal);
 * }</pre>
 *
 * @see Chip
 */
@StyleSheet("context://chip.css")
public class ChipGroup extends Div {

    private final List<Chip> chips = new ArrayList<>();
    private Chip activeChip;

    // ── Constructor ───────────────────────────────────────────────────────

    public ChipGroup() {
        addClassName("chip-group");
    }

    // ── Builder entry point ───────────────────────────────────────────────

    /** Returns a new {@link ChipGroup} for fluent configuration. */
    public static ChipGroup create() {
        return new ChipGroup();
    }

    // ── Fluent builder API ────────────────────────────────────────────────

    /**
     * Adds a chip to the group (initially inactive).
     * @param chip the chip to add
     * @return this
     */
    public ChipGroup addChip(Chip chip) {
        return addChip(chip, false);
    }

    /**
     * Adds a chip to the group with an explicit initial active state.
     * If {@code active} is {@code true} any previously active chip is deactivated.
     * @param chip   the chip to add
     * @param active whether this chip should start in the active state
     * @return this
     */
    public ChipGroup addChip(Chip chip, boolean active) {
        chips.add(chip);
        add(chip);
        chip.addClickListener(e -> select(chip));
        if (active) {
            activateQuietly(chip);
        }
        return this;
    }

    /**
     * Convenience: create and add a label-only chip.
     * @param label chip label
     * @return this
     */
    public ChipGroup addChip(String label) {
        return addChip(Chip.of(label));
    }

    /**
     * Convenience: create and add a label-only chip with an explicit initial active state.
     * @param label  chip label
     * @param active whether this chip should start active
     * @return this
     */
    public ChipGroup addChip(String label, boolean active) {
        return addChip(Chip.of(label), active);
    }

    /**
     * Convenience: create and add a chip with a count badge.
     * @param label chip label
     * @param count count value
     * @return this
     */
    public ChipGroup addChip(String label, long count) {
        return addChip(Chip.of(label, count));
    }

    /**
     * Convenience: create and add a chip with a count badge and control its initial active state.
     * @param label  chip label
     * @param count  count value
     * @param active whether this chip should start active
     * @return this
     */
    public ChipGroup addChip(String label, long count, boolean active) {
        return addChip(Chip.of(label, count), active);
    }

    /**
     * Registers a {@link SelectionEvent} listener.
     * @param listener fired whenever the active chip changes
     * @return this (for chaining — call {@link #build()} when done)
     */
    public ChipGroup onSelect(ComponentEventListener<SelectionEvent> listener) {
        addListener(SelectionEvent.class, listener);
        return this;
    }

    /** Terminal builder method — returns {@code this} for inline use: {@code add(group.build())}. */
    public ChipGroup build() {
        return this;
    }

    // ── Wrapping modifier ─────────────────────────────────────────────────

    /**
     * Allows chips to wrap to the next line when they overflow.
     * Adds {@code chip-group--wrap}.
     * @return this
     */
    public ChipGroup wrap() {
        addClassName("chip-group--wrap");
        return this;
    }

    // ── Runtime API ───────────────────────────────────────────────────────

    /**
     * Programmatically selects a chip by zero-based index.
     * Fires a {@link SelectionEvent} exactly as if the user clicked the chip.
     * @param index zero-based chip index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void select(int index) {
        select(chips.get(index));
    }

    /**
     * Programmatically selects the given chip.
     * @param chip must be a chip that belongs to this group
     */
    public void select(Chip chip) {
        if (chip == activeChip) return;
        activateQuietly(chip);
        fireEvent(new SelectionEvent(this, false, chip));
    }

    /** @return the currently active chip, or {@code null} if none is active */
    public Chip getActiveChip() {
        return activeChip;
    }

    /** @return an unmodifiable view of all chips in this group (in insertion order) */
    public List<Chip> getChips() {
        return Collections.unmodifiableList(chips);
    }

    // ── Internal ─────────────────────────────────────────────────────────

    /** Swaps the active chip without firing an event. */
    private void activateQuietly(Chip next) {
        if (activeChip != null) {
            activeChip.active(false);
        }
        next.active(true);
        activeChip = next;
    }

    // ── SelectionEvent ────────────────────────────────────────────────────

    /**
     * Fired by {@link ChipGroup} whenever the active chip changes.
     *
     * <p>Obtain the newly selected chip via {@link #getChip()}.</p>
     */
    public static final class SelectionEvent extends ComponentEvent<ChipGroup> {

        private final Chip chip;

        SelectionEvent(ChipGroup source, boolean fromClient, Chip chip) {
            super(source, fromClient);
            this.chip = chip;
        }

        /** @return the chip that was just activated */
        public Chip getChip() {
            return chip;
        }

        /** @return the label of the activated chip */
        public String getLabel() {
            return chip.getLabel();
        }
    }
}


