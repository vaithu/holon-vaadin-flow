package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Reactive, signal-driven master-detail layout for any item type {@code T}.
 *
 * <h3>Responsive behaviour</h3>
 * <ul>
 *   <li><b>MOBILE</b> — master fills the viewport. Selecting a row opens a {@link Sheet} that
 *       slides in from the right, fully covering the screen. The browser History API means the
 *       hardware back button (Android) / swipe-back (iOS) closes it naturally. Detail components
 *       are built on demand and removed from the DOM when the sheet closes.</li>
 *   <li><b>TABLET / DESKTOP</b> — master and detail sit side by side via
 *       {@link IyenResponsiveLayout}. Detail content is built on demand when a row is selected
 *       and physically removed when deselected (no {@code setVisible(false)}).</li>
 * </ul>
 *
 * <h3>Selected row highlight</h3>
 * <p>The currently selected row is marked with the CSS part {@code mdl-selected}, allowing
 * external stylesheets to apply a visible accent. Styled via
 * {@code .mdl-master-grid::part(mdl-selected)} in {@code master-detail-layout.css}.</p>
 *
 * <h3>Auto-select first row</h3>
 * <p>Use {@link MasterDetailBuilder#autoSelectFirst(boolean)} to have the first row selected
 * automatically when the layout is first shown (if no URL {@code ?id=} is present).</p>
 *
 * <h3>URL synchronisation</h3>
 * <p>Selecting an item writes {@code ?id=<encoded-id>} to the browser URL via
 * {@code history.replaceState} without triggering a Vaadin navigation round-trip.
 * To restore the selection on a hard refresh, add a {@code @QueryParameter}-annotated field
 * to the host view and call {@link #restoreSelection(String)} from {@code @OnShow}:</p>
 * <pre>{@code
 * @QueryParameter
 * private String id;
 *
 * @OnShow
 * void onShow() {
 *     if (id != null) masterDetailLayout.restoreSelection(id);
 * }
 * }</pre>
 *
 * <h3>Auto-refresh</h3>
 * <p>Call {@link #notifyDataChanged()} from the detail view after a save; all registered
 * {@code onDataChanged} listeners (typically {@code grid.getDataProvider().refreshAll()})
 * are invoked synchronously on the Vaadin UI thread.</p>
 *
 * <h3>Concurrency</h3>
 * <p>Fully per-UI-session. No static shared state. {@link ValueSignal} is UI-scoped and all
 * {@link Signal#effect} registrations are auto-cleaned on {@code onDetach}.</p>
 *
 * @param <T> the type of item displayed in the master grid
 * @see MasterDetailBuilder
 */
@StyleSheet("context://master-details.css")
@StyleSheet("context://master-detail-layout.css")
public class MasterDetailLayout<T> extends Layout {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MasterDetailLayout.class);

    // -------------------------------------------------------------------------
    // Internal structure
    // -------------------------------------------------------------------------

    /** Grid driving master row selection. */
    private final Grid<T> grid;

    /**
     * Slot inside the detail panel that holds per-item components.
     * Cleared and repopulated on each selection change.
     */
    private final Layout dynamicContentSlot;

    /** Handles responsive layout switching (master-only on mobile; side-by-side on tablet+). */
    private final IyenResponsiveLayout responsiveLayout;

    /**
     * Slide-in sheet used as the mobile detail overlay.
     * Attached to the current UI on first {@link Sheet#open()} and kept attached
     * until this layout is detached.
     */
    private final Sheet mobileSheet;

    // -------------------------------------------------------------------------
    // Signals
    // -------------------------------------------------------------------------

    /**
     * The currently selected item. {@code Optional.empty()} means no selection.
     * Drives detail content rendering and URL param update.
     * Read with {@code .get()} only inside a {@code Signal.effect} or {@code Signal.computed}.
     */
    private final ValueSignal<Optional<T>> selectionSignal = new ValueSignal<>(Optional.empty());

    /**
     * Monotonically-increasing version counter bumped by {@link #notifyDataChanged()}.
     * Does NOT change the DOM — only causes {@link #syncTrigger} to re-compute, which
     * in turn re-runs {@link #withDetailSync} handlers with the current item.
     * Read with {@code .get()} only inside a {@code Signal.effect} or {@code Signal.computed}.
     */
    private final ValueSignal<Integer> dataVersion = new ValueSignal<>(0);

    /**
     * Combined reactive trigger for {@link #withDetailSync} handlers.
     *
     * <p>This computed signal re-evaluates — and notifies all subscribers — whenever:
     * <ul>
     *   <li>the grid selection changes ({@code selectionSignal} is set by the selection listener),
     *       or</li>
     *   <li>{@link #notifyDataChanged()} is called ({@code dataVersion} is incremented).</li>
     * </ul>
     *
     * <p>Encapsulating the dual-signal dependency here means every {@code withDetailSync}
     * effect subscribes to <em>one</em> signal rather than independently reading two,
     * keeping the reactive graph minimal and consistent.</p>
     */
    private final Signal<Optional<T>> syncTrigger = Signal.computed(() -> {
        dataVersion.get();            // reactive dependency: re-compute on every notifyDataChanged()
        return selectionSignal.get(); // reactive dependency: re-compute on every selection change
    });

    // -------------------------------------------------------------------------
    // Config (immutable after construction)
    // -------------------------------------------------------------------------

    /** Builds the detail component(s) for a given item. Never null. */
    private final Function<T, Component[]> detailContentProvider;

    /** Converts an item to its URL-safe string ID. {@code null} means URL sync is disabled. */
    private final Function<T, String> idExtractor;

    /**
     * Loads an item by its string ID for URL-based selection restore.
     * {@code null} means URL sync is disabled.
     */
    private final Function<String, Optional<T>> itemLoader;

    /** Called when {@link #notifyDataChanged()} is invoked (e.g. after a save). */
    private final List<Runnable> dataChangedListeners;

    /**
     * When {@code true}, the first row is selected automatically the first time
     * the layout is shown in a desktop/tablet viewport and no selection exists.
     */
    private final boolean autoSelectFirst;

    // -------------------------------------------------------------------------
    // Mutable state
    // -------------------------------------------------------------------------

    /**
     * Tracks the item currently carrying the {@code mdl-selected} CSS part.
     * Updated on every selection change; used to call {@code refreshItem()} on the
     * old row so its part name is cleared before the new row receives it.
     */
    private T currentHighlightedItem = null;

    private Registration selectionEffectReg;
    private Registration modeChangeReg;

    /**
     * Guards against {@link #applySelection()} clearing the URL {@code ?id=} param on the
     * very first reactive-effect run (before {@link #restoreSelection(String)} has a chance
     * to re-apply it from a query parameter).  Set to {@code false} after the first run.
     */
    private boolean initialEffectRun = true;

    /**
     * Ensures {@link #autoSelectFirst} fires only once per attach cycle, not on every
     * mode transition. Reset in {@link #onAttach(AttachEvent)}.
     */
    private boolean autoSelectFired = false;

    // -------------------------------------------------------------------------
    // Build-once component cache (Option A/B)
    // -------------------------------------------------------------------------

    /**
     * Detail components built by {@code detailContentProvider} on the very first selection.
     * Never rebuilt — all subsequent data updates flow through {@link #withDetailSync} effects.
     * {@code null} until the first selection occurs.
     */
    private Component[] cachedComponents = null;

    /**
     * Tracks which container currently owns {@link #cachedComponents} so that
     * {@link #placeInDesktop()} and {@link #placeInMobile()} can avoid moving a component
     * to the parent it already lives in (which would cause an unnecessary detach/attach cycle
     * and briefly suspend any {@code Signal.effect} bound to those components).
     */
    private enum ComponentLocation { NONE, DESKTOP, MOBILE }
    private ComponentLocation componentLocation = ComponentLocation.NONE;

    // -------------------------------------------------------------------------
    // Constructor — called only from DefaultMasterDetailBuilder / create()
    // -------------------------------------------------------------------------

    public MasterDetailLayout(
            IyenMasterBuilder masterBuilder,
            Layout detailContainer,
            Layout dynamicContentSlot,
            Grid<T> grid,
            Function<T, Component[]> detailContentProvider,
            Function<T, String> idExtractor,
            Function<String, Optional<T>> itemLoader,
            String mobileSheetTitle,
            List<Runnable> dataChangedListeners,
            boolean autoSelectFirst) {

        this.grid = grid;
        this.dynamicContentSlot = dynamicContentSlot;
        this.detailContentProvider = detailContentProvider;
        this.idExtractor = idExtractor;
        this.itemLoader = itemLoader;
        this.dataChangedListeners = new ArrayList<>(dataChangedListeners);
        this.autoSelectFirst = autoSelectFirst;

        // Root CSS class — ensures flex layout fills available height correctly
        addClassName("mdl-root");

        // Compose the responsive layout using pre-built panels.
        this.responsiveLayout = new IyenResponsiveLayout(
                masterBuilder,
                IyenDetailBuilder.create(detailContainer));
        responsiveLayout.addClassName("mdl-responsive-host");
        add(responsiveLayout);

        // Mobile detail overlay — fullscreen on narrow viewports, History API enabled
        this.mobileSheet = Sheet.builder(Sheet.Side.RIGHT)
                .title(mobileSheetTitle != null ? mobileSheetTitle : "Details")
                .fullscreenOnMobile(true)
                .backButton(true)
                .closeButton(true)
                .build();
        mobileSheet.setOnClose(this::onMobileSheetClosed);

        // Mark the detail slot as "no selection" initially so the CSS placeholder
        // is shown even after the first component build (when the slot is no longer :empty).
        dynamicContentSlot.addClassName("mdl-detail--no-selection");

        // Part-name generator for selected-row CSS highlight.
        // Returns "mdl-selected" for the currently highlighted item so that
        // .mdl-master-grid::part(mdl-selected) can style it from master-detail-layout.css.
        // Uses Objects.equals so JPA entities with proper equals/hashCode work correctly.
        grid.setPartNameGenerator(item ->
                Objects.equals(currentHighlightedItem, item) ? "mdl-selected" : null);

        // Wire grid row selection → selection signal + refresh part names.
        grid.addSelectionListener(event -> {
            T prev = currentHighlightedItem;
            currentHighlightedItem = event.getFirstSelectedItem().orElse(null);
            selectionSignal.set(event.getFirstSelectedItem());
            // Refresh old and new items so the part-name generator re-evaluates for each row.
            safeRefreshItem(prev);
            safeRefreshItem(currentHighlightedItem);
        });
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Registers a lifecycle-bound reactive sync handler that is called automatically
     * whenever the selected item changes <em>or</em> {@link #notifyDataChanged()} is invoked
     * (e.g. after a save that mutates the item in-place).
     *
     * <p>The handler is bound to {@code owner}'s attach/detach lifecycle: it activates
     * when {@code owner} is attached to the UI and is automatically removed when it detaches.
     * No manual cleanup is required.</p>
     *
     * <p>Typical usage inside the detail form:</p>
     * <pre>{@code
     * masterDetail.withDetailSync(formBody, contact -> {
     *     nameField.setValue(contact.getName());
     *     emailField.setValue(contact.getEmail());
     * });
     * masterDetail.withDetailSync(headerComponent, contact ->
     *     headerComponent.setHeading(contact.getName()));
     * }</pre>
     *
     * @param owner   the component whose lifecycle bounds this effect (not null)
     * @param handler called with the currently selected item on every reactive update (not null)
     */
    public void withDetailSync(Component owner, Consumer<T> handler) {
        // Subscribe to the pre-composed syncTrigger — a single dependency that already
        // combines selectionSignal + dataVersion.  Reading two signals here directly would
        // duplicate the reactive graph that syncTrigger was created to encapsulate.
        Signal.effect(owner, () -> syncTrigger.get().ifPresent(handler));
    }

    /**
     * Returns a read-only view of the selection signal.
     * Use inside {@link Signal#effect} only.
     *
     * @return readonly {@link Signal} containing the currently selected item
     */
    public Signal<Optional<T>> selectionSignal() {
        return selectionSignal.asReadonly();
    }

    /**
     * Programmatically selects an item, triggering detail population (and the mobile sheet
     * overlay when on a narrow viewport). No-op if {@code item} is {@code null}.
     *
     * @param item the item to select
     */
    public void select(T item) {
        if (item != null) {
            grid.select(item);
        }
    }

    /**
     * Selects the first item returned by the grid's data provider.
     * No-op if the data provider is empty.
     *
     * <p>Suitable for auto-populating the detail panel on initial load.
     * The builder option {@link MasterDetailBuilder#autoSelectFirst(boolean)} calls this
     * automatically when the viewport first becomes tablet/desktop size.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void selectFirst() {
        DataProvider raw = grid.getDataProvider();
        raw.fetch(new Query(0, 1, null, null, null))
                .findFirst()
                .ifPresent(item -> select((T) item));
    }

    /**
     * Clears the current selection, removes all detail content from the DOM,
     * closes the mobile sheet (if open), and clears the URL {@code ?id=} parameter.
     */
    public void clearSelection() {
        grid.deselectAll();
        selectionSignal.set(Optional.empty());
    }

    /**
     * Restores selection from a URL query parameter value.
     *
     * <p>No-op if {@code idStr} is blank or if no {@code itemLoader} was configured.</p>
     *
     * @param idStr the serialised item ID from the URL query parameter
     */
    public void restoreSelection(String idStr) {
        if (itemLoader == null || idStr == null || idStr.isBlank()) return;
        // Mark auto-select as fired so it does not override the URL-restored selection
        autoSelectFired = true;
        itemLoader.apply(idStr).ifPresent(this::select);
    }

    /**
     * Signals that data was changed from the detail view (e.g. after a save).
     * <ol>
     *   <li>Bumps the internal {@code dataVersion} signal, causing all
     *       {@link #withDetailSync} effects to re-run with the current item —
     *       updating form fields, headers, etc. without any DOM replacement.</li>
     *   <li>Invokes all registered {@code onDataChanged} listeners, typically
     *       refreshing the master grid.</li>
     * </ol>
     */
    public void notifyDataChanged() {
        // Bump version first so sync effects see refreshed item data when the
        // grid listener (e.g. refreshAll) runs on the same UI thread tick.
        dataVersion.set(dataVersion.peek() + 1);
        // ArrayList.forEach() uses direct array access internally — no iterator or copy allocation.
        // Size is snapshotted by forEach before the loop; Vaadin UI is single-threaded.
        dataChangedListeners.forEach(listener -> {
            try {
                listener.run();
            } catch (Exception ex) {
                log.warn("DataChanged listener threw an exception", ex);
            }
        });
    }

    /**
     * Registers a listener called when {@link #notifyDataChanged()} is invoked.
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addDataChangedListener(Runnable listener) {
        dataChangedListeners.add(listener);
        return () -> dataChangedListeners.remove(listener);
    }

    /**
     * Exposes the underlying {@link IyenResponsiveLayout} for advanced customisation
     * (e.g. adding a separator or subscribing to view-mode changes).
     *
     * @return the responsive layout instance
     */
    @SuppressWarnings("java:S1845") // Lombok @Getter not used per project convention
    public IyenResponsiveLayout getResponsiveLayout() {
        return responsiveLayout;
    }

    // -------------------------------------------------------------------------
    // Static factories
    // -------------------------------------------------------------------------

    /**
     * Creates a new {@link MasterDetailBuilder} for the given item type.
     *
     * @param <T> the item type
     * @return a new builder
     */
    public static <T> MasterDetailBuilder<T> builder() {
        return MasterDetailBuilder.create();
    }

    /**
     * Internal factory — called exclusively by {@code DefaultMasterDetailBuilder}.
     */
    public static <T> MasterDetailLayout<T> create(
            IyenMasterBuilder masterBuilder,
            Layout detailContainer,
            Layout dynamicContentSlot,
            Grid<T> grid,
            Function<T, Component[]> detailContentProvider,
            Function<T, String> idExtractor,
            Function<String, Optional<T>> itemLoader,
            String mobileSheetTitle,
            List<Runnable> dataChangedListeners,
            boolean autoSelectFirst) {
        return new MasterDetailLayout<>(masterBuilder, detailContainer, dynamicContentSlot,
                grid, detailContentProvider, idExtractor, itemLoader,
                mobileSheetTitle, dataChangedListeners, autoSelectFirst);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Cancel any existing registrations before re-registering — onAttach may be called
        // more than once (e.g. by test utilities or re-attach after detach), and we must
        // never have more than one active selectionEffect or modeChange listener at a time.
        if (selectionEffectReg != null) {
            selectionEffectReg.remove();
            selectionEffectReg = null;
        }
        if (modeChangeReg != null) {
            modeChangeReg.remove();
            modeChangeReg = null;
        }
        initialEffectRun = true;   // reset guard on every re-attach
        autoSelectFired = false;   // reset auto-select on every re-attach
        selectionEffectReg = Signal.effect(this, this::applySelection);
        modeChangeReg = responsiveLayout.addModeChangeListener(this::onModeChanged);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (selectionEffectReg != null) {
            selectionEffectReg.remove();
            selectionEffectReg = null;
        }
        if (modeChangeReg != null) {
            modeChangeReg.remove();
            modeChangeReg = null;
        }
        if (mobileSheet.isAttached()) {
            mobileSheet.detach();
        }
        super.onDetach(detachEvent);
    }

    // -------------------------------------------------------------------------
    // Reactive logic
    // -------------------------------------------------------------------------

    /**
     * Called by {@link Signal#effect} whenever {@link #selectionSignal} changes.
     * Dispatches to the mobile (sheet) or desktop (inline) rendering path.
     *
     * <p><b>Option A — Desktop:</b> detail components are built once on the first selection
     * and remain in {@link #dynamicContentSlot} permanently.  The slot is shown/hidden via
     * the CSS class {@code mdl-detail--no-selection}; no DOM remove/add happens on
     * subsequent row clicks — data updates propagate entirely through
     * {@link #withDetailSync} effects.</p>
     *
     * <p><b>Option B — Mobile:</b> components are built once on the first selection and
     * assigned to the {@link Sheet} once.  The sheet is simply opened/closed on each
     * row click; the component tree is never rebuilt.</p>
     */
    private void applySelection() {
        Optional<T> selection = selectionSignal.get(); // inside Signal.effect — safe
        ViewMode mode = responsiveLayout.getCurrentMode();

        // Mode may be null before the first window-size event arrives.
        if (mode == null) {
            initialEffectRun = false;
            return;
        }

        if (selection.isEmpty()) {
            hideDesktopSlot();
            if (mobileSheet.isOpen()) mobileSheet.close();
            // Do NOT clear the URL on the very first run — restoreSelection() may be
            // called shortly after onAttach and needs the ?id= param to still be present.
            if (!initialEffectRun) clearUrlId();
            initialEffectRun = false;
            return;
        }

        initialEffectRun = false;
        T item = selection.get();
        ensureComponents(item);  // build once; no-op on subsequent calls

        if (isMobile(mode)) {
            placeInMobile();     // moves components to sheet only if not already there
            mobileSheet.open();
        } else {
            placeInDesktop();    // moves components to slot only if not already there
            showDesktopSlot();
        }

        pushUrlId(item);
    }

    /**
     * Called when the viewport transitions between MOBILE and TABLET/DESKTOP.
     * Uses {@code peek()} — this runs outside a {@code Signal.effect} reactive context.
     */
    private void onModeChanged(ViewMode newMode) {
        if (isMobile(newMode)) {
            // Entering mobile: hide the desktop slot; move components to sheet if selected.
            hideDesktopSlot();
            selectionSignal.peek().ifPresent(item -> {
                ensureComponents(item);
                placeInMobile();
                mobileSheet.open();
            });
        } else {
            // Entering desktop: close sheet; move components to slot if selected.
            if (mobileSheet.isOpen()) mobileSheet.close();
            selectionSignal.peek().ifPresent(item -> {
                ensureComponents(item);
                placeInDesktop();
                showDesktopSlot();
            });
            // Auto-select the first row once, when the layout first enters desktop mode
            // and no selection exists (or has been restored from URL).
            if (autoSelectFirst && !autoSelectFired && selectionSignal.peek().isEmpty()) {
                autoSelectFired = true;
                selectFirst();
            }
        }
    }

    /**
     * Invoked by {@link Sheet}'s onClose callback. Deselects the grid row,
     * clears the selection signal, and removes the {@code ?id=} URL parameter.
     */
    private void onMobileSheetClosed() {
        grid.deselectAll();
        selectionSignal.set(Optional.empty());
        clearUrlId();
    }

    // -------------------------------------------------------------------------
    // Component-cache helpers
    // -------------------------------------------------------------------------

    /**
     * Builds {@link #cachedComponents} on the very first call; no-op thereafter.
     * The {@code item} argument is forwarded to {@code detailContentProvider} so that
     * lambdas that capture the item for initial setup still work correctly.
     */
    private void ensureComponents(T item) {
        if (cachedComponents == null) {
            cachedComponents = detailContentProvider.apply(item);
            componentLocation = ComponentLocation.NONE;
        }
    }

    /**
     * Moves {@link #cachedComponents} into {@link #dynamicContentSlot} (Option A).
     * No-op if they are already there.  Vaadin automatically detaches them from the
     * mobile sheet first if {@link #componentLocation} is {@code MOBILE}.
     */
    private void placeInDesktop() {
        if (componentLocation != ComponentLocation.DESKTOP && cachedComponents != null) {
            dynamicContentSlot.add(cachedComponents); // auto-detaches from sheet if needed
            componentLocation = ComponentLocation.DESKTOP;
        }
    }

    /**
     * Moves {@link #cachedComponents} into the mobile {@link Sheet} (Option B).
     * No-op if they are already there.  Vaadin automatically detaches them from the
     * desktop slot first if {@link #componentLocation} is {@code DESKTOP}.
     */
    private void placeInMobile() {
        if (componentLocation != ComponentLocation.MOBILE && cachedComponents != null) {
            mobileSheet.setContent(cachedComponents); // auto-detaches from slot if needed
            componentLocation = ComponentLocation.MOBILE;
        }
    }

    /**
     * Hides the desktop detail slot by adding the {@code mdl-detail--no-selection} CSS class.
     * Cached components remain in the DOM; the CSS class makes them invisible and
     * renders the "Select an item" placeholder via a {@code ::before} pseudo-element.
     */
    private void hideDesktopSlot() {
        dynamicContentSlot.addClassName("mdl-detail--no-selection");
    }

    /**
     * Shows the desktop detail slot by removing the {@code mdl-detail--no-selection} CSS class.
     */
    private void showDesktopSlot() {
        dynamicContentSlot.removeClassName("mdl-detail--no-selection");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean isMobile(ViewMode mode) {
        return mode == ViewMode.MOBILE
                || mode == ViewMode.MOBILE_PORTRAIT
                || mode == ViewMode.MOBILE_LANDSCAPE;
    }


    private void pushUrlId(T item) {
        if (idExtractor == null) return;
        String idStr = idExtractor.apply(item);
        if (idStr == null || idStr.isBlank()) return;
        getElement().executeJs(
                "history.replaceState(null, '', location.pathname + '?id=' + $0)", idStr);
    }

    private void clearUrlId() {
        getElement().executeJs("history.replaceState(null, '', location.pathname)");
    }

    /**
     * Calls {@code DataProvider.refreshItem(item)} on the grid's data provider so that
     * {@link Grid#setPartNameGenerator} re-evaluates the part name for the given item.
     * Silently ignored if the data provider does not support item-level refresh.
     */
    private void safeRefreshItem(T item) {
        if (item == null) return;
        try {
            grid.getDataProvider().refreshItem(item);
        } catch (Exception ex) {
            log.debug("refreshItem not supported by data provider for {}: {}", item, ex.getMessage());
        }
    }
}

