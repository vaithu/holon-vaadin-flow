package com.holonplatform.vaadin.flow.internal.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

/**
 * A plain {@link Div} that additionally declares the {@code layout.css} stylesheet dependency.
 *
 * <p>The CSS Grid layout primitives emitted by the row/column builders ({@code .row},
 * {@code .col-span-N}, {@code .grid-cols-N} and their responsive {@code sm:}/{@code md:}/
 * {@code lg:}/{@code xl:} variants) live in {@code layout.css}. A bare {@link Div} carries no
 * dependency annotation, so a view that only used the builders rendered the class names without
 * ever loading the stylesheet. Using this component as the builder container makes the builders
 * self-sufficient: Flow registers {@code layout.css} as soon as a row or column is instantiated.</p>
 *
 * @since 10.0.0
 */
@StyleSheet("context://layout.css")
public class LayoutDiv extends Div {

    public LayoutDiv(Component... children) {
        super(children);
    }

    public LayoutDiv(String className, Component... children) {
        super(children);
        if (className != null && !className.isEmpty()) {
            addClassName(className);
        }
    }

}
