package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultZohoBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface ZohoBuilder extends ZohoConfigurator<ZohoBuilder>, ComponentBuilder<HorizontalLayout, ZohoBuilder> {

    static ZohoBuilder create(HorizontalLayout layout) {
        return new DefaultZohoBuilder(layout);
    }

    static ZohoBuilder create(HorizontalLayout layout, boolean mobile) {
        return new DefaultZohoBuilder(layout,mobile);
    }

    static ZohoBuilder create() {
        return create(new HorizontalLayout());
    }

    static ZohoBuilder create(boolean mobile) {
        return create(new HorizontalLayout(),mobile);
    }
}
