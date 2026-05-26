package com.holonplatform.vaadin.flow.internal.lumo;

public enum Display {
    FLEX("flex"),
    GRID("grid");

    private final String className;

    private Display(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
