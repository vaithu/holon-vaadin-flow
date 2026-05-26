package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.PriceListItem;
import com.holonplatform.vaadin.flow.UnorderedPriceList;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("PriceList – Holon Demo")
@Route(value = "price-list", layout = DemoMainLayout.class)
public class PriceListDemoView extends Div {

    public PriceListDemoView() {
        addClassName("app-view");

        var title = new H1("PriceList");

        var desc = new Paragraph(
                "An unordered list container (UnorderedPriceList) with PriceListItem rows. "
                + "Each row shows a time/label span and a price span side-by-side. "
                + "Styled via price-list.css using BEM naming (.price-list, .price-list__item).");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(menuPricingExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var list = new UnorderedPriceList();
        list.add(
                new PriceListItem(new Span("9:00 – 12:00"), new Span("$25.00")),
                new PriceListItem(new Span("12:00 – 17:00"), new Span("$35.00")),
                new PriceListItem(new Span("17:00 – 21:00"), new Span("$45.00"))
        );

        return new DemoExample("Time Slots with Pricing", list, """
                var list = new UnorderedPriceList();
                list.add(
                    new PriceListItem(new Span("9:00 – 12:00"),  new Span("$25.00")),
                    new PriceListItem(new Span("12:00 – 17:00"), new Span("$35.00")),
                    new PriceListItem(new Span("17:00 – 21:00"), new Span("$45.00"))
                );""");
    }

    private DemoExample menuPricingExample() {
        var list = new UnorderedPriceList();
        list.add(
                new PriceListItem(new Span("Espresso"), new Span("$3.50")),
                new PriceListItem(new Span("Cappuccino"), new Span("$4.75")),
                new PriceListItem(new Span("Flat White"), new Span("$5.00")),
                new PriceListItem(new Span("Mocha"), new Span("$5.50"))
        );

        return new DemoExample("Menu Pricing", list, """
                var list = new UnorderedPriceList();
                list.add(
                    new PriceListItem(new Span("Espresso"),   new Span("$3.50")),
                    new PriceListItem(new Span("Cappuccino"), new Span("$4.75")),
                    new PriceListItem(new Span("Flat White"), new Span("$5.00")),
                    new PriceListItem(new Span("Mocha"),      new Span("$5.50"))
                );""");
    }
}
