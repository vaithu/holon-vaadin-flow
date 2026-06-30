package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.iyensoft.vaadin.flow.enums.ColSpan;
import com.holonplatform.vaadin.flow.internal.components.DefaultXPanel;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

public class DefaultColumnBuilder implements ColumnBuilder {

    private final Div columnDiv;

    public DefaultColumnBuilder() {
        columnDiv = Components.div().build();
    }

    // -------------------------------------------------------------------------
    // Content
    // -------------------------------------------------------------------------

    @Override
    public ColumnBuilder column(String... styleNames) {
        columnDiv.addClassNames(styleNames);
        return this;
    }

    @Override
    public ColumnBuilder add(DefaultXPanel xPanel) {
        columnDiv.add(xPanel);
        return this;
    }

    @Override
    public ColumnBuilder add(Component... components) {
        columnDiv.add(components);
        return this;
    }

    @Override
    public Div build() {
        return columnDiv;
    }

    // -------------------------------------------------------------------------
    // Tailwind-style responsive span
    // -------------------------------------------------------------------------

    @Override
    public ColumnBuilder span(int span) {
        columnDiv.addClassName("col-span-" + span);
        return this;
    }

    /** Equal-division: N items per row → span = 12 / itemsPerRow. */
    @Override
    public ColumnBuilder at(ViewMode mode, int itemsPerRow) {
        columnDiv.addClassName(mode.toCssClass("col-span-" + (12 / itemsPerRow)));
        return this;
    }

    /** Asymmetric: explicit ColSpan constant → uses raw gridSpan directly. */
    @Override
    public ColumnBuilder at(ViewMode mode, ColSpan colSpan) {
        columnDiv.addClassName(mode.toCssClass("col-span-" + colSpan.getGridSpan()));
        return this;
    }

    // -------------------------------------------------------------------------
    // HasStyleConfigurator / HasComponent
    // -------------------------------------------------------------------------

    @Override
    public ColumnBuilder styleNames(String... styleNames) {
        columnDiv.addClassNames(styleNames);
        return this;
    }

    @Override
    public ColumnBuilder styleName(String styleName) {
        columnDiv.addClassName(styleName);
        return this;
    }

    @Override
    public Component getComponent() {
        return columnDiv;
    }

    @Override
    public Element getElement() {
        return columnDiv.getElement();
    }

}
