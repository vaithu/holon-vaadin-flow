package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.PriceListItem;
import com.holonplatform.vaadin.flow.UnorderedPriceList;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPriceList {

    @Test
    void testUnorderedPriceListClassName() {
        var list = new UnorderedPriceList();
        assertTrue(list.getClassNames().contains("price-list"));
    }

    @Test
    void testPriceListItemClassName() {
        var item = new PriceListItem(new Span("9:00-12:00"), new Span("$25"));
        assertTrue(item.getClassNames().contains("price-list__item"));
    }

    @Test
    void testPriceListItemChildren() {
        var time = new Span("9:00-12:00");
        var price = new Span("$25");
        var item = new PriceListItem(time, price);
        assertEquals(2, item.getComponentCount());
    }

    @Test
    void testAddItemsToList() {
        var list = new UnorderedPriceList();
        list.add(
                new PriceListItem(new Span("Morning"), new Span("$10")),
                new PriceListItem(new Span("Evening"), new Span("$20"))
        );
        assertEquals(2, list.getComponentCount());
    }
}
