package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultHeaderBuilder;
import com.iyensoft.vaadin.flow.components.Header;

public interface HeaderBuilder extends HeaderConfigurator<HeaderBuilder>, ComponentBuilder<Header, HeaderBuilder> {

    static HeaderBuilder create(String title) {
        return new DefaultHeaderBuilder(new Header(title));
    }

    static HeaderBuilder create(LabelBuilder<?> labelBuilder) {
        return new DefaultHeaderBuilder(new Header(labelBuilder));
    }

}
