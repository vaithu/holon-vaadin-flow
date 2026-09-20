package com.iyensoft.vaadin.flow.utils.responsive;

import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.ContextScope;
import com.holonplatform.vaadin.flow.VaadinSessionScope;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

/**
 * Holds the current {@link ViewMode}, derived from the browser window size, as a
 * {@link Signal} bound to the current Vaadin session through the Holon Platform
 * {@link Context} API, so it can be retrieved — and reactively observed — from
 * anywhere in the application, not only from the component that tracks the resize event.
 *
 * <p>The value is kept up to date automatically by {@link WindowSizeTracker#track}.
 * {@link #getCurrent()} returns a plain snapshot of the value, while {@link #getSignal()}
 * exposes the underlying {@link Signal}, which can be used with {@code ComponentEffect}
 * or {@code Signal.effect} to react automatically whenever the view mode changes.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // one-off read
 * Optional<ViewMode> mode = ViewModeContext.getCurrent();
 *
 * // reactive read, e.g. inside a component
 * ViewModeContext.getSignal().ifPresent(signal ->
 *     ComponentEffect.effect(this, () -> updateLayout(signal.get())));
 * }</pre>
 *
 * @since 10.0.0
 */
public final class ViewModeContext {

    /** Context resource key under which the current {@link ViewMode} signal is stored. */
    public static final String CONTEXT_KEY = ViewMode.class.getName();

    private ViewModeContext() {}

    /**
     * Gets the current {@link ViewMode}, as a snapshot of the underlying {@link Signal} value.
     *
     * @return the current {@link ViewMode}, or an empty Optional if not (yet) available
     */
    public static Optional<ViewMode> getCurrent() {
        // peek(), not get(): a one-time read must work outside a reactive context (e.g. a constructor).
        return getSignal().map(Signal::peek);
    }

    /**
     * Gets the {@link Signal} holding the current {@link ViewMode}, bound to the current Vaadin
     * session scope. Reading it (e.g. from a {@code Signal.effect}/{@code ComponentEffect}) makes
     * the dependent code automatically re-run whenever the view mode changes.
     *
     * @return the {@link ViewMode} {@link Signal}, or an empty Optional if no Vaadin session is
     *         available (e.g. outside a UI request)
     */
    public static Optional<Signal<ViewMode>> getSignal() {
        return VaadinSessionScope.get().map(ViewModeContext::resolveSignal).map(ValueSignal::asReadonly);
    }

    /**
     * Updates the {@link Signal} holding the current {@link ViewMode}, bound to the current
     * Vaadin session scope. No-op if no Vaadin session is available (e.g. outside a UI request).
     *
     * @param mode the {@link ViewMode} to store (not null)
     */
    public static void setCurrent(ViewMode mode) {
        if (mode == null) {
            return;
        }
        VaadinSessionScope.get().ifPresent(scope -> resolveSignal(scope).set(mode));
    }

    /** Gets (creating on first access) the session-scoped signal backing this context. */
    private static ValueSignal<ViewMode> resolveSignal(ContextScope scope) {
        ValueSignal<ViewMode> created = new ValueSignal<>(null);
        return scope.putIfAbsent(CONTEXT_KEY, created).orElse(created);
    }
}
