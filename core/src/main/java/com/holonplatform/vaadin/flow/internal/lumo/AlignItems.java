package com.holonplatform.vaadin.flow.internal.lumo;

public enum AlignItems {
    BASELINE("items-baseline"),
    CENTER("items-center"),
    END("items-end"),
    START("items-start"),
    STRETCH("items-stretch");

    private final String className;

    private AlignItems(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
