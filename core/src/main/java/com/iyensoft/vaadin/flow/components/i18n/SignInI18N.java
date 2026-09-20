package com.iyensoft.vaadin.flow.components.i18n;

import com.holonplatform.core.i18n.Localizable;

/**
 * Localizable message constants for {@link com.iyensoft.vaadin.flow.components.SignInPage}.
 *
 * <p>Each constant carries an English default message plus a message localization key. The actual
 * translation is resolved at runtime through the Holon platform localization: either a Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} or a Holon
 * {@link com.holonplatform.core.i18n.LocalizationContext}. If no localization is available the
 * default English message is used.
 *
 * <p>All keys follow the convention {@code signin.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Default bundle: {@code com.iyensoft.vaadin.flow.components.i18n.SignInMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.iyensoft.vaadin.flow.components.i18n.SignInMessages"))
 *     .build();
 * }</pre>
 *
 * <p>Add {@code SignInMessages_fr.properties}, {@code SignInMessages_de.properties}, etc. to
 * provide additional languages. The developer selects the language by choosing the current
 * {@link java.util.Locale} of the {@link com.holonplatform.core.i18n.LocalizationContext} or the
 * Vaadin session.
 *
 * <h3>Available message keys</h3>
 * <table border="1">
 *   <caption>Message keys and their English defaults</caption>
 *   <tr><th>Key</th><th>Default (English)</th></tr>
 *   <tr><td>{@code signin.heading}</td><td>Sign In</td></tr>
 *   <tr><td>{@code signin.subtitle}</td><td>Enter your email and password to sign in!</td></tr>
 *   <tr><td>{@code signin.email.label}</td><td>Email</td></tr>
 *   <tr><td>{@code signin.email.placeholder}</td><td>info@gmail.com</td></tr>
 *   <tr><td>{@code signin.email.required}</td><td>Email is required</td></tr>
 *   <tr><td>{@code signin.email.invalid}</td><td>Enter a valid email address</td></tr>
 *   <tr><td>{@code signin.password.label}</td><td>Password</td></tr>
 *   <tr><td>{@code signin.password.placeholder}</td><td>Enter your password</td></tr>
 *   <tr><td>{@code signin.password.required}</td><td>Password is required</td></tr>
 *   <tr><td>{@code signin.keep}</td><td>Keep me logged in</td></tr>
 *   <tr><td>{@code signin.forgot}</td><td>Forgot password?</td></tr>
 *   <tr><td>{@code signin.submit}</td><td>Sign In</td></tr>
 *   <tr><td>{@code signin.social.google}</td><td>Sign in with Google</td></tr>
 *   <tr><td>{@code signin.social.x}</td><td>Sign in with X</td></tr>
 *   <tr><td>{@code signin.divider}</td><td>Or</td></tr>
 *   <tr><td>{@code signin.signup.prompt}</td><td>Don't have an account?&nbsp;</td></tr>
 *   <tr><td>{@code signin.signup.link}</td><td>Sign Up</td></tr>
 *   <tr><td>{@code signin.brand.title}</td><td>TailAdmin</td></tr>
 *   <tr><td>{@code signin.brand.text}</td><td>Free and Open-Source Tailwind CSS Admin Dashboard Template</td></tr>
 * </table>
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class SignInI18N {

    private SignInI18N() {}

    private static Localizable of(String message, String messageCode) {
        return Localizable.builder().message(message).messageCode(messageCode).build();
    }

    /** Main heading. */
    public static final Localizable HEADING =
            of("Sign In", "signin.heading");
    /** Subtitle shown under the heading. */
    public static final Localizable SUBTITLE =
            of("Enter your email and password to sign in!", "signin.subtitle");

    /** Email field label. */
    public static final Localizable EMAIL_LABEL =
            of("Email", "signin.email.label");
    /** Email field placeholder. */
    public static final Localizable EMAIL_PLACEHOLDER =
            of("info@gmail.com", "signin.email.placeholder");
    /** Validation error shown when the email is empty. */
    public static final Localizable EMAIL_REQUIRED =
            of("Email is required", "signin.email.required");
    /** Validation error shown when the email format is invalid. */
    public static final Localizable EMAIL_INVALID =
            of("Enter a valid email address", "signin.email.invalid");

    /** Password field label. */
    public static final Localizable PASSWORD_LABEL =
            of("Password", "signin.password.label");
    /** Password field placeholder. */
    public static final Localizable PASSWORD_PLACEHOLDER =
            of("Enter your password", "signin.password.placeholder");
    /** Validation error shown when the password is empty. */
    public static final Localizable PASSWORD_REQUIRED =
            of("Password is required", "signin.password.required");

    /** "Keep me logged in" checkbox label. */
    public static final Localizable KEEP_LOGGED_IN =
            of("Keep me logged in", "signin.keep");
    /** "Forgot password?" link. */
    public static final Localizable FORGOT_PASSWORD =
            of("Forgot password?", "signin.forgot");
    /** Submit button label. */
    public static final Localizable SIGN_IN_BUTTON =
            of("Sign In", "signin.submit");

    /** "Sign in with Google" button label. */
    public static final Localizable SOCIAL_GOOGLE =
            of("Sign in with Google", "signin.social.google");
    /** "Sign in with X" button label. */
    public static final Localizable SOCIAL_X =
            of("Sign in with X", "signin.social.x");
    /** Divider label between social and form sign-in ("Or"). */
    public static final Localizable DIVIDER =
            of("Or", "signin.divider");

    /** "Don't have an account?" prompt preceding the sign-up link. */
    public static final Localizable SIGN_UP_PROMPT =
            of("Don't have an account? ", "signin.signup.prompt");
    /** "Sign Up" link. */
    public static final Localizable SIGN_UP_LINK =
            of("Sign Up", "signin.signup.link");

    /** Branding panel title. */
    public static final Localizable BRAND_TITLE =
            of("TailAdmin", "signin.brand.title");
    /** Branding panel description. */
    public static final Localizable BRAND_TEXT =
            of("Free and Open-Source Tailwind CSS Admin Dashboard Template", "signin.brand.text");
}
