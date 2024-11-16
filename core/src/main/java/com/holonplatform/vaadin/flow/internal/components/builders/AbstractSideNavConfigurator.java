package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSideNavConfigurator<C extends SideNavConfigurator<C>>
        extends AbstractComponentConfigurator<SideNav, C> implements SideNavConfigurator<C> {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractSideNavConfigurator(SideNav component) {
        super(component);
    }

    @Override
    public C addItem(SideNavItem... items) {
        getComponent().addItem(items);
        return getConfigurator();
    }

    @Override
    public C addItemAsFirst(SideNavItem item) {
        getComponent().addItemAsFirst(item);
        return getConfigurator();
    }

    @Override
    public C addItemAtIndex(int index, SideNavItem item) {
        getComponent().addItemAtIndex(index, item);
        return getConfigurator();
    }

    @Override
    public C collapsible(boolean collapsible) {
        getComponent().setCollapsible(collapsible);
        return getConfigurator();
    }

    @Override
    public C expanded(boolean expanded) {
        getComponent().setExpanded(expanded);
        return getConfigurator();
    }

    @Override
    public C label(String label) {
        getComponent().setLabel(label);
        return getConfigurator();
    }

    @Override
    public List<SideNavItem> getItems() {
        return getComponent().getItems();
    }

    @Override
    public void filter(String filter) {
        getItems().forEach(naviItem -> {
            boolean matches = naviItem.getLabel().toLowerCase().contains(filter.toLowerCase());
            naviItem.setVisible(matches);
        });
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).label(label);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, Class<? extends Component> view) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).label(label).path(view);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).label(label).path(view).prefixComponent(prefixComponent);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).withSubNavItem(label,view,routeParameters);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters, Component prefixComponent) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).withSubNavItem(label,view,routeParameters,prefixComponent);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, String path) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).label(label).path(path);
    }

    @Override
    public SideNavItemBuilder<C> withNavItem(String label, String path, Component prefixComponent) {
        return new DefaultSideNavItemConfigurator<>(getConfigurator()).label(label).path(path).prefixComponent(prefixComponent);
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
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
