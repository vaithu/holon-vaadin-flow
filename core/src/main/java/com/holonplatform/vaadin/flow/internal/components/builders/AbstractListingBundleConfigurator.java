package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.builders.ListingBundleConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Abstract base for {@link ListingBundleConfigurator} implementations.
 *
 * <p>Reuses {@link AbstractGridHeaderConfigurator} for all inherited
 * Header / GridHeader fluent methods — they operate on {@code bundle.header()}.
 * The bundle <strong>must</strong> have been built with a grid header
 * (via {@code ListingBundleBuilder#gridHeader(String)}); otherwise an
 * {@link IllegalStateException} is thrown at construction time.</p>
 *
 * @param <T> item type carried by the bundle
 * @param <C> concrete configurator type
 */
public abstract class AbstractListingBundleConfigurator<T, C extends ListingBundleConfigurator<T, C>>
        extends AbstractGridHeaderConfigurator<C>
        implements ListingBundleConfigurator<T, C> {

    private final ListingBundle<T> bundle;

    public AbstractListingBundleConfigurator(ListingBundle<T> bundle) {
        super(requireHeader(bundle));
        this.bundle = bundle;
    }

    private static GridHeader requireHeader(ListingBundle<?> bundle) {
        ObjectUtils.argumentNotNull(bundle, "ListingBundle must be not null");
        GridHeader header = bundle.header();
        if (header == null) {
            throw new IllegalStateException(
                    "ListingBundle has no GridHeader — configure with ListingBundleBuilder.gridHeader(String) "
                            + "before using ListingBundleConfigurator.configure(...)");
        }
        return header;
    }

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
    public TextField getSearchField() {
        return bundle.search();
    }

    @Override
    public DynamicFilterPanel<T> getFilterPanel() {
        return bundle.filterPanel();
    }

    @Override
    public Div getToolbar() {
        return bundle.toolbar();
    }

    @Override
    public Div getFooter() {
        return bundle.footer();
    }
}

