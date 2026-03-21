package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.FormHeaderConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormHeader;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface FormHeaderBuilder extends FormHeaderConfigurator<FormHeaderBuilder>, ComponentBuilder<HorizontalLayout,FormHeaderBuilder> {

    static FormHeaderBuilder create() {
        return new DefaultFormHeader();
    }


}
