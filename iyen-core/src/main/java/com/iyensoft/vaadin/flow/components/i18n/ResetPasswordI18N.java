package com.iyensoft.vaadin.flow.components.i18n;

import com.holonplatform.core.i18n.Localizable;

/**
 * Localizable message constants for {@link com.iyensoft.vaadin.flow.components.ResetPasswordPage}.
 *
 * <p>Each constant carries an English default message plus a message localization key. The actual
 * translation is resolved at runtime through the Holon platform localization: either a Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} or a Holon
 * {@link com.holonplatform.core.i18n.LocalizationContext}. If no localization is available the
 * default English message is used.
 *
 * <p>All keys follow the convention {@code resetpassword.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Default bundle: {@code com.iyensoft.vaadin.flow.components.i18n.ResetPasswordMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.iyensoft.vaadin.flow.components.i18n.ResetPasswordMessages"))
 *     .build();
 * }</pre>
 *
 * <p>Add {@code ResetPasswordMessages_fr.properties}, {@code ResetPasswordMessages_de.properties},
 * etc. to provide additional languages. The developer selects the language by choosing the current
 * {@link java.util.Locale} of the {@link com.holonplatform.core.i18n.LocalizationContext} or the
 * Vaadin session.
 *
 * <h3>Available message keys</h3>
 * <table border="1">
 *   <caption>Message keys and their English defaults</caption>
 *   <tr><th>Key</th><th>Default (English)</th></tr>
 *   <tr><td>{@code resetpassword.heading}</td><td>Forgot Your Password?</td></tr>
 *   <tr><td>{@code resetpassword.subtitle}</td><td>Enter the email address linked to your account, and we'll send you a link to reset your password.</td></tr>
 *   <tr><td>{@code resetpassword.email.label}</td><td>Email</td></tr>
 *   <tr><td>{@code resetpassword.email.placeholder}</td><td>info@gmail.com</td></tr>
 *   <tr><td>{@code resetpassword.email.required}</td><td>Email is required</td></tr>
 *   <tr><td>{@code resetpassword.email.invalid}</td><td>Enter a valid email address</td></tr>
 *   <tr><td>{@code resetpassword.submit}</td><td>Send Reset Link</td></tr>
 *   <tr><td>{@code resetpassword.confirmation}</td><td>If an account exists for that email, a reset link is on its way.</td></tr>
 *   <tr><td>{@code resetpassword.signin.prompt}</td><td>Wait, I remember my password...&nbsp;</td></tr>
 *   <tr><td>{@code resetpassword.signin.link}</td><td>Click here</td></tr>
 *   <tr><td>{@code resetpassword.brand.title}</td><td>TailAdmin</td></tr>
 *   <tr><td>{@code resetpassword.brand.text}</td><td>Free and Open-Source Tailwind CSS Admin Dashboard Template</td></tr>
 * </table>
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class ResetPasswordI18N {

    private ResetPasswordI18N() {}

    private static Localizable of(String message, String messageCode) {
        return Localizable.builder().message(message).messageCode(messageCode).build();
    }

    /** Main heading. */
    public static final Localizable HEADING =
            of("Forgot Your Password?", "resetpassword.heading");
    /** Subtitle shown under the heading. */
    public static final Localizable SUBTITLE =
            of("Enter the email address linked to your account, and we'll send you a link to reset your password.",
                    "resetpassword.subtitle");

    /** Email field label. */
    public static final Localizable EMAIL_LABEL =
            of("Email", "resetpassword.email.label");
    /** Email field placeholder. */
    public static final Localizable EMAIL_PLACEHOLDER =
            of("info@gmail.com", "resetpassword.email.placeholder");
    /** Validation error shown when the email is empty. */
    public static final Localizable EMAIL_REQUIRED =
            of("Email is required", "resetpassword.email.required");
    /** Validation error shown when the email format is invalid. */
    public static final Localizable EMAIL_INVALID =
            of("Enter a valid email address", "resetpassword.email.invalid");

    /** Submit button label. */
    public static final Localizable SEND_RESET_LINK_BUTTON =
            of("Send Reset Link", "resetpassword.submit");
    /** Confirmation message shown after the reset link is sent. */
    public static final Localizable CONFIRMATION =
            of("If an account exists for that email, a reset link is on its way.", "resetpassword.confirmation");

    /** "Wait, I remember my password..." prompt preceding the sign-in link. */
    public static final Localizable SIGN_IN_PROMPT =
            of("Wait, I remember my password... ", "resetpassword.signin.prompt");
    /** "Click here" link back to sign-in. */
    public static final Localizable SIGN_IN_LINK =
            of("Click here", "resetpassword.signin.link");

    /** Branding panel title. */
    public static final Localizable BRAND_TITLE =
            of("TailAdmin", "resetpassword.brand.title");
    /** Branding panel description. */
    public static final Localizable BRAND_TEXT =
            of("Free and Open-Source Tailwind CSS Admin Dashboard Template", "resetpassword.brand.text");
}
