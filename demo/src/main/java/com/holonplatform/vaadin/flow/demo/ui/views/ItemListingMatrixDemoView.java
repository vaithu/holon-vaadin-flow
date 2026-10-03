package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.ItemListingMatrix;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.stream.IntStream;

/**
 * The same data and columns shown as a Grid or as a DBeaver-style record matrix.
 */
@PageTitle("ItemListing Matrix - Holon Demo")
@Route(value = "item-listing-matrix", layout = DemoMainLayout.class)
public class ItemListingMatrixDemoView extends Div {

    public ItemListingMatrixDemoView() {
        addClassName("app-view");
        add(new H1("ItemListing matrix view"));
        add(new Paragraph("Use the switch to transpose the current page: fields become rows "
                + "and records become columns. The Pagination control fetches only five records "
                + "at a time, even when the source is lazy."));
        add(createExample().getComponent());
    }

    static BeanListing<BeanListingDemoView.Product> createExample() {
        var products = IntStream.rangeClosed(1, 23)
                .mapToObj(i -> new BeanListingDemoView.Product(i, "Product " + i,
                        i % 2 == 0 ? "Accessories" : "Electronics", i * 12.50,
                        i % 3 != 0, BeanListingDemoView.Status.AVAILABLE,
                        LocalDate.of(2026, 6, 1).plusDays(i)))
                .toList();
        BeanListing<BeanListingDemoView.Product> listing =
                BeanListing.builder(BeanListingDemoView.Product.class, true)
                        .header("id", "ID")
                        .header("name", "Product")
                        .header("category", "Category")
                        .header("price", "Price")
                        .header("status", "Status")
                        .header("active", "Active")
                        .header("addedOn", "Added on")
                        .matrixView(5)
                        .matrixHeading(BeanListingDemoView.Product::getName)
                        .matrixValue("price", product -> "€" + String.format("%.2f", product.getPrice()))
                        .height("450px")
                        .build();
        listing.setItems(q -> products.stream().skip(q.getOffset()).limit(q.getLimit()));
        return listing;
    }
}
