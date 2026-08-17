package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;

@StyleSheet("context://app-bar.css")
public class AppBar extends Header implements HasTheme {

    /** Always-present slot containers, created eagerly so the shell has a stable three-slot structure. */
    private final Div startSlot;
    private final Div middleSlot;
    private final Div endSlot;
    private Div bottomSlot;

    public AppBar(Component... components) {

        addClassName("app-bar");
        getElement().setAttribute("role", "banner");
        setWidthFull();

        startSlot = new Div();
        startSlot.addClassName("app-bar__start");
        middleSlot = new Div();
        middleSlot.addClassName("app-bar__middle");
        endSlot = new Div();
        endSlot.addClassName("app-bar__end");
        add(startSlot, middleSlot, endSlot);

        if (components != null && components.length > 0) {
            addToStart(components);
        }
    }

    /** Appends {@code components} to the start slot (left edge). */
    public void addToStart(Component... components) {
        startSlot.add(components);
    }

    /** Appends {@code components} to the middle slot (flex-grow centre). */
    public void addToMiddle(Component... components) {
        middleSlot.add(components);
    }

    /** Appends {@code components} to the end slot (right edge). */
    public void addToEnd(Component... components) {
        endSlot.add(components);
    }

    /**
     * Inserts {@code component} at {@code index} within the end slot.
     * Index 0 = leftmost position inside the end section.
     */
    public void addToEnd(int index, Component component) {
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
