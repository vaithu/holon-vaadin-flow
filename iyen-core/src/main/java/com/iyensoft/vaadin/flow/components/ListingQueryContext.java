package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QuerySort;

/**
 * Active search and Holon query configuration used by a {@link ListingBundle} fetch.
 *
 * @param searchText current search text, never {@code null}
 * @param queryFilter current advanced filter, or {@code null}
 * @param querySort current Holon sort, or {@code null}
 */
public record ListingQueryContext(
        String searchText,
        QueryFilter queryFilter,
        QuerySort querySort) implements QueryConfigurationProvider {

    public ListingQueryContext {
        searchText = searchText != null ? searchText : "";
    }

    @Override
    public QueryFilter getQueryFilter() {
        return queryFilter;
    }

    @Override
    public QuerySort getQuerySort() {
        return querySort;
    }

    public static ListingQueryContext empty() {
        return new ListingQueryContext("", null, null);
    }
}
