package com.holonplatform.vaadin.flow;

import com.holonplatform.vaadin.flow.components.GridDetailView;
import com.holonplatform.vaadin.flow.components.GridMainView;
import com.holonplatform.vaadin.flow.components.builders.ZohoBuilder;

public interface ZohoView<T>  {

//    ZohoBuilder createZohoView(boolean mobile);

    void updatePageURL(T beanInstance);
   /* void updateBeanListingListeners();

    void updateBeanListing();*/

    void processParameters(T beanInstance, Long id);

    void updateBeanListingListeners();

    ZohoBuilder createDesktopView();

    ZohoBuilder createMobileView();

    GridMainView<T> getGridMainView();

    void setGridMainView(GridMainView<T> gridMainView);

    GridDetailView<T> getGridDetailView();

    void setGridDetailView(GridDetailView<T> gridDetailView);

//    void withPostProcessor(Consumer<ZohoView<T>> postProcessor);

    /*static ZohoView<T> of(GridMainView<T> gridMainView, GridDetailView<T> gridDetailView) {
        return new
    }*/



}
