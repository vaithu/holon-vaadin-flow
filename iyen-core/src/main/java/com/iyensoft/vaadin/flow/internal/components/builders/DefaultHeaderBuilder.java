package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.HeaderBuilder;
import com.iyensoft.vaadin.flow.components.Header;

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
