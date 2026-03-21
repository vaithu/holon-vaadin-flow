package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public interface ItemFinder {
    VerticalLayout findAndHandle(QueryFilter queryFilter, PropertySet<?> propertySet);
}