package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link FlowStepper} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Horizontal stepper (default)</li>
 *   <li>Vertical stepper</li>
 *   <li>Variant: NUMBERED</li>
 *   <li>Variant: DOT</li>
 *   <li>Interactive stepper with next/previous navigation</li>
 * </ol>
 */
@PageTitle("FlowStepper – Holon Demo")
@Route(value = "flow-stepper", layout = DemoMainLayout.class)
public class FlowStepperDemoView extends Div {

    public FlowStepperDemoView() {
        addClassName("app-view");

        var title = new H1("FlowStepper");

        var desc = new Paragraph(
                "Multi-step progress indicator backed by the <flow-stepper> Shadow DOM web component. " +
                "Communicates through HTML attributes only — no inline styles. " +
                "Supports horizontal and vertical orientation, three visual variants " +
                "(DEFAULT, NUMBERED, DOT), and server-driven navigation via next() / previous() / goTo().");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(horizontalExample());
        examples.add(verticalExample());
        examples.add(numberedVariantExample());
        examples.add(dotVariantExample());
        examples.add(interactiveExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample horizontalExample() {
        var stepper = new FlowStepper(
                List.of("Account", "Personal Info", "Preferences", "Review"),
                1   // second step active (0-based)
        );

        return new DemoExample("Horizontal (default)", stepper, """
                FlowStepper stepper = new FlowStepper(
                    List.of("Account", "Personal Info", "Preferences", "Review"),
                    1    // 0-based index of the initially active step
                );
                // Or via builder:
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Account", "Personal Info", "Preferences", "Review")
                    .currentStep(1)
                    .build();
                """);
    }

    private DemoExample verticalExample() {
        var stepper = new FlowStepper(
                List.of("Choose plan", "Payment details", "Confirmation"),
                0
        );
        stepper.setOrientation(FlowStepper.Orientation.VERTICAL);

        return new DemoExample("Vertical Orientation", stepper, """
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Choose plan", "Payment details", "Confirmation")
                    .orientation(FlowStepper.Orientation.VERTICAL)
                    .build();
                """);
    }

    private DemoExample numberedVariantExample() {
        var stepper = new FlowStepper(
                List.of("Step 1", "Step 2", "Step 3", "Step 4"),
                2
        );
        stepper.setVariant(FlowStepper.Variant.NUMBERED);

        return new DemoExample("Variant: NUMBERED", stepper, """
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Step 1", "Step 2", "Step 3", "Step 4")
                    .currentStep(2)
                    .variant(FlowStepper.Variant.NUMBERED)  // always shows step number
                    .build();
                """);
    }

    private DemoExample dotVariantExample() {
        var stepper = new FlowStepper(
                List.of("Start", "In progress", "Done"),
                1
        );
        stepper.setVariant(FlowStepper.Variant.DOT);

        return new DemoExample("Variant: DOT (minimal)", stepper, """
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Start", "In progress", "Done")
                    .currentStep(1)
                    .variant(FlowStepper.Variant.DOT)  // minimal dot — no number/icon
                    .build();
                """);
    }

    private DemoExample interactiveExample() {
        var stepper = new FlowStepper(List.of("Details", "Shipping", "Payment", "Confirm"));

        var stepLabel = new Span("Step 1 of 4");

        var prevBtn = new Button("← Previous");
        prevBtn.setEnabled(false);
        var nextBtn = new Button("Next →");

        // State
        int[] current = {0};
        int total = 4;

        prevBtn.addClickListener(e -> {
            if (current[0] > 0) {
                current[0]--;
                stepper.setCurrentStep(current[0]);
                stepLabel.setText("Step " + (current[0] + 1) + " of " + total);
                prevBtn.setEnabled(current[0] > 0);
                nextBtn.setEnabled(true);
                nextBtn.setText(current[0] == total - 1 ? "Finish" : "Next →");
            }
        });

        nextBtn.addClickListener(e -> {
            if (current[0] < total - 1) {
                current[0]++;
                stepper.setCurrentStep(current[0]);
                stepLabel.setText("Step " + (current[0] + 1) + " of " + total);
                prevBtn.setEnabled(true);
                nextBtn.setEnabled(current[0] < total - 1);
                nextBtn.setText(current[0] == total - 1 ? "Finish" : "Next →");
            }
        });

        var navRow = new Div(prevBtn, stepLabel, nextBtn);

        var container = new Div(stepper, navRow);

        return new DemoExample("Interactive (server-driven navigation)", container, """
                FlowStepper stepper = new FlowStepper(
                    List.of("Details", "Shipping", "Payment", "Confirm")
                );

                // Server-driven navigation:
                nextButton.addClickListener(e -> stepper.next());
                prevButton.addClickListener(e -> stepper.previous());
                // Or jump to a specific step:
                stepper.goTo(2);

                // Listen for step changes:
                stepper.addStepChangedListener(e ->
                    log.info("Now on step {}", e.getStep())
                );
                """);
    }
}

