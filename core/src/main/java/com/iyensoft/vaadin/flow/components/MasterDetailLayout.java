package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.UrlSelectionSync;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A master-detail layout component that owns all selection and sync state.
 *
 * <p>Build via {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder#create()}.
 * Configure an existing instance via
 * {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator#configure(MasterDetailLayout)}.</p>
 *
 * <p>Post-build runtime operations:</p>
 * <ul>
 *   <li>{@link #selectFirst(ViewMode)} — auto-open first row on desktop</li>
 *   <li>{@link #restoreFromUrl(String)} — restore deep-link selection</li>
 *   <li>{@link #pushUrlState(Element, Object, ViewMode)} / {@link #clearUrlState(Element, ViewMode)} — URL sync</li>
 *   <li>{@link #notifyDataChanged()} — re-fire sync after a save</li>
 *   <li>{@link #clearSelection()} — reset highlight and signal</li>
 *   <li>{@link #selectionSignal()} — reactive {@link Signal} for the selected item</li>
 * </ul>
 *
 * @param <T> the item type of the master listing
 */
public class MasterDetailLayout<T> extends Div {

    private static final String CLASS_NAME = "master-detail-container";

    // ── Runtime state ───────────────────────────────────────────────────────────

    private final List<Consumer<T>> syncDispatchers = new ArrayList<>();
    private T currentItem;
    private ListingBundle<T> masterBundle;
    private SelectionHighlighter<T> masterHighlighter;
    private UrlSelectionSync<T> urlSync;
    private ValueSignal<T> selectedSignal;  // null until first selectionSignal() call
    private String currentAccentClass;       // tracks the active mdl-accent--* class

    // ── Constructor ─────────────────────────────────────────────────────────────

    public MasterDetailLayout() {

        Components.configure(this)
                .styleName(CLASS_NAME)
                .elementConfiguration(element -> element.setAttribute("role", "group"));
    }

    // ── Setup methods — called by the configurator during build phase ───────────

    /** Registers a detail-sync handler. Called by {@code DefaultDetailNode.add()}. */
    public void addSyncDispatcher(Consumer<T> dispatcher) {
        syncDispatchers.add(dispatcher);
    }

    /** Stores the master listing bundle for {@link #selectFirst} and {@link #notifyDataChanged}. */
    public void setMasterBundle(ListingBundle<T> bundle) {
        this.masterBundle = bundle;
    }

    /** Stores the row highlighter for selection visual feedback. */
    public void setMasterHighlighter(SelectionHighlighter<T> highlighter) {
        this.masterHighlighter = highlighter;
    }

    /** Wires URL {@code ?id=} sync. Called by {@code withUrlSync(...)}. */
    public void setUrlSync(UrlSelectionSync<T> urlSync) {
        this.urlSync = urlSync;
    }

    /**
     * Swaps the active accent CSS class on this container.
     * Called by the item-click listener when {@code withAccentColorProvider} is configured.
     * Removes the previous accent class (if any) and adds the new one.
     *
     * @param cssClass the CSS class to apply, or {@code null} to revert to the default variable
     */
    public void setAccentClass(String cssClass) {
        if (currentAccentClass != null) {
            removeClassName(currentAccentClass);
        }
        currentAccentClass = cssClass;
        if (cssClass != null && !cssClass.isBlank()) {
            addClassName(cssClass);
        }
    }

    /**
     * Fires all registered sync handlers with the given item.
     * Called by the item-click listener registered in {@code DefaultMasterNode}.
     */
    public void dispatchSync(T item) {
        currentItem = item;
        if (selectedSignal != null) selectedSignal.set(item);
        syncDispatchers.forEach(d -> d.accept(item));
    }

    // ── Runtime operations ──────────────────────────────────────────────────────

    /**
     * Selects and displays the first item of the master listing. Desktop only — no-op on mobile.
     *
     * @param mode the current viewport mode (never auto-detected)
     */
    public void selectFirst(ViewMode mode) {
        if (mode == null || mode.isMobile() || masterBundle == null) return;
        masterBundle.listing().getFirstItem().ifPresent(item -> {
            if (masterHighlighter != null) masterHighlighter.setHighlighted(item);
            dispatchSync(item);
        });
    }

    /**
     * Writes {@code ?id=<id>} to the browser URL via {@code history.replaceState}.
     * Desktop only — no-op when {@code mode} is mobile.
     */
    public void pushUrlState(Element host, T item, ViewMode mode) {
        if (urlSync != null) urlSync.pushId(host, item, mode);
    }

    /**
     * Removes the {@code ?id=} parameter from the browser URL.
     * Desktop only — no-op when {@code mode} is mobile.
     */
    public void clearUrlState(Element host, ViewMode mode) {
        if (urlSync != null) urlSync.clearId(host, mode);
    }

    /**
     * Looks up the item by {@code id} (using the loader from {@code withUrlSync})
     * and selects it — fires highlight + all sync handlers.
     * No-op if URL sync was not configured or {@code id} is blank.
     */
    public void restoreFromUrl(String id) {
        if (urlSync == null) return;
        urlSync.restore(id, item -> {
            if (masterHighlighter != null) masterHighlighter.setHighlighted(item);
            dispatchSync(item);
        });
    }

    /**
     * Clears the current selection: un-highlights the master row and resets the
     * reactive signal to {@code null}. Does not touch the URL.
     */
    public void clearSelection() {
        currentItem = null;
        if (masterHighlighter != null) masterHighlighter.setHighlighted(null);
        if (selectedSignal != null) selectedSignal.set(null);
    }

    /**
     * Re-fires all detail-sync handlers with the currently selected item.
     * Also refreshes the master listing row so it reflects any persisted changes.
     * No-op if nothing has been selected yet.
     */
    public void notifyDataChanged() {
        if (currentItem == null) return;
        dispatchSync(currentItem);
        if (masterBundle != null) {
            try { masterBundle.listing().refreshItem(currentItem); }
            catch (UnsupportedOperationException ignored) {}
        }
    }

    /**
     * Returns the reactive signal holding the currently selected item
     * ({@code null} = nothing selected).
     * Lazily initialized — zero overhead until the first subscriber.
     */
    public Signal<T> selectionSignal() {
        if (selectedSignal == null) selectedSignal = new ValueSignal<>(null);
        return selectedSignal;
    }
}
