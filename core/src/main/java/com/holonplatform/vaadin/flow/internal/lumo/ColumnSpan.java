package com.holonplatform.vaadin.flow.internal.lumo;

public enum ColumnSpan {
    COLUMN_SPAN_1("col-span-1"),
    COLUMN_SPAN_2("col-span-2"),
    COLUMN_SPAN_3("col-span-3"),
    COLUMN_SPAN_4("col-span-4"),
    COLUMN_SPAN_5("col-span-5"),
    COLUMN_SPAN_6("col-span-6"),
    COLUMN_SPAN_7("col-span-7"),
    COLUMN_SPAN_8("col-span-8"),
    COLUMN_SPAN_9("col-span-9"),
    COLUMN_SPAN_10("col-span-10"),
    COLUMN_SPAN_11("col-span-11"),
    COLUMN_SPAN_12("col-span-12");

    private final String value;

    ColumnSpan(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}