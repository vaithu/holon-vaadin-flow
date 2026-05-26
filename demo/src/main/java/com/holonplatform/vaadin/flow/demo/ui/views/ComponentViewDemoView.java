package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.ComponentView;
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
 * Demo page for the {@link ComponentView} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Minimal usage — just an H2 heading</li>
 *   <li>Heading + single preview row</li>
 *   <li>Multiple sections in one view</li>
 * </ol>
 */
@PageTitle("ComponentView – Holon Demo")
@Route(value = "component-view", layout = DemoMainLayout.class)
public class ComponentViewDemoView extends Div {

    public ComponentViewDemoView() {
        addClassName("app-view");

        var title = new H1("ComponentView");

        var desc = new Paragraph(
                "A semantic <main> page section that groups related component demonstrations. " +
                "addH2(text) appends a styled <h2> heading; addPreview(components…) " +
                "wraps the given components in a Preview flex container. " +
                "Useful as the outer container for a documentation or showcase page.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(headingOnlyExample());
        examples.add(headingAndPreviewExample());
        examples.add(multipleSectionsExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample headingOnlyExample() {
        var cv = new ComponentView();
        cv.addH2("My Section");

        return new DemoExample("Heading Only", cv, """
                ComponentView cv = new ComponentView();
                cv.addH2("My Section");
                """);
    }

    private DemoExample headingAndPreviewExample() {
        var cv = new ComponentView();
        cv.addH2("Button variants");
        cv.addPreview(
                new Button("Default"),
                new Button("Primary"),
                new Button("Tertiary")
        );

        return new DemoExample("Heading + Preview Row", cv, """
                ComponentView cv = new ComponentView();
                cv.addH2("Button variants");

                // addPreview() wraps components in a Preview flex-row
                cv.addPreview(
                    new Button("Default"),
                    new Button("Primary"),
                    new Button("Tertiary")
                );
                """);
    }

    private DemoExample multipleSectionsExample() {
        var cv = new ComponentView();

        cv.addH2("Text inputs");
        cv.addPreview(
                new TextField("Name"),
                new TextField("Email"),
                new TextField("Phone")
        );

        cv.addH2("Action buttons");
        cv.addPreview(
                new Button("Save"),
                new Button("Cancel")
        );

        cv.addH2("Status labels");
        var active  = new Span("Active");  active.addClassName("demo-cv__badge demo-cv__badge--green");
        var pending = new Span("Pending"); pending.addClassName("demo-cv__badge demo-cv__badge--yellow");
        var closed  = new Span("Closed");  closed.addClassName("demo-cv__badge demo-cv__badge--red");
        cv.addPreview(active, pending, closed);

        return new DemoExample("Multiple Sections", cv, """
                ComponentView cv = new ComponentView();

                // Each addH2() / addPreview() pair appends a new section.
                cv.addH2("Text inputs");
                cv.addPreview(new TextField("Name"), new TextField("Email"));

                cv.addH2("Action buttons");
                cv.addPreview(new Button("Save"), new Button("Cancel"));
                """);
    }
}

