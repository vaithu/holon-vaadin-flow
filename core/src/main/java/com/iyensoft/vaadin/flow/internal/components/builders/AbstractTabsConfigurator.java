package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.TabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.Optional;


public abstract class AbstractTabsConfigurator<C extends TabsConfigurator<C>>
        extends AbstractComponentConfigurator<Tabs, C>
        implements TabsConfigurator<C> {


    public AbstractTabsConfigurator(Tabs component) {
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

    // ---------------------------------------------------------------------
    // Tabs management
    // ---------------------------------------------------------------------

    @Override
    public C add(Tab... tabsToAdd) {
        getComponent().add(tabsToAdd);
        return getConfigurator();
    }

    @Override
    public C addTabAsFirst(Tab tab) {
        getComponent().addTabAsFirst(tab);
        return getConfigurator();
    }

    @Override
    public C addTabAtIndex(int index, Tab tab) {
        getComponent().addTabAtIndex(index, tab);
        return getConfigurator();
    }

    @Override
    public C remove(Tab... tabsToRemove) {
        getComponent().remove(tabsToRemove);
        return getConfigurator();
    }

    @Override
    public C removeAll() {
        getComponent().removeAll();
        return getConfigurator();
    }

    @Override
    public C replace(Tab oldTab, Tab newTab) {
        getComponent().replace(oldTab, newTab);
        return getConfigurator();
    }

    // ---------------------------------------------------------------------
    // Behavior & state
    // ---------------------------------------------------------------------

    @Override
    public C autoselect(boolean autoselect) {
        getComponent().setAutoselect(autoselect);
        return getConfigurator();
    }

    @Override
    public C flexGrowForEnclosedTabs(double flexGrow) {
        getComponent().setFlexGrowForEnclosedTabs(flexGrow);
        return getConfigurator();
    }

    @Override
    public C orientation(Tabs.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    // ---------------------------------------------------------------------
    // Selection
    // ---------------------------------------------------------------------

    @Override
    public C selectedIndex(int selectedIndex) {
        getComponent().setSelectedIndex(selectedIndex);
        return getConfigurator();
    }

    @Override
    public C selectedTab(Tab selectedTab) {
        getComponent().setSelectedTab(selectedTab);
        return getConfigurator();
    }

    @Override
    public C withThemeVariants(TabsVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    @Override
    public C addSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }

    private Span createBadge(int value) {
        return UIUtils.Badge.createBadge(value);
    }

    @Override
    public C withTab(String label, int counter, Component component) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        return withTab(tab, component);
    }

    @Override
    public C withTab(String label, int counter, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withTab(tab, component);
    }

    @Override
    public C withTab(String label, Icon icon, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(icon, new Span(label));
        tab.addThemeVariants(tabVariant);
        return withTab(tab, component);
    }

    @Override
    public C withTab(String label, Icon icon, Component component) {
        Tab tab = new Tab(icon, new Span(label));
        return withTab(tab, component);
    }

    @Override
    public C withTab(String label, Component component) {
        Tab tab = new Tab(label);
        return withTab(tab, component);
    }

    @Override
    public C withTab(Tab tab, Component component) {
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C add(String... tabs) {
        for (String tab : tabs) {
            getComponent().add(new Tab(tab));
        }
        return getConfigurator();
    }

    @Override
    public C withTab(String... tabs) {
        return add(tabs);
    }
}
