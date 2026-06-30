package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultBeanPropertyInputFormTest {

    @Test
    void setBean_nullClearsDelegateValue() {
        PropertyInputForm delegate = mock(PropertyInputForm.class);
        BeanPropertySet<Person> beanPropertySet = BeanPropertySet.create(Person.class);
        DefaultBeanPropertyInputForm<Person> form = new DefaultBeanPropertyInputForm<>(delegate, beanPropertySet);

        form.setBean(null);

        verify(delegate).setValue((PropertyBox) null, false);
    }

    @Test
    void setBean_populatesDelegateValueFromBean() {
        PropertyInputForm delegate = mock(PropertyInputForm.class);
        BeanPropertySet<Person> beanPropertySet = BeanPropertySet.create(Person.class);
        DefaultBeanPropertyInputForm<Person> form = new DefaultBeanPropertyInputForm<>(delegate, beanPropertySet);

        Person bean = new Person();
        bean.setName("Jane");
        bean.setAge(42);

        form.setBean(bean);

        ArgumentCaptor<PropertyBox> captor = ArgumentCaptor.forClass(PropertyBox.class);
        verify(delegate).setValue(captor.capture(), eq(false));

        PropertyBox value = captor.getValue();
        String name = value.getValue(beanPropertySet.property("name"));
        Integer age = value.getValue(beanPropertySet.property("age"));
        assertThat(name).isEqualTo("Jane");
        assertThat(age).isEqualTo(42);
    }

    public static class Person {
        private String name;
        private Integer age;

        public Person() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}