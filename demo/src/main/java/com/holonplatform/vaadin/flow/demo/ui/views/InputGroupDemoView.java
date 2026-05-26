package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroupText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link InputGroup} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Text prefix addon (@username)</li>
 *   <li>Text suffix addon (price .00)</li>
 *   <li>Both prefix and suffix addons (currency)</li>
 *   <li>Button at the end (search bar)</li>
 *   <li>Select + input (country phone prefix)</li>
 *   <li>Date range picker</li>
 *   <li>Responsive stacking modifier</li>
 * </ol>
 */
@PageTitle("InputGroup – Holon Demo")
@Route(value = "input-group", layout = DemoMainLayout.class)
public class InputGroupDemoView extends Div {

    public InputGroupDemoView() {
        addClassName("app-view");

        var title = new H1("InputGroup");

        var desc = new Paragraph(
                "A horizontal flex container that merges input fields, buttons, and " +
                "InputGroupText addons into a single, visually unified control. " +
                "Adjacent borders are removed and border-radius is applied only to the " +
                "outermost edges, creating a seamless composite widget.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(textPrefixExample());
        examples.add(textSuffixExample());
        examples.add(bothAddonsExample());
        examples.add(searchBarExample());
        examples.add(phoneWithSelectExample());
        examples.add(dateRangeExample());
        examples.add(responsiveExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample textPrefixExample() {
        var usernameField = new TextField();
        usernameField.setPlaceholder("username");

        var group = new InputGroup(
                new InputGroupText("@"),
                usernameField
        );

        return new DemoExample("Text Prefix Addon", group, """
                // InputGroupText renders a static <span> fused to the adjacent input.
                TextField usernameField = new TextField();
                usernameField.setPlaceholder("username");

                InputGroup group = new InputGroup(
                    new InputGroupText("@"),
                    usernameField
                );
                """);
    }

    private DemoExample textSuffixExample() {
        var field = new TextField();
        field.setPlaceholder("domain");
        var group = new InputGroup(field, new InputGroupText(".com"));

        return new DemoExample("Text Suffix Addon", group, """
                InputGroup group = new InputGroup(
                    Components.input.string().placeholder("domain").build(),
                    new InputGroupText(".com")
                );
                """);
    }

    private DemoExample bothAddonsExample() {
        var numField = new NumberField();
        numField.setPlaceholder("0.00");
        var group = new InputGroup(
                new InputGroupText("$"),
                numField,
                new InputGroupText(".00")
        );

        return new DemoExample("Prefix + Suffix Addons (Price)", group, """
                NumberField amount = new NumberField();
                amount.setPlaceholder("0.00");

                InputGroup group = new InputGroup(
                    new InputGroupText("$"),
                    amount,
                    new InputGroupText(".00")
                );
                """);
    }

    private DemoExample searchBarExample() {
        var searchField = new TextField();
        searchField.setPlaceholder("Search products…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());

        var searchBtn = new Button("Search");

        var group = new InputGroup(searchField, searchBtn);

        return new DemoExample("Search Field + Button", group, """
                TextField search = new TextField();
                search.setPlaceholder("Search products…");
                search.setPrefixComponent(VaadinIcon.SEARCH.create());

                InputGroup group = new InputGroup(search, new Button("Search"));
                """);
    }

    private DemoExample phoneWithSelectExample() {
        var countrySelect = new Select<String>();
        countrySelect.setItems("+1", "+44", "+49", "+33", "+81");
        countrySelect.setValue("+1");

        var phoneField = new TextField();
        phoneField.setPlaceholder("Phone number");

        var group = new InputGroup(countrySelect, phoneField);

        return new DemoExample("Select + Input (Phone with Country Code)", group, """
                Select<String> countryCode = new Select<>();
                countryCode.setItems("+1", "+44", "+49", "+33", "+81");
                countryCode.setValue("+1");

                TextField phone = new TextField();
                phone.setPlaceholder("Phone number");

                InputGroup group = new InputGroup(countryCode, phone);
                """);
    }

    private DemoExample dateRangeExample() {
        var from = new DatePicker();
        from.setPlaceholder("From");
        var to = new DatePicker();
        to.setPlaceholder("To");

        var group = new InputGroup(
                new InputGroupText("From"),
                from,
                new InputGroupText("To"),
                to
        );

        return new DemoExample("Date Range", group, """
                InputGroup group = new InputGroup(
                    new InputGroupText("From"), new DatePicker(),
                    new InputGroupText("To"),   new DatePicker()
                );
                """);
    }

    private DemoExample responsiveExample() {
        var searchField = new TextField();
        searchField.setPlaceholder("Search…");
        var goBtn = new Button("Go");

        var group = new InputGroup(searchField, goBtn);
        // CSS modifier class that stacks children vertically on very small screens
        group.addClassName("input-group--responsive");

        var note = new Paragraph("Resize the window below the sm breakpoint to see the group stack vertically.");

        var wrapper = new Div(group, note);

        return new DemoExample("Responsive Stacking (input-group--responsive)", wrapper, """
                // Adding the modifier class enables vertical stacking on small screens.
                InputGroup group = new InputGroup(searchField, new Button("Go"));
                group.addClassName("input-group--responsive");
                """);
    }
}


