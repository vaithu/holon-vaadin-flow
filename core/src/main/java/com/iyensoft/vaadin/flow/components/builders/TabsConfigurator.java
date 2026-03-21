package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultTabsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

public interface TabsConfigurator<C extends TabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>,HasEnabledConfigurator<C> {

    // ---------------------------------------------------------------------
    // Tabs management (same names as Tabs)
    // ---------------------------------------------------------------------

    /**
     * Adds the given tabs to the component.
     * Original: Tabs#add(Tab...).
     */
    C add(Tab... tabs);
    C add(String... tabs);

    /**
     * Adds the given tab as the first child of this component.
     * Original: Tabs#addTabAsFirst(Tab).
     */
    C addTabAsFirst(Tab tab);

    /**
     * Adds the given tab as child of this component at the specific index.
     * Original: Tabs#addTabAtIndex(int, Tab).
     */
    C addTabAtIndex(int index, Tab tab);

    /**
     * Removes the given child tabs from this component.
     * Original: Tabs#remove(Tab...).
     */
    C remove(Tab... tabs);

    /**
     * Removes all tabs from this component.
     * Original: Tabs#removeAll().
     */
    C removeAll();

    /**
     * Replaces the tab in the container with another one without changing position.
     * Original: Tabs#replace(Tab, Tab).
     */
    C replace(Tab oldTab, Tab newTab);

    // ---------------------------------------------------------------------
    // Behavior & state (no "set" prefix)
    // ---------------------------------------------------------------------

    /**
     * Specify that the tabs should be automatically selected.
     * Original: Tabs#setAutoselect(boolean).
     */
    C autoselect(boolean autoselect);

    /**
     * Sets the flex grow property of all enclosed tabs.
     * Original: Tabs#setFlexGrowForEnclosedTabs(double).
     */
    C flexGrowForEnclosedTabs(double flexGrow);

    /**
     * Sets the orientation of this tab sheet.
     * Original: Tabs#setOrientation(Tabs.Orientation).
     */
    C orientation(Tabs.Orientation orientation);

    // ---------------------------------------------------------------------
    // Selection (no "set" prefix)
    // ---------------------------------------------------------------------

    /**
     * Selects a tab based on its zero-based index.
     * Original: Tabs#setSelectedIndex(int).
     */
    C selectedIndex(int selectedIndex);

    /**
     * Selects the given tab.
     * Original: Tabs#setSelectedTab(Tab).
     */
    C selectedTab(Tab selectedTab);

    C addSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

    C withTab(String label, Component component);
    C withTab(String label, Icon icon, Component component);
    C withTab(String label, int counter, Component component);
    C withTab(String label, int counter, TabVariant tabVariant, Component component);
    C withTab(String label, Icon icon, TabVariant tabVariant, Component component);
    C withTab(Tab tab, Component component);
    C withTab(String... tabs);

    static BaseTabsConfigurator configure(Tabs tabs) {
        return new DefaultTabsConfigurator(tabs);
    }

    interface BaseTabsConfigurator extends TabsConfigurator<BaseTabsConfigurator> {

    }
}
