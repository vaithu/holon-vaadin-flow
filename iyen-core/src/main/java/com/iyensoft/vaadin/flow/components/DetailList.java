package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A detail panel that lists rows related to the selected master item.
 *
 * <p>Each time the master-detail selection changes, the row loader is invoked with the selected
 * item and the listing is repopulated. Because it implements {@link DetailSyncAware}, it works
 * directly as a detail section or as {@code LazyTabs} content without a wrapper class.</p>
 *
 * <p>Create instances with {@link Components#detailList(String, Class, SerializableFunction)}.</p>
 *
 * @param <I> selected master item type
 * @param <R> row type
 */
public class DetailList<I, R> extends Panel implements DetailSyncAware<I> {

    private final SerializableFunction<? super I, ? extends Collection<R>> rows;
    private ItemListing<R, ?> listing;

    /**
     * @param rows maps the selected item to the rows to display (not null)
     */
    public DetailList(SerializableFunction<? super I, ? extends Collection<R>> rows) {
        this.rows = Objects.requireNonNull(rows, "Row loader must not be null");
    }

    /**
     * Binds the listing that displays the rows. Called once by the builder.
     *
     * @param listing row listing (not null)
     * @throws IllegalStateException if a listing is already bound
     */
    public void setListing(ItemListing<R, ?> listing) {
        if (this.listing != null) {
            throw new IllegalStateException("The listing is already bound");
        }
        this.listing = Objects.requireNonNull(listing, "Listing must not be null");
    }

    /**
     * @return the listing displaying the rows
     */
    public ItemListing<R, ?> getListing() {
        return listing;
    }

    @Override
    public void onItemSelected(I item) {
        Collection<R> items = item != null ? rows.apply(item) : null;
        listing.getGrid().setItems(items != null ? items : List.of());
    }
}
