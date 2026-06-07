package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.ResponsiveDetailHost;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionController;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.UrlSelectionSync;
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

import java.io.Serial;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Reactive, signal-driven master-detail layout for any item type {@code T}.
 *
 * <h3>Architecture</h3>
 * <p>This class is a thin orchestrator. The real work is done by four collaborators:</p>
 * <ul>
 *   <li>{@link SelectionController}  — owns the selection signal and {@code dataVersion}
 *       counter, exposes {@code withDetailSync} for reactive form binding.</li>
 *   <li>{@link SelectionHighlighter} — manages the {@code mdl-selected} CSS part on
 *       the currently selected grid row.</li>
 *   <li>{@link ResponsiveDetailHost} — places detail components in the desktop slot
 *       or the mobile {@link Sheet} depending on viewport, with build-once caching.</li>
 *   <li>{@link UrlSelectionSync}     — pushes / clears / restores the selection via
 *       the {@code ?id=} URL query parameter using {@code history.replaceState}.</li>
 * </ul>
 *
 * <h3>Responsive behaviour</h3>
 * <ul>
 *   <li><b>MOBILE</b> — master fills the viewport. Selecting a row opens a {@link Sheet}
 *       that slides in from the right, fully covering the screen. The browser History API
 *       means the hardware back button (Android) / swipe-back (iOS) closes it naturally.</li>
 *   <li><b>TABLET / DESKTOP</b> — master and detail sit side by side via
 *       {@link IyenResponsiveLayout}. Detail content is built once on first selection
 *       and remains in the DOM; updates flow through {@link #withDetailSync} effects.</li>
 * </ul>
 *
 * <h3>URL synchronisation</h3>
 * <p>Add a {@code @QueryParameter}-annotated field to the host view and call
 * {@link #restoreSelection(String)} from {@code @OnShow}.</p>
 *
 * <h3>Auto-refresh</h3>
 * <p>Call {@link #notifyDataChanged()} from the detail view after a save; all registered
 * {@code onDataChanged} listeners are invoked synchronously on the Vaadin UI thread.</p>
 *
 * @param <T> the type of item displayed in the master grid
 * @see MasterDetailBuilder
 */
@StyleSheet("context://master-details.css")
@StyleSheet("context://master-detail-layout.css")
public class MasterDetailLayout<T> extends Layout {

    @Serial
    private static final long serialVersionUID = 1L;

    // -- Vaadin components ---------------------------------------------------
    private final Grid<T> grid;
    private final IyenResponsiveLayout responsiveLayout;

    // -- Collaborators -------------------------------------------------------
    private final SelectionController<T>  selectionController;
    private final SelectionHighlighter<T> highlighter;
    private final ResponsiveDetailHost    host;
    private final UrlSelectionSync<T>     urlSync;

    // -- Config (immutable) --------------------------------------------------
    private final Function<T, Component[]> detailContentProvider;
    private final boolean autoSelectFirst;

    // -- Lifecycle-scoped state ---------------------------------------------
    private Registration selectionEffectReg;
    private Registration modeChangeReg;

    /** Guards {@code applySelection} from clobbering URL {@code ?id=} on first run. */
    private boolean initialEffectRun = true;

    /** Ensures auto-select first runs at most once per attach cycle. */
    private boolean autoSelectFired = false;

    // -------------------------------------------------------------------------
    // Constructor
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

        this.grid                  = grid;
        this.detailContentProvider = detailContentProvider;
        this.autoSelectFirst       = autoSelectFirst;

        addClassName("mdl-root");

        this.responsiveLayout = new IyenResponsiveLayout(
                masterBuilder,
                IyenDetailBuilder.create(detailContainer));
        responsiveLayout.addClassName("mdl-responsive-host");
        add(responsiveLayout);

        Sheet mobileSheet = Sheet.builder(Sheet.Side.RIGHT)
                .title(mobileSheetTitle != null ? mobileSheetTitle : "Details")
                .fullscreenOnMobile(true)
                .backButton(true)
                .closeButton(true)
                .build();

        this.selectionController = new SelectionController<>(dataChangedListeners);
        this.highlighter         = new SelectionHighlighter<>(grid);
        this.host                = new ResponsiveDetailHost(dynamicContentSlot, mobileSheet);
        this.urlSync             = new UrlSelectionSync<>(idExtractor, itemLoader);

        mobileSheet.setOnClose(this::onMobileSheetClosed);

        // Bridge grid ITEM CLICK → highlighter + selection signal + detail update.
        // Detail view updates are driven exclusively by row clicks (real or
        // programmatic via {@link #clickItem(T)}); plain selection changes
        // (keyboard arrows, {@link #select(T)}, etc.) do NOT update the detail.
        grid.addItemClickListener(event -> handleClick(event.getItem()));
    }

    /**
     * Internal click-handler: accent highlight + detail update.
     * Invoked by the grid itemClickListener and by {@link #clickItem(T)}.
     *
     * <p>Does NOT call {@code grid.select(item)} — clicking a row to open
     * its detail is not the same as selecting it.  Selection (checkboxes in
     * multi-select, row highlight in single-select) is a separate concern
     * owned by the consumer via the grid's selection model.  The accent
     * highlight is provided by {@link SelectionHighlighter} via CSS parts.</p>
     */
    private void handleClick(T item) {
        if (item == null) return;
        highlighter.setHighlighted(item);
        selectionController.set(Optional.of(item));
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Registers a lifecycle-bound reactive sync handler invoked whenever the
     * selected item changes <em>or</em> {@link #notifyDataChanged()} is called.
     */
    public void withDetailSync(Component owner, Consumer<T> handler) {
        selectionController.withDetailSync(owner, handler);
    }

    /** Read-only view of the selection signal; use inside {@link Signal#effect}. */
    public Signal<Optional<T>> selectionSignal() {
        return selectionController.selectionSignal();
    }

    /**
     * Programmatically flips the grid's selection to {@code item}. <b>Does not</b>
     * update the detail view, the accent highlight, or the selection signal —
     * those are click-only side-effects (see {@link #clickItem(T)}).
     *
     * <p>Use this for purely visual / keyboard-equivalent selection changes
     * (e.g. wiring an external "select all" toolbar action). To programmatically
     * trigger a full row activation that also opens the detail panel, use
     * {@link #clickItem(T)}.</p>
     *
     * <p>No-op if {@code item} is {@code null}.</p>
     */
    public void select(T item) {
        if (item != null) grid.select(item);
    }

    /**
     * Simulates a row click programmatically: applies the accent highlight
     * and updates the detail panel via the selection signal.
     *
     * <p>This is the entry-point that {@link #selectFirst()},
     * {@link #restoreSelection(String)}, and any "open this item now" caller
     * (e.g. a freshly-created row in a CRUD view) should use. Plain
     * {@link #select(T)} does NOT open the detail.</p>
     *
     * <p>No-op if {@code item} is {@code null}.</p>
     */
    public void clickItem(T item) {
        handleClick(item);
    }

    /** Selects the first item from the grid's data provider AND opens its detail. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void selectFirst() {
        DataProvider raw = grid.getDataProvider();
        raw.fetch(new Query(0, 1, null, null, null))
                .findFirst()
                .ifPresent(item -> clickItem((T) item));
    }

    /** Clears the current selection, accent highlight, and detail panel. */
    public void clearSelection() {
        grid.deselectAll();
        highlighter.setHighlighted(null);
        selectionController.clear();
    }

    /**
     * Restores selection from a URL query parameter value, opening the detail
     * panel for the matched item. No-op if {@code idStr} is blank or no
     * {@code itemLoader} was configured.
     */
    public void restoreSelection(String idStr) {
        if (!urlSync.isEnabled()) return;
        autoSelectFired = true; // URL takes precedence over auto-select
        urlSync.restore(idStr, this::clickItem);
    }

    /**
     * Signals a data change from the detail view (e.g. after a save).
     * Bumps the data version (re-runs every {@link #withDetailSync} effect)
     * and invokes registered {@code onDataChanged} listeners.
     */
    public void notifyDataChanged() {
        selectionController.notifyDataChanged();
    }

    /** Registers a listener called when {@link #notifyDataChanged()} is invoked. */
    public Registration addDataChangedListener(Runnable listener) {
        Runnable removal = selectionController.addDataChangedListener(listener);
        return removal::run;
    }

    /** Exposes the underlying responsive layout for advanced customisation. */
    public IyenResponsiveLayout getResponsiveLayout() {
        return responsiveLayout;
    }

    // -------------------------------------------------------------------------
    // Static factories
    // -------------------------------------------------------------------------

    /** Creates a new {@link MasterDetailBuilder}. */
    public static <T> MasterDetailBuilder<T> builder() {
        return MasterDetailBuilder.create();
    }

    /** Internal factory — invoked exclusively by {@code DefaultMasterDetailBuilder}. */
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
        // onAttach can fire more than once (re-attach, test utilities).
        // Cancel prior subscriptions before registering new ones.
        disposeRegistrations();
        initialEffectRun = true;
        autoSelectFired  = false;
        selectionEffectReg = Signal.effect(this, this::applySelection);
        modeChangeReg      = responsiveLayout.addModeChangeListener(this::onModeChanged);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        disposeRegistrations();
        host.onOwnerDetach();
        super.onDetach(detachEvent);
    }

    private void disposeRegistrations() {
        if (selectionEffectReg != null) {
            selectionEffectReg.remove();
            selectionEffectReg = null;
        }
        if (modeChangeReg != null) {
            modeChangeReg.remove();
            modeChangeReg = null;
        }
    }

    // -------------------------------------------------------------------------
    // Reactive logic — the only state machine
    // -------------------------------------------------------------------------

    /**
     * Single source of truth for "given the current (selection × mode), where
     * should the detail go?"  Driven by {@link Signal#effect}.
     */
    private void applySelection() {
        Optional<T> selection = selectionController.read(); // reactive read
        ViewMode mode = responsiveLayout.getCurrentMode();

        // Mode is null until the first window-size event arrives.
        if (mode == null) {
            initialEffectRun = false;
            return;
        }

        if (selection.isEmpty()) {
            host.hide();
            // Don't clear ?id= on the very first run — restoreSelection() may yet fire.
            if (!initialEffectRun) urlSync.clearId(getElement());
            initialEffectRun = false;
            return;
        }

        initialEffectRun = false;
        T item = selection.get();
        host.place(item, mode, detailContentProvider);
        urlSync.pushId(getElement(), item);
    }

    /** Viewport transition handler — runs outside reactive context, uses peek(). */
    private void onModeChanged(ViewMode newMode) {
        T currentItem = selectionController.peek().orElse(null);
        host.onModeChanged(newMode, currentItem, detailContentProvider);

        // Auto-select once when entering desktop with no current selection.
        if (autoSelectFirst
                && !autoSelectFired
                && !isMobile(newMode)
                && currentItem == null) {
            autoSelectFired = true;
            selectFirst();
        }
    }

    /** Sheet onClose callback: deselect + clear URL. */
    private void onMobileSheetClosed() {
        grid.deselectAll();
        selectionController.clear();
        urlSync.clearId(getElement());
    }

    private static boolean isMobile(ViewMode mode) {
        return mode == ViewMode.MOBILE
                || mode == ViewMode.MOBILE_PORTRAIT
                || mode == ViewMode.MOBILE_LANDSCAPE;
    }
}

