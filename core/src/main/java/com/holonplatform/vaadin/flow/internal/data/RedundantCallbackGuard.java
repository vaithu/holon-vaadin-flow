/*
 * Copyright 2016-2018 Axioma srl.
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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Small reusable guard to avoid invoking Vaadin lazy {@code DataView} state-mutating
 * methods (such as {@code GridLazyDataView#setItemCountCallback(...)} or
 * {@code refreshAll()}) redundantly from within the very fetch/count callback they
 * configure.
 *
 * <h2>Why this is needed</h2>
 * <p>
 * Vaadin's {@code DataCommunicator} does not compare the previous and new state when a
 * mutator such as {@code setItemCountCallback(...)} is invoked: <b>any</b> call is
 * treated as an external state change and a new data request is scheduled — even when
 * the call happens re-entrantly, from inside the fetch callback currently being
 * processed, and even if the value being set is logically identical to the current one.
 * The observable effect is the very same backend query executed twice for a single grid
 * page load (visible as duplicate SQL statements, e.g. in a p6spy log).
 * </p>
 *
 * <h2>Usage</h2>
 * <p>
 * Keep one instance of this guard per mutable state you want to protect (e.g. one for an
 * item count "capped/full" toggle), and route every conditional call to the mutator
 * through {@link #applyIfChanged(Object, Consumer)}. When the same state is set from
 * <em>outside</em> the fetch callback (e.g. from a page-change listener) and the call is
 * intentionally unconditional, use {@link #markApplied(Object)} right after so future
 * comparisons stay accurate:
 * </p>
 * <pre>{@code
 * private final RedundantCallbackGuard<Boolean> itemCountCapped = new RedundantCallbackGuard<>();
 *
 * dataView = listing.setItems(q -> {
 *     ...
 *     boolean capped = actualCount < pageSize;
 *     itemCountCapped.applyIfChanged(capped,
 *             c -> dataView.setItemCountCallback(cq -> c ? actualCount : pageSize));
 *     ...
 * });
 * }</pre>
 *
 * @param <V> the guarded state value type
 *
 * @since 12.0.1
 */
public final class RedundantCallbackGuard<V> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private V lastValue;
	private boolean initialized = false;

	/**
	 * Runs {@code action} only if {@code newValue} differs from the last value applied
	 * (or recorded through {@link #markApplied(Object)}) through this guard, or if this
	 * is the first invocation.
	 * @param newValue the candidate new state value
	 * @param action   the action to run when the value actually changed; receives the
	 *                 new value
	 * @return {@code true} if the action was run, {@code false} if it was skipped because
	 *         the state did not actually change
	 */
	public boolean applyIfChanged(V newValue, Consumer<V> action) {
		if (!initialized || !Objects.equals(lastValue, newValue)) {
			lastValue = newValue;
			initialized = true;
			action.accept(newValue);
			return true;
		}
		return false;
	}

	/**
	 * Records {@code value} as the current state without invoking any action, typically
	 * right after the caller performed the state mutation itself unconditionally (e.g.
	 * from outside the guarded fetch callback), so subsequent
	 * {@link #applyIfChanged(Object, Consumer)} comparisons remain accurate.
	 * @param value the value to record as current
	 */
	public void markApplied(V value) {
		this.lastValue = value;
		this.initialized = true;
	}

	/**
	 * Resets the guard so the next {@link #applyIfChanged(Object, Consumer)} call always
	 * runs the action, regardless of the value.
	 */
	public void reset() {
		this.initialized = false;
		this.lastValue = null;
	}

}
