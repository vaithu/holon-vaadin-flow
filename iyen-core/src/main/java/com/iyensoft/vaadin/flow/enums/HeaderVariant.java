package com.iyensoft.vaadin.flow.enums;

/** Visual variants supported by {@code Header}. */
public enum HeaderVariant {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    TERTIARY("tertiary");

    private final String className;

    HeaderVariant(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
