package com.holonplatform.vaadin.flow.components.builders;

/**
 * Semantic colour tokens for {@link com.vaadin.flow.component.avatar.Avatar} backgrounds.
 *
 * <p>Maps directly to the 7 CSS colour slots built into the Vaadin theme
 * ({@code --vaadin-avatar-user-color-0} … {@code --vaadin-avatar-user-color-6}).
 * Override those custom properties in your stylesheet to match your brand palette;
 * the enum value names are indicative defaults, not hard-coded colours.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // Explicit colour
 * AvatarBuilder.create("Alice").colorIndex(AvatarColor.BLUE).build();
 *
 * // Deterministic colour from an entity id (modulo assignment)
 * AvatarColor color = AvatarColor.forId(user.getId());
 * AvatarBuilder.create(user.getName()).colorIndex(color).build();
 * }</pre>
 *
 * @since 10.0.0
 */
public enum AvatarColor {

    /** Slot 0 – blue by default */
    BLUE(0),

    /** Slot 1 – green by default */
    GREEN(1),

    /** Slot 2 – pink / rose by default */
    PINK(2),

    /** Slot 3 – orange by default */
    ORANGE(3),

    /** Slot 4 – violet / purple by default */
    VIOLET(4),

    /** Slot 5 – indigo by default */
    INDIGO(5),

    /** Slot 6 – red / error by default */
    RED(6);

    private final int index;

    AvatarColor(int index) {
        this.index = index;
    }

    /**
     * Returns the 0-based integer index to pass to
     * {@link com.vaadin.flow.component.avatar.Avatar#setColorIndex(Integer)}.
     *
     * @return 0–6
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns a stable {@link AvatarColor} for the given entity/user id using modulo assignment.
     *
     * <p>Useful for deterministically colouring avatars in collaborative UIs so that
     * the same person always gets the same colour across sessions:
     * <pre>{@code
     * AvatarColor color = AvatarColor.forId(user.getId());
     * }</pre>
     *
     * @param id any long identifier (negative values are handled via {@code Math.abs})
     * @return a stable, non-null {@link AvatarColor}
     */
    public static AvatarColor forId(long id) {
        return values()[(int) (Math.abs(id) % values().length)];
    }
}


