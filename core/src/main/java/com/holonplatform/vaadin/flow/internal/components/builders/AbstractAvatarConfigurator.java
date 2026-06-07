package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.AvatarColor;
import com.holonplatform.vaadin.flow.components.builders.AvatarConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.util.Optional;

/**
 * Base {@link AvatarConfigurator} implementation.
 *
 * <p>Extends {@link AbstractLocalizableComponentConfigurator} to inherit deferred-localization
 * support ({@code isDeferredLocalizationEnabled()}, {@code withAttachListener(...)}).
 *
 * @param <C> Concrete configurator type
 * @since 10.0.0
 */
public abstract class AbstractAvatarConfigurator<C extends AvatarConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Avatar, C>
        implements AvatarConfigurator<C> {

    public AbstractAvatarConfigurator(Avatar component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // Name
    // -----------------------------------------------------------------------

    @Override
    public C name(String name) {
        getComponent().setName(name);
        return getConfigurator();
    }

    @Override
    public C name(LabelBuilder<?> label) {
        return name(label != null ? label.build().getText() : null);
    }

    @Override
    public C name(Localizable name) {
        if (name == null) {
            getComponent().setName(null);
            return getConfigurator();
        }
        final String defaultValue = name.getMessage();
        if (isDeferredLocalizationEnabled()) {
            getComponent().setName(defaultValue);
            return withAttachListener(event -> {
                if (event.isInitialAttach()) {
                    LocalizationProvider.localize(name).ifPresent(getComponent()::setName);
                }
            });
        }
        getComponent().setName(LocalizationProvider.localize(name).orElse(defaultValue));
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Abbreviation
    // -----------------------------------------------------------------------

    @Override
    public C abbreviation(String abbreviation) {
        getComponent().setAbbreviation(abbreviation);
        return getConfigurator();
    }

    @Override
    public C abbreviation(Localizable abbreviation) {
        if (abbreviation == null) {
            getComponent().setAbbreviation(null);
            return getConfigurator();
        }
        final String defaultValue = abbreviation.getMessage();
        if (isDeferredLocalizationEnabled()) {
            getComponent().setAbbreviation(defaultValue);
            return withAttachListener(event -> {
                if (event.isInitialAttach()) {
                    LocalizationProvider.localize(abbreviation).ifPresent(getComponent()::setAbbreviation);
                }
            });
        }
        getComponent().setAbbreviation(LocalizationProvider.localize(abbreviation).orElse(defaultValue));
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Image
    // -----------------------------------------------------------------------

    @Override
    public C image(String imageUrl) {
        getComponent().setImage(imageUrl);
        return getConfigurator();
    }

    @Override
    public C imageHandler(DownloadHandler handler) {
        getComponent().setImageHandler(handler);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Color
    // -----------------------------------------------------------------------

    @Override
    public C colorIndex(int colorIndex) {
        getComponent().setColorIndex(colorIndex);
        return getConfigurator();
    }

    @Override
    public C colorIndex(AvatarColor color) {
        getComponent().setColorIndex(color.getIndex());
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Variant (IconBadge-style background)
    // -----------------------------------------------------------------------

    @Override
    public C variant(Alert.Variant variant) {
        // Base marker class — CSS targets vaadin-avatar.avatar--badge-*
        getComponent().addClassName("avatar--badge");
        if (variant != null) {
            String cls = switch (variant) {
                case DESTRUCTIVE -> "avatar--badge-destructive";
                case WARNING     -> "avatar--badge-warning";
                case SUCCESS     -> "avatar--badge-success";
                case INFO        -> "avatar--badge-info";
                default          -> "avatar--badge-default";
            };
            getComponent().addClassName(cls);
        }
        return getConfigurator();
    }

    @Override
    public C profile() {
        getComponent().addClassName("avatar--profile-xl");
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Accessibility – aria-label / aria-labelledby
    // -----------------------------------------------------------------------

    @Override
    public C ariaLabel(String ariaLabel) {
        getComponent().getElement().setAttribute("aria-label", ariaLabel != null ? ariaLabel : "");
        return getConfigurator();
    }

    @Override
    public C ariaLabelledBy(String ariaLabelledBy) {
        getComponent().getElement().setAttribute("aria-labelledby", ariaLabelledBy != null ? ariaLabelledBy : "");
        return getConfigurator();
    }

    @Override
    public C ariaLabel(Localizable ariaLabel) {
        final String defaultValue = (ariaLabel != null && ariaLabel.getMessage() != null)
                ? ariaLabel.getMessage() : "";
        if (ariaLabel == null) {
            return ariaLabel(defaultValue);
        }
        if (isDeferredLocalizationEnabled()) {
            ariaLabel(defaultValue);
            return withAttachListener(event -> {
                if (event.isInitialAttach()) {
                    LocalizationProvider.localize(ariaLabel).ifPresent(this::ariaLabel);
                }
            });
        }
        return ariaLabel(LocalizationProvider.localize(ariaLabel).orElse(defaultValue));
    }

    // -----------------------------------------------------------------------
    // Theme variants
    // -----------------------------------------------------------------------

    @Override
    public C withThemeVariants(AvatarVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // AbstractComponentConfigurator hooks
    // -----------------------------------------------------------------------

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }
}
