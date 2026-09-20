package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultGridToolbarBuilder;
import com.iyensoft.vaadin.flow.components.GridToolbar;
import com.vaadin.flow.component.Component;

/** Fluent builder for grid search, filter, selection, and bulk-action toolbars. */
public interface GridToolbarBuilder extends GridToolbarConfigurator<GridToolbarBuilder>,
        ComponentBuilder<GridToolbar, GridToolbarBuilder> {

    static GridToolbarBuilder create() {
        return new DefaultGridToolbarBuilder();
    }

    static GridToolbarBuilder create(Component... components) {
        return new DefaultGridToolbarBuilder(components);
    }
}