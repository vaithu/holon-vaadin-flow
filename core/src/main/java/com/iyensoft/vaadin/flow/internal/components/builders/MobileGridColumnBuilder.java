package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public interface MobileGridColumnBuilder extends MobileGridColumnConfigurator<MobileGridColumnBuilder>, ComponentBuilder<Layout, MobileGridColumnBuilder> {

    static MobileGridColumnBuilder create() {
        return new DefaultMobileGridColumnBuilder();
    }

    static MobileGridColumnBuilder create(Layout layout) {
        return new DefaultMobileGridColumnBuilder(layout);
    }
}
