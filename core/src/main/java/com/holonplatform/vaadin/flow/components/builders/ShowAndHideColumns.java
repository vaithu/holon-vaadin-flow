package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.popover.Popover;

public interface ShowAndHideColumns<T> {

    void showAndHideColumns(BeanListing<T> beanListing);
    void showAndHideColumns(Grid<T> grid);

    Button getShowHideBtn();

    Popover getPopover();
}
