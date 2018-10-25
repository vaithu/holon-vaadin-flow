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
package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Configurator for {@link ClickNotifier} type components.
 * 
 * @param <T> Concrete component type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ClickNotifierConfigurator<T extends Component, C extends ClickNotifierConfigurator<T, C>> {

	/**
	 * Adds a click listener to this component.
	 * @param listener The listener to add
	 * @return this
	 */
	C withClickListener(ComponentEventListener<ClickEvent<T>> listener);

	/**
	 * Adds a click listener to this component. Alias for {@link #withClickListener(ComponentEventListener)}.
	 * @param listener The listener to add
	 * @return this
	 */
	default C onClick(ComponentEventListener<ClickEvent<T>> listener) {
		return withClickListener(listener);
	}

}