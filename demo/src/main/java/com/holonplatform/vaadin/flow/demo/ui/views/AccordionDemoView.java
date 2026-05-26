package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the {@link Accordion} component via Holon's fluent
 * {@link com.holonplatform.vaadin.flow.components.builders.AccordionBuilder}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic accordion with string summaries</li>
 *   <li>Fluent panel sub-builder with content</li>
 *   <li>Panel styling (theme variants, class names, tooltip)</li>
 *   <li>Opened-change listener</li>
 * </ol>
 */
@PageTitle("Accordion – Holon Demo")
@Route(value = "accordion", layout = DemoMainLayout.class)
public class AccordionDemoView extends Div {

    public AccordionDemoView() {
        addClassName("app-view");

        // ── Page header ───────────────────────────────────────────────────────
        var header = ResponsiveDiv.flex()
                .column().gapXS()
                .add(
                    new H1("Accordion"),
                    new Paragraph(
                        "Fluent builder for Vaadin's Accordion component. "
                        + "Supports panel sub-builders with theme variants, class names, "
                        + "tooltips, and opened-change listeners — all via the Holon builder pattern.")
                )
                .build();

        // ── Examples — each DemoExample renders as an rdiv-card ───────────────
        var examples = ResponsiveDiv.flex()
                .column().gapL()
                .add(
                    basicExample(),
                    fluentPanelExample(),
                    styledPanelsExample(),
                    openedChangeExample()
                )
                .build();

        add(header, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var preview = new Div();

        Accordion accordion = Components.accordion()
                .withAccordion("Personal Information", new Paragraph("Name, date of birth, phone, email."))
                .withAccordion("Billing Address", new Paragraph("Street, city, state, zip code."))
                .withAccordion("Payment Method", new Paragraph("Credit card, PayPal, bank transfer."))
                .build();

        preview.add(accordion);

        return new DemoExample("Basic Accordion", preview, """
                Accordion accordion = Components.accordion()
                    .withAccordion("Personal Information",
                        new Paragraph("Name, date of birth, phone, email."))
                    .withAccordion("Billing Address",
                        new Paragraph("Street, city, state, zip code."))
                    .withAccordion("Payment Method",
                        new Paragraph("Credit card, PayPal, bank transfer."))
                    .build();
                """);
    }

    private DemoExample fluentPanelExample() {
        var preview = new Div();

        Accordion accordion = Components.accordion()
                .withPanel("Account Details")
                    .add(new Paragraph("Username, display name, avatar."))
                    .add(new Paragraph("Manage your public profile settings."))
                    .add()
                .withPanel("Security")
                    .add(new Paragraph("Password, two-factor authentication."))
                    .add()
                .withPanel("Notifications")
                    .add(new Paragraph("Email alerts, push notifications, digest frequency."))
                    .add()
                .build();

        preview.add(accordion);

        return new DemoExample("Fluent Panel Builder", preview, """
                Accordion accordion = Components.accordion()
                    .withPanel("Account Details")
                        .add(new Paragraph("Username, display name, avatar."))
                        .add(new Paragraph("Manage your public profile settings."))
                        .add()
                    .withPanel("Security")
                        .add(new Paragraph("Password, two-factor authentication."))
                        .add()
                    .withPanel("Notifications")
                        .add(new Paragraph("Email alerts, push notifications."))
                        .add()
                    .build();
                """);
    }

    private DemoExample styledPanelsExample() {
        var preview = new Div();

        Accordion accordion = Components.accordion()
                .withPanel("Filled Variant")
                    .withThemeVariants(DetailsVariant.FILLED)
                    .add(new Paragraph("This panel uses the FILLED theme variant."))
                    .add()
                .withPanel("Custom Class")
                    .styleName("demo-highlight-panel")
                    .tooltipText("This panel has a tooltip")
                    .add(new Paragraph("This panel has a custom CSS class and tooltip."))
                    .add()
                .withPanel("Disabled Panel")
                    .enabled(false)
                    .add(new Paragraph("This panel is disabled and cannot be expanded."))
                    .add()
                .build();

        preview.add(accordion);

        return new DemoExample("Styled Panels", preview, """
                Accordion accordion = Components.accordion()
                    .withPanel("Filled Variant")
                        .withThemeVariants(DetailsVariant.FILLED)
                        .add(new Paragraph("Uses the FILLED theme variant."))
                        .add()
                    .withPanel("Custom Class")
                        .styleName("my-panel")
                        .tooltipText("This panel has a tooltip")
                        .add(new Paragraph("Custom CSS class and tooltip."))
                        .add()
                    .withPanel("Disabled Panel")
                        .enabled(false)
                        .add(new Paragraph("Cannot be expanded."))
                        .add()
                    .build();
                """);
    }

    private DemoExample openedChangeExample() {
        var preview = new Div();

        var statusLabel = new Span("No panel opened yet.");

        Accordion accordion = Components.accordion()
                .withAccordion("Step 1: Choose Plan", new Paragraph("Free, Pro, or Enterprise."))
                .withAccordion("Step 2: Enter Details", new Paragraph("Billing and shipping information."))
                .withAccordion("Step 3: Confirm", new Paragraph("Review and place your order."))
                .withOpenedChangeListener(event -> {
                    var opened = event.getOpenedPanel();
                    if (opened.isPresent()) {
                        int index = event.getOpenedIndex().orElse(-1);
                        statusLabel.setText("Opened panel index: " + index);
                    } else {
                        statusLabel.setText("All panels closed.");
                    }
                })
                .build();

        preview.add(accordion, statusLabel);

        return new DemoExample("Opened Change Listener", preview, """
                var statusLabel = new Span("No panel opened yet.");

                Accordion accordion = Components.accordion()
                    .withAccordion("Step 1", new Paragraph("Choose Plan."))
                    .withAccordion("Step 2", new Paragraph("Enter Details."))
                    .withAccordion("Step 3", new Paragraph("Confirm."))
                    .withOpenedChangeListener(event -> {
                        var opened = event.getOpenedPanel();
                        if (opened.isPresent()) {
                            int index = event.getOpenedIndex().orElse(-1);
                            statusLabel.setText("Opened: " + index);
                        } else {
                            statusLabel.setText("All panels closed.");
                        }
                    })
                    .build();
                """);
    }
}
