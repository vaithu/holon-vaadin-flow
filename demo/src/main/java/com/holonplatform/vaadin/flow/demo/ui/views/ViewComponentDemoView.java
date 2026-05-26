package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link ViewComponent} and {@link PropertyViewForm}.
 *
 * <p>Covers:
 * <ol>
 *   <li>ViewComponent – basic string display</li>
 *   <li>ViewComponent – custom value converter</li>
 *   <li>ViewComponent – programmatic value update</li>
 *   <li>PropertyViewForm – read-only property display (FormLayout)</li>
 *   <li>PropertyViewForm – pre-populated display</li>
 * </ol>
 */
@PageTitle("ViewComponent – Holon Demo")
@Route(value = "view-component", layout = DemoMainLayout.class)
public class ViewComponentDemoView extends Div {

    // ── Holon properties ─────────────────────────────────────────────────────
    private static final StringProperty NAME = StringProperty.create("name").message("Name");
    private static final StringProperty EMAIL = StringProperty.create("email").message("Email");
    private static final StringProperty DEPARTMENT = StringProperty.create("department").message("Department");
    private static final NumericProperty<Double> SALARY = NumericProperty.doubleType("salary").message("Salary");
    private static final StringProperty STATUS = StringProperty.create("status").message("Status");

    private static final PropertySet<?> EMPLOYEE_SET = PropertySet.of(NAME, EMAIL, DEPARTMENT, SALARY, STATUS);

    public ViewComponentDemoView() {
        addClassName("app-view");

        var title = new H1("ViewComponent / PropertyViewForm");

        var desc = new Paragraph(
                "ViewComponent displays a read-only value in UI — it's the display counterpart "
                + "to Input. PropertyViewForm groups ViewComponents for a PropertySet, "
                + "producing a read-only form layout.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicViewComponentExample());
        examples.add(customConverterExample());
        examples.add(programmaticUpdateExample());
        examples.add(viewFormExample());
        examples.add(prePopulatedFormExample());

        add(title, desc, examples);
    }

    // ── Examples ─────────────────────────────────────────────────────────────

    private DemoExample basicViewComponentExample() {
        var vc = ViewComponent.builder(String.class).build();
        vc.setValue("Hello, Holon ViewComponent!");

        return new DemoExample("Basic ViewComponent (String)", vc.getComponent(), """
                ViewComponent<String> vc = ViewComponent.builder(String.class).build();
                vc.setValue("Hello, Holon ViewComponent!");
                """);
    }

    private DemoExample customConverterExample() {
        var vc = ViewComponent.builder((Double v) ->
                v == null ? "—" : String.format("$%,.2f", v)).build();
        vc.setValue(128430.50);

        return new DemoExample("ViewComponent – Custom Value Converter", vc.getComponent(), """
                // The converter function transforms the value for display.
                ViewComponent<Double> vc = ViewComponent.builder(
                    (Double v) -> v == null ? "—" : String.format("$%,.2f", v)
                ).build();
                vc.setValue(128430.50);
                // Renders: "$128,430.50"
                """);
    }

    private DemoExample programmaticUpdateExample() {
        var vc = ViewComponent.builder(String.class).build();
        vc.setValue("Initial value");

        var btn = new Button("Update Value", e ->
                vc.setValue("Updated at " + java.time.LocalTime.now().withNano(0)));

        var container = new Div(vc.getComponent(), btn);

        return new DemoExample("Programmatic Value Update", container, """
                ViewComponent<String> vc = ViewComponent.builder(String.class).build();
                vc.setValue("Initial value");

                button.addClickListener(e -> vc.setValue("Updated!"));
                """);
    }

    private DemoExample viewFormExample() {
        var form = PropertyViewForm.formLayout(NAME, EMAIL, DEPARTMENT, SALARY, STATUS)
                .build();

        form.setValue(PropertyBox.builder(EMPLOYEE_SET)
                .set(NAME, "Jane Smith")
                .set(EMAIL, "jane.smith@acme.com")
                .set(DEPARTMENT, "Engineering")
                .set(SALARY, 95000.0)
                .set(STATUS, "Active")
                .build());

        return new DemoExample("PropertyViewForm (FormLayout)", form.getComponent(), """
                // PropertyViewForm auto-creates ViewComponents for each property.
                var form = PropertyViewForm.formLayout(NAME, EMAIL, DEPARTMENT, SALARY, STATUS)
                    .build();

                // Set the PropertyBox to display:
                form.setValue(PropertyBox.builder(SET)
                    .set(NAME, "Jane Smith")
                    .set(EMAIL, "jane.smith@acme.com")
                    .set(DEPARTMENT, "Engineering")
                    .set(SALARY, 95000.0)
                    .set(STATUS, "Active")
                    .build());
                """);
    }

    private DemoExample prePopulatedFormExample() {
        var form = PropertyViewForm.formLayout(NAME, EMAIL, DEPARTMENT, SALARY, STATUS)
                .build();

        var record1 = PropertyBox.builder(EMPLOYEE_SET)
                .set(NAME, "Alice Johnson").set(EMAIL, "alice@example.com")
                .set(DEPARTMENT, "Product").set(SALARY, 110000.0).set(STATUS, "Active")
                .build();

        var record2 = PropertyBox.builder(EMPLOYEE_SET)
                .set(NAME, "Bob Chen").set(EMAIL, "bob@example.com")
                .set(DEPARTMENT, "Design").set(SALARY, 88000.0).set(STATUS, "On Leave")
                .build();

        form.setValue(record1);

        var switchBtn = new Button("Switch Record", e -> {
            var current = form.getValue();
            form.setValue("Alice Johnson".equals(current.getValue(NAME)) ? record2 : record1);
        });

        var container = new Div(form.getComponent(), switchBtn);

        return new DemoExample("Switch Between Records", container, """
                // Call setValue() with different PropertyBox instances to switch display.
                form.setValue(record1);

                switchButton.addClickListener(e -> form.setValue(record2));
                """);
    }
}
