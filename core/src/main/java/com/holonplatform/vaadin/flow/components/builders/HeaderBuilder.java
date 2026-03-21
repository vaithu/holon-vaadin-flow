package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHeaderBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.html.H3;

public interface HeaderBuilder extends HeaderConfigurator<HeaderBuilder>, ComponentBuilder<Header, HeaderBuilder> {

    static HeaderBuilder create(String title) {
        return new DefaultHeaderBuilder(new Header(title));
    }

    static HeaderBuilder create(LabelBuilder<?> labelBuilder) {
        return new DefaultHeaderBuilder(new Header(labelBuilder));
    }

}
