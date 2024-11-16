package com.holonplatform.vaadin.flow.internal.lumo;

public enum GridRow {
    ROWS_1("grid-rows-1"),
    ROWS_2("grid-rows-2"),
    ROWS_3("grid-rows-3"),
    ROWS_4("grid-rows-4"),
    ROWS_5("grid-rows-5"),
    ROWS_6("grid-rows-6");

    private final String value;

    GridRow(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}