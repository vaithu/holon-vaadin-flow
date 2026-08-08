package com.holonplatform.vaadin.flow;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.internal.utils.BeanUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class OverviewHandler<T> {

    private final BeanDatastoreHelper<T> helper;
    private final VerticalLayout container;
    private PropertyBox propertyBox;

    public OverviewHandler(BeanDatastoreHelper<T> helper, VerticalLayout container) {
        this.helper = helper;
        this.container = container;
    }

    public void configureLayoutWithKeyValuePairs(PropertyBox propertyBox) {
        UIUtils.clearContainer(container);
        final KeyValuePairs keyValuePairs = UIUtils.createKeyValuePairs(propertyBox);
        container.add(keyValuePairs);
    }

    public void handleNoValuesFound() {
        UIUtils.clearContainer(container);
        UIUtils.handleNoValuesFound(container);
    }

    public void handleNoRecordsFound() {
        UIUtils.clearContainer(container);
        UIUtils.handleNoRecordsFound(container);
    }

    private Optional<PropertyBox> findOne(QueryFilter queryFilter, PropertySet<?> properties) {
        return helper.getDatastore()
                .query(DataTarget.named(helper.getBeanClass().getName()))
                .filter(queryFilter)
                .findOne(properties);
    }

    public void findAndHandleItem(QueryFilter queryFilter, PropertySet<?> propertySet) {
        findOne(queryFilter, propertySet).ifPresentOrElse(propertyBox -> {
            this.propertyBox = propertyBox;
            if (UIUtils.isPropertyBoxEmpty(this.propertyBox)) {
                handleNoValuesFound();
            } else {
                configureLayoutWithKeyValuePairs(this.propertyBox);
            }
        }, this::handleNoRecordsFound);
    }

    public VerticalLayout getContent() {
        return container;
    }

    public T getResult() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return BeanUtils.writeToBean(propertyBox, helper.getBeanClass().getConstructor().newInstance());
    }
}
