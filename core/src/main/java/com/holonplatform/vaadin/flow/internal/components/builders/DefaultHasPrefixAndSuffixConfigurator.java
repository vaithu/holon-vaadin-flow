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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.HasPrefix;
import com.vaadin.flow.component.shared.HasSuffix;

/**
 * Default {@link HasPrefixAndSuffixConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasPrefixAndSuffixConfigurator
		implements HasPrefixAndSuffixConfigurator<DefaultHasPrefixAndSuffixConfigurator> {

	private final HasPrefix prefix;
	private final HasSuffix suffix;

	/**
	 * Constructor.
	 * @param prefix Component to create (not null)
	 */
	public DefaultHasPrefixAndSuffixConfigurator(HasPrefix prefix, HasSuffix suffix) {
		super();
        this.suffix = suffix;
		this.prefix = prefix;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#prefixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public DefaultHasPrefixAndSuffixConfigurator prefixComponent(Component component) {
		this.prefix.setPrefixComponent(component);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#suffixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public DefaultHasPrefixAndSuffixConfigurator suffixComponent(Component component) {
		this.suffix.setSuffixComponent(component);
		return this;
	}

}
