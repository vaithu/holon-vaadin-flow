package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.builders.ListingBundleConfigurator;

/**
 * Default implementation of {@link ListingBundleConfigurator.BaseListingBundleConfigurator}
 * for configuring an existing {@link ListingBundle} instance without rebuilding it.
 *
 * <p>Use {@link ListingBundleConfigurator#configure(ListingBundle)} to obtain an instance.</p>
 *
 * @param <T> item type carried by the bundle
 */
public class DefaultBaseListingBundleConfigurator<T>
        extends AbstractListingBundleConfigurator<T, ListingBundleConfigurator.BaseListingBundleConfigurator<T>>
        implements ListingBundleConfigurator.BaseListingBundleConfigurator<T> {

    public DefaultBaseListingBundleConfigurator(ListingBundle<T> bundle) {
        super(bundle);
    }

    @Override
    protected ListingBundleConfigurator.BaseListingBundleConfigurator<T> getConfigurator() {
        return this;
    }
}

