package com.iyensoft.vaadin.flow.components.i18n;

import com.holonplatform.core.i18n.Localizable;

/**
 * Localizable message constants for {@link com.iyensoft.vaadin.flow.components.NotFoundPage}.
 *
 * <p>Each constant carries an English default message plus a message localization key. The actual
 * translation is resolved at runtime through the Holon platform localization: either a Vaadin
 * {@link com.vaadin.flow.i18n.I18NProvider} or a Holon
 * {@link com.holonplatform.core.i18n.LocalizationContext}. If no localization is available the
 * default English message is used.
 *
 * <p>All keys follow the convention {@code notfound.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Default bundle: {@code com.iyensoft.vaadin.flow.components.i18n.NotFoundMessages}
 * <pre>{@code
 * // Holon LocalizationContext (Spring Boot auto-config picks this up automatically):
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.iyensoft.vaadin.flow.components.i18n.NotFoundMessages"))
 *     .build();
 * }</pre>
 *
 * <h3>Available message keys</h3>
 * <table border="1">
 *   <caption>Message keys and their English defaults</caption>
 *   <tr><th>Key</th><th>Default (English)</th></tr>
 *   <tr><td>{@code notfound.eyebrow}</td><td>ERROR</td></tr>
 *   <tr><td>{@code notfound.code}</td><td>404</td></tr>
 *   <tr><td>{@code notfound.message}</td><td>We can't seem to find the page you are looking for!</td></tr>
 *   <tr><td>{@code notfound.button}</td><td>Back to Home Page</td></tr>
 *   <tr><td>{@code notfound.footer}</td><td>&copy; 2026 - TailAdmin</td></tr>
 * </table>
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class NotFoundI18N {

    private NotFoundI18N() {}

    private static Localizable of(String message, String messageCode) {
        return Localizable.builder().message(message).messageCode(messageCode).build();
    }

    /** Small uppercase label shown above the error code. */
    public static final Localizable EYEBROW =
            of("ERROR", "notfound.eyebrow");
    /** The large error code. */
    public static final Localizable CODE =
            of("404", "notfound.code");
    /** Message shown under the error code. */
    public static final Localizable MESSAGE =
            of("We can't seem to find the page you are looking for!", "notfound.message");
    /** "Back to Home Page" button label. */
    public static final Localizable BUTTON =
            of("Back to Home Page", "notfound.button");
    /** Footer text shown at the bottom of the page. */
    public static final Localizable FOOTER =
            of("\u00A9 2026 - TailAdmin", "notfound.footer");
}
