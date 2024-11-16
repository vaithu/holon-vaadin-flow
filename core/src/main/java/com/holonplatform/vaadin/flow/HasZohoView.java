package com.holonplatform.vaadin.flow;

import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;

public interface HasZohoView {

    MenuBar createBulkActionMenuBar();

    LabelBuilder<Span> createBulkActionLabelBuilder();
    LabelBuilder<?> createFormHeaderLabelBuilder();

    SearchBarBuilder createSearchBarBuilder();

    MenuBar createSearchOptionsMenuBar();

    BulkActionBuilder createBulkActionBuilder();

    void refreshGrid();
    void refreshGrid(String searchText);

    Component createGrid();

    void updateDetailHeaderLabel();

    FormHeaderBuilder createDetailHeader();

    Component createDetailContent();

    void showSearchBar(boolean visible);

    void showBulkActionBar(boolean visible);

    Component createDetailMenuBar();

}
