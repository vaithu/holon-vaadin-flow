package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultIconBadgeBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.icon.Icon;

/**
 * Fluent builder for {@link IconBadge} components.
 *
 * <p>Usage:
 * <pre>{@code
 * // Shortest — VaadinIcon + variant + size
 * IconBadge badge = IconBadge.builder(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS)
 *     .size(IconBadge.Size.LG)
 *     .build();
 *
 * // Full chain
 * IconBadge badge = IconBadge.builder()
 *     .icon(VaadinIcon.TRUCK)
 *     .variant(Alert.Variant.WARNING)
 *     .size(IconBadge.Size.SM)
 *     .build();
 * }</pre>
 *
 * @see IconBadgeConfigurator
 * @see IconBadge
 */
public interface IconBadgeBuilder
        extends IconBadgeConfigurator<IconBadgeBuilder>, ComponentBuilder<IconBadge, IconBadgeBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a neutral {@link IconBadgeBuilder} with no icon pre-set.
     *
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder create() {
        return new DefaultIconBadgeBuilder(null, null, IconBadge.Size.DEFAULT);
    }

    /**
     * Create an {@link IconBadgeBuilder} pre-configured with the given icon.
     *
     * @param icon the VaadinIcon to display (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder create(Icon icon) {
        return new DefaultIconBadgeBuilder(icon, null, IconBadge.Size.DEFAULT);
    }

    /**
     * Create an {@link IconBadgeBuilder} pre-configured with icon and variant.
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder create(Icon icon, Alert.Variant variant) {
        return new DefaultIconBadgeBuilder(icon, variant, IconBadge.Size.DEFAULT);
    }

    /**
     * Create a fully-specified {@link IconBadgeBuilder}.
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @param size    the size preset (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder create(Icon icon, Alert.Variant variant, IconBadge.Size size) {
        return new DefaultIconBadgeBuilder(icon, variant, size);
    }
}

