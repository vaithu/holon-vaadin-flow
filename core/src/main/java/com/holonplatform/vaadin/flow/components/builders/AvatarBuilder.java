package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAvatarBuilder;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.server.streams.DownloadHandler;

/**
 * Fluent builder for {@link Avatar} components.
 *
 * <h3>Usage examples</h3>
 * <pre>{@code
 * // Anonymous
 * Avatar anon = AvatarBuilder.create().build();
 *
 * // Named — initials auto-generated
 * Avatar named = AvatarBuilder.create("Jane Smith").build();
 *
 * // Localizable name (resolved from message bundle)
 * Avatar i18n = AvatarBuilder.create()
 *     .name("user.full-name", "John Doe")
 *     .deferLocalization()               // resolve on first attach
 *     .build();
 *
 * // Explicit abbreviation
 * Avatar abbr = AvatarBuilder.create("Augusta Ada King")
 *     .abbreviation("AK")
 *     .build();
 *
 * // Enum-based colour + aria-label
 * Avatar coloured = AvatarBuilder.create("Alice")
 *     .colorIndex(AvatarColor.VIOLET)
 *     .ariaLabel("Alice's avatar")
 *     .build();
 *
 * // Aura-compatible XL profile avatar
 * Avatar profile = AvatarBuilder.create("Jane Doe")
 *     .profile()
 *     .build();
 *
 * // Deterministic colour from entity id
 * Avatar user = AvatarBuilder.create(person.getFullName())
 *     .colorIndex(AvatarColor.forId(person.getId()))
 *     .build();
 *
 * // Via Components factory
 * Avatar a = Components.avatar("Jane Smith").colorIndex(AvatarColor.BLUE).build();
 * }</pre>
 *
 * @see AvatarConfigurator
 * @see AvatarColor
 * @since 10.0.0
 */
public interface AvatarBuilder
        extends AvatarConfigurator<AvatarBuilder>, ComponentBuilder<Avatar, AvatarBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /** Create a new anonymous {@link AvatarBuilder}. */
    static AvatarBuilder create() {
        return new DefaultAvatarBuilder(null);
    }

    /**
     * Create a new {@link AvatarBuilder} for a named avatar.
     *
     * @param name display name (tooltip + initials auto-generation)
     */
    static AvatarBuilder create(String name) {
        return new DefaultAvatarBuilder(name);
    }

    /**
     * Create a new {@link AvatarBuilder} for an avatar whose name comes from a message bundle.
     *
     * @param name localizable name
     */
    static AvatarBuilder create(Localizable name) {
        AvatarBuilder b = new DefaultAvatarBuilder(null);
        if (name != null) b.name(name);
        return b;
    }

    /**
     * Create a new {@link AvatarBuilder} with name and profile-image URL.
     *
     * @param name     display name
     * @param imageUrl profile-image URL
     */
    static AvatarBuilder create(String name, String imageUrl) {
        return new DefaultAvatarBuilder(name, imageUrl);
    }

    /**
     * Create a new {@link AvatarBuilder} with a backend image handler.
     *
     * @param name    display name
     * @param handler {@link DownloadHandler} that streams the profile image
     */
    static AvatarBuilder create(String name, DownloadHandler handler) {
        return new DefaultAvatarBuilder(name, handler);
    }
}
