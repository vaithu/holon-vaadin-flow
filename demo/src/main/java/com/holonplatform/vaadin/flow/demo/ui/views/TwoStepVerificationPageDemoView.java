package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.TwoStepVerificationPage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the generic {@link TwoStepVerificationPage} component.
 */
@PageTitle("Two Step Verification – Holon Demo")
@Route(value = "two-step-verification", layout = DemoMainLayout.class)
public class TwoStepVerificationPageDemoView extends Div {

    public TwoStepVerificationPageDemoView() {
        addClassName("app-view");

        H1 title = new H1("Two Step Verification");
        Paragraph description = new Paragraph(
                "TwoStepVerificationPage is a generic, theme-agnostic split layout for a six-digit "
                        + "one-time code. It verifies nothing itself — wire its events to your own security layer.");

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(fullExample(), minimalExample());

        add(title, description, examples);
    }

    private DemoExample fullExample() {
        TwoStepVerificationPage verify = Components.twoStepVerificationPage()
                .styleName("iyen-verify--boxed")
                .withVerifyCodeListener(e -> notify("Verifying code: " + e.getCode()))
                .withResendCodeListener(e -> notify("Resend code clicked"))
                .build();

        return new DemoExample("Two step verification — full layout", verify, """
                TwoStepVerificationPage verify = Components.twoStepVerificationPage()
                    .withVerifyCodeListener(e -> verifyCode(e.getCode()))
                    .withResendCodeListener(e -> resendCode())
                    .build();

                // Show a server-side error:
                verify.setErrorMessage("That code is incorrect or has expired.");
                """);
    }

    private DemoExample minimalExample() {
        TwoStepVerificationPage verify = Components.twoStepVerificationPage()
                .styleName("iyen-verify--boxed")
                .branding(false)
                .heading("Enter your code")
                .subtitle("We texted you a 6-digit code.")
                .withVerifyCodeListener(e -> notify("Verifying code: " + e.getCode()))
                .build();

        return new DemoExample("Two step verification — minimal (form only)", verify, """
                TwoStepVerificationPage verify = Components.twoStepVerificationPage()
                    .branding(false)
                    .heading("Enter your code")
                    .subtitle("We texted you a 6-digit code.")
                    .withVerifyCodeListener(e -> verifyCode(e.getCode()))
                    .build();
                """);
    }

    private static void notify(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
