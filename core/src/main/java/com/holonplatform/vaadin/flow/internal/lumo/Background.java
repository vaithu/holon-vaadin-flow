package com.holonplatform.vaadin.flow.internal.lumo;

public enum Background {
    BASE("color-bg-base"),
    TRANSPARENT("color-bg-transparent"),
    CONTRAST("color-bg-contrast"),
    CONTRAST_90("color-bg-contrast-90"),
    CONTRAST_80("color-bg-contrast-80"),
    CONTRAST_70("color-bg-contrast-70"),
    CONTRAST_60("color-bg-contrast-60"),
    CONTRAST_50("color-bg-contrast-50"),
    CONTRAST_40("color-bg-contrast-40"),
    CONTRAST_30("color-bg-contrast-30"),
    CONTRAST_20("color-bg-contrast-20"),
    CONTRAST_10("color-bg-contrast-10"),
    CONTRAST_5("color-bg-contrast-5"),
    PRIMARY("color-bg-primary"),
    PRIMARY_50("color-bg-primary-50"),
    PRIMARY_10("color-bg-primary-10"),
    ERROR("color-bg-error"),
    ERROR_50("color-bg-error-50"),
    ERROR_10("color-bg-error-10"),
    SUCCESS("color-bg-success"),
    SUCCESS_50("color-bg-success-50"),
    SUCCESS_10("color-bg-success-10");

    private final String className;

    private Background(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
