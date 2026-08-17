package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ArAgingBarBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.ArAgingBar;

/**
 * Default {@link ArAgingBarBuilder} implementation.
 */
public class DefaultArAgingBarBuilder
        extends AbstractArAgingBarConfigurator<ArAgingBarBuilder>
        implements ArAgingBarBuilder {

    public DefaultArAgingBarBuilder() {
        super(new ArAgingBar(ArAgingBar.Variant.DEFAULT));
    }

    public DefaultArAgingBarBuilder(ArAgingBar.Variant variant) {
        super(new ArAgingBar(variant != null ? variant : ArAgingBar.Variant.DEFAULT));
    }

    @Override
    protected ArAgingBarBuilder getConfigurator() {
        return this;
    }

    @Override
    public ArAgingBar build() {
        applyPostProcessors();
        return getComponent();
    }
}
