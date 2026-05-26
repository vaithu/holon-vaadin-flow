package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.internal.lumo.Gap;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;

@StyleSheet("context://preview.css")
public class Preview extends Layout {

    public Preview(Component... components) {
        addClassName("preview");
        // Layout and spacing handled entirely by preview.css
        setFlexDirection(FlexDirection.COLUMN);
        setGap(Gap.MEDIUM);
        if (components != null) {
            add(components);
        }
    }

}