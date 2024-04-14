package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultColumnBuilder;

public interface ColumnBuilder extends ColumnConfigurator<ColumnBuilder>, HasComponent, HasStyleConfigurator<ColumnBuilder> {

    static ColumnBuilder create() {
        return new DefaultColumnBuilder();
    }
}
