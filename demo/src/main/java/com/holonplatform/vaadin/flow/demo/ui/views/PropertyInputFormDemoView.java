package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link PropertyInputForm} and {@link BeanPropertyInputForm}.
 *
 * <p>Covers:
 * <ol>
 *   <li>PropertyInputForm with Holon PropertySet + FormLayout</li>
 *   <li>PropertyInputForm with VerticalLayout</li>
 *   <li>BeanPropertyInputForm from a bean class</li>
 *   <li>BeanPropertyInputForm with setBean / getBean round-trip</li>
 *   <li>PropertyInputForm read-only toggle</li>
 * </ol>
 */
@PageTitle("PropertyInputForm – Holon Demo")
@Route(value = "property-input-form", layout = DemoMainLayout.class)
public class PropertyInputFormDemoView extends Div {

    // ── Holon properties ─────────────────────────────────────────────────────
    private static final StringProperty FIRST_NAME = StringProperty.create("firstName").message("First Name");
    private static final StringProperty LAST_NAME = StringProperty.create("lastName").message("Last Name");
    private static final StringProperty EMAIL = StringProperty.create("email").message("Email");
    private static final NumericProperty<Integer> AGE = NumericProperty.integerType("age").message("Age");
    private static final StringProperty CITY = StringProperty.create("city").message("City");

    private static final PropertySet<?> PERSON_SET = PropertySet.builderOf(FIRST_NAME, LAST_NAME, EMAIL, AGE, CITY)
            .withIdentifier(FIRST_NAME).build();

    // ── Bean for BeanPropertyInputForm ───────────────────────────────────────
    public static class Employee {
        private String name;
        private String department;
        private String role;
        private double salary;
        private boolean active;

        public Employee() {}

        public Employee(String name, String department, String role, double salary, boolean active) {
            this.name = name;
            this.department = department;
            this.role = role;
            this.salary = salary;
            this.active = active;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public double getSalary() { return salary; }
        public void setSalary(double salary) { this.salary = salary; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    // ── Constructor ──────────────────────────────────────────────────────────
    public PropertyInputFormDemoView() {
        addClassName("app-view");

        var title = new H1("PropertyInputForm");

        var desc = new Paragraph(
                "PropertyInputForm renders a set of Holon Properties as editable Input fields "
                + "inside a layout container (FormLayout, VerticalLayout, etc.). "
                + "BeanPropertyInputForm auto-introspects a Java bean class and provides "
                + "setBean()/getBean() for direct bean binding.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(formLayoutExample());
        examples.add(verticalLayoutExample());
        examples.add(beanFormExample());
        examples.add(beanRoundTripExample());
        examples.add(readOnlyToggleExample());

        add(title, desc, examples);
    }

    // ── Examples ─────────────────────────────────────────────────────────────

    private DemoExample formLayoutExample() {
        var form = PropertyInputForm.formLayout(FIRST_NAME, LAST_NAME, EMAIL, AGE, CITY)
                .build();

        var resultLabel = new Span("Submit to see values");

        var submitBtn = new Button("Submit", e -> {
            if (form.isValid()) {
                PropertyBox value = form.getValue();
                resultLabel.setText(String.format("%s %s, %s, age %s, %s",
                        value.getValue(FIRST_NAME), value.getValue(LAST_NAME),
                        value.getValue(EMAIL), value.getValue(AGE), value.getValue(CITY)));
            } else {
                Notification.show("Validation failed");
            }
        });

        var clearBtn = new Button("Clear", e -> form.clear());

        var container = new Div(form.getComponent(), submitBtn, clearBtn, resultLabel);

        return new DemoExample("PropertyInputForm (FormLayout)", container, """
                // Auto-generates Input fields for each Holon Property, laid out in a FormLayout.
                var form = PropertyInputForm.formLayout(FIRST_NAME, LAST_NAME, EMAIL, AGE, CITY)
                    .build();

                // Read values from the form:
                PropertyBox value = form.getValue();
                String name = value.getValue(FIRST_NAME);

                // Validate:
                boolean valid = form.isValid();

                // Clear all inputs:
                form.clear();
                """);
    }

    private DemoExample verticalLayoutExample() {
        var form = PropertyInputForm.verticalLayout(FIRST_NAME, LAST_NAME, EMAIL)
                .build();

        var resultLabel = new Span("Submit to see values");

        var submitBtn = new Button("Submit", e -> {
            PropertyBox value = form.getValue();
            resultLabel.setText("Values: " + value.getValue(FIRST_NAME)
                    + " " + value.getValue(LAST_NAME)
                    + ", " + value.getValue(EMAIL));
        });

        var container = new Div(form.getComponent(), submitBtn, resultLabel);

        return new DemoExample("PropertyInputForm (VerticalLayout)", container, """
                // VerticalLayout stacks inputs top-to-bottom (no responsive columns).
                var form = PropertyInputForm.verticalLayout(FIRST_NAME, LAST_NAME, EMAIL)
                    .build();

                PropertyBox value = form.getValue();
                """);
    }

    private DemoExample beanFormExample() {
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .build();

        var resultLabel = new Span("Submit to see values");

        var submitBtn = new Button("Submit", e -> {
            try {
                Employee emp = form.getBean();
                resultLabel.setText(String.format("Employee: %s, %s, %s, $%.2f, active=%s",
                        emp.getName(), emp.getDepartment(), emp.getRole(),
                        emp.getSalary(), emp.isActive()));
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        var container = new Div(form.getComponent(), submitBtn, resultLabel);

        return new DemoExample("BeanPropertyInputForm (auto-introspected)", container, """
                // BeanPropertyInputForm introspects the bean class and creates
                // matching Input fields automatically (String→TextField, double→NumberField, etc.)
                var form = BeanPropertyInputForm.formLayout(Employee.class)
                    .build();

                // Get the bean back:
                Employee emp = form.getBean();
                """);
    }

    private DemoExample beanRoundTripExample() {
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .build();

        var preloadBtn = new Button("Load Sample Data", e -> {
            form.setBean(new Employee("Jane Smith", "Engineering", "Senior Developer", 95000.0, true));
            Notification.show("Bean loaded into form");
        });

        var resultLabel = new Span("Load data, modify, then read back");

        var readBtn = new Button("Read Bean", e -> {
            try {
                Employee emp = form.getBean(false);
                resultLabel.setText(String.format("Read: %s, %s, $%.2f",
                        emp.getName(), emp.getDepartment(), emp.getSalary()));
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        var container = new Div(form.getComponent(), preloadBtn, readBtn, resultLabel);

        return new DemoExample("BeanPropertyInputForm – setBean / getBean round-trip", container, """
                // Pre-populate the form from an existing bean:
                form.setBean(existingEmployee);

                // Read back (with or without validation):
                Employee updated = form.getBean();            // validates
                Employee raw     = form.getBean(false);       // skips validation
                """);
    }

    private DemoExample readOnlyToggleExample() {
        var form = PropertyInputForm.formLayout(FIRST_NAME, LAST_NAME, EMAIL, AGE)
                .build();

        // Pre-populate via PropertyBox
        form.setValue(PropertyBox.builder(PERSON_SET)
                .set(FIRST_NAME, "Alice")
                .set(LAST_NAME, "Johnson")
                .set(EMAIL, "alice@example.com")
                .set(AGE, 30)
                .build());

        var readOnly = new boolean[]{false};
        var toggleBtn = new Button("Toggle Read-Only", e -> {
            readOnly[0] = !readOnly[0];
            form.setReadOnly(readOnly[0]);
        });

        var container = new Div(form.getComponent(), toggleBtn);

        return new DemoExample("Read-Only Toggle", container, """
                // Pre-populate:
                form.setValue(PropertyBox.builder(SET)
                    .set(FIRST_NAME, "Alice")
                    .set(LAST_NAME, "Johnson")
                    .build());

                // Toggle read-only mode:
                form.setReadOnly(true);   // make all inputs read-only
                form.setReadOnly(false);  // restore editing
                """);
    }
}
