package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Alert} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>All five variants (DEFAULT → INFO)</li>
 *   <li>Leading icon slot</li>
 *   <li>Action button slot</li>
 *   <li>Title-only (no description)</li>
 * </ol>
 */
@PageTitle("Alert – Holon Demo")
@Route(value = "alert", layout = DemoMainLayout.class)
public class AlertDemoView extends Div {

    public AlertDemoView() {
        addClassName("app-view");

        // ── Page header ──────────────────────────────────────────────────────
        var title = new H1("Alert");

        var desc = new Paragraph(
                "Contextual alert component inspired by shadcn/ui. " +
                "Five severity variants — DEFAULT, DESTRUCTIVE, WARNING, SUCCESS, INFO — each driven " +
                "purely by a CSS BEM modifier class. Supports optional icon, title, description, " +
                "and action (button/link) slots via the fluent builder.");

        // ── Examples ─────────────────────────────────────────────────────────
        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(variantsExample());
        examples.add(withIconExample());
        examples.add(withActionExample());
        examples.add(titleOnlyExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample variantsExample() {
        var preview = new Div();

        for (Alert.Variant variant : Alert.Variant.values()) {
            String label = capitalize(variant.name());
            preview.add(
                Alert.builder(variant)
                    .title(label)
                    .description("This is a " + label.toLowerCase() + " alert — use it for " + variantHint(variant) + ".")
                    .build()
            );
        }

        return new DemoExample("Variants", preview, """
                // DEFAULT
                Alert.builder(Alert.Variant.DEFAULT)
                    .title("Heads up!")
                    .description("You can add components using the fluent builder.")
                    .build();

                // DESTRUCTIVE
                Alert.builder(Alert.Variant.DESTRUCTIVE)
                    .title("Error")
                    .description("Your session has expired. Please sign in again.")
                    .build();

                // WARNING
                Alert.builder(Alert.Variant.WARNING)
                    .title("Warning")
                    .description("This action may have unintended side effects.")
                    .build();

                // SUCCESS
                Alert.builder(Alert.Variant.SUCCESS)
                    .title("Saved")
                    .description("Your changes have been saved successfully.")
                    .build();

                // INFO
                Alert.builder(Alert.Variant.INFO)
                    .title("Info")
                    .description("A new software update is available.")
                    .build();
                """);
    }

    private DemoExample withIconExample() {
        var preview = new Div();

        preview.add(
            Alert.builder(Alert.Variant.DESTRUCTIVE)
                .icon(VaadinIcon.EXCLAMATION_CIRCLE_O.create())
                .title("Authentication Error")
                .description("Your session has expired. Please sign in again.")
                .build(),
            Alert.builder(Alert.Variant.SUCCESS)
                .icon(VaadinIcon.CHECK_CIRCLE.create())
                .title("Profile Updated")
                .description("Your changes have been saved and are now live.")
                .build(),
            Alert.builder(Alert.Variant.INFO)
                .icon(VaadinIcon.INFO_CIRCLE.create())
                .title("Maintenance Scheduled")
                .description("The system will be unavailable on Sunday, 2–4 AM UTC.")
                .build()
        );

        return new DemoExample("With Icon", preview, """
                Alert.builder(Alert.Variant.DESTRUCTIVE)
                    .icon(VaadinIcon.EXCLAMATION_CIRCLE_O.create())
                    .title("Authentication Error")
                    .description("Your session has expired. Please sign in again.")
                    .build();

                Alert.builder(Alert.Variant.SUCCESS)
                    .icon(VaadinIcon.CHECK_CIRCLE.create())
                    .title("Profile Updated")
                    .description("Your changes have been saved and are now live.")
                    .build();
                """);
    }

    private DemoExample withActionExample() {
        var preview = new Div();

        preview.add(
            Alert.builder(Alert.Variant.WARNING)
                .icon(VaadinIcon.CLOCK.create())
                .title("Session Expiring")
                .description("Your session will expire in 5 minutes. Extend it to avoid losing unsaved work.")
                .action(new Button("Extend Session"))
                .build(),
            Alert.builder(Alert.Variant.DESTRUCTIVE)
                .title("Unsaved Changes")
                .description("You have unsaved changes that will be lost if you navigate away.")
                .action(new Button("Save"), new Button("Discard"))
                .build()
        );

        return new DemoExample("With Action", preview, """
                Alert.builder(Alert.Variant.WARNING)
                    .icon(VaadinIcon.CLOCK.create())
                    .title("Session Expiring")
                    .description("Your session will expire in 5 minutes.")
                    .action(new Button("Extend Session"))
                    .build();

                // Multiple action buttons
                Alert.builder(Alert.Variant.DESTRUCTIVE)
                    .title("Unsaved Changes")
                    .description("You have unsaved changes that will be lost.")
                    .action(new Button("Save"), new Button("Discard"))
                    .build();
                """);
    }

    private DemoExample titleOnlyExample() {
        var preview = new Div();

        for (Alert.Variant variant : Alert.Variant.values()) {
            preview.add(
                Alert.builder(variant)
                    .title(capitalize(variant.name()) + " — title only, no description")
                    .build()
            );
        }

        return new DemoExample("Title Only (no description)", preview, """
                // Description slot is entirely optional
                Alert.builder(Alert.Variant.INFO)
                    .title("Info — title only, no description")
                    .build();
                """);
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private static String variantHint(Alert.Variant v) {
        return switch (v) {
            case DEFAULT     -> "neutral messages";
            case DESTRUCTIVE -> "errors and failures";
            case WARNING     -> "caution and potential issues";
            case SUCCESS     -> "confirmations and success states";
            case INFO        -> "informational notices";
        };
    }
}

