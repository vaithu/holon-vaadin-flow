package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("all")
public interface HeaderConfigurator<C extends HeaderConfigurator<C>> extends ComponentConfigurator<C>,
        HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C> {

    C prefix(Component... components);

    default C heading(Component component) {
        return heading(component, HeadingLevel.NONE);
    }

    C heading(Component component, HeadingLevel headingLevel);

    default C heading(String title) {
        return heading(title, HeadingLevel.H2);
    }

    C heading(String title, HeadingLevel headingLevel);

    default C hidePrefixOnDesktop() {
        return hidePrefixOnDesktop(true);
    }

    C hidePrefixOnDesktop(boolean hidePrefixOnDesktop);

    C breadcrumb(BreadcrumbItem... items);

    C details(Component... components);

    C actions(Component... components);

    C edit(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C options(MenuBar menuBar);
    C options(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C newBtn(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C refresh(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C close(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    Optional<Tabs> getTabs();

    C tabs(Tab... tabs);

    C tabs(Tabs tabs);

    C withoutBorder();

    Layout getColumnLayout();
    Layout getRowLayout();

    static HeaderConfigurator.BaseHeaderConfigurator configure(Header header) {
        return new DefaultHeaderConfigurator(header);
    }

    interface BaseHeaderConfigurator extends HeaderConfigurator<HeaderConfigurator.BaseHeaderConfigurator> {

    }
}
