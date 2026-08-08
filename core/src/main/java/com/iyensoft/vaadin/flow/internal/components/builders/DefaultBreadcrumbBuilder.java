package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.iyensoft.vaadin.flow.components.builders.BreadcrumbBuilder;

public class DefaultBreadcrumbBuilder extends AbstractBreadcrumbConfigurator<BreadcrumbBuilder>
        implements BreadcrumbBuilder {

    public DefaultBreadcrumbBuilder(Breadcrumb component) {
        super(component);
    }

    @Override
    public Breadcrumb build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected BreadcrumbBuilder getConfigurator() {
        return this;
    }
}

