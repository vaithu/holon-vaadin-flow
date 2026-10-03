package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Table;
import com.vaadin.flow.component.html.TableRow;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataViewUtils;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Objects;

/**
 * Opt-in presentation of an ItemListing with a bounded, transposed record page.
 * The Grid remains the listing's data and sorting source.
 */
@StyleSheet("context://item-listing-matrix.css")
public class ItemListingMatrix<T, P> extends Div {

    private final ItemListing<T, P> listing;
    private final SerializableBiFunction<P, T, String> value;
    private final SerializableFunction<T, String> heading;
    private final int pageSize;
    private final Switch modeSwitch = new Switch();
    private final Div matrix = new Div();
    private final PaginationBase pagination = new PaginationBase();
    private final Button previous = new Button("Previous");
    private final Button next = new Button("Next");
    private final Span pageLabel = new Span();
    private Registration dataRegistration;
    private Registration sortRegistration;
    private DataProvider<T, ?> observedProvider;
    private int page = 1;

    public ItemListingMatrix(ItemListing<T, P> listing, int pageSize,
            SerializableFunction<T, String> heading, SerializableBiFunction<P, T, String> value) {
        this.listing = Objects.requireNonNull(listing, "listing");
        if (pageSize < 1 || pageSize > 10) {
            throw new IllegalArgumentException("Matrix page size must be between 1 and 10");
        }
        this.pageSize = pageSize;
        this.heading = heading;
        this.value = Objects.requireNonNull(value, "value");
        addClassName("item-listing-matrix");
        matrix.addClassName("item-listing-matrix__scroll");
        modeSwitch.setLabel(LocalizationProvider.localize("Matrix view", "listing.matrix.toggle"));
        modeSwitch.addValueChangeListener(event -> {
            boolean active = event.getValue();
            if (active) {
                observeProvider();
                page = 1;
                render();
            }
            listing.getGrid().setVisible(!active);
            matrix.setVisible(active);
            pagination.setVisible(active);
        });
        previous.addClickListener(event -> goToPreviousPage());
        next.addClickListener(event -> goToNextPage());
        ListItem previousItem = new ListItem();
        previousItem.add(previous);
        ListItem pageItem = new ListItem();
        pageItem.add(pageLabel);
        ListItem nextItem = new ListItem();
        nextItem.add(next);
        pagination.getContent().add(previousItem, pageItem, nextItem);
        matrix.setVisible(false);
        pagination.setVisible(false);
        add(modeSwitch, listing.getGrid(), matrix, pagination);
    }

    public Switch getModeSwitch() {
        return modeSwitch;
    }

    public PaginationBase getPagination() {
        return pagination;
    }

    public Table getTable() {
        return matrix.getChildren().filter(Table.class::isInstance)
                .map(Table.class::cast).findFirst().orElse(null);
    }

    public int getCurrentPage() {
        return page;
    }

    public void goToPreviousPage() {
        if (page > 1 && modeSwitch.getValue()) {
            page--;
            render();
        }
    }

    public void goToNextPage() {
        if (modeSwitch.getValue() && next.isEnabled()) {
            page++;
            render();
        }
    }

    public void refresh() {
        if (modeSwitch.getValue()) {
            page = 1;
            observeProvider();
            render();
        }
    }

    private void observeProvider() {
        DataProvider<T, ?> current = listing.getGrid().getDataProvider();
        if (current != observedProvider) {
            if (dataRegistration != null) {
                dataRegistration.remove();
            }
            observedProvider = current;
            dataRegistration = current.addDataProviderListener(event -> refresh());
        }
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (modeSwitch.getValue()) {
            observeProvider();
        }
        sortRegistration = listing.getGrid().addSortListener(change -> refresh());
    }

    @Override
    protected void onDetach(DetachEvent event) {
        if (dataRegistration != null) {
            dataRegistration.remove();
            dataRegistration = null;
            observedProvider = null;
        }
        if (sortRegistration != null) {
            sortRegistration.remove();
            sortRegistration = null;
        }
        super.onDetach(event);
    }

    private void render() {
        List<T> items = fetch(listing.getGrid().getDataProvider(), (page - 1) * pageSize, pageSize + 1);
        boolean hasNext = items.size() > pageSize;
        List<T> shown = items.stream().limit(pageSize).toList();
        matrix.removeAll();
        if (shown.isEmpty()) {
            matrix.add(new Span(LocalizationProvider.localize("No records", "listing.matrix.empty")));
        } else {
            Table table = new Table();
            table.addClassName("item-listing-matrix__table");
            TableRow header = table.addHeaderRow();
            header.addColumnHeaderCell(LocalizationProvider.localize("Field", "listing.matrix.field"));
            for (int i = 0; i < shown.size(); i++) {
                header.addColumnHeaderCell(heading == null
                        ? "Record " + ((page - 1) * pageSize + i + 1)
                        : Objects.requireNonNull(heading.apply(shown.get(i)), "matrix heading"));
            }
            for (P property : listing.getVisibleColumns()) {
                TableRow row = table.addRow();
                row.addRowHeaderCell(listing.getColumnHeader(property).orElse(String.valueOf(property)));
                for (T item : shown) {
                    String text = value.apply(property, item);
                    row.addDataCell(text == null ? "" : text);
                }
            }
            matrix.add(table);
        }
        previous.setEnabled(page > 1);
        next.setEnabled(hasNext);
        pageLabel.setText("Page " + page);
    }

    private List<T> fetch(DataProvider<T, ?> provider, int offset, int limit) {
        if (provider instanceof ListDataProvider<?> rawList) {
            @SuppressWarnings("unchecked")
            ListDataProvider<T> listProvider = (ListDataProvider<T>) rawList;
            return listProvider.fetch(new Query<>(offset, limit, listing.getColumnSorts(),
                            DataViewUtils.<T>getComponentSortComparator(listing.getGrid())
                                    .orElse(listing.getGrid().getDataCommunicator().getInMemorySorting()),
                            DataViewUtils.<T>getComponentFilter(listing.getGrid()).orElse(null)))
                    .limit(limit).toList();
        }
        return fetchTyped(provider, offset, limit);
    }

    private <F> List<T> fetchTyped(DataProvider<T, F> provider, int offset, int limit) {
        return provider.fetch(new Query<>(offset, limit, listing.getColumnSorts(),
                        listing.getGrid().getDataCommunicator().getInMemorySorting(), null))
                .limit(limit).toList();
    }
}
