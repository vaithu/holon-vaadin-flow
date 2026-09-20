package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.UrlSelectionSync;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ItemIndexProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;

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
 *   <li>{@link #getCurrentItem()} / {@link #addLateSyncDispatcher(SerializableConsumer)} — for content built
 *       after selection already happened (e.g. a {@code LazyTabsBuilder} tab realized on first click)</li>
 * </ul>
 *
 * @param <T> the item type of the master listing
 */
public class MasterDetailLayout<T> extends Div {

    private static final String CLASS_NAME = "master-detail-container";

    private static final Logger LOGGER = Logger.getLogger(MasterDetailLayout.class.getName());

    // ── Runtime state ───────────────────────────────────────────────────────────

    /**
     * Detail-sync handlers, invoked whenever the selection changes.
     *
     * <p>Declared as {@link SerializableConsumer} rather than {@link Consumer}: this list lives on
     * a {@link Div}, i.e. inside the server-side component tree that Vaadin keeps in the
     * {@code VaadinSession}. A plain {@code java.util.function.Consumer} lambda is not
     * {@code Serializable}, so as soon as a deployment enables session replication or disk
     * passivation — the norm once a single node can no longer hold every user's session — writing
     * the session out fails with {@code NotSerializableException} and the user loses their UI.</p>
     */
    private final List<SerializableConsumer<T>> syncDispatchers = new ArrayList<>();
    private T currentItem;
    private ListingBundle<T> masterBundle;
    private SelectionHighlighter<T> masterHighlighter;
    private UrlSelectionSync<T> urlSync;
    /** Serializable for the same reason as {@link #syncDispatchers}. */
    private SerializableSupplier<Optional<T>> initialItemSupplier;
    private volatile ValueSignal<T> selectedSignal;  // null until first selectionSignal() call
    private String currentAccentClass;       // tracks the active mdl-accent--* class
    /** Serializable for the same reason as {@link #syncDispatchers}. */
    private SerializableFunction<T, String> accentColorProvider;
    private ViewMode viewMode;
    private ItemIndexProvider<T, ?> itemIndexProvider;
    private boolean itemIndexProviderInstalled;
    private boolean autoSelectEnabled;
    private boolean autoSelectApplied;
    private String autoSelectTarget;         // last applied ?id= value (null = "first row")
    private Registration navigationRegistration;

    /** CSS width used for the master panel when the desktop media query applies. */
    private String desktopMasterWidth;

    // ── Constructor ─────────────────────────────────────────────────────────────

    public MasterDetailLayout() {

        Components.configure(this)
                .styleName(CLASS_NAME)
                .elementConfiguration(element -> element.setAttribute("role", "group"));
    }

    // ── Setup methods — called by the configurator during build phase ───────────

    /** Registers a detail-sync handler. Called by {@code DefaultDetailNode.add()}. */
    public void addSyncDispatcher(SerializableConsumer<T> dispatcher) {
        syncDispatchers.add(dispatcher);
    }

    /**
     * Returns the currently selected item, if any.
     * Use this from content built after selection already happened (e.g. inside a
     * lazily-realized tab) to pull the current state instead of waiting for the next
     * {@link #dispatchSync(Object)}.
     */
    public Optional<T> getCurrentItem() {
        return Optional.ofNullable(currentItem);
    }

    /**
     * Registers a detail-sync handler like {@link #addSyncDispatcher(SerializableConsumer)}, but also
     * immediately invokes it with the current item if one is already selected.
     *
     * <p>Intended for content built <em>after</em> selection already happened — most notably
     * a {@code LazyTabsBuilder.withLazyTab(...)} supplier: since the tab component doesn't
     * exist yet when the detail panel is wired, it would otherwise miss the item that was
     * selected before the tab was first opened.</p>
     */
    public void addLateSyncDispatcher(SerializableConsumer<T> dispatcher) {
        syncDispatchers.add(dispatcher);
        if (currentItem != null) {
            dispatcher.accept(currentItem);
        }
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
     * Stores the per-item accent CSS class provider so that <em>every</em> selection path —
     * row click, {@link #selectFirst(ViewMode)} and {@link #restoreFromUrl(String)} — applies
     * it. Called by {@code withAccentColorProvider(...)}.
     */
    public void setAccentColorProvider(SerializableFunction<T, String> accentColorProvider) {
        this.accentColorProvider = accentColorProvider;
    }

    /**
     * Selects {@code item}: highlights its master row, applies the accent class, fires all
     * detail-sync handlers and writes the {@code ?id=} URL state.
     *
     * <p>This is the single entry point for selection — {@link #selectFirst(ViewMode)},
     * {@link #restoreFromUrl(String)} and the master listing's item-click listener all route
     * through it, so a row opened by a deep link is indistinguishable from one opened by a
     * click. Prefer it over calling {@link #dispatchSync(Object)} directly, which only fires
     * the detail handlers and leaves highlight, accent and URL untouched.</p>
     *
     * @param item the item to select (not null)
     */
    public void selectItem(T item) {
        // No scroll: the row was clicked, so it is already in the viewport.
        applySelection(item, true, Scroll.NONE);
    }

    /** How a selection should reposition the master listing viewport. */
    private enum Scroll {
        /** Leave the viewport alone — the row was clicked, so it is already visible. */
        NONE,
        /** Jump to the top; free, and correct for {@link #selectFirst(ViewMode)}. */
        START,
        /** Locate the row and bring it into view; needs an {@link ItemIndexProvider} when lazy. */
        ITEM
    }

    /**
     * Applies every side effect of a selection in a fixed order.
     *
     * @param item    the newly selected item
     * @param pushUrl whether to reflect the selection in the browser URL
     * @param scroll  how to reposition the master listing viewport
     */
    private void applySelection(T item, boolean pushUrl, Scroll scroll) {
        if (masterHighlighter != null) masterHighlighter.setHighlighted(item);
        if (accentColorProvider != null) {
            setAccentClass(item != null ? accentColorProvider.apply(item) : null);
        }
        dispatchSync(item);
        if (pushUrl) pushUrlState(item);
        scrollMasterTo(item, scroll);
    }

    /**
     * Scrolls the master listing so the selected row is visible.
     *
     * <p>Grids render only a window of rows, so a programmatically selected item — a deep
     * link to an item far down the list, for instance — may otherwise never appear, leaving
     * the detail panel populated with no visible highlight.</p>
     *
     * <p>On a lazy data view Vaadin can only resolve a row index through an
     * {@link ItemIndexProvider}; register one with
     * {@link #setItemIndexProvider(ItemIndexProvider)} (builder:
     * {@code withItemIndexProvider(...)}). Scrolling is a best-effort nicety rather than a
     * correctness requirement, so when no provider is configured — or the item cannot be
     * located — the request is silently dropped and the row simply stays out of view.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void scrollMasterTo(T item, Scroll scroll) {
        if (scroll == Scroll.NONE || masterBundle == null) return;
        if (!(masterBundle.listing().getComponent() instanceof Grid<?> raw)) return;
        final Grid<T> grid = (Grid<T>) raw;
        try {
            if (scroll == Scroll.START) {
                // The first row is index 0 by definition, so no index lookup is needed.
                grid.scrollToStart();
                return;
            }
            if (item == null) return;
            if (itemIndexProvider != null && !itemIndexProviderInstalled) {
                grid.getLazyDataView().setItemIndexProvider((ItemIndexProvider) itemIndexProvider);
                itemIndexProviderInstalled = true;
            }
            grid.scrollToItem(item);
        } catch (IllegalStateException | IllegalArgumentException | UnsupportedOperationException e) {
            // Not a lazy data view, no index provider, or the item is not resolvable.
            // Scrolling is cosmetic, so the selection itself must still succeed.
            LOGGER.log(Level.FINE, e, () -> "Could not scroll the master listing to the selected item");
        }
    }

    /**
     * Registers the callback that maps a selected item to its row index in the master
     * listing, enabling {@link #selectFirst(ViewMode)} and {@link #restoreFromUrl(String)} to
     * scroll that row into view on a lazily loaded Grid. Called by
     * {@code withItemIndexProvider(...)}.
     *
     * <p>The callback is invoked at most once per programmatic selection, and only when the
     * row needs locating — a row click never triggers it. Return {@code null} when the item
     * is not part of the current data set.</p>
     */
    public void setItemIndexProvider(ItemIndexProvider<T, ?> itemIndexProvider) {
        this.itemIndexProvider = itemIndexProvider;
        this.itemIndexProviderInstalled = false;
    }

    /**
     * Stores the viewport mode the layout was built for, so runtime operations such as
     * {@link #enableAutoSelect()} know whether they are running on a mobile viewport.
     * Called by {@code viewMode(...)}.
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    /**
     * Enables automatic initial selection: on attach, and on every subsequent navigation,
     * the layout resolves its own target from the browser URL and selects it — the
     * {@code ?id=} item via {@link #restoreFromUrl(String)} when present, otherwise the
     * first row via {@link #selectFirst(ViewMode)}. Desktop only. Called by
     * {@code withAutoSelect()}.
     *
     * <p>This replaces the pattern of wiring the initial selection from the view with an
     * attach listener and/or a navigation callback such as {@code @OnShow}. Doing it there
     * is error-prone for two reasons this method avoids:</p>
     * <ul>
     *   <li><strong>Parameter timing.</strong> A field injected by the navigator (e.g.
     *       {@code @QueryParameter}) is <em>not</em> populated when the component attaches,
     *       so an attach-time read resolves a deep link as "no id" and wastes a
     *       {@code selectFirst} backend query before the real target is restored. Reading
     *       the {@link Location} directly is immune to injection order.</li>
     *   <li><strong>Double application.</strong> When both an attach listener and a
     *       navigation callback are wired, both fire on first load. Here a single
     *       {@code beforeClientResponse} callback coalesces the attach path to the end of
     *       the round trip, and the applied target is remembered, so the backend is queried
     *       exactly once per distinct target.</li>
     * </ul>
     *
     * <p>Re-navigation to a different {@code ?id=} still applies, because the UI-level
     * {@code AfterNavigationListener} re-resolves the target; the registration is removed
     * on detach along with the remembered target, so returning to the view re-selects.</p>
     */
    public void enableAutoSelect() {
        if (autoSelectEnabled) return;
        autoSelectEnabled = true;
        addAttachListener(event -> {
            UI ui = event.getUI();
            // Coalesce to the end of the round trip: by then the navigation is complete and
            // the active view location carries the ?id= of a deep link.
            ui.beforeClientResponse(this, ctx -> autoSelect(ui.getActiveViewLocation()));
            navigationRegistration =
                    ui.addAfterNavigationListener(e -> autoSelect(e.getLocation()));
        });
        addDetachListener(event -> {
            if (navigationRegistration != null) {
                navigationRegistration.remove();
                navigationRegistration = null;
            }
            autoSelectApplied = false;
            autoSelectTarget = null;
        });
    }

    /**
     * Resolves the selection target from {@code location} and applies it unless the same
     * target is already applied. No-op on a mobile viewport, where selection is driven by
     * the user tapping a row.
     *
     * <p>Normally driven automatically by {@link #enableAutoSelect()} — once from a
     * {@code beforeClientResponse} callback on attach and again from a UI-level
     * {@code AfterNavigationListener}. It is public so a view that manages its own
     * navigation lifecycle can trigger the same resolution explicitly; calling it
     * repeatedly with an equivalent location is cheap and does not re-query the
     * backend.</p>
     *
     * @param location the location to resolve the {@code ?id=} target from; {@code null}
     *                 is treated as "no id", i.e. select the first row
     */
    public void autoSelect(Location location) {
        if (viewMode != null && viewMode.isMobile()) return;
        String id = location == null ? null
                : location.getQueryParameters().getSingleParameter(urlParameterName()).orElse(null);
        if (id != null && id.isBlank()) id = null;
        if (autoSelectApplied && Objects.equals(autoSelectTarget, id)) return;
        autoSelectApplied = true;
        autoSelectTarget = id;
        if (id != null) {
            restoreFromUrl(id);
        } else {
            selectFirst(viewMode != null ? viewMode : ViewMode.DESKTOP);
        }
    }

    /** @return the query-parameter name used for deep links (defaults to {@code id}). */
    private String urlParameterName() {
        return urlSync != null ? urlSync.getParamName() : UrlSelectionSync.DEFAULT_PARAM_NAME;
    }

    /**
     * Wires a direct backend supplier used by {@link #selectFirst(ViewMode)} to resolve the
     * initial row instead of {@code ItemListing.getFirstItem()}. Called by
     * {@code withInitialItem(...)}.
     *
     * <p>{@code ItemListing.getFirstItem()} (via {@code GridLazyDataView.getItem(0)}) issues its
     * own backend fetch whenever index 0 is not yet in the Grid's active-item cache — which is
     * typically the case on initial attach, since {@link #selectFirst} runs before the master
     * grid's own rendering fetch completes. That means every initial page load pays for
     * <em>two</em> backend round trips for the same data set. Supplying a direct loader here
     * (e.g. {@code productService::findFirst}) avoids the redundant lazy-data-view fetch
     * entirely.</p>
     *
     * <p><strong>Contract:</strong> the supplier must return the row the master listing
     * renders <em>first</em>, i.e. it must apply the same default sort order as the listing's
     * fetch callback. A supplier that ignores the sort (a bare {@code SELECT … LIMIT 1})
     * returns an arbitrary row that may not even be inside the Grid's rendered window, so the
     * detail panel would open an item the user cannot see selected.</p>
     */
    public void setInitialItemSupplier(SerializableSupplier<Optional<T>> initialItemSupplier) {
        this.initialItemSupplier = initialItemSupplier;
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
     * Sets the desktop width of the master panel. The value is used only by the
     * desktop layout rule; mobile remains a single-panel layout.
     *
     * @param width a valid CSS length, such as {@code "340px"} or {@code "24rem"};
     *              {@code null} or blank restores the default width
     * @return this layout
     */
    public MasterDetailLayout<T> setDesktopMasterWidth(String width) {
        desktopMasterWidth = width == null || width.isBlank() ? null : width;
        if (desktopMasterWidth == null) {
            getStyle().remove("--mdl-master-width");
        } else {
            getStyle().set("--mdl-master-width", desktopMasterWidth);
        }
        return this;
    }

    /** @return the configured desktop master-panel width, if explicitly set. */
    public Optional<String> getDesktopMasterWidth() {
        return Optional.ofNullable(desktopMasterWidth);
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
     * <p>Prefers the direct {@link #setInitialItemSupplier(SerializableSupplier) initial-item supplier} when
     * configured (via {@code withInitialItem(...)}), since it avoids the extra backend fetch
     * that {@code ItemListing.getFirstItem()} triggers on a lazy data view (see
     * {@link #setInitialItemSupplier(SerializableSupplier)} javadoc). Falls back to
     * {@code masterBundle.listing().getFirstItem()} for backward compatibility when no supplier
     * is configured.</p>
     *
     * <p>When neither a supplier nor a master bundle is available there is nothing to
     * resolve, so the call is a no-op.</p>
     *
     * @param mode the current viewport mode (never auto-detected)
     */
    public void selectFirst(ViewMode mode) {
        if (mode == null || mode.isMobile()) return;
        Optional<T> first;
        if (initialItemSupplier != null) {
            first = initialItemSupplier.get();
        } else if (masterBundle != null) {
            first = masterBundle.listing().getFirstItem();
        } else {
            return;
        }
        first.ifPresent(item -> applySelection(item, true, Scroll.START));
    }

    /**
     * Writes {@code ?<param>=<id>} to the browser URL for {@code item}, using this layout's
     * own element and configured {@link #setViewMode(ViewMode) view mode}.
     * Desktop only — no-op on a mobile viewport or when URL sync was not configured.
     *
     * <p>Called automatically by {@link #selectItem(Object)}, so views no longer need to push
     * the URL from their detail-sync handler.</p>
     */
    public void pushUrlState(T item) {
        if (urlSync != null) urlSync.pushId(getElement(), item, viewMode);
    }

    /**
     * Writes {@code ?id=<id>} to the browser URL via {@code history.replaceState}.
     * Desktop only — no-op when {@code mode} is mobile.
     *
     * @deprecated selection now pushes the URL itself; use {@link #selectItem(Object)}, or
     *             {@link #pushUrlState(Object)} when pushing without selecting.
     */
    @Deprecated
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
     * and selects it — highlight, accent, detail sync and URL state.
     * No-op if URL sync was not configured or {@code id} is blank.
     *
     * <p>When the id cannot be resolved — a deep link to a deleted or invalid record — the
     * view is <em>not</em> left empty: the stale parameter is dropped from the URL and the
     * first row is selected instead.</p>
     */
    public void restoreFromUrl(String id) {
        if (urlSync == null) return;
        Optional<T> item = urlSync.load(id);
        if (item.isPresent()) {
            applySelection(item.get(), true, Scroll.ITEM);
            return;
        }
        if (id == null || id.isBlank()) return;
        // Stale or unknown id: drop it from the URL and fall back to the first row. The
        // recorded auto-select target is deliberately left as the requested id so the
        // fallback is not re-run within the same navigation; detach resets it.
        clearUrlState(getElement(), viewMode);
        selectFirst(viewMode != null ? viewMode : ViewMode.DESKTOP);
    }

    /**
     * Clears the current selection: un-highlights the master row, removes the accent class
     * and resets the reactive signal to {@code null}. Does not touch the URL.
     *
     * <p>The imperative detail-sync handlers registered with
     * {@link #addSyncDispatcher(SerializableConsumer)} are deliberately <em>not</em> invoked, since they
     * are contracted to receive a selected item and would have to null-check. React to
     * clearing through {@link #selectionSignal()}, which emits {@code null}.</p>
     */
    public void clearSelection() {
        currentItem = null;
        if (masterHighlighter != null) masterHighlighter.setHighlighted(null);
        if (accentColorProvider != null) setAccentClass(null);
        if (selectedSignal != null) selectedSignal.set(null);
        // Allow auto-select to re-apply the same target after an explicit clear.
        autoSelectApplied = false;
        autoSelectTarget = null;
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
     *
     * <p>Synchronized so that a background thread obtaining the signal through
     * {@code ui.access(...)} cannot race the UI thread into creating a second instance,
     * which would leave half the subscribers listening to a signal nothing ever writes.</p>
     *
     * <p>The signal is seeded with the current selection rather than {@code null}, so a
     * subscriber created after selection already happened — the common case with
     * {@code withAutoSelect()}, which selects during attach, or with detail content built
     * lazily on first display — immediately observes the selected item instead of a
     * spurious "nothing selected".</p>
     */
    public synchronized Signal<T> selectionSignal() {
        if (selectedSignal == null) selectedSignal = new ValueSignal<>(currentItem);
        return selectedSignal;
    }
}
