package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.Validator;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link ValidatableInput} — Holon validation patterns.
 *
 * <p>Covers:
 * <ol>
 *   <li>Wrapping an Input with ValidatableInput.from()</li>
 *   <li>Adding validators (required, pattern, min/max)</li>
 *   <li>Validate-on-value-change mode</li>
 *   <li>Builder-based validation with required() and withValidator()</li>
 *   <li>Programmatic validation with isValid() and getValue()</li>
 * </ol>
 */
@PageTitle("ValidatableInput – Holon Demo")
@Route(value = "validatable-input", layout = DemoMainLayout.class)
public class ValidatableInputDemoView extends Div {

    public ValidatableInputDemoView() {
        addClassName("app-view");

        var title = new H1("ValidatableInput");

        var desc = new Paragraph(
                "Holon ValidatableInput wraps any Input with Validator support. "
                + "Use ValidatableInput.from(input) or the builder's .required() and "
                + ".withValidator() methods. Validation runs on getValue() or on every "
                + "value change when setValidateOnValueChange(true) is enabled.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicValidationExample());
        examples.add(multipleValidatorsExample());
        examples.add(validateOnChangeExample());
        examples.add(builderValidationExample());
        examples.add(getValueIfValidExample());

        add(title, desc, examples);
    }

    /**
     * Example 1: Wrap an Input with ValidatableInput and add a required validator.
     */
    private DemoExample basicValidationExample() {
        var status = new Span("Not validated yet");

        var input = Input.string()
                .label("Username")
                .placeholder("Enter username")
                .build();

        var validatable = ValidatableInput.from(input);
        validatable.addValidator(Validator.notBlank());

        var validateBtn = new Button("Validate", e -> {
            try {
                String value = validatable.getValue();
                status.setText("Valid: " + value);
            } catch (Exception ex) {
                status.setText("Invalid: " + ex.getMessage());
            }
        });

        var container = new Div(input.getComponent(), validateBtn, status);

        return new DemoExample("Basic Validation", container, """
                var input = Input.string()
                    .label("Username")
                    .build();

                // Wrap with ValidatableInput to add validators.
                var validatable = ValidatableInput.from(input);
                validatable.addValidator(Validator.notBlank());

                // getValue() throws ValidationException if invalid.
                try {
                    String value = validatable.getValue();
                } catch (ValidationException e) {
                    // handle validation error
                }
                """);
    }

    /**
     * Example 2: Multiple validators — required + min length + pattern.
     */
    private DemoExample multipleValidatorsExample() {
        var status = new Span("Not validated yet");

        var input = Input.string()
                .label("Email")
                .placeholder("user@example.com")
                .build();

        var validatable = ValidatableInput.from(input);
        validatable.addValidator(Validator.notBlank());
        validatable.addValidator(Validator.create(
                v -> v != null && v.contains("@"),
                "Must contain @"));

        var validateBtn = new Button("Validate", e -> {
            try {
                String value = validatable.getValue();
                status.setText("Valid: " + value);
            } catch (Exception ex) {
                status.setText("Invalid: " + ex.getMessage());
            }
        });

        var container = new Div(input.getComponent(), validateBtn, status);

        return new DemoExample("Multiple Validators", container, """
                var validatable = ValidatableInput.from(input);

                // Chain multiple validators — all must pass.
                validatable.addValidator(Validator.notBlank());
                validatable.addValidator(Validator.create(
                    v -> v != null && v.contains("@"),
                    "Must contain @"));
                """);
    }

    /**
     * Example 3: Validate-on-value-change — auto-validates as user types.
     */
    private DemoExample validateOnChangeExample() {
        var status = new Span("Type to see live validation");

        var input = Input.string()
                .label("Password")
                .placeholder("Min 8 characters")
                .build();

        var validatable = ValidatableInput.from(input);
        validatable.addValidator(Validator.notBlank());
        validatable.addValidator(Validator.create(
                v -> v != null && v.length() >= 8,
                "Must be at least 8 characters"));
        validatable.setValidateOnValueChange(true);

        input.addValueChangeListener(e -> {
            if (validatable.isValid()) {
                status.setText("Valid password");
            } else {
                status.setText("Invalid — check requirements");
            }
        });

        var container = new Div(input.getComponent(), status);

        return new DemoExample("Validate On Change", container, """
                var validatable = ValidatableInput.from(input);
                validatable.addValidator(Validator.create(
                    v -> v != null && v.length() >= 8,
                    "Must be at least 8 characters"));

                // Enable auto-validation on every value change.
                validatable.setValidateOnValueChange(true);
                """);
    }

    /**
     * Example 4: Builder-based required() and withValidator() from Input builder.
     */
    private DemoExample builderValidationExample() {
        var status = new Span("Not validated yet");

        var input = Input.string()
                .label("Product Code")
                .placeholder("e.g., PRD-001")
                .required()
                .build();

        var validateBtn = new Button("Validate", e -> {
            try {
                input.getValue();
                status.setText("Valid: " + input.getValue());
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        var container = new Div(input.getComponent(), validateBtn, status);

        return new DemoExample("Builder Required()", container, """
                // Use .required() directly on the Input builder.
                var input = Input.string()
                    .label("Product Code")
                    .required()
                    .build();
                """);
    }

    /**
     * Example 5: getValueIfValid() — returns Optional.empty() on validation failure.
     */
    private DemoExample getValueIfValidExample() {
        var status = new Span("Not checked yet");

        var input = Input.number(Integer.class)
                .label("Age")
                .build();

        var validatable = ValidatableInput.from(input);
        validatable.addValidator(Validator.create(
                v -> v != null && v >= 18 && v <= 150,
                "Age must be between 18 and 150"));

        var checkBtn = new Button("Check Value", e -> {
            var maybeValue = validatable.getValueIfValid();
            if (maybeValue.isPresent()) {
                status.setText("Valid age: " + maybeValue.get());
            } else {
                status.setText("Invalid or empty — no exception thrown");
            }
        });

        var container = new Div(input.getComponent(), checkBtn, status);

        return new DemoExample("getValueIfValid()", container, """
                var validatable = ValidatableInput.from(input);
                validatable.addValidator(Validator.create(
                    v -> v != null && v >= 18 && v <= 150,
                    "Age must be 18-150"));

                // getValueIfValid() returns Optional.empty() on failure.
                // No exception is thrown — useful for conditional logic.
                var maybeValue = validatable.getValueIfValid();
                maybeValue.ifPresent(age -> process(age));
                """);
    }
}
