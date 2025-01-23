package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.test.pojo.Person;
import org.junit.jupiter.api.Test;

public class TestBeanUtils {

    @Test
    public void test() {
//        final Class<?> beanClass = BeanUtils.getBeanClass(Person.class);
        Person person = new Person();
        final PropertyBox propertyBox = BeanIntrospector.getDefault().read(person);
        System.out.println(propertyBox);
    }
}
