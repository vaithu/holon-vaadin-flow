package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator.DialogSize;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link DialogBuilder} hierarchy.
 *
 * <p>Covers:
 * <ol>
 *   <li>Message dialog — generic info overlay</li>
 *   <li>Confirm dialog — single OK action</li>
 *   <li>Question dialog — yes / no with callback</li>
 *   <li>Delete dialog — destructive confirmation</li>
 * </ol>
 */
@PageTitle("Dialog – Holon Demo")
@Route(value = "dialog", layout = DemoMainLayout.class)
public class DialogDemoView extends Div {

    public DialogDemoView() {
        addClassName("app-view");

        var title = new H1("Dialog");

        var desc = new Paragraph(
                "Fluent builders for Vaadin's Dialog component. "
                + "Provides message, confirm, question, delete, save, and save-and-new "
                + "dialog presets — all with structured header/body/footer slots, "
                + "i18n support, and optional trigger wiring.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(messageExample());
        examples.add(confirmExample());
        examples.add(questionExample());
        examples.add(deleteExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample messageExample() {
        var preview = new Div();

        var trigger = DialogBuilder.message()
                .withTitle("Information")
                .withDescription("This is a simple informational dialog.")
                .withContent("Nothing to worry about — just wanted to let you know.")
                .withSize(DialogSize.SM)
                .closeOnEsc(true)
                .withTrigger("Open Message Dialog")
                .buildTriggered();

        preview.add(trigger);

        return new DemoExample("Message Dialog",
                preview,
                """
                DialogBuilder.message()
                    .withTitle("Information")
                    .withDescription("This is a simple informational dialog.")
                    .withContent("Nothing to worry about — just wanted to let you know.")
                    .withSize(DialogSize.SM)
                    .closeOnEsc(true)
                    .withTrigger("Open Message Dialog")
                    .buildTriggered();
                """);
    }

    private DemoExample confirmExample() {
        var preview = new Div();

        var trigger = DialogBuilder.confirm()
                .withTitle("Terms & Conditions")
                .withContent("By clicking OK you agree to the terms of service.")
                .okButtonConfigurator(btn -> btn.text("I Agree"))
                .withSize(DialogSize.MD)
                .withTrigger("Open Confirm Dialog")
                .buildTriggered();

        preview.add(trigger);

        return new DemoExample("Confirm Dialog",
                preview,
                """
                DialogBuilder.confirm()
                    .withTitle("Terms & Conditions")
                    .withContent("By clicking OK you agree to the terms of service.")
                    .okButtonConfigurator(btn -> btn.text("I Agree"))
                    .withSize(DialogSize.MD)
                    .withTrigger("Open Confirm Dialog")
                    .buildTriggered();
                """);
    }

    private DemoExample questionExample() {
        var preview = new Div();

        var result = new Paragraph("No answer yet.");

        var trigger = DialogBuilder.question(confirmed ->
                        result.setText(confirmed ? "User confirmed!" : "User denied."))
                .withTitle("Unsaved Changes")
                .withContent("You have unsaved changes. Do you want to discard them?")
                .confirmButtonConfigurator(btn -> btn.text("Discard"))
                .denialButtonConfigurator(btn -> btn.text("Keep Editing"))
                .withTrigger("Open Question Dialog")
                .buildTriggered();

        preview.add(trigger, result);

        return new DemoExample("Question Dialog",
                preview,
                """
                DialogBuilder.question(confirmed ->
                        label.setText(confirmed ? "Confirmed!" : "Denied."))
                    .withTitle("Unsaved Changes")
                    .withContent("You have unsaved changes. Do you want to discard them?")
                    .confirmButtonConfigurator(btn -> btn.text("Discard"))
                    .denialButtonConfigurator(btn -> btn.text("Keep Editing"))
                    .withTrigger("Open Question Dialog")
                    .buildTriggered();
                """);
    }

    private DemoExample deleteExample() {
        var preview = new Div();

        var result = new Paragraph("Nothing deleted.");

        var trigger = DialogBuilder.delete(confirmed ->
                        result.setText(confirmed ? "Item deleted." : "Deletion cancelled."))
                .withTitle("Delete this record?")
                .withDescription("This action cannot be undone. The record will be permanently removed.")
                .withTrigger("Open Delete Dialog")
                .buildTriggered();

        preview.add(trigger, result);

        return new DemoExample("Delete Dialog",
                preview,
                """
                DialogBuilder.delete(confirmed ->
                        label.setText(confirmed ? "Deleted." : "Cancelled."))
                    .withTitle("Delete this record?")
                    .withDescription("This action cannot be undone.")
                    .withTrigger("Open Delete Dialog")
                    .buildTriggered();
                """);
    }
}
