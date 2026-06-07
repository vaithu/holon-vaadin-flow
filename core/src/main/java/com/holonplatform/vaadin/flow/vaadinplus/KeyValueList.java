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
 *
 * // From a bean — property names are automatically humanized (title-case):
 * KeyValueList fromBean = KeyValueList.from(customer);
 *
 * // Exclude specific raw field names:
 * KeyValueList filtered = KeyValueList.from(customer, "password", "salt");
 *
 * // Add a bean's properties into an existing list:
 * list.addFromBean(order);
 * list.addFromBean(order, "internalNotes");
 * }</pre>
 *
 * @see KeyValueItem
 * @see BeanToMap
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

    // ── Bean integration ────────────────────────────────────────────────────

    /**
     * Appends one {@link KeyValueItem} per readable, displayable property of {@code bean}.
     *
     * <p>Only simple scalar values are included: {@link String}, {@link Number}, {@link Boolean},
     * {@link Character}, {@link java.util.UUID}, {@link Enum}, {@link java.time.temporal.Temporal},
     * and {@link java.util.Date}.  Arrays, collections, nested objects, and {@link Class} references
     * are silently skipped — they produce no meaningful single-line display value.
     *
     * <p>Property names are humanized to Title Case via {@link BeanToMap#toTitleCase(String)}.
     * The ordering follows ID-first (exact {@code id}, then {@code *Id}), then A→Z.</p>
     *
     * @param bean source bean (not null)
     * @param <T>  bean type
     * @return this list
     */
    public <T> KeyValueList addFromBean(T bean) {
        if (bean instanceof Class<?> c) {
            throw new IllegalArgumentException(
                    "addFromBean() received a Class object (" + c.getSimpleName() + ".class). " +
                    "Pass a bean instance — e.g. addFromBean(product), not addFromBean(Product.class).");
        }
        BeanToMap.toTitleCaseMap(bean).forEach((key, value) -> {
            if (isDisplayable(value)) {
                addItem(KeyValueItem.of(key, value != null ? String.valueOf(value) : ""));
            }
        });
        return this;
    }

    /**
     * Appends one {@link KeyValueItem} per readable, displayable property of {@code bean},
     * excluding properties whose <em>raw</em> names match any entry in {@code excludeFields}.
     *
     * <pre>{@code
     * list.addFromBean(customer, "password", "createdBy", "updatedBy");
     * }</pre>
     *
     * @param bean          source bean (not null)
     * @param excludeFields raw property names to skip (e.g. {@code "password"})
     * @param <T>           bean type
     * @return this list
     */
    public <T> KeyValueList addFromBean(T bean, String... excludeFields) {
        if (bean instanceof Class<?> c) {
            throw new IllegalArgumentException(
                    "addFromBean() received a Class object (" + c.getSimpleName() + ".class). " +
                    "Pass a bean instance — e.g. addFromBean(product), not addFromBean(Product.class).");
        }
        java.util.Set<String> excluded = excludeFields != null ? java.util.Set.of(excludeFields) : java.util.Set.of();
        BeanToMap.toTitleCaseMap(bean, excluded).forEach((key, value) -> {
            if (isDisplayable(value)) {
                addItem(KeyValueItem.of(key, value != null ? String.valueOf(value) : ""));
            }
        });
        return this;
    }

    /**
     * Returns {@code true} when {@code value} can be meaningfully rendered as a single-line string.
     * Filters out nulls that should display as empty, arrays, collections, Class references,
     * and arbitrary nested objects.
     */
    private static boolean isDisplayable(Object value) {
        if (value == null) return true; // show empty string for null scalars
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof java.util.UUID
                || value instanceof Enum<?>
                || value instanceof java.time.temporal.Temporal
                || value instanceof java.util.Date;
    }

    // ── Factory methods ─────────────────────────────────────────────────────

    /**
     * Creates a {@link KeyValueList} pre-populated from all readable properties of {@code bean}.
     *
     * <pre>{@code
     * content(KeyValueList.from(customer));
     * }</pre>
     *
     * @param bean source bean (not null)
     * @param <T>  bean type
     * @return new list with one item per property
     */
    public static <T> KeyValueList from(T bean) {
        return new KeyValueList().addFromBean(bean);
    }

    /**
     * Creates a {@link KeyValueList} pre-populated from the readable properties of {@code bean},
     * excluding the specified raw field names.
     *
     * <pre>{@code
     * content(KeyValueList.from(customer, "password", "salt"));
     * }</pre>
     *
     * @param bean          source bean (not null)
     * @param excludeFields raw property names to skip
     * @param <T>           bean type
     * @return new list with one item per non-excluded property
     */
    public static <T> KeyValueList from(T bean, String... excludeFields) {
        return new KeyValueList().addFromBean(bean, excludeFields);
    }

    // ── Query ────────────────────────────────────────────────────────────────

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

