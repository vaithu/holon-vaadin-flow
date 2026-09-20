package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.i18n.NotFoundI18N;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;

/**
 * Generic, theme-agnostic 404 "page not found" screen inspired by the TailAdmin "404" template.
 *
 * <p>The component is intentionally UI-only: it exposes a typed {@link BackToHomeEvent} and does not
 * navigate by itself. For a self-contained flow, declare a navigation target with
 * {@link #setHomeNavigationTarget(Class)} (or {@link #setHomeNavigationTarget(String)}) or run a
 * custom action via {@link #setOnHome(SerializableRunnable)}; the "Back to Home Page" button then
 * drives it.
 *
 * <p>Layout is a single centered column: an optional brand logo, a small "ERROR" eyebrow, the large
 * error code (an illustration you can fully replace via {@link #setIllustration(Component)}), a
 * message, the home button and an optional footer. All styling lives in
 * {@code context://notfound-page.css} so the component works with any theme.
 *
 * <p>All texts are localized through the Holon platform (see {@link NotFoundI18N}). The developer
 * chooses the language by setting the current {@link java.util.Locale} on the
 * {@link com.holonplatform.core.i18n.LocalizationContext} (or Vaadin session) and registering the
 * {@code NotFoundMessages} bundle. Individual texts can also be overridden via the {@code set*}
 * methods, which accept either a plain {@link String} or a {@link Localizable}.
 */
@StyleSheet("context://notfound-page.css")
public class NotFoundPage extends Div implements LocaleChangeObserver {

    private static final String CLASS_ROOT = "iyen-notfound";

    private static final String DEFAULT_BRAND_LOGO_SVG = """
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
            </svg>
            """;

    private final Div brandLogo = new Div();
    private final Div content = new Div();
    private final Span eyebrow = new Span();
    private final Div illustration = new Div();
    private final Div code = new Div();
    private final H1 message = new H1();
    private final Button homeButton = ButtonBuilder.create()
            .primary()
            .styleName("iyen-notfound__button")
            .withClickListener(e -> fireBack())
            .build();
    private final Paragraph footer = new Paragraph();

    private Localizable eyebrowText = NotFoundI18N.EYEBROW;
    private Localizable codeText = NotFoundI18N.CODE;
    private Localizable messageText = NotFoundI18N.MESSAGE;
    private Localizable buttonText = NotFoundI18N.BUTTON;
    private Localizable footerText = NotFoundI18N.FOOTER;

    private SerializableRunnable onHome;

    public NotFoundPage() {
        addClassName(CLASS_ROOT);

        brandLogo.addClassName("iyen-notfound__brand-logo");
        brandLogo.add(new Html(DEFAULT_BRAND_LOGO_SVG));

        eyebrow.addClassName("iyen-notfound__eyebrow");

        illustration.addClassName("iyen-notfound__illustration");
        code.addClassName("iyen-notfound__code");
        illustration.add(code);

        message.addClassName("iyen-notfound__message");
        footer.addClassName("iyen-notfound__footer");

        content.addClassName("iyen-notfound__content");
        content.add(eyebrow, illustration, message, homeButton);

        add(brandLogo, content, footer);

        localizeStaticTexts();
    }

    private void fireBack() {
        fireEvent(new BackToHomeEvent(this, true));
        if (onHome != null) {
            onHome.run();
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

    private void localizeStaticTexts() {
        eyebrow.setText(localize(eyebrowText));
        code.setText(localize(codeText));
        message.setText(localize(messageText));
        homeButton.setText(localize(buttonText));
        footer.setText(localize(footerText));
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

    /** Sets the small uppercase eyebrow label, overriding the localized default. */
    public void setEyebrow(String text) {
        this.eyebrowText = Localizable.of(text);
        eyebrow.setText(text);
    }

    /** Sets the eyebrow label from a {@link Localizable}, keeping it locale-aware. */
    public void setEyebrow(Localizable text) {
        this.eyebrowText = text;
        eyebrow.setText(localize(text));
    }

    /** Sets the large error code (default {@code "404"}), overriding the localized default. */
    public void setErrorCode(String text) {
        this.codeText = Localizable.of(text);
        code.setText(text);
    }

    /** Sets the error code from a {@link Localizable}, keeping it locale-aware. */
    public void setErrorCode(Localizable text) {
        this.codeText = text;
        code.setText(localize(text));
    }

    /** Sets the message shown under the error code, overriding the localized default. */
    public void setMessage(String text) {
        this.messageText = Localizable.of(text);
        message.setText(text);
    }

    /** Sets the message from a {@link Localizable}, keeping it locale-aware. */
    public void setMessage(Localizable text) {
        this.messageText = text;
        message.setText(localize(text));
    }

    /** Sets the home button label, overriding the localized default. */
    public void setButtonText(String text) {
        this.buttonText = Localizable.of(text);
        homeButton.setText(text);
    }

    /** Sets the home button label from a {@link Localizable}, keeping it locale-aware. */
    public void setButtonText(Localizable text) {
        this.buttonText = text;
        homeButton.setText(localize(text));
    }

    /** Sets the route navigated to when the "Back to Home Page" button is pressed. */
    public void setHomeNavigationTarget(String route) {
        this.onHome = route == null ? null : () -> navigateTo(route);
    }

    /** Sets the view navigated to when the "Back to Home Page" button is pressed. */
    public void setHomeNavigationTarget(Class<? extends Component> target) {
        this.onHome = target == null ? null : () -> navigateTo(target);
    }

    /** Runs a custom action when the "Back to Home Page" button is pressed. */
    public void setOnHome(SerializableRunnable action) {
        this.onHome = action;
    }

    /** Shows or hides the brand logo at the top of the page. */
    public void setBrandingVisible(boolean visible) {
        brandLogo.setVisible(visible);
    }

    /** Replaces the brand logo with the given content (e.g. an {@code Image} or SVG). */
    public void setBrandingLogo(Component logo) {
        brandLogo.removeAll();
        brandLogo.add(logo);
    }

    /**
     * Fully replaces the large error-code illustration with a custom component (e.g. an
     * {@code Image} or inline SVG). Pass {@code null} to restore the default text code.
     */
    public void setIllustration(Component graphic) {
        illustration.removeAll();
        illustration.add(graphic == null ? code : graphic);
    }

    /** Shows or hides the footer. */
    public void setFooterVisible(boolean visible) {
        footer.setVisible(visible);
    }

    /** Sets the footer text, overriding the localized default. */
    public void setFooterText(String text) {
        this.footerText = Localizable.of(text);
        footer.setText(text);
    }

    /** Sets the footer text from a {@link Localizable}, keeping it locale-aware. */
    public void setFooterText(Localizable text) {
        this.footerText = text;
        footer.setText(localize(text));
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    public Button getHomeButton() {
        return homeButton;
    }

    // ---------------------------------------------------------------------
    // Event registration
    // ---------------------------------------------------------------------

    /** Adds a listener invoked when the "Back to Home Page" button is clicked. */
    public Registration addBackToHomeListener(ComponentEventListener<BackToHomeEvent> listener) {
        return addListener(BackToHomeEvent.class, listener);
    }

    // ---------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------

    /** Fired when the "Back to Home Page" button is clicked. */
    public static class BackToHomeEvent extends ComponentEvent<NotFoundPage> {
        public BackToHomeEvent(NotFoundPage source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
