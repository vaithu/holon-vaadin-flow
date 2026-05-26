package com.holonplatform.vaadin.flow.enums;

/**
 * Column-span constants for use with
 * {@link com.holonplatform.vaadin.flow.components.builders.ColumnConfigurator#span(ColSpan)}.
 *
 * <p>Each constant maps directly to a CSS Grid {@code col-span-N} class defined in
 * {@code layout.css}. The number in the name is the span value (1–12).</p>
 *
 * <pre>{@code
 * ColumnBuilder.create().span(ColSpan.COL_6)   // col-span-6  — half width
 * ColumnBuilder.create().span(ColSpan.COL_4)   // col-span-4  — one third
 * ColumnBuilder.create().span(ColSpan.COL_3)   // col-span-3  — one quarter
 *
 * // Responsive:
 * ColumnBuilder.create()
 *     .span(ColSpan.COL_12)              // col-span-12 — full width on mobile
 *     .at(ViewMode.TABLET,  2)           // md:col-span-6
 *     .at(ViewMode.DESKTOP, 4)           // lg:col-span-3
 * }</pre>
 */
public enum ColSpan {

    COL_1(1),
    COL_2(2),
    COL_3(3),
    COL_4(4),
    COL_5(5),
    COL_6(6),
    COL_7(7),
    COL_8(8),
    COL_9(9),
    COL_10(10),
    COL_11(11),
    COL_12(12);

    private final int gridSpan;

    ColSpan(int gridSpan) {
        this.gridSpan = gridSpan;
    }

    /**
     * Returns the raw CSS Grid column span value (1–12).
     */
    public int getGridSpan() {
        return gridSpan;
    }
}
