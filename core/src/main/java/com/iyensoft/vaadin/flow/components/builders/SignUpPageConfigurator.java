package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.SignUpPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSignUpPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link SignUpPage} components.
 *
 * @param <C> concrete configurator type
 */
public interface SignUpPageConfigurator<C extends SignUpPageConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Set the main heading text.
     *
     * @param text the heading text
     * @return this configurator
     */
    C heading(String text);

    /**
     * Set the subtitle shown under the heading.
     *
     * @param text the subtitle text
     * @return this configurator
     */
    C subtitle(String text);

    /**
     * Show or hide the social sign-up buttons and the "Or" divider.
     *
     * @param visible whether social sign-up is visible
     * @return this configurator
     */
    C socialLogin(boolean visible);

    /**
     * Show or hide the "agree to Terms and Conditions" checkbox.
     *
     * @param visible whether the checkbox is visible
     * @return this configurator
     */
    C terms(boolean visible);

    /**
     * Show or hide the "Already have an account? Sign In" row.
     *
     * @param visible whether the row is visible
     * @return this configurator
     */
    C signIn(boolean visible);

    /**
     * Show or hide the right-hand branding panel.
     *
     * @param visible whether the branding panel is visible
     * @return this configurator
     */
    C branding(boolean visible);

    /**
     * Set the branding title on the right-hand panel.
     *
     * @param text the branding title
     * @return this configurator
     */
    C brandingTitle(String text);

    /**
     * Set the branding description on the right-hand panel.
     *
     * @param text the branding description
     * @return this configurator
     */
    C brandingText(String text);

    /**
     * Replace the branding logo with the given content.
     *
     * @param logo the logo component
     * @return this configurator
     */
    C brandingLogo(Component logo);

    /**
     * Fully replace the inner content of the right-hand branding panel with a custom component.
     *
     * @param content the branding content ({@code null} restores the default)
     * @return this configurator
     */
    C brandingContent(Component content);

    /**
     * Render the consent text with clickable "Terms and Conditions" and "Privacy Policy" links.
     *
     * @param termsUrl   the Terms and Conditions URL
     * @param privacyUrl the Privacy Policy URL
     * @return this configurator
     */
    C termsLinks(String termsUrl, String privacyUrl);

    /**
     * Replace the consent text next to the checkbox with a custom component.
     *
     * @param content the consent content ({@code null} restores the default label)
     * @return this configurator
     */
    C termsContent(Component content);

    /**
     * Display an error message above the form.
     *
     * @param message the error message ({@code null} or blank clears it)
     * @return this configurator
     */
    C errorMessage(String message);

    /**
     * Add a listener invoked when the user submits the sign-up form.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSignUpListener(ComponentEventListener<SignUpPage.SignUpEvent> listener);

    /**
     * Add a listener invoked when a social sign-up button is pressed.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSocialSignUpListener(ComponentEventListener<SignUpPage.SocialSignUpEvent> listener);

    /**
     * Add a listener invoked when the "Sign In" link is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSignInListener(ComponentEventListener<SignUpPage.SignInEvent> listener);

    /**
     * Get a configurator for the given {@link SignUpPage} instance.
     *
     * @param signUpPage the sign-up page to configure (not null)
     * @return a {@link BaseSignUpPageConfigurator}
     */
    static BaseSignUpPageConfigurator configure(SignUpPage signUpPage) {
        return new DefaultSignUpPageConfigurator(signUpPage);
    }

    /**
     * Base configurator type for a {@link SignUpPage}.
     */
    interface BaseSignUpPageConfigurator extends SignUpPageConfigurator<BaseSignUpPageConfigurator> {

    }
}
