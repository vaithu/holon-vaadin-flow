package com.iyensoft.vaadin.flow.utils.responsive;

import com.vaadin.flow.component.Component;

import java.util.Objects;

/**
 * New-order helpers for responsive utility classes defined in your responsive.css.
 *
 * Usage (new-order only):
 *   ResponsiveHelpers.on(toolbar)
 *     .sm().flexCol().gapYS()            // -> adds "sm:flex-col", "sm:gap-y-s"
 *     .desktop().flexRow().itemsCenter().gapXM() // -> adds "lg:flex-row", "lg:items-center", "lg:gap-x-m"
 *     .apply(); // (no-op, for symmetry)
 *
 * Prefix groups: sm (mobile), md (tablet), lg (desktop), xl (large desktop), 2xl (ultra wide).
 * Methods add classes immediately, returning the same prefix helper for chaining.
 * Call another prefix (e.g., .desktop()) to switch groups and keep chaining.
 *
 * NOTE: Ensure your CSS defines these classes exactly as used here, e.g.:
 *   .sm\:flex { display: flex; }  // colon escaped in CSS selector
 *   .lg\:gap-x-m { column-gap: var(--lumo-space-m); }
 */
public final class ResponsiveHelpers {

    private ResponsiveHelpers() {}

    /** Start a chain for the given component. */
    public static Ctx on(Component target) {
        return new Ctx(target);
    }

    /** Context for selecting breakpoints (prefix groups). */
    public static final class Ctx {
        private final Component target;
        private Ctx(Component target) {
            this.target = Objects.requireNonNull(target, "target");
        }

        /** Mobile (<= 767px). */
        public Sm sm() { return new Sm(target); }

        /** Tablet (768..1023px). */
        public Md md() { return new Md(target); }

        /** Desktop (1024..1279px). */
        public Lg lg() { return new Lg(target); }

        /** Desktop alias. */
        public Lg desktop() { return lg(); }

        /** Large desktop (1280..1535px). */
        public Xl xl() { return new Xl(target); }

        /** Ultra wide (>= 1536px). */
        public X2l x2l() { return new X2l(target); }

        /** Ultra wide alias. */
        public X2l ultraWide() { return x2l(); }
    }

    /* ──────────────────────────────────────────────────────────
     * Base class implementing all utility methods once.
     * Concrete classes set the prefix (sm, md, lg, xl, 2xl).
     * ────────────────────────────────────────────────────────── */
    private static abstract class Prefix<T extends Prefix<T>> {
        protected final Component target;
        protected final String prefix;
        protected Prefix(Component target, String prefix) {
            this.target = target;
            this.prefix = prefix;
        }

        /** For chaining in Java with the correct type. */
        protected abstract T self();

        /** Adds a single class (prefix:utility) to the component. */
        protected void add(String utility) {
            target.getElement().getClassList().add(prefix + ":" + utility);
        }

        /** Return to the context to pick another prefix, then continue chaining. */
        public Ctx ctx() { return new Ctx(target); }

        /** Optional: no-op to mirror DSLs that end with apply(). */
        public void apply() { /* no-op; classes already added */ }

        // ───────────────────────────────────────────────────────
        // Display
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:block".
         * Example:
         *   ResponsiveHelpers.on(c).sm().block();
         */
        public T block() { add("block"); return self(); }

        /**
         * Adds "<prefix>:inline".
         * Example:
         *   ResponsiveHelpers.on(c).md().inline();
         */
        public T inline() { add("inline"); return self(); }

        /**
         * Adds "<prefix>:inline-block".
         * Example:
         *   ResponsiveHelpers.on(c).lg().inlineBlock();
         */
        public T inlineBlock() { add("inline-block"); return self(); }

        /**
         * Adds "<prefix>:flex".
         * Example:
         *   ResponsiveHelpers.on(c).xl().flex();
         */
        public T flex() { add("flex"); return self(); }

        /**
         * Adds "<prefix>:grid".
         * Example:
         *   ResponsiveHelpers.on(c).x2l().grid();
         */
        public T grid() { add("grid"); return self(); }

        /**
         * Adds "<prefix>:hidden".
         * Example:
         *   ResponsiveHelpers.on(c).sm().hidden();
         */
        public T hidden() { add("hidden"); return self(); }

        // ───────────────────────────────────────────────────────
        // Flex Direction
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:flex-row".
         * Example:
         *   ResponsiveHelpers.on(row).desktop().flexRow();
         */
        public T flexRow() { add("flex-row"); return self(); }

        /**
         * Adds "<prefix>:flex-col".
         * Example:
         *   ResponsiveHelpers.on(col).sm().flexCol();
         */
        public T flexCol() { add("flex-col"); return self(); }

        /**
         * Adds "<prefix>:flex-row-reverse".
         * Example:
         *   ResponsiveHelpers.on(row).md().flexRowReverse();
         */
        public T flexRowReverse() { add("flex-row-reverse"); return self(); }

        /**
         * Adds "<prefix>:flex-col-reverse".
         * Example:
         *   ResponsiveHelpers.on(col).lg().flexColReverse();
         */
        public T flexColReverse() { add("flex-col-reverse"); return self(); }

        // ───────────────────────────────────────────────────────
        // Flex Wrap
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:flex-wrap".
         * Example:
         *   ResponsiveHelpers.on(row).xl().flexWrap();
         */
        public T flexWrap() { add("flex-wrap"); return self(); }

        /**
         * Adds "<prefix>:flex-nowrap".
         * Example:
         *   ResponsiveHelpers.on(row).x2l().flexNowrap();
         */
        public T flexNowrap() { add("flex-nowrap"); return self(); }

        // ───────────────────────────────────────────────────────
        // Align Items
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:items-start".
         * Example:
         *   ResponsiveHelpers.on(row).desktop().itemsStart();
         */
        public T itemsStart() { add("items-start"); return self(); }

        /**
         * Adds "<prefix>:items-center".
         * Example:
         *   ResponsiveHelpers.on(row).lg().itemsCenter();
         */
        public T itemsCenter() { add("items-center"); return self(); }

        /**
         * Adds "<prefix>:items-end".
         * Example:
         *   ResponsiveHelpers.on(row).md().itemsEnd();
         */
        public T itemsEnd() { add("items-end"); return self(); }

        /**
         * Adds "<prefix>:items-stretch".
         * Example:
         *   ResponsiveHelpers.on(row).xl().itemsStretch();
         */
        public T itemsStretch() { add("items-stretch"); return self(); }

        /**
         * Adds "<prefix>:items-baseline".
         * Example:
         *   ResponsiveHelpers.on(row).sm().itemsBaseline();
         */
        public T itemsBaseline() { add("items-baseline"); return self(); }

        // ───────────────────────────────────────────────────────
        // Justify Content
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:justify-start".
         * Example:
         *   ResponsiveHelpers.on(row).desktop().justifyStart();
         */
        public T justifyStart() { add("justify-start"); return self(); }

        /**
         * Adds "<prefix>:justify-center".
         * Example:
         *   ResponsiveHelpers.on(row).lg().justifyCenter();
         */
        public T justifyCenter() { add("justify-center"); return self(); }

        /**
         * Adds "<prefix>:justify-end".
         * Example:
         *   ResponsiveHelpers.on(row).md().justifyEnd();
         */
        public T justifyEnd() { add("justify-end"); return self(); }

        /**
         * Adds "<prefix>:justify-between".
         * Example:
         *   ResponsiveHelpers.on(row).xl().justifyBetween();
         */
        public T justifyBetween() { add("justify-between"); return self(); }

        /**
         * Adds "<prefix>:justify-around".
         * Example:
         *   ResponsiveHelpers.on(row).sm().justifyAround();
         */
        public T justifyAround() { add("justify-around"); return self(); }

        /**
         * Adds "<prefix>:justify-evenly".
         * Example:
         *   ResponsiveHelpers.on(row).x2l().justifyEvenly();
         */
        public T justifyEvenly() { add("justify-evenly"); return self(); }

        // ───────────────────────────────────────────────────────
        // Gap (shorthand), Gap X, Gap Y
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:gap-s".
         * Example:
         *   ResponsiveHelpers.on(g).sm().gapS();
         */
        public T gapS() { add("gap-s"); return self(); }

        /**
         * Adds "<prefix>:gap-m".
         * Example:
         *   ResponsiveHelpers.on(g).md().gapM();
         */
        public T gapM() { add("gap-m"); return self(); }

        /**
         * Adds "<prefix>:gap-l".
         * Example:
         *   ResponsiveHelpers.on(g).lg().gapL();
         */
        public T gapL() { add("gap-l"); return self(); }

        /**
         * Adds "<prefix>:gap-xl".
         * Example:
         *   ResponsiveHelpers.on(g).xl().gapXL();
         */
        public T gapXL() { add("gap-xl"); return self(); }

        /**
         * Adds "<prefix>:gap-x-s".
         * Example:
         *   ResponsiveHelpers.on(g).sm().gapXS();
         */
        public T gapXS() { add("gap-x-s"); return self(); }

        /**
         * Adds "<prefix>:gap-x-m".
         * Example:
         *   ResponsiveHelpers.on(g).desktop().gapXM();
         */
        public T gapXM() { add("gap-x-m"); return self(); }

        /**
         * Adds "<prefix>:gap-x-l".
         * Example:
         *   ResponsiveHelpers.on(g).xl().gapXLh(); // h=horizontal
         */
        public T gapXLh() { add("gap-x-l"); return self(); }

        /**
         * Adds "<prefix>:gap-x-xl".
         * Example:
         *   ResponsiveHelpers.on(g).x2l().gapXXLh();
         */
        public T gapXXLh() { add("gap-x-xl"); return self(); }

        /**
         * Adds "<prefix>:gap-y-s".
         * Example:
         *   ResponsiveHelpers.on(g).sm().gapYS();
         */
        public T gapYS() { add("gap-y-s"); return self(); }

        /**
         * Adds "<prefix>:gap-y-m".
         * Example:
         *   ResponsiveHelpers.on(g).md().gapYM();
         */
        public T gapYM() { add("gap-y-m"); return self(); }

        /**
         * Adds "<prefix>:gap-y-l".
         * Example:
         *   ResponsiveHelpers.on(g).lg().gapYL();
         */
        public T gapYL() { add("gap-y-l"); return self(); }

        /**
         * Adds "<prefix>:gap-y-xl".
         * Example:
         *   ResponsiveHelpers.on(g).xl().gapYXL();
         */
        public T gapYXL() { add("gap-y-xl"); return self(); }

        // ───────────────────────────────────────────────────────
        // Grid Columns (1..4)
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:grid-cols-1".
         * Example:
         *   ResponsiveHelpers.on(grid).md().gridCols1();
         */
        public T gridCols1() { add("grid-cols-1"); return self(); }

        /**
         * Adds "<prefix>:grid-cols-2".
         * Example:
         *   ResponsiveHelpers.on(grid).lg().gridCols2();
         */
        public T gridCols2() { add("grid-cols-2"); return self(); }

        /**
         * Adds "<prefix>:grid-cols-3".
         * Example:
         *   ResponsiveHelpers.on(grid).xl().gridCols3();
         */
        public T gridCols3() { add("grid-cols-3"); return self(); }

        /**
         * Adds "<prefix>:grid-cols-4".
         * Example:
         *   ResponsiveHelpers.on(grid).x2l().gridCols4();
         */
        public T gridCols4() { add("grid-cols-4"); return self(); }

        // ───────────────────────────────────────────────────────
        // Sizing
        // ───────────────────────────────────────────────────────

        /**
         * Adds "<prefix>:w-full".
         * Example:
         *   ResponsiveHelpers.on(input).sm().wFull();
         */
        public T wFull() { add("w-full"); return self(); }
    }

    /* ──────────────────────────────────────────────────────────
     * Concrete prefix helpers
     * ────────────────────────────────────────────────────────── */

    /** MOBILE (<= 767px). */
    public static final class Sm extends Prefix<Sm> {
        public Sm(Component c) { super(c, "sm"); }
        @Override protected Sm self() { return this; }
    }

    /** TABLET (768..1023px). */
    public static final class Md extends Prefix<Md> {
        public Md(Component c) { super(c, "md"); }
        @Override protected Md self() { return this; }
    }

    /** DESKTOP (1024..1279px). */
    public static final class Lg extends Prefix<Lg> {
        public Lg(Component c) { super(c, "lg"); }
        @Override protected Lg self() { return this; }
    }

    /** LARGE DESKTOP (1280..1535px). */
    public static final class Xl extends Prefix<Xl> {
        public Xl(Component c) { super(c, "xl"); }
        @Override protected Xl self() { return this; }
    }

    /** ULTRA WIDE (>= 1536px). */
    public static final class X2l extends Prefix<X2l> {
        public X2l(Component c) { super(c, "2xl"); }
        @Override protected X2l self() { return this; }
    }
}