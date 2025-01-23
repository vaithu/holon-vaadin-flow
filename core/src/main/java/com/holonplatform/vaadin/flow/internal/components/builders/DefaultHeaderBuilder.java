package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HeaderBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;

public class DefaultHeaderBuilder
        extends AbstractHeaderConfigurator<HeaderBuilder>
        implements HeaderBuilder {
    public DefaultHeaderBuilder(Header header) {
        super(header);
    }

    @Override
    protected HeaderBuilder getConfigurator() {
        return this;
    }

    @Override
    public Header build() {
        return getComponent();
    }
}
