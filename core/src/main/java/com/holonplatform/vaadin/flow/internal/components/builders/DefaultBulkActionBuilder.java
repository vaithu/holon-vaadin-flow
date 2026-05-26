package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultBulkActionBuilder extends AbstractBulkActionConfigurator<BulkActionBuilder> implements BulkActionBuilder {


    public DefaultBulkActionBuilder(HorizontalLayout component) {
        super(component);
    }

    public DefaultBulkActionBuilder() {
        super(new HorizontalLayout());
    }

    @Override
    public HorizontalLayout build() {
        return getComponent();
    }

    @Override
    protected DefaultBulkActionBuilder getConfigurator() {
        return this;
    }
}
