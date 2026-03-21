package com.iyensoft.vaadin.flow.utils.responsive;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Automatically applies responsive tags to components based on current window ViewMode and Orientation.
 *
 * Adds:
 *   - ViewMode classes: "vm-<mode>"
 *   - data-view-prefix="sm|md|lg|xl|2xl"
 *   - Orientation classes: "portrait" / "landscape"
 *   - data-orientation="portrait|landscape"
 *
 * Supports:
 *   - apply(component): basic tagging + auto-retag on resize/rotation
 *   - apply(component, classesByMode): attaches responsive Tailwind-like classes per ViewMode
 *
 * Cleanup:
 *   - stop(component): removes listeners + responsive tags/classes
 */
public final class Responsive {

    private Responsive() {}

    /* -------------------------------------------------------------------------
     * Breakpoints (Tailwind-like defaults)
     * ------------------------------------------------------------------------- */
    public static int SM_MAX = 767;
    public static int MD_MAX = 1023;
    public static int LG_MAX = 1279;
    public static int XL_MAX = 1535;

    private static final Map<Component, Registration> resizeRegistrations = new WeakHashMap<>();
    private static final Map<Component, Map<ViewMode, List<String>>> classesByModeRegistry = new WeakHashMap<>();

    /* -------------------------------------------------------------------------
     * Entry Points
     * ------------------------------------------------------------------------- */

    /** Apply responsive tagging and keep in sync with window size/orientation. */
    public static void apply(Component root) {
        if (root == null) return;

        // Wait until attached
        if (root.getUI().isEmpty()) {
            root.addAttachListener(e -> apply(e.getSource()));
            return;
        }

        UI ui = root.getUI().orElse(UI.getCurrent());
        if (ui == null) return;

        Page page = ui.getPage();
        stop(root); // ensure no duplicates

        // Initial tagging with current client details
        page.retrieveExtendedClientDetails(details -> {
            int w = details.getWindowInnerWidth();
            int h = details.getWindowInnerHeight();
            retag(root, resolveViewMode(w), Orientation.of(w, h));
        });

        // Retag on resize
        Registration reg = page.addBrowserWindowResizeListener(e -> {
            retag(root, resolveViewMode(e.getWidth()), Orientation.of(e.getWidth(), e.getHeight()));
        });

        resizeRegistrations.put(root, reg);
        root.addDetachListener((DetachEvent e) -> stop(e.getSource()));
    }

    /** Apply responsive tagging + register ViewMode-specific classes. */
    public static void apply(Component root, Map<ViewMode, List<String>> classesByMode) {
        apply(root);
        if (root == null || classesByMode == null || classesByMode.isEmpty()) return;

        Map<ViewMode, List<String>> normalized = normalize(classesByMode);
        if (!normalized.isEmpty()) {
            classesByModeRegistry.put(root, normalized);
            attachResponsiveClasses(root, normalized);
        }
    }

    public static void apply(Component root, ResponsiveClasses cfg) {
        apply(root, (cfg == null ? null : cfg.toMap()));
    }

    /** Stop listening and remove all responsive tags/classes. */
    public static void stop(Component root) {
        if (root == null) return;

        Optional.ofNullable(resizeRegistrations.remove(root)).ifPresent(Registration::remove);
        classesByModeRegistry.remove(root);
        clearTags(root);
    }

    /* -------------------------------------------------------------------------
     * ViewMode Resolution
     * ------------------------------------------------------------------------- */

    /** Determine ViewMode from window width. */
    public static ViewMode resolveViewMode(int width) {
        if (width <= SM_MAX) return ViewMode.MOBILE;
        if (width <= MD_MAX) return ViewMode.TABLET;
        if (width <= LG_MAX) return ViewMode.DESKTOP;
        if (width <= XL_MAX) return ViewMode.LARGE_DESKTOP;
        return ViewMode.ULTRA_WIDE;
    }

    /* -------------------------------------------------------------------------
     * Tag lifecycle
     * ------------------------------------------------------------------------- */

    /** Retag with new ViewMode + Orientation, then reapply responsive classes. */
    private static void retag(Component root, ViewMode vm, Orientation o) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(vm);
        Objects.requireNonNull(o);

        clearTags(root);

        // Apply ViewMode tags
        vm.applyTo(root);

        // Apply Orientation tags
        root.getElement().getClassList().add(o.toClassName());
        root.getElement().setAttribute("data-orientation", o.toAttrValue());

        // Re-apply mode-specific classes (if any)
        Map<ViewMode, List<String>> cfg = classesByModeRegistry.get(root);
        if (cfg != null) attachResponsiveClasses(root, cfg);
    }

    /** Remove all ViewMode, orientation and prefixed responsive classes. */
    private static void clearTags(Component root) {
        // Remove ViewMode classes
        for (ViewMode vm : ViewMode.values()) vm.removeApplied(root);

        // Remove orientation
        root.getElement().getClassList().remove("portrait");
        root.getElement().getClassList().remove("landscape");
        root.getElement().removeAttribute("data-orientation");

        // Remove prefixed classes (sm:*, md:*, ...)
        List<String> toRemove = root.getElement().getClassList().stream()
                .filter(cls -> cls.matches("^(sm|md|lg|xl|2xl):.*"))
                .toList();

        toRemove.forEach(root.getElement().getClassList()::remove);
    }

    /* -------------------------------------------------------------------------
     * Responsive Class Handling
     * ------------------------------------------------------------------------- */

    public static void attachResponsiveClasses(Component component,
                                               boolean normalise,
                                               Map<ViewMode, List<String>> classesByMode) {

        Map<ViewMode, List<String>> cfg = normalise
                ? normalize(classesByMode)
                : classesByMode;

        if (cfg != null && !cfg.isEmpty()) {
            classesByModeRegistry.put(component, cfg);
            attachResponsiveClasses(component, cfg);
        }
    }

    /** Attach "sm:*", "md:*", ... classes. */
    private static void attachResponsiveClasses(Component root,
                                                Map<ViewMode, List<String>> classesByMode) {

        CssHelpers.addResponsiveClasses(root, classesByMode);
    }

    /* -------------------------------------------------------------------------
     * Utility
     * ------------------------------------------------------------------------- */

    /** Normalize class map: trim, distinct, remove blanks. */
    private static Map<ViewMode, List<String>> normalize(Map<ViewMode, List<String>> raw) {
        Map<ViewMode, List<String>> cleaned = new EnumMap<>(ViewMode.class);

        raw.forEach((mode, list) -> {
            if (mode == null || list == null) return;

            List<String> filtered = list.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) cleaned.put(mode, filtered);
        });

        return cleaned;
    }
}