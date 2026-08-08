package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("VaadinIcon – Holon Demo")
@Route(value = "VaadinIcon", layout = DemoMainLayout.class)
public class VaadinIconDemoView extends Div {

    public VaadinIconDemoView() {
        addClassName("app-view");

        var title = new H1("VaadinIcon");

        var desc = new Paragraph(
                "VaadinIcon is Vaadin's built-in icon enum with 600+ icons from the Vaadin icon set. "
                + "Each value has a create() method that produces an Icon component.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(commonIconsExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        Icon icon = VaadinIcon.HOME.create();

        var row = new HorizontalLayout(icon, new Span("Home icon"));
        row.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        row.setSpacing(true);

        return new DemoExample("Basic Usage", row, """
                Icon icon = VaadinIcon.HOME.create();""");
    }

    private DemoExample commonIconsExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        layout.add(iconWithLabel(VaadinIcon.SEARCH,       "Search"));
        layout.add(iconWithLabel(VaadinIcon.COG,          "Settings"));
        layout.add(iconWithLabel(VaadinIcon.TRASH,        "Delete"));
        layout.add(iconWithLabel(VaadinIcon.PLUS,         "Add"));
        layout.add(iconWithLabel(VaadinIcon.EDIT,         "Edit"));
        layout.add(iconWithLabel(VaadinIcon.HEART,        "Favorite"));
        layout.add(iconWithLabel(VaadinIcon.CHECK_CIRCLE, "Check"));
        layout.add(iconWithLabel(VaadinIcon.WARNING,      "Warning"));

        return new DemoExample("Common Icons", layout, """
                VaadinIcon.SEARCH.create();
                VaadinIcon.COG.create();
                VaadinIcon.TRASH.create();
                VaadinIcon.PLUS.create();
                VaadinIcon.EDIT.create();
                VaadinIcon.HEART.create();
                VaadinIcon.CHECK_CIRCLE.create();
                VaadinIcon.WARNING.create();""");
    }

    private static Div iconWithLabel(VaadinIcon icon, String label) {
        var container = new Div();
        container.addClassNames("flex-col", "items-center", "gap-xs");
        container.add(icon.create());
        var text = new Span(label);
        text.addClassName("text-xs");
        container.add(text);
        return container;
    }
}
