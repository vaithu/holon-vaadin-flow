package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultBreadcrumbBuilder;

public interface BreadcrumbBuilder extends BreadcrumbConfigurator<BreadcrumbBuilder>,
        ComponentBuilder<Breadcrumb, BreadcrumbBuilder> {

    static BreadcrumbBuilder create() {
        return create(new Breadcrumb());
    }

    static BreadcrumbBuilder create(Breadcrumb breadcrumb) {
        return new DefaultBreadcrumbBuilder(breadcrumb);
    }
}


