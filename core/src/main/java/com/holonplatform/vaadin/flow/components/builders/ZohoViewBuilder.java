package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultZohoViewBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface ZohoViewBuilder extends ZohoViewConfigurator<ZohoViewBuilder> {

    static ZohoViewBuilder create() {
        return create(new Layout());
    }

    static ZohoViewBuilder create(Layout layout) {
        return new DefaultZohoViewBuilder(layout);
    }
}
