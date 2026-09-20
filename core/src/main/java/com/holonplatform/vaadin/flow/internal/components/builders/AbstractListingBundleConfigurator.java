package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.builders.ListingBundleConfigurator;
import com.iyensoft.vaadin.flow.components.DynamicFilterPanel;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Optional;

/**
 * Abstract base for {@link ListingBundleConfigurator} implementations.
 *
 * @param <T> item type carried by the bundle
 * @param <C> concrete configurator type
 */
public abstract class AbstractListingBundleConfigurator<T, C extends ListingBundleConfigurator<T, C>>
        implements ListingBundleConfigurator<T, C> {

    private final ListingBundle<T> bundle;

    public AbstractListingBundleConfigurator(ListingBundle<T> bundle) {
        ObjectUtils.argumentNotNull(bundle, "ListingBundle must be not null");
        this.bundle = bundle;
    }

    /**
     * Get the concrete configurator instance, for fluent chaining.
     * @return the concrete configurator instance
     */
    protected abstract C getConfigurator();

    /* -------- Bundle-specific fluent methods -------- */

    @Override
    public C onItemClick(ComponentEventListener<ItemClickEvent<T>> listener) {
        bundle.addItemClickListener(listener);
        return getConfigurator();
    }

    /* -------- Read-only accessors -------- */

    @Override
    public ListingBundle<T> getBundle() {
        return bundle;
    }

    @Override
    public Grid<T> grid() {
        return bundle.grid();
    }

    @Override
    public Div toolbar() {
        return bundle.toolbar();
    }

    @Override
    public Div footer() {
        return bundle.footer();
    }

    @Override
    public Optional<TextField> getSearchOptional() {
        return bundle.getSearchOptional();
    }

    @Override
    public Optional<DynamicFilterPanel<T>> getFilterPanelOptional() {
        return bundle.getFilterPanelOptional();
    }
}

