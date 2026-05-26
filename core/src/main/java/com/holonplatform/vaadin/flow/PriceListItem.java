package com.holonplatform.vaadin.flow;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

/**
 * A single row in {@link UnorderedPriceList} — displays a time/label span
 * and a price span side-by-side. Styled via {@code price-list.css}.
 *
 * <p>BEM element: {@code .price-list__item}</p>
 *
 * @since 10.0.0
 */
@StyleSheet("context://price-list.css")
public class PriceListItem extends ListItem {

    /**
     * Creates a new {@link PriceListItem}.
     *
     * @param timeSpan  left span (time / label)
     * @param priceSpan right span (price)
     */
    public PriceListItem(Span timeSpan, Span priceSpan) {
        addClassName("price-list__item");
        add(timeSpan, priceSpan);
    }

}