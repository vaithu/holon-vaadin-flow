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

	// TODO APICHG removed
	// B dragSource(Consumer<DragSourceExtension<? extends AbstractComponent>> configurator);

	// TODO APICHG removed
	// B dropTarget(BiConsumer<DropTargetExtension<? extends AbstractComponent>, C> configurator);

	/**
	 * Build and returns the component.
	 * @return The component instance
	 */
	C build();

}
