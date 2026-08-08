package com.holonplatform.vaadin.flow;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
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

    /**
     * Creates a new {@link DoubleLabel} from {@link Localizable} descriptors.
     * Each label is resolved at construction time and re-resolved on locale change.
     *
     * @param titleTop    primary (top) localizable label
     * @param titleBottom secondary (bottom) localizable label
     */
    public DoubleLabel(Localizable titleTop, Localizable titleBottom) {
        addClassName("double-label");

        spanTop = Components.span().text(resolve(titleTop)).styleName("double-label__top").build();
        spanBottom = Components.span().text(resolve(titleBottom)).styleName("double-label__bottom").build();

        add(spanTop, spanBottom);
    }

    // -----------------------------------------------------------------------
    // Alignment modifiers
    // -----------------------------------------------------------------------

    /** Aligns both spans to the left (flex-start). */
    public void setAlignLeft() {
        addClassName("double-label--align-left");
    }

    /** Aligns both spans to the centre (default). */
    public void setAlignCenter() {
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

    /**
     * Sets the top label from a {@link Localizable} descriptor.
     * Re-resolved on each locale change.
     *
     * @param title localizable top label
     */
    public void setTitleTop(Localizable title) {
        spanTop.setText(resolve(title));
    }

    public void setTitleBottom(String title) {
        spanBottom.setText(title);
    }

    /**
     * Sets the bottom label from a {@link Localizable} descriptor.
     * Re-resolved on each locale change.
     *
     * @param title localizable bottom label
     */
    public void setTitleBottom(Localizable title) {
        spanBottom.setText(resolve(title));
    }

    private static String resolve(Localizable l) {
        if (l == null) return "";
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    public void addClassNamesToSpans(String... classNames) {
        spanTop.addClassNames(classNames);
        spanBottom.addClassNames(classNames);
    }

}
