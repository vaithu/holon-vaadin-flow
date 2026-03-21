package com.holonplatform.vaadin.flow;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Optional;

public interface HasFormView<T> {

    void clearForm();

    void setFormValueFromBean(T value) throws ValidationException;
    void setFormValueFromBean() throws ValidationException;

    Optional<T> getFormValueToBean() throws ValidationException;
    Optional<T> getFormValueToBean(T value) throws ValidationException;

    void setReadOnly(boolean readOnly);

    boolean isValid();

    void validate();

    boolean hasChanges();

    boolean isOK();

    BeanValidationBinder<T> getBinder();

    void setBinder(BeanValidationBinder<T> binder);

    void setBean(T bean);

//    T getBean();

    void populateForm(T bean);
    void populateForm();
    void populateBean(T bean) throws ValidationException;

    void populateBean() throws ValidationException;

    void initializeFields();

    void customizeBinder();

    T createNewInstance();

    Component createForm();

}
