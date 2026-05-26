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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyListing;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;

/**
 * An {@link ItemListing} component using {@link Property}s as item properties and {@link PropertyBox} as item data
 * type.
 * 
 * @since 5.2.0
 */
public interface PropertyListing extends ItemListing<PropertyBox, Property<?>>, HasPropertySet<Property<?>> {

	// ── FilterInputGroup integration ──────────────────────────────────────

	/**
	 * Sets listing items using a fetch callback that also receives the current
	 * combined filter from the provided {@link FilterInputGroup}.
	 *
	 * @param filterGroup    filter group to resolve the active combined filter from
	 * @param fetchCallback  callback that receives query metadata and current filter
	 * @return lazy data view bound to the listing data provider
	 * @since 10.0.0
	 */
	@Override
	default GridLazyDataView<PropertyBox> setItems(
			FilterInputGroup filterGroup,
			FilterInputSupport.FilteredFetchCallback<PropertyBox> fetchCallback) {
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
	 * Registers a signal-based refresh effect that reacts to filter signal changes.
	 *
	 * @param filterGroup filter group to observe
	 * @return registration to remove the signal effect
	 * @since 10.0.1
	 */
	@Override
	default com.holonplatform.core.Registration refreshOnFilterSignal(FilterInputGroup filterGroup) {
		return ItemListing.super.refreshOnFilterSignal(filterGroup);
	}

	/**
	 * Convenience method that sets filtered items and registers auto-refresh on
	 * filter changes in one call.
	 *
	 * @param filterGroup    filter group to observe and resolve filters from
	 * @param fetchCallback  callback that receives query metadata and current filter
	 * @return registration to remove the auto-refresh listener
	 * @since 10.0.0
	 */
	@Override
	default com.holonplatform.core.Registration bindFilters(
			FilterInputGroup filterGroup,
			FilterInputSupport.FilteredFetchCallback<PropertyBox> fetchCallback) {
		return ItemListing.super.bindFilters(filterGroup, fetchCallback);
	}

	// ── Builder factories ─────────────────────────────────────────────────

	/**
	 * Get a {@link PropertyListingBuilder} to create and setup a {@link PropertyListing}.
	 * @param <P> Property type
	 * @param properties The listing property set (not null)
	 * @return A new {@link PropertyListingBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyListingBuilder builder(Iterable<P> properties) {
		return new DefaultPropertyListing.DefaultPropertyListingBuilder(properties);
	}

	/**
	 * Get a {@link PropertyListingBuilder} to create and setup a {@link PropertyListing}.
	 * @param properties The listing property set (not null)
	 * @return A new {@link PropertyListingBuilder}
	 */
	static PropertyListingBuilder builder(Property<?>... properties) {
		return builder(PropertySet.of(properties));
	}

}
