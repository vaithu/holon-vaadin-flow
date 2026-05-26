package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutBuilder;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class DefaultSplitLayoutBuilder
        extends AbstractSplitLayoutConfigurator<SplitLayoutBuilder>
        implements SplitLayoutBuilder {
    public DefaultSplitLayoutBuilder(SplitLayout component) {
        super(component);
    }

    @Override
    public SplitLayout build() {
        return getComponent();
    }

    @Override
    protected SplitLayoutBuilder getConfigurator() {
        return this;
    }
}
