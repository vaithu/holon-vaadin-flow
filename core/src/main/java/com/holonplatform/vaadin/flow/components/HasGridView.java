package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;

import java.util.Optional;

public interface HasGridView<T> {
    BeanListing<T> getBeanListing();

    Optional<T> getCurrentItem();

    void setCurrentItem(T currentItem);

    void setCurrentItem(GridLazyDataView<T> gridLazyDataView);

    void refreshGrid();
    void refreshGrid(String searchText);
//    Component createGrid(BeanListingBuilder<T> beanListingBuilder, boolean mobile);
    Component createGrid( boolean mobile);
    Component createGrid( );

    void showAndHideGridColumns(boolean visible);

    void refreshItem(T beanInstance);

    void addGridSelectionEvent(Selectable.SelectionEvent<T> selectionEvent);

    void highlightEditedItem(T beanInstance);

}
