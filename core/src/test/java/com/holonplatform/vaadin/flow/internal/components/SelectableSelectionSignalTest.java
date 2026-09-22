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

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.internal.components.AbstractItemListingSelectionListenerTest.Item;
import com.holonplatform.vaadin.flow.test.AbstractSessionTest;
import com.vaadin.flow.signals.Signal;

/**
 * Tests for the signal-based selection accessor, including the lifecycle of the internal
 * listener bridge.
 */
class SelectableSelectionSignalTest extends AbstractSessionTest {

	private BeanListing<Item> attachedListing() {
		final BeanListing<Item> listing = BeanListing.builder(Item.class).selectionMode(SelectionMode.SINGLE).build();
		// attach the listing to the test UI, so that the signal bridge is registered
		ui.add(listing.getComponent());
		return listing;
	}

	@Test
	void selectionSignal_startsWithTheCurrentSelection() {
		final BeanListing<Item> listing = attachedListing();

		final Signal<Set<Item>> selection = listing.selectionSignal(listing.getComponent());

		assertThat(selection.peek()).isEmpty();
	}

	@Test
	void selectionSignal_tracksSelectionChanges() {
		final BeanListing<Item> listing = attachedListing();
		final Signal<Set<Item>> selection = listing.selectionSignal(listing.getComponent());

		final Item item = new Item("a");
		listing.select(item);

		assertThat(selection.peek()).containsExactly(item);
	}

	@Test
	void selectionSignal_tracksDeselection() {
		final BeanListing<Item> listing = attachedListing();
		final Signal<Set<Item>> selection = listing.selectionSignal(listing.getComponent());

		final Item item = new Item("a");
		listing.select(item);
		assertThat(selection.peek()).isNotEmpty();

		listing.deselectAll();

		assertThat(selection.peek()).isEmpty();
	}

	@Test
	void selectionSignal_bridgeIsRemovedOnOwnerDetach() {
		final BeanListing<Item> listing = attachedListing();
		final Signal<Set<Item>> selection = listing.selectionSignal(listing.getComponent());

		// detach the owner: the internal listener bridge must be removed
		listing.getComponent().removeFromParent();

		listing.select(new Item("a"));

		// the signal no longer tracks the selection, which proves the bridge was removed
		assertThat(selection.peek()).isEmpty();
	}

	@Test
	void selectionSignal_bridgeIsRestoredAndResyncedOnReattach() {
		final BeanListing<Item> listing = attachedListing();
		final Signal<Set<Item>> selection = listing.selectionSignal(listing.getComponent());

		listing.getComponent().removeFromParent();

		// selection changes while detached are not observed by the signal
		final Item item = new Item("a");
		listing.select(item);
		assertThat(selection.peek()).isEmpty();

		// on re-attach the bridge is registered again and the signal is re-synchronized
		ui.add(listing.getComponent());

		assertThat(selection.peek()).containsExactly(item);
	}

}


