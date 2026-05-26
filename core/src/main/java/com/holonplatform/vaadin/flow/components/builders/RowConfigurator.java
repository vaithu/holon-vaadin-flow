package com.holonplatform.vaadin.flow.components.builders;

import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public interface RowConfigurator<C> {

    C add(Component... components);

    C add(ColumnBuilder... columnBuilders);

    C remove(Component... components);

    Div build();

    /**
     * Overrides the default 12-column layout with a fixed column count at all viewport sizes.
     * <p>
     * Adds {@code grid-cols-{cols}}. Because {@code .grid-cols-*} is defined after {@code .row}
     * in {@code layout.css}, this always wins the CSS cascade.
     *
     * <pre>{@code
     * // 3-column card grid, same at all sizes
     * RowBuilder.create().gridColumns(3)
     * }</pre>
     *
     * @param cols number of equal columns (1–12)
     * @return this
     */
    C gridColumns(int cols);

    /**
     * Sets the column count for a specific {@link ViewMode} breakpoint.
     * <p>
     * Adds {@code {prefix}:grid-cols-{cols}}, e.g. {@code lg:grid-cols-3}.
     *
     * <pre>{@code
     * // Responsive card grid: 1 col → 2 col → 3 col
     * RowBuilder.create()
     *     .gridColumns(1)                      // base (mobile first)
     *     .gridColumns(ViewMode.TABLET, 2)     // md:grid-cols-2
     *     .gridColumns(ViewMode.DESKTOP, 3)    // lg:grid-cols-3
     * }</pre>
     *
     * @param mode the breakpoint (MOBILE, TABLET, DESKTOP, LARGE_DESKTOP, ULTRA_WIDE)
     * @param cols number of equal columns at that breakpoint (1–12)
     * @return this
     */
    C gridColumns(ViewMode mode, int cols);
}


