package com.holonplatform.vaadin.flow.components.builders;

import com.iyensoft.vaadin.flow.enums.ColSpan;
import com.holonplatform.vaadin.flow.internal.components.DefaultXPanel;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public interface ColumnConfigurator<C> {

    C column(String... styleNames);

    C add(DefaultXPanel xPanel);

    C add(Component... components);

    Div build();

    // ── Base span ─────────────────────────────────────────────────────────────

    /**
     * Sets the base (no-breakpoint) column span using a raw integer (1–12).
     * Prefer {@link #span(ColSpan)} for self-documenting code.
     */
    C span(int span);

    /**
     * Sets the base (no-breakpoint) column span using a {@link ColSpan} constant.
     *
     * <pre>{@code
     * ColumnBuilder.create().span(ColSpan.COL_6)   // col-span-6
     * ColumnBuilder.create().span(ColSpan.COL_12)  // col-span-12
     * }</pre>
     */
    default C span(ColSpan colSpan) {
        return span(colSpan.getGridSpan());
    }

    // ── Responsive span ───────────────────────────────────────────────────────

    /**
     * Sets how many of <em>these</em> components appear side-by-side at the given
     * {@link ViewMode} breakpoint. Span = {@code 12 / itemsPerRow}.
     * <p>Use for <strong>equal-division</strong> layouts.
     *
     * <pre>{@code
     * ColumnBuilder.create()
     *     .span(ColSpan.COL_12)
     *     .at(ViewMode.TABLET,  2)   // md:col-span-6  — 2 per row
     *     .at(ViewMode.DESKTOP, 4)   // lg:col-span-3  — 4 per row
     * }</pre>
     */
    C at(ViewMode mode, int itemsPerRow);

    /**
     * Sets an explicit column span at the given {@link ViewMode} breakpoint using
     * a {@link ColSpan} constant.
     * <p>Use for <strong>asymmetric</strong> layouts (main + sidebar, etc.).
     *
     * <pre>{@code
     * // 8-col main content, stacked full-width on mobile:
     * ColumnBuilder.create()
     *     .span(ColSpan.COL_12)
     *     .at(ViewMode.DESKTOP, ColSpan.COL_8);   // lg:col-span-8
     *
     * // 4-col sidebar:
     * ColumnBuilder.create()
     *     .span(ColSpan.COL_12)
     *     .at(ViewMode.DESKTOP, ColSpan.COL_4);   // lg:col-span-4
     * }</pre>
     */
    C at(ViewMode mode, ColSpan colSpan);
}
