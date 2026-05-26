package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link SingleSelect} Holon Input variants.
 *
 * <p>Covers:
 * <ol>
 *   <li>Filterable single select (ComboBox-backed)</li>
 *   <li>Simple single select (Select-backed)</li>
 *   <li>Options single select (RadioButtonGroup-backed)</li>
 *   <li>List single select (ListBox-backed)</li>
 *   <li>Enum select (auto-items from enum constants)</li>
 *   <li>Enum option select (RadioButtonGroup)</li>
 * </ol>
 */
@PageTitle("SingleSelect – Holon Demo")
@Route(value = "single-select", layout = DemoMainLayout.class)
public class SingleSelectDemoView extends Div {

    private enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    private enum Department { ENGINEERING, DESIGN, PRODUCT, MARKETING, SALES, SUPPORT }

    public SingleSelectDemoView() {
        addClassName("app-view");

        var title = new H1("SingleSelect");

        var desc = new Paragraph(
                "Holon SingleSelect Input builders create type-safe single-value selection "
                + "components backed by various Vaadin widgets: ComboBox (filterable), "
                + "Select (simple dropdown), RadioButtonGroup (options), and ListBox (list). "
                + "Enum-aware builders automatically populate items from enum constants.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(filterableExample());
        examples.add(simpleSelectExample());
        examples.add(optionsExample());
        examples.add(listSelectExample());
        examples.add(enumSelectExample());
        examples.add(enumOptionSelectExample());

        add(title, desc, examples);
    }

    private DemoExample filterableExample() {
        var selected = new Span("No selection");

        var select = Input.singleSelect(String.class)
                .items("New York", "London", "Paris", "Tokyo", "Berlin", "Sydney", "Toronto", "Singapore")
                .label("City")
                .placeholder("Search cities…")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Selected: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("Filterable (ComboBox)", container, """
                // Input.singleSelect() creates a ComboBox — user can type to filter.
                var select = Input.singleSelect(String.class)
                    .items("New York", "London", "Paris", "Tokyo", "Berlin")
                    .label("City")
                    .placeholder("Search cities…")
                    .build();

                select.addValueChangeListener(e ->
                    log.info("Selected: {}", e.getValue()));
                """);
    }

    private DemoExample simpleSelectExample() {
        var selected = new Span("No selection");

        var select = Input.singleSimpleSelect(String.class)
                .items("Draft", "Submitted", "Approved", "Rejected")
                .label("Status")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Status: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("Simple Select (Select dropdown)", container, """
                // Input.singleSimpleSelect() creates a Vaadin Select — compact dropdown.
                var select = Input.singleSimpleSelect(String.class)
                    .items("Draft", "Submitted", "Approved", "Rejected")
                    .label("Status")
                    .build();
                """);
    }

    private DemoExample optionsExample() {
        var selected = new Span("No selection");

        var select = Input.singleOptionSelect(String.class)
                .items("Small", "Medium", "Large", "Extra Large")
                .label("T-Shirt Size")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Size: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("Options (RadioButtonGroup)", container, """
                // Input.singleOptionSelect() uses a RadioButtonGroup — all options visible.
                var select = Input.singleOptionSelect(String.class)
                    .items("Small", "Medium", "Large", "Extra Large")
                    .label("T-Shirt Size")
                    .build();
                """);
    }

    private DemoExample listSelectExample() {
        var selected = new Span("No selection");

        var select = Input.singleListSelect(String.class)
                .items("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Selected: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("List Select (ListBox)", container, """
                // Input.singleListSelect() uses a ListBox — scrollable list.
                var select = Input.singleListSelect(String.class)
                    .items("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                    .build();
                """);
    }

    private DemoExample enumSelectExample() {
        var selected = new Span("No selection");

        var select = Input.enumSelect(Department.class)
                .label("Department")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Dept: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("Enum Select (ComboBox)", container, """
                // Input.enumSelect() populates all enum constants automatically.
                // Uses human-readable captions by default.
                var select = Input.enumSelect(Department.class)
                    .label("Department")
                    .build();
                """);
    }

    private DemoExample enumOptionSelectExample() {
        var selected = new Span("No selection");

        var select = Input.enumOptionSelect(Priority.class)
                .label("Priority")
                .build();

        select.addValueChangeListener(e ->
                selected.setText(e.getValue() == null ? "No selection" : "Priority: " + e.getValue()));

        var container = new Div(select.getComponent(), selected);

        return new DemoExample("Enum Options (RadioButtonGroup)", container, """
                // Input.enumOptionSelect() shows all enum values as radio buttons.
                var select = Input.enumOptionSelect(Priority.class)
                    .label("Priority")
                    .build();
                """);
    }
}
