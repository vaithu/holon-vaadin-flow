package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MaterialAppBarConfigurator;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.holonplatform.vaadin.flow.components.support.ViewMode;

import java.util.Optional;

/** Base implementation for {@link MaterialAppBarConfigurator}. */
public abstract class AbstractMaterialAppBarConfigurator<C extends MaterialAppBarConfigurator<C>>
        extends AbstractComponentConfigurator<MaterialAppBar, C>
        implements MaterialAppBarConfigurator<C> {

    protected AbstractMaterialAppBarConfigurator(MaterialAppBar component) {
        super(component);
    }

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

    @Override
    public C variant(MaterialAppBar.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C headline(Component headline) {
        getComponent().setHeadline(headline);
        return getConfigurator();
    }

    @Override
    public C subtitle(Component subtitle) {
        getComponent().setSubtitle(subtitle);
        return getConfigurator();
    }

    @Override
    public C leading(Component... components) {
        if (components != null) {
            getComponent().addToLeading(components);
        }
        return getConfigurator();
    }

    @Override
    public C actions(Component... components) {
        if (components != null) {
            getComponent().addToTrailing(components);
        }
        return getConfigurator();
    }

    @Override
    public C overflowAction(String label, Runnable action) {
        getComponent().addOverflowAction(label, action);
        return getConfigurator();
    }

    @Override
    public C responsiveAction(Component desktopComponent, String label, Runnable action) {
        getComponent().addResponsiveAction(desktopComponent, label, action);
        return getConfigurator();
    }

    @Override
    public C viewMode(ViewMode viewMode) {
        getComponent().setViewMode(viewMode);
        return getConfigurator();
    }

    @Override
    public C responsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action) {
        getComponent().addResponsiveAction(viewMode, desktopComponent, label, action);
        return getConfigurator();
    }

    @Override
    public C centered(boolean centered) {
        getComponent().setCentered(centered);
        return getConfigurator();
    }

    @Override
    public C search(boolean search) {
        getComponent().setSearch(search);
        return getConfigurator();
    }

    @Override
    public C scrolled(boolean scrolled) {
        getComponent().setScrolled(scrolled);
        return getConfigurator();
    }
}