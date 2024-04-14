package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.internal.components.ColumnSize;
import com.holonplatform.vaadin.flow.internal.components.DefaultXPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

public class DefaultColumnBuilder implements ColumnBuilder {

    private final Div columnDiv;
    
    public DefaultColumnBuilder() {
        columnDiv = new Div();
    }

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
    public Div build() {
        return columnDiv;
    }

    @Override
    public ColumnBuilder add(Component... components) {
        columnDiv.add(components);
        return this;
    }

    private String calcColumnSize(int column) {
        return String.valueOf(12 / column);
    }

    @Override
    public ColumnBuilder small(int column) {
        columnDiv.addClassName("col-sm-" + calcColumnSize(column));
        return this;
    }

    @Override
    public ColumnBuilder medium(int column) {
        columnDiv.addClassName("col-md-" + calcColumnSize(column));
        return this;
    }

    @Override
    public ColumnBuilder large(int column) {
        columnDiv.addClassName("col-lg-" + calcColumnSize(column));
        return this;
    }

    @Override
    public ColumnBuilder xLarge(int column) {
        columnDiv.addClassName("col-xl-" + calcColumnSize(column));
        return this;
    }

    @Override
    public ColumnBuilder xxLarge(int column) {
        columnDiv.addClassName("col-xxl-" + calcColumnSize(column));
        return this;
    }

    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public ColumnBuilder styleNames(String... styleNames) {
        columnDiv.addClassNames(styleNames);
        return this;
    }

    /**
     * Adds a CSS style class names to this component.
     * <p>
     * Multiple styles can be specified as a space-separated list of style names.
     * </p>
     *
     * @param styleName The CSS style class name to be added to the component
     * @return this
     */
    @Override
    public ColumnBuilder styleName(String styleName) {
        columnDiv.addClassName(styleName);
        return this;
    }

    /**
     * Get the UI {@link Component} which represents this object.
     *
     * @return the UI component (not null)
     */
    @Override
    public Component getComponent() {
        return columnDiv;
    }

    @Override
    public Element getElement() {
        return columnDiv.getElement();
    }

    @Override
    public ColumnBuilder small(ColumnSize column) {
        return small(column.getSize());
    }

    @Override
    public ColumnBuilder medium(ColumnSize column) {
        return medium(column.getSize());
    }

    @Override
    public ColumnBuilder large(ColumnSize column) {
        return large(column.getSize());
    }

    @Override
    public ColumnBuilder xLarge(ColumnSize column) {
        return xLarge(column.getSize());
    }

    @Override
    public ColumnBuilder xxLarge(ColumnSize column) {
        return xxLarge(column.getSize());
    }
}
