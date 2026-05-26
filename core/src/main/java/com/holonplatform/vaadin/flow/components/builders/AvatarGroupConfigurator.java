package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAvatarGroupConfigurator;
import com.vaadin.flow.component.avatar.AvatarGroup;

/**
 * Configurator for {@link AvatarGroup} components.
 *
 * <p>Key features:
 * <ul>
 *   <li>{@link #add(AvatarGroup.AvatarGroupItem...)} — append one or more items</li>
 *   <li>{@link #maxItemsVisible(int)} — limit visible avatars; extras collapse into an overflow counter</li>
 *   <li>{@link #i18n(AvatarGroup.AvatarGroupI18n)} — localize accessibility strings</li>
 * </ul>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 * @since 10.0.0
 */
public interface AvatarGroupConfigurator<C extends AvatarGroupConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Appends one or more {@link AvatarGroup.AvatarGroupItem}s to this group.
     *
     * @param items the items to add (not null)
     * @return this configurator
     */
    C add(AvatarGroup.AvatarGroupItem... items);

    /**
     * Sets the maximum number of avatars to display before collapsing into
     * an overflow counter. Overflowing avatars are shown in a tooltip list on click.
     *
     * @param max maximum visible count (&gt; 0)
     * @return this configurator
     */
    C maxItemsVisible(int max);

    /**
     * Sets the internationalisation object for all user-facing strings in this group.
     *
     * @param i18n the i18n configuration (not null)
     * @return this configurator
     */
    C i18n(AvatarGroup.AvatarGroupI18n i18n);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a {@link BaseAvatarGroupConfigurator} to configure an existing {@link AvatarGroup}.
     *
     * @param group the avatar group to configure (not null)
     * @return a new {@link BaseAvatarGroupConfigurator}
     */
    static BaseAvatarGroupConfigurator configure(AvatarGroup group) {
        return new DefaultAvatarGroupConfigurator(group);
    }

    /**
     * Base (non-generic) {@link AvatarGroupConfigurator}.
     */
    interface BaseAvatarGroupConfigurator extends AvatarGroupConfigurator<BaseAvatarGroupConfigurator> {
    }
}

