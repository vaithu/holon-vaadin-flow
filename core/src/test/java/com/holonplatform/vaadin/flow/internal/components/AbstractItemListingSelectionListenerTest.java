/*
 * Copyright 2016-2026 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;

/**
 * Tests for the selection listener registration lifecycle of {@link AbstractItemListing}.
 */
class AbstractItemListingSelectionListenerTest {

	public static class Item {

		private String name;

		public Item() {
		}

		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private static BeanListing<Item> listing() {
		return BeanListing.builder(Item.class).selectionMode(SelectionMode.SINGLE).build();
	}

	/**
	 * Explicitly typed listener: {@code addSelectionListener} is overloaded with the Vaadin
	 * selection listener type, so a bare lambda would be ambiguous.
	 */
	private static SelectionListener<Item> counting(AtomicInteger counter) {
		return event -> counter.incrementAndGet();
	}

	@Test
	void selectionListener_isNotifiedWhileRegistered() {
		final BeanListing<Item> listing = listing();
		final AtomicInteger notifications = new AtomicInteger();
		listing.addSelectionListener(counting(notifications));

		listing.select(new Item("a"));

		assertThat(notifications.get()).isEqualTo(1);
	}

	@Test
	void selectionListener_isNotNotifiedAfterRegistrationRemoved() {
		final BeanListing<Item> listing = listing();
		final AtomicInteger notifications = new AtomicInteger();
		final Registration registration = listing.addSelectionListener(counting(notifications));

		registration.remove();
		listing.select(new Item("a"));

		assertThat(notifications.get()).isZero();
	}

	@Test
	void selectionListener_isNotResurrectedBySelectionModeChange() {
		final BeanListing<Item> listing = listing();
		final AtomicInteger notifications = new AtomicInteger();
		final Registration registration = listing.addSelectionListener(counting(notifications));

		registration.remove();

		// changing the selection mode re-runs setupSelectionListeners(): a removed listener
		// must not be registered again
		listing.setSelectionMode(SelectionMode.MULTI);
		listing.select(new Item("a"));

		assertThat(notifications.get()).isZero();
	}

	@Test
	void selectionListener_removingOneKeepsTheOthers() {
		final BeanListing<Item> listing = listing();
		final AtomicInteger first = new AtomicInteger();
		final AtomicInteger second = new AtomicInteger();

		final Registration firstRegistration = listing.addSelectionListener(counting(first));
		listing.addSelectionListener(counting(second));

		firstRegistration.remove();
		listing.select(new Item("a"));

		assertThat(first.get()).isZero();
		assertThat(second.get()).isEqualTo(1);
	}

	@Test
	void selectionListener_registrationRemoveIsIdempotent() {
		final BeanListing<Item> listing = listing();
		final AtomicInteger notifications = new AtomicInteger();
		final Registration registration = listing.addSelectionListener(counting(notifications));

		registration.remove();

		// a second removal must not fail
		registration.remove();

		listing.select(new Item("a"));
		assertThat(notifications.get()).isZero();
	}

}



