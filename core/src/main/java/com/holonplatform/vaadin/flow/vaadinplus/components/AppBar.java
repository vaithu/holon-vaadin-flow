package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;

@StyleSheet("context://app-bar.css")
public class AppBar extends Header implements HasTheme {

    /** Lazily-created slot containers — null until first component is added. */
    private Div startSlot;
    private Div middleSlot;
    private Div endSlot;
    private Div bottomSlot;

    public AppBar(Component... components) {

        addClassName("app-bar");
        getElement().setAttribute("role", "banner");
        setWidthFull();
        if (components != null && components.length > 0) {
            addToStart(components);
        }
    }

    /** Appends {@code components} to the start slot (left edge). */
    public void addToStart(Component... components) {
        if (startSlot == null) {
            startSlot = new Div();
            startSlot.addClassName("app-bar__start");
            addComponentAsFirst(startSlot);
        }
        startSlot.add(components);
    }

    /** Appends {@code components} to the middle slot (flex-grow centre). */
    public void addToMiddle(Component... components) {
        if (middleSlot == null) {
            middleSlot = new Div();
            middleSlot.addClassName("app-bar__middle");
            add(middleSlot);
        }
        middleSlot.add(components);
    }

    /** Appends {@code components} to the end slot (right edge). */
    public void addToEnd(Component... components) {
        if (endSlot == null) {
            endSlot = new Div();
            endSlot.addClassName("app-bar__end");
            add(endSlot);
        }
        endSlot.add(components);
    }

    /**
     * Inserts {@code component} at {@code index} within the end slot.
     * Index 0 = leftmost position inside the end section.
     */
    public void addToEnd(int index, Component component) {
        if (endSlot == null) {
            endSlot = new Div();
            endSlot.addClassName("app-bar__end");
            add(endSlot);
        }
        endSlot.addComponentAtIndex(index, component);
    }

    /** Appends {@code components} to the full-width bottom row (below start/middle/end). */
    public void addToBottom(Component... components) {
        if (bottomSlot == null) {
            bottomSlot = new Div();
            add(bottomSlot);
        }
        if (components != null) {
            bottomSlot.setClassName("app-bar__bottom");
            bottomSlot.add(components);
        }

    }

}
