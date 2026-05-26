package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ScrollerBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Scroller – Holon Demo")
@Route(value = "scroller", layout = DemoMainLayout.class)
public class ScrollerDemoView extends Div {

    public ScrollerDemoView() {
        addClassName("app-view");

        var title = new H1("Scroller");

        var desc = new Paragraph(
                "ScrollerBuilder provides a fluent API for Vaadin Scroller "
                + "with configurable scroll direction and content.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(verticalExample());
        examples.add(horizontalExample());
        examples.add(bothExample());

        add(title, desc, examples);
    }

    private DemoExample verticalExample() {
        var content = new VerticalLayout();
        for (int i = 1; i <= 20; i++) {
            content.add(new Span("Item " + i));
        }

        var scroller = ScrollerBuilder.create()
                .content(content)
                .scrollDirection(ScrollDirection.VERTICAL)
                .height("200px")
                .width("100%")
                .build();

        return new DemoExample("Vertical Scroll", scroller, """
                var content = new VerticalLayout();
                for (int i = 1; i <= 20; i++) {
                    content.add(new Span("Item " + i));
                }
                ScrollerBuilder.create()
                    .content(content)
                    .scrollDirection(ScrollDirection.VERTICAL)
                    .height("200px")
                    .build();""");
    }

    private DemoExample horizontalExample() {
        var content = new Div();
        content.getStyle().set("display", "flex");
        content.getStyle().set("gap", "var(--space-m)");
        content.getStyle().set("white-space", "nowrap");
        for (int i = 1; i <= 15; i++) {
            var item = new Span("Horizontal Item " + i);
            item.getStyle().set("padding", "var(--space-s)");
            content.add(item);
        }

        var scroller = ScrollerBuilder.create()
                .content(content)
                .scrollDirection(ScrollDirection.HORIZONTAL)
                .height("60px")
                .width("100%")
                .build();

        return new DemoExample("Horizontal Scroll", scroller, """
                ScrollerBuilder.create()
                    .content(content)
                    .scrollDirection(ScrollDirection.HORIZONTAL)
                    .height("60px")
                    .build();""");
    }

    private DemoExample bothExample() {
        var content = new Div();
        content.getStyle().set("width", "800px");
        for (int i = 1; i <= 30; i++) {
            content.add(new Paragraph("Row " + i + " — This is a wide content area that enables both vertical and horizontal scrolling."));
        }

        var scroller = ScrollerBuilder.create()
                .content(content)
                .scrollDirection(ScrollDirection.BOTH)
                .height("200px")
                .width("100%")
                .build();

        return new DemoExample("Both Directions", scroller, """
                ScrollerBuilder.create()
                    .content(content)
                    .scrollDirection(ScrollDirection.BOTH)
                    .height("200px")
                    .build();""");
    }
}
