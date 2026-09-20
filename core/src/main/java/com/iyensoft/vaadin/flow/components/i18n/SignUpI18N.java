package com.iyensoft.vaadin.flow.components.i18n;

import com.holonplatform.core.i18n.Localizable;

/**
 * Localizable message constants for {@link com.iyensoft.vaadin.flow.components.SignUpPage}.
 *
 * <p>Each constant carries an English default message plus a message localization key. The actual
 * translation is resolved at runtime through the Holon platform localization: either a Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} or a Holon
 * {@link com.holonplatform.core.i18n.LocalizationContext}. If no localization is available the
 * default English message is used.
 *
 * <p>All keys follow the convention {@code signup.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Default bundle: {@code com.iyensoft.vaadin.flow.components.i18n.SignUpMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.iyensoft.vaadin.flow.components.i18n.SignUpMessages"))
 *     .build();
 * }</pre>
 *
 * <p>Add {@code SignUpMessages_fr.properties}, {@code SignUpMessages_de.properties}, etc. to
 * provide additional languages. The developer selects the language by choosing the current
 * {@link java.util.Locale} of the {@link com.holonplatform.core.i18n.LocalizationContext} or the
 * Vaadin session.
 *
 * <h3>Available message keys</h3>
 * <table border="1">
 *   <caption>Message keys and their English defaults</caption>
 *   <tr><th>Key</th><th>Default (English)</th></tr>
 *   <tr><td>{@code signup.heading}</td><td>Sign Up</td></tr>
 *   <tr><td>{@code signup.subtitle}</td><td>Enter your email and password to sign up!</td></tr>
 *   <tr><td>{@code signup.firstname.label}</td><td>First Name</td></tr>
 *   <tr><td>{@code signup.firstname.placeholder}</td><td>Enter your first name</td></tr>
 *   <tr><td>{@code signup.firstname.required}</td><td>First name is required</td></tr>
 *   <tr><td>{@code signup.lastname.label}</td><td>Last Name</td></tr>
 *   <tr><td>{@code signup.lastname.placeholder}</td><td>Enter your last name</td></tr>
 *   <tr><td>{@code signup.lastname.required}</td><td>Last name is required</td></tr>
 *   <tr><td>{@code signup.email.label}</td><td>Email</td></tr>
 *   <tr><td>{@code signup.email.placeholder}</td><td>info@gmail.com</td></tr>
 *   <tr><td>{@code signup.email.required}</td><td>Email is required</td></tr>
 *   <tr><td>{@code signup.email.invalid}</td><td>Enter a valid email address</td></tr>
 *   <tr><td>{@code signup.password.label}</td><td>Password</td></tr>
 *   <tr><td>{@code signup.password.placeholder}</td><td>Enter your password</td></tr>
 *   <tr><td>{@code signup.password.required}</td><td>Password is required</td></tr>
 *   <tr><td>{@code signup.terms}</td><td>By creating an account means you agree to the Terms and Conditions, and our Privacy Policy</td></tr>
 *   <tr><td>{@code signup.terms.prefix}</td><td>By creating an account, you agree to our </td></tr>
 *   <tr><td>{@code signup.terms.link}</td><td>Terms and Conditions</td></tr>
 *   <tr><td>{@code signup.terms.separator}</td><td> and our </td></tr>
 *   <tr><td>{@code signup.privacy.link}</td><td>Privacy Policy</td></tr>
 *   <tr><td>{@code signup.terms.required}</td><td>You must agree to the Terms and Conditions</td></tr>
 *   <tr><td>{@code signup.submit}</td><td>Sign Up</td></tr>
 *   <tr><td>{@code signup.social.google}</td><td>Sign up with Google</td></tr>
 *   <tr><td>{@code signup.social.x}</td><td>Sign up with X</td></tr>
 *   <tr><td>{@code signup.divider}</td><td>Or</td></tr>
 *   <tr><td>{@code signup.signin.prompt}</td><td>Already have an account?&nbsp;</td></tr>
 *   <tr><td>{@code signup.signin.link}</td><td>Sign In</td></tr>
 *   <tr><td>{@code signup.brand.title}</td><td>TailAdmin</td></tr>
 *   <tr><td>{@code signup.brand.text}</td><td>Free and Open-Source Tailwind CSS Admin Dashboard Template</td></tr>
 * </table>
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class SignUpI18N {

    private SignUpI18N() {}

    private static Localizable of(String message, String messageCode) {
        return Localizable.builder().message(message).messageCode(messageCode).build();
    }

    /** Main heading. */
    public static final Localizable HEADING =
            of("Sign Up", "signup.heading");
    /** Subtitle shown under the heading. */
    public static final Localizable SUBTITLE =
            of("Enter your email and password to sign up!", "signup.subtitle");

    /** First name field label. */
    public static final Localizable FIRST_NAME_LABEL =
            of("First Name", "signup.firstname.label");
    /** First name field placeholder. */
    public static final Localizable FIRST_NAME_PLACEHOLDER =
            of("Enter your first name", "signup.firstname.placeholder");
    /** Validation error shown when the first name is empty. */
    public static final Localizable FIRST_NAME_REQUIRED =
            of("First name is required", "signup.firstname.required");

    /** Last name field label. */
    public static final Localizable LAST_NAME_LABEL =
            of("Last Name", "signup.lastname.label");
    /** Last name field placeholder. */
    public static final Localizable LAST_NAME_PLACEHOLDER =
            of("Enter your last name", "signup.lastname.placeholder");
    /** Validation error shown when the last name is empty. */
    public static final Localizable LAST_NAME_REQUIRED =
            of("Last name is required", "signup.lastname.required");

    /** Email field label. */
    public static final Localizable EMAIL_LABEL =
            of("Email", "signup.email.label");
    /** Email field placeholder. */
    public static final Localizable EMAIL_PLACEHOLDER =
            of("info@gmail.com", "signup.email.placeholder");
    /** Validation error shown when the email is empty. */
    public static final Localizable EMAIL_REQUIRED =
            of("Email is required", "signup.email.required");
    /** Validation error shown when the email format is invalid. */
    public static final Localizable EMAIL_INVALID =
            of("Enter a valid email address", "signup.email.invalid");

    /** Password field label. */
    public static final Localizable PASSWORD_LABEL =
            of("Password", "signup.password.label");
    /** Password field placeholder. */
    public static final Localizable PASSWORD_PLACEHOLDER =
            of("Enter your password", "signup.password.placeholder");
    /** Validation error shown when the password is empty. */
    public static final Localizable PASSWORD_REQUIRED =
            of("Password is required", "signup.password.required");

    /** "Agree to Terms and Conditions" checkbox label. */
    public static final Localizable TERMS =
            of("By creating an account means you agree to the Terms and Conditions, and our Privacy Policy",
                    "signup.terms");
    /** Consent text shown before the Terms link when {@code setTermsLinks} is used. */
    public static final Localizable TERMS_PREFIX =
            of("By creating an account, you agree to our ", "signup.terms.prefix");
    /** "Terms and Conditions" link label. */
    public static final Localizable TERMS_LINK =
            of("Terms and Conditions", "signup.terms.link");
    /** Text between the Terms and Privacy links. */
    public static final Localizable TERMS_SEPARATOR =
            of(" and our ", "signup.terms.separator");
    /** "Privacy Policy" link label. */
    public static final Localizable PRIVACY_LINK =
            of("Privacy Policy", "signup.privacy.link");
    /** Error shown when the Terms and Conditions checkbox is not checked. */
    public static final Localizable TERMS_REQUIRED =
            of("You must agree to the Terms and Conditions", "signup.terms.required");
    /** Submit button label. */
    public static final Localizable SIGN_UP_BUTTON =
            of("Sign Up", "signup.submit");

    /** "Sign up with Google" button label. */
    public static final Localizable SOCIAL_GOOGLE =
            of("Sign up with Google", "signup.social.google");
    /** "Sign up with X" button label. */
    public static final Localizable SOCIAL_X =
            of("Sign up with X", "signup.social.x");
    /** Divider label between social and form sign-up ("Or"). */
    public static final Localizable DIVIDER =
            of("Or", "signup.divider");

    /** "Already have an account?" prompt preceding the sign-in link. */
    public static final Localizable SIGN_IN_PROMPT =
            of("Already have an account? ", "signup.signin.prompt");
    /** "Sign In" link. */
    public static final Localizable SIGN_IN_LINK =
            of("Sign In", "signup.signin.link");

    /** Branding panel title. */
    public static final Localizable BRAND_TITLE =
            of("TailAdmin", "signup.brand.title");
    /** Branding panel description. */
    public static final Localizable BRAND_TEXT =
            of("Free and Open-Source Tailwind CSS Admin Dashboard Template", "signup.brand.text");
}
