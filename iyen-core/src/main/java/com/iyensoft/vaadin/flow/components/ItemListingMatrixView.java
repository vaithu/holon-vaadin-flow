package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.shared.Registration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Two presentations of the same ItemListing: its Grid, or a transposed,
 * paginated comparison of the currently filtered and sorted items.
 *
 * <p>Register a text presenter for every visible property. Grid renderers
 * cannot be read back as text, so component/virtual columns must be presented
 * explicitly or hidden from the listing before enabling matrix mode.</p>
 */
@StyleSheet("context://item-listing-matrix-view.css")
public class ItemListingMatrixView<T, P> extends Div {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int MAX_PAGE_SIZE = 10;

    private final ItemListing<T, P> listing;
    private final ItemListingPaginationBar<T, P> pagination;
    private final Switch modeSwitch = new Switch();
    private final Div matrixSlot = new Div();
    private final Map<P, Function<T, String>> presenters = new LinkedHashMap<>();
    private Function<T, String> itemHeading;
    private Registration dataRegistration;
    private Registration sortRegistration;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int originalGridPageSize;
    private boolean refreshScheduled;

    public ItemListingMatrixView(ItemListing<T, P> listing) {
        this.listing = Objects.requireNonNull(listing, "listing");
        this.pagination = new ItemListingPaginationBar<>(listing);
        addClassName("item-listing-matrix-view");
        modeSwitch.setLabel(LocalizationProvider.localize("Matrix view", "listing.matrix.toggle"));
        modeSwitch.addValueChangeListener(event -> {
            if (event.getValue()) {
                originalGridPageSize = listing.getGrid().getPageSize();
                listing.getGrid().setPageSize(pageSize);
                pagination.refreshState();
                pagination.goToFirstPage();
            } else {
                listing.getGrid().setPageSize(originalGridPageSize);
            }
            listing.getComponent().setVisible(!event.getValue());
            matrixSlot.setVisible(event.getValue());
            pagination.setVisible(event.getValue());
        });
        pagination.setVisible(false);
        matrixSlot.setVisible(false);
        pagination.addPageChangeListener(page -> {
            if (modeSwitch.getValue()) {
                renderMatrix();
            }
        });
        add(modeSwitch, listing.getComponent(), matrixSlot, pagination);
    }

    public ItemListingMatrixView<T, P> itemHeading(Function<T, String> heading) {
        this.itemHeading = Objects.requireNonNull(heading, "heading");
        return this;
    }

    public ItemListingMatrixView<T, P> value(P property, Function<T, String> presenter) {
        presenters.put(Objects.requireNonNull(property, "property"),
                Objects.requireNonNull(presenter, "presenter"));
        return this;
    }

    /**
     * Maximum number of record columns per page (1-10).
     */
    public ItemListingMatrixView<T, P> pageSize(int size) {
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Matrix page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        pageSize = size;
        if (modeSwitch.getValue()) {
            listing.getGrid().setPageSize(size);
            pagination.refreshState();
            pagination.goToFirstPage();
        }
        return this;
    }

    public Switch getModeSwitch() {
        return modeSwitch;
    }

    public ItemListing<T, P> getListing() {
        return listing;
    }

    public ItemListingPaginationBar<T, P> getPagination() {
        return pagination;
    }

    public ComparisonMatrix getMatrix() {
        return matrixSlot.getChildren().filter(ComparisonMatrix.class::isInstance)
                .map(ComparisonMatrix.class::cast).findFirst().orElse(null);
    }

    public void refreshMatrix() {
        if (modeSwitch.getValue()) {
            pagination.refreshState();
            renderMatrix();
        }
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        dataRegistration = listing.getDataProvider().addDataProviderListener(change -> {
            scheduleRefresh(event);
        });
        sortRegistration = listing.getGrid().addSortListener(change -> scheduleRefresh(event));
    }

    private void scheduleRefresh(AttachEvent event) {
        if (modeSwitch.getValue() && !refreshScheduled) {
            refreshScheduled = true;
            event.getUI().beforeClientResponse(this, context -> {
                refreshScheduled = false;
                pagination.refreshState();
                pagination.goToFirstPage();
            });
        }
    }

    @Override
    protected void onDetach(DetachEvent event) {
        if (dataRegistration != null) {
            dataRegistration.remove();
            dataRegistration = null;
        }
        if (sortRegistration != null) {
            sortRegistration.remove();
            sortRegistration = null;
        }
        super.onDetach(event);
    }

    private void renderMatrix() {
        List<P> columns = listing.getVisibleColumns();
        if (itemHeading == null) {
            throw new IllegalStateException("Configure an item heading before showing matrix view");
        }
        for (P column : columns) {
            if (!presenters.containsKey(column)) {
                throw new IllegalStateException("No matrix value presenter for " + column);
            }
        }
        int page = pagination.getCurrentPage();
        List<T> items = fetchPage(listing.getDataProvider(), (page - 1) * pageSize,
                pageSize + 1, listing.getColumnSorts());
        boolean hasNext = items.size() > pageSize;
        if (pagination.getTotalPages() <= page) {
            pagination.setHasNextPage(hasNext);
        }

        ComparisonMatrix matrix = Components.comparisonMatrix()
                .heading(LocalizationProvider.localize("Field", "listing.matrix.field"))
                .build();
        items.stream().limit(pageSize).forEach(item ->
                matrix.addColumn(Objects.requireNonNull(itemHeading.apply(item), "item heading")));
        if (items.isEmpty()) {
            matrixSlot.removeAll();
            Span empty = new Span(LocalizationProvider.localize("No records", "listing.matrix.empty"));
            matrixSlot.add(empty);
            return;
        }
        for (P column : columns) {
            String label = listing.getColumnHeader(column).orElse(String.valueOf(column));
            String[] values = items.stream().limit(pageSize).map(item -> {
                String value = presenters.get(column).apply(item);
                return value == null ? "" : value;
            }).toArray(String[]::new);
            matrix.addRow(label, values);
        }
        matrixSlot.removeAll();
        matrixSlot.add(matrix);
    }

    private static <T, F> List<T> fetchPage(DataProvider<T, F> provider, int offset,
                                             int limit, List<QuerySortOrder> sort) {
        return provider.fetch(new Query<>(offset, limit, sort, null, null))
                .limit(limit).toList();
    }
}
