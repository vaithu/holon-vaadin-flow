package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

import java.util.Arrays;

public class DefaultRowBuilder implements RowBuilder {

    private final Div divRow;

    public DefaultRowBuilder() {
        divRow = new Div();
        divRow.addClassName("row");
    }

    /**
     * Get the UI {@link Component} which represents this object.
     *
     * @return the UI component (not null)
     */
    @Override
    public Component getComponent() {
        return this.divRow;
    }

    @Override
    public Element getElement() {
        return this.divRow.getElement();
    }

    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public RowBuilder styleNames(String... styleNames) {
        divRow.addClassNames(styleNames);
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
    public RowBuilder styleName(String styleName) {
        divRow.addClassName(styleName);
        return this;
    }



    @Override
    public RowBuilder add(Component... components) {
        divRow.add(components);
        return this;
    }

    @Override
    public RowBuilder add(ColumnBuilder... columnBuilders) {
        Arrays.stream(columnBuilders).forEach(columnBuilder -> divRow.add(columnBuilder.build()));
        return this;
    }

    @Override
    public RowBuilder remove(Component... components) {
        divRow.remove(components);
        return this;
    }

    @Override
    public Div build() {
        return divRow;
    }
}
