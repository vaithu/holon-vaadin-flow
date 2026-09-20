package com.iyensoft.vaadin.flow.utils.responsive;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.component.screenorientation.ScreenOrientation;
import com.vaadin.flow.component.screenorientation.ScreenOrientationData;
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
 * // Simplest: wire everything to the owner's attach lifecycle automatically.
 * public MyLayout() {
 *     WindowSizeTracker.enable(this);
 * }
 *
 * // With a callback on every mode change:
 * public MyLayout() {
 *     WindowSizeTracker.enable(this, mode -> updateLayout(mode));
 * }
 *
 * // Manual control (e.g. custom onAttach/onDetach lifecycle):
 * resizeRegistration = WindowSizeTracker.track(attachEvent.getUI(), this, this::onModeChanged);
 * // ... and in onDetach:
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
        Signal<ScreenOrientationData> orientation = ScreenOrientation.orientationSignal(ui);
        return Signal.effect(owner, () -> {
            WindowSize size = windowSize.get();
            ViewMode mode = UIUtils.getViewMode(size.width(), size.height(), orientation.get().type());
            ViewModeContext.setCurrent(mode);
            onModeChange.accept(mode);
        });
    }

    /**
     * Wires {@link #track(UI, Component, Consumer)} to {@code owner}'s attach lifecycle, so callers
     * do not need to override {@code onAttach}/{@code onDetach} themselves. Safe to call once from a
     * constructor: tracking (re)starts on every attach and is disposed on detach or re-attach.
     *
     * <p>Only keeps {@link ViewModeContext} in sync; use {@link #enable(Component, Consumer)} to also
     * react to mode changes.
     *
     * <pre>{@code
     * public MyLayout() {
     *     WindowSizeTracker.enable(this);
     * }
     * }</pre>
     *
     * @param owner the component whose attach lifecycle drives the tracking (not null)
     */
    public static void enable(Component owner) {
        enable(owner, mode -> { });
    }

    /**
     * Same as {@link #enable(Component)}, additionally invoking {@code onModeChange} immediately and
     * on every subsequent viewport resize.
     *
     * @param owner        the component whose attach lifecycle drives the tracking (not null)
     * @param onModeChange consumer called with the current {@link ViewMode} on each resize
     */
    public static void enable(Component owner, Consumer<ViewMode> onModeChange) {
        final Registration[] tracking = new Registration[1];
        owner.addAttachListener(event -> {
            if (tracking[0] != null) {
                tracking[0].remove();
            }
            event.getUI().beforeClientResponse(owner, context -> {
                if (owner.getUI().isPresent()) {
                    tracking[0] = track(event.getUI(), owner, onModeChange);
                }
            });
        });
        owner.addDetachListener(event -> {
            if (tracking[0] != null) {
                tracking[0].remove();
                tracking[0] = null;
            }
        });
    }
}

