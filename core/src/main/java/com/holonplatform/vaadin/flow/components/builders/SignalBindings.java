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
package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utilities to bind component configurator state to Vaadin {@link Signal}s.
 *
 * @since 5.5.8
 */
public final class SignalBindings {

	private SignalBindings() {
	}

	/**
	 * Signal binding owner contract.
	 */
	public interface Owner {

		/**
		 * Gets the component to use as signal effect owner.
		 * @return The owner component, or <code>null</code> if not available
		 */
		Component getSignalBindingOwner();
	}

	/**
	 * Bind a consumer to a {@link Signal}.
	 * <p>
	 * When the target implements {@link Owner} and provides a component owner, the effect is lifecycle-bound to that
	 * component. Otherwise, an unbound effect is used.
	 * </p>
	 * @param <T> Signal value type
	 * @param target Binding target
	 * @param signal Signal to observe (not null)
	 * @param consumer Consumer to invoke with the current signal value (not null)
	 * @return The signal effect registration
	 */
	public static <T> Registration bind(Object target, Signal<? extends T> signal, Consumer<? super T> consumer) {
		Objects.requireNonNull(signal, "Signal must be not null");
		Objects.requireNonNull(consumer, "Signal consumer must be not null");

		final Component owner = (target instanceof Owner bindingOwner) ? bindingOwner.getSignalBindingOwner() : null;
		if (owner != null) {
			return Signal.effect(owner, () -> consumer.accept(signal.get()));
		}
		return Signal.unboundEffect(() -> consumer.accept(signal.get()));
	}

}
