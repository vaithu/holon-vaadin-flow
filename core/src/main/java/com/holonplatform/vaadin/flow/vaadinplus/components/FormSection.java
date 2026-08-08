package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;

import java.io.Serial;

/**
 * A titled form section — a labeled {@link FormLayout} wrapped in a {@code <div>}.
 *
 * <p>Renders as:</p>
 * <pre>{@code
 * <div class="form-section">
 *   <h3 class="form-section__label" id="form-section-N-title">Section Title</h3>
 *   <vaadin-form-layout aria-labelledby="form-section-N-title">…fields…</vaadin-form-layout>
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
 *
 * // Localizable label:
 * FormSection section = FormSection.of(
 *     Localizable.builder().message("Identity").messageCode("form.section.identity").build(),
 *     nameField, emailField);
 * }</pre>
 */
@StyleSheet("context://form-section.css")
public class FormSection extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String CSS_SECTION = "form-section";
    private static final String CSS_LABEL   = "form-section__label";

    private static int idCounter = 0;

    private final H3         titleElement;
    private final FormLayout formLayout;

    /** Non-null when the section title was set via a {@link Localizable}. */
    private Localizable titleLocalizable;

    private FormSection(String label, int colspanLast, Component... fields) {
        addClassName(CSS_SECTION);

        String titleId = "form-section-" + (++idCounter) + "-title";
        this.titleElement = new H3(label);
        this.titleElement.addClassName(CSS_LABEL);
        this.titleElement.setId(titleId);

        this.formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        formLayout.getElement().setAttribute("aria-labelledby", titleId);
        formLayout.add(fields);

        if (colspanLast > 0 && fields.length > 0) {
            formLayout.setColspan(fields[fields.length - 1], colspanLast);
        }

        add(titleElement, formLayout);
    }

    // ── I18N ─────────────────────────────────────────────────────────────────
    // ── String factory methods ────────────────────────────────────────────────

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
     * Creates a titled form section with the given fields, setting a column span on the last field.
     *
     * @param label       the section title displayed above the form
     * @param colspanLast the number of columns the last field should span (e.g. 2 for full-width)
     * @param fields      the form fields to content
     * @return a new {@code FormSection}
     */
    public static FormSection of(String label, int colspanLast, Component... fields) {
        return new FormSection(label, colspanLast, fields);
    }

    // ── Localizable factory methods ───────────────────────────────────────────

    /**
     * Creates a titled form section whose heading is resolved from a {@link Localizable} descriptor.
     * The title is re-resolved on each locale change.
     *
     * @param label  the localizable section title (not null)
     * @param fields the form fields to content
     * @return a new {@code FormSection}
     */
    public static FormSection of(Localizable label, Component... fields) {
        FormSection section = new FormSection(resolve(label), -1, fields);
        section.titleLocalizable = label;
        return section;
    }

    /**
     * Creates a titled form section with a localizable heading and a column span on the last field.
     *
     * @param label       the localizable section title (not null)
     * @param colspanLast the number of columns the last field should span
     * @param fields      the form fields to content
     * @return a new {@code FormSection}
     */
    public static FormSection of(Localizable label, int colspanLast, Component... fields) {
        FormSection section = new FormSection(resolve(label), colspanLast, fields);
        section.titleLocalizable = label;
        return section;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /**
     * Returns the underlying {@link FormLayout} for further customization
     * (e.g. changing responsive steps or setting colspans).
     */
    public FormLayout getFormLayout() {
        return formLayout;
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }
}
