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
 * <p><strong>Field display mode</strong> — call {@link #asFields()} (or
 * {@link #setFieldDisplay(boolean) setFieldDisplay(true)}) to switch to the
 * stacked "label above value" two-column layout used in shipping summaries,
 * order details, and dashboard cards:
 * <pre>
 *   Tracking Number        ·
 *   1Z099E7R1367106342
 *
 *   Service                Shipped / Billed On
 *   UPS Next Day Air Saver® 06/30/2026
 * </pre>
 *
 * <p>CSS hook class: {@code kv-list} / {@code kv-list--fields}
 *
 * <p>Usage:
 * <pre>{@code
 * // Standard horizontal key : value list
 * KeyValueList list = new KeyValueList()
 *     .addItem(KeyValueItem.of("First name", "Jane"))
 *     .addItem(KeyValueItem.of("Last name",  "Smith"));
 *
 * // Field display (stacked, 2-column grid)
 * KeyValueList fields = new KeyValueList().asFields()
 *     .addItem(KeyValueItem.of("Tracking Number", "1Z099E7R1367106342"))
 *     .addItem(KeyValueItem.of("Service",         "UPS Next Day Air Saver®"))
 *     .addItem(KeyValueItem.of("Shipped / Billed On", "06/30/2026"))
 *     .addItem(KeyValueItem.of("Delivered On",    "07/01/2026 10:40 A.M."))
 *     .addItem(KeyValueItem.of("Delivery Location", "Package room"))
 *     .addItem(KeyValueItem.of("Delivered To",    "CLEVELAND, OH, US"));
 * }</pre>
 *
 * @see KeyValueItem
 * @see BeanToMap
 */
@StyleSheet("context://key-value-item.css")
public class KeyValueList extends Composite<Div> {

    public KeyValueList() {
        getContent().addClassName("kv-list");
        // Announce as a list so screen readers enumerate items correctly
        getContent().getElement().setAttribute("role", "list");
    }

    // ── Display mode ─────────────────────────────────────────────────────────

    /**
     * Switches to <em>field display mode</em> — a stacked "label above value"
     * two-column responsive grid, matching the UPS tracking / Stripe dashboard
     * detail pattern.
     *
     * <p>Equivalent to {@code setFieldDisplay(true)}. Returns {@code this} for
     * fluent chaining so it can be called immediately after construction:</p>
     *
     * <pre>{@code
     * new KeyValueList().asFields()
     *     .addItem(KeyValueItem.of("Tracking Number", "1Z099E7R1367106342"))
     *     .addItem(KeyValueItem.of("Service",         "UPS Next Day Air Saver®"))
     *     ...
     * }</pre>
     *
     * @return {@code this}
     */
    public KeyValueList asFields() {
        return setFieldDisplay(true);
    }

    /**
     * Toggles between the standard horizontal key-colon-value layout and the
     * stacked field-display layout.
     *
     * <ul>
     *   <li>{@code true}  — adds {@code kv-list--fields}; removes card border/shadow.
     *       Items render as stacked label + value cells in a 2-column grid.</li>
     *   <li>{@code false} — removes {@code kv-list--fields}; restores the default
     *       3-column horizontal grid with card wrapper.</li>
     * </ul>
     *
     * @param fields {@code true} for field display mode
     * @return {@code this}
     */
    public KeyValueList setFieldDisplay(boolean fields) {
        if (fields) {
            getContent().addClassName("kv-list--fields");
        } else {
            getContent().removeClassName("kv-list--fields");
        }
        return this;
    }

    /**
     * Returns {@code true} if this list is currently in field display mode.
     */
    public boolean isFieldDisplay() {
        return getContent().hasClassName("kv-list--fields");
    }

    // ── Summary / no-card variant ─────────────────────────────────────────

    /**
     * Switches to <em>summary display mode</em> — transparent background, no border,
     * no shadow. Use inside a card or panel that already provides the chrome.
     *
     * <p>Each row gains a subtle bottom rule so rows remain readable without the
     * outer card frame. Equivalent to {@code setSummaryDisplay(true)}.</p>
     *
     * <pre>{@code
     * // Inside a Nexus-style summary-card section
     * new KeyValueList().asSummary()
     *     .addItem(KeyValueItem.withDelta("Open tickets",  "47", "−12%",  DeltaDirection.DOWN))
     *     .addItem(KeyValueItem.withDelta("Monthly MRR",  "$84,120", "+4.6%", DeltaDirection.UP))
     *     .addItem(KeyValueItem.pill("SLA compliance", "98.2%", PillVariant.SUCCESS))
     * }</pre>
     *
     * @return {@code this}
     */
    public KeyValueList asSummary() {
        return setSummaryDisplay(true);
    }

    /**
     * Toggles the summary (no-card) display mode.
     *
     * @param summary {@code true} to remove the card border and shadow
     * @return {@code this}
     */
    public KeyValueList setSummaryDisplay(boolean summary) {
        if (summary) {
            getContent().addClassName("kv-summary");
        } else {
            getContent().removeClassName("kv-summary");
        }
        return this;
    }

    /**
     * Returns {@code true} if this list is in summary (no-card) display mode.
     */
    public boolean isSummaryDisplay() {
        return getContent().hasClassName("kv-summary");
    }

    // ── Dense mode ────────────────────────────────────────────────────────

    /**
     * Applies compact vertical rhythm to every row in this list. Matches the
     * {@code kv-row.dense} pattern from the design reference — tighter row-gap for
     * confirmation steps and review screens.
     *
     * <p>Equivalent to {@code setDenseMode(true)}.</p>
     *
     * <pre>{@code
     * new KeyValueList().asDense()
     *     .addItem(KeyValueItem.of("Item description", "14k gold chain, 22\""))
     *     .addItem(KeyValueItem.of("Loan amount",      "$400.00").setValueNumeric(true))
     *     .addItem(KeyValueItem.of("Maturity date",    "Sep 24, 2026"))
     * }</pre>
     *
     * @return {@code this}
     */
    public KeyValueList asDense() {
        return setDenseMode(true);
    }

    /**
     * Toggles compact row spacing for the whole list.
     *
     * @param dense {@code true} for compact row-gap
     * @return {@code this}
     */
    public KeyValueList setDenseMode(boolean dense) {
        if (dense) {
            getContent().addClassName("kv-dense");
        } else {
            getContent().removeClassName("kv-dense");
        }
        return this;
    }

    /**
     * Returns {@code true} if this list is in dense (compact) mode.
     */
    public boolean isDenseMode() {
        return getContent().hasClassName("kv-dense");
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

