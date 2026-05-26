package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("PropertyListing – Holon Demo")
@Route(value = "property-listing", layout = DemoMainLayout.class)
public class PropertyListingDemoView extends Div {

    private static final NumericProperty<Long> ID = NumericProperty.longType("id");
    private static final StringProperty NAME = StringProperty.create("name");
    private static final StringProperty CATEGORY = StringProperty.create("category");
    private static final NumericProperty<Double> PRICE = NumericProperty.doubleType("price");

    private static final PropertySet<?> SET = PropertySet.builderOf(ID, NAME, CATEGORY, PRICE)
            .withIdentifier(ID).build();

    private static final PropertyBox ITEM1 = PropertyBox.builder(SET)
            .set(ID, 1L).set(NAME, "Laptop").set(CATEGORY, "Electronics").set(PRICE, 999.99).build();
    private static final PropertyBox ITEM2 = PropertyBox.builder(SET)
            .set(ID, 2L).set(NAME, "Mouse").set(CATEGORY, "Accessories").set(PRICE, 29.99).build();
    private static final PropertyBox ITEM3 = PropertyBox.builder(SET)
            .set(ID, 3L).set(NAME, "Keyboard").set(CATEGORY, "Accessories").set(PRICE, 79.99).build();
    private static final PropertyBox ITEM4 = PropertyBox.builder(SET)
            .set(ID, 4L).set(NAME, "Monitor").set(CATEGORY, "Electronics").set(PRICE, 549.00).build();

    public PropertyListingDemoView() {
        addClassName("app-view");

        var title = new H1("PropertyListing");

        var desc = new Paragraph(
                "PropertyListing is the Holon-native Grid component for PropertyBox rows. "
                + "It uses PropertySet to define columns and supports sorting, selection, "
                + "header customization, and filter integration.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(customColumnsExample());
        examples.add(singleSelectExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var listing = PropertyListing.builder(SET)
                .items(Arrays.asList(ITEM1, ITEM2, ITEM3, ITEM4))
                .build();
        ((Grid<?>) listing.getComponent()).setHeight("250px");

        return new DemoExample("Basic PropertyListing", listing.getComponent(), """
                PropertyListing.builder(SET)
                    .items(Arrays.asList(item1, item2, item3))
                    .build();""");
    }

    private DemoExample customColumnsExample() {
        var listing = PropertyListing.builder(SET)
                .items(Arrays.asList(ITEM1, ITEM2, ITEM3, ITEM4))
                .header(NAME, "Product Name")
                .header(CATEGORY, "Product Category")
                .header(PRICE, "Unit Price")
                .visibleColumns(NAME, CATEGORY, PRICE)
                .sortable(NAME, true)
                .sortable(PRICE, true)
                .build();
        ((Grid<?>) listing.getComponent()).setHeight("250px");

        return new DemoExample("Custom Headers & Sorting", listing.getComponent(), """
                PropertyListing.builder(SET)
                    .items(items)
                    .header(NAME, "Product Name")
                    .header(CATEGORY, "Product Category")
                    .visibleColumns(NAME, CATEGORY, PRICE)
                    .sortable(NAME, true)
                    .build();""");
    }

    private DemoExample singleSelectExample() {
        var listing = PropertyListing.builder(SET)
                .items(Arrays.asList(ITEM1, ITEM2, ITEM3, ITEM4))
                .selectionMode(SelectionMode.SINGLE)
                .build();
        ((Grid<?>) listing.getComponent()).setHeight("250px");

        return new DemoExample("Single Select", listing.getComponent(), """
                PropertyListing.builder(SET)
                    .items(items)
                    .selectionMode(SelectionMode.SINGLE)
                    .build();""");
    }
}
