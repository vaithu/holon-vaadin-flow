package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.HasFormView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Optional;

public abstract class AbstractFormView<T> implements HasFormView<T>  {

    private  BeanValidationBinder<T> binder;
    private T beanInstance;

    public AbstractFormView(BeanValidationBinder<T> binder) {
        setBinder(binder);
    }

    public AbstractFormView(T beanInstance, BeanValidationBinder<T> binder) {
        this.beanInstance = beanInstance;
        this.binder = binder;
    }

    @Override
    public void populateForm(T beanInstance) {
        setFormValueFromBean(beanInstance);
    }

    @Override
    public void populateForm() {
        ObjectUtils.argumentNotNull(getBean(), "Bean  is not already set so it is null here");
        populateForm(getBean());
    }

    @Override
    public void populateBean(T beanInstance) throws ValidationException {
        getFormValueToBean(beanInstance).orElseThrow();
    }

    @Override
    public void populateBean() throws ValidationException {
        ObjectUtils.argumentNotNull(getBean(), "Bean  is not already set so it is null here");
        populateBean(getBean());
    }

    @Override
    public void setFormValueFromBean(T value)  {
        copyValuesFromBeanToBinder(value);
    }

    @Override
    public void setFormValueFromBean()  {
        copyValuesFromBeanToBinder();
    }

    @Override
    public Optional<T> getFormValueToBean() throws ValidationException {
        copyValuesFromBinderToBean();
        return Optional.ofNullable(getBean());
    }

    @Override
    public Optional<T> getFormValueToBean(T value) throws ValidationException {
        copyValuesFromBinderToBean(value);
        return Optional.ofNullable(getBean());
    }

    @Override
    public void clearForm() {
        setBean(null);
        binder.readBean(null);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    public boolean isValid() {
        return !binder.validate().hasErrors();
    }

    @Override
    public void validate() {
        binder.validate();
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
        ObjectUtils.argumentNotNull(binder, "Binder  is not already set so it is null here");
        return
                binder;
    }

    private void copyValuesFromBeanToBinder() {
        ObjectUtils.argumentNotNull(getBean(), "Bean  is not already set so it is null here");
        copyValuesFromBeanToBinder(getBean());
    }

    private void copyValuesFromBeanToBinder(T beanInstance)  {
        setBean(beanInstance);
        binder.readBean(getBean());
    }

    private void copyValuesFromBinderToBean(T beanInstance) throws ValidationException {
        setBean(beanInstance);
        binder.writeBean(getBean());
    }

    private void copyValuesFromBinderToBean() throws ValidationException {
        ObjectUtils.argumentNotNull(getBean(), "Bean  is not already set so it is null here");
        copyValuesFromBinderToBean(getBean());
    }

    @Override
    public void setBinder(BeanValidationBinder<T> binder) {
        this.binder = binder;
    }

    @Override
    public void setBean(T beanInstance) {
        this.beanInstance = beanInstance;
    }

    private T getBean() {
        return this.beanInstance;
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
