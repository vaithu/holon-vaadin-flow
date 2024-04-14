package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFlexBoxLayoutBuilder;

public interface FlexBoxLayoutBuilder extends FlexBoxLayoutConfigurator<FlexBoxLayoutBuilder>,
        ComponentBuilder<FlexBoxLayout, FlexBoxLayoutBuilder> {

    static FlexBoxLayoutBuilder create() {
        return new DefaultFlexBoxLayoutBuilder(new FlexBoxLayout());
    }
}
