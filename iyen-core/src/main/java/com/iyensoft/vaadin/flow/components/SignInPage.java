package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.i18n.SignInI18N;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;

/**
 * Generic, theme-agnostic sign-in page inspired by the TailAdmin "Sign In" template.
 *
 * <p>The component is intentionally UI-only: it exposes typed events and does not perform any
 * authentication itself. Wire the {@link SignInEvent} (and the optional social / navigation events)
 * to your own security layer.
 *
 * <p>For a self-contained flow, register an {@link Authenticator} via
 * {@link #setAuthenticator(Authenticator)} and declare navigation targets with
 * {@link #setSuccessNavigationTarget(Class)} and {@link #setFailureNavigationTarget(Class)}. The
 * authenticator returns an {@link AuthenticationResult}: success navigates to the success target,
 * failure navigates to the failure target, and invalid credentials keep the user on this page with
 * an inline error message.
 *
 * <p>Layout is a split view: a form panel on the left and an optional branding panel on the right.
 * All styling lives in {@code context://signin-page.css} so the component works with any theme.
 *
 * <p>All texts are localized through the Holon platform (see {@link SignInI18N}). The developer
 * chooses the language by setting the current {@link java.util.Locale} on the
 * {@link com.holonplatform.core.i18n.LocalizationContext} (or Vaadin session) and registering the
 * {@code SignInMessages} bundle. Individual texts can also be overridden via the {@code set*}
 * methods, which accept either a plain {@link String} or a {@link Localizable}.
 */
@StyleSheet("context://signin-page.css")
public class SignInPage extends Div implements LocaleChangeObserver {

    private static final String CLASS_ROOT = "iyen-signin";

    private static final String DEFAULT_BRAND_LOGO_SVG = """
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
            </svg>
            """;

    private final Div formSide = new Div();
    private final Div brandSide = new Div();

    private final H1 heading = new H1();
    private final Paragraph subtitle = new Paragraph();

    private final Div socialRow = new Div();
    private final Div divider = new Div();
    private final Span dividerLabel = new Span();

    private final ValidatableInput<String> emailField = Input.string()
            .label(SignInI18N.EMAIL_LABEL)
            .placeholder(SignInI18N.EMAIL_PLACEHOLDER)
            .clearButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signin__email")
            .validatable()
            .required(SignInI18N.EMAIL_REQUIRED)
            .withValidator(Validator.email(SignInI18N.EMAIL_INVALID))
            .build();
    private final ValidatableInput<String> passwordField = Input.password()
            .label(SignInI18N.PASSWORD_LABEL)
            .placeholder(SignInI18N.PASSWORD_PLACEHOLDER)
            .revealButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signin__password")
            .validatable()
            .required(SignInI18N.PASSWORD_REQUIRED)
            .build();
    private final Input<Boolean> keepLoggedIn = Input.boolean_()
            .label(SignInI18N.KEEP_LOGGED_IN)
            .styleName("iyen-signin__keep")
            .build();
    private final Span forgotPasswordLink = new Span();
    private final Button signInButton = ButtonBuilder.create()
            .text(SignInI18N.SIGN_IN_BUTTON)
            .primary()
            .styleName("iyen-signin__submit")
            .withClickListener(e -> fireSignIn())
            .build();

    private final Div signUpRow = new Div();
    private final Span signUpPrompt = new Span();
    private final Span signUpLink = new Span();

    private final Div errorMessage = new Div();

    private final Div brandContent = new Div();
    private final H2 brandTitle = new H2();
    private final Paragraph brandText = new Paragraph();
    private final Div brandLogo = new Div();

    private Localizable headingText = SignInI18N.HEADING;
    private Localizable subtitleText = SignInI18N.SUBTITLE;
    private Localizable dividerText = SignInI18N.DIVIDER;
    private Localizable forgotPasswordText = SignInI18N.FORGOT_PASSWORD;
    private Localizable signUpPromptText = SignInI18N.SIGN_UP_PROMPT;
    private Localizable signUpLinkText = SignInI18N.SIGN_UP_LINK;
    private Localizable brandTitleText = SignInI18N.BRAND_TITLE;
    private Localizable brandTextText = SignInI18N.BRAND_TEXT;

    private Authenticator authenticator;
    private SerializableRunnable onSuccess;
    private SerializableRunnable onFailure;

    public SignInPage() {
        addClassName(CLASS_ROOT);

        buildFormSide();
        buildBrandSide();

        add(formSide, brandSide);

        localizeStaticTexts();
    }

    // ---------------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------------

    private void buildFormSide() {
        formSide.addClassName("iyen-signin__form-side");

        Div card = new Div();
        card.addClassName("iyen-signin__card");

        heading.addClassName("iyen-signin__heading");
        subtitle.addClassName("iyen-signin__subtitle");

        buildSocialButtons();
        buildDivider();
        buildForm();

        errorMessage.addClassName("iyen-signin__error");
        errorMessage.setVisible(false);

        signUpRow.addClassName("iyen-signin__signup");
        signUpLink.addClassName("iyen-signin__link");
        signUpLink.addClickListener(e -> fireEvent(new SignUpEvent(this, true)));
        signUpRow.add(signUpPrompt, signUpLink);

        card.add(heading, subtitle, socialRow, divider, errorMessage,
                buildFields(), buildOptions(), signInButton, signUpRow);

        formSide.add(card);
    }

    private void buildSocialButtons() {
        socialRow.addClassName("iyen-signin__social");

        Button google = ButtonBuilder.create()
                .text(SignInI18N.SOCIAL_GOOGLE)
                .icon(googleIcon())
                .styleName("iyen-signin__social-button")
                .withClickListener(e -> fireEvent(new SocialSignInEvent(this, true, "google")))
                .build();

        Button x = ButtonBuilder.create()
                .text(SignInI18N.SOCIAL_X)
                .icon(xIcon())
                .styleName("iyen-signin__social-button")
                .withClickListener(e -> fireEvent(new SocialSignInEvent(this, true, "x")))
                .build();

        socialRow.add(google, x);
    }

    private void buildDivider() {
        divider.addClassName("iyen-signin__divider");
        divider.add(dividerLabel);
    }

    private void buildForm() {
        signInButton.addClickShortcut(Key.ENTER);
    }

    private Component buildFields() {
        Div fields = new Div(emailField.getComponent(), passwordField.getComponent());
        fields.addClassName("iyen-signin__fields");
        return fields;
    }

    private Component buildOptions() {
        Div options = new Div();
        options.addClassName("iyen-signin__options");
        forgotPasswordLink.addClassName("iyen-signin__link");
        forgotPasswordLink.addClickListener(e -> fireEvent(new ForgotPasswordEvent(this, true)));
        options.add(keepLoggedIn.getComponent(), forgotPasswordLink);
        return options;
    }

    private void buildBrandSide() {
        brandSide.addClassName("iyen-signin__brand-side");

        brandLogo.addClassName("iyen-signin__brand-logo");
        brandLogo.add(new Html(DEFAULT_BRAND_LOGO_SVG));

        brandTitle.addClassName("iyen-signin__brand-title");
        brandText.addClassName("iyen-signin__brand-text");

        brandContent.add(brandLogo, brandTitle, brandText);
        brandContent.addClassName("iyen-signin__brand-content");
        brandSide.add(brandContent);
    }

    private void fireSignIn() {
        // Validate both fields; validation errors are shown inline on each input.
        boolean emailValid = emailField.isValid();
        boolean passwordValid = passwordField.isValid();
        if (!emailValid || !passwordValid) {
            return;
        }
        String email = emailField.getValue();
        String password = passwordField.getValue();
        boolean keep = Boolean.TRUE.equals(keepLoggedIn.getValue());

        // Always notify listeners so the event-based API keeps working.
        fireEvent(new SignInEvent(this, true, email, password, keep));

        // If an authenticator is configured, let it decide the outcome and drive navigation.
        if (authenticator != null) {
            applyResult(authenticator.authenticate(email, password, keep));
        }
    }

    private void applyResult(AuthenticationResult result) {
        if (result == null) {
            return;
        }
        switch (result.outcome) {
            case SUCCESS -> {
                setErrorMessage(null);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            }
            case INVALID_CREDENTIALS -> setErrorMessage(result.errorMessage);
            case FAILURE -> {
                if (onFailure != null) {
                    onFailure.run();
                } else {
                    setErrorMessage(result.errorMessage);
                }
            }
        }
    }

    private static void navigateTo(String route) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.navigate(route);
        }
    }

    private static void navigateTo(Class<? extends Component> target) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.navigate(target);
        }
    }

    // ---------------------------------------------------------------------
    // Localization
    // ---------------------------------------------------------------------

    /**
     * Re-localizes the plain-text elements (headings, links, branding) using the current locale.
     * The Holon input and button components localize themselves from their {@link Localizable}.
     */
    private void localizeStaticTexts() {
        heading.setText(localize(headingText));
        subtitle.setText(localize(subtitleText));
        dividerLabel.setText(localize(dividerText));
        forgotPasswordLink.setText(localize(forgotPasswordText));
        signUpPrompt.setText(localize(signUpPromptText));
        signUpLink.setText(localize(signUpLinkText));
        brandTitle.setText(localize(brandTitleText));
        brandText.setText(localize(brandTextText));
    }

    private static String localize(Localizable text) {
        return LocalizationProvider.localize(text).orElseGet(() -> text.getMessage() == null ? "" : text.getMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        localizeStaticTexts();
    }

    // ---------------------------------------------------------------------
    // Public configuration API
    // ---------------------------------------------------------------------

    /** Sets the main heading, overriding the localized default. */
    public void setHeading(String text) {
        this.headingText = Localizable.of(text);
        heading.setText(text);
    }

    /** Sets the main heading from a {@link Localizable}, keeping it locale-aware. */
    public void setHeading(Localizable text) {
        this.headingText = text;
        heading.setText(localize(text));
    }

    /** Sets the subtitle shown under the heading, overriding the localized default. */
    public void setSubtitle(String text) {
        this.subtitleText = Localizable.of(text);
        subtitle.setText(text);
    }

    /** Sets the subtitle from a {@link Localizable}, keeping it locale-aware. */
    public void setSubtitle(Localizable text) {
        this.subtitleText = text;
        subtitle.setText(localize(text));
    }

    /**
     * Registers the authentication logic that runs when the user submits the form.
     *
     * <p>The authenticator decides the outcome by returning an {@link AuthenticationResult}:
     * {@link AuthenticationResult#success()} navigates to the configured success target,
     * {@link AuthenticationResult#failure(String)} navigates to the configured failure target,
     * and {@link AuthenticationResult#invalidCredentials(String)} keeps the user on this page and
     * shows the given error message inline.
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /** Sets the route navigated to after a successful sign-in. */
    public void setSuccessNavigationTarget(String route) {
        this.onSuccess = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to after a successful sign-in. */
    public void setSuccessNavigationTarget(Class<? extends Component> target) {
        this.onSuccess = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action (e.g. closing a dialog) after a successful sign-in. */
    public void setOnSuccess(SerializableRunnable action) {
        this.onSuccess = action;
    }

    /** Sets the route navigated to when authentication fails (other than invalid credentials). */
    public void setFailureNavigationTarget(String route) {
        this.onFailure = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to when authentication fails (other than invalid credentials). */
    public void setFailureNavigationTarget(Class<? extends Component> target) {
        this.onFailure = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action when authentication fails (other than invalid credentials). */
    public void setOnFailure(SerializableRunnable action) {
        this.onFailure = action;
    }

    /** Shows or hides the social sign-in buttons and the "Or" divider. */
    public void setSocialLoginVisible(boolean visible) {
        socialRow.setVisible(visible);
        divider.setVisible(visible);
    }

    /** Shows or hides the "Keep me logged in" checkbox. */
    public void setKeepLoggedInVisible(boolean visible) {
        keepLoggedIn.getComponent().setVisible(visible);
    }

    /** Shows or hides the "Forgot password?" link. */
    public void setForgotPasswordVisible(boolean visible) {
        forgotPasswordLink.setVisible(visible);
    }

    /** Shows or hides the "Don't have an account? Sign Up" row. */
    public void setSignUpVisible(boolean visible) {
        signUpRow.setVisible(visible);
    }

    /** Shows or hides the right-hand branding panel. */
    public void setBrandingVisible(boolean visible) {
        brandSide.setVisible(visible);
    }

    /** Sets the branding title on the right-hand panel, overriding the localized default. */
    public void setBrandingTitle(String text) {
        this.brandTitleText = Localizable.of(text);
        brandTitle.setText(text);
    }

    /** Sets the branding title from a {@link Localizable}, keeping it locale-aware. */
    public void setBrandingTitle(Localizable text) {
        this.brandTitleText = text;
        brandTitle.setText(localize(text));
    }

    /** Sets the branding description on the right-hand panel, overriding the localized default. */
    public void setBrandingText(String text) {
        this.brandTextText = Localizable.of(text);
        brandText.setText(text);
    }

    /** Sets the branding description from a {@link Localizable}, keeping it locale-aware. */
    public void setBrandingText(Localizable text) {
        this.brandTextText = text;
        brandText.setText(localize(text));
    }

    /** Replaces the branding logo with the given content (e.g. an {@code Image} or SVG). */
    public void setBrandingLogo(Component logo) {
        brandLogo.removeAll();
        brandLogo.add(logo);
    }

    /**
     * Fully replaces the inner content of the right-hand branding panel with a custom component,
     * ignoring the built-in logo/title/text. Pass {@code null} to restore the default content.
     */
    public void setBrandingContent(Component content) {
        brandSide.removeAll();
        brandSide.add(content == null ? brandContent : content);
    }

    /** Displays an error message above the form. Pass {@code null} or blank to clear it. */
    public void setErrorMessage(String message) {
        if (message == null || message.isBlank()) {
            errorMessage.setText("");
            errorMessage.setVisible(false);
        } else {
            errorMessage.setText(message);
            errorMessage.setVisible(true);
        }
    }

    /** Clears the email and password fields and any error message. */
    public void reset() {
        emailField.clear();
        passwordField.clear();
        keepLoggedIn.clear();
        setErrorMessage(null);
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    public ValidatableInput<String> getEmailField() {
        return emailField;
    }

    public ValidatableInput<String> getPasswordField() {
        return passwordField;
    }

    public Input<Boolean> getKeepLoggedInCheckbox() {
        return keepLoggedIn;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    // ---------------------------------------------------------------------
    // Event registration
    // ---------------------------------------------------------------------

    /** Adds a listener invoked when the user submits the sign-in form. */
    public Registration addSignInListener(ComponentEventListener<SignInEvent> listener) {
        return addListener(SignInEvent.class, listener);
    }

    /** Adds a listener invoked when a social sign-in button is pressed. */
    public Registration addSocialSignInListener(ComponentEventListener<SocialSignInEvent> listener) {
        return addListener(SocialSignInEvent.class, listener);
    }

    /** Adds a listener invoked when the "Forgot password?" link is clicked. */
    public Registration addForgotPasswordListener(ComponentEventListener<ForgotPasswordEvent> listener) {
        return addListener(ForgotPasswordEvent.class, listener);
    }

    /** Adds a listener invoked when the "Sign Up" link is clicked. */
    public Registration addSignUpListener(ComponentEventListener<SignUpEvent> listener) {
        return addListener(SignUpEvent.class, listener);
    }

    // ---------------------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------------------

    /**
     * Authentication callback invoked when the user submits the form.
     *
     * <p>Implementations validate the given credentials and return an {@link AuthenticationResult}
     * describing the outcome. The {@link SignInPage} then navigates or shows an inline error
     * accordingly.
     */
    @FunctionalInterface
    public interface Authenticator extends Serializable {

        /**
         * Validates the submitted credentials.
         *
         * @param email        the submitted email
         * @param password     the submitted password
         * @param keepLoggedIn whether "keep me logged in" was checked
         * @return the authentication outcome (never {@code null})
         */
        AuthenticationResult authenticate(String email, String password, boolean keepLoggedIn);
    }

    /**
     * Outcome returned by an {@link Authenticator}. Create one via the static factory methods:
     * {@link #success()} to navigate to the success target, {@link #invalidCredentials(String)} to
     * stay on the page and show an inline error, or {@link #failure(String)} to navigate to the
     * failure target.
     */
    public static final class AuthenticationResult implements Serializable {

        private enum Outcome {
            SUCCESS, INVALID_CREDENTIALS, FAILURE
        }

        private final Outcome outcome;
        private final String errorMessage;

        private AuthenticationResult(Outcome outcome, String errorMessage) {
            this.outcome = outcome;
            this.errorMessage = errorMessage;
        }

        /** Authentication succeeded: navigate to the configured success target. */
        public static AuthenticationResult success() {
            return new AuthenticationResult(Outcome.SUCCESS, null);
        }

        /** Credentials did not match: stay on the sign-in page and show {@code errorMessage}. */
        public static AuthenticationResult invalidCredentials(String errorMessage) {
            return new AuthenticationResult(Outcome.INVALID_CREDENTIALS, errorMessage);
        }

        /**
         * Authentication failed for another reason: navigate to the configured failure target, or,
         * if none is set, show {@code errorMessage} on the sign-in page.
         */
        public static AuthenticationResult failure(String errorMessage) {
            return new AuthenticationResult(Outcome.FAILURE, errorMessage);
        }

        /** Authentication failed for another reason: navigate to the configured failure target. */
        public static AuthenticationResult failure() {
            return new AuthenticationResult(Outcome.FAILURE, null);
        }
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    /** Fired when the user submits the sign-in form. */
    public static class SignInEvent extends ComponentEvent<SignInPage> {

        private final String email;
        private final String password;
        private final boolean keepLoggedIn;

        public SignInEvent(SignInPage source, boolean fromClient, String email, String password,
                boolean keepLoggedIn) {
            super(source, fromClient);
            this.email = email;
            this.password = password;
            this.keepLoggedIn = keepLoggedIn;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public boolean isKeepLoggedIn() {
            return keepLoggedIn;
        }
    }

    /** Fired when a social sign-in button is pressed. */
    public static class SocialSignInEvent extends ComponentEvent<SignInPage> {

        private final String provider;

        public SocialSignInEvent(SignInPage source, boolean fromClient, String provider) {
            super(source, fromClient);
            this.provider = provider;
        }

        /** The provider identifier, e.g. {@code "google"} or {@code "x"}. */
        public String getProvider() {
            return provider;
        }
    }

    /** Fired when the "Forgot password?" link is clicked. */
    public static class ForgotPasswordEvent extends ComponentEvent<SignInPage> {
        public ForgotPasswordEvent(SignInPage source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    /** Fired when the "Sign Up" link is clicked. */
    public static class SignUpEvent extends ComponentEvent<SignInPage> {
        public SignUpEvent(SignInPage source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    // ---------------------------------------------------------------------
    // Default inline icons (kept generic; override via setBrandingLogo)
    // ---------------------------------------------------------------------

    private static Html googleIcon() {
        return new Html("""
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 48 48">
                  <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
                  <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                  <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
                  <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
                </svg>
                """);
    }

    private static Html xIcon() {
        return new Html("""
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24h-6.66l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231 5.45-6.231zm-1.161 17.52h1.833L7.084 4.126H5.117L17.083 19.77z"/>
                </svg>
                """);
    }
}
