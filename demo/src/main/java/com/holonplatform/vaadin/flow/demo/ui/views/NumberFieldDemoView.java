package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("NumberField – Holon Demo")
@Route(value = "numberfield", layout = DemoMainLayout.class)
public class NumberFieldDemoView extends Div {

    public NumberFieldDemoView() {
        addClassName("app-view");

        var title = new H1("NumberField");

        var desc = new Paragraph(
                "Input.numberField() creates a Vaadin NumberField-backed numeric input. "
                + "It supports Integer, Long, Double, Float, and other Number types. "
                + "Features include label, placeholder, tooltip, locale, formatting, "
                + "negative number control, and decimals configuration.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicIntegerExample());
        examples.add(basicDoubleExample());
        examples.add(longExample());
        examples.add(formattedExample());
        examples.add(negativeDisabledExample());
        examples.add(valueChangeExample());

        add(title, desc, examples);
    }

    private DemoExample basicIntegerExample() {
        var input = Input.numberField(Integer.class)
                .label("Quantity")
                .placeholder("Enter quantity")
                .build();

        return new DemoExample("Integer NumberField", input.getComponent(), """
                Input.numberField(Integer.class)
                    .label("Quantity")
                    .placeholder("Enter quantity")
                    .build();""");
    }

    private DemoExample basicDoubleExample() {
        var input = Input.numberField(Double.class)
                .label("Price")
                .placeholder("0.00")
                .build();

        return new DemoExample("Double NumberField", input.getComponent(), """
                Input.numberField(Double.class)
                    .label("Price")
                    .placeholder("0.00")
                    .build();""");
    }

    private DemoExample longExample() {
        var input = Input.numberField(Long.class)
                .label("Record ID")
                .placeholder("Enter ID")
                .tooltip("Large integer values stored as Long")
                .build();

        return new DemoExample("Long NumberField", input.getComponent(), """
                Input.numberField(Long.class)
                    .label("Record ID")
                    .placeholder("Enter ID")
                    .tooltip("Large integer values stored as Long")
                    .build();""");
    }

    private DemoExample formattedExample() {
        var input = Input.numberField(Double.class)
                .label("Formatted Price (EUR)")
                .locale(Locale.GERMANY)
                .maxDecimals(2)
                .minDecimals(2)
                .build();

        return new DemoExample("Formatted (Locale & Decimals)", input.getComponent(), """
                Input.numberField(Double.class)
                    .label("Formatted Price (EUR)")
                    .locale(Locale.GERMANY)
                    .maxDecimals(2)
                    .minDecimals(2)
                    .build();""");
    }

    private DemoExample negativeDisabledExample() {
        var input = Input.numberField(Integer.class)
                .label("Age")
                .allowNegative(false)
                .placeholder("Positive numbers only")
                .helperText("Negative values are not accepted")
                .build();

        return new DemoExample("No Negatives", input.getComponent(), """
                Input.numberField(Integer.class)
                    .label("Age")
                    .allowNegative(false)
                    .placeholder("Positive numbers only")
                    .helperText("Negative values are not accepted")
                    .build();""");
    }

    private DemoExample valueChangeExample() {
        var input = Input.numberField(Double.class)
                .label("Amount")
                .withValueChangeListener(event -> {
                    Double val = event.getValue();
                    if (val != null) {
                        Notification.show("Value changed: " + val);
                    }
                })
                .build();

        return new DemoExample("Value Change Listener", input.getComponent(), """
                Input.numberField(Double.class)
                    .label("Amount")
                    .withValueChangeListener(event -> {
                        Double val = event.getValue();
                        if (val != null) {
                            Notification.show("Value changed: " + val);
                        }
                    })
                    .build();""");
    }
}
