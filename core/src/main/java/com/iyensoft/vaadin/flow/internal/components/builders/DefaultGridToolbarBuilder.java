package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.GridToolbarBuilder;
import com.iyensoft.vaadin.flow.components.GridToolbar;
import com.vaadin.flow.component.Component;

/** Default {@link GridToolbarBuilder} implementation. */
public class DefaultGridToolbarBuilder extends AbstractGridToolbarConfigurator<GridToolbarBuilder>
        implements GridToolbarBuilder {

    public DefaultGridToolbarBuilder(Component... components) {
        super(new GridToolbar(components));
    }

    @Override protected GridToolbarBuilder getConfigurator() {
        return this;
    }

    @Override public GridToolbar build() {
        applyPostProcessors();
        return getComponent();
    }
}