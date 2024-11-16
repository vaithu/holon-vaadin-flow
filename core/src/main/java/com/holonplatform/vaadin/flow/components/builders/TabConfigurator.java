package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;

public interface TabConfigurator<C extends TabConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C>,HasTooltipConfigurator<C>,DeferrableLocalizationConfigurator<C> {

    C flexGrow(double flexGrow);

    C label(String label);

    C selected(boolean selected);

    C icon(VaadinIcon icon);

    C icon(Icon icon);

    C span(String label);

    C badge(Badge badge);

    C badge(int value);

    C iconOnTop();

    default C add(String label, VaadinIcon icon) {
        icon(icon.create());
        return span(label);
    }

    default C add(String label, Icon icon) {
        icon(icon);
        return span(label);
    }



    C componentAsFirst(Component component);

    C componentAtIndex(int index, Component component);

    static BaseTabConfigurator configure(Tab tab) {
        return new DefaultTabConfigurator(tab);
    }

    interface BaseTabConfigurator extends TabConfigurator<BaseTabConfigurator> {

    }


}
