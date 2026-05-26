package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Tooltip – Holon Demo")
@Route(value = "tooltip", layout = DemoMainLayout.class)
public class TooltipDemoView extends Div {

    public TooltipDemoView() {
        addClassName("app-view");

        var title = new H1("Tooltip");

        var desc = new Paragraph(
                "Holon builders support .tooltip() for setting rich tooltips on components. "
                + "The tooltip is rendered via Vaadin's <vaadin-tooltip> element, supporting "
                + "localization via Localizable and reactive binding via Signal.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(buttonTooltipExample());
        examples.add(inputTooltipExample());
        examples.add(numberInputTooltipExample());
        examples.add(dateInputTooltipExample());

        add(title, desc, examples);
    }

    private DemoExample buttonTooltipExample() {
        var btn1 = ButtonBuilder.create().text("Save")
                .primary()
                .tooltip("Save the current document")
                .build();

        var btn2 = ButtonBuilder.create()
                .icon(VaadinIcon.TRASH)
                .icon()
                .tooltip("Delete this item permanently")
                .build();

        var btn3 = ButtonBuilder.create().text("Export")
                .secondary()
                .tooltip("Export data as CSV file")
                .build();

        var layout = new HorizontalLayout(btn1, btn2, btn3);
        return new DemoExample("Button Tooltips", layout, """
                ButtonBuilder.create().text("Save")
                    .primary()
                    .tooltip("Save the current document")
                    .build();

                ButtonBuilder.create()
                    .icon(VaadinIcon.TRASH).icon()
                    .tooltip("Delete this item permanently")
                    .build();

                ButtonBuilder.create().text("Export")
                    .secondary()
                    .tooltip("Export data as CSV file")
                    .build();""");
    }

    private DemoExample inputTooltipExample() {
        var nameInput = Input.string()
                .label("Username")
                .placeholder("e.g. john.doe")
                .tooltip("Enter your unique username (3–20 characters)")
                .build();

        var emailInput = Input.string()
                .label("Email")
                .placeholder("user@example.com")
                .tooltip("We'll never share your email")
                .build();

        var wrapper = new Div(nameInput.getComponent(), emailInput.getComponent());

        return new DemoExample("String Input Tooltips", wrapper, """
                Input.string()
                    .label("Username")
                    .placeholder("e.g. john.doe")
                    .tooltip("Enter your unique username (3-20 characters)")
                    .build();

                Input.string()
                    .label("Email")
                    .placeholder("user@example.com")
                    .tooltip("We'll never share your email")
                    .build();""");
    }

    private DemoExample numberInputTooltipExample() {
        var qtyInput = Input.numberField(Integer.class)
                .label("Quantity")
                .tooltip("Enter a whole number between 1 and 100")
                .build();

        var priceInput = Input.numberField(Double.class)
                .label("Price")
                .tooltip("Enter the unit price in USD")
                .build();

        var wrapper = new Div(qtyInput.getComponent(), priceInput.getComponent());

        return new DemoExample("Number Input Tooltips", wrapper, """
                Input.numberField(Integer.class)
                    .label("Quantity")
                    .tooltip("Enter a whole number between 1 and 100")
                    .build();

                Input.numberField(Double.class)
                    .label("Price")
                    .tooltip("Enter the unit price in USD")
                    .build();""");
    }

    private DemoExample dateInputTooltipExample() {
        var dateInput = Input.localDate()
                .label("Start Date")
                .tooltip("Select the project start date")
                .build();

        var timeInput = Input.localTime()
                .label("Meeting Time")
                .tooltip("Choose meeting time (24h format)")
                .build();

        var wrapper = new Div(dateInput.getComponent(), timeInput.getComponent());

        return new DemoExample("Date/Time Input Tooltips", wrapper, """
                Input.localDate()
                    .label("Start Date")
                    .tooltip("Select the project start date")
                    .build();

                Input.localTime()
                    .label("Meeting Time")
                    .tooltip("Choose meeting time (24h format)")
                    .build();""");
    }
}
