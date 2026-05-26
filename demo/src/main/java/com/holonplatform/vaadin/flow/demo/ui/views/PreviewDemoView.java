package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.Preview;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Preview} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Single component wrapped in Preview</li>
 *   <li>Multiple stacked components (column-flex + gap)</li>
 *   <li>Heterogeneous content (alerts, inputs, buttons)</li>
 *   <li>Preview as a documentation showcase container</li>
 * </ol>
 */
@PageTitle("Preview – Holon Demo")
@Route(value = "preview", layout = DemoMainLayout.class)
public class PreviewDemoView extends Div {

    public PreviewDemoView() {
        addClassName("app-view");

        var title = new H1("Preview");

        var desc = new Paragraph(
                "A column-flex Layout wrapper with consistent vertical gap, " +
                "loaded from preview.css. Used by ComponentView.addPreview() to showcase " +
                "live component examples in documentation and demo pages. " +
                "Can also be used standalone whenever a column-flex container " +
                "with standard spacing is needed.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(singleComponentExample());
        examples.add(multipleComponentsExample());
        examples.add(heterogeneousContentExample());
        examples.add(showcaseContainerExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample singleComponentExample() {
        var preview = new Preview(new Span("A single component wrapped in a Preview container."));

        return new DemoExample("Single Component", preview, """
                // Wrap any component in a Preview for consistent column-flex spacing.
                var preview = new Preview(new Span("Hello World"));
                """);
    }

    private DemoExample multipleComponentsExample() {
        var a = new Span("First item");

        var b = new Span("Second item");

        var c = new Span("Third item");

        var preview = new Preview(a, b, c);

        return new DemoExample("Multiple Components (column stack)", preview, """
                // Multiple children are stacked vertically with Gap.MEDIUM spacing.
                var preview = new Preview(
                    new Span("First item"),
                    new Span("Second item"),
                    new Span("Third item")
                );
                // The gap and flex-direction come from preview.css — no inline styles.
                """);
    }

    private DemoExample heterogeneousContentExample() {
        var nameField  = new TextField("Name");
        var emailField = new TextField("Email");
        var saveBtn    = new Button("Save profile");

        var preview = new Preview(nameField, emailField, saveBtn);

        return new DemoExample("Heterogeneous Content", preview, """
                // Accepts any mix of Vaadin components — useful for form mockups.
                var preview = new Preview(
                    new TextField("Name"),
                    new TextField("Email"),
                    new Button("Save profile")
                );
                """);
    }

    private DemoExample showcaseContainerExample() {
        // Preview is primarily used as the content wrapper inside ComponentView.addPreview().
        // Here we show what that looks like end-to-end.
        var info    = Alert.builder(Alert.Variant.INFO)
                           .description("Info: operation completed.")
                           .build();
        var success = Alert.builder(Alert.Variant.SUCCESS)
                           .description("Success: record saved.")
                           .build();
        var warning = Alert.builder(Alert.Variant.WARNING)
                           .description("Warning: quota is 80% used.")
                           .build();

        var preview = new Preview(info, success, warning);

        return new DemoExample("Documentation Showcase", preview, """
                // ComponentView.addPreview() internally wraps components in a Preview.
                // You can use Preview directly for the same effect.
                ComponentView cv = new ComponentView();
                cv.addH2("Alert variants");
                cv.addPreview(
                    Alert.builder(Alert.Variant.INFO).description("Info message").build(),
                    Alert.builder(Alert.Variant.SUCCESS).description("Success message").build(),
                    Alert.builder(Alert.Variant.WARNING).description("Warning message").build()
                );

                // Equivalent using Preview directly:
                var preview = new Preview(alertInfo, alertSuccess, alertWarning);
                """);
    }
}

