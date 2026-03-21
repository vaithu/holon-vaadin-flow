package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.internal.BeanRecord;

public interface HasBeanRecord<T> {

    void setBeanRecord(BeanRecord<T> beanRecord);

    BeanRecord<T> getBeanRecord();

}
