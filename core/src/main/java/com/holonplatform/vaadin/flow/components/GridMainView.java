package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.HasBulkAction;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.internal.CrudResults;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;

public interface GridMainView<T> extends CrudResults, HasGridView<T>, HasBeanRecord<T>, HasBulkAction<T> {
    MenuBar createBulkActionMenuBar();

    LabelBuilder<Span> createBulkActionLabelBuilder();

    SearchBarBuilder createSearchBarBuilder();

    MenuBar createSearchOptionsMenuBar();

    BulkActionBuilder createBulkActionBuilder();

    void showSearchBar(boolean visible);

    void showBulkActionBar(boolean visible);

    Component addMobileComponentColumn(T beanInstance);

    void newBtnActionPerformed(ButtonConfigurator.BaseButtonConfigurator buttonConfigurator);

    BeanListingBuilder<T> getBeanListingBuilderForMobile();

    BeanListingBuilder<T> getBeanListingBuilderForDesktop();

    void setBeanListingBuilder(BeanListingBuilder<T> beanListingBuilder);
}
