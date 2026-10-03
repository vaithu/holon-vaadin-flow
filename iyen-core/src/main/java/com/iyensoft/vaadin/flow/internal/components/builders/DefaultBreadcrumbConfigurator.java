package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.BreadcrumbConfigurator;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;

public class DefaultBreadcrumbConfigurator extends AbstractBreadcrumbConfigurator<BreadcrumbConfigurator.BaseBreadcrumbConfigurator>
        implements BreadcrumbConfigurator.BaseBreadcrumbConfigurator {

    public DefaultBreadcrumbConfigurator(Breadcrumbs component) {
        super(component);
    }

    @Override
    protected BreadcrumbConfigurator.BaseBreadcrumbConfigurator getConfigurator() {
        return this;
    }
}

