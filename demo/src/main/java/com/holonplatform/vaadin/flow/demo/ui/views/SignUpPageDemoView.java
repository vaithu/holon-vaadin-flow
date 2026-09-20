package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.SignUpPage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the generic {@link SignUpPage} component.
 */
@PageTitle("Sign Up – Holon Demo")
@Route(value = "sign-up", layout = DemoMainLayout.class)
public class SignUpPageDemoView extends Div {

    public SignUpPageDemoView() {
        addClassName("app-view");

        H1 title = new H1("Sign Up");
        Paragraph description = new Paragraph(
                "SignUpPage is a generic, theme-agnostic split sign-up layout. It performs no "
                        + "account registration itself — wire its events to your own security layer.");

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(fullExample(), minimalExample());

        add(title, description, examples);
    }

    private DemoExample fullExample() {
        SignUpPage signUp = Components.signUpPage()
                .styleName("iyen-signup--boxed")
                .withSignUpListener(e -> notify("Sign up: " + e.getFirstName() + " " + e.getLastName()
                        + " <" + e.getEmail() + "> (agreed: " + e.isAgreedToTerms() + ")"))
                .withSocialSignUpListener(e -> notify("Social sign up with: " + e.getProvider()))
                .withSignInListener(e -> notify("Sign in clicked"))
                .build();

        return new DemoExample("Sign up — full layout", signUp, """
                SignUpPage signUp = Components.signUpPage()
                    .withSignUpListener(e -> {
                        // e.getFirstName(), e.getLastName(), e.getEmail(), e.getPassword()
                        register(e.getEmail(), e.getPassword());
                    })
                    .withSocialSignUpListener(e -> oauth(e.getProvider()))
                    .withSignInListener(e -> UI.getCurrent().navigate("sign-in"))
                    .build();

                // Show a server-side validation error:
                signUp.setErrorMessage("That email is already registered.");
                """);
    }

    private DemoExample minimalExample() {
        SignUpPage signUp = Components.signUpPage()
                .styleName("iyen-signup--boxed")
                .socialLogin(false)
                .signIn(false)
                .branding(false)
                .heading("Create account")
                .subtitle("Sign up to get started.")
                .withSignUpListener(e -> notify("Sign up: " + e.getEmail()))
                .build();

        return new DemoExample("Sign up — minimal (form only)", signUp, """
                SignUpPage signUp = Components.signUpPage()
                    .socialLogin(false)
                    .signIn(false)
                    .branding(false)
                    .heading("Create account")
                    .subtitle("Sign up to get started.")
                    .withSignUpListener(e -> register(e.getEmail(), e.getPassword()))
                    .build();
                """);
    }

    private static void notify(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
