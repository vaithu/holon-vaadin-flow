package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serializable;

/**
 * Immutable query parameter object passed to the paginated item provider of
 * {@link BulkItemPickerDialog}.
 *
 * <p>Carries three values that mirror Spring Data's {@code Pageable} semantics:</p>
 * <ul>
 *   <li>{@link #query()} — verbatim search text entered by the user
 *       (empty string for the initial / unfiltered load)</li>
 *   <li>{@link #offset()} — 0-based index of the first row to return</li>
 *   <li>{@link #limit()} — maximum number of rows to return</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * BulkItemPickerDialog.builder()
 *     .pagedItemProvider(
 *         q -> repo.findByNameOrSku(q.query(), q.offset(), q.limit()),
 *         q -> repo.countByNameOrSku(q)
 *     )
 *     .build().open();
 * }</pre>
 *
 * @param query  verbatim search text (never null; may be empty)
 * @param offset 0-based row offset (≥ 0)
 * @param limit  maximum rows to return (≥ 1)
 *
 * @see BulkItemPickerDialog#setPagedProvider(java.util.function.Function, java.util.function.Function)
 */
public record BulkPickerFetchQuery(String query, int offset, int limit) implements Serializable {

    public BulkPickerFetchQuery {
        if (query == null) throw new IllegalArgumentException("query must not be null");
        if (offset < 0)  throw new IllegalArgumentException("offset must be >= 0");
        if (limit  < 1)  throw new IllegalArgumentException("limit must be >= 1");
    }

    /**
     * Returns the 0-based page number derived from offset and limit.
     *
     * @return {@code offset / limit}
     */
    public int pageNumber() {
        return offset / limit;
    }
}

