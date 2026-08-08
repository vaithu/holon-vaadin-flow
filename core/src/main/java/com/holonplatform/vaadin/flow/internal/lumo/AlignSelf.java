package com.holonplatform.vaadin.flow.internal.lumo;

public enum AlignSelf {
    AUTO("self-auto"),
    BASELINE("self-baseline"),
    CENTER("self-center"),
    END("self-end"),
    START("self-start"),
    STRETCH("self-stretch");

    private final String className;

    AlignSelf(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
