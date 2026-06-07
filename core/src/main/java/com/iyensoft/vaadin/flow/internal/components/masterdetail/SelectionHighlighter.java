package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.vaadin.flow.component.grid.Grid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * Manages the {@code mdl-selected} CSS part-name marker on the currently selected
 * grid row so external stylesheets can highlight it
 * (e.g. {@code .mdl-master-grid::part(mdl-selected)} in {@code master-detail-layout.css}).
 *
 * <p>Installs a {@link Grid#setPartNameGenerator} on construction and exposes a
 * single {@link #setHighlighted} method that swaps the highlight from the previous
 * to the next row, refreshing both rows so the part-name generator re-evaluates.</p>
 *
 * <p>Safe with JPA entities provided they implement {@code equals}/{@code hashCode}
 * — or use the {@link #SelectionHighlighter(Grid, Function)} overload to compare by
 * a stable key such as the entity id.</p>
 *
 * @param <T> the row item type
 */
public final class SelectionHighlighter<T> {

    private static final Logger log = LoggerFactory.getLogger(SelectionHighlighter.class);

    private static final String SELECTED_PART = "mdl-selected";

    private final Grid<T> grid;
    private final Function<T, ?> keyExtractor;
    private T currentItem;
    private Object currentKey;

    public SelectionHighlighter(Grid<T> grid) {
        this(grid, Function.identity());
    }

    public SelectionHighlighter(Grid<T> grid, Function<T, ?> keyExtractor) {
        this.grid = grid;
        this.keyExtractor = keyExtractor != null ? keyExtractor : Function.identity();
        grid.setPartNameGenerator(item -> Objects.equals(currentKey, keyOf(item)) ? SELECTED_PART : null);
    }

    /**
     * Marks {@code next} as the highlighted row and clears the highlight from the
     * previously highlighted row. {@code null} clears the highlight entirely.
     */
    public void setHighlighted(T next) {
        T prev = currentItem;
        currentItem = next;
        currentKey = keyOf(next);
        if (prev != null) {
            grid.setPartNameGenerator(item ->  null);
        }
        if (next != null) {
            try {
                grid.setPartNameGenerator(item -> Objects.equals(currentKey, keyOf(item)) ? SELECTED_PART : null);
            } catch (Exception ex) {
                log.debug("refreshItem not supported by data provider for {}: {}", next, ex.getMessage());
            }
        }
    }

    private Object keyOf(T item) {
        return item != null ? keyExtractor.apply(item) : null;
    }
}

