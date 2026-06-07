package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FlexLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Vaadin Layouts – Holon Demo")
@Route(value = "vaadin-layouts", layout = DemoMainLayout.class)
public class VaadinLayoutsDemoView extends Div {

    public VaadinLayoutsDemoView() {
        addClassName("app-view");

        var title = new H1("Vaadin Layouts");

        var desc = new Paragraph(
                "Holon fluent builders for Vaadin's standard layout components: "
                + "HorizontalLayout, VerticalLayout, FlexLayout, and FlexBoxLayout.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(horizontalExample());
        examples.add(verticalExample());
        examples.add(flexLayoutExample());
        examples.add(flexBoxLayoutExample());

        add(title, desc, examples);
    }

    private DemoExample horizontalExample() {
        var layout = HorizontalLayoutBuilder.create()
                .spacing()
                .add(colorBox("Left"), colorBox("Center"), colorBox("Right"))
                .build();

        return new DemoExample("HorizontalLayoutBuilder", layout, """
                HorizontalLayoutBuilder.create()
                    .spacing()
                    .add(left, center, right)
                    .build();""");
    }

    private DemoExample verticalExample() {
        var layout = VerticalLayoutBuilder.create()
                .spacing()
                .add(colorBox("Top"), colorBox("Middle"), colorBox("Bottom"))
                .build();

        return new DemoExample("VerticalLayoutBuilder", layout, """
                VerticalLayoutBuilder.create()
                    .spacing()
                    .add(top, middle, bottom)
                    .build();""");
    }

    private DemoExample flexLayoutExample() {
        var layout = FlexLayoutBuilder.create()
                .add(colorBox("A"), colorBox("B"), colorBox("C"), colorBox("D"))
                .flexWrap(com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap.WRAP)
                .build();

        return new DemoExample("FlexLayoutBuilder", layout, """
                FlexLayoutBuilder.create()
                    .add(a, b, c, d)
                    .flexWrap(FlexWrap.WRAP)
                    .build();""");
    }

    private DemoExample flexBoxLayoutExample() {
        var layout = FlexBoxLayoutBuilder.create()
                .add(colorBox("Item 1"), colorBox("Item 2"), colorBox("Item 3"))
                .build();

        return new DemoExample("FlexBoxLayoutBuilder", layout, """
                FlexBoxLayoutBuilder.create()
                    .add(item1, item2, item3)
                    .build();""");
    }

    private static Span colorBox(String text) {
        var span = new Span(text);
        span.getStyle().set("padding", "var(--space-s) var(--space-m)");
        span.getStyle().set("border-radius", "var(--radius-s)");
        return span;
    }
}
