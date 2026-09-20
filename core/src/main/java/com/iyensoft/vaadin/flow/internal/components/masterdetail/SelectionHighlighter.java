package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.util.Objects;

import com.vaadin.flow.function.SerializableFunction;

/**
 * Manages the {@code mdl-selected} CSS part-name marker on the currently selected
 * grid row so external stylesheets can highlight it
 * (e.g. {@code .mdl-master-grid::part(mdl-selected)} in {@code master-detail-v2.css}).
 *
 * <h3>How it works</h3>
 * <p>A single part-name generator is installed at construction via
 * {@link ItemListing#setPartNameGenerator}. The selected item is held in a
 * {@link ValueSignal}; {@link #setHighlighted} does nothing but write to it. A
 * {@link Signal#effect(Component, com.vaadin.flow.signals.function.EffectAction) signal
 * effect} bound to the Grid reacts to that write and refreshes only the two affected rows
 * (the previously and the newly highlighted one), so no backend re-query is issued.</p>
 *
 * <p>The signal is created with a key-based equality predicate, so re-selecting the same
 * row — even via a different bean instance returned by a later fetch — is not a change and
 * the effect does not run at all.</p>
 *
 * <p>Because the effect is bound to the Grid's lifecycle it is only active while the Grid
 * is attached and is disposed automatically on detach. Selections made before attach (such
 * as the initial row opened on desktop) are simply picked up by the first effect run, with
 * no wasted refresh against a Grid that has not rendered yet.</p>
 *
 * <h3>Row refresh strategy</h3>
 * <p>Vaadin's {@code DataCommunicator} can only honour {@code refreshItem} when it is able
 * to match the item against its key-mapper cache. For the lazy {@code CallbackDataProvider}
 * created by {@code ItemListingPageSizeSelector.withLazyFetch} every fetch returns fresh
 * bean instances, so the default identity (object {@code equals}) never matches and the
 * refresh is silently dropped.</p>
 *
 * <p>When a stable key extractor is supplied (e.g. {@code Product::getId}, either explicitly
 * via {@code MasterDetailConfigurator#withSelectionKey} or auto-detected from the bean's
 * identifier), this class installs it as the Grid's
 * {@code IdentifierProvider}. {@code refreshItem} then matches by key and re-serializes the
 * affected rows straight from the cache — the part names are re-evaluated <em>without</em>
 * hitting the data source.</p>
 *
 * <p>Without a stable key the class falls back to reinstalling the part-name generator,
 * which triggers {@code DataCommunicator.reset()} and therefore a full re-fetch of the
 * visible page. That is correct but costs one query per click, hence the key extractor is
 * strongly recommended.</p>
 *
 * <p>Safe with any bean type regardless of {@code equals}/{@code hashCode}
 * — or use the {@link #SelectionHighlighter(ItemListing, SerializableFunction)} overload to compare
 * by a stable key such as the entity id.</p>
 *
 * @param <T> the row item type
 */
public final class SelectionHighlighter<T> implements java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private static final String SELECTED_PART = "mdl-selected";

    private final ItemListing<T, ?> listing;
    private final SerializableFunction<T, ?> keyExtractor;
    private final boolean stableKey;
    private final String selectedPartName;
    private final SerializableFunction<T, String> statusPartNameGenerator;
    private final ValueSignal<T> selection;
    private final boolean reactive;
    private T rendered;
    private DataProvider<T, ?> identifiedProvider;

    public SelectionHighlighter(ItemListing<T, ?> listing) {
        this(listing, t -> t, SELECTED_PART, null);
    }

    public SelectionHighlighter(ItemListing<T, ?> listing, SerializableFunction<T, ?> keyExtractor) {
        this(listing, keyExtractor, SELECTED_PART, null);
    }

    /**
     * Full constructor allowing a custom CSS part name for the selected row.
     *
     * @param listing          the item listing to manage highlighting on
     * @param keyExtractor     stable key extractor for item identity (null → identity)
     * @param selectedPartName CSS part name added to the selected row
     *                         (null → defaults to {@value #SELECTED_PART})
     */
    public SelectionHighlighter(ItemListing<T, ?> listing, SerializableFunction<T, ?> keyExtractor,
                                String selectedPartName) {
        this(listing, keyExtractor, selectedPartName, null);
    }

    /**
     * Full constructor allowing a custom CSS part name for the selected row and an
     * additional, always-on per-item CSS part name — independent of selection — so a
     * business/status condition can be styled across every cell of the row too (see
     * {@code MasterDetailConfigurator#withRowPartNameGenerator}).
     *
     * <p>When both the selection part and the status part apply to the same row, both
     * part names are added to the cells (space separated), so neither rule shadows
     * the other in CSS.</p>
     *
     * @param listing                 the item listing to manage highlighting on
     * @param keyExtractor            stable key extractor for item identity (null → identity)
     * @param selectedPartName        CSS part name added to the selected row
     *                                (null → defaults to {@value #SELECTED_PART})
     * @param statusPartNameGenerator optional function mapping an item to an always-on
     *                                CSS part name (null, or a null-returning function,
     *                                disables this behaviour)
     */
    public SelectionHighlighter(ItemListing<T, ?> listing, SerializableFunction<T, ?> keyExtractor,
                                String selectedPartName, SerializableFunction<T, String> statusPartNameGenerator) {
        this.listing = listing;
        this.stableKey = keyExtractor != null;
        this.keyExtractor = keyExtractor != null ? keyExtractor : t -> t;
        this.selectedPartName = selectedPartName != null ? selectedPartName : SELECTED_PART;
        this.statusPartNameGenerator = statusPartNameGenerator;
        // Key-based equality: a re-click on the selected row is not a signal change, even
        // when the lazy data provider hands out a fresh bean instance for the same id.
        this.selection = new ValueSignal<>(null, (a, b) -> Objects.equals(keyOf(a), keyOf(b)));
        listing.setPartNameGenerator(this::partNameOf);
        this.reactive = installEffect();
    }

    /**
     * Binds the selection signal to the Grid so that writes to it re-render only the rows
     * that changed.
     *
     * @return {@code true} when the reactive path is available, {@code false} when the
     *         caller must fall back to reinstalling the part-name generator
     */
    private boolean installEffect() {
        final Component component = listing.getComponent();
        if (!stableKey || !(component instanceof Grid)) {
            return false;
        }
        Signal.effect(component, this::applySelection);
        return true;
    }

    /**
     * Effect body: re-serializes the row losing the highlight and the row gaining it.
     * Runs on the first Grid attach and on every selection change thereafter.
     */
    private void applySelection() {
        final T next = selection.get();
        final T previous = rendered;
        rendered = next;
        final Grid<T> grid = identifierAwareGrid();
        if (grid == null) {
            return;
        }
        // Refresh through the data provider directly (not ItemListing#refreshItem) to
        // avoid its cancelEditing() side effect on editable listings.
        if (previous != null) {
            grid.getDataProvider().refreshItem(previous);
        }
        if (next != null) {
            grid.getDataProvider().refreshItem(next);
        }
    }

    /**
     * Returns the read-only signal holding the highlighted item ({@code null} = none),
     * so callers can derive further reactive state from the master selection.
     */
    public Signal<T> selectionSignal() {
        return selection.asReadonly();
    }

    /**
     * Marks {@code next} as the highlighted row and clears the previous highlight.
     * {@code null} clears the highlight entirely.
     *
     * <p>On the reactive path this is a single signal write; the effect bound to the Grid
     * then refreshes the two affected rows through the identifier-aware
     * {@code refreshItem}, re-evaluating their part names from the key-mapper cache with
     * <strong>no data-source query</strong>. Re-selecting the already highlighted row is
     * not a change and does nothing at all.</p>
     *
     * <p>Without a stable key the part-name generator is reinstalled instead, which
     * triggers {@code DataCommunicator.reset()} and a full re-fetch of the visible page.
     * This is the only reliable strategy when items cannot be identified across fetches,
     * because {@code refreshItem} would otherwise be silently ignored and the row's part
     * name would go stale.</p>
     */
    public void setHighlighted(T next) {
        selection.set(next);
        if (!reactive) {
            // Fallback: Grid.setPartNameGenerator triggers DataCommunicator.reset() which
            // re-evaluates the generator for every visible row (at the cost of a re-fetch).
            listing.setPartNameGenerator(this::partNameOf);
        }
    }

    /**
     * @return the backing Grid once its {@code IdentifierProvider} is in place,
     *         or {@code null} when identity-based refresh is not available
     */
    @SuppressWarnings("unchecked")
    private Grid<T> identifierAwareGrid() {
        final Component component = listing.getComponent();
        if (!stableKey || !(component instanceof Grid)) {
            return null;
        }
        final Grid<T> grid = (Grid<T>) component;
        try {
            final DataProvider<T, ?> provider = grid.getDataProvider();
            if (provider == null) {
                return null;
            }
            // The data provider is replaced whenever the page size changes
            // (ItemListingPageSizeSelector) and Vaadin resets the identifier getter along
            // with it, so the identifier is re-applied on provider change.
            if (provider != identifiedProvider) {
                grid.getGenericDataView().setIdentifierProvider(this::keyOf);
                identifiedProvider = provider;
            }
            return grid;
        } catch (RuntimeException e) {
            // No data provider bound yet, or an unsupported data view type: keep the
            // generator-reinstall fallback.
            return null;
        }
    }

    /** Combines the selection part name (if this item is selected) with the status part name. */
    private String partNameOf(T item) {
        // peek(): the generator runs during row serialization, never as a signal dependency.
        boolean selected = Objects.equals(keyOf(selection.peek()), keyOf(item));
        String status = statusPartNameGenerator != null ? statusPartNameGenerator.apply(item) : null;
        if (selected && status != null) {
            return selectedPartName + " " + status;
        }
        if (selected) {
            return selectedPartName;
        }
        return status;
    }

    private Object keyOf(T item) {
        return item != null ? keyExtractor.apply(item) : null;
    }
}
