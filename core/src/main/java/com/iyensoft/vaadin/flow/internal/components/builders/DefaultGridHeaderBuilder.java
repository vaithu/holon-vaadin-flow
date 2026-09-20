package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.GridHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.iyensoft.vaadin.flow.components.GridHeader;

public class DefaultGridHeaderBuilder extends  AbstractGridHeaderConfigurator<GridHeaderBuilder>
        implements GridHeaderBuilder {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultGridHeaderBuilder(GridHeader component) {
        super(component);
    }

    public DefaultGridHeaderBuilder(String title) {
        super(new GridHeader(title));
    }

    public DefaultGridHeaderBuilder(LabelBuilder<?> labelBuilder) {
        super(new  GridHeader(labelBuilder));
    }

    @Override
    public GridHeader build() {
        return getComponent();
    }

    @Override
    protected GridHeaderBuilder getConfigurator() {
        return this;
    }
}
