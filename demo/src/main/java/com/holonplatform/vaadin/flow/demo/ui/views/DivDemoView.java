package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.DivBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Div – Holon Demo")
@Route(value = "div", layout = DemoMainLayout.class)
public class DivDemoView extends Div {

    public DivDemoView() {
        addClassName("app-view");

        var title = new H1("DivBuilder");

        var desc = new Paragraph(
                "DivBuilder provides a fluent API for creating styled Div containers "
                + "with CSS classes, children, and sizing.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(nestedExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var div = DivBuilder.create()
                .id("my-container")
                .styleName("demo-box")
                .add(new Span("Content inside a styled Div"))
                .build();
        div.getStyle().set("padding", "var(--space-m)");
        div.getStyle().set("border", "1px solid var(--color-border)");

        return new DemoExample("Basic Div", div, """
                DivBuilder.create()
                    .id("my-container")
                    .styleName("demo-box")
                    .add(new Span("Content"))
                    .build();""");
    }

    private DemoExample nestedExample() {
        var inner1 = DivBuilder.create()
                .add(new Span("Section A"))
                .build();
        inner1.getStyle().set("padding", "var(--space-s)");

        var inner2 = DivBuilder.create()
                .add(new Span("Section B"))
                .build();
        inner2.getStyle().set("padding", "var(--space-s)");

        var outer = DivBuilder.create()
                .add(inner1, inner2)
                .build();
        outer.getStyle().set("display", "flex");
        outer.getStyle().set("gap", "var(--space-m)");

        return new DemoExample("Nested Divs", outer, """
                var outer = DivBuilder.create()
                    .add(sectionA, sectionB)
                    .build();""");
    }
}
