package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSplitLayoutBuilder;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public interface SplitLayoutBuilder extends SplitLayoutConfigurator<SplitLayoutBuilder>,
        ComponentBuilder<SplitLayout, SplitLayoutBuilder> {

    static SplitLayoutBuilder create() {
        return new DefaultSplitLayoutBuilder(new SplitLayout());
    }

}
