package com.holonplatform.vaadin.flow.internal.lumo;

public enum Display {
    BLOCK("block"),
    FLEX("flex"),
    GRID("grid"),
    HIDDEN("hidden"),
    INLINE("inline"),
    INLINE_BLOCK("inline-block"),
    INLINE_FLEX("inline-flex"),
    INLINE_GRID("inline-grid");

    private final String value;

    Display(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}