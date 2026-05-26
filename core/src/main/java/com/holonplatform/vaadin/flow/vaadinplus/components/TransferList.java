package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.TransferListBuilder;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;

import java.util.*;

/**
 * Dual-panel shuttle component for moving {@link TransferItem} items between
 * an "Available" source list (left) and a "Selected" target list (right).
 *
 * <h3>Layout</h3>
 * <pre>
 * ┌─────────────────────┐  ┌───┐  ┌─────────────────────┐
 * │ Available      [5]  │  │ → │  │ Selected       [2]  │
 * │─────────────────────│  │ →→│  │─────────────────────│
 * │ [✓] Coffee Table    │  │ ← │  │ [ ] Sofa            │
 * │ [ ] Storage Cabinet │  │ ←←│  │ [ ] Area Rug        │
 * │ [✓] Dining Set      │  └───┘  └─────────────────────┘
 * └─────────────────────┘
 * </pre>
 *
 * <ul>
 *   <li>{@code →}  — move highlighted items from Available → Selected</li>
 *   <li>{@code →→} — move all Available → Selected</li>
 *   <li>{@code ←}  — move highlighted items from Selected → Available</li>
 *   <li>{@code ←←} — move all Selected → Available</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * var items = List.of(
 *     TransferItem.of("1", "Coffee Table"),
 *     TransferItem.of("2", "Storage Cabinet"),
 *     TransferItem.of("3", "Dining Set")
 * );
 *
 * TransferList list = TransferList.builder()
 *     .availableItems(items)
 *     .availableTitle("Products")
 *     .selectedTitle("Ordered Items")
 *     .onTransfer(event -> save(event.getSelectedItems()))
 *     .build();
 * }</pre>
 *
 * <p>All visual styling is defined in {@code transfer-list.css}.
 * No inline styles or Lumo tokens are used in Java.</p>
 *
 * @see TransferItem
 * @see TransferListBuilder
 */
@StyleSheet("context://transfer-list.css")
public class TransferList extends Div implements HasSize, HasStyle {

    private static final long serialVersionUID = 1L;

    // ── Internal state ────────────────────────────────────────────────────────

    /** Items currently shown in the left (available) panel. */
    private final List<TransferItem> availableItems = new ArrayList<>();

    /** Items currently shown in the right (selected) panel. */
    private final List<TransferItem> selectedItems = new ArrayList<>();

    /** IDs of items currently highlighted (row-selected) in the available panel. */
    private final Set<String> highlightedAvailable = new LinkedHashSet<>();

    /** IDs of items currently highlighted (row-selected) in the selected panel. */
    private final Set<String> highlightedSelected = new LinkedHashSet<>();

    // ── UI references ─────────────────────────────────────────────────────────

    private final Div availableListDiv;
    private final Div selectedListDiv;
    private final Span availableCountSpan;
    private final Span selectedCountSpan;
    private final Span availableTitleSpan;
    private final Span selectedTitleSpan;

    private final Button moveRightBtn;
    private final Button moveAllRightBtn;
    private final Button moveLeftBtn;
    private final Button moveAllLeftBtn;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an empty {@code TransferList} with default "Available" / "Selected" titles.
     */
    public TransferList() {
        addClassName("transfer-list");

        // ── Left panel (Available) ────────────────────────────────────────────
        availableTitleSpan = Components.span().text("Available").styleName("transfer-list__panel-title").build();
        availableCountSpan = Components.span().text("0").styleName("transfer-list__panel-count").build();

        Div availableHeader = Components.div().add(availableTitleSpan, availableCountSpan).styleName("transfer-list__panel-header").build();

        availableListDiv = Components.div().styleName("transfer-list__panel-list").build();

        Div availablePanel = Components.div().add(availableHeader, availableListDiv).styleName("transfer-list__panel").build();

        // ── Controls column ───────────────────────────────────────────────────
        moveRightBtn = buildCtrlButton(VaadinIcon.ANGLE_RIGHT, "Move selected right");
        moveAllRightBtn = buildCtrlButton(VaadinIcon.ANGLE_DOUBLE_RIGHT, "Move all right");
        moveLeftBtn = buildCtrlButton(VaadinIcon.ANGLE_LEFT, "Move selected left");
        moveAllLeftBtn = buildCtrlButton(VaadinIcon.ANGLE_DOUBLE_LEFT, "Move all left");

        moveRightBtn.addClickListener(e -> moveHighlightedRight());
        moveAllRightBtn.addClickListener(e -> moveAllRight());
        moveLeftBtn.addClickListener(e -> moveHighlightedLeft());
        moveAllLeftBtn.addClickListener(e -> moveAllLeft());

        Div toRightGroup = Components.div().add(moveRightBtn, moveAllRightBtn).styleName("transfer-list__ctrl-group").build();
        Div sep = Components.div().styleName("transfer-list__ctrl-sep").build();
        Div toLeftGroup = Components.div().add(moveLeftBtn, moveAllLeftBtn).styleName("transfer-list__ctrl-group").build();
        Div controls = Components.div().add(toRightGroup, sep, toLeftGroup).styleName("transfer-list__controls").build();

        // ── Right panel (Selected) ────────────────────────────────────────────
        selectedTitleSpan = Components.span().text("Selected").styleName("transfer-list__panel-title").build();
        selectedCountSpan = Components.span().text("0").styleName("transfer-list__panel-count").build();

        Div selectedHeader = Components.div().add(selectedTitleSpan, selectedCountSpan).styleName("transfer-list__panel-header").build();

        selectedListDiv = Components.div().styleName("transfer-list__panel-list").build();

        Div selectedPanel = Components.div().add(selectedHeader, selectedListDiv).styleName("transfer-list__panel").build();

        // ── Assembly ──────────────────────────────────────────────────────────
        add(availablePanel, controls, selectedPanel);

        // Initial render
        renderAvailableList();
        renderSelectedList();
        updateCounters();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Sets the initial items in the "Available" (left) panel.
     * Clears both panels' highlights. Does not clear already-selected items.
     *
     * @param items the items to show as available (not null)
     */
    public void setAvailableItems(Collection<TransferItem> items) {
        Objects.requireNonNull(items, "items must not be null");
        availableItems.clear();
        availableItems.addAll(items);
        highlightedAvailable.clear();
        renderAvailableList();
        updateCounters();
    }

    /**
     * Pre-populates the "Selected" (right) panel with the given items.
     * The same items are removed from the available list if present.
     *
     * @param items the items to pre-select (not null)
     */
    public void setSelectedItems(Collection<TransferItem> items) {
        Objects.requireNonNull(items, "items must not be null");
        selectedItems.clear();
        selectedItems.addAll(items);
        // Remove from available (avoid duplicates)
        Set<String> selectedIds = new HashSet<>();
        items.forEach(i -> selectedIds.add(i.id()));
        availableItems.removeIf(i -> selectedIds.contains(i.id()));
        highlightedSelected.clear();
        renderAvailableList();
        renderSelectedList();
        updateCounters();
    }

    /**
     * Sets the title label above the "Available" panel.
     *
     * @param title the panel title (not null)
     */
    public void setAvailableTitle(String title) {
        availableTitleSpan.setText(title);
    }

    /**
     * Sets the title label above the "Selected" panel.
     *
     * @param title the panel title (not null)
     */
    public void setSelectedTitle(String title) {
        selectedTitleSpan.setText(title);
    }

    /**
     * Returns an unmodifiable snapshot of items currently in the "Available" panel.
     *
     * @return available items (never null)
     */
    public List<TransferItem> getAvailableItems() {
        return List.copyOf(availableItems);
    }

    /**
     * Returns an unmodifiable snapshot of items currently in the "Selected" panel.
     *
     * @return selected items (never null)
     */
    public List<TransferItem> getSelectedItems() {
        return List.copyOf(selectedItems);
    }

    /**
     * Registers a listener invoked whenever items are transferred between panels.
     *
     * @param listener the transfer event listener (not null)
     * @return registration for removing the listener
     */
    public Registration addTransferListener(ComponentEventListener<TransferEvent> listener) {
        return addListener(TransferEvent.class, listener);
    }

    // ── Static factory ────────────────────────────────────────────────────────

    /**
     * Returns a new Holon fluent {@link TransferListBuilder}.
     *
     * <pre>{@code
     * TransferList list = TransferList.builder()
     *     .availableItems(items)
     *     .selectedTitle("Ordered")
     *     .onTransfer(e -> save(e.getSelectedItems()))
     *     .build();
     * }</pre>
     *
     * @return a new builder
     */
    public static TransferListBuilder builder() {
        return TransferListBuilder.create();
    }

    // ── Transfer operations ───────────────────────────────────────────────────

    /**
     * Moves all highlighted items in the Available panel to the Selected panel.
     */
    public void moveHighlightedRight() {
        List<TransferItem> toMove = drainHighlighted(availableItems, highlightedAvailable);
        if (toMove.isEmpty()) return;
        selectedItems.addAll(toMove);
        renderAvailableList();
        renderSelectedList();
        updateCounters();
        fireTransferEvent();
    }

    /**
     * Moves all items from Available to Selected.
     */
    public void moveAllRight() {
        if (availableItems.isEmpty()) return;
        selectedItems.addAll(availableItems);
        availableItems.clear();
        highlightedAvailable.clear();
        renderAvailableList();
        renderSelectedList();
        updateCounters();
        fireTransferEvent();
    }

    /**
     * Moves all highlighted items in the Selected panel back to Available.
     */
    public void moveHighlightedLeft() {
        List<TransferItem> toMove = drainHighlighted(selectedItems, highlightedSelected);
        if (toMove.isEmpty()) return;
        availableItems.addAll(toMove);
        renderAvailableList();
        renderSelectedList();
        updateCounters();
        fireTransferEvent();
    }

    /**
     * Moves all items from Selected back to Available.
     */
    public void moveAllLeft() {
        if (selectedItems.isEmpty()) return;
        availableItems.addAll(selectedItems);
        selectedItems.clear();
        highlightedSelected.clear();
        renderAvailableList();
        renderSelectedList();
        updateCounters();
        fireTransferEvent();
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    private void renderAvailableList() {
        availableListDiv.removeAll();
        if (availableItems.isEmpty()) {
            availableListDiv.add(Components.span().text("No items available").styleName("transfer-list__empty").build());
        } else {
            availableItems.forEach(item -> availableListDiv.add(
                    buildItemRow(item, highlightedAvailable, id -> toggleAvailableHighlight(id))));
        }
        updateMoveButtonStates();
    }

    private void renderSelectedList() {
        selectedListDiv.removeAll();
        if (selectedItems.isEmpty()) {
            selectedListDiv.add(Components.span().text("No items selected").styleName("transfer-list__empty").build());
        } else {
            selectedItems.forEach(item -> selectedListDiv.add(
                    buildItemRow(item, highlightedSelected, id -> toggleSelectedHighlight(id))));
        }
        updateMoveButtonStates();
    }

    private Div buildItemRow(TransferItem item, Set<String> highlighted,
                              java.util.function.Consumer<String> toggleFn) {
        boolean isHighlighted = highlighted.contains(item.id());

        // Check box visual
        Div checkBox = Components.div().styleName("transfer-list__item-check").build();
        if (isHighlighted) {
            checkBox.add(VaadinIcon.CHECK.create());
        }

        Span labelSpan = Components.span().text(item.label()).styleName("transfer-list__item-label").build();

        Div row = Components.div().add(checkBox, labelSpan).styleName("transfer-list__item").build();
        if (isHighlighted) row.addClassName("transfer-list__item--highlighted");
        row.addClickListener(e -> toggleFn.accept(item.id()));

        return row;
    }

    private void updateCounters() {
        availableCountSpan.setText(String.valueOf(availableItems.size()));
        selectedCountSpan.setText(String.valueOf(selectedItems.size()));
    }

    private void updateMoveButtonStates() {
        moveRightBtn.setEnabled(!highlightedAvailable.isEmpty());
        moveAllRightBtn.setEnabled(!availableItems.isEmpty());
        moveLeftBtn.setEnabled(!highlightedSelected.isEmpty());
        moveAllLeftBtn.setEnabled(!selectedItems.isEmpty());
    }

    // ── Highlight toggles ─────────────────────────────────────────────────────

    private void toggleAvailableHighlight(String id) {
        toggle(highlightedAvailable, id);
        renderAvailableList();
    }

    private void toggleSelectedHighlight(String id) {
        toggle(highlightedSelected, id);
        renderSelectedList();
    }

    private static void toggle(Set<String> set, String id) {
        if (!set.remove(id)) set.add(id);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /** Removes and returns all items whose id is in {@code highlighted}. */
    private static List<TransferItem> drainHighlighted(List<TransferItem> source, Set<String> highlighted) {
        List<TransferItem> result = source.stream()
                .filter(i -> highlighted.contains(i.id()))
                .toList();
        source.removeIf(i -> highlighted.contains(i.id()));
        highlighted.clear();
        return result;
    }

    private static Button buildCtrlButton(VaadinIcon icon, String ariaLabel) {
        Button btn = Components.button().icon(icon).tertiary().styleName("transfer-list__ctrl-btn").ariaLabel(ariaLabel).build();
        btn.setEnabled(false); // initially disabled — enabled by updateMoveButtonStates
        return btn;
    }

    private void fireTransferEvent() {
        fireEvent(new TransferEvent(this, false, List.copyOf(availableItems), List.copyOf(selectedItems)));
    }

    // ── Events ────────────────────────────────────────────────────────────────

    /**
     * Fired whenever items are transferred between the Available and Selected panels.
     */
    public static class TransferEvent extends ComponentEvent<TransferList> {

        private static final long serialVersionUID = 1L;

        private final List<TransferItem> availableItems;
        private final List<TransferItem> selectedItems;

        /**
         * @param source    the transfer list
         * @param fromClient always {@code false}
         * @param available  current available items snapshot
         * @param selected   current selected items snapshot
         */
        public TransferEvent(TransferList source, boolean fromClient,
                             List<TransferItem> available, List<TransferItem> selected) {
            super(source, fromClient);
            this.availableItems = available;
            this.selectedItems = selected;
        }

        /**
         * Returns the items currently in the Available panel.
         *
         * @return immutable list (never null)
         */
        public List<TransferItem> getAvailableItems() {
            return availableItems;
        }

        /**
         * Returns the items currently in the Selected panel.
         *
         * @return immutable list (never null)
         */
        public List<TransferItem> getSelectedItems() {
            return selectedItems;
        }
    }
}

