package com.holonplatform.vaadin.flow.internal.lumo;

public enum RowSpan {
    ROW_SPAN_1("row-span-1"),
    ROW_SPAN_2("row-span-2"),
    ROW_SPAN_3("row-span-3"),
    ROW_SPAN_4("row-span-4"),
    ROW_SPAN_5("row-span-5"),
    ROW_SPAN_6("row-span-6");

    private final String value;

    RowSpan(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}