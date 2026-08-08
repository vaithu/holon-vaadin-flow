package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo page for the {@link FlowStepper} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Desktop CRM-style mockup</li>
 *   <li>Mobile CRM-style mockup</li>
 *   <li>Interactive stepper with server-driven navigation</li>
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
                "This demo now mirrors the new customer mockups: a desktop layout with the full step rail " +
                "and a mobile layout that compresses the stepper into a pill row on small screens.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(desktopMockupExample());
        examples.add(mobileMockupExample());
        examples.add(interactiveExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample desktopMockupExample() {
        var stepper = new FlowStepper(List.of("Contact", "Company", "Address", "Terms", "Review"), 0);
        stepper.setClickNavigation(FlowStepper.ClickNavigation.COMPLETED);

        var preview = ResponsiveDiv.flex().column().gapM().build();
        preview.add(mockupHeader(
                "Desktop CRM mockup",
                "Full-width workspace with a linear step rail",
                "New"));
        preview.add(stepper);
        preview.add(mockupSummary(
                "Step 1 — Contact", "Import data, capture contact details, and validate the email address.",
                "Step 2 — Company", "Capture industry, company size, owner, and lead source.",
                "Step 3 — Review", "Confirm the record before saving the customer."));
        preview.add(mockupFooter());

        return new DemoExample("Desktop CRM mockup", preview, """
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Contact", "Company", "Address", "Terms", "Review")
                    .currentStep(0)
                    .clickNavigation(FlowStepper.ClickNavigation.COMPLETED)
                    .build();

                // The stepper now matches the desktop new-customer mockup.
                """);
    }

    private DemoExample mobileMockupExample() {
        var stepper = new FlowStepper(List.of("Contact", "Company", "Address", "Terms", "Review"), 1);
        stepper.setClickNavigation(FlowStepper.ClickNavigation.ALL);

        var preview = ResponsiveDiv.flex().column().gapM().build();
        preview.add(mockupHeader(
                "Mobile CRM mockup",
                "Compact chip row for smaller screens",
                "Mobile"));
        preview.add(stepper);
        preview.add(mockupSummary(
                "Compact step row", "The stepper collapses into a horizontal chip list on narrow viewports.",
                "Touch-friendly navigation", "The current step stays prominent while completed steps remain accessible.",
                "Responsive note", "Resize the browser to see the mobile presentation in action."));

        return new DemoExample("Mobile CRM mockup", preview, """
                FlowStepper stepper = FlowStepper.builder()
                    .steps("Contact", "Company", "Address", "Terms", "Review")
                    .currentStep(1)
                    .clickNavigation(FlowStepper.ClickNavigation.ALL)
                    .build();

                // On small screens, the component uses the pill/chip presentation.
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

        prevBtn.addClickListener(event -> {
            event.getSource();
            if (current[0] > 0) {
                current[0]--;
                stepper.setCurrentStep(current[0]);
                stepLabel.setText("Step " + (current[0] + 1) + " of " + total);
                prevBtn.setEnabled(current[0] > 0);
                nextBtn.setEnabled(true);
                nextBtn.setText(current[0] == total - 1 ? "Finish" : "Next →");
            }
        });

        nextBtn.addClickListener(event -> {
            event.getSource();
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
                nextButton.addClickListener(e -> stepper.nextStep());
                prevButton.addClickListener(e -> stepper.prevStep());
                // Or jump to a specific step:
                stepper.goToStep(2);

                // Listen for step changes:
                stepper.addStepChangedListener(e ->
                    log.info("Now on step {}", e.getStep())
                );
                """);
    }

    private Div mockupHeader(String title, String subtitle, String badgeText) {
        var header = new Div();
        header.addClassName("app-card");
        header.addClassName("app-card--elevated");

        var row = new Div();
        row.addClassName("app-card__header");

        var text = new Div(new H3(title), new Paragraph(subtitle));

        var badge = new Span(badgeText);
        badge.addClassName("app-badge");
        badge.addClassName("app-badge--success");

        row.add(text, badge);
        header.add(row);
        return header;
    }

    private Div mockupSummary(String leftTitle, String leftText, String middleTitle, String middleText,
            String rightTitle, String rightText) {
        var grid = ResponsiveDiv.grid().mobile(1).desktop(3).gapM().build();
        grid.add(mockupCard(leftTitle, leftText));
        grid.add(mockupCard(middleTitle, middleText));
        grid.add(mockupCard(rightTitle, rightText));
        return grid;
    }

    private Div mockupCard(String title, String text) {
        var card = new Div();
        card.addClassName("app-card");
        card.add(new H3(title), new Paragraph(text));
        return card;
    }

    private Div mockupFooter() {
        var footer = new Div();
        footer.addClassName("app-card");

        var row = new Div();
        row.addClassName("app-toolbar");

        var footerLabel = new Paragraph("Step 1 of 5 — Contact information");
        footerLabel.addClassName("app-card__title");

        var spacer = new Div();
        spacer.addClassName("app-toolbar__spacer");

        var cancel = new Button("Cancel");
        var confirm = new Button("Create customer");

        row.add(footerLabel, spacer, cancel, confirm);
        footer.add(row);
        return footer;
    }
}

