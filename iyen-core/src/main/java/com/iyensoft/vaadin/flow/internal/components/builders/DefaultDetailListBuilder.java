package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.iyensoft.vaadin.flow.components.DetailList;
import com.iyensoft.vaadin.flow.components.Empty;
import com.iyensoft.vaadin.flow.components.builders.DetailListBuilder;
import com.iyensoft.vaadin.flow.components.builders.EmptyBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default {@link DetailListBuilder} implementation.
 */
public class DefaultDetailListBuilder<I, R>
        extends AbstractDetailPanelConfigurator<DetailListBuilder<I, R>>
        implements DetailListBuilder<I, R> {

    private final DetailList<I, R> detailList;
    private final BeanListingBuilder<R> listingBuilder;
    private Empty emptyState;

    public DefaultDetailListBuilder(String title, Class<R> rowType,
                                    SerializableFunction<? super I, ? extends Collection<R>> rows) {
        this(new DetailList<>(rows), title, rowType);
    }

    private DefaultDetailListBuilder(DetailList<I, R> detailList, String title, Class<R> rowType) {
        super(detailList, title);
        this.detailList = detailList;
        this.listingBuilder = BeanListing.builder(Objects.requireNonNull(rowType, "Row type must not be null"), false)
                .withThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER,
                        GridVariant.LUMO_ROW_STRIPES)
                .allRowsVisible(true)
                .fullWidth();
        detailList.setWidthFull();
    }

    @Override
    public DetailListBuilder<I, R> column(ValueProvider<R, ?> valueProvider, String header) {
        listingBuilder.withColumn(Objects.requireNonNull(valueProvider, "Value provider must not be null"))
                .header(header)
                .autoWidth(true)
                .add();
        return this;
    }

    @Override
    public DetailListBuilder<I, R> emptyState(String title, String description) {
        EmptyBuilder empty = Empty.builder().title(Objects.requireNonNull(title, "Title must not be null"));
        if (description != null && !description.isBlank()) {
            empty.description(description);
        }
        return emptyState(empty.build());
    }

    @Override
    public DetailListBuilder<I, R> emptyState(Empty emptyState) {
        this.emptyState = Objects.requireNonNull(emptyState, "Empty state must not be null");
        return this;
    }

    @Override
    public DetailListBuilder<I, R> listing(Consumer<BeanListingBuilder<R>> configurer) {
        Objects.requireNonNull(configurer, "Configurer must not be null").accept(listingBuilder);
        return this;
    }

    @Override
    public DetailList<I, R> build() {
        if (emptyState != null) {
            listingBuilder.emptyStateComponent(emptyState);
        }
        BeanListing<R> listing = listingBuilder.build();
        detailList.setListing(listing);
        content(listing.getComponent());
        applyPostProcessors();
        return detailList;
    }

    @Override
    protected DetailListBuilder<I, R> getConfigurator() {
        return this;
    }
}
