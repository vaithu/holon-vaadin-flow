package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link com.iyensoft.vaadin.flow.components.IyenPanel} accessed via
 * the {@link PanelBuilder} fluent API.
 *
 * <p>{@code PanelBuilder} wraps {@code IyenPanel} — a {@link com.holonplatform.vaadin.flow.vaadinplus.Layout}
 * subclass — and maps type-safe Java enum values to CSS utility class names.
 * The result is a Tailwind-like declarative layout API that produces zero inline styles.
 *
 * <ol>
 *   <li>Vertical stack — {@code vertical()} + {@code gap()} + {@code padding()}</li>
 *   <li>Horizontal row — {@code horizontal()} + {@code alignItems(CENTER)} + {@code gap(SMALL)}</li>
 *   <li>Grow / expand — {@code addAndExpand()} for sidebar + expanding content</li>
 *   <li>flexWrap — {@code flexWrap(WRAP)} for responsive chip / tag clouds</li>
 *   <li>CSS Grid + columnSpan — {@code display(GRID)} + {@code columns()} + {@code columnSpan()}</li>
 *   <li>Responsive Grid — column count adapts across {@code ViewMode} breakpoints</li>
 *   <li>JustifyContent — all six alignment values side by side</li>
 *   <li>Overflow HIDDEN — clipped content in a fixed-height container</li>
 *   <li>Content card — {@code background(Color.Background)} + {@code padding()} as a styled card</li>
 *   <li>Scrollable panel — {@code scrollable()} + {@code maxHeight()} for auto-scroll containers</li>
 *   <li>Center pattern — {@code center()} for empty states and hero sections</li>
 * </ol>
 */
@PageTitle("Panel – Holon Demo")
@Route(value = "panel", layout = DemoMainLayout.class)
public class PanelDemoView extends Div {

    public PanelDemoView() {
        addClassName("app-view");

        var title = new H1("Panel / Layout");

        var desc = new Paragraph(
                "PanelBuilder wraps IyenPanel — a Layout-based container — with a fluent builder API " +
                "that maps Java enum values directly to CSS utility class names. " +
                "It provides declarative access to flex layout (direction, alignment, wrapping, grow), " +
                "CSS grid (columns, column-span), gap, overflow, semantic colour, and responsive breakpoints " +
                "— all without a single inline style or magic string. " +
                "Convenience shortcuts (center(), scrollable(), wrap(), background(), textColor()) " +
                "cover the most common enterprise UX patterns out of the box.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(verticalStackExample());
        examples.add(horizontalRowExample());
        examples.add(growExpandExample());
        examples.add(flexWrapExample());
        examples.add(gridWithColumnSpanExample());
        examples.add(responsiveGridExample());
        examples.add(justifyContentExample());
        examples.add(overflowExample());
        examples.add(contentCardExample());
        examples.add(scrollableExample());
        examples.add(centerExample());

        add(title, desc, examples);
    }

    // ── Example 1 ─────────────────────────────────────────────────────────────

    /**
     * Vertical stack: the most common UI pattern — stacking form fields, content
     * sections, or list items top-to-bottom with controlled spacing.
     */
    private DemoExample verticalStackExample() {
        var panel = PanelBuilder.create()
                .vertical()          // flexDirection(FlexDirection.COLUMN)
                .gap(Gap.MEDIUM)
                .padding()           // adds "padding-medium" utility class
                .add(cell("First item"), cell("Second item"), cell("Third item"))
                .build();

        return new DemoExample("Vertical stack — vertical() + gap(MEDIUM) + padding()", panel, """
                // vertical() = flexDirection(FlexDirection.COLUMN)
                // padding()  = styleName("padding-medium")
                // spacing()  = gap(Gap.MEDIUM)  — a convenient alias for the same effect
                PanelBuilder.create()
                    .vertical()
                    .gap(Gap.MEDIUM)
                    .padding()
                    .add(first, second, third)
                    .build();
                """);
    }

    // ── Example 2 ─────────────────────────────────────────────────────────────

    /**
     * Horizontal row: toolbars, breadcrumbs, icon+label pairs, action bars.
     * {@code alignItems(CENTER)} keeps children vertically centred in the row
     * regardless of their individual heights.
     */
    private DemoExample horizontalRowExample() {
        var panel = PanelBuilder.create()
                .horizontal()                        // flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .gap(Gap.SMALL)
                .add(cell("🔍 Icon"), cell("Label text"), cell("→ Badge"))
                .build();

        return new DemoExample("Horizontal row — horizontal() + alignItems(CENTER) + gap(SMALL)", panel, """
                // horizontal() = flexDirection(FlexDirection.ROW)
                // AlignItems.CENTER cross-axis-centres all children in the row — mixing
                // components of different heights remains visually balanced.
                PanelBuilder.create()
                    .horizontal()
                    .alignItems(AlignItems.CENTER)
                    .gap(Gap.SMALL)
                    .add(icon, label, badge)
                    .build();
                """);
    }

    // ── Example 3 ─────────────────────────────────────────────────────────────

    /**
     * addAndExpand: the canonical sidebar + main content pattern.
     * The expanded child receives {@code flex-grow:1} and absorbs all remaining space
     * while the sidebar keeps its natural width.
     */
    private DemoExample growExpandExample() {
        var sidebar = cell("Sidebar (fixed)");
        var main    = cell("Main content — this pane expands to fill all remaining width");

        var panel = PanelBuilder.create()
                .horizontal()
                .gap(Gap.MEDIUM)
                .fullWidth()               // fullWidth() is in the builder chain — no setWidth() needed
                .add(sidebar)
                .addAndExpand(main)        // add(main) + flexGrow(main)
                .build();

        return new DemoExample("addAndExpand — sidebar + expanding main content", panel, """
                // addAndExpand(component) = add(component) + flexGrow(component)
                // The expanded child receives flex-grow:1 and fills all remaining space.
                // fullWidth() is already in the builder chain — no need to call setWidth() after build.
                var sidebar = new Div("Sidebar");
                var main    = new Div("Main content");
                PanelBuilder.create()
                    .horizontal()
                    .gap(Gap.MEDIUM)
                    .fullWidth()
                    .add(sidebar)
                    .addAndExpand(main)     // main gets flex: 1 1 0
                    .build();
                """);
    }

    // ── Example 4 ─────────────────────────────────────────────────────────────

    /**
     * flexWrap: chips and tags that wrap onto new lines automatically.
     * The children define their own widths; the container handles the reflow.
     * Perfect for skill lists, filter chips, and badge groups.
     */
    private DemoExample flexWrapExample() {
        var panel = PanelBuilder.create()
                .horizontal()
                .flexWrap(FlexWrap.WRAP)
                .gap(Gap.XSMALL)
                .add(
                        chip("Java 21"),       chip("Spring Boot 4"), chip("Vaadin 25"),
                        chip("JPA"),           chip("REST API"),       chip("Docker"),
                        chip("Redis"),         chip("Kafka"),          chip("PostgreSQL"),
                        chip("OAuth2"),        chip("gRPC"),           chip("OpenAPI")
                )
                .build();

        return new DemoExample("flexWrap(WRAP) — responsive tag / chip cloud", panel, """
                // horizontal() → display:flex; flex-direction:row
                // flexWrap(WRAP) allows children to break onto new lines when the row is full.
                // UX use cases: skill tags, technology badges, filter chip rows.
                PanelBuilder.create()
                    .horizontal()
                    .flexWrap(FlexWrap.WRAP)
                    .gap(Gap.XSMALL)
                    .add(chip1, chip2, chip3, chip4, ...)
                    .build();
                """);
    }

    // ── Example 5 ──────────────────────────────────────────��──────────────────

    /**
     * CSS Grid with a featured item that spans multiple columns.
     * Demonstrates the {@code display(GRID)} + {@code columns()} + {@code columnSpan()} triad
     * — ideal for dashboard cards where one item deserves more prominence.
     */
    private DemoExample gridWithColumnSpanExample() {
        var featured = cell("★ Featured — spans 2 of 3 columns");
        var itemA    = cell("Card A");
        var itemB    = cell("Card B");
        var itemC    = cell("Card C");
        var itemD    = cell("Card D");

        var panel = PanelBuilder.create()
                .display(Display.GRID)
                .columns(GridColumns.COLUMNS_3)
                .gap(Gap.MEDIUM)
                .fullWidth()
                .add(featured, itemA, itemB, itemC, itemD)
                .columnSpan(ColumnSpan.COLUMN_SPAN_2, featured)
                .build();

        return new DemoExample("CSS Grid — columns(COLUMNS_3) + columnSpan(COLUMN_SPAN_2, featured)", panel, """
                // display(Display.GRID)               → adds "grid" CSS class
                // columns(GridColumns.COLUMNS_3)      → "grid-cols-3" (COLUMNS_1 … COLUMNS_12 available)
                // columnSpan(COLUMN_SPAN_2, featured) → "col-span-2" applied to the featured child
                // UX pattern: dashboard with a hero card spanning 2 columns + smaller side cards.
                var featured = new Div("Featured");
                PanelBuilder.create()
                    .display(Display.GRID)
                    .columns(GridColumns.COLUMNS_3)
                    .gap(Gap.MEDIUM)
                    .add(featured, itemA, itemB, itemC, itemD)
                    .columnSpan(ColumnSpan.COLUMN_SPAN_2, featured)
                    .build();
                """);
    }

    // ── Example 6 ─────────────────────────────────────────────────────────────

    /**
     * Responsive grid: the column count adapts as the viewport grows.
     * Uses {@code ViewMode} semantic breakpoint names (TABLET, DESKTOP) instead
     * of raw Breakpoint enum values for improved readability.
     * Resize the browser window to see the 1 → 2 → 3 column reflow.
     */
    private DemoExample responsiveGridExample() {
        var panel = PanelBuilder.create()
                .display(Display.GRID)
                .columns(GridColumns.COLUMNS_1)                       // mobile-first: 1 col
                .columns(ViewMode.TABLET,  GridColumns.COLUMNS_2)     // ≥ md  → 2 cols
                .columns(ViewMode.DESKTOP, GridColumns.COLUMNS_3)     // ≥ lg  → 3 cols
                .gap(Gap.MEDIUM)
                .fullWidth()
                .add(
                        cell("Card 1"), cell("Card 2"), cell("Card 3"),
                        cell("Card 4"), cell("Card 5"), cell("Card 6")
                )
                .build();

        return new DemoExample("Responsive Grid — 1 → 2 → 3 columns via ViewMode breakpoints", panel, """
                // columns(ViewMode, GridColumns) emits breakpoint-prefixed CSS grid classes.
                // ViewMode.TABLET  = Breakpoint.MEDIUM ("md:grid-cols-2")
                // ViewMode.DESKTOP = Breakpoint.LARGE  ("lg:grid-cols-3")
                // Resize the viewport to see the 1 → 2 → 3 column reflow live.
                PanelBuilder.create()
                    .display(Display.GRID)
                    .columns(GridColumns.COLUMNS_1)                       // default: 1 col
                    .columns(ViewMode.TABLET,  GridColumns.COLUMNS_2)     // md+: 2 cols
                    .columns(ViewMode.DESKTOP, GridColumns.COLUMNS_3)     // lg+: 3 cols
                    .gap(Gap.MEDIUM)
                    .add(card1, card2, card3, card4, card5, card6)
                    .build();
                """);
    }

    // ── Example 7 ─────────────────────────────────────────────────────────────

    /**
     * JustifyContent: all six values rendered side by side.
     * Each row holds the same three fixed-width cells; only the distribution rule changes.
     * This makes it immediately obvious how each value distributes leftover horizontal space.
     */
    private DemoExample justifyContentExample() {
        var container = new Div();

        for (var jc : JustifyContent.values()) {
            var row = PanelBuilder.create()
                    .horizontal()
                    .justifyContent(jc)
                    .gap(Gap.SMALL)
                    .fullWidth()
                    .add(cell("A"), cell("B"), cell("C"))
                    .build();

            var label = new Span("JustifyContent." + jc.name());

            var group = new Div(label, row);
            container.add(group);
        }

        return new DemoExample(
                "JustifyContent — START · END · CENTER · BETWEEN · AROUND · EVENLY", container, """
                // justifyContent controls distribution of children along the main axis.
                //   START   – children packed at the start
                //   END     – packed at the end
                //   CENTER  – centred in the container
                //   BETWEEN – equal gap between children, none at edges
                //   AROUND  – equal space around each child (half-gap at edges)
                //   EVENLY  – equal space between children AND at both edges
                for (JustifyContent jc : JustifyContent.values()) {
                    PanelBuilder.create()
                        .horizontal()
                        .justifyContent(jc)
                        .add(cellA, cellB, cellC)
                        .build();
                }
                """);
    }

    // ── Example 8 ─────────────────────────────────────────────────────────────

    /**
     * Overflow HIDDEN: content that overflows a constrained height is silently clipped.
     * Useful for fixed-height card previews, collapsed panels, and truncated sidebars.
     */
    private DemoExample overflowExample() {
        var panel = PanelBuilder.create()
                .vertical()
                .gap(Gap.SMALL)
                .overflow(Overflow.HIDDEN)
                .fullWidth()
                .add(
                        cell("Row 1 — visible"),
                        cell("Row 2 — visible"),
                        cell("Row 3 — visible"),
                        cell("Row 4 — clipped by overflow:hidden"),
                        cell("Row 5 — clipped by overflow:hidden")
                )
                .build();
        panel.setHeight("140px");

        return new DemoExample(
                "overflow(HIDDEN) — content clipped inside a fixed-height panel", panel, """
                // overflow(Overflow.HIDDEN) clips any child that exceeds the panel's bounds.
                // Set the height via setHeight() / setMaxHeight() to define the clip boundary.
                // UX use cases: collapsed preview cards, fixed sidebar bodies, truncated bios.
                var panel = PanelBuilder.create()
                    .vertical()
                    .gap(Gap.SMALL)
                    .overflow(Overflow.HIDDEN)
                    .add(row1, row2, row3, row4, row5)
                    .build();
                panel.setHeight("140px");   // triggers the clipping effect
                """);
    }

    // ── Example 9 ─────────────────────────────────────────────────────────────

    /**
     * Content card: Panel + semantic background + padding = styled card with zero custom CSS.
     * Covers 90 % of enterprise card requirements — info panels, KPI tiles, alert boxes —
     * using only theme-aware colour tokens from {@link Color.Background}.
     * Different backgrounds also illustrate success / warning / error states.
     */
    private DemoExample contentCardExample() {
        var container = new Div();

        // Neutral surface tint — default card
        container.add(
                PanelBuilder.create()
                        .vertical()
                        .gap(Gap.SMALL)
                        .padding()
                        .background(Color.Background.CONTRAST_5)
                        .add(cell("Card title"), cell("Supporting description"), cell("Action area"))
                        .build()
        );

        // Success state
        container.add(
                PanelBuilder.create()
                        .horizontal()
                        .alignItems(AlignItems.CENTER)
                        .gap(Gap.SMALL)
                        .padding()
                        .background(Color.Background.SUCCESS_10)
                        .add(cell("✓ Deployment succeeded"), cell("All 12 checks passed"))
                        .build()
        );

        // Error state
        container.add(
                PanelBuilder.create()
                        .horizontal()
                        .alignItems(AlignItems.CENTER)
                        .gap(Gap.SMALL)
                        .padding()
                        .background(Color.Background.ERROR_10)
                        .add(cell("✕ Build failed"), cell("3 errors in module core"))
                        .build()
        );

        // Primary accent — feature highlight
        container.add(
                PanelBuilder.create()
                        .horizontal()
                        .alignItems(AlignItems.CENTER)
                        .gap(Gap.SMALL)
                        .padding()
                        .background(Color.Background.PRIMARY_10)
                        .add(cell("★ New feature available"), cell("Upgrade to unlock advanced filters"))
                        .build()
        );

        return new DemoExample(
                "Semantic background — content card, success, error, and primary accent", container, """
                // background(Color.Background) applies a theme-aware colour token class.
                // Combined with padding() and vertical()/horizontal(), PanelBuilder replaces
                // custom CSS card styling for the majority of enterprise UX patterns.

                // Neutral surface tint:
                PanelBuilder.create()
                    .vertical().gap(Gap.SMALL).padding()
                    .background(Color.Background.CONTRAST_5)
                    .add(title, description, actions)
                    .build();

                // Success / error / primary accent — same API, different token:
                PanelBuilder.create()
                    .horizontal().alignItems(AlignItems.CENTER).gap(Gap.SMALL).padding()
                    .background(Color.Background.SUCCESS_10)   // or ERROR_10, PRIMARY_10 …
                    .add(icon, message)
                    .build();
                """);
    }

    // ── Example 10 ────────────────────────────────────────────────────────────

    /**
     * Scrollable panel: auto-scroll when content overflows a constrained height.
     * The {@code scrollable()} shortcut is the idiomatic way to build scrollable
     * sidebars, notification feeds, audit log panels, and dialog bodies.
     */
    private DemoExample scrollableExample() {
        var panel = PanelBuilder.create()
                .vertical()
                .gap(Gap.SMALL)
                .scrollable()              // overflow(Overflow.AUTO)
                .maxHeight("200px")
                .fullWidth()
                .add(
                        cell("Item 1"), cell("Item 2"), cell("Item 3"),
                        cell("Item 4 — scroll down to see"), cell("Item 5 — scroll down to see"),
                        cell("Item 6 — scroll down to see"), cell("Item 7 — scroll down to see")
                )
                .build();

        return new DemoExample("scrollable() — auto-scroll inside a maxHeight container", panel, """
                // scrollable() = overflow(Overflow.AUTO)
                // Pair with maxHeight() to create a scroll boundary within the builder chain.
                // Use cases: notification feeds, audit logs, scrollable sidebars, dialog bodies.
                PanelBuilder.create()
                    .vertical()
                    .gap(Gap.SMALL)
                    .scrollable()           // overflow(Overflow.AUTO)
                    .maxHeight("200px")     // defines the scroll boundary — no setMaxHeight() needed
                    .fullWidth()
                    .add(item1, item2, item3, item4, item5, item6, item7)
                    .build();
                """);
    }

    // ── Example 11 ────────────────────────────────────────────────────────────

    /**
     * Center pattern: the most common positioning shortcut — both axes centred.
     * Works for empty states, loading spinners, hero sections, and placeholder content.
     * {@code center()} is {@code alignItems(CENTER).justifyContent(CENTER)} in one call.
     */
    private DemoExample centerExample() {
        var container = new Div();

        // Empty state — no results
        container.add(
                PanelBuilder.create()
                        .center()
                        .vertical()
                        .gap(Gap.SMALL)
                        .fullWidth()
                        .height("160px")
                        .background(Color.Background.CONTRAST_5)
                        .add(cell("📭  No results found"), cell("Adjust your filters and try again"))
                        .build()
        );

        // Hero / feature spotlight — primary tinted
        container.add(
                PanelBuilder.create()
                        .center()
                        .vertical()
                        .gap(Gap.SMALL)
                        .fullWidth()
                        .height("160px")
                        .background(Color.Background.PRIMARY_10)
                        .add(cell("🚀  Ready to launch?"), cell("Your workspace is configured and ready"))
                        .build()
        );

        return new DemoExample(
                "center() — empty state and hero section centering", container, """
                // center() = alignItems(AlignItems.CENTER).justifyContent(JustifyContent.CENTER)
                // Combine with height() and background() to create full empty-state containers.
                // Use cases: empty grids, loading placeholders, hero banners, modal splash screens.
                PanelBuilder.create()
                    .center()
                    .vertical()
                    .gap(Gap.SMALL)
                    .fullWidth()
                    .height("160px")
                    .background(Color.Background.CONTRAST_5)
                    .add(emptyIcon, emptyMessage)
                    .build();
                """);
    }

    // ── Content helpers ───────────────────────────────────────────────────────

    /**
     * Bordered placeholder box — represents a flex or grid child in the demo.
     */
    private static Div cell(String text) {
        var d = new Div(new Span(text));
        return d;
    }

    /**
     * Inline pill chip — represents a tag or badge in the flexWrap example.
     */
    private static Div chip(String label) {
        var d = new Div(new Span(label));
        return d;
    }
}











