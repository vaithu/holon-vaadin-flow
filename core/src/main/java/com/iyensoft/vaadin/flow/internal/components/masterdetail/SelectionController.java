package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Owns the reactive selection + data-version state for a master-detail screen.
 *
 * <p>Encapsulates two source signals — the currently selected item and a monotonic
 * "data version" counter — and a single {@link Signal#computed} {@code syncTrigger}
 * that fires whenever <em>either</em> changes. {@link #withDetailSync} subscribers
 * therefore subscribe to one signal, keeping the reactive graph minimal.</p>
 *
 * <p>UI-thread, single-session use only. {@link ValueSignal} is UI-scoped and all
 * {@link Signal#effect} subscriptions registered through {@link #withDetailSync}
 * are bound to the supplied owner's attach/detach lifecycle.</p>
 *
 * @param <T> the item type
 */
public final class SelectionController<T> {

    private static final Logger log = LoggerFactory.getLogger(SelectionController.class);

    private final ValueSignal<Optional<T>> selection = new ValueSignal<>(Optional.empty());
    private final ValueSignal<Integer>     version   = new ValueSignal<>(0);

    /** Combined dependency: re-evaluates on selection change OR data version bump. */
    private final Signal<Optional<T>> syncTrigger = Signal.computed(() -> {
        version.get();           // reactive dependency
        return selection.get();  // reactive dependency
    });

    private final List<Runnable> dataChangedListeners;

    public SelectionController(List<Runnable> dataChangedListeners) {
        this.dataChangedListeners = new ArrayList<>(dataChangedListeners);
    }

    // -- selection -----------------------------------------------------------

    /** Sets the current selection (use within signal-aware code). */
    public void set(Optional<T> item) {
        selection.set(item);
    }

    /** Reads selection inside a {@link Signal#effect} or {@link Signal#computed}. */
    public Optional<T> read() {
        return selection.get();
    }

    /** Reads selection without subscribing to it (use outside reactive contexts). */
    public Optional<T> peek() {
        return selection.peek();
    }

    /** Clears the current selection. */
    public void clear() {
        selection.set(Optional.empty());
    }

    /** Read-only view of the selection signal for external observers. */
    public Signal<Optional<T>> selectionSignal() {
        return selection.asReadonly();
    }

    // -- data version --------------------------------------------------------

    /**
     * Bumps the data-version counter, causing every {@link #withDetailSync} effect
     * to re-run with the current item, and invokes all {@link #addDataChangedListener}
     * listeners (typically a grid refresh).
     */
    public void notifyDataChanged() {
        version.set(version.peek() + 1);
        // forEach: ArrayList uses direct array access, no iterator allocation.
        // Vaadin UI thread is single-threaded; no ConcurrentModification risk
        // unless a listener removes itself, which we guard with try/catch.
        dataChangedListeners.forEach(listener -> {
            try {
                listener.run();
            } catch (Exception ex) {
                log.warn("DataChanged listener threw an exception", ex);
            }
        });
    }

    public Runnable addDataChangedListener(Runnable listener) {
        dataChangedListeners.add(listener);
        return () -> dataChangedListeners.remove(listener);
    }

    // -- reactive sync hook --------------------------------------------------

    /**
     * Registers a lifecycle-bound effect that re-runs whenever the selection
     * changes or {@link #notifyDataChanged()} is called.
     *
     * @param owner   the component whose attach/detach lifecycle bounds the effect
     * @param handler invoked with the currently selected item on every reactive update
     */
    public void withDetailSync(Component owner, Consumer<T> handler) {
        Signal.effect(owner, () -> syncTrigger.get().ifPresent(handler));
    }
}

