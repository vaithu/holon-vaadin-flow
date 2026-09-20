package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.TwoStepVerificationPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultTwoStepVerificationPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link TwoStepVerificationPage} components.
 *
 * @param <C> concrete configurator type
 */
public interface TwoStepVerificationPageConfigurator<C extends TwoStepVerificationPageConfigurator<C>>
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
     * Set the label shown above the code field.
     *
     * @param text the code-field label
     * @return this configurator
     */
    C codeLabel(String text);

    /**
     * Show or hide the "Didn't get the code? Resend" row.
     *
     * @param visible whether the row is visible
     * @return this configurator
     */
    C resend(boolean visible);

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
     * Add a listener invoked when the user submits the verification code.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withVerifyCodeListener(ComponentEventListener<TwoStepVerificationPage.VerifyCodeEvent> listener);

    /**
     * Add a listener invoked when the "Resend" link is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withResendCodeListener(ComponentEventListener<TwoStepVerificationPage.ResendCodeEvent> listener);

    /**
     * Get a configurator for the given {@link TwoStepVerificationPage} instance.
     *
     * @param twoStepVerificationPage the two-step verification page to configure (not null)
     * @return a {@link BaseTwoStepVerificationPageConfigurator}
     */
    static BaseTwoStepVerificationPageConfigurator configure(
            TwoStepVerificationPage twoStepVerificationPage) {
        return new DefaultTwoStepVerificationPageConfigurator(twoStepVerificationPage);
    }

    /**
     * Base configurator type for a {@link TwoStepVerificationPage}.
     */
    interface BaseTwoStepVerificationPageConfigurator
            extends TwoStepVerificationPageConfigurator<BaseTwoStepVerificationPageConfigurator> {

    }
}
