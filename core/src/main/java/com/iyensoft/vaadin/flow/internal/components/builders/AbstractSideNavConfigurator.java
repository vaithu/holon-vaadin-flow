package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavItemBuilder;
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
    extends AbstractComponentConfigurator<SideNav, C>
        implements SideNavConfigurator<C> {

    protected final SideNav sideNav;

    public AbstractSideNavConfigurator(SideNav sideNav) {
        super(sideNav);
        this.sideNav = sideNav;
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    @Override
    public C addItem(SideNavItem... items) {
        sideNav.addItem(items);
        return getConfigurator();
    }

    @Override
    public C addItemAsFirst(SideNavItem item) {
        sideNav.addItemAsFirst(item);
        return getConfigurator();
    }

    @Override
    public C addItemAtIndex(int index, SideNavItem item) {
        sideNav.addItemAtIndex(index, item);
        return getConfigurator();
    }

    @Override
    public C collapsible(boolean collapsible) {
        sideNav.setCollapsible(collapsible);
        return getConfigurator();
    }

    @Override
    public C expanded(boolean expanded) {
        sideNav.setExpanded(expanded);
        return getConfigurator();
    }

    @Override
    public C label(String label) {
        sideNav.setLabel(label);
        return getConfigurator();
    }

    @Override
    public List<SideNavItem> getItems() {
        return sideNav.getItems();
    }

    /* ---------------------------------------------
       Simple label filter (top-level only)
       --------------------------------------------- */
    @Override
    public void filter(String filter) {
        String f = (filter == null) ? "" : filter.toLowerCase();

        for (SideNavItem item : getItems()) {
            boolean visible = item.getLabel() != null &&
                    item.getLabel().toLowerCase().contains(f);

            item.setVisible(visible);
        }
    }

    /* ---------------------------------------------
       Recursive filter (parent expands when child matches)
       --------------------------------------------- */
    public void filterRecursive(String filter) {
        String f = (filter == null) ? "" : filter.toLowerCase();

        for (SideNavItem item : getItems()) {
            filterItemRecursive(item, f);
        }
    }

    protected boolean filterItemRecursive(SideNavItem item, String filter) {

        boolean labelMatches = item.getLabel() != null &&
                item.getLabel().toLowerCase().contains(filter);

        boolean childMatches = false;
        for (SideNavItem child : item.getItems()) {
            childMatches |= filterItemRecursive(child, filter);
        }

        boolean visible = labelMatches || childMatches;
        item.setVisible(visible);

        if (childMatches) {
            item.setExpanded(true);
        }

        return visible;
    }

    /* ---------------------------------------------
       Item Builder Factories
       --------------------------------------------- */

    @Override
    public SideNavItemBuilder withNavItem(String label) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, Class<? extends Component> view) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, view)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            Component prefixComponent
    ) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, view, prefixComponent)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters params
    ) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, view, params)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters params,
            Component prefixComponent
    ) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, view, params, prefixComponent)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, String path) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, path)
        );
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, String path, Component prefixComponent) {
        return new DefaultSideNavItemBuilder(
                this,
                new SideNavItem(label, path, prefixComponent)
        );
    }
}