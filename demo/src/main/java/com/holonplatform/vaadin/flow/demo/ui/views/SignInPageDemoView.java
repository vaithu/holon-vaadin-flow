package com.holonplatform.vaadin.flow.demo.ui.views;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.SignInPage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the generic {@link SignInPage} component.
 */
@PageTitle("Sign In – Holon Demo")
@Route(value = "sign-in", layout = DemoMainLayout.class)
public class SignInPageDemoView extends Div {

    public SignInPageDemoView() {
        addClassName("app-view");

        H1 title = new H1("Sign In");
        Paragraph description = new Paragraph(
                "SignInPage is a generic, theme-agnostic split sign-in layout. It performs no "
                        + "authentication itself — wire its events to your own security layer.");

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(fullExample(), minimalExample());

        add(title, description, examples);
    }

    private DemoExample fullExample() {
        SignInPage signIn = Components.signInPage()
                .styleName("iyen-signin--boxed")
                .withSignInListener(e -> {
                    if (e.getEmail() == null || e.getEmail().isBlank()
                            || e.getPassword() == null || e.getPassword().isBlank()) {
                        notify("Please enter both email and password.");
                        return;
                    }
                    notify("Sign in: " + e.getEmail() + " (keep logged in: " + e.isKeepLoggedIn() + ")");
                })
                .withSocialSignInListener(e -> notify("Social sign in with: " + e.getProvider()))
                .withForgotPasswordListener(e -> notify("Forgot password clicked"))
                .withSignUpListener(e -> notify("Sign up clicked"))
                .build();

        return new DemoExample("Sign in — full layout", signIn, """
                SignInPage signIn = Components.signInPage()
                    .withSignInListener(e -> {
                        // e.getEmail(), e.getPassword(), e.isKeepLoggedIn()
                        authenticate(e.getEmail(), e.getPassword());
                    })
                    .withSocialSignInListener(e -> oauth(e.getProvider()))
                    .withForgotPasswordListener(e -> UI.getCurrent().navigate("reset-password"))
                    .withSignUpListener(e -> UI.getCurrent().navigate("sign-up"))
                    .build();

                // Show a server-side validation error:
                signIn.setErrorMessage("Invalid email or password.");
                """);
    }

    private DemoExample minimalExample() {
        SignInPage signIn = Components.signInPage()
                .styleName("iyen-signin--boxed")
                .socialLogin(false)
                .signUp(false)
                .branding(false)
                .heading("Welcome back")
                .subtitle("Sign in to continue.")
                .withSignInListener(e -> notify("Sign in: " + e.getEmail()))
                .build();

        return new DemoExample("Sign in — minimal (email + password only)", signIn, """
                SignInPage signIn = Components.signInPage()
                    .socialLogin(false)
                    .signUp(false)
                    .branding(false)
                    .heading("Welcome back")
                    .subtitle("Sign in to continue.")
                    .withSignInListener(e -> authenticate(e.getEmail(), e.getPassword()))
                    .build();
                """);
    }

    private static void notify(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
