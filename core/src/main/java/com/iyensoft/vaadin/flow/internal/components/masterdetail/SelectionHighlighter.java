package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.holonplatform.vaadin.flow.components.ItemListing;

import java.util.Objects;
import java.util.function.Function;

/**
 * Manages the {@code mdl-selected} CSS part-name marker on the currently selected
 * grid row so external stylesheets can highlight it
 * (e.g. {@code .mdl-master-grid::part(mdl-selected)} in {@code master-detail-v2.css}).
 *
 * <h3>How it works</h3>
 * <p>A single part-name generator is installed at construction via
 * {@link ItemListing#setPartNameGenerator}. On each {@link #setHighlighted} call
 * {@link #currentKey} is updated and the generator is reinstalled — this triggers
 * {@code DataCommunicator.reset()} inside Vaadin Grid, causing all visible rows to
 * re-evaluate their part names against the new key.</p>
 *
 * <h3>Why not refreshItem?</h3>
 * <p>The listing bundle uses a lazy {@code CallbackDataProvider} without an identifier
 * getter (created by {@code ItemListingPageSizeSelector.withLazyFetch}).  On such
 * providers, {@code DataProvider.refreshItem(item)} fires a {@code DataRefreshEvent}
 * without throwing, but the {@code DataCommunicator} silently ignores it because it
 * cannot match the item to a cached index.  Always reinstalling the generator is
 * therefore the only reliable approach for this data provider type.</p>
 *
 * <p>Safe with any bean type regardless of {@code equals}/{@code hashCode}
 * — or use the {@link #SelectionHighlighter(ItemListing, Function)} overload to compare
 * by a stable key such as the entity id.</p>
 *
 * @param <T> the row item type
 */
public final class SelectionHighlighter<T> {

    private static final String SELECTED_PART = "mdl-selected";

    private final ItemListing<T, ?> listing;
    private final Function<T, ?> keyExtractor;
    private final String selectedPartName;
    private T currentItem;
    private Object currentKey;

    public SelectionHighlighter(ItemListing<T, ?> listing) {
        this(listing, Function.identity(), SELECTED_PART);
    }

    public SelectionHighlighter(ItemListing<T, ?> listing, Function<T, ?> keyExtractor) {
        this(listing, keyExtractor, SELECTED_PART);
    }

    /**
     * Full constructor allowing a custom CSS part name for the selected row.
     *
     * @param listing          the item listing to manage highlighting on
     * @param keyExtractor     stable key extractor for item identity (null → identity)
     * @param selectedPartName CSS part name added to the selected row
     *                         (null → defaults to {@value #SELECTED_PART})
     */
    public SelectionHighlighter(ItemListing<T, ?> listing, Function<T, ?> keyExtractor,
                                String selectedPartName) {
        this.listing = listing;
        this.keyExtractor = keyExtractor != null ? keyExtractor : Function.identity();
        this.selectedPartName = selectedPartName != null ? selectedPartName : SELECTED_PART;
        listing.setPartNameGenerator(item -> Objects.equals(currentKey, keyOf(item)) ? this.selectedPartName : null);
    }

    /**
     * Marks {@code next} as the highlighted row and clears the previous highlight.
     * {@code null} clears the highlight entirely.
     *
     * <p>Always reinstalls the part-name generator so Vaadin's DataCommunicator
     * re-evaluates part names for all currently visible rows.  This is the only
     * reliable strategy when the underlying data provider is a lazy
     * {@code CallbackDataProvider} without an identifier getter (e.g. the one
     * created by {@code ItemListingPageSizeSelector.withLazyFetch}): in that case
     * {@code refreshItem} fires a {@code DataRefreshEvent} normally (no exception)
     * but the DataCommunicator silently ignores it because it cannot match the item
     * to a cached index — leaving the row's part name stale.</p>
     */
    public void setHighlighted(T next) {
        currentItem = next;
        currentKey = keyOf(next);
        // Always reinstall: Grid.setPartNameGenerator triggers DataCommunicator.reset()
        // which re-evaluates the generator for every visible row.  For a paginated
        // master list (typically 10–50 rows) this is O(visible rows) but happens only
        // on an explicit user click — the overhead is imperceptible.
        listing.setPartNameGenerator(item -> Objects.equals(currentKey, keyOf(item)) ? selectedPartName : null);
    }

    private Object keyOf(T item) {
        return item != null ? keyExtractor.apply(item) : null;
    }
}

