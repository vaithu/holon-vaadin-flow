package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for {@link FormStepCard} — a {@link com.iyensoft.vaadin.flow.components.Panel}-based
 * card that represents one step in a multi-step form.
 *
 * <p>The builder wires all step-specific structure (badge, counter, subtitle, help text) into the
 * underlying {@link PanelConfigurator} API, so the resulting card inherits all {@code iyen-panel}
 * styling while adding the step-card specific semantics via the {@code .fsc} BEM class.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * FormStepCard card = Components.formStepCard()
 *     .stepNumber(3)
 *     .totalSteps(6)
 *     .title("Address")
 *     .subtitle("Registered office and country")
 *     .state(FormStepCard.StepState.CURRENT)
 *     .helpText("This address will appear on invoices.")
 *     .content(streetField, cityField, countryPicker)
 *     .build();
 *
 * // Advance the step later:
 * card.setState(FormStepCard.StepState.DONE);
 * }</pre>
 *
 * <h3>Header layout (auto-composed)</h3>
 * <pre>
 * ┌─────────────────────────────────────────────┐
 * │ [badge]  Address            STEP 3 OF 6     │  ← panel header
 * │          Registered office and country      │  ← header details (subtitle)
 * ├─────────────────────────────────────────────┤
 * │  street · city · country fields             │  ← panel content
 * ├─────────────────────────────────────────────┤
 * │  This address will appear on invoices.      │  ← panel footer details (helpText)
 * └─────────────────────────────────────────────┘
 * </pre>
 *
 * @see FormStepCard
 */
public class FormStepCardBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Builder state ─────────────────────────────────────────────────────

    private int stepNumber  = 1;
    private int totalSteps  = 1;
    private String title;
    private String subtitle;
    private String helpText;
    private FormStepCard.StepState state = FormStepCard.StepState.PENDING;
    private final List<Component> contentComponents = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────

    /** Private: obtain via {@link #create()} or {@link com.holonplatform.vaadin.flow.components.Components#formStepCard()}. */
    private FormStepCardBuilder() {}

    /**
     * Creates a new {@code FormStepCardBuilder}.
     *
     * @return a new builder instance
     */
    public static FormStepCardBuilder create() {
        return new FormStepCardBuilder();
    }

    // ── Step position ─────────────────────────────────────────────────────

    /**
     * Sets the 1-based step number shown in the badge and "STEP N OF M" counter.
     *
     * @param stepNumber step number (&ge; 1)
     * @return this (fluent)
     */
    public FormStepCardBuilder stepNumber(int stepNumber) {
        this.stepNumber = Math.max(1, stepNumber);
        return this;
    }

    /**
     * Sets the total number of steps shown in the "STEP N OF M" counter.
     *
     * @param totalSteps total steps (&ge; 1)
     * @return this (fluent)
     */
    public FormStepCardBuilder totalSteps(int totalSteps) {
        this.totalSteps = Math.max(1, totalSteps);
        return this;
    }

    // ── Text ──────────────────────────────────────────────────────────────

    /**
     * Sets the step heading text (rendered as {@code h2} in the panel header).
     *
     * @param title step heading; {@code null} renders no heading text
     * @return this (fluent)
     */
    public FormStepCardBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets an optional subtitle displayed below the heading.
     *
     * @param subtitle short descriptive text (e.g. {@code "Name, email verified"}); {@code null} omits it
     * @return this (fluent)
     */
    public FormStepCardBuilder subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * Sets optional help/guidance text displayed in the card footer.
     *
     * <p>Rendered as a muted paragraph inside the panel footer's {@code details} slot.
     * Use for field-level guidance, requirements, or contextual hints.</p>
     *
     * @param helpText guidance text; {@code null} omits the footer entirely
     * @return this (fluent)
     */
    public FormStepCardBuilder helpText(String helpText) {
        this.helpText = helpText;
        return this;
    }

    // ── State ─────────────────────────────────────────────────────────────

    /**
     * Sets the visual state of the card.
     *
     * @param state step state; {@code null} defaults to {@link FormStepCard.StepState#PENDING}
     * @return this (fluent)
     */
    public FormStepCardBuilder state(FormStepCard.StepState state) {
        this.state = state != null ? state : FormStepCard.StepState.PENDING;
        return this;
    }

    // ── Content ───────────────────────────────────────────────────────────

    /**
     * Appends one or more components to the card's main content area.
     *
     * <p>Can be called multiple times; components are added in order.</p>
     *
     * @param components components to add; {@code null} entries are silently ignored
     * @return this (fluent)
     */
    public FormStepCardBuilder content(Component... components) {
        if (components != null) {
            for (Component c : components) {
                if (c != null) contentComponents.add(c);
            }
        }
        return this;
    }

    // ── Build ─────────────────────────────────────────────────────────────

    /**
     * Builds and returns the configured {@link FormStepCard}.
     *
     * <p>The returned card is fully formed: its header (badge, heading, subtitle, counter),
     * content, optional footer help-text, and state CSS class are all applied.
     * After building, call {@link FormStepCard#setState} to transition state without rebuilding.</p>
     *
     * @return the configured {@link FormStepCard}
     */
    public FormStepCard build() {
        FormStepCard card = new FormStepCard();

        // ── Header badge (prefix) ─────────────────────────────────────────
        Span badge = new Span(state == FormStepCard.StepState.DONE
                ? "✓"
                : String.valueOf(stepNumber));
        badge.addClassName("fsc__badge");

        // ── Step counter (actions) ────────────────────────────────────────
        Span counter = new Span("STEP " + stepNumber + " OF " + totalSteps);
        counter.addClassName("fsc__counter");

        // ── Configure via PanelConfigurator (card() adds rdiv-card class) ─
        var configurator = PanelConfigurator.configure(card).card();

        var headerBuilder = configurator.header().prefix(badge).sticky(false);

        if (title != null && !title.isEmpty()) {
            headerBuilder.heading(title);
        }

        if (subtitle != null && !subtitle.isEmpty()) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("fsc__subtitle");
            headerBuilder.details(subtitleSpan);
        }

        headerBuilder.actions(counter).add();

        // ── Content ───────────────────────────────────────────────────────
        if (!contentComponents.isEmpty()) {
            configurator.content(contentComponents.toArray(Component[]::new));
        }

        // ── Footer help text ──────────────────────────────────────────────
        if (helpText != null && !helpText.isEmpty()) {
            Span helpSpan = new Span(helpText);
            helpSpan.addClassName("fsc__help");
            configurator.footer().details(helpSpan).add();
        }

        // ── Apply state CSS and badge ─────────────────────────────────────
        card.setState(state);
        card.initBadge(badge, stepNumber);

        return card;
    }
}
