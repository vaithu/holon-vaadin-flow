package com.holonplatform.vaadin.flow.vaadinplus;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.enums.ColSpan;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.WindowSize;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.iyensoft.vaadin.flow.utils.responsive.WindowSizeTracker;

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
 *     .content(textBlock, imageBlock)
 *     .build();
 *
 * // Pattern 2 — card grid: 1 → 2 → 3 columns
 * ResponsiveDiv cards = ResponsiveDiv.grid()
 *     .mobile(1).tablet(2).desktop(3).gapM()
 *     .content(card1, card2, card3)
 *     .build();
 *
 * // Pattern 3 — asymmetric: 8-col main + 4-col sidebar
 * ResponsiveDiv layout = ResponsiveDiv.grid()
 *     .mobile(1).desktop(12)
 *     .content(
 *         GridEntry.of(mainContent).base(ColSpan.COL_12).desktop(ColSpan.COL_8),
 *         GridEntry.of(sidebar).base(ColSpan.COL_12).desktop(ColSpan.COL_4)
 *     )
 *     .build();
 * }</pre>
 *
 * @since 10.0.0
 */

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
     * Starts a mode-switching container that renders one child for mobile-sized viewports
     * and a different child for desktop-sized viewports.
     *
     * <p>The suppliers are resolved lazily and only once per branch. The currently selected
     * branch is swapped in response to viewport resize events, so the caller can reuse the
     * same master component instance across both branches while deferring the desktop detail
     * subtree until a desktop viewport is actually active.
     */
    public static ModeSwitchBuilder modeSwitch() {
        return new ModeSwitchBuilder();
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
     * Configures an <em>existing</em> {@link Div} as a Flexbox container, exposing the full
     * flex fluent API ({@code column()/row()}, gaps, {@code align*()}, {@code justify*()} and
     * per-breakpoint {@code desktop().row().end()} scopes) in addition to the shared sizing /
     * slot API from {@link BaseBuilder}.
     *
     * <p>The {@code flex} class and the default {@code gap-m} are applied to the given div, and
     * the same instance is returned from {@link FlexConfigurator#build()}.
     *
     * <pre>{@code
     * ResponsiveDiv.configureFlex(this)
     *     .column().gapM().alignStretch()
     *     .desktop().row().gapL().end()
     *     .fullHeight()
     *     .slotOnce(ViewMode.DESKTOP, this::buildDesktopView)
     *     .slotOnce(ViewMode.MOBILE,  this::buildMobileView)
     *     .build();
     * }</pre>
     *
     * @param div the existing {@link Div} to turn into a flex container (not null)
     * @return a {@link FlexConfigurator} wrapping the given instance
     */
    public static FlexConfigurator configureFlex(Div div) {
        return new FlexConfigurator(div);
    }

    /**
     * Configures an <em>existing</em> {@link Div} as a CSS Grid container, exposing the full
     * grid fluent API ({@code mobile(n)/tablet(n)/desktop(n)}, gaps, {@code placeCenter()},
     * {@code dense()}, col-span content and per-breakpoint {@code desktop().gridCols(3).end()}
     * scopes) in addition to the shared sizing / slot API from {@link BaseBuilder}.
     *
     * <p>The {@code grid} class and the default {@code gap-m} are applied to the given div, and
     * the same instance is returned from {@link GridConfigurator#build()}.
     *
     * <pre>{@code
     * ResponsiveDiv.configureGrid(this)
     *     .mobile(1).tablet(2).desktop(3).gapM()
     *     .fullHeight()
     *     .slotOnce(ViewMode.DESKTOP, this::buildDesktopView)
     *     .slotOnce(ViewMode.MOBILE,  this::buildMobileView)
     *     .build();
     * }</pre>
     *
     * @param div the existing {@link Div} to turn into a grid container (not null)
     * @return a {@link GridConfigurator} wrapping the given instance
     */
    public static GridConfigurator configureGrid(Div div) {
        return new GridConfigurator(div);
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

    /**
     * Responsive container that swaps between a mobile branch and a desktop branch.
     */
    public static final class ModeSwitchBuilder extends BaseBuilder<ModeSwitchBuilder, ResponsiveDiv> {

        private Supplier<Component> mobileSupplier;
        private Supplier<Component> desktopSupplier;
        private Component mobileComponent;
        private Component desktopComponent;
        private ViewMode currentMode;
        private Registration resizeRegistration;
        private Consumer<ViewMode> modeChangeListener;

        private ModeSwitchBuilder() {
        }

        public ModeSwitchBuilder mobile(Supplier<Component> supplier) {
            this.mobileSupplier = supplier;
            return this;
        }

        public ModeSwitchBuilder desktop(Supplier<Component> supplier) {
            this.desktopSupplier = supplier;
            return this;
        }

        public ModeSwitchBuilder onModeChange(Consumer<ViewMode> listener) {
            this.modeChangeListener = listener;
            return this;
        }

        @Override
        public ResponsiveDiv build() {
            div.addAttachListener(event -> {
                if (resizeRegistration != null) {
                    resizeRegistration.remove();
                }
                resizeRegistration = WindowSizeTracker.track(event.getUI(), div, this::applyMode);
            });

            div.addDetachListener(event -> {
                if (resizeRegistration != null) {
                    resizeRegistration.remove();
                    resizeRegistration = null;
                }
            });

            return div;
        }

        private void applyMode(ViewMode mode) {
            if (mode == null) {
                return;
            }

            ViewMode target = isDesktopMode(mode) ? ViewMode.DESKTOP : ViewMode.MOBILE;
            if (target == currentMode && !div.getChildren().toList().isEmpty()) {
                return;
            }

            currentMode = target;
            div.removeAll();

            Component branch = target == ViewMode.MOBILE ? resolveMobile() : resolveDesktop();
            if (branch != null) {
                div.add(branch);
            }

            if (modeChangeListener != null) {
                modeChangeListener.accept(target);
            }
        }

        private Component resolveMobile() {
            if (mobileComponent == null && mobileSupplier != null) {
                mobileComponent = mobileSupplier.get();
            }
            return mobileComponent;
        }

        private Component resolveDesktop() {
            if (desktopComponent == null && desktopSupplier != null) {
                desktopComponent = desktopSupplier.get();
            }
            return desktopComponent;
        }

        private static boolean isDesktopMode(ViewMode mode) {
            return mode == ViewMode.TABLET
                    || mode == ViewMode.DESKTOP
                    || mode == ViewMode.LARGE_DESKTOP
                    || mode == ViewMode.ULTRA_WIDE;
        }
    }

    // ── Base Builder ──────────────────────────────────────────────────────────

    /**
     * Static WeakHashMap registry for slot suppliers. Static fields are never serialized,
     * so non-serializable Supplier lambdas stored here are invisible to Java serialization.
     * WeakHashMap ensures Div instances can be GC'd once there are no other references.
     * After session deserialization the Div is a new instance — the registry returns null
     * and the attach listener is a graceful no-op.
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Div, Map> SLOT_REGISTRY =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

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
                // Store slot suppliers in the static WeakHashMap (never serialized).
                // After session deserialization the div is a new instance — registry returns null
                // and the attach listener below is a graceful no-op.
                final D capturedDiv = div;
                @SuppressWarnings("unchecked")
                Map<ViewMode, Supplier<Component>> slotsCopy = new java.util.HashMap<>(slots);
                SLOT_REGISTRY.put(capturedDiv, slotsCopy);
                capturedDiv.addAttachListener(event -> {
                    @SuppressWarnings("unchecked")
                    Map<ViewMode, Supplier<Component>> stored =
                            (Map<ViewMode, Supplier<Component>>) SLOT_REGISTRY.remove(capturedDiv);
                    if (stored == null) return;   // after deserialization: graceful no-op
                    WindowSize size = Signal.untracked(
                            () -> event.getUI().getPage().windowSizeSignal().get());
                    if (size != null) {
                        ViewMode mode = UIUtils.getViewMode(size.width(), size.height());
                        Supplier<Component> s = resolveSlot(stored, mode);
                        if (s != null) capturedDiv.add(s.get());
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
     *     .content(card1, card2, card3)
     *     .build();
     * }</pre>
     */
    public abstract static class AbstractGridBuilder<B extends AbstractGridBuilder<B, D>, D extends Div>
            extends BaseBuilder<B, D> {

        /** Creates a brand-new {@link ResponsiveDiv} as the grid container. */
        protected AbstractGridBuilder() {
        }

        /** Wraps an existing {@link Div} as the grid container. */
        protected AbstractGridBuilder(D existing) {
            super(existing);
        }

        // --- Column count per breakpoint ---

        public B mobile(int cols) {
            div.addClassName("grid-cols-" + cols);
            return self();
        }

        public B tablet(int cols) {
            div.addClassName(ViewMode.TABLET.toCssClass("grid-cols-" + cols));
            return self();
        }

        public B desktop(int cols) {
            div.addClassName(ViewMode.DESKTOP.toCssClass("grid-cols-" + cols));
            return self();
        }

        public B largeDesktop(int cols) {
            div.addClassName(ViewMode.LARGE_DESKTOP.toCssClass("grid-cols-" + cols));
            return self();
        }

        public B columns(ViewMode mode, int cols) {
            div.addClassName(mode.toCssClass("grid-cols-" + cols));
            return self();
        }

        // --- Gap ---

        /** Uniform gap: {@code gap-xs} (0.25 rem). */
        public B gapXS() { applyGap("gap-xs"); return self(); }
        /** Uniform gap: {@code gap-s} (0.5 rem). */
        public B gapS()  { applyGap("gap-s");  return self(); }
        /** Uniform gap: {@code gap-m} (1 rem). Default. */
        public B gapM()  { applyGap("gap-m");  return self(); }
        /** Uniform gap: {@code gap-l} (1.5 rem). */
        public B gapL()  { applyGap("gap-l");  return self(); }
        /** Uniform gap: {@code gap-xl} (2.5 rem). */
        public B gapXL() { applyGap("gap-xl"); return self(); }

        /** Responsive gap override for a specific breakpoint → {@code {prefix}:gap-{token}}. */
        public B gap(ViewMode mode, GapSize size) {
            div.addClassName(mode.toCssClass("gap-" + size.token()));
            return self();
        }

        // --- Independent column / row gaps ---

        /** Column gap (x-axis): xs (0.25 rem). */
        public B columnGapXS() { div.addClassName("gap-x-xs"); return self(); }
        /** Column gap (x-axis): s (0.5 rem). */
        public B columnGapS()  { div.addClassName("gap-x-s");  return self(); }
        /** Column gap (x-axis): m (1 rem). */
        public B columnGapM()  { div.addClassName("gap-x-m");  return self(); }
        /** Column gap (x-axis): l (1.5 rem). */
        public B columnGapL()  { div.addClassName("gap-x-l");  return self(); }

        /** Row gap (y-axis): xs (0.25 rem). */
        public B rowGapXS() { div.addClassName("gap-y-xs"); return self(); }
        /** Row gap (y-axis): s (0.5 rem). */
        public B rowGapS()  { div.addClassName("gap-y-s");  return self(); }
        /** Row gap (y-axis): m (1 rem). */
        public B rowGapM()  { div.addClassName("gap-y-m");  return self(); }
        /** Row gap (y-axis): l (1.5 rem). */
        public B rowGapL()  { div.addClassName("gap-y-l");  return self(); }

        // --- Grid extras ---

        /**
         * Centers all grid children both axes → {@code .place-center}
         * ({@code place-items: center}).
         */
        public B placeCenter() { div.addClassName("place-center");     return self(); }

        /**
         * Enables dense auto-placement so grid fills blank cells → {@code .grid-flow-dense}.
         * Useful for masonry-style card grids.
         */
        public B dense()       { div.addClassName("grid-flow-dense");  return self(); }

        // --- Scoped breakpoint blocks ---

        public GridScopeBuilder<B> mobile()       { return new GridScopeBuilder<>(self(), ViewMode.MOBILE);        }
        public GridScopeBuilder<B> tablet()       { return new GridScopeBuilder<>(self(), ViewMode.TABLET);        }
        public GridScopeBuilder<B> desktop()      { return new GridScopeBuilder<>(self(), ViewMode.DESKTOP);       }
        public GridScopeBuilder<B> largeDesktop() { return new GridScopeBuilder<>(self(), ViewMode.LARGE_DESKTOP); }
        public GridScopeBuilder<B> on(ViewMode mode) { return new GridScopeBuilder<>(self(), mode); }

        // --- Col-span–aware content ---

        public B add(ColSpan colSpan, Component... components) {
            for (Component c : components) {
                c.addClassName("col-span-" + colSpan.getGridSpan());
                div.add(c);
            }
            return self();
        }

        public B add(GridEntry... entries) {
            for (GridEntry e : entries) {
                if (e.base != null) {
                    e.component.addClassName("col-span-" + e.base.getGridSpan());
                }
                if (e.mode != null && e.responsive != null) {
                    e.component.addClassName(e.mode.toCssClass("col-span-" + e.responsive.getGridSpan()));
                }
                div.add(e.component);
            }
            return self();
        }
    }

    /**
     * CSS Grid container builder that creates a fresh {@link ResponsiveDiv}.
     * Obtain via {@link ResponsiveDiv#grid()}.
     */
    public static final class GridBuilder extends AbstractGridBuilder<GridBuilder, ResponsiveDiv> {

        private GridBuilder() {
            div.addClassName("grid");
            applyGap("gap-m");
        }
    }

    /**
     * CSS Grid configurator that turns an <em>existing</em> {@link Div} into a grid container,
     * exposing the full {@link AbstractGridBuilder} API (column counts, gap, breakpoint scopes,
     * col-span content) plus the shared {@link BaseBuilder} sizing / slot API.
     *
     * <p>Obtain via {@link ResponsiveDiv#configureGrid(Div)}:
     * <pre>{@code
     * ResponsiveDiv.configureGrid(this)
     *     .mobile(1).tablet(2).desktop(3).gapM()
     *     .fullHeight()
     *     .slotOnce(ViewMode.DESKTOP, this::buildDesktopView)
     *     .slotOnce(ViewMode.MOBILE,  this::buildMobileView)
     *     .build();
     * }</pre>
     */
    public static final class GridConfigurator extends AbstractGridBuilder<GridConfigurator, Div> {

        private GridConfigurator(Div existing) {
            super(existing);
            div.addClassName("grid");
            applyGap("gap-m");
        }
    }

    // ── Grid Entry ────────────────────────────────────────────────────────────

    /**
     * Self-documenting column-span configuration for a single grid child.
     *
     * <pre>{@code
     * .content(
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
    public static final class GridScopeBuilder<P extends AbstractGridBuilder<P, ?>> {

        private final P parent;
        private final ViewMode mode;

        private GridScopeBuilder(P parent, ViewMode mode) {
            this.parent = parent;
            this.mode   = mode;
        }

        private void css(String base) {
            parent.div.addClassName(mode.toCssClass(base));
        }

        // --- Columns ---

        /** Sets the number of grid columns at this breakpoint. */
        public GridScopeBuilder<P> gridCols(int n) { css("grid-cols-" + n);  return this; }

        // --- Gap ---

        public GridScopeBuilder<P> gapXS() { css("gap-xs"); return this; }
        public GridScopeBuilder<P> gapS()  { css("gap-s");  return this; }
        public GridScopeBuilder<P> gapM()  { css("gap-m");  return this; }
        public GridScopeBuilder<P> gapL()  { css("gap-l");  return this; }
        public GridScopeBuilder<P> gapXL() { css("gap-xl"); return this; }

        // --- Visibility ---

        public GridScopeBuilder<P> hide() { css("hidden"); return this; }
        public GridScopeBuilder<P> show() { css("block");  return this; }

        // --- Margin ---

        public GridScopeBuilder<P> marginNone()  { css("m-0");     return this; }
        public GridScopeBuilder<P> marginXAuto() { css("mx-auto"); return this; }
        public GridScopeBuilder<P> marginM()     { css("m-m");     return this; }
        public GridScopeBuilder<P> marginL()     { css("m-l");     return this; }
        public GridScopeBuilder<P> marginYM()    { css("my-m");    return this; }
        public GridScopeBuilder<P> marginTopM()  { css("mt-m");    return this; }
        public GridScopeBuilder<P> marginBotM()  { css("mb-m");    return this; }

        /** Returns to the parent grid builder / configurator. */
        public P end() { return parent; }
    }

    // ── Flex Builder ──────────────────────────────────────────────────────────

    /**
     * Fluent builder for Flexbox responsive containers.
     *
     * <pre>{@code
     * ResponsiveDiv.flex()
     *     .column().gapS()
     *     .desktop().row().gapL().alignCenter().end()
     *     .content(child1, child2)
     *     .build();
     * }</pre>
     */
    public abstract static class AbstractFlexBuilder<B extends AbstractFlexBuilder<B, D>, D extends Div>
            extends BaseBuilder<B, D> {

        /** Creates a brand-new {@link ResponsiveDiv} as the flex container. */
        protected AbstractFlexBuilder() {
        }

        /** Wraps an existing {@link Div} as the flex container. */
        protected AbstractFlexBuilder(D existing) {
            super(existing);
        }

        // --- Base (no-prefix) flex direction ---

        public B column()  { div.addClassName("flex-col");    return self(); }
        public B row()     { div.addClassName("flex-row");    return self(); }
        public B wrap()    { div.addClassName("flex-wrap");   return self(); }
        public B noWrap()  { div.addClassName("flex-nowrap"); return self(); }

        /**
         * Switches the container to {@code inline-flex} instead of {@code flex}.
         * Useful for inline widgets (tag groups, badge rows).
         */
        public B inlineFlex() {
            div.removeClassName("flex");
            div.addClassName("inline-flex");
            return self();
        }

        // --- Base gap ---

        public B gapXS()  { applyGap("gap-xs"); return self(); }
        public B gapS()   { applyGap("gap-s");  return self(); }
        public B gapM()   { applyGap("gap-m");  return self(); }
        public B gapL()   { applyGap("gap-l");  return self(); }
        public B gapXL()  { applyGap("gap-xl"); return self(); }

        public B gap(ViewMode mode, GapSize size) {
            div.addClassName(mode.toCssClass("gap-" + size.token()));
            return self();
        }

        // --- Independent column / row gaps ---

        /** Column gap (x-axis): xs (0.25 rem). */
        public B columnGapXS() { div.addClassName("gap-x-xs"); return self(); }
        /** Column gap (x-axis): s (0.5 rem). */
        public B columnGapS()  { div.addClassName("gap-x-s");  return self(); }
        /** Column gap (x-axis): m (1 rem). */
        public B columnGapM()  { div.addClassName("gap-x-m");  return self(); }
        /** Column gap (x-axis): l (1.5 rem). */
        public B columnGapL()  { div.addClassName("gap-x-l");  return self(); }

        /** Row gap (y-axis): xs (0.25 rem). */
        public B rowGapXS() { div.addClassName("gap-y-xs"); return self(); }
        /** Row gap (y-axis): s (0.5 rem). */
        public B rowGapS()  { div.addClassName("gap-y-s");  return self(); }
        /** Row gap (y-axis): m (1 rem). */
        public B rowGapM()  { div.addClassName("gap-y-m");  return self(); }
        /** Row gap (y-axis): l (1.5 rem). */
        public B rowGapL()  { div.addClassName("gap-y-l");  return self(); }

        // --- Base align ---

        public B alignCenter()   { div.addClassName("items-center");   return self(); }
        public B alignStart()    { div.addClassName("items-start");    return self(); }
        public B alignEnd()      { div.addClassName("items-end");      return self(); }
        public B alignStretch()  { div.addClassName("items-stretch");  return self(); }
        public B alignBaseline() { div.addClassName("items-baseline"); return self(); }

        // --- Base justify ---

        public B justifyBetween() { div.addClassName("justify-between"); return self(); }
        public B justifyCenter()  { div.addClassName("justify-center");  return self(); }
        public B justifyEnd()     { div.addClassName("justify-end");     return self(); }
        public B justifyStart()   { div.addClassName("justify-start");   return self(); }
        public B justifyAround()  { div.addClassName("justify-around");  return self(); }
        public B justifyEvenly()  { div.addClassName("justify-evenly");  return self(); }

        // --- Scoped breakpoint blocks ---

        public FlexScopeBuilder<B> mobile()       { return new FlexScopeBuilder<>(self(), ViewMode.MOBILE);        }
        public FlexScopeBuilder<B> tablet()       { return new FlexScopeBuilder<>(self(), ViewMode.TABLET);        }
        public FlexScopeBuilder<B> desktop()      { return new FlexScopeBuilder<>(self(), ViewMode.DESKTOP);       }
        public FlexScopeBuilder<B> largeDesktop() { return new FlexScopeBuilder<>(self(), ViewMode.LARGE_DESKTOP); }
        public FlexScopeBuilder<B> on(ViewMode mode) { return new FlexScopeBuilder<>(self(), mode); }
    }

    /**
     * Flexbox container builder that creates a fresh {@link ResponsiveDiv}.
     * Obtain via {@link ResponsiveDiv#flex()}.
     */
    public static final class FlexBuilder extends AbstractFlexBuilder<FlexBuilder, ResponsiveDiv> {

        private FlexBuilder() {
            div.addClassName("flex");
            applyGap("gap-m");
        }
    }

    /**
     * Flexbox configurator that turns an <em>existing</em> {@link Div} into a flex container,
     * exposing the full {@link AbstractFlexBuilder} API (direction, gap, align, justify,
     * breakpoint scopes) plus the shared {@link BaseBuilder} sizing / slot API.
     *
     * <p>Obtain via {@link ResponsiveDiv#configureFlex(Div)}:
     * <pre>{@code
     * ResponsiveDiv.configureFlex(this)
     *     .column().gapM().alignStretch()
     *     .desktop().row().gapL().end()
     *     .fullHeight()
     *     .slotOnce(ViewMode.DESKTOP, this::buildDesktopView)
     *     .slotOnce(ViewMode.MOBILE,  this::buildMobileView)
     *     .build();
     * }</pre>
     */
    public static final class FlexConfigurator extends AbstractFlexBuilder<FlexConfigurator, Div> {

        private FlexConfigurator(Div existing) {
            super(existing);
            div.addClassName("flex");
            applyGap("gap-m");
        }
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
    public static final class FlexScopeBuilder<P extends AbstractFlexBuilder<P, ?>> {

        private final P parent;
        private final ViewMode mode;

        private FlexScopeBuilder(P parent, ViewMode mode) {
            this.parent = parent;
            this.mode   = mode;
        }

        private void css(String base) {
            parent.div.addClassName(mode.toCssClass(base));
        }

        // --- Direction ---

        public FlexScopeBuilder<P> column()  { css("flex-col");    return this; }
        public FlexScopeBuilder<P> row()     { css("flex-row");    return this; }
        public FlexScopeBuilder<P> wrap()    { css("flex-wrap");   return this; }
        public FlexScopeBuilder<P> noWrap()  { css("flex-nowrap"); return this; }

        // --- Gap ---

        public FlexScopeBuilder<P> gapXS()  { css("gap-xs"); return this; }
        public FlexScopeBuilder<P> gapS()   { css("gap-s");  return this; }
        public FlexScopeBuilder<P> gapM()   { css("gap-m");  return this; }
        public FlexScopeBuilder<P> gapL()   { css("gap-l");  return this; }
        public FlexScopeBuilder<P> gapXL()  { css("gap-xl"); return this; }

        // --- Align items ---

        public FlexScopeBuilder<P> alignCenter()   { css("items-center");   return this; }
        public FlexScopeBuilder<P> alignStart()    { css("items-start");    return this; }
        public FlexScopeBuilder<P> alignEnd()      { css("items-end");      return this; }
        public FlexScopeBuilder<P> alignStretch()  { css("items-stretch");  return this; }
        public FlexScopeBuilder<P> alignBaseline() { css("items-baseline"); return this; }

        // --- Justify content ---

        public FlexScopeBuilder<P> justifyBetween() { css("justify-between"); return this; }
        public FlexScopeBuilder<P> justifyCenter()  { css("justify-center");  return this; }
        public FlexScopeBuilder<P> justifyEnd()     { css("justify-end");     return this; }
        public FlexScopeBuilder<P> justifyStart()   { css("justify-start");   return this; }
        public FlexScopeBuilder<P> justifyAround()  { css("justify-around");  return this; }
        public FlexScopeBuilder<P> justifyEvenly()  { css("justify-evenly");  return this; }

        // --- Align self (this div is a flex/grid child) ---

        public FlexScopeBuilder<P> selfCenter()  { css("self-center");  return this; }
        public FlexScopeBuilder<P> selfStart()   { css("self-start");   return this; }
        public FlexScopeBuilder<P> selfEnd()     { css("self-end");     return this; }
        public FlexScopeBuilder<P> selfStretch() { css("self-stretch"); return this; }

        // --- Sizing ---

        /** Forces {@code width: 100%} at this breakpoint → {@code {prefix}:w-full}. */
        public FlexScopeBuilder<P> fullWidth() { css("w-full"); return this; }

        /** Forces {@code height: 100%} at this breakpoint → {@code {prefix}:h-full}. */
        public FlexScopeBuilder<P> fullHeight() { css("h-full"); return this; }

        // --- Padding ---

        public FlexScopeBuilder<P> padS() { css("p-s"); return this; }
        public FlexScopeBuilder<P> padM() { css("p-m"); return this; }
        public FlexScopeBuilder<P> padL() { css("p-l"); return this; }

        // --- Text alignment ---

        public FlexScopeBuilder<P> textLeft()   { css("text-left");   return this; }
        public FlexScopeBuilder<P> textCenter() { css("text-center"); return this; }
        public FlexScopeBuilder<P> textRight()  { css("text-right");  return this; }

        // --- Overflow ---

        public FlexScopeBuilder<P> overflowHidden() { css("overflow-hidden"); return this; }

        // --- Margin ---

        public FlexScopeBuilder<P> marginNone()   { css("m-0");      return this; }
        public FlexScopeBuilder<P> marginXAuto()  { css("mx-auto");  return this; }
        public FlexScopeBuilder<P> marginS()      { css("m-s");      return this; }
        public FlexScopeBuilder<P> marginM()      { css("m-m");      return this; }
        public FlexScopeBuilder<P> marginL()      { css("m-l");      return this; }
        public FlexScopeBuilder<P> marginXS()     { css("mx-s");     return this; }
        public FlexScopeBuilder<P> marginXM()     { css("mx-m");     return this; }
        public FlexScopeBuilder<P> marginYS()     { css("my-s");     return this; }
        public FlexScopeBuilder<P> marginYM()     { css("my-m");     return this; }
        public FlexScopeBuilder<P> marginTopS()   { css("mt-s");     return this; }
        public FlexScopeBuilder<P> marginTopM()   { css("mt-m");     return this; }
        public FlexScopeBuilder<P> marginBotS()   { css("mb-s");     return this; }
        public FlexScopeBuilder<P> marginBotM()   { css("mb-m");     return this; }

        // --- Visibility ---

        public FlexScopeBuilder<P> hide() { css("hidden"); return this; }
        public FlexScopeBuilder<P> show() { css("block");  return this; }

        /** Returns to the parent flex builder / configurator. */
        public P end() { return parent; }
    }
}
