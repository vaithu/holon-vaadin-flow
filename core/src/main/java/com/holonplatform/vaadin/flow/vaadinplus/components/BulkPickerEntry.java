package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serializable;

/**
 * Represents a {@link BulkPickerItem} together with the quantity chosen by the
 * user in a {@link BulkItemPickerDialog}.
 *
 * <p>Lists of {@code BulkPickerEntry} are returned by the confirm callback:</p>
 * <pre>{@code
 * BulkItemPickerDialog.builder()
 *     .items(...)
 *     .onConfirm(entries -> entries.forEach(e -> addLine(e.item(), e.quantity())))
 *     .build();
 * }</pre>
 *
 * @param item     the selected {@link BulkPickerItem} (never null)
 * @param quantity the user-specified quantity (≥ 1)
 */
public record BulkPickerEntry(BulkPickerItem item, int quantity) implements Serializable {

    public BulkPickerEntry {
        if (item == null) throw new IllegalArgumentException("item must not be null");
        if (quantity < 1) throw new IllegalArgumentException("quantity must be ≥ 1");
    }

    /**
     * Convenience factory.
     *
     * @param item     the selected item
     * @param quantity the quantity
     * @return a new {@link BulkPickerEntry}
     */
    public static BulkPickerEntry of(BulkPickerItem item, int quantity) {
        return new BulkPickerEntry(item, quantity);
    }
}

