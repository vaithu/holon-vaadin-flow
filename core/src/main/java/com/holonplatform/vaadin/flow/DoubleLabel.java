package com.holonplatform.vaadin.flow;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DoubleLabel extends Div {
    private final Span spanTop;
    private final Span spanBottom;

    public DoubleLabel(String titleTop, String titleBottom) {
        spanTop = new Span(titleTop);
        spanTop.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.PRIMARY);
        spanBottom = new Span(titleBottom);
        spanBottom.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);
        add(spanTop, spanBottom);
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Flex.GROW, LumoUtility.Flex.SHRINK_NONE, LumoUtility.AlignItems.CENTER);
        addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_10, LumoUtility.Padding.SMALL);
    }

    public DoubleLabel(String titleTop, String titleBottom, boolean noBorders) {
        this(titleTop, titleBottom);
        if (noBorders) {
            removeClassNames(LumoUtility.Border.BOTTOM);
        }
        setAlignLeft();
        setFixedWidth();
    }

    public void setAlignLeft() {
        addClassNames(LumoUtility.AlignItems.START);
        removeClassNames(LumoUtility.AlignItems.CENTER);
    }

    public void setFixedWidth() {
        getStyle().set("flex-basis", "300px");
       /* .flex-basis-300-pixels {
            flex-basis: 300px;
        }
        addClassNames(FLEX_BASIS_300_PIXELS);*/
        removeClassNames(LumoUtility.Flex.GROW);
    }

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