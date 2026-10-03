package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Common empty-state compositions for data-driven views.
 *
 * <p>Applications can use these presets directly or build a custom {@link Empty} when the
 * empty state needs a domain-specific action or illustration.</p>
 */
public final class EmptyStates {

    private EmptyStates() {
    }

    /**
     * Creates an empty state for a collection that has no records.
     *
     * @param title title to display (not null)
     * @param description optional supporting description
     * @return configured empty state
     */
    public static Empty noItems(String title, String description) {
        return create(VaadinIcon.INBOX, title, description);
    }

    /**
     * Creates an empty state for a search or filter that returned no records.
     *
     * @return configured empty state
     */
    public static Empty noResults() {
        return create(VaadinIcon.SEARCH, "No results found",
                "Try different search criteria or clear the filters.");
    }

    /**
     * Creates an empty state for a related collection.
     *
     * @param title title to display (not null)
     * @return configured empty state
     */
    public static Empty relatedItems(String title) {
        return create(VaadinIcon.LIST, title, null);
    }

    /**
     * Creates an empty state for a related collection with supporting text.
     *
     * @param title title to display (not null)
     * @param description optional supporting description
     * @return configured empty state
     */
    public static Empty relatedItems(String title, String description) {
        return create(VaadinIcon.LIST, title, description);
    }

    private static Empty create(VaadinIcon icon, String title, String description) {
        Empty empty = Empty.builder()
                .icon(new Icon(icon))
                .title(title)
                .build();
        if (description != null && !description.isBlank()) {
            empty.setDescription(description);
        }
        return empty;
    }
}
