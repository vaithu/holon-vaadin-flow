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
package com.holonplatform.vaadin.flow.data;

import com.holonplatform.core.query.QueryConfigurationProvider;
import com.vaadin.flow.shared.Registration;

/**
 * Declares the support for {@link QueryConfigurationProvider} registration.
 *
 * @since 5.1.0
 */
public interface QueryConfigurationProviderSupport {

	/**
	 * Register a new {@link QueryConfigurationProvider}.
	 * @param queryConfigurationProvider The {@link QueryConfigurationProvider} to add (not null)
	 * @return Handler which can be used to remove the registered provider
	 */
	Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

}
