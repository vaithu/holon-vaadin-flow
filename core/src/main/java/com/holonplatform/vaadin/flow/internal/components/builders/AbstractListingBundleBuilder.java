package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.ListingBundleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Standalone listing bundle builder — delegates all configuration to
 * {@link AbstractListingBundleConfigurer} and adds the {@link #build()} terminal.
 *
 * @param <T> bean item type
 */
public abstract class AbstractListingBundleBuilder<T>
        extends AbstractListingBundleConfigurer<T, ListingBundleBuilder<T>>
        implements ListingBundleBuilder<T> {

    /** Post-processors applied just before {@code build()} returns. */
    private final List<Consumer<ListingBundle<T>>> postProcessors = new ArrayList<>();

    protected AbstractListingBundleBuilder(Class<T> beanType) {
        super(beanType);
    }

    @Override
    protected ListingBundleBuilder<T> getConfigurator() {
        return this;
    }

	@Override
	public ListingBundleBuilder<T> withBuildPostProcessor(Consumer<ListingBundle<T>> postProcessor) {
		Objects.requireNonNull(postProcessor, "Post-processor must not be null");
		this.postProcessors.add(postProcessor);
		return this;
	}

    @Override
    public ListingBundle<T> build() {
        ListingBundle<T> bundle = buildBundle();
        postProcessors.forEach(pp -> pp.accept(bundle));
        return bundle;
    }
}

