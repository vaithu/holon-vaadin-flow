package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.HasFormView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Optional;

public abstract class AbstractFormView<T> implements HasFormView<T> {

    private  BeanValidationBinder<T> binder;
    private T bean;

    public AbstractFormView(BeanValidationBinder<T> binder, T bean) {
        this.binder = binder;
        this.bean = bean;
    }

    public AbstractFormView(BeanValidationBinder<T> binder) {
        this.binder = binder;
    }

    public AbstractFormView() {

    }

    @Override
    public void populateForm(T bean) {
        setFormValue(bean);
    }

    @Override
    public void populateForm() {
        if (this.bean != null) {
            populateForm(this.bean);
        } else {
            throw new IllegalArgumentException("There is no bean already set");
        }
    }

    @Override
    public void populateBean(T bean) throws ValidationException {
        getFormValue(bean).orElseThrow();
    }

    @Override
    public void populateBean() throws ValidationException {
        if (this.bean != null) {
            populateBean(this.bean);
        } else {
            throw new IllegalArgumentException("There is no bean already set");
        }
    }

    @Override
    public void setFormValue(T value)  {
        copyValuesFromBeanToBinder(value);
    }

    @Override
    public void setFormValue()  {
        copyValuesFromBeanToBinder();
    }

    @Override
    public Optional<T> getFormValue() throws ValidationException {
        copyValuesFromBinderToBean();
        return Optional.ofNullable(this.bean);
    }

    @Override
    public Optional<T> getFormValue(T value) throws ValidationException {
        if (isOK()) {
            copyValuesFromBinderToBean(value);
        }
        return getFormValue();
    }

    @Override
    public void clearForm() {
        this.bean = null;
        binder.readBean(null);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    public boolean isValid() {
        return binder.isValid();
    }


    @Override
    public boolean hasChanges() {
        return binder.hasChanges();
    }

    @Override
    public boolean isOK() {
        return binder.validate().isOk();
    }

    @Override
    public BeanValidationBinder<T> getBinder() {
        return binder;
    }

    private void copyValuesFromBeanToBinder() {
        if (this.bean != null) {
            copyValuesFromBeanToBinder(this.bean);
        } else {
            throw new IllegalArgumentException("There is no value/bean already set");
        }
    }

    private void copyValuesFromBeanToBinder(T bean)  {
        binder.readBean(bean);
    }

    private void copyValuesFromBinderToBean(T bean) throws ValidationException{
        binder.writeBean(bean);
    }

    private void copyValuesFromBinderToBean() throws ValidationException {
        if (this.bean != null) {
            copyValuesFromBinderToBean(this.bean);
        }
    }

    @Override
    public void setBinder(BeanValidationBinder<T> binder) {
        this.binder = binder;
    }

    @Override
    public void setBean(T bean) {
        this.bean = bean;
    }

    @Override
    public T getBean() {
        return this.bean;
    }

    private <T> T createEntity(Class<T> entityClass) {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exceptions appropriately
        }
    }

    public void byPassValidation() {
        // Disables all validators, both binder-level and binding-level
        binder.setValidatorsDisabled(true);
    }

    public void updateButtonStatus(Button saveButton, Button resetButton) {
        binder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();

            saveButton.setEnabled(hasChanges && isValid);
            resetButton.setEnabled(hasChanges);
        });
    }


    
}
