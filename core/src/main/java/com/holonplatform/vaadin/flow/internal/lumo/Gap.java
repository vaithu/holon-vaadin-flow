package com.holonplatform.vaadin.flow.internal.lumo;

public enum Gap {
    XSMALL("gap-xs"),
    SMALL("gap-s"),
    MEDIUM("gap-m"),
    LARGE("gap-l"),
    XLARGE("gap-xl");

    private final String className;

    private Gap(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public ColumnGap getColumnGap() {
        return ColumnGap.valueOf(this.name());
    }

    public RowGap getRowGap() {
        return RowGap.valueOf(this.name());
    }
}
