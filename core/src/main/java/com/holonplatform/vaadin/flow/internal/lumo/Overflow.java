package com.holonplatform.vaadin.flow.internal.lumo;

public enum Overflow {
    AUTO("overflow-auto"),
    HIDDEN("overflow-hidden");

    private final String className;

    Overflow(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
