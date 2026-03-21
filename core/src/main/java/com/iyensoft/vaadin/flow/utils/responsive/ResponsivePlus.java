package com.iyensoft.vaadin.flow.utils.responsive;

import com.vaadin.flow.component.Component;

import static com.iyensoft.vaadin.flow.utils.responsive.ResponsiveDSL.*;

/**
 * ResponsiveChain (Option A: Companion, Fluent)
 * ----------------------------------------------------------------------------
 * A fluent façade over ResponsiveDSL.Plan that:
 *  - keeps scope safety (sm()/md()/lg() scopes expose only per-breakpoint utilities)
 *  - keeps cross-breakpoint sugar on the main chain (e.g., rowDesktop())
 *  - avoids "Result of method call ignored" warnings (methods always return the chain)
 *
 * It DOES NOT change ResponsiveDSL; it wraps a Plan.
 * Your theme must provide colon-prefixed utilities (escaped in CSS):
 *   .sm\:flex, .lg\:flex-row, .sm\:w-full, .lg\:gap-x-m, etc.
 */
public final class ResponsivePlus {

    private final ResponsiveDSL.Plan plan;

    private ResponsivePlus(ResponsiveDSL.Plan plan) {
        this.plan = plan;
    }

    /** Start a fluent chain for a Vaadin component. */
    public static ResponsivePlus on(Component target) {
        return new ResponsivePlus(ResponsiveDSL.on(target));
    }

    /** Build a chain from an existing Plan. */
    public static ResponsivePlus from(ResponsiveDSL.Plan plan) {
        return new ResponsivePlus(plan);
    }



    /* ---------------------------------------------------------------------
     * Scopes (fluent, scope-safe)
     * ------------------------------------------------------------------- */

    public Scope sm()  { return new Scope(plan.sm(), this); }
    public Scope md()  { return new Scope(plan.md(), this); }
    public Scope lg()  { return new Scope(plan.lg(), this); }
    public Scope xl()  { return new Scope(plan.xl(), this); }
    public Scope x2l() { return new Scope(plan.x2l(), this); }
    public Scope at(BP bp) { return new Scope(plan.at(bp), this); }

    /* ---------------------------------------------------------------------
     * Lifecycle / integration
     * ------------------------------------------------------------------- */

    public ResponsivePlus semantic(ResponsiveDSL.ViewMode mode) { plan.semantic(mode); return this; }
    public ResponsivePlus useBundle(String name) { plan.use(name); return this; }
    public ResponsivePlus reset() { plan.reset(); return this; }
    public ResponsivePlus addRaw(String... classes) { plan.addRaw(classes); return this; }

    public void apply()        { plan.apply(); }
    public void applyReplace() { plan.applyReplace(); }
    public void remove()       { plan.remove(); }

    public ResponsiveDSL.Plan plan() { return plan; }

    /* ---------------------------------------------------------------------
     * SUGAR (cross-breakpoint recipes on the MAIN chain)
     * ------------------------------------------------------------------- */

    // ----- Layout presets (Stack / Row)
    public ResponsivePlus stackMobile()        { plan.sm().add("flex-col", "gap-y-s"); return this; }
    public ResponsivePlus stackTablet()        { plan.md().add("flex-col", "gap-y-m"); return this; }
    public ResponsivePlus stackDesktop()       { plan.lg().add("flex-col", "gap-y-m"); return this; }
    public ResponsivePlus stackLargeDesktop()  { plan.xl().add("flex-col", "gap-y-l"); return this; }
    public ResponsivePlus stackUltraWide()     { plan.x2l().add("flex-col", "gap-y-xl"); return this; }

    public ResponsivePlus rowMobile()          { plan.sm().add("flex-row", "gap-x-s"); return this; }
    public ResponsivePlus rowTablet()          { plan.md().add("flex-row", "gap-x-m"); return this; }
    public ResponsivePlus rowDesktop()         { plan.lg().add("flex-row", "items-center", "gap-x-m"); return this; }
    public ResponsivePlus rowLargeDesktop()    { plan.xl().add("flex-row", "gap-x-l"); return this; }
    public ResponsivePlus rowUltraWide()       { plan.x2l().add("flex-row", "gap-x-xl"); return this; }

    // ----- Alignment presets
    public ResponsivePlus centerMobile()       { plan.sm().add("flex", "items-center", "justify-center"); return this; }
    public ResponsivePlus centerTablet()       { plan.md().add("flex", "items-center", "justify-center"); return this; }
    public ResponsivePlus centerDesktop()      { plan.lg().add("flex", "items-center", "justify-center"); return this; }
    public ResponsivePlus centerLargeDesktop() { plan.xl().add("flex", "items-center", "justify-center"); return this; }
    public ResponsivePlus centerUltraWide()    { plan.x2l().add("flex", "items-center", "justify-center"); return this; }

    public ResponsivePlus spaceBetweenMobile()       { plan.sm().add("flex", "justify-between", "items-center"); return this; }
    public ResponsivePlus spaceBetweenTablet()       { plan.md().add("flex", "justify-between", "items-center"); return this; }
    public ResponsivePlus spaceBetweenDesktop()      { plan.lg().add("flex", "justify-between", "items-center"); return this; }
    public ResponsivePlus spaceBetweenLargeDesktop() { plan.xl().add("flex", "justify-between", "items-center"); return this; }
    public ResponsivePlus spaceBetweenUltraWide()    { plan.x2l().add("flex", "justify-between", "items-center"); return this; }

    public ResponsivePlus spaceAroundDesktop()  { plan.lg().add("flex", "justify-around", "items-center"); return this; }
    public ResponsivePlus spaceEvenlyDesktop()  { plan.lg().add("flex", "justify-evenly", "items-center"); return this; }

    // ----- Wrap / No-wrap
    public ResponsivePlus wrapMobile()         { plan.sm().add("flex", "flex-wrap", "gap-s"); return this; }
    public ResponsivePlus wrapTablet()         { plan.md().add("flex", "flex-wrap", "gap-m"); return this; }
    public ResponsivePlus wrapDesktop()        { plan.lg().add("flex", "flex-wrap", "gap-m"); return this; }
    public ResponsivePlus wrapLargeDesktop()   { plan.xl().add("flex", "flex-wrap", "gap-l"); return this; }
    public ResponsivePlus wrapUltraWide()      { plan.x2l().add("flex", "flex-wrap", "gap-xl"); return this; }
    public ResponsivePlus noWrapDesktop()      { plan.lg().add("flex", "flex-nowrap", "gap-m"); return this; }

    // ----- Visibility
    public ResponsivePlus hideMobileShowDesktop() {
        plan.sm().add("hidden");
        plan.lg().add("flex");
        return this;
    }
    public ResponsivePlus showMobileOnly() {
        plan.sm().add("flex");
        plan.md().add("hidden");
        plan.lg().add("hidden");
        plan.xl().add("hidden");
        plan.x2l().add("hidden");
        return this;
    }
    public ResponsivePlus showTabletOnly() {
        plan.sm().add("hidden");
        plan.md().add("flex");
        plan.lg().add("hidden");
        plan.xl().add("hidden");
        plan.x2l().add("hidden");
        return this;
    }
    public ResponsivePlus showDesktopOnly() {
        plan.sm().add("hidden");
        plan.md().add("hidden");
        plan.lg().add("flex");
        plan.xl().add("hidden");
        plan.x2l().add("hidden");
        return this;
    }
    public ResponsivePlus showLargeDesktopOnly() {
        plan.sm().add("hidden");
        plan.md().add("hidden");
        plan.lg().add("hidden");
        plan.xl().add("flex");
        plan.x2l().add("hidden");
        return this;
    }
    public ResponsivePlus showUltraWideOnly() {
        plan.sm().add("hidden");
        plan.md().add("hidden");
        plan.lg().add("hidden");
        plan.xl().add("hidden");
        plan.x2l().add("flex");
        return this;
    }
    public ResponsivePlus showUpToTabletOnly() {
        plan.sm().add("flex");
        plan.md().add("flex");
        plan.lg().add("hidden");
        plan.xl().add("hidden");
        plan.x2l().add("hidden");
        return this;
    }
    public ResponsivePlus showFromDesktopUp() {
        plan.sm().add("hidden");
        plan.md().add("hidden");
        plan.lg().add("flex");
        plan.xl().add("flex");
        plan.x2l().add("flex");
        return this;
    }

    // ----- Width helpers
    public ResponsivePlus fullWidthMobile()        { plan.sm().add("w-full"); return this; }
    public ResponsivePlus fullWidthTablet()        { plan.md().add("w-full"); return this; }
    public ResponsivePlus fullWidthDesktop()       { plan.lg().add("w-full"); return this; }
    public ResponsivePlus fullWidthLargeDesktop()  { plan.xl().add("w-full"); return this; }
    public ResponsivePlus fullWidthUltraWide()     { plan.x2l().add("w-full"); return this; }

    // ----- Gap helpers
    public ResponsivePlus gapMobileS()         { plan.sm().add("gap-s"); return this; }
    public ResponsivePlus gapTabletM()         { plan.md().add("gap-m"); return this; }
    public ResponsivePlus gapDesktopM()        { plan.lg().add("gap-m"); return this; }
    public ResponsivePlus gapLargeDesktopL()   { plan.xl().add("gap-l"); return this; }
    public ResponsivePlus gapUltraWideXL()     { plan.x2l().add("gap-xl"); return this; }

    public ResponsivePlus gapXMobileS()        { plan.sm().add("gap-x-s"); return this; }
    public ResponsivePlus gapXTabletM()        { plan.md().add("gap-x-m"); return this; }
    public ResponsivePlus gapXDesktopM()       { plan.lg().add("gap-x-m"); return this; }
    public ResponsivePlus gapXLargeDesktopL()  { plan.xl().add("gap-x-l"); return this; }
    public ResponsivePlus gapXUltraWideXL()    { plan.x2l().add("gap-x-xl"); return this; }

    public ResponsivePlus gapYMobileS()        { plan.sm().add("gap-y-s"); return this; }
    public ResponsivePlus gapYTabletM()        { plan.md().add("gap-y-m"); return this; }
    public ResponsivePlus gapYDesktopL()       { plan.lg().add("gap-y-l"); return this; }
    public ResponsivePlus gapYLargeDesktopL()  { plan.xl().add("gap-y-l"); return this; }
    public ResponsivePlus gapYUltraWideXL()    { plan.x2l().add("gap-y-xl"); return this; }

    // ----- Grid helpers (container-level presets)
    public ResponsivePlus gridAutoScale() {
        plan.sm().add("grid", "grid-cols-1");
        plan.md().add("grid-cols-2");
        plan.lg().add("grid-cols-3");
        plan.xl().add("grid-cols-4");
        return this;
    }
    public ResponsivePlus gridMobile(int cols)       { return gridCols("sm", cols, true); }
    public ResponsivePlus gridTablet(int cols)       { return gridCols("md", cols, false); }
    public ResponsivePlus gridDesktop(int cols)      { return gridCols("lg", cols, false); }
    public ResponsivePlus gridLargeDesktop(int cols) { return gridCols("xl", cols, false); }
    public ResponsivePlus gridUltraWide(int cols)    { return gridCols("2xl", cols, false); }

    private ResponsivePlus gridCols(String prefix, int cols, boolean addGridToo) {
        int n = Math.max(1, Math.min(4, cols));
        BP bp = switch (prefix) {
            case "sm" -> BP.SM;
            case "md" -> BP.MD;
            case "lg" -> BP.LG;
            case "xl" -> BP.XL;
            case "2xl" -> BP.X2L;
            default -> throw new IllegalArgumentException("Unknown prefix: " + prefix);
        };
        if (addGridToo) plan.at(bp).add("grid");
        plan.at(bp).add("grid-cols-" + n);
        return this;
    }

    /* =====================================================================
     * Scope (fluent, scope-safe). Only per-breakpoint utilities live here.
     * ===================================================================== */
    public static final class Scope {
        private final ResponsiveDSL.Scope scope;
        private final ResponsivePlus chain;

        Scope(ResponsiveDSL.Scope scope, ResponsivePlus chain) {
            this.scope = scope;
            this.chain = chain;
        }

        /* ---- End scope: return to the main chain ---- */
        public ResponsivePlus end() { return chain; }

        /* ---- Navigate between scopes fluently ---- */
        public Scope sm()  { return new Scope(scope.sm(), chain); }
        public Scope md()  { return new Scope(scope.md(), chain); }
        public Scope lg()  { return new Scope(scope.lg(), chain); }
        public Scope xl()  { return new Scope(scope.xl(), chain); }
        public Scope x2l() { return new Scope(scope.x2l(), chain); }
        public Scope at(BP bp) { return new Scope(scope.at(bp), chain); }

        /* ---- Raw add (per-breakpoint) ---- */
        public Scope add(String... baseUtilities) { scope.add(baseUtilities); return this; }

        // === In ResponsiveChain.Scope ===
        public Scope growContent() { scope.growContent(); return this; }
        public Scope fixedItem()   { scope.fixedItem();   return this; }

        // Optional fine-grained
        public Scope growOnly()    { scope.growOnly();    return this; }
        public Scope allowShrink() { scope.allowShrink(); return this; }
        public Scope preventGrow() { scope.preventGrow(); return this; }
        /* ---- FLEX primitives ---- */
        public Scope flex()    { scope.flex(); return this; }
        public Scope row()     { scope.row(); return this; }
        public Scope col()     { scope.col(); return this; }
        public Scope wrap()    { scope.wrap(); return this; }
        public Scope noWrap()  { scope.noWrap(); return this; }

        /* ---- Spacing ---- */
        public Scope gap(Space s)  { scope.gap(s); return this; }
        public Scope gapX(Space s) { scope.gapX(s); return this; }
        public Scope gapY(Space s) { scope.gapY(s); return this; }

        /* ---- Align / justify ---- */
        public Scope items(Align a)         { scope.items(a); return this; }
        public Scope justify(Justify j)     { scope.justify(j); return this; }
        public Scope placeItems(Align a)    { scope.placeItems(a); return this; }
        public Scope placeContent(Justify j){ scope.placeContent(j); return this; }

        /* ---- Width & flex growth ---- */
        public Scope fullWidth()   { scope.fullWidth(); return this; }
        public Scope grow()        { scope.grow(); return this; }
        public Scope noGrow()      { scope.noGrow(); return this; }

        /* ---- Visibility ---- */
        public Scope hidden()      { scope.hidden(); return this; }

        /* ---- Padding ---- */
        public Scope p(Space s)  { scope.p(s); return this; }
        public Scope px(Space s) { scope.px(s); return this; }
        public Scope py(Space s) { scope.py(s); return this; }
        public Scope pt(Space s) { scope.pt(s); return this; }
        public Scope pr(Space s) { scope.pr(s); return this; }
        public Scope pb(Space s) { scope.pb(s); return this; }
        public Scope pl(Space s) { scope.pl(s); return this; }

        /* ---- Margin ---- */
        public Scope m(Space s)  { scope.m(s); return this; }
        public Scope mx(Space s) { scope.mx(s); return this; }
        public Scope my(Space s) { scope.my(s); return this; }
        public Scope mt(Space s) { scope.mt(s); return this; }
        public Scope mr(Space s) { scope.mr(s); return this; }
        public Scope mb(Space s) { scope.mb(s); return this; }
        public Scope ml(Space s) { scope.ml(s); return this; }
    }
}