package com.holonplatform.vaadin.flow.internal.lumo;

public enum GridColumn {
    COLUMNS_1("grid-cols-1"),
    COLUMNS_2("grid-cols-2"),
    COLUMNS_3("grid-cols-3"),
    COLUMNS_4("grid-cols-4"),
    COLUMNS_5("grid-cols-5"),
    COLUMNS_6("grid-cols-6"),
    COLUMNS_7("grid-cols-7"),
    COLUMNS_8("grid-cols-8"),
    COLUMNS_9("grid-cols-9"),
    COLUMNS_10("grid-cols-10"),
    COLUMNS_11("grid-cols-11"),
    COLUMNS_12("grid-cols-12");

    private final String value;

    GridColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}