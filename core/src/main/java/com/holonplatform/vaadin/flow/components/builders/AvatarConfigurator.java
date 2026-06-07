package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAvatarConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.server.streams.DownloadHandler;

/**
 * Configurator for {@link Avatar} components.
 *
 * <p>All Avatar properties are available as fluent methods:
 * <ul>
 *   <li>{@link #name(String)} / {@link #name(Localizable)} — display name (tooltip + initials)</li>
 *   <li>{@link #abbreviation(String)} / {@link #abbreviation(Localizable)} — override initials</li>
 *   <li>{@link #image(String)} — profile image URL</li>
 *   <li>{@link #imageHandler(DownloadHandler)} — stream image from a backend resource</li>
 *   <li>{@link #colorIndex(int)} / {@link #colorIndex(AvatarColor)} — background colour slot 0–6</li>
 *   <li>{@link #variant(Alert.Variant)} — IconBadge-style tinted background (success/warning/info/destructive)</li>
 *   <li>{@link #profile()} — Aura-compatible XL profile avatar styling</li>
 *   <li>{@link #ariaLabel(String)} / {@link #ariaLabel(Localizable)} — accessible label</li>
 *   <li>Theme variants (xsmall / small / large / xlarge) via the inherited avatar theme-variant API</li>
 * </ul>
 *
 * <p>Implements {@link DeferrableLocalizationConfigurator}: call
 * {@link #deferLocalization()} to resolve i18n strings on first attach instead of at
 * build time. Required when using message bundles that are not yet available during
 * view construction.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 * @since 10.0.0
 */
public interface AvatarConfigurator<C extends AvatarConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasThemeVariantConfigurator<AvatarVariant, C>,
        HasAriaLabelConfigurator<C>,
        DeferrableLocalizationConfigurator<C> {

    // -----------------------------------------------------------------------
    // Name
    // -----------------------------------------------------------------------

    /**
     * Sets the display name shown in the tooltip.
     * The abbreviation (initials) is auto-generated from the name unless
     * {@link #abbreviation(String)} is also called.
     *
     * @param name display name (may be {@code null} for anonymous)
     * @return this configurator
     */
    C name(String name);

    /**
     * Sets the display name from a {@link Localizable} message.
     * Supports deferred resolution via {@link #deferLocalization()}.
     *
     * @param name localizable name (may be {@code null})
     * @return this configurator
     */
    C name(Localizable name);

    C name(LabelBuilder<?> label);

    /**
     * Sets the display name using a message-code lookup.
     *
     * @param defaultName  fallback text when no translation is available
     * @param messageCode  i18n message key
     * @param arguments    optional message arguments
     * @return this configurator
     */
    default C name(String defaultName, String messageCode, Object... arguments) {
        return name(Localizable.builder()
                .message(defaultName != null ? defaultName : "")
                .messageCode(messageCode)
                .messageArguments(arguments)
                .build());
    }

    // -----------------------------------------------------------------------
    // Abbreviation
    // -----------------------------------------------------------------------

    /**
     * Overrides the auto-generated two-letter initials.
     * Keep to 2–3 characters for optimal display.
     *
     * @param abbreviation custom abbreviation (may be {@code null} to revert to auto)
     * @return this configurator
     */
    C abbreviation(String abbreviation);

    /**
     * Overrides the auto-generated initials from a {@link Localizable} message.
     * Supports deferred resolution via {@link #deferLocalization()}.
     *
     * @param abbreviation localizable abbreviation (may be {@code null})
     * @return this configurator
     */
    C abbreviation(Localizable abbreviation);

    /**
     * Overrides the auto-generated initials using a message-code lookup.
     *
     * @param defaultAbbrev fallback text when no translation is available
     * @param messageCode   i18n message key
     * @param arguments     optional message arguments
     * @return this configurator
     */
    default C abbreviation(String defaultAbbrev, String messageCode, Object... arguments) {
        return abbreviation(Localizable.builder()
                .message(defaultAbbrev != null ? defaultAbbrev : "")
                .messageCode(messageCode)
                .messageArguments(arguments)
                .build());
    }

    // -----------------------------------------------------------------------
    // Image
    // -----------------------------------------------------------------------

    /**
     * Sets the profile-image URL.
     * When an image is set the abbreviation is not displayed.
     *
     * @param imageUrl absolute or context-relative URL (may be {@code null} to clear)
     * @return this configurator
     */
    C image(String imageUrl);

    /**
     * Sets a backend {@link DownloadHandler} that streams the profile image.
     *
     * @param handler the download handler (not null)
     * @return this configurator
     */
    C imageHandler(DownloadHandler handler);

    // -----------------------------------------------------------------------
    // Color
    // -----------------------------------------------------------------------

    /**
     * Sets the background-colour slot (0–6).
     * Each index maps to a CSS custom property override-able in your theme.
     *
     * @param colorIndex 0 through 6
     * @return this configurator
     */
    C colorIndex(int colorIndex);

    /**
     * Sets the background colour using a {@link AvatarColor} constant.
     * Prefer this over the raw integer overload for readability and deterministic
     * assignment via {@link AvatarColor#forId(long)}.
     *
     * @param color the semantic colour token (not null)
     * @return this configurator
     */
    C colorIndex(AvatarColor color);

    // -----------------------------------------------------------------------
    // Variant (IconBadge-style background)
    // -----------------------------------------------------------------------

    /**
     * Applies an {@link Alert.Variant} tinted background to the avatar, matching
     * the exact same palette used by {@link com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge}.
     *
     * <p>This lets the developer choose between two visual styles:
     * <ul>
     *   <li><strong>Default</strong> — Vaadin's 7-slot per-user colour ring ({@link #colorIndex(AvatarColor)})</li>
     *   <li><strong>Semantic tint</strong> — success green, warning amber, info violet, destructive red</li>
     * </ul>
     *
     * <p>Requires {@code utilities.css} to be loaded in the view or its parent layout.
     *
     * @param variant the semantic colour variant (not null)
     * @return this configurator
     */
    C variant(Alert.Variant variant);

    // -----------------------------------------------------------------------
    // Profile avatar (Aura-compatible XL presentation)
    // -----------------------------------------------------------------------

    /**
     * Applies the Aura-compatible XL profile-avatar presentation.
     *
     * <p>This is a CSS-only variant that adds the {@code avatar--profile-xl} class,
     * letting the stylesheet control the 128px sizing, surface background, and
     * abbreviation styling without relying on theme tokens.</p>
     *
     * @return this configurator
     */
    C profile();

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a {@link BaseAvatarConfigurator} to configure an existing {@link Avatar}.
     *
     * @param avatar the avatar to configure (not null)
     * @return a new {@link BaseAvatarConfigurator}
     */
    static BaseAvatarConfigurator configure(Avatar avatar) {
        return new DefaultAvatarConfigurator(avatar);
    }

    /**
     * Base (non-generic) {@link AvatarConfigurator}.
     */
    interface BaseAvatarConfigurator extends AvatarConfigurator<BaseAvatarConfigurator> {
    }
}

