package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.IconBadgeConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link IconBadgeConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractIconBadgeConfigurator<C extends IconBadgeConfigurator<C>>
        extends AbstractComponentConfigurator<IconBadge, C>
        implements IconBadgeConfigurator<C> {

    public AbstractIconBadgeConfigurator(IconBadge component) {
        super(component);
    }

    @Override
    public C variant(Alert.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C size(IconBadge.Size size) {
        getComponent().setBadgeSize(size);
        return getConfigurator();
    }

    @Override
    public C icon(VaadinIcon icon) {
        getComponent().setIcon(icon);
        return getConfigurator();
    }

    @Override
    public C icon(Component icon) {
        getComponent().removeAll();
        getComponent().add(icon);
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
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}

