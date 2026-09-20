package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.Breadcrumb;
import com.iyensoft.vaadin.flow.components.builders.BreadcrumbConfigurator;

public class DefaultBreadcrumbConfigurator extends AbstractBreadcrumbConfigurator<BreadcrumbConfigurator.BaseBreadcrumbConfigurator>
        implements BreadcrumbConfigurator.BaseBreadcrumbConfigurator {

    public DefaultBreadcrumbConfigurator(Breadcrumb component) {
        super(component);
    }

    @Override
    protected BreadcrumbConfigurator.BaseBreadcrumbConfigurator getConfigurator() {
        return this;
    }
}

