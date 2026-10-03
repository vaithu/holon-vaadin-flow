package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultBreadcrumbConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.VaadinIcon;

public interface BreadcrumbConfigurator<C extends BreadcrumbConfigurator<C>> extends ComponentConfigurator<C> {

    C add(ListItem... items);

    C add(BreadcrumbsItem item);

    C clear();

    C item(String text, Class<? extends Component> navigationTarget);

    C item(Component content);

    C separator();

    C separator(VaadinIcon icon);

    C separator(Component customContent);

    C page(String text);

    C page(Localizable localizable);

    C page(Component... components);

    C addWithSeparators(ListItem... items);

    C setWithSeparators(ListItem... items);

    static BaseBreadcrumbConfigurator configure(Breadcrumbs breadcrumb) {
        return new DefaultBreadcrumbConfigurator(breadcrumb);
    }

    interface BaseBreadcrumbConfigurator extends BreadcrumbConfigurator<BaseBreadcrumbConfigurator> {
    }
}




