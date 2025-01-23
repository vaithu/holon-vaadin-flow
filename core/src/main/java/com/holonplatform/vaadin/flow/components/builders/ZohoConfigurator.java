package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.FormHeaderConfigurator;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.internal.lumo.Background;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public interface ZohoConfigurator<C extends ZohoConfigurator<C>> extends ComponentConfigurator<C>
        , HasStyleConfigurator<C>, HasSizeConfigurator<C> {

    C masterContent(Component component);

    C detailContent(Component component);

    C searchBar(Component component);
    C searchBar(SearchBarBuilder searchBarBuilder);

    C bulkAction(Component component);
    C bulkAction(BulkActionBuilder bulkActionBuilder);

    C grid(Grid<?> grid);
    C grid(Component grid);
    C grid(BeanListing<?> listing);
    C grid(PropertyListing listing);

    C gridHeader(GridHeader<?> gridHeader);

    C masterHeader(Header header);
    C detailsHeader(Header header);

    C listing(PropertyListing listing);
    C listing(BeanListing<?> listing);

    C separator(boolean separator);
    default C separator() {
        return separator(true);
    }

    C separator(Background background);

    default C withoutSeparator() {
        return separator(false);
    }

    C detailHeader(FormHeaderBuilder formHeader);
    C detailHeader(Component component);

    VerticalLayout getMasterLayout();
    VerticalLayout getDetailLayout();

    BulkActionBarBuilder<C> bulkActionBar();

    FormHeaderBarBuilder<C> formHeader();

    SearchFormBarBuilder<C> searchBar();

    interface BulkActionBarBuilder<B extends ZohoConfigurator<B>> extends BulkActionConfigurator<BulkActionBarBuilder<B>> {
        B add();
    }

    interface FormHeaderBarBuilder<B extends ZohoConfigurator<B>> extends FormHeaderConfigurator<FormHeaderBarBuilder<B>> {
        B add();
    }

    interface SearchFormBarBuilder<B extends ZohoConfigurator<B>> extends SearchBarConfigurator<SearchFormBarBuilder<B>> {
        B add();
    }

}
