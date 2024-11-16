package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Initializer;
import com.holonplatform.vaadin.flow.components.builders.TabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
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
    public C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }


    @Override
    public C autoShowTab() {
        if (this.verticalLayout == null && this.div == null) {
            throw new RuntimeException("No container specified. Use container() method before you call this method");
        }
        Tab firstTab = getFirstTab();
        setContent(firstTab);
        getComponent().addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));
        return getConfigurator();
    }

    private Tab getFirstTab() {
        // Populate the map with some data// Get the first entry from the LinkedHashMap
//        Map.Entry<Tab, Component> firstEntry = contentsTab.entrySet().iterator().next();
        Tab firstTab = isLazyLoading() ? lazyContentsTab.entrySet().iterator().next().getKey()
                : contentsTab.entrySet().iterator().next().getKey();
        return firstTab;
    }

    @Override
    public C showTab(String label) {
        if (isLazyLoading()) {
            Map.Entry<Tab, Initializer<Component>> selectedTab = lazyContentsTab.entrySet().stream()
                    .filter(tabComponentEntry -> tabComponentEntry.getKey().getLabel().equalsIgnoreCase(label))
                    .findFirst()
                    .orElseThrow();
            setContent(selectedTab.getKey());
        } else {
            Map.Entry<Tab, Component> selectedTab = contentsTab.entrySet().stream()
                    .filter(tabComponentEntry -> tabComponentEntry.getKey().getLabel().equalsIgnoreCase(label))
                    .findFirst()
                    .orElseThrow();
            setContent(selectedTab.getKey());
        }
        return getConfigurator();
    }

    @Override
    public C showTab(Tab tab) {
        setContent(tab);
        return getConfigurator();
    }

    private boolean isLazyLoading() {
        return !lazyContentsTab.entrySet().isEmpty();
    }

    private void setContent(Tab selectedTab) {
        if (this.verticalLayout != null) {
            addToVerticalLayout(selectedTab);
        } else if (this.div != null) {
            addToDiv(selectedTab);
        }

    }

    private void addToVerticalLayout(Tab tab) {
        this.verticalLayout.removeAll();
        if (isLazyLoading()) {
            this.verticalLayout.add(lazyContentsTab.get(tab).get());
        } else {
            this.verticalLayout.add(contentsTab.get(tab));
        }
    }

    private void addToDiv(Tab tab) {
        this.div.removeAll();
        if (isLazyLoading()) {
            this.div.add(lazyContentsTab.get(tab).get());
        } else {
            this.div.add(contentsTab.get(tab));
        }
    }
    
    

    @Override
    public C withTab(String label, Initializer<Component> component) {
        return withTab(new Tab(label), component);
    }

    @Override
    public C withTab(Icon icon, String label, Initializer<Component> component) {
        return withTab(new Tab(icon, new Span(label)), component);
    }

    @Override
    public C withTab(SvgIcon icon, String label, Initializer<Component> component) {
        return withTab(new Tab(icon, new Span(label)), component);
    }

    @Override
    public C withTab(Tab tab, Initializer<Component> component) {
        lazyContentsTab.put(tab, component);
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C container(VerticalLayout layout) {
        this.verticalLayout = layout;
        return getConfigurator();
    }

    @Override
    public C container(Div div) {
        this.div = div;
        return getConfigurator();
    }

    @Override
    public Component getContent() {
        return this.verticalLayout == null ? this.div : null;
    }
    
    /* @Override
    public C autoShowTab() {
        if (!contents.isEmpty()) {
            Map.Entry<Tab, Component> firstEntry = this.contents.entrySet().iterator().next();
            display.add(firstEntry.getFormValue());
        } else {
            throw new IllegalArgumentException("No component found. Use method withTab() before using this autoShowTab");
        }

        getComponent().addSelectedChangeListener(event -> {
            if (!contents.isEmpty()) {
                // remove old contents, if there was a previously selected tab
                if (event.getPreviousTab() != getConfigurator()) {
                    display.remove(this.contents.get(event.getPreviousTab()));
                }
                // add new contents, if there is a currently selected tab
                if (event.getSelectedTab() != getConfigurator()) {
                    display.add(this.contents.get(event.getPreviousTab()));
                }
            }
        });

        return getConfigurator();
    }*/

   /* @Override
    public Div getContent() {
        return display;
    }

    @Override
    public C add(String label, Component component) {
        Tab tab = new Tab(label);
        contents.put(tab, component);
        getComponent().add(tab);
        return getConfigurator();
    }*/

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
