package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultLazyTabsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.function.Supplier;


public interface LazyTabsConfigurator<C extends LazyTabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C> {

    C enableCache(boolean enableCache);

    C cacheEnabled();

    C scrollIntoView();
    C scrollIntoView(ScrollOptions scrollOptions);


    C autoSelect(boolean autoSelect);

    C flexGrowForEnclosedTabs(double flexGrow);

    C orientation(Tabs.Orientation orientation);

    C selectedIndex(int selectedIndex);

    C selectedTab(Tab tab);
    C selectedTab(String tabTitle);

    C withSelectedChangeEvent(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

    C withEagerTab(String label, Component component);
    C withEagerTab(String label, Icon icon, Component component);
    C withEagerTab(String label, int counter, Component component);
    C withEagerTab(String label, int counter, TabVariant tabVariant, Component component);
    C withEagerTab(String label, Icon icon, TabVariant tabVariant, Component component);
    C withEagerTab(Tab tab, Component component);

    C withLazyTab(String label, Supplier<Component> factory);
    C withLazyTab(String label, Icon icon,Supplier<Component> factory);
    C withLazyTab(String label, int counter,Supplier<Component> factory);
    C withLazyTab(String label, Icon icon,TabVariant tabVariant,Supplier<Component> factory);
    C withLazyTab(String label, int counter,TabVariant tabVariant,Supplier<Component> factory);

    C withLazyTab(Tab tab, Supplier<Component> factory);

    VerticalLayout getContentContainer();

    Tab getSelectedTab();
    Tabs getTabs();

    static BaseTabsConfigurator configure(VerticalLayout layout) {
        return new DefaultLazyTabsConfigurator(layout);
    }

    interface BaseTabsConfigurator extends LazyTabsConfigurator<BaseTabsConfigurator> {

    }


}
