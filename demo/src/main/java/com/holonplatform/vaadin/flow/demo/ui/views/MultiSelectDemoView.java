package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link MultiSelect} Holon Input variants.
 *
 * <p>Covers:
 * <ol>
 *   <li>Multi-option select (CheckboxGroup-backed)</li>
 *   <li>Multi-list select (MultiSelectListBox-backed)</li>
 *   <li>Enum multi select (CheckboxGroup with auto-items)</li>
 * </ol>
 */
@PageTitle("MultiSelect – Holon Demo")
@Route(value = "multi-select", layout = DemoMainLayout.class)
public class MultiSelectDemoView extends Div {

    private enum Permission { READ, WRITE, EXECUTE, DELETE, ADMIN }

    public MultiSelectDemoView() {
        addClassName("app-view");

        var title = new H1("MultiSelect");

        var desc = new Paragraph(
                "Holon MultiSelect Input builders create type-safe multi-value selection "
                + "components. Options (CheckboxGroup) shows all items with checkboxes; "
                + "List (MultiSelectListBox) presents a scrollable list. "
                + "Enum builders auto-populate from enum constants.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(multiOptionExample());
        examples.add(multiListExample());
        examples.add(enumMultiExample());

        add(title, desc, examples);
    }

    private DemoExample multiOptionExample() {
        var info = new Span("Selected: none");

        var multiSelect = Input.multiOptionSelect(String.class)
                .items("Java", "Kotlin", "Scala", "Groovy", "Clojure")
                .label("Programming languages")
                .build();

        multiSelect.addValueChangeListener(e -> {
            var vals = e.getValue();
            info.setText(vals.isEmpty() ? "Selected: none" : "Selected: " + String.join(", ", vals));
        });

        var container = new Div(multiSelect.getComponent(), info);

        return new DemoExample("Multi Options (CheckboxGroup)", container, """
                // Input.multiOptionSelect() creates a CheckboxGroup — all options visible.
                var multiSelect = Input.multiOptionSelect(String.class)
                    .items("Java", "Kotlin", "Scala", "Groovy", "Clojure")
                    .label("Programming languages")
                    .build();

                multiSelect.addValueChangeListener(e ->
                    log.info("Selected: {}", e.getValue()));
                """);
    }

    private DemoExample multiListExample() {
        var info = new Span("Selected: none");

        var multiSelect = Input.multiListSelect(String.class)
                .items("Read", "Write", "Execute", "Delete", "Admin", "Audit", "Export")
                .build();

        multiSelect.addValueChangeListener(e -> {
            var vals = e.getValue();
            info.setText(vals.isEmpty() ? "Selected: none" : "Selected: " + String.join(", ", vals));
        });

        var container = new Div(multiSelect.getComponent(), info);

        return new DemoExample("Multi List (MultiSelectListBox)", container, """
                // Input.multiListSelect() creates a MultiSelectListBox — scrollable list.
                var multiSelect = Input.multiListSelect(String.class)
                    .items("Read", "Write", "Execute", "Delete", "Admin")
                    .build();
                """);
    }

    private DemoExample enumMultiExample() {
        var info = new Span("Selected: none");

        var multiSelect = Input.enumMultiSelect(Permission.class)
                .label("Permissions")
                .build();

        multiSelect.addValueChangeListener(e -> {
            var vals = e.getValue();
            info.setText(vals.isEmpty() ? "Selected: none"
                    : "Selected: " + vals.stream().map(Enum::name).collect(Collectors.joining(", ")));
        });

        var container = new Div(multiSelect.getComponent(), info);

        return new DemoExample("Enum Multi (CheckboxGroup)", container, """
                // Input.enumMultiSelect() auto-populates from enum constants.
                var multiSelect = Input.enumMultiSelect(Permission.class)
                    .label("Permissions")
                    .build();
                """);
    }
}
