package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;

import com.iyensoft.vaadin.flow.components.builders.BreadcrumbConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractBreadcrumbConfigurator<C extends BreadcrumbConfigurator<C>>
        extends AbstractComponentConfigurator<Breadcrumbs, C>
        implements BreadcrumbConfigurator<C> {

    protected AbstractBreadcrumbConfigurator(Breadcrumbs component) {
        super(component);
    }


    @Override
    public C add(BreadcrumbsItem item) {
        getComponent().add(item);
        return getConfigurator();
    }

    @Override
    public C clear() {
        getComponent().removeAll();
        return getConfigurator();
    }

    @Override
    public C item(String text, Class<? extends Component> navigationTarget) {
        return add(new BreadcrumbsItem(text, navigationTarget));
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



