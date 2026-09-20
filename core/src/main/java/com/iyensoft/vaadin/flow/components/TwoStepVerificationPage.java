package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.InputOTP;
import com.iyensoft.vaadin.flow.components.i18n.TwoStepVerificationI18N;
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
 * Generic, theme-agnostic two-step verification page inspired by the TailAdmin
 * "2 Step Verification" template.
 *
 * <p>The component is intentionally UI-only: it exposes typed events and does not verify any code
 * itself. Wire the {@link VerifyCodeEvent} (and the optional {@link ResendCodeEvent}) to your own
 * security layer.
 *
 * <p>For a self-contained flow, register a {@link CodeVerifier} via
 * {@link #setCodeVerifier(CodeVerifier)} and declare navigation targets with
 * {@link #setSuccessNavigationTarget(Class)} and {@link #setFailureNavigationTarget(Class)}. The
 * verifier returns a {@link VerificationResult}: success navigates to the success target (or shows
 * an inline confirmation when no target is set), failure navigates to the failure target, and
 * invalid input keeps the user on this page with an inline error message.
 *
 * <p>Layout is a split view: a form panel on the left (heading, subtitle, a six-digit
 * {@link InputOTP} code field, a verify button and a "resend" link) and an optional branding panel
 * on the right. All styling lives in {@code context://two-step-verification-page.css} so the
 * component works with any theme.
 *
 * <p>All texts are localized through the Holon platform (see {@link TwoStepVerificationI18N}). The
 * developer chooses the language by setting the current {@link java.util.Locale} on the
 * {@link com.holonplatform.core.i18n.LocalizationContext} (or Vaadin session) and registering the
 * {@code TwoStepVerificationMessages} bundle. Individual texts can also be overridden via the
 * {@code set*} methods, which accept either a plain {@link String} or a {@link Localizable}.
 */
@StyleSheet("context://two-step-verification-page.css")
public class TwoStepVerificationPage extends Div implements LocaleChangeObserver {

    private static final String CLASS_ROOT = "iyen-verify";

    /** Number of digits in the security code. */
    private static final int CODE_LENGTH = 6;

    private static final String DEFAULT_BRAND_LOGO_SVG = """
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 15a2 2 0 100-4 2 2 0 000 4z"/>
              <path d="M18 8h1a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2v-9a2 2 0 012-2h1V6a6 6 0 0112 0v2zm-2 0V6a4 4 0 00-8 0v2h8z"/>
            </svg>
            """;

    private final Div formSide = new Div();
    private final Div brandSide = new Div();

    private final H1 heading = new H1();
    private final Paragraph subtitle = new Paragraph();

    private final Span codeLabel = new Span();
    private final InputOTP codeField = InputOTP.builder()
            .group(CODE_LENGTH)
            .pattern("[0-9]")
            .build();
    private final Button verifyButton = ButtonBuilder.create()
            .text(TwoStepVerificationI18N.VERIFY_BUTTON)
            .primary()
            .styleName("iyen-verify__submit")
            .withClickListener(e -> fireVerify())
            .build();

    private final Div resendRow = new Div();
    private final Span resendPrompt = new Span();
    private final Span resendLink = new Span();

    private final Div errorMessage = new Div();
    private final Div confirmationMessage = new Div();

    private final H2 brandTitle = new H2();
    private final Paragraph brandText = new Paragraph();
    private final Div brandLogo = new Div();

    private Localizable headingText = TwoStepVerificationI18N.HEADING;
    private Localizable subtitleText = TwoStepVerificationI18N.SUBTITLE;
    private Localizable codeLabelText = TwoStepVerificationI18N.CODE_LABEL;
    private Localizable resendPromptText = TwoStepVerificationI18N.RESEND_PROMPT;
    private Localizable resendLinkText = TwoStepVerificationI18N.RESEND_LINK;
    private Localizable confirmationText = TwoStepVerificationI18N.CONFIRMATION;
    private Localizable codeRequiredText = TwoStepVerificationI18N.CODE_REQUIRED;
    private Localizable brandTitleText = TwoStepVerificationI18N.BRAND_TITLE;
    private Localizable brandTextText = TwoStepVerificationI18N.BRAND_TEXT;

    private CodeVerifier codeVerifier;
    private SerializableRunnable onSuccess;
    private SerializableRunnable onFailure;

    public TwoStepVerificationPage() {
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
        formSide.addClassName("iyen-verify__form-side");

        Div card = new Div();
        card.addClassName("iyen-verify__card");

        heading.addClassName("iyen-verify__heading");
        subtitle.addClassName("iyen-verify__subtitle");

        verifyButton.addClickShortcut(Key.ENTER);

        errorMessage.addClassName("iyen-verify__error");
        errorMessage.setVisible(false);

        confirmationMessage.addClassName("iyen-verify__confirmation");
        confirmationMessage.setVisible(false);

        resendRow.addClassName("iyen-verify__resend");
        resendLink.addClassName("iyen-verify__link");
        resendLink.addClickListener(e -> fireEvent(new ResendCodeEvent(this, true)));
        resendRow.add(resendPrompt, resendLink);

        card.add(heading, subtitle, errorMessage, confirmationMessage,
                buildCode(), verifyButton, resendRow);

        formSide.add(card);
    }

    private Component buildCode() {
        codeLabel.addClassName("iyen-verify__code-label");
        codeField.addClassName("iyen-verify__otp");

        Div code = new Div(codeLabel, codeField);
        code.addClassName("iyen-verify__code");
        return code;
    }

    private void buildBrandSide() {
        brandSide.addClassName("iyen-verify__brand-side");

        brandLogo.addClassName("iyen-verify__brand-logo");
        brandLogo.add(new Html(DEFAULT_BRAND_LOGO_SVG));

        brandTitle.addClassName("iyen-verify__brand-title");
        brandText.addClassName("iyen-verify__brand-text");

        Div content = new Div(brandLogo, brandTitle, brandText);
        content.addClassName("iyen-verify__brand-content");
        brandSide.add(content);
    }

    private void fireVerify() {
        String code = codeField.getValue();

        // The code must be fully entered before we notify anyone.
        if (code == null || code.length() < CODE_LENGTH) {
            setErrorMessage(localize(codeRequiredText));
            return;
        }
        setErrorMessage(null);

        // Always notify listeners so the event-based API keeps working.
        fireEvent(new VerifyCodeEvent(this, true, code));

        // If a verifier is configured, let it decide the outcome and drive navigation.
        if (codeVerifier != null) {
            applyResult(codeVerifier.verify(code));
        }
    }

    private void applyResult(VerificationResult result) {
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
     * Re-localizes the plain-text elements (headings, labels, links, branding) using the current
     * locale.
     */
    private void localizeStaticTexts() {
        heading.setText(localize(headingText));
        subtitle.setText(localize(subtitleText));
        codeLabel.setText(localize(codeLabelText));
        resendPrompt.setText(localize(resendPromptText));
        resendLink.setText(localize(resendLinkText));
        verifyButton.setText(localize(TwoStepVerificationI18N.VERIFY_BUTTON));
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

    /** Sets the label shown above the code field, overriding the localized default. */
    public void setCodeLabel(String text) {
        this.codeLabelText = Localizable.of(text);
        codeLabel.setText(text);
    }

    /** Sets the code-field label from a {@link Localizable}, keeping it locale-aware. */
    public void setCodeLabel(Localizable text) {
        this.codeLabelText = text;
        codeLabel.setText(localize(text));
    }

    /**
     * Registers the verification logic that runs when the user submits the code.
     *
     * <p>The verifier decides the outcome by returning a {@link VerificationResult}:
     * {@link VerificationResult#success()} navigates to the configured success target (or shows an
     * inline confirmation when none is set), {@link VerificationResult#failure(String)} navigates
     * to the configured failure target, and {@link VerificationResult#invalidInput(String)} keeps
     * the user on this page and shows the given error message inline.
     */
    public void setCodeVerifier(CodeVerifier codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    /** Sets the route navigated to after the code is verified. */
    public void setSuccessNavigationTarget(String route) {
        this.onSuccess = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to after the code is verified. */
    public void setSuccessNavigationTarget(Class<? extends Component> target) {
        this.onSuccess = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action (e.g. closing a dialog) after the code is verified. */
    public void setOnSuccess(SerializableRunnable action) {
        this.onSuccess = action;
    }

    /** Sets the route navigated to when verification fails (other than an invalid code). */
    public void setFailureNavigationTarget(String route) {
        this.onFailure = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to when verification fails (other than an invalid code). */
    public void setFailureNavigationTarget(Class<? extends Component> target) {
        this.onFailure = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action when verification fails (other than an invalid code). */
    public void setOnFailure(SerializableRunnable action) {
        this.onFailure = action;
    }

    /** Shows or hides the "Didn't get the code? Resend" row. */
    public void setResendVisible(boolean visible) {
        resendRow.setVisible(visible);
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

    /** Clears the code field and any error / confirmation message. */
    public void reset() {
        codeField.clear();
        setErrorMessage(null);
        confirmationMessage.setText("");
        confirmationMessage.setVisible(false);
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    /** Returns the number of digits in the security code. */
    public int getCodeLength() {
        return CODE_LENGTH;
    }

    public InputOTP getCodeField() {
        return codeField;
    }

    public Button getVerifyButton() {
        return verifyButton;
    }

    // ---------------------------------------------------------------------
    // Event registration
    // ---------------------------------------------------------------------

    /** Adds a listener invoked when the user submits the verification code. */
    public Registration addVerifyCodeListener(ComponentEventListener<VerifyCodeEvent> listener) {
        return addListener(VerifyCodeEvent.class, listener);
    }

    /** Adds a listener invoked when the "Resend" link is clicked. */
    public Registration addResendCodeListener(ComponentEventListener<ResendCodeEvent> listener) {
        return addListener(ResendCodeEvent.class, listener);
    }

    // ---------------------------------------------------------------------
    // Verification
    // ---------------------------------------------------------------------

    /**
     * Verification callback invoked when the user submits the code.
     *
     * <p>Implementations verify the given code and return a {@link VerificationResult} describing
     * the outcome. The {@link TwoStepVerificationPage} then navigates, confirms, or shows an inline
     * error accordingly.
     */
    @FunctionalInterface
    public interface CodeVerifier extends Serializable {

        /**
         * Verifies the submitted security code.
         *
         * @param code the submitted code
         * @return the verification outcome (never {@code null})
         */
        VerificationResult verify(String code);
    }

    /**
     * Outcome returned by a {@link CodeVerifier}. Create one via the static factory methods:
     * {@link #success()} to navigate to the success target (or show the default confirmation),
     * {@link #invalidInput(String)} to stay on the page and show an inline error, or
     * {@link #failure(String)} to navigate to the failure target.
     */
    public static final class VerificationResult implements Serializable {

        private enum Outcome {
            SUCCESS, INVALID_INPUT, FAILURE
        }

        private final Outcome outcome;
        private final String message;
        private final String confirmationMessage;

        private VerificationResult(Outcome outcome, String message, String confirmationMessage) {
            this.outcome = outcome;
            this.message = message;
            this.confirmationMessage = confirmationMessage;
        }

        /** Code verified: navigate to the success target, or show the default confirmation. */
        public static VerificationResult success() {
            return new VerificationResult(Outcome.SUCCESS, null, null);
        }

        /** Code verified: navigate to the success target, or show {@code confirmationMessage}. */
        public static VerificationResult success(String confirmationMessage) {
            return new VerificationResult(Outcome.SUCCESS, null, confirmationMessage);
        }

        /** Code was rejected (e.g. wrong or expired): stay on the page and show {@code errorMessage}. */
        public static VerificationResult invalidInput(String errorMessage) {
            return new VerificationResult(Outcome.INVALID_INPUT, errorMessage, null);
        }

        /**
         * Verification failed for another reason: navigate to the configured failure target, or, if
         * none is set, show {@code errorMessage} on the page.
         */
        public static VerificationResult failure(String errorMessage) {
            return new VerificationResult(Outcome.FAILURE, errorMessage, null);
        }

        /** Verification failed for another reason: navigate to the failure target. */
        public static VerificationResult failure() {
            return new VerificationResult(Outcome.FAILURE, null, null);
        }
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    /** Fired when the user submits the verification code. */
    public static class VerifyCodeEvent extends ComponentEvent<TwoStepVerificationPage> {

        private final String code;

        public VerifyCodeEvent(TwoStepVerificationPage source, boolean fromClient, String code) {
            super(source, fromClient);
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /** Fired when the "Resend" link is clicked. */
    public static class ResendCodeEvent extends ComponentEvent<TwoStepVerificationPage> {
        public ResendCodeEvent(TwoStepVerificationPage source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
