package com.holonplatform.vaadin.flow.internal.lumo;

public enum Gap {
    XSMALL("gap-xs"),
    SMALL("gap-s"),
    MEDIUM("gap-m"),
    LARGE("gap-l"),
    XLARGE("gap-xl");

    private final String value;

    Gap(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}