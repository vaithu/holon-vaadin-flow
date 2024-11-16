package com.holonplatform.vaadin.flow.internal.lumo;

public enum ColumnGap {
    XSMALL("gap-x-xs"),
    SMALL("gap-x-s"),
    MEDIUM("gap-x-m"),
    LARGE("gap-x-l"),
    XLARGE("gap-x-xl");

    private final String value;

    ColumnGap(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
