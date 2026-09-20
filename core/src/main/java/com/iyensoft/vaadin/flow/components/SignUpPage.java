package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.i18n.SignUpI18N;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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
 * Generic, theme-agnostic sign-up page inspired by the TailAdmin "Sign Up" template.
 *
 * <p>The component is intentionally UI-only: it exposes typed events and does not perform any
 * account registration itself. Wire the {@link SignUpEvent} (and the optional social / navigation
 * events) to your own security layer.
 *
 * <p>For a self-contained flow, register a {@link Registrar} via
 * {@link #setRegistrar(Registrar)} and declare navigation targets with
 * {@link #setSuccessNavigationTarget(Class)} and {@link #setFailureNavigationTarget(Class)}. The
 * registrar returns a {@link RegistrationResult}: success navigates to the success target,
 * failure navigates to the failure target, and invalid input keeps the user on this page with an
 * inline error message.
 *
 * <p>Layout is a split view: a form panel on the left and an optional branding panel on the right.
 * All styling lives in {@code context://signup-page.css} so the component works with any theme.
 *
 * <p>All texts are localized through the Holon platform (see {@link SignUpI18N}). The developer
 * chooses the language by setting the current {@link java.util.Locale} on the
 * {@link com.holonplatform.core.i18n.LocalizationContext} (or Vaadin session) and registering the
 * {@code SignUpMessages} bundle. Individual texts can also be overridden via the {@code set*}
 * methods, which accept either a plain {@link String} or a {@link Localizable}.
 */
@StyleSheet("context://signup-page.css")
public class SignUpPage extends Div implements LocaleChangeObserver {

    private static final String CLASS_ROOT = "iyen-signup";

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

    private final ValidatableInput<String> firstNameField = Input.string()
            .label(SignUpI18N.FIRST_NAME_LABEL)
            .placeholder(SignUpI18N.FIRST_NAME_PLACEHOLDER)
            .clearButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signup__firstname")
            .validatable()
            .required(SignUpI18N.FIRST_NAME_REQUIRED)
            .build();
    private final ValidatableInput<String> lastNameField = Input.string()
            .label(SignUpI18N.LAST_NAME_LABEL)
            .placeholder(SignUpI18N.LAST_NAME_PLACEHOLDER)
            .clearButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signup__lastname")
            .validatable()
            .required(SignUpI18N.LAST_NAME_REQUIRED)
            .build();
    private final ValidatableInput<String> emailField = Input.string()
            .label(SignUpI18N.EMAIL_LABEL)
            .placeholder(SignUpI18N.EMAIL_PLACEHOLDER)
            .clearButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signup__email")
            .validatable()
            .required(SignUpI18N.EMAIL_REQUIRED)
            .withValidator(Validator.email(SignUpI18N.EMAIL_INVALID))
            .build();
    private final ValidatableInput<String> passwordField = Input.password()
            .label(SignUpI18N.PASSWORD_LABEL)
            .placeholder(SignUpI18N.PASSWORD_PLACEHOLDER)
            .revealButtonVisible(true)
            .fullWidth()
            .styleName("iyen-signup__password")
            .validatable()
            .required(SignUpI18N.PASSWORD_REQUIRED)
            .build();
    private final Input<Boolean> agreeTerms = Input.boolean_()
            .label(SignUpI18N.TERMS)
            .styleName("iyen-signup__terms")
            .build();
    private final Button signUpButton = ButtonBuilder.create()
            .text(SignUpI18N.SIGN_UP_BUTTON)
            .primary()
            .styleName("iyen-signup__submit")
            .withClickListener(e -> fireSignUp())
            .build();

    private final Div signInRow = new Div();
    private final Span signInPrompt = new Span();
    private final Span signInLink = new Span();

    private final Div errorMessage = new Div();

    // Holds rich consent text (with links) or a custom consent component; empty by default.
    private final Div termsSlot = new Div();

    private final Div brandContent = new Div();
    private final H2 brandTitle = new H2();
    private final Paragraph brandText = new Paragraph();
    private final Div brandLogo = new Div();

    private Localizable headingText = SignUpI18N.HEADING;
    private Localizable subtitleText = SignUpI18N.SUBTITLE;
    private Localizable dividerText = SignUpI18N.DIVIDER;
    private Localizable signInPromptText = SignUpI18N.SIGN_IN_PROMPT;
    private Localizable signInLinkText = SignUpI18N.SIGN_IN_LINK;
    private Localizable termsRequiredText = SignUpI18N.TERMS_REQUIRED;
    private Localizable termsPrefixText = SignUpI18N.TERMS_PREFIX;
    private Localizable termsLinkText = SignUpI18N.TERMS_LINK;
    private Localizable termsSeparatorText = SignUpI18N.TERMS_SEPARATOR;
    private Localizable privacyLinkText = SignUpI18N.PRIVACY_LINK;
    private Localizable brandTitleText = SignUpI18N.BRAND_TITLE;
    private Localizable brandTextText = SignUpI18N.BRAND_TEXT;

    private Registrar registrar;
    private SerializableRunnable onSuccess;
    private SerializableRunnable onFailure;
    private String termsUrl;
    private String privacyUrl;
    private boolean termsLinksActive;

    public SignUpPage() {
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
        formSide.addClassName("iyen-signup__form-side");

        Div card = new Div();
        card.addClassName("iyen-signup__card");

        heading.addClassName("iyen-signup__heading");
        subtitle.addClassName("iyen-signup__subtitle");

        buildSocialButtons();
        buildDivider();
        buildForm();

        errorMessage.addClassName("iyen-signup__error");
        errorMessage.setVisible(false);

        signInRow.addClassName("iyen-signup__signin");
        signInLink.addClassName("iyen-signup__link");
        signInLink.addClickListener(e -> fireEvent(new SignInEvent(this, true)));
        signInRow.add(signInPrompt, signInLink);

        card.add(heading, subtitle, socialRow, divider, errorMessage,
                buildFields(), buildOptions(), signUpButton, signInRow);

        formSide.add(card);
    }

    private void buildSocialButtons() {
        socialRow.addClassName("iyen-signup__social");

        Button google = ButtonBuilder.create()
                .text(SignUpI18N.SOCIAL_GOOGLE)
                .icon(googleIcon())
                .styleName("iyen-signup__social-button")
                .withClickListener(e -> fireEvent(new SocialSignUpEvent(this, true, "google")))
                .build();

        Button x = ButtonBuilder.create()
                .text(SignUpI18N.SOCIAL_X)
                .icon(xIcon())
                .styleName("iyen-signup__social-button")
                .withClickListener(e -> fireEvent(new SocialSignUpEvent(this, true, "x")))
                .build();

        socialRow.add(google, x);
    }

    private void buildDivider() {
        divider.addClassName("iyen-signup__divider");
        divider.add(dividerLabel);
    }

    private void buildForm() {
        signUpButton.addClickShortcut(Key.ENTER);
    }

    private Component buildFields() {
        Div nameRow = new Div(firstNameField.getComponent(), lastNameField.getComponent());
        nameRow.addClassName("iyen-signup__name-row");

        Div fields = new Div(nameRow, emailField.getComponent(), passwordField.getComponent());
        fields.addClassName("iyen-signup__fields");
        return fields;
    }

    private Component buildOptions() {
        Div options = new Div();
        options.addClassName("iyen-signup__options");
        termsSlot.addClassName("iyen-signup__terms-text");
        termsSlot.setVisible(false);
        options.add(agreeTerms.getComponent(), termsSlot);
        return options;
    }

    private void buildBrandSide() {
        brandSide.addClassName("iyen-signup__brand-side");

        brandLogo.addClassName("iyen-signup__brand-logo");
        brandLogo.add(new Html(DEFAULT_BRAND_LOGO_SVG));

        brandTitle.addClassName("iyen-signup__brand-title");
        brandText.addClassName("iyen-signup__brand-text");

        brandContent.add(brandLogo, brandTitle, brandText);
        brandContent.addClassName("iyen-signup__brand-content");
        brandSide.add(brandContent);
    }

    private void fireSignUp() {
        // Validate all fields; validation errors are shown inline on each input.
        boolean firstNameValid = firstNameField.isValid();
        boolean lastNameValid = lastNameField.isValid();
        boolean emailValid = emailField.isValid();
        boolean passwordValid = passwordField.isValid();
        boolean agreed = Boolean.TRUE.equals(agreeTerms.getValue());
        if (!firstNameValid || !lastNameValid || !emailValid || !passwordValid) {
            return;
        }
        if (!agreed) {
            setErrorMessage(localize(termsRequiredText));
            return;
        }
        setErrorMessage(null);
        String firstName = firstNameField.getValue();
        String lastName = lastNameField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();

        // Always notify listeners so the event-based API keeps working.
        fireEvent(new SignUpEvent(this, true, firstName, lastName, email, password, true));

        // If a registrar is configured, let it decide the outcome and drive navigation.
        if (registrar != null) {
            applyResult(registrar.register(firstName, lastName, email, password));
        }
    }

    private void applyResult(RegistrationResult result) {
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
            case INVALID_INPUT -> setErrorMessage(result.errorMessage);
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
        signInPrompt.setText(localize(signInPromptText));
        signInLink.setText(localize(signInLinkText));
        brandTitle.setText(localize(brandTitleText));
        brandText.setText(localize(brandTextText));
        if (termsLinksActive) {
            renderTermsLinks();
        }
    }

    private static String localize(Localizable text) {
        return LocalizationProvider.localize(text).orElseGet(() -> text.getMessage() == null ? "" : text.getMessage());
    }

    private void renderTermsLinks() {
        String html = "<span>"
                + escapeHtml(localize(termsPrefixText))
                + anchor(termsUrl, escapeHtml(localize(termsLinkText)))
                + escapeHtml(localize(termsSeparatorText))
                + anchor(privacyUrl, escapeHtml(localize(privacyLinkText)))
                + "</span>";
        termsSlot.removeAll();
        termsSlot.add(new Html(html));
        termsSlot.setVisible(true);
    }

    private static String anchor(String url, String text) {
        if (url == null || url.isBlank()) {
            return text;
        }
        return "<a class=\"iyen-signup__link\" href=\"" + escapeHtml(url)
                + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + text + "</a>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void clearCheckboxLabel() {
        if (agreeTerms.getComponent() instanceof Checkbox checkbox) {
            checkbox.setLabel("");
        }
    }

    private void restoreCheckboxLabel() {
        if (agreeTerms.getComponent() instanceof Checkbox checkbox) {
            checkbox.setLabel(localize(SignUpI18N.TERMS));
        }
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
     * Registers the account-creation logic that runs when the user submits the form.
     *
     * <p>The registrar decides the outcome by returning a {@link RegistrationResult}:
     * {@link RegistrationResult#success()} navigates to the configured success target,
     * {@link RegistrationResult#failure(String)} navigates to the configured failure target,
     * and {@link RegistrationResult#invalidInput(String)} keeps the user on this page and
     * shows the given error message inline.
     */
    public void setRegistrar(Registrar registrar) {
        this.registrar = registrar;
    }

    /** Sets the route navigated to after a successful sign-up. */
    public void setSuccessNavigationTarget(String route) {
        this.onSuccess = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to after a successful sign-up. */
    public void setSuccessNavigationTarget(Class<? extends Component> target) {
        this.onSuccess = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action (e.g. closing a dialog) after a successful sign-up. */
    public void setOnSuccess(SerializableRunnable action) {
        this.onSuccess = action;
    }

    /** Sets the route navigated to when registration fails (other than invalid input). */
    public void setFailureNavigationTarget(String route) {
        this.onFailure = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to when registration fails (other than invalid input). */
    public void setFailureNavigationTarget(Class<? extends Component> target) {
        this.onFailure = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action when registration fails (other than invalid input). */
    public void setOnFailure(SerializableRunnable action) {
        this.onFailure = action;
    }

    /** Shows or hides the social sign-up buttons and the "Or" divider. */
    public void setSocialLoginVisible(boolean visible) {
        socialRow.setVisible(visible);
        divider.setVisible(visible);
    }

    /** Shows or hides the "agree to Terms and Conditions" checkbox. */
    public void setTermsVisible(boolean visible) {
        agreeTerms.getComponent().setVisible(visible);
        termsSlot.setVisible(visible && (termsLinksActive || termsSlot.getElement().getChildCount() > 0));
    }

    /**
     * Renders the consent text next to the checkbox with clickable "Terms and Conditions" and
     * "Privacy Policy" links pointing to the given URLs.
     *
     * <p>This replaces the checkbox's plain-text label. The surrounding wording and the link
     * labels stay localizable (keys {@code signup.terms.prefix}, {@code signup.terms.link},
     * {@code signup.terms.separator}, {@code signup.privacy.link}). Pass {@code null} or blank for
     * a URL to render that phrase as plain text.
     *
     * @param termsUrl   the Terms and Conditions URL
     * @param privacyUrl the Privacy Policy URL
     */
    public void setTermsLinks(String termsUrl, String privacyUrl) {
        this.termsUrl = termsUrl;
        this.privacyUrl = privacyUrl;
        this.termsLinksActive = true;
        clearCheckboxLabel();
        renderTermsLinks();
    }

    /**
     * Replaces the consent text next to the checkbox with a custom component (e.g. a rich
     * {@link Html} block or {@code Anchor}s). Pass {@code null} to restore the default localized
     * checkbox label.
     */
    public void setTermsContent(Component content) {
        this.termsLinksActive = false;
        termsSlot.removeAll();
        if (content == null) {
            termsSlot.setVisible(false);
            restoreCheckboxLabel();
        } else {
            termsSlot.add(content);
            termsSlot.setVisible(true);
            clearCheckboxLabel();
        }
    }

    /** Shows or hides the "Already have an account? Sign In" row. */
    public void setSignInVisible(boolean visible) {
        signInRow.setVisible(visible);
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

    /** Clears all fields and any error message. */
    public void reset() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        agreeTerms.clear();
        setErrorMessage(null);
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    public ValidatableInput<String> getFirstNameField() {
        return firstNameField;
    }

    public ValidatableInput<String> getLastNameField() {
        return lastNameField;
    }

    public ValidatableInput<String> getEmailField() {
        return emailField;
    }

    public ValidatableInput<String> getPasswordField() {
        return passwordField;
    }

    public Input<Boolean> getAgreeTermsCheckbox() {
        return agreeTerms;
    }

    public Button getSignUpButton() {
        return signUpButton;
    }

    // ---------------------------------------------------------------------
    // Event registration
    // ---------------------------------------------------------------------

    /** Adds a listener invoked when the user submits the sign-up form. */
    public Registration addSignUpListener(ComponentEventListener<SignUpEvent> listener) {
        return addListener(SignUpEvent.class, listener);
    }

    /** Adds a listener invoked when a social sign-up button is pressed. */
    public Registration addSocialSignUpListener(ComponentEventListener<SocialSignUpEvent> listener) {
        return addListener(SocialSignUpEvent.class, listener);
    }

    /** Adds a listener invoked when the "Sign In" link is clicked. */
    public Registration addSignInListener(ComponentEventListener<SignInEvent> listener) {
        return addListener(SignInEvent.class, listener);
    }

    // ---------------------------------------------------------------------
    // Registration
    // ---------------------------------------------------------------------

    /**
     * Registration callback invoked when the user submits the form.
     *
     * <p>Implementations create the account for the given details and return a
     * {@link RegistrationResult} describing the outcome. The {@link SignUpPage} then navigates or
     * shows an inline error accordingly.
     */
    @FunctionalInterface
    public interface Registrar extends Serializable {

        /**
         * Creates an account for the submitted details.
         *
         * @param firstName the submitted first name
         * @param lastName  the submitted last name
         * @param email     the submitted email
         * @param password  the submitted password
         * @return the registration outcome (never {@code null})
         */
        RegistrationResult register(String firstName, String lastName, String email, String password);
    }

    /**
     * Outcome returned by a {@link Registrar}. Create one via the static factory methods:
     * {@link #success()} to navigate to the success target, {@link #invalidInput(String)} to
     * stay on the page and show an inline error, or {@link #failure(String)} to navigate to the
     * failure target.
     */
    public static final class RegistrationResult implements Serializable {

        private enum Outcome {
            SUCCESS, INVALID_INPUT, FAILURE
        }

        private final Outcome outcome;
        private final String errorMessage;

        private RegistrationResult(Outcome outcome, String errorMessage) {
            this.outcome = outcome;
            this.errorMessage = errorMessage;
        }

        /** Registration succeeded: navigate to the configured success target. */
        public static RegistrationResult success() {
            return new RegistrationResult(Outcome.SUCCESS, null);
        }

        /** Input was rejected (e.g. email already taken): stay on the page and show {@code errorMessage}. */
        public static RegistrationResult invalidInput(String errorMessage) {
            return new RegistrationResult(Outcome.INVALID_INPUT, errorMessage);
        }

        /**
         * Registration failed for another reason: navigate to the configured failure target, or,
         * if none is set, show {@code errorMessage} on the sign-up page.
         */
        public static RegistrationResult failure(String errorMessage) {
            return new RegistrationResult(Outcome.FAILURE, errorMessage);
        }

        /** Registration failed for another reason: navigate to the configured failure target. */
        public static RegistrationResult failure() {
            return new RegistrationResult(Outcome.FAILURE, null);
        }
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    /** Fired when the user submits the sign-up form. */
    public static class SignUpEvent extends ComponentEvent<SignUpPage> {

        private final String firstName;
        private final String lastName;
        private final String email;
        private final String password;
        private final boolean agreedToTerms;

        public SignUpEvent(SignUpPage source, boolean fromClient, String firstName, String lastName,
                String email, String password, boolean agreedToTerms) {
            super(source, fromClient);
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.agreedToTerms = agreedToTerms;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public boolean isAgreedToTerms() {
            return agreedToTerms;
        }
    }

    /** Fired when a social sign-up button is pressed. */
    public static class SocialSignUpEvent extends ComponentEvent<SignUpPage> {

        private final String provider;

        public SocialSignUpEvent(SignUpPage source, boolean fromClient, String provider) {
            super(source, fromClient);
            this.provider = provider;
        }

        /** The provider identifier, e.g. {@code "google"} or {@code "x"}. */
        public String getProvider() {
            return provider;
        }
    }

    /** Fired when the "Sign In" link is clicked. */
    public static class SignInEvent extends ComponentEvent<SignUpPage> {
        public SignInEvent(SignUpPage source, boolean fromClient) {
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
