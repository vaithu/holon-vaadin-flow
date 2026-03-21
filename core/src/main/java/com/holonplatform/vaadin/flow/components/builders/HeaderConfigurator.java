package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.function.Consumer;

public interface HeaderConfigurator<C extends HeaderConfigurator<C>> extends ComponentConfigurator<C>,
        HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C>, HasComponentsConfigurator<C> {

    C prefix(Component... components);

    C breadcrumb(BreadcrumbItem... items);

    C heading(String title, HeadingLevel level);

    C heading(String title);

    C headingFontSize(Font.Size fontSize);

    C headingFontWeight(Font.Weight fontWeight);

    C headingId(String id);

    C headingLineHeight(Font.LineHeight lineHeight);

    C headingTextColor(Color.Text textColor);

    C details(Component... components);

    C actions(Component... components);

    C close(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    Layout getRowLayout();

    Layout getColumnLayout();

    Tabs getTabs();

    C tabs(Tab... tabs);

    C tabs(Tabs tabs);

    static HeaderConfigurator.BaseHeaderConfigurator configure(Header header) {
        return new DefaultHeaderConfigurator(header);
    }

    interface BaseHeaderConfigurator extends HeaderConfigurator<HeaderConfigurator.BaseHeaderConfigurator> {

    }
}
