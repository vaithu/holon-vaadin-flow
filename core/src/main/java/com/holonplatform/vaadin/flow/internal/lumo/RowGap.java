package com.holonplatform.vaadin.flow.internal.lumo;

public enum RowGap {
    XSMALL("gap-y-xs"),
    SMALL("gap-y-s"),
    MEDIUM("gap-y-m"),
    LARGE("gap-y-l"),
    XLARGE("gap-y-xl");

    private final String className;

    private RowGap(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
