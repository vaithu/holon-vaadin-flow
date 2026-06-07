package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.ZohoView;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.builders.ZohoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractZohoView<T> implements ZohoView<T> {


    private static final Logger log = LoggerFactory.getLogger(AbstractZohoView.class);
    private GridMainView<T> gridMainView;
    private GridDetailView<T> gridDetailView;

    public AbstractZohoView(GridMainView<T> gridMainView, GridDetailView<T> gridDetailView) {
        this();
        setGridMainView(gridMainView);
        setGridDetailView(gridDetailView);
    }

    public AbstractZohoView() {
        super();
    }

    @Override
    public void processParameters(T beanInstance, Long id) {
        Optional.ofNullable(beanInstance)
                .ifPresentOrElse(item -> {
                    updatePageURL(item);
                    gridMainView.setCurrentItem(item);
                    gridMainView.getBeanListing().select(item);
                }, () -> {
                    Components.notification()
                            .error()
                            .autoClose(false)
                            .text(String.format(" %d is not found in the table", id));
                });
    }


    private void updateOperations(T beanInstance) {

        this.gridDetailView.updateDetailHeaderLabel(beanInstance);
        log.info("Updated item is {}", beanInstance);
        this.gridMainView.setCurrentItem(beanInstance);
        updatePageURL(beanInstance);

    }
    @Override
    public void updateBeanListingListeners() {
        this.gridMainView.getBeanListing().addItemClickListener(tItemClickEvent -> {
            updateOperations(tItemClickEvent.getItem());
        });

        this.gridMainView.getBeanListing().addSelectionListener((Selectable.SelectionListener<T>) selectionEvent -> {
            selectionEvent.getFirstSelectedItem().ifPresent(this::updateOperations);
        });
    }

    private ZohoBuilder createZohoView() {

        return ZohoBuilder.create()
                .bulkAction(getGridMainView().createBulkActionBuilder())
                .searchBar(getGridMainView().createSearchBarBuilder());

    }
    @Override
    public ZohoBuilder createDesktopView() {
        return createZohoView()
                .grid(getGridMainView().createGrid())
                .separator()
                .detailHeader(getGridDetailView().createDetailHeader())
                .detailContent(getGridDetailView().createDetailContent());
    }
    @Override
    public ZohoBuilder createMobileView() {
        final ZohoBuilder zohoView = createZohoView();
        zohoView.grid(gridMainView.createGrid(true));
        return zohoView;
    }
    @Override
    public GridMainView<T> getGridMainView() {
        return gridMainView;
    }
    @Override
    public void setGridMainView(GridMainView<T> gridMainView) {
        this.gridMainView = gridMainView;
    }
    @Override
    public GridDetailView<T> getGridDetailView() {
        return gridDetailView;
    }
    @Override
    public void setGridDetailView(GridDetailView<T> gridDetailView) {
        this.gridDetailView = gridDetailView;
    }

    private void setCurrentItem() {
        final BeanListing<T> beanListing = getGridMainView().getBeanListing();

        beanListing.getFirstSelectedItem().ifPresentOrElse(this::updateOperations, () ->
                beanListing.getFirstItem().ifPresent(this::updateOperations));
    }

    @SuppressWarnings("unused") // called reflectively or reserved for future use
    private final void postProcessor() {
        gridMainView.refreshGrid();
        setCurrentItem();
    }

    @SuppressWarnings("unused") // called reflectively or reserved for future use
    private final void preProcessor() {

//           updateBeanListingListeners();
//           setCurrentItem();

//        updateBeanListing();

//        getGridMainView().refreshGrid();
//        setCurrentItem();

    }


}
