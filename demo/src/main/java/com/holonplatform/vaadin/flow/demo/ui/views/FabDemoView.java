package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the {@link Fab} component — a Material Design 3
 * <a href="https://m3.material.io/components/floating-action-button/overview">Floating Action Button</a>.
 *
 * <p>Covers:
 * <ol>
 *   <li>All color schemes (SURFACE, PRIMARY, SECONDARY, TERTIARY)</li>
 *   <li>All size presets (SMALL, DEFAULT, LARGE)</li>
 *   <li>Extended FAB with a text label, collapsible back to icon-only</li>
 *   <li>Lowered (reduced elevation) variant</li>
 *   <li>Fluent builder API usage</li>
 *   <li>Fixed on-screen docking positions</li>
 *   <li>{@link FabMenu} — a "speed-dial" menu of related actions</li>
 * </ol>
 */
@PageTitle("Fab – Holon Demo")
@Route(value = "fab", layout = DemoMainLayout.class)
public class FabDemoView extends Div {

    public FabDemoView() {
        addClassName("app-view");

        var title = new H1("Fab");

        var desc = new Paragraph(
                "Material Design 3 Floating Action Button — represents the primary, most important, " +
                "or most common action on a screen. Comes in three sizes and four color schemes, and " +
                "can be extended to show a text label next to the icon.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(colorsExample());
        examples.add(sizesExample());
        examples.add(extendedExample());
        examples.add(loweredExample());
        examples.add(builderExample());
        examples.add(positionExample());
        examples.add(menuExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample colorsExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.SURFACE).build());
        row.add(Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY).build());
        row.add(Fab.builder(VaadinIcon.STAR, Fab.Color.SECONDARY).build());
        row.add(Fab.builder(VaadinIcon.HEART, Fab.Color.TERTIARY).build());

        return new DemoExample("Color Schemes", row, """
                // Surface (default, low emphasis)
                Fab.builder(VaadinIcon.PLUS, Fab.Color.SURFACE).build();

                // Primary — the most prominent action
                Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY).build();

                // Secondary
                Fab.builder(VaadinIcon.STAR, Fab.Color.SECONDARY).build();

                // Tertiary — for visual variety
                Fab.builder(VaadinIcon.HEART, Fab.Color.TERTIARY).build();
                """);
    }

    private DemoExample sizesExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.SMALL).build());
        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.DEFAULT).build());
        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.LARGE).build());

        return new DemoExample("Size Presets", row, """
                // Small — 40dp container / 24dp icon
                Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.SMALL).build();

                // Default — 56dp container / 24dp icon
                Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.DEFAULT).build();

                // Large — 96dp container / 36dp icon
                Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).size(Fab.Size.LARGE).build();
                """);
    }

    private DemoExample extendedExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        var compose = Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY).extended("Compose").build();
        var collapsed = Fab.builder(VaadinIcon.PLUS, Fab.Color.SECONDARY).extended("New item").build();
        collapsed.collapse();

        row.add(compose);
        row.add(collapsed);
        row.add(new Paragraph("(second FAB was extended then collapsed back to icon-only)"));

        return new DemoExample("Extended FAB", row, """
                // Extended — pill shape with icon + label
                Fab compose = Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY)
                    .extended("Compose")
                    .build();

                // Collapse back to icon-only at runtime (e.g. on scroll)
                Fab fab = Fab.builder(VaadinIcon.PLUS, Fab.Color.SECONDARY).extended("New item").build();
                fab.collapse();
                """);
    }

    private DemoExample loweredExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).build());
        row.add(Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).lowered().build());

        return new DemoExample("Lowered (reduced elevation)", row, """
                // Standard elevation
                Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).build();

                // Lowered — for FABs overlapping other elevated surfaces (e.g. bottom app bar)
                Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY).lowered().build();
                """);
    }

    private DemoExample builderExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(Fab.builder().build());
        row.add(Fab.builder(VaadinIcon.CHECK).build());
        row.add(Fab.builder(VaadinIcon.TRASH, Fab.Color.TERTIARY).size(Fab.Size.LARGE).build());

        return new DemoExample("Builder API", row, """
                // Bare — VaadinIcon.PLUS, surface color, default size
                Fab.builder().build();

                // Icon only
                Fab.builder(VaadinIcon.CHECK).build();

                // Full chain
                Fab.builder(VaadinIcon.TRASH, Fab.Color.TERTIARY)
                    .size(Fab.Size.LARGE)
                    .onClick(e -> confirmDelete())
                    .build();
                """);
    }

    private DemoExample positionExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");
        row.add(new Paragraph(
                "Fab.Position docks the FAB with position:fixed and a 16dp margin from the " +
                "screen edge — M3 recommends a single, consistently placed FAB per screen."));

        return new DemoExample("Fixed Position (bottom-end)", row, """
                Fab fab = Fab.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY)
                    .position(Fab.Position.BOTTOM_END)
                    .onClick(e -> createNewItem())
                    .build();
                """);
    }

    private DemoExample menuExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");
        row.getStyle().set("min-height", "260px").set("align-items", "flex-end");

        var menu = FabMenu.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY)
                .item(VaadinIcon.EDIT, "Compose", e -> { })
                .item(VaadinIcon.CAMERA, "Photo", Fab.Color.TERTIARY, e -> { })
                .item(VaadinIcon.UPLOAD, "Attach", e -> { })
                .build();

        row.add(menu);
        row.add(new Paragraph(
                "Tap the primary FAB to reveal the stacked actions (each closes the menu on " +
                "selection); tap the scrim or the FAB again to close without choosing."));

        return new DemoExample("FAB Menu (speed-dial)", row, """
                FabMenu menu = FabMenu.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY)
                    .position(Fab.Position.BOTTOM_END)   // dock to a screen corner in real usage
                    .item(VaadinIcon.EDIT, "Compose", e -> compose())
                    .item(VaadinIcon.CAMERA, "Photo", Fab.Color.TERTIARY, e -> takePhoto())
                    .item(VaadinIcon.UPLOAD, "Attach", e -> attach())
                    .build();

                // Programmatic control
                menu.open();
                menu.close();
                menu.toggle();
                menu.isOpen();

                // Disable the full-screen scrim, or add items later
                menu.setBackdrop(false);
                menu.addItem(FabMenuItem.of(VaadinIcon.TRASH, "Delete", Fab.Color.SECONDARY, e -> delete()));
                """);
    }
}





