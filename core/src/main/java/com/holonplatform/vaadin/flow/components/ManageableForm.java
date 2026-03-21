package com.holonplatform.vaadin.flow.components;

public interface ManageableForm {

    void enable(boolean enabled);

    void clearForm();

    void saveEntity();

    void deleteEntity();

    void discard();

    default void setReadyOnly() {
        enable(false);
    }

    void updateEntity();

    boolean isFormValid();

    void updateForm();
    <T> void setFormValue(T value);

    <T> T getFormValue();

    void setFormValue(Long id);

    void setContent();

    void copyValuesToForm();
}
