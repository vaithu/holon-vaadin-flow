/*
 * Copyright 2016-2019 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.internal.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

/**
 * Support class to handle the <em>additional items</em> of a data provider in a
 * paging-aware way.
 * <p>
 * Additional items are always provided <em>before</em> any item returned by the
 * concrete data provider, so the resulting item sequence is the concatenation
 * of the additional items and the back end items. This class takes care of
 * translating the {@link Query} offset and limit, which refer to such
 * concatenated sequence, into the offset and limit to be used to query the
 * concrete data provider.
 * </p>
 * <p>
 * Without this translation, the additional items would be prepended to
 * <em>every</em> requested page, causing the same items to be repeated on each
 * page and each page to return more items than the requested limit.
 * </p>
 *
 * @since 5.2.2
 */
final class AdditionalItemsSupport {

	private AdditionalItemsSupport() {
	}

	/**
	 * Fetch the items for given <code>query</code>, taking into account the
	 * <code>additionalItems</code> which have to be provided before any item
	 * returned by the concrete <code>dataProvider</code>.
	 * @param <T>             Data type
	 * @param <F>             Filter type
	 * @param additionalItems The additional items (not null)
	 * @param dataProvider    The concrete data provider (not null)
	 * @param query           The data provider query (not null)
	 * @return The item stream for the requested page
	 */
	static <T, F> Stream<T> fetch(List<T> additionalItems, DataProvider<T, F> dataProvider, Query<T, F> query) {
		if (additionalItems.isEmpty()) {
			return dataProvider.fetch(query);
		}

		final int additionalCount = additionalItems.size();
		final int offset = query.getOffset();
		final int limit = query.getLimit();

		// the portion of the requested page which is covered by the additional items
		final int additionalFrom = Math.min(offset, additionalCount);
		// use long arithmetic: the limit may be Integer.MAX_VALUE
		final int additionalTo = (int) Math.min((long) offset + (long) limit, additionalCount);
		final int additionalTaken = Math.max(0, additionalTo - additionalFrom);

		// the remaining part of the page has to be retrieved from the back end
		final int backEndOffset = Math.max(0, offset - additionalCount);
		final int backEndLimit = (limit == Integer.MAX_VALUE) ? Integer.MAX_VALUE : (limit - additionalTaken);

		// defensive copy: the returned stream is lazy and the additional items may change
		final Stream<T> additionalStream = (additionalTaken > 0)
				? new ArrayList<>(additionalItems.subList(additionalFrom, additionalTo)).stream()
				: Stream.empty();

		if (backEndLimit <= 0) {
			// the requested page is entirely covered by the additional items
			return additionalStream;
		}

		final Stream<T> backEndStream = dataProvider
				.fetch(withPaging(query, backEndOffset, backEndLimit));

		return (additionalTaken > 0) ? Stream.concat(additionalStream, backEndStream) : backEndStream;
	}

	/**
	 * Create a copy of given <code>query</code> using the provided
	 * <code>offset</code> and <code>limit</code>, keeping any sort and filter
	 * configuration.
	 * @param <T>    Data type
	 * @param <F>    Filter type
	 * @param query  The source query (not null)
	 * @param offset The offset to set
	 * @param limit  The limit to set
	 * @return A new {@link Query} with the given paging configuration
	 */
	private static <T, F> Query<T, F> withPaging(Query<T, F> query, int offset, int limit) {
		if (query.getOffset() == offset && query.getLimit() == limit) {
			return query;
		}
		return new Query<>(offset, limit, query.getSortOrders(), query.getInMemorySorting(),
				query.getFilter().orElse(null));
	}

}

