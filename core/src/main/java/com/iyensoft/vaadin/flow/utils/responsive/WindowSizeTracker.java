package com.iyensoft.vaadin.flow.utils.responsive;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;

import java.util.function.Consumer;

/**
 * Utility for subscribing to the browser's window-size signal and translating
 * each {@link WindowSize} change into a {@link ViewMode}.
 *
 * <p>The underlying Vaadin {@code Signal.effect} is scoped to the supplied
 * {@code owner} component, so the subscription is automatically garbage-collected
 * when the owner is detached. The returned {@link Registration} allows explicit
 * early cancellation (e.g. in {@code onDetach}).
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // In onAttach / registerResizeLifecycle:
 * resizeRegistration = WindowSizeTracker.track(attachEvent.getUI(), this, this::onModeChanged);
 *
 * // In onDetach:
 * if (resizeRegistration != null) {
 *     resizeRegistration.remove();
 *     resizeRegistration = null;
 * }
 * }</pre>
 *
 * @see UIUtils#getViewMode(int, int)
 * @since 10.0.0
 */
public final class WindowSizeTracker {

    private WindowSizeTracker() {}

    /**
     * Subscribes to the window-size signal of the given {@link UI} and invokes
     * {@code onModeChange} immediately and on every subsequent viewport resize.
     *
     * <p>The {@code Signal.effect} is scoped to {@code owner}, meaning it is
     * automatically disposed when {@code owner} is removed from the component tree.
     * The returned {@link Registration} allows explicit early cancellation.
     *
     * @param ui           the current {@link UI} (from {@code AttachEvent.getUI()})
     * @param owner        the component that owns this subscription (used for effect scoping)
     * @param onModeChange consumer called with the current {@link ViewMode} on each resize
     * @return a {@link Registration} that cancels the subscription when removed
     */
    public static Registration track(UI ui, Component owner, Consumer<ViewMode> onModeChange) {
        Signal<WindowSize> windowSize = ui.getPage().windowSizeSignal();
        return Signal.effect(owner, () -> {
            WindowSize size = windowSize.get();
            onModeChange.accept(UIUtils.getViewMode(size.width(), size.height()));
        });
    }
}

