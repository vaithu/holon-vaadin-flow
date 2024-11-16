package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormResponsiveStepBuilder;

public interface FormResponsiveStepBuilder extends FormResponsiveStepConfigurator<FormResponsiveStepBuilder> {

    static FormResponsiveStepBuilder create() {
        return new DefaultFormResponsiveStepBuilder();
    }
}
