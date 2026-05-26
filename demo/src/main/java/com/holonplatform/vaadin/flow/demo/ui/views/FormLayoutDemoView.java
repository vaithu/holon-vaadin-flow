package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("FormLayout – Holon Demo")
@Route(value = "form-layout", layout = DemoMainLayout.class)
public class FormLayoutDemoView extends Div {

    public FormLayoutDemoView() {
        addClassName("app-view");

        var title = new H1("FormLayout");

        var desc = new Paragraph(
                "FormLayoutBuilder provides a fluent API for Vaadin FormLayout "
                + "with responsive steps and column spanning.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(responsiveExample());
        examples.add(colSpanExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var firstName = new TextField("First Name");
        var lastName = new TextField("Last Name");
        var email = new EmailField("Email");

        var form = FormLayoutBuilder.create()
                .add(firstName, lastName, email)
                .build();

        return new DemoExample("Basic Form", form, """
                FormLayoutBuilder.create()
                    .add(firstNameField, lastNameField, emailField)
                    .build();""");
    }

    private DemoExample responsiveExample() {
        var firstName = new TextField("First Name");
        var lastName = new TextField("Last Name");
        var email = new EmailField("Email");
        var phone = new TextField("Phone");

        var form = FormLayoutBuilder.create()
                .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("500px", 2))
                .add(firstName, lastName, email, phone)
                .build();

        return new DemoExample("Responsive Steps", form, """
                FormLayoutBuilder.create()
                    .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("500px", 2))
                    .add(firstName, lastName, email, phone)
                    .build();""");
    }

    private DemoExample colSpanExample() {
        var firstName = new TextField("First Name");
        var lastName = new TextField("Last Name");
        var address = new TextField("Address");

        var form = FormLayoutBuilder.create()
                .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("500px", 2))
                .add(firstName, lastName)
                .add(2, address)
                .build();

        return new DemoExample("Column Spanning", form, """
                FormLayoutBuilder.create()
                    .responsiveSteps(
                        new ResponsiveStep("0", 1),
                        new ResponsiveStep("500px", 2))
                    .add(firstName, lastName)
                    .add(2, address)  // spans 2 columns
                    .build();""");
    }
}
