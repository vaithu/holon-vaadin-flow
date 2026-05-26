package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link InputOTP} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>6-digit OTP (standard single group)</li>
 *   <li>Split 3+3 with separator</li>
 *   <li>Digit-only constraint ([0-9] pattern)</li>
 *   <li>Alphanumeric constraint</li>
 *   <li>Value change listener + onComplete callback</li>
 *   <li>Read-only / disabled state</li>
 * </ol>
 */
@PageTitle("InputOTP – Holon Demo")
@Route(value = "input-otp", layout = DemoMainLayout.class)
public class InputOTPDemoView extends Div {

    public InputOTPDemoView() {
        addClassName("app-view");

        var title = new H1("InputOTP");

        var desc = new Paragraph(
                "A One-Time Password input that groups individual character slots into a single, " +
                "accessible, visually unified control. " +
                "Focus auto-advances to the next slot on entry; backspace retreats. " +
                "Supports digit/alphanumeric patterns, separators between groups, " +
                "onComplete callback, value change listeners, and read-only/disabled states.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(sixDigitExample());
        examples.add(splitGroupsExample());
        examples.add(digitOnlyExample());
        examples.add(alphanumericExample());
        examples.add(callbackExample());
        examples.add(disabledExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample sixDigitExample() {
        var otp = InputOTP.builder()
                .group(6)
                .build();

        return new DemoExample("6-digit (single group)", otp, """
                // Single group of 6 slots — the simplest form.
                InputOTP otp = InputOTP.builder()
                    .group(6)
                    .build();
                """);
    }

    private DemoExample splitGroupsExample() {
        var otp = InputOTP.builder()
                .group(3)
                .separator()
                .group(3)
                .build();

        return new DemoExample("Split 3 + 3 with separator", otp, """
                // Two groups separated by a visual dash.
                InputOTP otp = InputOTP.builder()
                    .group(3)
                    .separator()
                    .group(3)
                    .build();
                """);
    }

    private DemoExample digitOnlyExample() {
        var otp = InputOTP.builder()
                .group(3)
                .separator()
                .group(3)
                .pattern("[0-9]")
                .build();

        return new DemoExample("Digit-only ([0-9] pattern)", otp, """
                // Reject non-digit keystrokes via a per-slot regex pattern.
                InputOTP otp = InputOTP.builder()
                    .group(3).separator().group(3)
                    .pattern("[0-9]")
                    .build();
                """);
    }

    private DemoExample alphanumericExample() {
        var otp = InputOTP.builder()
                .group(4)
                .separator()
                .group(4)
                .pattern("[0-9a-zA-Z]")
                .build();

        return new DemoExample("Alphanumeric ([0-9a-zA-Z])", otp, """
                InputOTP otp = InputOTP.builder()
                    .group(4).separator().group(4)
                    .pattern("[0-9a-zA-Z]")
                    .build();
                """);
    }

    private DemoExample callbackExample() {
        var valueLabel = new Span("—");

        var otp = InputOTP.builder()
                .group(3)
                .separator()
                .group(3)
                .pattern("[0-9]")
                .onComplete(value ->
                        Notification.show("OTP entered: " + value))
                .build();

        otp.addValueChangeListener(value -> valueLabel.setText(value.isBlank() ? "—" : value));

        var clearBtn = new Button("Clear", e -> otp.clear());

        var statusRow = new Div(new Span("Current value: "), valueLabel, clearBtn);

        var container = new Div(otp, statusRow);

        return new DemoExample("onComplete Callback + Value Listener", container, """
                InputOTP otp = InputOTP.builder()
                    .group(3).separator().group(3)
                    .pattern("[0-9]")
                    // Fires once when ALL slots are filled:
                    .onComplete(value -> Notification.show("OTP: " + value))
                    .build();

                // Fires on every slot change:
                otp.addValueChangeListener(value -> label.setText(value));

                // Read / clear programmatically:
                String current = otp.getValue();
                otp.clear();
                """);
    }

    private DemoExample disabledExample() {
        var preview = new Div();

        // Pre-filled + read-only
        var readOnly = InputOTP.builder().group(3).separator().group(3).build();
        readOnly.setValue("123456");
        readOnly.setReadOnly(true);

        var readOnlyLabel = new Span("Read-only (pre-filled):");
        preview.add(readOnlyLabel, readOnly);

        // Disabled
        var disabled = InputOTP.builder().group(3).separator().group(3).build();
        disabled.setValue("ABC");
        disabled.setEnabled(false);

        var disabledLabel = new Span("Disabled:");
        preview.add(disabledLabel, disabled);

        return new DemoExample("Read-Only & Disabled", preview, """
                // Read-only — shows value but blocks input
                InputOTP otp = InputOTP.builder().group(3).separator().group(3).build();
                otp.setValue("123456");
                otp.setReadOnly(true);

                // Disabled — fully non-interactive
                otp.setEnabled(false);
                """);
    }
}

