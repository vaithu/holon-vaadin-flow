package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DetailsBuilder;
import com.vaadin.flow.component.details.Details;

public class DefaultDetailsBuilder
        extends AbstractDetailsConfigurator<DetailsBuilder>
        implements DetailsBuilder {

    public DefaultDetailsBuilder(Details component) {
        super(component);
    }

    @Override
    public Details build() {
        return getComponent();
    }

    @Override
    protected DetailsBuilder getConfigurator() {
        return this;
    }
}
