package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link ButtonGroup}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic horizontal group — three plain buttons</li>
 *   <li>Icon buttons (icon-only square)</li>
 *   <li>Mixed icon + text buttons</li>
 *   <li>Vertical orientation</li>
 *   <li>Custom corner radius via CSS property</li>
 *   <li>Full-width group</li>
 *   <li>Disabled buttons inside a group</li>
 *   <li>Fluent builder API</li>
 * </ol>
 */
@PageTitle("ButtonGroup – Holon Demo")
@Route(value = "button-group", layout = DemoMainLayout.class)
public class ButtonGroupDemoView extends Div {

    public ButtonGroupDemoView() {
        addClassName("app-view");

        var title = new H1("ButtonGroup");

        var desc = new Paragraph(
                "ButtonGroup merges adjacent Vaadin buttons into a single cohesive control. " +
                "Shared borders are collapsed so only the outermost edges carry a border-radius, " +
                "giving the appearance of one compound button. Supports horizontal (default) and " +
                "vertical orientations, icon-only buttons, mixed icon+text, disabled entries, and " +
                "a custom corner radius via the --btn-group-radius CSS property.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(iconOnlyExample());
        examples.add(mixedExample());
        examples.add(verticalExample());
        examples.add(customRadiusExample());
        examples.add(fullWidthExample());
        examples.add(disabledExample());
        examples.add(builderExample());

        add(title, desc, examples);
    }

    // ── Example 1 ─────────────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var group = new ButtonGroup(
                new Button("Day"),
                new Button("Week"),
                new Button("Month")
        );

        return new DemoExample("Basic — plain text buttons", group, """
                // Pass Button instances to the constructor.
                // Adjacent borders collapse; only outer corners are rounded.
                var group = new ButtonGroup(
                    new Button("Day"),
                    new Button("Week"),
                    new Button("Month")
                );
                """);
    }

    // ── Example 2 ─────────────────────────────────────────────────────────────

    private DemoExample iconOnlyExample() {
        var bold   = new Button(VaadinIcon.BOLD.create());
        var italic = new Button(VaadinIcon.ITALIC.create());
        var underl = new Button(VaadinIcon.UNDERLINE.create());

        // icon buttons — add btn--icon class for square sizing (from buttons.css)
        bold.addClassName("btn--icon");
        italic.addClassName("btn--icon");
        underl.addClassName("btn--icon");

        var group = new ButtonGroup(bold, italic, underl);

        return new DemoExample("Icon-only buttons", group, """
                // Add the btn--icon class (from buttons.css) to each button for
                // square sizing. No Lumo variants are used.
                var bold   = new Button(VaadinIcon.BOLD.create());
                bold.addClassName("btn--icon");
                var italic = new Button(VaadinIcon.ITALIC.create());
                italic.addClassName("btn--icon");
                var underl = new Button(VaadinIcon.UNDERLINE.create());
                underl.addClassName("btn--icon");

                var group = new ButtonGroup(bold, italic, underl);
                """);
    }

    // ── Example 3 ─────────────────────────────────────────────────────────────

    private DemoExample mixedExample() {
        var prev = new Button("Prev", VaadinIcon.ANGLE_LEFT.create());
        var next = new Button("Next", VaadinIcon.ANGLE_RIGHT.create());
        var group = new ButtonGroup(prev, next);

        return new DemoExample("Mixed icon + text buttons", group, """
                // Vaadin Button(text, icon) places the icon as a suffix.
                // For a prefix icon use Button(icon) then setText() separately.
                var prev = new Button("Prev", VaadinIcon.ANGLE_LEFT.create());
                var next = new Button("Next", VaadinIcon.ANGLE_RIGHT.create());
                var group = new ButtonGroup(prev, next);
                """);
    }

    // ── Example 4 ─────────────────────────────────────────────────────────────

    private DemoExample verticalExample() {
        var group = new ButtonGroup(
                ButtonGroup.Orientation.VERTICAL,
                new Button("Profile"),
                new Button("Security"),
                new Button("Notifications"),
                new Button("Billing")
        );

        return new DemoExample("Vertical orientation", group, """
                // Pass Orientation.VERTICAL as the first argument.
                // Border merging flips to the block axis; top/bottom corners get the radius.
                var group = new ButtonGroup(
                    ButtonGroup.Orientation.VERTICAL,
                    new Button("Profile"),
                    new Button("Security"),
                    new Button("Notifications"),
                    new Button("Billing")
                );

                // Or via setter after construction:
                // group.setOrientation(ButtonGroup.Orientation.VERTICAL);
                """);
    }

    // ── Example 5 ─────────────────────────────────────────────────────────────

    private DemoExample customRadiusExample() {
        var group = new ButtonGroup(
                new Button("Left"),
                new Button("Center"),
                new Button("Right")
        );
        // Override --btn-group-radius at the group level
        group.getElement().getStyle().set("--btn-group-radius", "9999px");

        return new DemoExample("Custom corner radius via --btn-group-radius", group, """
                // By default --btn-group-radius is 0 (fully square).
                // Override it to add rounding — pill example:
                var group = new ButtonGroup(
                    new Button("Left"), new Button("Center"), new Button("Right")
                );
                group.getElement().getStyle().set("--btn-group-radius", "9999px");

                // Or in CSS on a parent selector:
                //   .my-toolbar .btn-group { --btn-group-radius: 0.375rem; }
                """);
    }

    // ── Example 6 ─────────────────────────────────────────────────────────────

    private DemoExample fullWidthExample() {
        var group = ButtonGroup.builder()
                .add(new Button("Export"), new Button("Import"), new Button("Sync"))
                .fullWidth()
                .build();

        return new DemoExample("Full-width group", group, """
                // fullWidth() calls setWidthFull() on the group, making each button
                // expand to fill an equal share of the available row width.
                var group = ButtonGroup.builder()
                    .add(new Button("Export"), new Button("Import"), new Button("Sync"))
                    .fullWidth()
                    .build();
                """);
    }

    // ── Example 7 ─────────────────────────────────────────────────────────────

    private DemoExample disabledExample() {
        var save   = new Button("Save");
        var cancel = new Button("Cancel");
        var delete = new Button("Delete");
        delete.setEnabled(false);   // disabled individually; group is unaffected

        var group = new ButtonGroup(save, cancel, delete);

        return new DemoExample("Disabled buttons inside a group", group, """
                // Disable individual buttons as usual — ButtonGroup has no global
                // disable API because groups often mix enabled/disabled entries.
                var save   = new Button("Save");
                var cancel = new Button("Cancel");
                var delete = new Button("Delete");
                delete.setEnabled(false);

                var group = new ButtonGroup(save, cancel, delete);
                """);
    }

    // ── Example 8 ─────────────────────────────────────────────────────────────

    private DemoExample builderExample() {
        var statusLabel = new Span("No view selected");

        var list  = new Button("List");
        var board = new Button("Board");
        var table = new Button("Table");

        list.addClickListener(e  -> statusLabel.setText("View: List"));
        board.addClickListener(e -> statusLabel.setText("View: Board"));
        table.addClickListener(e -> statusLabel.setText("View: Table"));

        var group = ButtonGroup.builder()
                .add(list, board, table)
                .build();

        var wrapper = new FlexLayout(group, statusLabel);
        wrapper.setAlignItems(FlexLayout.Alignment.CENTER);
        wrapper.getStyle().set("gap", "1rem");

        return new DemoExample("Fluent builder with click listeners", wrapper, """
                // ButtonGroup.builder() produces the same result as the constructor,
                // but reads well when combining with width, orientation, and click wiring.
                var list  = new Button("List");
                var board = new Button("Board");
                var table = new Button("Table");

                list.addClickListener(e  -> label.setText("View: List"));
                board.addClickListener(e -> label.setText("View: Board"));
                table.addClickListener(e -> label.setText("View: Table"));

                var group = ButtonGroup.builder()
                    .add(list, board, table)
                    .build();
                """);
    }
}






