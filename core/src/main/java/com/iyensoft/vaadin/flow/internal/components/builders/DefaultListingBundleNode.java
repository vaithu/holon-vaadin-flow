package com.iyensoft.vaadin.flow.internal.components.builders;

import java.util.Objects;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractListingBundleConfigurer;
import com.iyensoft.vaadin.flow.components.builders.MasterConfigurator;

/**
 * Default implementation of {@link MasterConfigurator.ListingBundleNode}.
 *
 * <p>Inherits all build-time configuration from {@link AbstractListingBundleConfigurer}.
 * {@link #add()} returns control to the enclosing master builder; the master calls
 * {@link #build()} (package-private) when it is ready to assemble the final component.</p>
 *
 * @param <T> listing item type
 * @param <E> enclosing master configurator type
 */
public class DefaultListingBundleNode<T, E extends MasterConfigurator<E>>
        extends AbstractListingBundleConfigurer<T, MasterConfigurator.ListingBundleNode<T, E>>
        implements MasterConfigurator.ListingBundleNode<T, E> {

    private final E masterConfigurator;

    public DefaultListingBundleNode(Class<T> beanType, E masterConfigurator) {
        super(beanType);
        this.masterConfigurator = Objects.requireNonNull(masterConfigurator);
    }

    @Override
    protected MasterConfigurator.ListingBundleNode<T, E> getConfigurator() {
        return this;
    }

    @Override
    public E add() {
        return masterConfigurator;
    }

    /** Called by the master builder to assemble the listing bundle. */
    public ListingBundle<T> build() {
        return buildBundle();
    }
}
