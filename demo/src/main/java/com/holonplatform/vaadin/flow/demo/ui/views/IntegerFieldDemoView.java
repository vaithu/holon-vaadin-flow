package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("IntegerField – Holon Demo")
@Route(value = "integerfield", layout = DemoMainLayout.class)
public class IntegerFieldDemoView extends Div {

    public IntegerFieldDemoView() {
        addClassName("app-view");

        var title = new H1("IntegerField");

        var desc = new Paragraph(
                "IntegerField provides a native stepper control for whole numbers. "
                + "Vaadin's IntegerField can be used directly, or through "
                + "Holon's Input.numberField(Integer.class) which wraps a NumberField. "
                + "This demo shows both approaches.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(nativeBasicExample());
        examples.add(nativeMinMaxExample());
        examples.add(nativeStepExample());
        examples.add(holonIntegerExample());
        examples.add(holonFormattedExample());

        add(title, desc, examples);
    }

    private DemoExample nativeBasicExample() {
        var field = new IntegerField("Quantity");
        field.setPlaceholder("Enter quantity");
        field.setClearButtonVisible(true);

        return new DemoExample("Vaadin IntegerField – Basic", field, """
                var field = new IntegerField("Quantity");
                field.setPlaceholder("Enter quantity");
                field.setClearButtonVisible(true);""");
    }

    private DemoExample nativeMinMaxExample() {
        var field = new IntegerField("Rating (1–10)");
        field.setMin(1);
        field.setMax(10);
        field.setValue(5);
        field.setStepButtonsVisible(true);
        field.setHelperText("Must be between 1 and 10");

        return new DemoExample("Min / Max Constraints", field, """
                var field = new IntegerField("Rating (1-10)");
                field.setMin(1);
                field.setMax(10);
                field.setValue(5);
                field.setStepButtonsVisible(true);
                field.setHelperText("Must be between 1 and 10");""");
    }

    private DemoExample nativeStepExample() {
        var field = new IntegerField("Tickets");
        field.setMin(0);
        field.setMax(100);
        field.setStep(5);
        field.setValue(10);
        field.setStepButtonsVisible(true);
        field.setHelperText("Step by 5");
        field.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                Notification.show("Tickets: " + e.getValue());
            }
        });

        return new DemoExample("Step Buttons", field, """
                var field = new IntegerField("Tickets");
                field.setMin(0);
                field.setMax(100);
                field.setStep(5);
                field.setValue(10);
                field.setStepButtonsVisible(true);
                field.addValueChangeListener(e ->
                    Notification.show("Tickets: " + e.getValue()));""");
    }

    private DemoExample holonIntegerExample() {
        var input = Input.numberField(Integer.class)
                .label("Holon NumberField (Integer)")
                .placeholder("Whole numbers")
                .allowNegative(false)
                .tooltip("Uses Vaadin NumberField under the hood")
                .build();

        return new DemoExample("Holon Input.numberField(Integer.class)", input.getComponent(), """
                Input.numberField(Integer.class)
                    .label("Holon NumberField (Integer)")
                    .placeholder("Whole numbers")
                    .allowNegative(false)
                    .tooltip("Uses Vaadin NumberField under the hood")
                    .build();""");
    }

    private DemoExample holonFormattedExample() {
        var input = Input.number(Integer.class)
                .label("Holon Input.number (TextField-backed)")
                .useGrouping(true)
                .placeholder("e.g. 1,000")
                .helperText("TextField-backed with grouping separator")
                .build();

        return new DemoExample("Holon Input.number(Integer.class)", input.getComponent(), """
                Input.number(Integer.class)
                    .label("Holon Input.number (TextField-backed)")
                    .useGrouping(true)
                    .placeholder("e.g. 1,000")
                    .helperText("TextField-backed with grouping separator")
                    .build();""");
    }
}
