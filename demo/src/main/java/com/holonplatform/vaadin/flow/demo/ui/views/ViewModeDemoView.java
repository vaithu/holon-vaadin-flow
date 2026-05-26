package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;


/**
 * Exhaustive reference demo for {@link ViewMode} — covering every enum value,
 * every API method, and every integration pattern in the Holon responsive system.
 *
 * <p>Sections:
 * <ol>
 *   <li>ViewMode Enum Reference — all 7 values with prefix, min-width, CSS class, and data attribute</li>
 *   <li>toCssClass() — prefix-driven class name generation</li>
 *   <li>applyTo(Component) — semantic tagging (vm-* class + data-view-prefix)</li>
 *   <li>applyTo(Component, String...) — semantic tagging + auto-prefixed utility classes</li>
 *   <li>applyAll() — same base class broadcast across multiple ViewModes</li>
 *   <li>removeApplied() — reversible mode cleanup</li>
 *   <li>fromPrefix() / fromBreakpoint() — reverse-lookup API</li>
 *   <li>Posture variants — MOBILE_PORTRAIT / MOBILE_LANDSCAPE</li>
 *   <li>Stack ↔ Row responsive pattern via direct ViewMode API</li>
 *   <li>Full 5-breakpoint responsive grid via ColumnBuilder</li>
 * </ol>
 */
@PageTitle("ViewMode – Holon Demo")
@Route(value = "viewmode", layout = DemoMainLayout.class)
public class ViewModeDemoView extends Div {

    public ViewModeDemoView() {
        addClassName("app-view");

        var title = new H1("ViewMode");

        var desc = new Paragraph(
                "ViewMode is the semantic breakpoint enum at the heart of Holon's responsive system. "
                + "It maps every viewport tier — MOBILE through ULTRA_WIDE — to a CSS prefix "
                + "(\"sm\", \"md\", \"lg\", \"xl\", \"2xl\"), and provides a fluent API for tagging "
                + "components with semantic classes, generating prefixed utility class names, and "
                + "It integrates with ColumnBuilder, RowBuilder, and IyenResponsiveLayout.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(enumReferenceExample());
        examples.add(toCssClassExample());
        examples.add(applyToSemanticExample());
        examples.add(applyToWithUtilitiesExample());
        examples.add(applyAllExample());
        examples.add(removeAppliedExample());
        examples.add(reverseLookupExample());
        examples.add(postureVariantsExample());
        examples.add(stackRowPatternExample());
        examples.add(fullBreakpointGridExample());

        add(title, desc, examples);
    }

    // ── Example 1 ────────────────────────────────────────────────────────────

    /**
     * Full reference table: every ViewMode value with prefix, min-width, semantic class, and Breakpoint.
     */
    private DemoExample enumReferenceExample() {
        record Row(ViewMode mode, int minPx, String semanticClass, String breakpoint) {}

        var rows = List.of(
                new Row(ViewMode.MOBILE,           640,  "vm-mobile",          "Breakpoint.SMALL"),
                new Row(ViewMode.TABLET,           768,  "vm-tablet",          "Breakpoint.MEDIUM"),
                new Row(ViewMode.DESKTOP,         1024,  "vm-desktop",         "Breakpoint.LARGE"),
                new Row(ViewMode.LARGE_DESKTOP,   1280,  "vm-large-desktop",   "Breakpoint.XLARGE"),
                new Row(ViewMode.ULTRA_WIDE,      1536,  "vm-ultra-wide",      "Breakpoint.XXLARGE"),
                new Row(ViewMode.MOBILE_PORTRAIT,   -1,  "vm-mobile-portrait", "Breakpoint.SMALL"),
                new Row(ViewMode.MOBILE_LANDSCAPE,  -1,  "vm-mobile-landscape","Breakpoint.SMALL")
        );

        var layout = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)
                .gridColumns(ViewMode.TABLET,        2)
                .gridColumns(ViewMode.DESKTOP,       3)
                .gridColumns(ViewMode.LARGE_DESKTOP, 4);

        for (var row : rows) {
            var col = ColumnBuilder.create().add(
                    Highlight.builder(row.mode().name(), "\"" + row.mode().getPrefix() + ":\"")
                            .valueFontSize(Font.Size.MEDIUM)
                            .details(modeDetailSpan(rows.indexOf(row) >= 5))
                            .build());
            layout.add(col);
        }

        return new DemoExample("ViewMode Enum Reference — All 7 Values", layout.build(), """
                // ViewMode → CSS prefix → min-width → semantic class
                //
                // MOBILE          → "sm:"  → ≥  640 px → "vm-mobile"
                // TABLET          → "md:"  → ≥  768 px → "vm-tablet"
                // DESKTOP         → "lg:"  → ≥ 1024 px → "vm-desktop"
                // LARGE_DESKTOP   → "xl:"  → ≥ 1280 px → "vm-large-desktop"
                // ULTRA_WIDE      → "2xl:" → ≥ 1536 px → "vm-ultra-wide"
                // MOBILE_PORTRAIT → "sm:"  → posture-aware (portrait phone)
                // MOBILE_LANDSCAPE→ "sm:"  → posture-aware (landscape phone)
                //
                // Get the prefix string:
                ViewMode.DESKTOP.getPrefix();         // "lg"
                ViewMode.ULTRA_WIDE.getPrefix();      // "2xl"
                //
                // Get the Breakpoint enum:
                ViewMode.TABLET.toBreakpoint();       // Breakpoint.MEDIUM
                """);
    }

    private Span modeDetailSpan(boolean postureVariant) {
        var s = new Span();
        s.setText(postureVariant ? "posture-aware · shares \"sm:\" prefix" : "");
        return s;
    }

    // ── Example 2 ────────────────────────────────────────────────────────────

    /**
     * toCssClass(String) — generates a single prefixed class name without touching any component.
     */
    private DemoExample toCssClassExample() {
        var grid = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)
                .gridColumns(ViewMode.TABLET, 2)
                .gridColumns(ViewMode.DESKTOP, 3);

        var inputs = new String[]{"flex-row", "hidden", "grid-cols-3", "gap-x-m", "w-full", "items-center"};

        for (String base : inputs) {
            var col = ColumnBuilder.create().add(
                    Highlight.builder("toCssClass(\"" + base + "\")", "…")
                            .valueFontSize(Font.Size.SMALL)
                            .details(buildPrefixedRow(base))
                            .build());
            grid.add(col);
        }

        return new DemoExample("toCssClass() — Prefix-Driven Class Name Generation", grid.build(), """
                // toCssClass(base) → "<prefix>:<base>"  — no component mutation.
                //
                ViewMode.MOBILE.toCssClass("flex-col");       // "sm:flex-col"
                ViewMode.TABLET.toCssClass("flex-col");       // "md:flex-col"
                ViewMode.DESKTOP.toCssClass("flex-row");      // "lg:flex-row"
                ViewMode.LARGE_DESKTOP.toCssClass("hidden");  // "xl:hidden"
                ViewMode.ULTRA_WIDE.toCssClass("grid-cols-3");// "2xl:grid-cols-3"
                //
                // Idiomatic: generate and add manually
                String cls = ViewMode.DESKTOP.toCssClass("gap-x-m");   // "lg:gap-x-m"
                component.getElement().getClassList().add(cls);
                //
                // Or let applyTo() do the prefixing for you (see next example).
                """);
    }

    private Span buildPrefixedRow(String base) {
        var sb = new StringBuilder();
        for (ViewMode vm : new ViewMode[]{
                ViewMode.MOBILE, ViewMode.TABLET, ViewMode.DESKTOP,
                ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE}) {
            if (!sb.isEmpty()) sb.append("  |  ");
            sb.append(vm.toCssClass(base));
        }
        return new Span(sb.toString());
    }

    // ── Example 3 ────────────────────────────────────────────────────────────

    /**
     * applyTo(component) — semantic tagging only: adds "vm-*" class + data-view-prefix attribute.
     */
    private DemoExample applyToSemanticExample() {
        var wrapper = new Layout();

        for (ViewMode vm : new ViewMode[]{
                ViewMode.MOBILE, ViewMode.TABLET, ViewMode.DESKTOP,
                ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE}) {

            var card = new Layout();
            vm.applyTo(card);

            var name = new H3(vm.name());
            var semanticCls = new Span("." + "vm-" + vm.name().toLowerCase().replace('_', '-'));
            var attr = new Span("[data-view-prefix=\"" + vm.getPrefix() + "\"]");

            card.add(name, semanticCls, attr);
            wrapper.add(card);
        }

        return new DemoExample("applyTo(Component) — Semantic Tagging Only", wrapper, """
                // Adds: semantic CSS class + data-view-prefix attribute. No visual change on its own.
                // CSS can then target both:
                //   .vm-desktop .toolbar { flex-direction: row; }
                //   [data-view-prefix="lg"] .toolbar { column-gap: var(--space-m); }
                //
                ViewMode.MOBILE.applyTo(container);
                // → adds: class="vm-mobile"  data-view-prefix="sm"
                //
                ViewMode.TABLET.applyTo(container);
                // → adds: class="vm-tablet"  data-view-prefix="md"
                //
                ViewMode.DESKTOP.applyTo(container);
                // → adds: class="vm-desktop"  data-view-prefix="lg"
                //
                ViewMode.LARGE_DESKTOP.applyTo(container);
                // → adds: class="vm-large-desktop"  data-view-prefix="xl"
                //
                ViewMode.ULTRA_WIDE.applyTo(container);
                // → adds: class="vm-ultra-wide"  data-view-prefix="2xl"
                """);
    }

    // ── Example 4 ────────────────────────────────────────────────────────────

    /**
     * applyTo(component, String...) — semantic tagging PLUS auto-prefixed utility classes.
     */
    private DemoExample applyToWithUtilitiesExample() {
        var wrapper = new Layout();

        // MOBILE: stacked column
        var mobileCard = makeTaggedCard(
                ViewMode.MOBILE,
                new String[]{"flex-col", "gap-y-s"},
                "sm:flex-col  sm:gap-y-s");

        // TABLET: row with medium gap
        var tabletCard = makeTaggedCard(
                ViewMode.TABLET,
                new String[]{"flex-row", "gap-x-m"},
                "md:flex-row  md:gap-x-m");

        // DESKTOP: row + align-center + large gap
        var desktopCard = makeTaggedCard(
                ViewMode.DESKTOP,
                new String[]{"flex-row", "items-center", "gap-x-m"},
                "lg:flex-row  lg:items-center  lg:gap-x-m");

        // LARGE_DESKTOP: grid 4 cols
        var ldCard = makeTaggedCard(
                ViewMode.LARGE_DESKTOP,
                new String[]{"grid", "grid-cols-4", "gap-l"},
                "xl:grid  xl:grid-cols-4  xl:gap-l");

        // ULTRA_WIDE: grid 6 cols
        var uwCard = makeTaggedCard(
                ViewMode.ULTRA_WIDE,
                new String[]{"grid", "grid-cols-6", "gap-xl"},
                "2xl:grid  2xl:grid-cols-6  2xl:gap-xl");

        wrapper.add(mobileCard, tabletCard, desktopCard, ldCard, uwCard);

        return new DemoExample("applyTo(Component, String...) — Semantic + Utility Classes", wrapper, """
                // Semantic tagging + one prefixed class per base argument — all in one call.
                //
                // MOBILE  → adds: vm-mobile  data-view-prefix="sm"  sm:flex-col  sm:gap-y-s
                ViewMode.MOBILE.applyTo(container, "flex-col", "gap-y-s");
                //
                // TABLET  → adds: vm-tablet  data-view-prefix="md"  md:flex-row  md:gap-x-m
                ViewMode.TABLET.applyTo(container, "flex-row", "gap-x-m");
                //
                // DESKTOP → adds: vm-desktop  data-view-prefix="lg"  lg:flex-row  lg:items-center  lg:gap-x-m
                ViewMode.DESKTOP.applyTo(container, "flex-row", "items-center", "gap-x-m");
                //
                // LARGE_DESKTOP → xl:grid  xl:grid-cols-4  xl:gap-l
                ViewMode.LARGE_DESKTOP.applyTo(container, "grid", "grid-cols-4", "gap-l");
                //
                // ULTRA_WIDE    → 2xl:grid  2xl:grid-cols-6  2xl:gap-xl
                ViewMode.ULTRA_WIDE.applyTo(container, "grid", "grid-cols-6", "gap-xl");
                """);
    }

    private Layout makeTaggedCard(ViewMode mode, String[] utilities, String classList) {
        var card = new Layout();
        mode.applyTo(card, utilities);

        var heading = new H3(mode.name());
        var cls = new Span(classList);

        card.add(heading, cls);
        return card;
    }

    // ── Example 5 ────────────────────────────────────────────────────────────

    /**
     * applyAll() — broadcast the same base class to multiple ViewModes at once.
     */
    private DemoExample applyAllExample() {
        // Show all 5 main ViewModes receiving "flex-row" simultaneously
        var container5 = new Layout();

        ViewMode.applyAll(container5, "flex-row",
                ViewMode.MOBILE, ViewMode.TABLET, ViewMode.DESKTOP,
                ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);

        var labelAll = new Span("sm:flex-row  md:flex-row  lg:flex-row  xl:flex-row  2xl:flex-row");
        container5.add(new H3("All 5 modes"), labelAll);

        // Partial: only desktop + above
        var containerDesktopUp = new Layout();

        ViewMode.applyAll(containerDesktopUp, "hidden",
                ViewMode.MOBILE, ViewMode.TABLET);
        ViewMode.applyAll(containerDesktopUp, "flex",
                ViewMode.DESKTOP, ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);

        var labelPartial = new Span("sm:hidden  md:hidden  lg:flex  xl:flex  2xl:flex");
        containerDesktopUp.add(new H3("Desktop and above"), labelPartial);

        var wrapper = new Layout();
        wrapper.add(container5, containerDesktopUp);

        return new DemoExample("applyAll() — Same Class Across Multiple ViewModes", wrapper, """
                // Adds the same base class for every listed ViewMode in one call.
                // The FIRST mode's semantic class + data attribute are written to the component.
                //
                // All 5 tiers receive "flex-row":
                ViewMode.applyAll(container, "flex-row",
                    ViewMode.MOBILE, ViewMode.TABLET, ViewMode.DESKTOP,
                    ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);
                // → sm:flex-row  md:flex-row  lg:flex-row  xl:flex-row  2xl:flex-row
                //   + vm-mobile  data-view-prefix="sm"   (first mode wins for semantic tag)
                //
                // Show desktop-and-above, hide everything below:
                ViewMode.applyAll(container, "hidden", ViewMode.MOBILE, ViewMode.TABLET);
                ViewMode.applyAll(container, "flex",   ViewMode.DESKTOP, ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);
                // → sm:hidden  md:hidden  lg:flex  xl:flex  2xl:flex
                """);
    }

    // ── Example 6 ────────────────────────────────────────────────────────────

    /**
     * removeApplied() — removes semantic class, data attribute, and all prefixed classes.
     * Interactive: click Apply / Remove to see the HTML attribute panel update.
     */
    private DemoExample removeAppliedExample() {
        var target = new Layout();
        var statusLabel = new Span("Not applied");
        var attrLabel  = new Span("classes: (none)");
        target.add(new H3("Target container"), statusLabel, attrLabel);

        var applyBtn = new Button("applyTo(DESKTOP, \"flex-row\", \"gap-x-m\")");
        var removeBtn = new Button("removeApplied(DESKTOP)");
        removeBtn.setEnabled(false);

        applyBtn.addClickListener(e -> {
            ViewMode.DESKTOP.applyTo(target, "flex-row", "gap-x-m");
            statusLabel.setText("Applied ViewMode.DESKTOP");
            attrLabel.setText("lg:flex-row  lg:gap-x-m  vm-desktop  [data-view-prefix=lg]");
            applyBtn.setEnabled(false);
            removeBtn.setEnabled(true);
        });

        removeBtn.addClickListener(e -> {
            ViewMode.DESKTOP.removeApplied(target);
            statusLabel.setText("Removed — all DESKTOP classes stripped");
            attrLabel.setText("classes: (none)");
            applyBtn.setEnabled(true);
            removeBtn.setEnabled(false);
        });

        var controls = new HorizontalLayout(applyBtn, removeBtn);

        var wrapper = new Div(target, controls);

        return new DemoExample("removeApplied() — Reversible Mode Cleanup", wrapper, """
                // removeApplied() undoes exactly what applyTo() added:
                //   1. Removes the semantic class "vm-desktop"
                //   2. Removes data-view-prefix (only if it matches "lg")
                //   3. Removes all classes starting with "lg:"
                //
                ViewMode.DESKTOP.applyTo(container, "flex-row", "gap-x-m");
                // → adds: vm-desktop  data-view-prefix="lg"  lg:flex-row  lg:gap-x-m
                //
                ViewMode.DESKTOP.removeApplied(container);
                // → removes all of the above; other classes are untouched
                //
                // Useful for runtime mode switching:
                ViewMode oldMode = currentMode;
                oldMode.removeApplied(container);
                newMode.applyTo(container, "flex-row", "gap-x-m");
                """);
    }

    // ── Example 7 ────────────────────────────────────────────────────────────

    /**
     * Reverse lookups: fromPrefix() and fromBreakpoint().
     */
    private DemoExample reverseLookupExample() {
        var layout = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)
                .gridColumns(ViewMode.TABLET, 2)
                .gridColumns(ViewMode.DESKTOP, 3);

        record Lookup(String method, String input, String result) {}
        var lookups = List.of(
                new Lookup("fromPrefix(\"sm\")",             "\"sm\"",             "ViewMode.MOBILE"),
                new Lookup("fromPrefix(\"md\")",             "\"md\"",             "ViewMode.TABLET"),
                new Lookup("fromPrefix(\"lg\")",             "\"lg\"",             "ViewMode.DESKTOP"),
                new Lookup("fromPrefix(\"xl\")",             "\"xl\"",             "ViewMode.LARGE_DESKTOP"),
                new Lookup("fromPrefix(\"2xl\")",            "\"2xl\"",            "ViewMode.ULTRA_WIDE"),
                new Lookup("fromBreakpoint(SMALL)",          "Breakpoint.SMALL",   "ViewMode.MOBILE"),
                new Lookup("fromBreakpoint(MEDIUM)",         "Breakpoint.MEDIUM",  "ViewMode.TABLET"),
                new Lookup("fromBreakpoint(LARGE)",          "Breakpoint.LARGE",   "ViewMode.DESKTOP"),
                new Lookup("fromBreakpoint(XLARGE)",         "Breakpoint.XLARGE",  "ViewMode.LARGE_DESKTOP"),
                new Lookup("fromBreakpoint(XXLARGE)",        "Breakpoint.XXLARGE", "ViewMode.ULTRA_WIDE")
        );

        for (var l : lookups) {
            layout.add(ColumnBuilder.create().add(
                    Highlight.builder(l.method(), l.result())
                            .valueFontSize(Font.Size.SMALL)
                            .details(new Span("← " + l.input()))
                            .build()));
        }

        return new DemoExample("fromPrefix() / fromBreakpoint() — Reverse Lookups", layout.build(), """
                // Reverse-map a CSS prefix string back to a ViewMode enum constant:
                ViewMode vm = ViewMode.fromPrefix("md");     // ViewMode.TABLET
                ViewMode vm = ViewMode.fromPrefix("lg");     // ViewMode.DESKTOP
                ViewMode vm = ViewMode.fromPrefix("2xl");    // ViewMode.ULTRA_WIDE
                // Throws IllegalArgumentException for unknown prefix.
                //
                // Reverse-map a Breakpoint enum back to ViewMode:
                ViewMode vm = ViewMode.fromBreakpoint(Breakpoint.SMALL);   // MOBILE
                ViewMode vm = ViewMode.fromBreakpoint(Breakpoint.MEDIUM);  // TABLET
                ViewMode vm = ViewMode.fromBreakpoint(Breakpoint.LARGE);   // DESKTOP
                ViewMode vm = ViewMode.fromBreakpoint(Breakpoint.XLARGE);  // LARGE_DESKTOP
                ViewMode vm = ViewMode.fromBreakpoint(Breakpoint.XXLARGE); // ULTRA_WIDE
                //
                // Round-trip example:
                String prefix = ViewMode.DESKTOP.getPrefix();          // "lg"
                ViewMode round = ViewMode.fromPrefix(prefix);          // DESKTOP
                Breakpoint bp  = round.toBreakpoint();                 // Breakpoint.LARGE
                ViewMode back  = ViewMode.fromBreakpoint(bp);          // DESKTOP
                """);
    }

    // ── Example 8 ────────────────────────────────────────────────────────────

    /**
     * Posture-aware variants: MOBILE_PORTRAIT and MOBILE_LANDSCAPE.
     * Both share the "sm" prefix but represent distinct posture states.
     */
    private DemoExample postureVariantsExample() {
        var portraitCard = new Layout();
        ViewMode.MOBILE_PORTRAIT.applyTo(portraitCard, "flex-col", "gap-y-s");
        var pLabel = new Span("vm-mobile-portrait  data-view-prefix=\"sm\"  sm:flex-col  sm:gap-y-s");
        portraitCard.add(new H3("MOBILE_PORTRAIT"), pLabel,
                new Paragraph("Best for single-column stacked layouts (phone held upright)."));

        var landscapeCard = new Layout();
        ViewMode.MOBILE_LANDSCAPE.applyTo(landscapeCard, "flex-row", "gap-x-s");
        var lLabel = new Span("vm-mobile-landscape  data-view-prefix=\"sm\"  sm:flex-row  sm:gap-x-s");
        landscapeCard.add(new H3("MOBILE_LANDSCAPE"), lLabel,
                new Paragraph("Best for two-column side-by-side layouts (phone held sideways)."));

        var diffNote = Alert.builder(Alert.Variant.INFO)
                .title("Shared \"sm:\" prefix — semantic class is the differentiator")
                .description(
                    "Both posture variants share the \"sm\" prefix, so their responsive CSS classes are identical. "
                    + "The semantic class (vm-mobile-portrait vs vm-mobile-landscape) lets you style "
                    + "differently per posture via CSS selectors. "
                    + "Use Responsive.apply() to auto-detect and apply the correct posture tag at runtime.")
                .build();

        var wrapper = new Layout();
        wrapper.add(portraitCard, landscapeCard, diffNote);

        return new DemoExample("Posture Variants — MOBILE_PORTRAIT / MOBILE_LANDSCAPE", wrapper, """
                // Both posture variants map to "sm" prefix — same responsive breakpoint.
                // They differ only in semantic class, enabling per-posture CSS targeting.
                //
                // Portrait — single column:
                ViewMode.MOBILE_PORTRAIT.applyTo(container, "flex-col", "gap-y-s");
                // → vm-mobile-portrait  data-view-prefix="sm"  sm:flex-col  sm:gap-y-s
                //
                // Landscape — horizontal row:
                ViewMode.MOBILE_LANDSCAPE.applyTo(container, "flex-row", "gap-x-s");
                // → vm-mobile-landscape  data-view-prefix="sm"  sm:flex-row  sm:gap-x-s
                //
                // CSS targeting by posture:
                //   .vm-mobile-portrait .sidebar  { display: none; }
                //   .vm-mobile-landscape .sidebar { display: flex; width: 30%; }
                //
                // Auto-detect posture at runtime via Responsive.apply():
                //   Responsive.apply(root); // auto-tags with correct posture + mode
                //
                // toCssClass() works identically for both:
                ViewMode.MOBILE_PORTRAIT.toCssClass("flex-col");   // "sm:flex-col"
                ViewMode.MOBILE_LANDSCAPE.toCssClass("flex-col");  // "sm:flex-col"
                //
                // Distinguish posture in code:
                boolean portrait = mode == ViewMode.MOBILE_PORTRAIT;
                """);
    }

    // ── Example 9 ────────────────────────────────────────────────────────────

    /**
     * Three flavours of stack-mobile / row-desktop using the direct ViewMode API.
     */
    private DemoExample stackRowPatternExample() {
        // ① applyTo per breakpoint — explicit
        var container1 = new Layout();
        container1.add(card("Panel A"), card("Panel B"), card("Panel C"));
        ViewMode.MOBILE.applyTo(container1, "flex-col", "gap-y-s");
        ViewMode.TABLET.applyTo(container1, "flex-row", "gap-x-m");
        ViewMode.DESKTOP.applyTo(container1, "flex-row", "gap-x-m");
        var label1 = new Span("① applyTo() per breakpoint — explicit");

        // ② applyAll() for shared class — compact
        var container2 = new Layout();
        container2.add(card("Alpha"), card("Beta"), card("Gamma"));
        ViewMode.applyAll(container2, "flex-col", ViewMode.MOBILE);
        ViewMode.applyAll(container2, "flex-row",
                ViewMode.TABLET, ViewMode.DESKTOP, ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);
        var label2 = new Span("② applyAll() — stack mobile, row tablet+");

        // ③ Column-count grid variant
        var container3 = new Layout();
        container3.add(card("X"), card("Y"), card("Z"), card("W"));
        ViewMode.MOBILE.applyTo(container3, "grid", "grid-cols-1");
        ViewMode.TABLET.applyTo(container3, "grid", "grid-cols-2");
        ViewMode.DESKTOP.applyTo(container3, "grid", "grid-cols-4");
        var label3 = new Span("③ applyTo() for grid columns — 1→2→4 per breakpoint");

        var wrapper = new Layout();
        wrapper.add(label1, container1, label2, container2, label3, container3);

        return new DemoExample(
                "Stack ↔ Row Pattern — Three Direct ViewMode Styles (resize to see)", wrapper, """
                // Three ways to express responsive layout using ViewMode directly:
                //
                // ①  Per-breakpoint applyTo() — maximum explicit control:
                ViewMode.MOBILE.applyTo(container, "flex-col", "gap-y-s");
                ViewMode.TABLET.applyTo(container, "flex-row", "gap-x-m");
                ViewMode.DESKTOP.applyTo(container, "flex-row", "gap-x-m");
                //
                // ②  applyAll() — same class at multiple breakpoints in one call:
                ViewMode.applyAll(container, "flex-col", ViewMode.MOBILE);
                ViewMode.applyAll(container, "flex-row",
                    ViewMode.TABLET, ViewMode.DESKTOP, ViewMode.LARGE_DESKTOP, ViewMode.ULTRA_WIDE);
                //
                // ③  Grid columns — 1-col mobile → 2-col tablet → 4-col desktop:
                ViewMode.MOBILE.applyTo(container, "grid", "grid-cols-1");
                ViewMode.TABLET.applyTo(container, "grid", "grid-cols-2");
                ViewMode.DESKTOP.applyTo(container, "grid", "grid-cols-4");
                """);
    }

    // ── Example 10 ───────────────────────────────────────────────────────────

    /**
     * Full 5-breakpoint responsive grid via ColumnBuilder.at(ViewMode, int).
     * Demonstrates that ViewMode is the lingua franca shared by ColumnBuilder as well.
     */
    private DemoExample fullBreakpointGridExample() {
        var kpis = new String[][]{
                {"Revenue", "$284K", "↑ 12%"},
                {"Orders",  "3,842", "↑ 5%"},
                {"Users",   "18.2K", "↑ 4%"},
                {"Returns",  "2.1%", "↓ 1%"},
                {"Rating",  "4.8 ★", "↑ 0%"},
                {"NPS",      "72",   "↑ 3%"},
                {"Churn",   "1.4%",  "↓ 2%"},
                {"CSAT",    "93%",   "↑ 1%"},
                {"LTV",     "$840",  "↑ 8%"},
                {"CAC",      "$32",  "↓ 5%"},
        };

        var row = RowBuilder.create().styleName("gap-m")
                .gridColumns(1)                           // base: 1 column
                .gridColumns(ViewMode.MOBILE,       2)   // sm: 2 columns
                .gridColumns(ViewMode.TABLET,       3)   // md: 3 columns
                .gridColumns(ViewMode.DESKTOP,      5)   // lg: 5 columns
                .gridColumns(ViewMode.LARGE_DESKTOP,5)   // xl: 5 columns
                .gridColumns(ViewMode.ULTRA_WIDE,  10);  // 2xl: all 10 in one row

        for (var kpi : kpis) {
            row.add(ColumnBuilder.create().add(
                    Highlight.builder(kpi[0], kpi[1])
                            .valueFontSize(Font.Size.XLARGE)
                            .details(trend(kpi[2]))
                            .build()));
        }

        return new DemoExample(
                "Full 5-Breakpoint Grid — ColumnBuilder.at(ViewMode, int) (resize to see)", row.build(), """
                // ViewMode feeds directly into ColumnBuilder / RowBuilder for responsive grids.
                //
                // RowBuilder.gridColumns(ViewMode, int) — N equal columns at a breakpoint:
                RowBuilder.create()
                    .gridColumns(1)                           // base: 1 column (< 640 px)
                    .gridColumns(ViewMode.MOBILE,        2)  // sm:grid-cols-2   ≥  640 px
                    .gridColumns(ViewMode.TABLET,        3)  // md:grid-cols-3   ≥  768 px
                    .gridColumns(ViewMode.DESKTOP,       5)  // lg:grid-cols-5   ≥ 1024 px
                    .gridColumns(ViewMode.LARGE_DESKTOP, 5)  // xl:grid-cols-5   ≥ 1280 px
                    .gridColumns(ViewMode.ULTRA_WIDE,   10)  // 2xl:grid-cols-10 ≥ 1536 px
                    .add(ColumnBuilder.create().add(card), …)
                    .build();
                //
                // ColumnBuilder.at(ViewMode, int) — "N items per row" (span = 12/N):
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)               // base: full-width
                    .at(ViewMode.TABLET,        2)      // md:col-span-6  — 2 per row
                    .at(ViewMode.DESKTOP,       3)      // lg:col-span-4  — 3 per row
                    .at(ViewMode.LARGE_DESKTOP, 4)      // xl:col-span-3  — 4 per row
                    .at(ViewMode.ULTRA_WIDE,    6)      // 2xl:col-span-2 — 6 per row
                    .add(card);
                //
                // ColumnBuilder.at(ViewMode, ColSpan) — exact span for asymmetric layouts:
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.DESKTOP, ColSpan.COL_8)   // lg:col-span-8  main content
                    .add(mainCard);
                ColumnBuilder.create()
                    .span(ColSpan.COL_12)
                    .at(ViewMode.DESKTOP, ColSpan.COL_4)   // lg:col-span-4  sidebar
                    .add(sidebarCard);
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Span card(String text) {
        var s = new Span(text);
        return s;
    }

    private Span trend(String text) {
        var s = new Span(text);
        if (text.startsWith("↑"))      s.addClassName("demo-highlight-detail--positive");
        else if (text.startsWith("↓")) s.addClassName("demo-highlight-detail--negative");
        else                           s.addClassName("demo-highlight-detail");
        return s;
    }
}






