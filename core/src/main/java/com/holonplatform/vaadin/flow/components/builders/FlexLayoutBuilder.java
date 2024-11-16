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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFlexLayoutBuilder;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * Builder to create {@link FlexLayout} components.
 * 
 * @since 5.5.4
 */
public interface FlexLayoutBuilder
		extends FlexLayoutConfigurator<FlexLayoutBuilder>, ComponentBuilder<FlexLayout, FlexLayoutBuilder> {

	/**
	 * Create a new {@link FlexLayoutBuilder} to build a {@link FlexLayout} component.
	 * @return A new {@link FlexLayoutBuilder}
	 */
	static FlexLayoutBuilder create() {
		return new DefaultFlexLayoutBuilder();
	}

}