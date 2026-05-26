/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultBeanListing;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;

/**
 * An {@link ItemListing} component using a bean type as item type and the bean property names as property set.
 * 
 * @param <T> Bean type
 * 
 * @since 5.2.0
 */
public interface BeanListing<T> extends ItemListing<T, String> {

	/**
	 * Sets listing items using a fetch callback that also receives the current
	 * combined filter from the provided {@link FilterInputGroup}.
	 *
	 * @param filterGroup filter group to resolve the active combined filter from
	 * @param fetchCallback callback that receives query metadata and current filter
	 * @return lazy data view bound to the listing data provider
	 * @since 10.0.0
	 */
	@Override
	default GridLazyDataView<T> setItems(
			FilterInputGroup filterGroup,
			FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
		return ItemListing.super.setItems(filterGroup, fetchCallback);
	}

	/**
	 * Registers a listener to refresh this listing whenever any filter in the
	 * provided group changes.
	 *
	 * @param filterGroup filter group to observe
	 * @return registration to remove the listener
	 * @since 10.0.0
	 */
	@Override
	default com.holonplatform.core.Registration refreshOnFilterChange(FilterInputGroup filterGroup) {
		return ItemListing.super.refreshOnFilterChange(filterGroup);
	}

	/**
	 * Convenience method that sets filtered items and registers auto-refresh on
	 * filter changes in one call.
	 *
	 * @param filterGroup filter group to observe and resolve filters from
	 * @param fetchCallback callback that receives query metadata and current filter
	 * @return registration to remove the auto-refresh listener
	 * @since 10.0.0
	 */
	@Override
	default com.holonplatform.core.Registration bindFilters(
			FilterInputGroup filterGroup,
			FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
		return ItemListing.super.bindFilters(filterGroup, fetchCallback);
	}

	/**
	 * Get a {@link BeanListingBuilder} to create and setup a {@link BeanListing} using given <code>beanType</code>.
	 * @param <T> Bean type
	 * @param beanType The bean class to use (not null)
	 * @param autoCreateColumns an initial set of columns for each of the bean's properties
	 * @return A new {@link BeanListingBuilder}
	 */
	static <T> BeanListingBuilder<T> builder(Class<T> beanType,boolean autoCreateColumns) {
		return new DefaultBeanListing.DefaultBeanListingBuilder<>(beanType,autoCreateColumns);
	}

	/**
	 * Get a {@link BeanListingBuilder} to create and setup a {@link BeanListing} using given <code>beanType</code>.
	 * @param <T> Bean type
	 * @param beanType The bean class to use (not null)
	 * @return A new {@link BeanListingBuilder}
	 */
	static <T> BeanListingBuilder<T> builder(Class<T> beanType) {
		return new DefaultBeanListing.DefaultBeanListingBuilder<>(beanType);
	}

}
