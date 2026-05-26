package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AvatarGroupConfigurator;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link AvatarGroupConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 * @since 10.0.0
 */
public abstract class AbstractAvatarGroupConfigurator<C extends AvatarGroupConfigurator<C>>
        extends AbstractComponentConfigurator<AvatarGroup, C>
        implements AvatarGroupConfigurator<C> {

    public AbstractAvatarGroupConfigurator(AvatarGroup component) {
        super(component);
    }

    @Override
    public C add(AvatarGroup.AvatarGroupItem... items) {
        getComponent().add(items);
        return getConfigurator();
    }

    @Override
    public C maxItemsVisible(int max) {
        getComponent().setMaxItemsVisible(max);
        return getConfigurator();
    }

    @Override
    public C i18n(AvatarGroup.AvatarGroupI18n i18n) {
        getComponent().setI18n(i18n);
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

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}

