package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultNotFoundPageConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link NotFoundPage} components.
 *
 * @param <C> concrete configurator type
 */
public interface NotFoundPageConfigurator<C extends NotFoundPageConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Set the small uppercase eyebrow label shown above the error code.
     *
     * @param text the eyebrow text
     * @return this configurator
     */
    C eyebrow(String text);

    /**
     * Set the large error code (default {@code "404"}).
     *
     * @param text the error code
     * @return this configurator
     */
    C errorCode(String text);

    /**
     * Set the message shown under the error code.
     *
     * @param text the message text
     * @return this configurator
     */
    C message(String text);

    /**
     * Set the "Back to Home Page" button label.
     *
     * @param text the button text
     * @return this configurator
     */
    C buttonText(String text);

    /**
     * Show or hide the brand logo at the top of the page.
     *
     * @param visible whether the brand logo is visible
     * @return this configurator
     */
    C branding(boolean visible);

    /**
     * Replace the brand logo with the given content.
     *
     * @param logo the logo component
     * @return this configurator
     */
    C brandingLogo(Component logo);

    /**
     * Fully replace the large error-code illustration with a custom component.
     *
     * @param graphic the illustration ({@code null} restores the default text code)
     * @return this configurator
     */
    C illustration(Component graphic);

    /**
     * Show or hide the footer.
     *
     * @param visible whether the footer is visible
     * @return this configurator
     */
    C footer(boolean visible);

    /**
     * Set the footer text.
     *
     * @param text the footer text
     * @return this configurator
     */
    C footerText(String text);

    /**
     * Set the route navigated to when the "Back to Home Page" button is pressed.
     *
     * @param route the navigation route
     * @return this configurator
     */
    C homeTarget(String route);

    /**
     * Set the view navigated to when the "Back to Home Page" button is pressed.
     *
     * @param target the navigation target
     * @return this configurator
     */
    C homeTarget(Class<? extends Component> target);

    /**
     * Add a listener invoked when the "Back to Home Page" button is clicked.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withBackToHomeListener(ComponentEventListener<NotFoundPage.BackToHomeEvent> listener);

    /**
     * Get a configurator for the given {@link NotFoundPage} instance.
     *
     * @param notFoundPage the not-found page to configure (not null)
     * @return a {@link BaseNotFoundPageConfigurator}
     */
    static BaseNotFoundPageConfigurator configure(NotFoundPage notFoundPage) {
        return new DefaultNotFoundPageConfigurator(notFoundPage);
    }

    /**
     * Base configurator type for a {@link NotFoundPage}.
     */
    interface BaseNotFoundPageConfigurator extends NotFoundPageConfigurator<BaseNotFoundPageConfigurator> {

    }
}
