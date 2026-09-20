package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Ribbon;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the {@link Ribbon} component.
 *
 * <p>Covers the three TailAdmin ribbon shapes (Rounded, Shape, Filed) and the colour variants.
 */
@PageTitle("Ribbons – Holon Demo")
@Route(value = "ribbons", layout = DemoMainLayout.class)
public class RibbonDemoView extends Div {

    private static final String LOREM =
            "Lorem ipsum dolor sit amet consectetur. Eget nulla suscipit arcu rutrum amet vel nec "
                    + "fringilla vulputate. Sed aliquam fringilla vulputate imperdiet arcu natoque purus ac.";

    public RibbonDemoView() {
        addClassName("app-view");

        H1 title = new H1("Ribbons");
        Paragraph desc = new Paragraph(
                "A card decorated with a corner/edge ribbon label. Three shapes (Rounded, Shape, Filed) "
                        + "and six colour variants, all theme-agnostic.");

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(shapesExample(), colorsExample());

        add(title, desc, examples);
    }

    private DemoExample shapesExample() {
        Div grid = new Div();
        grid.addClassName("ribbon-grid");
        grid.add(
                card(Ribbon.Variant.ROUNDED, "Popular", Ribbon.Color.PRIMARY, "Rounded Ribbon"),
                card(Ribbon.Variant.SHAPE, "Popular", Ribbon.Color.PRIMARY, "Ribbon With Shape"),
                card(Ribbon.Variant.FILED, "New", Ribbon.Color.SUCCESS, "Filed Ribbon"));

        return new DemoExample("Ribbon shapes", grid, """
                Ribbon rounded = Components.ribbon()
                    .variant(Ribbon.Variant.ROUNDED)
                    .label("Popular")
                    .content(new H3("Rounded Ribbon"), new Paragraph("..."))
                    .build();

                Ribbon filed = Components.ribbon()
                    .variant(Ribbon.Variant.FILED)
                    .color(Ribbon.Color.SUCCESS)
                    .label("New")
                    .build();
                """);
    }

    private DemoExample colorsExample() {
        Div grid = new Div();
        grid.addClassName("ribbon-grid");
        for (Ribbon.Color color : Ribbon.Color.values()) {
            String label = color.name().charAt(0) + color.name().substring(1).toLowerCase();
            grid.add(card(Ribbon.Variant.SHAPE, label, color, label + " ribbon"));
        }

        return new DemoExample("Colour variants", grid, """
                Components.ribbon().variant(Ribbon.Variant.SHAPE).color(Ribbon.Color.SUCCESS).label("Success");
                Components.ribbon().variant(Ribbon.Variant.SHAPE).color(Ribbon.Color.WARNING).label("Warning");
                Components.ribbon().variant(Ribbon.Variant.SHAPE).color(Ribbon.Color.ERROR).label("Error");
                // … PRIMARY, INFO, DARK
                """);
    }

    private static Ribbon card(Ribbon.Variant variant, String label, Ribbon.Color color, String heading) {
        return Components.ribbon()
                .variant(variant)
                .color(color)
                .label(label)
                .content(new H3(heading), new Paragraph(LOREM))
                .build();
    }
}
