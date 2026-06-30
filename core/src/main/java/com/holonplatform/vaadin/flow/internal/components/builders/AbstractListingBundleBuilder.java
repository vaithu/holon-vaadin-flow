package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.ListingBundleBuilder;

/**
 * Standalone listing bundle builder — delegates all configuration to
 * {@link AbstractListingBundleConfigurer} and adds the {@link #build()} terminal.
 *
 * @param <T> bean item type
 */
public abstract class AbstractListingBundleBuilder<T>
        extends AbstractListingBundleConfigurer<T, ListingBundleBuilder<T>>
        implements ListingBundleBuilder<T> {

    protected AbstractListingBundleBuilder(Class<T> beanType) {
        super(beanType);
    }

    @Override
    protected ListingBundleBuilder<T> getConfigurator() {
        return this;
    }

    @Override
    public ListingBundle<T> build() {
        return buildBundle();
    }
}

