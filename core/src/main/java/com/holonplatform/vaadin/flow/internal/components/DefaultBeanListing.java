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

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder.DatastoreBeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.*;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.DatastoreLazyDataProvider;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultShortcutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import java.io.Serial;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Default {@link BeanListing} implementation.
 *
 * @param <T> Bean type
 * @since 5.2.0
 */
public class DefaultBeanListing<T> extends AbstractItemListing<T, String> implements BeanListing<T> {

    @Serial
    private static final long serialVersionUID = -4447503694235650581L;

    /**
     * Bean type
     */
    private final Class<T> beanType;

    /**
     * Bean property set
     */
    private final BeanPropertySet<T> propertySet;

    /**
     * Virtual columns
     */
    private final Set<String> virtualColumnProperties = new HashSet<>(4);

    /**
     * Constructor.
     *
     * @param beanType Bean type (not null)
     */
    public DefaultBeanListing(Class<T> beanType) {
        this(beanType, true);
    }

    /**
     * Constructor.
     *
     * @param beanType          Bean type (not null)
     * @param autoCreateColumns an initial set of columns for each of the bean's properties.
     */
    public DefaultBeanListing(Class<T> beanType, boolean autoCreateColumns) {
        super();
        ObjectUtils.argumentNotNull(beanType, "Bean type must be not null");
        this.beanType = beanType;
        this.propertySet = BeanPropertySet.create(beanType);
        if (autoCreateColumns) {
            // add properties as columns
            for (PathProperty<?> property : propertySet) {
                addPropertyColumn(property.relativeName());
            }
        }
    }

    /**
     * Get the bean class.
     *
     * @return the bean type
     */
    protected Class<T> getBeanType() {
        return beanType;
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
     */
    @Override
    public Collection<String> getProperties() {
        return propertySet.stream().map(PathProperty::relativeName).toList();
    }

    /**
     * Add a virtual column.
     *
     * @return The property id
     */
    protected String addColumnProperty() {
        final String id = "_!virtual#" + virtualColumnProperties.size();
        virtualColumnProperties.add(id);
        addPropertyColumn(id);
        return id;
    }

    protected Set<String> getVirtualColumns() {
        return virtualColumnProperties;
    }

    @Override
    public <V extends Component> Column<T> addComponentColumn(ValueProvider<T, V> valueProvider) {
        ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
        final String columnId = addColumnProperty();
        final ItemListingColumn<String, T, ?> columnConfiguration = getColumnConfiguration(columnId);
        columnConfiguration.setRenderer(new ComponentRenderer<>(valueProvider));
        return getGrid().addComponentColumn(valueProvider).setKey(getColumnKey(columnId));
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * isReadOnlyByDefault(java.lang.Object)
     */
    @Override
    protected boolean isReadOnlyByDefault(String property) {
        return property != null && virtualColumnProperties.contains(property);
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * generateColumnKey(java.lang.Object)
     */
    @Override
    protected String generateColumnKey(String property) {
        return property;
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * generateDefaultGridColumn(com.holonplatform .vaadin.flow.internal.components.support.ItemListingColumn)
     */
    @Override
    protected Column<T> generateDefaultGridColumn(ItemListingColumn<String, T, ?> configuration) {
        final String property = configuration.getProperty();
        return getGrid().addColumn(item -> propertySet.getProperty(property).map(p -> p.present(propertySet.read(p, item))).orElse(null));
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * preProcessConfiguration(com.holonplatform. vaadin.flow.internal.components.support.ItemListingColumn)
     */
    @Override
    protected ItemListingColumn<String, T, ?> preProcessConfiguration(ItemListingColumn<String, T, ?> configuration) {
        if (configuration.getSortProperties().isEmpty()) {
            configuration.setSortProperties(List.of(configuration.getProperty()));
        }
        if (configuration.getSortMode() == SortMode.DEFAULT) {
            configuration.setSortMode(SortMode.ENABLED);
        }
        return configuration;
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * getSortPropertyName(java.lang.Object)
     */
    @Override
    protected Optional<String> getSortPropertyName(String property) {
        return Optional.of(property);
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * getDefaultColumnHeader(java.lang.Object)
     */
    @Override
    protected Optional<Localizable> getDefaultColumnHeader(String property) {
        return propertySet.getProperty(property).map(p -> {
            if (p.getMessage() != null || p.getMessageCode() != null) {
                return Localizable.builder().message((p.getMessage() != null) ? p.getMessage() : p.getName())
                        .messageCode(p.getMessageCode()).build();
            }
            return Localizable.of(p.getName());
        });
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing# getPropertyType(java.lang.Object)
     */
    @Override
    protected Class<?> getPropertyType(String property) {
        return propertySet.property(property).getType();
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * getPropertyValueGetter(java.lang.Object)
     */
    @Override
    protected ValueProvider<T, ?> getPropertyValueGetter(String property) {
        return item -> propertySet.read(property, item);
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * getPropertyValueSetter(java.lang.Object)
     */
    @Override
    protected Optional<Setter<T, ?>> getPropertyValueSetter(String property) {
        return Optional.of((item, value) -> propertySet.write(property, value, item));
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * buildPropertyEditor(com.holonplatform. vaadin.flow.internal.components.support.ItemListingColumn)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <V> Optional<Input<V>> buildPropertyEditor(ItemListingColumn<String, T, V> configuration) {
        return propertySet.getProperty(configuration.getProperty()).flatMap(p -> {
            final Property<V> property = (Property<V>) p;
            // check custom renderer
            Optional<Input<V>> component = configuration.getEditorInputRenderer().map(r -> r.render(property));
            if (component.isPresent()) {
                return component;
            }
            // check specific registry
            if (getPropertyRendererRegistry().isPresent()) {
                return getPropertyRendererRegistry().get().getRenderer(Input.class, property)
                        .map(r -> (Input<V>) r.render(property));
            } else {
                // use default
                return property.renderIfAvailable(Input.class).map(c -> (Input<V>) c);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
     * getDefaultPropertyValidators(java.lang. Object)
     */
    @Override
    protected Collection<Validator<Object>> getDefaultPropertyValidators(String property) {
        return propertySet.getProperty(property).map(Property::getValidators).orElse(List.of());
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing# refreshVirtualProperties()
     */
    @Override
    protected void refreshVirtualProperties() {
        // noop
    }



    // ------- Builder

    /**
     * Default {@link BeanListingBuilder} implementation.
     *
     * @param <T> Bean type
     */
    public static class DefaultBeanListingBuilder<T> extends
            AbstractItemListingConfigurator<T, String, BeanListing<T>, DefaultBeanListing<T>, BeanListingBuilder<T>>
            implements BeanListingBuilder<T> {

        private boolean includeVirtualColumns;

        public DefaultBeanListingBuilder(Class<T> beanType) {
            super(new DefaultBeanListing<>(beanType));
        }

        public DefaultBeanListingBuilder(Class<T> beanType, boolean autoCreateColumns) {
            super(new DefaultBeanListing<>(beanType, autoCreateColumns));
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing. AbstractItemListingConfigurator#
         * getItemListing()
         */
        @Override
        protected BeanListing<T> getItemListing() {
            return getInstance();
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder# withValidator(java.lang.String,
         * com.holonplatform.core.Validator)
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public BeanListingBuilder<T> withValidator(String property, Validator<?> validator) {
            ObjectUtils.argumentNotNull(property, "Property must be not null");
            ObjectUtils.argumentNotNull(validator, "Validator must be not null");
            getInstance().getColumnConfiguration(property).addValidator((Validator) validator);
            return getConfigurator();
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder#editor( java.lang.String,
         * com.holonplatform.vaadin.flow.components.Input)
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public BeanListingBuilder<T> editor(String property, Input<?> editor) {
            ObjectUtils.argumentNotNull(property, "Property must be not null");
            getInstance().getColumnConfiguration(property)
                    .setEditorInputRenderer(InputPropertyRenderer.create(p -> (Input) editor));
            return getConfigurator();
        }

    @Override
    public BeanListingBuilder<T> setItems(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      ObjectUtils.argumentNotNull(filterGroup, "Filter group must be not null");
      ObjectUtils.argumentNotNull(fetchCallback, "Fetch callback must be not null");
      getInstance().setItems(filterGroup, fetchCallback);
      return getConfigurator();
    }

    @Override
    public BeanListingBuilder<T> refreshOnFilterChange(FilterInputGroup filterGroup) {
      ObjectUtils.argumentNotNull(filterGroup, "Filter group must be not null");
      getInstance().refreshOnFilterChange(filterGroup);
      return getConfigurator();
    }

    @Override
    public BeanListingBuilder<T> refreshOnFilterSignal(FilterInputGroup filterGroup) {
      ObjectUtils.argumentNotNull(filterGroup, "Filter group must be not null");
      getInstance().refreshOnFilterSignal(filterGroup);
      return getConfigurator();
    }

    @Override
    public BeanListingBuilder<T> bindFilters(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      ObjectUtils.argumentNotNull(filterGroup, "Filter group must be not null");
      ObjectUtils.argumentNotNull(fetchCallback, "Fetch callback must be not null");
      getInstance().bindFilters(filterGroup, fetchCallback);
      return getConfigurator();
    }

    @Override
    public BeanListingBuilder<T> bindFiltersSignal(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      ObjectUtils.argumentNotNull(filterGroup, "Filter group must be not null");
      ObjectUtils.argumentNotNull(fetchCallback, "Fetch callback must be not null");
      getInstance().bindFiltersSignal(filterGroup, fetchCallback);
      return getConfigurator();
    }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator. BeanInputGroupConfigurator#
         * defaultValue(java.lang.String, java.util.function.Function)
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public BeanListingBuilder<T> defaultValue(String property, Supplier<Object> defaultValueProvider) {
            ObjectUtils.argumentNotNull(property, "Property must be not null");
            getInstance().getColumnConfiguration(property).setDefaultValueProvider((Supplier) defaultValueProvider);
            return getConfigurator();
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator. BeanInputGroupConfigurator#
         * withValueChangeListener(java.lang.String,
         * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public BeanListingBuilder<T> withValueChangeListener(String property,
                                                             ValueChangeListener<?, GroupValueChangeEvent<?, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
            ObjectUtils.argumentNotNull(property, "Property must be not null");
            getInstance().getColumnConfiguration(property).addValueChangeListener((ValueChangeListener) listener);
            return getConfigurator();
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * withComponentColumn(com.vaadin.flow .function.ValueProvider)
         */
        @Override
        public ItemListingColumnBuilder<T, String, BeanListing<T>, BeanListingBuilder<T>> withComponentColumn(
                ValueProvider<T, Component> valueProvider) {
            ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
            final String columnId = getInstance().addColumnProperty();
            final ItemListingColumn<String, T, ?> columnConfiguration = getInstance().getColumnConfiguration(columnId);
            columnConfiguration.setRenderer(new ComponentRenderer<>(valueProvider));
            return new DefaultItemListingColumnBuilder<>(columnId, getInstance(), this);
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * withColumn(com.vaadin.flow.function .ValueProvider)
         */
        @Override
        public <X> ItemListingColumnBuilder<T, String, BeanListing<T>, BeanListingBuilder<T>> withColumn(
                ValueProvider<T, X> valueProvider) {
            ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
            final String columnId = getInstance().addColumnProperty();
            getInstance().getColumnConfiguration(columnId).setValueProvider(new ValueProviderAdapter<>(valueProvider));
            return new DefaultItemListingColumnBuilder<>(columnId, getInstance(), this);
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders. HasDatastoreDataProviderConfigurator#dataSource(com.
         * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
         * java.util.function.Function, java.lang.Iterable)
         */
        @SuppressWarnings("rawtypes")
        @Override
        public <P extends Property> DatastoreBeanListingBuilder<T> dataSource(Datastore datastore, DataTarget<?> target,
                                                                              Function<PropertyBox, T> itemConverter, Iterable<P> properties) {
            final DatastoreLazyDataProvider<T, QueryFilter> datastoreDataProvider = DatastoreLazyDataProvider
                    .create(datastore, target, DatastoreDataProvider.asPropertySet(properties), itemConverter,
                            Function.identity());
            configureNoCountLazyItems(datastoreDataProvider);
            return new DefaultDatastoreBeanListingBuilder<>(this, datastoreDataProvider);
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders. HasDatastoreDataProviderConfigurator#dataSource(com.
         * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.lang.Class)
         */
        @Override
        public DatastoreBeanListingBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
            final DatastoreLazyDataProvider<T, QueryFilter> datastoreDataProvider = DatastoreLazyDataProvider
                    .create(datastore, target, getInstance().getBeanType());

            configureNoCountLazyItems(datastoreDataProvider);

            return new DefaultDatastoreBeanListingBuilder<>(this, datastoreDataProvider);
        }

        // Binds only a fetch callback and marks item count as unknown to avoid count callback wiring.
        private void configureNoCountLazyItems(DatastoreLazyDataProvider<T, QueryFilter> datastoreDataProvider) {
            getInstance().setItems(query -> datastoreDataProvider.fetch(toFilterlessQuery(query))).setItemCountUnknown();
        }

        private static <T> Query<T, QueryFilter> toFilterlessQuery(Query<T, Void> query) {
            return new Query<>(query.getOffset(), query.getLimit(), query.getSortOrders(), query.getInMemorySorting(),
                    null);
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing. AbstractItemListingConfigurator#
         * getConfigurator()
         */
        @Override
        public BeanListingBuilder<T> getConfigurator() {
            return this;
        }

        /**
         * Whether the listing can include virtual columns when explicitly not specified what they are
         */

        @Override
        public BeanListingBuilder<T> includeVirtualColumns(boolean yes) {
            this.includeVirtualColumns = yes;
            return this;
        }

        @Override
        public BeanListingBuilder<T> tooltipMarkdownEnabled(boolean markdownEnabled) {
            getInstance().getGrid().setTooltipMarkdownEnabled(markdownEnabled);
            return this;
        }

        @Override
        public BeanListingBuilder<T> scrollToColumn(int columnIndex) {
            getInstance().getGrid().scrollToColumn(columnIndex);
            return this;
        }

        @Override
        public BeanListingBuilder<T> scrollToColumn(Grid.Column<T> column) {
            getInstance().getGrid().scrollToColumn(column);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BeanListingBuilder<T> itemsPageable(Grid.SpringData.FetchCallback<?, T> fetchCallback) {
            getInstance().getGrid().setItemsPageable((Grid.SpringData.FetchCallback) fetchCallback);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BeanListingBuilder<T> itemsPageable(Grid.SpringData.FetchCallback<?, T> fetchCallback,
                                                   Grid.SpringData.CountCallback<?> countCallback) {
            getInstance().getGrid().setItemsPageable((Grid.SpringData.FetchCallback) fetchCallback,
                    (Grid.SpringData.CountCallback) countCallback);
            return this;
        }

        /*
         * When explicitly specifying columns without specifying virtual column names,
         * they're getting ignored which is incorrect. To circumvent this situation, making them
         * readOnly = true makes them visible. Still this is not the correct solution
         */
        private void addVirtualColumns() {

            if (includeVirtualColumns) {

                List<String> updatedColumnsList = new ArrayList<>(getInstance().getVisibleColumnProperties());

                getInstance().getVirtualColumns().forEach(property -> {
                    getInstance().getColumnConfiguration(property).setReadOnly(true);

                    if (!updatedColumnsList.contains(property)) {
                        updatedColumnsList.add(property);
                    }
                });

                if (!updatedColumnsList.isEmpty()) {
                    getInstance().setVisibleColumns(updatedColumnsList);
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
         */
        @Override
        public BeanListing<T> build() {
            addVirtualColumns();
            return configureAndBuild();
        }

    }

    public static class DefaultDatastoreBeanListingBuilder<T> implements DatastoreBeanListingBuilder<T> {

        private interface QueryConfigurator<T> {

            void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

            void setDefaultSort(QuerySort defaultQuerySort);

            void setItemIdentifier(Function<T, Object> itemIdentifierProvider);

            void setQuerySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter);
        }

        private final DefaultBeanListingBuilder<T> builder;
        private final QueryConfigurator<T> queryConfigurator;

        @SuppressWarnings("unused")
        public DefaultDatastoreBeanListingBuilder(DefaultBeanListingBuilder<T> builder,
                                                  DatastoreDataProvider<T, QueryFilter> datastoreDataProvider) {
            super();
            this.builder = builder;
            this.queryConfigurator = new QueryConfigurator<>() {
                @Override
                public void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
                    datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
                }

                @Override
                public void setDefaultSort(QuerySort defaultQuerySort) {
                    datastoreDataProvider.setDefaultSort(defaultQuerySort);
                }

                @Override
                public void setItemIdentifier(Function<T, Object> itemIdentifierProvider) {
                    datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
                }

                @Override
                public void setQuerySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
                    datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
                }
            };
        }

        public DefaultDatastoreBeanListingBuilder(DefaultBeanListingBuilder<T> builder,
                                                  DatastoreLazyDataProvider<T, QueryFilter> datastoreDataProvider) {
            super();
            this.builder = builder;
            this.queryConfigurator = new QueryConfigurator<>() {
                @Override
                public void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
                    datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
                }

                @Override
                public void setDefaultSort(QuerySort defaultQuerySort) {
                    datastoreDataProvider.setDefaultSort(defaultQuerySort);
                }

                @Override
                public void setItemIdentifier(Function<T, Object> itemIdentifierProvider) {
                    datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
                }

                @Override
                public void setQuerySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
                    datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
                }
            };
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.BeanListingConfigurator#
         * withValidator(java.lang.String, com.holonplatform.core.Validator)
         */
        @Override
        public DatastoreBeanListingBuilder<T> withValidator(String property, Validator<?> validator) {
            builder.withValidator(property, validator);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.BeanListingConfigurator# editor(java.lang.String,
         * com.holonplatform.vaadin.flow.components.Input)
         */
        @Override
        public DatastoreBeanListingBuilder<T> editor(String property, Input<?> editor) {
            builder.editor(property, editor);
            return this;
        }

    @Override
    public DatastoreBeanListingBuilder<T> setItems(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      builder.setItems(filterGroup, fetchCallback);
      return this;
    }

    @Override
    public DatastoreBeanListingBuilder<T> refreshOnFilterChange(FilterInputGroup filterGroup) {
      builder.refreshOnFilterChange(filterGroup);
      return this;
    }

    @Override
    public DatastoreBeanListingBuilder<T> refreshOnFilterSignal(FilterInputGroup filterGroup) {
      builder.refreshOnFilterSignal(filterGroup);
      return this;
    }

    @Override
    public DatastoreBeanListingBuilder<T> bindFilters(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      builder.bindFilters(filterGroup, fetchCallback);
      return this;
    }

    @Override
    public DatastoreBeanListingBuilder<T> bindFiltersSignal(FilterInputGroup filterGroup,
        FilterInputSupport.FilteredFetchCallback<T> fetchCallback) {
      builder.bindFiltersSignal(filterGroup, fetchCallback);
      return this;
    }

        @Override
        public DatastoreBeanListingBuilder<T> includeVirtualColumns(boolean yes) {
            builder.includeVirtualColumns(yes);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders. DatastoreDataProviderConfigurator#
         * withQueryConfigurationProvider(com.holonplatform.core.query. QueryConfigurationProvider)
         */
        @Override
        public DatastoreBeanListingBuilder<T> withQueryConfigurationProvider(
                QueryConfigurationProvider queryConfigurationProvider) {
            queryConfigurator.addQueryConfigurationProvider(queryConfigurationProvider);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.
         * DatastoreDataProviderConfigurator#withDefaultQuerySort(com. holonplatform.core.query.QuerySort)
         */
        @Override
        public DatastoreBeanListingBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
            queryConfigurator.setDefaultSort(defaultQuerySort);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.
         * DatastoreDataProviderConfigurator#itemIdentifierProvider( java.util.function.Function)
         */
        @Override
        public DatastoreBeanListingBuilder<T> itemIdentifierProvider(Function<T, Object> itemIdentifierProvider) {
            queryConfigurator.setItemIdentifier(itemIdentifierProvider);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.
         * DatastoreDataProviderConfigurator#querySortOrderConverter( java.util.function.Function)
         */
        @Override
        public DatastoreBeanListingBuilder<T> querySortOrderConverter(
                Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
            queryConfigurator.setQuerySortOrderConverter(querySortOrderConverter);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * editorComponent(java.lang.Object, java.util.function.Function)
         */
        @Override
        public DatastoreBeanListingBuilder<T> editorComponent(String property,
                                                              Function<T, ? extends Component> editorComponentProvider) {
            builder.editorComponent(property, editorComponentProvider);
            return this;
        }

        @Override
        public ItemListingColumnBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> withComponentColumn(
                ValueProvider<T, Component> valueProvider) {
            ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
            final String columnId = builder.getInstance().addColumnProperty();
            final ItemListingColumn<String, T, ?> columnConfiguration = builder.getInstance().getColumnConfiguration(columnId);
            columnConfiguration.setRenderer(new ComponentRenderer<>(valueProvider));
            return new DefaultItemListingColumnBuilder<>(columnId, builder.getInstance(), this);
        }

        @Override
        public <X> ItemListingColumnBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> withColumn(
                ValueProvider<T, X> valueProvider) {
            ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
            final String columnId = builder.getInstance().addColumnProperty();
            builder.getInstance().getColumnConfiguration(columnId).setValueProvider(new ValueProviderAdapter<>(valueProvider));
            return new DefaultItemListingColumnBuilder<>(columnId, builder.getInstance(), this);
        }

        @Override
        public DatastoreBeanListingBuilder<T> hiddenColumns(List<? extends String> hiddenColumns) {
            builder.hiddenColumns(hiddenColumns);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * displayAsFirst(java.lang.Object)
         */
        @Override
        public DatastoreBeanListingBuilder<T> displayAsFirst(String property) {
            builder.displayAsFirst(property);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> freezeMultiSelectCheckBoxColumn(boolean freeze) {
            builder.freezeMultiSelectCheckBoxColumn(freeze);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> scrollUsingUpDownKeys() {
            builder.scrollUsingUpDownKeys();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> toggleableColumns() {
            builder.toggleableColumns();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> compact() {
            builder.compact();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> stretch() {
            builder.stretch();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> wrapCellContent() {
            builder.wrapCellContent();
            return this;
        }

        // Kept commented out intentionally (legacy API placeholder).
		/*@Override
		public DatastoreBeanListingBuilder<T> autoCreateColumns(boolean autoCreateColumns) {
			builder.autoCreateColumns(autoCreateColumns);
			return this;
		}*/

        /**
         * Set all items selected
         *
         * @return this
         */
        @Override
        public DatastoreBeanListingBuilder<T> selectAll() {
            builder.selectAll();
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * displayAsLast(java.lang.Object)
         */
        @Override
        public DatastoreBeanListingBuilder<T> displayAsLast(String property) {
            builder.displayAsLast(property);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * displayBefore(java.lang.Object, java.lang.Object)
         */
        @Override
        public DatastoreBeanListingBuilder<T> displayBefore(String property, String beforeProperty) {
            builder.displayBefore(property, beforeProperty);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * displayAfter(java.lang.Object, java.lang.Object)
         */
        @Override
        public DatastoreBeanListingBuilder<T> displayAfter(String property, String afterProperty) {
            builder.displayAfter(property, afterProperty);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
         * visibleColumns(java.util.List)
         */
        @Override
        public DatastoreBeanListingBuilder<T> visibleColumns(List<? extends String> visibleColumns) {
            builder.visibleColumns(visibleColumns);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# sortable(boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> sortable(boolean sortable) {
            builder.sortable(sortable);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# sortable(java.lang.Object,
         * boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> sortable(String property, boolean sortable) {
            builder.sortable(property, sortable);
            return this;
        }


        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# resizable(boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> resizable(boolean resizable) {
            builder.resizable(resizable);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# resizable(java.lang.Object,
         * boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> resizable(String property, boolean resizable) {
            builder.resizable(property, resizable);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> width(String property, String width) {
            builder.width(property, width);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> autoWidth(String property, boolean autoWidth) {
            builder.autoWidth(property, autoWidth);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> flexGrow(String property, int flexGrow) {
            builder.flexGrow(property, flexGrow);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> flexGrow(int flexGrow, String... properties) {
            builder.flexGrow(flexGrow, properties);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> styleNameGenerator(Function<T, String> styleNameGenerator) {
            builder.styleNameGenerator(styleNameGenerator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> partNameGenerator(SerializableFunction<T, String> partNameGenerator) {
            builder.partNameGenerator(partNameGenerator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> styleNameGenerator(String property,
                                                                 Function<T, String> styleNameGenerator) {
            builder.styleNameGenerator(property, styleNameGenerator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> expand(String property) {
            builder.expand(property);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> columnsAutoWidth() {
            builder.columnsAutoWidth();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> alignment(String property, ColumnAlignment alignment) {
            builder.alignment(property, alignment);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> renderer(String property, Renderer<T> renderer) {
            builder.renderer(property, renderer);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> numberRenderer(String property) {
            builder.numberRenderer(property);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> statusColumn(String property, String available) {
            builder.statusColumn(property, available);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> valueProvider(String property, ValueProvider<T, String> valueProvider) {
            builder.valueProvider(property, valueProvider);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> sortComparator(String property, Comparator<T> comparator) {
            builder.sortComparator(property, comparator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> sortUsing(String property, List<String> sortProperties) {
            builder.sortUsing(property, sortProperties);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> sortProvider(String property,
                                                           Function<SortDirection, Stream<ItemSort<String>>> sortProvider) {
            builder.sortProvider(property, sortProvider);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> header(String property, Localizable header) {
            builder.header(property, header);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> headerComponent(String property, Component header) {
            builder.headerComponent(property, header);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> headerPartName(String property, String headerPartName) {
            builder.headerPartName(property, headerPartName);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> footer(String property, Localizable footer) {
            builder.footer(property, footer);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> footerPartName(String property, String footerPartName) {
            builder.footerPartName(property, footerPartName);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> footerComponent(String property, Component footer) {
            builder.footerComponent(property, footer);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withColumnPostProcessor(ColumnPostProcessor<String> columnPostProcessor) {
            builder.withColumnPostProcessor(columnPostProcessor);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> pageSize(int pageSize) {
            builder.pageSize(pageSize);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> allRowsVisible(boolean allRowsVisible) {
            builder.allRowsVisible(allRowsVisible);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> columnReorderingAllowed(boolean columnReorderingAllowed) {
            builder.columnReorderingAllowed(columnReorderingAllowed);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemDetailsRenderer(Renderer<T> renderer) {
            builder.itemDetailsRenderer(renderer);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
            builder.itemDetailsVisibleOnClick(detailsVisibleOnClick);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> selectionMode(SelectionMode selectionMode) {
            builder.selectionMode(selectionMode);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> selectAllCheckboxVisibility(
                SelectAllCheckboxVisibility selectAllCheckBoxVisibility) {
            builder.selectAllCheckboxVisibility(selectAllCheckBoxVisibility);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
            builder.withSelectionListener(selectionListener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> multiSort(boolean multiSort) {
            builder.multiSort(multiSort);
            return this;
        }

        @Override
        public ItemListingContextMenuBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> contextMenu() {
            return new DefaultItemListingContextMenuBuilder<>(builder.getInstance(),
                    builder.getInstance().getGrid().addContextMenu(), this);
        }

        @Override
        public DatastoreBeanListingBuilder<T> header(Consumer<EditableItemListingSection<String>> headerConfigurator) {
            builder.header(headerConfigurator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> footer(Consumer<EditableItemListingSection<String>> footerConfigurator) {
            builder.footer(footerConfigurator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> editable(boolean editable) {
            builder.editable(editable);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> editorBuffered(boolean buffered) {
            builder.editorBuffered(buffered);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEditorSaveListener(EditorSaveListener<T, String> listener) {
            builder.withEditorSaveListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEditorCancelListener(EditorCancelListener<T, String> listener) {
            builder.withEditorCancelListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEditorOpenListener(EditorOpenListener<T, String> listener) {
            builder.withEditorOpenListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEditorCloseListener(EditorCloseListener<T, String> listener) {
            builder.withEditorCloseListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withValidator(Validator<T> validator) {
            builder.withValidator(validator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> defaultValue(String property, Supplier<Object> defaultValueProvider) {
            builder.defaultValue(property, defaultValueProvider);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withValueChangeListener(String property,
                ValueChangeListener<?, GroupValueChangeEvent<?, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
            builder.withValueChangeListener(property, listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> required(String property) {
            builder.required(property);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> required(String property, Localizable message) {
            builder.required(property, message);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withPostProcessor(BiConsumer<String, Input<?>> postProcessor) {
            builder.withPostProcessor(postProcessor);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> validationStatusHandler(
                ValidationStatusHandler<EditorComponentGroup<String, T>> validationStatusHandler) {
            builder.validationStatusHandler(validationStatusHandler);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> groupValidationStatusHandler(
                GroupValidationStatusHandler<EditorComponentGroup<String, T>, String, Input<?>> groupValidationStatusHandler) {
            builder.groupValidationStatusHandler(groupValidationStatusHandler);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> validationStatusHandler(String property,
                ValidationStatusHandler<Input<?>> validationStatusHandler) {
            builder.validationStatusHandler(property, validationStatusHandler);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
            builder.enableRefreshOnValueChange(enableRefreshOnValueChange);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
            builder.usePropertyRendererRegistry(propertyRendererRegistry);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withValueChangeListener(
                ValueChangeListener<T, GroupValueChangeEvent<T, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
            builder.withValueChangeListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withItemClickListener(
                ClickEventListener<BeanListing<T>, ItemClickEvent<BeanListing<T>, T>> listener) {
            builder.withItemClickListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withItemRefreshListener(
                ItemEventListener<BeanListing<T>, T, ItemEvent<BeanListing<T>, T>> listener) {
            builder.withItemRefreshListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> id(String id) {
            builder.id(id);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> visible(boolean visible) {
            builder.visible(visible);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> elementConfiguration(Consumer<Element> element) {
            builder.elementConfiguration(element);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withAttachListener(ComponentEventListener<AttachEvent> listener) {
            builder.withAttachListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withDetachListener(ComponentEventListener<DetachEvent> listener) {
            builder.withDetachListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withThemeName(String themeName) {
            builder.withThemeName(themeName);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEventListener(String eventType, DomEventListener listener) {
            builder.withEventListener(eventType, listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withEventListener(String eventType, DomEventListener listener, String filter) {
            builder.withEventListener(eventType, listener, filter);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> width(String width) {
            builder.width(width);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> height(String height) {
            builder.height(height);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> minWidth(String minWidth) {
            builder.minWidth(minWidth);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> maxWidth(String maxWidth) {
            builder.maxWidth(maxWidth);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> minHeight(String minHeight) {
            builder.minHeight(minHeight);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> maxHeight(String maxHeight) {
            builder.maxHeight(maxHeight);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> styleNames(String... styleNames) {
            builder.styleNames(styleNames);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> styleName(String styleName) {
            builder.styleName(styleName);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> enabled(boolean enabled) {
            builder.enabled(enabled);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> tabIndex(int tabIndex) {
            builder.tabIndex(tabIndex);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
            builder.withFocusListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
            builder.withBlurListener(listener);
            return this;
        }

        @Override
        public ShortcutConfigurator<DatastoreBeanListingBuilder<T>> withFocusShortcut(Key key) {
            return new DefaultShortcutConfigurator<>(builder.getInstance().getGrid().addFocusShortcut(key), this);
        }

        @Override
        public DatastoreBeanListingBuilder<T> withThemeVariants(GridVariant... variants) {
            builder.withThemeVariants(variants);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> frozen(boolean frozen) {
            builder.frozen(frozen);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> rowsDraggable(boolean rowsDraggable) {
            builder.rowsDraggable(rowsDraggable);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> dragFilter(Predicate<T> dragFilter) {
            builder.dragFilter(dragFilter);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> dragDataGenerator(String type, Function<T, String> dragDataGenerator) {
            builder.dragDataGenerator(type, dragDataGenerator);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withDragStartListener(
                ItemListingDnDListener<T, String, ItemListingDragStartEvent<T, String>> listener) {
            builder.withDragStartListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withDragEndListener(
                ItemListingDnDListener<T, String, ItemListingDragEndEvent<T, String>> listener) {
            builder.withDragEndListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> dropMode(GridDropMode dropMode) {
            builder.dropMode(dropMode);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> dropFilter(Predicate<T> dropFilter) {
            builder.dropFilter(dropFilter);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withDropListener(
                ItemListingDnDListener<T, String, ItemListingDropEvent<T, String>> listener) {
            builder.withDropListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withColumnResizeListener(ColumnResizeListener<T, String> listener) {
            builder.withColumnResizeListener(listener);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> withColumnReorderListener(ColumnReorderListener<T, String> listener) {
            builder.withColumnReorderListener(listener);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# visible(java.lang.Object,
         * boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> visible(String property, boolean visible) {
            builder.visible(property, visible);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# readOnly(java.lang.Object,
         * boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> readOnly(String property, boolean readOnly) {
            builder.readOnly(property, readOnly);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# frozen(java.lang.Object,
         * boolean)
         */
        @Override
        public DatastoreBeanListingBuilder<T> frozen(String property, boolean frozen) {
            builder.frozen(property, frozen);
            return this;
        }

        /**
         * Set whether the column which corresponds to given property is frozen at the end.
         *
         * @param property The property to create (not null)
         * @param frozen   Whether given property is frozen
         * @return this
         */
        @Override
        public DatastoreBeanListingBuilder<T> frozenAtEnd(String property, boolean frozen) {
            builder.frozenAtEnd(property, frozen);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> tooltipGenerator(String property, SerializableFunction<T, String> tooltipGenerator) {
            builder.tooltipGenerator(property, tooltipGenerator);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# frozenColumns(int)
         */
        @Override
        public DatastoreBeanListingBuilder<T> frozenColumns(int frozenColumnsCount) {
            builder.frozenColumns(frozenColumnsCount);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> emptyStateText(String text) {
            builder.emptyStateText(text);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> scrollToColumn(int columnIndex) {
            builder.scrollToColumn(columnIndex);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> scrollToColumn(Grid.Column<T> column) {
            builder.scrollToColumn(column);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> emptyStateComponent(Component component) {
            builder.emptyStateComponent(component);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> tooltipMarkdownEnabled(boolean markdownEnabled) {
            builder.tooltipMarkdownEnabled(markdownEnabled);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# build()
         */
        @Override
        public BeanListing<T> build() {
            return builder.build();
        }

        @Override
        public DatastoreBeanListingBuilder<T> items(BackEndDataProvider<T, Void> dataProvider) {
            builder.items(dataProvider);
            return this;
        }

		/*@Override
		public DatastoreBeanListingBuilder<T> mobileColumn(ValueProvider<T, Component> valueProvider) {
			builder.mobileColumn(valueProvider);
			return this;
		}*/

        @Override
        public DatastoreBeanListingBuilder<T> mobileColumn(String property, Renderer<T> renderer) {
            builder.mobileColumn(property, renderer);
            return this;
        }

		/*@Override
		public DatastoreBeanListingBuilder<T> showMobileColumn(boolean show) {
			builder.showMobileColumn(show);
			return this;
		}*/

        @Override
        public DatastoreBeanListingBuilder<T> items(CallbackDataProvider.FetchCallback<T, Void> fetchCallback) {
            builder.items(fetchCallback);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> items(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                                    CallbackDataProvider.CountCallback<T, Void> countCallback) {
            builder.items(fetchCallback, countCallback);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemCountEstimate(int estimate) {
            builder.itemCountEstimate(estimate);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemCountEstimateIncrease(int estimateIncrease) {
            builder.itemCountEstimateIncrease(estimateIncrease);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemCountUnknown() {
            builder.itemCountUnknown();
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemsPageable(Grid.SpringData.FetchCallback<?, T> fetchCallback) {
            builder.itemsPageable(fetchCallback);
            return this;
        }

        @Override
        public DatastoreBeanListingBuilder<T> itemsPageable(Grid.SpringData.FetchCallback<?, T> fetchCallback,
                                                            Grid.SpringData.CountCallback<?> countCallback) {
            builder.itemsPageable(fetchCallback, countCallback);
            builder.itemCountUnknown();
            return this;
        }
    }

    static class ValueProviderAdapter<T, V> implements ValueProvider<T, String> {

        @Serial
        private static final long serialVersionUID = -3231386190085166260L;

        private final ValueProvider<T, V> provider;

        public ValueProviderAdapter(ValueProvider<T, V> provider) {
            super();
            this.provider = provider;
        }

        @Override
        public String apply(T source) {
            return StringValuePresenter.getDefault().present(provider.apply(source));
        }

    }

}














