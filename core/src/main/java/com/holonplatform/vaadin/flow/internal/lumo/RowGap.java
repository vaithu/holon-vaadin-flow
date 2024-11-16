package com.holonplatform.vaadin.flow.internal.lumo;

public enum RowGap {
    XSMALL("gap-y-xs"),
    SMALL("gap-y-s"),
    MEDIUM("gap-y-m"),
    LARGE("gap-y-l"),
    XLARGE("gap-y-xl");

    private final String value;

    RowGap(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}