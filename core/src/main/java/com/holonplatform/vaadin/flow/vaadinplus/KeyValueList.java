package com.holonplatform.vaadin.flow.vaadinplus;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

import java.util.List;

/**
 * Container for {@link KeyValueItem} rows rendered as a flat 3-column CSS grid:
 * <pre>
 *   [key]   [:]   [value]
 *   [key]   [:]   [value]
 *   ...
 * </pre>
 *
 * <p>Each {@link KeyValueItem} uses {@code display:contents} so its three internal
 * spans become direct grid children of this container — giving perfect vertical
 * alignment across all rows without any JavaScript or manual width management.
 *
 * <p>CSS hook class: {@code kv-list}
 *
 * <p>Usage:
 * <pre>{@code
 * KeyValueList list = new KeyValueList()
 *     .addItem(KeyValueItem.of("First name", "Jane"))
 *     .addItem(KeyValueItem.of("Last name",  "Smith"))
 *     .addItem(KeyValueItem.builder()
 *                 .key("Token")
 *                 .value("abc-123-xyz")
 *                 .copyable(true)
 *                 .build());
 * }</pre>
 *
 * @see KeyValueItem
 */
@StyleSheet("context://key-value-item.css")
public class KeyValueList extends Composite<Div> {

    public KeyValueList() {
        getContent().addClassName("kv-list");
    }

    /**
     * Appends a {@link KeyValueItem} to this list. Returns {@code this} for fluent chaining.
     */
    public KeyValueList addItem(KeyValueItem item) {
        getContent().add(item);
        return this;
    }

    /**
     * Removes a specific {@link KeyValueItem} from this list. Returns {@code this} for fluent chaining.
     */
    public KeyValueList removeItem(KeyValueItem item) {
        getContent().remove(item);
        return this;
    }

    /**
     * Removes all items from this list. Returns {@code this} for fluent chaining.
     */
    public KeyValueList clearItems() {
        getContent().removeAll();
        return this;
    }

    /**
     * Returns an unmodifiable snapshot of the current items in this list.
     */
    public List<KeyValueItem> getItems() {
        return getContent().getChildren()
                .filter(KeyValueItem.class::isInstance)
                .map(KeyValueItem.class::cast)
                .toList();
    }
}

