package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Header;

@StyleSheet("context://app-bar.css")
public class AppBar extends Header implements HasTheme {

    private final Layout start;
    private final Layout middle;
    private final Layout end;

    public AppBar(Component... components) {
        this.start = new Layout();
        this.start.addClassName("app-bar__start");

        this.middle = new Layout();
        this.middle.addClassName("app-bar__middle");

        this.end = new Layout();
        this.end.addClassName("app-bar__end");

        addClassName("app-bar");
        getElement().setAttribute("role", "banner");
        setWidthFull();
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