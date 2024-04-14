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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Path;
import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.*;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder.DatastorePropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.DatastoreLazyDataProvider;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default {@link PropertyListing} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyListing extends AbstractItemListing<PropertyBox, Property<?>> implements PropertyListing {

	private static final long serialVersionUID = -1099573388730286182L;

	/**
	 * Property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Constructor.
	 * @param <P>        Property type
	 * @param properties Property set (not null)
	 */
	public <P extends Property<?>> DefaultPropertyListing(Iterable<P> properties) {
		super();
		ObjectUtils.argumentNotNull(properties, "Property set must be not null");
		this.propertySet = (properties instanceof PropertySet<?>) ? (PropertySet<?>) properties
				: PropertySet.of(properties);
		// add properties as columns
		for (Property<?> property : propertySet) {
			addPropertyColumn(property);
		}
	}
//this is not fully implemented
	public <P extends Property<?>> DefaultPropertyListing(boolean defaultIndex,Iterable<P> properties) {
		super();
		ObjectUtils.argumentNotNull(properties, "Property set must be not null");
		if (defaultIndex) {
			VirtualProperty<String> ID = VirtualProperty.create(String.class)
					.message("#ID");
			ItemListingColumn<Property<?>, PropertyBox, ?> rowIndex = addPropertyColumn(ID);
			rowIndex.setFlexGrow(0);
//			rowIndex.setDefaultValueProvider();
		}
		this.propertySet = (properties instanceof PropertySet<?>) ? (PropertySet<?>) properties
				: PropertySet.of(properties);
		// add properties as columns
		for (Property<?> property : propertySet) {
			addPropertyColumn(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
	 */
	@Override
	public Collection<Property<?>> getProperties() {
		return getPropertySet().stream().map(p -> (Property<?>) p).collect(Collectors.toList());
	}

	/**
	 * Get the listing property set.
	 * @return the listing property set
	 */
	protected PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * isReadOnlyByDefault(java.lang.Object)
	 */
	@Override
	protected boolean isReadOnlyByDefault(Property<?> property) {
		return property.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getColumnKey(java.lang.Object)
	 */
	@Override
	protected String generateColumnKey(Property<?> property) {
		if (Path.class.isAssignableFrom(property.getClass())) {
			return ((Path<?>) property).fullName();
		}
		String name = property.getName();
		if (name != null) {
			return name;
		}
		return String.valueOf(property.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getDefaultColumnHeader(java.lang.Object)
	 */
	@Override
	protected Optional<Localizable> getDefaultColumnHeader(Property<?> property) {
		if (property.getMessage() != null || property.getMessageCode() != null) {
			return Optional.of(Localizable.builder()
					.message((property.getMessage() != null) ? property.getMessage() : property.getName())
					.messageCode(property.getMessageCode()).build());
		}
		if (Path.class.isAssignableFrom(property.getClass()) && property.getName() != null) {
			return Optional.of(Localizable.of(property.getName()));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * preProcessConfiguration(com.holonplatform.
	 * vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@Override
	protected ItemListingColumn<Property<?>, PropertyBox, ?> preProcessConfiguration(
			ItemListingColumn<Property<?>, PropertyBox, ?> configuration) {
		// sort
		if (Path.class.isAssignableFrom(configuration.getProperty().getClass())) {
			if (configuration.getSortProperties().isEmpty()) {
				configuration.setSortProperties(Collections.singletonList(configuration.getProperty()));
			}
			if (configuration.getSortMode() == SortMode.DEFAULT) {
				configuration.setSortMode(SortMode.ENABLED);
			}
		}
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getSortPropertyName(java.lang.Object)
	 */
	@Override
	protected Optional<String> getSortPropertyName(Property<?> property) {
		if (property != null && Path.class.isAssignableFrom(property.getClass())) {
			return Optional.ofNullable(((Path<?>) property).relativeName());
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * generateDefaultGridColumn(com.holonplatform
	 * .vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Column<PropertyBox> generateDefaultGridColumn(
			ItemListingColumn<Property<?>, PropertyBox, ?> configuration) {
		final Property<?> property = configuration.getProperty();
		// check component
		if (Component.class.isAssignableFrom(property.getType())) {
			final Property<? extends Component> componentProperty = (Property<? extends Component>) property;
			return getGrid().addComponentColumn(item -> {
				if (item.contains(property)) {
					return item.getValue(componentProperty);
				}
				return null;
			});
		}
		// default provider using property presenter
		return getGrid().addColumn(item -> {
			if (item.contains(property)) {
				return item.present(property);
			}
			return null;
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyType(java.lang.Object)
	 */
	@Override
	protected Class<?> getPropertyType(Property<?> property) {
		return property.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyValueGetter(java.lang.Object)
	 */
	@Override
	protected ValueProvider<PropertyBox, ?> getPropertyValueGetter(Property<?> property) {
		return item -> item.getValue(property);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyValueSetter(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Optional<Setter<PropertyBox, ?>> getPropertyValueSetter(Property<?> property) {
		if (!property.isReadOnly()) {
			return Optional.of((item, value) -> item.setValue((Property<Object>) property, value));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * buildPropertyEditor(com.holonplatform.
	 * vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <V> Optional<Input<V>> buildPropertyEditor(ItemListingColumn<Property<?>, PropertyBox, V> configuration) {
		final Property<V> property = (Property<V>) configuration.getProperty();
		// check custom renderer
		Optional<Input<V>> component = configuration.getEditorInputRenderer().map(r -> r.render(property));
		if (component.isPresent()) {
			return component;
		}
		// check specific registry
		if (getPropertyRendererRegistry().isPresent()) {
			return getPropertyRendererRegistry().get().getRenderer(Input.class, property).map(r -> r.render(property));
		} else {
			// use default
			return property.renderIfAvailable(Input.class).map(c -> (Input<V>) c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getDefaultPropertyValidators(java.lang. Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Collection<Validator<Object>> getDefaultPropertyValidators(Property<?> property) {
		return ((Property) property).getValidators();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * refreshVirtualProperties()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void refreshVirtualProperties() {
		isEditing().ifPresent(value -> {
			getBindings().filter(b -> b.getProperty() instanceof VirtualProperty).forEach(b -> {
				((Input) b.getElement()).setValue((value != null) ? value.getValue(b.getProperty()) : null);
			});
		});
	}

	// ------- Builder

	/**
	 * Default {@link PropertyListingBuilder} implementation.
	 */
	public static class DefaultPropertyListingBuilder extends
			AbstractItemListingConfigurator<PropertyBox, Property<?>, PropertyListing, DefaultPropertyListing, PropertyListingBuilder>
			implements PropertyListingBuilder {

		public <P extends Property<?>> DefaultPropertyListingBuilder(Iterable<P> properties) {
			super(new DefaultPropertyListing(properties));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.
		 * AbstractItemListingConfigurator# getItemListing()
		 */
		@Override
		protected PropertyListing getItemListing() {
			return getInstance();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.
		 * AbstractItemListingConfigurator# getConfigurator()
		 */
		@Override
		public DefaultPropertyListingBuilder getConfigurator() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withColumn(com.vaadin.flow.function .ValueProvider)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withColumn(
				ValueProvider<PropertyBox, X> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withColumn(VirtualProperty.create(Object.class, item -> valueProvider.apply(item)));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
		 * #withColumn(com.holonplatform. core.property.VirtualProperty)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withColumn(
				VirtualProperty<X> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			getInstance().addPropertyColumn(property).setValueProvider(new VirtualPropertyValueProvider<>(property));
			return new DefaultItemListingColumnBuilder<>(property, getInstance(), getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withComponentColumn(com.vaadin.flow .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withComponentColumn(
				ValueProvider<PropertyBox, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withComponentColumn(VirtualProperty.create(Component.class, item -> valueProvider.apply(item)));
		}

		@Override
		public PropertyListingBuilder hiddenColumns(List<? extends Property<?>> hiddenColumns) {
			hiddenColumns.forEach(property -> hidden(property));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
		 * #withComponentColumn(com. holonplatform.core.property.VirtualProperty)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withComponentColumn(
				VirtualProperty<? extends Component> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			final ItemListingColumn<Property<?>, PropertyBox, ?> column = getInstance().addPropertyColumn(property);
			column.setRenderer(new ComponentRenderer<>(item -> property.getValueProvider().getPropertyValue(item)));
			return new DefaultItemListingColumnBuilder<>(property, getInstance(), getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
		 * #withValidator(com.holonplatform .core.property.Property,
		 * com.holonplatform.core.Validator)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <V> PropertyListingBuilder withValidator(Property<V> property, Validator<? super V> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validator, "Validator must be not null");
			getInstance().getColumnConfiguration(property).addValidator((Validator) validator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator
		 * #editor(com.holonplatform.core. property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <T> PropertyListingBuilder editor(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			((ItemListingColumn) getInstance().getColumnConfiguration(property)).setEditorInputRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * PropertySetInputGroupConfigurator#
		 * defaultValue(com.holonplatform.core.property.Property,
		 * java.util.function.Supplier)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <V> PropertyListingBuilder defaultValue(Property<V> property, Supplier<V> defaultValueProvider) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).setDefaultValueProvider((Supplier) defaultValueProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * PropertySetInputGroupConfigurator#
		 * withValueChangeListener(com.holonplatform.core.property.Property,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <V> PropertyListingBuilder withValueChangeListener(Property<V> property,
				ValueChangeListener<V, GroupValueChangeEvent<V, Property<?>, Input<?>, EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).addValueChangeListener((ValueChangeListener) listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasPropertySetDatastoreDataProviderConfigurator#dataSource(
		 * com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public DatastorePropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> target) {
			final DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider = DatastoreDataProvider
					.create(datastore, target, getInstance().getPropertySet());
			getInstance().setDataProvider(datastoreDataProvider);
			return new DefaultDatastorePropertyListingBuilder(this, datastoreDataProvider);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasPropertySetDatastoreDataProviderConfigurator#dataSource(
		 * com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public DatastorePropertyListingBuilder lazyDataSource(Datastore datastore, DataTarget<?> target) {
			final DatastoreLazyDataProvider<PropertyBox, QueryFilter> datastoreDataProvider = DatastoreLazyDataProvider
					.create(datastore, target, getInstance().getPropertySet());
			getInstance().setItems(datastoreDataProvider);
			return new DefaultDatastoreLazyPropertyListingBuilder(this, datastoreDataProvider);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
		 */
		@Override
		public PropertyListing build() {
			return configureAndBuild();
		}

	}

	public static class DefaultDatastorePropertyListingBuilder extends AbstractDatastorePropertyListingBuilder {

		private final DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider;

		public DefaultDatastorePropertyListingBuilder(DefaultPropertyListingBuilder builder,
				DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider) {
			super(builder);
			this.datastoreDataProvider = datastoreDataProvider;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyListingBuilder withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public DatastorePropertyListingBuilder withDefaultQuerySort(QuerySort defaultQuerySort) {
			datastoreDataProvider.setDefaultSort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

	}

	public static class DefaultDatastoreLazyPropertyListingBuilder extends AbstractDatastorePropertyListingBuilder {

		private final DatastoreLazyDataProvider<PropertyBox, QueryFilter> datastoreDataProvider;

		public DefaultDatastoreLazyPropertyListingBuilder(DefaultPropertyListingBuilder builder,
													  DatastoreLazyDataProvider<PropertyBox, QueryFilter> datastoreDataProvider) {
			super(builder);
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyListingBuilder withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public DatastorePropertyListingBuilder withDefaultQuerySort(QuerySort defaultQuerySort) {
			datastoreDataProvider.setDefaultSort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

	}

	static class VirtualPropertyValueProvider<T> implements ValueProvider<PropertyBox, String> {

		private static final long serialVersionUID = 582674628237265592L;

		private final VirtualProperty<T> property;

		public VirtualPropertyValueProvider(VirtualProperty<T> property) {
			super();
			this.property = property;
		}

		@Override
		public String apply(PropertyBox source) {
			return property.present(property.getValueProvider().getPropertyValue(source));
		}

	}

}
