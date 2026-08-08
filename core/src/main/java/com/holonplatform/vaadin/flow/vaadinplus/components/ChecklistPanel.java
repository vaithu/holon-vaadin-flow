package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A vertical checklist panel showing ordered steps with {@link ItemState#DONE},
 * {@link ItemState#CURRENT}, and {@link ItemState#PENDING} visual states.
 *
 * <p>Designed to sit in a sidebar alongside a multi-step form, giving users a clear
 * overview of progress at a glance. Each item returns a {@link ChecklistItem} handle
 * that can be updated without rebuilding the component.</p>
 *
 * <h3>Visual states</h3>
 * <ul>
 *   <li>{@link ItemState#DONE}    — green circle with a checkmark ✓</li>
 *   <li>{@link ItemState#CURRENT} — blue circle with the step number (pulsing ring)</li>
 *   <li>{@link ItemState#PENDING} — grey circle with the step number</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * ChecklistPanel checklist = new ChecklistPanel();
 * ChecklistPanel.ChecklistItem org     = checklist.addItem("Organization",    "Name, type, industry",  ItemState.DONE);
 * ChecklistPanel.ChecklistItem contact = checklist.addItem("Primary contact", "Name, email verified",  ItemState.DONE);
 * ChecklistPanel.ChecklistItem address = checklist.addItem("Address",         "In progress",           ItemState.CURRENT);
 * ChecklistPanel.ChecklistItem finance = checklist.addItem("Financial & tax", "Required to invoice",   ItemState.PENDING);
 * ChecklistPanel.ChecklistItem assign  = checklist.addItem("Assignment",      null,                    ItemState.PENDING);
 * ChecklistPanel.ChecklistItem notes   = checklist.addItem("Notes & tags",    "Optional",              ItemState.PENDING);
 *
 * // Advance to next step:
 * address.setState(ItemState.DONE);
 * finance.setState(ItemState.CURRENT);
 * }</pre>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/checklist-panel.css} — BEM root: {@code .clp}
 */
@StyleSheet("context://checklist-panel.css")
public class ChecklistPanel extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── BEM class constants ───────────────────────────────────────────────

    private static final String CSS_ROOT      = "clp";
    private static final String CSS_ITEM      = "clp__item";
    private static final String CSS_IC        = "clp__item-ic";
    private static final String CSS_IC_DONE   = "clp__item-ic--done";
    private static final String CSS_IC_CUR    = "clp__item-ic--current";
    private static final String CSS_IC_PEND   = "clp__item-ic--pending";
    private static final String CSS_BODY      = "clp__item-body";
    private static final String CSS_LABEL     = "clp__item-label";
    private static final String CSS_LABEL_CUR = "clp__item-label--current";
    private static final String CSS_SUB       = "clp__item-sub";

    // ── Internal state ────────────────────────────────────────────────────

    private final List<ChecklistItem> items = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────

    /** Creates an empty {@code ChecklistPanel}. Items are added via {@link #addItem}. */
    public ChecklistPanel() {
        addClassName(CSS_ROOT);
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Appends an item to the checklist.
     *
     * <p>The step number shown in the circle indicator is automatically derived from
     * the insertion order (1-based).</p>
     *
     * @param label    primary label text (e.g. {@code "Address"})
     * @param subLabel optional sub-label shown below the label (e.g. {@code "In progress"}); {@code null} to omit
     * @param state    initial visual state of the step
     * @return a {@link ChecklistItem} handle that can update label / sub-label / state at runtime
     */
    public ChecklistItem addItem(String label, String subLabel, ItemState state) {
        int stepNumber = items.size() + 1;
        ChecklistItem item = new ChecklistItem(stepNumber, label, subLabel, state);
        items.add(item);
        add(item.root);
        return item;
    }

    /**
     * Convenience overload — adds an item without a sub-label.
     *
     * @param label primary label text
     * @param state initial visual state
     * @return the {@link ChecklistItem} handle
     */
    public ChecklistItem addItem(String label, ItemState state) {
        return addItem(label, null, state);
    }

    /**
     * Convenience overload — adds an item with a {@link Localizable} label and no sub-label.
     *
     * @param label localizable label
     * @param state initial visual state
     * @return the {@link ChecklistItem} handle
     */
    public ChecklistItem addItem(Localizable label, ItemState state) {
        return addItem(LocalizationProvider.localize(label.getMessage(), label.getMessageCode()), null, state);
    }

    /**
     * Returns an unmodifiable view of all items in insertion order.
     *
     * @return list of all checklist items
     */
    public List<ChecklistItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Advances exactly one step:
     * <ul>
     *   <li>The currently {@link ItemState#CURRENT} item (if any) transitions to {@link ItemState#DONE}.</li>
     *   <li>The first {@link ItemState#PENDING} item becomes {@link ItemState#CURRENT}.</li>
     * </ul>
     *
     * <p>Calling this repeatedly walks through the checklist automatically.</p>
     *
     * @return this (fluent)
     */
    public ChecklistPanel advance() {
        items.stream()
             .filter(i -> i.getState() == ItemState.CURRENT)
             .findFirst()
             .ifPresent(i -> i.setState(ItemState.DONE));
        items.stream()
             .filter(i -> i.getState() == ItemState.PENDING)
             .findFirst()
             .ifPresent(i -> i.setState(ItemState.CURRENT));
        return this;
    }

    // ── ItemState ─────────────────────────────────────────────────────────

    /**
     * Visual state of a {@link ChecklistItem}.
     */
    public enum ItemState {

        /** Step has been completed — green circle with a checkmark. */
        DONE,

        /** Step is currently active — blue circle with step number and pulsing ring. */
        CURRENT,

        /** Step has not yet been started — grey circle with step number. */
        PENDING
    }

    // ── ChecklistItem handle ──────────────────────────────────────────────

    /**
     * Handle to a single row inside a {@link ChecklistPanel}.
     * All mutations are applied in-place without rebuilding the panel.
     */
    public static final class ChecklistItem {

        private final Div  root;
        private final Span iconSpan;
        private final Span labelSpan;
        private final Span subSpan;
        private final int  stepNumber;
        private ItemState  currentState;

        private ChecklistItem(int stepNumber, String label, String subLabel, ItemState state) {
            this.stepNumber = stepNumber;

            root = new Div();
            root.addClassName(CSS_ITEM);

            iconSpan = new Span();
            iconSpan.addClassName(CSS_IC);

            Div body = new Div();
            body.addClassName(CSS_BODY);

            labelSpan = new Span(label != null ? label : "");
            labelSpan.addClassName(CSS_LABEL);

            subSpan = new Span(subLabel != null ? subLabel : "");
            subSpan.addClassName(CSS_SUB);
            subSpan.setVisible(subLabel != null && !subLabel.isEmpty());

            body.add(labelSpan, subSpan);
            root.add(iconSpan, body);

            applyState(Objects.requireNonNullElse(state, ItemState.PENDING));
        }

        /**
         * Changes the visual state of this step.
         *
         * @param state new state; must not be {@code null}
         * @return this (fluent)
         */
        public ChecklistItem setState(ItemState state) {
            Objects.requireNonNull(state, "state must not be null");
            // Remove previous state classes
            iconSpan.removeClassName(CSS_IC_DONE);
            iconSpan.removeClassName(CSS_IC_CUR);
            iconSpan.removeClassName(CSS_IC_PEND);
            labelSpan.removeClassName(CSS_LABEL_CUR);
            applyState(state);
            return this;
        }

        /**
         * Returns the current visual state.
         *
         * @return the current {@link ItemState}
         */
        public ItemState getState() {
            return currentState;
        }

        /**
         * Updates the primary label text.
         *
         * @param label new label; {@code null} clears it
         * @return this (fluent)
         */
        public ChecklistItem setLabel(String label) {
            labelSpan.setText(label != null ? label : "");
            return this;
        }

        /**
         * Updates (or clears) the sub-label.
         *
         * @param subLabel new sub-label; {@code null} or empty hides the element
         * @return this (fluent)
         */
        public ChecklistItem setSubLabel(String subLabel) {
            subSpan.setText(subLabel != null ? subLabel : "");
            subSpan.setVisible(subLabel != null && !subLabel.isEmpty());
            return this;
        }

        /** Returns the 1-based step number of this item. */
        public int getStepNumber() {
            return stepNumber;
        }

        private void applyState(ItemState state) {
            this.currentState = state;
            switch (state) {
                case DONE -> {
                    iconSpan.addClassName(CSS_IC_DONE);
                    iconSpan.setText("✓");
                }
                case CURRENT -> {
                    iconSpan.addClassName(CSS_IC_CUR);
                    iconSpan.setText(String.valueOf(stepNumber));
                    labelSpan.addClassName(CSS_LABEL_CUR);
                }
                case PENDING -> {
                    iconSpan.addClassName(CSS_IC_PEND);
                    iconSpan.setText(String.valueOf(stepNumber));
                }
            }
        }
    }
}
