package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Initializer;
import com.holonplatform.vaadin.flow.components.builders.TabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractTabsConfigurator<C extends TabsConfigurator<C>>
        extends AbstractComponentConfigurator<Tabs, C> implements TabsConfigurator<C> {

    /**
     * Contents of the TabSheet.
     */
    private final Map<Tab, Component> contentsTab = new LinkedHashMap<>();
    private final Map<Tab, Initializer<Component>> lazyContentsTab = new LinkedHashMap<>();

    private boolean isCacheEnabled;
    // display area
    private VerticalLayout verticalLayout;
    private Div div;

    /**
     * Constructor.
     *
     * @param component The component instance (not getConfigurator())
     */
    public AbstractTabsConfigurator(Tabs component) {
        super(component);
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
    public C replace(Tab oldTab, Tab newTab) {
        getComponent().replace(oldTab, newTab);
        return getConfigurator();
    }

    @Override
    public C add(Tab... tabs) {
        getComponent().add(tabs);
        return getConfigurator();
    }

    @Override
    public C add(String... tabLabels) {
        for (String tab : tabLabels) {
            getComponent().add(new Tab(tab));
        }
        return getConfigurator();
    }

    @Override
    public C scrollIntoView() {
        getComponent().scrollIntoView();
        return getConfigurator();
    }

    @Override
    public C scrollIntoView(ScrollOptions scrollOptions) {
        getComponent().scrollIntoView(scrollOptions);
        return getConfigurator();
    }

    @Override
    public C autoSelect(boolean autoSelect) {
        getComponent().setAutoselect(autoSelect);
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
    public C withSelectedChangeEvent(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public C withThemeVariants(TabsVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
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
