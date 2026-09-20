package com.holonplatform.vaadin.flow.demo.ui.views;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the generic {@link ResetPasswordPage} component.
 */
@PageTitle("Reset Password – Holon Demo")
@Route(value = "reset-password", layout = DemoMainLayout.class)
public class ResetPasswordPageDemoView extends Div {

    public ResetPasswordPageDemoView() {
        addClassName("app-view");

        H1 title = new H1("Reset Password");
        Paragraph description = new Paragraph(
                "ResetPasswordPage is a generic, theme-agnostic split \"forgot password\" layout. It "
                        + "sends no email itself — wire its events to your own security layer.");

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(fullExample(), minimalExample());

        add(title, description, examples);
    }

    private DemoExample fullExample() {
        ResetPasswordPage reset = Components.resetPasswordPage()
                .styleName("iyen-reset--boxed")
                .withResetPasswordListener(e -> notify("Reset link requested for: " + e.getEmail()))
                .withBackToSignInListener(e -> notify("Back to sign in clicked"))
                .build();

        return new DemoExample("Reset password — full layout", reset, """
                ResetPasswordPage reset = Components.resetPasswordPage()
                    .withResetPasswordListener(e -> sendResetLink(e.getEmail()))
                    .withBackToSignInListener(e -> UI.getCurrent().navigate("sign-in"))
                    .build();

                // Show a server-side error:
                reset.setErrorMessage("We couldn't process that request.");
                """);
    }

    private DemoExample minimalExample() {
        ResetPasswordPage reset = Components.resetPasswordPage()
                .styleName("iyen-reset--boxed")
                .signIn(false)
                .branding(false)
                .heading("Reset your password")
                .subtitle("We'll email you a reset link.")
                .withResetPasswordListener(e -> notify("Reset link requested for: " + e.getEmail()))
                .build();

        return new DemoExample("Reset password — minimal (form only)", reset, """
                ResetPasswordPage reset = Components.resetPasswordPage()
                    .signIn(false)
                    .branding(false)
                    .heading("Reset your password")
                    .subtitle("We'll email you a reset link.")
                    .withResetPasswordListener(e -> sendResetLink(e.getEmail()))
                    .build();
                """);
    }

    private static void notify(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
