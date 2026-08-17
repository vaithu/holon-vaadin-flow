package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultArAgingBarBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;

/**
 * Builder to create and configure {@link ArAgingBar} components.
 */
public interface ArAgingBarBuilder
        extends ArAgingBarConfigurator<ArAgingBarBuilder>, ComponentBuilder<ArAgingBar, ArAgingBarBuilder> {

    static ArAgingBarBuilder create() {
        return new DefaultArAgingBarBuilder();
    }

    static ArAgingBarBuilder create(ArAgingBar.Variant variant) {
        return new DefaultArAgingBarBuilder(variant);
    }
}
