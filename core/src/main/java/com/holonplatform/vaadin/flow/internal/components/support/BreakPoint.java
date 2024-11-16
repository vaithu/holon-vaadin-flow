package com.holonplatform.vaadin.flow.internal.components.support;

public enum BreakPoint {

    BREAKPOINT_XS ( "0px"),
    BREAKPOINT_SM ( "576px"),
    BREAKPOINT_MD ( "768px"),
    BREAKPOINT_LG ( "992px"),
    BREAKPOINT_XL ( "1200px"),
    BREAKPOINT_XXL ( "1400px");

    private String size;

    BreakPoint(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "BreakPoint{" +
                "size='" + size + '\'' +
                '}';
    }
}
