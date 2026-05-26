package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.components.HasIyenView;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import lombok.Getter;

/**
 * Base view that:
 *  - provides <main class="iyen-responsive-layout"> (CSS relies on this)
 *  - hosts a shared overlay container inside <main>
 *  - mirrors current ViewMode and exposes it to subclasses
 *  - wires a single IyenResponsiveLayout instance
 */
@StyleSheet(value = "context://master-details.css")
public abstract class AbstractIyenView extends Main implements HasIyenView {

    private boolean initialized = false;

    /** Latest known responsive mode (mirrored from the layout). */
    private volatile ViewMode currentMode = ViewMode.MOBILE; // default

    @Getter
    private IyenResponsiveLayout iyenResponsiveLayout;

    /** Listener registration — cleaned up on detach. */
    private Registration modeListenerRegistration;

    /** Shared overlay container for drawers/dialogs/toasts mounted INSIDE <main>. */
    private final Div overlayHost = Components.div().styleName("iyen-overlay-host").build();

    protected AbstractIyenView() {
        // IMPORTANT: must match CSS ("main.iyen-responsive-layout")
        addClassName("iyen-responsive-layout");
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (!initialized) {
            initialized = true;

            // Build responsive layout once
            ViewMode initialMode = selectInitialMode();
            iyenResponsiveLayout = (initialMode == null)
                    ? new IyenResponsiveLayout(master(), detail())
                    : new IyenResponsiveLayout(master(), detail(), initialMode);

            // Allow subclasses to tweak separator / options
            configureLayout(iyenResponsiveLayout);

            // Compose: [ responsive content | overlay host ]
            add(iyenResponsiveLayout, overlayHost);
        }

        // Re-register on every attach because detach removes the previous registration.
        if (modeListenerRegistration == null) {
            modeListenerRegistration = iyenResponsiveLayout.addModeChangeListener(this::onViewModeChanged);
        }

        synchronizeCurrentMode(event);
    }

    @Override
    protected void onDetach(DetachEvent event) {
        if (modeListenerRegistration != null) {
            modeListenerRegistration.remove();
            modeListenerRegistration = null;
        }
        super.onDetach(event);
    }

    /** Protected access for subclasses to mount drawers/overlays in a single canonical place. */
    protected Div getOverlayHost() {
        return overlayHost;
    }

    /** Called whenever IyenResponsiveLayout detects a new mode. */
    protected void onViewModeChanged(ViewMode mode) {
        this.currentMode = mode;
    }

    private void synchronizeCurrentMode(AttachEvent event) {
        ViewMode layoutMode = iyenResponsiveLayout.getCurrentMode();
        if (layoutMode != null) {
            onViewModeChanged(layoutMode);
            return;
        }

        WindowSize windowSize = Signal.untracked(() -> event.getUI().getPage().windowSizeSignal().get());
        if (windowSize != null) {
            onViewModeChanged(UIUtils.getViewMode(windowSize.width(), windowSize.height()));
        }
    }

    /** Subclasses use this; always reads the latest mirrored mode. */
    protected ViewMode getViewMode() {
        return currentMode;
    }

    /**
     * Hook for subclasses to configure the responsive layout
     * (separator, color, etc.) without touching core logic.
     */
    protected void configureLayout(IyenResponsiveLayout layout) {
        // default no separator; subclasses can override:
        // layout.withSeparator();
        // or layout.withSeparator(SeparatorColor.PRIMARY);
    }

    @Override
    public ViewMode selectInitialMode() {
        return null;
    }

}