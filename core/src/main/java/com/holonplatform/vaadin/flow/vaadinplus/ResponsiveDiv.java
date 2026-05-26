package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.enums.ColSpan;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.signals.Signal;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A responsive container {@link Div} with a fluent builder API covering the five
 * universal responsive layout patterns:
 *
 * <ol>
 *   <li><b>Stack → Inline</b> — flex-col on mobile-first, flex-row on desktop</li>
 *   <li><b>1-col → N-col Grid</b> — column count adapts per breakpoint</li>
 *   <li><b>Asymmetric Split</b> — explicit {@link ColSpan} per child</li>
 *   <li><b>Responsive Spacing</b> — gap scales per viewport</li>
 *   <li><b>Show / Hide</b> — visibility toggled at breakpoints</li>
 * </ol>
 *
 * <h3>Default gap</h3>
 * <p>Both {@link #flex()} and {@link #grid()} containers default to {@code gap-m} (1 rem)
 * so content is never accidentally squished. Override with any {@code .gapXS/S/L/XL()} call,
 * or remove entirely with {@code .noGap()}:</p>
 * <pre>{@code
 * ResponsiveDiv.grid().mobile(3).gapXS().build()   // override → gap-xs
 * ResponsiveDiv.flex().column().noGap().build()     // remove  → no gap class
 * }</pre>
 *
 * <h3>Mobile-first convention</h3>
 * <p>Base methods (without a scope) apply to <em>all</em> viewport sizes.
 * Scoped methods ({@code .desktop().row().end()}) apply only at that breakpoint and above,
 * overriding the base. This mirrors the standard mobile-first CSS approach.</p>
 *
 * <h3>Entry points</h3>
 * <ul>
 *   <li>{@link #flex()} — Flexbox container builder</li>
 *   <li>{@link #grid()} — CSS Grid container builder</li>
 * </ul>
 *
 * <h3>Examples</h3>
 * <pre>{@code
 * // Pattern 1 — hero: stacked on mobile, side-by-side on desktop
 * ResponsiveDiv hero = ResponsiveDiv.flex()
 *     .column().gapS()
 *     .desktop().row().gapL().alignCenter().end()
 *     .add(textBlock, imageBlock)
 *     .build();
 *
 * // Pattern 2 — card grid: 1 → 2 → 3 columns
 * ResponsiveDiv cards = ResponsiveDiv.grid()
 *     .mobile(1).tablet(2).desktop(3).gapM()
 *     .add(card1, card2, card3)
 *     .build();
 *
 * // Pattern 3 — asymmetric: 8-col main + 4-col sidebar
 * ResponsiveDiv layout = ResponsiveDiv.grid()
 *     .mobile(1).desktop(12)
 *     .add(
 *         GridEntry.of(mainContent).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
 *         GridEntry.of(sidebar).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
 *     )
 *     .build();
 * }</pre>
 *
 * @since 10.0.0
 */
@StyleSheet("context://layout.css")
public class ResponsiveDiv extends Div {

    protected ResponsiveDiv() {
        // constructed via builders or subclass
    }

    // ── Entry points ──────────────────────────────────────────────────────────

    /**
     * Starts a Flexbox-based responsive container ({@code class="flex"}).
     *
     * @return a new {@link FlexBuilder}
     */
    public static FlexBuilder flex() {
        return new FlexBuilder();
    }

    /**
     * Starts a CSS Grid-based responsive container ({@code class="grid"}).
     *
     * @return a new {@link GridBuilder}
     */
    public static GridBuilder grid() {
        return new GridBuilder();
    }

    /**
     * Configures an <em>existing</em> {@link ResponsiveDiv} instance using the full
     * fluent builder API. All existing CSS class names are preserved — no new layout
     * class ({@code flex} / {@code grid}) is added.
     *
     * <pre>{@code
     * // Inject additional responsive behaviour into a pre-built div:
     * ResponsiveDiv.configure(existingDiv)
     *     .fullWidth()
     *     .padM()
     *     .desktop().gapL().end()
     *     .build();          // returns the same existingDiv instance
     * }</pre>
     *
     * @param div the existing {@link ResponsiveDiv} to configure (not null)
     * @return a {@link DivConfigurator} wrapping the given instance
     */
    public static DivConfigurator configure(Div div) {
        return new DivConfigurator(div);
    }

    /**
     * Non-building configurator for an existing {@link Div} (or any subclass).
     *
     * <p>Provides the full {@link BaseBuilder} API (sizing, padding, margin, gap,
     * overflow, visibility, etc.) without adding an initial layout class.
     * Obtain via {@link ResponsiveDiv#configure(Div)}.
     */
    public static final class DivConfigurator extends BaseBuilder<DivConfigurator, Div> {

        private DivConfigurator(Div existing) {
            super(existing);
        }
    }

    // ── Base Builder ──────────────────────────────────────────────────────────

    /**
     * Shared builder state and operations for both {@link FlexBuilder} and {@link GridBuilder}.
     *
     * @param <B> concrete builder subtype for fluent method chaining
     */
    public abstract static class BaseBuilder<B extends BaseBuilder<B, D>, D extends Div> {

        protected final D div;

        /** Tracks the currently active base gap class so it can be swapped, not stacked. */
        private String currentGap = null;

        @SuppressWarnings("unchecked")
        protected BaseBuilder() {
            this.div = (D) new ResponsiveDiv();
        }

        /**
         * Constructor for the {@link DivConfigurator} — wraps an existing instance.
         * No new layout class or default gap is applied.
         *
         * @param existing pre-built {@link Div} to wrap (not null)
         */
        protected BaseBuilder(D existing) {
            this.div = existing;
        }

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }

        /**
         * Replaces the current base gap class with {@code gapClass}.
         * Always swaps rather than stacking to avoid unpredictable cascade.
         */
        protected final void applyGap(String gapClass) {
            if (currentGap != null) {
                div.removeClassName(currentGap);
            }
            div.addClassName(gapClass);
            currentGap = gapClass;
        }

        /**
         * Removes the default (or any previously set) base gap class entirely.
         *
         * @return this builder
         */
        public B noGap() {
            if (currentGap != null) {
                div.removeClassName(currentGap);
                currentGap = null;
            }
            return self();
        }

        /**
         * Sets the HTML {@code id} attribute on the div.
         *
         * @return this builder
         */
        public B id(String id) {
            div.setId(id);
            return self();
        }

        /**
         * Adds one or more CSS class names directly to the div.
         *
         * @return this builder
         */
        public B styleName(String... names) {
            div.addClassNames(names);
            return self();
        }

        /**
         * Appends child components to this container.
         *
         * @return this builder
         */
        public B add(Component... components) {
            div.add(components);
            return self();
        }

        /**
         * Registers a <em>lazy</em> component supplier for a specific {@link ViewMode}.
         *
         * <p>The supplier is called <strong>once</strong> on first attach, using the actual
         * browser window size to select the matching slot. Only one component (the one that
         * matches the current viewport) is ever built and added to the DOM — all others stay
         * as cheap {@link Supplier} references on the heap and are discarded after attach.
         *
         * <p><strong>Fallback chain</strong> (when no exact slot matches):
         * <ul>
         *   <li>{@code MOBILE_PORTRAIT} / {@code MOBILE_LANDSCAPE} → {@code MOBILE} → {@code TABLET} → {@code DESKTOP}</li>
         *   <li>{@code TABLET} → {@code DESKTOP} → {@code MOBILE}</li>
         *   <li>{@code LARGE_DESKTOP} / {@code ULTRA_WIDE} → {@code DESKTOP}</li>
         * </ul>
         *
         * <h3>Usage</h3>
         * <pre>{@code
         * // Only the matching variant is added to the DOM — the other supplier is never called.
         * ResponsiveDiv container = ResponsiveDiv.flex().column()
         *     .slotOnce(ViewMode.MOBILE,   () -> new MobileCardList(items))
         *     .slotOnce(ViewMode.DESKTOP,  () -> new DesktopGrid(items))
         *     .build();
         * }</pre>
         *
         * @param mode     the viewport mode this slot should render for
         * @param supplier called at most once when the viewport matches {@code mode}
         * @return this builder
         */
        public B slotOnce(ViewMode mode, Supplier<Component> supplier) {
            if (slots == null) slots = new EnumMap<>(ViewMode.class);
            slots.put(mode, supplier);
            return self();
        }

        /**
         * Builds and returns the configured div.
         *
         * @return the built component
         */
        public D build() {
            if (slots != null && !slots.isEmpty()) {
                // Snapshot: builder may be GC'd after build(); the lambda must not hold a ref to it.
                final Map<ViewMode, Supplier<Component>> capturedSlots = Map.copyOf(slots);
                final boolean[] resolved = {false};
                div.addAttachListener(event -> {
                    if (resolved[0]) return;      // guard: survive re-attach without rebuilding
                    resolved[0] = true;
                    WindowSize size = Signal.untracked(
                            () -> event.getUI().getPage().windowSizeSignal().get());
                    if (size != null) {
                        ViewMode mode = UIUtils.getViewMode(size.width(), size.height());
                        Supplier<Component> s = resolveSlot(capturedSlots, mode);
                        if (s != null) div.add(s.get());
                    }
                });
            }
            return div;
        }

        // ── slot resolution ───────────────────────────────────────────────────

        /** Lazy slot registry — null until first {@link #slotOnce} call. */
        private Map<ViewMode, Supplier<Component>> slots = null;

        /**
         * Resolves the best-matching supplier for the given mode using a priority fallback chain.
         * Exact match is always preferred; falls back by semantic proximity.
         */
        private static Supplier<Component> resolveSlot(Map<ViewMode, Supplier<Component>> slots, ViewMode mode) {
            Supplier<Component> exact = slots.get(mode);
            if (exact != null) return exact;
            return switch (mode) {
                case MOBILE_PORTRAIT, MOBILE_LANDSCAPE -> {
                    Supplier<Component> s = slots.get(ViewMode.MOBILE);
                    if (s != null) yield s;
                    s = slots.get(ViewMode.TABLET);
                    yield s != null ? s : slots.get(ViewMode.DESKTOP);
                }
                case TABLET -> {
                    Supplier<Component> s = slots.get(ViewMode.DESKTOP);
                    yield s != null ? s : slots.get(ViewMode.MOBILE);
                }
                case LARGE_DESKTOP, ULTRA_WIDE -> slots.get(ViewMode.DESKTOP);
                default -> null;
            };
        }

        /**
         * Hides this div at the given breakpoint and above → {@code {prefix}:hidden}.
         *
         * @return this builder
         */
        public B hide(ViewMode mode) {
            div.addClassName(mode.toCssClass("hidden"));
            return self();
        }

        /**
         * Makes this div visible ({@code display:block}) at the given breakpoint and above.
         *
         * @return this builder
         */
        public B show(ViewMode mode) {
            div.addClassName(mode.toCssClass("block"));
            return self();
        }

        /**
         * Hides this div at all sizes → {@code .hidden}.
         * Combine with {@link #show(ViewMode)} to create show-at-breakpoint patterns.
         *
         * @return this builder
         */
        public B hidden() {
            div.addClassName("hidden");
            return self();
        }

        /**
         * Applies a card surface to this div → {@code .rdiv-card}.
         *
         * <p>Adds background, padding, border-radius and a subtle border.
         * Use this whenever the container should appear as a distinct content card —
         * KPI tiles, chart panels, form sections, list widgets.</p>
         *
         * @return this builder
         * @see #elevated()
         */
        public B card() {
            div.addClassName("rdiv-card");
            return self();
        }

        /**
         * Applies an elevated card surface to this div → {@code .rdiv-card--elevated}.
         *
         * <p>Same as {@link #card()} but uses a drop shadow instead of a border,
         * creating a stronger visual lift. Prefer for hero cards, featured content,
         * or cards that need to stand out from a tinted page background.</p>
         *
         * @return this builder
         */
        public B elevated() {
            div.addClassName("rdiv-card--elevated");
            return self();
        }

        // --- Sizing ---

        /** Sets {@code width: 100%} → {@code .w-full}. */
        public B fullWidth()  { div.addClassName("w-full");     return self(); }

        /** Sets {@code height: 100%} → {@code .h-full}. */
        public B fullHeight() { div.addClassName("h-full");     return self(); }

        /** Sets {@code min-height: 100%} → {@code .min-h-full}. */
        public B minHFull()   { div.addClassName("min-h-full"); return self(); }

        // --- Position shortcuts ---

        /** {@code position: relative} → {@code .relative}. */
        public B relative() { div.addClassName("relative"); return self(); }

        /** {@code position: absolute} → {@code .absolute}. */
        public B absolute() { div.addClassName("absolute"); return self(); }

        /** {@code position: sticky} → {@code .sticky}. */
        public B sticky()   { div.addClassName("sticky");   return self(); }

        // --- Overflow ---

        /**
         * Controls how overflowing content is handled → one of the {@code .overflow-*} classes.
         *
         * @param o overflow behaviour
         * @return this builder
         */
        public B overflow(Overflow o) { div.addClassName(o.className()); return self(); }

        // --- Align self (when this div is a flex/grid child) ---

        /** {@code align-self: center} → {@code .self-center}. */
        public B selfCenter()  { div.addClassName("self-center");  return self(); }

        /** {@code align-self: flex-start} → {@code .self-start}. */
        public B selfStart()   { div.addClassName("self-start");   return self(); }

        /** {@code align-self: flex-end} → {@code .self-end}. */
        public B selfEnd()     { div.addClassName("self-end");     return self(); }

        /** {@code align-self: stretch} → {@code .self-stretch}. */
        public B selfStretch() { div.addClassName("self-stretch"); return self(); }

        // --- Flex child: grow / shrink ---

        /**
         * Allows this div to grow and fill remaining space → {@code .flex-1}
         * ({@code flex: 1 1 0%}).
         */
        public B grow()     { div.addClassName("flex-1");   return self(); }

        /**
         * Prevents this div from shrinking below its natural size → {@code .shrink-0}
         * ({@code flex-shrink: 0}).
         */
        public B noShrink() { div.addClassName("shrink-0"); return self(); }

        // --- Text alignment ---

        /** {@code text-align: left} → {@code .text-left}. */
        public B textLeft()   { div.addClassName("text-left");   return self(); }

        /** {@code text-align: center} → {@code .text-center}. */
        public B textCenter() { div.addClassName("text-center"); return self(); }

        /** {@code text-align: right} → {@code .text-right}. */
        public B textRight()  { div.addClassName("text-right");  return self(); }

        // --- Truncate ---

        /**
         * Clips overflowing text with an ellipsis → {@code .truncate}
         * ({@code white-space: nowrap; overflow: hidden; text-overflow: ellipsis}).
         */
        public B truncate() { div.addClassName("truncate"); return self(); }

        // --- Padding ---

        /**
         * Sets uniform padding using the shared spacing scale.
         *
         * @param s padding size token
         * @return this builder
         */
        public B pad(PadSize s)  { div.addClassName("p-"  + s.token()); return self(); }

        /**
         * Sets inline (horizontal) padding using the shared spacing scale.
         *
         * @param s padding size token
         * @return this builder
         */
        public B padX(PadSize s) { div.addClassName("px-" + s.token()); return self(); }

        /**
         * Sets block (vertical) padding using the shared spacing scale.
         *
         * @param s padding size token
         * @return this builder
         */
        public B padY(PadSize s) { div.addClassName("py-" + s.token()); return self(); }

        /** Removes all padding → {@code .p-0}. */
        public B padNone() { div.addClassName("p-0");              return self(); }
        /** Uniform small padding (0.5 rem). */
        public B padS()    { return pad(PadSize.S); }
        /** Uniform medium padding (1 rem). */
        public B padM()    { return pad(PadSize.M); }
        /** Uniform large padding (1.5 rem). */
        public B padL()    { return pad(PadSize.L); }

        // --- Border ---

        /** Thin border on all sides → {@code .border-all}. */
        public B border()       { div.addClassName("border-all");    return self(); }

        /** Thin border on the bottom edge only → {@code .border-bottom}. */
        public B borderBottom() { div.addClassName("border-bottom"); return self(); }

        // --- Margin ---

        /**
         * Sets uniform margin using the shared spacing scale.
         *
         * @param s margin size token
         * @return this builder
         */
        public B margin(MarginSize s)  { div.addClassName("m-"  + s.token()); return self(); }

        /**
         * Sets inline (horizontal) margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginX(MarginSize s) { div.addClassName("mx-" + s.token()); return self(); }

        /**
         * Sets block (vertical) margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginY(MarginSize s) { div.addClassName("my-" + s.token()); return self(); }

        /**
         * Sets top margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginTop(MarginSize s)    { div.addClassName("mt-" + s.token()); return self(); }

        /**
         * Sets bottom margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginBottom(MarginSize s) { div.addClassName("mb-" + s.token()); return self(); }

        /**
         * Sets inline-start (LTR: left) margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginStart(MarginSize s) { div.addClassName("ms-" + s.token()); return self(); }

        /**
         * Sets inline-end (LTR: right) margin.
         *
         * @param s margin size token
         * @return this builder
         */
        public B marginEnd(MarginSize s) { div.addClassName("me-" + s.token()); return self(); }

        /** Removes all margin → {@code .m-0}. */
        public B marginNone()  { div.addClassName("m-0");      return self(); }

        /**
         * Centres this div horizontally inside its block container → {@code .mx-auto}
         * ({@code margin-inline: auto}). The div must have an explicit or max-width set.
         */
        public B marginXAuto() { div.addClassName("mx-auto");  return self(); }

        /** Uniform small margin (0.5 rem). */
        public B marginS()     { return margin(MarginSize.S); }
        /** Uniform medium margin (1 rem). */
        public B marginM()     { return margin(MarginSize.M); }
        /** Uniform large margin (1.5 rem). */
        public B marginL()     { return margin(MarginSize.L); }

    }

    // ── Gap Size ──────────────────────────────────────────────────────────────

    /**
     * Strongly-typed gap size tokens for use with responsive gap overrides.
     *
     * <pre>{@code
     * ResponsiveDiv.grid()
     *     .gapXS()
     *     .gap(ViewMode.DESKTOP, GapSize.L)
     *     .build();
     * }</pre>
     */
    public enum GapSize {

        /** {@code gap-xs} — 0.25 rem */
        XS("xs"),
        /** {@code gap-s}  — 0.5 rem */
        S("s"),
        /** {@code gap-m}  — 1 rem (default) */
        M("m"),
        /** {@code gap-l}  — 1.5 rem */
        L("l"),
        /** {@code gap-xl} — 2.5 rem */
        XL("xl");

        private final String token;

        GapSize(String token) { this.token = token; }

        /** Returns the raw CSS token (e.g. {@code "m"} for {@code gap-m}). */
        public String token() { return token; }
    }

    // ── Pad Size ──────────────────────────────────────────────────────────────

    /**
     * Strongly-typed padding size tokens — mirrors the {@link GapSize} scale so
     * spacing is always consistent across gap and padding declarations.
     *
     * <pre>{@code
     * ResponsiveDiv.flex().column().padM().build();
     * ResponsiveDiv.grid().pad(PadSize.L).padX(PadSize.XL).build();
     * }</pre>
     */
    public enum PadSize {

        /** {@code p-xs} — 0.25 rem */
        XS("xs"),
        /** {@code p-s}  — 0.5 rem */
        S("s"),
        /** {@code p-m}  — 1 rem */
        M("m"),
        /** {@code p-l}  — 1.5 rem */
        L("l"),
        /** {@code p-xl} — 2.5 rem */
        XL("xl");

        private final String token;

        PadSize(String token) { this.token = token; }

        /** Returns the raw CSS token (e.g. {@code "m"} for {@code p-m}). */
        public String token() { return token; }
    }

    // ── Margin Size ───────────────────────────────────────────────────────────

    /**
     * Strongly-typed margin size tokens — mirrors the {@link PadSize} / {@link GapSize} scale
     * so that spacing is always consistent across gap, padding, and margin declarations.
     *
     * <pre>{@code
     * ResponsiveDiv.flex().column().marginXAuto().marginY(MarginSize.L).build();
     * }</pre>
     */
    public enum MarginSize {

        /** {@code m-xs} — 0.25 rem */
        XS("xs"),
        /** {@code m-s}  — 0.5 rem */
        S("s"),
        /** {@code m-m}  — 1 rem */
        M("m"),
        /** {@code m-l}  — 1.5 rem */
        L("l"),
        /** {@code m-xl} — 2.5 rem */
        XL("xl");

        private final String token;

        MarginSize(String token) { this.token = token; }

        /** Returns the raw CSS token (e.g. {@code "m"} for {@code m-m}). */
        public String token() { return token; }
    }

    // ── Overflow ──────────────────────────────────────────────────────────────
    /**
     * Overflow behaviour options for use with {@link BaseBuilder#overflow(Overflow)}.
     *
     * <pre>{@code
     * ResponsiveDiv.flex().column().overflow(Overflow.Y_AUTO).build();
     * }</pre>
     */
    public enum Overflow {

        /** {@code overflow-auto} — scrollbars when needed */
        AUTO("overflow-auto"),
        /** {@code overflow-hidden} — clip content */
        HIDDEN("overflow-hidden"),
        /** {@code overflow-scroll} — always show scrollbars */
        SCROLL("overflow-scroll"),
        /** {@code overflow-visible} — allow content to bleed out */
        VISIBLE("overflow-visible"),
        /** {@code overflow-x-auto} — horizontal scroll only */
        X_AUTO("overflow-x-auto"),
        /** {@code overflow-y-auto} — vertical scroll only */
        Y_AUTO("overflow-y-auto");

        private final String cls;

        Overflow(String cls) { this.cls = cls; }

        /** Returns the CSS class name for this overflow value. */
        public String className() { return cls; }
    }

    // ── Grid Builder ──────────────────────────────────────────────────────────

    /**
     * Fluent builder for CSS Grid responsive containers.
     *
     * <pre>{@code
     * ResponsiveDiv.grid()
     *     .mobile(1).tablet(2).desktop(3).gapM()
     *     .add(card1, card2, card3)
     *     .build();
     * }</pre>
     */
    public static final class GridBuilder extends BaseBuilder<GridBuilder, ResponsiveDiv> {

        private GridBuilder() {
            div.addClassName("grid");
            applyGap("gap-m");
        }

        // --- Column count per breakpoint ---

        public GridBuilder mobile(int cols) {
            div.addClassName("grid-cols-" + cols);
            return this;
        }

        public GridBuilder tablet(int cols) {
            div.addClassName(ViewMode.TABLET.toCssClass("grid-cols-" + cols));
            return this;
        }

        public GridBuilder desktop(int cols) {
            div.addClassName(ViewMode.DESKTOP.toCssClass("grid-cols-" + cols));
            return this;
        }

        public GridBuilder largeDesktop(int cols) {
            div.addClassName(ViewMode.LARGE_DESKTOP.toCssClass("grid-cols-" + cols));
            return this;
        }

        public GridBuilder columns(ViewMode mode, int cols) {
            div.addClassName(mode.toCssClass("grid-cols-" + cols));
            return this;
        }

        // --- Gap ---

        /** Uniform gap: {@code gap-xs} (0.25 rem). */
        public GridBuilder gapXS() { applyGap("gap-xs"); return this; }
        /** Uniform gap: {@code gap-s} (0.5 rem). */
        public GridBuilder gapS()  { applyGap("gap-s");  return this; }
        /** Uniform gap: {@code gap-m} (1 rem). Default. */
        public GridBuilder gapM()  { applyGap("gap-m");  return this; }
        /** Uniform gap: {@code gap-l} (1.5 rem). */
        public GridBuilder gapL()  { applyGap("gap-l");  return this; }
        /** Uniform gap: {@code gap-xl} (2.5 rem). */
        public GridBuilder gapXL() { applyGap("gap-xl"); return this; }

        /** Responsive gap override for a specific breakpoint → {@code {prefix}:gap-{token}}. */
        public GridBuilder gap(ViewMode mode, GapSize size) {
            div.addClassName(mode.toCssClass("gap-" + size.token()));
            return this;
        }

        // --- Independent column / row gaps ---

        /** Column gap (x-axis): xs (0.25 rem). */
        public GridBuilder columnGapXS() { div.addClassName("gap-x-xs"); return this; }
        /** Column gap (x-axis): s (0.5 rem). */
        public GridBuilder columnGapS()  { div.addClassName("gap-x-s");  return this; }
        /** Column gap (x-axis): m (1 rem). */
        public GridBuilder columnGapM()  { div.addClassName("gap-x-m");  return this; }
        /** Column gap (x-axis): l (1.5 rem). */
        public GridBuilder columnGapL()  { div.addClassName("gap-x-l");  return this; }

        /** Row gap (y-axis): xs (0.25 rem). */
        public GridBuilder rowGapXS() { div.addClassName("gap-y-xs"); return this; }
        /** Row gap (y-axis): s (0.5 rem). */
        public GridBuilder rowGapS()  { div.addClassName("gap-y-s");  return this; }
        /** Row gap (y-axis): m (1 rem). */
        public GridBuilder rowGapM()  { div.addClassName("gap-y-m");  return this; }
        /** Row gap (y-axis): l (1.5 rem). */
        public GridBuilder rowGapL()  { div.addClassName("gap-y-l");  return this; }

        // --- Grid extras ---

        /**
         * Centers all grid children both axes → {@code .place-center}
         * ({@code place-items: center}).
         */
        public GridBuilder placeCenter() { div.addClassName("place-center");     return this; }

        /**
         * Enables dense auto-placement so grid fills blank cells → {@code .grid-flow-dense}.
         * Useful for masonry-style card grids.
         */
        public GridBuilder dense()       { div.addClassName("grid-flow-dense");  return this; }

        // --- Scoped breakpoint blocks ---

        public GridScopeBuilder mobile()       { return new GridScopeBuilder(this, ViewMode.MOBILE);        }
        public GridScopeBuilder tablet()       { return new GridScopeBuilder(this, ViewMode.TABLET);        }
        public GridScopeBuilder desktop()      { return new GridScopeBuilder(this, ViewMode.DESKTOP);       }
        public GridScopeBuilder largeDesktop() { return new GridScopeBuilder(this, ViewMode.LARGE_DESKTOP); }
        public GridScopeBuilder on(ViewMode mode) { return new GridScopeBuilder(this, mode); }

        // --- Col-span–aware add ---

        public GridBuilder add(ColSpan colSpan, Component... components) {
            for (Component c : components) {
                c.addClassName("col-span-" + colSpan.getGridSpan());
                div.add(c);
            }
            return this;
        }

        public GridBuilder add(GridEntry... entries) {
            for (GridEntry e : entries) {
                if (e.base != null) {
                    e.component.addClassName("col-span-" + e.base.getGridSpan());
                }
                if (e.mode != null && e.responsive != null) {
                    e.component.addClassName(e.mode.toCssClass("col-span-" + e.responsive.getGridSpan()));
                }
                div.add(e.component);
            }
            return this;
        }
    }

    // ── Grid Entry ────────────────────────────────────────────────────────────

    /**
     * Self-documenting column-span configuration for a single grid child.
     *
     * <pre>{@code
     * .add(
     *     GridEntry.of(mainContent).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
     *     GridEntry.of(sidebar).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
     * )
     * }</pre>
     */
    public static final class GridEntry {

        private final Component component;
        private ColSpan base;
        private ViewMode mode;
        private ColSpan responsive;

        private GridEntry(Component component) {
            this.component = component;
        }

        /** Creates a new {@link GridEntry} for the given component. */
        public static GridEntry of(Component component) {
            return new GridEntry(component);
        }

        /** Base (mobile-first, no prefix) column span → {@code col-span-{N}}. */
        public GridEntry base(ColSpan colSpan) {
            this.base = colSpan;
            return this;
        }

        /** Responsive span override at the given breakpoint → {@code {prefix}:col-span-{N}}. */
        public GridEntry at(ViewMode mode, ColSpan span) {
            this.mode = mode;
            this.responsive = span;
            return this;
        }

        /** Shorthand for {@link #at(ViewMode, ColSpan) at(ViewMode.TABLET, span)}. */
        public GridEntry tablet(ColSpan span) { return at(ViewMode.TABLET, span); }

        /** Shorthand for {@link #at(ViewMode, ColSpan) at(ViewMode.DESKTOP, span)}. */
        public GridEntry desktop(ColSpan span) { return at(ViewMode.DESKTOP, span); }

        /** Shorthand for {@link #at(ViewMode, ColSpan) at(ViewMode.LARGE_DESKTOP, span)}. */
        public GridEntry largeDesktop(ColSpan span) { return at(ViewMode.LARGE_DESKTOP, span); }
    }

    // ── Grid Scope Builder ────────────────────────────────────────────────────

    /**
     * A transient scope opened by {@link GridBuilder#mobile()}, {@link GridBuilder#tablet()},
     * {@link GridBuilder#desktop()}, etc. Every method adds a breakpoint-prefixed CSS class
     * (e.g. {@code lg:grid-cols-3}). Call {@link #end()} to return to the parent builder.
     *
     * <pre>{@code
     * ResponsiveDiv.grid()
     *     .mobile(1)
     *     .tablet().gridCols(2).gapM().end()
     *     .desktop().gridCols(3).gapL().end()
     *     .build();
     * }</pre>
     */
    public static final class GridScopeBuilder {

        private final GridBuilder parent;
        private final ViewMode mode;

        private GridScopeBuilder(GridBuilder parent, ViewMode mode) {
            this.parent = parent;
            this.mode   = mode;
        }

        private void css(String base) {
            parent.div.addClassName(mode.toCssClass(base));
        }

        // --- Columns ---

        /** Sets the number of grid columns at this breakpoint. */
        public GridScopeBuilder gridCols(int n) { css("grid-cols-" + n);  return this; }

        // --- Gap ---

        public GridScopeBuilder gapXS() { css("gap-xs"); return this; }
        public GridScopeBuilder gapS()  { css("gap-s");  return this; }
        public GridScopeBuilder gapM()  { css("gap-m");  return this; }
        public GridScopeBuilder gapL()  { css("gap-l");  return this; }
        public GridScopeBuilder gapXL() { css("gap-xl"); return this; }

        // --- Visibility ---

        public GridScopeBuilder hide() { css("hidden"); return this; }
        public GridScopeBuilder show() { css("block");  return this; }

        // --- Margin ---

        public GridScopeBuilder marginNone()  { css("m-0");     return this; }
        public GridScopeBuilder marginXAuto() { css("mx-auto"); return this; }
        public GridScopeBuilder marginM()     { css("m-m");     return this; }
        public GridScopeBuilder marginL()     { css("m-l");     return this; }
        public GridScopeBuilder marginYM()    { css("my-m");    return this; }
        public GridScopeBuilder marginTopM()  { css("mt-m");    return this; }
        public GridScopeBuilder marginBotM()  { css("mb-m");    return this; }

        /** Returns to the parent {@link GridBuilder}. */
        public GridBuilder end() { return parent; }
    }

    // ── Flex Builder ──────────────────────────────────────────────────────────

    /**
     * Fluent builder for Flexbox responsive containers.
     *
     * <pre>{@code
     * ResponsiveDiv.flex()
     *     .column().gapS()
     *     .desktop().row().gapL().alignCenter().end()
     *     .add(child1, child2)
     *     .build();
     * }</pre>
     */
    public static final class FlexBuilder extends BaseBuilder<FlexBuilder, ResponsiveDiv> {

        private FlexBuilder() {
            div.addClassName("flex");
            applyGap("gap-m");
        }

        // --- Base (no-prefix) flex direction ---

        public FlexBuilder column()  { div.addClassName("flex-col");    return this; }
        public FlexBuilder row()     { div.addClassName("flex-row");    return this; }
        public FlexBuilder wrap()    { div.addClassName("flex-wrap");   return this; }
        public FlexBuilder noWrap()  { div.addClassName("flex-nowrap"); return this; }

        /**
         * Switches the container to {@code inline-flex} instead of {@code flex}.
         * Useful for inline widgets (tag groups, badge rows).
         */
        public FlexBuilder inlineFlex() {
            div.removeClassName("flex");
            div.addClassName("inline-flex");
            return this;
        }

        // --- Base gap ---

        public FlexBuilder gapXS()  { applyGap("gap-xs"); return this; }
        public FlexBuilder gapS()   { applyGap("gap-s");  return this; }
        public FlexBuilder gapM()   { applyGap("gap-m");  return this; }
        public FlexBuilder gapL()   { applyGap("gap-l");  return this; }
        public FlexBuilder gapXL()  { applyGap("gap-xl"); return this; }

        public FlexBuilder gap(ViewMode mode, GapSize size) {
            div.addClassName(mode.toCssClass("gap-" + size.token()));
            return this;
        }

        // --- Independent column / row gaps ---

        /** Column gap (x-axis): xs (0.25 rem). */
        public FlexBuilder columnGapXS() { div.addClassName("gap-x-xs"); return this; }
        /** Column gap (x-axis): s (0.5 rem). */
        public FlexBuilder columnGapS()  { div.addClassName("gap-x-s");  return this; }
        /** Column gap (x-axis): m (1 rem). */
        public FlexBuilder columnGapM()  { div.addClassName("gap-x-m");  return this; }
        /** Column gap (x-axis): l (1.5 rem). */
        public FlexBuilder columnGapL()  { div.addClassName("gap-x-l");  return this; }

        /** Row gap (y-axis): xs (0.25 rem). */
        public FlexBuilder rowGapXS() { div.addClassName("gap-y-xs"); return this; }
        /** Row gap (y-axis): s (0.5 rem). */
        public FlexBuilder rowGapS()  { div.addClassName("gap-y-s");  return this; }
        /** Row gap (y-axis): m (1 rem). */
        public FlexBuilder rowGapM()  { div.addClassName("gap-y-m");  return this; }
        /** Row gap (y-axis): l (1.5 rem). */
        public FlexBuilder rowGapL()  { div.addClassName("gap-y-l");  return this; }

        // --- Base align ---

        public FlexBuilder alignCenter()   { div.addClassName("items-center");   return this; }
        public FlexBuilder alignStart()    { div.addClassName("items-start");    return this; }
        public FlexBuilder alignEnd()      { div.addClassName("items-end");      return this; }
        public FlexBuilder alignStretch()  { div.addClassName("items-stretch");  return this; }
        public FlexBuilder alignBaseline() { div.addClassName("items-baseline"); return this; }

        // --- Base justify ---

        public FlexBuilder justifyBetween() { div.addClassName("justify-between"); return this; }
        public FlexBuilder justifyCenter()  { div.addClassName("justify-center");  return this; }
        public FlexBuilder justifyEnd()     { div.addClassName("justify-end");     return this; }
        public FlexBuilder justifyStart()   { div.addClassName("justify-start");   return this; }
        public FlexBuilder justifyAround()  { div.addClassName("justify-around");  return this; }
        public FlexBuilder justifyEvenly()  { div.addClassName("justify-evenly");  return this; }

        // --- Scoped breakpoint blocks ---

        public FlexScopeBuilder mobile()       { return new FlexScopeBuilder(this, ViewMode.MOBILE);       }
        public FlexScopeBuilder tablet()       { return new FlexScopeBuilder(this, ViewMode.TABLET);       }
        public FlexScopeBuilder desktop()      { return new FlexScopeBuilder(this, ViewMode.DESKTOP);      }
        public FlexScopeBuilder largeDesktop() { return new FlexScopeBuilder(this, ViewMode.LARGE_DESKTOP); }
        public FlexScopeBuilder on(ViewMode mode) { return new FlexScopeBuilder(this, mode); }
    }

    // ── Flex Scope Builder ────────────────────────────────────────────────────

    /**
     * A transient scope opened by {@link FlexBuilder#mobile()}, {@link FlexBuilder#tablet()},
     * {@link FlexBuilder#desktop()}, etc. Every method adds a breakpoint-prefixed CSS class
     * (e.g. {@code lg:flex-row}). Call {@link #end()} to return to the parent builder.
     *
     * <pre>{@code
     * .desktop().row().gapL().alignCenter().justifyBetween().end()
     * // adds: lg:flex-row lg:gap-l lg:items-center lg:justify-between
     * }</pre>
     */
    public static final class FlexScopeBuilder {

        private final FlexBuilder parent;
        private final ViewMode mode;

        private FlexScopeBuilder(FlexBuilder parent, ViewMode mode) {
            this.parent = parent;
            this.mode   = mode;
        }

        private void css(String base) {
            parent.div.addClassName(mode.toCssClass(base));
        }

        // --- Direction ---

        public FlexScopeBuilder column()  { css("flex-col");    return this; }
        public FlexScopeBuilder row()     { css("flex-row");    return this; }
        public FlexScopeBuilder wrap()    { css("flex-wrap");   return this; }
        public FlexScopeBuilder noWrap()  { css("flex-nowrap"); return this; }

        // --- Gap ---

        public FlexScopeBuilder gapXS()  { css("gap-xs"); return this; }
        public FlexScopeBuilder gapS()   { css("gap-s");  return this; }
        public FlexScopeBuilder gapM()   { css("gap-m");  return this; }
        public FlexScopeBuilder gapL()   { css("gap-l");  return this; }
        public FlexScopeBuilder gapXL()  { css("gap-xl"); return this; }

        // --- Align items ---

        public FlexScopeBuilder alignCenter()   { css("items-center");   return this; }
        public FlexScopeBuilder alignStart()    { css("items-start");    return this; }
        public FlexScopeBuilder alignEnd()      { css("items-end");      return this; }
        public FlexScopeBuilder alignStretch()  { css("items-stretch");  return this; }
        public FlexScopeBuilder alignBaseline() { css("items-baseline"); return this; }

        // --- Justify content ---

        public FlexScopeBuilder justifyBetween() { css("justify-between"); return this; }
        public FlexScopeBuilder justifyCenter()  { css("justify-center");  return this; }
        public FlexScopeBuilder justifyEnd()     { css("justify-end");     return this; }
        public FlexScopeBuilder justifyStart()   { css("justify-start");   return this; }
        public FlexScopeBuilder justifyAround()  { css("justify-around");  return this; }
        public FlexScopeBuilder justifyEvenly()  { css("justify-evenly");  return this; }

        // --- Align self (this div is a flex/grid child) ---

        public FlexScopeBuilder selfCenter()  { css("self-center");  return this; }
        public FlexScopeBuilder selfStart()   { css("self-start");   return this; }
        public FlexScopeBuilder selfEnd()     { css("self-end");     return this; }
        public FlexScopeBuilder selfStretch() { css("self-stretch"); return this; }

        // --- Sizing ---

        /** Forces {@code width: 100%} at this breakpoint → {@code {prefix}:w-full}. */
        public FlexScopeBuilder fullWidth() { css("w-full"); return this; }

        // --- Padding ---

        public FlexScopeBuilder padS() { css("p-s"); return this; }
        public FlexScopeBuilder padM() { css("p-m"); return this; }
        public FlexScopeBuilder padL() { css("p-l"); return this; }

        // --- Text alignment ---

        public FlexScopeBuilder textLeft()   { css("text-left");   return this; }
        public FlexScopeBuilder textCenter() { css("text-center"); return this; }
        public FlexScopeBuilder textRight()  { css("text-right");  return this; }

        // --- Overflow ---

        public FlexScopeBuilder overflowHidden() { css("overflow-hidden"); return this; }

        // --- Margin ---

        public FlexScopeBuilder marginNone()   { css("m-0");      return this; }
        public FlexScopeBuilder marginXAuto()  { css("mx-auto");  return this; }
        public FlexScopeBuilder marginS()      { css("m-s");      return this; }
        public FlexScopeBuilder marginM()      { css("m-m");      return this; }
        public FlexScopeBuilder marginL()      { css("m-l");      return this; }
        public FlexScopeBuilder marginXS()     { css("mx-s");     return this; }
        public FlexScopeBuilder marginXM()     { css("mx-m");     return this; }
        public FlexScopeBuilder marginYS()     { css("my-s");     return this; }
        public FlexScopeBuilder marginYM()     { css("my-m");     return this; }
        public FlexScopeBuilder marginTopS()   { css("mt-s");     return this; }
        public FlexScopeBuilder marginTopM()   { css("mt-m");     return this; }
        public FlexScopeBuilder marginBotS()   { css("mb-s");     return this; }
        public FlexScopeBuilder marginBotM()   { css("mb-m");     return this; }

        // --- Visibility ---

        public FlexScopeBuilder hide() { css("hidden"); return this; }
        public FlexScopeBuilder show() { css("block");  return this; }

        /** Returns to the parent {@link FlexBuilder}. */
        public FlexBuilder end() { return parent; }
    }
}

