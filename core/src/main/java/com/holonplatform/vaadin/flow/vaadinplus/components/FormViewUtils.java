package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Static helpers for building common recurring UI patterns inside form views.
 *
 * <p>Views that use these helpers must declare
 * {@code @StyleSheet("context://form-utils.css")} on their class to load the
 * required {@code .futil__*} CSS classes.</p>
 *
 * <p><strong>Notifications:</strong> use
 * {@link com.holonplatform.vaadin.flow.components.utils.NotificationUtil} or
 * {@code Components.notification()} — not duplicated here.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // Label above a component (ChipGroup, AssignmentPickerRow, etc.)
 * Div fieldWrapper = FormViewUtils.labeledField("Role", buildRoleChipGroup());
 *
 * // Toggle row (label + description + switch)
 * Div toggleRow = FormViewUtils.toggleRow("Sync with LinkedIn",
 *                                          "Auto-pull profile updates", true);
 *
 * // Subtitle with a red required-field asterisk
 * Component hint = FormViewUtils.requiredFieldsHint();
 * // → "All required fields are marked with *" (star is red)
 * }</pre>
 */
public final class FormViewUtils {

    private FormViewUtils() {}

    // ── Labeled field ─────────────────────────────────────────────────────

    /**
     * Wraps {@code content} in a flex column with a small muted {@code labelText}
     * label above it — suitable for {@link ChipGroup}, assignment rows, or any
     * component that doesn't have its own built-in label.
     *
     * @param labelText label to show above the content
     * @param content   the component to wrap
     * @return a {@code Div} with BEM class {@code futil__field}
     */
    public static Div labeledField(String labelText, Component content) {
        Span label = new Span(labelText);
        label.addClassName("futil__field-label");
        Div wrapper = new Div(label, content);
        wrapper.addClassName("futil__field");
        return wrapper;
    }

    // ── Toggle row ────────────────────────────────────────────────────────

    /**
     * Creates a horizontal row containing a two-line label block on the left and
     * a boolean switch on the right — a standard settings-panel toggle pattern.
     *
     * @param label       primary label text
     * @param description secondary description text (shown below the label)
     * @param checked     initial switch state
     * @return a {@code Div} with BEM class {@code futil__toggle-row}
     */
    public static Div toggleRow(String label, String description, boolean checked) {
        Span labelSpan = new Span(label);
        labelSpan.addClassName("futil__toggle-label");
        Span subSpan = new Span(description);
        subSpan.addClassName("futil__toggle-sub");
        Div left = new Div(labelSpan, subSpan);
        var toggle = Input.boolean_().asSwitch().withValue(checked).build();
        Div row = new Div(left, toggle.getComponent());
        row.addClassName("futil__toggle-row");
        return row;
    }

    // ── Required-fields hint ──────────────────────────────────────────────

    /**
     * Returns a composite inline component that renders:
     * <pre>All required fields are marked with <span style="color:red">*</span></pre>
     *
     * <p>Intended for use as a page subtitle via
     * {@link EntityCreationFormBuilder#subtitle(Component)}.</p>
     *
     * @return a {@code Span} containing the hint text with a red asterisk
     */
    public static Component requiredFieldsHint() {
        return requiredFieldsHint("All required fields are marked with");
    }

    /**
     * Returns a composite inline component that renders {@code prefixText} followed
     * by a red asterisk — e.g. {@code "Fields marked with *"}.
     *
     * @param prefixText text that precedes the asterisk (no trailing space needed)
     * @return a {@code Span} containing the hint text with a red asterisk
     */
    public static Component requiredFieldsHint(String prefixText) {
        Span text = new Span((prefixText != null ? prefixText : "") + " ");
        Span star = new Span("*");
        star.addClassName("futil__required-star");
        return new Span(text, star);
    }

    // ── Progress tracking ─────────────────────────────────────────────────

    /**
     * Recalculates the progress bar and status text for the given {@link EntityCreationForm}
     * based on how many inputs across all {@code panels} are non-blank.
     *
     * @param form          the form whose progress bar and status to update
     * @param panels        all panels whose inputs count toward completion
     * @param progressLabel short label shown next to the progress bar (e.g. {@code "Profile"})
     */
    public static void updateProgress(EntityCreationForm form,
                                      List<EntityFormPanel<?>> panels,
                                      String progressLabel) {
        long total  = panels.stream().flatMap(p -> p.getForm().getElements()).count();
        long filled = panels.stream().flatMap(p -> p.getForm().getElements())
                .filter(input -> {
                    Object v = input.getValue();
                    return v != null && !v.toString().isBlank();
                })
                .count();
        int pct = total > 0 ? (int) (filled * 100 / total) : 0;
        form.setProgress(progressLabel, pct);
        form.setStatus(
                filled + "/" + total + " fields complete",
                pct == 100 ? StickyActionBar.Variant.SUCCESS : StickyActionBar.Variant.WARNING);
    }

    /**
     * Attaches a value-change listener to every input inside each panel so that
     * the form's progress bar is updated and the draft badge is shown as dirty
     * whenever any field value changes.
     *
     * @param form            the form to update
     * @param panels          all panels to observe
     * @param progressLabel   short label for the progress bar (e.g. {@code "Profile"})
     * @param dirtyBadgeText  text for the draft badge when the form is dirty
     *                        (e.g. {@code "Unsaved changes"})
     */
    public static void wireProgressTracking(EntityCreationForm form,
                                            List<EntityFormPanel<?>> panels,
                                            String progressLabel,
                                            String dirtyBadgeText) {
        panels.forEach(panel ->
                panel.getForm().getElements().forEach(input ->
                        input.addValueChangeListener(e -> {
                            updateProgress(form, panels, progressLabel);
                            form.setDraftBadge(dirtyBadgeText,
                                    EntityCreationForm.DraftBadgeVariant.WARNING);
                        })));
    }

    // ── Step transitions ──────────────────────────────────────────────────

    /**
     * Returns a new {@link StepTransitionsBuilder} for wiring focus-based step-state
     * transitions (PENDING → CURRENT → DONE) across a multi-step {@link EntityCreationForm}.
     *
     * <pre>{@code
     * StepTransitionsHandle h = FormViewUtils.stepTransitions()
     *         .addPanelStep(step1, orgPanel)
     *         .addPanelStep(step2, contactPanel)
     *         .addClickStep(step5)          // no validation — auto-DONE on leave
     *         .autoDone(step5, step6)        // start in DONE state
     *         .wire();
     *
     * saveBtn.addClickListener(e -> {
     *     if (h.validateAll()) { /* save *\/ } else { /* show errors *\/ }
     * });
     * }</pre>
     */
    public static StepTransitionsBuilder stepTransitions() {
        return new StepTransitionsBuilder();
    }

    /**
     * Handle returned by {@link StepTransitionsBuilder#wire()} that allows callers
     * to trigger a full validation pass (e.g. on Save).
     */
    public interface StepTransitionsHandle {
        /**
         * Validates all panel-backed steps.
         * <ul>
         *   <li>Passing steps → {@link FormStepCard.StepState#DONE}</li>
         *   <li>Failing step that is currently active → {@link FormStepCard.StepState#CURRENT}</li>
         *   <li>Failing step that is not active → {@link FormStepCard.StepState#PENDING}</li>
         * </ul>
         *
         * @return {@code true} if every panel passed validation
         */
        boolean validateAll();
    }

    /**
     * Fluent builder for wiring multi-step form focus transitions.
     * Obtain an instance via {@link FormViewUtils#stepTransitions()}.
     */
    public static final class StepTransitionsBuilder {

        private record StepEntry(FormStepCard step, EntityFormPanel<?> panel) {}

        private final List<StepEntry>    panelSteps    = new ArrayList<>();
        private final List<FormStepCard> clickOnlySteps = new ArrayList<>();
        private final List<FormStepCard> autoDoneSteps  = new ArrayList<>();

        private StepTransitionsBuilder() {}

        /**
         * Registers a step whose completion is determined by validating {@code panel}.
         */
        public StepTransitionsBuilder addPanelStep(FormStepCard step, EntityFormPanel<?> panel) {
            panelSteps.add(new StepEntry(step, panel));
            return this;
        }

        /**
         * Registers a step that has no validation panel — it transitions to DONE
         * automatically when the user leaves it.
         */
        public StepTransitionsBuilder addClickStep(FormStepCard step) {
            clickOnlySteps.add(step);
            return this;
        }

        /**
         * Steps that should immediately be set to {@link FormStepCard.StepState#DONE}
         * after wiring (e.g. steps with no required fields or pre-filled assignments).
         */
        public StepTransitionsBuilder autoDone(FormStepCard... steps) {
            autoDoneSteps.addAll(Arrays.asList(steps));
            return this;
        }

        /**
         * Attaches all focus / click listeners and returns a {@link StepTransitionsHandle}
         * that can be used for save-time validation.
         */
        public StepTransitionsHandle wire() {
            Map<FormStepCard, EntityFormPanel<?>> panelOf = new IdentityHashMap<>();
            panelSteps.forEach(e -> panelOf.put(e.step(), e.panel()));

            AtomicReference<FormStepCard> activeStep = new AtomicReference<>();

            Consumer<FormStepCard> onLeave = leaving -> {
                EntityFormPanel<?> p = panelOf.get(leaving);
                if (p == null) {
                    leaving.setState(FormStepCard.StepState.DONE);
                } else if (p.validate()) {
                    leaving.setState(FormStepCard.StepState.DONE);
                } else {
                    leaving.setState(FormStepCard.StepState.PENDING);
                }
            };

            // Panel steps — wire focus events
            panelSteps.forEach(entry ->
                    entry.panel().getForm().getElements().forEach(input -> {
                        if (input.getComponent() instanceof Focusable<?> focusable) {
                            focusable.addFocusListener(e -> {
                                FormStepCard prev = activeStep.getAndSet(entry.step());
                                if (entry.step().getState() != FormStepCard.StepState.CURRENT) {
                                    entry.step().setState(FormStepCard.StepState.CURRENT);
                                }
                                if (prev != null && prev != entry.step()) {
                                    onLeave.accept(prev);
                                }
                            });
                        }
                    }));

            // Click-only steps — any click activates the step; leaving advances to DONE
            clickOnlySteps.forEach(step ->
                    step.getElement().addEventListener("click", ignored -> {
                        FormStepCard prev = activeStep.getAndSet(step);
                        if (step.getState() != FormStepCard.StepState.CURRENT) {
                            step.setState(FormStepCard.StepState.CURRENT);
                        }
                        if (prev != null && prev != step) {
                            onLeave.accept(prev);
                        }
                    }));

            autoDoneSteps.forEach(step -> step.setState(FormStepCard.StepState.DONE));

            List<StepEntry> capturedSteps = List.copyOf(panelSteps);
            return () -> {
                FormStepCard active = activeStep.get();
                boolean allValid = true;
                for (StepEntry entry : capturedSteps) {
                    if (!entry.panel().validate()) {
                        allValid = false;
                        entry.step().setState(
                                entry.step() == active
                                        ? FormStepCard.StepState.CURRENT
                                        : FormStepCard.StepState.PENDING);
                    }
                }
                return allValid;
            };
        }
    }
}
