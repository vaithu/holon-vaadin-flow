package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Chip;
import com.holonplatform.vaadin.flow.vaadinplus.components.ChipGroup;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for {@link Chip} and {@link ChipGroup}.
 *
 * <p>Covers:
 * <ol>
 *   <li>ChipGroup — mutually exclusive toggle (the canonical filter-tab pattern)</li>
 *   <li>Chip with count badge — "All 2418 / Open 14" pattern from the design</li>
 *   <li>Standalone chips — active / inactive / disabled states</li>
 *   <li>Size variants — default, SM, LG</li>
 *   <li>Wrapping group</li>
 *   <li>ButtonConfigurator.chip() — vaadin-button styled as a chip</li>
 * </ol>
 */
@PageTitle("Chip – Holon Demo")
@Route(value = "chip", layout = DemoMainLayout.class)
public class ChipDemoView extends Div {

    public ChipDemoView() {
        addClassName("app-view");
        add(new H1("Chip & ChipGroup"));
        add(new Paragraph(
                "Interactive pill-shaped filter chips. " +
                "Chip is a native <button> with full CSS control. " +
                "ChipGroup manages a set of mutually exclusive chips and fires a SelectionEvent " +
                "whenever the active chip changes."));

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(
                chipGroupWithCountsExample(),
                chipGroupSimpleExample(),
                standaloneStatesExample(),
                sizesExample(),
                wrappingGroupExample(),
                buttonBuilderChipExample()
        );
        add(examples);
    }

    // ── Example 1: ChipGroup with count badges — the canonical design ────

    private DemoExample chipGroupWithCountsExample() {
        var selectionLabel = new Span("Active filter: All");

        var group = Components.chipGroup()
                .addChip(Components.chip("All",       2418), true)
                .addChip(Components.chip("Open",        14))
                .addChip(Components.chip("Posted",    2376))
                .addChip(Components.chip("QC Issues",    3))
                .onSelect(e -> selectionLabel.setText("Active filter: " + e.getLabel()))
                .build();

        var container = new Div(group, selectionLabel);
        container.addClassNames("d-flex", "flex-column", "gap-2");

        return new DemoExample("ChipGroup with count badges (filter-tab pattern)", container, """
                // The canonical design: each chip carries a count badge.
                // ChipGroup ensures only one chip is active at a time.
                Components.chipGroup()
                    .addChip(Components.chip("All",       2418), true)  // active on load
                    .addChip(Components.chip("Open",        14))
                    .addChip(Components.chip("Posted",    2376))
                    .addChip(Components.chip("QC Issues",    3))
                    .onSelect(e -> applyFilter(e.getLabel()))
                    .build();

                // e.getChip()  — the activated Chip instance
                // e.getLabel() — shortcut to e.getChip().getLabel()
                """);
    }

    // ── Example 2: ChipGroup label-only ──────────────────────────────────

    private DemoExample chipGroupSimpleExample() {
        var resultLabel = new Span("Selected: Daily");

        var group = Components.chipGroup()
                .addChip("Daily",   true)
                .addChip("Weekly")
                .addChip("Monthly")
                .addChip("Yearly")
                .onSelect(e -> resultLabel.setText("Selected: " + e.getLabel()))
                .build();        var container = new Div(group, resultLabel);
        container.addClassNames("d-flex", "flex-column", "gap-2");

        return new DemoExample("ChipGroup — label-only chips", container, """
                // Convenience addChip(String) / addChip(String, boolean) overloads
                // create Chip instances automatically.
                Components.chipGroup()
                    .addChip("Daily", true)
                    .addChip("Weekly")
                    .addChip("Monthly")
                    .addChip("Yearly")
                    .onSelect(e -> refresh(e.getLabel()))
                    .build();
                """);
    }

    // ── Example 3: standalone chip states ────────────────────────────────

    private DemoExample standaloneStatesExample() {
        var row = new Div();
        row.addClassName("chip-group");

        // Inactive (default)
        var inactive = Chip.of("Inactive");

        // Active
        var active = Chip.of("Active").active(true);

        // With count, inactive
        var withCount = Chip.of("With count", 42);

        // Active + count
        var activeCount = Chip.of("Active + count", 7).active(true);

        // Disabled
        var disabled = Chip.of("Disabled").enabled(false);

        row.add(inactive, active, withCount, activeCount, disabled);

        return new DemoExample("Standalone Chip — states", row, """
                Chip.of("Inactive")                  // default gray pill
                Chip.of("Active").active(true)        // blue tint
                Chip.of("With count", 42)             // trailing count badge
                Chip.of("Active + count", 7).active(true)
                Chip.of("Disabled").enabled(false)    // opacity 0.45, no pointer

                // Toggle active state at runtime:
                chip.active(!chip.isActive());
                // Update count at runtime:
                chip.setCount(newTotal);
                """);
    }

    // ── Example 4: size variants ──────────────────────────────────────────

    private DemoExample sizesExample() {
        var row = new Div();
        row.addClassName("chip-group");
        row.add(
                Chip.of("Small",   99).small().active(true),
                Chip.of("Default", 99).active(true),
                Chip.of("Large",   99).large().active(true),
                Chip.of("Small").small(),
                Chip.of("Default"),
                Chip.of("Large").large()
        );
        return new DemoExample("Size Variants — SM · Default · LG", row, """
                Chip.of("Label", 99).small()    // height 22px, 11px font
                Chip.of("Label", 99)            // height 28px, 12px font (default)
                Chip.of("Label", 99).large()    // height 32px, 13px font
                """);
    }

    // ── Example 5: wrapping group ─────────────────────────────────────────

    private DemoExample wrappingGroupExample() {
        var resultLabel = new Span("Selected: All categories");

        var categories = new String[]{ "Electronics", "Furniture", "Office supplies",
                "Cleaning", "Beverages", "Packaging", "Safety", "Maintenance" };

        var group = Components.chipGroup().wrap();
        group.addChip(Components.chip("All categories"), true);
        for (var cat : categories) {
            group.addChip(cat);
        }
        group.onSelect(e -> resultLabel.setText("Selected: " + e.getLabel()));

        var container = new Div(group, resultLabel);
        container.addClassNames("d-flex", "flex-column", "gap-2");

        return new DemoExample("Wrapping ChipGroup — many chips", container, """
                // Call .wrap() on the group to allow chips to wrap to the next line.
                // Adds CSS class "chip-group--wrap" (flex-wrap: wrap).
                Components.chipGroup()
                    .wrap()
                    .addChip("All categories", true)
                    .addChip("Electronics")
                    .addChip("Furniture")
                    // … more chips
                    .onSelect(e -> filter(e.getLabel()))
                    .build();
                """);
    }

    // ── Example 6: ButtonConfigurator.chip() ──────────────────────────────

    private DemoExample buttonBuilderChipExample() {
        var label = new Span("Clicked: none");

        var row = new Div();
        row.addClassName("chip-group");

        var btn1 = Components.button()
                .text("Orders")
                .chip()
                .chipActive(true)
                .withClickListener(e -> label.setText("Clicked: Orders"))
                .build();

        var btn2 = Components.button()
                .text("Invoices")
                .chip()
                .withClickListener(e -> label.setText("Clicked: Invoices"))
                .build();

        var btn3 = Components.button()
                .text("Payments")
                .chip()
                .withClickListener(e -> label.setText("Clicked: Payments"))
                .build();

        row.add(btn1, btn2, btn3, label);

        return new DemoExample("ButtonConfigurator.chip() — vaadin-button as chip", row, """
                // Use ButtonConfigurator when you need a full Holon button API
                // (icons, tooltips, click shortcuts, Localizable text, etc.)
                Components.button()
                    .withText("Orders")
                    .chip()              // adds theme="tertiary" + class "btn--chip"
                    .chipActive(true)    // adds class "btn--chip-active"
                    .withClickListener(e -> navigate())
                    .build();

                // Note: for purely visual toggle groups, prefer ChipGroup + Chip
                // (native <button>, full CSS control, SelectionEvent built in).
                """);
    }
}



