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


@PageTitle("Inputs – Holon Demo")
@Route(value = "inputs", layout = DemoMainLayout.class)
public class InputsDemoView extends Div {

    public InputsDemoView() {
        addClassName("app-view");

        var title = new H1("Inputs");

        var desc = new Paragraph(
                "Holon fluent Input builders for text, number, boolean, date, and password fields. "
                + "All builders are accessed via Input.string(), Input.number(), etc.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(stringExample());
        examples.add(stringAreaExample());
        examples.add(numberExample());
        examples.add(booleanExample());
        examples.add(passwordExample());
        examples.add(localDateExample());
        examples.add(localDateTimeExample());
        examples.add(localTimeExample());

        add(title, desc, examples);
    }

    private DemoExample stringExample() {
        var input = Input.string()
                .label("Name")
                .placeholder("Enter your name")
                .build();

        return new DemoExample("String Input", input.getComponent(), """
                Input.string()
                    .label("Name")
                    .placeholder("Enter your name")
                    .build();""");
    }

    private DemoExample stringAreaExample() {
        var input = Input.stringArea()
                .label("Description")
                .placeholder("Enter description...")
                .build();

        return new DemoExample("String Area Input", input.getComponent(), """
                Input.stringArea()
                    .label("Description")
                    .placeholder("Enter description...")
                    .build();""");
    }

    private DemoExample numberExample() {
        var intInput = Input.number(Integer.class)
                .label("Quantity")
                .build();

        var doubleInput = Input.number(Double.class)
                .label("Price")
                .build();

        var wrapper = new Div(intInput.getComponent(), doubleInput.getComponent());
        wrapper.getStyle().set("display", "flex");
        wrapper.getStyle().set("gap", "var(--space-m)");

        return new DemoExample("Number Input", wrapper, """
                Input.number(Integer.class)
                    .label("Quantity")
                    .build();
                Input.number(Double.class)
                    .label("Price")
                    .build();""");
    }

    private DemoExample booleanExample() {
        var input = Input.boolean_()
                .label("Active")
                .build();

        return new DemoExample("Boolean Input (Checkbox)", input.getComponent(), """
                Input.boolean_()
                    .label("Active")
                    .build();""");
    }

    private DemoExample passwordExample() {
        var input = Input.password()
                .label("Password")
                .placeholder("Enter password")
                .build();

        return new DemoExample("Password Input", input.getComponent(), """
                Input.password()
                    .label("Password")
                    .placeholder("Enter password")
                    .build();""");
    }

    private DemoExample localDateExample() {
        var input = Input.localDate()
                .label("Birth Date")
                .build();

        return new DemoExample("LocalDate Input", input.getComponent(), """
                Input.localDate()
                    .label("Birth Date")
                    .build();""");
    }

    private DemoExample localDateTimeExample() {
        var input = Input.localDateTime()
                .label("Appointment")
                .build();

        return new DemoExample("LocalDateTime Input", input.getComponent(), """
                Input.localDateTime()
                    .label("Appointment")
                    .build();""");
    }

    private DemoExample localTimeExample() {
        var input = Input.localTime()
                .label("Start Time")
                .build();

        return new DemoExample("LocalTime Input", input.getComponent(), """
                Input.localTime()
                    .label("Start Time")
                    .build();""");
    }
}
