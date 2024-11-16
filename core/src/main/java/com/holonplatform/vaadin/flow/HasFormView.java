package com.holonplatform.vaadin.flow;

import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Optional;

public interface HasFormView<T> {

    void clearForm();

    void setFormValue(T value) throws ValidationException;
    void setFormValue() throws ValidationException;

    Optional<T> getFormValue() throws ValidationException;
    Optional<T> getFormValue(T value) throws ValidationException;

    void setReadOnly(boolean readOnly);

    boolean isValid();

    boolean hasChanges();

    boolean isOK();

    T createBlankForm();

    BeanValidationBinder<T> getBinder();

    void setBinder(BeanValidationBinder<T> binder);

    void setBean(T bean);

    T getBean();

    void populateForm(T bean);
    void populateForm();
    void populateBean(T bean) throws ValidationException;
    void populateBean() throws ValidationException;

}
