package com.holonplatform.vaadin.flow.internal.lumo;

public enum BoxSizing {
    BORDER("box-border"),
    CONTENT("box-content");

    private final String className;

    BoxSizing(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
