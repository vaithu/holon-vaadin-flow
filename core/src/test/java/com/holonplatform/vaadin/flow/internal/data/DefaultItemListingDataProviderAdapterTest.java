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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

/**
 * Tests for the <em>additional items</em> paging behaviour of
 * {@link DefaultItemListingDataProviderAdapter}.
 */
class DefaultItemListingDataProviderAdapterTest {

	private static final int BACK_END_SIZE = 100;

	/**
	 * A back end data provider which honours the query offset and limit and records the
	 * paging of every received query.
	 */
	private static final class BackEnd extends AbstractDataProvider<String, Void> {

		@Serial
		private static final long serialVersionUID = 1L;

		private final List<String> items = IntStream.range(0, BACK_END_SIZE).mapToObj(i -> "b" + i).toList();

		private final transient List<int[]> receivedPaging = new ArrayList<>();

		@Override
		public boolean isInMemory() {
			return false;
		}

		@Override
		public int size(Query<String, Void> query) {
			return items.size();
		}

		@Override
		public Stream<String> fetch(Query<String, Void> query) {
			receivedPaging.add(new int[] { query.getOffset(), query.getLimit() });
			return items.stream().skip(query.getOffset()).limit(query.getLimit());
		}
	}

	private static Query<String, Void> query(int offset, int limit) {
		return new Query<>(offset, limit, Collections.emptyList(), null, null);
	}

	private static List<String> fetch(DefaultItemListingDataProviderAdapter<String, Void> adapter, int offset,
			int limit) {
		return adapter.fetch(query(offset, limit)).toList();
	}

	@Test
	void fetch_withoutAdditionalItems_delegatesQueryUnchanged() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);

		assertThat(fetch(adapter, 10, 5)).containsExactly("b10", "b11", "b12", "b13", "b14");
		assertThat(backEnd.receivedPaging).containsExactly(new int[] { 10, 5 });
	}

	@Test
	void fetch_firstPage_prependsAdditionalItemsAndShrinksBackEndLimit() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		final List<String> page = fetch(adapter, 0, 10);

		// the page must never exceed the requested limit
		assertThat(page).hasSize(10);
		assertThat(page).containsExactly("a0", "a1", "a2", "b0", "b1", "b2", "b3", "b4", "b5", "b6");
		// the back end must be asked only for the remaining 7 items
		assertThat(backEnd.receivedPaging).containsExactly(new int[] { 0, 7 });
	}

	@Test
	void fetch_secondPage_doesNotRepeatAdditionalItemsAndShiftsBackEndOffset() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		final List<String> page = fetch(adapter, 10, 10);

		assertThat(page).hasSize(10);
		// the additional items belong to the first page only: they must not be repeated
		assertThat(page).doesNotContain("a0", "a1", "a2");
		// the second page continues exactly where the first one ended (b6)
		assertThat(page).containsExactly("b7", "b8", "b9", "b10", "b11", "b12", "b13", "b14", "b15", "b16");
		assertThat(backEnd.receivedPaging).containsExactly(new int[] { 7, 10 });
	}

	@Test
	void fetch_pagesAreContiguousAndNonOverlapping() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		final List<String> all = new ArrayList<>();
		for (int offset = 0; offset < 30; offset += 10) {
			all.addAll(fetch(adapter, offset, 10));
		}

		assertThat(all).hasSize(30);
		// no duplicates across pages
		assertThat(all).doesNotHaveDuplicates();
		assertThat(all.subList(0, 3)).containsExactly("a0", "a1", "a2");
		assertThat(all.subList(3, 30))
				.isEqualTo(IntStream.range(0, 27).mapToObj(i -> "b" + i).toList());
	}

	@Test
	void fetch_offsetInsideAdditionalItems_returnsRemainingAdditionalItemsFirst() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		final List<String> page = fetch(adapter, 2, 10);

		assertThat(page).hasSize(10);
		assertThat(page).startsWith("a2", "b0", "b1");
		assertThat(backEnd.receivedPaging).containsExactly(new int[] { 0, 9 });
	}

	@Test
	void fetch_pageFullyCoveredByAdditionalItems_doesNotQueryBackEnd() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		final List<String> page = fetch(adapter, 0, 2);

		assertThat(page).containsExactly("a0", "a1");
		// the back end holds no item of this page: it must not be queried at all
		assertThat(backEnd.receivedPaging).isEmpty();
	}

	@Test
	void fetch_frozen_returnsNoItems() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItem("a0");
		adapter.setFrozen(true);

		assertThat(fetch(adapter, 0, 10)).isEmpty();
		assertThat(backEnd.receivedPaging).isEmpty();
	}

	@Test
	void size_addsAdditionalItemsCount() {
		final BackEnd backEnd = new BackEnd();
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				backEnd);
		adapter.addAdditionalItems(List.of("a0", "a1", "a2"));

		assertThat(adapter.size(query(0, 10))).isEqualTo(BACK_END_SIZE + 3);
	}

	@Test
	void addAdditionalItems_refreshesOnlyOnceForTheWholeBatch() {
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				new BackEnd());
		final AtomicInteger refreshes = new AtomicInteger();
		adapter.addDataProviderListener(e -> refreshes.incrementAndGet());

		adapter.addAdditionalItems(List.of("a0", "a1", "a2", "a3", "a4"));

		assertThat(refreshes.get()).isEqualTo(1);
		assertThat(adapter.getAdditionalItems()).containsExactly("a0", "a1", "a2", "a3", "a4");
	}

	@Test
	void removeAdditionalItems_refreshesOnlyOnceForTheWholeBatch() {
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				new BackEnd());
		adapter.addAdditionalItems(List.of("a0", "a1", "a2", "a3", "a4"));

		final AtomicInteger refreshes = new AtomicInteger();
		adapter.addDataProviderListener(e -> refreshes.incrementAndGet());

		assertThat(adapter.removeAdditionalItems(List.of("a1", "a3"))).isTrue();

		assertThat(refreshes.get()).isEqualTo(1);
		assertThat(adapter.getAdditionalItems()).containsExactly("a0", "a2", "a4");
	}

	@Test
	void removeAdditionalItems_withNoMatchingItem_doesNotRefresh() {
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				new BackEnd());
		adapter.addAdditionalItems(List.of("a0"));

		final AtomicInteger refreshes = new AtomicInteger();
		adapter.addDataProviderListener(e -> refreshes.incrementAndGet());

		assertThat(adapter.removeAdditionalItems(List.of("zz"))).isFalse();

		assertThat(refreshes.get()).isZero();
	}

	@Test
	void addAdditionalItems_emptyBatch_doesNotRefresh() {
		final DefaultItemListingDataProviderAdapter<String, Void> adapter = new DefaultItemListingDataProviderAdapter<>(
				new BackEnd());
		final AtomicInteger refreshes = new AtomicInteger();
		adapter.addDataProviderListener(e -> refreshes.incrementAndGet());

		adapter.addAdditionalItems(List.of());

		assertThat(refreshes.get()).isZero();
	}

}

