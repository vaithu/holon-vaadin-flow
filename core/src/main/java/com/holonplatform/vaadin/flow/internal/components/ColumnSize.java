package com.holonplatform.vaadin.flow.internal.components;

public enum ColumnSize {

    COL_1(1),
    COL_2(2),
    COL_3(3),
    COL_4(4),
    COL_6(6),

    COL_FULL_SIZE(1),
    COL_EQUAL_HALF_SIZE(2),
    COL_EQUAL_3_SIZE(4),
    COL_EQUAL_4_SIZE(3),

    COL_100P(1),
    COL_50P(2),
    COL_75P(4),
    COL_12(12);

    private final int size;

    public int getSize() {
        return size;
    }

    ColumnSize(int i) {

        size = i;

    }

    @Override
    public String toString() {
        return "ColumnSize{" +
                "size=" + size +
                '}';
    }
}
