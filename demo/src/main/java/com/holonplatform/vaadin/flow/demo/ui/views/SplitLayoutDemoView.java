package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("SplitLayout – Holon Demo")
@Route(value = "split-layout", layout = DemoMainLayout.class)
public class SplitLayoutDemoView extends Div {

    public SplitLayoutDemoView() {
        addClassName("app-view");

        var title = new H1("SplitLayout");

        var desc = new Paragraph(
                "SplitLayoutBuilder provides a fluent API for Vaadin SplitLayout. "
                + "Configure primary/secondary components, orientation, and splitter position.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(horizontalExample());
        examples.add(verticalExample());
        examples.add(customPositionExample());

        add(title, desc, examples);
    }

    private DemoExample horizontalExample() {
        var split = SplitLayoutBuilder.create()
                .primaryComponent(createPanel("Primary Panel"))
                .secondaryComponent(createPanel("Secondary Panel"))
                .build();
        split.setHeight("200px");

        return new DemoExample("Horizontal (Default)", split, """
                SplitLayoutBuilder.create()
                    .primaryComponent(leftPanel)
                    .secondaryComponent(rightPanel)
                    .build();""");
    }

    private DemoExample verticalExample() {
        var split = SplitLayoutBuilder.create()
                .primaryComponent(createPanel("Top Panel"))
                .secondaryComponent(createPanel("Bottom Panel"))
                .orientation(SplitLayout.Orientation.VERTICAL)
                .build();
        split.setHeight("300px");

        return new DemoExample("Vertical", split, """
                SplitLayoutBuilder.create()
                    .primaryComponent(topPanel)
                    .secondaryComponent(bottomPanel)
                    .orientation(SplitLayout.Orientation.VERTICAL)
                    .build();""");
    }

    private DemoExample customPositionExample() {
        var split = SplitLayoutBuilder.create()
                .primaryComponent(createPanel("Narrow (30%)"))
                .secondaryComponent(createPanel("Wide (70%)"))
                .splitterPosition(30)
                .build();
        split.setHeight("200px");

        return new DemoExample("Custom Splitter Position", split, """
                SplitLayoutBuilder.create()
                    .primaryComponent(narrowPanel)
                    .secondaryComponent(widePanel)
                    .splitterPosition(30)
                    .build();""");
    }

    private Div createPanel(String text) {
        var panel = new Div(new Span(text));
        panel.setSizeFull();
        return panel;
    }
}
