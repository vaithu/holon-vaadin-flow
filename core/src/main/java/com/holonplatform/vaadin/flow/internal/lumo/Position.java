package com.holonplatform.vaadin.flow.internal.lumo;

public enum Position {
    ABSOLUTE("absolute"),
    FIXED("fixed"),
    RELATIVE("relative"),
    STATIC("static"),
    STICKY("sticky");

    private final String className;

    Position(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
