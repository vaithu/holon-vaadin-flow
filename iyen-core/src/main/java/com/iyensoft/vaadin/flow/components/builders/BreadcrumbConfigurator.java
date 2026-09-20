package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.iyensoft.vaadin.flow.components.Breadcrumb;
import com.iyensoft.vaadin.flow.components.BreadcrumbItem;
import com.iyensoft.vaadin.flow.components.BreadcrumbPage;
import com.iyensoft.vaadin.flow.components.BreadcrumbSeparator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultBreadcrumbConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Supplier;

public interface BreadcrumbConfigurator<C extends BreadcrumbConfigurator<C>> extends ComponentConfigurator<C> {

    C add(ListItem... items);

    C add(BreadcrumbItem item);

    C add(BreadcrumbSeparator separator);

    C add(BreadcrumbPage page);

    C clear();

    C item(BreadcrumbItem item);

    C item(String text, Class<? extends Component> navigationTarget);

    C item(Component content);

    C separator(BreadcrumbSeparator separator);

    C separator();

    C separator(VaadinIcon icon);

    C separator(Component customContent);

    C page(BreadcrumbPage page);

    C page(String text);

    C page(Localizable localizable);

    C page(Component... components);

    C addWithSeparators(ListItem... items);

    C setWithSeparators(ListItem... items);

    C separatorSupplier(Supplier<BreadcrumbSeparator> separatorSupplier);

    static BaseBreadcrumbConfigurator configure(Breadcrumb breadcrumb) {
        return new DefaultBreadcrumbConfigurator(breadcrumb);
    }

    interface BaseBreadcrumbConfigurator extends BreadcrumbConfigurator<BaseBreadcrumbConfigurator> {
    }
}




