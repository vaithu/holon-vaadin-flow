package com.iyensoft.vaadin.flow.utils.responsive;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsive master-detail layout
 *  - MOBILE: master only
 *  - TABLET/DESKTOP+: master + (optional) separator + detail
 *  - Notifies listeners on mode changes
 *  - Keeps SAME component instances; only re-composes lightweight wrapper
 *  - Adds CSS hook classes so the final CSS can style/stick/scroll
 *
 * Note: this class does NOT toggle 'iyen-mobile'/'iyen-desktop'.
 * Your CSS is driven by media queries + structural classes only.
 */
@Slf4j
public class IyenResponsiveLayout extends Layout {

    // Built once; we add CSS hooks directly on these
    private final Component master;
    private final Component detail;

    private boolean separatorEnabled;
    private SeparatorColor separatorColor;
    private final Layout separator;

    /** Single lightweight wrapper we recompose into. */
    private final Layout parentLayout = new Layout();

    @Getter
    private ViewMode currentMode;

    private Registration resizeReg;

    // Subscribers to mode changes
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
        this.currentMode = initialMode;
        renderForMode(initialMode);
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

    private boolean shouldShowSeparator() {
        return separatorEnabled; // color is purely styling; enabled controls visibility
    }

    private void updateSeparatorStyle() {
        separator.getClassNames().removeIf(name -> name.startsWith("iyen-separator--"));
        if (separatorColor != null) {
            String colorClass = "iyen-separator--" + separatorColor.name().toLowerCase();
            separator.addClassName(colorClass);
        }
    }

    // ---------------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------------

    private boolean isComposedFor(ViewMode mode) {
        boolean hasMaster = master.getParent().isPresent() && master.getParent().get() == parentLayout;
        boolean hasDetail = detail.getParent().isPresent() && detail.getParent().get() == parentLayout;
        boolean hasSep    = separator.getParent().isPresent() && separator.getParent().get() == parentLayout;

        return switch (mode) {
            case MOBILE,MOBILE_PORTRAIT,MOBILE_LANDSCAPE -> hasMaster && !hasDetail && !hasSep;
            case TABLET, DESKTOP, LARGE_DESKTOP, ULTRA_WIDE ->
                    hasMaster && hasDetail && (!shouldShowSeparator() || hasSep);
        };
    }

    private void renderForMode(ViewMode mode) {
        log.info("Rendering view mode: {} and currentMode {}", mode, currentMode);

        // Skip only if we are already correctly composed for this mode
        if (mode == currentMode && isComposedFor(mode)) return;
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

        if (newMode != this.currentMode) {
            this.currentMode = newMode;
            notifyModeListeners(newMode);
            renderForMode(newMode);
        } else {
            // Even if same family, allow re-render to fix empty wrapper cases
            renderForMode(newMode);
        }
        log.debug("Responsive mode = {}", this.currentMode);
    }

    // ---------------------------------------------------------------------
    // Mode subscription (for views/components to react)
    // ---------------------------------------------------------------------

    public Registration addModeChangeListener(Consumer<ViewMode> listener) {
        modeListeners.add(listener);
        // If mode is already known, notify immediately
        if (currentMode != null) listener.accept(currentMode);
        return () -> modeListeners.remove(listener);
    }

    private void notifyModeListeners(ViewMode mode) {
        for (var l : List.copyOf(modeListeners)) {
            try {
                l.accept(mode);
            } catch (Exception ex) {
                log.warn("Mode listener threw", ex);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        registerResizeLifecycle(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (resizeReg != null) {
            resizeReg.remove();
            resizeReg = null;
        }
        super.onDetach(detachEvent);

    }

    private void registerResizeLifecycle(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        Page page = ui.getPage();

        // 1) Initial client details (async) → compute + render once
        page.retrieveExtendedClientDetails(details -> {
            ViewMode initial = UIUtils.getViewMode(details.getBodyClientWidth());
            applyMode(initial);
        });

        // 2) Ongoing window resizes
        if (resizeReg == null) {
            resizeReg = page.addBrowserWindowResizeListener(event -> {
                ViewMode next = UIUtils.getViewMode(event.getWidth());
                applyMode(next);
            });
        }
    }

    // For tests / direct control
    public void forceMode(ViewMode mode) {
        applyMode(mode);
    }
}