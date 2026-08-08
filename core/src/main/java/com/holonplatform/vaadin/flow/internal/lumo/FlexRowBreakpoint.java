package com.holonplatform.vaadin.flow.internal.lumo;

public enum FlexRowBreakpoint {
    SMALL("sm:flex-row"),
    MEDIUM("md:flex-row"),
    LARGE("lg:flex-row"),
    XLARGE("xl:flex-row"),
    XXLARGE("2xl:flex-row");

    private final String className;

    FlexRowBreakpoint(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }
}
