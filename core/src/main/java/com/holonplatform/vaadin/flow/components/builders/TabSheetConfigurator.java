package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabSheetConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;

public interface TabSheetConfigurator<C extends TabSheetConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabSheetVariant, C>,
        HasSizeConfigurator<C>,HasEnabledConfigurator<C>,DeferrableLocalizationConfigurator<C> {

    C withTab(Component tabContent,
              Component content);

    C withTab(Tab tab,
              Component content);

    C withTab(Tab tab,
              Component content,
              int position);

    C withTab(String tabText,
              Component content);

    C bordered();

    C prefixComponent(Component component);
    C suffixComponent(Component component);


    C withSelectedChangeListener(ComponentEventListener<TabSheet.SelectedChangeEvent> listener);

      static BaseTabSheetConfigurator configure(TabSheet tabSheet) {
        return new DefaultTabSheetConfigurator(tabSheet);
    }

    interface BaseTabSheetConfigurator extends TabSheetConfigurator<BaseTabSheetConfigurator> {

    }


}
