package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Badge – Holon Demo")
@Route(value = "badge", layout = DemoMainLayout.class)
public class BadgeDemoView extends Div {

    public BadgeDemoView() {
        addClassName("app-view");

        var title = new H1("Badge");

        var desc = new Paragraph(
                "A small label chip rendered as a themed <span>. "
                + "Supports color variants (NORMAL, SUCCESS, ERROR, CONTRAST, PRIMARY), "
                + "sizes (default, S), and shapes (default, PILL).");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(defaultExample());
        examples.add(colorsExample());
        examples.add(pillExample());
        examples.add(smallExample());

        add(title, desc, examples);
    }

    private DemoExample defaultExample() {
        var badge = new Badge("Default");

        return new DemoExample("Default Badge", badge, """
                new Badge("Default")""");
    }

    private DemoExample colorsExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.add(
                new Badge("Normal", BadgeColor.NORMAL),
                new Badge("Success", BadgeColor.SUCCESS),
                new Badge("Error", BadgeColor.ERROR),
                new Badge("Contrast", BadgeColor.CONTRAST),
                new Badge("Primary", BadgeColor.NORMAL_PRIMARY)
        );

        return new DemoExample("Color Variants", layout, """
                new Badge("Normal",   BadgeColor.NORMAL)
                new Badge("Success",  BadgeColor.SUCCESS)
                new Badge("Error",    BadgeColor.ERROR)
                new Badge("Contrast", BadgeColor.CONTRAST)
                new Badge("Primary",  BadgeColor.NORMAL_PRIMARY)""");
    }

    private DemoExample pillExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.add(
                new Badge("Pill Success", BadgeColor.SUCCESS, BadgeSize.M, BadgeShape.PILL),
                new Badge("Pill Error", BadgeColor.ERROR, BadgeSize.M, BadgeShape.PILL)
        );

        return new DemoExample("Pill Shape", layout, """
                new Badge("Pill Success", BadgeColor.SUCCESS, BadgeSize.M, BadgeShape.PILL)
                new Badge("Pill Error",   BadgeColor.ERROR,   BadgeSize.M, BadgeShape.PILL)""");
    }

    private DemoExample smallExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.add(
                new Badge("Small", BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.NORMAL),
                new Badge("Small Pill", BadgeColor.CONTRAST, BadgeSize.S, BadgeShape.PILL)
        );

        return new DemoExample("Small Size", layout, """
                new Badge("Small",      BadgeColor.NORMAL_PRIMARY, BadgeSize.S, BadgeShape.NORMAL)
                new Badge("Small Pill", BadgeColor.CONTRAST,       BadgeSize.S, BadgeShape.PILL)""");
    }
}
