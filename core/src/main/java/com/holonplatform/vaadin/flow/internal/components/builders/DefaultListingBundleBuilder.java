package com.holonplatform.vaadin.flow.internal.components.builders;


/**
 * Default bean listing bundle builder implementation.
 *
 * @param <T> bean item type
 */
public class DefaultListingBundleBuilder<T> extends AbstractListingBundleBuilder<T> {

    public DefaultListingBundleBuilder(Class<T> beanType) {
        super(beanType);
    }
}