package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.WizardFrameBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.Panel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

/**
 * A multi-step wizard panel built on top of {@link Panel} (header / content / footer).
 *
 * <p>The header shows a title and a {@link FlowStepper} displaying current progress.
 * The scrollable content area shows one step at a time.
 * The bordered footer provides Back and Next/Finish navigation buttons plus a step counter label.
 *
 * <p>Each step's content is an arbitrary {@link Component}. When the content is an
 * {@link EntityFormPanel}, the wizard calls {@link EntityFormPanel#validate()} before
 * advancing to the next step so inline validation errors are shown automatically.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * WizardFrame wizard = WizardFrame.builder()
 *     .title("New Customer")
 *     .step("Contact", EntityFormPanel.bean(ContactBean.class).noFooter().build())
 *     .step("Company", EntityFormPanel.bean(CompanyBean.class).noFooter().build())
 *     .step("Review", "Create Customer", reviewPanel)
 *     .onFinish(wf -> service.save(collected))
 *     .build();
 * }</pre>
 *
 * <p>All styling is managed by {@code wizard-frame.css}. No inline styles or CSS classes
 * are set in Java code.
 *
 * @see WizardFrameBuilder
 * @see EntityFormPanel
 * @see FlowStepper
 */
@StyleSheet("context://wizard-frame.css")
public class WizardFrame extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Immutable descriptor for a single wizard step.
     *
     * @param label           label shown in the {@link FlowStepper} indicator
     * @param nextButtonLabel optional custom Next button label for this step; {@code null} → default "Next" / "Finish"
     * @param content         the step content shown in the scrollable area
     */
    public record WizardStep(String label, String nextButtonLabel, Component content) {
    }

    // ── Internal state ─────────────────────────────────────────────────────

    private final List<WizardStep> steps;
    private final FlowStepper stepper;
    private final Button backButton;
    private final Button nextButton;
    private final Button finishButton;
    private final Layout stepLabel;
    private final Consumer<WizardFrame> onFinish;
    private final IntPredicate beforeNext;
    private final IntConsumer onStepChanged;
    private int currentStep = 0;

    // ── Public constructor — use WizardFrame.builder() ─────────────────────

    public WizardFrame(String title,
                       List<WizardStep> steps,
                       Consumer<Header> headerConfig,
                       Consumer<ButtonBuilder> backBtnConfig,
                       Consumer<ButtonBuilder> nextBtnConfig,
                       Consumer<ButtonBuilder> finishBtnConfig,
                       Consumer<WizardFrame> onFinish,
                       IntPredicate beforeNext,
                       IntConsumer onStepChanged) {

        this.steps = List.copyOf(steps);
        this.onFinish = onFinish;
        this.beforeNext = beforeNext;
        this.onStepChanged = onStepChanged;
        addClassName("wizard-frame");

        // ── FlowStepper ────────────────────────────────────────────────────
        String[] stepLabels = steps.stream().map(WizardStep::label).toArray(String[]::new);
        this.stepper = FlowStepper.builder()
                .steps(stepLabels)
                .clickNavigation(FlowStepper.ClickNavigation.NONE)
                .build();

        // ── Header: title only (stepper moved to content area) ────────────
        Header header = new Header(title);

        // Apply breadcrumb, headerActions, headerPrefix, and any custom config
        if (headerConfig != null) {
            headerConfig.accept(header);
        }

        // ── Scrollable content area ────────────────────────────────────────
        // Stepper sits at the top of the content zone — sticky within the
        // scroll container on desktop, scrolls with the page on mobile.
        Layout stepperSection = new Layout();
        stepperSection.addClassName("wizard-frame__stepper");
        stepperSection.add(stepper);

        Layout content = new Layout();
        content.addClassName("wizard-frame__content");
        content.add(stepperSection);
        for (WizardStep step : steps) {
            step.content().setVisible(false);
            content.add(step.content());
        }

        // ── Navigation buttons ─────────────────────────────────────────────
        this.backButton = buildButton(backBtnConfig,
                LocalizationProvider.localize("Back", "wizard.back_btn"),
                VaadinIcon.CHEVRON_LEFT, false);
        this.backButton.addClickListener(e -> goBack());

        this.nextButton = buildButton(nextBtnConfig,
                LocalizationProvider.localize("Next", "wizard.next_btn"),
                VaadinIcon.CHEVRON_RIGHT, true);
        this.nextButton.addClickListener(e -> goNext());

        // Finish button is styled separately (typically primary) and only shown on the last step
        this.finishButton = buildButton(finishBtnConfig,
                LocalizationProvider.localize("Finish", "wizard.finish_btn"),
                VaadinIcon.CHECK, true);
        this.finishButton.addClickListener(e -> goFinish());

        // ── Step counter label ─────────────────────────────────────────────
        this.stepLabel = new Layout();

        // ── Footer ────────────────────────────────────────────────────────
        Footer footer = new Footer();
        footer.setBordered(true);
        footer.setBrand(stepLabel);
        footer.setActions(backButton, nextButton, finishButton);

        // ── Assemble ──────────────────────────────────────────────────────
        setHeader(header);
        setContent(content);
        setFooter(footer);

        // ── Initial state ─────────────────────────────────────────────────
        setCurrentStep(0);
    }

    // ── Public navigation API ──────────────────────────────────────────────

    /**
     * Returns the index of the currently active step (0-based).
     *
     * @return current step index
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Navigates directly to the specified step.
     * <p>
     * No validation is performed when jumping programmatically — use with care.
     * </p>
     *
     * @param index 0-based step index; clamped to valid range
     */
    public void setCurrentStep(int index) {
        currentStep = Math.max(0, Math.min(index, steps.size() - 1));
        refresh();
        if (onStepChanged != null) {
            onStepChanged.accept(currentStep);
        }
    }

    /**
     * Returns the total number of steps.
     *
     * @return step count
     */
    public int getStepCount() {
        return steps.size();
    }

    // ── Internal navigation ────────────────────────────────────────────────

    private void goNext() {
        // Validate current step if it is an EntityFormPanel
        if (!validateCurrentStep()) {
            return;
        }
        // Optional guard from the caller
        if (beforeNext != null && !beforeNext.test(currentStep)) {
            return;
        }
        if (currentStep < steps.size() - 1) {
            setCurrentStep(currentStep + 1);
        }
    }

    private void goFinish() {
        if (!validateCurrentStep()) {
            return;
        }
        if (beforeNext != null && !beforeNext.test(currentStep)) {
            return;
        }
        if (onFinish != null) {
            onFinish.accept(this);
        }
    }

    private void goBack() {
        if (currentStep > 0) {
            setCurrentStep(currentStep - 1);
        }
    }

    private boolean validateCurrentStep() {
        Component content = steps.get(currentStep).content();
        if (content instanceof EntityFormPanel<?> panel) {
            return panel.validate();
        }
        return true;
    }

    private void refresh() {
        // Step content visibility
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).content().setVisible(i == currentStep);
        }

        // Stepper indicator
        stepper.setCurrentStep(currentStep);

        // Step counter label
        stepLabel.removeAll();
        String template = LocalizationProvider.localize("Step {0} of {1}", "wizard.step_counter");
        stepLabel.add(Components.label()
                              .text(MessageFormat.format(template, currentStep + 1, steps.size()))
                              .build());

        // Back button: hidden on first step
        backButton.setVisible(currentStep > 0);

        // On the last step show finishButton; on all other steps show nextButton.
        // Per-step nextButtonLabel overrides the nextButton text only (not the finishButton).
        boolean isLast = currentStep == steps.size() - 1;
        nextButton.setVisible(!isLast);
        finishButton.setVisible(isLast);

        String customLabel = steps.get(currentStep).nextButtonLabel();
        if (!isLast && customLabel != null) {
            nextButton.setText(customLabel);
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private static Button buildButton(Consumer<ButtonBuilder> config,
                                      String defaultText,
                                      VaadinIcon icon,
                                      boolean iconAfterText) {
        var builder = Components.button().text(defaultText)
                .icon(icon.create()).iconAfterText(iconAfterText);
        if (config != null) {
            config.accept(builder);
        }
        return builder.build();
    }

    // ── Static factory ─────────────────────────────────────────────────────

    /**
     * Entry point for building a {@link WizardFrame} via the fluent builder.
     *
     * @return a new {@link WizardFrameBuilder}
     */
    public static WizardFrameBuilder builder() {
        return WizardFrameBuilder.create();
    }
}

