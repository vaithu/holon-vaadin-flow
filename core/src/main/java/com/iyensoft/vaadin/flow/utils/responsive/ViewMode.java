package com.iyensoft.vaadin.flow.utils.responsive;

import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.vaadin.flow.component.Component;

import java.util.*;

/*
*
How to use
------------------------------------------------
Mark a container with the current view mode
var container = Components.layout().build();
ViewMode.TABLET.applyTo(container); // adds "vm-tablet" + data-view-prefix="md"
--------------------------------------------------------------
 Generate a prefixed utility class and content it
 String cls = ViewMode.DESKTOP.toCssClass("flex-row"); // "lg:flex-row"
container.getElement().getClassList().content(cls);
-------------------------------------------------------
Example CSS targeting
* /* Semantic class (high-level)
*.vm-mobile .toolbar { flex-direction: column; row-gap: var(--lumo-space-s); }

/* Attribute-based (prefix-driven)
*[data-view-prefix="lg"] .toolbar { flex-direction: row; column-gap: var(--lumo-space-m); }
-------------------------------------------------------
*
 */

public enum ViewMode {

    // You can use either constructor:
    // - prefix-only: ViewMode will derive Breakpoint from the prefix
    // - prefix + Breakpoint: overrides/explicit mapping

    MOBILE("sm"),                         // phones → derives Breakpoint.SMALL
    TABLET("md"),                         // small tablets → derives Breakpoint.MEDIUM
    DESKTOP("lg"),                        // laptops / standard screens → derives Breakpoint.LARGE
    LARGE_DESKTOP("xl"),                  // large monitors → derives Breakpoint.XLARGE
    ULTRA_WIDE("2xl"),                    // extra-wide displays → derives Breakpoint.XXLARGE

    // Posture-aware mobile variants (same prefix “sm”)
    MOBILE_PORTRAIT("sm", Breakpoint.SMALL),
    MOBILE_LANDSCAPE("sm", Breakpoint.SMALL);




    private final String prefix;
    private final Breakpoint breakpoint; // may be null if prefix has no mapping
    private final String semanticClass;  // precomputed once: "vm-mobile", "vm-tablet", etc.
    private final String prefixColon;    // precomputed once: "sm:", "md:", "lg:", etc.

    /** Prefix-only constructor: Breakpoint is derived from the prefix. */
    ViewMode(String prefix) {
        this.prefix = prefix;
        this.breakpoint = Breakpoint.fromPrefixOrNull(prefix);
        this.semanticClass = "vm-" + name().toLowerCase().replace('_', '-');
        this.prefixColon = prefix + ":";
    }

    /** Explicit constructor: if you want to override the derived mapping. */
    ViewMode(String prefix, Breakpoint breakpoint) {
        this.prefix = prefix;
        this.breakpoint = breakpoint;
        this.semanticClass = "vm-" + name().toLowerCase().replace('_', '-');
        this.prefixColon = prefix + ":";
    }

    // ── Static O(1) reverse-lookup maps ───────────────────────────────────────
    // Built once at class-load time; avoids values() array allocation on every call.

    private static final Map<Breakpoint, ViewMode> BY_BREAKPOINT;
    private static final Map<String, ViewMode>     BY_PREFIX;

    static {
        Map<Breakpoint, ViewMode> byBp  = new EnumMap<>(Breakpoint.class);
        Map<String, ViewMode>     byPfx = new HashMap<>(7);
        for (ViewMode vm : values()) {
            if (vm.breakpoint != null) byBp.putIfAbsent(vm.breakpoint, vm);
            byPfx.putIfAbsent(vm.prefix, vm);
        }
        BY_BREAKPOINT = Collections.unmodifiableMap(byBp);
        BY_PREFIX     = Collections.unmodifiableMap(byPfx);
    }

    /** Returns prefix like "sm", "md", "lg", "xl", "2xl". */
    public String getPrefix() {
        return prefix;
    }

    /** Maps ViewMode → Breakpoint (may be null if no mapping was found for this prefix). */
    public Breakpoint toBreakpoint() {
        return breakpoint;
    }

    /** O(1) reverse mapping: Breakpoint → ViewMode (throws if not mapped). */
    public static ViewMode fromBreakpoint(Breakpoint bp) {
        if (bp == null) {
            throw new IllegalArgumentException("Breakpoint cannot be null");
        }
        ViewMode vm = BY_BREAKPOINT.get(bp);
        if (vm == null) {
            throw new IllegalArgumentException("No ViewMode mapped for Breakpoint: " + bp);
        }
        return vm;
    }

    /** O(1) reverse mapping from prefix string (e.g., "md" → TABLET). */
    public static ViewMode fromPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("Prefix cannot be null/blank");
        }
        ViewMode vm = BY_PREFIX.get(prefix);
        if (vm == null) {
            throw new IllegalArgumentException("Unknown ViewMode prefix: " + prefix);
        }
        return vm;
    }

    /**
     * Generates a CSS class name like:
     *   "md:flex-row", "lg:hidden", "sm:block"
     *
     * @param base a CSS utility or semantic class (e.g., "flex-row", "hidden")
     * @return prefixed class "<prefix>:<base>"
     */
    public String toCssClass(String base) {
        // Uses pre-computed prefixColon ("lg:") to avoid string concatenation on every builder call.
        return prefixColon + base;
    }

    /**
     * Tags a Vaadin component with:
     *  - A semantic class: "vm-mobile", "vm-tablet", "vm-desktop", "vm-large-desktop", "vm-ultra-wide"
     *  - A data attribute: data-view-prefix="sm|md|lg|xl|2xl"
     *  // 1) Tag a container with the current ViewMode (semantic + data attribute only)
     * ViewMode.TABLET.applyTo(container);
     * // 2) Tag + content responsive utility classes in one call
     * ViewMode.DESKTOP.applyTo(container, "flex-row", "items-baseline", "gap-x-m");
     * // Adds: "vm-desktop", data-view-prefix="lg", and "lg:flex-row", "lg:items-baseline", "lg:gap-x-m"
     * // 3) Generate a single class and content manually (if needed)
     * String cls = ViewMode.MOBILE.toCssClass("flex-col"); // "sm:flex-col"
     * container.getElement().getClassList().content(cls);
     */
    public void applyTo(Component component) {
        if (component == null) return;
        component.getElement().getClassList().add(semanticClass);
        component.getElement().setAttribute("data-view-prefix", prefix);
    }

    /**
     * Applies semantic class + data attribute and also adds prefixed classes
     * for each provided base class using this mode's prefix.
     */
    public void applyTo(Component component, String... baseClasses) {
        applyTo(component); // content semantic class + data attribute
        if (component == null || baseClasses == null) return;

        for (String base : baseClasses) {
            if (base == null || base.isBlank()) continue;
            String normalized = base.trim();
            component.getElement().getClassList().add(toCssClass(normalized));
        }
    }

    /**
     * Adds the same base class for multiple ViewModes in one call:
     *   ViewMode.applyAll(container, "flex-row", MOBILE, TABLET, DESKTOP)
     * Produces:
     *   "sm:flex-row", "md:flex-row", "lg:flex-row"
     *
     * Additionally, it tags the component with the FIRST mode's semantic class and data attribute,
     * e.g., "vm-mobile" + data-view-prefix="sm".
     * // 1) Tag + content multiple responsive classes for a single base
     * ViewMode.applyAll(container, "flex-row", ViewMode.MOBILE, ViewMode.TABLET, ViewMode.DESKTOP);
     * // Adds: "vm-mobile", data-view-prefix="sm", and "sm:flex-row", "md:flex-row", "lg:flex-row"
     *
     * // 2) Tag + content multiple base classes for a single mode
     * ViewMode.LARGE_DESKTOP.applyTo(container, "grid", "gap-x-m");
     * // Adds: "vm-large-desktop", data-view-prefix="xl", and "xl:grid", "xl:gap-x-m"
     *
     * // 3) Just tag the component (semantic + data attribute)
     * ViewMode.ULTRA_WIDE.applyTo(container);
     * // Adds: "vm-ultra-wide", data-view-prefix="2xl"
     */
    public static void applyAll(Component component, String baseClass, ViewMode... modes) {
        if (component == null || baseClass == null || baseClass.isBlank() || modes == null || modes.length == 0) {
            return;
        }

        // Tag with the first mode's semantic class + data attribute
        modes[0].applyTo(component);

        String normalized = baseClass.trim();
        for (ViewMode vm : modes) {
            if (vm == null) continue;
            component.getElement().getClassList().add(vm.toCssClass(normalized));
        }
    }

    /**
     * Removes what this ViewMode typically applies to the component:
     *  - Semantic class "vm-<mode>"
     *  - data-view-prefix (only if it matches this mode's prefix)
     *  - All responsive classes starting with "<prefix>:"
     */
    public void removeApplied(Component component) {
        if (component == null) return;

        // 1) Remove semantic class for this mode
        component.getElement().getClassList().remove(semanticClass);

        // 2) Remove data-view-prefix ONLY if it matches this mode's prefix
        String current = component.getElement().getAttribute("data-view-prefix");
        if (prefix.equals(current)) {
            component.getElement().removeAttribute("data-view-prefix");
        }

        // 3) Remove all classes that start with "<prefix>:" — use precomputed prefixColon.
        // removeIf avoids the two-pass collect-then-remove pattern (no intermediate List allocation).
        var classList = component.getElement().getClassList();
        classList.removeIf(cls -> cls != null && cls.startsWith(prefixColon));
    }
}