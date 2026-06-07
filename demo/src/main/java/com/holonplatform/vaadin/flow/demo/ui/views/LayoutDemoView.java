package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Layout – Holon Demo")
@Route(value = "layout", layout = DemoMainLayout.class)
public class LayoutDemoView extends Div {

    public LayoutDemoView() {
        addClassName("app-view");

        var title = new H1("Layout");

        var desc = new Paragraph(
                "Layout is the foundation layout component from the vaadinplus package. "
                + "It extends Div with flex/grid CSS utility methods: "
                + "horizontal(), vertical(), center(), wrap(), gap(), columns(), and more.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(horizontalExample());
        examples.add(verticalExample());
        examples.add(centerExample());
        examples.add(gridColumnsExample());
        examples.add(wrapExample());

        add(title, desc, examples);
    }

    private DemoExample horizontalExample() {
        var layout = LayoutBuilder.create()
                .horizontal()
                .gap(Gap.MEDIUM)
                .add(colorBox("A"), colorBox("B"), colorBox("C"))
                .build();

        return new DemoExample("Horizontal Row", layout, """
                LayoutBuilder.create()
                    .horizontal()
                    .gap(Gap.MEDIUM)
                    .add(boxA, boxB, boxC)
                    .build();""");
    }

    private DemoExample verticalExample() {
        var layout = LayoutBuilder.create()
                .vertical()
                .gap(Gap.SMALL)
                .add(colorBox("Row 1"), colorBox("Row 2"), colorBox("Row 3"))
                .build();

        return new DemoExample("Vertical Column", layout, """
                LayoutBuilder.create()
                    .vertical()
                    .gap(Gap.SMALL)
                    .add(row1, row2, row3)
                    .build();""");
    }

    private DemoExample centerExample() {
        var layout = LayoutBuilder.create()
                .center()
                .height("150px")
                .add(new Span("Centered content"))
                .build();
        layout.getStyle().set("border", "1px dashed var(--color-border)");

        return new DemoExample("Centered", layout, """
                LayoutBuilder.create()
                    .center()
                    .height("150px")
                    .add(new Span("Centered content"))
                    .build();""");
    }

    private DemoExample gridColumnsExample() {
        var layout = LayoutBuilder.create()
                .display(Display.GRID)
                .columns(GridColumns.COLUMNS_3)
                .gap(Gap.MEDIUM)
                .add(colorBox("1"), colorBox("2"), colorBox("3"),
                     colorBox("4"), colorBox("5"), colorBox("6"))
                .build();

        return new DemoExample("Grid Columns", layout, """
                LayoutBuilder.create()
                    .display(Display.GRID)
                    .columns(GridColumns.COLUMNS_3)
                    .gap(Gap.MEDIUM)
                    .add(box1, box2, box3, box4, box5, box6)
                    .build();""");
    }

    private DemoExample wrapExample() {
        var layout = LayoutBuilder.create()
                .wrap()
                .gap(Gap.SMALL)
                .build();
        for (int i = 1; i <= 12; i++) {
            layout.add(colorBox("Tag " + i));
        }

        return new DemoExample("Wrapping Flow", layout, """
                LayoutBuilder.create()
                    .wrap()
                    .gap(Gap.SMALL)
                    .add(tag1, tag2, ... tag12)
                    .build();""");
    }

    private static Span colorBox(String text) {
        var span = new Span(text);
        span.getStyle().set("padding", "var(--space-s) var(--space-m)");
        span.getStyle().set("border-radius", "var(--radius-s)");
        return span;
    }
}
