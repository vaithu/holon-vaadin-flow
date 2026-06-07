package com.iyensoft.vaadin.flow.utils.responsive;

import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsive master-detail layout
 * - MOBILE: master only
 * - TABLET/DESKTOP+: master + (optional) separator + detail
 * - Notifies listeners on mode changes
 * - Keeps SAME component instances; only re-composes lightweight wrapper
 * - Adds CSS hook classes so the final CSS can style/stick/scroll
 * <p>
 * Note: this class does NOT toggle 'iyen-mobile'/'iyen-desktop'.
 * Your CSS is driven by media queries + structural classes only.
 */
public class IyenResponsiveLayout extends Layout {

    private static final Logger log = LoggerFactory.getLogger(IyenResponsiveLayout.class);

    // Built once; we content CSS hooks directly on these
    private final Component master;
    private final Component detail;

    private boolean separatorEnabled;
    private SeparatorColor separatorColor;
    private final Layout separator;

    /**
     * Single lightweight wrapper we recompose into.
     */
    private final Layout parentLayout = new Layout();

    private ViewMode currentMode;

    public ViewMode getCurrentMode() {
        return currentMode;
    }
    private final ValueSignal<ViewMode> viewModeSignal = new ValueSignal<>(ViewMode.MOBILE);
    private Registration resizeEffectRegistration;
    private final List<Consumer<ViewMode>> modeListeners = new ArrayList<>();

    public IyenResponsiveLayout(IyenMasterBuilder masterBuilder, IyenDetailBuilder detailBuilder) {
        super();

        this.master = masterBuilder.build();
        this.detail = detailBuilder.build();

        this.separator = new Layout();
        this.separator.addClassName("iyen-md-separator");

        parentLayout.addClassName("iyen-md-row"); // matches CSS (row container)

        add(parentLayout);
    }

    public IyenResponsiveLayout(IyenMasterBuilder masterBuilder,
                                IyenDetailBuilder detailBuilder,
                                ViewMode initialMode) {
        this(masterBuilder, detailBuilder);
        if (initialMode != null) {
            applyMode(initialMode); // also publishes to viewModeSignal
        }
    }

    // ---------------------------------------------------------------------
    // Configuration API
    // ---------------------------------------------------------------------

    public IyenResponsiveLayout withSeparator() {
        this.separatorEnabled = true;
        this.separatorColor = null;
        updateSeparatorStyle();
        if (currentMode != null) {
            renderForMode(currentMode);
        }
        return this;
    }

    public IyenResponsiveLayout withSeparator(SeparatorColor color) {
        this.separatorEnabled = true;   // enable separator
        this.separatorColor = color;    // apply color
        updateSeparatorStyle();
        if (currentMode != null) {
            renderForMode(currentMode);
        }
        return this;
    }

    /**
     * Reactive mode Signal API for external consumers (readonly).
     * Use inside Signal.effect() or Signal.computed() only.
     */
    public Signal<ViewMode> viewModeSignal() {
        return viewModeSignal.asReadonly();
    }

    /**
     * Subscribe to mode changes. Listener is called immediately with the current mode
     * (if known) and on every subsequent change.
     */
    public Registration addModeChangeListener(Consumer<ViewMode> listener) {
        modeListeners.add(listener);
        if (currentMode != null) listener.accept(currentMode);
        return () -> modeListeners.remove(listener);
    }

    private boolean shouldShowSeparator() {
        return separatorEnabled; // color is purely styling; enabled controls visibility
    }

    private void updateSeparatorStyle() {
        // Remove any previously applied color class (both old prefix and utilities.css pattern)
        separator.getClassNames().removeIf(name ->
                name.startsWith("iyen-separator--") || name.startsWith("color-bg-"));
        if (separatorColor != null) {
            // Use the utilities.css class name (e.g. "color-bg-contrast-20", "color-bg-primary")
            separator.addClassName(separatorColor.getClassName());
        }
    }

    // ---------------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------------

    private boolean isComposedFor(ViewMode mode) {
        boolean hasMaster = master.getParent().isPresent() && master.getParent().get() == parentLayout;
        boolean hasDetail = detail.getParent().isPresent() && detail.getParent().get() == parentLayout;
        boolean hasSep = separator.getParent().isPresent() && separator.getParent().get() == parentLayout;

        return switch (mode) {
            case MOBILE, MOBILE_PORTRAIT, MOBILE_LANDSCAPE -> hasMaster && !hasDetail && !hasSep;
            case TABLET, DESKTOP, LARGE_DESKTOP, ULTRA_WIDE ->
                    hasMaster && hasDetail && (!shouldShowSeparator() || hasSep);
        };
    }

    private void renderForMode(ViewMode mode) {
        // Skip only if we are already correctly composed for this mode
        if (mode == currentMode && isComposedFor(mode)) {
            return;
        }

        log.debug("Recomposing for view mode: {} (previous: {})", mode, currentMode);
        currentMode = mode;

        // Re-compose using the SAME component instances (no new Grid/provider)
        parentLayout.removeAll();

        switch (mode) {
            case MOBILE, MOBILE_PORTRAIT, MOBILE_LANDSCAPE -> {
                // master only (CSS handles stacking/spacing)
                addClassName("iyen-mobile");
                removeClassName("iyen-desktop");
                parentLayout.add(master);
            }
            case TABLET, DESKTOP, LARGE_DESKTOP, ULTRA_WIDE -> {
                addClassName("iyen-desktop");
                removeClassName("iyen-mobile");
                // two-pane: master + (optional) separator + detail
                parentLayout.add(master);
                if (shouldShowSeparator()) parentLayout.add(separator);
                parentLayout.add(detail);
            }
            default -> throw new IllegalStateException("Unsupported view mode: " + mode);
        }
    }

    private void applyMode(ViewMode newMode) {
        if (newMode == null) return;

        boolean modeChanged = newMode != this.currentMode;
        // Publish mode changes only after successful rendering to keep observers in sync with UI composition.
        renderForMode(newMode);

        if (modeChanged) {
            viewModeSignal.set(newMode);
            notifyModeListeners(newMode);
            log.debug("Responsive mode = {}", this.currentMode);
        }
    }

    private void notifyModeListeners(ViewMode mode) {
        // ArrayList.forEach() uses direct array access — no iterator or copy allocation.
        modeListeners.forEach(l -> {
            try {
                l.accept(mode);
            } catch (Exception ex) {
                log.warn("Mode listener threw", ex);
            }
        });
    }

    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        registerResizeLifecycle(attachEvent);
    }

    private void registerResizeLifecycle(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        if (resizeEffectRegistration != null) {
            resizeEffectRegistration.remove();
            resizeEffectRegistration = null;
        }

        resizeEffectRegistration = WindowSizeTracker.track(ui, this, this::applyMode);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (resizeEffectRegistration != null) {
            resizeEffectRegistration.remove();
            resizeEffectRegistration = null;
        }
        super.onDetach(detachEvent);
    }

    // For tests / direct control
    public void forceMode(ViewMode mode) {
        applyMode(mode);
    }
}

