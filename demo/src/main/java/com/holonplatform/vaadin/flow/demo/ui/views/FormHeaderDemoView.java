package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("FormHeader – Holon Demo")
@Route(value = "form-header", layout = DemoMainLayout.class)
public class FormHeaderDemoView extends Div {

    public FormHeaderDemoView() {
        addClassName("app-view");

        var title = new H1("FormHeader");

        var desc = new Paragraph(
                "FormHeaderBuilder creates a form header bar with a title "
                + "and optional edit, close, and options buttons.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withButtonsExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var header = FormHeaderBuilder.create()
                .title("Customer Details")
                .build();

        return new DemoExample("Basic FormHeader", header, """
                FormHeaderBuilder.create()
                    .title("Customer Details")
                    .build();""");
    }

    private DemoExample withButtonsExample() {
        var header = FormHeaderBuilder.create()
                .title("Edit Order")
                .editBtnConfigurator(btn -> btn.text("Edit"))
                .closeBtnConfigurator(btn -> btn.text("Close"))
                .build();

        return new DemoExample("With Edit & Close Buttons", header, """
                FormHeaderBuilder.create()
                    .title("Edit Order")
                    .editBtnConfigurator(btn -> btn.text("Edit"))
                    .closeBtnConfigurator(btn -> btn.text("Close"))
                    .build();""");
    }
}
