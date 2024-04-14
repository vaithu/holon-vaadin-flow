package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultRowBuilder;

public interface RowBuilder extends RowConfigurator<RowBuilder>, HasStyleConfigurator<RowBuilder>
, HasComponent {

    static RowBuilder create() {
        return new DefaultRowBuilder();
    }
}
