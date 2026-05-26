package com.holonplatform.vaadin.flow.internal.lumo;

public enum FlexDirection {
    COLUMN("flex-col"),
    COLUMN_REVERSE("flex-col-reverse"),
    ROW("flex-row"),
    ROW_REVERSE("flex-row-reverse");

    private final String className;

    private FlexDirection(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
