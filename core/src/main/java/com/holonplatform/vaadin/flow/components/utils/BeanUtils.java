package com.holonplatform.vaadin.flow.components.utils;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.property.PropertyBox;

public class BeanUtils {
    public static <T> Class<?> getBeanClass(T bean) {
        return bean.getClass();
    }

    public static <T> T getBean(PropertyBox propertyBox, T beanInstance) {
        return BeanIntrospector.getDefault().write(propertyBox, beanInstance, true);
    }

    public static <T> PropertyBox getPropertyBox(T beanInstance) {
        return BeanIntrospector.get().read(beanInstance);
    }


}