package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serializable;

/**
 * Immutable value object representing a single pickable item in a
 * {@link BulkItemPickerDialog}.
 *
 * <p>Instances are created via the static factory {@link #of(String, String, String, double)}
 * or the canonical record constructor.</p>
 *
 * @param id           unique item identifier (not blank)
 * @param name         display name shown in the list
 * @param sku          stock-keeping unit / code label
 * @param purchaseRate per-unit purchase rate or price
 */
public record BulkPickerItem(String id, String name, String sku, double purchaseRate)
        implements Serializable {

    /**
     * Compact canonical constructor — validates the id.
     */
    public BulkPickerItem {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("BulkPickerItem id must not be blank");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("BulkPickerItem name must not be blank");
    }

    /**
     * Convenience factory.
     *
     * @param id           unique item identifier
     * @param name         display name
     * @param sku          SKU / code label
     * @param purchaseRate per-unit rate
     * @return a new {@link BulkPickerItem}
     */
    public static BulkPickerItem of(String id, String name, String sku, double purchaseRate) {
        return new BulkPickerItem(id, name, sku, purchaseRate);
    }
}

