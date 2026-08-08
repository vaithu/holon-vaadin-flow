package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbSeparator;
import com.iyensoft.vaadin.flow.components.builders.BreadcrumbConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractBreadcrumbConfigurator<C extends BreadcrumbConfigurator<C>>
        extends AbstractComponentConfigurator<Breadcrumb, C>
        implements BreadcrumbConfigurator<C> {

    protected AbstractBreadcrumbConfigurator(Breadcrumb component) {
        super(component);
    }

    @Override
    public C add(ListItem... items) {
        getComponent().add(items);
        return getConfigurator();
    }

    @Override
    public C add(BreadcrumbItem item) {
        getComponent().add(item);
        return getConfigurator();
    }

    @Override
    public C add(BreadcrumbSeparator separator) {
        getComponent().add(separator);
        return getConfigurator();
    }

    @Override
    public C add(BreadcrumbPage page) {
        getComponent().add(page);
        return getConfigurator();
    }

    @Override
    public C clear() {
        getComponent().removeAll();
        return getConfigurator();
    }

    @Override
    public C item(BreadcrumbItem item) {
        return add(item);
    }

    @Override
    public C item(String text, Class<? extends Component> navigationTarget) {
        return add(new BreadcrumbItem(text, navigationTarget));
    }

    @Override
    public C item(Component content) {
        return add(new BreadcrumbItem(content));
    }

    @Override
    public C separator(BreadcrumbSeparator separator) {
        return add(separator);
    }

    @Override
    public C separator() {
        return add(new BreadcrumbSeparator());
    }

    @Override
    public C separator(VaadinIcon icon) {
        return add(new BreadcrumbSeparator(icon));
    }

    @Override
    public C separator(Component customContent) {
        return add(new BreadcrumbSeparator(customContent));
    }

    @Override
    public C page(BreadcrumbPage page) {
        return add(page);
    }

    @Override
    public C page(String text) {
        return add(new BreadcrumbPage(text));
    }

    @Override
    public C page(Localizable localizable) {
        return add(new BreadcrumbPage(localizable));
    }

    @Override
    public C page(Component... components) {
        return add(new BreadcrumbPage(components));
    }

    @Override
    public C addWithSeparators(ListItem... items) {
        getComponent().addWithSeparators(items);
        return getConfigurator();
    }

    @Override
    public C setWithSeparators(ListItem... items) {
        getComponent().setWithSeparators(items);
        return getConfigurator();
    }

    @Override
    public C separatorSupplier(Supplier<BreadcrumbSeparator> separatorSupplier) {
        getComponent().setSeparatorSupplier(separatorSupplier);
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}



