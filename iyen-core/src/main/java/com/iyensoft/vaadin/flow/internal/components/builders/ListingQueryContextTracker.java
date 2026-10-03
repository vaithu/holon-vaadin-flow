package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.ListingQueryContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable holder whose non-serializable query criteria remain request-local.
 */
public final class ListingQueryContextTracker implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private transient ListingQueryContext current = ListingQueryContext.empty();

    public ListingQueryContext current() {
        if (current == null) {
            current = ListingQueryContext.empty();
        }
        return current;
    }

    public void update(ListingQueryContext context) {
        current = context != null ? context : ListingQueryContext.empty();
    }
}
