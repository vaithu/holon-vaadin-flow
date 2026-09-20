package com.iyensoft.vaadin.flow.components.i18n;

import com.holonplatform.core.i18n.Localizable;

/**
 * Localizable message constants for
 * {@link com.iyensoft.vaadin.flow.components.TwoStepVerificationPage}.
 *
 * <p>Each constant carries an English default message plus a message localization key. The actual
 * translation is resolved at runtime through the Holon platform localization: either a Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} or a Holon
 * {@link com.holonplatform.core.i18n.LocalizationContext}. If no localization is available the
 * default English message is used.
 *
 * <p>All keys follow the convention {@code twostepverification.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Default bundle: {@code com.iyensoft.vaadin.flow.components.i18n.TwoStepVerificationMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.iyensoft.vaadin.flow.components.i18n.TwoStepVerificationMessages"))
 *     .build();
 * }</pre>
 *
 * <p>Add {@code TwoStepVerificationMessages_fr.properties},
 * {@code TwoStepVerificationMessages_de.properties}, etc. to provide additional languages. The
 * developer selects the language by choosing the current {@link java.util.Locale} of the
 * {@link com.holonplatform.core.i18n.LocalizationContext} or the Vaadin session.
 *
 * <h3>Available message keys</h3>
 * <table border="1">
 *   <caption>Message keys and their English defaults</caption>
 *   <tr><th>Key</th><th>Default (English)</th></tr>
 *   <tr><td>{@code twostepverification.heading}</td><td>Two Step Verification</td></tr>
 *   <tr><td>{@code twostepverification.subtitle}</td><td>A verification code has been sent to your mobile. Please enter it in the field below.</td></tr>
 *   <tr><td>{@code twostepverification.code.label}</td><td>Type your 6 digits security code</td></tr>
 *   <tr><td>{@code twostepverification.code.required}</td><td>Enter the complete 6-digit code</td></tr>
 *   <tr><td>{@code twostepverification.verify}</td><td>Verify My Account</td></tr>
 *   <tr><td>{@code twostepverification.confirmation}</td><td>Your account has been verified.</td></tr>
 *   <tr><td>{@code twostepverification.resend.prompt}</td><td>Didn't get the code?&nbsp;</td></tr>
 *   <tr><td>{@code twostepverification.resend.link}</td><td>Resend</td></tr>
 *   <tr><td>{@code twostepverification.brand.title}</td><td>TailAdmin</td></tr>
 *   <tr><td>{@code twostepverification.brand.text}</td><td>Free and Open-Source Tailwind CSS Admin Dashboard Template</td></tr>
 * </table>
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class TwoStepVerificationI18N {

    private TwoStepVerificationI18N() {}

    private static Localizable of(String message, String messageCode) {
        return Localizable.builder().message(message).messageCode(messageCode).build();
    }

    /** Main heading. */
    public static final Localizable HEADING =
            of("Two Step Verification", "twostepverification.heading");
    /** Subtitle shown under the heading. */
    public static final Localizable SUBTITLE =
            of("A verification code has been sent to your mobile. Please enter it in the field below.",
                    "twostepverification.subtitle");

    /** Label shown above the code field. */
    public static final Localizable CODE_LABEL =
            of("Type your 6 digits security code", "twostepverification.code.label");
    /** Validation error shown when the code is incomplete. */
    public static final Localizable CODE_REQUIRED =
            of("Enter the complete 6-digit code", "twostepverification.code.required");

    /** Verify button label. */
    public static final Localizable VERIFY_BUTTON =
            of("Verify My Account", "twostepverification.verify");
    /** Confirmation message shown after the code is verified. */
    public static final Localizable CONFIRMATION =
            of("Your account has been verified.", "twostepverification.confirmation");

    /** "Didn't get the code?" prompt preceding the resend link. */
    public static final Localizable RESEND_PROMPT =
            of("Didn't get the code? ", "twostepverification.resend.prompt");
    /** "Resend" link. */
    public static final Localizable RESEND_LINK =
            of("Resend", "twostepverification.resend.link");

    /** Branding panel title. */
    public static final Localizable BRAND_TITLE =
            of("TailAdmin", "twostepverification.brand.title");
    /** Branding panel description. */
    public static final Localizable BRAND_TEXT =
            of("Free and Open-Source Tailwind CSS Admin Dashboard Template", "twostepverification.brand.text");
}
