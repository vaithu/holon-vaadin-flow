package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.i18n.ResetPasswordI18N;
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
 * Generic, theme-agnostic reset-password ("forgot password") page inspired by the TailAdmin
 * "Reset Password" template.
 *
 * <p>The component is intentionally UI-only: it exposes typed events and does not send any email
 * itself. Wire the {@link ResetPasswordEvent} (and the optional navigation event) to your own
 * security layer.
 *
 * <p>For a self-contained flow, register a {@link PasswordResetter} via
 * {@link #setPasswordResetter(PasswordResetter)} and declare navigation targets with
 * {@link #setSuccessNavigationTarget(Class)} and {@link #setFailureNavigationTarget(Class)}. The
 * resetter returns a {@link ResetResult}: success navigates to the success target (or shows an
 * inline confirmation when no target is set), failure navigates to the failure target, and invalid
 * input keeps the user on this page with an inline error message.
 *
 * <p>Layout is a split view: a form panel on the left and an optional branding panel on the right.
 * All styling lives in {@code context://reset-password-page.css} so the component works with any
 * theme.
 *
 * <p>All texts are localized through the Holon platform (see {@link ResetPasswordI18N}). The
 * developer chooses the language by setting the current {@link java.util.Locale} on the
 * {@link com.holonplatform.core.i18n.LocalizationContext} (or Vaadin session) and registering the
 * {@code ResetPasswordMessages} bundle. Individual texts can also be overridden via the
 * {@code set*} methods, which accept either a plain {@link String} or a {@link Localizable}.
 */
@StyleSheet("context://reset-password-page.css")
public class ResetPasswordPage extends Div implements LocaleChangeObserver {

    private static final String CLASS_ROOT = "iyen-reset";

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

    private final ValidatableInput<String> emailField = Input.string()
            .label(ResetPasswordI18N.EMAIL_LABEL)
            .placeholder(ResetPasswordI18N.EMAIL_PLACEHOLDER)
            .clearButtonVisible(true)
            .fullWidth()
            .styleName("iyen-reset__email")
            .validatable()
            .required(ResetPasswordI18N.EMAIL_REQUIRED)
            .withValidator(Validator.email(ResetPasswordI18N.EMAIL_INVALID))
            .build();
    private final Button submitButton = ButtonBuilder.create()
            .text(ResetPasswordI18N.SEND_RESET_LINK_BUTTON)
            .primary()
            .styleName("iyen-reset__submit")
            .withClickListener(e -> fireReset())
            .build();

    private final Div signInRow = new Div();
    private final Span signInPrompt = new Span();
    private final Span signInLink = new Span();

    private final Div errorMessage = new Div();
    private final Div confirmationMessage = new Div();

    private final H2 brandTitle = new H2();
    private final Paragraph brandText = new Paragraph();
    private final Div brandLogo = new Div();

    private Localizable headingText = ResetPasswordI18N.HEADING;
    private Localizable subtitleText = ResetPasswordI18N.SUBTITLE;
    private Localizable signInPromptText = ResetPasswordI18N.SIGN_IN_PROMPT;
    private Localizable signInLinkText = ResetPasswordI18N.SIGN_IN_LINK;
    private Localizable confirmationText = ResetPasswordI18N.CONFIRMATION;
    private Localizable brandTitleText = ResetPasswordI18N.BRAND_TITLE;
    private Localizable brandTextText = ResetPasswordI18N.BRAND_TEXT;

    private PasswordResetter passwordResetter;
    private SerializableRunnable onSuccess;
    private SerializableRunnable onFailure;

    public ResetPasswordPage() {
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
        formSide.addClassName("iyen-reset__form-side");

        Div card = new Div();
        card.addClassName("iyen-reset__card");

        heading.addClassName("iyen-reset__heading");
        subtitle.addClassName("iyen-reset__subtitle");

        submitButton.addClickShortcut(Key.ENTER);

        errorMessage.addClassName("iyen-reset__error");
        errorMessage.setVisible(false);

        confirmationMessage.addClassName("iyen-reset__confirmation");
        confirmationMessage.setVisible(false);

        signInRow.addClassName("iyen-reset__signin");
        signInLink.addClassName("iyen-reset__link");
        signInLink.addClickListener(e -> fireEvent(new BackToSignInEvent(this, true)));
        signInRow.add(signInPrompt, signInLink);

        card.add(heading, subtitle, errorMessage, confirmationMessage,
                buildFields(), submitButton, signInRow);

        formSide.add(card);
    }

    private Component buildFields() {
        Div fields = new Div(emailField.getComponent());
        fields.addClassName("iyen-reset__fields");
        return fields;
    }

    private void buildBrandSide() {
        brandSide.addClassName("iyen-reset__brand-side");

        brandLogo.addClassName("iyen-reset__brand-logo");
        brandLogo.add(new Html(DEFAULT_BRAND_LOGO_SVG));

        brandTitle.addClassName("iyen-reset__brand-title");
        brandText.addClassName("iyen-reset__brand-text");

        Div content = new Div(brandLogo, brandTitle, brandText);
        content.addClassName("iyen-reset__brand-content");
        brandSide.add(content);
    }

    private void fireReset() {
        // Validate the email; validation errors are shown inline on the input.
        if (!emailField.isValid()) {
            return;
        }
        setErrorMessage(null);
        String email = emailField.getValue();

        // Always notify listeners so the event-based API keeps working.
        fireEvent(new ResetPasswordEvent(this, true, email));

        // If a resetter is configured, let it decide the outcome and drive navigation.
        if (passwordResetter != null) {
            applyResult(passwordResetter.sendResetLink(email));
        }
    }

    private void applyResult(ResetResult result) {
        if (result == null) {
            return;
        }
        switch (result.outcome) {
            case SUCCESS -> {
                setErrorMessage(null);
                if (onSuccess != null) {
                    onSuccess.run();
                } else {
                    showConfirmation(result.confirmationMessage);
                }
            }
            case INVALID_INPUT -> setErrorMessage(result.message);
            case FAILURE -> {
                if (onFailure != null) {
                    onFailure.run();
                } else {
                    setErrorMessage(result.message);
                }
            }
        }
    }

    private void showConfirmation(String message) {
        String text = (message == null || message.isBlank()) ? localize(confirmationText) : message;
        confirmationMessage.setText(text);
        confirmationMessage.setVisible(true);
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
        signInPrompt.setText(localize(signInPromptText));
        signInLink.setText(localize(signInLinkText));
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
     * Registers the reset-link logic that runs when the user submits the form.
     *
     * <p>The resetter decides the outcome by returning a {@link ResetResult}:
     * {@link ResetResult#success()} navigates to the configured success target (or shows an inline
     * confirmation when none is set), {@link ResetResult#failure(String)} navigates to the
     * configured failure target, and {@link ResetResult#invalidInput(String)} keeps the user on
     * this page and shows the given error message inline.
     */
    public void setPasswordResetter(PasswordResetter passwordResetter) {
        this.passwordResetter = passwordResetter;
    }

    /** Sets the route navigated to after the reset link is sent. */
    public void setSuccessNavigationTarget(String route) {
        this.onSuccess = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to after the reset link is sent. */
    public void setSuccessNavigationTarget(Class<? extends Component> target) {
        this.onSuccess = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action (e.g. closing a dialog) after the reset link is sent. */
    public void setOnSuccess(SerializableRunnable action) {
        this.onSuccess = action;
    }

    /** Sets the route navigated to when sending the reset link fails (other than invalid input). */
    public void setFailureNavigationTarget(String route) {
        this.onFailure = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to when sending the reset link fails (other than invalid input). */
    public void setFailureNavigationTarget(Class<? extends Component> target) {
        this.onFailure = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action when sending the reset link fails (other than invalid input). */
    public void setOnFailure(SerializableRunnable action) {
        this.onFailure = action;
    }

    /** Shows or hides the "Wait, I remember my password... Click here" row. */
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

    /** Displays an error message above the form. Pass {@code null} or blank to clear it. */
    public void setErrorMessage(String message) {
        if (message == null || message.isBlank()) {
            errorMessage.setText("");
            errorMessage.setVisible(false);
        } else {
            confirmationMessage.setVisible(false);
            errorMessage.setText(message);
            errorMessage.setVisible(true);
        }
    }

    /** Clears the email field and any error / confirmation message. */
    public void reset() {
        emailField.clear();
        setErrorMessage(null);
        confirmationMessage.setText("");
        confirmationMessage.setVisible(false);
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    public ValidatableInput<String> getEmailField() {
        return emailField;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    // ---------------------------------------------------------------------
    // Event registration
    // ---------------------------------------------------------------------

    /** Adds a listener invoked when the user submits the reset-password form. */
    public Registration addResetPasswordListener(ComponentEventListener<ResetPasswordEvent> listener) {
        return addListener(ResetPasswordEvent.class, listener);
    }

    /** Adds a listener invoked when the "Click here" (back to sign-in) link is clicked. */
    public Registration addBackToSignInListener(ComponentEventListener<BackToSignInEvent> listener) {
        return addListener(BackToSignInEvent.class, listener);
    }

    // ---------------------------------------------------------------------
    // Password reset
    // ---------------------------------------------------------------------

    /**
     * Reset callback invoked when the user submits the form.
     *
     * <p>Implementations send the reset link for the given email and return a {@link ResetResult}
     * describing the outcome. The {@link ResetPasswordPage} then navigates, confirms, or shows an
     * inline error accordingly.
     */
    @FunctionalInterface
    public interface PasswordResetter extends Serializable {

        /**
         * Sends a password-reset link to the submitted email.
         *
         * @param email the submitted email
         * @return the reset outcome (never {@code null})
         */
        ResetResult sendResetLink(String email);
    }

    /**
     * Outcome returned by a {@link PasswordResetter}. Create one via the static factory methods:
     * {@link #success()} to navigate to the success target (or show the default confirmation),
     * {@link #invalidInput(String)} to stay on the page and show an inline error, or
     * {@link #failure(String)} to navigate to the failure target.
     */
    public static final class ResetResult implements Serializable {

        private enum Outcome {
            SUCCESS, INVALID_INPUT, FAILURE
        }

        private final Outcome outcome;
        private final String message;
        private final String confirmationMessage;

        private ResetResult(Outcome outcome, String message, String confirmationMessage) {
            this.outcome = outcome;
            this.message = message;
            this.confirmationMessage = confirmationMessage;
        }

        /** Reset link sent: navigate to the success target, or show the default confirmation. */
        public static ResetResult success() {
            return new ResetResult(Outcome.SUCCESS, null, null);
        }

        /** Reset link sent: navigate to the success target, or show {@code confirmationMessage}. */
        public static ResetResult success(String confirmationMessage) {
            return new ResetResult(Outcome.SUCCESS, null, confirmationMessage);
        }

        /** Input was rejected (e.g. unknown email): stay on the page and show {@code errorMessage}. */
        public static ResetResult invalidInput(String errorMessage) {
            return new ResetResult(Outcome.INVALID_INPUT, errorMessage, null);
        }

        /**
         * Sending the reset link failed for another reason: navigate to the configured failure
         * target, or, if none is set, show {@code errorMessage} on the page.
         */
        public static ResetResult failure(String errorMessage) {
            return new ResetResult(Outcome.FAILURE, errorMessage, null);
        }

        /** Sending the reset link failed for another reason: navigate to the failure target. */
        public static ResetResult failure() {
            return new ResetResult(Outcome.FAILURE, null, null);
        }
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    /** Fired when the user submits the reset-password form. */
    public static class ResetPasswordEvent extends ComponentEvent<ResetPasswordPage> {

        private final String email;

        public ResetPasswordEvent(ResetPasswordPage source, boolean fromClient, String email) {
            super(source, fromClient);
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    /** Fired when the "Click here" (back to sign-in) link is clicked. */
    public static class BackToSignInEvent extends ComponentEvent<ResetPasswordPage> {
        public BackToSignInEvent(ResetPasswordPage source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
