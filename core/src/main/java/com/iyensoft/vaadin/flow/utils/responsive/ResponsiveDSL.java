package com.iyensoft.vaadin.flow.utils.responsive;

import com.vaadin.flow.component.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * ResponsiveDSL (core, scope-safe)
 * - Breakpoint scopes expose only generic per-breakpoint utilities.
 * - Cross-breakpoint "sugar" moved to ResponsivePlus wrapper.
 * - Keeps bundle registry and semantic(ViewMode) support.
 *
 * Assumes colon-prefixed utilities exist in theme CSS:
 *   .sm\:flex, .lg\:flex-row, .sm\:w-full, .lg\:gap-x-m, etc.
 */
public final class ResponsiveDSL {

    private ResponsiveDSL() {}

    /* ---------------- Bundle registry (Plan-level) ---------------- */
    private static final Map<String, Consumer<Plan>> BUNDLES = new ConcurrentHashMap<>();

    /** Register or replace a named bundle (thread-safe). */
    public static void bundle(String name, Consumer<Plan> bundleFn) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(bundleFn, "bundleFn");
        BUNDLES.put(name, bundleFn);
    }

    /* ---------------- Tokens ---------------- */
    public enum BP { SM("sm"), MD("md"), LG("lg"), XL("xl"), X2L("2xl");
        final String key; BP(String k){ this.key = k; } public String key(){ return key; } }

    /**
     * Spacing tokens must match your CSS scale (0, xs, s, m, l, xl).
     * ZERO maps to "0" so p(Space.ZERO) => "p-0", mx(Space.ZERO) => "mx-0".
     */
    public enum Space { ZERO("0"), XS("xs"), S("s"), M("m"), L("l"), XL("xl");
        final String key; Space(String k){ this.key = k; } public String key(){ return key; } }

    public enum Align { START("start"), CENTER("center"), END("end"), STRETCH("stretch");
        final String key; Align(String k){ this.key = k; } public String key(){ return key; } }

    public enum Justify { START("start"), CENTER("center"), END("end"),
        BETWEEN("between"), AROUND("around"), EVENLY("evenly");
        final String key; Justify(String k){ this.key = k; } public String key(){ return key; } }

    /* ---------------- Entry ---------------- */
    public static Plan on(Component target) { return new Plan(target); }

    /* ======================================================================
     * Plan: holds staged utilities by breakpoint, lifecycle helpers
     * ====================================================================== */
    public static final class Plan {
        private final Component target;
        private final Map<String, LinkedHashSet<String>> byPrefix = new LinkedHashMap<>();
        private final LinkedHashSet<String> lastApplied = new LinkedHashSet<>();

        // Optional semantic tag
        private ViewMode semanticMode;

        Plan(Component target) {
            this.target = Objects.requireNonNull(target, "target");
        }

        /* ---------- Scopes (single breakpoint) ---------- */
        public Scope sm()  { return new Scope(this, BP.SM); }
        public Scope md()  { return new Scope(this, BP.MD); }
        public Scope lg()  { return new Scope(this, BP.LG); }
        public Scope xl()  { return new Scope(this, BP.XL); }
        public Scope x2l() { return new Scope(this, BP.X2L); }
        public Scope at(BP bp) { return new Scope(this, bp); }

        /* ---------- Multi-scope (several breakpoints at once) ---------- */
        public MultiScope forBreakpoints(BP... bps) {
            if (bps == null || bps.length == 0) throw new IllegalArgumentException("At least one breakpoint required");
            return new MultiScope(this, List.of(bps));
        }

        /** Apply a named custom bundle registered via ResponsiveDSL.bundle(...). */
        public Plan use(String bundleName) {
            Consumer<Plan> fn = BUNDLES.get(bundleName);
            if (fn == null) throw new IllegalArgumentException("Bundle not found: " + bundleName);
            fn.accept(this);
            return this;
        }

        /** Also apply semantic tag + data-view-prefix from a ViewMode. */
        public Plan semantic(ViewMode mode) {
            this.semanticMode = mode;
            return this;
        }

        /* ---------- Lifecycle ---------- */
        /**
         * Apply the staged classes to the component (adds them to classList).
         * Subsequent calls will add duplicates unless you call reset() or applyReplace().
         */
        public void apply() {
            if (semanticMode != null) {
                semanticMode.applyTo(target); // user-provided: adds "vm-..." + data-view-prefix="..."
            }
            LinkedHashSet<String> toAdd = computePrefixedClasses();
            if (toAdd.isEmpty()) return;
            for (String cls : toAdd) target.getElement().getClassList().add(cls);
            lastApplied.clear();
            lastApplied.addAll(toAdd);
        }

        /** Remove last applied by this plan (if any). */
        public void remove() {
            if (lastApplied.isEmpty()) return;
            for (String cls : lastApplied) target.getElement().getClassList().remove(cls);
            lastApplied.clear();
        }

        /** Convenience: remove previous classes (if any), then apply current plan. */
        public void applyReplace() {
            remove();
            apply();
        }

        /** Clear the staged plan; semanticMode intentionally preserved. */
        public Plan reset() {
            byPrefix.clear();
            return this;
        }

        public Plan withDefaultClassNames(String... classNames) {
            return addRaw(classNames);
        }

        /** Add literal class names immediately to the element (already prefixed). */
        public Plan addRaw(String... classNames) {
            if (classNames != null) {
                for (String cls : classNames) {
                    if (cls != null && !cls.isBlank()) {

                        // Split on any whitespace; trim and filter blanks
                        String[] tokens = java.util.Arrays.stream(cls.split("\\s+"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toArray(String[]::new);

                        if (tokens.length > 0) {
                            target.addClassNames(tokens);
                        } else {
                            target.getElement().getClassList().add(cls.trim());
                        }
                    }
                }
            }
            return this;
        }

        /* ---------- Internals ---------- */
        void add(BP bp, String... baseClasses) {
            if (baseClasses == null || baseClasses.length == 0) return;
            LinkedHashSet<String> bucket = byPrefix.computeIfAbsent(bp.key(), k -> new LinkedHashSet<>());
            for (String b : baseClasses) {
                if (b == null) continue;
                String s = b.trim();
                if (!s.isEmpty()) bucket.add(s);
            }
        }

        LinkedHashSet<String> computePrefixedClasses() {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            byPrefix.forEach((prefix, bases) -> {
                for (String base : bases) out.add(prefix + ":" + base);
            });
            return out;
        }
    }

    /* ======================================================================
     * Scope (single breakpoint): only per-breakpoint utilities belong here
     * ====================================================================== */
    public static final class Scope {
        private final Plan plan;
        private final BP bp;

        Scope(Plan plan, BP bp) { this.plan = plan; this.bp = bp; }

        /* Navigate */
        public Plan and() { return plan; }               // return to plan
        public Scope sm()  { return plan.sm(); }
        public Scope md()  { return plan.md(); }
        public Scope lg()  { return plan.lg(); }
        public Scope xl()  { return plan.xl(); }
        public Scope x2l() { return plan.x2l(); }
        public Scope at(BP bp) { return plan.at(bp); }

        /* Raw */
        public Scope add(String... baseUtilities) { plan.add(bp, baseUtilities); return this; }

        /* ------------ FLEX layout primitives ------------ */
        public Scope flex()    { return add("flex"); }
        public Scope row()     { return add("flex-row"); }
        public Scope col()     { return add("flex-col"); }
        public Scope wrap()    { return add("flex-wrap"); }
        public Scope noWrap()  { return add("flex-nowrap"); }

        /* Spacing (gaps) */
        public Scope gap(Space s)  { return add("gap-"   + s.key()); }
        public Scope gapX(Space s) { return add("gap-x-" + s.key()); }
        public Scope gapY(Space s) { return add("gap-y-" + s.key()); }

        // column/row semantic gap helpers
        public Scope gapForCol(Space s) { return col().gapY(s); }
        public Scope gapForRow(Space s) { return row().gapX(s); }

        /* Align / justify */
        public Scope items(Align a)         { return add("items-" + a.key()); }
        public Scope justify(Justify j)     { return add("justify-" + j.key()); }
        public Scope placeItems(Align a)    { return add("place-items-" + a.key()); }
        public Scope placeContent(Justify j){ return add("place-content-" + j.key()); }

        /* Width & flex growth */
        public Scope fullWidth()   { return add("w-full"); }
        public Scope grow()        { return add("flex-1"); }
        public Scope noGrow()      { return add("flex-none"); }

        /** Grow and allow shrink (best for inputs in flex-row). => flex-1 + min-w-0 */
        public Scope growContent() { return add("flex-1", "min-w-0"); }
        /** Keep natural size (no growth). */
        public Scope fixedItem()   { return add("flex-none"); }
        public Scope growOnly()    { return add("flex-1"); }
        public Scope allowShrink() { return add("min-w-0"); }
        public Scope preventGrow() { return add("flex-none"); }

        /* Visibility */
        public Scope hidden()      { return add("hidden"); }

        /* ---------------- PADDING (tokenized) ---------------- */
        public Scope p(Space s)  { return add("p-"  + s.key()); }
        public Scope px(Space s) { return add("px-" + s.key()); }
        public Scope py(Space s) { return add("py-" + s.key()); }
        public Scope pt(Space s) { return add("pt-" + s.key()); }
        public Scope pr(Space s) { return add("pr-" + s.key()); }
        public Scope pb(Space s) { return add("pb-" + s.key()); }
        public Scope pl(Space s) { return add("pl-" + s.key()); }

        // Zero helpers for padding (syntactic sugar)
        public Scope p0()  { return p(Space.ZERO); }
        public Scope px0() { return px(Space.ZERO); }
        public Scope py0() { return py(Space.ZERO); }
        public Scope pt0() { return pt(Space.ZERO); }
        public Scope pr0() { return pr(Space.ZERO); }
        public Scope pb0() { return pb(Space.ZERO); }
        public Scope pl0() { return pl(Space.ZERO); }

        /* ---------------- MARGIN (tokenized) ---------------- */
        public Scope m(Space s)  { return add("m-"  + s.key()); }
        public Scope mx(Space s) { return add("mx-" + s.key()); }
        public Scope my(Space s) { return add("my-" + s.key()); }
        public Scope mt(Space s) { return add("mt-" + s.key()); }
        public Scope mr(Space s) { return add("mr-" + s.key()); }
        public Scope mb(Space s) { return add("mb-" + s.key()); }
        public Scope ml(Space s) { return add("ml-" + s.key()); }

        // Margin zero + auto
        public Scope m0()     { return m(Space.ZERO); }
        public Scope mx0()    { return mx(Space.ZERO); }
        public Scope my0()    { return my(Space.ZERO); }
        public Scope mt0()    { return mt(Space.ZERO); }
        public Scope mr0()    { return mr(Space.ZERO); }
        public Scope mb0()    { return mb(Space.ZERO); }
        public Scope ml0()    { return ml(Space.ZERO); }
        /** Equivalent of Tailwind's m-auto (we intentionally expose only m-auto to match CSS). */
        public Scope mAuto()  { return add("m-auto"); }

        // Negative margins (match CSS: -m-*, -mx-*, -my-*, -mt/-mr/-mb/-ml)
        public Scope negM(Space s)  { return add("-m-"  + s.key()); }
        public Scope negMx(Space s) { return add("-mx-" + s.key()); }
        public Scope negMy(Space s) { return add("-my-" + s.key()); }
        public Scope negMt(Space s) { return add("-mt-" + s.key()); }
        public Scope negMr(Space s) { return add("-mr-" + s.key()); }
        public Scope negMb(Space s) { return add("-mb-" + s.key()); }
        public Scope negMl(Space s) { return add("-ml-" + s.key()); }
    }

    /* ======================================================================
     * MultiScope (several breakpoints at once)
     * ====================================================================== */
    public static final class MultiScope {
        private final Plan plan;
        private final List<BP> bps;

        MultiScope(Plan plan, List<BP> bps) { this.plan = plan; this.bps = List.copyOf(bps); }

        public Plan and() { return plan; }

        private MultiScope add(String... utils) {
            for (BP bp : bps) plan.add(bp, utils);
            return this;
        }

        // Growth helpers
        public MultiScope growContent() { return add("flex-1", "min-w-0"); }
        public MultiScope fixedItem()   { return add("flex-none"); }
        public MultiScope growOnly()    { return add("flex-1"); }
        public MultiScope allowShrink() { return add("min-w-0"); }

        // Layout
        public MultiScope flex()    { return add("flex"); }
        public MultiScope row()     { return add("flex-row"); }
        public MultiScope col()     { return add("flex-col"); }
        public MultiScope wrap()    { return add("flex-wrap"); }
        public MultiScope noWrap()  { return add("flex-nowrap"); }

        // Gap
        public MultiScope gap(Space s)  { return add("gap-"   + s.key()); }
        public MultiScope gapX(Space s) { return add("gap-x-" + s.key()); }
        public MultiScope gapY(Space s) { return add("gap-y-" + s.key()); }
        public MultiScope items(Align a) { return add("items-" + a.key()); }
        public MultiScope justify(Justify j) { return add("justify-" + j.key()); }
        public MultiScope fullWidth() { return add("w-full"); }
        public MultiScope hidden()    { return add("hidden"); }

        // Padding (all variants)
        public MultiScope p(Space s)  { return add("p-"  + s.key()); }
        public MultiScope px(Space s) { return add("px-" + s.key()); }
        public MultiScope py(Space s) { return add("py-" + s.key()); }
        public MultiScope pt(Space s) { return add("pt-" + s.key()); }
        public MultiScope pr(Space s) { return add("pr-" + s.key()); }
        public MultiScope pb(Space s) { return add("pb-" + s.key()); }
        public MultiScope pl(Space s) { return add("pl-" + s.key()); }

        public MultiScope p0()  { return p(Space.ZERO); }
        public MultiScope px0() { return px(Space.ZERO); }
        public MultiScope py0() { return py(Space.ZERO); }
        public MultiScope pt0() { return pt(Space.ZERO); }
        public MultiScope pr0() { return pr(Space.ZERO); }
        public MultiScope pb0() { return pb(Space.ZERO); }
        public MultiScope pl0() { return pl(Space.ZERO); }

        // Margin (all variants)
        public MultiScope m(Space s)  { return add("m-"  + s.key()); }
        public MultiScope mx(Space s) { return add("mx-" + s.key()); }
        public MultiScope my(Space s) { return add("my-" + s.key()); }
        public MultiScope mt(Space s) { return add("mt-" + s.key()); }
        public MultiScope mr(Space s) { return add("mr-" + s.key()); }
        public MultiScope mb(Space s) { return add("mb-" + s.key()); }
        public MultiScope ml(Space s) { return add("ml-" + s.key()); }

        public MultiScope m0()  { return m(Space.ZERO); }
        public MultiScope mx0() { return mx(Space.ZERO); }
        public MultiScope my0() { return my(Space.ZERO); }
        public MultiScope mt0() { return mt(Space.ZERO); }
        public MultiScope mr0() { return mr(Space.ZERO); }
        public MultiScope mb0() { return mb(Space.ZERO); }
        public MultiScope ml0() { return ml(Space.ZERO); }

        /** m-auto only; (no mx-auto/my-auto in your CSS) */
        public MultiScope mAuto() { return add("m-auto"); }

        // Negative margins
        public MultiScope negM(Space s)  { return add("-m-"  + s.key()); }
        public MultiScope negMx(Space s) { return add("-mx-" + s.key()); }
        public MultiScope negMy(Space s) { return add("-my-" + s.key()); }
        public MultiScope negMt(Space s) { return add("-mt-" + s.key()); }
        public MultiScope negMr(Space s) { return add("-mr-" + s.key()); }
        public MultiScope negMb(Space s) { return add("-mb-" + s.key()); }
        public MultiScope negMl(Space s) { return add("-ml-" + s.key()); }
    }

    /* ======================================================================
     * Optional integration point (your existing class)
     * ====================================================================== */
    /** Your existing enum/class. Keep as-is in your codebase. */
    public interface ViewMode {
        String getPrefix();               // e.g., "lg"
        void applyTo(Component target);   // e.g., adds "vm-desktop" + data-view-prefix="lg"
    }
}