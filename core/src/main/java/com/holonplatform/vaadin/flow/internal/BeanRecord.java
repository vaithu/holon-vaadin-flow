package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.core.property.PathProperty;

import java.io.Serializable;
import java.util.List;

public record BeanRecord<T>(Class<T> beanClass, List<PathProperty<? extends Serializable>> columnList) {

    public static <T> BeanRecord<T> of(Class<T> beanClass, List<PathProperty<? extends Serializable>> columnList) {
        return new BeanRecord<>(beanClass, columnList);
    }
}