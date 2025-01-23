package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.Initializer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.function.Function;
import java.util.function.Supplier;


public interface AutoTabsConfigurator<C extends AutoTabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>,HasEnabledConfigurator<C> {


    C withTab(String label, Initializer<Component> component);
    C withTab(Icon icon,String label, Initializer<Component> component);
    C withTab(SvgIcon icon, String label, Initializer<Component> component);
    C withTab(Tab tab, Initializer<Component> component);

    C withTab(String label, Component component);
    C withTab(Icon icon,String label, Component component);
    C withTab(SvgIcon icon, String label, Component component);
    C withTab(Tab tab, Component component);

    C container(VerticalLayout layout);

    C container(Div div);

    C useCache(boolean cache);

    C autoShowTab();

    C showTab(String label);

    C showTab(Tab tab);

    Component getContent();

    C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

    C withSelectedChangeEvent(ComponentEventListener<Tabs.SelectedChangeEvent> listener, Supplier<Component> supplier);
    C withSelectedChangeEvent(Function<ComponentEventListener<Tabs.SelectedChangeEvent>,Component> contentSet);
}
