package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Context;
import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.datastore.beans.BeanDatastore;
import com.holonplatform.core.datastore.beans.BeanQuery;
import com.holonplatform.core.internal.datastore.beans.DefaultBeanDatastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.internal.BeanRecord;
import com.vaadin.flow.data.provider.Query;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
public final class DefaultBeanCrudV2<T> implements HasBeanRecord<T>{

    private final DefaultBeanDatastore beanDatastore;
    private final DefaultWriteOption defaultWriteOption = DefaultWriteOption.BRING_BACK_GENERATED_IDS;
    private BeanRecord<T> beanRecord;

    public DefaultBeanCrudV2() {
        Datastore datastore = Context.get().resource(Datastore.class)
                .orElseThrow(() -> new IllegalStateException("Cannot retrieve Datastore from Context."));
        beanDatastore = new DefaultBeanDatastore(datastore);
    }

    @Override
    public BeanRecord<T> getBeanRecord() {
        return beanRecord;
    }

    @Override
    public void setBeanRecord(BeanRecord<T> beanRecord) {
        this.beanRecord = beanRecord;
    }

    @Transactional
    public Optional<T> save(T bean) {
        final BeanDatastore.BeanOperationResult<T> operationResult =
                beanDatastore.save(bean, defaultWriteOption);

        return operationResult.getResult();
    }

    @Transactional
    public Optional<T> insert(T bean) {
        return beanDatastore.insert(bean, defaultWriteOption).getResult();
    }

    @Transactional
    public Optional<T> update(T bean) {
        return beanDatastore.update(bean, defaultWriteOption).getResult();
    }

    @Transactional
    public Optional<T> delete(T bean) {
        return beanDatastore.delete(bean, defaultWriteOption).getResult();
    }

    @Transactional
    public  List<T>  bulkInsert( List<T> list) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(t -> {
            beanDatastore.bulkInsert(t.getClass(), defaultWriteOption);
            updatedList.add(t);
        });
        return updatedList;
    }

    @Transactional
    public  List<T>  bulkUpdate( List<T> list) {
        List<T> updatedList = new ArrayList<>();
       list.forEach(t -> {
           beanDatastore.bulkUpdate(t.getClass(), defaultWriteOption);
           updatedList.add(t);
       });
        return updatedList;
    }

    @Transactional
    public  List<T>  bulkUpdate(List<T> list, QueryFilter... queryFilters) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(t -> {
            beanDatastore.bulkUpdate(t.getClass(), defaultWriteOption).filter(queryFilters);
            updatedList.add(t);
        });
        return updatedList;
    }

    @Transactional
    public  List<T>  bulkDelete( List<T> list) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(t -> {
            beanDatastore.bulkDelete(t.getClass(), defaultWriteOption);
            updatedList.add(t);
        });
        return updatedList;
    }

    @Transactional
    public  List<T>  bulkDelete(List<T> list, QueryFilter... queryFilters) {
        List<T> updatedList = new ArrayList<>();
        list.forEach(t -> {
            beanDatastore.bulkDelete(t.getClass(), defaultWriteOption).filter(queryFilters);
            updatedList.add(t);
        });
        return updatedList;
    }

    public BeanQuery<T> paging(Class<T> beanClass, Query<T, ?> query) {
        return
        beanDatastore.query(beanClass)
                .restrict(query.getLimit(), query.getOffset());
    }

    private void checkBeanRecord() {
        ObjectUtils.argumentNotNull(beanRecord, "BeanRecord cannot be null here. Set it first");
    }

    public BeanQuery<T> paging(Query<T, ?> query) {
        checkBeanRecord();
        return
                beanDatastore.query(beanRecord.beanClass())
                        .restrict(query.getLimit(), query.getOffset());
    }

    private Path<?>[] getPath() {
        return beanRecord.columnList().toArray(new Path[0]);
    }

    public Stream<T> fetchAll( Query<T,?> query) {
        return paging(query)
                .stream(beanRecord.beanClass(), getPath());
    }

    public Stream<T> fetchAll(Class<T> beanClass, Query<T,?> query) {
        return paging(beanClass, query)
                .stream();
    }

    public Stream<T> sortBy(Class<T> beanClass,Query<T, ?> query, QuerySort... querySort) {
        return paging(beanClass, query)
                .sort(querySort)
                .stream();
    }

    public Stream<T> sortBy(Query<T, ?> query, QuerySort... querySort) {
        return paging(query)
                .sort(querySort)
                .stream(beanRecord.beanClass(), getPath());
    }

    public Stream<T> filterAndSortBy(Class<T> beanClass,Query<T, ?> query, QuerySort querySort, QueryFilter... queryFilter) {
        return paging(beanClass, query)
                .sort(querySort)
                .filter(queryFilter)
                .stream();
    }

    public Stream<T> filterAndSortBy(Query<T, ?> query, QuerySort querySort, QueryFilter... queryFilter) {
        return paging(query)
                .sort(querySort)
                .filter(queryFilter)
                .stream(beanRecord.beanClass(), getPath());
    }

    public BeanQuery<T> filter(Class<T> beanClass, Query<T, ?> query, QueryFilter... queryFilter) {
        return
        paging(beanClass, query).filter(queryFilter);
    }

    public BeanQuery<T> filter( Query<T, ?> query, QueryFilter... queryFilter) {
        return
                paging(query).filter(queryFilter);
    }

    public Stream<T> filterBy(Class<T> beanClass,Query<T, ?> query, QueryFilter... queryFilter) {
        return filter(beanClass, query, queryFilter).stream();
    }

    public Stream<T> filterBy(Query<T, ?> query, QueryFilter... queryFilter) {
        return filter(query, queryFilter).stream(beanRecord.beanClass(), getPath());
    }

    public Stream<T> filterBy(Class<T> beanClass, Query<T, ?> query, QueryFilter queryFilter, Path<?>... paths) {
        return filter(beanClass, query, queryFilter).stream(beanClass, paths);
    }

    public Optional<T> findOne(Class<T> beanClass,  QueryFilter... queryFilter) {
        return beanDatastore.query(beanClass)
                .filter(queryFilter)
                .findOne();
    }

    public Optional<T> findOne(  QueryFilter... queryFilter) {
        return beanDatastore.query(beanRecord.beanClass())
                .filter(queryFilter)
                .findOne();
    }

    public Optional<T> findFirst(Class<T> beanClass,  QueryFilter... queryFilter) {
        return beanDatastore.query(beanClass)
                .filter(queryFilter)
                .stream().findFirst();
    }

    public Optional<T> findFirst( QueryFilter... queryFilter) {
        return beanDatastore.query(beanRecord.beanClass())
                .filter(queryFilter)
                .stream(beanRecord.beanClass(), getPath()).findFirst();
    }

    public Optional<T> findAny(Class<T> beanClass,  QueryFilter... queryFilter) {
        return beanDatastore.query(beanClass)
                .filter(queryFilter)
                .stream().findAny();
    }

    public Optional<T> findAny(  QueryFilter... queryFilter) {
        return beanDatastore.query(beanRecord.beanClass())
                .filter(queryFilter)
                .stream(beanRecord.beanClass(), getPath()).findAny();
    }

    public T refresh(T bean) {
        return beanDatastore.refresh(bean);
    }

    public DefaultBeanDatastore getBeanDatastore() {
        return beanDatastore;
    }
}
