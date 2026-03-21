package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class AppBar extends Header implements HasTheme {

    private final Layout start;
    private final Layout middle;
    private final Layout end;

    public AppBar(Component... components) {
        addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX,
                LumoUtility.Gap.LARGE, LumoUtility.Height.XLARGE,
                LumoUtility.Padding.Horizontal.MEDIUM);
        setWidthFull();

        this.start = new Layout();
        this.start.setDisplay(Display.FLEX);
        this.start.setAlignItems(AlignItems.CENTER);
        this.start.setGap(Gap.LARGE);
        this.start.setOverflow(Overflow.HIDDEN);

        this.middle = new Layout();
        this.middle.setDisplay(Display.FLEX);
        this.middle.setAlignItems(AlignItems.CENTER);
        this.middle.setFlexGrow();
        this.middle.setJustifyContent(JustifyContent.CENTER);

        this.end = new Layout();
        this.end.setDisplay(Display.FLEX);
        this.end.setAlignItems(AlignItems.CENTER);
        this.end.setGap(Gap.SMALL);

        add(this.start, this.middle, this.end);

        addToStart(components);
    }

    public void addToStart(Component... components) {
        this.start.add(components);
    }

    public void addToMiddle(Component... components) {
        this.middle.add(components);
    }

    public void addToEnd(Component... components) {
        this.end.add(components);
    }

    public void addToEnd(int index, Component component) {
        this.end.addComponentAtIndex(index, component);
    }

}