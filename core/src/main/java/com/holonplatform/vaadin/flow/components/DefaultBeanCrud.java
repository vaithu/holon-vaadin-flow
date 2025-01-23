package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Context;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.*;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.utils.BeanUtils;
import com.holonplatform.vaadin.flow.internal.BeanRecord;
import com.vaadin.flow.data.provider.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultBeanCrud<T> implements HasBeanRecord<T> {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanCrud.class);

    private final Datastore datastore;
    private BeanRecord<T> beanRecord;

    public DefaultBeanCrud() {
        datastore = Context.get().resource(Datastore.class)
                .orElseThrow(() -> new IllegalStateException("Cannot retrieve Datastore from Context."));
    }

    public DefaultBeanCrud(BeanRecord<T> beanRecord) {
        this();
        this.beanRecord = beanRecord;
    }


    @Transactional
    public T save(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
        final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
        datastore.save(DataTarget.named(beanClazz.getName()), propertyBox, DefaultWriteOption.BRING_BACK_GENERATED_IDS);
        return BeanUtils.getBean(propertyBox, beanInstance);
    }

    @Transactional
    public T insert(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
        final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
        datastore.insert(DataTarget.named(beanClazz.getName()), propertyBox, DefaultWriteOption.BRING_BACK_GENERATED_IDS);
        return BeanUtils.getBean(propertyBox, beanInstance);
    }


    @Transactional
    public T update(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
        final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
        datastore.update(DataTarget.named(beanClazz.getName()), propertyBox, DefaultWriteOption.BRING_BACK_GENERATED_IDS);
        return BeanUtils.getBean(propertyBox, beanInstance);
    }

    @Transactional
    public List<T> bulkUpdate(List<T> list) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(beanInstance -> {
            final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
            final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
            datastore.bulkUpdate(DataTarget.named(beanClazz.getName()))
                    .set(propertyBox)
                    .withWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)
                    .execute();

            updatedList.add(BeanUtils.getBean(propertyBox, beanInstance));
        });

        return updatedList;
    }

    @Transactional
    public List<T> bulkInsert(List<T> list) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(beanInstance -> {
            final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
            final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
            datastore.bulkInsert(DataTarget.named(beanClazz.getName()),propertyBox)
                    .withWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)
                    .execute();

            updatedList.add(BeanUtils.getBean(propertyBox, beanInstance));
        });

        return updatedList;
    }

    @Transactional
    public boolean delete(T beanInstance)  {

        final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
        final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
        final Datastore.OperationResult operationResult = datastore.delete(DataTarget.named(beanClazz.getName()), propertyBox);

        return operationResult.getAffectedCount() > 0;
    }

    public Stream<T> fetchAll(Query<T, ?> query) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .restrict(query.getLimit(), query.getOffset())
                .stream(BeanProjection.of(getBeanRecord().beanClass(), getBeanRecord().columnList()));
    }

    public Stream<T> sortBy(Query<T, ?> query, PathProperty<? extends Serializable> pathProperty) {
        return sortBy(query, pathProperty.asc());
    }

    public Stream<T> sortBy(Query<T, ?> query, QuerySort... querySort) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .restrict(query.getLimit(), query.getOffset())
                .sort(querySort)
                .stream(BeanProjection.of(getBeanRecord().beanClass(), getBeanRecord().columnList()));
    }

    public Stream<T> filterAndSortBy(Query<T, ?> query, QuerySort querySort, QueryFilter... queryFilter) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .restrict(query.getLimit(), query.getOffset())
                .filter(queryFilter)
                .sort(querySort)
                .stream(BeanProjection.of(getBeanRecord().beanClass(), getBeanRecord().columnList()));
    }

    public Stream<T> filterBy(Query<T, ?> query, QueryFilter... queryFilter) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .restrict(query.getLimit(), query.getOffset())
                .filter(queryFilter)
                .stream(BeanProjection.of(getBeanRecord().beanClass(), getBeanRecord().columnList()));
    }

    @SuppressWarnings("unchecked")
    private static QueryFilter getQueryFilter(PathProperty<?> property, String searchFilter) {
        if (TypeUtils.isString(property.getType())) {
            return ((StringProperty) property).containsIgnoreCase(searchFilter);
        } else if (TypeUtils.isBigDecimal(property.getType())) {
            return ((NumericProperty<BigDecimal>) property)
                    .eq(new BigDecimal(searchFilter));
        } else if (TypeUtils.isDouble(property.getType())) {
            return ((NumericProperty<Double>) property).eq(Double.valueOf(searchFilter));
        } else if (TypeUtils.isInteger(property.getType())) {
            return ((NumericProperty<Integer>) property).eq(Integer.valueOf(searchFilter));
        } else if (TypeUtils.isLong(property.getType())) {
            return ((NumericProperty<Long>) property).eq(Long.valueOf(searchFilter));
        } else {
            throw new IllegalArgumentException("Invalid Type. Please check the allowed types");
        }
//        return null;
    }

    public Stream<T> filterBy(Query<T, ?> query, PathProperty<?> pathProperty, String searchText) {
        if (searchText.isEmpty()) {
            log.info("SearchText is empty so showing all records");
            return fetchAll(query);
        }
        return filterBy(query, getQueryFilter(pathProperty, searchText));
    }

    public Stream<T> findAllBy(Query<T, ?> query, QueryFilter... queryFilter) {
        return filterBy(query, queryFilter);
    }

    public Optional<PropertyBox> findOne(QueryFilter... queryFilters) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilters)
                .findOne(getBeanRecord().columnList());
    }

    public Optional<PropertyBox> findOne(QueryFilter queryFilter, PropertySet<?> properties) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilter)
                .findOne(properties);
    }

    public Optional<T> findOne(T beanInstance,QueryFilter queryFilter, PropertySet<?> properties) {
        return convertToBean(beanInstance,findOne(queryFilter, properties));
    }

    private Optional<T> convertToBean(T beanInstance,Optional<PropertyBox> propertyBox) {
        if (propertyBox.isPresent()) {
            final T bean = (T) BeanUtils.getBean(propertyBox.get(), beanInstance);
            return Optional.ofNullable(bean);
        } else {
            return Optional.empty();
        }
    }

    public Optional<T> findOne(T beanInstance, QueryFilter... queryFilters) {
        final Optional<PropertyBox> propertyBox = findOne(queryFilters);
        return convertToBean(beanInstance, propertyBox);
    }


    @Transactional
    public boolean deleteSelectedItems(QueryFilter... queryFilter) {
        return datastore.bulkDelete(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilter)
                .execute().getAffectedCount() > 0;
    }

    public T refresh(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.getPropertyBox(beanInstance);
        final Class<?> beanClazz = BeanUtils.getBeanClass(beanInstance);
        datastore.refresh(DataTarget.named(beanClazz.getName()), propertyBox);
        return BeanUtils.getBean(propertyBox, beanInstance);
    }

    public Datastore getDatastore() {
        return datastore;
    }

    @Override
    public BeanRecord<T> getBeanRecord() {
        return beanRecord;
    }

    @Override
    public void setBeanRecord(BeanRecord<T> beanRecord) {
        this.beanRecord = beanRecord;
    }
}