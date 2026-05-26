package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.MaterialSymbol;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("MaterialSymbol – Holon Demo")
@Route(value = "material-symbol", layout = DemoMainLayout.class)
public class MaterialSymbolDemoView extends Div {

    public MaterialSymbolDemoView() {
        addClassName("app-view");

        var title = new H1("MaterialSymbol");

        var desc = new Paragraph(
                "MaterialSymbol is an enum of 3800+ Google Material Symbols. "
                + "Each value has a create() method that produces a <span> with the "
                + "'material-symbols' CSS class.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(commonIconsExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        Span icon = MaterialSymbol.HOME.create();

        var row = new HorizontalLayout(icon, new Span("Home icon"));
        row.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        row.setSpacing(true);

        return new DemoExample("Basic Usage", row, """
                Span icon = MaterialSymbol.HOME.create();""");
    }

    private DemoExample commonIconsExample() {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        layout.add(iconWithLabel(MaterialSymbol.SEARCH, "Search"));
        layout.add(iconWithLabel(MaterialSymbol.SETTINGS, "Settings"));
        layout.add(iconWithLabel(MaterialSymbol.DELETE, "Delete"));
        layout.add(iconWithLabel(MaterialSymbol.ADD, "Add"));
        layout.add(iconWithLabel(MaterialSymbol.EDIT, "Edit"));
        layout.add(iconWithLabel(MaterialSymbol.FAVORITE, "Favorite"));
        layout.add(iconWithLabel(MaterialSymbol.CHECK_CIRCLE, "Check"));
        layout.add(iconWithLabel(MaterialSymbol.WARNING, "Warning"));

        return new DemoExample("Common Icons", layout, """
                MaterialSymbol.SEARCH.create();
                MaterialSymbol.SETTINGS.create();
                MaterialSymbol.DELETE.create();
                MaterialSymbol.ADD.create();
                MaterialSymbol.EDIT.create();
                MaterialSymbol.FAVORITE.create();""");
    }

    private static Div iconWithLabel(MaterialSymbol symbol, String label) {
        var container = new Div();
        container.getStyle().set("display", "flex");
        container.getStyle().set("flex-direction", "column");
        container.getStyle().set("align-items", "center");
        container.getStyle().set("gap", "var(--space-xs)");
        container.add(symbol.create());
        var text = new Span(label);
        text.getStyle().set("font-size", "var(--font-size-xs)");
        container.add(text);
        return container;
    }
}
