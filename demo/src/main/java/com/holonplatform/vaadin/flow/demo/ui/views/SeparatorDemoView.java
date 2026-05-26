package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Separator} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Horizontal (default)</li>
 *   <li>Vertical</li>
 *   <li>Decorative (hidden from assistive technologies)</li>
 *   <li>In context — separating page sections</li>
 *   <li>In context — inline between text/icon elements</li>
 * </ol>
 */
@PageTitle("Separator – Holon Demo")
@Route(value = "separator", layout = DemoMainLayout.class)
public class SeparatorDemoView extends Div {

    public SeparatorDemoView() {
        addClassName("app-view");

        var title = new H1("Separator");

        var desc = new Paragraph(
                "A visual divider that separates content horizontally or vertically. " +
                "Two semantics modes: meaningful (role=separator with aria-orientation — " +
                "announced by screen readers as a thematic break) and decorative " +
                "(role=none / aria-hidden=true — purely visual, invisible to assistive technologies). " +
                "Fluent builder available via Separator.builder().");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(horizontalExample());
        examples.add(verticalExample());
        examples.add(decorativeExample());
        examples.add(sectionsExample());
        examples.add(inlineExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample horizontalExample() {
        var container = new Div();

        var above = new Paragraph("Content above the separator");
        var sep = Separator.builder().build();
        var below = new Paragraph("Content below the separator");

        container.add(above, sep, below);

        return new DemoExample("Horizontal (default)", container, """
                // Horizontal is the default orientation.
                Separator sep = Separator.builder().build();
                // Equivalent:
                Separator sep = new Separator();
                """);
    }

    private DemoExample verticalExample() {
        var container = new Div();

        var left  = new Span("Profile");
        var sep   = Separator.builder()
                .orientation(Separator.Orientation.VERTICAL)
                .build();
        var right = new Span("Settings");

        container.add(left, sep, right);

        return new DemoExample("Vertical", container, """
                Separator sep = Separator.builder()
                    .orientation(Separator.Orientation.VERTICAL)
                    .build();
                """);
    }

    private DemoExample decorativeExample() {
        var container = new Div();

        var above = new Paragraph("The separator below is decorative: aria-hidden=\"true\" and role=\"none\".");
        var sep = Separator.builder()
                .decorative(true)
                .build();
        var below = new Paragraph("Screen readers skip directly from the paragraph above to this one.");

        container.add(above, sep, below);

        return new DemoExample("Decorative (aria-hidden)", container, """
                // Use decorative when the rule is purely visual — e.g. inside a card footer.
                Separator sep = Separator.builder()
                    .decorative(true)
                    .build();
                // Renders: role="none" aria-hidden="true"
                """);
    }

    private DemoExample sectionsExample() {
        var container = new Div();

        var section1 = new Div();
        var s1title = new Span("Personal Information");
        var s1body  = new Paragraph("Name, date of birth, contact details.");
        section1.add(s1title, s1body);

        var section2 = new Div();
        var s2title = new Span("Account Settings");
        var s2body  = new Paragraph("Password, notifications, privacy.");
        section2.add(s2title, s2body);

        var section3 = new Div();
        var s3title = new Span("Billing");
        var s3body  = new Paragraph("Subscription plan, payment methods.");
        section3.add(s3title, s3body);

        container.add(section1, Separator.builder().build(),
                      section2, Separator.builder().build(),
                      section3);

        return new DemoExample("Separating Page Sections", container, """
                // Place between form sections to visually group fields.
                container.add(personalInfoSection);
                container.add(Separator.builder().build());
                container.add(accountSection);
                container.add(Separator.builder().build());
                container.add(billingSection);
                """);
    }

    private DemoExample inlineExample() {
        var container = new Div();

        var items = new String[]{"Dashboard", "Orders", "Products", "Customers"};
        for (int i = 0; i < items.length; i++) {
            var label = new Span(items[i]);
            container.add(label);
            if (i < items.length - 1) {
                container.add(Separator.builder()
                        .orientation(Separator.Orientation.VERTICAL)
                        .decorative(true)
                        .build());
            }
        }

        return new DemoExample("Inline Vertical (nav toolbar)", container, """
                // Vertical decorative separators between nav items.
                for (int i = 0; i < items.length; i++) {
                    container.add(new Span(items[i]));
                    if (i < items.length - 1) {
                        container.add(Separator.builder()
                            .orientation(Separator.Orientation.VERTICAL)
                            .decorative(true)
                            .build());
                    }
                }
                """);
    }
}

