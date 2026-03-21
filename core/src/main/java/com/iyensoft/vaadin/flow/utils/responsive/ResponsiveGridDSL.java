package com.iyensoft.vaadin.flow.utils.responsive;

import com.vaadin.flow.component.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * ResponsiveGridDSL – Fluent, prefix-first builder for CSS Grid utilities.
 *
 * Philosophy:
 *  - Select breakpoint(s) first: sm(), md(), desktop()/lg(), xl(), ultraWide()/x2l().
 *  - Add grid utilities via sugar (grid(), gridCols(n), gap(...), colSpan(n), ...), or raw add("grid-cols-3").
 *  - Call apply() to mutate the component classList.
 *  - Lifecycle helpers: applyReplace(), remove(), reset(); Custom bundles via bundle()/use().
 *
 * Assumptions:
 *  - Your CSS defines selectors with escaped colons, e.g., .sm\:grid, .lg\:grid-cols-3, .md\:col-span-2, etc.
 *  - Breakpoint semantics align with your media queries:
 *      sm (<= 767), md (768..1023), lg (1024..1279), xl (1280..1535), 2xl (>= 1536)
 */
public final class ResponsiveGridDSL {

    private ResponsiveGridDSL() {}

    /* ---------------- Custom bundle registry (same pattern as your ResponsiveDSL) ---------------- */
    private static final Map<String, Consumer<Builder>> BUNDLES = new ConcurrentHashMap<>();

    /** Register or replace a named bundle (thread-safe). */
    public static void bundle(String name, Consumer<Builder> bundleFn) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(bundleFn, "bundleFn");
        BUNDLES.put(name, bundleFn);
    }

    /** Start a chain for the given component. */
    public static Builder on(Component target) {
        return new Builder(target);
    }

    /* ======================================================================
     * Builder
     * ====================================================================== */
    public static final class Builder {
        private static final int MAX_TRACKS = 24; // clamp for cols/rows/start/span

        private final Component target;

        /**
         * Active prefixes chosen by sm()/md()/desktop()/xl()/ultraWide().
         * E.g., ["sm","lg"] means subsequent sugar methods add both sm:... and lg:...
         */
        private final LinkedHashSet<String> activePrefixes = new LinkedHashSet<>();

        /**
         * Accumulate base utilities per prefix:
         *   "sm" -> {"grid","grid-cols-1"}
         *   "lg" -> {"grid-cols-4","gap-m"}
         */
        private final Map<String, LinkedHashSet<String>> basesByPrefix = new LinkedHashMap<>();

        /** Track last applied for applyReplace/remove */
        private final LinkedHashSet<String> lastApplied = new LinkedHashSet<>();

        private Builder(Component target) {
            this.target = Objects.requireNonNull(target, "target");
        }

        /* ------------ Core API ------------ */

        /**
         * Select a raw prefix (e.g., "sm", "md", "lg", "xl", "2xl").
         * After selecting, sugar methods add classes for all selected prefixes.
         */
        public Builder prefix(String prefix) {
            if (prefix != null && !prefix.isBlank()) {
                activePrefixes.add(prefix.trim());
            }
            return this;
        }

        /**
         * Add raw grid utility class names to currently active prefixes.
         * Example:
         *   .sm().add("grid", "grid-cols-2") // sm:grid sm:grid-cols-2
         */
        public Builder add(String... baseClasses) {
            ensurePrefixes();
            if (baseClasses == null || baseClasses.length == 0) return this;

            List<String> cleaned = new ArrayList<>();
            for (String b : baseClasses) {
                if (b == null) continue;
                String s = b.trim();
                if (!s.isEmpty()) cleaned.add(s);
            }
            if (cleaned.isEmpty()) return this;

            for (String p : activePrefixes) {
                basesByPrefix.computeIfAbsent(p, k -> new LinkedHashSet<>())
                        .addAll(cleaned);
            }
            return this;
        }

        /** Apply a named custom bundle registered via ResponsiveGridDSL.bundle(...). */
        public Builder use(String bundleName) {
            Consumer<Builder> fn = BUNDLES.get(bundleName);
            if (fn == null) {
                throw new IllegalArgumentException("Bundle not found: " + bundleName);
            }
            fn.accept(this);
            return this;
        }

        /** Apply the staged classes to the component. */
        public void apply() {
            if (basesByPrefix.isEmpty()) return;
            LinkedHashSet<String> toAdd = computePrefixedClasses();
            for (String cls : toAdd) {
                target.getElement().getClassList().add(cls);
            }
            lastApplied.clear();
            lastApplied.addAll(toAdd);
        }

        /** Remove the last set of classes applied by this builder (if any). */
        public void remove() {
            if (lastApplied.isEmpty()) return;
            for (String cls : lastApplied) {
                target.getElement().getClassList().remove(cls);
            }
            lastApplied.clear();
        }

        /** Remove previous classes (if any), then apply current plan. */
        public void applyReplace() {
            remove();
            apply();
        }

        /** Clear the staged plan (does not modify the component). */
        public Builder reset() {
            basesByPrefix.clear();
            activePrefixes.clear();
            return this;
        }

        /** Add literal class names (already prefixed like "md:grid-cols-4", no processing). */
        public Builder addRaw(String... classNames) {
            if (classNames != null) {
                for (String cls : classNames) {
                    if (cls != null && !cls.isBlank()) {
                        target.getElement().getClassList().add(cls.trim());
                    }
                }
            }
            return this;
        }

        private void ensurePrefixes() {
            if (activePrefixes.isEmpty()) {
                throw new IllegalStateException(
                    "No prefixes selected. Call sm()/md()/desktop()/xl()/ultraWide() before adding utilities.");
            }
        }

        private LinkedHashSet<String> computePrefixedClasses() {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            basesByPrefix.forEach((prefix, bases) -> {
                for (String base : bases) {
                    out.add(prefix + ":" + base);
                }
            });
            return out;
        }

        /* ------------ Convenience breakpoint selectors ------------ */

        public Builder sm() { return prefix("sm"); }
        public Builder md() { return prefix("md"); }
        public Builder lg() { return prefix("lg"); }
        public Builder desktop() { return lg(); }
        public Builder xl() { return prefix("xl"); }
        public Builder ultraWide() { return prefix("2xl"); }
        public Builder x2l() { return ultraWide(); }

        /** Select multiple modes at once (keys are the raw prefixes). */
        public Builder modes(String... prefixes) {
            if (prefixes != null) {
                for (String p : prefixes) {
                    if (p != null && !p.isBlank()) activePrefixes.add(p.trim());
                }
            }
            return this;
        }

        /* ==================================================================
         * SUGAR: Container grid utilities
         * ================================================================== */

        /** Adds: <bp>:grid */
        public Builder grid() { return this.add("grid"); }

        /** Adds: <bp>:grid-cols-{n} (1..24) */
        public Builder gridCols(int n) { return this.add("grid-cols-" + clamp(n)); }

        /** Adds: <bp>:grid-rows-{n} (1..24) */
        public Builder gridRows(int n) { return this.add("grid-rows-" + clamp(n)); }

        /** Adds: <bp>:gap-{token} (tokens mapped in theme) */
        public Builder gap(Space s) { return this.add("gap-" + s.key()); }
        /** Adds: <bp>:gap-x-{token} */
        public Builder gapX(Space s) { return this.add("gap-x-" + s.key()); }
        /** Adds: <bp>:gap-y-{token} */
        public Builder gapY(Space s) { return this.add("gap-y-" + s.key()); }

        /** Adds: <bp>:place-items-{start|center|end|stretch} */
        public Builder placeItems(Align a) { return this.add("place-items-" + a.key()); }
        /** Adds: <bp>:align-items-{...} */
        public Builder alignItems(Align a) { return this.add("items-" + a.key()); }
        /** Adds: <bp>:justify-items-{...} */
        public Builder justifyItems(Align a) { return this.add("justify-items-" + a.key()); }

        /** Adds: <bp>:place-content-{start|center|end|between|around|evenly|stretch} */
        public Builder placeContent(Content c) { return this.add("place-content-" + c.key()); }
        /** Adds: <bp>:align-content-{...} */
        public Builder alignContent(Content c) { return this.add("content-" + c.key()); }
        /** Adds: <bp>:justify-content-{...} */
        public Builder justifyContent(Content c) { return this.add("justify-" + c.key()); }

        /** Adds: <bp>:auto-rows-min / <bp>:auto-rows-fr / etc. */
        public Builder autoRows(AutoTrack t) { return this.add("auto-rows-" + t.key()); }
        /** Adds: <bp>:auto-cols-min / <bp>:auto-cols-fr / etc. */
        public Builder autoCols(AutoTrack t) { return this.add("auto-cols-" + t.key()); }

        /** Adds common scaling pattern: sm:1 md:2 lg:3 xl:4 cols (also ensures grid on sm). */
        public Builder gridAutoScale() {
            this.sm().add("grid", "grid-cols-1");
            this.md().add("grid-cols-2");
            this.desktop().add("grid-cols-3");
            this.xl().add("grid-cols-4");
            return this;
        }

        /**
         * Adds Auto-fit/Auto-fill presets, assuming theme defines:
         *  .<bp>\:grid-cols-autofit-<px> { grid-template-columns: repeat(auto-fit, minmax(<px>px, 1fr)); }
         *  .<bp>\:grid-cols-autofill-<px> { repeat(auto-fill, ...) }
         */
        public Builder gridColsAutoFitPx(int minPx) {
            return this.add("grid-cols-autofit-" + Math.max(1, minPx));
        }
        public Builder gridColsAutoFillPx(int minPx) {
            return this.add("grid-cols-autofill-" + Math.max(1, minPx));
        }

        /* ==================================================================
         * SUGAR: Item placement utilities (children inside grid)
         * ================================================================== */

        /** Adds: <bp>:col-span-{n} (1..24) */
        public Builder colSpan(int n) { return this.add("col-span-" + clamp(n)); }
        /** Adds: <bp>:row-span-{n} (1..24) */
        public Builder rowSpan(int n) { return this.add("row-span-" + clamp(n)); }
        /** Adds: <bp>:col-start-{n} (1..24) */
        public Builder colStart(int n) { return this.add("col-start-" + clamp(n)); }
        /** Adds: <bp>:row-start-{n} (1..24) */
        public Builder rowStart(int n) { return this.add("row-start-" + clamp(n)); }

        /** Adds: <bp>:place-self-{start|center|end|stretch} */
        public Builder placeSelf(Align a) { return this.add("place-self-" + a.key()); }
        /** Adds: <bp>:self-{start|center|end|stretch} */
        public Builder alignSelf(Align a) { return this.add("self-" + a.key()); }
        /** Adds: <bp>:justify-self-{start|center|end|stretch} */
        public Builder justifySelf(Align a) { return this.add("justify-self-" + a.key()); }

        /* ==================================================================
         * Generic helpers
         * ================================================================== */

        /** Generic: grid with N columns at an arbitrary prefix. */
        public Builder gridCols(String prefix, int cols, boolean ensureGrid) {
            this.prefix(prefix);
            if (ensureGrid) this.add("grid");
            return this.add("grid-cols-" + clamp(cols));
        }

        /** Generic: add any utilities under one-off prefix and return this. */
        public Builder at(String prefix, String... baseUtilities) {
            this.prefix(prefix);
            return this.add(baseUtilities);
        }

        // ====== Tokens ======
        public enum Space {
            ZERO("0"), XS("xs"), S("s"), M("m"), L("l"), XL("xl");
            private final String key;
            Space(String key) { this.key = key; }
            public String key() { return key; }
        }

        public enum Align {
            START("start"), CENTER("center"), END("end"), STRETCH("stretch");
            private final String key;
            Align(String key) { this.key = key; }
            public String key() { return key; }
        }

        public enum Content {
            START("start"), CENTER("center"), END("end"), STRETCH("stretch"),
            BETWEEN("between"), AROUND("around"), EVENLY("evenly");
            private final String key;
            Content(String key) { this.key = key; }
            public String key() { return key; }
        }

        /** Tailwind-like auto track keywords (define these in your CSS if not present). */
        public enum AutoTrack {
            MIN("min"), MAX("max"), FR("fr"), MIN_CONTENT("min-content"),
            MAX_CONTENT("max-content"), AUTO("auto");
            private final String key;
            AutoTrack(String key) { this.key = key; }
            public String key() { return key; }
        }

        // inside ResponsiveGridDSL.Builder

        /** Curated preset min-widths for auto-fit/auto-fill */
        public enum AutoPreset {
            PX120(120), PX160(160), PX180(180), PX200(200),
            PX220(220), PX240(240), PX280(280), PX320(320);

            private final int px;
            AutoPreset(int px) { this.px = px; }
            public int px() { return px; }
        }

        /**
         * AUTO-FIT helpers (max=1fr).
         * Adds: <bp>:grid-cols-autofit-<minPx>
         * Example: md:grid-cols-autofit-180
         */
        public Builder gridColsAutoFit(AutoPreset p) {
            return gridColsAutoFitPx(p.px());
        }


        /**
         * AUTO-FILL helpers (max=1fr).
         * Adds: <bp>:grid-cols-autofill-<minPx>
         * Example: lg:grid-cols-autofill-220
         */
        public Builder gridColsAutoFill(AutoPreset p) {
            return gridColsAutoFillPx(p.px());
        }


        /**
         * “Tight” variants (max = max-content) for denser packing.
         * Adds: <bp>:grid-cols-autofit-tight-<minPx> or <bp>:grid-cols-autofill-tight-<minPx>
         */
        public Builder gridColsAutoFitTight(AutoPreset p) {
            return gridColsAutoFitTightPx(p.px());
        }
        public Builder gridColsAutoFitTightPx(int minPx) {
            return this.add("grid-cols-autofit-tight-" + Math.max(1, minPx));
        }
        public Builder gridColsAutoFillTight(AutoPreset p) {
            return gridColsAutoFillTightPx(p.px());
        }
        public Builder gridColsAutoFillTightPx(int minPx) {
            return this.add("grid-cols-autofill-tight-" + Math.max(1, minPx));
        }

        /* ===== internals ===== */
        private int clamp(int n) {
            return Math.max(1, Math.min(MAX_TRACKS, n));
        }
    }
}