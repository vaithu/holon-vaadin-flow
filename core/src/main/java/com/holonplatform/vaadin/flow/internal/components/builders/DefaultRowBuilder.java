package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

import java.util.Arrays;

public class DefaultRowBuilder implements RowBuilder {

    private final Div divRow;

    public DefaultRowBuilder() {
        divRow = Components.div().styleName("row").build();
    }

    @Override
    public Component getComponent() { return this.divRow; }

    @Override
    public Element getElement() { return this.divRow.getElement(); }

    @Override
    public RowBuilder styleNames(String... styleNames) { divRow.addClassNames(styleNames); return this; }

    @Override
    public RowBuilder styleName(String styleName) { divRow.addClassName(styleName); return this; }

    @Override
    public RowBuilder add(Component... components) { divRow.add(components); return this; }

    @Override
    public RowBuilder add(ColumnBuilder... columnBuilders) {
        Arrays.stream(columnBuilders).forEach(cb -> divRow.add(cb.build()));
        return this;
    }

    @Override
    public RowBuilder remove(Component... components) { divRow.remove(components); return this; }

    /**
     * Overrides the default 12-col layout with a fixed column count at all viewport sizes.
     * Adds {@code grid-cols-{cols}} — placed after {@code .row} in layout.css so it wins the cascade.
     */
    @Override
    public RowBuilder gridColumns(int cols) {
        divRow.addClassName("grid-cols-" + cols);
        return this;
    }

    /**
     * Sets the column count for a specific breakpoint via {@link ViewMode#toCssClass(String)}.
     * Example: {@code gridColumns(ViewMode.DESKTOP, 3)} → {@code lg:grid-cols-3}.
     */
    @Override
    public RowBuilder gridColumns(ViewMode mode, int cols) {
        divRow.addClassName(mode.toCssClass("grid-cols-" + cols));
        return this;
    }

    @Override
    public Div build() { return divRow; }
}



