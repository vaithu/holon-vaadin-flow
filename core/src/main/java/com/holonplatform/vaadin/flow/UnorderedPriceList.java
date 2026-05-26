package com.holonplatform.vaadin.flow;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.UnorderedList;

/**
 * An unordered list ({@code <ul>}) used as a price/time list container.
 * Individual rows should be added as {@link PriceListItem} instances.
 * Styled via {@code price-list.css}.
 *
 * <p>BEM root: {@code .price-list}</p>
 *
 * @since 10.0.0
 */
@StyleSheet("context://price-list.css")
public class UnorderedPriceList extends UnorderedList {

    /** Creates a new empty {@link UnorderedPriceList}. */
    public UnorderedPriceList() {
        addClassName("price-list");
    }

}