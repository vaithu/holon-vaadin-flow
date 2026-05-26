package com.holonplatform.vaadin.flow.internal.lumo;

public enum Text {
    HEADER("color-text-header"),
    BODY("color-text-body"),
    SECONDARY("color-text-secondary"),
    TERTIARY("color-text-tertiary"),
    DISABLED("color-text-disabled"),
    PRIMARY("color-text-primary"),
    PRIMARY_CONTRAST("color-text-primary-contrast"),
    ERROR("color-text-error"),
    ERROR_CONTRAST("color-text-error-contrast"),
    SUCCESS("color-text-success"),
    SUCCESS_CONTRAST("color-text-success-contrast");

    private final String className;

    private Text(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
