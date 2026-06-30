package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.ListingBundleConfigurer;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMasterConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.Optional;

public interface MasterConfigurator<C extends MasterConfigurator<C>> extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasSizeConfigurator<C> {
    
    HeaderBuilder<C> header();

    FooterBuilder<C> footer();

    C content(Component... components);

    C header(Header header);

    C footer(Footer footer);

    C card();

    Optional<Header> getMasterHeader();
    Optional<Footer> getMasterFooter();

    <T> ListingBundleNode<T, C> listing(Class<T> beanType);

    /**
     * Opens a listing sub-builder backed by the given Holon {@link PropertySet}.
     * The item type is always {@link PropertyBox}; use this when the master listing
     * is driven by a Holon property model rather than a plain Java bean class.
     *
     * @param propertySet the property set that defines the listing columns (not null)
     * @return a new {@link ListingBundleNode} for {@code PropertyBox} items
     */
    ListingBundleNode<PropertyBox, C> listing(PropertySet<?> propertySet);


    static BaseMasterConfigurator configure(Div div) {
        return new DefaultMasterConfigurator(div);
    }

    interface BaseMasterConfigurator extends MasterConfigurator<BaseMasterConfigurator> {

    }

    interface HeaderBuilder<B extends MasterConfigurator<B>> extends HeaderConfigurator<HeaderBuilder<B>> {
        B add();
    }

    interface FooterBuilder<D extends MasterConfigurator<D>> extends FooterConfigurator<FooterBuilder<D>> {
        D add();
    }


    /**
     * Sub-builder for embedding a listing bundle inside the master panel.
     * <p>
     * Extends {@link ListingBundleConfigurer} so the full build-time API
     * (columns, fetch, pageSizes, search, filterPanel, etc.) is available directly
     * on the node. Call {@link #add()} to finalise and return to the master builder.
     * <p>
     * {@code build()} is intentionally absent — the listing lifecycle is managed
     * by the master builder.
     *
     * @param <T> listing item type
     * @param <E> enclosing master configurator type
     */
    interface ListingBundleNode<T, E extends MasterConfigurator<E>>
            extends ListingBundleConfigurer<T, ListingBundleNode<T, E>> {

        /** Finalise the listing node and return to the enclosing master configurator. */
        E add();
    }

    


    
}
