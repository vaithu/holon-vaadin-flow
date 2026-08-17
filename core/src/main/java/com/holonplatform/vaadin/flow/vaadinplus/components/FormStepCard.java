package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.iyensoft.vaadin.flow.components.Panel;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A form step card — a styled {@link Panel} that represents one step in a multi-step form.
 *
 * <p>Each card shows a numbered (or check-marked) badge, a heading, an optional subtitle, a
 * "STEP N OF M" counter, step content, and an optional help-text footer. Three visual states
 * communicate the step's progress to the user.</p>
 *
 * <h3>Visual states</h3>
 * <ul>
 *   <li>{@link StepState#PENDING} — grey badge, slightly muted card</li>
 *   <li>{@link StepState#CURRENT} — brand-coloured badge with glow ring, highlighted card border</li>
 *   <li>{@link StepState#DONE}    — green badge with a ✓ checkmark</li>
 * </ul>
 *
 * <h3>Usage — via the builder</h3>
 * <pre>{@code
 * FormStepCard card = Components.formStepCard()
 *     .stepNumber(2)
 *     .totalSteps(6)
 *     .title("Primary contact")
 *     .subtitle("Name, email, phone")
 *     .state(FormStepCard.StepState.CURRENT)
 *     .helpText("At least one contact is required before saving.")
 *     .content(nameField, emailField)
 *     .build();
 * }</pre>
 *
 * <h3>CSS file</h3>
 * {@code META-INF/resources/form-step-card.css} — BEM root: {@code .fsc}
 */
@StyleSheet("context://form-step-card.css")
public class FormStepCard extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    static final String CSS_ROOT    = "fsc";
    static final String CSS_PENDING = "fsc--pending";
    static final String CSS_CURRENT = "fsc--current";
    static final String CSS_DONE    = "fsc--done";

    private StepState currentState;
    private Span      badge;
    private int       stepNumber;

    /** Constructor — also used directly by {@link com.holonplatform.vaadin.flow.components.builders.FormStepCardBuilder}. */
    public FormStepCard() {
        addClassName(CSS_ROOT);
    }

    /**
     * Visual state of a form step card.
     */
    public enum StepState {

        /** Not yet reached — grey badge, muted appearance. */
        PENDING,

        /** Currently active step — brand badge with glow, highlighted border. */
        CURRENT,

        /** Completed step — green badge with a ✓ checkmark. */
        DONE
    }

    /**
     * Returns the current visual state.
     *
     * @return the current {@link StepState}; may be {@code null} before {@link FormStepCardBuilder#build()} sets it
     */
    public StepState getState() {
        return currentState;
    }

    /** Called by builders to hand off the badge span after DOM assembly. */
    public void initBadge(Span badge, int stepNumber) {
        this.badge      = badge;
        this.stepNumber = stepNumber;
    }

    /**
     * Changes the visual state of the card after it has been built.
     *
     * <p>Swaps the BEM modifier CSS class on the root element.
     * Use this to reactively advance the form wizard without rebuilding the card.</p>
     *
     * @param state new state; {@code null} is treated as {@link StepState#PENDING}
     * @return this (fluent)
     */
    public FormStepCard setState(StepState state) {
        if (currentState != null) {
            removeClassName(cssFor(currentState));
        }
        this.currentState = state != null ? state : StepState.PENDING;
        addClassName(cssFor(this.currentState));
        if (badge != null) {
            badge.setText(this.currentState == StepState.DONE ? "✓" : String.valueOf(stepNumber));
        }
        return this;
    }

    // ── Internal helpers ──────────────────────────────────────────────────

    private static String cssFor(StepState state) {
        return switch (state) {
            case PENDING -> CSS_PENDING;
            case CURRENT -> CSS_CURRENT;
            case DONE    -> CSS_DONE;
        };
    }
}
