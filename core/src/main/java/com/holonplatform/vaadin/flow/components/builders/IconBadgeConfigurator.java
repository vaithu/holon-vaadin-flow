package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultIconBadgeConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Configurator for {@link IconBadge} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface IconBadgeConfigurator<C extends IconBadgeConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Sets the semantic color variant.
     *
     * @param variant the variant to apply ({@code null} = neutral default)
     * @return this configurator
     */
    C variant(Alert.Variant variant);

    /**
     * Sets the badge size preset.
     *
     * @param size the size (not null)
     * @return this configurator
     */
    C size(IconBadge.Size size);

    /**
     * Sets the icon from a {@link VaadinIcon} constant.
     *
     * @param icon the VaadinIcon (not null)
     * @return this configurator
     */
    C icon(VaadinIcon icon);

    /**
     * Sets the icon from any component.
     *
     * @param icon the icon component (not null)
     * @return this configurator
     */
    C icon(Component icon);

    /**
     * Sets the optional text label shown next to the icon.
     *
     * @param text the text to display, or {@code null} / blank to clear
     * @return this configurator
     */
    C text(String text);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a {@link BaseIconBadgeConfigurator} to configure an existing {@link IconBadge}.
     *
     * @param badge the badge to configure (not null)
     * @return a new {@link BaseIconBadgeConfigurator}
     */
    static BaseIconBadgeConfigurator configure(IconBadge badge) {
        return new DefaultIconBadgeConfigurator(badge);
    }

    /**
     * Base (non-generic) {@link IconBadgeConfigurator}.
     */
    interface BaseIconBadgeConfigurator extends IconBadgeConfigurator<BaseIconBadgeConfigurator> {
    }
}
