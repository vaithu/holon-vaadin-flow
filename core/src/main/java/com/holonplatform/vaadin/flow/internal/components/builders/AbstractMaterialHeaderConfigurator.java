package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MaterialHeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.MaterialHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.ListItem;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/** Base implementation for {@link MaterialHeaderConfigurator}. */
public abstract class AbstractMaterialHeaderConfigurator<C extends MaterialHeaderConfigurator<C>>
        extends AbstractComponentConfigurator<MaterialHeader, C>
        implements MaterialHeaderConfigurator<C> {

    protected AbstractMaterialHeaderConfigurator(MaterialHeader component) {
        super(component);
    }

    @Override protected Optional<HasSize> hasSize() { return Optional.of(getComponent()); }
    @Override protected Optional<HasStyle> hasStyle() { return Optional.of(getComponent()); }
    @Override protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }
    @Override protected Optional<HasTooltip> hasTooltip() { return Optional.empty(); }

    @Override public C variant(MaterialHeader.Variant variant) { getComponent().setVariant(variant); return getConfigurator(); }
    @Override public C headline(Component headline) {getComponent().setHeadline(headline); return getConfigurator(); }
    @Override public C headline(String headline) {getComponent().setHeadline(headline); return getConfigurator(); }
    @Override public C subtitle(Component subtitle) { getComponent().setSubtitle(subtitle); return getConfigurator(); }
    @Override public C breadcrumb(Component breadcrumb) { getComponent().setBreadcrumb(breadcrumb); return getConfigurator(); }
    @Override public C breadcrumb(Breadcrumb breadcrumb) { getComponent().setBreadcrumb(breadcrumb); return getConfigurator(); }
    @Override public C breadcrumb(ListItem... items) { getComponent().setBreadcrumb(items); return getConfigurator(); }
    @Override public C media(Component media) { getComponent().setMedia(media); return getConfigurator(); }
    @Override public C details(Component... components) { getComponent().setDetails(components); return getConfigurator(); }
    @Override public C tags(Component... components) { getComponent().setTags(components); return getConfigurator(); }
    @Override public C leading(Component... components) { getComponent().addToLeading(components); return getConfigurator(); }
    @Override public C actions(Component... components) { getComponent().addToTrailing(components); return getConfigurator(); }
    @Override public C primaryAction(Component component) { getComponent().setPrimaryAction(component); return getConfigurator(); }
    @Override public C secondaryAction(Component... components) { getComponent().addSecondaryAction(components); return getConfigurator(); }
    @Override public C overflowAction(String label, Runnable action) { getComponent().addOverflowAction(label, action); return getConfigurator(); }
    @Override public C responsiveAction(Component desktopComponent, String label, Runnable action) { getComponent().addResponsiveAction(desktopComponent, label, action); return getConfigurator(); }
    @Override public C viewMode(ViewMode viewMode) { getComponent().setViewMode(viewMode); return getConfigurator(); }
    @Override public C responsiveAction(ViewMode viewMode, Component desktopComponent, String label, Runnable action) { getComponent().addResponsiveAction(viewMode, desktopComponent, label, action); return getConfigurator(); }
}
