package com.holonplatform.vaadin.flow.internal.lumo;

public enum JustifyContent {
    AROUND("justify-around"),
    BETWEEN("justify-between"),
    CENTER("justify-center"),
    END("justify-end"),
    EVENLY("justify-evenly"),
    START("justify-start");

    private final String className;

    private JustifyContent(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
