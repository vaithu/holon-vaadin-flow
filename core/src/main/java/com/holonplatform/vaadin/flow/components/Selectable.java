/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

/**
 * Represents a component which supports items selection.
 *
 * @param <T> Selection item type
 *
 * @since 5.2.0
 */
public interface Selectable<T> {

	/**
	 * Get the selection mode
	 * @return the selection mode
	 */
	SelectionMode getSelectionMode();

	/**
	 * Returns whether the given <code>item</code> is selected.
	 * @param item Item to check (not null)
	 * @return <code>true</code> if the given item is selected, <code>false</code> otherwise
	 */
	default boolean isSelected(T item) {
		return getSelectedItems().contains(item);
	}

	/**
	 * Get an immutable set of the currently selected items.
	 * <p>
	 * The iteration order of the items in the returned set is implementation dependent.
	 * </p>
	 * @return Selected items set, empty if none
	 */
	Set<T> getSelectedItems();

	/**
	 * Get the first selected item.
	 * @return the first selected item, empty if none
	 */
	Optional<T> getFirstSelectedItem();

	/**
	 * Selects the given item. If the item is already selected, does nothing.
	 * <p>
	 * When in {@link SelectionMode#SINGLE}, any previously selected item is deselected.
	 * </p>
	 * @param item the item to select (not null)
	 */
	void select(T item);

	/**
	 * Deselects the given item. If the item is not currently selected, does nothing.
	 * @param item the item to deselect (not null)
	 */
	void deselect(T item);

	/**
	 * Deselects all currently selected items, if any.
	 */
	void deselectAll();

	/**
	 * Adds a {@link SelectionListener} to listen to selection changes.
	 * @param selectionListener The listener to content (not null)
	 * @return the listener {@link Registration}
	 */
	Registration addSelectionListener(SelectionListener<T> selectionListener);

	/**
	 * Exposes the currently selected items as a read-only {@link Signal}, using the given <code>owner</code> component
	 * to scope the lifetime of the internal listener bridge.
	 * <p>
	 * Use this to bind UI state reactively to the selection, for example to enable an action button only when
	 * something is selected:
	 * </p>
	 * <pre>{@code
	 * Signal<Set<MyItem>> selection = listing.selectionSignal(listing.getComponent());
	 * ComponentEffect.effect(deleteButton,
	 *         () -> deleteButton.setEnabled(!selection.get().isEmpty()));
	 * }</pre>
	 * <p>
	 * The bridge is registered while the owner is attached and removed when the owner is detached, so no listener
	 * (and nothing it captures) is retained after the owner goes away. The signal is re-synchronized with the current
	 * selection whenever the owner is attached again.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> a signal models the current selection <em>state</em>. Use
	 * {@link #addSelectionListener(SelectionListener)} instead when the selection <em>event</em> itself matters, for
	 * example to know whether the change originated from the client via
	 * {@link SelectionEvent#isFromClient()}.
	 * </p>
	 *
	 * @param owner the component whose lifecycle scopes the internal listener bridge (not null)
	 * @return read-only signal of the currently selected items
	 * @since 5.5.8
	 */
	default Signal<Set<T>> selectionSignal(Component owner) {
		ObjectUtils.argumentNotNull(owner, "Owner component must be not null");
		final ValueSignal<Set<T>> signal = new ValueSignal<>(getSelectedItems());
		bindSelectionBridge(this, owner, () -> signal.set(getSelectedItems()));
		return signal.asReadonly();
	}

	/**
	 * Register a selection listener bridge whose lifetime is bound to the given <code>owner</code> component: the
	 * bridge is active only while the owner is attached, and is removed on detach.
	 *
	 * @param <T>        Selection item type
	 * @param selectable the selectable to observe (not null)
	 * @param owner      the component whose lifecycle scopes the bridge (not null)
	 * @param sync       the action synchronizing the target signal with the current selection (not null)
	 */
	private static <T> void bindSelectionBridge(Selectable<T> selectable, Component owner, Runnable sync) {
		final AtomicReference<Registration> bridge = new AtomicReference<>();
		final Runnable register = () -> {
			if (bridge.get() == null) {
				bridge.set(selectable.addSelectionListener(event -> sync.run()));
				// the selection may have changed while the owner was detached
				sync.run();
			}
		};
		owner.addAttachListener(event -> register.run());
		owner.addDetachListener(event -> {
			final Registration registration = bridge.getAndSet(null);
			if (registration != null) {
				registration.remove();
			}
		});
		if (owner.isAttached()) {
			register.run();
		}
	}

	/**
	 * Selection modes enumeration.
	 */
	enum SelectionMode {

		/**
		 * The selection is not active.
		 */
		NONE,

		/**
		 * Single selection mode.
		 */
		SINGLE,

		/**
		 * Multiple selection mode.
		 */
		MULTI;

	}

	/**
	 * Selection event.
	 * @param <T> Selection item type
	 */
	interface SelectionEvent<T> extends Serializable {

		/**
		 * Get first selected data item, if any.
		 * @return the first selected item, empty if none
		 */
		Optional<T> getFirstSelectedItem();

		/**
		 * Gets all the currently selected items. For single selection it returns a set containing the only selected
		 * item.
		 * @return return all the selected items, if any, never <code>null</code>
		 */
		Set<T> getAllSelectedItems();

		/**
		 * Returns whether this event was triggered by user interaction, on the client side, or programmatically, on the
		 * server side.
		 * @return <code>true</code> if this event originates from the client, <code>false</code> otherwise.
		 */
		boolean isUserOriginated();

	}

	/**
	 * A listener for listening for selection changes from a {@link Selectable}.
	 * @param <T> Selection item type
	 */
	@FunctionalInterface
	interface SelectionListener<T> extends Serializable {

		/**
		 * Invoked when the selection has changed.
		 * @param selectionEvent The selection event to inspect the selected items
		 */
		void onSelectionChange(SelectionEvent<T> selectionEvent);

	}

}
