package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A titled form section — a labeled {@link FormLayout} wrapped in a {@code <div>}.
 *
 * <p>Renders as:</p>
 * <pre>{@code
 * <div class="form-section">
 *   <span class="form-section__label">Section Title</span>
 *   <vaadin-form-layout>…fields…</vaadin-form-layout>
 * </div>
 * }</pre>
 *
 * <p>By default the form uses two responsive steps: 1 column below 480px,
 * 2 columns at 480px and above. Override via {@link #getFormLayout()}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * FormSection section = FormSection.of("Identity", nameField, emailField);
 *
 * // With last-field colspan override:
 * FormSection section = FormSection.of("Address", 2, streetField, cityField, countryField);
 * }</pre>
 */
@StyleSheet("context://form-section.css")
public class FormSection extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String CSS_SECTION = "form-section";
    private static final String CSS_LABEL   = "form-section__label";

    private final FormLayout formLayout;

    private FormSection(String label, int colspanLast, Component... fields) {
        addClassName(CSS_SECTION);

        Span title = new Span(label);
        title.addClassName(CSS_LABEL);

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        formLayout.add(fields);

        if (colspanLast > 0 && fields.length > 0) {
            formLayout.setColspan(fields[fields.length - 1], colspanLast);
        }

        add(title, formLayout);
    }

    /**
     * Creates a titled form section with the given fields.
     *
     * @param label  the section title displayed above the form
     * @param fields the form fields to content
     * @return a new {@code FormSection}
     */
    public static FormSection of(String label, Component... fields) {
        return new FormSection(label, -1, fields);
    }

    /**
     * Creates a titled form section with the given fields, setting a column span
     * on the last field.
     *
     * @param label       the section title displayed above the form
     * @param colspanLast the number of columns the last field should span (e.g. 2 for full-width)
     * @param fields      the form fields to content
     * @return a new {@code FormSection}
     */
    public static FormSection of(String label, int colspanLast, Component... fields) {
        return new FormSection(label, colspanLast, fields);
    }

    /**
     * Returns the underlying {@link FormLayout} for further customization
     * (e.g. changing responsive steps or setting colspans).
     */
    public FormLayout getFormLayout() {
        return formLayout;
    }
}
