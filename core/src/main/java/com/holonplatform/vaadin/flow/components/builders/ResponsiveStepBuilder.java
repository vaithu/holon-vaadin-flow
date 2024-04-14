package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultResponsiveStepBuilder;

public interface ResponsiveStepBuilder extends ResponsiveStepConfigurator<ResponsiveStepBuilder> {

    static ResponsiveStepBuilder create() {
        return new DefaultResponsiveStepBuilder();
    }
}
