package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.ResetPasswordPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultResetPasswordPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link ResetPasswordPage} components.
 *
 * @param <C> concrete configurator type
 */
public interface ResetPasswordPageConfigurator<C extends ResetPasswordPageConfigurator<C>>
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
     * Show or hide the "Wait, I remember my password... Click here" row.
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
     * Display an error message above the form.
     *
     * @param message the error message ({@code null} or blank clears it)
     * @return this configurator
     */
    C errorMessage(String message);

    /**
     * Add a listener invoked when the user submits the reset-password form.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withResetPasswordListener(ComponentEventListener<ResetPasswordPage.ResetPasswordEvent> listener);

    /**
     * Add a listener invoked when the "Click here" (back to sign-in) link is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withBackToSignInListener(ComponentEventListener<ResetPasswordPage.BackToSignInEvent> listener);

    /**
     * Get a configurator for the given {@link ResetPasswordPage} instance.
     *
     * @param resetPasswordPage the reset-password page to configure (not null)
     * @return a {@link BaseResetPasswordPageConfigurator}
     */
    static BaseResetPasswordPageConfigurator configure(ResetPasswordPage resetPasswordPage) {
        return new DefaultResetPasswordPageConfigurator(resetPasswordPage);
    }

    /**
     * Base configurator type for a {@link ResetPasswordPage}.
     */
    interface BaseResetPasswordPageConfigurator
            extends ResetPasswordPageConfigurator<BaseResetPasswordPageConfigurator> {

    }
}
