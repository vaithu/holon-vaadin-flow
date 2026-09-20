package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.SignInPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSignInPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link SignInPage} components.
 *
 * @param <C> concrete configurator type
 */
public interface SignInPageConfigurator<C extends SignInPageConfigurator<C>>
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
     * Show or hide the social sign-in buttons and the "Or" divider.
     *
     * @param visible whether social login is visible
     * @return this configurator
     */
    C socialLogin(boolean visible);

    /**
     * Show or hide the "Keep me logged in" checkbox.
     *
     * @param visible whether the checkbox is visible
     * @return this configurator
     */
    C keepLoggedIn(boolean visible);

    /**
     * Show or hide the "Forgot password?" link.
     *
     * @param visible whether the link is visible
     * @return this configurator
     */
    C forgotPassword(boolean visible);

    /**
     * Show or hide the "Don't have an account? Sign Up" row.
     *
     * @param visible whether the row is visible
     * @return this configurator
     */
    C signUp(boolean visible);

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
     * Display an error message above the form.
     *
     * @param message the error message ({@code null} or blank clears it)
     * @return this configurator
     */
    C errorMessage(String message);

    /**
     * Add a listener invoked when the user submits the sign-in form.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSignInListener(ComponentEventListener<SignInPage.SignInEvent> listener);

    /**
     * Add a listener invoked when a social sign-in button is pressed.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSocialSignInListener(ComponentEventListener<SignInPage.SocialSignInEvent> listener);

    /**
     * Add a listener invoked when the "Forgot password?" link is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withForgotPasswordListener(ComponentEventListener<SignInPage.ForgotPasswordEvent> listener);

    /**
     * Add a listener invoked when the "Sign Up" link is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withSignUpListener(ComponentEventListener<SignInPage.SignUpEvent> listener);

    /**
     * Get a configurator for the given {@link SignInPage} instance.
     *
     * @param signInPage the sign-in page to configure (not null)
     * @return a {@link BaseSignInPageConfigurator}
     */
    static BaseSignInPageConfigurator configure(SignInPage signInPage) {
        return new DefaultSignInPageConfigurator(signInPage);
    }

    /**
     * Base configurator type for a {@link SignInPage}.
     */
    interface BaseSignInPageConfigurator extends SignInPageConfigurator<BaseSignInPageConfigurator> {

    }
}
