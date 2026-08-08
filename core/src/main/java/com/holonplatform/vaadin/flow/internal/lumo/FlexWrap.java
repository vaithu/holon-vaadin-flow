package com.holonplatform.vaadin.flow.internal.lumo;

public enum FlexWrap {
    NOWRAP("flex-nowrap"),
    WRAP("flex-wrap"),
    WRAP_REVERSE("flex-wrap-reverse");

    private final String className;

    FlexWrap(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
