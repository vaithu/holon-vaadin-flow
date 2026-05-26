package com.holonplatform.vaadin.flow;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * A stacked two-line label component with a primary (top) and secondary
 * (bottom) span. Styled via {@code double-label.css}.
 *
 * <p>BEM root: {@code .double-label}</p>
 *
 * @since 10.0.0
 */
@StyleSheet("context://double-label.css")
public class DoubleLabel extends Div {

    private final Span spanTop;
    private final Span spanBottom;

    /**
     * Creates a new {@link DoubleLabel} with the given top and bottom text.
     *
     * @param titleTop    primary (top) label text
     * @param titleBottom secondary (bottom) label text
     */
    public DoubleLabel(String titleTop, String titleBottom) {
        addClassName("double-label");

        spanTop = Components.span().text(titleTop).styleName("double-label__top").build();
        spanBottom = Components.span().text(titleBottom).styleName("double-label__bottom").build();

        add(spanTop, spanBottom);
    }

    // -----------------------------------------------------------------------
    // Alignment modifiers
    // -----------------------------------------------------------------------

    /** Aligns both spans to the left (flex-start). */
    public void setAlignLeft() {
        addClassName("double-label--align-left");
        removeClassName("double-label--align-center");
    }

    /** Aligns both spans to the centre (default). */
    public void setAlignCenter() {
        addClassName("double-label--align-center");
        removeClassName("double-label--align-left");
    }

    // -----------------------------------------------------------------------
    // Width modifiers
    // -----------------------------------------------------------------------

    /** Sets a fixed 300 px flex-basis (no grow). */
    public void setFixedWidth() {
        addClassName("double-label--fixed-width");
        removeClassName("double-label--grow");
    }

    /** Allows the component to grow to fill its flex container. */
    public void setGrow() {
        addClassName("double-label--grow");
        removeClassName("double-label--fixed-width");
    }

    // -----------------------------------------------------------------------
    // Border modifier
    // -----------------------------------------------------------------------

    /** Removes the bottom border separator. */
    public void setNoBorder() {
        addClassName("double-label--no-border");
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public Span getSpanTop() {
        return spanTop;
    }

    public Span getSpanBottom() {
        return spanBottom;
    }

    public void setTitleTop(String title) {
        spanTop.setText(title);
    }

    public void setTitleBottom(String title) {
        spanBottom.setText(title);
    }

    public void addClassNamesToSpans(String... classNames) {
        spanTop.addClassNames(classNames);
        spanBottom.addClassNames(classNames);
    }

}