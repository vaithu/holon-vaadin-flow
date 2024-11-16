package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.Initializer;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;


public interface TabsConfigurator<C extends TabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>,HasEnabledConfigurator<C> {

    C addTabAsFirst(Tab tab);

    C addTabAtIndex(int index,
                    Tab tab);

    C replace(Tab oldTab,
              Tab newTab);

    C add(Tab... tabs);

    C scrollIntoView();
    C scrollIntoView(ScrollOptions scrollOptions);


    C autoSelect(boolean autoSelect);

    C flexGrowForEnclosedTabs(double flexGrow);

    C orientation(Tabs.Orientation orientation);

    C selectedIndex(int selectedIndex);

    C selectedTab(Tab selectedTab);

    C withTab(String label, Initializer<Component> component);
    C withTab(Icon icon,String label, Initializer<Component> component);
    C withTab(SvgIcon icon, String label, Initializer<Component> component);
    C withTab(Tab tab, Initializer<Component> component);

    C container(VerticalLayout layout);

    C container(Div div);

    C autoShowTab();

    C showTab(String label);

    C showTab(Tab tab);

    Component getContent();

    C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

      static BaseTabsConfigurator configure(Tabs tabs) {
        return new DefaultTabsConfigurator(tabs);
    }

    interface BaseTabsConfigurator extends TabsConfigurator<BaseTabsConfigurator> {

    }


}
