package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBulkActionBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface BulkActionBuilder extends BulkActionConfigurator<BulkActionBuilder>,
        ComponentBuilder<HorizontalLayout, BulkActionBuilder> {

    static BulkActionBuilder create() {
        return new DefaultBulkActionBuilder(new HorizontalLayout());
    }

}
