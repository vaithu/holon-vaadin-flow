package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Select – Holon Demo")
@Route(value = "select", layout = DemoMainLayout.class)
public class SelectDemoView extends Div {

    private enum Color { RED, GREEN, BLUE, YELLOW }

    public SelectDemoView() {
        addClassName("app-view");

        var title = new H1("Select Inputs");

        var desc = new Paragraph(
                "Holon fluent select builders: filterable single select (ComboBox), "
                + "enum select, radio-button options, list select, and multi-select variants.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(singleSelectExample());
        examples.add(enumSelectExample());
        examples.add(optionsSingleExample());
        examples.add(listSingleExample());
        examples.add(multiOptionExample());
        examples.add(multiListExample());

        add(title, desc, examples);
    }

    private DemoExample singleSelectExample() {
        var input = Input.singleSelect(String.class)
                .items("Apple", "Banana", "Cherry", "Date")
                .label("Fruit")
                .placeholder("Select a fruit")
                .build();

        return new DemoExample("Filterable Single Select (ComboBox)", input.getComponent(), """
                Input.singleSelect(String.class)
                    .items("Apple", "Banana", "Cherry", "Date")
                    .label("Fruit")
                    .placeholder("Select a fruit")
                    .build();""");
    }

    private DemoExample enumSelectExample() {
        var input = Input.enumSelect(Color.class)
                .label("Color")
                .build();

        return new DemoExample("Enum Select", input.getComponent(), """
                Input.enumSelect(Color.class)
                    .label("Color")
                    .build();""");
    }

    private DemoExample optionsSingleExample() {
        var input = Input.singleOptionSelect(String.class)
                .items("Small", "Medium", "Large")
                .label("Size (Radio Buttons)")
                .build();

        return new DemoExample("Options Single Select (RadioButtonGroup)", input.getComponent(), """
                Input.singleOptionSelect(String.class)
                    .items("Small", "Medium", "Large")
                    .label("Size (Radio Buttons)")
                    .build();""");
    }

    private DemoExample listSingleExample() {
        var input = Input.singleListSelect(String.class)
                .items("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .build();

        return new DemoExample("List Single Select (ListBox)", input.getComponent(), """
                Input.singleListSelect(String.class)
                    .items("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                    .build();""");
    }

    private DemoExample multiOptionExample() {
        var input = Input.multiOptionSelect(String.class)
                .items("Java", "Python", "TypeScript", "Go", "Rust")
                .label("Languages (CheckboxGroup)")
                .build();

        return new DemoExample("Multi-Select Options (CheckboxGroup)", input.getComponent(), """
                Input.multiOptionSelect(String.class)
                    .items("Java", "Python", "TypeScript", "Go", "Rust")
                    .label("Languages (CheckboxGroup)")
                    .build();""");
    }

    private DemoExample multiListExample() {
        var input = Input.multiListSelect(String.class)
                .items("Read", "Write", "Execute", "Admin")
                .build();

        return new DemoExample("Multi-Select List (MultiSelectListBox)", input.getComponent(), """
                Input.multiListSelect(String.class)
                    .items("Read", "Write", "Execute", "Admin")
                    .build();""");
    }
}
