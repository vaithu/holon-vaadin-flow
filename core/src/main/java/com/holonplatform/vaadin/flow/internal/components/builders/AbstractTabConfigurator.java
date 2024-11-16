package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.builders.TabConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractTabConfigurator<C extends TabConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Tab, C> implements TabConfigurator<C> {

    protected final DefaultHasTooltipConfigurator<Tab> tooltipConfigurator;

    private final Map<Tab, Component> contentMap = new HashMap<>();
    private VerticalLayout layout = null;
    private Div div = null;

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

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
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
        getComponent().add(new Span(label));
        return getConfigurator();
    }

    /**
     * Helper method for creating a badge.
     */
    private Span createBadge(int value) {
        Span badge = new Span(String.valueOf(value));
        badge.getElement().getThemeList().add("badge small contrast");
        badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
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


    /**
     * Sets the tooltip text using a {@link Localizable} message.
     * <p>
     * The tooltip is set using the <vaadin-tooltip slot="tooltip"> attribute. Browsers typically use the title to show a tooltip
     * when hovering an element
     * <p>
     *
     * @param tooltip Localizable tooltip message (may be getConfigurator())
     * @return this
     * @see LocalizationProvider
     */
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

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
