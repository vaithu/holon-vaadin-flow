package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.TabSheetBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("TabSheet – Holon Demo")
@Route(value = "tab-sheet", layout = DemoMainLayout.class)
public class TabSheetDemoView extends Div {

    public TabSheetDemoView() {
        addClassName("app-view");

        var title = new H1("TabSheet");

        var desc = new Paragraph(
                "TabSheetBuilder provides a fluent API for Vaadin TabSheet — "
                + "a component that pairs tabs with content panels, showing one at a time.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withIconsExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var tabSheet = TabSheetBuilder.create()
                .withTab("Overview", new Span("This is the overview content."))
                .withTab("Details", new Span("Here are the details."))
                .withTab("Settings", new Span("Settings go here."))
                .build();

        return new DemoExample("Basic TabSheet", tabSheet, """
                TabSheetBuilder.create()
                    .withTab("Overview", new Span("Overview content"))
                    .withTab("Details", new Span("Details content"))
                    .withTab("Settings", new Span("Settings content"))
                    .build();""");
    }

    private DemoExample withIconsExample() {
        var tabSheet = TabSheetBuilder.create()
                .withTab(new Icon(VaadinIcon.HOME), "Home",
                        new Span("Home tab content"))
                .withTab(new Icon(VaadinIcon.USER), "Profile",
                        new Span("Profile tab content"))
                .withTab(new Icon(VaadinIcon.COG), "Settings",
                        new Span("Settings tab content"))
                .build();

        return new DemoExample("With Icons", tabSheet, """
                TabSheetBuilder.create()
                    .withTab(new Icon(VaadinIcon.HOME), "Home", content)
                    .withTab(new Icon(VaadinIcon.USER), "Profile", content)
                    .withTab(new Icon(VaadinIcon.COG), "Settings", content)
                    .build();""");
    }
}
