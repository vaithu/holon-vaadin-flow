package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Label – Holon Demo")
@Route(value = "label", layout = DemoMainLayout.class)
public class LabelDemoView extends Div {

    public LabelDemoView() {
        addClassName("app-view");

        var title = new H1("Label");

        var desc = new Paragraph(
                "LabelBuilder creates text-display components using semantic HTML tags: "
                + "Span, Div, Paragraph, and H1 through H6.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(spanExample());
        examples.add(headingsExample());
        examples.add(paragraphExample());

        add(title, desc, examples);
    }

    private DemoExample spanExample() {
        var label = LabelBuilder.span()
                .text("This is a Span label")
                .build();

        return new DemoExample("Span Label", label, """
                LabelBuilder.span()
                    .text("This is a Span label")
                    .build();""");
    }

    private DemoExample headingsExample() {
        var h1 = LabelBuilder.h1().text("Heading 1").build();
        var h2 = LabelBuilder.h2().text("Heading 2").build();
        var h3 = LabelBuilder.h3().text("Heading 3").build();
        var h4 = LabelBuilder.h4().text("Heading 4").build();
        var h5 = LabelBuilder.h5().text("Heading 5").build();
        var h6 = LabelBuilder.h6().text("Heading 6").build();

        var layout = new VerticalLayout(h1, h2, h3, h4, h5, h6);
        layout.setSpacing(false);
        layout.setPadding(false);

        return new DemoExample("Heading Labels (H1–H6)", layout, """
                LabelBuilder.h1().text("Heading 1").build();
                LabelBuilder.h2().text("Heading 2").build();
                LabelBuilder.h3().text("Heading 3").build();
                LabelBuilder.h4().text("Heading 4").build();
                LabelBuilder.h5().text("Heading 5").build();
                LabelBuilder.h6().text("Heading 6").build();""");
    }

    private DemoExample paragraphExample() {
        var p = LabelBuilder.paragraph()
                .text("This is a Paragraph label, suitable for longer text blocks.")
                .build();

        var div = LabelBuilder.div()
                .text("This is a Div label, a generic block-level text element.")
                .build();

        var layout = new VerticalLayout(p, div);
        layout.setSpacing(true);
        layout.setPadding(false);

        return new DemoExample("Paragraph & Div Labels", layout, """
                LabelBuilder.paragraph()
                    .text("Paragraph text")
                    .build();
                LabelBuilder.div()
                    .text("Div text")
                    .build();""");
    }
}
