package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAvatarGroupBuilder;
import com.vaadin.flow.component.avatar.AvatarGroup;

/**
 * Fluent builder for {@link AvatarGroup} components.
 *
 * <h3>Usage examples</h3>
 * <pre>{@code
 * // Basic group from a list of people
 * AvatarGroup group = AvatarGroupBuilder.create()
 *     .add(new AvatarGroup.AvatarGroupItem("Alice"))
 *     .add(new AvatarGroup.AvatarGroupItem("Bob"))
 *     .add(new AvatarGroup.AvatarGroupItem("Carol"))
 *     .build();
 *
 * // With overflow limit
 * AvatarGroup group = AvatarGroupBuilder.create()
 *     .maxItemsVisible(3)
 *     .add(people.stream()
 *         .map(p -> {
 *             var item = new AvatarGroup.AvatarGroupItem(p.getName());
 *             item.setColorIndex(p.getId() % Avatar.MAX_COLOR_INDEX);
 *             return item;
 *         })
 *         .toArray(AvatarGroup.AvatarGroupItem[]::new))
 *     .build();
 *
 * // Via Components factory
 * AvatarGroup grp = Components.avatarGroup()
 *     .maxItemsVisible(5)
 *     .build();
 * }</pre>
 *
 * @see AvatarGroupConfigurator
 * @see AvatarGroup
 * @since 10.0.0
 */
public interface AvatarGroupBuilder
        extends AvatarGroupConfigurator<AvatarGroupBuilder>, ComponentBuilder<AvatarGroup, AvatarGroupBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a new empty {@link AvatarGroupBuilder}.
     *
     * @return a new {@link AvatarGroupBuilder}
     */
    static AvatarGroupBuilder create() {
        return new DefaultAvatarGroupBuilder();
    }

    /**
     * Create a new {@link AvatarGroupBuilder} pre-filled with the given items.
     *
     * @param items initial group items (not null)
     * @return a new {@link AvatarGroupBuilder}
     */
    static AvatarGroupBuilder create(AvatarGroup.AvatarGroupItem... items) {
        AvatarGroupBuilder builder = new DefaultAvatarGroupBuilder();
        if (items != null && items.length > 0) {
            builder.add(items);
        }
        return builder;
    }
}

