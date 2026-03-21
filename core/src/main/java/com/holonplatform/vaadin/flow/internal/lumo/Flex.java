package com.holonplatform.vaadin.flow.internal.lumo;

public enum Flex {
    ONE("flex-1"),
    AUTO("flex-auto"),
    NONE("flex-none"),
    GROW("flex-grow"),
    GROW_NONE("flex-grow-0"),
    SHRINK("flex-shrink"),
    SHRINK_NONE("flex-shrink-0");

    private final String value;

    Flex(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}