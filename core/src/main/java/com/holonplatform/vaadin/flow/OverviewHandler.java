package com.holonplatform.vaadin.flow;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.DefaultBeanCrud;
import com.holonplatform.vaadin.flow.components.utils.BeanUtils;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.lang.reflect.InvocationTargetException;

public class OverviewHandler<T> {

        private final DefaultBeanCrud<T> beanCrud;
        private final VerticalLayout container;
        private PropertyBox propertyBox;

        public OverviewHandler(DefaultBeanCrud<T> beanCrud, VerticalLayout container) {
            this.beanCrud = beanCrud;
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

        public void findAndHandleItem(QueryFilter queryFilter, PropertySet<?> propertySet) {
            beanCrud.findOne(queryFilter, propertySet).ifPresentOrElse(propertyBox -> {
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
            return BeanUtils.getBean(propertyBox, beanCrud.getBeanRecord().beanClass().getConstructor().newInstance());
        }
    }