package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultFormHeaderBarBuilder<B extends ZohoConfigurator<B>>
        extends AbstractFormHeaderConfigurator<ZohoConfigurator.FormHeaderBarBuilder<B>>
        implements ZohoConfigurator.FormHeaderBarBuilder<B> {

    private final B parentBuilder;

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultFormHeaderBarBuilder(B parentBuilder,HorizontalLayout component) {
        super(component);
        this.parentBuilder = parentBuilder;
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected ZohoConfigurator.FormHeaderBarBuilder<B> getConfigurator() {
        return this;
    }


    @Override
    public B add() {
        parentBuilder.detailHeader(getComponent());
        return parentBuilder;
    }

    @Override
    public HorizontalLayout build() {
        return getComponent();
    }


}
