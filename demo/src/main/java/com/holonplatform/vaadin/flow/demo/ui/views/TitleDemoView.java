package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.TitleBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Title – Holon Demo")
@Route(value = "title", layout = DemoMainLayout.class)
public class TitleDemoView extends Div {

    public TitleDemoView() {
        addClassName("app-view");

        var title = new H1("Title");

        var desc = new Paragraph(
                "TitleBuilder creates a horizontal title bar with a text label "
                + "and optional additional components.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withComponentsExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var titleBar = TitleBuilder.create()
                .text("Dashboard")
                .build();

        return new DemoExample("Basic Title", titleBar, """
                TitleBuilder.create()
                    .text("Dashboard")
                    .build();""");
    }

    private DemoExample withComponentsExample() {
        var badge = new Span("Beta");
        badge.getStyle().set("padding", "2px 8px");
        badge.getStyle().set("border-radius", "var(--radius-s)");
        badge.getStyle().set("font-size", "var(--font-size-xs)");

        var titleBar = TitleBuilder.create()
                .text("Analytics")
                .add(badge)
                .build();

        return new DemoExample("Title with Badge", titleBar, """
                TitleBuilder.create()
                    .text("Analytics")
                    .add(badgeComponent)
                    .build();""");
    }
}
