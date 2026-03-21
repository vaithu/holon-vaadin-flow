package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.HasIyenView;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import lombok.Getter;

/**
 * Base view that:
 *  - provides <main class="iyen-responsive-layout"> (CSS relies on this)
 *  - hosts a shared overlay container inside <main>
 *  - mirrors current ViewMode and exposes it to subclasses
 *  - wires a single IyenResponsiveLayout instance
 */
public abstract class AbstractIyenView extends Main implements HasIyenView {

    private boolean initialized = false;

    /** Latest known responsive mode (mirrored from the layout). */
    private volatile ViewMode currentMode = ViewMode.MOBILE; // default

    @Getter
    private IyenResponsiveLayout iyenResponsiveLayout;

    /** Shared overlay container for drawers/dialogs/toasts mounted INSIDE <main>. */
    private final Div overlayHost = new Div();

    protected AbstractIyenView() {
        // IMPORTANT: must match CSS ("main.iyen-responsive-layout")
        addClassName("iyen-responsive-layout");

        // Overlay host lives inside <main>; CSS controls its absolute fill and pointer-events
        overlayHost.addClassName("iyen-overlay-host");
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (!initialized) {
            initialized = true;

            // Build responsive layout once
            if (selectInitialMode() == null) {
                iyenResponsiveLayout = new IyenResponsiveLayout(master(), detail());
            } else {
                iyenResponsiveLayout = new IyenResponsiveLayout(master(), detail(), selectInitialMode());
            }

            // Allow subclasses to tweak separator / options
            configureLayout(iyenResponsiveLayout);

            // Mirror mode updates into this base class (subclasses can read via getViewMode())
            iyenResponsiveLayout.addModeChangeListener(this::onViewModeChanged);

            // Compose: [ responsive content | overlay host ]
            add(iyenResponsiveLayout, overlayHost);
        }
    }

    /** Protected access for subclasses to mount drawers/overlays in a single canonical place. */
    protected Div getOverlayHost() {
        return overlayHost;
    }

    /** Called whenever IyenResponsiveLayout detects a new mode. */
    protected void onViewModeChanged(ViewMode mode) {
        this.currentMode = mode;
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