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
package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

/**
 * Base builder to create {@link Component}s.
 *
 * @param <C> Concrete component type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface ComponentBuilder<C extends Component, B extends ComponentBuilder<C, B>>
		extends ComponentConfigurator<B> {

	/**
	 * Build and returns the component.
	 * @return The component instance
	 */
	C build();

	/**
	 * Register a post-processor {@link Consumer} that will be invoked on the built component
	 * immediately before {@link #build()} returns. Post-processors are applied in registration
	 * order and are useful for last-mile customisations not covered by the builder API.
	 * <pre>{@code
	 * Button btn = ButtonBuilder.create()
	 *     .text("Save").primary()
	 *     .withBuildPostProcessor(b -> b.getElement().setAttribute("data-testid", "save-btn"))
	 *     .build();
	 * }</pre>
	 * @param postProcessor Consumer to apply to the built component (not null)
	 * @return this builder
	 * @since 10.0.3
	 */
	B withBuildPostProcessor(Consumer<C> postProcessor);

}
