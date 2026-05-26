package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serializable;

/**
 * An item in a {@link TransferList}, identified by an id and rendered as a label.
 *
 * <p>Create instances via the static factories:</p>
 * <pre>{@code
 * TransferItem.of("id-1", "Coffee Table")
 * TransferItem.of("Coffee Table")       // id == label
 * }</pre>
 *
 * @param id    unique, stable item identifier used for equality and tracking
 * @param label human-readable display label shown in the list
 */
public record TransferItem(String id, String label) implements Serializable {

    public TransferItem {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("TransferItem id must not be blank");
        if (label == null)
            throw new IllegalArgumentException("TransferItem label must not be null");
    }

    /**
     * Creates a {@code TransferItem} with a distinct id and label.
     *
     * @param id    unique identifier
     * @param label human-readable label
     * @return new instance
     */
    public static TransferItem of(String id, String label) {
        return new TransferItem(id, label);
    }

    /**
     * Creates a {@code TransferItem} where the label is also used as the id.
     *
     * @param value the id and label
     * @return new instance
     */
    public static TransferItem of(String value) {
        return new TransferItem(value, value);
    }
}

