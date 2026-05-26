package com.holonplatform.vaadin.flow.components.utils;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;

public class BeanUtils {
    /**
     * Get the runtime class of a bean instance.
     */
    public static Class<?> resolveBeanClass(Object bean) {
        return bean.getClass();
    }

    /**
     * Populate a bean instance from a PropertyBox.
     * (PropertyBox → Bean)
     */
    public static <T> T writeToBean(PropertyBox propertyBox, T targetBean) {
        return BeanIntrospector.getDefault()
                .write(propertyBox, targetBean, true);
    }



    /**
     * Read bean values into a PropertyBox using the given PropertySet.
     * (Bean → PropertyBox constrained by PropertySet)
     */
    public static <T> PropertyBox readFromBean(
            PropertySet<?> propertySet,
            T sourceBean
    ) {
        PropertyBox propertyBox = PropertyBox.builder(propertySet).build();
        return BeanIntrospector.get()
                .read(propertyBox, sourceBean);
    }

    /**
     * Read bean values into a PropertyBox using introspected properties.
     * (Bean → PropertyBox, unconstrained)
     */
    public static <T> PropertyBox readFromBean(T sourceBean) {
        return BeanIntrospector.get()
                .read(sourceBean);
    }
}