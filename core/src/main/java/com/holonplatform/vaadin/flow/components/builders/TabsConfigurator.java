package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabsConfigurator;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ScrollOptions;
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

    C add(String... tabs);

    C scrollIntoView();
    C scrollIntoView(ScrollOptions scrollOptions);


    C autoSelect(boolean autoSelect);

    C flexGrowForEnclosedTabs(double flexGrow);

    C orientation(Tabs.Orientation orientation);

    C selectedIndex(int selectedIndex);

    C selectedTab(Tab selectedTab);

    C withSelectedChangeEvent(ComponentEventListener<Tabs.SelectedChangeEvent> listener);


      static BaseTabsConfigurator configure(Tabs tabs) {
        return new DefaultTabsConfigurator(tabs);
    }

    interface BaseTabsConfigurator extends TabsConfigurator<BaseTabsConfigurator> {

    }


}
