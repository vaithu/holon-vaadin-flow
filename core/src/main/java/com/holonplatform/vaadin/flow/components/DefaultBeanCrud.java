package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Context;
import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.*;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.utils.BeanUtils;
import com.holonplatform.vaadin.flow.internal.BeanRecord;
import com.vaadin.flow.data.provider.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @deprecated Use {@link com.holonplatform.core.datastore.beans.BeanDatastoreHelper} instead.
 *             {@code BeanDatastoreHelper} is the official, type-safe holon-core façade for bean
 *             CRUD operations and offers a far richer query API (findPage, findSlice, findFirst,
 *             findTop, count, exists, bulk operations, transactions).
 *             <pre>{@code
 *             BeanDatastoreHelper<Product> products =
 *                 BeanDatastoreHelper.of(datastore, Product.class);
 *             products.save(product);
 *             Optional<Product> one = products.findOne(filter);
 *             List<Product> page   = products.findPage(0, 20);
 *             }</pre>
 */
@Deprecated(since = "10.0.0", forRemoval = true)
public class DefaultBeanCrud<T> implements HasBeanRecord<T> {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanCrud.class);

    private final Datastore datastore;

    public Datastore getDatastore() {
        return datastore;
    }
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

        final PropertyBox propertyBox = BeanUtils.readFromBean(beanInstance);
        final Class<?> beanClazz = BeanUtils.resolveBeanClass(beanInstance);
        datastore.save(DataTarget.named(beanClazz.getName()), propertyBox,
                DefaultWriteOption.BRING_BACK_GENERATED_IDS_AND_VERSION);
        return BeanUtils.writeToBean(propertyBox, beanInstance);
    }

    @Transactional
    public T insert(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.readFromBean(beanInstance);
        final Class<?> beanClazz = BeanUtils.resolveBeanClass(beanInstance);
        datastore.insert(DataTarget.named(beanClazz.getName()), propertyBox,
                DefaultWriteOption.BRING_BACK_GENERATED_IDS_AND_VERSION);
        return BeanUtils.writeToBean(propertyBox, beanInstance);
    }


    @Transactional
    public T update(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.readFromBean(beanInstance);
        final Class<?> beanClazz = BeanUtils.resolveBeanClass(beanInstance);
        datastore.update(DataTarget.named(beanClazz.getName()), propertyBox,
                DefaultWriteOption.BRING_BACK_GENERATED_IDS_AND_VERSION);
        return BeanUtils.writeToBean(propertyBox, beanInstance);
    }

    @Transactional
    public List<T> bulkUpdate(List<T> list) {
        return list.stream()
                .map(this::update)
                .toList();
    }

    @Transactional
    public List<T> bulkInsert(List<T> list) {
        return list.stream()
                .map(this::insert)
                .toList();
    }

    @Transactional
    public boolean delete(T beanInstance)  {

        final PropertyBox propertyBox = BeanUtils.readFromBean(beanInstance);
        final Class<?> beanClazz = BeanUtils.resolveBeanClass(beanInstance);
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
        final Class<?> type = property.getType();
        if (TypeUtils.isString(type))     return ((StringProperty) property).containsIgnoreCase(searchFilter);
        if (TypeUtils.isBigDecimal(type)) return ((NumericProperty<BigDecimal>) property).eq(new BigDecimal(searchFilter));
        if (TypeUtils.isDouble(type))     return ((NumericProperty<Double>) property).eq(Double.parseDouble(searchFilter));
        if (TypeUtils.isInteger(type))    return ((NumericProperty<Integer>) property).eq(Integer.parseInt(searchFilter));
        if (TypeUtils.isLong(type))       return ((NumericProperty<Long>) property).eq(Long.parseLong(searchFilter));
        throw new IllegalArgumentException("Unsupported property type: " + type.getName());
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

    public Stream<T> findAll(QueryFilter... queryFilters) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilters)
                .stream(QueryProjection.bean(getBeanRecord().beanClass()));
    }

    public Optional<T> findOne(QueryFilter... queryFilters) {
        return datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilters)
                .findOne(QueryProjection.bean(getBeanRecord().beanClass()));
    }

    public Optional<T> findOne(PropertySet<?> properties, T bean, QueryFilter... queryFilters) {
        Optional<PropertyBox> propertyBox = datastore.query(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilters)
                .findOne(properties);
        return propertyBox.map(box -> BeanIntrospector.getDefault().write(box, bean));
    }


    @Transactional
    public boolean deleteSelectedItems(QueryFilter... queryFilter) {
        return datastore.bulkDelete(DataTarget.named(getBeanRecord().beanClass().getName()))
                .filter(queryFilter)
                .execute().getAffectedCount() > 0;
    }

    public T refresh(T beanInstance) {

        final PropertyBox propertyBox = BeanUtils.readFromBean(beanInstance);
        final Class<?> beanClazz = BeanUtils.resolveBeanClass(beanInstance);
        datastore.refresh(DataTarget.named(beanClazz.getName()), propertyBox);
        return BeanUtils.writeToBean(propertyBox, beanInstance);
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