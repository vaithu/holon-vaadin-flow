package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.TabConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;

import java.util.Optional;

public abstract class AbstractTabConfigurator<C extends TabConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Tab, C> implements TabConfigurator<C> {

    protected final DefaultHasTooltipConfigurator<Tab> tooltipConfigurator;

    /**
     * Constructor.
     *
     * @param component The component instance (not getConfigurator())
     */
    public AbstractTabConfigurator(Tab component) {
        super(component);
        this.tooltipConfigurator = new DefaultHasTooltipConfigurator<>(component, tooltip -> {
            component.setTooltipText(tooltip);
        }, this);
    }

    @Override
    public C flexGrow(double flexGrow) {
        getComponent().setFlexGrow(flexGrow);
        return getConfigurator();
    }

    @Override
    public C label(String label) {
        getComponent().setLabel(label);
        return getConfigurator();
    }

    @Override
    public C selected(boolean selected) {
        getComponent().setSelected(selected);
        return getConfigurator();
    }

    @Override
    public C icon(VaadinIcon icon) {
        getComponent().add(icon.create());
        return getConfigurator();
    }

    @Override
    public C icon(Icon icon) {
        getComponent().addComponentAsFirst(icon);
        return getConfigurator();
    }

    @Override
    public C withThemeVariants(TabVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    @Override
    public C badge(Badge badge) {
        getComponent().add(badge);
        return getConfigurator();
    }

    @Override
    public C span(String label) {
        getComponent().add(Components.span().text(label).build());
        return getConfigurator();
    }

    /**
     * Helper method for creating a badge.
     */
    private Span createBadge(int value) {
        Span badge = Components.span().text(String.valueOf(value)).styleName("tab__badge").build();
        badge.getElement().getThemeList().add("badge small contrast");
        return badge;
    }

    @Override
    public C badge(int value) {
        getComponent().add(createBadge(value));
        return getConfigurator();
    }

    @Override
    public C iconOnTop() {
        getComponent().addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        return getConfigurator();
    }

    @Override
    public C componentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C componentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
        return getConfigurator();
    }


    @Override
    public C tooltip(Localizable tooltip) {
        tooltipConfigurator.tooltip(tooltip);
        return getConfigurator();
    }

    @Override
    public C tooltipText(String text) {
        tooltipConfigurator.tooltipText(text);
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
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
